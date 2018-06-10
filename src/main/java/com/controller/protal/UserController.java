package com.controller.protal;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


/**
 * @FunctionName:
 * @Name: luo chuan
 * @Date: 2018/6/9 14:56
 * @Description:
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    IUserService iUserService;

    /**
     * Name: login
     * Description: 用户登陆
     * Author: luo chuan
     * CreateDate: 2018/6/9 14:58
    */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> userLogin(String userName, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(userName, password);
        if(response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * Name: ServerResponse
     * Description: 用户退出
     * Author: luo chuan
     * CreateDate: 2018/6/9 17:20
    */
    @RequestMapping(value = "logout.do")
    @ResponseBody
    public ServerResponse<String> userLogout(HttpSession session) {
        // 移除session
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * Name: userRegister
     * Description: 用户注册
     * Author: luo chuan
     * CreateDate: 2018/6/9 17:34
    */
    @RequestMapping(value = "userRegister.do")
    @ResponseBody
    public ServerResponse<String> userRegister(User user) {
        return iUserService.register(user);
    }

    /**
     * Name: checkValue
     * Description: 检查用户名和Email是否有效
     * Author: luo chuan
     * CreateDate: 2018/6/10 0:53
    */
    @RequestMapping(value = "checkValue.do")
    @ResponseBody
    public ServerResponse<String> checkValue(String str, String type) {
        return iUserService.checkValue(str, type);
    }

    /**
     * Name: getUserInfo
     * Description: 获取用户信息
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:26
    */
    @RequestMapping(value = "getUserInfo.do")
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取信息");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * Name: getPasswordPrompt
     * Description: 获取密码提示
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:43
    */
    @RequestMapping(value = "getPasswordPrompt.do")
    @ResponseBody
    public ServerResponse<String> getPasswordPrompt(String userName) {
        return iUserService.getPasswordPrompt(userName);
    }

    public ServerResponse<String> checkQuestion(String userName, String question, String answer) {
        return iUserService.checkQuestion(userName, question, answer);
    }

}
