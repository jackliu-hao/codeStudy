/*    */ package cn.hutool.core.clone;
/*    */ 
/*    */ import cn.hutool.core.util.ReflectUtil;
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
/*    */ public interface DefaultCloneable<T>
/*    */   extends java.lang.Cloneable
/*    */ {
/*    */   default T clone0() {
/*    */     try {
/* 21 */       return (T)ReflectUtil.invoke(this, "clone", new Object[0]);
/* 22 */     } catch (Exception e) {
/* 23 */       throw new CloneRuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\clone\DefaultCloneable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */