/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringCollectionUtil
/*     */ {
/*     */   public static void retainMatching(Collection<String> values, String... patterns) {
/*  41 */     retainMatching(values, Arrays.asList(patterns));
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
/*     */   public static void retainMatching(Collection<String> values, Collection<String> patterns) {
/*  56 */     if (patterns.isEmpty())
/*     */       return; 
/*  58 */     List<String> matches = new ArrayList<String>(values.size());
/*  59 */     for (String p : patterns) {
/*  60 */       Pattern pattern = Pattern.compile(p);
/*  61 */       for (String value : values) {
/*  62 */         if (pattern.matcher(value).matches()) {
/*  63 */           matches.add(value);
/*     */         }
/*     */       } 
/*     */     } 
/*  67 */     values.retainAll(matches);
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
/*     */   public static void removeMatching(Collection<String> values, String... patterns) {
/*  82 */     removeMatching(values, Arrays.asList(patterns));
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
/*     */   public static void removeMatching(Collection<String> values, Collection<String> patterns) {
/*  97 */     List<String> matches = new ArrayList<String>(values.size());
/*  98 */     for (String p : patterns) {
/*  99 */       Pattern pattern = Pattern.compile(p);
/* 100 */       for (String value : values) {
/* 101 */         if (pattern.matcher(value).matches()) {
/* 102 */           matches.add(value);
/*     */         }
/*     */       } 
/*     */     } 
/* 106 */     values.removeAll(matches);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\StringCollectionUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */