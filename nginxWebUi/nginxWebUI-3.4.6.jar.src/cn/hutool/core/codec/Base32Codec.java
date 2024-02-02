/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ public class Base32Codec
/*     */   implements Encoder<byte[], String>, Decoder<CharSequence, byte[]>
/*     */ {
/*  20 */   public static Base32Codec INSTANCE = new Base32Codec();
/*     */ 
/*     */   
/*     */   public String encode(byte[] data) {
/*  24 */     return encode(data, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(byte[] data, boolean useHex) {
/*  35 */     Base32Encoder encoder = useHex ? Base32Encoder.HEX_ENCODER : Base32Encoder.ENCODER;
/*  36 */     return encoder.encode(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] decode(CharSequence encoded) {
/*  41 */     return decode(encoded, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decode(CharSequence encoded, boolean useHex) {
/*  52 */     Base32Decoder decoder = useHex ? Base32Decoder.HEX_DECODER : Base32Decoder.DECODER;
/*  53 */     return decoder.decode(encoded);
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Base32Encoder
/*     */     implements Encoder<byte[], String>
/*     */   {
/*     */     private static final String DEFAULT_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
/*     */     private static final String HEX_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV";
/*  62 */     private static final Character DEFAULT_PAD = Character.valueOf('=');
/*  63 */     private static final int[] BASE32_FILL = new int[] { -1, 4, 1, 6, 3 };
/*     */     
/*  65 */     public static final Base32Encoder ENCODER = new Base32Encoder("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", DEFAULT_PAD);
/*  66 */     public static final Base32Encoder HEX_ENCODER = new Base32Encoder("0123456789ABCDEFGHIJKLMNOPQRSTUV", DEFAULT_PAD);
/*     */ 
/*     */ 
/*     */     
/*     */     private final char[] alphabet;
/*     */ 
/*     */     
/*     */     private final Character pad;
/*     */ 
/*     */ 
/*     */     
/*     */     public Base32Encoder(String alphabet, Character pad) {
/*  78 */       this.alphabet = alphabet.toCharArray();
/*  79 */       this.pad = pad;
/*     */     }
/*     */ 
/*     */     
/*     */     public String encode(byte[] data) {
/*  84 */       int i = 0;
/*  85 */       int index = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  90 */       int encodeLen = data.length * 8 / 5;
/*  91 */       if (encodeLen != 0) {
/*  92 */         encodeLen = encodeLen + 1 + BASE32_FILL[data.length * 8 % 5];
/*     */       }
/*     */       
/*  95 */       StringBuilder base32 = new StringBuilder(encodeLen);
/*     */       
/*  97 */       while (i < data.length) {
/*     */         
/*  99 */         int digit, currByte = (data[i] >= 0) ? data[i] : (data[i] + 256);
/*     */ 
/*     */         
/* 102 */         if (index > 3) {
/* 103 */           int nextByte; if (i + 1 < data.length) {
/* 104 */             nextByte = (data[i + 1] >= 0) ? data[i + 1] : (data[i + 1] + 256);
/*     */           } else {
/* 106 */             nextByte = 0;
/*     */           } 
/*     */           
/* 109 */           digit = currByte & 255 >> index;
/* 110 */           index = (index + 5) % 8;
/* 111 */           digit <<= index;
/* 112 */           digit |= nextByte >> 8 - index;
/* 113 */           i++;
/*     */         } else {
/* 115 */           digit = currByte >> 8 - index + 5 & 0x1F;
/* 116 */           index = (index + 5) % 8;
/* 117 */           if (index == 0) {
/* 118 */             i++;
/*     */           }
/*     */         } 
/* 121 */         base32.append(this.alphabet[digit]);
/*     */       } 
/*     */       
/* 124 */       if (null != this.pad)
/*     */       {
/* 126 */         while (base32.length() < encodeLen) {
/* 127 */           base32.append(this.pad.charValue());
/*     */         }
/*     */       }
/*     */       
/* 131 */       return base32.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Base32Decoder
/*     */     implements Decoder<CharSequence, byte[]>
/*     */   {
/*     */     private static final char BASE_CHAR = '0';
/*     */     
/* 141 */     public static final Base32Decoder DECODER = new Base32Decoder("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567");
/* 142 */     public static final Base32Decoder HEX_DECODER = new Base32Decoder("0123456789ABCDEFGHIJKLMNOPQRSTUV");
/*     */ 
/*     */ 
/*     */     
/*     */     private final byte[] lookupTable;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Base32Decoder(String alphabet) {
/* 152 */       this.lookupTable = new byte[128];
/* 153 */       Arrays.fill(this.lookupTable, (byte)-1);
/*     */       
/* 155 */       int length = alphabet.length();
/*     */ 
/*     */       
/* 158 */       for (int i = 0; i < length; i++) {
/* 159 */         char c = alphabet.charAt(i);
/* 160 */         this.lookupTable[c - 48] = (byte)i;
/*     */         
/* 162 */         if (c >= 'A' && c <= 'Z') {
/* 163 */           this.lookupTable[Character.toLowerCase(c) - 48] = (byte)i;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public byte[] decode(CharSequence encoded) {
/* 171 */       String base32 = encoded.toString();
/* 172 */       int len = base32.endsWith("=") ? (base32.indexOf("=") * 5 / 8) : (base32.length() * 5 / 8);
/* 173 */       byte[] bytes = new byte[len];
/*     */       
/* 175 */       for (int i = 0, index = 0, offset = 0; i < base32.length(); i++) {
/* 176 */         int lookup = base32.charAt(i) - 48;
/*     */ 
/*     */         
/* 179 */         if (lookup >= 0 && lookup < this.lookupTable.length) {
/*     */ 
/*     */ 
/*     */           
/* 183 */           int digit = this.lookupTable[lookup];
/*     */ 
/*     */           
/* 186 */           if (digit >= 0)
/*     */           {
/*     */ 
/*     */             
/* 190 */             if (index <= 3) {
/* 191 */               index = (index + 5) % 8;
/* 192 */               if (index == 0) {
/* 193 */                 bytes[offset] = (byte)(bytes[offset] | digit);
/* 194 */                 offset++;
/* 195 */                 if (offset >= bytes.length) {
/*     */                   break;
/*     */                 }
/*     */               } else {
/* 199 */                 bytes[offset] = (byte)(bytes[offset] | digit << 8 - index);
/*     */               } 
/*     */             } else {
/* 202 */               index = (index + 5) % 8;
/* 203 */               bytes[offset] = (byte)(bytes[offset] | digit >>> index);
/* 204 */               offset++;
/*     */               
/* 206 */               if (offset >= bytes.length) {
/*     */                 break;
/*     */               }
/* 209 */               bytes[offset] = (byte)(bytes[offset] | digit << 8 - index);
/*     */             }  } 
/*     */         } 
/* 212 */       }  return bytes;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base32Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */