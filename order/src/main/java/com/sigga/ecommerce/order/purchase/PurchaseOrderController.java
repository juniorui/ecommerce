package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.controller.EcommerceController;
import com.sigga.ecommerce.inventory.product.Product;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("purchase-order")
public class PurchaseOrderController extends EcommerceController<PurchaseOrderEntity, PurchaseOrder> implements PurchaseOrderResource {

    public PurchaseOrderController(PurchaseOrderService service) {

        super(service);

    }

}
