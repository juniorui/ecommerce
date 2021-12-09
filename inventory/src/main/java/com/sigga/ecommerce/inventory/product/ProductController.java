package com.sigga.ecommerce.inventory.product;

import com.sigga.ecommerce.core.controller.EcommerceController;
import com.sigga.ecommerce.core.service.EcommerceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product")
public class ProductController extends EcommerceController<ProductEntity, Product> implements ProductResource {

    public ProductController(EcommerceService<ProductEntity, Product> service) {

        super(service);
    }
}
