/*     */ package ch.qos.logback.core.encoder;
/*     */ 
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.Layout;
/*     */ import ch.qos.logback.core.OutputStreamAppender;
/*     */ import ch.qos.logback.core.spi.ContextAware;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class LayoutWrappingEncoder<E>
/*     */   extends EncoderBase<E>
/*     */ {
/*     */   protected Layout<E> layout;
/*     */   private Charset charset;
/*     */   ContextAware parent;
/*  38 */   Boolean immediateFlush = null;
/*     */   
/*     */   public Layout<E> getLayout() {
/*  41 */     return this.layout;
/*     */   }
/*     */   
/*     */   public void setLayout(Layout<E> layout) {
/*  45 */     this.layout = layout;
/*     */   }
/*     */   
/*     */   public Charset getCharset() {
/*  49 */     return this.charset;
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
/*     */   public void setCharset(Charset charset) {
/*  63 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setImmediateFlush(boolean immediateFlush) {
/*  74 */     addWarn("As of version 1.2.0 \"immediateFlush\" property should be set within the enclosing Appender.");
/*  75 */     addWarn("Please move \"immediateFlush\" property into the enclosing appender.");
/*  76 */     this.immediateFlush = Boolean.valueOf(immediateFlush);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] headerBytes() {
/*  81 */     if (this.layout == null) {
/*  82 */       return null;
/*     */     }
/*  84 */     StringBuilder sb = new StringBuilder();
/*  85 */     appendIfNotNull(sb, this.layout.getFileHeader());
/*  86 */     appendIfNotNull(sb, this.layout.getPresentationHeader());
/*  87 */     if (sb.length() > 0)
/*     */     {
/*     */ 
/*     */       
/*  91 */       sb.append(CoreConstants.LINE_SEPARATOR);
/*     */     }
/*  93 */     return convertToBytes(sb.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] footerBytes() {
/*  98 */     if (this.layout == null) {
/*  99 */       return null;
/*     */     }
/* 101 */     StringBuilder sb = new StringBuilder();
/* 102 */     appendIfNotNull(sb, this.layout.getPresentationFooter());
/* 103 */     appendIfNotNull(sb, this.layout.getFileFooter());
/* 104 */     return convertToBytes(sb.toString());
/*     */   }
/*     */   
/*     */   private byte[] convertToBytes(String s) {
/* 108 */     if (this.charset == null) {
/* 109 */       return s.getBytes();
/*     */     }
/* 111 */     return s.getBytes(this.charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encode(E event) {
/* 116 */     String txt = this.layout.doLayout(event);
/* 117 */     return convertToBytes(txt);
/*     */   }
/*     */   
/*     */   public boolean isStarted() {
/* 121 */     return false;
/*     */   }
/*     */   
/*     */   public void start() {
/* 125 */     if (this.immediateFlush != null) {
/* 126 */       if (this.parent instanceof OutputStreamAppender) {
/* 127 */         addWarn("Setting the \"immediateFlush\" property of the enclosing appender to " + this.immediateFlush);
/*     */         
/* 129 */         OutputStreamAppender<E> parentOutputStreamAppender = (OutputStreamAppender<E>)this.parent;
/* 130 */         parentOutputStreamAppender.setImmediateFlush(this.immediateFlush.booleanValue());
/*     */       } else {
/* 132 */         addError("Could not set the \"immediateFlush\" property of the enclosing appender.");
/*     */       } 
/*     */     }
/* 135 */     this.started = true;
/*     */   }
/*     */   
/*     */   public void stop() {
/* 139 */     this.started = false;
/*     */   }
/*     */   
/*     */   private void appendIfNotNull(StringBuilder sb, String s) {
/* 143 */     if (s != null) {
/* 144 */       sb.append(s);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParent(ContextAware parent) {
/* 155 */     this.parent = parent;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\encoder\LayoutWrappingEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */