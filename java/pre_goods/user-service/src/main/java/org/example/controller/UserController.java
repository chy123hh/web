package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.request.UpdateProfileRequest;
import org.example.service.UserService;
import org.example.common.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户注册、登录、个人资料管理、积分/信用分查询接口")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/login")
  @Operation(summary = "用户登录", description = "通过学号和密码登录，返回JWT token")
  public Result login(@RequestBody LoginRequest request) {
    return userService.login(request);
  }

  @PostMapping("/register")
  @Operation(summary = "用户注册", description = "使用学号、密码、昵称注册新用户")
  public Result register(@RequestBody RegisterRequest request) {
    return userService.register(request);
  }

  @GetMapping("/profile")
  @Operation(summary = "获取个人资料", description = "获取当前登录用户的详细资料")
  public Result profile() {
    return userService.profile();
  }

  @PutMapping("/profile")
  @Operation(summary = "更新个人资料", description = "更新用户昵称和头像")
  public Result updateProfile(@RequestBody UpdateProfileRequest request) {
    return userService.updateProfile(request);
  }

  @GetMapping("/points")
  @Operation(summary = "查询积分余额", description = "获取当前用户的积分余额")
  public Result points() {
    return userService.points();
  }

  @GetMapping("/credit")
  @Operation(summary = "查询信用分", description = "获取当前用户的信用分及是否可接单")
  public Result credit() {
    return userService.credit();
  }

}