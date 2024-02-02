/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import java.util.Date;
/*    */ import org.apache.http.cookie.SetCookie2;
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
/*    */ public class BasicClientCookie2
/*    */   extends BasicClientCookie
/*    */   implements SetCookie2
/*    */ {
/*    */   private static final long serialVersionUID = -7744598295706617057L;
/*    */   private String commentURL;
/*    */   private int[] ports;
/*    */   private boolean discard;
/*    */   
/*    */   public BasicClientCookie2(String name, String value) {
/* 54 */     super(name, value);
/*    */   }
/*    */ 
/*    */   
/*    */   public int[] getPorts() {
/* 59 */     return this.ports;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setPorts(int[] ports) {
/* 64 */     this.ports = ports;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCommentURL() {
/* 69 */     return this.commentURL;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setCommentURL(String commentURL) {
/* 74 */     this.commentURL = commentURL;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setDiscard(boolean discard) {
/* 79 */     this.discard = discard;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isPersistent() {
/* 84 */     return (!this.discard && super.isPersistent());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isExpired(Date date) {
/* 89 */     return (this.discard || super.isExpired(date));
/*    */   }
/*    */ 
/*    */   
/*    */   public Object clone() throws CloneNotSupportedException {
/* 94 */     BasicClientCookie2 clone = (BasicClientCookie2)super.clone();
/* 95 */     if (this.ports != null) {
/* 96 */       clone.ports = (int[])this.ports.clone();
/*    */     }
/* 98 */     return clone;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BasicClientCookie2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */