/*    */ package cn.hutool.extra.pinyin;
/*    */ 
/*    */ import cn.hutool.extra.pinyin.engine.PinyinFactory;
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
/*    */ public class PinyinUtil
/*    */ {
/*    */   private static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";
/*    */   
/*    */   public static PinyinEngine getEngine() {
/* 21 */     return PinyinFactory.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getPinyin(char c) {
/* 31 */     return getEngine().getPinyin(c);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getPinyin(String str) {
/* 41 */     return getPinyin(str, " ");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getPinyin(String str, String separator) {
/* 52 */     return getEngine().getPinyin(str, separator);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static char getFirstLetter(char c) {
/* 62 */     return getEngine().getFirstLetter(c);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getFirstLetter(String str, String separator) {
/* 73 */     return getEngine().getFirstLetter(str, separator);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isChinese(char c) {
/* 83 */     return ('〇' == c || String.valueOf(c).matches("[\\u4e00-\\u9fa5]"));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\PinyinUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */