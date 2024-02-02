/*    */ package org.h2.mode;
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
/*    */ public enum DefaultNullOrdering
/*    */ {
/* 20 */   LOW(2, 4),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 25 */   HIGH(4, 2),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   FIRST(2, 2),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 37 */   LAST(4, 4);
/*    */   static {
/* 39 */     VALUES = values();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static final DefaultNullOrdering[] VALUES;
/*    */ 
/*    */   
/*    */   private final int defaultAscNulls;
/*    */ 
/*    */   
/*    */   private final int defaultDescNulls;
/*    */   
/*    */   private final int nullAsc;
/*    */   
/*    */   private final int nullDesc;
/*    */ 
/*    */   
/*    */   DefaultNullOrdering(int paramInt1, int paramInt2) {
/* 58 */     this.defaultAscNulls = paramInt1;
/* 59 */     this.defaultDescNulls = paramInt2;
/* 60 */     this.nullAsc = (paramInt1 == 2) ? -1 : 1;
/* 61 */     this.nullDesc = (paramInt2 == 2) ? -1 : 1;
/*    */   }
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
/*    */   public int addExplicitNullOrdering(int paramInt) {
/* 74 */     if ((paramInt & 0x6) == 0) {
/* 75 */       paramInt |= ((paramInt & 0x1) == 0) ? this.defaultAscNulls : this.defaultDescNulls;
/*    */     }
/* 77 */     return paramInt;
/*    */   }
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
/*    */   public int compareNull(boolean paramBoolean, int paramInt) {
/* 91 */     if ((paramInt & 0x2) != 0)
/* 92 */       return paramBoolean ? -1 : 1; 
/* 93 */     if ((paramInt & 0x4) != 0)
/* 94 */       return paramBoolean ? 1 : -1; 
/* 95 */     if ((paramInt & 0x1) == 0) {
/* 96 */       return paramBoolean ? this.nullAsc : -this.nullAsc;
/*    */     }
/* 98 */     return paramBoolean ? this.nullDesc : -this.nullDesc;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\DefaultNullOrdering.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */