package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.repository.EcommerceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends EcommerceRepository<PurchaseOrderEntity> {

}
