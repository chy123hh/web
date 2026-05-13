package org.example.service;

import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.request.UpdateProfileRequest;
import org.example.common.dto.Result;

public interface UserService {

  Result login(LoginRequest request);

  Result register(RegisterRequest request);

  Result profile();

  Result updateProfile(UpdateProfileRequest request);

  Result points();

  Result credit();

  /**
   * 根据用户 ID 获取用户信息（供 Feign 调用）
   */
  Result getUserProfileById(Long userId);

  /**
   * 根据学号获取用户信息（供 Feign 调用）
   */
  Result getUserByStudentId(String studentId);
}