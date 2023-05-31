package com.github.unldenis.easydb4j.api.asm;
import static org.objectweb.asm.Opcodes.*;


import com.github.unldenis.easydb4j.api.annotation.PK;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class Add implements IMethod{

  private final DBTableCompiler compiler;

  public Add(DBTableCompiler compiler) {
    this.compiler = compiler;
  }


  @Override
  public void visit(ClassWriter classWriter) {
    var firstField = compiler.columns[0];
    boolean hasId = firstField.isAnnotationPresent(PK.class) && firstField.getAnnotation(PK.class).auto_increment();

    String queryColumns= Arrays.stream(compiler.columns).skip(hasId ? 1 : 0).map(Field::getName).collect(
        Collectors.joining(","));
    String questionColumns = queryColumns.replaceAll("[_a-zA-Z]+", "?");

    var methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "add", "(L%s;)Z".formatted( compiler.modelFullClassName),
        null, new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();

    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("INSERT INTO %s (%s) VALUES (%s);".formatted(compiler.modelClassName, queryColumns, questionColumns));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, compiler.targetFullClassName, "prepare",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", false);
    methodVisitor.visitVarInsn(ASTORE, 2);


    for(int j = 0; j < compiler.columns.length; j++) {
      var column = compiler.columns[j];

      if(hasId && j == 0) {
        continue;
      }

      int parameterIndex = j + (hasId ? 0 : 1);

      Label if_not_null = new Label();
      Label endif = new Label();

      var columnTypeDescriptor = Types.typeDescriptor(column.getType());

      // check if property is null
      methodVisitor.visitVarInsn(ALOAD, 1);
      methodVisitor.visitFieldInsn(GETFIELD, compiler.modelFullClassName, column.getName(),
          columnTypeDescriptor);
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
      if(Types.isInt(column.getType())) {
        methodVisitor.visitFieldInsn(GETFIELD, compiler.modelFullClassName, column.getName(),
            columnTypeDescriptor);
        // unbox
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
            "()I", false);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setInt",
            "(II)V", true);
      } else if(Types.isString(column.getType())) {
        methodVisitor.visitFieldInsn(GETFIELD, compiler.modelFullClassName, column.getName(),
            columnTypeDescriptor);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setString",
            "(ILjava/lang/String;)V", true);
      } else if(Types.isTimestamp(column.getType())) {
        methodVisitor.visitFieldInsn(GETFIELD, compiler.modelFullClassName, column.getName(),
            columnTypeDescriptor);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/PreparedStatement", "setTimestamp",
            "(ILjava/sql/Timestamp;)V", true);
      } else {
        throw new RuntimeException("In table '%s' the type '%s' of column '%s' is not implemented yet."
            .formatted(compiler.modelClassName, column.getType().getName(), column.getName()));
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
}
