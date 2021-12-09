package com.sigga.ecommerce.crm.customer;

import com.sigga.ecommerce.core.entity.EcommerceEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Entity
@Table(name = "customer")
public class CustomerEntity implements EcommerceEntity {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "hibernate-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(length = 100)
    private String name;

    @NotNull
    @Size(max = 150)
    @Column(length = 150)
    private String email;
}
