package com.github.unldenis.easydb4j;

import com.github.unldenis.easydb4j.api.DataSource;
import com.github.unldenis.easydb4j.api.IDB;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EasyDBMyUser implements IDB<MyUser> {

  private final DataSource dS;

  public EasyDBMyUser(DataSource dS) {
    this.dS = dS;
  }

  @Override
  public List<MyUser> findAll() throws SQLException {
    var list = new ArrayList<MyUser>();

    var query = "SELECT * FROM MyUser;";
    var stmt = statement();
    var result = stmt.executeQuery(query);

    while (result.next()) {
      MyUser t = new MyUser();
      t.id = result.getInt("id");
      t.name = result.getString("name");
      t.address = result.getString("address");
      list.add(t);
    }

    stmt.close();
    return list;
  }

  @Override
  public Optional<MyUser> findOne(int id) throws SQLException {
    var stmt = prepare("SELECT * FROM MyUser WHERE id=?;");
    stmt.setInt(1, id);
    var result = stmt.executeQuery();

    if (result.next()) {
      MyUser t = new MyUser();
      t.id = result.getInt("id");
      t.name = result.getString("name");
      t.address = result.getString("address");

      stmt.close();
      return Optional.of(t);
    }

    stmt.close();
    return Optional.empty();
  }

  @Override
  public boolean add(MyUser o) throws SQLException {
    var stmt = prepare("INSERT INTO MyUser (name, address) VALUES (?, ?);");
    if(o.name == null) {
      stmt.setNull(1, Types.NULL);
    } else {
      stmt.setString(1, o.name);
    }
    stmt.setString(2, o.address);
    int rowsInserted = stmt.executeUpdate();

    stmt.close();
    return rowsInserted > 0;
  }

  @Override
  public boolean update(int id, MyUser o) throws SQLException {
    var stmt = prepare("UPDATE MyUser SET name=?, address=? WHERE id=?;");
    stmt.setString(1, o.name);
    stmt.setString(2, o.address);
    stmt.setInt(3, id);
    int rowsUpdated = stmt.executeUpdate();

    System.out.println(o);
    stmt.close();
    return rowsUpdated > 0;
  }

  @Override
  public boolean delete(int id) throws SQLException {
    var stmt = prepare("DELETE FROM MyUser WHERE id=?;");
    stmt.setInt(1, id);
    int rowsDeleted = stmt.executeUpdate();

    stmt.close();
    return rowsDeleted > 0;
  }

  @Override
  public PreparedStatement prepare(String query) throws SQLException {
    return dS.getConnection().prepareStatement(query);
  }

  @Override
  public Statement statement() throws SQLException {
    return dS.getConnection().createStatement();
  }

}
