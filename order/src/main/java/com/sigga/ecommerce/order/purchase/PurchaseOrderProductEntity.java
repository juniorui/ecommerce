package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.entity.EcommerceEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "purchase_order_product")
public class PurchaseOrderProductEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrderEntity purchaseOrder;

    @NotNull
    @Column(name = "product_id")
    private UUID productId;

    @Column
    private Integer quantity;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;
}
