package com.sigga.ecommerce.inventory.product;

import com.sigga.ecommerce.core.service.EcommerceService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends EcommerceService<ProductEntity, Product> {

    public ProductService(ProductRepository repository, ModelMapper modelMapper, AmqpTemplate amqpTemplate) {

        super(repository, modelMapper, amqpTemplate);
    }
}
