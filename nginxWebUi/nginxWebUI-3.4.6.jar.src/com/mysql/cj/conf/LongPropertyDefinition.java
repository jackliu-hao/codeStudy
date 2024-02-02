/*    */ package com.mysql.cj.conf;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
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
/*    */ public class LongPropertyDefinition
/*    */   extends AbstractPropertyDefinition<Long>
/*    */ {
/*    */   private static final long serialVersionUID = -5264490959206230852L;
/*    */   
/*    */   public LongPropertyDefinition(PropertyKey key, long defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/* 42 */     super(key, Long.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/*    */   }
/*    */ 
/*    */   
/*    */   public LongPropertyDefinition(PropertyKey key, long defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory, long lowerBound, long upperBound) {
/* 47 */     super(key, Long.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory, (int)lowerBound, (int)upperBound);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Long parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
/*    */     try {
/* 54 */       return Long.valueOf(Double.valueOf(value).longValue());
/*    */     }
/* 56 */     catch (NumberFormatException nfe) {
/* 57 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + getName() + "' only accepts long integer values. The value '" + value + "' can not be converted to a long integer.", exceptionInterceptor);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isRangeBased() {
/* 64 */     return (getUpperBound() != getLowerBound());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RuntimeProperty<Long> createRuntimeProperty() {
/* 74 */     return new LongProperty(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\LongPropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */