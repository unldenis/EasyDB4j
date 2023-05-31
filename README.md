# EasyDB4j

A few types are still supported (strings,integers and timestamps only) and also support for a few class types.
Born as a utility because light and ad hoc for the needs I have.
#### Used the following libraries:
- HikariCP : for jdbc pooling
- mysql-connector-j : the mysql driver, future versions to be added in another module but for now it's ok
- asm : low-level bytecode manipulation framework that allows me to generate classes for each table.
- classgraph : utility to scan classes with @Table annotation

#### Test
From a class like this.
```java
@Table
public class MyUser {
public Integer id;
public String name;
public String address;
public Integer age;

public MyUser() { }
}
```
Generated this class in runtime(Decompiling with intellij):

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.unldenis.easydb4j;

import com.github.unldenis.easydb4j.api.DataSource;
import com.github.unldenis.easydb4j.api.DB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EasyDBMyUser implements DB<MyUser> {

  private final DataSource dS;

  public EasyDBMyUser(DataSource var1) {
    this.dS = var1;
  }

  public List<MyUser> findAll() throws SQLException {
    ArrayList var1 = new ArrayList();
    String var2 = "SELECT * FROM MyUser;";
    Statement var3 = this.statement();
    ResultSet var4 = var3.executeQuery(var2);

    while (var4.next()) {
      MyUser var5 = new MyUser();
      var5.id = var4.getInt("id");
      var5.name = var4.getString("name");
      var5.address = var4.getString("address");
      var5.age = var4.getInt("age");
      var1.add(var5);
    }

    var3.close();
    return var1;
  }

  public Optional<MyUser> findOne(int var1) throws SQLException {
    PreparedStatement var2 = this.prepare("SELECT * FROM MyUser WHERE id=?;");
    var2.setInt(1, var1);
    ResultSet var3 = var2.executeQuery();
    if (var3.next()) {
      MyUser var4 = new MyUser();
      var4.id = var3.getInt("id");
      var4.name = var3.getString("name");
      var4.name = var3.getString("name");
      var4.age = var3.getInt("age");
      var2.close();
      return Optional.of(var4);
    } else {
      var2.close();
      return Optional.empty();
    }
  }

  public boolean add(MyUser var1) throws SQLException {
    PreparedStatement var2 = this.prepare("INSERT INTO MyUser (name,address,age) VALUES (?,?,?);");
    if (var1.name == null) {
      var2.setNull(1, 0);
    } else {
      var2.setString(1, var1.name);
    }

    if (var1.address == null) {
      var2.setNull(2, 0);
    } else {
      var2.setString(2, var1.address);
    }

    if (var1.age == null) {
      var2.setNull(3, 0);
    } else {
      var2.setInt(3, var1.age);
    }

    int var3 = var2.executeUpdate();
    var2.close();
    return var3 > 0;
  }

  public boolean update(int var1, MyUser var2) throws SQLException {
    PreparedStatement var3 = this.prepare("UPDATE MyUser SET name=?,address=?,age=? WHERE id=?;");
    if (var2.name == null) {
      var3.setNull(1, 0);
    } else {
      var3.setString(1, var2.name);
    }

    if (var2.address == null) {
      var3.setNull(2, 0);
    } else {
      var3.setString(2, var2.address);
    }

    if (var2.age == null) {
      var3.setNull(3, 0);
    } else {
      var3.setInt(3, var2.age);
    }

    var3.setInt(4, var1);
    int var4 = var3.executeUpdate();
    var3.close();
    return var4 > 0;
  }

  public boolean delete(int var1) throws SQLException {
    PreparedStatement var2 = this.prepare("DELETE FROM MyUser WHERE id=?;");
    var2.setInt(1, var1);
    int var3 = var2.executeUpdate();
    var2.close();
    return var3 > 0;
  }

  public PreparedStatement prepare(String var1) throws SQLException {
    return this.dS.getConnection().prepareStatement(var1);
  }

  public Statement statement() throws SQLException {
    return this.dS.getConnection().createStatement();
  }
}

```