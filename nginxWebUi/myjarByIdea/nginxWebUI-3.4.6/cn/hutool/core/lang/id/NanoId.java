package cn.hutool.core.lang.id;

import cn.hutool.core.util.RandomUtil;
import java.security.SecureRandom;
import java.util.Random;

public class NanoId {
   private static final SecureRandom DEFAULT_NUMBER_GENERATOR = RandomUtil.getSecureRandom();
   private static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
   public static final int DEFAULT_SIZE = 21;

   public static String randomNanoId() {
      return randomNanoId(21);
   }

   public static String randomNanoId(int size) {
      return randomNanoId((Random)null, (char[])null, size);
   }

   public static String randomNanoId(Random random, char[] alphabet, int size) {
      if (random == null) {
         random = DEFAULT_NUMBER_GENERATOR;
      }

      if (alphabet == null) {
         alphabet = DEFAULT_ALPHABET;
      }

      if (alphabet.length != 0 && alphabet.length < 256) {
         if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero.");
         } else {
            int mask = (2 << (int)Math.floor(Math.log((double)(alphabet.length - 1)) / Math.log(2.0))) - 1;
            int step = (int)Math.ceil(1.6 * (double)mask * (double)size / (double)alphabet.length);
            StringBuilder idBuilder = new StringBuilder();

            while(true) {
               byte[] bytes = new byte[step];
               ((Random)random).nextBytes(bytes);

               for(int i = 0; i < step; ++i) {
                  int alphabetIndex = bytes[i] & mask;
                  if (alphabetIndex < alphabet.length) {
                     idBuilder.append(alphabet[alphabetIndex]);
                     if (idBuilder.length() == size) {
                        return idBuilder.toString();
                     }
                  }
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Alphabet must contain between 1 and 255 symbols.");
      }
   }
}
