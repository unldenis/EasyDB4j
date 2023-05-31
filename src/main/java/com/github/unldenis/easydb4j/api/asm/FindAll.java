package com.github.unldenis.easydb4j.api.asm;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Opcodes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class FindAll implements IMethod {

  private final DBTableCompiler compiler;

  public FindAll(DBTableCompiler compiler) {
    this.compiler = compiler;
  }


  @Override
  public void visit(ClassWriter classWriter) {
    MethodVisitor methodVisitor = null;

    methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "findAll", "()Ljava/util/List;",
        "()Ljava/util/List<L%s;>;".formatted(compiler.modelFullClassName),
        new String[]{"java/sql/SQLException"});
    methodVisitor.visitCode();


    methodVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
    methodVisitor.visitVarInsn(ASTORE, 1);


    methodVisitor.visitLdcInsn("SELECT * FROM %s;".formatted(compiler.modelClassName));
    methodVisitor.visitVarInsn(ASTORE, 2);


    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, compiler.targetFullClassName, "statement",
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
        new Object[]{compiler.targetFullClassName, "java/util/ArrayList",
            "java/lang/String", "java/sql/Statement", "java/sql/ResultSet"}, 0, new Object[]{});
    methodVisitor.visitVarInsn(ALOAD, 4);
    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "next", "()Z", true);

    Label label5 = new Label();
    methodVisitor.visitJumpInsn(IFEQ, label5);
    Label label6 = new Label();
    methodVisitor.visitLabel(label6);
    methodVisitor.visitTypeInsn(NEW, compiler.modelFullClassName);
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, compiler.modelFullClassName, "<init>", "()V",
        false);
    methodVisitor.visitVarInsn(ASTORE, 5);

    for(var column: compiler.columns) {
      var columnTypeDescriptor = Types.typeDescriptor(column.getType());

      methodVisitor.visitVarInsn(ALOAD, 5);
      methodVisitor.visitVarInsn(ALOAD, 4);
      methodVisitor.visitLdcInsn(column.getName());

      if(Types.isInt(column.getType())) {
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

}
