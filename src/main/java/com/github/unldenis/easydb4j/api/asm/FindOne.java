package com.github.unldenis.easydb4j.api.asm;

import static org.objectweb.asm.Opcodes.*;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FindOne implements IMethod {


  private final DBTableCompiler compiler;

  public FindOne(DBTableCompiler compiler) {
    this.compiler = compiler;
  }

  @Override
  public void visit(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "findOne", "(I)Ljava/util/Optional;",
        "(I)Ljava/util/Optional<L%s;>;".formatted(compiler.modelFullClassName),
        new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();


    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("SELECT * FROM %s WHERE id=?;".formatted(compiler.modelClassName));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, compiler.targetFullClassName, "prepare",
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


    methodVisitor.visitTypeInsn(NEW, compiler.modelFullClassName);
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, compiler.modelFullClassName, "<init>", "()V",
        false);
    methodVisitor.visitVarInsn(ASTORE, 4);

    for(var column: compiler.columns) {
      var columnTypeDescriptor = Types.typeDescriptor(column.getType());

      methodVisitor.visitVarInsn(ALOAD, 4);
      methodVisitor.visitVarInsn(ALOAD, 3);
      methodVisitor.visitLdcInsn(column.getName());
      if (Types.isInt(column.getType())) {
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getInt",
            "(Ljava/lang/String;)I", true);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
            "(I)Ljava/lang/Integer;", false);
        methodVisitor.visitFieldInsn(PUTFIELD, compiler.modelFullClassName, column.getName(),
            columnTypeDescriptor);

      } else if(Types.isString(column.getType())) {
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getString",
            "(Ljava/lang/String;)Ljava/lang/String;", true);
        methodVisitor.visitFieldInsn(PUTFIELD, compiler.modelFullClassName, column.getName(),
            columnTypeDescriptor);

      } else if(Types.isTimestamp(column.getType())) {
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getTimestamp",
            "(Ljava/lang/String;)Ljava/sql/Timestamp;", true);
        methodVisitor.visitFieldInsn(PUTFIELD, compiler.modelFullClassName, column.getName(),
            columnTypeDescriptor);
      } else {
        throw new RuntimeException("In table '%s' the type '%s' of column '%s' is not implemented yet."
            .formatted(compiler.modelClassName, column.getType().getName(), column.getName()));
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
}
