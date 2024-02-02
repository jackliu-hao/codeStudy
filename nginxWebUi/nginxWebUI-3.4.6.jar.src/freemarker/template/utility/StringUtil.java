/*      */ package freemarker.template.utility;
/*      */ 
/*      */ import freemarker.core.Environment;
/*      */ import freemarker.core.ParseException;
/*      */ import freemarker.ext.dom._ExtDomApi;
/*      */ import freemarker.template.Version;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.io.Writer;
/*      */ import java.text.ParseException;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.regex.Pattern;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StringUtil
/*      */ {
/*   45 */   private static final char[] ESCAPES = createEscapes();
/*      */   
/*   47 */   private static final char[] LT = new char[] { '&', 'l', 't', ';' };
/*   48 */   private static final char[] GT = new char[] { '&', 'g', 't', ';' };
/*   49 */   private static final char[] AMP = new char[] { '&', 'a', 'm', 'p', ';' };
/*   50 */   private static final char[] QUOT = new char[] { '&', 'q', 'u', 'o', 't', ';' };
/*   51 */   private static final char[] HTML_APOS = new char[] { '&', '#', '3', '9', ';' };
/*   52 */   private static final char[] XML_APOS = new char[] { '&', 'a', 'p', 'o', 's', ';' };
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int NO_ESC = 0;
/*      */ 
/*      */   
/*      */   private static final int ESC_HEXA = 1;
/*      */ 
/*      */   
/*      */   private static final int ESC_BACKSLASH = 3;
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String HTMLEnc(String s) {
/*   68 */     return XMLEncNA(s);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String XMLEnc(String s) {
/*   76 */     return XMLOrHTMLEnc(s, true, true, XML_APOS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void XMLEnc(String s, Writer out) throws IOException {
/*   85 */     XMLOrHTMLEnc(s, XML_APOS, out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String XHTMLEnc(String s) {
/*   96 */     return XMLOrHTMLEnc(s, true, true, HTML_APOS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void XHTMLEnc(String s, Writer out) throws IOException {
/*  105 */     XMLOrHTMLEnc(s, HTML_APOS, out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String XMLOrHTMLEnc(String s, boolean escGT, boolean escQuot, char[] apos) {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual length : ()I
/*      */     //   4: istore #4
/*      */     //   6: iconst_m1
/*      */     //   7: istore #5
/*      */     //   9: iconst_0
/*      */     //   10: istore #6
/*      */     //   12: iconst_0
/*      */     //   13: istore #7
/*      */     //   15: iconst_0
/*      */     //   16: istore #8
/*      */     //   18: iload #8
/*      */     //   20: iload #4
/*      */     //   22: if_icmpge -> 205
/*      */     //   25: aload_0
/*      */     //   26: iload #8
/*      */     //   28: invokevirtual charAt : (I)C
/*      */     //   31: istore #9
/*      */     //   33: iload #9
/*      */     //   35: lookupswitch default -> 182, 34 -> 142, 38 -> 128, 39 -> 163, 60 -> 84, 62 -> 98
/*      */     //   84: iload #7
/*      */     //   86: getstatic freemarker/template/utility/StringUtil.LT : [C
/*      */     //   89: arraylength
/*      */     //   90: iconst_1
/*      */     //   91: isub
/*      */     //   92: iadd
/*      */     //   93: istore #7
/*      */     //   95: goto -> 185
/*      */     //   98: iload_1
/*      */     //   99: ifne -> 114
/*      */     //   102: aload_0
/*      */     //   103: iload #8
/*      */     //   105: invokestatic maybeCDataEndGT : (Ljava/lang/String;I)Z
/*      */     //   108: ifne -> 114
/*      */     //   111: goto -> 199
/*      */     //   114: iload #7
/*      */     //   116: getstatic freemarker/template/utility/StringUtil.GT : [C
/*      */     //   119: arraylength
/*      */     //   120: iconst_1
/*      */     //   121: isub
/*      */     //   122: iadd
/*      */     //   123: istore #7
/*      */     //   125: goto -> 185
/*      */     //   128: iload #7
/*      */     //   130: getstatic freemarker/template/utility/StringUtil.AMP : [C
/*      */     //   133: arraylength
/*      */     //   134: iconst_1
/*      */     //   135: isub
/*      */     //   136: iadd
/*      */     //   137: istore #7
/*      */     //   139: goto -> 185
/*      */     //   142: iload_2
/*      */     //   143: ifne -> 149
/*      */     //   146: goto -> 199
/*      */     //   149: iload #7
/*      */     //   151: getstatic freemarker/template/utility/StringUtil.QUOT : [C
/*      */     //   154: arraylength
/*      */     //   155: iconst_1
/*      */     //   156: isub
/*      */     //   157: iadd
/*      */     //   158: istore #7
/*      */     //   160: goto -> 185
/*      */     //   163: aload_3
/*      */     //   164: ifnonnull -> 170
/*      */     //   167: goto -> 199
/*      */     //   170: iload #7
/*      */     //   172: aload_3
/*      */     //   173: arraylength
/*      */     //   174: iconst_1
/*      */     //   175: isub
/*      */     //   176: iadd
/*      */     //   177: istore #7
/*      */     //   179: goto -> 185
/*      */     //   182: goto -> 199
/*      */     //   185: iload #5
/*      */     //   187: iconst_m1
/*      */     //   188: if_icmpne -> 195
/*      */     //   191: iload #8
/*      */     //   193: istore #5
/*      */     //   195: iload #8
/*      */     //   197: istore #6
/*      */     //   199: iinc #8, 1
/*      */     //   202: goto -> 18
/*      */     //   205: iload #5
/*      */     //   207: iconst_m1
/*      */     //   208: if_icmpne -> 213
/*      */     //   211: aload_0
/*      */     //   212: areturn
/*      */     //   213: iload #4
/*      */     //   215: iload #7
/*      */     //   217: iadd
/*      */     //   218: newarray char
/*      */     //   220: astore #8
/*      */     //   222: iload #5
/*      */     //   224: ifeq -> 237
/*      */     //   227: aload_0
/*      */     //   228: iconst_0
/*      */     //   229: iload #5
/*      */     //   231: aload #8
/*      */     //   233: iconst_0
/*      */     //   234: invokevirtual getChars : (II[CI)V
/*      */     //   237: iload #5
/*      */     //   239: istore #9
/*      */     //   241: iload #5
/*      */     //   243: istore #10
/*      */     //   245: iload #10
/*      */     //   247: iload #6
/*      */     //   249: if_icmpgt -> 431
/*      */     //   252: aload_0
/*      */     //   253: iload #10
/*      */     //   255: invokevirtual charAt : (I)C
/*      */     //   258: istore #11
/*      */     //   260: iload #11
/*      */     //   262: lookupswitch default -> 415, 34 -> 373, 38 -> 358, 39 -> 395, 60 -> 312, 62 -> 327
/*      */     //   312: getstatic freemarker/template/utility/StringUtil.LT : [C
/*      */     //   315: aload #8
/*      */     //   317: iload #9
/*      */     //   319: invokestatic shortArrayCopy : ([C[CI)I
/*      */     //   322: istore #9
/*      */     //   324: goto -> 425
/*      */     //   327: iload_1
/*      */     //   328: ifne -> 343
/*      */     //   331: aload_0
/*      */     //   332: iload #10
/*      */     //   334: invokestatic maybeCDataEndGT : (Ljava/lang/String;I)Z
/*      */     //   337: ifne -> 343
/*      */     //   340: goto -> 415
/*      */     //   343: getstatic freemarker/template/utility/StringUtil.GT : [C
/*      */     //   346: aload #8
/*      */     //   348: iload #9
/*      */     //   350: invokestatic shortArrayCopy : ([C[CI)I
/*      */     //   353: istore #9
/*      */     //   355: goto -> 425
/*      */     //   358: getstatic freemarker/template/utility/StringUtil.AMP : [C
/*      */     //   361: aload #8
/*      */     //   363: iload #9
/*      */     //   365: invokestatic shortArrayCopy : ([C[CI)I
/*      */     //   368: istore #9
/*      */     //   370: goto -> 425
/*      */     //   373: iload_2
/*      */     //   374: ifne -> 380
/*      */     //   377: goto -> 415
/*      */     //   380: getstatic freemarker/template/utility/StringUtil.QUOT : [C
/*      */     //   383: aload #8
/*      */     //   385: iload #9
/*      */     //   387: invokestatic shortArrayCopy : ([C[CI)I
/*      */     //   390: istore #9
/*      */     //   392: goto -> 425
/*      */     //   395: aload_3
/*      */     //   396: ifnonnull -> 402
/*      */     //   399: goto -> 415
/*      */     //   402: aload_3
/*      */     //   403: aload #8
/*      */     //   405: iload #9
/*      */     //   407: invokestatic shortArrayCopy : ([C[CI)I
/*      */     //   410: istore #9
/*      */     //   412: goto -> 425
/*      */     //   415: aload #8
/*      */     //   417: iload #9
/*      */     //   419: iinc #9, 1
/*      */     //   422: iload #11
/*      */     //   424: castore
/*      */     //   425: iinc #10, 1
/*      */     //   428: goto -> 245
/*      */     //   431: iload #6
/*      */     //   433: iload #4
/*      */     //   435: iconst_1
/*      */     //   436: isub
/*      */     //   437: if_icmpeq -> 454
/*      */     //   440: aload_0
/*      */     //   441: iload #6
/*      */     //   443: iconst_1
/*      */     //   444: iadd
/*      */     //   445: iload #4
/*      */     //   447: aload #8
/*      */     //   449: iload #9
/*      */     //   451: invokevirtual getChars : (II[CI)V
/*      */     //   454: aload #8
/*      */     //   456: invokestatic valueOf : ([C)Ljava/lang/String;
/*      */     //   459: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #109	-> 0
/*      */     //   #112	-> 6
/*      */     //   #113	-> 9
/*      */     //   #114	-> 12
/*      */     //   #115	-> 15
/*      */     //   #117	-> 25
/*      */     //   #118	-> 33
/*      */     //   #120	-> 84
/*      */     //   #121	-> 95
/*      */     //   #123	-> 98
/*      */     //   #124	-> 111
/*      */     //   #126	-> 114
/*      */     //   #127	-> 125
/*      */     //   #129	-> 128
/*      */     //   #130	-> 139
/*      */     //   #132	-> 142
/*      */     //   #133	-> 146
/*      */     //   #135	-> 149
/*      */     //   #136	-> 160
/*      */     //   #138	-> 163
/*      */     //   #139	-> 167
/*      */     //   #141	-> 170
/*      */     //   #142	-> 179
/*      */     //   #144	-> 182
/*      */     //   #147	-> 185
/*      */     //   #148	-> 191
/*      */     //   #150	-> 195
/*      */     //   #115	-> 199
/*      */     //   #154	-> 205
/*      */     //   #155	-> 211
/*      */     //   #157	-> 213
/*      */     //   #158	-> 222
/*      */     //   #159	-> 227
/*      */     //   #161	-> 237
/*      */     //   #162	-> 241
/*      */     //   #163	-> 252
/*      */     //   #164	-> 260
/*      */     //   #166	-> 312
/*      */     //   #167	-> 324
/*      */     //   #169	-> 327
/*      */     //   #170	-> 340
/*      */     //   #172	-> 343
/*      */     //   #173	-> 355
/*      */     //   #175	-> 358
/*      */     //   #176	-> 370
/*      */     //   #178	-> 373
/*      */     //   #179	-> 377
/*      */     //   #181	-> 380
/*      */     //   #182	-> 392
/*      */     //   #184	-> 395
/*      */     //   #185	-> 399
/*      */     //   #187	-> 402
/*      */     //   #188	-> 412
/*      */     //   #190	-> 415
/*      */     //   #162	-> 425
/*      */     //   #192	-> 431
/*      */     //   #193	-> 440
/*      */     //   #196	-> 454
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   33	166	9	c	C
/*      */     //   18	187	8	i	I
/*      */     //   260	165	11	c	C
/*      */     //   245	186	10	i	I
/*      */     //   222	238	8	esced	[C
/*      */     //   241	219	9	dst	I
/*      */     //   0	460	0	s	Ljava/lang/String;
/*      */     //   0	460	1	escGT	Z
/*      */     //   0	460	2	escQuot	Z
/*      */     //   0	460	3	apos	[C
/*      */     //   6	454	4	ln	I
/*      */     //   9	451	5	firstEscIdx	I
/*      */     //   12	448	6	lastEscIdx	I
/*      */     //   15	445	7	plusOutLn	I
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean maybeCDataEndGT(String s, int i) {
/*  201 */     if (i == 0) return true; 
/*  202 */     if (s.charAt(i - 1) != ']') return false; 
/*  203 */     if (i == 1 || s.charAt(i - 2) == ']') return true; 
/*  204 */     return false;
/*      */   }
/*      */   
/*      */   private static void XMLOrHTMLEnc(String s, char[] apos, Writer out) throws IOException {
/*  208 */     int writtenEnd = 0;
/*  209 */     int ln = s.length();
/*  210 */     for (int i = 0; i < ln; i++) {
/*  211 */       char c = s.charAt(i);
/*  212 */       if (c == '<' || c == '>' || c == '&' || c == '"' || c == '\'') {
/*  213 */         int flushLn = i - writtenEnd;
/*  214 */         if (flushLn != 0) {
/*  215 */           out.write(s, writtenEnd, flushLn);
/*      */         }
/*  217 */         writtenEnd = i + 1;
/*      */         
/*  219 */         switch (c) { case '<':
/*  220 */             out.write(LT); break;
/*  221 */           case '>': out.write(GT); break;
/*  222 */           case '&': out.write(AMP); break;
/*  223 */           case '"': out.write(QUOT); break;
/*  224 */           default: out.write(apos); break; }
/*      */       
/*      */       } 
/*      */     } 
/*  228 */     if (writtenEnd < ln) {
/*  229 */       out.write(s, writtenEnd, ln - writtenEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int shortArrayCopy(char[] src, char[] dst, int dstOffset) {
/*  237 */     int ln = src.length;
/*  238 */     for (int i = 0; i < ln; i++) {
/*  239 */       dst[dstOffset++] = src[i];
/*      */     }
/*  241 */     return dstOffset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String XMLEncNA(String s) {
/*  249 */     return XMLOrHTMLEnc(s, true, true, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String XMLEncQAttr(String s) {
/*  258 */     return XMLOrHTMLEnc(s, false, true, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String XMLEncNQG(String s) {
/*  267 */     return XMLOrHTMLEnc(s, false, false, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String RTFEnc(String s) {
/*  275 */     int ln = s.length();
/*      */ 
/*      */     
/*  278 */     int firstEscIdx = -1;
/*  279 */     int lastEscIdx = 0;
/*  280 */     int plusOutLn = 0;
/*  281 */     for (int i = 0; i < ln; i++) {
/*  282 */       char c = s.charAt(i);
/*  283 */       if (c == '{' || c == '}' || c == '\\') {
/*  284 */         if (firstEscIdx == -1) {
/*  285 */           firstEscIdx = i;
/*      */         }
/*  287 */         lastEscIdx = i;
/*  288 */         plusOutLn++;
/*      */       } 
/*      */     } 
/*      */     
/*  292 */     if (firstEscIdx == -1) {
/*  293 */       return s;
/*      */     }
/*  295 */     char[] esced = new char[ln + plusOutLn];
/*  296 */     if (firstEscIdx != 0) {
/*  297 */       s.getChars(0, firstEscIdx, esced, 0);
/*      */     }
/*  299 */     int dst = firstEscIdx;
/*  300 */     for (int j = firstEscIdx; j <= lastEscIdx; j++) {
/*  301 */       char c = s.charAt(j);
/*  302 */       if (c == '{' || c == '}' || c == '\\') {
/*  303 */         esced[dst++] = '\\';
/*      */       }
/*  305 */       esced[dst++] = c;
/*      */     } 
/*  307 */     if (lastEscIdx != ln - 1) {
/*  308 */       s.getChars(lastEscIdx + 1, ln, esced, dst);
/*      */     }
/*      */     
/*  311 */     return String.valueOf(esced);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void RTFEnc(String s, Writer out) throws IOException {
/*  321 */     int writtenEnd = 0;
/*  322 */     int ln = s.length();
/*  323 */     for (int i = 0; i < ln; i++) {
/*  324 */       char c = s.charAt(i);
/*  325 */       if (c == '{' || c == '}' || c == '\\') {
/*  326 */         int flushLn = i - writtenEnd;
/*  327 */         if (flushLn != 0) {
/*  328 */           out.write(s, writtenEnd, flushLn);
/*      */         }
/*  330 */         out.write(92);
/*  331 */         writtenEnd = i;
/*      */       } 
/*      */     } 
/*  334 */     if (writtenEnd < ln) {
/*  335 */       out.write(s, writtenEnd, ln - writtenEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String URLEnc(String s, String charset) throws UnsupportedEncodingException {
/*  345 */     return URLEnc(s, charset, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String URLPathEnc(String s, String charset) throws UnsupportedEncodingException {
/*  357 */     return URLEnc(s, charset, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private static String URLEnc(String s, String charset, boolean keepSlash) throws UnsupportedEncodingException {
/*  362 */     int ln = s.length();
/*      */     int i;
/*  364 */     for (i = 0; i < ln; i++) {
/*  365 */       char c = s.charAt(i);
/*  366 */       if (!safeInURL(c, keepSlash)) {
/*      */         break;
/*      */       }
/*      */     } 
/*  370 */     if (i == ln)
/*      */     {
/*  372 */       return s;
/*      */     }
/*      */     
/*  375 */     StringBuilder b = new StringBuilder(ln + ln / 3 + 2);
/*  376 */     b.append(s.substring(0, i));
/*      */     
/*  378 */     int encStart = i;
/*  379 */     for (; ++i < ln; i++) {
/*  380 */       char c = s.charAt(i);
/*  381 */       if (safeInURL(c, keepSlash)) {
/*  382 */         if (encStart != -1) {
/*  383 */           byte[] o = s.substring(encStart, i).getBytes(charset);
/*  384 */           for (int j = 0; j < o.length; j++) {
/*  385 */             b.append('%');
/*  386 */             byte bc = o[j];
/*  387 */             int c1 = bc & 0xF;
/*  388 */             int c2 = bc >> 4 & 0xF;
/*  389 */             b.append((char)((c2 < 10) ? (c2 + 48) : (c2 - 10 + 65)));
/*  390 */             b.append((char)((c1 < 10) ? (c1 + 48) : (c1 - 10 + 65)));
/*      */           } 
/*  392 */           encStart = -1;
/*      */         } 
/*  394 */         b.append(c);
/*      */       }
/*  396 */       else if (encStart == -1) {
/*  397 */         encStart = i;
/*      */       } 
/*      */     } 
/*      */     
/*  401 */     if (encStart != -1) {
/*  402 */       byte[] o = s.substring(encStart, i).getBytes(charset);
/*  403 */       for (int j = 0; j < o.length; j++) {
/*  404 */         b.append('%');
/*  405 */         byte bc = o[j];
/*  406 */         int c1 = bc & 0xF;
/*  407 */         int c2 = bc >> 4 & 0xF;
/*  408 */         b.append((char)((c2 < 10) ? (c2 + 48) : (c2 - 10 + 65)));
/*  409 */         b.append((char)((c1 < 10) ? (c1 + 48) : (c1 - 10 + 65)));
/*      */       } 
/*      */     } 
/*      */     
/*  413 */     return b.toString();
/*      */   }
/*      */   
/*      */   private static boolean safeInURL(char c, boolean keepSlash) {
/*  417 */     return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '.' || c == '!' || c == '~' || (c >= '\'' && c <= '*') || (keepSlash && c == '/'));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static char[] createEscapes() {
/*  425 */     char[] escapes = new char[93];
/*  426 */     for (int i = 0; i < 32; i++) {
/*  427 */       escapes[i] = '\001';
/*      */     }
/*  429 */     escapes[92] = '\\';
/*  430 */     escapes[39] = '\'';
/*  431 */     escapes[34] = '"';
/*  432 */     escapes[60] = 'l';
/*      */     
/*  434 */     escapes[62] = 'g';
/*  435 */     escapes[38] = 'a';
/*  436 */     escapes[8] = 'b';
/*  437 */     escapes[9] = 't';
/*  438 */     escapes[10] = 'n';
/*  439 */     escapes[12] = 'f';
/*  440 */     escapes[13] = 'r';
/*  441 */     return escapes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String FTLStringLiteralEnc(String s, char quotation) {
/*  456 */     return FTLStringLiteralEnc(s, quotation, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String FTLStringLiteralEnc(String s) {
/*  467 */     return FTLStringLiteralEnc(s, false, false);
/*      */   }
/*      */   private static String FTLStringLiteralEnc(String s, char quotation, boolean addQuotation) {
/*      */     char otherQuotation;
/*  471 */     int ln = s.length();
/*      */ 
/*      */     
/*  474 */     if (quotation == '\000') {
/*  475 */       otherQuotation = Character.MIN_VALUE;
/*  476 */     } else if (quotation == '"') {
/*  477 */       otherQuotation = '\'';
/*  478 */     } else if (quotation == '\'') {
/*  479 */       otherQuotation = '"';
/*      */     } else {
/*  481 */       throw new IllegalArgumentException("Unsupported quotation character: " + quotation);
/*      */     } 
/*      */     
/*  484 */     int escLn = ESCAPES.length;
/*  485 */     StringBuilder buf = null;
/*  486 */     for (int i = 0; i < ln; i++) {
/*  487 */       char escape, c = s.charAt(i);
/*      */       
/*  489 */       if (c == '=') {
/*  490 */         escape = (i > 0 && s.charAt(i - 1) == '[') ? '=' : Character.MIN_VALUE;
/*  491 */       } else if (c < escLn) {
/*  492 */         escape = ESCAPES[c];
/*  493 */       } else if (c == '{' && i > 0 && isInterpolationStart(s.charAt(i - 1))) {
/*  494 */         escape = '{';
/*      */       } else {
/*  496 */         escape = Character.MIN_VALUE;
/*      */       } 
/*  498 */       if (escape == '\000' || escape == otherQuotation) {
/*  499 */         if (buf != null) {
/*  500 */           buf.append(c);
/*      */         }
/*      */       } else {
/*  503 */         if (buf == null) {
/*  504 */           buf = new StringBuilder(s.length() + 4 + (addQuotation ? 2 : 0));
/*  505 */           if (addQuotation) {
/*  506 */             buf.append(quotation);
/*      */           }
/*  508 */           buf.append(s.substring(0, i));
/*      */         } 
/*  510 */         if (escape == '\001') {
/*      */ 
/*      */           
/*  513 */           buf.append("\\x00");
/*  514 */           int c2 = c >> 4 & 0xF;
/*  515 */           c = (char)(c & 0xF);
/*  516 */           buf.append((char)((c2 < 10) ? (c2 + 48) : (c2 - 10 + 65)));
/*  517 */           buf.append((char)((c < '\n') ? (c + 48) : (c - 10 + 65)));
/*      */         } else {
/*  519 */           buf.append('\\');
/*  520 */           buf.append(escape);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  525 */     if (buf == null) {
/*  526 */       return addQuotation ? (quotation + s + quotation) : s;
/*      */     }
/*  528 */     if (addQuotation) {
/*  529 */       buf.append(quotation);
/*      */     }
/*  531 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isInterpolationStart(char c) {
/*  536 */     return (c == '$' || c == '#');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String FTLStringLiteralDec(String s) throws ParseException {
/*  559 */     int idx = s.indexOf('\\');
/*  560 */     if (idx == -1) {
/*  561 */       return s;
/*      */     }
/*      */     
/*  564 */     int lidx = s.length() - 1;
/*  565 */     int bidx = 0;
/*  566 */     StringBuilder buf = new StringBuilder(lidx); while (true) {
/*      */       int x, y, z;
/*  568 */       buf.append(s.substring(bidx, idx));
/*  569 */       if (idx >= lidx) {
/*  570 */         throw new ParseException("The last character of string literal is backslash", 0, 0);
/*      */       }
/*  572 */       char c = s.charAt(idx + 1);
/*  573 */       switch (c) {
/*      */         case '"':
/*  575 */           buf.append('"');
/*  576 */           bidx = idx + 2;
/*      */           break;
/*      */         case '\'':
/*  579 */           buf.append('\'');
/*  580 */           bidx = idx + 2;
/*      */           break;
/*      */         case '\\':
/*  583 */           buf.append('\\');
/*  584 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'n':
/*  587 */           buf.append('\n');
/*  588 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'r':
/*  591 */           buf.append('\r');
/*  592 */           bidx = idx + 2;
/*      */           break;
/*      */         case 't':
/*  595 */           buf.append('\t');
/*  596 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'f':
/*  599 */           buf.append('\f');
/*  600 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'b':
/*  603 */           buf.append('\b');
/*  604 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'g':
/*  607 */           buf.append('>');
/*  608 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'l':
/*  611 */           buf.append('<');
/*  612 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'a':
/*  615 */           buf.append('&');
/*  616 */           bidx = idx + 2;
/*      */           break;
/*      */         case '=':
/*      */         case '{':
/*  620 */           buf.append(c);
/*  621 */           bidx = idx + 2;
/*      */           break;
/*      */         case 'x':
/*  624 */           idx += 2;
/*  625 */           x = idx;
/*  626 */           y = 0;
/*  627 */           z = (lidx > idx + 3) ? (idx + 3) : lidx;
/*  628 */           while (idx <= z) {
/*  629 */             char b = s.charAt(idx);
/*  630 */             if (b >= '0' && b <= '9') {
/*  631 */               y <<= 4;
/*  632 */               y += b - 48;
/*  633 */             } else if (b >= 'a' && b <= 'f') {
/*  634 */               y <<= 4;
/*  635 */               y += b - 97 + 10;
/*  636 */             } else if (b >= 'A' && b <= 'F') {
/*  637 */               y <<= 4;
/*  638 */               y += b - 65 + 10;
/*      */             } else {
/*      */               break;
/*      */             } 
/*  642 */             idx++;
/*      */           } 
/*  644 */           if (x < idx) {
/*  645 */             buf.append((char)y);
/*      */           } else {
/*  647 */             throw new ParseException("Invalid \\x escape in a string literal", 0, 0);
/*      */           } 
/*  649 */           bidx = idx;
/*      */           break;
/*      */         
/*      */         default:
/*  653 */           throw new ParseException("Invalid escape sequence (\\" + c + ") in a string literal", 0, 0);
/*      */       } 
/*  655 */       idx = s.indexOf('\\', bidx);
/*  656 */       if (idx == -1) {
/*  657 */         buf.append(s.substring(bidx));
/*      */         
/*  659 */         return buf.toString();
/*      */       } 
/*      */     } 
/*      */   } public static Locale deduceLocale(String input) {
/*  663 */     if (input == null) return null; 
/*  664 */     Locale locale = Locale.getDefault();
/*  665 */     if (input.length() > 0 && input.charAt(0) == '"') input = input.substring(1, input.length() - 1); 
/*  666 */     StringTokenizer st = new StringTokenizer(input, ",_ ");
/*  667 */     String lang = "", country = "";
/*  668 */     if (st.hasMoreTokens()) {
/*  669 */       lang = st.nextToken();
/*      */     }
/*  671 */     if (st.hasMoreTokens()) {
/*  672 */       country = st.nextToken();
/*      */     }
/*  674 */     if (!st.hasMoreTokens()) {
/*  675 */       locale = new Locale(lang, country);
/*      */     } else {
/*  677 */       locale = new Locale(lang, country, st.nextToken());
/*      */     } 
/*  679 */     return locale;
/*      */   }
/*      */   
/*      */   public static String capitalize(String s) {
/*  683 */     StringTokenizer st = new StringTokenizer(s, " \t\r\n", true);
/*  684 */     StringBuilder buf = new StringBuilder(s.length());
/*  685 */     while (st.hasMoreTokens()) {
/*  686 */       String tok = st.nextToken();
/*  687 */       buf.append(tok.substring(0, 1).toUpperCase());
/*  688 */       buf.append(tok.substring(1).toLowerCase());
/*      */     } 
/*  690 */     return buf.toString();
/*      */   }
/*      */   
/*      */   public static boolean getYesNo(String s) {
/*  694 */     if (s.startsWith("\"")) {
/*  695 */       s = s.substring(1, s.length() - 1);
/*      */     }
/*      */     
/*  698 */     if (s.equalsIgnoreCase("n") || s
/*  699 */       .equalsIgnoreCase("no") || s
/*  700 */       .equalsIgnoreCase("f") || s
/*  701 */       .equalsIgnoreCase("false"))
/*  702 */       return false; 
/*  703 */     if (s.equalsIgnoreCase("y") || s
/*  704 */       .equalsIgnoreCase("yes") || s
/*  705 */       .equalsIgnoreCase("t") || s
/*  706 */       .equalsIgnoreCase("true")) {
/*  707 */       return true;
/*      */     }
/*  709 */     throw new IllegalArgumentException("Illegal boolean value: " + s);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] split(String s, char c) {
/*  719 */     int ln = s.length();
/*      */     
/*  721 */     int i = 0;
/*  722 */     int cnt = 1;
/*  723 */     while ((i = s.indexOf(c, i)) != -1) {
/*  724 */       cnt++;
/*  725 */       i++;
/*      */     } 
/*  727 */     String[] res = new String[cnt];
/*      */     
/*  729 */     i = 0;
/*  730 */     int b = 0;
/*  731 */     while (b <= ln) {
/*  732 */       int e = s.indexOf(c, b);
/*  733 */       if (e == -1) e = ln; 
/*  734 */       res[i++] = s.substring(b, e);
/*  735 */       b = e + 1;
/*      */     } 
/*  737 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] split(String s, String sep, boolean caseInsensitive) {
/*  748 */     int sepLn = sep.length();
/*      */     
/*  750 */     String convertedS = caseInsensitive ? s.toLowerCase() : s;
/*  751 */     int sLn = s.length();
/*      */     
/*  753 */     if (sepLn == 0) {
/*  754 */       String[] arrayOfString = new String[sLn];
/*  755 */       for (int j = 0; j < sLn; j++) {
/*  756 */         arrayOfString[j] = String.valueOf(s.charAt(j));
/*      */       }
/*  758 */       return arrayOfString;
/*      */     } 
/*      */     
/*  761 */     String splitString = caseInsensitive ? sep.toLowerCase() : sep;
/*      */ 
/*      */ 
/*      */     
/*  765 */     int next = 0;
/*  766 */     int count = 1;
/*  767 */     while ((next = convertedS.indexOf(splitString, next)) != -1) {
/*  768 */       count++;
/*  769 */       next += sepLn;
/*      */     } 
/*  771 */     String[] res = new String[count];
/*      */ 
/*      */     
/*  774 */     int dst = 0;
/*  775 */     int i = 0;
/*  776 */     while (i <= sLn) {
/*  777 */       int end = convertedS.indexOf(splitString, i);
/*  778 */       if (end == -1) end = sLn; 
/*  779 */       res[dst++] = s.substring(i, end);
/*  780 */       i = end + sepLn;
/*      */     } 
/*  782 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(String text, String oldSub, String newSub) {
/*  790 */     return replace(text, oldSub, newSub, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String replace(String text, String oldsub, String newsub, boolean caseInsensitive, boolean firstOnly) {
/*  806 */     int oln = oldsub.length();
/*      */     
/*  808 */     if (oln == 0) {
/*  809 */       int nln = newsub.length();
/*  810 */       if (nln == 0) {
/*  811 */         return text;
/*      */       }
/*  813 */       if (firstOnly) {
/*  814 */         return newsub + text;
/*      */       }
/*  816 */       int j = text.length();
/*  817 */       StringBuilder stringBuilder = new StringBuilder(j + (j + 1) * nln);
/*  818 */       stringBuilder.append(newsub);
/*  819 */       for (int i = 0; i < j; i++) {
/*  820 */         stringBuilder.append(text.charAt(i));
/*  821 */         stringBuilder.append(newsub);
/*      */       } 
/*  823 */       return stringBuilder.toString();
/*      */     } 
/*      */ 
/*      */     
/*  827 */     oldsub = caseInsensitive ? oldsub.toLowerCase() : oldsub;
/*  828 */     String input = caseInsensitive ? text.toLowerCase() : text;
/*  829 */     int e = input.indexOf(oldsub);
/*  830 */     if (e == -1) {
/*  831 */       return text;
/*      */     }
/*  833 */     int b = 0;
/*  834 */     int tln = text.length();
/*      */     
/*  836 */     StringBuilder buf = new StringBuilder(tln + Math.max(newsub.length() - oln, 0) * 3);
/*      */     do {
/*  838 */       buf.append(text.substring(b, e));
/*  839 */       buf.append(newsub);
/*  840 */       b = e + oln;
/*  841 */       e = input.indexOf(oldsub, b);
/*  842 */     } while (e != -1 && !firstOnly);
/*  843 */     buf.append(text.substring(b));
/*  844 */     return buf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String chomp(String s) {
/*  852 */     if (s.endsWith("\r\n")) return s.substring(0, s.length() - 2); 
/*  853 */     if (s.endsWith("\r") || s.endsWith("\n"))
/*  854 */       return s.substring(0, s.length() - 1); 
/*  855 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String emptyToNull(String s) {
/*  863 */     if (s == null) return null; 
/*  864 */     return (s.length() == 0) ? null : s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String jQuote(Object obj) {
/*  872 */     return jQuote((obj != null) ? obj.toString() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String jQuote(String s) {
/*  881 */     if (s == null) {
/*  882 */       return "null";
/*      */     }
/*  884 */     int ln = s.length();
/*  885 */     StringBuilder b = new StringBuilder(ln + 4);
/*  886 */     b.append('"');
/*  887 */     for (int i = 0; i < ln; i++) {
/*  888 */       char c = s.charAt(i);
/*  889 */       if (c == '"') {
/*  890 */         b.append("\\\"");
/*  891 */       } else if (c == '\\') {
/*  892 */         b.append("\\\\");
/*  893 */       } else if (c < ' ') {
/*  894 */         if (c == '\n') {
/*  895 */           b.append("\\n");
/*  896 */         } else if (c == '\r') {
/*  897 */           b.append("\\r");
/*  898 */         } else if (c == '\f') {
/*  899 */           b.append("\\f");
/*  900 */         } else if (c == '\b') {
/*  901 */           b.append("\\b");
/*  902 */         } else if (c == '\t') {
/*  903 */           b.append("\\t");
/*      */         } else {
/*  905 */           b.append("\\u00");
/*  906 */           int x = c / 16;
/*  907 */           b.append(toHexDigit(x));
/*  908 */           x = c & 0xF;
/*  909 */           b.append(toHexDigit(x));
/*      */         } 
/*      */       } else {
/*  912 */         b.append(c);
/*      */       } 
/*      */     } 
/*  915 */     b.append('"');
/*  916 */     return b.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String jQuoteNoXSS(Object obj) {
/*  924 */     return jQuoteNoXSS((obj != null) ? obj.toString() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String jQuoteNoXSS(String s) {
/*  933 */     if (s == null) {
/*  934 */       return "null";
/*      */     }
/*  936 */     int ln = s.length();
/*  937 */     StringBuilder b = new StringBuilder(ln + 4);
/*  938 */     b.append('"');
/*  939 */     for (int i = 0; i < ln; i++) {
/*  940 */       char c = s.charAt(i);
/*  941 */       if (c == '"') {
/*  942 */         b.append("\\\"");
/*  943 */       } else if (c == '\\') {
/*  944 */         b.append("\\\\");
/*  945 */       } else if (c == '<') {
/*  946 */         b.append("\\u003C");
/*  947 */       } else if (c < ' ') {
/*  948 */         if (c == '\n') {
/*  949 */           b.append("\\n");
/*  950 */         } else if (c == '\r') {
/*  951 */           b.append("\\r");
/*  952 */         } else if (c == '\f') {
/*  953 */           b.append("\\f");
/*  954 */         } else if (c == '\b') {
/*  955 */           b.append("\\b");
/*  956 */         } else if (c == '\t') {
/*  957 */           b.append("\\t");
/*      */         } else {
/*  959 */           b.append("\\u00");
/*  960 */           int x = c / 16;
/*  961 */           b.append(toHexDigit(x));
/*  962 */           x = c & 0xF;
/*  963 */           b.append(toHexDigit(x));
/*      */         } 
/*      */       } else {
/*  966 */         b.append(c);
/*      */       } 
/*      */     } 
/*  969 */     b.append('"');
/*  970 */     return b.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String ftlQuote(String s) {
/*      */     char quotation;
/*  986 */     if (s.indexOf('"') != -1 && s.indexOf('\'') == -1) {
/*  987 */       quotation = '\'';
/*      */     } else {
/*  989 */       quotation = '"';
/*      */     } 
/*  991 */     return FTLStringLiteralEnc(s, quotation, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isFTLIdentifierStart(char c) {
/* 1001 */     if (c < 'ª') {
/* 1002 */       if ((c >= 'a' && c <= 'z') || (c >= '@' && c <= 'Z')) {
/* 1003 */         return true;
/*      */       }
/* 1005 */       return (c == '$' || c == '_');
/*      */     } 
/*      */     
/* 1008 */     if (c < 'ꟸ') {
/* 1009 */       if (c < 'ⵯ') {
/* 1010 */         if (c < 'ℨ') {
/* 1011 */           if (c < 'ₐ') {
/* 1012 */             if (c < 'Ø') {
/* 1013 */               if (c < 'º') {
/* 1014 */                 return (c == 'ª' || c == 'µ');
/*      */               }
/* 1016 */               return (c == 'º' || (c >= 'À' && c <= 'Ö'));
/*      */             } 
/*      */             
/* 1019 */             if (c < 'ⁱ') {
/* 1020 */               return ((c >= 'Ø' && c <= 'ö') || (c >= 'ø' && c <= '῿'));
/*      */             }
/* 1022 */             return (c == 'ⁱ' || c == 'ⁿ');
/*      */           } 
/*      */ 
/*      */           
/* 1026 */           if (c < 'ℕ') {
/* 1027 */             if (c < 'ℇ') {
/* 1028 */               return ((c >= 'ₐ' && c <= 'ₜ') || c == 'ℂ');
/*      */             }
/* 1030 */             return (c == 'ℇ' || (c >= 'ℊ' && c <= 'ℓ'));
/*      */           } 
/*      */           
/* 1033 */           if (c < 'ℤ') {
/* 1034 */             return (c == 'ℕ' || (c >= 'ℙ' && c <= 'ℝ'));
/*      */           }
/* 1036 */           return (c == 'ℤ' || c == 'Ω');
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1041 */         if (c < 'ⰰ') {
/* 1042 */           if (c < 'ⅅ') {
/* 1043 */             if (c < 'ℯ') {
/* 1044 */               return (c == 'ℨ' || (c >= 'K' && c <= 'ℭ'));
/*      */             }
/* 1046 */             return ((c >= 'ℯ' && c <= 'ℹ') || (c >= 'ℼ' && c <= 'ℿ'));
/*      */           } 
/*      */           
/* 1049 */           if (c < 'Ↄ') {
/* 1050 */             return ((c >= 'ⅅ' && c <= 'ⅉ') || c == 'ⅎ');
/*      */           }
/* 1052 */           return ((c >= 'Ↄ' && c <= 'ↄ') || (c >= 'Ⰰ' && c <= 'Ⱞ'));
/*      */         } 
/*      */ 
/*      */         
/* 1056 */         if (c < 'ⴀ') {
/* 1057 */           if (c < 'Ⳬ') {
/* 1058 */             return ((c >= 'ⰰ' && c <= 'ⱞ') || (c >= 'Ⱡ' && c <= 'ⳤ'));
/*      */           }
/* 1060 */           return ((c >= 'Ⳬ' && c <= 'ⳮ') || (c >= 'Ⳳ' && c <= 'ⳳ'));
/*      */         } 
/*      */         
/* 1063 */         if (c < 'ⴭ') {
/* 1064 */           return ((c >= 'ⴀ' && c <= 'ⴥ') || c == 'ⴧ');
/*      */         }
/* 1066 */         return (c == 'ⴭ' || (c >= 'ⴰ' && c <= 'ⵧ'));
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1072 */       if (c < 'ㇰ') {
/* 1073 */         if (c < 'ⷐ') {
/* 1074 */           if (c < 'ⶰ') {
/* 1075 */             if (c < 'ⶠ') {
/* 1076 */               return (c == 'ⵯ' || (c >= 'ⶀ' && c <= 'ⶖ'));
/*      */             }
/* 1078 */             return ((c >= 'ⶠ' && c <= 'ⶦ') || (c >= 'ⶨ' && c <= 'ⶮ'));
/*      */           } 
/*      */           
/* 1081 */           if (c < 'ⷀ') {
/* 1082 */             return ((c >= 'ⶰ' && c <= 'ⶶ') || (c >= 'ⶸ' && c <= 'ⶾ'));
/*      */           }
/* 1084 */           return ((c >= 'ⷀ' && c <= 'ⷆ') || (c >= 'ⷈ' && c <= 'ⷎ'));
/*      */         } 
/*      */ 
/*      */         
/* 1088 */         if (c < '〱') {
/* 1089 */           if (c < 'ⸯ') {
/* 1090 */             return ((c >= 'ⷐ' && c <= 'ⷖ') || (c >= 'ⷘ' && c <= 'ⷞ'));
/*      */           }
/* 1092 */           return (c == 'ⸯ' || (c >= '々' && c <= '〆'));
/*      */         } 
/*      */         
/* 1095 */         if (c < '぀') {
/* 1096 */           return ((c >= '〱' && c <= '〵') || (c >= '〻' && c <= '〼'));
/*      */         }
/* 1098 */         return ((c >= '぀' && c <= '㆏') || (c >= 'ㆠ' && c <= 'ㆺ'));
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1103 */       if (c < 'ꙿ') {
/* 1104 */         if (c < 'ꓐ') {
/* 1105 */           if (c < '㐀') {
/* 1106 */             return ((c >= 'ㇰ' && c <= 'ㇿ') || (c >= '㌀' && c <= '㍿'));
/*      */           }
/* 1108 */           return ((c >= '㐀' && c <= '䶵') || (c >= '一' && c <= 'ꒌ'));
/*      */         } 
/*      */         
/* 1111 */         if (c < 'ꘐ') {
/* 1112 */           return ((c >= 'ꓐ' && c <= 'ꓽ') || (c >= 'ꔀ' && c <= 'ꘌ'));
/*      */         }
/* 1114 */         return ((c >= 'ꘐ' && c <= 'ꘫ') || (c >= 'Ꙁ' && c <= 'ꙮ'));
/*      */       } 
/*      */ 
/*      */       
/* 1118 */       if (c < 'Ꞌ') {
/* 1119 */         if (c < 'ꜗ') {
/* 1120 */           return ((c >= 'ꙿ' && c <= 'ꚗ') || (c >= 'ꚠ' && c <= 'ꛥ'));
/*      */         }
/* 1122 */         return ((c >= 'ꜗ' && c <= 'ꜟ') || (c >= 'Ꜣ' && c <= 'ꞈ'));
/*      */       } 
/*      */       
/* 1125 */       if (c < 'Ꞡ') {
/* 1126 */         return ((c >= 'Ꞌ' && c <= 'ꞎ') || (c >= 'Ꞑ' && c <= 'ꞓ'));
/*      */       }
/* 1128 */       return (c >= 'Ꞡ' && c <= 'Ɦ');
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1135 */     if (c < 'ꬠ') {
/* 1136 */       if (c < 'ꩄ') {
/* 1137 */         if (c < 'ꣻ') {
/* 1138 */           if (c < 'ꡀ') {
/* 1139 */             if (c < 'ꠇ') {
/* 1140 */               return ((c >= 'ꟸ' && c <= 'ꠁ') || (c >= 'ꠃ' && c <= 'ꠅ'));
/*      */             }
/* 1142 */             return ((c >= 'ꠇ' && c <= 'ꠊ') || (c >= 'ꠌ' && c <= 'ꠢ'));
/*      */           } 
/*      */           
/* 1145 */           if (c < '꣐') {
/* 1146 */             return ((c >= 'ꡀ' && c <= 'ꡳ') || (c >= 'ꢂ' && c <= 'ꢳ'));
/*      */           }
/* 1148 */           return ((c >= '꣐' && c <= '꣙') || (c >= 'ꣲ' && c <= 'ꣷ'));
/*      */         } 
/*      */ 
/*      */         
/* 1152 */         if (c < 'ꦄ') {
/* 1153 */           if (c < 'ꤰ') {
/* 1154 */             return (c == 'ꣻ' || (c >= '꤀' && c <= 'ꤥ'));
/*      */           }
/* 1156 */           return ((c >= 'ꤰ' && c <= 'ꥆ') || (c >= 'ꥠ' && c <= 'ꥼ'));
/*      */         } 
/*      */         
/* 1159 */         if (c < 'ꨀ') {
/* 1160 */           return ((c >= 'ꦄ' && c <= 'ꦲ') || (c >= 'ꧏ' && c <= '꧙'));
/*      */         }
/* 1162 */         return ((c >= 'ꨀ' && c <= 'ꨨ') || (c >= 'ꩀ' && c <= 'ꩂ'));
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1167 */       if (c < 'ꫀ') {
/* 1168 */         if (c < 'ꪀ') {
/* 1169 */           if (c < 'ꩠ') {
/* 1170 */             return ((c >= 'ꩄ' && c <= 'ꩋ') || (c >= '꩐' && c <= '꩙'));
/*      */           }
/* 1172 */           return ((c >= 'ꩠ' && c <= 'ꩶ') || c == 'ꩺ');
/*      */         } 
/*      */         
/* 1175 */         if (c < 'ꪵ') {
/* 1176 */           return ((c >= 'ꪀ' && c <= 'ꪯ') || c == 'ꪱ');
/*      */         }
/* 1178 */         return ((c >= 'ꪵ' && c <= 'ꪶ') || (c >= 'ꪹ' && c <= 'ꪽ'));
/*      */       } 
/*      */ 
/*      */       
/* 1182 */       if (c < 'ꫲ') {
/* 1183 */         if (c < 'ꫛ') {
/* 1184 */           return (c == 'ꫀ' || c == 'ꫂ');
/*      */         }
/* 1186 */         return ((c >= 'ꫛ' && c <= 'ꫝ') || (c >= 'ꫠ' && c <= 'ꫪ'));
/*      */       } 
/*      */       
/* 1189 */       if (c < 'ꬉ') {
/* 1190 */         return ((c >= 'ꫲ' && c <= 'ꫴ') || (c >= 'ꬁ' && c <= 'ꬆ'));
/*      */       }
/* 1192 */       return ((c >= 'ꬉ' && c <= 'ꬎ') || (c >= 'ꬑ' && c <= 'ꬖ'));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1198 */     if (c < 'צּ') {
/* 1199 */       if (c < 'ﬓ') {
/* 1200 */         if (c < '가') {
/* 1201 */           if (c < 'ꯀ') {
/* 1202 */             return ((c >= 'ꬠ' && c <= 'ꬦ') || (c >= 'ꬨ' && c <= 'ꬮ'));
/*      */           }
/* 1204 */           return ((c >= 'ꯀ' && c <= 'ꯢ') || (c >= '꯰' && c <= '꯹'));
/*      */         } 
/*      */         
/* 1207 */         if (c < 'ퟋ') {
/* 1208 */           return ((c >= '가' && c <= '힣') || (c >= 'ힰ' && c <= 'ퟆ'));
/*      */         }
/* 1210 */         return ((c >= 'ퟋ' && c <= 'ퟻ') || (c >= '豈' && c <= 'ﬆ'));
/*      */       } 
/*      */ 
/*      */       
/* 1214 */       if (c < 'טּ') {
/* 1215 */         if (c < 'ײַ') {
/* 1216 */           return ((c >= 'ﬓ' && c <= 'ﬗ') || c == 'יִ');
/*      */         }
/* 1218 */         return ((c >= 'ײַ' && c <= 'ﬨ') || (c >= 'שׁ' && c <= 'זּ'));
/*      */       } 
/*      */       
/* 1221 */       if (c < 'נּ') {
/* 1222 */         return ((c >= 'טּ' && c <= 'לּ') || c == 'מּ');
/*      */       }
/* 1224 */       return ((c >= 'נּ' && c <= 'סּ') || (c >= 'ףּ' && c <= 'פּ'));
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1229 */     if (c < 'Ａ') {
/* 1230 */       if (c < 'ﷰ') {
/* 1231 */         if (c < 'ﵐ') {
/* 1232 */           return ((c >= 'צּ' && c <= 'ﮱ') || (c >= 'ﯓ' && c <= 'ﴽ'));
/*      */         }
/* 1234 */         return ((c >= 'ﵐ' && c <= 'ﶏ') || (c >= 'ﶒ' && c <= 'ﷇ'));
/*      */       } 
/*      */       
/* 1237 */       if (c < 'ﹶ') {
/* 1238 */         return ((c >= 'ﷰ' && c <= 'ﷻ') || (c >= 'ﹰ' && c <= 'ﹴ'));
/*      */       }
/* 1240 */       return ((c >= 'ﹶ' && c <= 'ﻼ') || (c >= '０' && c <= '９'));
/*      */     } 
/*      */ 
/*      */     
/* 1244 */     if (c < 'ￊ') {
/* 1245 */       if (c < 'ｦ') {
/* 1246 */         return ((c >= 'Ａ' && c <= 'Ｚ') || (c >= 'ａ' && c <= 'ｚ'));
/*      */       }
/* 1248 */       return ((c >= 'ｦ' && c <= 'ﾾ') || (c >= 'ￂ' && c <= 'ￇ'));
/*      */     } 
/*      */     
/* 1251 */     if (c < 'ￚ') {
/* 1252 */       return ((c >= 'ￊ' && c <= 'ￏ') || (c >= 'ￒ' && c <= 'ￗ'));
/*      */     }
/* 1254 */     return (c >= 'ￚ' && c <= 'ￜ');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isFTLIdentifierPart(char c) {
/* 1271 */     return (isFTLIdentifierStart(c) || (c >= '0' && c <= '9'));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBackslashEscapedFTLIdentifierCharacter(char c) {
/* 1282 */     return (c == '-' || c == '.' || c == ':' || c == '#');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String javaStringEnc(String s) {
/* 1297 */     int ln = s.length();
/* 1298 */     for (int i = 0; i < ln; i++) {
/* 1299 */       char c = s.charAt(i);
/* 1300 */       if (c == '"' || c == '\\' || c < ' ') {
/* 1301 */         StringBuilder b = new StringBuilder(ln + 4);
/* 1302 */         b.append(s.substring(0, i));
/*      */         while (true) {
/* 1304 */           if (c == '"') {
/* 1305 */             b.append("\\\"");
/* 1306 */           } else if (c == '\\') {
/* 1307 */             b.append("\\\\");
/* 1308 */           } else if (c < ' ') {
/* 1309 */             if (c == '\n') {
/* 1310 */               b.append("\\n");
/* 1311 */             } else if (c == '\r') {
/* 1312 */               b.append("\\r");
/* 1313 */             } else if (c == '\f') {
/* 1314 */               b.append("\\f");
/* 1315 */             } else if (c == '\b') {
/* 1316 */               b.append("\\b");
/* 1317 */             } else if (c == '\t') {
/* 1318 */               b.append("\\t");
/*      */             } else {
/* 1320 */               b.append("\\u00");
/* 1321 */               int x = c / 16;
/* 1322 */               b.append((char)((x < 10) ? (x + 48) : (x - 10 + 97)));
/*      */               
/* 1324 */               x = c & 0xF;
/* 1325 */               b.append((char)((x < 10) ? (x + 48) : (x - 10 + 97)));
/*      */             } 
/*      */           } else {
/*      */             
/* 1329 */             b.append(c);
/*      */           } 
/* 1331 */           i++;
/* 1332 */           if (i >= ln) {
/* 1333 */             return b.toString();
/*      */           }
/* 1335 */           c = s.charAt(i);
/*      */         } 
/*      */       } 
/*      */     } 
/* 1339 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String javaScriptStringEnc(String s) {
/* 1347 */     return jsStringEnc(s, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String jsonStringEnc(String s) {
/* 1355 */     return jsStringEnc(s, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String jsStringEnc(String s, boolean json) {
/* 1415 */     NullArgumentException.check("s", s);
/*      */     
/* 1417 */     int ln = s.length();
/* 1418 */     StringBuilder sb = null;
/* 1419 */     for (int i = 0; i < ln; i++) {
/* 1420 */       char c = s.charAt(i);
/*      */       
/* 1422 */       if ((c <= '>' || c >= '' || c == '\\') && c != ' ' && (c < ' ' || c >= ' ')) {
/* 1423 */         int escapeType; if (c <= '\037') {
/* 1424 */           if (c == '\n') {
/* 1425 */             escapeType = 110;
/* 1426 */           } else if (c == '\r') {
/* 1427 */             escapeType = 114;
/* 1428 */           } else if (c == '\f') {
/* 1429 */             escapeType = 102;
/* 1430 */           } else if (c == '\b') {
/* 1431 */             escapeType = 98;
/* 1432 */           } else if (c == '\t') {
/* 1433 */             escapeType = 116;
/*      */           } else {
/* 1435 */             escapeType = 1;
/*      */           } 
/* 1437 */         } else if (c == '"') {
/* 1438 */           escapeType = 3;
/* 1439 */         } else if (c == '\'') {
/* 1440 */           escapeType = json ? 0 : 3;
/* 1441 */         } else if (c == '\\') {
/* 1442 */           escapeType = 3;
/* 1443 */         } else if (c == '/' && (i == 0 || s.charAt(i - 1) == '<')) {
/* 1444 */           escapeType = 3;
/* 1445 */         } else if (c == '>') {
/*      */           boolean dangerous;
/* 1447 */           if (i == 0) {
/* 1448 */             dangerous = true;
/*      */           } else {
/* 1450 */             char prevC = s.charAt(i - 1);
/* 1451 */             if (prevC == ']' || prevC == '-') {
/* 1452 */               if (i == 1) {
/* 1453 */                 dangerous = true;
/*      */               } else {
/* 1455 */                 char prevPrevC = s.charAt(i - 2);
/* 1456 */                 dangerous = (prevPrevC == prevC);
/*      */               } 
/*      */             } else {
/* 1459 */               dangerous = false;
/*      */             } 
/*      */           } 
/* 1462 */           escapeType = dangerous ? (json ? 1 : 3) : 0;
/* 1463 */         } else if (c == '<') {
/*      */           boolean dangerous;
/* 1465 */           if (i == ln - 1) {
/* 1466 */             dangerous = true;
/*      */           } else {
/* 1468 */             char nextC = s.charAt(i + 1);
/* 1469 */             dangerous = (nextC == '!' || nextC == '?');
/*      */           } 
/* 1471 */           escapeType = dangerous ? 1 : 0;
/* 1472 */         } else if ((c >= '' && c <= '') || c == ' ' || c == ' ') {
/*      */ 
/*      */           
/* 1475 */           escapeType = 1;
/*      */         } else {
/* 1477 */           escapeType = 0;
/*      */         } 
/*      */         
/* 1480 */         if (escapeType != 0) {
/* 1481 */           if (sb == null) {
/* 1482 */             sb = new StringBuilder(ln + 6);
/* 1483 */             sb.append(s.substring(0, i));
/*      */           } 
/*      */           
/* 1486 */           sb.append('\\');
/* 1487 */           if (escapeType > 32) {
/* 1488 */             sb.append((char)escapeType);
/* 1489 */           } else if (escapeType == 1) {
/* 1490 */             if (!json && c < 'Ā') {
/* 1491 */               sb.append('x');
/* 1492 */               sb.append(toHexDigit(c >> 4));
/* 1493 */               sb.append(toHexDigit(c & 0xF));
/*      */             } else {
/* 1495 */               sb.append('u');
/* 1496 */               int cp = c;
/* 1497 */               sb.append(toHexDigit(cp >> 12 & 0xF));
/* 1498 */               sb.append(toHexDigit(cp >> 8 & 0xF));
/* 1499 */               sb.append(toHexDigit(cp >> 4 & 0xF));
/* 1500 */               sb.append(toHexDigit(cp & 0xF));
/*      */             } 
/*      */           } else {
/* 1503 */             sb.append(c);
/*      */           } 
/*      */ 
/*      */           
/*      */           continue;
/*      */         } 
/*      */       } 
/*      */       
/* 1511 */       if (sb != null) sb.append(c); 
/*      */       continue;
/*      */     } 
/* 1514 */     return (sb == null) ? s : sb.toString();
/*      */   }
/*      */   
/*      */   private static char toHexDigit(int d) {
/* 1518 */     return (char)((d < 10) ? (d + 48) : (d - 10 + 65));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Map parseNameValuePairList(String s, String defaultValue) throws ParseException {
/* 1542 */     Map<Object, Object> map = new HashMap<>();
/*      */     
/* 1544 */     char c = ' ';
/* 1545 */     int ln = s.length();
/* 1546 */     int p = 0;
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*      */       String value;
/*      */ 
/*      */       
/* 1554 */       if (p < ln) {
/* 1555 */         c = s.charAt(p);
/* 1556 */         if (Character.isWhitespace(c)) {
/*      */ 
/*      */           
/* 1559 */           p++; continue;
/*      */         } 
/* 1561 */       }  if (p == ln) {
/*      */         break;
/*      */       }
/* 1564 */       int keyStart = p;
/*      */ 
/*      */       
/* 1567 */       while (p < ln) {
/* 1568 */         c = s.charAt(p);
/* 1569 */         if (!Character.isLetterOrDigit(c) && c != '_') {
/*      */           break;
/*      */         }
/* 1572 */         p++;
/*      */       } 
/* 1574 */       if (keyStart == p) {
/* 1575 */         throw new ParseException("Expecting letter, digit or \"_\" here, (the first character of the key) but found " + 
/*      */ 
/*      */             
/* 1578 */             jQuote(String.valueOf(c)) + " at position " + p + ".", p);
/*      */       }
/*      */ 
/*      */       
/* 1582 */       String key = s.substring(keyStart, p);
/*      */ 
/*      */       
/* 1585 */       while (p < ln) {
/* 1586 */         c = s.charAt(p);
/* 1587 */         if (!Character.isWhitespace(c)) {
/*      */           break;
/*      */         }
/* 1590 */         p++;
/*      */       } 
/* 1592 */       if (p == ln) {
/* 1593 */         if (defaultValue == null) {
/* 1594 */           throw new ParseException("Expecting \":\", but reached the end of the string  at position " + p + ".", p);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1600 */         value = defaultValue;
/* 1601 */       } else if (c != ':') {
/* 1602 */         if (defaultValue == null || c != ',') {
/* 1603 */           throw new ParseException("Expecting \":\" here, but found " + 
/*      */               
/* 1605 */               jQuote(String.valueOf(c)) + " at position " + p + ".", p);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1611 */         p++;
/*      */         
/* 1613 */         value = defaultValue;
/*      */       } else {
/*      */         
/* 1616 */         p++;
/*      */ 
/*      */         
/* 1619 */         while (p < ln) {
/* 1620 */           c = s.charAt(p);
/* 1621 */           if (!Character.isWhitespace(c)) {
/*      */             break;
/*      */           }
/* 1624 */           p++;
/*      */         } 
/* 1626 */         if (p == ln) {
/* 1627 */           throw new ParseException("Expecting the value of the key here, but reached the end of the string  at position " + p + ".", p);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1633 */         int valueStart = p;
/*      */ 
/*      */         
/* 1636 */         while (p < ln) {
/* 1637 */           c = s.charAt(p);
/* 1638 */           if (!Character.isLetterOrDigit(c) && c != '_') {
/*      */             break;
/*      */           }
/* 1641 */           p++;
/*      */         } 
/* 1643 */         if (valueStart == p) {
/* 1644 */           throw new ParseException("Expecting letter, digit or \"_\" here, (the first character of the value) but found " + 
/*      */ 
/*      */ 
/*      */               
/* 1648 */               jQuote(String.valueOf(c)) + " at position " + p + ".", p);
/*      */         }
/*      */ 
/*      */         
/* 1652 */         value = s.substring(valueStart, p);
/*      */ 
/*      */         
/* 1655 */         while (p < ln) {
/* 1656 */           c = s.charAt(p);
/* 1657 */           if (!Character.isWhitespace(c)) {
/*      */             break;
/*      */           }
/* 1660 */           p++;
/*      */         } 
/*      */ 
/*      */         
/* 1664 */         if (p < ln) {
/* 1665 */           if (c != ',') {
/* 1666 */             throw new ParseException("Excpecting \",\" or the end of the string here, but found " + 
/*      */ 
/*      */                 
/* 1669 */                 jQuote(String.valueOf(c)) + " at position " + p + ".", p);
/*      */           }
/*      */ 
/*      */           
/* 1673 */           p++;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1679 */       if (map.put(key, value) != null) {
/* 1680 */         throw new ParseException("Dublicated key: " + 
/*      */             
/* 1682 */             jQuote(key), keyStart);
/*      */       }
/*      */     } 
/*      */     
/* 1686 */     return map;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isXMLID(String name) {
/* 1699 */     return _ExtDomApi.isXMLNameLike(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
/* 1706 */     return _ExtDomApi.matchesName(qname, nodeName, nsURI, env);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String leftPad(String s, int minLength) {
/* 1718 */     return leftPad(s, minLength, ' ');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String leftPad(String s, int minLength, char filling) {
/* 1731 */     int ln = s.length();
/* 1732 */     if (minLength <= ln) {
/* 1733 */       return s;
/*      */     }
/*      */     
/* 1736 */     StringBuilder res = new StringBuilder(minLength);
/*      */     
/* 1738 */     int dif = minLength - ln;
/* 1739 */     for (int i = 0; i < dif; i++) {
/* 1740 */       res.append(filling);
/*      */     }
/*      */     
/* 1743 */     res.append(s);
/*      */     
/* 1745 */     return res.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String leftPad(String s, int minLength, String filling) {
/* 1760 */     int ln = s.length();
/* 1761 */     if (minLength <= ln) {
/* 1762 */       return s;
/*      */     }
/*      */     
/* 1765 */     StringBuilder res = new StringBuilder(minLength);
/*      */     
/* 1767 */     int dif = minLength - ln;
/* 1768 */     int fln = filling.length();
/* 1769 */     if (fln == 0) {
/* 1770 */       throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
/*      */     }
/*      */     
/* 1773 */     int cnt = dif / fln; int i;
/* 1774 */     for (i = 0; i < cnt; i++) {
/* 1775 */       res.append(filling);
/*      */     }
/* 1777 */     cnt = dif % fln;
/* 1778 */     for (i = 0; i < cnt; i++) {
/* 1779 */       res.append(filling.charAt(i));
/*      */     }
/*      */     
/* 1782 */     res.append(s);
/*      */     
/* 1784 */     return res.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String rightPad(String s, int minLength) {
/* 1796 */     return rightPad(s, minLength, ' ');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String rightPad(String s, int minLength, char filling) {
/* 1809 */     int ln = s.length();
/* 1810 */     if (minLength <= ln) {
/* 1811 */       return s;
/*      */     }
/*      */     
/* 1814 */     StringBuilder res = new StringBuilder(minLength);
/*      */     
/* 1816 */     res.append(s);
/*      */     
/* 1818 */     int dif = minLength - ln;
/* 1819 */     for (int i = 0; i < dif; i++) {
/* 1820 */       res.append(filling);
/*      */     }
/*      */     
/* 1823 */     return res.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String rightPad(String s, int minLength, String filling) {
/* 1840 */     int ln = s.length();
/* 1841 */     if (minLength <= ln) {
/* 1842 */       return s;
/*      */     }
/*      */     
/* 1845 */     StringBuilder res = new StringBuilder(minLength);
/*      */     
/* 1847 */     res.append(s);
/*      */     
/* 1849 */     int dif = minLength - ln;
/* 1850 */     int fln = filling.length();
/* 1851 */     if (fln == 0) {
/* 1852 */       throw new IllegalArgumentException("The \"filling\" argument can't be 0 length string.");
/*      */     }
/*      */     
/* 1855 */     int start = ln % fln;
/* 1856 */     int end = (fln - start <= dif) ? fln : (start + dif);
/*      */ 
/*      */     
/* 1859 */     for (int i = start; i < end; i++) {
/* 1860 */       res.append(filling.charAt(i));
/*      */     }
/* 1862 */     dif -= end - start;
/* 1863 */     int cnt = dif / fln; int j;
/* 1864 */     for (j = 0; j < cnt; j++) {
/* 1865 */       res.append(filling);
/*      */     }
/* 1867 */     cnt = dif % fln;
/* 1868 */     for (j = 0; j < cnt; j++) {
/* 1869 */       res.append(filling.charAt(j));
/*      */     }
/*      */     
/* 1872 */     return res.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int versionStringToInt(String version) {
/* 1887 */     return (new Version(version)).intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String tryToString(Object object) {
/* 1898 */     if (object == null) return null;
/*      */     
/*      */     try {
/* 1901 */       return object.toString();
/* 1902 */     } catch (Throwable e) {
/* 1903 */       return failedToStringSubstitute(object, e);
/*      */     } 
/*      */   }
/*      */   
/*      */   private static String failedToStringSubstitute(Object object, Throwable e) {
/*      */     String eStr;
/*      */     try {
/* 1910 */       eStr = e.toString();
/* 1911 */     } catch (Throwable e2) {
/* 1912 */       eStr = ClassUtil.getShortClassNameOfObject(e);
/*      */     } 
/* 1914 */     return "[" + ClassUtil.getShortClassNameOfObject(object) + ".toString() failed: " + eStr + "]";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toUpperABC(int n) {
/* 1928 */     return toABC(n, 'A');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toLowerABC(int n) {
/* 1937 */     return toABC(n, 'a');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String toABC(int n, char oneDigit) {
/* 1945 */     if (n < 1) {
/* 1946 */       throw new IllegalArgumentException("Can't convert 0 or negative numbers to latin-number: " + n);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1953 */     int reached = 1;
/* 1954 */     int weight = 1;
/*      */     while (true) {
/* 1956 */       int nextWeight = weight * 26;
/* 1957 */       int nextReached = reached + nextWeight;
/* 1958 */       if (nextReached <= n) {
/*      */         
/* 1960 */         weight = nextWeight;
/* 1961 */         reached = nextReached;
/*      */ 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/*      */       break;
/*      */     } 
/*      */     
/* 1970 */     StringBuilder sb = new StringBuilder();
/* 1971 */     while (weight != 0) {
/*      */       
/* 1973 */       int digitIncrease = (n - reached) / weight;
/* 1974 */       sb.append((char)(oneDigit + digitIncrease));
/* 1975 */       reached += digitIncrease * weight;
/*      */       
/* 1977 */       weight /= 26;
/*      */     } 
/*      */     
/* 1980 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] trim(char[] cs) {
/* 1991 */     if (cs.length == 0) {
/* 1992 */       return cs;
/*      */     }
/*      */     
/* 1995 */     int start = 0;
/* 1996 */     int end = cs.length;
/* 1997 */     while (start < end && cs[start] <= ' ') {
/* 1998 */       start++;
/*      */     }
/* 2000 */     while (start < end && cs[end - 1] <= ' ') {
/* 2001 */       end--;
/*      */     }
/*      */     
/* 2004 */     if (start == 0 && end == cs.length) {
/* 2005 */       return cs;
/*      */     }
/* 2007 */     if (start == end) {
/* 2008 */       return CollectionUtils.EMPTY_CHAR_ARRAY;
/*      */     }
/*      */     
/* 2011 */     char[] newCs = new char[end - start];
/* 2012 */     System.arraycopy(cs, start, newCs, 0, end - start);
/* 2013 */     return newCs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTrimmableToEmpty(char[] text) {
/* 2022 */     return isTrimmableToEmpty(text, 0, text.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTrimmableToEmpty(char[] text, int start) {
/* 2031 */     return isTrimmableToEmpty(text, start, text.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTrimmableToEmpty(char[] text, int start, int end) {
/* 2041 */     for (int i = start; i < end; i++) {
/*      */       
/* 2043 */       if (text[i] > ' ') {
/* 2044 */         return false;
/*      */       }
/*      */     } 
/* 2047 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pattern globToRegularExpression(String glob) {
/* 2056 */     return globToRegularExpression(glob, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pattern globToRegularExpression(String glob, boolean caseInsensitive) {
/* 2082 */     StringBuilder regex = new StringBuilder();
/*      */     
/* 2084 */     int nextStart = 0;
/* 2085 */     boolean escaped = false;
/* 2086 */     int ln = glob.length();
/* 2087 */     for (int idx = 0; idx < ln; idx++) {
/* 2088 */       char c = glob.charAt(idx);
/* 2089 */       if (!escaped) {
/* 2090 */         if (c == '?') {
/* 2091 */           appendLiteralGlobSection(regex, glob, nextStart, idx);
/* 2092 */           regex.append("[^/]");
/* 2093 */           nextStart = idx + 1;
/* 2094 */         } else if (c == '*') {
/* 2095 */           appendLiteralGlobSection(regex, glob, nextStart, idx);
/* 2096 */           if (idx + 1 < ln && glob.charAt(idx + 1) == '*') {
/* 2097 */             if (idx != 0 && glob.charAt(idx - 1) != '/') {
/* 2098 */               throw new IllegalArgumentException("The \"**\" wildcard must be directly after a \"/\" or it must be at the beginning, in this glob: " + glob);
/*      */             }
/*      */ 
/*      */ 
/*      */             
/* 2103 */             if (idx + 2 == ln) {
/* 2104 */               regex.append(".*");
/* 2105 */               idx++;
/*      */             } else {
/* 2107 */               if (idx + 2 >= ln || glob.charAt(idx + 2) != '/') {
/* 2108 */                 throw new IllegalArgumentException("The \"**\" wildcard must be followed by \"/\", or must be at tehe end, in this glob: " + glob);
/*      */               }
/*      */ 
/*      */               
/* 2112 */               regex.append("(.*?/)*");
/* 2113 */               idx += 2;
/*      */             } 
/*      */           } else {
/* 2116 */             regex.append("[^/]*");
/*      */           } 
/* 2118 */           nextStart = idx + 1;
/* 2119 */         } else if (c == '\\') {
/* 2120 */           escaped = true;
/* 2121 */         } else if (c == '[' || c == '{') {
/* 2122 */           throw new IllegalArgumentException("The \"" + c + "\" glob operator is currently unsupported (precede it with \\ for literal matching), in this glob: " + glob);
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 2128 */         escaped = false;
/*      */       } 
/*      */     } 
/* 2131 */     appendLiteralGlobSection(regex, glob, nextStart, glob.length());
/*      */     
/* 2133 */     return Pattern.compile(regex.toString(), caseInsensitive ? 66 : 0);
/*      */   }
/*      */   
/*      */   private static void appendLiteralGlobSection(StringBuilder regex, String glob, int start, int end) {
/* 2137 */     if (start == end)
/* 2138 */       return;  String part = unescapeLiteralGlobSection(glob.substring(start, end));
/* 2139 */     regex.append(Pattern.quote(part));
/*      */   }
/*      */   
/*      */   private static String unescapeLiteralGlobSection(String s) {
/* 2143 */     int backslashIdx = s.indexOf('\\');
/* 2144 */     if (backslashIdx == -1) {
/* 2145 */       return s;
/*      */     }
/* 2147 */     int ln = s.length();
/* 2148 */     StringBuilder sb = new StringBuilder(ln - 1);
/* 2149 */     int nextStart = 0;
/*      */     do {
/* 2151 */       sb.append(s, nextStart, backslashIdx);
/* 2152 */       nextStart = backslashIdx + 1;
/* 2153 */     } while ((backslashIdx = s.indexOf('\\', nextStart + 1)) != -1);
/* 2154 */     if (nextStart < ln) {
/* 2155 */       sb.append(s, nextStart, ln);
/*      */     }
/* 2157 */     return sb.toString();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\StringUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */