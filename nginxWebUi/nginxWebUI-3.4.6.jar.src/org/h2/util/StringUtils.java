/*      */ package org.h2.util;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.net.URLEncoder;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Locale;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.h2.engine.SysProperties;
/*      */ import org.h2.message.DbException;
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
/*      */ public class StringUtils
/*      */ {
/*      */   private static SoftReference<String[]> softCache;
/*      */   private static long softCacheCreatedNs;
/*   29 */   private static final char[] HEX = "0123456789abcdef".toCharArray();
/*   30 */   private static final int[] HEX_DECODE = new int[103];
/*      */   
/*      */   private static final int TO_UPPER_CACHE_LENGTH = 2048;
/*      */   
/*      */   private static final int TO_UPPER_CACHE_MAX_ENTRY_LENGTH = 64;
/*      */   
/*   36 */   private static final String[][] TO_UPPER_CACHE = new String[2048][];
/*      */   static {
/*      */     byte b;
/*   39 */     for (b = 0; b < HEX_DECODE.length; b++) {
/*   40 */       HEX_DECODE[b] = -1;
/*      */     }
/*   42 */     for (b = 0; b <= 9; b++) {
/*   43 */       HEX_DECODE[b + 48] = b;
/*      */     }
/*   45 */     for (b = 0; b <= 5; b++) {
/*   46 */       HEX_DECODE[b + 65] = b + 10; HEX_DECODE[b + 97] = b + 10;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String[] getCache() {
/*   56 */     if (softCache != null) {
/*   57 */       String[] arrayOfString = softCache.get();
/*   58 */       if (arrayOfString != null) {
/*   59 */         return arrayOfString;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*   64 */     long l = System.nanoTime();
/*   65 */     if (softCacheCreatedNs != 0L && l - softCacheCreatedNs < TimeUnit.SECONDS.toNanos(5L)) {
/*   66 */       return null;
/*      */     }
/*      */     try {
/*   69 */       String[] arrayOfString = new String[SysProperties.OBJECT_CACHE_SIZE];
/*   70 */       softCache = (SoftReference)new SoftReference<>(arrayOfString);
/*   71 */       return arrayOfString;
/*      */     } finally {
/*   73 */       softCacheCreatedNs = System.nanoTime();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toUpperEnglish(String paramString) {
/*   84 */     if (paramString.length() > 64) {
/*   85 */       return paramString.toUpperCase(Locale.ENGLISH);
/*      */     }
/*   87 */     int i = paramString.hashCode() & 0x7FF;
/*   88 */     String[] arrayOfString = TO_UPPER_CACHE[i];
/*   89 */     if (arrayOfString != null && 
/*   90 */       arrayOfString[0].equals(paramString)) {
/*   91 */       return arrayOfString[1];
/*      */     }
/*      */     
/*   94 */     String str = paramString.toUpperCase(Locale.ENGLISH);
/*   95 */     arrayOfString = new String[] { paramString, str };
/*   96 */     TO_UPPER_CACHE[i] = arrayOfString;
/*   97 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toLowerEnglish(String paramString) {
/*  107 */     return paramString.toLowerCase(Locale.ENGLISH);
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
/*      */   public static String quoteStringSQL(String paramString) {
/*  119 */     if (paramString == null) {
/*  120 */       return "NULL";
/*      */     }
/*  122 */     return quoteStringSQL(new StringBuilder(paramString.length() + 2), paramString).toString();
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
/*      */   public static StringBuilder quoteStringSQL(StringBuilder paramStringBuilder, String paramString) {
/*  136 */     if (paramString == null) {
/*  137 */       return paramStringBuilder.append("NULL");
/*      */     }
/*  139 */     return quoteIdentifierOrLiteral(paramStringBuilder, paramString, '\'');
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
/*      */   public static String decodeUnicodeStringSQL(String paramString, int paramInt) {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual length : ()I
/*      */     //   4: istore_2
/*      */     //   5: new java/lang/StringBuilder
/*      */     //   8: dup
/*      */     //   9: iload_2
/*      */     //   10: invokespecial <init> : (I)V
/*      */     //   13: astore_3
/*      */     //   14: iconst_0
/*      */     //   15: istore #4
/*      */     //   17: iload #4
/*      */     //   19: iload_2
/*      */     //   20: if_icmpge -> 196
/*      */     //   23: aload_0
/*      */     //   24: iload #4
/*      */     //   26: invokevirtual codePointAt : (I)I
/*      */     //   29: istore #5
/*      */     //   31: iload #4
/*      */     //   33: iload #5
/*      */     //   35: invokestatic charCount : (I)I
/*      */     //   38: iadd
/*      */     //   39: istore #4
/*      */     //   41: iload #5
/*      */     //   43: iload_1
/*      */     //   44: if_icmpne -> 186
/*      */     //   47: iload #4
/*      */     //   49: iload_2
/*      */     //   50: if_icmplt -> 60
/*      */     //   53: aload_0
/*      */     //   54: iload #4
/*      */     //   56: invokestatic getFormatException : (Ljava/lang/String;I)Lorg/h2/message/DbException;
/*      */     //   59: athrow
/*      */     //   60: aload_0
/*      */     //   61: iload #4
/*      */     //   63: invokevirtual codePointAt : (I)I
/*      */     //   66: istore #5
/*      */     //   68: iload #5
/*      */     //   70: iload_1
/*      */     //   71: if_icmpne -> 87
/*      */     //   74: iload #4
/*      */     //   76: iload #5
/*      */     //   78: invokestatic charCount : (I)I
/*      */     //   81: iadd
/*      */     //   82: istore #4
/*      */     //   84: goto -> 186
/*      */     //   87: iload #4
/*      */     //   89: iconst_4
/*      */     //   90: iadd
/*      */     //   91: iload_2
/*      */     //   92: if_icmple -> 102
/*      */     //   95: aload_0
/*      */     //   96: iload #4
/*      */     //   98: invokestatic getFormatException : (Ljava/lang/String;I)Lorg/h2/message/DbException;
/*      */     //   101: athrow
/*      */     //   102: aload_0
/*      */     //   103: iload #4
/*      */     //   105: invokevirtual charAt : (I)C
/*      */     //   108: istore #6
/*      */     //   110: iload #6
/*      */     //   112: bipush #43
/*      */     //   114: if_icmpne -> 156
/*      */     //   117: iload #4
/*      */     //   119: bipush #7
/*      */     //   121: iadd
/*      */     //   122: iload_2
/*      */     //   123: if_icmple -> 133
/*      */     //   126: aload_0
/*      */     //   127: iload #4
/*      */     //   129: invokestatic getFormatException : (Ljava/lang/String;I)Lorg/h2/message/DbException;
/*      */     //   132: athrow
/*      */     //   133: aload_0
/*      */     //   134: iload #4
/*      */     //   136: iconst_1
/*      */     //   137: iadd
/*      */     //   138: iinc #4, 7
/*      */     //   141: iload #4
/*      */     //   143: invokevirtual substring : (II)Ljava/lang/String;
/*      */     //   146: bipush #16
/*      */     //   148: invokestatic parseUnsignedInt : (Ljava/lang/String;I)I
/*      */     //   151: istore #5
/*      */     //   153: goto -> 174
/*      */     //   156: aload_0
/*      */     //   157: iload #4
/*      */     //   159: iinc #4, 4
/*      */     //   162: iload #4
/*      */     //   164: invokevirtual substring : (II)Ljava/lang/String;
/*      */     //   167: bipush #16
/*      */     //   169: invokestatic parseUnsignedInt : (Ljava/lang/String;I)I
/*      */     //   172: istore #5
/*      */     //   174: goto -> 186
/*      */     //   177: astore #7
/*      */     //   179: aload_0
/*      */     //   180: iload #4
/*      */     //   182: invokestatic getFormatException : (Ljava/lang/String;I)Lorg/h2/message/DbException;
/*      */     //   185: athrow
/*      */     //   186: aload_3
/*      */     //   187: iload #5
/*      */     //   189: invokevirtual appendCodePoint : (I)Ljava/lang/StringBuilder;
/*      */     //   192: pop
/*      */     //   193: goto -> 17
/*      */     //   196: aload_3
/*      */     //   197: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   200: areturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #154	-> 0
/*      */     //   #155	-> 5
/*      */     //   #156	-> 14
/*      */     //   #157	-> 23
/*      */     //   #158	-> 31
/*      */     //   #159	-> 41
/*      */     //   #160	-> 47
/*      */     //   #161	-> 53
/*      */     //   #163	-> 60
/*      */     //   #164	-> 68
/*      */     //   #165	-> 74
/*      */     //   #167	-> 87
/*      */     //   #168	-> 95
/*      */     //   #170	-> 102
/*      */     //   #172	-> 110
/*      */     //   #173	-> 117
/*      */     //   #174	-> 126
/*      */     //   #176	-> 133
/*      */     //   #178	-> 156
/*      */     //   #182	-> 174
/*      */     //   #180	-> 177
/*      */     //   #181	-> 179
/*      */     //   #185	-> 186
/*      */     //   #186	-> 193
/*      */     //   #187	-> 196
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   110	174	177	java/lang/NumberFormatException
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
/*      */   public static String javaEncode(String paramString) {
/*  199 */     StringBuilder stringBuilder = new StringBuilder(paramString.length());
/*  200 */     javaEncode(paramString, stringBuilder, false);
/*  201 */     return stringBuilder.toString();
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
/*      */   public static void javaEncode(String paramString, StringBuilder paramStringBuilder, boolean paramBoolean) {
/*  214 */     int i = paramString.length();
/*  215 */     for (byte b = 0; b < i; b++) {
/*  216 */       char c = paramString.charAt(b);
/*  217 */       switch (c) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case '\t':
/*  225 */           paramStringBuilder.append("\\t");
/*      */           break;
/*      */         
/*      */         case '\n':
/*  229 */           paramStringBuilder.append("\\n");
/*      */           break;
/*      */         
/*      */         case '\f':
/*  233 */           paramStringBuilder.append("\\f");
/*      */           break;
/*      */         
/*      */         case '\r':
/*  237 */           paramStringBuilder.append("\\r");
/*      */           break;
/*      */         
/*      */         case '"':
/*  241 */           paramStringBuilder.append("\\\"");
/*      */           break;
/*      */         
/*      */         case '\'':
/*  245 */           if (paramBoolean) {
/*  246 */             paramStringBuilder.append('\'');
/*      */           }
/*  248 */           paramStringBuilder.append('\'');
/*      */           break;
/*      */         
/*      */         case '\\':
/*  252 */           paramStringBuilder.append("\\\\");
/*      */           break;
/*      */         default:
/*  255 */           if (c >= ' ' && c < '') {
/*  256 */             paramStringBuilder.append(c);
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*      */ 
/*      */           
/*  263 */           paramStringBuilder.append("\\u")
/*  264 */             .append(HEX[c >>> 12])
/*  265 */             .append(HEX[c >>> 8 & 0xF])
/*  266 */             .append(HEX[c >>> 4 & 0xF])
/*  267 */             .append(HEX[c & 0xF]);
/*      */           break;
/*      */       } 
/*      */     } 
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
/*      */   public static String addAsterisk(String paramString, int paramInt) {
/*  282 */     if (paramString != null) {
/*  283 */       int i = paramString.length();
/*  284 */       paramInt = Math.min(paramInt, i);
/*  285 */       paramString = (new StringBuilder(i + 3)).append(paramString, 0, paramInt).append("[*]").append(paramString, paramInt, i).toString();
/*      */     } 
/*  287 */     return paramString;
/*      */   }
/*      */   
/*      */   private static DbException getFormatException(String paramString, int paramInt) {
/*  291 */     return DbException.get(90095, addAsterisk(paramString, paramInt));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String javaDecode(String paramString) {
/*  302 */     int i = paramString.length();
/*  303 */     StringBuilder stringBuilder = new StringBuilder(i);
/*  304 */     for (byte b = 0; b < i; b++) {
/*  305 */       char c = paramString.charAt(b);
/*  306 */       if (c == '\\') {
/*  307 */         if (b + 1 >= paramString.length()) {
/*  308 */           throw getFormatException(paramString, b);
/*      */         }
/*  310 */         c = paramString.charAt(++b);
/*  311 */         switch (c) {
/*      */           case 't':
/*  313 */             stringBuilder.append('\t');
/*      */             break;
/*      */           case 'r':
/*  316 */             stringBuilder.append('\r');
/*      */             break;
/*      */           case 'n':
/*  319 */             stringBuilder.append('\n');
/*      */             break;
/*      */           case 'b':
/*  322 */             stringBuilder.append('\b');
/*      */             break;
/*      */           case 'f':
/*  325 */             stringBuilder.append('\f');
/*      */             break;
/*      */           
/*      */           case '#':
/*  329 */             stringBuilder.append('#');
/*      */             break;
/*      */           
/*      */           case '=':
/*  333 */             stringBuilder.append('=');
/*      */             break;
/*      */           
/*      */           case ':':
/*  337 */             stringBuilder.append(':');
/*      */             break;
/*      */           case '"':
/*  340 */             stringBuilder.append('"');
/*      */             break;
/*      */           case '\\':
/*  343 */             stringBuilder.append('\\');
/*      */             break;
/*      */           case 'u':
/*  346 */             if (b + 4 >= i) {
/*  347 */               throw getFormatException(paramString, b);
/*      */             }
/*      */             try {
/*  350 */               c = (char)Integer.parseInt(paramString.substring(b + 1, b + 5), 16);
/*  351 */             } catch (NumberFormatException numberFormatException) {
/*  352 */               throw getFormatException(paramString, b);
/*      */             } 
/*  354 */             b += 4;
/*  355 */             stringBuilder.append(c);
/*      */             break;
/*      */           
/*      */           default:
/*  359 */             if (c >= '0' && c <= '9' && b + 2 < i) {
/*      */               try {
/*  361 */                 c = (char)Integer.parseInt(paramString.substring(b, b + 3), 8);
/*  362 */               } catch (NumberFormatException numberFormatException) {
/*  363 */                 throw getFormatException(paramString, b);
/*      */               } 
/*  365 */               b += 2;
/*  366 */               stringBuilder.append(c); break;
/*      */             } 
/*  368 */             throw getFormatException(paramString, b);
/*      */         } 
/*      */       
/*      */       } else {
/*  372 */         stringBuilder.append(c);
/*      */       } 
/*      */     } 
/*  375 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quoteJavaString(String paramString) {
/*  386 */     if (paramString == null) {
/*  387 */       return "null";
/*      */     }
/*  389 */     StringBuilder stringBuilder = (new StringBuilder(paramString.length() + 2)).append('"');
/*  390 */     javaEncode(paramString, stringBuilder, false);
/*  391 */     return stringBuilder.append('"').toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quoteJavaStringArray(String[] paramArrayOfString) {
/*  402 */     if (paramArrayOfString == null) {
/*  403 */       return "null";
/*      */     }
/*  405 */     StringBuilder stringBuilder = new StringBuilder("new String[]{");
/*  406 */     for (byte b = 0; b < paramArrayOfString.length; b++) {
/*  407 */       if (b > 0) {
/*  408 */         stringBuilder.append(", ");
/*      */       }
/*  410 */       stringBuilder.append(quoteJavaString(paramArrayOfString[b]));
/*      */     } 
/*  412 */     return stringBuilder.append('}').toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quoteJavaIntArray(int[] paramArrayOfint) {
/*  423 */     if (paramArrayOfint == null) {
/*  424 */       return "null";
/*      */     }
/*  426 */     StringBuilder stringBuilder = new StringBuilder("new int[]{");
/*  427 */     for (byte b = 0; b < paramArrayOfint.length; b++) {
/*  428 */       if (b > 0) {
/*  429 */         stringBuilder.append(", ");
/*      */       }
/*  431 */       stringBuilder.append(paramArrayOfint[b]);
/*      */     } 
/*  433 */     return stringBuilder.append('}').toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String urlEncode(String paramString) {
/*      */     try {
/*  444 */       return URLEncoder.encode(paramString, "UTF-8");
/*  445 */     } catch (Exception exception) {
/*      */       
/*  447 */       throw DbException.convert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String urlDecode(String paramString) {
/*  458 */     int i = paramString.length();
/*  459 */     byte[] arrayOfByte = new byte[i];
/*  460 */     byte b1 = 0;
/*  461 */     for (byte b2 = 0; b2 < i; b2++) {
/*  462 */       char c = paramString.charAt(b2);
/*  463 */       if (c == '+') {
/*  464 */         arrayOfByte[b1++] = 32;
/*  465 */       } else if (c == '%') {
/*  466 */         arrayOfByte[b1++] = (byte)Integer.parseInt(paramString.substring(b2 + 1, b2 + 3), 16);
/*  467 */         b2 += 2;
/*  468 */       } else if (c <= '' && c >= ' ') {
/*  469 */         arrayOfByte[b1++] = (byte)c;
/*      */       } else {
/*  471 */         throw new IllegalArgumentException("Unexpected char " + c + " decoding " + paramString);
/*      */       } 
/*      */     } 
/*  474 */     return new String(arrayOfByte, 0, b1, StandardCharsets.UTF_8);
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
/*      */   public static String[] arraySplit(String paramString, char paramChar, boolean paramBoolean) {
/*  488 */     if (paramString == null) {
/*  489 */       return null;
/*      */     }
/*  491 */     int i = paramString.length();
/*  492 */     if (i == 0) {
/*  493 */       return new String[0];
/*      */     }
/*  495 */     ArrayList<?> arrayList = Utils.newSmallArrayList();
/*  496 */     StringBuilder stringBuilder = new StringBuilder(i);
/*  497 */     for (byte b = 0; b < i; b++) {
/*  498 */       char c = paramString.charAt(b);
/*  499 */       if (c == paramChar) {
/*  500 */         String str1 = stringBuilder.toString();
/*  501 */         arrayList.add(paramBoolean ? str1.trim() : str1);
/*  502 */         stringBuilder.setLength(0);
/*  503 */       } else if (c == '\\' && b < i - 1) {
/*  504 */         stringBuilder.append(paramString.charAt(++b));
/*      */       } else {
/*  506 */         stringBuilder.append(c);
/*      */       } 
/*      */     } 
/*  509 */     String str = stringBuilder.toString();
/*  510 */     arrayList.add(paramBoolean ? str.trim() : str);
/*  511 */     return arrayList.<String>toArray(new String[0]);
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
/*      */   public static String arrayCombine(String[] paramArrayOfString, char paramChar) {
/*  524 */     StringBuilder stringBuilder = new StringBuilder();
/*  525 */     for (byte b = 0; b < paramArrayOfString.length; b++) {
/*  526 */       if (b > 0) {
/*  527 */         stringBuilder.append(paramChar);
/*      */       }
/*  529 */       String str = paramArrayOfString[b];
/*  530 */       if (str != null) {
/*      */         byte b1;
/*      */         int i;
/*  533 */         for (b1 = 0, i = str.length(); b1 < i; b1++) {
/*  534 */           char c = str.charAt(b1);
/*  535 */           if (c == '\\' || c == paramChar) {
/*  536 */             stringBuilder.append('\\');
/*      */           }
/*  538 */           stringBuilder.append(c);
/*      */         } 
/*      */       } 
/*  541 */     }  return stringBuilder.toString();
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
/*      */   public static String xmlAttr(String paramString1, String paramString2) {
/*  553 */     return " " + paramString1 + "=\"" + xmlText(paramString2) + "\"";
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
/*      */   public static String xmlNode(String paramString1, String paramString2, String paramString3) {
/*  566 */     return xmlNode(paramString1, paramString2, paramString3, true);
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
/*      */   public static String xmlNode(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
/*  582 */     StringBuilder stringBuilder = new StringBuilder();
/*  583 */     stringBuilder.append('<').append(paramString1);
/*  584 */     if (paramString2 != null) {
/*  585 */       stringBuilder.append(paramString2);
/*      */     }
/*  587 */     if (paramString3 == null) {
/*  588 */       stringBuilder.append("/>\n");
/*  589 */       return stringBuilder.toString();
/*      */     } 
/*  591 */     stringBuilder.append('>');
/*  592 */     if (paramBoolean && paramString3.indexOf('\n') >= 0) {
/*  593 */       stringBuilder.append('\n');
/*  594 */       indent(stringBuilder, paramString3, 4, true);
/*      */     } else {
/*  596 */       stringBuilder.append(paramString3);
/*      */     } 
/*  598 */     stringBuilder.append("</").append(paramString1).append(">\n");
/*  599 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder indent(StringBuilder paramStringBuilder, String paramString, int paramInt, boolean paramBoolean) {
/*      */     int i;
/*      */     int j;
/*  612 */     for (i = 0, j = paramString.length(); i < j; ) {
/*  613 */       int k; for (k = 0; k < paramInt; k++) {
/*  614 */         paramStringBuilder.append(' ');
/*      */       }
/*  616 */       k = paramString.indexOf('\n', i);
/*  617 */       k = (k < 0) ? j : (k + 1);
/*  618 */       paramStringBuilder.append(paramString, i, k);
/*  619 */       i = k;
/*      */     } 
/*  621 */     if (paramBoolean && !paramString.endsWith("\n")) {
/*  622 */       paramStringBuilder.append('\n');
/*      */     }
/*  624 */     return paramStringBuilder;
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
/*      */   public static String xmlComment(String paramString) {
/*  636 */     int i = 0;
/*      */     while (true) {
/*  638 */       i = paramString.indexOf("--", i);
/*  639 */       if (i < 0) {
/*      */         break;
/*      */       }
/*  642 */       paramString = paramString.substring(0, i + 1) + " " + paramString.substring(i + 1);
/*      */     } 
/*      */ 
/*      */     
/*  646 */     if (paramString.indexOf('\n') >= 0) {
/*  647 */       StringBuilder stringBuilder = (new StringBuilder(paramString.length() + 18)).append("<!--\n");
/*  648 */       return indent(stringBuilder, paramString, 4, true).append("-->\n").toString();
/*      */     } 
/*  650 */     return "<!-- " + paramString + " -->\n";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String xmlCData(String paramString) {
/*  661 */     if (paramString.contains("]]>")) {
/*  662 */       return xmlText(paramString);
/*      */     }
/*  664 */     boolean bool = paramString.endsWith("\n");
/*  665 */     paramString = "<![CDATA[" + paramString + "]]>";
/*  666 */     return bool ? (paramString + "\n") : paramString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String xmlStartDoc() {
/*  674 */     return "<?xml version=\"1.0\"?>\n";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String xmlText(String paramString) {
/*  684 */     return xmlText(paramString, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String xmlText(String paramString, boolean paramBoolean) {
/*  695 */     int i = paramString.length();
/*  696 */     StringBuilder stringBuilder = new StringBuilder(i);
/*  697 */     for (byte b = 0; b < i; b++) {
/*  698 */       char c = paramString.charAt(b);
/*  699 */       switch (c) {
/*      */         case '<':
/*  701 */           stringBuilder.append("&lt;");
/*      */           break;
/*      */         case '>':
/*  704 */           stringBuilder.append("&gt;");
/*      */           break;
/*      */         case '&':
/*  707 */           stringBuilder.append("&amp;");
/*      */           break;
/*      */         
/*      */         case '\'':
/*  711 */           stringBuilder.append("&#39;");
/*      */           break;
/*      */         case '"':
/*  714 */           stringBuilder.append("&quot;");
/*      */           break;
/*      */         case '\n':
/*      */         case '\r':
/*  718 */           if (paramBoolean) {
/*  719 */             stringBuilder.append("&#x")
/*  720 */               .append(Integer.toHexString(c))
/*  721 */               .append(';'); break;
/*      */           } 
/*  723 */           stringBuilder.append(c);
/*      */           break;
/*      */         
/*      */         case '\t':
/*  727 */           stringBuilder.append(c);
/*      */           break;
/*      */         default:
/*  730 */           if (c < ' ' || c > '') {
/*  731 */             stringBuilder.append("&#x")
/*  732 */               .append(Integer.toHexString(c))
/*  733 */               .append(';'); break;
/*      */           } 
/*  735 */           stringBuilder.append(c);
/*      */           break;
/*      */       } 
/*      */     } 
/*  739 */     return stringBuilder.toString();
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
/*      */   public static String replaceAll(String paramString1, String paramString2, String paramString3) {
/*  754 */     int i = paramString1.indexOf(paramString2);
/*  755 */     if (i < 0 || paramString2.isEmpty()) {
/*  756 */       return paramString1;
/*      */     }
/*      */     
/*  759 */     StringBuilder stringBuilder = new StringBuilder(paramString1.length() - paramString2.length() + paramString3.length());
/*  760 */     int j = 0;
/*      */     while (true) {
/*  762 */       stringBuilder.append(paramString1, j, i).append(paramString3);
/*  763 */       j = i + paramString2.length();
/*  764 */       i = paramString1.indexOf(paramString2, j);
/*  765 */       if (i < 0) {
/*  766 */         stringBuilder.append(paramString1, j, paramString1.length());
/*      */ 
/*      */ 
/*      */         
/*  770 */         return stringBuilder.toString();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quoteIdentifier(String paramString) {
/*  781 */     return quoteIdentifierOrLiteral(new StringBuilder(paramString.length() + 2), paramString, '"').toString();
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
/*      */   public static StringBuilder quoteIdentifier(StringBuilder paramStringBuilder, String paramString) {
/*  794 */     return quoteIdentifierOrLiteral(paramStringBuilder, paramString, '"');
/*      */   }
/*      */   
/*      */   private static StringBuilder quoteIdentifierOrLiteral(StringBuilder paramStringBuilder, String paramString, char paramChar) {
/*  798 */     int i = paramStringBuilder.length();
/*  799 */     paramStringBuilder.append(paramChar);
/*  800 */     for (int j = 0, k = paramString.length(); j < k; ) {
/*  801 */       int m = paramString.codePointAt(j);
/*  802 */       j += Character.charCount(m);
/*  803 */       if (m < 32 || m > 127) {
/*      */         
/*  805 */         paramStringBuilder.setLength(i);
/*  806 */         paramStringBuilder.append("U&").append(paramChar);
/*  807 */         for (j = 0; j < k; ) {
/*  808 */           m = paramString.codePointAt(j);
/*  809 */           j += Character.charCount(m);
/*  810 */           if (m >= 32 && m < 127) {
/*  811 */             char c = (char)m;
/*  812 */             if (c == paramChar || c == '\\') {
/*  813 */               paramStringBuilder.append(c);
/*      */             }
/*  815 */             paramStringBuilder.append(c); continue;
/*  816 */           }  if (m <= 65535) {
/*  817 */             appendHex(paramStringBuilder.append('\\'), m, 2); continue;
/*      */           } 
/*  819 */           appendHex(paramStringBuilder.append("\\+"), m, 3);
/*      */         } 
/*      */         
/*      */         break;
/*      */       } 
/*  824 */       if (m == paramChar) {
/*  825 */         paramStringBuilder.append(paramChar);
/*      */       }
/*  827 */       paramStringBuilder.append((char)m);
/*      */     } 
/*  829 */     return paramStringBuilder.append(paramChar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNullOrEmpty(String paramString) {
/*  839 */     return (paramString == null || paramString.isEmpty());
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
/*      */   public static String pad(String paramString1, int paramInt, String paramString2, boolean paramBoolean) {
/*      */     char c;
/*  852 */     if (paramInt < 0) {
/*  853 */       paramInt = 0;
/*      */     }
/*  855 */     if (paramInt < paramString1.length())
/*  856 */       return paramString1.substring(0, paramInt); 
/*  857 */     if (paramInt == paramString1.length()) {
/*  858 */       return paramString1;
/*      */     }
/*      */     
/*  861 */     if (paramString2 == null || paramString2.isEmpty()) {
/*  862 */       c = ' ';
/*      */     } else {
/*  864 */       c = paramString2.charAt(0);
/*      */     } 
/*  866 */     StringBuilder stringBuilder = new StringBuilder(paramInt);
/*  867 */     paramInt -= paramString1.length();
/*  868 */     if (paramBoolean) {
/*  869 */       stringBuilder.append(paramString1);
/*      */     }
/*  871 */     for (byte b = 0; b < paramInt; b++) {
/*  872 */       stringBuilder.append(c);
/*      */     }
/*  874 */     if (!paramBoolean) {
/*  875 */       stringBuilder.append(paramString1);
/*      */     }
/*  877 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] cloneCharArray(char[] paramArrayOfchar) {
/*  888 */     if (paramArrayOfchar == null) {
/*  889 */       return null;
/*      */     }
/*  891 */     int i = paramArrayOfchar.length;
/*  892 */     if (i == 0) {
/*  893 */       return paramArrayOfchar;
/*      */     }
/*  895 */     return Arrays.copyOf(paramArrayOfchar, i);
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
/*      */   public static String trim(String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2) {
/*  910 */     boolean bool = (paramString2 == null || paramString2.isEmpty()) ? true : paramString2.charAt(0);
/*  911 */     byte b = 0; int i = paramString1.length();
/*  912 */     if (paramBoolean1) {
/*  913 */       while (b < i && paramString1.charAt(b) == bool) {
/*  914 */         b++;
/*      */       }
/*      */     }
/*  917 */     if (paramBoolean2) {
/*  918 */       while (i > b && paramString1.charAt(i - 1) == bool) {
/*  919 */         i--;
/*      */       }
/*      */     }
/*      */     
/*  923 */     return paramString1.substring(b, i);
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
/*      */   public static String trimSubstring(String paramString, int paramInt) {
/*  935 */     return trimSubstring(paramString, paramInt, paramString.length());
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
/*      */   public static String trimSubstring(String paramString, int paramInt1, int paramInt2) {
/*  948 */     while (paramInt1 < paramInt2 && paramString.charAt(paramInt1) <= ' ') {
/*  949 */       paramInt1++;
/*      */     }
/*  951 */     while (paramInt1 < paramInt2 && paramString.charAt(paramInt2 - 1) <= ' ') {
/*  952 */       paramInt2--;
/*      */     }
/*  954 */     return paramString.substring(paramInt1, paramInt2);
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
/*      */   public static StringBuilder trimSubstring(StringBuilder paramStringBuilder, String paramString, int paramInt1, int paramInt2) {
/*  969 */     while (paramInt1 < paramInt2 && paramString.charAt(paramInt1) <= ' ') {
/*  970 */       paramInt1++;
/*      */     }
/*  972 */     while (paramInt1 < paramInt2 && paramString.charAt(paramInt2 - 1) <= ' ') {
/*  973 */       paramInt2--;
/*      */     }
/*  975 */     return paramStringBuilder.append(paramString, paramInt1, paramInt2);
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
/*      */   public static String truncateString(String paramString, int paramInt) {
/*  992 */     if (paramString.length() > paramInt) {
/*  993 */       paramString = (paramInt > 0) ? paramString.substring(0, 
/*  994 */           Character.isSurrogatePair(paramString.charAt(paramInt - 1), paramString.charAt(paramInt)) ? (paramInt - 1) : paramInt) : "";
/*      */     }
/*      */ 
/*      */     
/*  998 */     return paramString;
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
/*      */   public static String cache(String paramString) {
/* 1010 */     if (!SysProperties.OBJECT_CACHE) {
/* 1011 */       return paramString;
/*      */     }
/* 1013 */     if (paramString == null)
/* 1014 */       return paramString; 
/* 1015 */     if (paramString.isEmpty()) {
/* 1016 */       return "";
/*      */     }
/* 1018 */     String[] arrayOfString = getCache();
/* 1019 */     if (arrayOfString != null) {
/* 1020 */       int i = paramString.hashCode();
/* 1021 */       int j = i & SysProperties.OBJECT_CACHE_SIZE - 1;
/* 1022 */       String str = arrayOfString[j];
/* 1023 */       if (paramString.equals(str)) {
/* 1024 */         return str;
/*      */       }
/* 1026 */       arrayOfString[j] = paramString;
/*      */     } 
/* 1028 */     return paramString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void clearCache() {
/* 1035 */     softCache = null;
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
/*      */   public static int parseUInt31(String paramString, int paramInt1, int paramInt2) {
/* 1047 */     if (paramInt2 > paramString.length() || paramInt1 < 0 || paramInt1 > paramInt2) {
/* 1048 */       throw new IndexOutOfBoundsException();
/*      */     }
/* 1050 */     if (paramInt1 == paramInt2) {
/* 1051 */       throw new NumberFormatException("");
/*      */     }
/* 1053 */     int i = 0;
/* 1054 */     for (int j = paramInt1; j < paramInt2; j++) {
/* 1055 */       char c = paramString.charAt(j);
/*      */ 
/*      */       
/* 1058 */       if (c < '0' || c > '9' || i > 214748364) {
/* 1059 */         throw new NumberFormatException(paramString.substring(paramInt1, paramInt2));
/*      */       }
/* 1061 */       i = i * 10 + c - 48;
/* 1062 */       if (i < 0)
/*      */       {
/* 1064 */         throw new NumberFormatException(paramString.substring(paramInt1, paramInt2));
/*      */       }
/*      */     } 
/* 1067 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] convertHexToBytes(String paramString) {
/* 1077 */     int i = paramString.length();
/* 1078 */     if (i % 2 != 0) {
/* 1079 */       throw DbException.get(90003, paramString);
/*      */     }
/* 1081 */     i /= 2;
/* 1082 */     byte[] arrayOfByte = new byte[i];
/* 1083 */     int j = 0;
/* 1084 */     int[] arrayOfInt = HEX_DECODE;
/*      */     try {
/* 1086 */       for (byte b = 0; b < i; b++) {
/* 1087 */         int k = arrayOfInt[paramString.charAt(b + b)] << 4 | arrayOfInt[paramString.charAt(b + b + 1)];
/* 1088 */         j |= k;
/* 1089 */         arrayOfByte[b] = (byte)k;
/*      */       } 
/* 1091 */     } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
/* 1092 */       throw DbException.get(90004, paramString);
/*      */     } 
/* 1094 */     if ((j & 0xFFFFFF00) != 0) {
/* 1095 */       throw DbException.get(90004, paramString);
/*      */     }
/* 1097 */     return arrayOfByte;
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
/*      */   public static ByteArrayOutputStream convertHexWithSpacesToBytes(ByteArrayOutputStream paramByteArrayOutputStream, String paramString, int paramInt1, int paramInt2) {
/* 1112 */     if (paramByteArrayOutputStream == null) {
/* 1113 */       paramByteArrayOutputStream = new ByteArrayOutputStream(paramInt2 - paramInt1 >>> 1);
/*      */     }
/* 1115 */     int i = 0;
/* 1116 */     int[] arrayOfInt = HEX_DECODE;
/*      */     try {
/* 1118 */       int j = paramInt1;
/*      */ 
/*      */       
/* 1121 */       while (j < paramInt2) {
/*      */ 
/*      */         
/* 1124 */         char c = paramString.charAt(j++);
/* 1125 */         if (c != ' ')
/*      */           while (true)
/* 1127 */           { if (j >= paramInt2) {
/* 1128 */               if (((i | arrayOfInt[c]) & 0xFFFFFF00) != 0) {
/* 1129 */                 throw getHexStringException(90004, paramString, paramInt1, paramInt2);
/*      */               }
/* 1131 */               throw getHexStringException(90003, paramString, paramInt1, paramInt2);
/*      */             } 
/* 1133 */             char c1 = paramString.charAt(j++);
/* 1134 */             if (c1 != ' ')
/* 1135 */             { int k = arrayOfInt[c] << 4 | arrayOfInt[c1];
/* 1136 */               i |= k;
/* 1137 */               paramByteArrayOutputStream.write(k); }  }  
/*      */       } 
/* 1139 */     } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
/* 1140 */       throw getHexStringException(90004, paramString, paramInt1, paramInt2);
/*      */     } 
/* 1142 */     if ((i & 0xFFFFFF00) != 0) {
/* 1143 */       throw getHexStringException(90004, paramString, paramInt1, paramInt2);
/*      */     }
/* 1145 */     return paramByteArrayOutputStream;
/*      */   }
/*      */   
/*      */   private static DbException getHexStringException(int paramInt1, String paramString, int paramInt2, int paramInt3) {
/* 1149 */     return DbException.get(paramInt1, paramString.substring(paramInt2, paramInt3));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String convertBytesToHex(byte[] paramArrayOfbyte) {
/* 1159 */     return convertBytesToHex(paramArrayOfbyte, paramArrayOfbyte.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String convertBytesToHex(byte[] paramArrayOfbyte, int paramInt) {
/* 1170 */     byte[] arrayOfByte = new byte[paramInt * 2];
/* 1171 */     char[] arrayOfChar = HEX;
/* 1172 */     for (byte b1 = 0, b2 = 0; b1 < paramInt; b1++) {
/* 1173 */       int i = paramArrayOfbyte[b1] & 0xFF;
/* 1174 */       arrayOfByte[b2++] = (byte)arrayOfChar[i >> 4];
/* 1175 */       arrayOfByte[b2++] = (byte)arrayOfChar[i & 0xF];
/*      */     } 
/* 1177 */     return new String(arrayOfByte, StandardCharsets.ISO_8859_1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder convertBytesToHex(StringBuilder paramStringBuilder, byte[] paramArrayOfbyte) {
/* 1188 */     return convertBytesToHex(paramStringBuilder, paramArrayOfbyte, paramArrayOfbyte.length);
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
/*      */   public static StringBuilder convertBytesToHex(StringBuilder paramStringBuilder, byte[] paramArrayOfbyte, int paramInt) {
/* 1200 */     char[] arrayOfChar = HEX;
/* 1201 */     for (byte b = 0; b < paramInt; b++) {
/* 1202 */       int i = paramArrayOfbyte[b] & 0xFF;
/* 1203 */       paramStringBuilder.append(arrayOfChar[i >>> 4]).append(arrayOfChar[i & 0xF]);
/*      */     } 
/* 1205 */     return paramStringBuilder;
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
/*      */   public static StringBuilder appendHex(StringBuilder paramStringBuilder, long paramLong, int paramInt) {
/* 1221 */     char[] arrayOfChar = HEX;
/* 1222 */     for (int i = paramInt * 8; i > 0; ) {
/* 1223 */       i -= 4; i -= 4; paramStringBuilder.append(arrayOfChar[(int)(paramLong >> i) & 0xF]).append(arrayOfChar[(int)(paramLong >> i) & 0xF]);
/*      */     } 
/* 1225 */     return paramStringBuilder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNumber(String paramString) {
/* 1235 */     int i = paramString.length();
/* 1236 */     if (i == 0) {
/* 1237 */       return false;
/*      */     }
/* 1239 */     for (byte b = 0; b < i; b++) {
/* 1240 */       if (!Character.isDigit(paramString.charAt(b))) {
/* 1241 */         return false;
/*      */       }
/*      */     } 
/* 1244 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWhitespaceOrEmpty(String paramString) {
/*      */     byte b;
/*      */     int i;
/* 1255 */     for (b = 0, i = paramString.length(); b < i; b++) {
/* 1256 */       if (paramString.charAt(b) > ' ') {
/* 1257 */         return false;
/*      */       }
/*      */     } 
/* 1260 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder appendTwoDigits(StringBuilder paramStringBuilder, int paramInt) {
/* 1271 */     if (paramInt < 10) {
/* 1272 */       paramStringBuilder.append('0');
/*      */     }
/* 1274 */     return paramStringBuilder.append(paramInt);
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
/*      */   public static StringBuilder appendZeroPadded(StringBuilder paramStringBuilder, int paramInt, long paramLong) {
/* 1286 */     String str = Long.toString(paramLong);
/* 1287 */     paramInt -= str.length();
/* 1288 */     for (; paramInt > 0; paramInt--) {
/* 1289 */       paramStringBuilder.append('0');
/*      */     }
/* 1291 */     return paramStringBuilder.append(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String escapeMetaDataPattern(String paramString) {
/* 1301 */     if (paramString == null || paramString.isEmpty()) {
/* 1302 */       return paramString;
/*      */     }
/* 1304 */     return replaceAll(paramString, "\\", "\\\\");
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */