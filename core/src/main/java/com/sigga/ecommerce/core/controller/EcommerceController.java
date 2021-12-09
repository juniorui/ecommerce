package com.sigga.ecommerce.core.controller;

import com.sigga.ecommerce.core.entity.EcommerceEntity;
import com.sigga.ecommerce.core.exception.ResourceNotFoundException;
import com.sigga.ecommerce.core.resource.EcommerceResource;
import com.sigga.ecommerce.core.service.EcommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
public class EcommerceController<E extends EcommerceEntity, VO> implements EcommerceResource<VO> {

    private final EcommerceService<E, VO> service;

    public VO findById(UUID id) {

        return this.service.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public UUID save(@Valid VO valueObject) {

        return this.service.save(valueObject);
    }

    public void edit(UUID id, VO valueObject) {

        this.service.edit(id, valueObject);
    }

    public void delete(UUID id) {

        this.service.delete(id);
    }

    public Page<VO> search(VO example, Pageable pageable) {

        return this.service.search(example, pageable);
    }
}
