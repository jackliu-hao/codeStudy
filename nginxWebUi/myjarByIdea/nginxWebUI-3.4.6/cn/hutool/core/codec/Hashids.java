package cn.hutool.core.codec;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Hashids implements Encoder<long[], String>, Decoder<String, long[]> {
   private static final int LOTTERY_MOD = 100;
   private static final double GUARD_THRESHOLD = 12.0;
   private static final double SEPARATOR_THRESHOLD = 3.5;
   private static final int MIN_ALPHABET_LENGTH = 16;
   private static final Pattern HEX_VALUES_PATTERN = Pattern.compile("[\\w\\W]{1,12}");
   public static final char[] DEFAULT_ALPHABET = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
   private static final char[] DEFAULT_SEPARATORS = new char[]{'c', 'f', 'h', 'i', 's', 't', 'u', 'C', 'F', 'H', 'I', 'S', 'T', 'U'};
   private final char[] alphabet;
   private final char[] separators;
   private final Set<Character> separatorsSet;
   private final char[] salt;
   private final char[] guards;
   private final int minLength;

   public static Hashids create(char[] salt) {
      return create(salt, DEFAULT_ALPHABET, -1);
   }

   public static Hashids create(char[] salt, int minLength) {
      return create(salt, DEFAULT_ALPHABET, minLength);
   }

   public static Hashids create(char[] salt, char[] alphabet, int minLength) {
      return new Hashids(salt, alphabet, minLength);
   }

   public Hashids(char[] salt, char[] alphabet, int minLength) {
      this.minLength = minLength;
      this.salt = Arrays.copyOf(salt, salt.length);
      char[] tmpSeparators = this.shuffle(this.filterSeparators(DEFAULT_SEPARATORS, alphabet), this.salt);
      char[] tmpAlphabet = this.validateAndFilterAlphabet(alphabet, tmpSeparators);
      if (tmpSeparators.length == 0 || (double)(tmpAlphabet.length / tmpSeparators.length) > 3.5) {
         int minSeparatorsSize = (int)Math.ceil((double)tmpAlphabet.length / 3.5);
         if (minSeparatorsSize > tmpSeparators.length) {
            int missingSeparators = minSeparatorsSize - tmpSeparators.length;
            tmpSeparators = Arrays.copyOf(tmpSeparators, tmpSeparators.length + missingSeparators);
            System.arraycopy(tmpAlphabet, 0, tmpSeparators, tmpSeparators.length - missingSeparators, missingSeparators);
            System.arraycopy(tmpAlphabet, 0, tmpSeparators, tmpSeparators.length - missingSeparators, missingSeparators);
            tmpAlphabet = Arrays.copyOfRange(tmpAlphabet, missingSeparators, tmpAlphabet.length);
         }
      }

      this.shuffle(tmpAlphabet, this.salt);
      this.guards = new char[(int)Math.ceil((double)tmpAlphabet.length / 12.0)];
      if (alphabet.length < 3) {
         System.arraycopy(tmpSeparators, 0, this.guards, 0, this.guards.length);
         this.separators = Arrays.copyOfRange(tmpSeparators, this.guards.length, tmpSeparators.length);
         this.alphabet = tmpAlphabet;
      } else {
         System.arraycopy(tmpAlphabet, 0, this.guards, 0, this.guards.length);
         this.separators = tmpSeparators;
         this.alphabet = Arrays.copyOfRange(tmpAlphabet, this.guards.length, tmpAlphabet.length);
      }

      this.separatorsSet = (Set)IntStream.range(0, this.separators.length).mapToObj((idx) -> {
         return this.separators[idx];
      }).collect(Collectors.toSet());
   }

   public String encodeFromHex(String hexNumbers) {
      if (hexNumbers == null) {
         return null;
      } else {
         String hex = !hexNumbers.startsWith("0x") && !hexNumbers.startsWith("0X") ? hexNumbers : hexNumbers.substring(2);
         LongStream values = LongStream.empty();

         long value;
         for(Matcher matcher = HEX_VALUES_PATTERN.matcher(hex); matcher.find(); values = LongStream.concat(values, LongStream.of(value))) {
            value = (new BigInteger("1" + matcher.group(), 16)).longValue();
         }

         return this.encode(values.toArray());
      }
   }

   public String encode(long... numbers) {
      if (numbers == null) {
         return null;
      } else {
         char[] currentAlphabet = Arrays.copyOf(this.alphabet, this.alphabet.length);
         long lotteryId = LongStream.range(0L, (long)numbers.length).reduce(0L, (state, i) -> {
            long number = numbers[(int)i];
            if (number < 0L) {
               throw new IllegalArgumentException("invalid number: " + number);
            } else {
               return state + number % (i + 100L);
            }
         });
         char lottery = currentAlphabet[(int)(lotteryId % (long)currentAlphabet.length)];
         StringBuilder global = new StringBuilder();
         IntStream.range(0, numbers.length).forEach((idx) -> {
            this.deriveNewAlphabet(currentAlphabet, this.salt, lottery);
            int initialLength = global.length();
            this.translate(numbers[idx], currentAlphabet, global, initialLength);
            if (idx == 0) {
               global.insert(0, lottery);
            }

            if (idx + 1 < numbers.length) {
               long n = numbers[idx] % (long)(global.charAt(initialLength) + 1);
               global.append(this.separators[(int)(n % (long)this.separators.length)]);
            }

         });
         int paddingLeft;
         if (this.minLength > global.length()) {
            paddingLeft = (int)((lotteryId + (long)lottery) % (long)this.guards.length);
            global.insert(0, this.guards[paddingLeft]);
            if (this.minLength > global.length()) {
               paddingLeft = (int)((lotteryId + (long)global.charAt(2)) % (long)this.guards.length);
               global.append(this.guards[paddingLeft]);
            }
         }

         paddingLeft = this.minLength - global.length();

         while(paddingLeft > 0) {
            this.shuffle(currentAlphabet, Arrays.copyOf(currentAlphabet, currentAlphabet.length));
            int alphabetHalfSize = currentAlphabet.length / 2;
            int initialSize = global.length();
            int offset;
            if (paddingLeft > currentAlphabet.length) {
               offset = alphabetHalfSize + (currentAlphabet.length % 2 == 0 ? 0 : 1);
               global.insert(0, currentAlphabet, alphabetHalfSize, offset);
               global.insert(offset + initialSize, currentAlphabet, 0, alphabetHalfSize);
               paddingLeft -= currentAlphabet.length;
            } else {
               offset = currentAlphabet.length + global.length() - this.minLength;
               int secondHalfStartOffset = alphabetHalfSize + Math.floorDiv(offset, 2);
               int secondHalfLength = currentAlphabet.length - secondHalfStartOffset;
               int firstHalfLength = paddingLeft - secondHalfLength;
               global.insert(0, currentAlphabet, secondHalfStartOffset, secondHalfLength);
               global.insert(secondHalfLength + initialSize, currentAlphabet, 0, firstHalfLength);
               paddingLeft = 0;
            }
         }

         return global.toString();
      }
   }

   public String decodeToHex(String hash) {
      if (hash == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();
         Arrays.stream(this.decode(hash)).mapToObj(Long::toHexString).forEach((hex) -> {
            sb.append(hex, 1, hex.length());
         });
         return sb.toString();
      }
   }

   public long[] decode(String hash) {
      if (hash == null) {
         return null;
      } else {
         Set<Character> guardsSet = (Set)IntStream.range(0, this.guards.length).mapToObj((idx) -> {
            return this.guards[idx];
         }).collect(Collectors.toSet());
         int[] guardsIdx = IntStream.range(0, hash.length()).filter((idx) -> {
            return guardsSet.contains(hash.charAt(idx));
         }).toArray();
         int startIdx;
         int endIdx;
         if (guardsIdx.length > 0) {
            startIdx = guardsIdx[0] + 1;
            endIdx = guardsIdx.length > 1 ? guardsIdx[1] : hash.length();
         } else {
            startIdx = 0;
            endIdx = hash.length();
         }

         LongStream decoded = LongStream.empty();
         if (hash.length() > 0) {
            char lottery = hash.charAt(startIdx);
            int length = hash.length() - guardsIdx.length - 1;
            StringBuilder block = new StringBuilder(length);
            char[] decodeSalt = new char[this.alphabet.length];
            decodeSalt[0] = lottery;
            int saltLength = this.salt.length >= this.alphabet.length ? this.alphabet.length - 1 : this.salt.length;
            System.arraycopy(this.salt, 0, decodeSalt, 1, saltLength);
            int saltLeft = this.alphabet.length - saltLength - 1;
            char[] currentAlphabet = Arrays.copyOf(this.alphabet, this.alphabet.length);

            for(int i = startIdx + 1; i < endIdx; ++i) {
               if (!this.separatorsSet.contains(hash.charAt(i))) {
                  block.append(hash.charAt(i));
                  if (i < endIdx - 1) {
                     continue;
                  }
               }

               if (block.length() > 0) {
                  if (saltLeft > 0) {
                     System.arraycopy(currentAlphabet, 0, decodeSalt, this.alphabet.length - saltLeft, saltLeft);
                  }

                  this.shuffle(currentAlphabet, decodeSalt);
                  long n = this.translate(block.toString().toCharArray(), currentAlphabet);
                  decoded = LongStream.concat(decoded, LongStream.of(n));
                  block = new StringBuilder(length);
               }
            }
         }

         long[] decodedValue = decoded.toArray();
         if (!Objects.equals(hash, this.encode(decodedValue))) {
            throw new IllegalArgumentException("invalid hash: " + hash);
         } else {
            return decodedValue;
         }
      }
   }

   private StringBuilder translate(long n, char[] alphabet, StringBuilder sb, int start) {
      long input = n;

      do {
         sb.insert(start, alphabet[(int)(input % (long)alphabet.length)]);
         input /= (long)alphabet.length;
      } while(input > 0L);

      return sb;
   }

   private long translate(char[] hash, char[] alphabet) {
      long number = 0L;
      Map<Character, Integer> alphabetMapping = (Map)IntStream.range(0, alphabet.length).mapToObj((idx) -> {
         return new Object[]{alphabet[idx], idx};
      }).collect(Collectors.groupingBy((arr) -> {
         return (Character)arr[0];
      }, Collectors.mapping((arr) -> {
         return (Integer)arr[1];
      }, Collectors.reducing((Object)null, (a, b) -> {
         return a == null ? b : a;
      }))));

      for(int i = 0; i < hash.length; ++i) {
         number += (long)(Integer)alphabetMapping.computeIfAbsent(hash[i], (k) -> {
            throw new IllegalArgumentException("Invalid alphabet for hash");
         }) * (long)Math.pow((double)alphabet.length, (double)(hash.length - i - 1));
      }

      return number;
   }

   private char[] deriveNewAlphabet(char[] alphabet, char[] salt, char lottery) {
      char[] newSalt = new char[alphabet.length];
      newSalt[0] = lottery;
      int spaceLeft = newSalt.length - 1;
      int offset = 1;
      if (salt.length > 0 && spaceLeft > 0) {
         int length = Math.min(salt.length, spaceLeft);
         System.arraycopy(salt, 0, newSalt, offset, length);
         spaceLeft -= length;
         offset += length;
      }

      if (spaceLeft > 0) {
         System.arraycopy(alphabet, 0, newSalt, offset, spaceLeft);
      }

      return this.shuffle(alphabet, newSalt);
   }

   private char[] validateAndFilterAlphabet(char[] alphabet, char[] separators) {
      if (alphabet.length < 16) {
         throw new IllegalArgumentException(String.format("alphabet must contain at least %d unique characters: %d", 16, alphabet.length));
      } else {
         Set<Character> seen = new LinkedHashSet(alphabet.length);
         Set<Character> invalid = (Set)IntStream.range(0, separators.length).mapToObj((idxx) -> {
            return separators[idxx];
         }).collect(Collectors.toSet());
         IntStream.range(0, alphabet.length).forEach((i) -> {
            if (alphabet[i] == ' ') {
               throw new IllegalArgumentException(String.format("alphabet must not contain spaces: index %d", i));
            } else {
               Character c = alphabet[i];
               if (!invalid.contains(c)) {
                  seen.add(c);
               }

            }
         });
         char[] uniqueAlphabet = new char[seen.size()];
         int idx = 0;

         char c;
         for(Iterator var7 = seen.iterator(); var7.hasNext(); uniqueAlphabet[idx++] = c) {
            c = (Character)var7.next();
         }

         return uniqueAlphabet;
      }
   }

   private char[] filterSeparators(char[] separators, char[] alphabet) {
      Set<Character> valid = (Set)IntStream.range(0, alphabet.length).mapToObj((idx) -> {
         return alphabet[idx];
      }).collect(Collectors.toSet());
      Stream var10000 = IntStream.range(0, separators.length).mapToObj((idx) -> {
         return separators[idx];
      });
      valid.getClass();
      return ((String)var10000.filter(valid::contains).map((c) -> {
         return Character.toString(c);
      }).collect(Collectors.joining())).toCharArray();
   }

   private char[] shuffle(char[] alphabet, char[] salt) {
      int i = alphabet.length - 1;
      int v = 0;

      for(int p = 0; salt.length > 0 && i > 0; ++v) {
         v %= salt.length;
         char z;
         p += z = salt[v];
         int j = (z + v + p) % i;
         char tmp = alphabet[j];
         alphabet[j] = alphabet[i];
         alphabet[i] = tmp;
         --i;
      }

      return alphabet;
   }
}
