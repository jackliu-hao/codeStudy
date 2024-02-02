/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.IntStream;
/*     */ import java.util.stream.LongStream;
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
/*     */ public class Hashids
/*     */   implements Encoder<long[], String>, Decoder<String, long[]>
/*     */ {
/*     */   private static final int LOTTERY_MOD = 100;
/*     */   private static final double GUARD_THRESHOLD = 12.0D;
/*     */   private static final double SEPARATOR_THRESHOLD = 3.5D;
/*     */   private static final int MIN_ALPHABET_LENGTH = 16;
/*  43 */   private static final Pattern HEX_VALUES_PATTERN = Pattern.compile("[\\w\\W]{1,12}");
/*     */ 
/*     */   
/*  46 */   public static final char[] DEFAULT_ALPHABET = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   private static final char[] DEFAULT_SEPARATORS = new char[] { 'c', 'f', 'h', 'i', 's', 't', 'u', 'C', 'F', 'H', 'I', 'S', 'T', 'U' };
/*     */ 
/*     */ 
/*     */   
/*     */   private final char[] alphabet;
/*     */ 
/*     */   
/*     */   private final char[] separators;
/*     */ 
/*     */   
/*     */   private final Set<Character> separatorsSet;
/*     */ 
/*     */   
/*     */   private final char[] salt;
/*     */ 
/*     */   
/*     */   private final char[] guards;
/*     */ 
/*     */   
/*     */   private final int minLength;
/*     */ 
/*     */ 
/*     */   
/*     */   public static Hashids create(char[] salt) {
/*  78 */     return create(salt, DEFAULT_ALPHABET, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Hashids create(char[] salt, int minLength) {
/*  89 */     return create(salt, DEFAULT_ALPHABET, minLength);
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
/*     */   public static Hashids create(char[] salt, char[] alphabet, int minLength) {
/* 101 */     return new Hashids(salt, alphabet, minLength);
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
/*     */   public Hashids(char[] salt, char[] alphabet, int minLength) {
/* 113 */     this.minLength = minLength;
/* 114 */     this.salt = Arrays.copyOf(salt, salt.length);
/*     */ 
/*     */     
/* 117 */     char[] tmpSeparators = shuffle(filterSeparators(DEFAULT_SEPARATORS, alphabet), this.salt);
/*     */ 
/*     */     
/* 120 */     char[] tmpAlphabet = validateAndFilterAlphabet(alphabet, tmpSeparators);
/*     */ 
/*     */     
/* 123 */     if (tmpSeparators.length == 0 || (tmpAlphabet.length / tmpSeparators.length) > 3.5D) {
/*     */       
/* 125 */       int minSeparatorsSize = (int)Math.ceil(tmpAlphabet.length / 3.5D);
/*     */       
/* 127 */       if (minSeparatorsSize > tmpSeparators.length) {
/*     */         
/* 129 */         int missingSeparators = minSeparatorsSize - tmpSeparators.length;
/* 130 */         tmpSeparators = Arrays.copyOf(tmpSeparators, tmpSeparators.length + missingSeparators);
/* 131 */         System.arraycopy(tmpAlphabet, 0, tmpSeparators, tmpSeparators.length - missingSeparators, missingSeparators);
/*     */         
/* 133 */         System.arraycopy(tmpAlphabet, 0, tmpSeparators, tmpSeparators.length - missingSeparators, missingSeparators);
/*     */         
/* 135 */         tmpAlphabet = Arrays.copyOfRange(tmpAlphabet, missingSeparators, tmpAlphabet.length);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 140 */     shuffle(tmpAlphabet, this.salt);
/*     */ 
/*     */     
/* 143 */     this.guards = new char[(int)Math.ceil(tmpAlphabet.length / 12.0D)];
/* 144 */     if (alphabet.length < 3) {
/* 145 */       System.arraycopy(tmpSeparators, 0, this.guards, 0, this.guards.length);
/* 146 */       this.separators = Arrays.copyOfRange(tmpSeparators, this.guards.length, tmpSeparators.length);
/* 147 */       this.alphabet = tmpAlphabet;
/*     */     } else {
/* 149 */       System.arraycopy(tmpAlphabet, 0, this.guards, 0, this.guards.length);
/* 150 */       this.separators = tmpSeparators;
/* 151 */       this.alphabet = Arrays.copyOfRange(tmpAlphabet, this.guards.length, tmpAlphabet.length);
/*     */     } 
/*     */ 
/*     */     
/* 155 */     this
/*     */       
/* 157 */       .separatorsSet = (Set<Character>)IntStream.range(0, this.separators.length).mapToObj(idx -> Character.valueOf(this.separators[idx])).collect(Collectors.toSet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encodeFromHex(String hexNumbers) {
/* 168 */     if (hexNumbers == null) {
/* 169 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 174 */     String hex = (hexNumbers.startsWith("0x") || hexNumbers.startsWith("0X")) ? hexNumbers.substring(2) : hexNumbers;
/*     */ 
/*     */     
/* 177 */     LongStream values = LongStream.empty();
/* 178 */     Matcher matcher = HEX_VALUES_PATTERN.matcher(hex);
/* 179 */     while (matcher.find()) {
/* 180 */       long value = (new BigInteger("1" + matcher.group(), 16)).longValue();
/* 181 */       values = LongStream.concat(values, LongStream.of(value));
/*     */     } 
/*     */     
/* 184 */     return encode(values.toArray());
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
/*     */   public String encode(long... numbers) {
/* 196 */     if (numbers == null) {
/* 197 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 201 */     char[] currentAlphabet = Arrays.copyOf(this.alphabet, this.alphabet.length);
/*     */ 
/*     */ 
/*     */     
/* 205 */     long lotteryId = LongStream.range(0L, numbers.length).reduce(0L, (state, i) -> {
/*     */           long number = numbers[(int)i];
/*     */           if (number < 0L) {
/*     */             throw new IllegalArgumentException("invalid number: " + number);
/*     */           }
/*     */           return state + number % (i + 100L);
/*     */         });
/* 212 */     char lottery = currentAlphabet[(int)(lotteryId % currentAlphabet.length)];
/*     */ 
/*     */     
/* 215 */     StringBuilder global = new StringBuilder();
/* 216 */     IntStream.range(0, numbers.length)
/* 217 */       .forEach(idx -> {
/*     */           deriveNewAlphabet(currentAlphabet, this.salt, lottery);
/*     */ 
/*     */           
/*     */           int initialLength = global.length();
/*     */ 
/*     */           
/*     */           translate(numbers[idx], currentAlphabet, global, initialLength);
/*     */ 
/*     */           
/*     */           if (idx == 0) {
/*     */             global.insert(0, lottery);
/*     */           }
/*     */           
/*     */           if (idx + 1 < numbers.length) {
/*     */             long n = numbers[idx] % (global.charAt(initialLength) + 1);
/*     */             
/*     */             global.append(this.separators[(int)(n % this.separators.length)]);
/*     */           } 
/*     */         });
/*     */     
/* 238 */     if (this.minLength > global.length()) {
/* 239 */       int guardIdx = (int)((lotteryId + lottery) % this.guards.length);
/* 240 */       global.insert(0, this.guards[guardIdx]);
/* 241 */       if (this.minLength > global.length()) {
/* 242 */         guardIdx = (int)((lotteryId + global.charAt(2)) % this.guards.length);
/* 243 */         global.append(this.guards[guardIdx]);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 248 */     int paddingLeft = this.minLength - global.length();
/* 249 */     while (paddingLeft > 0) {
/* 250 */       shuffle(currentAlphabet, Arrays.copyOf(currentAlphabet, currentAlphabet.length));
/*     */       
/* 252 */       int alphabetHalfSize = currentAlphabet.length / 2;
/* 253 */       int initialSize = global.length();
/* 254 */       if (paddingLeft > currentAlphabet.length) {
/*     */         
/* 256 */         int offset = alphabetHalfSize + ((currentAlphabet.length % 2 == 0) ? 0 : 1);
/*     */         
/* 258 */         global.insert(0, currentAlphabet, alphabetHalfSize, offset);
/* 259 */         global.insert(offset + initialSize, currentAlphabet, 0, alphabetHalfSize);
/*     */         
/* 261 */         paddingLeft -= currentAlphabet.length;
/*     */         continue;
/*     */       } 
/* 264 */       int excess = currentAlphabet.length + global.length() - this.minLength;
/* 265 */       int secondHalfStartOffset = alphabetHalfSize + Math.floorDiv(excess, 2);
/* 266 */       int secondHalfLength = currentAlphabet.length - secondHalfStartOffset;
/* 267 */       int firstHalfLength = paddingLeft - secondHalfLength;
/*     */       
/* 269 */       global.insert(0, currentAlphabet, secondHalfStartOffset, secondHalfLength);
/* 270 */       global.insert(secondHalfLength + initialSize, currentAlphabet, 0, firstHalfLength);
/*     */       
/* 272 */       paddingLeft = 0;
/*     */     } 
/*     */ 
/*     */     
/* 276 */     return global.toString();
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
/*     */   public String decodeToHex(String hash) {
/* 291 */     if (hash == null) {
/* 292 */       return null;
/*     */     }
/*     */     
/* 295 */     StringBuilder sb = new StringBuilder();
/* 296 */     Arrays.stream(decode(hash))
/* 297 */       .mapToObj(Long::toHexString)
/* 298 */       .forEach(hex -> sb.append(hex, 1, hex.length()));
/* 299 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long[] decode(String hash) {
/*     */     int startIdx, endIdx;
/* 311 */     if (hash == null) {
/* 312 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 318 */     Set<Character> guardsSet = (Set<Character>)IntStream.range(0, this.guards.length).mapToObj(idx -> Character.valueOf(this.guards[idx])).collect(Collectors.toSet());
/*     */ 
/*     */ 
/*     */     
/* 322 */     int[] guardsIdx = IntStream.range(0, hash.length()).filter(idx -> guardsSet.contains(Character.valueOf(hash.charAt(idx)))).toArray();
/*     */ 
/*     */     
/* 325 */     if (guardsIdx.length > 0) {
/* 326 */       startIdx = guardsIdx[0] + 1;
/* 327 */       endIdx = (guardsIdx.length > 1) ? guardsIdx[1] : hash.length();
/*     */     } else {
/* 329 */       startIdx = 0;
/* 330 */       endIdx = hash.length();
/*     */     } 
/*     */     
/* 333 */     LongStream decoded = LongStream.empty();
/*     */     
/* 335 */     if (hash.length() > 0) {
/* 336 */       char lottery = hash.charAt(startIdx);
/*     */ 
/*     */       
/* 339 */       int length = hash.length() - guardsIdx.length - 1;
/* 340 */       StringBuilder block = new StringBuilder(length);
/*     */ 
/*     */       
/* 343 */       char[] decodeSalt = new char[this.alphabet.length];
/* 344 */       decodeSalt[0] = lottery;
/* 345 */       int saltLength = (this.salt.length >= this.alphabet.length) ? (this.alphabet.length - 1) : this.salt.length;
/* 346 */       System.arraycopy(this.salt, 0, decodeSalt, 1, saltLength);
/* 347 */       int saltLeft = this.alphabet.length - saltLength - 1;
/*     */ 
/*     */       
/* 350 */       char[] currentAlphabet = Arrays.copyOf(this.alphabet, this.alphabet.length);
/*     */       
/* 352 */       for (int i = startIdx + 1; i < endIdx; i++) {
/* 353 */         if (false == this.separatorsSet.contains(Character.valueOf(hash.charAt(i)))) {
/* 354 */           block.append(hash.charAt(i));
/*     */           
/* 356 */           if (i < endIdx - 1) {
/*     */             continue;
/*     */           }
/*     */         } 
/*     */         
/* 361 */         if (block.length() > 0) {
/*     */           
/* 363 */           if (saltLeft > 0) {
/* 364 */             System.arraycopy(currentAlphabet, 0, decodeSalt, this.alphabet.length - saltLeft, saltLeft);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 369 */           shuffle(currentAlphabet, decodeSalt);
/*     */ 
/*     */           
/* 372 */           long n = translate(block.toString().toCharArray(), currentAlphabet);
/* 373 */           decoded = LongStream.concat(decoded, LongStream.of(n));
/*     */ 
/*     */           
/* 376 */           block = new StringBuilder(length);
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */     } 
/* 382 */     long[] decodedValue = decoded.toArray();
/* 383 */     if (!Objects.equals(hash, encode(decodedValue))) {
/* 384 */       throw new IllegalArgumentException("invalid hash: " + hash);
/*     */     }
/*     */     
/* 387 */     return decodedValue;
/*     */   }
/*     */ 
/*     */   
/*     */   private StringBuilder translate(long n, char[] alphabet, StringBuilder sb, int start) {
/* 392 */     long input = n;
/*     */     
/*     */     do {
/* 395 */       sb.insert(start, alphabet[(int)(input % alphabet.length)]);
/*     */ 
/*     */       
/* 398 */       input /= alphabet.length;
/* 399 */     } while (input > 0L);
/*     */     
/* 401 */     return sb;
/*     */   }
/*     */   
/*     */   private long translate(char[] hash, char[] alphabet) {
/* 405 */     long number = 0L;
/*     */ 
/*     */ 
/*     */     
/* 409 */     Map<Character, Integer> alphabetMapping = (Map<Character, Integer>)IntStream.range(0, alphabet.length).mapToObj(idx -> new Object[] { Character.valueOf(alphabet[idx]), Integer.valueOf(idx) }).collect(Collectors.groupingBy(arr -> (Character)arr[0], 
/* 410 */           Collectors.mapping(arr -> (Integer)arr[1], 
/* 411 */             Collectors.reducing(null, (a, b) -> (a == null) ? b : a))));
/*     */     
/* 413 */     for (int i = 0; i < hash.length; i++) {
/* 414 */       number += ((Integer)alphabetMapping.computeIfAbsent(Character.valueOf(hash[i]), k -> { throw new IllegalArgumentException("Invalid alphabet for hash"); })).intValue() * 
/*     */         
/* 416 */         (long)Math.pow(alphabet.length, (hash.length - i - 1));
/*     */     } 
/*     */     
/* 419 */     return number;
/*     */   }
/*     */ 
/*     */   
/*     */   private char[] deriveNewAlphabet(char[] alphabet, char[] salt, char lottery) {
/* 424 */     char[] newSalt = new char[alphabet.length];
/*     */ 
/*     */     
/* 427 */     newSalt[0] = lottery;
/* 428 */     int spaceLeft = newSalt.length - 1;
/* 429 */     int offset = 1;
/*     */     
/* 431 */     if (salt.length > 0 && spaceLeft > 0) {
/* 432 */       int length = Math.min(salt.length, spaceLeft);
/* 433 */       System.arraycopy(salt, 0, newSalt, offset, length);
/* 434 */       spaceLeft -= length;
/* 435 */       offset += length;
/*     */     } 
/*     */     
/* 438 */     if (spaceLeft > 0) {
/* 439 */       System.arraycopy(alphabet, 0, newSalt, offset, spaceLeft);
/*     */     }
/*     */ 
/*     */     
/* 443 */     return shuffle(alphabet, newSalt);
/*     */   }
/*     */ 
/*     */   
/*     */   private char[] validateAndFilterAlphabet(char[] alphabet, char[] separators) {
/* 448 */     if (alphabet.length < 16) {
/* 449 */       throw new IllegalArgumentException(String.format("alphabet must contain at least %d unique characters: %d", new Object[] {
/* 450 */               Integer.valueOf(16), Integer.valueOf(alphabet.length)
/*     */             }));
/*     */     }
/* 453 */     Set<Character> seen = new LinkedHashSet<>(alphabet.length);
/*     */ 
/*     */     
/* 456 */     Set<Character> invalid = (Set<Character>)IntStream.range(0, separators.length).mapToObj(idx -> Character.valueOf(separators[idx])).collect(Collectors.toSet());
/*     */ 
/*     */     
/* 459 */     IntStream.range(0, alphabet.length)
/* 460 */       .forEach(i -> {
/*     */           if (alphabet[i] == ' ') {
/*     */             throw new IllegalArgumentException(String.format("alphabet must not contain spaces: index %d", new Object[] { Integer.valueOf(i) }));
/*     */           }
/*     */           
/*     */           Character c = Character.valueOf(alphabet[i]);
/*     */           
/*     */           if (!invalid.contains(c)) {
/*     */             seen.add(c);
/*     */           }
/*     */         });
/*     */     
/* 472 */     char[] uniqueAlphabet = new char[seen.size()];
/* 473 */     int idx = 0;
/* 474 */     for (Iterator<Character> iterator = seen.iterator(); iterator.hasNext(); ) { char c = ((Character)iterator.next()).charValue();
/* 475 */       uniqueAlphabet[idx++] = c; }
/*     */     
/* 477 */     return uniqueAlphabet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] filterSeparators(char[] separators, char[] alphabet) {
/* 484 */     Set<Character> valid = (Set<Character>)IntStream.range(0, alphabet.length).mapToObj(idx -> Character.valueOf(alphabet[idx])).collect(Collectors.toSet());
/*     */     
/* 486 */     return ((String)IntStream.range(0, separators.length)
/* 487 */       .mapToObj(idx -> Character.valueOf(separators[idx]))
/* 488 */       .filter(valid::contains)
/*     */       
/* 490 */       .map(c -> Character.toString(c.charValue()))
/* 491 */       .collect(Collectors.joining()))
/* 492 */       .toCharArray();
/*     */   }
/*     */   
/*     */   private char[] shuffle(char[] alphabet, char[] salt) {
/* 496 */     for (int i = alphabet.length - 1, v = 0, p = 0; salt.length > 0 && i > 0; i--, v++) {
/* 497 */       v %= salt.length; int z;
/* 498 */       p += z = salt[v];
/* 499 */       int j = (z + v + p) % i;
/* 500 */       char tmp = alphabet[j];
/* 501 */       alphabet[j] = alphabet[i];
/* 502 */       alphabet[i] = tmp;
/*     */     } 
/* 504 */     return alphabet;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Hashids.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */