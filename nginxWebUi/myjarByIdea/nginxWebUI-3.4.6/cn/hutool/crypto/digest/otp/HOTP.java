package cn.hutool.crypto.digest.otp;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;

public class HOTP {
   private static final int[] MOD_DIVISORS = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
   public static final int DEFAULT_PASSWORD_LENGTH = 6;
   public static final HmacAlgorithm HOTP_HMAC_ALGORITHM;
   private final HMac mac;
   private final int passwordLength;
   private final int modDivisor;
   private final byte[] buffer;

   public HOTP(byte[] key) {
      this(6, key);
   }

   public HOTP(int passwordLength, byte[] key) {
      this(passwordLength, HOTP_HMAC_ALGORITHM, key);
   }

   public HOTP(int passwordLength, HmacAlgorithm algorithm, byte[] key) {
      if (passwordLength >= MOD_DIVISORS.length) {
         throw new IllegalArgumentException("Password length must be < " + MOD_DIVISORS.length);
      } else {
         this.mac = new HMac(algorithm, key);
         this.modDivisor = MOD_DIVISORS[passwordLength];
         this.passwordLength = passwordLength;
         this.buffer = new byte[8];
      }
   }

   public synchronized int generate(long counter) {
      this.buffer[0] = (byte)((int)((counter & -72057594037927936L) >>> 56));
      this.buffer[1] = (byte)((int)((counter & 71776119061217280L) >>> 48));
      this.buffer[2] = (byte)((int)((counter & 280375465082880L) >>> 40));
      this.buffer[3] = (byte)((int)((counter & 1095216660480L) >>> 32));
      this.buffer[4] = (byte)((int)((counter & 4278190080L) >>> 24));
      this.buffer[5] = (byte)((int)((counter & 16711680L) >>> 16));
      this.buffer[6] = (byte)((int)((counter & 65280L) >>> 8));
      this.buffer[7] = (byte)((int)(counter & 255L));
      byte[] digest = this.mac.digest(this.buffer);
      return this.truncate(digest);
   }

   public static String generateSecretKey(int numBytes) {
      return Base32.encode(RandomUtil.getSHA1PRNGRandom(RandomUtil.randomBytes(256)).generateSeed(numBytes));
   }

   public int getPasswordLength() {
      return this.passwordLength;
   }

   public String getAlgorithm() {
      return this.mac.getAlgorithm();
   }

   private int truncate(byte[] digest) {
      int offset = digest[digest.length - 1] & 15;
      return ((digest[offset] & 127) << 24 | (digest[offset + 1] & 255) << 16 | (digest[offset + 2] & 255) << 8 | digest[offset + 3] & 255) % this.modDivisor;
   }

   static {
      HOTP_HMAC_ALGORITHM = HmacAlgorithm.HmacSHA1;
   }
}
