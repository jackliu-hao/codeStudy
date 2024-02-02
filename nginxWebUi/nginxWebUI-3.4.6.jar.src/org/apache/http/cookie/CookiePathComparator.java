/*    */ package org.apache.http.cookie;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class CookiePathComparator
/*    */   implements Serializable, Comparator<Cookie>
/*    */ {
/* 54 */   public static final CookiePathComparator INSTANCE = new CookiePathComparator();
/*    */   
/*    */   private static final long serialVersionUID = 7523645369616405818L;
/*    */   
/*    */   private String normalizePath(Cookie cookie) {
/* 59 */     String path = cookie.getPath();
/* 60 */     if (path == null) {
/* 61 */       path = "/";
/*    */     }
/* 63 */     if (!path.endsWith("/")) {
/* 64 */       path = path + '/';
/*    */     }
/* 66 */     return path;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(Cookie c1, Cookie c2) {
/* 71 */     String path1 = normalizePath(c1);
/* 72 */     String path2 = normalizePath(c2);
/* 73 */     if (path1.equals(path2))
/* 74 */       return 0; 
/* 75 */     if (path1.startsWith(path2))
/* 76 */       return -1; 
/* 77 */     if (path2.startsWith(path1)) {
/* 78 */       return 1;
/*    */     }
/*    */     
/* 81 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\cookie\CookiePathComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */