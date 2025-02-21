package com.example.jwt_autho.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.jwt_autho.dtos.RegisterUserDto;
import com.example.jwt_autho.entities.*;
import com.example.jwt_autho.repositories.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService mailService;

    @InjectMocks
    private UserService userService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setLikedProducts(new HashSet<>());

        product = new Product();
        product.setId(1);
        product.setName("Laptop");
        product.setStatus(ProductStatusEnum.AVAILABLE);
        product.setLikedByUsers(new HashSet<>());
    }

    @Test
    void testAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.allUsers();

        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getFullName());
    }

    @Test
    void testCreateAdministrator() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setFullName("Admin User");
        dto.setEmail("admin@example.com");
        dto.setPassword("password");

        Role adminRole = new Role();
        adminRole.setName(RoleEnum.ADMIN);

        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User admin = userService.createAdministrator(dto);

        assertNotNull(admin);
        assertEquals("Admin User", admin.getFullName());
        assertEquals(adminRole, admin.getRole());
    }

    @Test
    void testLikeProduct() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        userService.likeProduct(1, 1);

        assertTrue(user.getLikedProducts().contains(product));
        assertTrue(product.getLikedByUsers().contains(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUnlikeProduct() {
        user.getLikedProducts().add(product);
        product.getLikedByUsers().add(user);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        userService.unlikeProduct(1, 1);

        assertFalse(user.getLikedProducts().contains(product));
        assertFalse(product.getLikedByUsers().contains(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetLikedProducts() {
        user.getLikedProducts().add(product);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Set<Product> likedProducts = userService.getLikedProducts(1);

        assertEquals(1, likedProducts.size());
        assertTrue(likedProducts.contains(product));
    }

    @Test
    void testBuyProduct() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ResponseEntity<String> response = userService.buyProduct(1, 1);

        assertEquals("Purchase successful, confirmation email sent.", response.getBody());
        assertEquals(ProductStatusEnum.SOLD, product.getStatus());
        assertEquals(user, product.getBuyer());

        verify(mailService, times(1)).sendEmail(
            eq(user.getEmail()), eq("Purchase Confirmation"), eq("thank-you-email"), anyMap()
        );
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testBuyProduct_AlreadySold() {
        product.setStatus(ProductStatusEnum.SOLD);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.buyProduct(1, 1);
        });

        assertEquals("Product is already sold", exception.getMessage());
    }
}