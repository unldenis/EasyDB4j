package com.github.unldenis.easydb4j.api.asm;

import org.objectweb.asm.ClassWriter;

public interface IMethod {

  void visit(ClassWriter classWriter);


}
