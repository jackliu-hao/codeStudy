/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.ConvertException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CastConverter<T>
/*    */   extends AbstractConverter<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Class<T> targetType;
/*    */   
/*    */   protected T convertInternal(Object value) {
/* 21 */     throw new ConvertException("Can not cast value to [{}]", new Object[] { this.targetType });
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<T> getTargetType() {
/* 26 */     return this.targetType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\CastConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */