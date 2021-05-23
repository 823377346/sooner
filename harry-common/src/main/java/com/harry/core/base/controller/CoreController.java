package com.harry.core.base.controller;

import com.harry.core.base.service.BaseService;
import com.harry.core.base.service.mapper.BaseMapper;
import com.harry.core.http.ResultEntity;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * CoreController
 * Only CRUD methods
 * @author Tony Luo 2019-10-09
 */


public class CoreController<T, D, ID> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final BaseService<T, D, ID> baseService;
    protected final BaseMapper<T, D> baseMapper;

    public CoreController(BaseService baseService, BaseMapper<T, D> baseMapper) {
        this.baseService = baseService;
        this.baseMapper = baseMapper;
    }


    /**
     * {@code GET  /entity/:id} : get an {@link BaseDTO} by id.
     *
     * @param id the id of the entity to get.
     * @return the {@link ResultEntity} with status {@code 0 (OK)} and the {@link BaseDTO} in body,
     * or status {@code 40404 (Not Found)}.
     */
    @ApiOperation(value = "根据ID查找", notes = "根据ID查找")
    @GetMapping("/{id:.+}")
    public <U extends D> ResultEntity<U> find(@PathVariable ID id) {
        return ResultEntity
                .wrapOrNotFound(baseService.findById(id).map(entity -> baseMapper.toDTO(entity)));

    }

    /**
     * {@code POST  /entity}  : Creates a new entity.
     * <p>
     * Creates a new entity if the name are not already used
     *
     * @param dto the entity to create.
     * @return the {@link ResultEntity} with status {@code 0 (Created)} and with body the new entity
     * @throws BadRequestAlertException {@code 40400 (Bad Request)} if the name is already in use.
     */
    @PostMapping("/create")
//    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @ApiOperation(value = "新建", notes = "新建")
    public <U extends D> ResultEntity<U> create(@RequestBody U dto) {

        T entity = baseService.create(dto);
        U newDTO = baseMapper.toDTO(entity);
        return ResultEntity.ok(newDTO);

    }

    /**
     * {@code POST  /base}  : Update a  entity.
     * <p>
     * Update entity if the id exits
     *
     * @param dto the entity to update.
     * @return the {@link ResultEntity} with status {@code 0 (Created)} and with body the new
     * entity, or with status {@code 40400 (Bad Request)} if the id is null.
     * @throws BadRequestAlertException {@code 40400 (Bad Request)} if the Entity does not exist.
     */
    @PutMapping("/update")
//    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @ApiOperation(value = "修改", notes = "修改")
    public <U extends D> ResultEntity update(@RequestBody U dto) {
        T entity = baseService.update(dto);
        U newDTO = baseMapper.toDTO(entity);
        return ResultEntity.ok(newDTO);
    }

    /**
     * {@code POST  /}  : Modify a  entity.
     * <p>
     * Only Modify the DTO not null field for entity if the id exits
     *
     * @param dto the dto to modify.
     * @return the {@link ResultEntity} with status {@code 0 (Created)} and with body the new
     * entity, or with status {@code 40400 (Bad Request)} if the id is null.
     * @throws BadRequestAlertException {@code 40400 (Bad Request)} if the Entity does not exist.
     */
    @PutMapping("/modify")
//    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @ApiOperation(value = "修改部分非空字段，只更新对应DTO里的not null字段", notes = "修改部分非空字段,只更新对应DTO里的not null字段")
    public <U extends D> ResultEntity<U> modify(@RequestBody U dto) {

        final T entity = (T) baseService.modify(dto);
        U newDTO = baseMapper.toDTO(entity);
        return ResultEntity.ok(newDTO);
    }

    /**
     * {@code DELETE /:id} : delete the Entity.
     *
     * @param id the entity id.
     * @return the {@link ResultEntity} with status {@code 0 (SUCCESS)}.
     */
    @ApiOperation(value = "刪除", notes = "刪除")
    @DeleteMapping("/{id:.+}")
//    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResultEntity<String> delete(@PathVariable ID id) {
        if (log.isDebugEnabled()) {
            log.debug("REST request to delete Entity: {}", id);
        }
        baseService.deleteById(id);
        return ResultEntity.success();
    }

    /**
     * {@code GET  /list} : get all {@link BaseDTO}.
     *
     * @return the {@link ResultEntity} with status {@code 0 (OK)} and the {@link BaseDTO} in body}.
     */
    @ApiOperation(value = "获取列表", notes = "获取列表")
    @GetMapping("/list")
    // @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResultEntity<Page> findAll(Pageable pageable) {
        Sort createdDate = Sort.by(Sort.Direction.DESC,"lastModifiedDate","createdDate");
        Pageable pageable1 = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),createdDate);
        return ResultEntity
                .ok(baseService.findAll(pageable1).map(entity -> baseMapper.toDTO(entity)));

    }

}
