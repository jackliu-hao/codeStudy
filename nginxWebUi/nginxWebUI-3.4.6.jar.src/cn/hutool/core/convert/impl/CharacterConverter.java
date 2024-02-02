/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.util.BooleanUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CharacterConverter
/*    */   extends AbstractConverter<Character>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Character convertInternal(Object value) {
/* 18 */     if (value instanceof Boolean) {
/* 19 */       return BooleanUtil.toCharacter(((Boolean)value).booleanValue());
/*    */     }
/* 21 */     String valueStr = convertToStr(value);
/* 22 */     if (StrUtil.isNotBlank(valueStr)) {
/* 23 */       return Character.valueOf(valueStr.charAt(0));
/*    */     }
/*    */     
/* 26 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\CharacterConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */