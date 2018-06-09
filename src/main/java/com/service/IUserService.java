package com.service;

import com.common.ServerResponse;
import com.pojo.User;

public interface IUserService {
     ServerResponse<User> login(String userName, String password);

     ServerResponse<String> register(User user);

     ServerResponse<String> checkValue(String str, String type);

     ServerResponse<String> getPasswordPrompt(String userName);
}
