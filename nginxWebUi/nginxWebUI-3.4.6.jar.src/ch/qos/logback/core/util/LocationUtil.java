/*    */ package ch.qos.logback.core.util;
/*    */ 
/*    */ import java.io.FileNotFoundException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
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
/*    */ public class LocationUtil
/*    */ {
/*    */   public static final String SCHEME_PATTERN = "^\\p{Alpha}[\\p{Alnum}+.-]*:.*$";
/*    */   public static final String CLASSPATH_SCHEME = "classpath:";
/*    */   
/*    */   public static URL urlForResource(String location) throws MalformedURLException, FileNotFoundException {
/* 45 */     if (location == null) {
/* 46 */       throw new NullPointerException("location is required");
/*    */     }
/* 48 */     URL url = null;
/* 49 */     if (!location.matches("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$")) {
/* 50 */       url = Loader.getResourceBySelfClassLoader(location);
/* 51 */     } else if (location.startsWith("classpath:")) {
/* 52 */       String path = location.substring("classpath:".length());
/* 53 */       if (path.startsWith("/")) {
/* 54 */         path = path.substring(1);
/*    */       }
/* 56 */       if (path.length() == 0) {
/* 57 */         throw new MalformedURLException("path is required");
/*    */       }
/* 59 */       url = Loader.getResourceBySelfClassLoader(path);
/*    */     } else {
/* 61 */       url = new URL(location);
/*    */     } 
/* 63 */     if (url == null) {
/* 64 */       throw new FileNotFoundException(location);
/*    */     }
/* 66 */     return url;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\LocationUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */