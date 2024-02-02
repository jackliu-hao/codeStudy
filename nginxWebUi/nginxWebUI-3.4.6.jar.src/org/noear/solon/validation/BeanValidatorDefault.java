/*    */ package org.noear.solon.validation;
/*    */ 
/*    */ import org.noear.solon.core.handle.Result;
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
/*    */ public class BeanValidatorDefault
/*    */   implements BeanValidator
/*    */ {
/*    */   public Result validate(Object obj, Class<?>... groups) {
/* 21 */     return ValidatorManager.validateOfEntity(obj, groups);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\BeanValidatorDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */