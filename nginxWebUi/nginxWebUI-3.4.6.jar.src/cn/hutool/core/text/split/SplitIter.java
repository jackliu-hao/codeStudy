/*     */ package cn.hutool.core.text.split;
/*     */ 
/*     */ import cn.hutool.core.collection.ComputeIter;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.text.finder.TextFinder;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Function;
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
/*     */ public class SplitIter
/*     */   extends ComputeIter<String>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final String text;
/*     */   private final TextFinder finder;
/*     */   private final int limit;
/*     */   private final boolean ignoreEmpty;
/*     */   private int offset;
/*     */   private int count;
/*     */   
/*     */   public SplitIter(CharSequence text, TextFinder separatorFinder, int limit, boolean ignoreEmpty) {
/*  47 */     Assert.notNull(text, "Text must be not null!", new Object[0]);
/*  48 */     this.text = text.toString();
/*  49 */     this.finder = separatorFinder.setText(text);
/*  50 */     this.limit = (limit > 0) ? limit : Integer.MAX_VALUE;
/*  51 */     this.ignoreEmpty = ignoreEmpty;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected String computeNext() {
/*  57 */     if (this.count >= this.limit || this.offset > this.text.length()) {
/*  58 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  62 */     if (this.count == this.limit - 1) {
/*     */       
/*  64 */       if (this.ignoreEmpty && this.offset == this.text.length())
/*     */       {
/*  66 */         return null;
/*     */       }
/*     */ 
/*     */       
/*  70 */       this.count++;
/*  71 */       return this.text.substring(this.offset);
/*     */     } 
/*     */     
/*  74 */     int start = this.finder.start(this.offset);
/*     */     
/*  76 */     if (start < 0) {
/*     */       
/*  78 */       if (this.offset <= this.text.length()) {
/*  79 */         String str = this.text.substring(this.offset);
/*  80 */         if (false == this.ignoreEmpty || false == str.isEmpty()) {
/*     */           
/*  82 */           this.offset = Integer.MAX_VALUE;
/*  83 */           return str;
/*     */         } 
/*     */       } 
/*  86 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*  90 */     String result = this.text.substring(this.offset, start);
/*  91 */     this.offset = this.finder.end(start);
/*     */     
/*  93 */     if (this.ignoreEmpty && result.isEmpty())
/*     */     {
/*  95 */       return computeNext();
/*     */     }
/*     */     
/*  98 */     this.count++;
/*  99 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 106 */     this.finder.reset();
/* 107 */     this.offset = 0;
/* 108 */     this.count = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] toArray(boolean trim) {
/* 118 */     return toList(trim).<String>toArray(new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> toList(boolean trim) {
/* 128 */     return toList(str -> trim ? StrUtil.trim(str) : str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> toList(Function<String, T> mapping) {
/* 139 */     List<T> result = new ArrayList<>();
/* 140 */     while (hasNext()) {
/* 141 */       T apply = mapping.apply(next());
/* 142 */       if (this.ignoreEmpty && StrUtil.isEmptyIfStr(apply)) {
/*     */         continue;
/*     */       }
/*     */       
/* 146 */       result.add(apply);
/*     */     } 
/* 148 */     if (result.isEmpty()) {
/* 149 */       return new ArrayList<>(0);
/*     */     }
/* 151 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\split\SplitIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */