package org.noear.solon.aspect.asm;

public class AsmProxyClassLoader extends ClassLoader {
   public AsmProxyClassLoader(ClassLoader classLoader) {
      super(classLoader);
   }

   public Class<?> transfer2Class(byte[] bytes) {
      return this.defineClass((String)null, bytes, 0, bytes.length);
   }
}
