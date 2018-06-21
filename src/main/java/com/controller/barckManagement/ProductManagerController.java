package com.controller.barckManagement;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.Product;
import com.pojo.User;
import com.service.IFileService;
import com.service.IProductService;
import com.service.IUserService;
import com.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2018/6/13 22:12
 * @Description: 产品管理
 */
@Controller
@RequestMapping(value = "/manage/product/")
public class ProductManagerController {

    @Autowired
    IProductService iProductService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IFileService iFileService;
    
    /**
     * Description: 产品保存
     * CreateDate: 2018/6/13 22:18
     * 
    */
    @RequestMapping(value = "productSave.do")
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            // 增加产品
            return iProductService.saveOrUpdateProduct(product);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }
    
    /**
     * Description: 修改产品状态
     * CreateDate: 2018/6/13 23:06
     * 
    */
    public ServerResponse editProductStatus(Integer productId, Integer status, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            return iProductService.editProductStatus(productId, status);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }
    
    /**
     * Description: 获取商品详情
     * CreateDate: 2018/6/15 20:30
     * 
    */
    @RequestMapping(value = "getProductDetail.do")
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            return iProductService.manageGetProductDetail(productId);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * Description: 后台获取商品列表
     * CreateDate: 2018/6/16 15:13
     * 
    */
    @RequestMapping(value = "getProductList.do")
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            return iProductService.getProductList(pageNumber, pageSize);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * Description: 商品搜索
     * CreateDate: 2018/6/16 16:06
     * 
    */
    @RequestMapping(value = "productSearch.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "12") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            return iProductService.productSearch(pageNumber, pageSize, productId, productName);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }
    
    /**
     * Description: 文件上传
     * CreateDate: 2018/6/16 22:10
     * 
    */
    // RequestParam 对应from的name
    @RequestMapping(value = "fileUpload.do")
    @ResponseBody
    public ServerResponse fileUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest servletRequest) {
        // 用户验证,防止恶意上传文件
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            String path = servletRequest.getSession().getServletContext().getRealPath("upload");
            // 上传文件
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = new HashMap();
            fileMap.put("fileName", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }
        else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }
    
    /**
     * Description: 富文本编辑器图片上传
     * CreateDate: 2018/6/18 22:28
     * 
    */
    // 富文本编辑器对于返回值拥有格式要求simditor
    @RequestMapping(value = "rich_img_upload.do")
    @ResponseBody
    public Map richTextImageUploag(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest servletRequest, HttpServletResponse response) {
        Map resultMap = new HashMap();
        // 用户验证,防止恶意上传文件
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录");
            return resultMap;
        }
        if(iUserService.checkUserIsAdmin(user).isSuccess()) {
            String path = servletRequest.getSession().getServletContext().getRealPath("upload");
            // 上传文件
            String targetFileName = iFileService.upload(file, path);
            if(StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-name");
            return resultMap;
        }
        else {
            resultMap.put("success", false);
            resultMap.put("msg", "没有权限");
            return resultMap;
        }
    }

}
