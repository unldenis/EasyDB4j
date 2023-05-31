package com.github.unldenis.easydb4j.api.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.*;

public class Delete implements IMethod {

  private final DBTableCompiler compiler;

  public Delete(DBTableCompiler compiler) {
    this.compiler = compiler;
  }

  @Override
  public void visit(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "delete", "(I)Z", null,
        new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();

    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitLdcInsn("DELETE FROM %s WHERE id=?;".formatted(compiler.modelClassName));
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, compiler.targetFullClassName, "prepare",
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
}
