/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.KeyUtil;
/*     */ import java.io.Serializable;
/*     */ import java.security.Key;
/*     */ import java.security.KeyPair;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.util.concurrent.locks.Lock;
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
/*     */ public class BaseAsymmetric<T extends BaseAsymmetric<T>>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected String algorithm;
/*     */   protected PublicKey publicKey;
/*     */   protected PrivateKey privateKey;
/*  40 */   protected final Lock lock = new ReentrantLock();
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
/*     */   public BaseAsymmetric(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
/*  56 */     init(algorithm, privateKey, publicKey);
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
/*     */   protected T init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
/*  72 */     this.algorithm = algorithm;
/*     */     
/*  74 */     if (null == privateKey && null == publicKey) {
/*  75 */       initKeys();
/*     */     } else {
/*  77 */       if (null != privateKey) {
/*  78 */         this.privateKey = privateKey;
/*     */       }
/*  80 */       if (null != publicKey) {
/*  81 */         this.publicKey = publicKey;
/*     */       }
/*     */     } 
/*  84 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T initKeys() {
/*  94 */     KeyPair keyPair = KeyUtil.generateKeyPair(this.algorithm);
/*  95 */     this.publicKey = keyPair.getPublic();
/*  96 */     this.privateKey = keyPair.getPrivate();
/*  97 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PublicKey getPublicKey() {
/* 108 */     return this.publicKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPublicKeyBase64() {
/* 117 */     PublicKey publicKey = getPublicKey();
/* 118 */     return (null == publicKey) ? null : Base64.encode(publicKey.getEncoded());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setPublicKey(PublicKey publicKey) {
/* 129 */     this.publicKey = publicKey;
/* 130 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrivateKey getPrivateKey() {
/* 139 */     return this.privateKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPrivateKeyBase64() {
/* 148 */     PrivateKey privateKey = getPrivateKey();
/* 149 */     return (null == privateKey) ? null : Base64.encode(privateKey.getEncoded());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setPrivateKey(PrivateKey privateKey) {
/* 160 */     this.privateKey = privateKey;
/* 161 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setKey(Key key) {
/* 172 */     Assert.notNull(key, "key must be not null !", new Object[0]);
/*     */     
/* 174 */     if (key instanceof PublicKey)
/* 175 */       return setPublicKey((PublicKey)key); 
/* 176 */     if (key instanceof PrivateKey) {
/* 177 */       return setPrivateKey((PrivateKey)key);
/*     */     }
/* 179 */     throw new CryptoException("Unsupported key type: {}", new Object[] { key.getClass() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Key getKeyByType(KeyType type) {
/* 189 */     switch (type) {
/*     */       case PrivateKey:
/* 191 */         if (null == this.privateKey) {
/* 192 */           throw new NullPointerException("Private key must not null when use it !");
/*     */         }
/* 194 */         return this.privateKey;
/*     */       case PublicKey:
/* 196 */         if (null == this.publicKey) {
/* 197 */           throw new NullPointerException("Public key must not null when use it !");
/*     */         }
/* 199 */         return this.publicKey;
/*     */     } 
/* 201 */     throw new CryptoException("Unsupported key type: " + type);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\BaseAsymmetric.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */