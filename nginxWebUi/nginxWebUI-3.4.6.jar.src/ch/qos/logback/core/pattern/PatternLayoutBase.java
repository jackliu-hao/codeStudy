/*     */ package ch.qos.logback.core.pattern;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.LayoutBase;
/*     */ import ch.qos.logback.core.pattern.parser.Node;
/*     */ import ch.qos.logback.core.pattern.parser.Parser;
/*     */ import ch.qos.logback.core.spi.ScanException;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusManager;
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
/*     */ public abstract class PatternLayoutBase<E>
/*     */   extends LayoutBase<E>
/*     */ {
/*     */   static final int INTIAL_STRING_BUILDER_SIZE = 256;
/*     */   Converter<E> head;
/*     */   String pattern;
/*     */   protected PostCompileProcessor<E> postCompileProcessor;
/*  35 */   Map<String, String> instanceConverterMap = new HashMap<String, String>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean outputPatternAsHeader = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract Map<String, String> getDefaultConverterMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getEffectiveConverterMap() {
/*  51 */     Map<String, String> effectiveMap = new HashMap<String, String>();
/*     */ 
/*     */     
/*  54 */     Map<String, String> defaultMap = getDefaultConverterMap();
/*  55 */     if (defaultMap != null) {
/*  56 */       effectiveMap.putAll(defaultMap);
/*     */     }
/*     */ 
/*     */     
/*  60 */     Context context = getContext();
/*  61 */     if (context != null) {
/*     */       
/*  63 */       Map<String, String> contextMap = (Map<String, String>)context.getObject("PATTERN_RULE_REGISTRY");
/*  64 */       if (contextMap != null) {
/*  65 */         effectiveMap.putAll(contextMap);
/*     */       }
/*     */     } 
/*     */     
/*  69 */     effectiveMap.putAll(this.instanceConverterMap);
/*  70 */     return effectiveMap;
/*     */   }
/*     */   
/*     */   public void start() {
/*  74 */     if (this.pattern == null || this.pattern.length() == 0) {
/*  75 */       addError("Empty or null pattern.");
/*     */       return;
/*     */     } 
/*     */     try {
/*  79 */       Parser<E> p = new Parser(this.pattern);
/*  80 */       if (getContext() != null) {
/*  81 */         p.setContext(getContext());
/*     */       }
/*  83 */       Node t = p.parse();
/*  84 */       this.head = p.compile(t, getEffectiveConverterMap());
/*  85 */       if (this.postCompileProcessor != null) {
/*  86 */         this.postCompileProcessor.process(this.context, this.head);
/*     */       }
/*  88 */       ConverterUtil.setContextForConverters(getContext(), this.head);
/*  89 */       ConverterUtil.startConverters(this.head);
/*  90 */       super.start();
/*  91 */     } catch (ScanException sce) {
/*  92 */       StatusManager sm = getContext().getStatusManager();
/*  93 */       sm.add((Status)new ErrorStatus("Failed to parse pattern \"" + getPattern() + "\".", this, (Throwable)sce));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setPostCompileProcessor(PostCompileProcessor<E> postCompileProcessor) {
/*  98 */     this.postCompileProcessor = postCompileProcessor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setContextForConverters(Converter<E> head) {
/* 108 */     ConverterUtil.setContextForConverters(getContext(), head);
/*     */   }
/*     */   
/*     */   protected String writeLoopOnConverters(E event) {
/* 112 */     StringBuilder strBuilder = new StringBuilder(256);
/* 113 */     Converter<E> c = this.head;
/* 114 */     while (c != null) {
/* 115 */       c.write(strBuilder, event);
/* 116 */       c = c.getNext();
/*     */     } 
/* 118 */     return strBuilder.toString();
/*     */   }
/*     */   
/*     */   public String getPattern() {
/* 122 */     return this.pattern;
/*     */   }
/*     */   
/*     */   public void setPattern(String pattern) {
/* 126 */     this.pattern = pattern;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 130 */     return getClass().getName() + "(\"" + getPattern() + "\")";
/*     */   }
/*     */   
/*     */   public Map<String, String> getInstanceConverterMap() {
/* 134 */     return this.instanceConverterMap;
/*     */   }
/*     */   
/*     */   protected String getPresentationHeaderPrefix() {
/* 138 */     return "";
/*     */   }
/*     */   
/*     */   public boolean isOutputPatternAsHeader() {
/* 142 */     return this.outputPatternAsHeader;
/*     */   }
/*     */   
/*     */   public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
/* 146 */     this.outputPatternAsHeader = outputPatternAsHeader;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPresentationHeader() {
/* 151 */     if (this.outputPatternAsHeader) {
/* 152 */       return getPresentationHeaderPrefix() + this.pattern;
/*     */     }
/* 154 */     return super.getPresentationHeader();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\PatternLayoutBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */