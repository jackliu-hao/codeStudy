/*    */ package org.noear.snack.core;
/*    */ 
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ public abstract class TypeRef<T> {
/*    */   protected final Type type;
/*    */   
/*    */   protected TypeRef() {
/* 10 */     Type superClass = getClass().getGenericSuperclass();
/* 11 */     this.type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
/*    */   }
/*    */   
/*    */   public Type getType() {
/* 15 */     return this.type;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\TypeRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */