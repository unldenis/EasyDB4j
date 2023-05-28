package com.github.unldenis.easydb4j.api.asm;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IDBCreator implements Opcodes {

//  public static void main(String[] args) throws IOException {
//    var idbcreator = new IDBCreator(MyUser.class);
//
//    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
//        idbcreator.targetClassName + "-ASM.class"));
//    outputStream.write(idbcreator.dump());
//    outputStream.close();
//  }

  private final Class<?> modelClass;

  private final Field[] columns;
  private final String modelClassName;
  private final String modelFullClassName;
  private final String targetClassName;
  public String targetFullClassName;

  public String getTargetClassName() {
    return targetClassName;
  }

  private static final String API_PATH = "com/github/unldenis/easydb4j/api/";
  public IDBCreator(Class<?> modelClass) {
    this.modelClass = modelClass;
    this.columns = modelClass.getDeclaredFields();

    this.modelClassName = modelClass.getSimpleName();
    this.modelFullClassName = modelClass.getName().replace(".", "/");

    this.targetClassName = "EasyDB" + modelClassName;
    this.targetFullClassName = "com/github/unldenis/easydb4j/" + targetClassName;
  }

  public byte[] dump() {

    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

    classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, targetFullClassName,
        "Ljava/lang/Object;L%sIDB<L%s;>;".formatted(API_PATH, modelFullClassName),
        "java/lang/Object", new String[]{API_PATH + "IDB"});

    classWriter.visitSource(targetClassName + ".java", null);

    this.visitFields(classWriter);

    this.visitConstructor(classWriter);

    this.visitFindAll(classWriter);

    this.visitFindOne(classWriter);

    this.visitAdd(classWriter);

    this.visitUpdate(classWriter);

    this.visitDelete(classWriter);

    this.visitPrepare(classWriter);

    this.visitStatement(classWriter);

    MethodVisitor methodVisitor;
    {
      methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "update",
          "(ILjava/lang/Object;)Z", null, new String[]{"java/sql/SQLException"});
      methodVisitor.visitCode();
      Label label0 = new Label();
      methodVisitor.visitLabel(label0);
      methodVisitor.visitVarInsn(ALOAD, 0);
      methodVisitor.visitVarInsn(ILOAD, 1);
      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitTypeInsn(CHECKCAST, modelFullClassName);
      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetFullClassName,
          "update", "(IL%s;)Z".formatted(modelFullClassName), false);
      methodVisitor.visitInsn(IRETURN);

      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
    }
    {
      methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "add",
          "(Ljava/lang/Object;)Z", null, new String[]{"java/sql/SQLException"});
      methodVisitor.visitCode();
      Label label0 = new Label();
      methodVisitor.visitLabel(label0);
      methodVisitor.visitVarInsn(ALOAD, 0);
      methodVisitor.visitVarInsn(ALOAD, 1);
      methodVisitor.visitTypeInsn(CHECKCAST, modelFullClassName);
      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetFullClassName,
          "add", "(L%s;)Z".formatted(modelFullClassName), false);
      methodVisitor.visitInsn(IRETURN);

      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
    }

    classWriter.visitEnd();

    return classWriter.toByteArray();
  }

  private void visitFields(ClassWriter classWriter) {
    FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "dS",
        "L%sDataSource;".formatted(API_PATH), null, null);
    fieldVisitor.visitEnd();
  }

  private void visitConstructor(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;
    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>",
        "(L%sDataSource;)V".formatted(API_PATH), null, null);
    methodVisitor.visitCode();

    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitVarInsn(ALOAD, 1);
    methodVisitor.visitFieldInsn(PUTFIELD, targetFullClassName, "dS",
        "L%sDataSource;".formatted(API_PATH));

    methodVisitor.visitInsn(RETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

  private void visitFindAll(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "findAll", "()Ljava/util/List;",
        "()Ljava/util/List<L%s;>;".formatted(modelFullClassName),
        new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();


    methodVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
    methodVisitor.visitVarInsn(ASTORE, 1);


    methodVisitor.visitLdcInsn("SELECT * FROM %s;".formatted(modelClassName));
    methodVisitor.visitVarInsn(ASTORE, 2);


    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetFullClassName, "statement",
        "()Ljava/sql/Statement;", false);
    methodVisitor.visitVarInsn(ASTORE, 3);


    methodVisitor.visitVarInsn(ALOAD, 3);
    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/Statement", "executeQuery",
        "(Ljava/lang/String;)Ljava/sql/ResultSet;", true);
    methodVisitor.visitVarInsn(ASTORE, 4);

    Label label4 = new Label();
    methodVisitor.visitLabel(label4);
    methodVisitor.visitFrame(Opcodes.F_FULL, 5,
        new Object[]{targetFullClassName, "java/util/ArrayList",
            "java/lang/String", "java/sql/Statement", "java/sql/ResultSet"}, 0, new Object[]{});
    methodVisitor.visitVarInsn(ALOAD, 4);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "next", "()Z", true);

    Label label5 = new Label();
    methodVisitor.visitJumpInsn(IFEQ, label5);
    Label label6 = new Label();
    methodVisitor.visitLabel(label6);
    methodVisitor.visitTypeInsn(NEW, modelFullClassName);
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, modelFullClassName, "<init>", "()V",
        false);
    methodVisitor.visitVarInsn(ASTORE, 5);

    for(var column: columns) {
      if(isInt(column)) {
        Label label7 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitLdcInsn(column.getName());
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getInt",
            "(Ljava/lang/String;)I", true);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
            "(I)Ljava/lang/Integer;", false);
        methodVisitor.visitFieldInsn(PUTFIELD, modelFullClassName, column.getName(),
            "Ljava/lang/Integer;");
      }
      else if(column.getType().equals(String.class)) {
        Label label8 = new Label();
        methodVisitor.visitLabel(label8);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitLdcInsn(column.getName());
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getString",
            "(Ljava/lang/String;)Ljava/lang/String;", true);
        methodVisitor.visitFieldInsn(PUTFIELD, modelFullClassName, column.getName(),
            "Ljava/lang/String;");
      } else {
        throw new RuntimeException("In table '%s' the type '%s' of column '%s' is not implemented yet."
            .formatted(modelClassName, column.getType().getName(), column.getName()));
      }
    }


    methodVisitor.visitVarInsn(ALOAD, 1);
    methodVisitor.visitVarInsn(ALOAD, 5);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add",
        "(Ljava/lang/Object;)Z", false);
    methodVisitor.visitInsn(POP);


    methodVisitor.visitJumpInsn(GOTO, label4);
    methodVisitor.visitLabel(label5);
    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    methodVisitor.visitVarInsn(ALOAD, 3);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/Statement", "close", "()V", true);


    methodVisitor.visitVarInsn(ALOAD, 1);
    methodVisitor.visitInsn(ARETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

  private void visitFindOne(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "findOne", "(I)Ljava/util/Optional;",
        "(I)Ljava/util/Optional<L%s;>;".formatted(modelFullClassName),
        new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();


    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("SELECT * FROM %s WHERE id=?;".formatted(modelClassName));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetFullClassName, "prepare",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", false);
    methodVisitor.visitVarInsn(ASTORE, 2);

    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitInsn(ICONST_1);
    methodVisitor.visitVarInsn(ILOAD, 1);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setInt",
        "(II)V", true);


    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "executeQuery",
        "()Ljava/sql/ResultSet;", true);
    methodVisitor.visitVarInsn(ASTORE, 3);


    methodVisitor.visitVarInsn(ALOAD, 3);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "next", "()Z", true);
    Label label4 = new Label();
    methodVisitor.visitJumpInsn(IFEQ, label4);


    methodVisitor.visitTypeInsn(NEW, modelFullClassName);
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, modelFullClassName, "<init>", "()V",
        false);
    methodVisitor.visitVarInsn(ASTORE, 4);

    for(var column: columns) {

      if (isInt(column)) {
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitLdcInsn(column.getName());
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getInt",
            "(Ljava/lang/String;)I", true);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
            "(I)Ljava/lang/Integer;", false);
        methodVisitor.visitFieldInsn(PUTFIELD, modelFullClassName, column.getName(),
            "Ljava/lang/Integer;");
      } else if(column.getType().equals(String.class)) {
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitLdcInsn("name");
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getString",
            "(Ljava/lang/String;)Ljava/lang/String;", true);
        methodVisitor.visitFieldInsn(PUTFIELD, modelFullClassName, "name",
            "Ljava/lang/String;");
      } else {
        throw new RuntimeException("In table '%s' the type '%s' of column '%s' is not implemented yet."
            .formatted(modelClassName, column.getType().getName(), column.getName()));
      }

    }

    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "close", "()V",
        true);


    methodVisitor.visitVarInsn(ALOAD, 4);
    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Optional", "of",
        "(Ljava/lang/Object;)Ljava/util/Optional;", false);
    methodVisitor.visitInsn(ARETURN);
    methodVisitor.visitLabel(label4);
    methodVisitor.visitFrame(Opcodes.F_APPEND, 2,
        new Object[]{"java/sql/PreparedStatement", "java/sql/ResultSet"}, 0, null);
    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "close", "()V",
        true);


    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Optional", "empty",
        "()Ljava/util/Optional;", false);
    methodVisitor.visitInsn(ARETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

  private boolean isInt(Field f) {
    return f.getType().equals(Integer.class) || f.getType().equals(int.class);
  }

  private void visitAdd(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    boolean hasId = columns[0].getName().equalsIgnoreCase("id") && isInt(columns[0]);

    String queryColumns= Arrays.stream(columns).skip(hasId ? 1 : 0).map(Field::getName).collect(Collectors.joining(","));
    String questionColumns = queryColumns.replaceAll("[a-zA-Z]+", "?");

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "add", "(L%s;)Z".formatted(modelFullClassName),
        null, new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();

    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("INSERT INTO %s (%s) VALUES (%s);".formatted(modelClassName, queryColumns, questionColumns));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetFullClassName, "prepare",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", false);
    methodVisitor.visitVarInsn(ASTORE, 2);


    for(int j = 0; j < columns.length; j++) {
      var column = columns[j];

      if(hasId && j == 0) {
        continue;
      }

      int parameterIndex = j + (hasId ? 0 : 1);

      Label if_not_null = new Label();
      Label endif = new Label();

      // check if property is null
      methodVisitor.visitVarInsn(ALOAD, 1);
      methodVisitor.visitFieldInsn(GETFIELD, modelFullClassName, column.getName(),
          isInt(column) ? "Ljava/lang/Integer;" : "Ljava/lang/String;");
      methodVisitor.visitJumpInsn(IFNONNULL, if_not_null);

      // If null
      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitLdcInsn(parameterIndex);
      methodVisitor.visitInsn(ICONST_0);
      methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setNull",
          "(II)V", true);
      methodVisitor.visitJumpInsn(GOTO, endif);


      // If not null
      methodVisitor.visitLabel(if_not_null);

      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitLdcInsn(parameterIndex);
      methodVisitor.visitVarInsn(ALOAD, 1);
      if(isInt(column)) {
        methodVisitor.visitFieldInsn(GETFIELD, modelFullClassName, column.getName(),
            "Ljava/lang/Integer;");
        // unbox
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
            "()I", false);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setInt",
            "(II)V", true);
      } else if(column.getType().equals(String.class)) {
        methodVisitor.visitFieldInsn(GETFIELD, modelFullClassName, column.getName(),
            "Ljava/lang/String;");
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setString",
            "(ILjava/lang/String;)V", true);
      } else {
        throw new RuntimeException("In table '%s' the type '%s' of column '%s' is not implemented yet."
            .formatted(modelClassName, column.getType().getName(), column.getName()));
      }

      methodVisitor.visitLabel(endif);
    }


    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "executeUpdate",
        "()I", true);
    methodVisitor.visitVarInsn(ISTORE, 3);


    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "close", "()V",
        true);


    methodVisitor.visitVarInsn(ILOAD, 3);
    Label label6 = new Label();
    methodVisitor.visitJumpInsn(IFLE, label6);
    methodVisitor.visitInsn(ICONST_1);

    Label label7 = new Label();
    methodVisitor.visitJumpInsn(GOTO, label7);
    methodVisitor.visitLabel(label6);
    methodVisitor.visitFrame(Opcodes.F_APPEND, 2,
        new Object[]{"java/sql/PreparedStatement", Opcodes.INTEGER}, 0, null);
    methodVisitor.visitInsn(ICONST_0);
    methodVisitor.visitLabel(label7);
    methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
    methodVisitor.visitInsn(IRETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

  private void visitUpdate(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    boolean hasId = columns[0].getName().equalsIgnoreCase("id") && isInt(columns[0]);

    String queryColumns= Arrays.stream(columns).skip(hasId ? 1 : 0).map(field -> field.getName()+"=?").collect(Collectors.joining(","));

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "update",
        "(IL%s;)Z".formatted(modelFullClassName), null, new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();


    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("UPDATE %s SET %s WHERE id=?;".formatted(modelClassName, queryColumns));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetFullClassName, "prepare",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", false);
    methodVisitor.visitVarInsn(ASTORE, 3);


    for(int j = 0; j < columns.length; j++) {
//      methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//      methodVisitor.visitLdcInsn(j);
//      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
//          "(I)V", false);

      var column = columns[j];

      if (hasId && j == 0) {
        continue;
      }

      int parameterIndex = j + (hasId ? 0 : 1);


      // check if property is null
      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitFieldInsn(GETFIELD, modelFullClassName, column.getName(),
          isInt(column) ? "Ljava/lang/Integer;" : "Ljava/lang/String;");
      Label label = new Label();
      methodVisitor.visitJumpInsn(IFNONNULL, label);

      // If null
      methodVisitor.visitVarInsn(ALOAD, 3);
      methodVisitor.visitLdcInsn(parameterIndex);
      methodVisitor.visitInsn(ICONST_0);
      methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setNull",
          "(II)V", true);
      Label label1 = new Label();
      methodVisitor.visitJumpInsn(GOTO, label1);

      // If not null
      methodVisitor.visitLabel(label);

      methodVisitor.visitVarInsn(ALOAD, 3);
      methodVisitor.visitLdcInsn(parameterIndex);
      methodVisitor.visitVarInsn(ALOAD, 2);

      if(isInt(column)) {
        methodVisitor.visitFieldInsn(GETFIELD, modelFullClassName, column.getName(),
            "Ljava/lang/Integer;");
        // unbox
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
            "()I", false);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setInt",
            "(II)V", true);
      } else if(column.getType().equals(String.class)) {
        methodVisitor.visitFieldInsn(GETFIELD, modelFullClassName, column.getName(),
            "Ljava/lang/String;");
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setString",
            "(ILjava/lang/String;)V", true);
      } else {
        throw new RuntimeException("In table '%s' the type '%s' of column '%s' is not implemented yet."
            .formatted(modelClassName, column.getType().getName(), column.getName()));
      }

      methodVisitor.visitLabel(label1);
      methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }


    methodVisitor.visitVarInsn(ALOAD, 3);
    methodVisitor.visitLdcInsn(columns.length + (hasId ? 0 : 1));
    methodVisitor.visitVarInsn(ILOAD, 1);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setInt",
        "(II)V", true);


    methodVisitor.visitVarInsn(ALOAD, 3);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "executeUpdate",
        "()I", true);
    methodVisitor.visitVarInsn(ISTORE, 4);


    methodVisitor.visitVarInsn(ALOAD, 3);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "close", "()V",
        true);


    methodVisitor.visitVarInsn(ILOAD, 4);
    Label label7 = new Label();
    methodVisitor.visitJumpInsn(IFLE, label7);
    methodVisitor.visitInsn(ICONST_1);
    Label label8 = new Label();
    methodVisitor.visitJumpInsn(GOTO, label8);
    methodVisitor.visitLabel(label7);
    methodVisitor.visitFrame(Opcodes.F_APPEND, 2,
        new Object[]{"java/sql/PreparedStatement", Opcodes.INTEGER}, 0, null);
    methodVisitor.visitInsn(ICONST_0);
    methodVisitor.visitLabel(label8);
    methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
    methodVisitor.visitInsn(IRETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

  private void visitDelete(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "delete", "(I)Z", null,
        new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();

    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("DELETE FROM %s WHERE id=?;".formatted(modelClassName));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetFullClassName, "prepare",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", false);
    methodVisitor.visitVarInsn(ASTORE, 2);


    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitInsn(ICONST_1);
    methodVisitor.visitVarInsn(ILOAD, 1);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setInt",
        "(II)V", true);

    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "executeUpdate",
        "()I", true);
    methodVisitor.visitVarInsn(ISTORE, 3);


    methodVisitor.visitVarInsn(ALOAD, 2);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "close", "()V",
        true);


    methodVisitor.visitVarInsn(ILOAD, 3);
    Label label5 = new Label();
    methodVisitor.visitJumpInsn(IFLE, label5);
    methodVisitor.visitInsn(ICONST_1);
    Label label6 = new Label();
    methodVisitor.visitJumpInsn(GOTO, label6);
    methodVisitor.visitLabel(label5);
    methodVisitor.visitFrame(Opcodes.F_APPEND, 2,
        new Object[]{"java/sql/PreparedStatement", Opcodes.INTEGER}, 0, null);
    methodVisitor.visitInsn(ICONST_0);
    methodVisitor.visitLabel(label6);
    methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
    methodVisitor.visitInsn(IRETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

  private void visitPrepare(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;


    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "prepare",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", null,
        new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();


    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitFieldInsn(GETFIELD, targetFullClassName, "dS",
        "L%sDataSource;".formatted(API_PATH));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, API_PATH + "DataSource",
        "getConnection", "()Ljava/sql/Connection;", false);
    methodVisitor.visitVarInsn(ALOAD, 1);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/Connection", "prepareStatement",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", true);
    methodVisitor.visitInsn(ARETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

  private void visitStatement(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;


    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "statement", "()Ljava/sql/Statement;",
        null, new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();

    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitFieldInsn(GETFIELD, targetFullClassName, "dS",
        "L%sDataSource;".formatted(API_PATH));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, API_PATH + "DataSource",
        "getConnection", "()Ljava/sql/Connection;", false);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/Connection", "createStatement",
        "()Ljava/sql/Statement;", true);
    methodVisitor.visitInsn(ARETURN);

    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();

  }

}
