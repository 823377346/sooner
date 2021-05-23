package com.harry.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName @{NAME}
 * @Description
 * @Authorsooner
 * @Date 2019/5/6 15:26
 * @Version 1.0
 **/
@Data
public class DrawingRequestParamVo implements Serializable {
    //图纸id
    private String drawingsId;
    //登陆账号
    private String userLogin;
    //文件类型
    private int fileType;
    //图纸版本
    private Integer drawingVersion;
    //图纸批注与位置信息
    private String modifyInformation;

}
