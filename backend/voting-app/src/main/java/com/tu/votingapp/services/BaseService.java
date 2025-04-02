package com.tu.votingapp.services;

import java.util.List;

public interface BaseService<T, ID> {
    List<T> findAll();
    T findById(ID id);
    T create(T dto);
    T update(ID id, T dto);
    void delete(ID id);
}