/*     */ package org.xnio.http;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public class RedirectException
/*     */   extends IOException
/*     */ {
/*     */   private final int statusCode;
/*     */   private final String location;
/*     */   
/*     */   public RedirectException(int statusCode, String location) {
/*  40 */     this.statusCode = statusCode;
/*  41 */     this.location = location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RedirectException(String msg, int statusCode, String location) {
/*  52 */     super(msg);
/*  53 */     this.statusCode = statusCode;
/*  54 */     this.location = location;
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
/*     */   public RedirectException(Throwable cause, int statusCode, String location) {
/*  67 */     super(cause);
/*  68 */     this.statusCode = statusCode;
/*  69 */     this.location = location;
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
/*     */   public RedirectException(String msg, Throwable cause, int statusCode, String location) {
/*  81 */     super(msg, cause);
/*  82 */     this.statusCode = statusCode;
/*  83 */     this.location = location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStatusCode() {
/*  92 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLocation() {
/* 101 */     return this.location;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\http\RedirectException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */