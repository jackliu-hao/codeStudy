/*    */ package cn.hutool.core.lang;
/*    */ 
/*    */ import cn.hutool.core.util.TypeUtil;
/*    */ import java.lang.reflect.Type;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class TypeReference<T>
/*    */   implements Type
/*    */ {
/* 35 */   private final Type type = TypeUtil.getTypeArgument(getClass());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Type getType() {
/* 44 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     return this.type.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\TypeReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */