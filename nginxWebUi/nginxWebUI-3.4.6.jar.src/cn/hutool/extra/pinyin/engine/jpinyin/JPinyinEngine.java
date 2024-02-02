/*    */ package cn.hutool.extra.pinyin.engine.jpinyin;
/*    */ 
/*    */ import cn.hutool.core.util.ArrayUtil;
/*    */ import cn.hutool.extra.pinyin.PinyinEngine;
/*    */ import cn.hutool.extra.pinyin.PinyinException;
/*    */ import com.github.stuxuhai.jpinyin.PinyinException;
/*    */ import com.github.stuxuhai.jpinyin.PinyinFormat;
/*    */ import com.github.stuxuhai.jpinyin.PinyinHelper;
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
/*    */ public class JPinyinEngine
/*    */   implements PinyinEngine
/*    */ {
/*    */   PinyinFormat format;
/*    */   
/*    */   public JPinyinEngine() {
/* 34 */     this(null);
/*    */   }
/*    */   
/*    */   public JPinyinEngine(PinyinFormat format) {
/* 38 */     init(format);
/*    */   }
/*    */   
/*    */   public void init(PinyinFormat format) {
/* 42 */     if (null == format)
/*    */     {
/* 44 */       format = PinyinFormat.WITHOUT_TONE;
/*    */     }
/* 46 */     this.format = format;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getPinyin(char c) {
/* 52 */     String[] results = PinyinHelper.convertToPinyinArray(c, this.format);
/* 53 */     return ArrayUtil.isEmpty((Object[])results) ? String.valueOf(c) : results[0];
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPinyin(String str, String separator) {
/*    */     try {
/* 59 */       return PinyinHelper.convertToPinyinString(str, separator, this.format);
/* 60 */     } catch (PinyinException e) {
/* 61 */       throw new PinyinException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\engine\jpinyin\JPinyinEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */