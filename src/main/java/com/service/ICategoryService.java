package com.service;

import com.common.ServerResponse;
import com.pojo.Category;

import java.util.List;

/**
 * @FunctionName:
 * @Name: luo chuan
 * @Date: 2018/6/12 21:13
 * @Description:
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse editCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParalleCategory(Integer parentId);

    ServerResponse selectCategoryAndChildrenById(Integer categoreyId);
}
