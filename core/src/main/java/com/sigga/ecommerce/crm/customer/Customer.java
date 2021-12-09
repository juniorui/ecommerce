package com.sigga.ecommerce.crm.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class Customer {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    @Size(max = 100)
    @NotNull(message = "customer name is required")
    private String name;

    @Size(max = 150)
    @NotNull(message = "customer email is required")
    private String email;
}
