/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base58Codec
/*     */   implements Encoder<byte[], String>, Decoder<CharSequence, byte[]>
/*     */ {
/*  16 */   public static Base58Codec INSTANCE = new Base58Codec();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(byte[] data) {
/*  26 */     return Base58Encoder.ENCODER.encode(data);
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
/*     */   public byte[] decode(CharSequence encoded) throws IllegalArgumentException {
/*  38 */     return Base58Decoder.DECODER.decode(encoded);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Base58Encoder
/*     */     implements Encoder<byte[], String>
/*     */   {
/*     */     private static final String DEFAULT_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
/*     */ 
/*     */     
/*  49 */     public static final Base58Encoder ENCODER = new Base58Encoder("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray());
/*     */ 
/*     */     
/*     */     private final char[] alphabet;
/*     */ 
/*     */     
/*     */     private final char alphabetZero;
/*     */ 
/*     */ 
/*     */     
/*     */     public Base58Encoder(char[] alphabet) {
/*  60 */       this.alphabet = alphabet;
/*  61 */       this.alphabetZero = alphabet[0];
/*     */     }
/*     */ 
/*     */     
/*     */     public String encode(byte[] data) {
/*  66 */       if (null == data) {
/*  67 */         return null;
/*     */       }
/*  69 */       if (data.length == 0) {
/*  70 */         return "";
/*     */       }
/*     */       
/*  73 */       int zeroCount = 0;
/*  74 */       while (zeroCount < data.length && data[zeroCount] == 0) {
/*  75 */         zeroCount++;
/*     */       }
/*     */       
/*  78 */       data = Arrays.copyOf(data, data.length);
/*  79 */       char[] encoded = new char[data.length * 2];
/*  80 */       int outputStart = encoded.length;
/*  81 */       for (int inputStart = zeroCount; inputStart < data.length; ) {
/*  82 */         encoded[--outputStart] = this.alphabet[Base58Codec.divmod(data, inputStart, 256, 58)];
/*  83 */         if (data[inputStart] == 0) {
/*  84 */           inputStart++;
/*     */         }
/*     */       } 
/*     */       
/*  88 */       while (outputStart < encoded.length && encoded[outputStart] == this.alphabetZero) {
/*  89 */         outputStart++;
/*     */       }
/*  91 */       while (--zeroCount >= 0) {
/*  92 */         encoded[--outputStart] = this.alphabetZero;
/*     */       }
/*     */       
/*  95 */       return new String(encoded, outputStart, encoded.length - outputStart);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Base58Decoder
/*     */     implements Decoder<CharSequence, byte[]>
/*     */   {
/* 106 */     public static Base58Decoder DECODER = new Base58Decoder("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
/*     */ 
/*     */ 
/*     */     
/*     */     private final byte[] lookupTable;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Base58Decoder(String alphabet) {
/* 116 */       byte[] lookupTable = new byte[123];
/* 117 */       Arrays.fill(lookupTable, (byte)-1);
/*     */       
/* 119 */       int length = alphabet.length();
/* 120 */       for (int i = 0; i < length; i++) {
/* 121 */         lookupTable[alphabet.charAt(i)] = (byte)i;
/*     */       }
/* 123 */       this.lookupTable = lookupTable;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] decode(CharSequence encoded) {
/* 128 */       if (encoded.length() == 0) {
/* 129 */         return new byte[0];
/*     */       }
/*     */       
/* 132 */       byte[] input58 = new byte[encoded.length()];
/* 133 */       for (int i = 0; i < encoded.length(); i++) {
/* 134 */         char c = encoded.charAt(i);
/* 135 */         int digit = (c < '') ? this.lookupTable[c] : -1;
/* 136 */         if (digit < 0) {
/* 137 */           throw new IllegalArgumentException(StrUtil.format("Invalid char '{}' at [{}]", new Object[] { Character.valueOf(c), Integer.valueOf(i) }));
/*     */         }
/* 139 */         input58[i] = (byte)digit;
/*     */       } 
/*     */       
/* 142 */       int zeros = 0;
/* 143 */       while (zeros < input58.length && input58[zeros] == 0) {
/* 144 */         zeros++;
/*     */       }
/*     */       
/* 147 */       byte[] decoded = new byte[encoded.length()];
/* 148 */       int outputStart = decoded.length;
/* 149 */       for (int inputStart = zeros; inputStart < input58.length; ) {
/* 150 */         decoded[--outputStart] = Base58Codec.divmod(input58, inputStart, 58, 256);
/* 151 */         if (input58[inputStart] == 0) {
/* 152 */           inputStart++;
/*     */         }
/*     */       } 
/*     */       
/* 156 */       while (outputStart < decoded.length && decoded[outputStart] == 0) {
/* 157 */         outputStart++;
/*     */       }
/*     */       
/* 160 */       return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
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
/*     */   private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
/* 178 */     int remainder = 0;
/* 179 */     for (int i = firstDigit; i < number.length; i++) {
/* 180 */       int digit = number[i] & 0xFF;
/* 181 */       int temp = remainder * base + digit;
/* 182 */       number[i] = (byte)(temp / divisor);
/* 183 */       remainder = temp % divisor;
/*     */     } 
/* 185 */     return (byte)remainder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base58Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */