package com.harry.core.base.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Tony Luo
 */
public class BaseDTO<ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    private ID id;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Instant createdDate;

    public BaseDTO() {
        // Empty constructor needed for Jackson.
    }

    /**
     * getId
     *
     * @return
     */
    public ID getId() {
        return id;
    }

    /**
     * setId
     *
     * @param id
     */
    public void setId(ID id) {
        this.id = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
