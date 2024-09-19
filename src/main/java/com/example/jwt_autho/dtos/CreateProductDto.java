package com.example.jwt_autho.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class CreateProductDto {
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @Positive
    private double price;

    @Size(max = 500)
    private String description;

    @NotNull
    private Integer sellerId;

    @NotNull
    private String status; // Assuming you will handle the conversion to `ProductStatusEnum` in the service
}