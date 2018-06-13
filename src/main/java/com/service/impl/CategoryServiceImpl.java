package com.service.impl;

import com.common.ServerResponse;
import com.dao.CategoryMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pojo.Category;
import com.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Set;

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

    // 日志
    private org.slf4j.Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    
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
     * Description: 更新分类名
     * CreateDate: 2018/6/13 11:47
     *
    */
    public ServerResponse editCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)) {
            ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        // updateByPrimaryKeySelective:选择性的更新,更新不为空的字段
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccess("更新分类成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }
    
    /**
     * Description: 查询子分类信息
     * CreateDate: 2018/6/13 16:25
     * 
    */
    public ServerResponse<List<Category>> getChildrenParalleCategory(Integer parentId) {
        List<Category> categoryList = categoryMapper.getCategoryChildrenByParentId(parentId);
        if(CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }
    
    /**
     * Description: 查询当前节点的所有子节点的分类信息
     * CreateDate: 2018/6/13 20:33
     * 
    */
    public ServerResponse selectCategoryAndChildrenById(Integer categoreyId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categoreyId, categorySet);
        List<Integer> categoryList = Lists.newArrayList();
        if(categoreyId != null) {
            for(Category category : categorySet) {
                categoryList.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * Description: 递归得到子节点
     * CreateDate: 2018/6/13 20:38
     * 
    */
    private Set<Category> findChildCategory(Integer categoreyId, Set<Category> categorySet) {
        Category category = categoryMapper.selectByPrimaryKey(categoreyId);
        if(category != null) {
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.getCategoryChildrenByParentId(categoreyId);
        for(Category categoryFor : categoryList) {
            findChildCategory(categoryFor.getId(), categorySet);
        }
        return categorySet;
    }
}
