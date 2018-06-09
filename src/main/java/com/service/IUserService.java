package com.service;

import com.common.ServerResponse;
import com.pojo.User;

public interface IUserService {
     ServerResponse<User> login(String userName, String password);
     ServerResponse<User> register(User user);
}
