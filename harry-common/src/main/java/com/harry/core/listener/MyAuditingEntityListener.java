package com.harry.core.listener;

import com.alibaba.fastjson.JSONObject;

import com.harry.core.base.domain.AbstractAuditingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;

@Slf4j
public class MyAuditingEntityListener extends AuditingEntityListener {
    @PrePersist
    @PreUpdate
    public void PrePersist(Object entity) {
        AbstractAuditingEntity entity1 = (AbstractAuditingEntity) entity;
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            String userName = "admin";
            if(requestAttributes != null){
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                if(request != null){
                    HttpSession session = request.getSession();
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(session.getAttribute("U")));
                    userName = jsonObject == null ? "admin" : String.valueOf(jsonObject.get("userName"));
                }
            }
            if(entity1.getCreatedBy() == null ){
                entity1.setCreatedBy(userName);
                entity1.setCreatedDate(Instant.now());
            }
            entity1.setLastModifiedBy(userName);
            entity1.setLastModifiedDate(Instant.now());
        }catch (Exception e){
            e.printStackTrace();
            if(entity1.getCreatedBy() == null ){
                entity1.setCreatedBy("admin");
                entity1.setCreatedDate(Instant.now());
            }
            entity1.setLastModifiedBy("admin");
            entity1.setLastModifiedDate(Instant.now());
        }

    }

}
