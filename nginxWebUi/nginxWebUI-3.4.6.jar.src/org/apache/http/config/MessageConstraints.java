/*     */ package org.apache.http.config;
/*     */ 
/*     */ import org.apache.http.util.Args;
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
/*     */ public class MessageConstraints
/*     */   implements Cloneable
/*     */ {
/*  44 */   public static final MessageConstraints DEFAULT = (new Builder()).build();
/*     */   
/*     */   private final int maxLineLength;
/*     */   
/*     */   private final int maxHeaderCount;
/*     */   
/*     */   MessageConstraints(int maxLineLength, int maxHeaderCount) {
/*  51 */     this.maxLineLength = maxLineLength;
/*  52 */     this.maxHeaderCount = maxHeaderCount;
/*     */   }
/*     */   
/*     */   public int getMaxLineLength() {
/*  56 */     return this.maxLineLength;
/*     */   }
/*     */   
/*     */   public int getMaxHeaderCount() {
/*  60 */     return this.maxHeaderCount;
/*     */   }
/*     */ 
/*     */   
/*     */   protected MessageConstraints clone() throws CloneNotSupportedException {
/*  65 */     return (MessageConstraints)super.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  70 */     StringBuilder builder = new StringBuilder();
/*  71 */     builder.append("[maxLineLength=").append(this.maxLineLength).append(", maxHeaderCount=").append(this.maxHeaderCount).append("]");
/*     */ 
/*     */     
/*  74 */     return builder.toString();
/*     */   }
/*     */   
/*     */   public static MessageConstraints lineLen(int max) {
/*  78 */     return new MessageConstraints(Args.notNegative(max, "Max line length"), -1);
/*     */   }
/*     */   
/*     */   public static Builder custom() {
/*  82 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static Builder copy(MessageConstraints config) {
/*  86 */     Args.notNull(config, "Message constraints");
/*  87 */     return (new Builder()).setMaxHeaderCount(config.getMaxHeaderCount()).setMaxLineLength(config.getMaxLineLength());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/*  98 */     private int maxLineLength = -1;
/*  99 */     private int maxHeaderCount = -1;
/*     */ 
/*     */     
/*     */     public Builder setMaxLineLength(int maxLineLength) {
/* 103 */       this.maxLineLength = maxLineLength;
/* 104 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setMaxHeaderCount(int maxHeaderCount) {
/* 108 */       this.maxHeaderCount = maxHeaderCount;
/* 109 */       return this;
/*     */     }
/*     */     
/*     */     public MessageConstraints build() {
/* 113 */       return new MessageConstraints(this.maxLineLength, this.maxHeaderCount);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\config\MessageConstraints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */