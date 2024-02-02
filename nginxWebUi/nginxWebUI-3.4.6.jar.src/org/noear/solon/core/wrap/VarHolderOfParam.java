/*    */ package org.noear.solon.core.wrap;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.Parameter;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
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
/*    */ 
/*    */ public class VarHolderOfParam
/*    */   implements VarHolder
/*    */ {
/*    */   private final Parameter p;
/*    */   private final ParameterizedType genericType;
/*    */   private final AopContext ctx;
/*    */   protected Object val;
/*    */   protected boolean done;
/*    */   protected Runnable onDone;
/*    */   
/*    */   public VarHolderOfParam(AopContext ctx, Parameter p, Runnable onDone) {
/* 29 */     this.ctx = ctx;
/* 30 */     this.p = p;
/* 31 */     this.onDone = onDone;
/*    */ 
/*    */     
/* 34 */     Type tmp = p.getParameterizedType();
/* 35 */     if (tmp instanceof ParameterizedType) {
/* 36 */       this.genericType = (ParameterizedType)tmp;
/*    */     } else {
/* 38 */       this.genericType = null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public AopContext context() {
/* 44 */     return this.ctx;
/*    */   }
/*    */ 
/*    */   
/*    */   public ParameterizedType getGenericType() {
/* 49 */     return this.genericType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isField() {
/* 54 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getType() {
/* 59 */     return this.p.getType();
/*    */   }
/*    */ 
/*    */   
/*    */   public Annotation[] getAnnoS() {
/* 64 */     return this.p.getAnnotations();
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(Object val) {
/* 69 */     this.val = val;
/* 70 */     this.done = true;
/*    */     
/* 72 */     if (this.onDone != null) {
/* 73 */       this.onDone.run();
/*    */     }
/*    */   }
/*    */   
/*    */   public Object getValue() {
/* 78 */     return this.val;
/*    */   }
/*    */   
/*    */   public boolean isDone() {
/* 82 */     return this.done;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\VarHolderOfParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */