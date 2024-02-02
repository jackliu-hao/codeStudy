/*     */ package org.apache.commons.compress.compressors.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Map;
/*     */ import java.util.jar.JarInputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*     */ import org.apache.commons.compress.java.util.jar.Pack200;
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
/*     */ public class Pack200CompressorOutputStream
/*     */   extends CompressorOutputStream
/*     */ {
/*     */   private boolean finished;
/*     */   private final OutputStream originalOutput;
/*     */   private final StreamBridge streamBridge;
/*     */   private final Map<String, String> properties;
/*     */   
/*     */   public Pack200CompressorOutputStream(OutputStream out) throws IOException {
/*  51 */     this(out, Pack200Strategy.IN_MEMORY);
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
/*     */   public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode) throws IOException {
/*  65 */     this(out, mode, null);
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
/*     */   public Pack200CompressorOutputStream(OutputStream out, Map<String, String> props) throws IOException {
/*  79 */     this(out, Pack200Strategy.IN_MEMORY, props);
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
/*     */   public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode, Map<String, String> props) throws IOException {
/*  95 */     this.originalOutput = out;
/*  96 */     this.streamBridge = mode.newStreamBridge();
/*  97 */     this.properties = props;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 102 */     this.streamBridge.write(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 107 */     this.streamBridge.write(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int from, int length) throws IOException {
/* 112 */     this.streamBridge.write(b, from, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 118 */       finish();
/*     */     } finally {
/*     */       try {
/* 121 */         this.streamBridge.stop();
/*     */       } finally {
/* 123 */         this.originalOutput.close();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void finish() throws IOException {
/* 129 */     if (!this.finished) {
/* 130 */       this.finished = true;
/* 131 */       Pack200.Packer p = Pack200.newPacker();
/* 132 */       if (this.properties != null) {
/* 133 */         p.properties().putAll(this.properties);
/*     */       }
/* 135 */       try (JarInputStream ji = new JarInputStream(this.streamBridge.getInput())) {
/* 136 */         p.pack(ji, this.originalOutput);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\pack200\Pack200CompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */