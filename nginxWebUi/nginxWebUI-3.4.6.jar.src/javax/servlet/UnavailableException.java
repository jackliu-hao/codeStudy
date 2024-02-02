/*     */ package javax.servlet;
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
/*     */ 
/*     */ public class UnavailableException
/*     */   extends ServletException
/*     */ {
/*     */   private Servlet servlet;
/*     */   private boolean permanent;
/*     */   private int seconds;
/*     */   
/*     */   @Deprecated
/*     */   public UnavailableException(Servlet servlet, String msg) {
/*  71 */     super(msg);
/*  72 */     this.servlet = servlet;
/*  73 */     this.permanent = true;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public UnavailableException(int seconds, Servlet servlet, String msg) {
/*  94 */     super(msg);
/*  95 */     this.servlet = servlet;
/*  96 */     if (seconds <= 0) {
/*  97 */       this.seconds = -1;
/*     */     } else {
/*  99 */       this.seconds = seconds;
/* 100 */     }  this.permanent = false;
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
/*     */   public UnavailableException(String msg) {
/* 115 */     super(msg);
/*     */     
/* 117 */     this.permanent = true;
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
/*     */   public UnavailableException(String msg, int seconds) {
/* 143 */     super(msg);
/*     */     
/* 145 */     if (seconds <= 0) {
/* 146 */       this.seconds = -1;
/*     */     } else {
/* 148 */       this.seconds = seconds;
/*     */     } 
/* 150 */     this.permanent = false;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPermanent() {
/* 168 */     return this.permanent;
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
/*     */   @Deprecated
/*     */   public Servlet getServlet() {
/* 182 */     return this.servlet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUnavailableSeconds() {
/* 203 */     return this.permanent ? -1 : this.seconds;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\UnavailableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */