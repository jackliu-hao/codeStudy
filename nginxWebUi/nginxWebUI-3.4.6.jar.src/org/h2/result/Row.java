/*    */ package org.h2.result;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.h2.value.Value;
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
/*    */ public abstract class Row
/*    */   extends SearchRow
/*    */ {
/*    */   public static Row get(Value[] paramArrayOfValue, int paramInt) {
/* 25 */     return new DefaultRow(paramArrayOfValue, paramInt);
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
/*    */   public static Row get(Value[] paramArrayOfValue, int paramInt, long paramLong) {
/* 37 */     DefaultRow defaultRow = new DefaultRow(paramArrayOfValue, paramInt);
/* 38 */     defaultRow.setKey(paramLong);
/* 39 */     return defaultRow;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract Value[] getValueList();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean hasSameValues(Row paramRow) {
/* 58 */     return Arrays.equals((Object[])getValueList(), (Object[])paramRow.getValueList());
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
/*    */ 
/*    */   
/*    */   public boolean hasSharedData(Row paramRow) {
/* 74 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\Row.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */