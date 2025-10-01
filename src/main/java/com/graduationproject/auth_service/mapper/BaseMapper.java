package com.graduationproject.auth_service.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper<E, D> {
    D toDto(E entity);

    default List<D> toDtoList(List<E> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
