package com.sigga.ecommerce.crm.customer;

import com.sigga.ecommerce.core.service.EcommerceService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends EcommerceService<CustomerEntity, Customer> {

    public CustomerService(CustomerRepository repository, ModelMapper modelMapper, AmqpTemplate amqpTemplate) {

        super(repository, modelMapper, amqpTemplate);
    }
}
