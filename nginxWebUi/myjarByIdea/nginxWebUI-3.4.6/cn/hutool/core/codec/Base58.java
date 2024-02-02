package cn.hutool.core.codec;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.exceptions.ValidateException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Base58 {
   private static final int CHECKSUM_SIZE = 4;

   public static String encodeChecked(Integer version, byte[] data) {
      return encode(addChecksum(version, data));
   }

   public static String encode(byte[] data) {
      return Base58Codec.INSTANCE.encode(data);
   }

   public static byte[] decodeChecked(CharSequence encoded) throws ValidateException {
      try {
         return decodeChecked(encoded, true);
      } catch (ValidateException var2) {
         return decodeChecked(encoded, false);
      }
   }

   public static byte[] decodeChecked(CharSequence encoded, boolean withVersion) throws ValidateException {
      byte[] valueWithChecksum = decode(encoded);
      return verifyAndRemoveChecksum(valueWithChecksum, withVersion);
   }

   public static byte[] decode(CharSequence encoded) {
      return Base58Codec.INSTANCE.decode(encoded);
   }

   private static byte[] verifyAndRemoveChecksum(byte[] data, boolean withVersion) {
      byte[] payload = Arrays.copyOfRange(data, withVersion ? 1 : 0, data.length - 4);
      byte[] checksum = Arrays.copyOfRange(data, data.length - 4, data.length);
      byte[] expectedChecksum = checksum(payload);
      if (!Arrays.equals(checksum, expectedChecksum)) {
         throw new ValidateException("Base58 checksum is invalid");
      } else {
         return payload;
      }
   }

   private static byte[] addChecksum(Integer version, byte[] payload) {
      byte[] addressBytes;
      if (null != version) {
         addressBytes = new byte[1 + payload.length + 4];
         addressBytes[0] = (byte)version;
         System.arraycopy(payload, 0, addressBytes, 1, payload.length);
      } else {
         addressBytes = new byte[payload.length + 4];
         System.arraycopy(payload, 0, addressBytes, 0, payload.length);
      }

      byte[] checksum = checksum(payload);
      System.arraycopy(checksum, 0, addressBytes, addressBytes.length - 4, 4);
      return addressBytes;
   }

   private static byte[] checksum(byte[] data) {
      byte[] hash = hash256(hash256(data));
      return Arrays.copyOfRange(hash, 0, 4);
   }

   private static byte[] hash256(byte[] data) {
      try {
         return MessageDigest.getInstance("SHA-256").digest(data);
      } catch (NoSuchAlgorithmException var2) {
         throw new UtilException(var2);
      }
   }
}
