/*    */ package org.noear.solon.core.util;
/*    */ 
/*    */ import java.util.Set;
/*    */ import java.util.function.Predicate;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.JarClassLoader;
/*    */ import org.noear.solon.core.ResourceScanner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ScanUtil
/*    */ {
/*    */   static ResourceScanner scanner;
/*    */   
/*    */   static {
/* 22 */     ResourceScanner ext = (ResourceScanner)Utils.newInstance("org.noear.solon.extend.impl.ResourceScannerExt");
/*    */     
/* 24 */     if (ext == null) {
/* 25 */       scanner = new ResourceScanner();
/*    */     } else {
/* 27 */       scanner = ext;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setScanner(ResourceScanner scanner) {
/* 35 */     if (scanner != null) {
/* 36 */       ScanUtil.scanner = scanner;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Set<String> scan(String path, Predicate<String> filter) {
/* 47 */     return scan((ClassLoader)JarClassLoader.global(), path, filter);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
/* 58 */     return scanner.scan(classLoader, path, filter);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\ScanUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */