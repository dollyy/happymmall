package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * create by Yang chen
 */

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param session 当前用户信息
     * @return ServerResponse<User>
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //service
        ServerResponse<User> response=iUserService.login(username, password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 退出登录
     * @param session 当前用户信息
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册
     * @param user 注册用户信息
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验用户名和邮箱
     * @param str 值
     * @param type 类型，用户名or邮箱
     * @return
     */
    @RequestMapping(value = "checkValid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @param session 当前用户信息
     * @return
     */
    @RequestMapping(value = "getUserInfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null != user){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    /**
     * 忘记密码获取密保问题
     * @param username 当前用户名
     * @return
     */
    @RequestMapping(value = "forgetGetQuestion.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 校验密保答案
     * @param username 当前用户名
     * @param question 密保问题
     * @param answer 密保答案
     * @return
     */
    @RequestMapping(value = "forgetCheckAnswer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 重置密码
     * @param username 当前用户名
     * @param passwordNew 新密码
     * @param forgetToken 重置密码的token
     * @return
     */
    @RequestMapping(value = "forgetResetPassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态重置密码
     * @param session 当前用户信息
     * @return
     */
    @RequestMapping(value = "resetPassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == user){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user,passwordOld,passwordNew);
    }

    /**
     *
     * @param session 当前用户信息
     * @param user 更新后用户的信息
     * @return
     */
    @RequestMapping(value = "updateInformation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == currentUser){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //id不能更新
        user.setId(currentUser.getId());
        //username不能更新
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response=iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     *
     * @param session 当前用户信息
     * @return
     */
    @RequestMapping(value = "getInformation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(null == currentUser){    //未登录则强制登录
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要强制登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }

}
