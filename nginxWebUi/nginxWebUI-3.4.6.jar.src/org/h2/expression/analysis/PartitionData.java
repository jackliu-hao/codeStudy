/*    */ package org.h2.expression.analysis;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ public final class PartitionData
/*    */ {
/*    */   private Object data;
/*    */   private Value result;
/*    */   private HashMap<Integer, Value> orderedResult;
/*    */   
/*    */   PartitionData(Object paramObject) {
/* 39 */     this.data = paramObject;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Object getData() {
/* 48 */     return this.data;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Value getResult() {
/* 57 */     return this.result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void setResult(Value paramValue) {
/* 67 */     this.result = paramValue;
/* 68 */     this.data = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   HashMap<Integer, Value> getOrderedResult() {
/* 77 */     return this.orderedResult;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void setOrderedResult(HashMap<Integer, Value> paramHashMap) {
/* 87 */     this.orderedResult = paramHashMap;
/* 88 */     this.data = null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\PartitionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */