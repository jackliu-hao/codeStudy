/*    */ package cn.hutool.extra.pinyin.engine.bopomofo4j;
/*    */ 
/*    */ import cn.hutool.extra.pinyin.PinyinEngine;
/*    */ import com.rnkrsoft.bopomofo4j.Bopomofo4j;
/*    */ import com.rnkrsoft.bopomofo4j.ToneType;
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
/*    */ public class Bopomofo4jEngine
/*    */   implements PinyinEngine
/*    */ {
/*    */   public Bopomofo4jEngine() {
/* 31 */     Bopomofo4j.local();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPinyin(char c) {
/* 36 */     return Bopomofo4j.pinyin(String.valueOf(c), ToneType.WITHOUT_TONE, Boolean.valueOf(false), Boolean.valueOf(false), "");
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPinyin(String str, String separator) {
/* 41 */     return Bopomofo4j.pinyin(str, ToneType.WITHOUT_TONE, Boolean.valueOf(false), Boolean.valueOf(false), separator);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\engine\bopomofo4j\Bopomofo4jEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */