package com.sigga.ecommerce.crm.customer;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "customer", url = "http://localhost:9000/api/customer")
public interface CustomerResourceClient extends CustomerResource {

}
