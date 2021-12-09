package com.sigga.ecommerce.core.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.UUID;

@NoRepositoryBean
public interface EcommerceRepository<T> extends PagingAndSortingRepository<T, UUID>, JpaSpecificationExecutor<T>, QueryByExampleExecutor<T> {

}
