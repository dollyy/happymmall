package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        String fileName=file.getOriginalFilename(); //文件名
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);   //扩展名
        String uploadFileName= UUID.randomUUID()+"."+fileExtensionName;   //文件名
        //{}是占位符
        logger.info("***开始上传文件,上传文件的文件名:{},上传的路径:{},新文件:{}",fileName,path,uploadFileName);

        logger.info("************0");
        File fileDir=new File(path);
        logger.info("************1");
        if(!fileDir.exists()){
            logger.info("************not exist");
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        logger.info("************exist");
        File targetFile=new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //至此文件已经成功上传
            //将targetFile上传到FTP服务器上
            FTPUtil.uploadFile(Lists.<File>newArrayList());
            //删除upload下的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }

}
