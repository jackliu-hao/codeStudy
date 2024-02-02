/*     */ package cn.hutool.extra.emoji;
/*     */ 
/*     */ import com.vdurmont.emoji.Emoji;
/*     */ import com.vdurmont.emoji.EmojiManager;
/*     */ import com.vdurmont.emoji.EmojiParser;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EmojiUtil
/*     */ {
/*     */   public static boolean isEmoji(String str) {
/*  28 */     return EmojiManager.isEmoji(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean containsEmoji(String str) {
/*  39 */     return EmojiManager.containsEmoji(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Emoji> getByTag(String tag) {
/*  49 */     return EmojiManager.getForTag(tag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Emoji get(String alias) {
/*  59 */     return EmojiManager.getForAlias(alias);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toUnicode(String str) {
/*  77 */     return EmojiParser.parseToUnicode(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toAlias(String str) {
/* 104 */     return toAlias(str, EmojiParser.FitzpatrickAction.PARSE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toAlias(String str, EmojiParser.FitzpatrickAction fitzpatrickAction) {
/* 117 */     return EmojiParser.parseToAliases(str, fitzpatrickAction);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toHtmlHex(String str) {
/* 129 */     return toHtml(str, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toHtml(String str) {
/* 141 */     return toHtml(str, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toHtml(String str, boolean isHex) {
/* 157 */     return isHex ? EmojiParser.parseToHtmlHexadecimal(str) : 
/* 158 */       EmojiParser.parseToHtmlDecimal(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String removeAllEmojis(String str) {
/* 168 */     return EmojiParser.removeAllEmojis(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> extractEmojis(String str) {
/* 178 */     return EmojiParser.extractEmojis(str);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\emoji\EmojiUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */