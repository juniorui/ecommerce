package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.entity.EcommerceEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "purchase_order")
public class PurchaseOrderEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @Column(name = "customer_id")
    private UUID customerId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchaseOrder")
    private List<PurchaseOrderProductEntity> products;

    @PreUpdate
    @PrePersist
    private void preMerge() {

        if (this.products != null) {

            this.products.forEach(p -> p.setPurchaseOrder(this));
        }
    }
}
