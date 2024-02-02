/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import java.util.UUID;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UUIDConverter
/*    */   extends AbstractConverter<UUID>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected UUID convertInternal(Object value) {
/* 19 */     return UUID.fromString(convertToStr(value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\UUIDConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */