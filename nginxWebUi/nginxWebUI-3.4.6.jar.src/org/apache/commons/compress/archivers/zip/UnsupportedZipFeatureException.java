/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.zip.ZipException;
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
/*     */ public class UnsupportedZipFeatureException
/*     */   extends ZipException
/*     */ {
/*     */   private final Feature reason;
/*     */   private final transient ZipArchiveEntry entry;
/*     */   private static final long serialVersionUID = 20161219L;
/*     */   
/*     */   public UnsupportedZipFeatureException(Feature reason, ZipArchiveEntry entry) {
/*  41 */     super("Unsupported feature " + reason + " used in entry " + entry
/*  42 */         .getName());
/*  43 */     this.reason = reason;
/*  44 */     this.entry = entry;
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
/*     */   public UnsupportedZipFeatureException(ZipMethod method, ZipArchiveEntry entry) {
/*  56 */     super("Unsupported compression method " + entry.getMethod() + " (" + method
/*  57 */         .name() + ") used in entry " + entry.getName());
/*  58 */     this.reason = Feature.METHOD;
/*  59 */     this.entry = entry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnsupportedZipFeatureException(Feature reason) {
/*  70 */     super("Unsupported feature " + reason + " used in archive.");
/*  71 */     this.reason = reason;
/*  72 */     this.entry = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Feature getFeature() {
/*  80 */     return this.reason;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZipArchiveEntry getEntry() {
/*  88 */     return this.entry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Feature
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 4112582948775420359L;
/*     */ 
/*     */ 
/*     */     
/* 101 */     public static final Feature ENCRYPTION = new Feature("encryption");
/*     */ 
/*     */ 
/*     */     
/* 105 */     public static final Feature METHOD = new Feature("compression method");
/*     */ 
/*     */ 
/*     */     
/* 109 */     public static final Feature DATA_DESCRIPTOR = new Feature("data descriptor");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     public static final Feature SPLITTING = new Feature("splitting");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     public static final Feature UNKNOWN_COMPRESSED_SIZE = new Feature("unknown compressed size");
/*     */     
/*     */     private final String name;
/*     */     
/*     */     private Feature(String name) {
/* 126 */       this.name = name;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 131 */       return this.name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\UnsupportedZipFeatureException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */