package org.example.service.impl;

import org.example.common.dto.Result;
import org.example.common.util.JwtUtil;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.request.UpdateProfileRequest;
import org.example.dto.response.CreditResponse;
import org.example.dto.response.LoginResponse;
import org.example.dto.response.UserProfileResponse;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Resource
  private UserMapper userMapper;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private JwtUtil jwtUtil;

  @Override
  public Result login(LoginRequest request) {
    log.info("用户登录，学号：{}", request.getStudentId());

    User dbUser = userMapper.selectByStudentId(request.getStudentId());

    if (dbUser == null) {
      log.error("用户不存在，学号：{}", request.getStudentId());
      return Result.error(2001, "学号或密码错误");
    }

    if (!passwordEncoder.matches(request.getPassword(), dbUser.getPassword())) {
      log.error("密码错误，学号：{}", request.getStudentId());
      return Result.error(2001, "学号或密码错误");
    }

    String token = jwtUtil.generateToken(dbUser.getId());

    log.info("登录成功，用户ID：{}", dbUser.getId());
    return Result.success(LoginResponse.builder()
        .token(token)
        .userId(dbUser.getId())
        .studentId(dbUser.getStudentId())
        .nickname(dbUser.getNickname())
        .build());
  }

  @Override
  public Result register(RegisterRequest request) {
    log.info("用户注册，学号：{}", request.getStudentId());

    if (userMapper.existsByStudentId(request.getStudentId())) {
      log.error("学号已存在，学号：{}", request.getStudentId());
      return Result.error(2001, "学号已存在");
    }

    User user = new User();
    user.setStudentId(request.getStudentId());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setNickname(request.getNickname());
    user.setPoints(0);
    user.setCreditScore(100);
    user.setRole(User.ROLE_NORMAL);
    user.setStatus(User.STATUS_NORMAL);
    user.setCreateTime(LocalDateTime.now());
    user.setUpdateTime(LocalDateTime.now());

    userMapper.insert(user);

    log.info("用户注册成功，用户ID：{}", user.getId());
    return Result.success(user.getId());
  }

  @Override
  public Result profile() {
    log.info("用户资料查询");

    Long userId = getCurrentUserId();

    User user = userMapper.selectById(userId);

    if (user == null) {
      return Result.error(2001, "用户不存在");
    }

    return Result.success(UserProfileResponse.builder()
        .userId(user.getId())
        .studentId(user.getStudentId())
        .nickname(user.getNickname())
        .avatarUrl(user.getAvatarUrl())
        .points(user.getPoints())
        .creditScore(user.getCreditScore())
        .role(user.getRole())
        .status(user.getStatus())
        .createTime(user.getCreateTime())
        .build());
  }

  @Override
  public Result updateProfile(UpdateProfileRequest request) {
    log.info("用户资料更新");

    Long userId = getCurrentUserId();

    User user = userMapper.selectById(userId);

    if (user == null) {
      return Result.error(2001, "用户不存在");
    }

    if (request.getNickname() != null) {
      user.setNickname(request.getNickname());
    }
    if (request.getAvatarUrl() != null) {
      user.setAvatarUrl(request.getAvatarUrl());
    }
    user.setUpdateTime(LocalDateTime.now());

    userMapper.updateById(user);

    log.info("用户资料更新成功，用户ID：{}", userId);
    return Result.success("用户资料更新成功");
  }

  @Override
  public Result points() {
    log.info("用户积分查询");

    Long userId = getCurrentUserId();

    User user = userMapper.selectById(userId);

    if (user == null) {
      log.error("用户不存在");
      return Result.error(2001, "用户不存在");
    }

    return Result.success(user.getPoints());
  }

  @Override
  public Result credit() {
    log.info("用户信用分查询");

    Long userId = getCurrentUserId();

    User user = userMapper.selectById(userId);

    if (user == null) {
      return Result.error(2001, "用户不存在");
    }

    return Result.success(CreditResponse.builder()
        .userId(user.getId())
        .creditScore(user.getCreditScore())
        .canTakeOrder(user.getCreditScore() >= 60)
        .build());
  }

  /**
   * 从 SecurityContext 获取当前登录用户ID
   * JWT过滤器已解析Token并设置认证信息，这里直接获取
   *
   * @return 用户ID，未登录返回null
   */
  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof Long) {
        return (Long) principal;
      }
    }
    return null;
  }
}