/*    */ package cn.hutool.extra.pinyin.engine.tinypinyin;
/*    */ 
/*    */ import cn.hutool.extra.pinyin.PinyinEngine;
/*    */ import com.github.promeg.pinyinhelper.Pinyin;
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
/*    */ public class TinyPinyinEngine
/*    */   implements PinyinEngine
/*    */ {
/*    */   public TinyPinyinEngine() {
/* 33 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TinyPinyinEngine(Pinyin.Config config) {
/* 41 */     Pinyin.init(config);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPinyin(char c) {
/* 46 */     if (false == Pinyin.isChinese(c)) {
/* 47 */       return String.valueOf(c);
/*    */     }
/* 49 */     return Pinyin.toPinyin(c).toLowerCase();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPinyin(String str, String separator) {
/* 54 */     return Pinyin.toPinyin(str, separator).toLowerCase();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\engine\tinypinyin\TinyPinyinEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */