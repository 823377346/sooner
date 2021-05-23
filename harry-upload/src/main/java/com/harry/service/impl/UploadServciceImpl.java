package com.harry.service.impl;

import com.harry.ServiceClient.DrawingServiceClient;
import com.harry.common.pojo.vo.ResponseVo;
import com.harry.config.ConfigMsg;
import com.harry.entity.DrawingRequestParamVo;
import com.harry.entity.DrawingResponseParamVo;
import com.harry.entity.DrawingsInfoEntity;
import com.harry.service.UploadServcice;
import com.harry.utils.UUIDUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: HuYi.Zhang
 * @create: 2018-05-30 11:26
 **/
@Service
public class UploadServciceImpl implements UploadServcice {

    //private static final List<String> allowTypes = Arrays.asList(".dwg", ".dxf",".dws",".dwt",".jpg",".png");//dwg、dxf、dws、dwt


	@Autowired
	private DrawingServiceClient drawingServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(UploadServciceImpl.class);
	@Autowired
    private  ConfigMsg configMsg;

    @Override
    public List upload(MultipartFile[] files,String moduleName,HttpServletRequest request) throws ApplicationException, IOException {
    	List urlList = new ArrayList();
		String suffix;
		//String serverUrl = request.getScheme()+"://"+ request.getServerName()+":"+ request.getServerPort() +request.getContextPath();
		String fileServerPath = configMsg.getFileServerPath();
			for (MultipartFile multipartFile:files) {
				//判断文件上传大小
				if(multipartFile.isEmpty()){
					urlList.clear();
					return null;
				}
				//判断格式
				String originalFilename = multipartFile.getOriginalFilename();
				int pos = originalFilename.lastIndexOf(".");
				//原文件后缀名
				suffix = originalFilename.substring(pos);
				String[] format = configMsg.getImgUploadFormat().split(",");
				if(!Arrays.asList(format).contains(suffix)){
					urlList.clear();
					return null;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String newTime = sdf.format(new Date());

				String dataPath =  moduleName + File.separator + newTime + File.separator;
				String uploadUrl = configMsg.getDrawingBaseDownloadUrl()+dataPath;

				String newFile = UUIDUtil.getUUID()+suffix;
				File uploadFile = new File(uploadUrl +File.separator+ newFile);
				File uploadUrlFile = new File(configMsg.getDrawingBaseDownloadUrl()+dataPath);
				if(!uploadUrlFile.exists()){
					uploadUrlFile.mkdirs();
				}
				multipartFile.transferTo(uploadFile);
				urlList.add(fileServerPath + dataPath + newFile);
			}

		return urlList;
    }

    @Override
    public DrawingResponseParamVo drawingUpload(MultipartFile myFile, String drawingsId, String  userLogin, int  fileType, int drawingVersion, String modifyInformation) throws ApplicationException {

    	DrawingRequestParamVo drawingRequestParamVo = new DrawingRequestParamVo();
		drawingRequestParamVo.setDrawingsId(drawingsId);
		drawingRequestParamVo.setUserLogin(userLogin);
		drawingRequestParamVo.setFileType(fileType);
		drawingRequestParamVo.setDrawingVersion(drawingVersion);

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        DrawingResponseParamVo fileResponseParamVo = new DrawingResponseParamVo(901, "图纸上传成功");
		String data = "";
		File upFile = null;
		try {
			//900、当前账号不具备上传图纸的权限！
			//901、图纸上传成功！
			//902、传递参数数量不完整或存在空值，请保证您所传递的参数是完整的！
			//903、您所传递的附件格式不符合要求！目前仅支持dwg、dxf、dws、dwt四种格式的附件
			//904、登录账号或登录密码不正确！请传入正确的登录账号及登录密码！
			//905、图纸上传失败！

			//判断参数是否符合要求，不符合返回错误信息
			if (myFile == null || drawingRequestParamVo == null || drawingRequestParamVo.getDrawingsId() == null
					|| "".equals(drawingRequestParamVo.getDrawingsId()) || drawingRequestParamVo.getUserLogin() == null || "".equals(drawingRequestParamVo.getUserLogin())) {
				fileResponseParamVo.setReason("传递参数数量不完整或存在空值，请保证您所传递的参数是完整的！");
				fileResponseParamVo.setCode(902);
				return fileResponseParamVo;
			}

			if (myFile.getSize() == 0) {
				fileResponseParamVo.setReason("上传空文件");
				fileResponseParamVo.setCode(907);
				return fileResponseParamVo;
			}

			//获得原来文件名(含后缀名)
			String originalFilename = myFile.getOriginalFilename();
			int pos = originalFilename.lastIndexOf(".");
			//原文件后缀名
			String suffix = originalFilename.substring(pos);
			//dwg、dxf、dws、dwt
			String[] downloadFormatArry = configMsg.getDrawingUploadFormat().split(",");

			if (!Arrays.asList(downloadFormatArry).contains(suffix)) {
				fileResponseParamVo.setReason("您所传递的附件格式不符合要求！目前仅支持dwg、dxf、dws、dwt四种格式的附件");
				fileResponseParamVo.setCode(903);
				return fileResponseParamVo;

			}

			//具体项目路径
			String url = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
			//保存文件
			//String realPath = request.getSession().getServletContext().getRealPath("/image/");
			//
			//服务器上传地址C:\harry

			String realPath = configMsg.getDrawingBaseDownloadUrl();
			//文件名 年月日 + 类型 + 毫秒值
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String newTime = sdf.format(new Date());
			Long newTimeM = System.currentTimeMillis();
			String newFileName = newTime + drawingRequestParamVo.getFileType() + newTimeM;
			String dataPath = fileType + File.separator + newTime;
			realPath = realPath + dataPath;
			//资源服务器路径
			String dataServucePath = configMsg.getFileServerPath() + dataPath + File.separator + newFileName + suffix;
			//上传路径
			String fullPath = realPath + File.separator + newFileName + suffix;
			File newFile = new File(realPath);
			if (!newFile.exists()) {
				newFile.mkdirs();
			}
			upFile = new File(fullPath);
			//上传
			myFile.transferTo(upFile);
			//查询最新版本
			DrawingsInfoEntity drawingsInfoEntityVersion = new DrawingsInfoEntity();
			drawingsInfoEntityVersion.setSerialId(drawingRequestParamVo.getDrawingsId());
			ResponseVo responseVo = drawingServiceClient.listByType(drawingsInfoEntityVersion);
			ArrayList listVersion = (ArrayList) responseVo.getData();
			Map<String,Object> infoEntity = (HashMap<String,Object>)listVersion.get(0);
			Map<String,Object> drawingsInfoEntityMap = (HashMap<String,Object>)infoEntity.get("drawingsInfoEntity");
			int version = (int)drawingsInfoEntityMap.get("drawingVersion")+ 1;
			//附件上传成功，进行保存数据库操作
			//1、根据所传DrawingsID获取图纸相关信息（如：Title等信息）
			DrawingsInfoEntity drawingsInfoEntity = new DrawingsInfoEntity();
			//drawingsInfoEntity.setUrl(dataServucePath);
			drawingsInfoEntity.setUrl(fullPath);
			drawingsInfoEntity.setDrawingName(newFileName);
			drawingsInfoEntity.setDrawingsId(drawingRequestParamVo.getDrawingsId());
			drawingsInfoEntity.setDrawingVersion( version );
			drawingsInfoEntity.setSerialId(UUIDUtil.getUUID());
			drawingsInfoEntity.setCommentInformation(modifyInformation);//批注信息
			drawingServiceClient.save(drawingsInfoEntity);
			//TODO 通知模块
		}catch (Exception e){
			logger.error("图纸上传模块异常："+e.getMessage(),e );
			e.printStackTrace();
			fileResponseParamVo.setCode(905);
			fileResponseParamVo.setReason("接口异常，麻烦联系开发人员，谢谢");
			if(upFile != null){
				upFile.delete();
			}
		}
		return fileResponseParamVo;

    }

	@Override
	public void download(String downloadUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	if(StringUtils.isBlank(downloadUrl)){
    		throw  new RuntimeException("下载路径为null请检查");
		}
		InputStream in = null;
		File file = null;
		OutputStream toClient = null;
		//String a = request.getScheme()+"://"+ request.getServerName()+request.getContextPath();
			try{
				//提供下载文件前进行压缩，即服务端生成压缩文件
//				file = new File(downloadUrl);
//				FileOutputStream fos = new FileOutputStream(file);
//				//ZipUtils.toZip(downloadUrl, fos, true);
//				//1.获取要下载的文件的绝对路径
//				String realPath = downloadUrl;
				//2.获取要下载的文件名
				String fileName = downloadUrl.substring(downloadUrl.lastIndexOf(File.separator)+1);
				response.reset();
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/octet-stream");
				//3.设置content-disposition响应头控制浏览器以下载的形式打开文件

				response.addHeader("Content-Disposition","attachment;filename=" +URLEncoder.encode(fileName, "utf-8"));
				//获取文件输入流
				in = new BufferedInputStream(new FileInputStream(downloadUrl));
				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				toClient = new BufferedOutputStream(response.getOutputStream());
				//将缓冲区的数据输出到客户端浏览器
				IOUtils.write(buffer,toClient);
				//toClient.write(buffer);
			}catch (Exception e){
				e.printStackTrace();
				logger.error("下载文件异常",e);
			}finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(toClient);
			}
	}

	public String getExtension(String fileName){
        return StringUtils.substringAfterLast(fileName,".");
    }
}
