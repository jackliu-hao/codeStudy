/*     */ package org.h2.expression.aggregate;
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
/*     */ public final class ListaggArguments
/*     */ {
/*     */   private String separator;
/*     */   private boolean onOverflowTruncate;
/*     */   private String filter;
/*     */   private boolean withoutCount;
/*     */   
/*     */   public void setSeparator(String paramString) {
/*  36 */     this.separator = (paramString != null) ? paramString : "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSeparator() {
/*  45 */     return this.separator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEffectiveSeparator() {
/*  54 */     return (this.separator != null) ? this.separator : ",";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOnOverflowTruncate(boolean paramBoolean) {
/*  65 */     this.onOverflowTruncate = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getOnOverflowTruncate() {
/*  75 */     return this.onOverflowTruncate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilter(String paramString) {
/*  86 */     this.filter = (paramString != null) ? paramString : "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFilter() {
/*  95 */     return this.filter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEffectiveFilter() {
/* 104 */     return (this.filter != null) ? this.filter : "...";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWithoutCount(boolean paramBoolean) {
/* 114 */     this.withoutCount = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWithoutCount() {
/* 123 */     return this.withoutCount;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\ListaggArguments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */