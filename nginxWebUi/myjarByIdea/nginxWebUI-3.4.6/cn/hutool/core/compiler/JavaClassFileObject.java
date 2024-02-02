package cn.hutool.core.compiler;

import cn.hutool.core.util.URLUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;

class JavaClassFileObject extends SimpleJavaFileObject {
   private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

   protected JavaClassFileObject(String className) {
      super(URLUtil.getStringURI(className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
   }

   public InputStream openInputStream() {
      return new ByteArrayInputStream(this.byteArrayOutputStream.toByteArray());
   }

   public OutputStream openOutputStream() {
      return this.byteArrayOutputStream;
   }
}
