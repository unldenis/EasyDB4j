import com.github.unldenis.easydb4j.api.annotation.PK;
import com.github.unldenis.easydb4j.api.annotation.Table;
import java.sql.Timestamp;

@Table
public class Person {
  @PK(auto_increment = true)
  public Integer id;
  public String name;
  public Timestamp created_at;

  public Person() {
  }

  public Person(String name, Timestamp created_at) {
    this.name = name;
    this.created_at = created_at;
  }

  @Override
  public String toString() {
    return "Person{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", created_at=" + created_at +
        '}';
  }
}