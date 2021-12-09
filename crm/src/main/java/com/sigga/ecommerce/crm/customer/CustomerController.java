package com.sigga.ecommerce.crm.customer;

import com.sigga.ecommerce.core.controller.EcommerceController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customer")
public class CustomerController extends EcommerceController<CustomerEntity, Customer> implements CustomerResource {

    public CustomerController(CustomerService service) {

        super(service);
    }
}
