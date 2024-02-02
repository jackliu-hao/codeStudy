/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base62Codec
/*     */   implements Encoder<byte[], byte[]>, Decoder<byte[], byte[]>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int STANDARD_BASE = 256;
/*     */   private static final int TARGET_BASE = 62;
/*  21 */   public static Base62Codec INSTANCE = new Base62Codec();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode(byte[] data) {
/*  31 */     return encode(data, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] encode(byte[] data, boolean useInverted) {
/*  42 */     Base62Encoder encoder = useInverted ? Base62Encoder.INVERTED_ENCODER : Base62Encoder.GMP_ENCODER;
/*  43 */     return encoder.encode(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decode(byte[] encoded) {
/*  54 */     return decode(encoded, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decode(byte[] encoded, boolean useInverted) {
/*  65 */     Base62Decoder decoder = useInverted ? Base62Decoder.INVERTED_DECODER : Base62Decoder.GMP_DECODER;
/*  66 */     return decoder.decode(encoded);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Base62Encoder
/*     */     implements Encoder<byte[], byte[]>
/*     */   {
/*  78 */     private static final byte[] GMP = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 };
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
/*  92 */     private static final byte[] INVERTED = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90 };
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
/* 103 */     public static Base62Encoder GMP_ENCODER = new Base62Encoder(GMP);
/* 104 */     public static Base62Encoder INVERTED_ENCODER = new Base62Encoder(INVERTED);
/*     */ 
/*     */ 
/*     */     
/*     */     private final byte[] alphabet;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Base62Encoder(byte[] alphabet) {
/* 114 */       this.alphabet = alphabet;
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] encode(byte[] data) {
/* 119 */       byte[] indices = Base62Codec.convert(data, 256, 62);
/* 120 */       return Base62Codec.translate(indices, this.alphabet);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Base62Decoder
/*     */     implements Decoder<byte[], byte[]>
/*     */   {
/* 131 */     public static Base62Decoder GMP_DECODER = new Base62Decoder(Base62Codec.Base62Encoder.GMP);
/* 132 */     public static Base62Decoder INVERTED_DECODER = new Base62Decoder(Base62Codec.Base62Encoder.INVERTED);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     private final byte[] lookupTable = new byte[123]; public Base62Decoder(byte[] alphabet) {
/* 143 */       for (int i = 0; i < alphabet.length; i++) {
/* 144 */         this.lookupTable[alphabet[i]] = (byte)i;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public byte[] decode(byte[] encoded) {
/* 151 */       byte[] prepared = Base62Codec.translate(encoded, this.lookupTable);
/* 152 */       return Base62Codec.convert(prepared, 62, 256);
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
/*     */   private static byte[] translate(byte[] indices, byte[] dictionary) {
/* 166 */     byte[] translation = new byte[indices.length];
/*     */     
/* 168 */     for (int i = 0; i < indices.length; i++) {
/* 169 */       translation[i] = dictionary[indices[i]];
/*     */     }
/*     */     
/* 172 */     return translation;
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
/*     */   private static byte[] convert(byte[] message, int sourceBase, int targetBase) {
/* 185 */     int estimatedLength = estimateOutputLength(message.length, sourceBase, targetBase);
/*     */     
/* 187 */     ByteArrayOutputStream out = new ByteArrayOutputStream(estimatedLength);
/*     */     
/* 189 */     byte[] source = message;
/*     */     
/* 191 */     while (source.length > 0) {
/* 192 */       ByteArrayOutputStream quotient = new ByteArrayOutputStream(source.length);
/*     */       
/* 194 */       int remainder = 0;
/*     */       
/* 196 */       for (byte b : source) {
/* 197 */         int accumulator = (b & 0xFF) + remainder * sourceBase;
/* 198 */         int digit = (accumulator - accumulator % targetBase) / targetBase;
/*     */         
/* 200 */         remainder = accumulator % targetBase;
/*     */         
/* 202 */         if (quotient.size() > 0 || digit > 0) {
/* 203 */           quotient.write(digit);
/*     */         }
/*     */       } 
/*     */       
/* 207 */       out.write(remainder);
/*     */       
/* 209 */       source = quotient.toByteArray();
/*     */     } 
/*     */ 
/*     */     
/* 213 */     for (int i = 0; i < message.length - 1 && message[i] == 0; i++) {
/* 214 */       out.write(0);
/*     */     }
/*     */     
/* 217 */     return ArrayUtil.reverse(out.toByteArray());
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
/*     */   private static int estimateOutputLength(int inputLength, int sourceBase, int targetBase) {
/* 229 */     return (int)Math.ceil(Math.log(sourceBase) / Math.log(targetBase) * inputLength);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Base62Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */