package com.harry.core.base.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * Spring Data JPA repository for the {@link } entity.
 *
 * @author Tony Luo 2019-10-09
 */

public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

}
