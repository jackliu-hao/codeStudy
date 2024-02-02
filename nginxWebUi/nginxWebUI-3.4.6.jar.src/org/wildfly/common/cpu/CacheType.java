/*     */ package org.wildfly.common.cpu;
/*     */ 
/*     */ import java.util.EnumSet;
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
/*     */ public enum CacheType
/*     */ {
/*  32 */   UNKNOWN(false, false),
/*     */ 
/*     */ 
/*     */   
/*  36 */   DATA(false, true),
/*     */ 
/*     */ 
/*     */   
/*  40 */   INSTRUCTION(true, false),
/*     */ 
/*     */ 
/*     */   
/*  44 */   UNIFIED(true, true); private static final int fullSize;
/*     */   
/*     */   static {
/*  47 */     fullSize = (values()).length;
/*     */   }
/*     */   private final boolean instruction; private final boolean data;
/*     */   
/*     */   CacheType(boolean instruction, boolean data) {
/*  52 */     this.instruction = instruction;
/*  53 */     this.data = data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInstruction() {
/*  62 */     return this.instruction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isData() {
/*  71 */     return this.data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isFull(EnumSet<CacheType> set) {
/*  82 */     return (set != null && set.size() == fullSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean in(CacheType v1) {
/*  93 */     return (this == v1);
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
/*     */   public boolean in(CacheType v1, CacheType v2) {
/* 105 */     return (this == v1 || this == v2);
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
/*     */   public boolean in(CacheType v1, CacheType v2, CacheType v3) {
/* 118 */     return (this == v1 || this == v2 || this == v3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean in(CacheType... values) {
/* 129 */     if (values != null) for (CacheType value : values) {
/* 130 */         if (this == value) return true; 
/*     */       }  
/* 132 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\cpu\CacheType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */