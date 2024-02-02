/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.util.EscapeUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ public class HtmlUtil
/*     */ {
/*     */   public static final String NBSP = "&nbsp;";
/*     */   public static final String AMP = "&amp;";
/*     */   public static final String QUOTE = "&quot;";
/*     */   public static final String APOS = "&apos;";
/*     */   public static final String LT = "&lt;";
/*     */   public static final String GT = "&gt;";
/*     */   public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
/*     */   public static final String RE_SCRIPT = "<[\\s]*?script[^>]*?>.*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
/*  29 */   private static final char[][] TEXT = new char[64][];
/*     */   
/*     */   static {
/*  32 */     for (int i = 0; i < 64; i++) {
/*  33 */       (new char[1])[0] = (char)i; TEXT[i] = new char[1];
/*     */     } 
/*     */ 
/*     */     
/*  37 */     TEXT[39] = "&#039;".toCharArray();
/*  38 */     TEXT[34] = "&quot;".toCharArray();
/*  39 */     TEXT[38] = "&amp;".toCharArray();
/*  40 */     TEXT[60] = "&lt;".toCharArray();
/*  41 */     TEXT[62] = "&gt;".toCharArray();
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
/*     */   public static String escape(String text) {
/*  58 */     return encode(text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String unescape(String htmlStr) {
/*  68 */     if (StrUtil.isBlank(htmlStr)) {
/*  69 */       return htmlStr;
/*     */     }
/*     */     
/*  72 */     return EscapeUtil.unescapeHtml4(htmlStr);
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
/*     */   public static String cleanHtmlTag(String content) {
/*  84 */     return content.replaceAll("(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)", "");
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
/*     */   public static String removeHtmlTag(String content, String... tagNames) {
/*  96 */     return removeHtmlTag(content, true, tagNames);
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
/*     */   public static String unwrapHtmlTag(String content, String... tagNames) {
/* 108 */     return removeHtmlTag(content, false, tagNames);
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
/*     */   public static String removeHtmlTag(String content, boolean withTagContent, String... tagNames) {
/* 122 */     for (String tagName : tagNames) {
/* 123 */       if (!StrUtil.isBlank(tagName)) {
/*     */         String regex;
/*     */         
/* 126 */         tagName = tagName.trim();
/*     */         
/* 128 */         if (withTagContent) {
/*     */           
/* 130 */           regex = StrUtil.format("(?i)<{}(\\s+[^>]*?)?/?>(.*?</{}>)?", new Object[] { tagName, tagName });
/*     */         } else {
/*     */           
/* 133 */           regex = StrUtil.format("(?i)<{}(\\s+[^>]*?)?/?>|</?{}>", new Object[] { tagName, tagName });
/*     */         } 
/*     */         
/* 136 */         content = ReUtil.delAll(regex, content);
/*     */       } 
/* 138 */     }  return content;
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
/*     */   public static String removeHtmlAttr(String content, String... attrs) {
/* 150 */     for (String attr : attrs) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 156 */       String regex = StrUtil.format("(?i)(\\s*{}\\s*=[^>]+?\\s+(?=>))|(\\s*{}\\s*=[^>]+?(?=\\s|>))", new Object[] { attr, attr });
/* 157 */       content = content.replaceAll(regex, "");
/*     */     } 
/* 159 */     return content;
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
/*     */   public static String removeAllHtmlAttr(String content, String... tagNames) {
/* 171 */     for (String tagName : tagNames) {
/* 172 */       String regex = StrUtil.format("(?i)<{}[^>]*?>", new Object[] { tagName });
/* 173 */       content = content.replaceAll(regex, StrUtil.format("<{}>", new Object[] { tagName }));
/*     */     } 
/* 175 */     return content;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String encode(String text) {
/*     */     int len;
/* 186 */     if (text == null || (len = text.length()) == 0) {
/* 187 */       return "";
/*     */     }
/* 189 */     StringBuilder buffer = new StringBuilder(len + (len >> 2));
/*     */     
/* 191 */     for (int i = 0; i < len; i++) {
/* 192 */       char c = text.charAt(i);
/* 193 */       if (c < '@') {
/* 194 */         buffer.append(TEXT[c]);
/*     */       } else {
/* 196 */         buffer.append(c);
/*     */       } 
/*     */     } 
/* 199 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String filter(String htmlContent) {
/* 209 */     return (new HTMLFilter()).filter(htmlContent);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HtmlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */