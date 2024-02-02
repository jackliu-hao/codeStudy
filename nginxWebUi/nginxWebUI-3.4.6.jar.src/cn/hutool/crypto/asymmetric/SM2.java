/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.crypto.BCUtil;
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.ECKeyUtil;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import java.math.BigInteger;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import org.bouncycastle.crypto.CipherParameters;
/*     */ import org.bouncycastle.crypto.CryptoException;
/*     */ import org.bouncycastle.crypto.Digest;
/*     */ import org.bouncycastle.crypto.InvalidCipherTextException;
/*     */ import org.bouncycastle.crypto.digests.SM3Digest;
/*     */ import org.bouncycastle.crypto.engines.SM2Engine;
/*     */ import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*     */ import org.bouncycastle.crypto.params.ECPublicKeyParameters;
/*     */ import org.bouncycastle.crypto.params.ParametersWithID;
/*     */ import org.bouncycastle.crypto.params.ParametersWithRandom;
/*     */ import org.bouncycastle.crypto.signers.DSAEncoding;
/*     */ import org.bouncycastle.crypto.signers.PlainDSAEncoding;
/*     */ import org.bouncycastle.crypto.signers.SM2Signer;
/*     */ import org.bouncycastle.crypto.signers.StandardDSAEncoding;
/*     */ import org.bouncycastle.util.BigIntegers;
/*     */ import org.bouncycastle.util.encoders.Hex;
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
/*     */ public class SM2
/*     */   extends AbstractAsymmetricCrypto<SM2>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String ALGORITHM_SM2 = "SM2";
/*     */   protected SM2Engine engine;
/*     */   protected SM2Signer signer;
/*     */   private ECPrivateKeyParameters privateKeyParams;
/*     */   private ECPublicKeyParameters publicKeyParams;
/*  59 */   private DSAEncoding encoding = (DSAEncoding)StandardDSAEncoding.INSTANCE;
/*  60 */   private Digest digest = (Digest)new SM3Digest();
/*  61 */   private SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2() {
/*  69 */     this((byte[])null, (byte[])null);
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
/*     */   public SM2(String privateKeyStr, String publicKeyStr) {
/*  81 */     this(SecureUtil.decode(privateKeyStr), SecureUtil.decode(publicKeyStr));
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
/*     */   public SM2(byte[] privateKey, byte[] publicKey) {
/*  93 */     this(
/*  94 */         ECKeyUtil.decodePrivateKeyParams(privateKey), 
/*  95 */         ECKeyUtil.decodePublicKeyParams(publicKey));
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
/*     */   public SM2(PrivateKey privateKey, PublicKey publicKey) {
/* 108 */     this(BCUtil.toParams(privateKey), BCUtil.toParams(publicKey));
/* 109 */     if (null != privateKey) {
/* 110 */       this.privateKey = privateKey;
/*     */     }
/* 112 */     if (null != publicKey) {
/* 113 */       this.publicKey = publicKey;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2(String privateKeyHex, String publicKeyPointXHex, String publicKeyPointYHex) {
/* 128 */     this(BCUtil.toSm2Params(privateKeyHex), BCUtil.toSm2Params(publicKeyPointXHex, publicKeyPointYHex));
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
/*     */   public SM2(byte[] privateKey, byte[] publicKeyPointX, byte[] publicKeyPointY) {
/* 142 */     this(BCUtil.toSm2Params(privateKey), BCUtil.toSm2Params(publicKeyPointX, publicKeyPointY));
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
/*     */   public SM2(ECPrivateKeyParameters privateKeyParams, ECPublicKeyParameters publicKeyParams) {
/* 154 */     super("SM2", (PrivateKey)null, (PublicKey)null);
/* 155 */     this.privateKeyParams = privateKeyParams;
/* 156 */     this.publicKeyParams = publicKeyParams;
/* 157 */     init();
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
/*     */   public SM2 init() {
/* 170 */     if (null == this.privateKeyParams && null == this.publicKeyParams) {
/* 171 */       super.initKeys();
/* 172 */       this.privateKeyParams = BCUtil.toParams(this.privateKey);
/* 173 */       this.publicKeyParams = BCUtil.toParams(this.publicKey);
/*     */     } 
/* 175 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2 initKeys() {
/* 182 */     return this;
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
/*     */   public byte[] encrypt(byte[] data) throws CryptoException {
/* 202 */     return encrypt(data, KeyType.PublicKey);
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
/*     */   public byte[] encrypt(byte[] data, KeyType keyType) throws CryptoException {
/* 221 */     if (KeyType.PublicKey != keyType) {
/* 222 */       throw new IllegalArgumentException("Encrypt is only support by public key");
/*     */     }
/* 224 */     return encrypt(data, (CipherParameters)new ParametersWithRandom(getCipherParameters(keyType)));
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
/*     */   public byte[] encrypt(byte[] data, CipherParameters pubKeyParameters) throws CryptoException {
/* 243 */     this.lock.lock();
/* 244 */     SM2Engine engine = getEngine();
/*     */     try {
/* 246 */       engine.init(true, pubKeyParameters);
/* 247 */       return engine.processBlock(data, 0, data.length);
/* 248 */     } catch (InvalidCipherTextException e) {
/* 249 */       throw new CryptoException(e);
/*     */     } finally {
/* 251 */       this.lock.unlock();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decrypt(byte[] data) throws CryptoException {
/* 266 */     return decrypt(data, KeyType.PrivateKey);
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
/*     */   public byte[] decrypt(byte[] data, KeyType keyType) throws CryptoException {
/* 279 */     if (KeyType.PrivateKey != keyType) {
/* 280 */       throw new IllegalArgumentException("Decrypt is only support by private key");
/*     */     }
/* 282 */     return decrypt(data, getCipherParameters(keyType));
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
/*     */   public byte[] decrypt(byte[] data, CipherParameters privateKeyParameters) throws CryptoException {
/* 295 */     this.lock.lock();
/* 296 */     SM2Engine engine = getEngine();
/*     */     try {
/* 298 */       engine.init(false, privateKeyParameters);
/* 299 */       return engine.processBlock(data, 0, data.length);
/* 300 */     } catch (InvalidCipherTextException e) {
/* 301 */       throw new CryptoException(e);
/*     */     } finally {
/* 303 */       this.lock.unlock();
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
/*     */   public String signHex(String dataHex) {
/* 315 */     return signHex(dataHex, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] sign(byte[] data) {
/* 326 */     return sign(data, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String signHex(String dataHex, String idHex) {
/* 337 */     return HexUtil.encodeHexStr(sign(HexUtil.decodeHex(dataHex), HexUtil.decodeHex(idHex)));
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
/*     */   public byte[] sign(byte[] data, byte[] id) {
/* 349 */     this.lock.lock();
/* 350 */     SM2Signer signer = getSigner(); try {
/*     */       ParametersWithID parametersWithID;
/* 352 */       ParametersWithRandom parametersWithRandom = new ParametersWithRandom(getCipherParameters(KeyType.PrivateKey));
/* 353 */       if (id != null) {
/* 354 */         parametersWithID = new ParametersWithID((CipherParameters)parametersWithRandom, id);
/*     */       }
/* 356 */       signer.init(true, (CipherParameters)parametersWithID);
/* 357 */       signer.update(data, 0, data.length);
/* 358 */       return signer.generateSignature();
/* 359 */     } catch (CryptoException e) {
/* 360 */       throw new CryptoException(e);
/*     */     } finally {
/* 362 */       this.lock.unlock();
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
/*     */   
/*     */   public boolean verifyHex(String dataHex, String signHex) {
/* 375 */     return verifyHex(dataHex, signHex, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean verify(byte[] data, byte[] sign) {
/* 386 */     return verify(data, sign, (byte[])null);
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
/*     */   public boolean verifyHex(String dataHex, String signHex, String idHex) {
/* 399 */     return verify(HexUtil.decodeHex(dataHex), HexUtil.decodeHex(signHex), HexUtil.decodeHex(idHex));
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
/*     */   public boolean verify(byte[] data, byte[] sign, byte[] id) {
/* 411 */     this.lock.lock();
/* 412 */     SM2Signer signer = getSigner(); try {
/*     */       ParametersWithID parametersWithID;
/* 414 */       CipherParameters param = getCipherParameters(KeyType.PublicKey);
/* 415 */       if (id != null) {
/* 416 */         parametersWithID = new ParametersWithID(param, id);
/*     */       }
/* 418 */       signer.init(false, (CipherParameters)parametersWithID);
/* 419 */       signer.update(data, 0, data.length);
/* 420 */       return signer.verifySignature(sign);
/*     */     } finally {
/* 422 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public SM2 setPrivateKey(PrivateKey privateKey) {
/* 428 */     super.setPrivateKey(privateKey);
/*     */ 
/*     */     
/* 431 */     this.privateKeyParams = BCUtil.toParams(privateKey);
/*     */     
/* 433 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2 setPrivateKeyParams(ECPrivateKeyParameters privateKeyParams) {
/* 444 */     this.privateKeyParams = privateKeyParams;
/* 445 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SM2 setPublicKey(PublicKey publicKey) {
/* 450 */     super.setPublicKey(publicKey);
/*     */ 
/*     */     
/* 453 */     this.publicKeyParams = BCUtil.toParams(publicKey);
/*     */     
/* 455 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2 setPublicKeyParams(ECPublicKeyParameters publicKeyParams) {
/* 465 */     this.publicKeyParams = publicKeyParams;
/* 466 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2 usePlainEncoding() {
/* 476 */     return setEncoding((DSAEncoding)PlainDSAEncoding.INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2 setEncoding(DSAEncoding encoding) {
/* 487 */     this.encoding = encoding;
/* 488 */     this.signer = null;
/* 489 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2 setDigest(Digest digest) {
/* 500 */     this.digest = digest;
/* 501 */     this.engine = null;
/* 502 */     this.signer = null;
/* 503 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SM2 setMode(SM2Engine.Mode mode) {
/* 513 */     this.mode = mode;
/* 514 */     this.engine = null;
/* 515 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getD() {
/* 525 */     return BigIntegers.asUnsignedByteArray(32, getDBigInteger());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDHex() {
/* 535 */     return new String(Hex.encode(getD()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getDBigInteger() {
/* 545 */     return this.privateKeyParams.getD();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getQ(boolean isCompressed) {
/* 556 */     return this.publicKeyParams.getQ().getEncoded(isCompressed);
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
/*     */   private CipherParameters getCipherParameters(KeyType keyType) {
/* 568 */     switch (keyType) {
/*     */       case PublicKey:
/* 570 */         Assert.notNull(this.publicKeyParams, "PublicKey must be not null !", new Object[0]);
/* 571 */         return (CipherParameters)this.publicKeyParams;
/*     */       case PrivateKey:
/* 573 */         Assert.notNull(this.privateKeyParams, "PrivateKey must be not null !", new Object[0]);
/* 574 */         return (CipherParameters)this.privateKeyParams;
/*     */     } 
/*     */     
/* 577 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SM2Engine getEngine() {
/* 586 */     if (null == this.engine) {
/* 587 */       Assert.notNull(this.digest, "digest must be not null !", new Object[0]);
/* 588 */       this.engine = new SM2Engine(this.digest, this.mode);
/*     */     } 
/* 590 */     this.digest.reset();
/* 591 */     return this.engine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SM2Signer getSigner() {
/* 600 */     if (null == this.signer) {
/* 601 */       Assert.notNull(this.digest, "digest must be not null !", new Object[0]);
/* 602 */       this.signer = new SM2Signer(this.encoding, this.digest);
/*     */     } 
/* 604 */     this.digest.reset();
/* 605 */     return this.signer;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\SM2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */