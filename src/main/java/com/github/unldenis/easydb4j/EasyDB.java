package com.github.unldenis.easydb4j;

import com.github.unldenis.easydb4j.api.DB;
import com.github.unldenis.easydb4j.api.DataSource;
import com.github.unldenis.easydb4j.api.annotation.Table;
import com.github.unldenis.easydb4j.api.asm.DBTableCompiler;
import io.github.classgraph.ClassGraph;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyDB {
  private static final Logger logger = LoggerFactory.getLogger(EasyDB.class);

  private static final List<Class<?>> tables;

  static {
    try (var scanner = new ClassGraph().enableClassInfo().enableAnnotationInfo().scan()) {
      tables = scanner.getClassesWithAnnotation(Table.class).loadClasses();
    }
  }


  private final DataSource dataSource;
  private final Map<Class<?>, DB<?>> tableMap = new HashMap<>();

  public EasyDB(DataSource dataSource, Consumer<Exception> catchErr) {
    this.dataSource = dataSource;
    for(var t: tables) {
      var tableName = t.getSimpleName();
      try {
        // check if table exist
        if(!tableExist(tableName)) {
          logger.error("Table %s doesn't exist.".formatted(tableName));
          return;
        }

        // compile DbTable
        var compiler = new DBTableCompiler(t);
        var bytecode = compiler.compile();
        var compiled = MethodHandles.lookup().defineClass(bytecode);
        tableMap.put(t, (DB<?>) compiled.getConstructor(DataSource.class).newInstance(dataSource));

        // create .class file
        var path = t.getAnnotation(Table.class).outputClassPath();
        if(!path.isEmpty()) {
          var outputStream = new BufferedOutputStream(new FileOutputStream(path));
          outputStream.write(bytecode);
          outputStream.close();
        }

      } catch (Exception e) {
        if(catchErr == null) {
          throw new RuntimeException(e);
        } else {
          catchErr.accept(e);
        }
      }
    }
  }

  private boolean tableExist(String tableName) throws SQLException {
    var meta = dataSource.getConnection().getMetaData();
    var resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
    return resultSet.next();
  }


  public EasyDB(DataSource dataSource) {
    this(dataSource, null);
  }

  public <T> DB<T> get(Class<T> table) {
    if(!table.isAnnotationPresent(Table.class)) {
      throw new RuntimeException("Class '%s' has not the @Table annotation.".formatted(table.getName()));
    }
    return (DB<T>) tableMap.get(table);
  }

}
