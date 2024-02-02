/*    */ package cn.hutool.extra.pinyin;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ public interface PinyinEngine
/*    */ {
/*    */   String getPinyin(char paramChar);
/*    */   
/*    */   String getPinyin(String paramString1, String paramString2);
/*    */   
/*    */   default char getFirstLetter(char c) {
/* 40 */     return getPinyin(c).charAt(0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default String getFirstLetter(String str, String separator) {
/* 51 */     String splitSeparator = StrUtil.isEmpty(separator) ? "#" : separator;
/* 52 */     List<String> split = StrUtil.split(getPinyin(str, splitSeparator), splitSeparator);
/* 53 */     return CollUtil.join(split, separator, s -> String.valueOf((s.length() > 0) ? Character.valueOf(s.charAt(0)) : ""));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\PinyinEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */