/*     */ package cn.hutool.core.net.url;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.net.RFC3986;
/*     */ import cn.hutool.core.net.URLDecoder;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class UrlPath
/*     */ {
/*     */   private List<String> segments;
/*     */   private boolean withEngTag;
/*     */   
/*     */   public static UrlPath of(CharSequence pathStr, Charset charset) {
/*  33 */     UrlPath urlPath = new UrlPath();
/*  34 */     urlPath.parse(pathStr, charset);
/*  35 */     return urlPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlPath setWithEndTag(boolean withEngTag) {
/*  45 */     this.withEngTag = withEngTag;
/*  46 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getSegments() {
/*  55 */     return this.segments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSegment(int index) {
/*  65 */     if (null == this.segments || index >= this.segments.size()) {
/*  66 */       return null;
/*     */     }
/*  68 */     return this.segments.get(index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlPath add(CharSequence segment) {
/*  78 */     addInternal(fixPath(segment), false);
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlPath addBefore(CharSequence segment) {
/*  89 */     addInternal(fixPath(segment), true);
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlPath parse(CharSequence path, Charset charset) {
/* 101 */     if (StrUtil.isNotEmpty(path)) {
/*     */       
/* 103 */       if (StrUtil.endWith(path, '/')) {
/* 104 */         this.withEngTag = true;
/*     */       }
/*     */       
/* 107 */       path = fixPath(path);
/* 108 */       if (StrUtil.isNotEmpty(path)) {
/* 109 */         List<String> split = StrUtil.split(path, '/');
/* 110 */         for (String seg : split) {
/* 111 */           addInternal(URLDecoder.decodeForPath(seg, charset), false);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 116 */     return this;
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
/*     */   public String build(Charset charset) {
/* 129 */     return build(charset, true);
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
/*     */   public String build(Charset charset, boolean encodePercent) {
/* 144 */     if (CollUtil.isEmpty(this.segments)) {
/* 145 */       return "";
/*     */     }
/*     */     
/* 148 */     (new char[1])[0] = '%'; char[] safeChars = encodePercent ? null : new char[1];
/* 149 */     StringBuilder builder = new StringBuilder();
/* 150 */     for (String segment : this.segments) {
/* 151 */       if (builder.length() == 0) {
/*     */ 
/*     */ 
/*     */         
/* 155 */         builder.append('/').append(RFC3986.SEGMENT_NZ_NC.encode(segment, charset, safeChars)); continue;
/*     */       } 
/* 157 */       builder.append('/').append(RFC3986.SEGMENT.encode(segment, charset, safeChars));
/*     */     } 
/*     */     
/* 160 */     if (StrUtil.isEmpty(builder)) {
/*     */       
/* 162 */       builder.append('/');
/* 163 */     } else if (this.withEngTag && false == StrUtil.endWith(builder, '/')) {
/*     */       
/* 165 */       builder.append('/');
/*     */     } 
/*     */     
/* 168 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 173 */     return build(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addInternal(CharSequence segment, boolean before) {
/* 183 */     if (this.segments == null) {
/* 184 */       this.segments = new LinkedList<>();
/*     */     }
/*     */     
/* 187 */     String seg = StrUtil.str(segment);
/* 188 */     if (before) {
/* 189 */       this.segments.add(0, seg);
/*     */     } else {
/* 191 */       this.segments.add(seg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String fixPath(CharSequence path) {
/* 202 */     Assert.notNull(path, "Path segment must be not null!", new Object[0]);
/* 203 */     if ("/".contentEquals(path)) {
/* 204 */       return "";
/*     */     }
/*     */     
/* 207 */     String segmentStr = StrUtil.trim(path);
/* 208 */     segmentStr = StrUtil.removePrefix(segmentStr, "/");
/* 209 */     segmentStr = StrUtil.removeSuffix(segmentStr, "/");
/* 210 */     segmentStr = StrUtil.trim(segmentStr);
/* 211 */     return segmentStr;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\ne\\url\UrlPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */