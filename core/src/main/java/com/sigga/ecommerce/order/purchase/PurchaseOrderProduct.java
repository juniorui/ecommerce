package com.sigga.ecommerce.order.purchase;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sigga.ecommerce.inventory.product.Product;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PurchaseOrderProduct {

    private UUID id;

    @NotNull(message = "the purchase order product is required")
    private Product product;

    @NotNull(message = "the product quantity is required")
    private Integer quantity;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal purchasePrice;

}
