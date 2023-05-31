package com.github.unldenis.easydb4j.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface DB<T> {
  Logger logger = LoggerFactory.getLogger(DB.class);

  List<T> findAll() throws SQLException;

  Optional<T> findOne(int id) throws SQLException;

  boolean add(T o) throws SQLException;

  boolean update(int id, T o) throws SQLException;

  boolean delete(int id) throws SQLException;

  PreparedStatement prepare(String query) throws SQLException;

  Statement statement() throws SQLException;

  default void findAll(Consumer<List<T>> then) {
    try {
      then.accept(findAll());
    } catch (SQLException e) {
      logger.error("findAll SQLException", e);
      then.accept(null);
    }
  }

  default void findOne(int id, Consumer<T> then) {
    try {
      then.accept(findOne(id).orElse(null));
    } catch (SQLException e) {
      logger.error("findOne SQLException", e);
      then.accept(null);
    }
  }

  default void add(T o, Consumer<Boolean> then) {
    try {
      then.accept(add(o));
    } catch (SQLException e) {
      logger.error("add SQLException", e);
      then.accept(null);
    }
  }
  default void update(int id, T o, Consumer<Boolean> then) {
    try {
      then.accept(update(id, o));
    } catch (SQLException e) {
      logger.error("update SQLException", e);
      then.accept(null);
    }
  }
  default void delete(Consumer<List<T>> then) {
    try {
      then.accept(findAll());
    } catch (SQLException e) {
      logger.error("delete SQLException", e);
      then.accept(null);
    }
  }
  default void prepare(String query, Consumer<PreparedStatement> then) {
    try {
      then.accept(prepare(query));
    } catch (SQLException e) {
      logger.error("prepare SQLException", e);
      then.accept(null);
    }
  }
  default void statement(Consumer<Statement> then) {
    try {
      then.accept(statement());
    } catch (SQLException e) {
      logger.error("statement SQLException", e);
      then.accept(null);
    }
  }

}
