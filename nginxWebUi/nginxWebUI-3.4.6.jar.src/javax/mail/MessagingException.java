/*     */ package javax.mail;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessagingException
/*     */   extends Exception
/*     */ {
/*     */   private Exception next;
/*     */   private static final long serialVersionUID = -7569192289819959253L;
/*     */   
/*     */   public MessagingException() {
/*  68 */     initCause(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessagingException(String s) {
/*  77 */     super(s);
/*  78 */     initCause(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessagingException(String s, Exception e) {
/*  93 */     super(s);
/*  94 */     this.next = e;
/*  95 */     initCause(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Exception getNextException() {
/* 106 */     return this.next;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Throwable getCause() {
/* 116 */     return this.next;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean setNextException(Exception ex) {
/* 129 */     Exception theEnd = this;
/* 130 */     while (theEnd instanceof MessagingException && ((MessagingException)theEnd).next != null)
/*     */     {
/* 132 */       theEnd = ((MessagingException)theEnd).next;
/*     */     }
/*     */ 
/*     */     
/* 136 */     if (theEnd instanceof MessagingException) {
/* 137 */       ((MessagingException)theEnd).next = ex;
/* 138 */       return true;
/*     */     } 
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String toString() {
/* 148 */     String s = super.toString();
/* 149 */     Exception n = this.next;
/* 150 */     if (n == null)
/* 151 */       return s; 
/* 152 */     StringBuffer sb = new StringBuffer((s == null) ? "" : s);
/* 153 */     while (n != null) {
/* 154 */       sb.append(";\n  nested exception is:\n\t");
/* 155 */       if (n instanceof MessagingException) {
/* 156 */         MessagingException mex = (MessagingException)n;
/* 157 */         sb.append(mex.superToString());
/* 158 */         n = mex.next; continue;
/*     */       } 
/* 160 */       sb.append(n.toString());
/* 161 */       n = null;
/*     */     } 
/*     */     
/* 164 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String superToString() {
/* 172 */     return super.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\MessagingException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */