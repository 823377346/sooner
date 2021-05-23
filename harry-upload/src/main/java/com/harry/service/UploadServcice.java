package com.harry.service;


import com.harry.entity.DrawingResponseParamVo;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName @{NAME}
 * @Description
 * @Authorsooner
 * @Date 2019/5/6 14:39
 * @Version 1.0
 **/
public interface UploadServcice {
    /**
     * 上传
     * @author qqg
     * @date 2019/5/6
     * @param [file]
     * @return java.lang.String
     */
    public List upload(MultipartFile[] files, String moduleName,HttpServletRequest request) throws ApplicationException, IOException;

    /**
     * 图纸上传
     * @author harry
     * @date 2019/5/6
     * @param [file]
     * @return java.lang.String
     */
    public DrawingResponseParamVo drawingUpload(MultipartFile file, String drawingsId, String  userLogin, int  fileType, int drawingVersion, String modifyInformation) throws ApplicationException;

    /**
     * 文件下载
     * @author qqg
     * @date 2019/5/7
     * @param [downloadUrl]
     * @return java.lang.String
     */
    public void download(String downloadUrl, HttpServletRequest request, HttpServletResponse response) throws IOException;


}
