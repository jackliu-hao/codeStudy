/*     */ package org.h2.store.fs.encrypt;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.FilePathWrapper;
/*     */ import org.h2.store.fs.FileUtils;
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
/*     */ public class FilePathEncrypt
/*     */   extends FilePathWrapper
/*     */ {
/*     */   private static final String SCHEME = "encrypt";
/*     */   
/*     */   public static void register() {
/*  29 */     FilePath.register((FilePath)new FilePathEncrypt());
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel open(String paramString) throws IOException {
/*  34 */     String[] arrayOfString = parse(this.name);
/*  35 */     FileChannel fileChannel = FileUtils.open(arrayOfString[1], paramString);
/*  36 */     byte[] arrayOfByte = arrayOfString[0].getBytes(StandardCharsets.UTF_8);
/*  37 */     return (FileChannel)new FileEncrypt(this.name, arrayOfByte, fileChannel);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getScheme() {
/*  42 */     return "encrypt";
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getPrefix() {
/*  47 */     String[] arrayOfString = parse(this.name);
/*  48 */     return getScheme() + ":" + arrayOfString[0] + ":";
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath unwrap(String paramString) {
/*  53 */     return FilePath.get(parse(paramString)[1]);
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  58 */     long l = getBase().size() - 4096L;
/*  59 */     l = Math.max(0L, l);
/*  60 */     if ((l & 0xFFFL) != 0L) {
/*  61 */       l -= 4096L;
/*     */     }
/*  63 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputStream newOutputStream(boolean paramBoolean) throws IOException {
/*  68 */     return newFileChannelOutputStream(open("rw"), paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream newInputStream() throws IOException {
/*  73 */     return Channels.newInputStream(open("r"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String[] parse(String paramString) {
/*  83 */     if (!paramString.startsWith(getScheme())) {
/*  84 */       throw new IllegalArgumentException(paramString + " doesn't start with " + 
/*  85 */           getScheme());
/*     */     }
/*  87 */     paramString = paramString.substring(getScheme().length() + 1);
/*  88 */     int i = paramString.indexOf(':');
/*     */     
/*  90 */     if (i < 0) {
/*  91 */       throw new IllegalArgumentException(paramString + " doesn't contain encryption algorithm and password");
/*     */     }
/*     */     
/*  94 */     String str = paramString.substring(0, i);
/*  95 */     paramString = paramString.substring(i + 1);
/*  96 */     return new String[] { str, paramString };
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
/*     */   public static byte[] getPasswordBytes(char[] paramArrayOfchar) {
/* 108 */     int i = paramArrayOfchar.length;
/* 109 */     byte[] arrayOfByte = new byte[i * 2];
/* 110 */     for (byte b = 0; b < i; b++) {
/* 111 */       char c = paramArrayOfchar[b];
/* 112 */       arrayOfByte[b + b] = (byte)(c >>> 8);
/* 113 */       arrayOfByte[b + b + 1] = (byte)c;
/*     */     } 
/* 115 */     return arrayOfByte;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\encrypt\FilePathEncrypt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */