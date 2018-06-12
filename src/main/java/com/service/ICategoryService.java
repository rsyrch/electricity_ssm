package com.service;

import com.common.ServerResponse;

/**
 * @FunctionName:
 * @Name: luo chuan
 * @Date: 2018/6/12 21:13
 * @Description:
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse editCategoryName(Integer categoryId, String categoryName);
}
