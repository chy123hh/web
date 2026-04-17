import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long id;
  private String studentId;
  private String password;
  private String nickname;
}
