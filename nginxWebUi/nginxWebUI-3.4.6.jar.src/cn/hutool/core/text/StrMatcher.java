/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class StrMatcher
/*     */ {
/*     */   List<String> patterns;
/*     */   
/*     */   public StrMatcher(String pattern) {
/*  33 */     this.patterns = parse(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> match(String text) {
/*  43 */     HashMap<String, String> result = MapUtil.newHashMap(true);
/*  44 */     int from = 0;
/*  45 */     String key = null;
/*     */     
/*  47 */     for (String part : this.patterns) {
/*  48 */       if (StrUtil.isWrap(part, "${", "}")) {
/*     */         
/*  50 */         key = StrUtil.sub(part, 2, part.length() - 1); continue;
/*     */       } 
/*  52 */       int to = text.indexOf(part, from);
/*  53 */       if (to < 0)
/*     */       {
/*  55 */         return MapUtil.empty();
/*     */       }
/*  57 */       if (null != key && to > from)
/*     */       {
/*  59 */         result.put(key, text.substring(from, to));
/*     */       }
/*     */       
/*  62 */       from = to + part.length();
/*  63 */       key = null;
/*     */     } 
/*     */ 
/*     */     
/*  67 */     if (null != key && from < text.length())
/*     */     {
/*  69 */       result.put(key, text.substring(from));
/*     */     }
/*     */     
/*  72 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<String> parse(String pattern) {
/*  82 */     List<String> patterns = new ArrayList<>();
/*  83 */     int length = pattern.length();
/*  84 */     char c = Character.MIN_VALUE;
/*     */     
/*  86 */     boolean inVar = false;
/*  87 */     StringBuilder part = StrUtil.builder();
/*  88 */     for (int i = 0; i < length; i++) {
/*  89 */       char pre = c;
/*  90 */       c = pattern.charAt(i);
/*  91 */       if (inVar) {
/*  92 */         part.append(c);
/*  93 */         if ('}' == c) {
/*     */           
/*  95 */           inVar = false;
/*  96 */           patterns.add(part.toString());
/*  97 */           part.setLength(0);
/*     */         } 
/*  99 */       } else if ('{' == c && '$' == pre) {
/*     */         
/* 101 */         inVar = true;
/* 102 */         String preText = part.substring(0, part.length() - 1);
/* 103 */         if (StrUtil.isNotEmpty(preText)) {
/* 104 */           patterns.add(preText);
/*     */         }
/* 106 */         part.setLength(0);
/* 107 */         part.append(pre).append(c);
/*     */       } else {
/*     */         
/* 110 */         part.append(c);
/*     */       } 
/*     */     } 
/*     */     
/* 114 */     if (part.length() > 0) {
/* 115 */       patterns.add(part.toString());
/*     */     }
/* 117 */     return patterns;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\StrMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */