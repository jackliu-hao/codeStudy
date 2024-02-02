/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
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
/*     */ public class Nysiis
/*     */   implements StringEncoder
/*     */ {
/*  71 */   private static final char[] CHARS_A = new char[] { 'A' };
/*  72 */   private static final char[] CHARS_AF = new char[] { 'A', 'F' };
/*  73 */   private static final char[] CHARS_C = new char[] { 'C' };
/*  74 */   private static final char[] CHARS_FF = new char[] { 'F', 'F' };
/*  75 */   private static final char[] CHARS_G = new char[] { 'G' };
/*  76 */   private static final char[] CHARS_N = new char[] { 'N' };
/*  77 */   private static final char[] CHARS_NN = new char[] { 'N', 'N' };
/*  78 */   private static final char[] CHARS_S = new char[] { 'S' };
/*  79 */   private static final char[] CHARS_SSS = new char[] { 'S', 'S', 'S' };
/*     */   
/*  81 */   private static final Pattern PAT_MAC = Pattern.compile("^MAC");
/*  82 */   private static final Pattern PAT_KN = Pattern.compile("^KN");
/*  83 */   private static final Pattern PAT_K = Pattern.compile("^K");
/*  84 */   private static final Pattern PAT_PH_PF = Pattern.compile("^(PH|PF)");
/*  85 */   private static final Pattern PAT_SCH = Pattern.compile("^SCH");
/*  86 */   private static final Pattern PAT_EE_IE = Pattern.compile("(EE|IE)$");
/*  87 */   private static final Pattern PAT_DT_ETC = Pattern.compile("(DT|RT|RD|NT|ND)$");
/*     */ 
/*     */   
/*     */   private static final char SPACE = ' ';
/*     */ 
/*     */   
/*     */   private static final int TRUE_LENGTH = 6;
/*     */ 
/*     */   
/*     */   private final boolean strict;
/*     */ 
/*     */   
/*     */   private static boolean isVowel(char c) {
/* 100 */     return (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U');
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
/*     */ 
/*     */   
/*     */   private static char[] transcodeRemaining(char prev, char curr, char next, char aNext) {
/* 119 */     if (curr == 'E' && next == 'V') {
/* 120 */       return CHARS_AF;
/*     */     }
/*     */ 
/*     */     
/* 124 */     if (isVowel(curr)) {
/* 125 */       return CHARS_A;
/*     */     }
/*     */ 
/*     */     
/* 129 */     if (curr == 'Q')
/* 130 */       return CHARS_G; 
/* 131 */     if (curr == 'Z')
/* 132 */       return CHARS_S; 
/* 133 */     if (curr == 'M') {
/* 134 */       return CHARS_N;
/*     */     }
/*     */ 
/*     */     
/* 138 */     if (curr == 'K') {
/* 139 */       if (next == 'N') {
/* 140 */         return CHARS_NN;
/*     */       }
/* 142 */       return CHARS_C;
/*     */     } 
/*     */ 
/*     */     
/* 146 */     if (curr == 'S' && next == 'C' && aNext == 'H') {
/* 147 */       return CHARS_SSS;
/*     */     }
/*     */ 
/*     */     
/* 151 */     if (curr == 'P' && next == 'H') {
/* 152 */       return CHARS_FF;
/*     */     }
/*     */ 
/*     */     
/* 156 */     if (curr == 'H' && (!isVowel(prev) || !isVowel(next))) {
/* 157 */       return new char[] { prev };
/*     */     }
/*     */ 
/*     */     
/* 161 */     if (curr == 'W' && isVowel(prev)) {
/* 162 */       return new char[] { prev };
/*     */     }
/*     */     
/* 165 */     return new char[] { curr };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Nysiis() {
/* 176 */     this(true);
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
/*     */   public Nysiis(boolean strict) {
/* 191 */     this.strict = strict;
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
/*     */   
/*     */   public Object encode(Object obj) throws EncoderException {
/* 209 */     if (!(obj instanceof String)) {
/* 210 */       throw new EncoderException("Parameter supplied to Nysiis encode is not of type java.lang.String");
/*     */     }
/* 212 */     return nysiis((String)obj);
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
/*     */   public String encode(String str) {
/* 226 */     return nysiis(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStrict() {
/* 235 */     return this.strict;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String nysiis(String str) {
/* 246 */     if (str == null) {
/* 247 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 251 */     str = SoundexUtils.clean(str);
/*     */     
/* 253 */     if (str.length() == 0) {
/* 254 */       return str;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 259 */     str = PAT_MAC.matcher(str).replaceFirst("MCC");
/* 260 */     str = PAT_KN.matcher(str).replaceFirst("NN");
/* 261 */     str = PAT_K.matcher(str).replaceFirst("C");
/* 262 */     str = PAT_PH_PF.matcher(str).replaceFirst("FF");
/* 263 */     str = PAT_SCH.matcher(str).replaceFirst("SSS");
/*     */ 
/*     */ 
/*     */     
/* 267 */     str = PAT_EE_IE.matcher(str).replaceFirst("Y");
/* 268 */     str = PAT_DT_ETC.matcher(str).replaceFirst("D");
/*     */ 
/*     */     
/* 271 */     StringBuilder key = new StringBuilder(str.length());
/* 272 */     key.append(str.charAt(0));
/*     */ 
/*     */     
/* 275 */     char[] chars = str.toCharArray();
/* 276 */     int len = chars.length;
/*     */     
/* 278 */     for (int i = 1; i < len; i++) {
/* 279 */       char next = (i < len - 1) ? chars[i + 1] : ' ';
/* 280 */       char aNext = (i < len - 2) ? chars[i + 2] : ' ';
/* 281 */       char[] transcoded = transcodeRemaining(chars[i - 1], chars[i], next, aNext);
/* 282 */       System.arraycopy(transcoded, 0, chars, i, transcoded.length);
/*     */ 
/*     */       
/* 285 */       if (chars[i] != chars[i - 1]) {
/* 286 */         key.append(chars[i]);
/*     */       }
/*     */     } 
/*     */     
/* 290 */     if (key.length() > 1) {
/* 291 */       char lastChar = key.charAt(key.length() - 1);
/*     */ 
/*     */       
/* 294 */       if (lastChar == 'S') {
/* 295 */         key.deleteCharAt(key.length() - 1);
/* 296 */         lastChar = key.charAt(key.length() - 1);
/*     */       } 
/*     */       
/* 299 */       if (key.length() > 2) {
/* 300 */         char last2Char = key.charAt(key.length() - 2);
/*     */         
/* 302 */         if (last2Char == 'A' && lastChar == 'Y') {
/* 303 */           key.deleteCharAt(key.length() - 2);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 308 */       if (lastChar == 'A') {
/* 309 */         key.deleteCharAt(key.length() - 1);
/*     */       }
/*     */     } 
/*     */     
/* 313 */     String string = key.toString();
/* 314 */     return isStrict() ? string.substring(0, Math.min(6, string.length())) : string;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\Nysiis.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */