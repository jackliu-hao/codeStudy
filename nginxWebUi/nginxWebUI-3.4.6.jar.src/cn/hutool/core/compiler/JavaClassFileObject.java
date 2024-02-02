/*    */ package cn.hutool.core.compiler;
/*    */ 
/*    */ import cn.hutool.core.util.URLUtil;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
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
/*    */ class JavaClassFileObject
/*    */   extends SimpleJavaFileObject
/*    */ {
/*    */   private final ByteArrayOutputStream byteArrayOutputStream;
/*    */   
/*    */   protected JavaClassFileObject(String className) {
/* 33 */     super(URLUtil.getStringURI(className.replace('.', '/') + JavaFileObject.Kind.CLASS.extension), JavaFileObject.Kind.CLASS);
/* 34 */     this.byteArrayOutputStream = new ByteArrayOutputStream();
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
/*    */   public InputStream openInputStream() {
/* 46 */     return new ByteArrayInputStream(this.byteArrayOutputStream.toByteArray());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OutputStream openOutputStream() {
/* 57 */     return this.byteArrayOutputStream;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compiler\JavaClassFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */