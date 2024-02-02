/*     */ package ch.qos.logback.core;
/*     */ 
/*     */ import ch.qos.logback.core.encoder.Encoder;
/*     */ import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
/*     */ import ch.qos.logback.core.spi.DeferredProcessingAware;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ public class OutputStreamAppender<E>
/*     */   extends UnsynchronizedAppenderBase<E>
/*     */ {
/*     */   protected Encoder<E> encoder;
/*  47 */   protected final ReentrantLock lock = new ReentrantLock(false);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private OutputStream outputStream;
/*     */ 
/*     */ 
/*     */   
/*     */   boolean immediateFlush = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOutputStream() {
/*  62 */     return this.outputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  70 */     int errors = 0;
/*  71 */     if (this.encoder == null) {
/*  72 */       addStatus((Status)new ErrorStatus("No encoder set for the appender named \"" + this.name + "\".", this));
/*  73 */       errors++;
/*     */     } 
/*     */     
/*  76 */     if (this.outputStream == null) {
/*  77 */       addStatus((Status)new ErrorStatus("No output stream set for the appender named \"" + this.name + "\".", this));
/*  78 */       errors++;
/*     */     } 
/*     */     
/*  81 */     if (errors == 0) {
/*  82 */       super.start();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLayout(Layout<E> layout) {
/*  87 */     addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
/*  88 */     addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
/*  89 */     addWarn("See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details");
/*  90 */     LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder();
/*  91 */     lwe.setLayout(layout);
/*  92 */     lwe.setContext(this.context);
/*  93 */     this.encoder = (Encoder<E>)lwe;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void append(E eventObject) {
/*  98 */     if (!isStarted()) {
/*     */       return;
/*     */     }
/*     */     
/* 102 */     subAppend(eventObject);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 113 */     this.lock.lock();
/*     */     try {
/* 115 */       closeOutputStream();
/* 116 */       super.stop();
/*     */     } finally {
/* 118 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void closeOutputStream() {
/* 126 */     if (this.outputStream != null) {
/*     */       
/*     */       try {
/* 129 */         encoderClose();
/* 130 */         this.outputStream.close();
/* 131 */         this.outputStream = null;
/* 132 */       } catch (IOException e) {
/* 133 */         addStatus((Status)new ErrorStatus("Could not close output stream for OutputStreamAppender.", this, e));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   void encoderClose() {
/* 139 */     if (this.encoder != null && this.outputStream != null) {
/*     */       try {
/* 141 */         byte[] footer = this.encoder.footerBytes();
/* 142 */         writeBytes(footer);
/* 143 */       } catch (IOException ioe) {
/* 144 */         this.started = false;
/* 145 */         addStatus((Status)new ErrorStatus("Failed to write footer for appender named [" + this.name + "].", this, ioe));
/*     */       } 
/*     */     }
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
/*     */   public void setOutputStream(OutputStream outputStream) {
/* 161 */     this.lock.lock();
/*     */     
/*     */     try {
/* 164 */       closeOutputStream();
/* 165 */       this.outputStream = outputStream;
/* 166 */       if (this.encoder == null) {
/* 167 */         addWarn("Encoder has not been set. Cannot invoke its init method.");
/*     */         
/*     */         return;
/*     */       } 
/* 171 */       encoderInit();
/*     */     } finally {
/* 173 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   void encoderInit() {
/* 178 */     if (this.encoder != null && this.outputStream != null)
/*     */       try {
/* 180 */         byte[] header = this.encoder.headerBytes();
/* 181 */         writeBytes(header);
/* 182 */       } catch (IOException ioe) {
/* 183 */         this.started = false;
/* 184 */         addStatus((Status)new ErrorStatus("Failed to initialize encoder for appender named [" + this.name + "].", this, ioe));
/*     */       }  
/*     */   }
/*     */   
/*     */   protected void writeOut(E event) throws IOException {
/* 189 */     byte[] byteArray = this.encoder.encode(event);
/* 190 */     writeBytes(byteArray);
/*     */   }
/*     */   
/*     */   private void writeBytes(byte[] byteArray) throws IOException {
/* 194 */     if (byteArray == null || byteArray.length == 0) {
/*     */       return;
/*     */     }
/* 197 */     this.lock.lock();
/*     */     try {
/* 199 */       this.outputStream.write(byteArray);
/* 200 */       if (this.immediateFlush) {
/* 201 */         this.outputStream.flush();
/*     */       }
/*     */     } finally {
/* 204 */       this.lock.unlock();
/*     */     } 
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
/*     */   protected void subAppend(E event) {
/* 217 */     if (!isStarted()) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 222 */       if (event instanceof DeferredProcessingAware) {
/* 223 */         ((DeferredProcessingAware)event).prepareForDeferredProcessing();
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 230 */       byte[] byteArray = this.encoder.encode(event);
/* 231 */       writeBytes(byteArray);
/*     */     }
/* 233 */     catch (IOException ioe) {
/*     */ 
/*     */       
/* 236 */       this.started = false;
/* 237 */       addStatus((Status)new ErrorStatus("IO failure in appender", this, ioe));
/*     */     } 
/*     */   }
/*     */   
/*     */   public Encoder<E> getEncoder() {
/* 242 */     return this.encoder;
/*     */   }
/*     */   
/*     */   public void setEncoder(Encoder<E> encoder) {
/* 246 */     this.encoder = encoder;
/*     */   }
/*     */   
/*     */   public boolean isImmediateFlush() {
/* 250 */     return this.immediateFlush;
/*     */   }
/*     */   
/*     */   public void setImmediateFlush(boolean immediateFlush) {
/* 254 */     this.immediateFlush = immediateFlush;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\OutputStreamAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */