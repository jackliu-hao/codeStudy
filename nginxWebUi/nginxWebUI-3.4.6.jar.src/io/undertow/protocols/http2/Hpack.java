/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.nio.ByteBuffer;
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
/*     */ final class Hpack
/*     */ {
/*     */   private static final byte LOWER_DIFF = 32;
/*     */   static final int DEFAULT_TABLE_SIZE = 4096;
/*     */   private static final int MAX_INTEGER_OCTETS = 8;
/*     */   static final HeaderField[] STATIC_TABLE;
/*  46 */   private static final int[] PREFIX_TABLE = new int[32]; static {
/*  47 */     for (int i = 0; i < 32; i++) {
/*  48 */       int n = 0;
/*  49 */       for (int j = 0; j < i; j++) {
/*  50 */         n <<= 1;
/*  51 */         n |= 0x1;
/*     */       } 
/*  53 */       PREFIX_TABLE[i] = n;
/*     */     } 
/*     */     
/*  56 */     HeaderField[] fields = new HeaderField[62];
/*     */     
/*  58 */     fields[1] = new HeaderField(new HttpString(":authority"), null);
/*  59 */     fields[2] = new HeaderField(new HttpString(":method"), "GET");
/*  60 */     fields[3] = new HeaderField(new HttpString(":method"), "POST");
/*  61 */     fields[4] = new HeaderField(new HttpString(":path"), "/");
/*  62 */     fields[5] = new HeaderField(new HttpString(":path"), "/index.html");
/*  63 */     fields[6] = new HeaderField(new HttpString(":scheme"), "http");
/*  64 */     fields[7] = new HeaderField(new HttpString(":scheme"), "https");
/*  65 */     fields[8] = new HeaderField(new HttpString(":status"), "200");
/*  66 */     fields[9] = new HeaderField(new HttpString(":status"), "204");
/*  67 */     fields[10] = new HeaderField(new HttpString(":status"), "206");
/*  68 */     fields[11] = new HeaderField(new HttpString(":status"), "304");
/*  69 */     fields[12] = new HeaderField(new HttpString(":status"), "400");
/*  70 */     fields[13] = new HeaderField(new HttpString(":status"), "404");
/*  71 */     fields[14] = new HeaderField(new HttpString(":status"), "500");
/*  72 */     fields[15] = new HeaderField(new HttpString("accept-charset"), null);
/*  73 */     fields[16] = new HeaderField(new HttpString("accept-encoding"), "gzip, deflate");
/*  74 */     fields[17] = new HeaderField(new HttpString("accept-language"), null);
/*  75 */     fields[18] = new HeaderField(new HttpString("accept-ranges"), null);
/*  76 */     fields[19] = new HeaderField(new HttpString("accept"), null);
/*  77 */     fields[20] = new HeaderField(new HttpString("access-control-allow-origin"), null);
/*  78 */     fields[21] = new HeaderField(new HttpString("age"), null);
/*  79 */     fields[22] = new HeaderField(new HttpString("allow"), null);
/*  80 */     fields[23] = new HeaderField(new HttpString("authorization"), null);
/*  81 */     fields[24] = new HeaderField(new HttpString("cache-control"), null);
/*  82 */     fields[25] = new HeaderField(new HttpString("content-disposition"), null);
/*  83 */     fields[26] = new HeaderField(new HttpString("content-encoding"), null);
/*  84 */     fields[27] = new HeaderField(new HttpString("content-language"), null);
/*  85 */     fields[28] = new HeaderField(new HttpString("content-length"), null);
/*  86 */     fields[29] = new HeaderField(new HttpString("content-location"), null);
/*  87 */     fields[30] = new HeaderField(new HttpString("content-range"), null);
/*  88 */     fields[31] = new HeaderField(new HttpString("content-type"), null);
/*  89 */     fields[32] = new HeaderField(new HttpString("cookie"), null);
/*  90 */     fields[33] = new HeaderField(new HttpString("date"), null);
/*  91 */     fields[34] = new HeaderField(new HttpString("etag"), null);
/*  92 */     fields[35] = new HeaderField(new HttpString("expect"), null);
/*  93 */     fields[36] = new HeaderField(new HttpString("expires"), null);
/*  94 */     fields[37] = new HeaderField(new HttpString("from"), null);
/*  95 */     fields[38] = new HeaderField(new HttpString("host"), null);
/*  96 */     fields[39] = new HeaderField(new HttpString("if-match"), null);
/*  97 */     fields[40] = new HeaderField(new HttpString("if-modified-since"), null);
/*  98 */     fields[41] = new HeaderField(new HttpString("if-none-match"), null);
/*  99 */     fields[42] = new HeaderField(new HttpString("if-range"), null);
/* 100 */     fields[43] = new HeaderField(new HttpString("if-unmodified-since"), null);
/* 101 */     fields[44] = new HeaderField(new HttpString("last-modified"), null);
/* 102 */     fields[45] = new HeaderField(new HttpString("link"), null);
/* 103 */     fields[46] = new HeaderField(new HttpString("location"), null);
/* 104 */     fields[47] = new HeaderField(new HttpString("max-forwards"), null);
/* 105 */     fields[48] = new HeaderField(new HttpString("proxy-authenticate"), null);
/* 106 */     fields[49] = new HeaderField(new HttpString("proxy-authorization"), null);
/* 107 */     fields[50] = new HeaderField(new HttpString("range"), null);
/* 108 */     fields[51] = new HeaderField(new HttpString("referer"), null);
/* 109 */     fields[52] = new HeaderField(new HttpString("refresh"), null);
/* 110 */     fields[53] = new HeaderField(new HttpString("retry-after"), null);
/* 111 */     fields[54] = new HeaderField(new HttpString("server"), null);
/* 112 */     fields[55] = new HeaderField(new HttpString("set-cookie"), null);
/* 113 */     fields[56] = new HeaderField(new HttpString("strict-transport-security"), null);
/* 114 */     fields[57] = new HeaderField(new HttpString("transfer-encoding"), null);
/* 115 */     fields[58] = new HeaderField(new HttpString("user-agent"), null);
/* 116 */     fields[59] = new HeaderField(new HttpString("vary"), null);
/* 117 */     fields[60] = new HeaderField(new HttpString("via"), null);
/* 118 */     fields[61] = new HeaderField(new HttpString("www-authenticate"), null);
/* 119 */     STATIC_TABLE = fields;
/* 120 */   } static final int STATIC_TABLE_LENGTH = STATIC_TABLE.length - 1;
/*     */   
/*     */   static class HeaderField
/*     */   {
/*     */     final HttpString name;
/*     */     final String value;
/*     */     final int size;
/*     */     
/*     */     HeaderField(HttpString name, String value) {
/* 129 */       this.name = name;
/* 130 */       this.value = value;
/* 131 */       if (value != null) {
/* 132 */         this.size = 32 + name.length() + value.length();
/*     */       } else {
/* 134 */         this.size = -1;
/*     */       } 
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
/*     */   static int decodeInteger(ByteBuffer source, int n) throws HpackException {
/*     */     int b;
/* 151 */     if (source.remaining() == 0) {
/* 152 */       return -1;
/*     */     }
/* 154 */     if (n >= PREFIX_TABLE.length) {
/* 155 */       throw UndertowMessages.MESSAGES.integerEncodedOverTooManyOctets(8);
/*     */     }
/* 157 */     int count = 1;
/* 158 */     int sp = source.position();
/* 159 */     int mask = PREFIX_TABLE[n];
/*     */     
/* 161 */     int i = mask & source.get();
/*     */     
/* 163 */     if (i < PREFIX_TABLE[n]) {
/* 164 */       return i;
/*     */     }
/* 166 */     int m = 0;
/*     */     do {
/* 168 */       if (count++ > 8) {
/* 169 */         throw UndertowMessages.MESSAGES.integerEncodedOverTooManyOctets(8);
/*     */       }
/* 171 */       if (source.remaining() == 0) {
/*     */ 
/*     */         
/* 174 */         source.position(sp);
/* 175 */         return -1;
/*     */       } 
/*     */       
/* 178 */       if (m >= PREFIX_TABLE.length) {
/* 179 */         throw UndertowMessages.MESSAGES.integerEncodedOverTooManyOctets(8);
/*     */       }
/* 181 */       b = source.get();
/* 182 */       i += (b & 0x7F) * (PREFIX_TABLE[m] + 1);
/* 183 */       m += 7;
/* 184 */     } while ((b & 0x80) == 128);
/*     */     
/* 186 */     return i;
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
/*     */   static void encodeInteger(ByteBuffer source, int value, int n) {
/* 201 */     int twoNminus1 = PREFIX_TABLE[n];
/* 202 */     int pos = source.position() - 1;
/* 203 */     if (value < twoNminus1) {
/* 204 */       source.put(pos, (byte)(source.get(pos) | value));
/*     */     } else {
/* 206 */       source.put(pos, (byte)(source.get(pos) | twoNminus1));
/* 207 */       value -= twoNminus1;
/* 208 */       while (value >= 128) {
/* 209 */         source.put((byte)(value % 128 + 128));
/* 210 */         value /= 128;
/*     */       } 
/* 212 */       source.put((byte)value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static byte toLower(byte b) {
/* 218 */     if (b >= 65 && b <= 90) {
/* 219 */       return (byte)(b + 32);
/*     */     }
/* 221 */     return b;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Hpack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */