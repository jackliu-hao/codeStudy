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
/*    */ public class IntegerPropertyDefinition
/*    */   extends AbstractPropertyDefinition<Integer>
/*    */ {
/*    */   private static final long serialVersionUID = 4151893695173946081L;
/* 40 */   protected int multiplier = 1;
/*    */ 
/*    */   
/*    */   public IntegerPropertyDefinition(PropertyKey key, int defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/* 44 */     super(key, Integer.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/*    */   }
/*    */ 
/*    */   
/*    */   public IntegerPropertyDefinition(PropertyKey key, int defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory, int lowerBound, int upperBound) {
/* 49 */     super(key, Integer.valueOf(defaultValue), isRuntimeModifiable, description, sinceVersion, category, orderInCategory, lowerBound, upperBound);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isRangeBased() {
/* 54 */     return (getUpperBound() != getLowerBound());
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
/* 59 */     return integerFrom(getName(), value, this.multiplier, exceptionInterceptor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RuntimeProperty<Integer> createRuntimeProperty() {
/* 69 */     return new IntegerProperty(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public static Integer integerFrom(String name, String value, int multiplier, ExceptionInterceptor exceptionInterceptor) {
/*    */     try {
/* 75 */       int intValue = (int)(Double.valueOf(value).doubleValue() * multiplier);
/*    */ 
/*    */ 
/*    */       
/* 79 */       return Integer.valueOf(intValue);
/*    */     }
/* 81 */     catch (NumberFormatException nfe) {
/* 82 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + name + "' only accepts integer values. The value '" + value + "' can not be converted to an integer.", exceptionInterceptor);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\IntegerPropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */