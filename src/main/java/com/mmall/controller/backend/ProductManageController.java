package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 保存 or 更新产品
     * @param session 当前用户
     * @param product 产品
     * @return
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("非管理员，不得操作");
        }
    }

    /**
     * 更改产品状态
     * @param session 当前用户
     * @param productId 产品id
     * @param status 产品状态
     * @return
     */
    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("非管理员，不得操作");
        }
    }

    /**
     * 获取产品详细信息
     * @param session 当前用户
     * @param productId 产品id
     * @return
     */
    @RequestMapping(value = "detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("非管理员，不得操作");
        }
    }

    /**
     * 获取产品列表
     * @param session 当前用户
     * @param pageNum 页码
     * @param pageSize 一页显示上平的个数
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum, pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员，不得操作");
        }
    }

    /**
     * 查询产品
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "search.do")
    @ResponseBody
    public ServerResponse search(HttpSession session, String productName, Integer productId,
                                 @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }else{
            return ServerResponse.createByErrorMessage("非管理员，不得操作");
        }
    }

    /**
     * 上传文件
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, MultipartFile file, HttpServletRequest request){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            System.out.println("***********"+file+","+path);
            String targetFileName=iFileService.upload(file, path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessage("非管理员，不得操作");
        }
    }

    /**
     * 富文本上传图片
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "richtext_ing_upload.do")
    @ResponseBody
    public Map<String,Object> richtextIngUpload(HttpSession session, MultipartFile file, HttpServletRequest request , HttpServletResponse response){
        Map<String,Object> resultMap=Maps.newHashMap();
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回,返回值如下
        //        {
        //            "success": true/false,
        //                "msg": "error message", # optional
        //            "file_path": "[real file path]"
        //        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file, path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",false);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.setHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }

}
