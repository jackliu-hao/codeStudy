/*    */ package org.h2.util;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SimpleColumnInfo
/*    */ {
/*    */   public final String name;
/*    */   public final int type;
/*    */   public final String typeName;
/*    */   public final int precision;
/*    */   public final int scale;
/*    */   
/*    */   public SimpleColumnInfo(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3) {
/* 57 */     this.name = paramString1;
/* 58 */     this.type = paramInt1;
/* 59 */     this.typeName = paramString2;
/* 60 */     this.precision = paramInt2;
/* 61 */     this.scale = paramInt3;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object paramObject) {
/* 66 */     if (this == paramObject) {
/* 67 */       return true;
/*    */     }
/* 69 */     if (paramObject == null || getClass() != paramObject.getClass()) {
/* 70 */       return false;
/*    */     }
/* 72 */     SimpleColumnInfo simpleColumnInfo = (SimpleColumnInfo)paramObject;
/* 73 */     return this.name.equals(simpleColumnInfo.name);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 78 */     return this.name.hashCode();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\SimpleColumnInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */