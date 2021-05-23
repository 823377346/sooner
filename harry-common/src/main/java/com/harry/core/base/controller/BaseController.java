package com.harry.core.base.controller;

import com.harry.core.base.service.BaseService;
import com.harry.core.base.service.mapper.BaseMapper;
import com.harry.core.http.ResultEntity;
import io.swagger.annotations.ApiParam;
import net.sf.jsqlparser.JSQLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * BaseController
 * includes advancedQuery() method
 * @author Tony Luo 2019-10-09
 */
public class BaseController<T, D, ID> extends CoreController<T, D, ID> {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public BaseController(BaseService baseService, BaseMapper baseMapper) {
        super(baseService, baseMapper);
    }


    /**
     * {@code GET  /adv/{whereClause}} : Advanced Search {@link BaseDTO}.
     *
     * @param whereClause example: id between 1 and 3 or (id=1 or lastModifiedDate > 'yyyy-MM-dd
     *                    HH:mm') and (createdBy=weathertask or createdBy like '*mdi*') 日期格式为：'yyyy-MM-dd
     *                    HH:mm'；日期单引号括起来 模糊查询Like 用星号（*）而不是百分号（%）做模糊匹配；Like 里面的内容用单引号括起来
     *                    前端需要调用encodeURIComponent(whereClause)进行编码;
     *
     * @return the {@link ResultEntity} with status {@code 0 (OK)} and the {@link BaseDTO} in body}.
     * @throws JSQLParserException
     */
//    @ApiOperation(value = "高级查询", notes = "whereClause example: id between 1 and 3 or (id=1 or lastModifiedDate > '2019-09-29 15:33') " +
//            "and (createdBy=weathertask or createdBy like '*mdi*') .\n<b style='color:red'>注意：</b>\n日期格式为：'yyyy-MM-dd HH:mm'，" +
//            "日期需要用单引号括起来；\n模糊查询Like 用星号（*）而不是百分号（%）做模糊匹配；Like 里面的内容需要用单引号括起来；\n" +
//            "request请求为POST时，data 格式为： {\"whereClause\":\"id =2 and (createdBy=weathertask or createdBy like '*mdi*')\"}.\n" +
//            "request请求为GET时，HTML需要调用encodeURIComponent(whereClause)进行编码.\n"
//
//    )
    @RequestMapping(value = {"/adv/{whereClause}", "/adv"}, method = {RequestMethod.GET})
    // @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResultEntity<Page> advancedQuery(
            @ApiParam(required = false, value = "此参数优先级最高，需要用encodeURIComponent转码 ，" +
                    "当whereClause不为空时，会忽略post请求的数据；" +
                    "数据格式：encodeURIComponent(\"id between 1 and 3 or (id=1 or lastModifiedDate > '2019-09-29 15:33')\")"
            )
            @PathVariable(required = false) String whereClause,
            @ApiParam(required = false, value = "当whereClause为空时，才用此参数进行解析查询；" +
                    "数据格式：{\"whereClause\":\"id =2 and (createdBy=weathertask or createdBy like '*mdi*')\"}")
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
            ) throws JSQLParserException {

        Sort createdDate = Sort.by(Sort.Direction.DESC,"lastModifiedDate","createdDate");
        Pageable pageable = PageRequest.of(page == null ? 0:page-1,
                pageSize== null ? 10:pageSize,createdDate);

        return ResultEntity.ok(baseService.advancedQuery(whereClause, pageable)
                .map(entity -> baseMapper.toDTO(entity)));
    }


}
