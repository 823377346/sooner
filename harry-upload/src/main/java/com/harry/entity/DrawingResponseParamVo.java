package com.harry.entity;

import java.io.Serializable;

/**
 * @ClassName @{NAME}
 * @Description
 * @Authorsooner
 * @Date 2019/5/6 15:10
 * @Version 1.0
 **/
public class DrawingResponseParamVo implements Serializable {

    private int code;

    private String reason;

    private String data;

    public DrawingResponseParamVo (){

    }
    public DrawingResponseParamVo(int code,String reason){
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
