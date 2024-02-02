/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class HPackHuffman
/*     */ {
/*     */   private static final HuffmanCode[] HUFFMAN_CODES;
/*     */   private static final int[] DECODING_TABLE;
/*     */   private static final int LOW_TERMINAL_BIT = 32768;
/*     */   private static final int HIGH_TERMINAL_BIT = -2147483648;
/*     */   private static final int LOW_MASK = 32767;
/*     */   
/*     */   static {
/*  50 */     HuffmanCode[] codes = new HuffmanCode[257];
/*     */     
/*  52 */     codes[0] = new HuffmanCode(8184, 13);
/*  53 */     codes[1] = new HuffmanCode(8388568, 23);
/*  54 */     codes[2] = new HuffmanCode(268435426, 28);
/*  55 */     codes[3] = new HuffmanCode(268435427, 28);
/*  56 */     codes[4] = new HuffmanCode(268435428, 28);
/*  57 */     codes[5] = new HuffmanCode(268435429, 28);
/*  58 */     codes[6] = new HuffmanCode(268435430, 28);
/*  59 */     codes[7] = new HuffmanCode(268435431, 28);
/*  60 */     codes[8] = new HuffmanCode(268435432, 28);
/*  61 */     codes[9] = new HuffmanCode(16777194, 24);
/*  62 */     codes[10] = new HuffmanCode(1073741820, 30);
/*  63 */     codes[11] = new HuffmanCode(268435433, 28);
/*  64 */     codes[12] = new HuffmanCode(268435434, 28);
/*  65 */     codes[13] = new HuffmanCode(1073741821, 30);
/*  66 */     codes[14] = new HuffmanCode(268435435, 28);
/*  67 */     codes[15] = new HuffmanCode(268435436, 28);
/*  68 */     codes[16] = new HuffmanCode(268435437, 28);
/*  69 */     codes[17] = new HuffmanCode(268435438, 28);
/*  70 */     codes[18] = new HuffmanCode(268435439, 28);
/*  71 */     codes[19] = new HuffmanCode(268435440, 28);
/*  72 */     codes[20] = new HuffmanCode(268435441, 28);
/*  73 */     codes[21] = new HuffmanCode(268435442, 28);
/*  74 */     codes[22] = new HuffmanCode(1073741822, 30);
/*  75 */     codes[23] = new HuffmanCode(268435443, 28);
/*  76 */     codes[24] = new HuffmanCode(268435444, 28);
/*  77 */     codes[25] = new HuffmanCode(268435445, 28);
/*  78 */     codes[26] = new HuffmanCode(268435446, 28);
/*  79 */     codes[27] = new HuffmanCode(268435447, 28);
/*  80 */     codes[28] = new HuffmanCode(268435448, 28);
/*  81 */     codes[29] = new HuffmanCode(268435449, 28);
/*  82 */     codes[30] = new HuffmanCode(268435450, 28);
/*  83 */     codes[31] = new HuffmanCode(268435451, 28);
/*  84 */     codes[32] = new HuffmanCode(20, 6);
/*  85 */     codes[33] = new HuffmanCode(1016, 10);
/*  86 */     codes[34] = new HuffmanCode(1017, 10);
/*  87 */     codes[35] = new HuffmanCode(4090, 12);
/*  88 */     codes[36] = new HuffmanCode(8185, 13);
/*  89 */     codes[37] = new HuffmanCode(21, 6);
/*  90 */     codes[38] = new HuffmanCode(248, 8);
/*  91 */     codes[39] = new HuffmanCode(2042, 11);
/*  92 */     codes[40] = new HuffmanCode(1018, 10);
/*  93 */     codes[41] = new HuffmanCode(1019, 10);
/*  94 */     codes[42] = new HuffmanCode(249, 8);
/*  95 */     codes[43] = new HuffmanCode(2043, 11);
/*  96 */     codes[44] = new HuffmanCode(250, 8);
/*  97 */     codes[45] = new HuffmanCode(22, 6);
/*  98 */     codes[46] = new HuffmanCode(23, 6);
/*  99 */     codes[47] = new HuffmanCode(24, 6);
/* 100 */     codes[48] = new HuffmanCode(0, 5);
/* 101 */     codes[49] = new HuffmanCode(1, 5);
/* 102 */     codes[50] = new HuffmanCode(2, 5);
/* 103 */     codes[51] = new HuffmanCode(25, 6);
/* 104 */     codes[52] = new HuffmanCode(26, 6);
/* 105 */     codes[53] = new HuffmanCode(27, 6);
/* 106 */     codes[54] = new HuffmanCode(28, 6);
/* 107 */     codes[55] = new HuffmanCode(29, 6);
/* 108 */     codes[56] = new HuffmanCode(30, 6);
/* 109 */     codes[57] = new HuffmanCode(31, 6);
/* 110 */     codes[58] = new HuffmanCode(92, 7);
/* 111 */     codes[59] = new HuffmanCode(251, 8);
/* 112 */     codes[60] = new HuffmanCode(32764, 15);
/* 113 */     codes[61] = new HuffmanCode(32, 6);
/* 114 */     codes[62] = new HuffmanCode(4091, 12);
/* 115 */     codes[63] = new HuffmanCode(1020, 10);
/* 116 */     codes[64] = new HuffmanCode(8186, 13);
/* 117 */     codes[65] = new HuffmanCode(33, 6);
/* 118 */     codes[66] = new HuffmanCode(93, 7);
/* 119 */     codes[67] = new HuffmanCode(94, 7);
/* 120 */     codes[68] = new HuffmanCode(95, 7);
/* 121 */     codes[69] = new HuffmanCode(96, 7);
/* 122 */     codes[70] = new HuffmanCode(97, 7);
/* 123 */     codes[71] = new HuffmanCode(98, 7);
/* 124 */     codes[72] = new HuffmanCode(99, 7);
/* 125 */     codes[73] = new HuffmanCode(100, 7);
/* 126 */     codes[74] = new HuffmanCode(101, 7);
/* 127 */     codes[75] = new HuffmanCode(102, 7);
/* 128 */     codes[76] = new HuffmanCode(103, 7);
/* 129 */     codes[77] = new HuffmanCode(104, 7);
/* 130 */     codes[78] = new HuffmanCode(105, 7);
/* 131 */     codes[79] = new HuffmanCode(106, 7);
/* 132 */     codes[80] = new HuffmanCode(107, 7);
/* 133 */     codes[81] = new HuffmanCode(108, 7);
/* 134 */     codes[82] = new HuffmanCode(109, 7);
/* 135 */     codes[83] = new HuffmanCode(110, 7);
/* 136 */     codes[84] = new HuffmanCode(111, 7);
/* 137 */     codes[85] = new HuffmanCode(112, 7);
/* 138 */     codes[86] = new HuffmanCode(113, 7);
/* 139 */     codes[87] = new HuffmanCode(114, 7);
/* 140 */     codes[88] = new HuffmanCode(252, 8);
/* 141 */     codes[89] = new HuffmanCode(115, 7);
/* 142 */     codes[90] = new HuffmanCode(253, 8);
/* 143 */     codes[91] = new HuffmanCode(8187, 13);
/* 144 */     codes[92] = new HuffmanCode(524272, 19);
/* 145 */     codes[93] = new HuffmanCode(8188, 13);
/* 146 */     codes[94] = new HuffmanCode(16380, 14);
/* 147 */     codes[95] = new HuffmanCode(34, 6);
/* 148 */     codes[96] = new HuffmanCode(32765, 15);
/* 149 */     codes[97] = new HuffmanCode(3, 5);
/* 150 */     codes[98] = new HuffmanCode(35, 6);
/* 151 */     codes[99] = new HuffmanCode(4, 5);
/* 152 */     codes[100] = new HuffmanCode(36, 6);
/* 153 */     codes[101] = new HuffmanCode(5, 5);
/* 154 */     codes[102] = new HuffmanCode(37, 6);
/* 155 */     codes[103] = new HuffmanCode(38, 6);
/* 156 */     codes[104] = new HuffmanCode(39, 6);
/* 157 */     codes[105] = new HuffmanCode(6, 5);
/* 158 */     codes[106] = new HuffmanCode(116, 7);
/* 159 */     codes[107] = new HuffmanCode(117, 7);
/* 160 */     codes[108] = new HuffmanCode(40, 6);
/* 161 */     codes[109] = new HuffmanCode(41, 6);
/* 162 */     codes[110] = new HuffmanCode(42, 6);
/* 163 */     codes[111] = new HuffmanCode(7, 5);
/* 164 */     codes[112] = new HuffmanCode(43, 6);
/* 165 */     codes[113] = new HuffmanCode(118, 7);
/* 166 */     codes[114] = new HuffmanCode(44, 6);
/* 167 */     codes[115] = new HuffmanCode(8, 5);
/* 168 */     codes[116] = new HuffmanCode(9, 5);
/* 169 */     codes[117] = new HuffmanCode(45, 6);
/* 170 */     codes[118] = new HuffmanCode(119, 7);
/* 171 */     codes[119] = new HuffmanCode(120, 7);
/* 172 */     codes[120] = new HuffmanCode(121, 7);
/* 173 */     codes[121] = new HuffmanCode(122, 7);
/* 174 */     codes[122] = new HuffmanCode(123, 7);
/* 175 */     codes[123] = new HuffmanCode(32766, 15);
/* 176 */     codes[124] = new HuffmanCode(2044, 11);
/* 177 */     codes[125] = new HuffmanCode(16381, 14);
/* 178 */     codes[126] = new HuffmanCode(8189, 13);
/* 179 */     codes[127] = new HuffmanCode(268435452, 28);
/* 180 */     codes[128] = new HuffmanCode(1048550, 20);
/* 181 */     codes[129] = new HuffmanCode(4194258, 22);
/* 182 */     codes[130] = new HuffmanCode(1048551, 20);
/* 183 */     codes[131] = new HuffmanCode(1048552, 20);
/* 184 */     codes[132] = new HuffmanCode(4194259, 22);
/* 185 */     codes[133] = new HuffmanCode(4194260, 22);
/* 186 */     codes[134] = new HuffmanCode(4194261, 22);
/* 187 */     codes[135] = new HuffmanCode(8388569, 23);
/* 188 */     codes[136] = new HuffmanCode(4194262, 22);
/* 189 */     codes[137] = new HuffmanCode(8388570, 23);
/* 190 */     codes[138] = new HuffmanCode(8388571, 23);
/* 191 */     codes[139] = new HuffmanCode(8388572, 23);
/* 192 */     codes[140] = new HuffmanCode(8388573, 23);
/* 193 */     codes[141] = new HuffmanCode(8388574, 23);
/* 194 */     codes[142] = new HuffmanCode(16777195, 24);
/* 195 */     codes[143] = new HuffmanCode(8388575, 23);
/* 196 */     codes[144] = new HuffmanCode(16777196, 24);
/* 197 */     codes[145] = new HuffmanCode(16777197, 24);
/* 198 */     codes[146] = new HuffmanCode(4194263, 22);
/* 199 */     codes[147] = new HuffmanCode(8388576, 23);
/* 200 */     codes[148] = new HuffmanCode(16777198, 24);
/* 201 */     codes[149] = new HuffmanCode(8388577, 23);
/* 202 */     codes[150] = new HuffmanCode(8388578, 23);
/* 203 */     codes[151] = new HuffmanCode(8388579, 23);
/* 204 */     codes[152] = new HuffmanCode(8388580, 23);
/* 205 */     codes[153] = new HuffmanCode(2097116, 21);
/* 206 */     codes[154] = new HuffmanCode(4194264, 22);
/* 207 */     codes[155] = new HuffmanCode(8388581, 23);
/* 208 */     codes[156] = new HuffmanCode(4194265, 22);
/* 209 */     codes[157] = new HuffmanCode(8388582, 23);
/* 210 */     codes[158] = new HuffmanCode(8388583, 23);
/* 211 */     codes[159] = new HuffmanCode(16777199, 24);
/* 212 */     codes[160] = new HuffmanCode(4194266, 22);
/* 213 */     codes[161] = new HuffmanCode(2097117, 21);
/* 214 */     codes[162] = new HuffmanCode(1048553, 20);
/* 215 */     codes[163] = new HuffmanCode(4194267, 22);
/* 216 */     codes[164] = new HuffmanCode(4194268, 22);
/* 217 */     codes[165] = new HuffmanCode(8388584, 23);
/* 218 */     codes[166] = new HuffmanCode(8388585, 23);
/* 219 */     codes[167] = new HuffmanCode(2097118, 21);
/* 220 */     codes[168] = new HuffmanCode(8388586, 23);
/* 221 */     codes[169] = new HuffmanCode(4194269, 22);
/* 222 */     codes[170] = new HuffmanCode(4194270, 22);
/* 223 */     codes[171] = new HuffmanCode(16777200, 24);
/* 224 */     codes[172] = new HuffmanCode(2097119, 21);
/* 225 */     codes[173] = new HuffmanCode(4194271, 22);
/* 226 */     codes[174] = new HuffmanCode(8388587, 23);
/* 227 */     codes[175] = new HuffmanCode(8388588, 23);
/* 228 */     codes[176] = new HuffmanCode(2097120, 21);
/* 229 */     codes[177] = new HuffmanCode(2097121, 21);
/* 230 */     codes[178] = new HuffmanCode(4194272, 22);
/* 231 */     codes[179] = new HuffmanCode(2097122, 21);
/* 232 */     codes[180] = new HuffmanCode(8388589, 23);
/* 233 */     codes[181] = new HuffmanCode(4194273, 22);
/* 234 */     codes[182] = new HuffmanCode(8388590, 23);
/* 235 */     codes[183] = new HuffmanCode(8388591, 23);
/* 236 */     codes[184] = new HuffmanCode(1048554, 20);
/* 237 */     codes[185] = new HuffmanCode(4194274, 22);
/* 238 */     codes[186] = new HuffmanCode(4194275, 22);
/* 239 */     codes[187] = new HuffmanCode(4194276, 22);
/* 240 */     codes[188] = new HuffmanCode(8388592, 23);
/* 241 */     codes[189] = new HuffmanCode(4194277, 22);
/* 242 */     codes[190] = new HuffmanCode(4194278, 22);
/* 243 */     codes[191] = new HuffmanCode(8388593, 23);
/* 244 */     codes[192] = new HuffmanCode(67108832, 26);
/* 245 */     codes[193] = new HuffmanCode(67108833, 26);
/* 246 */     codes[194] = new HuffmanCode(1048555, 20);
/* 247 */     codes[195] = new HuffmanCode(524273, 19);
/* 248 */     codes[196] = new HuffmanCode(4194279, 22);
/* 249 */     codes[197] = new HuffmanCode(8388594, 23);
/* 250 */     codes[198] = new HuffmanCode(4194280, 22);
/* 251 */     codes[199] = new HuffmanCode(33554412, 25);
/* 252 */     codes[200] = new HuffmanCode(67108834, 26);
/* 253 */     codes[201] = new HuffmanCode(67108835, 26);
/* 254 */     codes[202] = new HuffmanCode(67108836, 26);
/* 255 */     codes[203] = new HuffmanCode(134217694, 27);
/* 256 */     codes[204] = new HuffmanCode(134217695, 27);
/* 257 */     codes[205] = new HuffmanCode(67108837, 26);
/* 258 */     codes[206] = new HuffmanCode(16777201, 24);
/* 259 */     codes[207] = new HuffmanCode(33554413, 25);
/* 260 */     codes[208] = new HuffmanCode(524274, 19);
/* 261 */     codes[209] = new HuffmanCode(2097123, 21);
/* 262 */     codes[210] = new HuffmanCode(67108838, 26);
/* 263 */     codes[211] = new HuffmanCode(134217696, 27);
/* 264 */     codes[212] = new HuffmanCode(134217697, 27);
/* 265 */     codes[213] = new HuffmanCode(67108839, 26);
/* 266 */     codes[214] = new HuffmanCode(134217698, 27);
/* 267 */     codes[215] = new HuffmanCode(16777202, 24);
/* 268 */     codes[216] = new HuffmanCode(2097124, 21);
/* 269 */     codes[217] = new HuffmanCode(2097125, 21);
/* 270 */     codes[218] = new HuffmanCode(67108840, 26);
/* 271 */     codes[219] = new HuffmanCode(67108841, 26);
/* 272 */     codes[220] = new HuffmanCode(268435453, 28);
/* 273 */     codes[221] = new HuffmanCode(134217699, 27);
/* 274 */     codes[222] = new HuffmanCode(134217700, 27);
/* 275 */     codes[223] = new HuffmanCode(134217701, 27);
/* 276 */     codes[224] = new HuffmanCode(1048556, 20);
/* 277 */     codes[225] = new HuffmanCode(16777203, 24);
/* 278 */     codes[226] = new HuffmanCode(1048557, 20);
/* 279 */     codes[227] = new HuffmanCode(2097126, 21);
/* 280 */     codes[228] = new HuffmanCode(4194281, 22);
/* 281 */     codes[229] = new HuffmanCode(2097127, 21);
/* 282 */     codes[230] = new HuffmanCode(2097128, 21);
/* 283 */     codes[231] = new HuffmanCode(8388595, 23);
/* 284 */     codes[232] = new HuffmanCode(4194282, 22);
/* 285 */     codes[233] = new HuffmanCode(4194283, 22);
/* 286 */     codes[234] = new HuffmanCode(33554414, 25);
/* 287 */     codes[235] = new HuffmanCode(33554415, 25);
/* 288 */     codes[236] = new HuffmanCode(16777204, 24);
/* 289 */     codes[237] = new HuffmanCode(16777205, 24);
/* 290 */     codes[238] = new HuffmanCode(67108842, 26);
/* 291 */     codes[239] = new HuffmanCode(8388596, 23);
/* 292 */     codes[240] = new HuffmanCode(67108843, 26);
/* 293 */     codes[241] = new HuffmanCode(134217702, 27);
/* 294 */     codes[242] = new HuffmanCode(67108844, 26);
/* 295 */     codes[243] = new HuffmanCode(67108845, 26);
/* 296 */     codes[244] = new HuffmanCode(134217703, 27);
/* 297 */     codes[245] = new HuffmanCode(134217704, 27);
/* 298 */     codes[246] = new HuffmanCode(134217705, 27);
/* 299 */     codes[247] = new HuffmanCode(134217706, 27);
/* 300 */     codes[248] = new HuffmanCode(134217707, 27);
/* 301 */     codes[249] = new HuffmanCode(268435454, 28);
/* 302 */     codes[250] = new HuffmanCode(134217708, 27);
/* 303 */     codes[251] = new HuffmanCode(134217709, 27);
/* 304 */     codes[252] = new HuffmanCode(134217710, 27);
/* 305 */     codes[253] = new HuffmanCode(134217711, 27);
/* 306 */     codes[254] = new HuffmanCode(134217712, 27);
/* 307 */     codes[255] = new HuffmanCode(67108846, 26);
/* 308 */     codes[256] = new HuffmanCode(1073741823, 30);
/* 309 */     HUFFMAN_CODES = codes;
/*     */ 
/*     */     
/* 312 */     int[] codingTree = new int[256];
/*     */     
/* 314 */     int pos = 0;
/* 315 */     int allocated = 1;
/*     */ 
/*     */     
/* 318 */     HuffmanCode[] currentCode = new HuffmanCode[256];
/* 319 */     currentCode[0] = new HuffmanCode(0, 0);
/*     */     
/* 321 */     Set<HuffmanCode> allCodes = new HashSet<>();
/* 322 */     allCodes.addAll(Arrays.asList(HUFFMAN_CODES));
/*     */     
/* 324 */     while (!allCodes.isEmpty()) {
/* 325 */       int length = (currentCode[pos]).length;
/* 326 */       int code = (currentCode[pos]).value;
/*     */       
/* 328 */       int newLength = length + 1;
/* 329 */       HuffmanCode high = new HuffmanCode(code << 1 | 0x1, newLength);
/* 330 */       HuffmanCode low = new HuffmanCode(code << 1, newLength);
/* 331 */       int newVal = 0;
/* 332 */       boolean highTerminal = allCodes.remove(high);
/* 333 */       if (highTerminal) {
/*     */         
/* 335 */         int i = 0;
/* 336 */         for (i = 0; i < codes.length && 
/* 337 */           !codes[i].equals(high); i++);
/*     */ 
/*     */ 
/*     */         
/* 341 */         newVal = 0x8000 | i;
/*     */       } else {
/* 343 */         int highPos = allocated++;
/* 344 */         currentCode[highPos] = high;
/* 345 */         newVal = highPos;
/*     */       } 
/* 347 */       newVal <<= 16;
/* 348 */       boolean lowTerminal = allCodes.remove(low);
/* 349 */       if (lowTerminal) {
/*     */         
/* 351 */         int i = 0;
/* 352 */         for (i = 0; i < codes.length && 
/* 353 */           !codes[i].equals(low); i++);
/*     */ 
/*     */ 
/*     */         
/* 357 */         newVal |= 0x8000 | i;
/*     */       } else {
/* 359 */         int lowPos = allocated++;
/* 360 */         currentCode[lowPos] = low;
/* 361 */         newVal |= lowPos;
/*     */       } 
/* 363 */       codingTree[pos] = newVal;
/* 364 */       pos++;
/*     */     } 
/* 366 */     DECODING_TABLE = codingTree;
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
/*     */   public static void decode(ByteBuffer data, int length, StringBuilder target) throws HpackException {
/* 378 */     assert data.remaining() >= length;
/* 379 */     int treePos = 0;
/* 380 */     boolean eosBits = true;
/* 381 */     int eosCount = 0;
/* 382 */     for (int i = 0; i < length; i++) {
/* 383 */       byte b = data.get();
/* 384 */       int bitPos = 7;
/* 385 */       while (bitPos >= 0) {
/* 386 */         int val = DECODING_TABLE[treePos];
/* 387 */         if ((1 << bitPos & b) == 0) {
/*     */           
/* 389 */           if ((val & 0x8000) == 0) {
/* 390 */             treePos = val & 0x7FFF;
/* 391 */             eosBits = false;
/* 392 */             eosCount = 0;
/*     */           } else {
/* 394 */             target.append((char)(val & 0x7FFF));
/* 395 */             treePos = 0;
/* 396 */             eosBits = true;
/* 397 */             eosCount = 0;
/*     */           }
/*     */         
/*     */         }
/* 401 */         else if ((val & Integer.MIN_VALUE) == 0) {
/* 402 */           treePos = val >> 16 & 0x7FFF;
/* 403 */           if (eosBits) {
/* 404 */             eosCount++;
/*     */           }
/*     */         } else {
/* 407 */           target.append((char)(val >> 16 & 0x7FFF));
/* 408 */           treePos = 0;
/* 409 */           eosCount = 0;
/* 410 */           eosBits = true;
/*     */         } 
/*     */         
/* 413 */         bitPos--;
/*     */       } 
/*     */     } 
/* 416 */     if (!eosBits || eosCount > 7) {
/* 417 */       throw UndertowMessages.MESSAGES.huffmanEncodedHpackValueDidNotEndWithEOS();
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
/*     */   public static boolean encode(ByteBuffer buffer, String toEncode, boolean forceLowercase) {
/* 432 */     if (buffer.remaining() <= toEncode.length()) {
/* 433 */       return false;
/*     */     }
/* 435 */     int start = buffer.position();
/*     */ 
/*     */ 
/*     */     
/* 439 */     int length = 0;
/* 440 */     for (int i = 0; i < toEncode.length(); i++) {
/* 441 */       byte c = (byte)toEncode.charAt(i);
/* 442 */       if (forceLowercase) {
/* 443 */         c = Hpack.toLower(c);
/*     */       }
/* 445 */       HuffmanCode code = HUFFMAN_CODES[c];
/* 446 */       length += code.length;
/*     */     } 
/* 448 */     int byteLength = length / 8 + ((length % 8 == 0) ? 0 : 1);
/*     */     
/* 450 */     buffer.put(-128);
/* 451 */     Hpack.encodeInteger(buffer, byteLength, 7);
/*     */ 
/*     */     
/* 454 */     int bytePos = 0;
/* 455 */     byte currentBufferByte = 0;
/* 456 */     for (int j = 0; j < toEncode.length(); j++) {
/* 457 */       byte c = (byte)toEncode.charAt(j);
/* 458 */       if (forceLowercase) {
/* 459 */         c = Hpack.toLower(c);
/*     */       }
/* 461 */       HuffmanCode code = HUFFMAN_CODES[c];
/* 462 */       if (code.length + bytePos <= 8) {
/*     */         
/* 464 */         currentBufferByte = (byte)(currentBufferByte | (code.value & 0xFF) << 8 - code.length + bytePos);
/* 465 */         bytePos += code.length;
/*     */       } else {
/*     */         
/* 468 */         int val = code.value;
/* 469 */         int rem = code.length;
/* 470 */         while (rem > 0) {
/* 471 */           if (!buffer.hasRemaining()) {
/* 472 */             buffer.position(start);
/* 473 */             return false;
/*     */           } 
/* 475 */           int remainingInByte = 8 - bytePos;
/* 476 */           if (rem > remainingInByte) {
/* 477 */             currentBufferByte = (byte)(currentBufferByte | val >> rem - remainingInByte);
/*     */           } else {
/* 479 */             currentBufferByte = (byte)(currentBufferByte | val << remainingInByte - rem);
/*     */           } 
/* 481 */           if (rem > remainingInByte) {
/* 482 */             buffer.put(currentBufferByte);
/* 483 */             currentBufferByte = 0;
/* 484 */             bytePos = 0;
/*     */           } else {
/* 486 */             bytePos = rem;
/*     */           } 
/* 488 */           rem -= remainingInByte;
/*     */         } 
/*     */       } 
/* 491 */       if (bytePos == 8) {
/* 492 */         if (!buffer.hasRemaining()) {
/* 493 */           buffer.position(start);
/* 494 */           return false;
/*     */         } 
/* 496 */         buffer.put(currentBufferByte);
/* 497 */         currentBufferByte = 0;
/* 498 */         bytePos = 0;
/*     */       } 
/* 500 */       if (buffer.position() - start > toEncode.length()) {
/*     */ 
/*     */         
/* 503 */         buffer.position(start);
/* 504 */         return false;
/*     */       } 
/*     */     } 
/* 507 */     if (bytePos > 0) {
/*     */       
/* 509 */       if (!buffer.hasRemaining()) {
/* 510 */         buffer.position(start);
/* 511 */         return false;
/*     */       } 
/* 513 */       buffer.put((byte)(currentBufferByte | 255 >> bytePos));
/*     */     } 
/* 515 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static class HuffmanCode
/*     */   {
/*     */     int value;
/*     */ 
/*     */     
/*     */     int length;
/*     */ 
/*     */     
/*     */     public HuffmanCode(int value, int length) {
/* 529 */       this.value = value;
/* 530 */       this.length = length;
/*     */     }
/*     */     
/*     */     public int getValue() {
/* 534 */       return this.value;
/*     */     }
/*     */     
/*     */     public int getLength() {
/* 538 */       return this.length;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 545 */       if (this == o) return true; 
/* 546 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 548 */       HuffmanCode that = (HuffmanCode)o;
/*     */       
/* 550 */       if (this.length != that.length) return false; 
/* 551 */       if (this.value != that.value) return false;
/*     */       
/* 553 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 558 */       int result = this.value;
/* 559 */       result = 31 * result + this.length;
/* 560 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 565 */       return "HuffmanCode{value=" + this.value + ", length=" + this.length + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\HPackHuffman.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */