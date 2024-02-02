/*     */ package com.mysql.cj.protocol;
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
/*     */ public interface Resultset
/*     */   extends ProtocolEntity
/*     */ {
/*     */   void setColumnDefinition(ColumnDefinition paramColumnDefinition);
/*     */   
/*     */   ColumnDefinition getColumnDefinition();
/*     */   
/*     */   boolean hasRows();
/*     */   
/*     */   ResultsetRows getRows();
/*     */   
/*     */   void initRowsWithMetadata();
/*     */   
/*     */   int getResultId();
/*     */   
/*     */   void setNextResultset(Resultset paramResultset);
/*     */   
/*     */   Resultset getNextResultset();
/*     */   
/*     */   void clearNextResultset();
/*     */   
/*     */   long getUpdateCount();
/*     */   
/*     */   long getUpdateID();
/*     */   
/*     */   String getServerInfo();
/*     */   
/*     */   public enum Concurrency
/*     */   {
/*  48 */     READ_ONLY(1007),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     UPDATABLE(1008);
/*     */     
/*     */     private int value;
/*     */     
/*     */     Concurrency(int jdbcRsConcur) {
/*  59 */       this.value = jdbcRsConcur;
/*     */     }
/*     */     
/*     */     public int getIntValue() {
/*  63 */       return this.value;
/*     */     }
/*     */     
/*     */     public static Concurrency fromValue(int concurMode, Concurrency backupValue) {
/*  67 */       for (Concurrency c : values()) {
/*  68 */         if (c.getIntValue() == concurMode) {
/*  69 */           return c;
/*     */         }
/*     */       } 
/*  72 */       return backupValue;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Type
/*     */   {
/*  82 */     FORWARD_ONLY(1003),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     SCROLL_INSENSITIVE(1004),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     SCROLL_SENSITIVE(1005);
/*     */     
/*     */     private int value;
/*     */     
/*     */     Type(int jdbcRsType) {
/* 101 */       this.value = jdbcRsType;
/*     */     }
/*     */     
/*     */     public int getIntValue() {
/* 105 */       return this.value;
/*     */     }
/*     */     
/*     */     public static Type fromValue(int rsType, Type backupValue) {
/* 109 */       for (Type t : values()) {
/* 110 */         if (t.getIntValue() == rsType) {
/* 111 */           return t;
/*     */         }
/*     */       } 
/* 114 */       return backupValue;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\Resultset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */