package com.github.unldenis.easydb4j.api.asm;

import java.sql.Timestamp;

public class Types {

  public static boolean isInt(Class<?> type) {
    return type.equals(Integer.class);
  }

  public static boolean isString(Class<?> type) {
    return type.equals(String.class);
  }

  public static boolean isTimestamp(Class<?> type) { return type.equals(Timestamp.class); }

  public static String typeDescriptor(Class<?> type) {
    if(isInt(type)) {
      return "Ljava/lang/Integer;";
    } else if(isString(type)) {
      return "Ljava/lang/String;";
    } else if(isTimestamp(type)) {
      return "Ljava/sql/Timestamp;";
    } else {
      throw new RuntimeException("Undefined type '%s'".formatted(type.getName()));
    }
  }
}
