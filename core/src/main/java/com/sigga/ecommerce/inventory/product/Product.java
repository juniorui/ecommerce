package com.sigga.ecommerce.inventory.product;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Product {

    private UUID id;

    @NotNull(message = "the product name is required")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "the product price must be greater than 0")
    @NotNull(message = "the product price is required")
    private BigDecimal price;
}
