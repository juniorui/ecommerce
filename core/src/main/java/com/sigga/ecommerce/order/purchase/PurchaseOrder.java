package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.crm.customer.Customer;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class PurchaseOrder {

    private UUID id;

    @NotNull(message = "the purchase order customer is required")
    private Customer customer;

    @Valid
    private List<PurchaseOrderProduct> products;
}
