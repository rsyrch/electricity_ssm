package com.controller.barckManagement;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.ICategoryService;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @FunctionName: CategoryController
 * @Name: luo chuan
 * @Date: 2018/6/12 20:50
 * @Description: 分类管理
 */
@Controller
@RequestMapping(value="/manage/category")
public class CategoryController {

    @Autowired
    IUserService iUserService;

    @Autowired
    ICategoryService iCategoryService;

    /**
     * Name: addCategory
     * Description: 增加品类
     * Author: luo chuan
     * CreateDate: 2018/6/12 20:55
    */
    // @RequestParam : 如果parentId为空,默认值为0
    @RequestMapping(value = "addCategory")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value="parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        // 检验是否为管理员
        ServerResponse response = iUserService.checkUserIsAdmin(user);
        if(response.isSuccess()) {
            // 增加分类
            return iCategoryService.addCategory(categoryName, parentId);
        }
        else {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        }
    }

    
    /**
     * Name: editCategoryName
     * Description: 更新分类
     * Author: luo chuan
     * CreateDate: 2018/6/12 23:46
    */
    @RequestMapping(value = "editCategoryName")
    @ResponseBody
    public ServerResponse editCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        // 检验是否为管理员
        ServerResponse response = iUserService.checkUserIsAdmin(user);
        if(response.isSuccess()) {
            // 修改分类
            return iCategoryService.editCategoryName(categoryId, categoryName);
        }
        else {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        }
    }
    
    /**
     * Description: 根据id查询下级的所有分类的信息
     * CreateDate: 2018/6/13 15:40
     * 
    */
    @RequestMapping(value = "getChildrenParalleCategoryById.do")
    @ResponseBody
    public ServerResponse getChildrenParalleCategoryById(HttpSession session, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        // 检验是否为管理员
        ServerResponse response = iUserService.checkUserIsAdmin(user);
        if(response.isSuccess()) {
            // 查询分类
            return iCategoryService.getChildrenParalleCategory(parentId);
        }
        else {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        }
    }

    /**
     * Description: 查询当前节点和所有子节点的分类信息
     * CreateDate: 2018/6/13 20:27
     * 
    */
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        // 检验是否为管理员
        ServerResponse response = iUserService.checkUserIsAdmin(user);
        if(response.isSuccess()) {
            // 递归查询当前节点和子节点的分类信息
            // 子节点个数不定，递归查询
            return iCategoryService.selectCategoryAndChildrenById(parentId);
        }
        else {
            return ServerResponse.createByErrorMessage("非管理员,无权限操作");
        }

    }

}
