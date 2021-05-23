package com.harry.controller;

import com.harry.common.pojo.vo.ResponseVo;
import com.harry.entity.DrawingResponseParamVo;
import com.harry.service.UploadServcice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-05-30 11:15
 **/
@Api(value="上传下载模块接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadServcice uploadServcice;

    private Logger logger = LoggerFactory.getLogger(UploadController.class);

//    @PostMapping("image")
//    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
//        String url = this.uploadServciceImpl.upload(file);
//        if (StringUtils.isBlank(url)) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//        return ResponseEntity.ok(url);
//    }
    /**
     * 上传图片
     * @author qqg
     * @date 2019/5/6
     * @param moduleName 模块名称
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     */
    @PostMapping("/upload")
    @ApiOperation(value="上传图片", notes="上传图片")
    public ResponseVo upload(@Param("files") MultipartFile[] files, @Param("moduleName")String moduleName, HttpServletRequest request) {
        ResponseVo responseVo = new ResponseVo(200,true);
        try {
            List upload = this.uploadServcice.upload(files, moduleName, request);
            responseVo.setData(upload);
        } catch (Exception e) {
            e.printStackTrace();
            responseVo.setReason("上传图片异常："+ e.getMessage());
            responseVo.setSuccess(true);
            responseVo.setStateCode(500);
            logger.error("上传图片异常："+ e.getMessage() ,e);
        }
        return responseVo;
    }

    /**
     * （中望app）上传图纸
     * @author qqg
     * @date 2019/5/6
     * @param [file]
     * @return org.springframework.http.ResponseEntity<java.lang.String>
     */
    @PostMapping("/drawingUpload")
    @ApiOperation(value="图纸上传模块", notes="图纸上传模块")
    public DrawingResponseParamVo DrawingUpload(@RequestParam("file") MultipartFile file,  @RequestParam("drawingsId") String drawingsId,@RequestParam("userLogin") String userLogin
                        ,@RequestParam("fileType") int fileType,@RequestParam("drawingVersion") int drawingVersion,@RequestParam("modifyInformation")String modifyInformation) throws ApplicationException {
        return this.uploadServcice.drawingUpload(file,drawingsId, userLogin,fileType,drawingVersion,modifyInformation);
    }

    @GetMapping("/download")
    @ApiOperation(value="文件下载", notes="文件下载")
    public void download(@RequestParam("downloadUrl") String downloadUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
         uploadServcice.download(downloadUrl, request, response);
    }
}
