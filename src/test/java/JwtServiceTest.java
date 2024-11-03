import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.jwt_autho.services.JwtService;

import java.security.Key;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b"; // Placeholder secret key for testing
    private long jwtExpiration = 86400000; // 1-day expiration for testing

    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secretKey, jwtExpiration); // Instantiate JwtService with constructor

        // Initialize UserDetails (using a mock or a test user)
        userDetails = new User("tom@gmail.com", "12345", new ArrayList<>()); // Mock user details for testing
        
        // Generate token
        token = jwtService.generateToken(userDetails);
    }

    @Test
    void testExtractUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(token);
    }

    @Test
    void testIsTokenValid() {
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenExpired() {
        assertFalse(jwtService.isTokenExpired(token));
    }

    // Utility method to test the private signing key method
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
