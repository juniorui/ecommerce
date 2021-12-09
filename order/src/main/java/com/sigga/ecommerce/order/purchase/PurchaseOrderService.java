package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.service.EcommerceService;
import com.sigga.ecommerce.crm.customer.CustomerResourceClient;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderService extends EcommerceService<PurchaseOrderEntity, PurchaseOrder> {

    private final CustomerResourceClient customerClient;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            ModelMapper modelMapper,
            CustomerResourceClient customerClient) {

        super(repository, modelMapper);

        this.customerClient = customerClient;
    }

    @Override
    protected PurchaseOrder mapEntityToValueObject(PurchaseOrderEntity customer) {

        var purchase = super.mapEntityToValueObject(customer);

        purchase.setCustomer(this.customerClient.findById(customer.getCustomerId()));

        return purchase;
    }
}
