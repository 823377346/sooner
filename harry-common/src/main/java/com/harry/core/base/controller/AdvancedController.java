package com.harry.core.base.controller;

import com.harry.core.base.service.BaseService;
import com.harry.core.base.service.mapper.BaseMapper;
import com.harry.core.http.ResultEntity;
import io.swagger.annotations.ApiParam;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * AdvancedController
 * includes search() common method for elasticsearch
 * @author Tony Luo 2019-10-09
 */

public class AdvancedController <T, D, ID> extends BaseController<T, D, ID> {
    public AdvancedController(BaseService baseService, BaseMapper baseMapper) {
        super(baseService, baseMapper);
    }

    //TODO add elasticsearch common method

    @RequestMapping(value = {"/search/{whereClause}", "/adv"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ResultEntity<Page> search(
            @ApiParam("此参数优先级最高，需要用encodeURIComponent转码 ，" +
                    "当whereClause不为空时，会忽略post请求的数据；" +
                    "数据格式：encodeURIComponent(\"id between 1 and 3 or (id=1 or lastModifiedDate > '2019-09-29 15:33')\")"
            )
            @PathVariable(required = false) String whereClause,
            @ApiParam("当whereClause为空时，才用此参数进行解析查询；" +
                    "数据格式：{\"whereClause\":\"id =2 and (createdBy=weathertask or createdBy like '*mdi*')\"}")
            @RequestBody(required = false) Map<String, String> params,
            Pageable pageable) throws JSQLParserException {
        if (StringUtils.isBlank(whereClause) && params != null) {
            whereClause = params.get("whereClause");
        }
        //TODO add elasticsearch common method
        return null;

//        return ResultEntity.ok(baseService.advancedQuery(whereClause, pageable)
//                .map(entity -> baseMapper.toDTO(entity)));
    }
}
