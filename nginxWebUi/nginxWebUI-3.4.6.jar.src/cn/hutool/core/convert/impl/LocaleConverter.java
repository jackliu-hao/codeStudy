/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocaleConverter
/*    */   extends AbstractConverter<Locale>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Locale convertInternal(Object value) {
/*    */     try {
/* 22 */       String str = convertToStr(value);
/* 23 */       if (StrUtil.isEmpty(str)) {
/* 24 */         return null;
/*    */       }
/*    */       
/* 27 */       String[] items = str.split("_");
/* 28 */       if (items.length == 1) {
/* 29 */         return new Locale(items[0]);
/*    */       }
/* 31 */       if (items.length == 2) {
/* 32 */         return new Locale(items[0], items[1]);
/*    */       }
/* 34 */       return new Locale(items[0], items[1], items[2]);
/* 35 */     } catch (Exception exception) {
/*    */ 
/*    */       
/* 38 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\LocaleConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */