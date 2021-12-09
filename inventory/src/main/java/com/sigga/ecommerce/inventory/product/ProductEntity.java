package com.sigga.ecommerce.inventory.product;

import com.sigga.ecommerce.core.entity.EcommerceEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "product")
public class ProductEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(length = 100)
    private String name;

    @Column
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
}
