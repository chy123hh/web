package org.example.controller;

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
@RequestMapping("/api/user")  // 修改路径以匹配 Feign Client
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/login")
  public Result login(@RequestBody LoginRequest request) {
    return userService.login(request);
  }

  @PostMapping("/register")
  public Result register(@RequestBody RegisterRequest request) {
    return userService.register(request);
  }

  @GetMapping("/profile")
  public Result profile() {
    return userService.profile();
  }

  @PutMapping("/profile")
  public Result updateProfile(@RequestBody UpdateProfileRequest request) {
    return userService.updateProfile(request);
  }

  @GetMapping("/points")
  public Result points() {
    return userService.points();
  }

  @GetMapping("/credit")
  public Result credit() {
    return userService.credit();
  }

  /**
   * 根据用户 ID 获取用户信息（供其他服务调用）
   */
  @GetMapping("/{userId}/profile")
  public Result getUserProfile(@PathVariable("userId") Long userId) {
    log.info("Feign 调用：获取用户信息，userId: {}", userId);
    return userService.getUserProfileById(userId);
  }

  /**
   * 根据学号获取用户信息（供其他服务调用）
   */
  @GetMapping("/student/{studentId}")
  public Result getUserByStudentId(@PathVariable("studentId") String studentId) {
    log.info("Feign 调用：根据学号获取用户信息，studentId: {}", studentId);
    return userService.getUserByStudentId(studentId);
  }

}
