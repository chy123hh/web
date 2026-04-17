import org.example.entity.User;
import org.example.service.BaseServiceImpl;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.example.utils.Result;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
  @Resource
  private UserMapper userMapper;
  @Resource
  private PasswordEncoder passwordEncoder;

  // 用户登录
  @Override
  public Result login(UserRequest request) {

    log.info("用户登录，用户名：{}", request.getStudentId());
    User dbUser = userMapper.selectByUsername(request.getStudentId());
    if (dbUser == null) {
      log.error("用户不存在");
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

  // 用户注册
  @Override
  public Result register(UserRequest request) {
    log.info("用户注册，用户名：{}", request.getStudentId());
    User dbUser = userMapper.selectByUsername(request.getStudentId());
    if (dbUser != null) {
      log.error("用户已存在，学号：{}", request.getStudentId());
      return Result.error(2001, "学号已存在");
    }
    User user = new User();
    user.setStudentId(request.getStudentId());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setNickname(request.getNickname());
    userMapper.insert(user);
    log.info("用户注册成功，学号：{}", request.getStudentId());
    return Result.success("用户注册成功");
  }

  @Override
  public Result profile() {
    return null;
  }

  @Override
  public Result updateProfile(UserRequest request) {
    return null;
  }

  @Override
  public Result points() {
    return null;
  }

  @Override
  public Result credit() {
    return null;
  }

  @Override
  public Result delete() {
    return null;
  }
}
