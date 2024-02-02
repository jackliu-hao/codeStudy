package cn.hutool.core.compiler;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;

class JavaSourceFileObject extends SimpleJavaFileObject {
   private InputStream inputStream;

   protected JavaSourceFileObject(URI uri) {
      super(uri, Kind.SOURCE);
   }

   protected JavaSourceFileObject(String className, String code, Charset charset) {
      this(className, IoUtil.toStream(code, charset));
   }

   protected JavaSourceFileObject(String name, InputStream inputStream) {
      this(URLUtil.getStringURI(name.replace('.', '/') + Kind.SOURCE.extension));
      this.inputStream = inputStream;
   }

   public InputStream openInputStream() throws IOException {
      if (this.inputStream == null) {
         this.inputStream = this.toUri().toURL().openStream();
      }

      return new BufferedInputStream(this.inputStream);
   }

   public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
      InputStream in = this.openInputStream();
      Throwable var3 = null;

      String var4;
      try {
         var4 = IoUtil.readUtf8(in);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (in != null) {
            if (var3 != null) {
               try {
                  in.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               in.close();
            }
         }

      }

      return var4;
   }
}
