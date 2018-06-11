package com.service;

import com.common.ServerResponse;
import com.pojo.User;

public interface IUserService {
     ServerResponse<User> login(String userName, String password);

     ServerResponse<String> register(User user);

     ServerResponse<String> checkValue(String str, String type);

     ServerResponse<String> getPasswordPrompt(String userName);

     ServerResponse<String> checkQuestion(String userName, String question, String answer);

     ServerResponse<String> resetPassword(String userName, String newPassword, String token);

     ServerResponse<String> loginResetPassword(String oldPassword, String newPassword, User user);

     ServerResponse<User> updateUserInfo(User user);

     ServerResponse<User> getUserInfoById(Integer userId);
}
