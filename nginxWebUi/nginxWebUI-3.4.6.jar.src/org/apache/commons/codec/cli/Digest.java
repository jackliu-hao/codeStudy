/*     */ package org.apache.commons.codec.cli;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import org.apache.commons.codec.binary.Hex;
/*     */ import org.apache.commons.codec.digest.DigestUtils;
/*     */ import org.apache.commons.codec.digest.MessageDigestAlgorithms;
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
/*     */ public class Digest
/*     */ {
/*     */   private final String algorithm;
/*     */   private final String[] args;
/*     */   private final String[] inputs;
/*     */   
/*     */   public static void main(String[] args) throws IOException {
/*  53 */     (new Digest(args)).run();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Digest(String[] args) {
/*  61 */     if (args == null) {
/*  62 */       throw new IllegalArgumentException("args");
/*     */     }
/*  64 */     if (args.length == 0) {
/*  65 */       throw new IllegalArgumentException(
/*  66 */           String.format("Usage: java %s [algorithm] [FILE|DIRECTORY|string] ...", new Object[] { Digest.class.getName() }));
/*     */     }
/*  68 */     this.args = args;
/*  69 */     this.algorithm = args[0];
/*  70 */     if (args.length <= 1) {
/*  71 */       this.inputs = null;
/*     */     } else {
/*  73 */       this.inputs = new String[args.length - 1];
/*  74 */       System.arraycopy(args, 1, this.inputs, 0, this.inputs.length);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void println(String prefix, byte[] digest) {
/*  79 */     println(prefix, digest, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void println(String prefix, byte[] digest, String fileName) {
/*  88 */     System.out.println(prefix + Hex.encodeHexString(digest) + ((fileName != null) ? ("  " + fileName) : ""));
/*     */   }
/*     */   
/*     */   private void run() throws IOException {
/*  92 */     if (this.algorithm.equalsIgnoreCase("ALL") || this.algorithm.equals("*")) {
/*  93 */       run(MessageDigestAlgorithms.values());
/*     */       return;
/*     */     } 
/*  96 */     MessageDigest messageDigest = DigestUtils.getDigest(this.algorithm, null);
/*  97 */     if (messageDigest != null) {
/*  98 */       run("", messageDigest);
/*     */     } else {
/* 100 */       run("", DigestUtils.getDigest(this.algorithm.toUpperCase(Locale.ROOT)));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void run(String[] digestAlgorithms) throws IOException {
/* 105 */     for (String messageDigestAlgorithm : digestAlgorithms) {
/* 106 */       if (DigestUtils.isAvailable(messageDigestAlgorithm)) {
/* 107 */         run(messageDigestAlgorithm + " ", messageDigestAlgorithm);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void run(String prefix, MessageDigest messageDigest) throws IOException {
/* 113 */     if (this.inputs == null) {
/* 114 */       println(prefix, DigestUtils.digest(messageDigest, System.in));
/*     */       return;
/*     */     } 
/* 117 */     for (String source : this.inputs) {
/* 118 */       File file = new File(source);
/* 119 */       if (file.isFile()) {
/* 120 */         println(prefix, DigestUtils.digest(messageDigest, file), source);
/* 121 */       } else if (file.isDirectory()) {
/* 122 */         File[] listFiles = file.listFiles();
/* 123 */         if (listFiles != null) {
/* 124 */           run(prefix, messageDigest, listFiles);
/*     */         }
/*     */       } else {
/*     */         
/* 128 */         byte[] bytes = source.getBytes(Charset.defaultCharset());
/* 129 */         println(prefix, DigestUtils.digest(messageDigest, bytes));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void run(String prefix, MessageDigest messageDigest, File[] files) throws IOException {
/* 135 */     for (File file : files) {
/* 136 */       if (file.isFile()) {
/* 137 */         println(prefix, DigestUtils.digest(messageDigest, file), file.getName());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void run(String prefix, String messageDigestAlgorithm) throws IOException {
/* 143 */     run(prefix, DigestUtils.getDigest(messageDigestAlgorithm));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 148 */     return String.format("%s %s", new Object[] { super.toString(), Arrays.toString((Object[])this.args) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\cli\Digest.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */