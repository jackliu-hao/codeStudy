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
/*    */ public class IntegerProperty
/*    */   extends AbstractRuntimeProperty<Integer>
/*    */ {
/*    */   private static final long serialVersionUID = 9208223182595760858L;
/*    */   
/*    */   public IntegerProperty(PropertyDefinition<Integer> propertyDefinition) {
/* 41 */     super(propertyDefinition);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void checkRange(Integer val, String valueAsString, ExceptionInterceptor exceptionInterceptor) {
/* 46 */     if (val.intValue() < getPropertyDefinition().getLowerBound() || val.intValue() > getPropertyDefinition().getUpperBound())
/* 47 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "The connection property '" + 
/* 48 */           getPropertyDefinition().getName() + "' only accepts integer values in the range of " + 
/* 49 */           getPropertyDefinition().getLowerBound() + " - " + getPropertyDefinition().getUpperBound() + ", the value '" + ((valueAsString == null) ? 
/* 50 */           Integer.valueOf(val.intValue()) : valueAsString) + "' exceeds this range.", exceptionInterceptor); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\IntegerProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */