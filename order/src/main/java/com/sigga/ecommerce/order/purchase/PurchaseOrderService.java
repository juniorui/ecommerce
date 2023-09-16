package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.service.EcommerceService;
import com.sigga.ecommerce.crm.customer.CustomerResourceClient;
import com.sigga.ecommerce.product.ProductResourceClient;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderService extends EcommerceService<PurchaseOrderEntity, PurchaseOrder> {

    private final CustomerResourceClient customerClient;

    private final ProductResourceClient productClient;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            ModelMapper modelMapper,
            CustomerResourceClient customerClient, ProductResourceClient productClient) {

        super(repository, modelMapper);

        this.customerClient = customerClient;
        this.productClient = productClient;
    }

    @Override
    protected PurchaseOrder mapEntityToValueObject(PurchaseOrderEntity entity) {

        var purchase = super.mapEntityToValueObject(entity);

        purchase.setCustomer(this.customerClient.findById(entity.getCustomerId()));
        purchase.getProducts().forEach(product ->
                product.setProduct(this.productClient.findById(product.getProduct().getId())));

        return purchase;
    }
}
