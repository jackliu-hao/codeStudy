/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.util.BooleanUtil;
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
/*    */ public class BooleanConverter
/*    */   extends AbstractConverter<Boolean>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Boolean convertInternal(Object value) {
/* 24 */     if (value instanceof Number)
/*    */     {
/* 26 */       return Boolean.valueOf((0.0D != ((Number)value).doubleValue()));
/*    */     }
/*    */     
/* 29 */     return Boolean.valueOf(BooleanUtil.toBoolean(convertToStr(value)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\BooleanConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */