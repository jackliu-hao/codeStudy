/*     */ package cn.hutool.crypto;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.crypto.asymmetric.SM2;
/*     */ import cn.hutool.crypto.digest.HMac;
/*     */ import cn.hutool.crypto.digest.HmacAlgorithm;
/*     */ import cn.hutool.crypto.digest.SM3;
/*     */ import cn.hutool.crypto.digest.mac.BCHMacEngine;
/*     */ import cn.hutool.crypto.digest.mac.MacEngine;
/*     */ import cn.hutool.crypto.symmetric.SM4;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import org.bouncycastle.asn1.ASN1ObjectIdentifier;
/*     */ import org.bouncycastle.asn1.gm.GMNamedCurves;
/*     */ import org.bouncycastle.crypto.Digest;
/*     */ import org.bouncycastle.crypto.digests.SM3Digest;
/*     */ import org.bouncycastle.crypto.params.ECDomainParameters;
/*     */ import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
/*     */ import org.bouncycastle.crypto.params.ECPublicKeyParameters;
/*     */ import org.bouncycastle.crypto.signers.StandardDSAEncoding;
/*     */ import org.bouncycastle.util.Arrays;
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
/*     */ public class SmUtil
/*     */ {
/*     */   private static final int RS_LEN = 32;
/*     */   public static final String SM2_CURVE_NAME = "sm2p256v1";
/*  54 */   public static final ECDomainParameters SM2_DOMAIN_PARAMS = BCUtil.toDomainParams(GMNamedCurves.getByName("sm2p256v1"));
/*     */ 
/*     */ 
/*     */   
/*  58 */   public static final ASN1ObjectIdentifier ID_SM2_PUBLIC_KEY_PARAM = new ASN1ObjectIdentifier("1.2.156.10197.1.301");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SM2 sm2() {
/*  67 */     return new SM2();
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
/*     */   public static SM2 sm2(String privateKeyStr, String publicKeyStr) {
/*  80 */     return new SM2(privateKeyStr, publicKeyStr);
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
/*     */   public static SM2 sm2(byte[] privateKey, byte[] publicKey) {
/*  93 */     return new SM2(privateKey, publicKey);
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
/*     */   public static SM2 sm2(PrivateKey privateKey, PublicKey publicKey) {
/* 107 */     return new SM2(privateKey, publicKey);
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
/*     */   public static SM2 sm2(ECPrivateKeyParameters privateKeyParams, ECPublicKeyParameters publicKeyParams) {
/* 121 */     return new SM2(privateKeyParams, publicKeyParams);
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
/*     */   public static SM3 sm3() {
/* 133 */     return new SM3();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SM3 sm3WithSalt(byte[] salt) {
/* 144 */     return new SM3(salt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sm3(String data) {
/* 154 */     return sm3().digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sm3(InputStream data) {
/* 164 */     return sm3().digestHex(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String sm3(File dataFile) {
/* 174 */     return sm3().digestHex(dataFile);
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
/*     */   public static SM4 sm4() {
/* 189 */     return new SM4();
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
/*     */   public static SM4 sm4(byte[] key) {
/* 205 */     return new SM4(key);
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
/*     */   public static byte[] changeC1C2C3ToC1C3C2(byte[] c1c2c3, ECDomainParameters ecDomainParameters) {
/* 217 */     int c1Len = (ecDomainParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
/* 218 */     int c3Len = 32;
/* 219 */     byte[] result = new byte[c1c2c3.length];
/* 220 */     System.arraycopy(c1c2c3, 0, result, 0, c1Len);
/* 221 */     System.arraycopy(c1c2c3, c1c2c3.length - 32, result, c1Len, 32);
/* 222 */     System.arraycopy(c1c2c3, c1Len, result, c1Len + 32, c1c2c3.length - c1Len - 32);
/* 223 */     return result;
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
/*     */   public static byte[] changeC1C3C2ToC1C2C3(byte[] c1c3c2, ECDomainParameters ecDomainParameters) {
/* 235 */     int c1Len = (ecDomainParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
/* 236 */     int c3Len = 32;
/* 237 */     byte[] result = new byte[c1c3c2.length];
/* 238 */     System.arraycopy(c1c3c2, 0, result, 0, c1Len);
/* 239 */     System.arraycopy(c1c3c2, c1Len + 32, result, c1Len, c1c3c2.length - c1Len - 32);
/* 240 */     System.arraycopy(c1c3c2, c1Len, result, c1c3c2.length - 32, 32);
/* 241 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] rsAsn1ToPlain(byte[] rsDer) {
/*     */     BigInteger[] decode;
/*     */     try {
/* 254 */       decode = StandardDSAEncoding.INSTANCE.decode(SM2_DOMAIN_PARAMS.getN(), rsDer);
/* 255 */     } catch (IOException e) {
/* 256 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/* 259 */     byte[] r = bigIntToFixedLengthBytes(decode[0]);
/* 260 */     byte[] s = bigIntToFixedLengthBytes(decode[1]);
/*     */     
/* 262 */     return ArrayUtil.addAll(new byte[][] { r, s });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] rsPlainToAsn1(byte[] sign) {
/* 273 */     if (sign.length != 64) {
/* 274 */       throw new CryptoException("err rs. ");
/*     */     }
/* 276 */     BigInteger r = new BigInteger(1, Arrays.copyOfRange(sign, 0, 32));
/* 277 */     BigInteger s = new BigInteger(1, Arrays.copyOfRange(sign, 32, 64));
/*     */     try {
/* 279 */       return StandardDSAEncoding.INSTANCE.encode(SM2_DOMAIN_PARAMS.getN(), r, s);
/* 280 */     } catch (IOException e) {
/* 281 */       throw new IORuntimeException(e);
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
/*     */   public static MacEngine createHmacSm3Engine(byte[] key) {
/* 293 */     return (MacEngine)new BCHMacEngine((Digest)new SM3Digest(), key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HMac hmacSm3(byte[] key) {
/* 304 */     return new HMac(HmacAlgorithm.HmacSM3, key);
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
/*     */   private static byte[] bigIntToFixedLengthBytes(BigInteger rOrS) {
/* 319 */     byte[] rs = rOrS.toByteArray();
/* 320 */     if (rs.length == 32)
/* 321 */       return rs; 
/* 322 */     if (rs.length == 33 && rs[0] == 0)
/* 323 */       return Arrays.copyOfRange(rs, 1, 33); 
/* 324 */     if (rs.length < 32) {
/* 325 */       byte[] result = new byte[32];
/* 326 */       Arrays.fill(result, (byte)0);
/* 327 */       System.arraycopy(rs, 0, result, 32 - rs.length, rs.length);
/* 328 */       return result;
/*     */     } 
/* 330 */     throw new CryptoException("Error rs: {}", new Object[] { Hex.toHexString(rs) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\SmUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */