/*    */ package cn.hutool.core.lang.generator;
/*    */ 
/*    */ import cn.hutool.core.util.IdUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UUIDGenerator
/*    */   implements Generator<String>
/*    */ {
/*    */   public String next() {
/* 14 */     return IdUtil.fastUUID();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\generator\UUIDGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */