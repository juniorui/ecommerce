package com.sigga.ecommerce.product;

import com.sigga.ecommerce.inventory.product.ProductResource;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "product", url = "http://localhost:9001/api/product")
public interface ProductResourceClient extends ProductResource {

}
