/*    */ package org.apache.http.params;
/*    */ 
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.SAFE)
/*    */ public class SyncBasicHttpParams
/*    */   extends BasicHttpParams
/*    */ {
/*    */   private static final long serialVersionUID = 5387834869062660642L;
/*    */   
/*    */   public synchronized boolean removeParameter(String name) {
/* 52 */     return super.removeParameter(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized HttpParams setParameter(String name, Object value) {
/* 57 */     return super.setParameter(name, value);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized Object getParameter(String name) {
/* 62 */     return super.getParameter(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized boolean isParameterSet(String name) {
/* 67 */     return super.isParameterSet(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized boolean isParameterSetLocally(String name) {
/* 72 */     return super.isParameterSetLocally(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized void setParameters(String[] names, Object value) {
/* 77 */     super.setParameters(names, value);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized void clear() {
/* 82 */     super.clear();
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized Object clone() throws CloneNotSupportedException {
/* 87 */     return super.clone();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\SyncBasicHttpParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */