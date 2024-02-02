package cn.hutool.crypto;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.digest.mac.BCHMacEngine;
import cn.hutool.crypto.digest.mac.MacEngine;
import cn.hutool.crypto.symmetric.SM4;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SmUtil {
   private static final int RS_LEN = 32;
   public static final String SM2_CURVE_NAME = "sm2p256v1";
   public static final ECDomainParameters SM2_DOMAIN_PARAMS = BCUtil.toDomainParams(GMNamedCurves.getByName("sm2p256v1"));
   public static final ASN1ObjectIdentifier ID_SM2_PUBLIC_KEY_PARAM = new ASN1ObjectIdentifier("1.2.156.10197.1.301");

   public static SM2 sm2() {
      return new SM2();
   }

   public static SM2 sm2(String privateKeyStr, String publicKeyStr) {
      return new SM2(privateKeyStr, publicKeyStr);
   }

   public static SM2 sm2(byte[] privateKey, byte[] publicKey) {
      return new SM2(privateKey, publicKey);
   }

   public static SM2 sm2(PrivateKey privateKey, PublicKey publicKey) {
      return new SM2(privateKey, publicKey);
   }

   public static SM2 sm2(ECPrivateKeyParameters privateKeyParams, ECPublicKeyParameters publicKeyParams) {
      return new SM2(privateKeyParams, publicKeyParams);
   }

   public static SM3 sm3() {
      return new SM3();
   }

   public static SM3 sm3WithSalt(byte[] salt) {
      return new SM3(salt);
   }

   public static String sm3(String data) {
      return sm3().digestHex(data);
   }

   public static String sm3(InputStream data) {
      return sm3().digestHex(data);
   }

   public static String sm3(File dataFile) {
      return sm3().digestHex(dataFile);
   }

   public static SM4 sm4() {
      return new SM4();
   }

   public static SM4 sm4(byte[] key) {
      return new SM4(key);
   }

   public static byte[] changeC1C2C3ToC1C3C2(byte[] c1c2c3, ECDomainParameters ecDomainParameters) {
      int c1Len = (ecDomainParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
      int c3Len = true;
      byte[] result = new byte[c1c2c3.length];
      System.arraycopy(c1c2c3, 0, result, 0, c1Len);
      System.arraycopy(c1c2c3, c1c2c3.length - 32, result, c1Len, 32);
      System.arraycopy(c1c2c3, c1Len, result, c1Len + 32, c1c2c3.length - c1Len - 32);
      return result;
   }

   public static byte[] changeC1C3C2ToC1C2C3(byte[] c1c3c2, ECDomainParameters ecDomainParameters) {
      int c1Len = (ecDomainParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
      int c3Len = true;
      byte[] result = new byte[c1c3c2.length];
      System.arraycopy(c1c3c2, 0, result, 0, c1Len);
      System.arraycopy(c1c3c2, c1Len + 32, result, c1Len, c1c3c2.length - c1Len - 32);
      System.arraycopy(c1c3c2, c1Len, result, c1c3c2.length - 32, 32);
      return result;
   }

   public static byte[] rsAsn1ToPlain(byte[] rsDer) {
      BigInteger[] decode;
      try {
         decode = StandardDSAEncoding.INSTANCE.decode(SM2_DOMAIN_PARAMS.getN(), rsDer);
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }

      byte[] r = bigIntToFixedLengthBytes(decode[0]);
      byte[] s = bigIntToFixedLengthBytes(decode[1]);
      return ArrayUtil.addAll(new byte[][]{r, s});
   }

   public static byte[] rsPlainToAsn1(byte[] sign) {
      if (sign.length != 64) {
         throw new CryptoException("err rs. ");
      } else {
         BigInteger r = new BigInteger(1, Arrays.copyOfRange(sign, 0, 32));
         BigInteger s = new BigInteger(1, Arrays.copyOfRange(sign, 32, 64));

         try {
            return StandardDSAEncoding.INSTANCE.encode(SM2_DOMAIN_PARAMS.getN(), r, s);
         } catch (IOException var4) {
            throw new IORuntimeException(var4);
         }
      }
   }

   public static MacEngine createHmacSm3Engine(byte[] key) {
      return new BCHMacEngine(new SM3Digest(), key);
   }

   public static HMac hmacSm3(byte[] key) {
      return new HMac(HmacAlgorithm.HmacSM3, key);
   }

   private static byte[] bigIntToFixedLengthBytes(BigInteger rOrS) {
      byte[] rs = rOrS.toByteArray();
      if (rs.length == 32) {
         return rs;
      } else if (rs.length == 33 && rs[0] == 0) {
         return Arrays.copyOfRange(rs, 1, 33);
      } else if (rs.length < 32) {
         byte[] result = new byte[32];
         Arrays.fill(result, (byte)0);
         System.arraycopy(rs, 0, result, 32 - rs.length, rs.length);
         return result;
      } else {
         throw new CryptoException("Error rs: {}", new Object[]{Hex.toHexString(rs)});
      }
   }
}
