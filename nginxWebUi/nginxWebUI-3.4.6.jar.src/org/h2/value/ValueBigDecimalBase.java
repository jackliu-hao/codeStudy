/*    */ package org.h2.value;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import org.h2.message.DbException;
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
/*    */ abstract class ValueBigDecimalBase
/*    */   extends Value
/*    */ {
/*    */   final BigDecimal value;
/*    */   TypeInfo type;
/*    */   
/*    */   ValueBigDecimalBase(BigDecimal paramBigDecimal) {
/* 24 */     if (paramBigDecimal != null) {
/* 25 */       if (paramBigDecimal.getClass() != BigDecimal.class) {
/* 26 */         throw DbException.get(90125, new String[] { BigDecimal.class.getName(), paramBigDecimal
/* 27 */               .getClass().getName() });
/*    */       }
/* 29 */       int i = paramBigDecimal.precision();
/* 30 */       if (i > 100000) {
/* 31 */         throw DbException.getValueTooLongException(getTypeName(getValueType()), paramBigDecimal.toString(), i);
/*    */       }
/*    */     } 
/* 34 */     this.value = paramBigDecimal;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueBigDecimalBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */