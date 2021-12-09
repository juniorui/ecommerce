package com.sigga.ecommerce.core.service;

import com.sigga.ecommerce.core.entity.EcommerceEntity;
import com.sigga.ecommerce.core.exception.ResourceNotFoundException;
import com.sigga.ecommerce.core.repository.EcommerceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class EcommerceService<T extends EcommerceEntity, VO> {

    private final EcommerceRepository<T> repository;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Optional<VO> findById(UUID id) {

        return this.repository.findById(id).map(this::mapEntityToValueObject);
    }

    @Transactional
    public UUID save(VO valueObject) {

        return this.repository.save(this.mapValueObjectToEntity(valueObject)).getId();
    }

    @Transactional
    public void edit(UUID id, VO valueObject) {

        var customerEntity = this.mapValueObjectToEntity(valueObject);

        customerEntity.setId(id);

        this.repository.save(customerEntity);
    }

    public void delete(UUID id) {

        ResourceNotFoundException.throwIf(!this.repository.existsById(id));

        this.repository.deleteById(id);
    }

    public Page<VO> search(VO valueObject, Pageable pageable) {

        var exampleMatcher = ExampleMatcher.matchingAll()

                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)

                .withIgnoreCase();

        var example = Example.of(this.mapValueObjectToEntity(valueObject), exampleMatcher);

        return this.repository.findAll(example, pageable)

                .map(entity -> this.modelMapper.map(entity, getValueObjectClass()));
    }

    protected VO mapEntityToValueObject(T customer) {

        return this.modelMapper.map(customer, this.getValueObjectClass());
    }

    protected T mapValueObjectToEntity(VO valueObject) {

        return this.modelMapper.map(valueObject, this.getEntityClass());
    }

    @SuppressWarnings("unchecked")
    protected Class<VO> getValueObjectClass() {

        return (Class<VO>) Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(this.getClass(), EcommerceService.class))[1];
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {

        return (Class<T>) Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(this.getClass(), EcommerceService.class))[0];
    }
}
