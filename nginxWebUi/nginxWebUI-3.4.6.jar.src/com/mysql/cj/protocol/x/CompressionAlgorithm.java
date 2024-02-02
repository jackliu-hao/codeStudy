/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.zip.InflaterInputStream;
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
/*     */ public class CompressionAlgorithm
/*     */ {
/*  46 */   private static final Map<String, String> ALIASES = new HashMap<>();
/*     */   static {
/*  48 */     ALIASES.put("deflate", "deflate_stream");
/*  49 */     ALIASES.put("lz4", "lz4_message");
/*  50 */     ALIASES.put("zstd", "zstd_stream");
/*     */   }
/*     */   
/*     */   private String algorithmIdentifier;
/*     */   private CompressionMode compressionMode;
/*     */   private String inputStreamClassFqn;
/*  56 */   private Class<?> inputStreamClass = null;
/*     */   private String outputStreamClassFqn;
/*  58 */   private Class<?> outputStreamClass = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, CompressionAlgorithm> getDefaultInstances() {
/*  66 */     HashMap<String, CompressionAlgorithm> defaultInstances = new HashMap<>();
/*  67 */     defaultInstances.put("deflate_stream", new CompressionAlgorithm("deflate_stream", InflaterInputStream.class
/*  68 */           .getName(), SyncFlushDeflaterOutputStream.class.getName()));
/*  69 */     return defaultInstances;
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
/*     */   public static String getNormalizedAlgorithmName(String name) {
/*  83 */     return ALIASES.getOrDefault(name, name);
/*     */   }
/*     */   
/*     */   public CompressionAlgorithm(String name, String inputStreamClassFqn, String outputStreamClassFqn) {
/*  87 */     this.algorithmIdentifier = getNormalizedAlgorithmName(name);
/*  88 */     String[] nameMode = this.algorithmIdentifier.split("_");
/*  89 */     if (nameMode.length != 2) {
/*  90 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.4", new Object[] { name }));
/*     */     }
/*     */     try {
/*  93 */       CompressionMode mode = CompressionMode.valueOf(nameMode[1].toUpperCase());
/*  94 */       this.compressionMode = mode;
/*  95 */     } catch (IllegalArgumentException e) {
/*  96 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.5", new Object[] { nameMode[1] }));
/*     */     } 
/*  98 */     this.inputStreamClassFqn = inputStreamClassFqn;
/*  99 */     this.outputStreamClassFqn = outputStreamClassFqn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAlgorithmIdentifier() {
/* 108 */     return this.algorithmIdentifier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressionMode getCompressionMode() {
/* 117 */     return this.compressionMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getInputStreamClass() {
/* 126 */     if (this.inputStreamClass == null) {
/*     */       try {
/* 128 */         this.inputStreamClass = Class.forName(this.inputStreamClassFqn);
/* 129 */       } catch (ClassNotFoundException e) {
/* 130 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 131 */             Messages.getString("Protocol.Compression.3", new Object[] { this.inputStreamClassFqn }), e);
/*     */       } 
/*     */     }
/* 134 */     return this.inputStreamClass;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getOutputStreamClass() {
/* 143 */     if (this.outputStreamClass == null) {
/*     */       try {
/* 145 */         this.outputStreamClass = Class.forName(this.outputStreamClassFqn);
/* 146 */       } catch (ClassNotFoundException e) {
/* 147 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 148 */             Messages.getString("Protocol.Compression.3", new Object[] { this.outputStreamClassFqn }), e);
/*     */       } 
/*     */     }
/* 151 */     return this.outputStreamClass;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\CompressionAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */