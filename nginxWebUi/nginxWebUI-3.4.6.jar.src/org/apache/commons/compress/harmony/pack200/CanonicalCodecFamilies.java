/*     */ package org.apache.commons.compress.harmony.pack200;
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
/*     */ public class CanonicalCodecFamilies
/*     */ {
/*  27 */   public static BHSDCodec[] nonDeltaUnsignedCodecs1 = new BHSDCodec[] {
/*     */ 
/*     */       
/*  30 */       CodecEncoding.getCanonicalCodec(5), 
/*  31 */       CodecEncoding.getCanonicalCodec(9), 
/*  32 */       CodecEncoding.getCanonicalCodec(13)
/*     */     };
/*     */   
/*  35 */   public static BHSDCodec[] nonDeltaUnsignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(17), 
/*  36 */       CodecEncoding.getCanonicalCodec(20), 
/*  37 */       CodecEncoding.getCanonicalCodec(23), 
/*  38 */       CodecEncoding.getCanonicalCodec(26), 
/*  39 */       CodecEncoding.getCanonicalCodec(29) };
/*     */ 
/*     */   
/*  42 */   public static BHSDCodec[] nonDeltaUnsignedCodecs3 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(47), 
/*  43 */       CodecEncoding.getCanonicalCodec(48), 
/*  44 */       CodecEncoding.getCanonicalCodec(49), 
/*  45 */       CodecEncoding.getCanonicalCodec(50), 
/*  46 */       CodecEncoding.getCanonicalCodec(51) };
/*     */ 
/*     */   
/*  49 */   public static BHSDCodec[] nonDeltaUnsignedCodecs4 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(70), 
/*  50 */       CodecEncoding.getCanonicalCodec(71), 
/*  51 */       CodecEncoding.getCanonicalCodec(72), 
/*  52 */       CodecEncoding.getCanonicalCodec(73), 
/*  53 */       CodecEncoding.getCanonicalCodec(74) };
/*     */ 
/*     */   
/*  56 */   public static BHSDCodec[] nonDeltaUnsignedCodecs5 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(93), 
/*  57 */       CodecEncoding.getCanonicalCodec(94), 
/*  58 */       CodecEncoding.getCanonicalCodec(95), 
/*  59 */       CodecEncoding.getCanonicalCodec(96), 
/*  60 */       CodecEncoding.getCanonicalCodec(97) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   public static BHSDCodec[] deltaUnsignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(3), 
/*  67 */       CodecEncoding.getCanonicalCodec(7), 
/*  68 */       CodecEncoding.getCanonicalCodec(11), 
/*  69 */       CodecEncoding.getCanonicalCodec(15) };
/*     */ 
/*     */   
/*  72 */   public static BHSDCodec[] deltaUnsignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(32), 
/*  73 */       CodecEncoding.getCanonicalCodec(35), 
/*  74 */       CodecEncoding.getCanonicalCodec(38), 
/*  75 */       CodecEncoding.getCanonicalCodec(41), 
/*  76 */       CodecEncoding.getCanonicalCodec(44) };
/*     */ 
/*     */   
/*  79 */   public static BHSDCodec[] deltaUnsignedCodecs3 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(52), 
/*  80 */       CodecEncoding.getCanonicalCodec(54), 
/*  81 */       CodecEncoding.getCanonicalCodec(56), 
/*  82 */       CodecEncoding.getCanonicalCodec(58), 
/*  83 */       CodecEncoding.getCanonicalCodec(60), 
/*  84 */       CodecEncoding.getCanonicalCodec(62), 
/*  85 */       CodecEncoding.getCanonicalCodec(64), 
/*  86 */       CodecEncoding.getCanonicalCodec(66), 
/*  87 */       CodecEncoding.getCanonicalCodec(68) };
/*     */ 
/*     */   
/*  90 */   public static BHSDCodec[] deltaUnsignedCodecs4 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(75), 
/*  91 */       CodecEncoding.getCanonicalCodec(77), 
/*  92 */       CodecEncoding.getCanonicalCodec(79), 
/*  93 */       CodecEncoding.getCanonicalCodec(81), 
/*  94 */       CodecEncoding.getCanonicalCodec(83), 
/*  95 */       CodecEncoding.getCanonicalCodec(85), 
/*  96 */       CodecEncoding.getCanonicalCodec(87), 
/*  97 */       CodecEncoding.getCanonicalCodec(89), 
/*  98 */       CodecEncoding.getCanonicalCodec(91) };
/*     */ 
/*     */   
/* 101 */   public static BHSDCodec[] deltaUnsignedCodecs5 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(98), 
/* 102 */       CodecEncoding.getCanonicalCodec(100), 
/* 103 */       CodecEncoding.getCanonicalCodec(102), 
/* 104 */       CodecEncoding.getCanonicalCodec(104), 
/* 105 */       CodecEncoding.getCanonicalCodec(106), 
/* 106 */       CodecEncoding.getCanonicalCodec(108), 
/* 107 */       CodecEncoding.getCanonicalCodec(110), 
/* 108 */       CodecEncoding.getCanonicalCodec(112), 
/* 109 */       CodecEncoding.getCanonicalCodec(114) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   public static BHSDCodec[] deltaSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(4), 
/* 116 */       CodecEncoding.getCanonicalCodec(8), 
/* 117 */       CodecEncoding.getCanonicalCodec(12), 
/* 118 */       CodecEncoding.getCanonicalCodec(16) };
/*     */ 
/*     */   
/* 121 */   public static BHSDCodec[] deltaSignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(33), 
/* 122 */       CodecEncoding.getCanonicalCodec(36), 
/* 123 */       CodecEncoding.getCanonicalCodec(39), 
/* 124 */       CodecEncoding.getCanonicalCodec(42), 
/* 125 */       CodecEncoding.getCanonicalCodec(45) };
/*     */ 
/*     */   
/* 128 */   public static BHSDCodec[] deltaSignedCodecs3 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(53), 
/* 129 */       CodecEncoding.getCanonicalCodec(55), 
/* 130 */       CodecEncoding.getCanonicalCodec(57), 
/* 131 */       CodecEncoding.getCanonicalCodec(59), 
/* 132 */       CodecEncoding.getCanonicalCodec(61), 
/* 133 */       CodecEncoding.getCanonicalCodec(63), 
/* 134 */       CodecEncoding.getCanonicalCodec(65), 
/* 135 */       CodecEncoding.getCanonicalCodec(67), 
/* 136 */       CodecEncoding.getCanonicalCodec(69) };
/*     */ 
/*     */   
/* 139 */   public static BHSDCodec[] deltaSignedCodecs4 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(76), 
/* 140 */       CodecEncoding.getCanonicalCodec(78), 
/* 141 */       CodecEncoding.getCanonicalCodec(80), 
/* 142 */       CodecEncoding.getCanonicalCodec(82), 
/* 143 */       CodecEncoding.getCanonicalCodec(84), 
/* 144 */       CodecEncoding.getCanonicalCodec(86), 
/* 145 */       CodecEncoding.getCanonicalCodec(88), 
/* 146 */       CodecEncoding.getCanonicalCodec(90), 
/* 147 */       CodecEncoding.getCanonicalCodec(92) };
/*     */ 
/*     */   
/* 150 */   public static BHSDCodec[] deltaSignedCodecs5 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(99), 
/* 151 */       CodecEncoding.getCanonicalCodec(101), 
/* 152 */       CodecEncoding.getCanonicalCodec(103), 
/* 153 */       CodecEncoding.getCanonicalCodec(105), 
/* 154 */       CodecEncoding.getCanonicalCodec(107), 
/* 155 */       CodecEncoding.getCanonicalCodec(109), 
/* 156 */       CodecEncoding.getCanonicalCodec(111), 
/* 157 */       CodecEncoding.getCanonicalCodec(113), 
/* 158 */       CodecEncoding.getCanonicalCodec(115) };
/*     */ 
/*     */   
/* 161 */   public static BHSDCodec[] deltaDoubleSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(34), 
/* 162 */       CodecEncoding.getCanonicalCodec(37), 
/* 163 */       CodecEncoding.getCanonicalCodec(40), 
/* 164 */       CodecEncoding.getCanonicalCodec(43), 
/* 165 */       CodecEncoding.getCanonicalCodec(46) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   public static BHSDCodec[] nonDeltaSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(2), 
/* 172 */       CodecEncoding.getCanonicalCodec(6), 
/* 173 */       CodecEncoding.getCanonicalCodec(10), 
/* 174 */       CodecEncoding.getCanonicalCodec(14) };
/*     */ 
/*     */   
/* 177 */   public static BHSDCodec[] nonDeltaSignedCodecs2 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(18), 
/* 178 */       CodecEncoding.getCanonicalCodec(21), 
/* 179 */       CodecEncoding.getCanonicalCodec(24), 
/* 180 */       CodecEncoding.getCanonicalCodec(27), 
/* 181 */       CodecEncoding.getCanonicalCodec(30) };
/*     */ 
/*     */   
/* 184 */   public static BHSDCodec[] nonDeltaDoubleSignedCodecs1 = new BHSDCodec[] { CodecEncoding.getCanonicalCodec(19), 
/* 185 */       CodecEncoding.getCanonicalCodec(22), 
/* 186 */       CodecEncoding.getCanonicalCodec(25), 
/* 187 */       CodecEncoding.getCanonicalCodec(28), 
/* 188 */       CodecEncoding.getCanonicalCodec(31) };
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CanonicalCodecFamilies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */