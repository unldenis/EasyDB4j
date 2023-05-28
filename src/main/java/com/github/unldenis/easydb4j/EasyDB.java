package com.github.unldenis.easydb4j;

import com.github.unldenis.easydb4j.api.DataSource;
import com.github.unldenis.easydb4j.api.IDB;
import com.github.unldenis.easydb4j.api.annotation.Table;
import com.github.unldenis.easydb4j.api.asm.IDBCreator;
import io.github.classgraph.ClassGraph;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EasyDB {
  private static final List<Class<?>> tables;

  static {
    try (var scanner = new ClassGraph().enableClassInfo().enableAnnotationInfo().scan()) {
      tables = scanner.getClassesWithAnnotation(Table.class).loadClasses();
    }
  }


  private final DataSource dataSource;
  private final Map<Class<?>, IDB<?>> tableMap = new HashMap<>();

  public EasyDB(DataSource dataSource, Consumer<ReflectiveOperationException> catchErr) {
    this.dataSource = dataSource;
    for(var t: tables) {
      try {
        var creator = new IDBCreator(t);
        var compiled = MethodHandles.lookup().defineClass(creator.dump());
        tableMap.put(t, (IDB<?>) compiled.getConstructor(DataSource.class).newInstance(dataSource));
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException e) {
        if(catchErr == null) {
          throw new RuntimeException(e);
        } else {
          catchErr.accept(e);
        }
      }
    }
  }

  public EasyDB(DataSource dataSource) {
    this(dataSource, null);
  }

  public <T> IDB<T> get(Class<T> table) {
    return (IDB<T>) tableMap.get(table);
  }

}
