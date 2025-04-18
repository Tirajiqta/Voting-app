package com.tu.votingapp.controllers;

import com.tu.votingapp.services.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

public abstract class BaseController<T, ID> {

    protected abstract BaseService<T, ID> getService();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @GetMapping
    public ResponseEntity<List<T>> getAll() {
        List<T> dtos = getService().findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable ID id) {
        T dto = getService().findById(id);
        if(dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T dto) {
        T created = getService().create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable ID id, @RequestBody T dto) {
        T updated = getService().update(id, dto);
        if(updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.noContent().build();
    }
}