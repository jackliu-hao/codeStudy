/*    */ package org.apache.http.cookie;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.Date;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.impl.cookie.BasicClientCookie;
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
/*    */ public class CookiePriorityComparator
/*    */   implements Comparator<Cookie>
/*    */ {
/* 47 */   public static final CookiePriorityComparator INSTANCE = new CookiePriorityComparator();
/*    */   
/*    */   private int getPathLength(Cookie cookie) {
/* 50 */     String path = cookie.getPath();
/* 51 */     return (path != null) ? path.length() : 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(Cookie c1, Cookie c2) {
/* 56 */     int l1 = getPathLength(c1);
/* 57 */     int l2 = getPathLength(c2);
/*    */     
/* 59 */     int result = l2 - l1;
/* 60 */     if (result == 0 && c1 instanceof BasicClientCookie && c2 instanceof BasicClientCookie) {
/* 61 */       Date d1 = ((BasicClientCookie)c1).getCreationDate();
/* 62 */       Date d2 = ((BasicClientCookie)c2).getCreationDate();
/* 63 */       if (d1 != null && d2 != null) {
/* 64 */         return (int)(d1.getTime() - d2.getTime());
/*    */       }
/*    */     } 
/* 67 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\cookie\CookiePriorityComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */