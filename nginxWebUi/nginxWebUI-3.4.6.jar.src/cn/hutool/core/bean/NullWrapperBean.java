/*    */ package cn.hutool.core.bean;
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
/*    */ public class NullWrapperBean<T>
/*    */ {
/*    */   private final Class<T> clazz;
/*    */   
/*    */   public NullWrapperBean(Class<T> clazz) {
/* 18 */     this.clazz = clazz;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<T> getWrappedClass() {
/* 27 */     return this.clazz;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\NullWrapperBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */