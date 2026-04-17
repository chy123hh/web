package org.example.controller;

import org.example.model.UserRequest;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import org.example.model.User;
import org.example.service.UserService;
import org.example.utils.Result;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
  @Resource
  private UserService userService;

  @PostMapping("/login")
  public Result login(@RequestBody UserRequest request) {
    return userService.login(request);
  }

  @PostMapping("/register")
  public Result register(@RequestBody UserRequest request) {
    return userService.register(request);
  }

  @GetMapping("/profile")
  public Result profile() {
    return userService.profile();
  }

  @PutMapping("/profile")
  public Result updateProfile(@RequestBody UserRequest request) {
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
}