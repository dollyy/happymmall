package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * create by Yang chen
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加品类
     * @param session 当前用户
     * @param categoryName 品类名
     * @param parentId 父级id
     * @return
     */
    @RequestMapping("addCategory.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,
                                      @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        //校验是否是管理员
        ServerResponse response=iUserService.checkAdminRole(user);
        if(response.isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return ServerResponse.createByErrorMessage("非管理员");
        }
    }

    /**
     * 更新品类名
     * @param session 当前用户
     * @param categoryId 品类id
     * @param categoryName 品类名
     * @return
     */
    @RequestMapping("updateCategoryName.do")
    @ResponseBody
    public ServerResponse updateCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServerResponse.createByErrorMessage("非管理员");
        }
    }

    /**
     * 查询子节点category的信息，不递归，保持平级
     * @param session 当前用户
     * @param categoryId 品类id
     * @return
     */
    @RequestMapping("getChildrenParallelCategory.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("非管理员");
        }
    }

    /**
     * 当前节点的id和递归子节点的id
     * @param session 当前用户
     * @param categoryId 品类id
     * @return
     */
    @RequestMapping("getCategoryAndChildrenCategory.do")
    @ResponseBody
    public ServerResponse getCategoryAndChildrenCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("非管理员");
        }
    }

}
