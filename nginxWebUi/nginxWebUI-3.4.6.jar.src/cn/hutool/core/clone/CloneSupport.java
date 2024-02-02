/*    */ package cn.hutool.core.clone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CloneSupport<T>
/*    */   implements Cloneable<T>
/*    */ {
/*    */   public T clone() {
/*    */     try {
/* 15 */       return (T)super.clone();
/* 16 */     } catch (CloneNotSupportedException e) {
/* 17 */       throw new CloneRuntimeException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\clone\CloneSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */