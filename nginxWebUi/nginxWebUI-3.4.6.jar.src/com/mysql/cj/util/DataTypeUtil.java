/*    */ package com.mysql.cj.util;
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
/*    */ public class DataTypeUtil
/*    */ {
/*    */   public static long bitToLong(byte[] bytes, int offset, int length) {
/* 49 */     long valueAsLong = 0L;
/* 50 */     for (int i = 0; i < length; i++) {
/* 51 */       valueAsLong = valueAsLong << 8L | (bytes[offset + i] & 0xFF);
/*    */     }
/* 53 */     return valueAsLong;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\DataTypeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */