/*     */ package ch.qos.logback.core.html;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.LayoutBase;
/*     */ import ch.qos.logback.core.pattern.Converter;
/*     */ import ch.qos.logback.core.pattern.ConverterUtil;
/*     */ import ch.qos.logback.core.pattern.parser.Node;
/*     */ import ch.qos.logback.core.pattern.parser.Parser;
/*     */ import ch.qos.logback.core.spi.ScanException;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HTMLLayoutBase<E>
/*     */   extends LayoutBase<E>
/*     */ {
/*     */   protected String pattern;
/*     */   protected Converter<E> head;
/*  42 */   protected String title = "Logback Log Messages";
/*     */ 
/*     */ 
/*     */   
/*     */   protected CssBuilder cssBuilder;
/*     */ 
/*     */   
/*  49 */   protected long counter = 0L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPattern(String conversionPattern) {
/*  57 */     this.pattern = conversionPattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPattern() {
/*  64 */     return this.pattern;
/*     */   }
/*     */   
/*     */   public CssBuilder getCssBuilder() {
/*  68 */     return this.cssBuilder;
/*     */   }
/*     */   
/*     */   public void setCssBuilder(CssBuilder cssBuilder) {
/*  72 */     this.cssBuilder = cssBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  80 */     int errorCount = 0;
/*     */     
/*     */     try {
/*  83 */       Parser<E> p = new Parser(this.pattern);
/*  84 */       p.setContext(getContext());
/*  85 */       Node t = p.parse();
/*  86 */       this.head = p.compile(t, getEffectiveConverterMap());
/*  87 */       ConverterUtil.startConverters(this.head);
/*  88 */     } catch (ScanException ex) {
/*  89 */       addError("Incorrect pattern found", (Throwable)ex);
/*  90 */       errorCount++;
/*     */     } 
/*     */     
/*  93 */     if (errorCount == 0) {
/*  94 */       this.started = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Map<String, String> getDefaultConverterMap();
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getEffectiveConverterMap() {
/* 105 */     Map<String, String> effectiveMap = new HashMap<String, String>();
/*     */ 
/*     */     
/* 108 */     Map<String, String> defaultMap = getDefaultConverterMap();
/* 109 */     if (defaultMap != null) {
/* 110 */       effectiveMap.putAll(defaultMap);
/*     */     }
/*     */ 
/*     */     
/* 114 */     Context context = getContext();
/* 115 */     if (context != null) {
/*     */       
/* 117 */       Map<String, String> contextMap = (Map<String, String>)context.getObject("PATTERN_RULE_REGISTRY");
/* 118 */       if (contextMap != null) {
/* 119 */         effectiveMap.putAll(contextMap);
/*     */       }
/*     */     } 
/* 122 */     return effectiveMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTitle(String title) {
/* 133 */     this.title = title;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTitle() {
/* 140 */     return this.title;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 148 */     return "text/html";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileHeader() {
/* 156 */     StringBuilder sbuf = new StringBuilder();
/* 157 */     sbuf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
/* 158 */     sbuf.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
/* 159 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 160 */     sbuf.append("<html>");
/* 161 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 162 */     sbuf.append("  <head>");
/* 163 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 164 */     sbuf.append("    <title>");
/* 165 */     sbuf.append(this.title);
/* 166 */     sbuf.append("</title>");
/* 167 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/* 169 */     this.cssBuilder.addCss(sbuf);
/*     */     
/* 171 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 172 */     sbuf.append("  </head>");
/* 173 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 174 */     sbuf.append("<body>");
/* 175 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/* 177 */     return sbuf.toString();
/*     */   }
/*     */   
/*     */   public String getPresentationHeader() {
/* 181 */     StringBuilder sbuf = new StringBuilder();
/* 182 */     sbuf.append("<hr/>");
/* 183 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 184 */     sbuf.append("<p>Log session start time ");
/* 185 */     sbuf.append(new Date());
/* 186 */     sbuf.append("</p><p></p>");
/* 187 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 188 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 189 */     sbuf.append("<table cellspacing=\"0\">");
/* 190 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/* 192 */     buildHeaderRowForTable(sbuf);
/*     */     
/* 194 */     return sbuf.toString();
/*     */   }
/*     */   
/*     */   private void buildHeaderRowForTable(StringBuilder sbuf) {
/* 198 */     Converter<E> c = this.head;
/*     */     
/* 200 */     sbuf.append("<tr class=\"header\">");
/* 201 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 202 */     while (c != null) {
/* 203 */       String name = computeConverterName(c);
/* 204 */       if (name == null) {
/* 205 */         c = c.getNext();
/*     */         continue;
/*     */       } 
/* 208 */       sbuf.append("<td class=\"");
/* 209 */       sbuf.append(computeConverterName(c));
/* 210 */       sbuf.append("\">");
/* 211 */       sbuf.append(computeConverterName(c));
/* 212 */       sbuf.append("</td>");
/* 213 */       sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 214 */       c = c.getNext();
/*     */     } 
/* 216 */     sbuf.append("</tr>");
/* 217 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/*     */   }
/*     */   
/*     */   public String getPresentationFooter() {
/* 221 */     StringBuilder sbuf = new StringBuilder();
/* 222 */     sbuf.append("</table>");
/* 223 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFileFooter() {
/* 231 */     StringBuilder sbuf = new StringBuilder();
/* 232 */     sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 233 */     sbuf.append("</body></html>");
/* 234 */     return sbuf.toString();
/*     */   }
/*     */   
/*     */   protected void startNewTableIfLimitReached(StringBuilder sbuf) {
/* 238 */     if (this.counter >= 10000L) {
/* 239 */       this.counter = 0L;
/* 240 */       sbuf.append("</table>");
/* 241 */       sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 242 */       sbuf.append("<p></p>");
/* 243 */       sbuf.append("<table cellspacing=\"0\">");
/* 244 */       sbuf.append(CoreConstants.LINE_SEPARATOR);
/* 245 */       buildHeaderRowForTable(sbuf);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected String computeConverterName(Converter c) {
/* 250 */     String className = c.getClass().getSimpleName();
/* 251 */     int index = className.indexOf("Converter");
/* 252 */     if (index == -1) {
/* 253 */       return className;
/*     */     }
/* 255 */     return className.substring(0, index);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\html\HTMLLayoutBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */