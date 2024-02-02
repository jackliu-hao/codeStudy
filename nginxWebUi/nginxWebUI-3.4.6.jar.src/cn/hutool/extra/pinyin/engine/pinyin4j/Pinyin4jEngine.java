/*     */ package cn.hutool.extra.pinyin.engine.pinyin4j;
/*     */ 
/*     */ import cn.hutool.core.text.StrBuilder;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.pinyin.PinyinEngine;
/*     */ import cn.hutool.extra.pinyin.PinyinException;
/*     */ import net.sourceforge.pinyin4j.PinyinHelper;
/*     */ import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
/*     */ import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
/*     */ import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
/*     */ import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
/*     */ import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
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
/*     */ public class Pinyin4jEngine
/*     */   implements PinyinEngine
/*     */ {
/*     */   HanyuPinyinOutputFormat format;
/*     */   
/*     */   public Pinyin4jEngine() {
/*  43 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pinyin4jEngine(HanyuPinyinOutputFormat format) {
/*  52 */     init(format);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(HanyuPinyinOutputFormat format) {
/*  61 */     if (null == format) {
/*  62 */       format = new HanyuPinyinOutputFormat();
/*     */       
/*  64 */       format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
/*     */       
/*  66 */       format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
/*     */       
/*  68 */       format.setVCharType(HanyuPinyinVCharType.WITH_V);
/*     */     } 
/*  70 */     this.format = format;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPinyin(char c) {
/*     */     String result;
/*     */     try {
/*  77 */       String[] results = PinyinHelper.toHanyuPinyinStringArray(c, this.format);
/*  78 */       result = ArrayUtil.isEmpty((Object[])results) ? String.valueOf(c) : results[0];
/*  79 */     } catch (BadHanyuPinyinOutputFormatCombination e) {
/*  80 */       result = String.valueOf(c);
/*     */     } 
/*  82 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPinyin(String str, String separator) {
/*  87 */     StrBuilder result = StrUtil.strBuilder();
/*  88 */     boolean isFirst = true;
/*  89 */     int strLen = str.length();
/*     */     try {
/*  91 */       for (int i = 0; i < strLen; i++) {
/*  92 */         if (isFirst) {
/*  93 */           isFirst = false;
/*     */         } else {
/*  95 */           result.append(separator);
/*     */         } 
/*  97 */         String[] pinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i), this.format);
/*  98 */         if (ArrayUtil.isEmpty((Object[])pinyinStringArray)) {
/*  99 */           result.append(str.charAt(i));
/*     */         } else {
/* 101 */           result.append(pinyinStringArray[0]);
/*     */         } 
/*     */       } 
/* 104 */     } catch (BadHanyuPinyinOutputFormatCombination e) {
/* 105 */       throw new PinyinException(e);
/*     */     } 
/*     */     
/* 108 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\engine\pinyin4j\Pinyin4jEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */