/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode.forms;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
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
/*     */ public abstract class ByteCodeForm
/*     */ {
/*     */   protected static final boolean WIDENED = true;
/*  30 */   protected static final ByteCodeForm[] byteCodeArray = new ByteCodeForm[256];
/*  31 */   protected static final Map byteCodesByName = new HashMap<>(256);
/*     */   static {
/*  33 */     byteCodeArray[0] = new NoArgumentForm(0, "nop");
/*  34 */     byteCodeArray[1] = new NoArgumentForm(1, "aconst_null");
/*  35 */     byteCodeArray[2] = new NoArgumentForm(2, "iconst_m1");
/*  36 */     byteCodeArray[3] = new NoArgumentForm(3, "iconst_0");
/*  37 */     byteCodeArray[4] = new NoArgumentForm(4, "iconst_1");
/*  38 */     byteCodeArray[5] = new NoArgumentForm(5, "iconst_2");
/*  39 */     byteCodeArray[6] = new NoArgumentForm(6, "iconst_3");
/*  40 */     byteCodeArray[7] = new NoArgumentForm(7, "iconst_4");
/*  41 */     byteCodeArray[8] = new NoArgumentForm(8, "iconst_5");
/*  42 */     byteCodeArray[9] = new NoArgumentForm(9, "lconst_0");
/*  43 */     byteCodeArray[10] = new NoArgumentForm(10, "lconst_1");
/*  44 */     byteCodeArray[11] = new NoArgumentForm(11, "fconst_0");
/*  45 */     byteCodeArray[12] = new NoArgumentForm(12, "fconst_1");
/*  46 */     byteCodeArray[13] = new NoArgumentForm(13, "fconst_2");
/*  47 */     byteCodeArray[14] = new NoArgumentForm(14, "dconst_0");
/*  48 */     byteCodeArray[15] = new NoArgumentForm(15, "dconst_1");
/*  49 */     byteCodeArray[16] = new ByteForm(16, "bipush", new int[] { 16, -1 });
/*  50 */     byteCodeArray[17] = new ShortForm(17, "sipush", new int[] { 17, -1, -1 });
/*  51 */     byteCodeArray[18] = new StringRefForm(18, "ldc", new int[] { 18, -1 });
/*  52 */     byteCodeArray[19] = new StringRefForm(19, "ldc_w", new int[] { 19, -1, -1 }, true);
/*  53 */     byteCodeArray[20] = new LongForm(20, "ldc2_w", new int[] { 20, -1, -1 });
/*  54 */     byteCodeArray[21] = new LocalForm(21, "iload", new int[] { 21, -1 });
/*  55 */     byteCodeArray[22] = new LocalForm(22, "lload", new int[] { 22, -1 });
/*  56 */     byteCodeArray[23] = new LocalForm(23, "fload", new int[] { 23, -1 });
/*  57 */     byteCodeArray[24] = new LocalForm(24, "dload", new int[] { 24, -1 });
/*  58 */     byteCodeArray[25] = new LocalForm(25, "aload", new int[] { 25, -1 });
/*  59 */     byteCodeArray[26] = new NoArgumentForm(26, "iload_0");
/*  60 */     byteCodeArray[27] = new NoArgumentForm(27, "iload_1");
/*  61 */     byteCodeArray[28] = new NoArgumentForm(28, "iload_2");
/*  62 */     byteCodeArray[29] = new NoArgumentForm(29, "iload_3");
/*  63 */     byteCodeArray[30] = new NoArgumentForm(30, "lload_0");
/*  64 */     byteCodeArray[31] = new NoArgumentForm(31, "lload_1");
/*  65 */     byteCodeArray[32] = new NoArgumentForm(32, "lload_2");
/*  66 */     byteCodeArray[33] = new NoArgumentForm(33, "lload_3");
/*  67 */     byteCodeArray[34] = new NoArgumentForm(34, "fload_0");
/*  68 */     byteCodeArray[35] = new NoArgumentForm(35, "fload_1");
/*  69 */     byteCodeArray[36] = new NoArgumentForm(36, "fload_2");
/*  70 */     byteCodeArray[37] = new NoArgumentForm(37, "fload_3");
/*  71 */     byteCodeArray[38] = new NoArgumentForm(38, "dload_0");
/*  72 */     byteCodeArray[39] = new NoArgumentForm(39, "dload_1");
/*  73 */     byteCodeArray[40] = new NoArgumentForm(40, "dload_2");
/*  74 */     byteCodeArray[41] = new NoArgumentForm(41, "dload_3");
/*  75 */     byteCodeArray[42] = new NoArgumentForm(42, "aload_0");
/*  76 */     byteCodeArray[43] = new NoArgumentForm(43, "aload_1");
/*  77 */     byteCodeArray[44] = new NoArgumentForm(44, "aload_2");
/*  78 */     byteCodeArray[45] = new NoArgumentForm(45, "aload_3");
/*  79 */     byteCodeArray[46] = new NoArgumentForm(46, "iaload");
/*  80 */     byteCodeArray[47] = new NoArgumentForm(47, "laload");
/*  81 */     byteCodeArray[48] = new NoArgumentForm(48, "faload");
/*  82 */     byteCodeArray[49] = new NoArgumentForm(49, "daload");
/*  83 */     byteCodeArray[50] = new NoArgumentForm(50, "aaload");
/*  84 */     byteCodeArray[51] = new NoArgumentForm(51, "baload");
/*  85 */     byteCodeArray[52] = new NoArgumentForm(52, "caload");
/*  86 */     byteCodeArray[53] = new NoArgumentForm(53, "saload");
/*  87 */     byteCodeArray[54] = new LocalForm(54, "istore", new int[] { 54, -1 });
/*  88 */     byteCodeArray[55] = new LocalForm(55, "lstore", new int[] { 55, -1 });
/*  89 */     byteCodeArray[56] = new LocalForm(56, "fstore", new int[] { 56, -1 });
/*  90 */     byteCodeArray[57] = new LocalForm(57, "dstore", new int[] { 57, -1 });
/*  91 */     byteCodeArray[58] = new LocalForm(58, "astore", new int[] { 58, -1 });
/*  92 */     byteCodeArray[59] = new NoArgumentForm(59, "istore_0");
/*  93 */     byteCodeArray[60] = new NoArgumentForm(60, "istore_1");
/*  94 */     byteCodeArray[61] = new NoArgumentForm(61, "istore_2");
/*  95 */     byteCodeArray[62] = new NoArgumentForm(62, "istore_3");
/*  96 */     byteCodeArray[63] = new NoArgumentForm(63, "lstore_0");
/*  97 */     byteCodeArray[64] = new NoArgumentForm(64, "lstore_1");
/*  98 */     byteCodeArray[65] = new NoArgumentForm(65, "lstore_2");
/*  99 */     byteCodeArray[66] = new NoArgumentForm(66, "lstore_3");
/* 100 */     byteCodeArray[67] = new NoArgumentForm(67, "fstore_0");
/* 101 */     byteCodeArray[68] = new NoArgumentForm(68, "fstore_1");
/* 102 */     byteCodeArray[69] = new NoArgumentForm(69, "fstore_2");
/* 103 */     byteCodeArray[70] = new NoArgumentForm(70, "fstore_3");
/* 104 */     byteCodeArray[71] = new NoArgumentForm(71, "dstore_0");
/* 105 */     byteCodeArray[72] = new NoArgumentForm(72, "dstore_1");
/* 106 */     byteCodeArray[73] = new NoArgumentForm(73, "dstore_2");
/* 107 */     byteCodeArray[74] = new NoArgumentForm(74, "dstore_3");
/* 108 */     byteCodeArray[75] = new NoArgumentForm(75, "astore_0");
/* 109 */     byteCodeArray[76] = new NoArgumentForm(76, "astore_1");
/* 110 */     byteCodeArray[77] = new NoArgumentForm(77, "astore_2");
/* 111 */     byteCodeArray[78] = new NoArgumentForm(78, "astore_3");
/* 112 */     byteCodeArray[79] = new NoArgumentForm(79, "iastore");
/* 113 */     byteCodeArray[80] = new NoArgumentForm(80, "lastore");
/* 114 */     byteCodeArray[81] = new NoArgumentForm(81, "fastore");
/* 115 */     byteCodeArray[82] = new NoArgumentForm(82, "dastore");
/* 116 */     byteCodeArray[83] = new NoArgumentForm(83, "aastore");
/* 117 */     byteCodeArray[84] = new NoArgumentForm(84, "bastore");
/* 118 */     byteCodeArray[85] = new NoArgumentForm(85, "castore");
/* 119 */     byteCodeArray[86] = new NoArgumentForm(86, "sastore");
/* 120 */     byteCodeArray[87] = new NoArgumentForm(87, "pop");
/* 121 */     byteCodeArray[88] = new NoArgumentForm(88, "pop2");
/* 122 */     byteCodeArray[89] = new NoArgumentForm(89, "dup");
/* 123 */     byteCodeArray[90] = new NoArgumentForm(90, "dup_x1");
/* 124 */     byteCodeArray[91] = new NoArgumentForm(91, "dup_x2");
/* 125 */     byteCodeArray[92] = new NoArgumentForm(92, "dup2");
/* 126 */     byteCodeArray[93] = new NoArgumentForm(93, "dup2_x1");
/* 127 */     byteCodeArray[94] = new NoArgumentForm(94, "dup2_x2");
/* 128 */     byteCodeArray[95] = new NoArgumentForm(95, "swap");
/* 129 */     byteCodeArray[96] = new NoArgumentForm(96, "iadd");
/* 130 */     byteCodeArray[97] = new NoArgumentForm(97, "ladd");
/* 131 */     byteCodeArray[98] = new NoArgumentForm(98, "fadd");
/* 132 */     byteCodeArray[99] = new NoArgumentForm(99, "dadd");
/* 133 */     byteCodeArray[100] = new NoArgumentForm(100, "isub");
/* 134 */     byteCodeArray[101] = new NoArgumentForm(101, "lsub");
/* 135 */     byteCodeArray[102] = new NoArgumentForm(102, "fsub");
/* 136 */     byteCodeArray[103] = new NoArgumentForm(103, "dsub");
/* 137 */     byteCodeArray[104] = new NoArgumentForm(104, "imul");
/* 138 */     byteCodeArray[105] = new NoArgumentForm(105, "lmul");
/* 139 */     byteCodeArray[106] = new NoArgumentForm(106, "fmul");
/* 140 */     byteCodeArray[107] = new NoArgumentForm(107, "dmul");
/* 141 */     byteCodeArray[108] = new NoArgumentForm(108, "idiv");
/* 142 */     byteCodeArray[109] = new NoArgumentForm(109, "ldiv");
/* 143 */     byteCodeArray[110] = new NoArgumentForm(110, "fdiv");
/* 144 */     byteCodeArray[111] = new NoArgumentForm(111, "ddiv");
/* 145 */     byteCodeArray[112] = new NoArgumentForm(112, "irem");
/* 146 */     byteCodeArray[113] = new NoArgumentForm(113, "lrem");
/* 147 */     byteCodeArray[114] = new NoArgumentForm(114, "frem");
/* 148 */     byteCodeArray[115] = new NoArgumentForm(115, "drem");
/* 149 */     byteCodeArray[116] = new NoArgumentForm(116, "");
/* 150 */     byteCodeArray[117] = new NoArgumentForm(117, "lneg");
/* 151 */     byteCodeArray[118] = new NoArgumentForm(118, "fneg");
/* 152 */     byteCodeArray[119] = new NoArgumentForm(119, "dneg");
/* 153 */     byteCodeArray[120] = new NoArgumentForm(120, "ishl");
/* 154 */     byteCodeArray[121] = new NoArgumentForm(121, "lshl");
/* 155 */     byteCodeArray[122] = new NoArgumentForm(122, "ishr");
/* 156 */     byteCodeArray[123] = new NoArgumentForm(123, "lshr");
/* 157 */     byteCodeArray[124] = new NoArgumentForm(124, "iushr");
/* 158 */     byteCodeArray[125] = new NoArgumentForm(125, "lushr");
/* 159 */     byteCodeArray[126] = new NoArgumentForm(126, "iand");
/* 160 */     byteCodeArray[127] = new NoArgumentForm(127, "land");
/* 161 */     byteCodeArray[128] = new NoArgumentForm(128, "ior");
/* 162 */     byteCodeArray[129] = new NoArgumentForm(129, "lor");
/* 163 */     byteCodeArray[130] = new NoArgumentForm(130, "ixor");
/* 164 */     byteCodeArray[131] = new NoArgumentForm(131, "lxor");
/* 165 */     byteCodeArray[132] = new IincForm(132, "iinc", new int[] { 132, -1, -1 });
/* 166 */     byteCodeArray[133] = new NoArgumentForm(133, "i2l");
/* 167 */     byteCodeArray[134] = new NoArgumentForm(134, "i2f");
/* 168 */     byteCodeArray[135] = new NoArgumentForm(135, "i2d");
/* 169 */     byteCodeArray[136] = new NoArgumentForm(136, "l2i");
/* 170 */     byteCodeArray[137] = new NoArgumentForm(137, "l2f");
/* 171 */     byteCodeArray[138] = new NoArgumentForm(138, "l2d");
/* 172 */     byteCodeArray[139] = new NoArgumentForm(139, "f2i");
/* 173 */     byteCodeArray[140] = new NoArgumentForm(140, "f2l");
/* 174 */     byteCodeArray[141] = new NoArgumentForm(141, "f2d");
/* 175 */     byteCodeArray[142] = new NoArgumentForm(142, "d2i");
/* 176 */     byteCodeArray[143] = new NoArgumentForm(143, "d2l");
/* 177 */     byteCodeArray[144] = new NoArgumentForm(144, "d2f");
/* 178 */     byteCodeArray[145] = new NoArgumentForm(145, "i2b");
/* 179 */     byteCodeArray[146] = new NoArgumentForm(146, "i2c");
/* 180 */     byteCodeArray[147] = new NoArgumentForm(147, "i2s");
/* 181 */     byteCodeArray[148] = new NoArgumentForm(148, "lcmp");
/* 182 */     byteCodeArray[149] = new NoArgumentForm(149, "fcmpl");
/* 183 */     byteCodeArray[150] = new NoArgumentForm(150, "fcmpg");
/* 184 */     byteCodeArray[151] = new NoArgumentForm(151, "dcmpl");
/* 185 */     byteCodeArray[152] = new NoArgumentForm(152, "dcmpg");
/* 186 */     byteCodeArray[153] = new LabelForm(153, "ifeq", new int[] { 153, -1, -1 });
/* 187 */     byteCodeArray[154] = new LabelForm(154, "ifne", new int[] { 154, -1, -1 });
/* 188 */     byteCodeArray[155] = new LabelForm(155, "iflt", new int[] { 155, -1, -1 });
/* 189 */     byteCodeArray[156] = new LabelForm(156, "ifge", new int[] { 156, -1, -1 });
/* 190 */     byteCodeArray[157] = new LabelForm(157, "ifgt", new int[] { 157, -1, -1 });
/* 191 */     byteCodeArray[158] = new LabelForm(158, "ifle", new int[] { 158, -1, -1 });
/* 192 */     byteCodeArray[159] = new LabelForm(159, "if_icmpeq", new int[] { 159, -1, -1 });
/* 193 */     byteCodeArray[160] = new LabelForm(160, "if_icmpne", new int[] { 160, -1, -1 });
/* 194 */     byteCodeArray[161] = new LabelForm(161, "if_icmplt", new int[] { 161, -1, -1 });
/* 195 */     byteCodeArray[162] = new LabelForm(162, "if_icmpge", new int[] { 162, -1, -1 });
/* 196 */     byteCodeArray[163] = new LabelForm(163, "if_icmpgt", new int[] { 163, -1, -1 });
/* 197 */     byteCodeArray[164] = new LabelForm(164, "if_icmple", new int[] { 164, -1, -1 });
/* 198 */     byteCodeArray[165] = new LabelForm(165, "if_acmpeq", new int[] { 165, -1, -1 });
/* 199 */     byteCodeArray[166] = new LabelForm(166, "if_acmpne", new int[] { 166, -1, -1 });
/* 200 */     byteCodeArray[167] = new LabelForm(167, "goto", new int[] { 167, -1, -1 });
/* 201 */     byteCodeArray[168] = new LabelForm(168, "jsr", new int[] { 168, -1, -1 });
/* 202 */     byteCodeArray[169] = new LocalForm(169, "ret", new int[] { 169, -1 });
/* 203 */     byteCodeArray[170] = new TableSwitchForm(170, "tableswitch");
/* 204 */     byteCodeArray[171] = new LookupSwitchForm(171, "lookupswitch");
/* 205 */     byteCodeArray[172] = new NoArgumentForm(172, "ireturn");
/* 206 */     byteCodeArray[173] = new NoArgumentForm(173, "lreturn");
/* 207 */     byteCodeArray[174] = new NoArgumentForm(174, "freturn");
/* 208 */     byteCodeArray[175] = new NoArgumentForm(175, "dreturn");
/* 209 */     byteCodeArray[176] = new NoArgumentForm(176, "areturn");
/* 210 */     byteCodeArray[177] = new NoArgumentForm(177, "return");
/* 211 */     byteCodeArray[178] = new FieldRefForm(178, "getstatic", new int[] { 178, -1, -1 });
/* 212 */     byteCodeArray[179] = new FieldRefForm(179, "putstatic", new int[] { 179, -1, -1 });
/* 213 */     byteCodeArray[180] = new FieldRefForm(180, "getfield", new int[] { 180, -1, -1 });
/* 214 */     byteCodeArray[181] = new FieldRefForm(181, "putfield", new int[] { 181, -1, -1 });
/* 215 */     byteCodeArray[182] = new MethodRefForm(182, "invokevirtual", new int[] { 182, -1, -1 });
/* 216 */     byteCodeArray[183] = new MethodRefForm(183, "invokespecial", new int[] { 183, -1, -1 });
/* 217 */     byteCodeArray[184] = new MethodRefForm(184, "invokestatic", new int[] { 184, -1, -1 });
/* 218 */     byteCodeArray[185] = new IMethodRefForm(185, "invokeinterface", new int[] { 185, -1, -1, -1, 0 });
/* 219 */     byteCodeArray[186] = new NoArgumentForm(186, "xxxunusedxxx");
/* 220 */     byteCodeArray[187] = new NewClassRefForm(187, "new", new int[] { 187, -1, -1 });
/* 221 */     byteCodeArray[188] = new ByteForm(188, "newarray", new int[] { 188, -1 });
/* 222 */     byteCodeArray[189] = new ClassRefForm(189, "anewarray", new int[] { 189, -1, -1 });
/* 223 */     byteCodeArray[190] = new NoArgumentForm(190, "arraylength");
/* 224 */     byteCodeArray[191] = new NoArgumentForm(191, "athrow");
/* 225 */     byteCodeArray[192] = new ClassRefForm(192, "checkcast", new int[] { 192, -1, -1 });
/* 226 */     byteCodeArray[193] = new ClassRefForm(193, "instanceof", new int[] { 193, -1, -1 });
/* 227 */     byteCodeArray[194] = new NoArgumentForm(194, "monitorenter");
/* 228 */     byteCodeArray[195] = new NoArgumentForm(195, "monitorexit");
/* 229 */     byteCodeArray[196] = new WideForm(196, "wide");
/* 230 */     byteCodeArray[197] = new MultiANewArrayForm(197, "multianewarray", new int[] { 197, -1, -1, -1 });
/* 231 */     byteCodeArray[198] = new LabelForm(198, "ifnull", new int[] { 198, -1, -1 });
/* 232 */     byteCodeArray[199] = new LabelForm(199, "ifnonnull", new int[] { 199, -1, -1 });
/* 233 */     byteCodeArray[200] = new LabelForm(200, "goto_w", new int[] { 200, -1, -1, -1, -1 }, true);
/* 234 */     byteCodeArray[201] = new LabelForm(201, "jsr_w", new int[] { 201, -1, -1, -1, -1 }, true);
/*     */ 
/*     */     
/* 237 */     byteCodeArray[202] = new ThisFieldRefForm(202, "getstatic_this", new int[] { 178, -1, -1 });
/* 238 */     byteCodeArray[203] = new ThisFieldRefForm(203, "putstatic_this", new int[] { 179, -1, -1 });
/* 239 */     byteCodeArray[204] = new ThisFieldRefForm(204, "getfield_this", new int[] { 180, -1, -1 });
/* 240 */     byteCodeArray[205] = new ThisFieldRefForm(205, "putfield_this", new int[] { 181, -1, -1 });
/* 241 */     byteCodeArray[206] = new ThisMethodRefForm(206, "invokevirtual_this", new int[] { 182, -1, -1 });
/* 242 */     byteCodeArray[207] = new ThisMethodRefForm(207, "invokespecial_this", new int[] { 183, -1, -1 });
/* 243 */     byteCodeArray[208] = new ThisMethodRefForm(208, "invokestatic_this", new int[] { 184, -1, -1 });
/* 244 */     byteCodeArray[209] = new ThisFieldRefForm(209, "aload_0_getstatic_this", new int[] { 42, 178, -1, -1 });
/* 245 */     byteCodeArray[210] = new ThisFieldRefForm(210, "aload_0_putstatic_this", new int[] { 42, 179, -1, -1 });
/* 246 */     byteCodeArray[211] = new ThisFieldRefForm(211, "aload_0_getfield_this", new int[] { 42, 180, -1, -1 });
/* 247 */     byteCodeArray[212] = new ThisFieldRefForm(212, "aload_0_putfield_this", new int[] { 42, 181, -1, -1 });
/* 248 */     byteCodeArray[213] = new ThisMethodRefForm(213, "aload_0_invokevirtual_this", new int[] { 42, 182, -1, -1 });
/* 249 */     byteCodeArray[214] = new ThisMethodRefForm(214, "aload_0_invokespecial_this", new int[] { 42, 183, -1, -1 });
/* 250 */     byteCodeArray[215] = new ThisMethodRefForm(215, "aload_0_invokestatic_this", new int[] { 42, 184, -1, -1 });
/* 251 */     byteCodeArray[216] = new SuperFieldRefForm(216, "getstatic_super", new int[] { 178, -1, -1 });
/* 252 */     byteCodeArray[217] = new SuperFieldRefForm(217, "putstatic_super", new int[] { 179, -1, -1 });
/* 253 */     byteCodeArray[218] = new SuperFieldRefForm(218, "getfield_super", new int[] { 180, -1, -1 });
/* 254 */     byteCodeArray[219] = new SuperFieldRefForm(219, "putfield_super", new int[] { 181, -1, -1 });
/* 255 */     byteCodeArray[220] = new SuperMethodRefForm(220, "invokevirtual_super", new int[] { 182, -1, -1 });
/* 256 */     byteCodeArray[221] = new SuperMethodRefForm(221, "invokespecial_super", new int[] { 183, -1, -1 });
/* 257 */     byteCodeArray[222] = new SuperMethodRefForm(222, "invokestatic_super", new int[] { 184, -1, -1 });
/* 258 */     byteCodeArray[223] = new SuperFieldRefForm(223, "aload_0_getstatic_super", new int[] { 42, 178, -1, -1 });
/* 259 */     byteCodeArray[224] = new SuperFieldRefForm(224, "aload_0_putstatic_super", new int[] { 42, 179, -1, -1 });
/* 260 */     byteCodeArray[225] = new SuperFieldRefForm(225, "aload_0_getfield_super", new int[] { 42, 180, -1, -1 });
/* 261 */     byteCodeArray[226] = new SuperFieldRefForm(226, "aload_0_putfield_super", new int[] { 42, 181, -1, -1 });
/* 262 */     byteCodeArray[227] = new SuperMethodRefForm(227, "aload_0_invokevirtual_super", new int[] { 42, 182, -1, -1 });
/* 263 */     byteCodeArray[228] = new SuperMethodRefForm(228, "aload_0_invokespecial_super", new int[] { 42, 183, -1, -1 });
/* 264 */     byteCodeArray[229] = new SuperMethodRefForm(229, "aload_0_invokestatic_super", new int[] { 42, 184, -1, -1 });
/* 265 */     byteCodeArray[230] = new ThisInitMethodRefForm(230, "invokespecial_this_init", new int[] { 183, -1, -1 });
/* 266 */     byteCodeArray[231] = new SuperInitMethodRefForm(231, "invokespecial_super_init", new int[] { 183, -1, -1 });
/* 267 */     byteCodeArray[232] = new NewInitMethodRefForm(232, "invokespecial_new_init", new int[] { 183, -1, -1 });
/* 268 */     byteCodeArray[233] = new NarrowClassRefForm(233, "cldc", new int[] { 18, -1 });
/* 269 */     byteCodeArray[234] = new IntRefForm(234, "ildc", new int[] { 18, -1 });
/* 270 */     byteCodeArray[235] = new FloatRefForm(235, "fldc", new int[] { 18, -1 });
/* 271 */     byteCodeArray[236] = new NarrowClassRefForm(236, "cldc_w", new int[] { 19, -1, -1 }, true);
/* 272 */     byteCodeArray[237] = new IntRefForm(237, "ildc_w", new int[] { 19, -1, -1 }, true);
/* 273 */     byteCodeArray[238] = new FloatRefForm(238, "fldc_w", new int[] { 19, -1, -1 }, true);
/* 274 */     byteCodeArray[239] = new DoubleForm(239, "dldc2_w", new int[] { 20, -1, -1 });
/*     */ 
/*     */     
/* 277 */     byteCodeArray[254] = new NoArgumentForm(254, "impdep1");
/* 278 */     byteCodeArray[255] = new NoArgumentForm(255, "impdep2");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 286 */     for (int i = 0; i < byteCodeArray.length; i++) {
/* 287 */       ByteCodeForm byteCode = byteCodeArray[i];
/* 288 */       if (byteCode != null) {
/* 289 */         byteCodesByName.put(byteCode.getName(), byteCode);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private final int opcode;
/*     */   
/*     */   private final String name;
/*     */   
/*     */   private final int[] rewrite;
/*     */   
/*     */   private int firstOperandIndex;
/*     */   
/*     */   private int operandLength;
/*     */ 
/*     */   
/*     */   public ByteCodeForm(int opcode, String name) {
/* 307 */     this(opcode, name, new int[] { opcode });
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
/*     */   public ByteCodeForm(int opcode, String name, int[] rewrite) {
/* 319 */     this.opcode = opcode;
/* 320 */     this.name = name;
/* 321 */     this.rewrite = rewrite;
/* 322 */     calculateOperandPosition();
/*     */   }
/*     */   
/*     */   protected void calculateOperandPosition() {
/* 326 */     this.firstOperandIndex = -1;
/* 327 */     this.operandLength = -1;
/*     */ 
/*     */     
/* 330 */     int iterationIndex = 0;
/* 331 */     while (iterationIndex < this.rewrite.length) {
/* 332 */       if (this.rewrite[iterationIndex] < 0) {
/*     */         
/* 334 */         this.firstOperandIndex = iterationIndex;
/* 335 */         iterationIndex = this.rewrite.length; continue;
/*     */       } 
/* 337 */       iterationIndex++;
/*     */     } 
/*     */ 
/*     */     
/* 341 */     if (this.firstOperandIndex == -1) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 347 */     int lastOperandIndex = -1;
/* 348 */     iterationIndex = this.firstOperandIndex;
/* 349 */     while (iterationIndex < this.rewrite.length) {
/* 350 */       if (this.rewrite[iterationIndex] < 0) {
/* 351 */         lastOperandIndex = iterationIndex;
/*     */       }
/* 353 */       iterationIndex++;
/*     */     } 
/*     */ 
/*     */     
/* 357 */     int difference = lastOperandIndex - this.firstOperandIndex;
/*     */ 
/*     */     
/* 360 */     if (difference < 0) {
/* 361 */       throw new Error("Logic error: not finding rewrite operands correctly");
/*     */     }
/* 363 */     this.operandLength = difference + 1;
/*     */   }
/*     */   
/*     */   public static ByteCodeForm get(int opcode) {
/* 367 */     return byteCodeArray[opcode];
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 372 */     return getClass().getName() + "(" + getName() + ")";
/*     */   }
/*     */   
/*     */   public int getOpcode() {
/* 376 */     return this.opcode;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 380 */     return this.name;
/*     */   }
/*     */   
/*     */   public int[] getRewrite() {
/* 384 */     return this.rewrite;
/*     */   }
/*     */   
/*     */   public int[] getRewriteCopy() {
/* 388 */     int[] result = new int[this.rewrite.length];
/* 389 */     System.arraycopy(this.rewrite, 0, result, 0, this.rewrite.length);
/* 390 */     return result;
/*     */   }
/*     */   
/*     */   public int firstOperandIndex() {
/* 394 */     return this.firstOperandIndex;
/*     */   }
/*     */   
/*     */   public int operandLength() {
/* 398 */     return this.operandLength;
/*     */   }
/*     */   
/*     */   public boolean hasNoOperand() {
/* 402 */     return false;
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
/*     */   public boolean hasMultipleByteCodes() {
/* 414 */     if (this.rewrite.length > 1 && this.rewrite[0] == 42)
/*     */     {
/*     */ 
/*     */       
/* 418 */       return (this.rewrite[1] > 0);
/*     */     }
/* 420 */     return false;
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
/*     */   public void fixUpByteCodeTargets(ByteCode byteCode, CodeAttribute codeAttribute) {}
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
/*     */   public boolean nestedMustStartClassPool() {
/* 446 */     return false;
/*     */   }
/*     */   
/*     */   public abstract void setByteCodeOperands(ByteCode paramByteCode, OperandManager paramOperandManager, int paramInt);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\forms\ByteCodeForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */