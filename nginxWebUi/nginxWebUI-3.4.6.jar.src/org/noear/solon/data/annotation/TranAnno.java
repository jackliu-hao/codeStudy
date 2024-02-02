/*    */ package org.noear.solon.data.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import org.noear.solon.data.tran.TranIsolation;
/*    */ import org.noear.solon.data.tran.TranPolicy;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TranAnno
/*    */   implements Tran
/*    */ {
/* 18 */   private TranPolicy _policy = TranPolicy.required;
/*    */ 
/*    */ 
/*    */   
/* 22 */   private TranIsolation _isolation = TranIsolation.unspecified;
/*    */ 
/*    */   
/*    */   private boolean _readOnly = false;
/*    */ 
/*    */ 
/*    */   
/*    */   public TranPolicy policy() {
/* 30 */     return this._policy;
/*    */   }
/*    */   
/*    */   public TranAnno policy(TranPolicy policy) {
/* 34 */     this._policy = policy;
/* 35 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public TranIsolation isolation() {
/* 40 */     return this._isolation;
/*    */   }
/*    */   public TranAnno isolation(TranIsolation isolation) {
/* 43 */     this._isolation = isolation;
/* 44 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean readOnly() {
/* 49 */     return this._readOnly;
/*    */   }
/*    */   
/*    */   public TranAnno readOnly(boolean readOnly) {
/* 53 */     this._readOnly = readOnly;
/* 54 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<? extends Annotation> annotationType() {
/* 59 */     return (Class)Tran.class;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\annotation\TranAnno.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */