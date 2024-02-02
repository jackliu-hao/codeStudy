/*    */ package org.noear.solon.aspect.asm;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AsmProxyClassLoader
/*    */   extends ClassLoader
/*    */ {
/*    */   public AsmProxyClassLoader(ClassLoader classLoader) {
/* 10 */     super(classLoader);
/*    */   }
/*    */   
/*    */   public Class<?> transfer2Class(byte[] bytes) {
/* 14 */     return defineClass(null, bytes, 0, bytes.length);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\asm\AsmProxyClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */