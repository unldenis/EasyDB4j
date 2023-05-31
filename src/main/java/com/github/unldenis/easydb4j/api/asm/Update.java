package com.github.unldenis.easydb4j.api.asm;

import com.github.unldenis.easydb4j.api.annotation.PK;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.*;

public class Update implements IMethod {

  private final DBTableCompiler compiler;

  public Update(DBTableCompiler compiler) {
    this.compiler = compiler;
  }


  @Override
  public void visit(ClassWriter classWriter) {
    var firstField = compiler.columns[0];
    boolean hasId = firstField.isAnnotationPresent(PK.class) && firstField.getAnnotation(PK.class).auto_increment();

    String queryColumns= Arrays.stream(compiler.columns).skip(hasId ? 1 : 0).map(field -> field.getName()+"=?").collect(
        Collectors.joining(","));

    var methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "update",
        "(IL%s;)Z".formatted(compiler.modelFullClassName), null, new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();


    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("UPDATE %s SET %s WHERE id=?;".formatted(compiler.modelClassName, queryColumns));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, compiler.targetFullClassName, "prepare",
        "(Ljava/lang/String;)Ljava/sql/PreparedStatement;", false);
    methodVisitor.visitVarInsn(ASTORE, 3);


    for(int j = 0; j < compiler.columns.length; j++) {
//      methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//      methodVisitor.visitLdcInsn(j);
//      methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
//          "(I)V", false);

      var column = compiler.columns[j];

      if (hasId && j == 0) {
        continue;
      }

      int parameterIndex = j + (hasId ? 0 : 1);

      var columnTypeDescriptor = Types.typeDescriptor(column.getType());

      // check if property is null
      methodVisitor.visitVarInsn(ALOAD, 2);
      methodVisitor.visitFieldInsn(GETFIELD, compiler.modelFullClassName, column.getName(),
          columnTypeDescriptor);
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

      methodVisitor.visitLabel(label1);
      methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }


    methodVisitor.visitVarInsn(ALOAD, 3);
    methodVisitor.visitLdcInsn(compiler.columns.length + (hasId ? 0 : 1));
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
}
