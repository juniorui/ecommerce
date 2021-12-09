package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.controller.EcommerceController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("purchase-order")
public class PurchaseOrderController extends EcommerceController<PurchaseOrderEntity, PurchaseOrder> implements PurchaseOrderResource {

    public PurchaseOrderController(PurchaseOrderService service) {

        super(service);
    }
}
