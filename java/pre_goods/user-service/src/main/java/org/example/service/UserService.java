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
}