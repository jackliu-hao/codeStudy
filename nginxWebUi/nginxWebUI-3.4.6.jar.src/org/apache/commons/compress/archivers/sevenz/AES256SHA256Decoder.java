/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.CipherInputStream;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.apache.commons.compress.PasswordRequiredException;
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
/*     */ class AES256SHA256Decoder
/*     */   extends CoderBase
/*     */ {
/*     */   AES256SHA256Decoder() {
/*  32 */     super(new Class[0]);
/*     */   }
/*     */   
/*     */   InputStream decode(final String archiveName, final InputStream in, long uncompressedLength, final Coder coder, final byte[] passwordBytes, int maxMemoryLimitInKb) throws IOException {
/*  36 */     return new InputStream() {
/*     */         private boolean isInitialized;
/*     */         
/*     */         private CipherInputStream init() throws IOException {
/*     */           byte[] aesKeyBytes;
/*  41 */           if (this.isInitialized) {
/*  42 */             return this.cipherInputStream;
/*     */           }
/*  44 */           if (coder.properties == null) {
/*  45 */             throw new IOException("Missing AES256 properties in " + archiveName);
/*     */           }
/*  47 */           if (coder.properties.length < 2) {
/*  48 */             throw new IOException("AES256 properties too short in " + archiveName);
/*     */           }
/*  50 */           int byte0 = 0xFF & coder.properties[0];
/*  51 */           int numCyclesPower = byte0 & 0x3F;
/*  52 */           int byte1 = 0xFF & coder.properties[1];
/*  53 */           int ivSize = (byte0 >> 6 & 0x1) + (byte1 & 0xF);
/*  54 */           int saltSize = (byte0 >> 7 & 0x1) + (byte1 >> 4);
/*  55 */           if (2 + saltSize + ivSize > coder.properties.length) {
/*  56 */             throw new IOException("Salt size + IV size too long in " + archiveName);
/*     */           }
/*  58 */           byte[] salt = new byte[saltSize];
/*  59 */           System.arraycopy(coder.properties, 2, salt, 0, saltSize);
/*  60 */           byte[] iv = new byte[16];
/*  61 */           System.arraycopy(coder.properties, 2 + saltSize, iv, 0, ivSize);
/*     */           
/*  63 */           if (passwordBytes == null) {
/*  64 */             throw new PasswordRequiredException(archiveName);
/*     */           }
/*     */           
/*  67 */           if (numCyclesPower == 63) {
/*  68 */             aesKeyBytes = new byte[32];
/*  69 */             System.arraycopy(salt, 0, aesKeyBytes, 0, saltSize);
/*  70 */             System.arraycopy(passwordBytes, 0, aesKeyBytes, saltSize, 
/*  71 */                 Math.min(passwordBytes.length, aesKeyBytes.length - saltSize));
/*     */           } else {
/*     */             MessageDigest digest;
/*     */             try {
/*  75 */               digest = MessageDigest.getInstance("SHA-256");
/*  76 */             } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
/*  77 */               throw new IOException("SHA-256 is unsupported by your Java implementation", noSuchAlgorithmException);
/*     */             } 
/*     */             
/*  80 */             byte[] extra = new byte[8]; long j;
/*  81 */             for (j = 0L; j < 1L << numCyclesPower; j++) {
/*  82 */               digest.update(salt);
/*  83 */               digest.update(passwordBytes);
/*  84 */               digest.update(extra);
/*  85 */               for (int k = 0; k < extra.length; k++) {
/*  86 */                 extra[k] = (byte)(extra[k] + 1);
/*  87 */                 if (extra[k] != 0) {
/*     */                   break;
/*     */                 }
/*     */               } 
/*     */             } 
/*  92 */             aesKeyBytes = digest.digest();
/*     */           } 
/*     */           
/*  95 */           SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
/*     */           try {
/*  97 */             Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
/*  98 */             cipher.init(2, aesKey, new IvParameterSpec(iv));
/*  99 */             this.cipherInputStream = new CipherInputStream(in, cipher);
/* 100 */             this.isInitialized = true;
/* 101 */             return this.cipherInputStream;
/* 102 */           } catch (GeneralSecurityException generalSecurityException) {
/* 103 */             throw new IOException("Decryption error (do you have the JCE Unlimited Strength Jurisdiction Policy Files installed?)", generalSecurityException);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         private CipherInputStream cipherInputStream;
/*     */         
/*     */         public int read() throws IOException {
/* 111 */           return init().read();
/*     */         }
/*     */ 
/*     */         
/*     */         public int read(byte[] b, int off, int len) throws IOException {
/* 116 */           return init().read(b, off, len);
/*     */         }
/*     */ 
/*     */         
/*     */         public void close() throws IOException {
/* 121 */           if (this.cipherInputStream != null)
/* 122 */             this.cipherInputStream.close(); 
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\AES256SHA256Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */