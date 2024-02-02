/*     */ package cn.hutool.crypto;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.security.Key;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import org.bouncycastle.util.io.pem.PemObject;
/*     */ import org.bouncycastle.util.io.pem.PemObjectGenerator;
/*     */ import org.bouncycastle.util.io.pem.PemReader;
/*     */ import org.bouncycastle.util.io.pem.PemWriter;
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
/*     */ public class PemUtil
/*     */ {
/*     */   public static PrivateKey readPemPrivateKey(InputStream pemStream) {
/*  42 */     return (PrivateKey)readPemKey(pemStream);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PublicKey readPemPublicKey(InputStream pemStream) {
/*  53 */     return (PublicKey)readPemKey(pemStream);
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
/*     */   public static Key readPemKey(InputStream keyStream) {
/*  65 */     PemObject object = readPemObject(keyStream);
/*  66 */     String type = object.getType();
/*  67 */     if (StrUtil.isNotBlank(type)) {
/*     */       
/*  69 */       if (type.endsWith("EC PRIVATE KEY")) {
/*  70 */         return KeyUtil.generatePrivateKey("EC", object.getContent());
/*     */       }
/*  72 */       if (type.endsWith("PRIVATE KEY")) {
/*  73 */         return KeyUtil.generateRSAPrivateKey(object.getContent());
/*     */       }
/*     */ 
/*     */       
/*  77 */       if (type.endsWith("EC PUBLIC KEY"))
/*  78 */         return KeyUtil.generatePublicKey("EC", object.getContent()); 
/*  79 */       if (type.endsWith("PUBLIC KEY"))
/*  80 */         return KeyUtil.generateRSAPublicKey(object.getContent()); 
/*  81 */       if (type.endsWith("CERTIFICATE")) {
/*  82 */         return KeyUtil.readPublicKeyFromCert(IoUtil.toStream(object.getContent()));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] readPem(InputStream keyStream) {
/*  98 */     PemObject pemObject = readPemObject(keyStream);
/*  99 */     if (null != pemObject) {
/* 100 */       return pemObject.getContent();
/*     */     }
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PemObject readPemObject(InputStream keyStream) {
/* 113 */     return readPemObject(IoUtil.getUtf8Reader(keyStream));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PemObject readPemObject(Reader reader) {
/* 124 */     PemReader pemReader = null;
/*     */     try {
/* 126 */       pemReader = new PemReader(reader);
/* 127 */       return pemReader.readPemObject();
/* 128 */     } catch (IOException e) {
/* 129 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 131 */       IoUtil.close((Closeable)pemReader);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PrivateKey readSm2PemPrivateKey(InputStream keyStream) {
/*     */     try {
/* 143 */       return KeyUtil.generatePrivateKey("sm2", ECKeyUtil.createOpenSSHPrivateKeySpec(readPem(keyStream)));
/*     */     } finally {
/* 145 */       IoUtil.close(keyStream);
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
/*     */   public static String toPem(String type, byte[] content) {
/* 157 */     StringWriter stringWriter = new StringWriter();
/* 158 */     writePemObject(type, content, stringWriter);
/* 159 */     return stringWriter.toString();
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
/*     */   public static void writePemObject(String type, byte[] content, OutputStream keyStream) {
/* 171 */     writePemObject((PemObjectGenerator)new PemObject(type, content), keyStream);
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
/*     */   public static void writePemObject(String type, byte[] content, Writer writer) {
/* 183 */     writePemObject((PemObjectGenerator)new PemObject(type, content), writer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writePemObject(PemObjectGenerator pemObject, OutputStream keyStream) {
/* 194 */     writePemObject(pemObject, IoUtil.getUtf8Writer(keyStream));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writePemObject(PemObjectGenerator pemObject, Writer writer) {
/* 205 */     PemWriter pemWriter = new PemWriter(writer);
/*     */     try {
/* 207 */       pemWriter.writeObject(pemObject);
/* 208 */     } catch (IOException e) {
/* 209 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 211 */       IoUtil.close((Closeable)pemWriter);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\PemUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */