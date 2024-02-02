/*     */ package cn.hutool.crypto;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.Key;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.spec.KeySpec;
/*     */ import org.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
/*     */ import org.bouncycastle.asn1.sec.ECPrivateKey;
/*     */ import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
/*     */ import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*     */ import org.bouncycastle.crypto.params.ECDomainParameters;
/*     */ import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*     */ import org.bouncycastle.crypto.params.ECPublicKeyParameters;
/*     */ import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
/*     */ import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
/*     */ import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;
/*     */ import org.bouncycastle.math.ec.ECCurve;
/*     */ import org.bouncycastle.math.ec.ECPoint;
/*     */ import org.bouncycastle.math.ec.FixedPointCombMultiplier;
/*     */ import org.bouncycastle.util.BigIntegers;
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
/*     */ public class ECKeyUtil
/*     */ {
/*     */   public static AsymmetricKeyParameter toParams(Key key) {
/*  43 */     if (key instanceof PrivateKey)
/*  44 */       return (AsymmetricKeyParameter)toPrivateParams((PrivateKey)key); 
/*  45 */     if (key instanceof PublicKey) {
/*  46 */       return (AsymmetricKeyParameter)toPublicParams((PublicKey)key);
/*     */     }
/*     */     
/*  49 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters getPublicParams(ECPrivateKeyParameters privateKeyParameters) {
/*  60 */     ECDomainParameters domainParameters = privateKeyParameters.getParameters();
/*  61 */     ECPoint q = (new FixedPointCombMultiplier()).multiply(domainParameters.getG(), privateKeyParameters.getD());
/*  62 */     return new ECPublicKeyParameters(q, domainParameters);
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
/*     */   public static ECPublicKeyParameters toSm2PublicParams(byte[] q) {
/*  74 */     return toPublicParams(q, SmUtil.SM2_DOMAIN_PARAMS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters toSm2PublicParams(String q) {
/*  84 */     return toPublicParams(q, SmUtil.SM2_DOMAIN_PARAMS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters toSm2PublicParams(String x, String y) {
/*  95 */     return toPublicParams(x, y, SmUtil.SM2_DOMAIN_PARAMS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters toSm2PublicParams(byte[] xBytes, byte[] yBytes) {
/* 106 */     return toPublicParams(xBytes, yBytes, SmUtil.SM2_DOMAIN_PARAMS);
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
/*     */   public static ECPublicKeyParameters toPublicParams(String x, String y, ECDomainParameters domainParameters) {
/* 118 */     return toPublicParams(SecureUtil.decode(x), SecureUtil.decode(y), domainParameters);
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
/*     */   public static ECPublicKeyParameters toPublicParams(byte[] xBytes, byte[] yBytes, ECDomainParameters domainParameters) {
/* 130 */     if (null == xBytes || null == yBytes) {
/* 131 */       return null;
/*     */     }
/* 133 */     return toPublicParams(BigIntegers.fromUnsignedByteArray(xBytes), BigIntegers.fromUnsignedByteArray(yBytes), domainParameters);
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
/*     */   public static ECPublicKeyParameters toPublicParams(BigInteger x, BigInteger y, ECDomainParameters domainParameters) {
/* 145 */     if (null == x || null == y) {
/* 146 */       return null;
/*     */     }
/* 148 */     ECCurve curve = domainParameters.getCurve();
/* 149 */     return toPublicParams(curve.createPoint(x, y), domainParameters);
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
/*     */   public static ECPublicKeyParameters toPublicParams(String pointEncoded, ECDomainParameters domainParameters) {
/* 161 */     ECCurve curve = domainParameters.getCurve();
/* 162 */     return toPublicParams(curve.decodePoint(SecureUtil.decode(pointEncoded)), domainParameters);
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
/*     */   public static ECPublicKeyParameters toPublicParams(byte[] pointEncoded, ECDomainParameters domainParameters) {
/* 174 */     ECCurve curve = domainParameters.getCurve();
/* 175 */     return toPublicParams(curve.decodePoint(pointEncoded), domainParameters);
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
/*     */   public static ECPublicKeyParameters toPublicParams(ECPoint point, ECDomainParameters domainParameters) {
/* 187 */     return new ECPublicKeyParameters(point, domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters toPublicParams(PublicKey publicKey) {
/* 197 */     if (null == publicKey) {
/* 198 */       return null;
/*     */     }
/*     */     try {
/* 201 */       return (ECPublicKeyParameters)ECUtil.generatePublicKeyParameter(publicKey);
/* 202 */     } catch (InvalidKeyException e) {
/* 203 */       throw new CryptoException(e);
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
/*     */   public static ECPrivateKeyParameters toSm2PrivateParams(String d) {
/* 216 */     return toPrivateParams(d, SmUtil.SM2_DOMAIN_PARAMS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toSm2PrivateParams(byte[] d) {
/* 226 */     return toPrivateParams(d, SmUtil.SM2_DOMAIN_PARAMS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toSm2PrivateParams(BigInteger d) {
/* 236 */     return toPrivateParams(d, SmUtil.SM2_DOMAIN_PARAMS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toPrivateParams(String d, ECDomainParameters domainParameters) {
/* 247 */     return toPrivateParams(BigIntegers.fromUnsignedByteArray(SecureUtil.decode(d)), domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toPrivateParams(byte[] d, ECDomainParameters domainParameters) {
/* 258 */     return toPrivateParams(BigIntegers.fromUnsignedByteArray(d), domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toPrivateParams(BigInteger d, ECDomainParameters domainParameters) {
/* 269 */     if (null == d) {
/* 270 */       return null;
/*     */     }
/* 272 */     return new ECPrivateKeyParameters(d, domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toPrivateParams(PrivateKey privateKey) {
/* 282 */     if (null == privateKey) {
/* 283 */       return null;
/*     */     }
/*     */     try {
/* 286 */       return (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(privateKey);
/* 287 */     } catch (InvalidKeyException e) {
/* 288 */       throw new CryptoException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PrivateKey toSm2PrivateKey(ECPrivateKey privateKey) {
/*     */     try {
/* 300 */       PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)SmUtil.ID_SM2_PUBLIC_KEY_PARAM), (ASN1Encodable)privateKey);
/*     */       
/* 302 */       return KeyUtil.generatePrivateKey("SM2", info.getEncoded());
/* 303 */     } catch (IOException e) {
/* 304 */       throw new IORuntimeException(e);
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
/*     */   public static KeySpec createOpenSSHPrivateKeySpec(byte[] key) {
/* 316 */     return (KeySpec)new OpenSSHPrivateKeySpec(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static KeySpec createOpenSSHPublicKeySpec(byte[] key) {
/* 327 */     return (KeySpec)new OpenSSHPublicKeySpec(key);
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
/*     */   public static ECPrivateKeyParameters decodePrivateKeyParams(byte[] privateKeyBytes) {
/*     */     try {
/* 346 */       return toSm2PrivateParams(privateKeyBytes);
/* 347 */     } catch (Exception exception) {
/*     */       PrivateKey privateKey;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 354 */         privateKey = KeyUtil.generatePrivateKey("sm2", privateKeyBytes);
/* 355 */       } catch (Exception ignore) {
/*     */         
/* 357 */         privateKey = KeyUtil.generatePrivateKey("sm2", createOpenSSHPrivateKeySpec(privateKeyBytes));
/*     */       } 
/*     */       
/* 360 */       return toPrivateParams(privateKey);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters decodePublicKeyParams(byte[] publicKeyBytes) {
/*     */     try {
/* 379 */       return toSm2PublicParams(publicKeyBytes);
/* 380 */     } catch (Exception exception) {
/*     */       PublicKey publicKey;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 387 */         publicKey = KeyUtil.generatePublicKey("sm2", publicKeyBytes);
/* 388 */       } catch (Exception ignore) {
/*     */         
/* 390 */         publicKey = KeyUtil.generatePublicKey("sm2", createOpenSSHPublicKeySpec(publicKeyBytes));
/*     */       } 
/*     */       
/* 393 */       return toPublicParams(publicKey);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\ECKeyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */