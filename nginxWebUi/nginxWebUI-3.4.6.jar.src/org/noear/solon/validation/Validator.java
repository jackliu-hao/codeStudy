/*    */ package org.noear.solon.validation;
/*    */ 
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.Result;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface Validator<T extends java.lang.annotation.Annotation>
/*    */ {
/*    */   default String message(T anno) {
/* 17 */     return "";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   default Class<?>[] groups(T anno) {
/* 23 */     return null;
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
/*    */   default Result validateOfValue(T anno, Object val, StringBuilder tmp) {
/* 35 */     return Result.failure();
/*    */   }
/*    */   
/*    */   Result validateOfContext(Context paramContext, T paramT, String paramString, StringBuilder paramStringBuilder);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\Validator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */