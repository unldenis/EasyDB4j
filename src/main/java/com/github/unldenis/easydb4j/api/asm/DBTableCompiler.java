package com.github.unldenis.easydb4j.api.asm;


import java.lang.reflect.Field;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DBTableCompiler implements Opcodes {

  protected final Class<?> modelClass;

  protected final Field[] columns;
  protected final String modelClassName;
  protected final String modelFullClassName;
  protected final String targetClassName;
  protected String targetFullClassName;

  private static final String API_PATH = "com/github/unldenis/easydb4j/api/";
  public DBTableCompiler(Class<?> modelClass) {
    this.modelClass = modelClass;
    this.columns = modelClass.getDeclaredFields();

    this.modelClassName = modelClass.getSimpleName();
    this.modelFullClassName = modelClass.getName().replace(".", "/");

    this.targetClassName = "EasyDB" + modelClassName;
    this.targetFullClassName = "com/github/unldenis/easydb4j/" + targetClassName;
  }

  public byte[] compile() {

    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

    classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, targetFullClassName,
        "Ljava/lang/Object;L%sDB<L%s;>;".formatted(API_PATH, modelFullClassName),
        "java/lang/Object", new String[]{API_PATH + "DB"});

    classWriter.visitSource(targetClassName + ".java", null);

    this.visitFields(classWriter);

    this.visitConstructor(classWriter);

    new FindAll(this).visit(classWriter);
    new FindOne(this).visit(classWriter);
    new Add(this).visit(classWriter);
    new Update(this).visit(classWriter);
    new Delete(this).visit(classWriter);

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
