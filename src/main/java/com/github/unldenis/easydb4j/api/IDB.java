package com.github.unldenis.easydb4j.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface IDB<T> {

  List<T> findAll() throws SQLException;

  Optional<T> findOne(int id) throws SQLException;

  boolean add(T o) throws SQLException;

  boolean update(int id, T o) throws SQLException;

  boolean delete(int id) throws SQLException;

  PreparedStatement prepare(String query) throws SQLException;

  Statement statement() throws SQLException;

  default void findAll(BiConsumer<List<T>, SQLException> then) {
    try {
      then.accept(findAll(), null);
    } catch (SQLException e) {
      then.accept(null, e);
    }
  }
  default void findOne(int id, BiConsumer<Optional<T>, SQLException> then) {
    try {
      then.accept(findOne(id), null);
    } catch (SQLException e) {
      then.accept(null, e);
    }
  }

  default void add(T o, BiConsumer<Boolean, SQLException> then) {
    try {
      then.accept(add(o), null);
    } catch (SQLException e) {
      then.accept(null, e);
    }
  }
  default void update(int id, T o, BiConsumer<Boolean, SQLException> then) {
    try {
      then.accept(update(id, o), null);
    } catch (SQLException e) {
      then.accept(null, e);
    }
  }
  default void delete(BiConsumer<List<T>, SQLException> then) {
    try {
      then.accept(findAll(), null);
    } catch (SQLException e) {
      then.accept(null, e);
    }
  }
  default void prepare(String query, BiConsumer<PreparedStatement, SQLException> then) {
    try {
      then.accept(prepare(query), null);
    } catch (SQLException e) {
      then.accept(null, e);
    }
  }
  default void statement(BiConsumer<Statement, SQLException> then) {
    try {
      then.accept(statement(), null);
    } catch (SQLException e) {
      then.accept(null, e);
    }
  }



}
