/*    */ package org.noear.solon.core;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.StringReader;
/*    */ import java.net.URL;
/*    */ import java.util.Properties;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.util.PrintUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PropsLoader
/*    */ {
/*    */   private static PropsLoader global;
/*    */   
/*    */   public static PropsLoader global() {
/* 25 */     return global;
/*    */   }
/*    */   
/*    */   public static void globalSet(PropsLoader instance) {
/* 29 */     if (instance != null) {
/* 30 */       global = instance;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   static {
/* 36 */     PropsLoader tmp = (PropsLoader)Utils.newInstance("org.noear.solon.extend.impl.PropsLoaderExt");
/*    */     
/* 38 */     if (tmp == null) {
/* 39 */       global = new PropsLoader();
/*    */     } else {
/* 41 */       global = tmp;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isSupport(String suffix) {
/* 51 */     if (suffix == null) {
/* 52 */       return false;
/*    */     }
/*    */     
/* 55 */     return suffix.endsWith(".properties");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Properties load(URL url) throws IOException {
/* 63 */     if (url == null) {
/* 64 */       return null;
/*    */     }
/*    */     
/* 67 */     String fileName = url.toString();
/*    */     
/* 69 */     if (fileName.endsWith(".properties")) {
/* 70 */       PrintUtil.info(url);
/*    */       
/* 72 */       Properties tmp = new Properties();
/* 73 */       tmp.load(new InputStreamReader(url.openStream(), Solon.encoding()));
/* 74 */       return tmp;
/*    */     } 
/*    */     
/* 77 */     throw new IllegalStateException("This profile is not supported: " + fileName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Properties build(String txt) throws IOException {
/* 84 */     int idx1 = txt.indexOf("=");
/* 85 */     int idx2 = txt.indexOf(":");
/*    */     
/* 87 */     if (idx1 > 0 && (idx1 < idx2 || idx2 < 0)) {
/* 88 */       Properties tmp = new Properties();
/* 89 */       tmp.load(new StringReader(txt));
/* 90 */       return tmp;
/*    */     } 
/*    */     
/* 93 */     return new Properties();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\PropsLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */