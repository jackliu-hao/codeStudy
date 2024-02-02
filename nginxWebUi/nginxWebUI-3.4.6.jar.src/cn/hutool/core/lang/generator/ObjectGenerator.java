/*    */ package cn.hutool.core.lang.generator;
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
/*    */ public class ObjectGenerator<T>
/*    */   implements Generator<T>
/*    */ {
/*    */   private final Class<T> clazz;
/*    */   
/*    */   public ObjectGenerator(Class<T> clazz) {
/* 21 */     this.clazz = clazz;
/*    */   }
/*    */ 
/*    */   
/*    */   public T next() {
/* 26 */     return (T)ReflectUtil.newInstanceIfPossible(this.clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\generator\ObjectGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */