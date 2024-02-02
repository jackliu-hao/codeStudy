/*     */ package org.apache.commons.compress.compressors.pack200;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Map;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.java.util.jar.Pack200;
/*     */ import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
/*     */ import org.apache.commons.compress.utils.IOUtils;
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
/*     */ 
/*     */ public class Pack200CompressorInputStream
/*     */   extends CompressorInputStream
/*     */ {
/*     */   private final InputStream originalInput;
/*     */   private final StreamBridge streamBridge;
/*     */   
/*     */   public Pack200CompressorInputStream(InputStream in) throws IOException {
/*  60 */     this(in, Pack200Strategy.IN_MEMORY);
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
/*     */   public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode) throws IOException {
/*  77 */     this(in, null, mode, null);
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
/*     */   public Pack200CompressorInputStream(InputStream in, Map<String, String> props) throws IOException {
/*  94 */     this(in, Pack200Strategy.IN_MEMORY, props);
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
/*     */ 
/*     */   
/*     */   public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode, Map<String, String> props) throws IOException {
/* 113 */     this(in, null, mode, props);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pack200CompressorInputStream(File f) throws IOException {
/* 124 */     this(f, Pack200Strategy.IN_MEMORY);
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
/*     */   public Pack200CompressorInputStream(File f, Pack200Strategy mode) throws IOException {
/* 137 */     this(null, f, mode, null);
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
/*     */   public Pack200CompressorInputStream(File f, Map<String, String> props) throws IOException {
/* 151 */     this(f, Pack200Strategy.IN_MEMORY, props);
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
/*     */   public Pack200CompressorInputStream(File f, Pack200Strategy mode, Map<String, String> props) throws IOException {
/* 166 */     this(null, f, mode, props);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Pack200CompressorInputStream(InputStream in, File f, Pack200Strategy mode, Map<String, String> props) throws IOException {
/* 173 */     this.originalInput = in;
/* 174 */     this.streamBridge = mode.newStreamBridge();
/* 175 */     try (JarOutputStream jarOut = new JarOutputStream(this.streamBridge)) {
/* 176 */       Pack200.Unpacker u = Pack200.newUnpacker();
/* 177 */       if (props != null) {
/* 178 */         u.properties().putAll(props);
/*     */       }
/* 180 */       if (f == null) {
/*     */ 
/*     */         
/* 183 */         u.unpack((InputStream)new CloseShieldFilterInputStream(in), jarOut);
/*     */       } else {
/* 185 */         u.unpack(f, jarOut);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 192 */     return this.streamBridge.getInput().read();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 197 */     return this.streamBridge.getInput().read(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int count) throws IOException {
/* 202 */     return this.streamBridge.getInput().read(b, off, count);
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 207 */     return this.streamBridge.getInput().available();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/*     */     try {
/* 213 */       return this.streamBridge.getInput().markSupported();
/* 214 */     } catch (IOException ex) {
/* 215 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void mark(int limit) {
/*     */     try {
/* 222 */       this.streamBridge.getInput().mark(limit);
/* 223 */     } catch (IOException ex) {
/* 224 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void reset() throws IOException {
/* 230 */     this.streamBridge.getInput().reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long count) throws IOException {
/* 235 */     return IOUtils.skip(this.streamBridge.getInput(), count);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 241 */       this.streamBridge.stop();
/*     */     } finally {
/* 243 */       if (this.originalInput != null) {
/* 244 */         this.originalInput.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/* 249 */   private static final byte[] CAFE_DOOD = new byte[] { -54, -2, -48, 13 };
/*     */ 
/*     */   
/* 252 */   private static final int SIG_LENGTH = CAFE_DOOD.length;
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 266 */     if (length < SIG_LENGTH) {
/* 267 */       return false;
/*     */     }
/*     */     
/* 270 */     for (int i = 0; i < SIG_LENGTH; i++) {
/* 271 */       if (signature[i] != CAFE_DOOD[i]) {
/* 272 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 276 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\pack200\Pack200CompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */