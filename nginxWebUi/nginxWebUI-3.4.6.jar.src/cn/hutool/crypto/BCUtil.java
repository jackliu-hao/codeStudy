/*     */ package cn.hutool.crypto;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.Key;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.spec.ECParameterSpec;
/*     */ import java.security.spec.ECPoint;
/*     */ import java.security.spec.ECPublicKeySpec;
/*     */ import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
/*     */ import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.bouncycastle.asn1.x9.X9ECParameters;
/*     */ import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
/*     */ import org.bouncycastle.crypto.params.ECDomainParameters;
/*     */ import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*     */ import org.bouncycastle.crypto.params.ECPublicKeyParameters;
/*     */ import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
/*     */ import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
/*     */ import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
/*     */ import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
/*     */ import org.bouncycastle.jce.spec.ECNamedCurveSpec;
/*     */ import org.bouncycastle.jce.spec.ECParameterSpec;
/*     */ import org.bouncycastle.math.ec.ECCurve;
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
/*     */ public class BCUtil
/*     */ {
/*     */   public static byte[] encodeECPrivateKey(PrivateKey privateKey) {
/*  44 */     return ((BCECPrivateKey)privateKey).getD().toByteArray();
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
/*     */   public static byte[] encodeECPublicKey(PublicKey publicKey) {
/*  56 */     return encodeECPublicKey(publicKey, true);
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
/*     */   public static byte[] encodeECPublicKey(PublicKey publicKey, boolean isCompressed) {
/*  69 */     return ((BCECPublicKey)publicKey).getQ().getEncoded(isCompressed);
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
/*     */   public static PublicKey decodeECPoint(String encode, String curveName) {
/*  82 */     return decodeECPoint(SecureUtil.decode(encode), curveName);
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
/*     */   public static PublicKey decodeECPoint(byte[] encodeByte, String curveName) {
/*  94 */     X9ECParameters x9ECParameters = ECUtil.getNamedCurveByName(curveName);
/*  95 */     ECCurve curve = x9ECParameters.getCurve();
/*  96 */     ECPoint point = EC5Util.convertPoint(curve.decodePoint(encodeByte));
/*     */ 
/*     */     
/*  99 */     ECNamedCurveSpec ecSpec = new ECNamedCurveSpec(curveName, curve, x9ECParameters.getG(), x9ECParameters.getN());
/* 100 */     return KeyUtil.generatePublicKey("EC", new ECPublicKeySpec(point, (ECParameterSpec)ecSpec));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECDomainParameters toDomainParams(ECParameterSpec parameterSpec) {
/* 111 */     return new ECDomainParameters(parameterSpec
/* 112 */         .getCurve(), parameterSpec
/* 113 */         .getG(), parameterSpec
/* 114 */         .getN(), parameterSpec
/* 115 */         .getH());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECDomainParameters toDomainParams(String curveName) {
/* 126 */     return toDomainParams(ECUtil.getNamedCurveByName(curveName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECDomainParameters toDomainParams(X9ECParameters x9ECParameters) {
/* 137 */     return new ECDomainParameters(x9ECParameters
/* 138 */         .getCurve(), x9ECParameters
/* 139 */         .getG(), x9ECParameters
/* 140 */         .getN(), x9ECParameters
/* 141 */         .getH());
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
/*     */   public static AsymmetricKeyParameter toParams(Key key) {
/* 153 */     return ECKeyUtil.toParams(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toSm2Params(String d) {
/* 163 */     return ECKeyUtil.toSm2PrivateParams(d);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toParams(String dHex, ECDomainParameters domainParameters) {
/* 174 */     return ECKeyUtil.toPrivateParams(dHex, domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toSm2Params(byte[] d) {
/* 184 */     return ECKeyUtil.toSm2PrivateParams(d);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toParams(byte[] d, ECDomainParameters domainParameters) {
/* 195 */     return ECKeyUtil.toPrivateParams(d, domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toSm2Params(BigInteger d) {
/* 205 */     return ECKeyUtil.toSm2PrivateParams(d);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toParams(BigInteger d, ECDomainParameters domainParameters) {
/* 216 */     return ECKeyUtil.toPrivateParams(d, domainParameters);
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
/*     */   public static ECPublicKeyParameters toParams(BigInteger x, BigInteger y, ECDomainParameters domainParameters) {
/* 228 */     return ECKeyUtil.toPublicParams(x, y, domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters toSm2Params(String xHex, String yHex) {
/* 239 */     return ECKeyUtil.toSm2PublicParams(xHex, yHex);
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
/*     */   public static ECPublicKeyParameters toParams(String xHex, String yHex, ECDomainParameters domainParameters) {
/* 251 */     return ECKeyUtil.toPublicParams(xHex, yHex, domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters toSm2Params(byte[] xBytes, byte[] yBytes) {
/* 262 */     return ECKeyUtil.toSm2PublicParams(xBytes, yBytes);
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
/*     */   public static ECPublicKeyParameters toParams(byte[] xBytes, byte[] yBytes, ECDomainParameters domainParameters) {
/* 274 */     return ECKeyUtil.toPublicParams(xBytes, yBytes, domainParameters);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPublicKeyParameters toParams(PublicKey publicKey) {
/* 284 */     return ECKeyUtil.toPublicParams(publicKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ECPrivateKeyParameters toParams(PrivateKey privateKey) {
/* 294 */     return ECKeyUtil.toPrivateParams(privateKey);
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
/*     */   public static PrivateKey readPemPrivateKey(InputStream pemStream) {
/* 306 */     return PemUtil.readPemPrivateKey(pemStream);
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
/*     */   public static PublicKey readPemPublicKey(InputStream pemStream) {
/* 318 */     return PemUtil.readPemPublicKey(pemStream);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] toPkcs1(PrivateKey privateKey) {
/* 329 */     PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privateKey.getEncoded());
/*     */     try {
/* 331 */       return pkInfo.parsePrivateKey().toASN1Primitive().getEncoded();
/* 332 */     } catch (IOException e) {
/* 333 */       throw new IORuntimeException(e);
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
/*     */   public static byte[] toPkcs1(PublicKey publicKey) {
/* 346 */     SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
/*     */     try {
/* 348 */       return spkInfo.parsePublicKey().getEncoded();
/* 349 */     } catch (IOException e) {
/* 350 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\BCUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */