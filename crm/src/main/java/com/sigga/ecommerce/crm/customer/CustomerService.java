package com.sigga.ecommerce.crm.customer;

import com.sigga.ecommerce.core.service.EcommerceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends EcommerceService<CustomerEntity, Customer> {

    public CustomerService(CustomerRepository repository, ModelMapper modelMapper) {

        super(repository, modelMapper);
    }
}
