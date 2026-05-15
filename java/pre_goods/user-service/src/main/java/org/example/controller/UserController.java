package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.request.UpdateProfileRequest;
import org.example.handler.UserBlockHandler;
import org.example.service.UserService;
import org.example.common.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/login")
  @SentinelResource(
      value = "user_login",
      blockHandlerClass = UserBlockHandler.class,
      blockHandler = "loginBlockHandler"
  )
  public Result login(@RequestBody LoginRequest request) {
    return userService.login(request);
  }

  @PostMapping("/register")
  @SentinelResource(
      value = "user_register",
      blockHandlerClass = UserBlockHandler.class,
      blockHandler = "registerBlockHandler"
  )
  public Result register(@RequestBody RegisterRequest request) {
    return userService.register(request);
  }

  @GetMapping("/profile")
  @SentinelResource(
      value = "user_profile",
      blockHandlerClass = UserBlockHandler.class,
      blockHandler = "profileBlockHandler"
  )
  public Result profile() {
    return userService.profile();
  }

  @PutMapping("/profile")
  @SentinelResource(
      value = "user_update_profile"
  )
  public Result updateProfile(@RequestBody UpdateProfileRequest request) {
    return userService.updateProfile(request);
  }

  @GetMapping("/points")
  @SentinelResource(
      value = "user_points",
      blockHandlerClass = UserBlockHandler.class,
      blockHandler = "pointsBlockHandler"
  )
  public Result points() {
    return userService.points();
  }

  @GetMapping("/credit")
  @SentinelResource(
      value = "user_credit",
      blockHandlerClass = UserBlockHandler.class,
      blockHandler = "creditBlockHandler"
  )
  public Result credit() {
    return userService.credit();
  }

  @GetMapping("/{userId}/profile")
  @SentinelResource(
      value = "user_get_profile_by_id"
  )
  public Result getUserProfile(@PathVariable("userId") Long userId) {
    log.info("Feign 调用：获取用户信息，userId: {}", userId);
    return userService.getUserProfileById(userId);
  }

  @GetMapping("/student/{studentId}")
  @SentinelResource(
      value = "user_get_by_student_id"
  )
  public Result getUserByStudentId(@PathVariable("studentId") String studentId) {
    log.info("Feign 调用：根据学号获取用户信息，studentId: {}", studentId);
    return userService.getUserByStudentId(studentId);
  }

}
