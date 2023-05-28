import com.github.unldenis.easydb4j.api.annotation.Table;

@Table
public class MyUser {
  public Integer id;
  public String name;
  public String address;
  public Integer age;

  public MyUser() {

  }
  public MyUser(String name, String address, Integer age) {
    this.name = name;
    this.address = address;
    this.age = age;
  }

  @Override
  public String toString() {
    return "MyUser{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", address='" + address + '\'' +
        ", age=" + age +
        '}';
  }
}
