
import org.example.entity.User;
import org.example.service.BaseService;
import org.example.utils.Result;

import org.springframework.stereotype.Service;

public interface UserService extends BaseService<User> {
  Result login(UserRequest request);

  Result register(UserRequest request);

  Result profile();

  Result updateProfile(UserRequest request);

  Result points();

  Result credit();
}
