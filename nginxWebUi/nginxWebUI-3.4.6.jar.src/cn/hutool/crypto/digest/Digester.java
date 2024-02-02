/*     */ package cn.hutool.crypto.digest;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Provider;
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
/*     */ public class Digester
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private MessageDigest digest;
/*     */   protected byte[] salt;
/*     */   protected int saltPosition;
/*     */   protected int digestCount;
/*     */   
/*     */   public Digester(DigestAlgorithm algorithm) {
/*  47 */     this(algorithm.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Digester(String algorithm) {
/*  56 */     this(algorithm, (Provider)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Digester(DigestAlgorithm algorithm, Provider provider) {
/*  67 */     init(algorithm.getValue(), provider);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Digester(String algorithm, Provider provider) {
/*  78 */     init(algorithm, provider);
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
/*     */   public Digester init(String algorithm, Provider provider) {
/*  91 */     if (null == provider) {
/*  92 */       this.digest = SecureUtil.createMessageDigest(algorithm);
/*     */     } else {
/*     */       try {
/*  95 */         this.digest = MessageDigest.getInstance(algorithm, provider);
/*  96 */       } catch (NoSuchAlgorithmException e) {
/*  97 */         throw new CryptoException(e);
/*     */       } 
/*     */     } 
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Digester setSalt(byte[] salt) {
/* 111 */     this.salt = salt;
/* 112 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Digester setSaltPosition(int saltPosition) {
/* 135 */     this.saltPosition = saltPosition;
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Digester setDigestCount(int digestCount) {
/* 146 */     this.digestCount = digestCount;
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Digester reset() {
/* 157 */     this.digest.reset();
/* 158 */     return this;
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
/*     */   public byte[] digest(String data, String charsetName) {
/* 170 */     return digest(data, CharsetUtil.charset(charsetName));
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
/*     */   public byte[] digest(String data, Charset charset) {
/* 182 */     return digest(StrUtil.bytes(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(String data) {
/* 192 */     return digest(data, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(String data, String charsetName) {
/* 203 */     return digestHex(data, CharsetUtil.charset(charsetName));
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
/*     */   public String digestHex(String data, Charset charset) {
/* 215 */     return HexUtil.encodeHexStr(digest(data, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(String data) {
/* 225 */     return digestHex(data, "UTF-8");
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
/*     */   public byte[] digest(File file) throws CryptoException {
/* 237 */     InputStream in = null;
/*     */     try {
/* 239 */       in = FileUtil.getInputStream(file);
/* 240 */       return digest(in);
/*     */     } finally {
/* 242 */       IoUtil.close(in);
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
/*     */   public String digestHex(File file) {
/* 254 */     return HexUtil.encodeHexStr(digest(file));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(byte[] data) {
/*     */     byte[] result;
/* 265 */     if (this.saltPosition <= 0) {
/*     */       
/* 267 */       result = doDigest(new byte[][] { this.salt, data });
/* 268 */     } else if (this.saltPosition >= data.length) {
/*     */       
/* 270 */       result = doDigest(new byte[][] { data, this.salt });
/* 271 */     } else if (ArrayUtil.isNotEmpty(this.salt)) {
/*     */       
/* 273 */       this.digest.update(data, 0, this.saltPosition);
/* 274 */       this.digest.update(this.salt);
/* 275 */       this.digest.update(data, this.saltPosition, data.length - this.saltPosition);
/* 276 */       result = this.digest.digest();
/*     */     } else {
/*     */       
/* 279 */       result = doDigest(new byte[][] { data });
/*     */     } 
/*     */     
/* 282 */     return resetAndRepeatDigest(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(byte[] data) {
/* 292 */     return HexUtil.encodeHexStr(digest(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(InputStream data) {
/* 302 */     return digest(data, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String digestHex(InputStream data) {
/* 313 */     return HexUtil.encodeHexStr(digest(data));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] digest(InputStream data, int bufferLength) throws IORuntimeException {
/*     */     byte[] result;
/* 325 */     if (bufferLength < 1) {
/* 326 */       bufferLength = 8192;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 331 */       if (ArrayUtil.isEmpty(this.salt)) {
/* 332 */         result = digestWithoutSalt(data, bufferLength);
/*     */       } else {
/* 334 */         result = digestWithSalt(data, bufferLength);
/*     */       } 
/* 336 */     } catch (IOException e) {
/* 337 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/* 340 */     return resetAndRepeatDigest(result);
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
/*     */   public String digestHex(InputStream data, int bufferLength) {
/* 352 */     return HexUtil.encodeHexStr(digest(data, bufferLength));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageDigest getDigest() {
/* 361 */     return this.digest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDigestLength() {
/* 371 */     return this.digest.getDigestLength();
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
/*     */   private byte[] digestWithoutSalt(InputStream data, int bufferLength) throws IOException {
/* 384 */     byte[] buffer = new byte[bufferLength];
/*     */     int read;
/* 386 */     while ((read = data.read(buffer, 0, bufferLength)) > -1) {
/* 387 */       this.digest.update(buffer, 0, read);
/*     */     }
/* 389 */     return this.digest.digest();
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
/*     */   private byte[] digestWithSalt(InputStream data, int bufferLength) throws IOException {
/* 401 */     if (this.saltPosition <= 0)
/*     */     {
/* 403 */       this.digest.update(this.salt);
/*     */     }
/*     */     
/* 406 */     byte[] buffer = new byte[bufferLength];
/* 407 */     int total = 0;
/*     */     int read;
/* 409 */     while ((read = data.read(buffer, 0, bufferLength)) > -1) {
/* 410 */       total += read;
/* 411 */       if (this.saltPosition > 0 && total >= this.saltPosition) {
/* 412 */         if (total != this.saltPosition) {
/* 413 */           this.digest.update(buffer, 0, total - this.saltPosition);
/*     */         }
/*     */         
/* 416 */         this.digest.update(this.salt);
/* 417 */         this.digest.update(buffer, total - this.saltPosition, read); continue;
/*     */       } 
/* 419 */       this.digest.update(buffer, 0, read);
/*     */     } 
/*     */ 
/*     */     
/* 423 */     if (total < this.saltPosition)
/*     */     {
/* 425 */       this.digest.update(this.salt);
/*     */     }
/*     */     
/* 428 */     return this.digest.digest();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] doDigest(byte[]... datas) {
/* 439 */     for (byte[] data : datas) {
/* 440 */       if (null != data) {
/* 441 */         this.digest.update(data);
/*     */       }
/*     */     } 
/* 444 */     return this.digest.digest();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] resetAndRepeatDigest(byte[] digestData) {
/* 455 */     int digestCount = Math.max(1, this.digestCount);
/* 456 */     reset();
/* 457 */     for (int i = 0; i < digestCount - 1; i++) {
/* 458 */       digestData = doDigest(new byte[][] { digestData });
/* 459 */       reset();
/*     */     } 
/* 461 */     return digestData;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\Digester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */