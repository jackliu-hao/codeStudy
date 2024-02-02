/*    */ package org.noear.solon.validation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
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
/*    */ public class BeanValidateInfo
/*    */   extends Result
/*    */ {
/*    */   public final Annotation anno;
/*    */   public final String message;
/*    */   
/*    */   public BeanValidateInfo(Annotation anno, String message) {
/* 24 */     this.anno = anno;
/* 25 */     this.message = message;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\BeanValidateInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */