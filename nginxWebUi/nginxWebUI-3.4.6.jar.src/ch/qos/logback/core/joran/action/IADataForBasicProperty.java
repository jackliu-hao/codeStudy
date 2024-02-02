/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.util.PropertySetter;
/*    */ import ch.qos.logback.core.util.AggregationType;
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
/*    */ class IADataForBasicProperty
/*    */ {
/*    */   final PropertySetter parentBean;
/*    */   final AggregationType aggregationType;
/*    */   final String propertyName;
/*    */   boolean inError;
/*    */   
/*    */   IADataForBasicProperty(PropertySetter parentBean, AggregationType aggregationType, String propertyName) {
/* 31 */     this.parentBean = parentBean;
/* 32 */     this.aggregationType = aggregationType;
/* 33 */     this.propertyName = propertyName;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\IADataForBasicProperty.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */