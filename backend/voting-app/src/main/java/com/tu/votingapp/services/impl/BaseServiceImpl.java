package com.tu.votingapp.services.impl;

import com.tu.votingapp.services.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<E, D, ID> implements BaseService<D, ID> {

    // Subclasses must provide the repository for the entity.
    protected abstract JpaRepository<E, ID> getRepository();

    // Subclasses must provide a mapper: convert entity to DTO.
    protected abstract D toDto(E entity);

    // Subclasses must provide a mapper: convert DTO to entity.
    protected abstract E toEntity(D dto);

    @Override
    public List<D> findAll() {
        return getRepository().findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public D findById(ID id) {
        E entity = getRepository().findById(id).orElse(null);
        return entity != null ? toDto(entity) : null;
    }

    @Override
    public D create(D dto) {
        E entity = toEntity(dto);
        E saved = getRepository().save(entity);
        return toDto(saved);
    }

    @Override
    public D update(ID id, D dto) {
        if (!getRepository().existsById(id)) {
            return null;
        }
        E entity = toEntity(dto);
        E updated = getRepository().save(entity);
        return toDto(updated);
    }

    @Override
    public void delete(ID id) {
        getRepository().deleteById(id);
    }
}
