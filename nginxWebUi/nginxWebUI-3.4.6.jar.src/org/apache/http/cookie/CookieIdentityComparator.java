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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class CookieIdentityComparator
/*    */   implements Serializable, Comparator<Cookie>
/*    */ {
/*    */   private static final long serialVersionUID = 4466565437490631532L;
/*    */   
/*    */   public int compare(Cookie c1, Cookie c2) {
/* 51 */     int res = c1.getName().compareTo(c2.getName());
/* 52 */     if (res == 0) {
/*    */       
/* 54 */       String d1 = c1.getDomain();
/* 55 */       if (d1 == null) {
/* 56 */         d1 = "";
/* 57 */       } else if (d1.indexOf('.') == -1) {
/* 58 */         d1 = d1 + ".local";
/*    */       } 
/* 60 */       String d2 = c2.getDomain();
/* 61 */       if (d2 == null) {
/* 62 */         d2 = "";
/* 63 */       } else if (d2.indexOf('.') == -1) {
/* 64 */         d2 = d2 + ".local";
/*    */       } 
/* 66 */       res = d1.compareToIgnoreCase(d2);
/*    */     } 
/* 68 */     if (res == 0) {
/* 69 */       String p1 = c1.getPath();
/* 70 */       if (p1 == null) {
/* 71 */         p1 = "/";
/*    */       }
/* 73 */       String p2 = c2.getPath();
/* 74 */       if (p2 == null) {
/* 75 */         p2 = "/";
/*    */       }
/* 77 */       res = p1.compareTo(p2);
/*    */     } 
/* 79 */     return res;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\cookie\CookieIdentityComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */