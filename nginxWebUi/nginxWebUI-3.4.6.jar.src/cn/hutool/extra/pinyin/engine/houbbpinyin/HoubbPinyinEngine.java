/*    */ package cn.hutool.extra.pinyin.engine.houbbpinyin;
/*    */ 
/*    */ import cn.hutool.extra.pinyin.PinyinEngine;
/*    */ import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
/*    */ import com.github.houbb.pinyin.util.PinyinHelper;
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
/*    */ public class HoubbPinyinEngine
/*    */   implements PinyinEngine
/*    */ {
/*    */   PinyinStyleEnum format;
/*    */   
/*    */   public HoubbPinyinEngine() {
/* 37 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HoubbPinyinEngine(PinyinStyleEnum format) {
/* 46 */     init(format);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void init(PinyinStyleEnum format) {
/* 55 */     if (null == format) {
/* 56 */       format = PinyinStyleEnum.NORMAL;
/*    */     }
/* 58 */     this.format = format;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getPinyin(char c) {
/* 64 */     String result = PinyinHelper.toPinyin(String.valueOf(c), this.format);
/* 65 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getPinyin(String str, String separator) {
/* 71 */     String result = PinyinHelper.toPinyin(str, this.format, separator);
/* 72 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\engine\houbbpinyin\HoubbPinyinEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */