/*    */ package cn.hutool.core.lang.generator;
/*    */ 
/*    */ import cn.hutool.core.lang.ObjectId;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ObjectIdGenerator
/*    */   implements Generator<String>
/*    */ {
/*    */   public String next() {
/* 14 */     return ObjectId.next();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\generator\ObjectIdGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */