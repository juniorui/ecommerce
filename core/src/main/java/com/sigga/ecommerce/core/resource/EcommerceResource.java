package com.sigga.ecommerce.core.resource;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface EcommerceResource<VO> {

    @ResponseBody
    @GetMapping("/{id}")
    VO findById(@PathVariable UUID id);

    @ResponseBody
    @PostMapping
    UUID save(@RequestBody VO customer);

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void edit(@PathVariable UUID id, @RequestBody VO customer);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id);

    @GetMapping
    Page<VO> search(@SpringQueryMap VO example, @SpringQueryMap Pageable pageable);
}
