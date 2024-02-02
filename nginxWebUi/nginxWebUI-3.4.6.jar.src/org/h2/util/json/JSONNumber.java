/*    */ package org.h2.util.json;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONNumber
/*    */   extends JSONValue
/*    */ {
/*    */   private final BigDecimal value;
/*    */   
/*    */   JSONNumber(BigDecimal paramBigDecimal) {
/* 18 */     this.value = paramBigDecimal;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addTo(JSONTarget<?> paramJSONTarget) {
/* 23 */     paramJSONTarget.valueNumber(this.value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BigDecimal getBigDecimal() {
/* 32 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */