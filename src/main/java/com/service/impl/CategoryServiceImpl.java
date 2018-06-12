package com.service.impl;

import com.common.ServerResponse;
import com.dao.CategoryMapper;
import com.pojo.Category;
import com.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @FunctionName:
 * @Name: luo chuan
 * @Date: 2018/6/12 21:14
 * @Description:
 */
@Service(value = "iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;
    
    /**
     * Name: addCategory
     * Description: 增加分类
     * Author: luo chuan
     * CreateDate: 2018/6/12 23:21
    */
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        // 分类可用
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccess("添加成功");
        }
        return ServerResponse.createByErrorMessage("添加失败");
    }

    /**
     * Name: editCategoryName
     * Description: 更新分类
     * Author: luo chuan
     * CreateDate: 2018/6/12 23:50
    */
    public ServerResponse editCategoryName(Integer categoryId, String categoryName) {
        return null;
    }
}
