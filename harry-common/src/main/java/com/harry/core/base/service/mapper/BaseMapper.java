package com.harry.core.base.service.mapper;

/**
 * @author Tony Luo
 */
public interface BaseMapper<T, D> {

    /**
     * convert DTO to Entity
     *
     * @param dto
     * @return the entity which fields' value is the same with dto
     */

    <U extends D, S extends T>S toEntity(U dto);

    /**
     * Set Entity field value with DTO field which value is not null
     *
     * @param dto
     * @param entity
     * @return the entity, the entity fields' value is the same with the dto not null value
     */
    <U extends D, S extends T> S toEntity(U dto, S entity);

    /**
     * convert Entity to DTO
     *
     * @param entity
     * @return the dto
     */
    <U extends D, S extends T> U toDTO(S entity);

}
