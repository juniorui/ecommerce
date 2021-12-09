package com.sigga.ecommerce.crm.customer;

import com.sigga.ecommerce.core.repository.EcommerceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends EcommerceRepository<CustomerEntity> {

}
