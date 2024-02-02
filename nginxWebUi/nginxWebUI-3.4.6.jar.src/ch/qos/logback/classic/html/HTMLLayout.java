/*     */ package ch.qos.logback.classic.html;
/*     */ 
/*     */ import ch.qos.logback.classic.PatternLayout;
/*     */ import ch.qos.logback.classic.pattern.MDCConverter;
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.helpers.Transform;
/*     */ import ch.qos.logback.core.html.HTMLLayoutBase;
/*     */ import ch.qos.logback.core.html.IThrowableRenderer;
/*     */ import ch.qos.logback.core.pattern.Converter;
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
/*     */ public class HTMLLayout
/*     */   extends HTMLLayoutBase<ILoggingEvent>
/*     */ {
/*     */   static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";
/*  56 */   IThrowableRenderer<ILoggingEvent> throwableRenderer = new DefaultThrowableRenderer();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  62 */     int errorCount = 0;
/*  63 */     if (this.throwableRenderer == null) {
/*  64 */       addError("ThrowableRender cannot be null.");
/*  65 */       errorCount++;
/*     */     } 
/*  67 */     if (errorCount == 0) {
/*  68 */       super.start();
/*     */     }
/*     */   }
/*     */   
/*     */   protected Map<String, String> getDefaultConverterMap() {
/*  73 */     return PatternLayout.DEFAULT_CONVERTER_MAP;
/*     */   }
/*     */   
/*     */   public String doLayout(ILoggingEvent event) {
/*  77 */     StringBuilder buf = new StringBuilder();
/*  78 */     startNewTableIfLimitReached(buf);
/*     */     
/*  80 */     boolean odd = true;
/*  81 */     if ((this.counter++ & 0x1L) == 0L) {
/*  82 */       odd = false;
/*     */     }
/*     */     
/*  85 */     String level = event.getLevel().toString().toLowerCase();
/*     */     
/*  87 */     buf.append(CoreConstants.LINE_SEPARATOR);
/*  88 */     buf.append("<tr class=\"");
/*  89 */     buf.append(level);
/*  90 */     if (odd) {
/*  91 */       buf.append(" odd\">");
/*     */     } else {
/*  93 */       buf.append(" even\">");
/*     */     } 
/*  95 */     buf.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/*  97 */     Converter<ILoggingEvent> c = this.head;
/*  98 */     while (c != null) {
/*  99 */       appendEventToBuffer(buf, c, event);
/* 100 */       c = c.getNext();
/*     */     } 
/* 102 */     buf.append("</tr>");
/* 103 */     buf.append(CoreConstants.LINE_SEPARATOR);
/*     */     
/* 105 */     if (event.getThrowableProxy() != null) {
/* 106 */       this.throwableRenderer.render(buf, event);
/*     */     }
/* 108 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
/* 112 */     buf.append("<td class=\"");
/* 113 */     buf.append(computeConverterName(c));
/* 114 */     buf.append("\">");
/* 115 */     buf.append(Transform.escapeTags(c.convert(event)));
/* 116 */     buf.append("</td>");
/* 117 */     buf.append(CoreConstants.LINE_SEPARATOR);
/*     */   }
/*     */   
/*     */   public IThrowableRenderer getThrowableRenderer() {
/* 121 */     return this.throwableRenderer;
/*     */   }
/*     */   
/*     */   public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
/* 125 */     this.throwableRenderer = throwableRenderer;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String computeConverterName(Converter c) {
/* 130 */     if (c instanceof MDCConverter) {
/* 131 */       MDCConverter mc = (MDCConverter)c;
/* 132 */       String key = mc.getFirstOption();
/* 133 */       if (key != null) {
/* 134 */         return key;
/*     */       }
/* 136 */       return "MDC";
/*     */     } 
/*     */     
/* 139 */     return super.computeConverterName(c);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\html\HTMLLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */