/*    */ package org.noear.solon.core.wrap;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.VarHolder;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VarHolderOfField
/*    */   implements VarHolder
/*    */ {
/*    */   protected final FieldWrap fw;
/*    */   protected final Object obj;
/*    */   protected final AopContext ctx;
/*    */   
/*    */   public VarHolderOfField(AopContext ctx, FieldWrap fw, Object obj) {
/* 23 */     this.ctx = ctx;
/* 24 */     this.fw = fw;
/* 25 */     this.obj = obj;
/*    */   }
/*    */ 
/*    */   
/*    */   public AopContext context() {
/* 30 */     return this.ctx;
/*    */   }
/*    */ 
/*    */   
/*    */   public ParameterizedType getGenericType() {
/* 35 */     return this.fw.genericType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isField() {
/* 40 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<?> getType() {
/* 48 */     return this.fw.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Annotation[] getAnnoS() {
/* 56 */     return this.fw.annoS;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setValue(Object val) {
/* 64 */     this.fw.setValue(this.obj, val, true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\VarHolderOfField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */