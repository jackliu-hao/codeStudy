/*    */ package cn.hutool.core.compiler;
/*    */ 
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.util.URLUtil;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URI;
/*    */ import java.nio.charset.Charset;
/*    */ import javax.tools.JavaFileObject;
/*    */ import javax.tools.SimpleJavaFileObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class JavaSourceFileObject
/*    */   extends SimpleJavaFileObject
/*    */ {
/*    */   private InputStream inputStream;
/*    */   
/*    */   protected JavaSourceFileObject(URI uri) {
/* 37 */     super(uri, JavaFileObject.Kind.SOURCE);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected JavaSourceFileObject(String className, String code, Charset charset) {
/* 47 */     this(className, IoUtil.toStream(code, charset));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected JavaSourceFileObject(String name, InputStream inputStream) {
/* 57 */     this(URLUtil.getStringURI(name.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension));
/* 58 */     this.inputStream = inputStream;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputStream openInputStream() throws IOException {
/* 69 */     if (this.inputStream == null) {
/* 70 */       this.inputStream = toUri().toURL().openStream();
/*    */     }
/* 72 */     return new BufferedInputStream(this.inputStream);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
/* 85 */     try (InputStream in = openInputStream()) {
/* 86 */       return IoUtil.readUtf8(in);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\JavaSourceFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */