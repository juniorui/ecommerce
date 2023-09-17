package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.service.EcommerceService;
import com.sigga.ecommerce.crm.customer.CustomerResourceClient;
import com.sigga.ecommerce.inventory.product.Product;
import com.sigga.ecommerce.inventory.product.ProductResourceClient;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PurchaseOrderService extends EcommerceService<PurchaseOrderEntity, PurchaseOrder> {

    private final CustomerResourceClient customerClient;

    private final ProductResourceClient productClient;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            ModelMapper modelMapper,
            AmqpTemplate amqpTemplate,
            CustomerResourceClient customerClient,
            ProductResourceClient productClient) {

        super(repository, modelMapper, amqpTemplate);

        this.customerClient = customerClient;
        this.productClient = productClient;
    }

    @Override
    protected PurchaseOrder mapEntityToValueObject(PurchaseOrderEntity entity) {
        var purchase = super.mapEntityToValueObject(entity);
        purchase.setCustomer(this.customerClient.findById(entity.getCustomerId()));

        purchase.getProducts().forEach(product -> {
            var prod = findProductClient(product.getProduct().getId());
            prod.setPrice(product.getPurchasePrice());
            product.setProduct(prod);
            product.setPurchasePrice(null);
        });

        return purchase;
    }

    @Override
    public Product findProductClient(UUID id) {
        return productClient.findById(id);
    }

}
