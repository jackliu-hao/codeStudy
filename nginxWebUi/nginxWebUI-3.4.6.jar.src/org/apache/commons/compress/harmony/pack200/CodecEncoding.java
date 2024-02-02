/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class CodecEncoding
/*     */ {
/*  35 */   private static final BHSDCodec[] canonicalCodec = new BHSDCodec[] { null, new BHSDCodec(1, 256), new BHSDCodec(1, 256, 1), new BHSDCodec(1, 256, 0, 1), new BHSDCodec(1, 256, 1, 1), new BHSDCodec(2, 256), new BHSDCodec(2, 256, 1), new BHSDCodec(2, 256, 0, 1), new BHSDCodec(2, 256, 1, 1), new BHSDCodec(3, 256), new BHSDCodec(3, 256, 1), new BHSDCodec(3, 256, 0, 1), new BHSDCodec(3, 256, 1, 1), new BHSDCodec(4, 256), new BHSDCodec(4, 256, 1), new BHSDCodec(4, 256, 0, 1), new BHSDCodec(4, 256, 1, 1), new BHSDCodec(5, 4), new BHSDCodec(5, 4, 1), new BHSDCodec(5, 4, 2), new BHSDCodec(5, 16), new BHSDCodec(5, 16, 1), new BHSDCodec(5, 16, 2), new BHSDCodec(5, 32), new BHSDCodec(5, 32, 1), new BHSDCodec(5, 32, 2), new BHSDCodec(5, 64), new BHSDCodec(5, 64, 1), new BHSDCodec(5, 64, 2), new BHSDCodec(5, 128), new BHSDCodec(5, 128, 1), new BHSDCodec(5, 128, 2), new BHSDCodec(5, 4, 0, 1), new BHSDCodec(5, 4, 1, 1), new BHSDCodec(5, 4, 2, 1), new BHSDCodec(5, 16, 0, 1), new BHSDCodec(5, 16, 1, 1), new BHSDCodec(5, 16, 2, 1), new BHSDCodec(5, 32, 0, 1), new BHSDCodec(5, 32, 1, 1), new BHSDCodec(5, 32, 2, 1), new BHSDCodec(5, 64, 0, 1), new BHSDCodec(5, 64, 1, 1), new BHSDCodec(5, 64, 2, 1), new BHSDCodec(5, 128, 0, 1), new BHSDCodec(5, 128, 1, 1), new BHSDCodec(5, 128, 2, 1), new BHSDCodec(2, 192), new BHSDCodec(2, 224), new BHSDCodec(2, 240), new BHSDCodec(2, 248), new BHSDCodec(2, 252), new BHSDCodec(2, 8, 0, 1), new BHSDCodec(2, 8, 1, 1), new BHSDCodec(2, 16, 0, 1), new BHSDCodec(2, 16, 1, 1), new BHSDCodec(2, 32, 0, 1), new BHSDCodec(2, 32, 1, 1), new BHSDCodec(2, 64, 0, 1), new BHSDCodec(2, 64, 1, 1), new BHSDCodec(2, 128, 0, 1), new BHSDCodec(2, 128, 1, 1), new BHSDCodec(2, 192, 0, 1), new BHSDCodec(2, 192, 1, 1), new BHSDCodec(2, 224, 0, 1), new BHSDCodec(2, 224, 1, 1), new BHSDCodec(2, 240, 0, 1), new BHSDCodec(2, 240, 1, 1), new BHSDCodec(2, 248, 0, 1), new BHSDCodec(2, 248, 1, 1), new BHSDCodec(3, 192), new BHSDCodec(3, 224), new BHSDCodec(3, 240), new BHSDCodec(3, 248), new BHSDCodec(3, 252), new BHSDCodec(3, 8, 0, 1), new BHSDCodec(3, 8, 1, 1), new BHSDCodec(3, 16, 0, 1), new BHSDCodec(3, 16, 1, 1), new BHSDCodec(3, 32, 0, 1), new BHSDCodec(3, 32, 1, 1), new BHSDCodec(3, 64, 0, 1), new BHSDCodec(3, 64, 1, 1), new BHSDCodec(3, 128, 0, 1), new BHSDCodec(3, 128, 1, 1), new BHSDCodec(3, 192, 0, 1), new BHSDCodec(3, 192, 1, 1), new BHSDCodec(3, 224, 0, 1), new BHSDCodec(3, 224, 1, 1), new BHSDCodec(3, 240, 0, 1), new BHSDCodec(3, 240, 1, 1), new BHSDCodec(3, 248, 0, 1), new BHSDCodec(3, 248, 1, 1), new BHSDCodec(4, 192), new BHSDCodec(4, 224), new BHSDCodec(4, 240), new BHSDCodec(4, 248), new BHSDCodec(4, 252), new BHSDCodec(4, 8, 0, 1), new BHSDCodec(4, 8, 1, 1), new BHSDCodec(4, 16, 0, 1), new BHSDCodec(4, 16, 1, 1), new BHSDCodec(4, 32, 0, 1), new BHSDCodec(4, 32, 1, 1), new BHSDCodec(4, 64, 0, 1), new BHSDCodec(4, 64, 1, 1), new BHSDCodec(4, 128, 0, 1), new BHSDCodec(4, 128, 1, 1), new BHSDCodec(4, 192, 0, 1), new BHSDCodec(4, 192, 1, 1), new BHSDCodec(4, 224, 0, 1), new BHSDCodec(4, 224, 1, 1), new BHSDCodec(4, 240, 0, 1), new BHSDCodec(4, 240, 1, 1), new BHSDCodec(4, 248, 0, 1), new BHSDCodec(4, 248, 1, 1) };
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
/*     */   private static Map canonicalCodecsToSpecifiers;
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
/*     */   public static Codec getCodec(int value, InputStream in, Codec defaultCodec) throws IOException, Pack200Exception {
/*  90 */     if (canonicalCodec.length != 116) {
/*  91 */       throw new Error("Canonical encodings have been incorrectly modified");
/*     */     }
/*  93 */     if (value < 0) {
/*  94 */       throw new IllegalArgumentException("Encoding cannot be less than zero");
/*     */     }
/*  96 */     if (value == 0) {
/*  97 */       return defaultCodec;
/*     */     }
/*  99 */     if (value <= 115) {
/* 100 */       return canonicalCodec[value];
/*     */     }
/* 102 */     if (value == 116) {
/* 103 */       int code = in.read();
/* 104 */       if (code == -1) {
/* 105 */         throw new EOFException("End of buffer read whilst trying to decode codec");
/*     */       }
/* 107 */       int d = code & 0x1;
/* 108 */       int s = code >> 1 & 0x3;
/* 109 */       int b = (code >> 3 & 0x7) + 1;
/*     */ 
/*     */       
/* 112 */       code = in.read();
/* 113 */       if (code == -1) {
/* 114 */         throw new EOFException("End of buffer read whilst trying to decode codec");
/*     */       }
/* 116 */       int h = code + 1;
/*     */       
/* 118 */       return new BHSDCodec(b, h, s, d);
/*     */     } 
/* 120 */     if (value >= 117 && value <= 140) {
/* 121 */       Codec aCodec, bCodec; int i = value - 117;
/* 122 */       int kx = i & 0x3;
/* 123 */       boolean kbflag = ((i >> 2 & 0x1) == 1);
/* 124 */       boolean adef = ((i >> 3 & 0x1) == 1);
/* 125 */       boolean bdef = ((i >> 4 & 0x1) == 1);
/*     */ 
/*     */       
/* 128 */       if (adef && bdef) {
/* 129 */         throw new Pack200Exception("ADef and BDef should never both be true");
/*     */       }
/* 131 */       int kb = kbflag ? in.read() : 3;
/* 132 */       int k = (kb + 1) * (int)Math.pow(16.0D, kx);
/*     */       
/* 134 */       if (adef) {
/* 135 */         aCodec = defaultCodec;
/*     */       } else {
/* 137 */         aCodec = getCodec(in.read(), in, defaultCodec);
/*     */       } 
/* 139 */       if (bdef) {
/* 140 */         bCodec = defaultCodec;
/*     */       } else {
/* 142 */         bCodec = getCodec(in.read(), in, defaultCodec);
/*     */       } 
/* 144 */       return new RunCodec(k, aCodec, bCodec);
/*     */     } 
/* 146 */     if (value < 141 || value > 188) {
/* 147 */       throw new Pack200Exception("Invalid codec encoding byte (" + value + ") found");
/*     */     }
/* 149 */     int offset = value - 141;
/* 150 */     boolean fdef = ((offset & 0x1) == 1);
/* 151 */     boolean udef = ((offset >> 1 & 0x1) == 1);
/* 152 */     int tdefl = offset >> 2;
/* 153 */     boolean tdef = (tdefl != 0);
/*     */     
/* 155 */     int[] tdefToL = { 0, 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252 };
/* 156 */     int l = tdefToL[tdefl];
/*     */ 
/*     */ 
/*     */     
/* 160 */     if (tdef) {
/* 161 */       Codec codec1 = fdef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
/* 162 */       Codec codec2 = udef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 168 */       return new PopulationCodec(codec1, l, codec2);
/*     */     } 
/* 170 */     Codec fCodec = fdef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
/* 171 */     Codec tCodec = getCodec(in.read(), in, defaultCodec);
/* 172 */     Codec uCodec = udef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
/* 173 */     return new PopulationCodec(fCodec, tCodec, uCodec);
/*     */   }
/*     */   
/*     */   public static int getSpecifierForDefaultCodec(BHSDCodec defaultCodec) {
/* 177 */     return getSpecifier(defaultCodec, null)[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public static int[] getSpecifier(Codec codec, Codec defaultForBand) {
/* 182 */     if (canonicalCodecsToSpecifiers == null) {
/* 183 */       HashMap<Object, Object> reverseMap = new HashMap<>(canonicalCodec.length);
/* 184 */       for (int i = 0; i < canonicalCodec.length; i++) {
/* 185 */         reverseMap.put(canonicalCodec[i], Integer.valueOf(i));
/*     */       }
/* 187 */       canonicalCodecsToSpecifiers = reverseMap;
/*     */     } 
/*     */     
/* 190 */     if (canonicalCodecsToSpecifiers.containsKey(codec)) {
/* 191 */       return new int[] { ((Integer)canonicalCodecsToSpecifiers.get(codec)).intValue() };
/*     */     }
/* 193 */     if (codec instanceof BHSDCodec) {
/*     */       
/* 195 */       BHSDCodec bhsdCodec = (BHSDCodec)codec;
/* 196 */       int[] specifiers = new int[3];
/* 197 */       specifiers[0] = 116;
/* 198 */       specifiers[1] = (bhsdCodec.isDelta() ? 1 : 0) + 2 * bhsdCodec.getS() + 8 * (bhsdCodec.getB() - 1);
/* 199 */       specifiers[2] = bhsdCodec.getH() - 1;
/* 200 */       return specifiers;
/*     */     } 
/* 202 */     if (codec instanceof RunCodec) {
/* 203 */       int kb, kx; RunCodec runCodec = (RunCodec)codec;
/* 204 */       int k = runCodec.getK();
/*     */ 
/*     */       
/* 207 */       if (k <= 256) {
/* 208 */         kb = 0;
/* 209 */         kx = k - 1;
/* 210 */       } else if (k <= 4096) {
/* 211 */         kb = 1;
/* 212 */         kx = k / 16 - 1;
/* 213 */       } else if (k <= 65536) {
/* 214 */         kb = 2;
/* 215 */         kx = k / 256 - 1;
/*     */       } else {
/* 217 */         kb = 3;
/* 218 */         kx = k / 4096 - 1;
/*     */       } 
/* 220 */       Codec aCodec = runCodec.getACodec();
/* 221 */       Codec bCodec = runCodec.getBCodec();
/* 222 */       int abDef = 0;
/* 223 */       if (aCodec.equals(defaultForBand)) {
/* 224 */         abDef = 1;
/* 225 */       } else if (bCodec.equals(defaultForBand)) {
/* 226 */         abDef = 2;
/*     */       } 
/* 228 */       int first = 117 + kb + ((kx == 3) ? 0 : 4) + 8 * abDef;
/* 229 */       int[] aSpecifier = (abDef == 1) ? new int[0] : getSpecifier(aCodec, defaultForBand);
/* 230 */       int[] bSpecifier = (abDef == 2) ? new int[0] : getSpecifier(bCodec, defaultForBand);
/* 231 */       int[] specifier = new int[1 + ((kx == 3) ? 0 : 1) + aSpecifier.length + bSpecifier.length];
/* 232 */       specifier[0] = first;
/* 233 */       int index = 1;
/* 234 */       if (kx != 3) {
/* 235 */         specifier[1] = kx;
/* 236 */         index++;
/*     */       }  int i;
/* 238 */       for (i = 0; i < aSpecifier.length; i++) {
/* 239 */         specifier[index] = aSpecifier[i];
/* 240 */         index++;
/*     */       } 
/* 242 */       for (i = 0; i < bSpecifier.length; i++) {
/* 243 */         specifier[index] = bSpecifier[i];
/* 244 */         index++;
/*     */       } 
/* 246 */       return specifier;
/*     */     } 
/* 248 */     if (codec instanceof PopulationCodec) {
/* 249 */       PopulationCodec populationCodec = (PopulationCodec)codec;
/* 250 */       Codec tokenCodec = populationCodec.getTokenCodec();
/* 251 */       Codec favouredCodec = populationCodec.getFavouredCodec();
/* 252 */       Codec unfavouredCodec = populationCodec.getUnfavouredCodec();
/* 253 */       int fDef = favouredCodec.equals(defaultForBand) ? 1 : 0;
/* 254 */       int uDef = unfavouredCodec.equals(defaultForBand) ? 1 : 0;
/* 255 */       int tDefL = 0;
/* 256 */       int[] favoured = populationCodec.getFavoured();
/* 257 */       if (favoured != null) {
/* 258 */         int k = favoured.length;
/* 259 */         if (tokenCodec == Codec.BYTE1) {
/* 260 */           tDefL = 1;
/* 261 */         } else if (tokenCodec instanceof BHSDCodec) {
/* 262 */           BHSDCodec tokenBHSD = (BHSDCodec)tokenCodec;
/* 263 */           if (tokenBHSD.getS() == 0) {
/* 264 */             int[] possibleLValues = { 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252 };
/* 265 */             int l = 256 - tokenBHSD.getH();
/* 266 */             int j = Arrays.binarySearch(possibleLValues, l);
/* 267 */             if (j != -1)
/*     */             {
/* 269 */               tDefL = j++;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/* 274 */       int first = 141 + fDef + 2 * uDef + 4 * tDefL;
/* 275 */       int[] favouredSpecifier = (fDef == 1) ? new int[0] : getSpecifier(favouredCodec, defaultForBand);
/* 276 */       int[] tokenSpecifier = (tDefL != 0) ? new int[0] : getSpecifier(tokenCodec, defaultForBand);
/* 277 */       int[] unfavouredSpecifier = (uDef == 1) ? new int[0] : getSpecifier(unfavouredCodec, defaultForBand);
/* 278 */       int[] specifier = new int[1 + favouredSpecifier.length + unfavouredSpecifier.length + tokenSpecifier.length];
/*     */       
/* 280 */       specifier[0] = first;
/* 281 */       int index = 1; int i;
/* 282 */       for (i = 0; i < favouredSpecifier.length; i++) {
/* 283 */         specifier[index] = favouredSpecifier[i];
/* 284 */         index++;
/*     */       } 
/* 286 */       for (i = 0; i < tokenSpecifier.length; i++) {
/* 287 */         specifier[index] = tokenSpecifier[i];
/* 288 */         index++;
/*     */       } 
/* 290 */       for (i = 0; i < unfavouredSpecifier.length; i++) {
/* 291 */         specifier[index] = unfavouredSpecifier[i];
/* 292 */         index++;
/*     */       } 
/* 294 */       return specifier;
/*     */     } 
/*     */     
/* 297 */     return null;
/*     */   }
/*     */   
/*     */   public static BHSDCodec getCanonicalCodec(int i) {
/* 301 */     return canonicalCodec[i];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CodecEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */