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
/*    */ public class LongProperty
/*    */   extends AbstractRuntimeProperty<Long>
/*    */ {
/*    */   private static final long serialVersionUID = 1814429804634837665L;
/*    */   
/*    */   protected LongProperty(PropertyDefinition<Long> propertyDefinition) {
/* 41 */     super(propertyDefinition);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void checkRange(Long val, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
/* 46 */     if (val.longValue() < getPropertyDefinition().getLowerBound() || val.longValue() > getPropertyDefinition().getUpperBound())
/* 47 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + 
/* 48 */           getPropertyDefinition().getName() + "' only accepts long integer values in the range of " + 
/* 49 */           getPropertyDefinition().getLowerBound() + " - " + getPropertyDefinition().getUpperBound() + ", the value '" + ((valueAsString == null) ? 
/* 50 */           Long.valueOf(val.longValue()) : valueAsString) + "' exceeds this range.", exceptionInterceptor); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\LongProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */