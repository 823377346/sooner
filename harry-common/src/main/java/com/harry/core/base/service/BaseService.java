package com.harry.core.base.service;


import com.harry.core.base.dto.BaseDTO;
import com.harry.core.base.repository.BaseRepository;
import com.harry.core.base.service.mapper.BaseMapper;
import com.harry.core.http.rest.errors.BadRequestAlertException;

import com.harry.core.jpa.criteria.GenericSpecification;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Service class for managing Entity.
 *
 * @author Tony Luo 2019-11-19
 */

@Transactional(rollbackFor = Exception.class)
public class BaseService<T, D, ID> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BaseRepository<T, ID> baseRepository;
    private final BaseMapper<T, D> baseMapper;

    public BaseService(BaseRepository<T, ID> baseRepository, BaseMapper<T, D> baseMapper) {
        this.baseRepository = baseRepository;
        this.baseMapper = baseMapper;
    }

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames="#id", keyGenerator="myKeyGenerator")
    public Optional<T> findById(ID id) {
        Objects.requireNonNull(id, "id must not be null.");
        return baseRepository.findById(id);
    }

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    @Transactional(readOnly = true)
    public Iterable<T> findAll() {
        return baseRepository.findAll();
    }

//    /**
//     * Returns all instances of the type with the given IDs.
//     *
//     * @param ids
//     * @return
//     */
//    @Transactional(readOnly = true)
//    @Cacheable(cacheNames = "#ids",keyGenerator = "wiselyKeyGenerator")
//    public Iterable<T> findAllById(Iterable<ID> ids) {
//        return baseRepository.findAllById(ids);
//    }

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "#id")
    public boolean existsById(ID id) {
        Objects.requireNonNull(id, "id must not be null.");
        return baseRepository.existsById(id);
    }


//    /**
//     * Deletes a given entity.
//     *
//     * @param entity
//     * @throws IllegalArgumentException in case the given entity is {@literal null}.
//     */
//    @CacheEvict(cacheNames = "#entity.id", keyGenerator = "wiselyKeyGenerator")
//    public <S extends T> void delete(S entity) {
//        Objects.requireNonNull(entity, "entity must not be null.");
//        baseRepository.delete(entity);
//    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation
     * might have changed the entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     */
    @CachePut(cacheNames = "#entity.id")
    public <S extends T> S save(S entity) {

        Objects.requireNonNull(entity, "Entity must not be null.");

        return baseRepository.save(entity);
    }


//    /**
//     * Saves all given entities.
//     *
//     * @param entities must not be {@literal null}.
//     * @return the saved entities; will never be {@literal null}.
//     * @throws IllegalArgumentException in case the given entity is {@literal null}.
//     */
//    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
//        Objects.requireNonNull(entities, "entities must not be null.");
//        return baseRepository.saveAll(entities);
//    }

//    /**
//     * Deletes the given entities.
//     *
//     * @param entities
//     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
//     */
//    public void deleteAll(Iterable<? extends T> entities) {
//        Objects.requireNonNull(entities, "entities must not be null.");
//        baseRepository.deleteAll(entities);
//    }

    /**
     * Create a new entity with dto. Use the returned instance for further operations.
     *
     * @param dto
     * @return the new entity; will never be {@literal null}.
     */
    public <S extends T> S create(D dto) {

        Objects.requireNonNull(dto, "DTO must not be null.");

        BaseDTO<ID> baseDTO = (BaseDTO<ID>) dto;
        if (log.isDebugEnabled()) {
            log.debug("request to save DTO : {}", baseDTO);
        }

//        if (baseDTO.getId() != null) {
//            throw new BadRequestAlertException("A new dto cannot already have an ID", "dto",
//                "idexists");
//        }

        baseDTO.setId(null);
        S entity = baseMapper.toEntity((D) baseDTO);
        return this.save(entity);
    }

    /**
     * Updates a given entity. Use the returned instance for further operations as the save
     * operation might have changed the entity instance completely.
     *
     * @param dto must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     */

    public <U extends D, S extends T> S update(U dto) {
        Objects.requireNonNull(dto, "DTO must not be null.");

        BaseDTO<ID> baseDTO = (BaseDTO<ID>) dto;
        if (log.isDebugEnabled()) {
            log.debug("request to update dto : {}", baseDTO);
        }
        if (baseDTO.getId() == null) {
            throw new BadRequestAlertException("A  Entity cannot be updated without an ID",
                "Entity",
                "idBlank");
        }
        if (this.findById(baseDTO.getId()).isPresent()) {
            if (log.isDebugEnabled()) {
                log.debug("Update DTO: {}", baseDTO);
            }

            S entity = baseMapper.toEntity((D) baseDTO);

            return this.save(entity);
        } else {
            throw new BadRequestAlertException("Entity does not exist !", "Entity", "notExists");
        }

    }

    /**
     * modify a given entity. only update the fields which is not null in dto. Use the returned
     * instance for further operations as the save operation might have changed the entity instance
     * completely.
     *
     * @param dto must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     */
    public <U extends D> T modify(U dto) {
        Objects.requireNonNull(dto, "DTO must not be null.");

        BaseDTO<ID> baseDTO = (BaseDTO<ID>) dto;

        if (baseDTO.getId() == null) {
            throw new BadRequestAlertException("A Entity cannot be modified without an ID",
                "Entity",
                "idBlank");
        }
        if (log.isDebugEnabled()) {
            log.debug("Modify Entity: {}", baseDTO);
        }
        return  baseRepository.findById(baseDTO.getId()).map(entity -> {

            baseMapper.toEntity((U)baseDTO, entity);

            return this.save(entity);
        }).orElseGet(() -> {
            throw new BadRequestAlertException("Entity does not exist !", "Entity", "notExists");
        });
    }

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     * @throws BadRequestAlertException in case the given {@code id} can not be found in database
     */
    @CacheEvict(cacheNames = "#id")
    public void deleteById(ID id) {
        Objects.requireNonNull(id, "id must not be null.");
        baseRepository.findById(id).map(entity -> {
            baseRepository.delete(entity);
            if (log.isDebugEnabled()) {
                log.debug("Deleted  Entity: {}", entity);
            }
            return id;
        }).orElseGet(() -> {
            throw new BadRequestAlertException("Entity does not exist !", "Entity", "notExists");
        });

    }


    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code
     * Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    @Transactional(readOnly = true)
//    @CacheEvict(cacheNames = "#id", keyGenerator = "smpkeyGenerator")
    public Page<T> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }


    /**
     * Returns a {@link Page} of entities matching the given where Clause.
     *
     * @param whereClause (id=1 or lastModifiedDate > 20190926) and createdBy=weathertask
     * @param pageable
     * @return a page of entities
     * @throws JSQLParserException
     */
    @Transactional(readOnly = true)
    public Page<T> advancedQuery(String whereClause, Pageable pageable)
        throws JSQLParserException {
        if (StringUtils.isBlank(whereClause)) {
            return this.findAll(pageable);
        }
        GenericSpecification<T> specification = GenericSpecification.of(whereClause);
        return baseRepository.findAll(specification, pageable);

    }

}
