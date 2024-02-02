/*     */ package org.apache.commons.codec.language;
/*     */ 
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
/*     */ public class Soundex
/*     */   implements StringEncoder
/*     */ {
/*     */   public static final char SILENT_MARKER = '-';
/*     */   public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
/*  65 */   private static final char[] US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final Soundex US_ENGLISH = new Soundex();
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
/*  88 */   public static final Soundex US_ENGLISH_SIMPLIFIED = new Soundex("01230120022455012623010202", false);
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
/* 103 */   public static final Soundex US_ENGLISH_GENEALOGY = new Soundex("-123-12--22455-12623-1-2-2");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 111 */   private int maxLength = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final char[] soundexMapping;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean specialCaseHW;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Soundex() {
/* 136 */     this.soundexMapping = US_ENGLISH_MAPPING;
/* 137 */     this.specialCaseHW = true;
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
/*     */   public Soundex(char[] mapping) {
/* 153 */     this.soundexMapping = new char[mapping.length];
/* 154 */     System.arraycopy(mapping, 0, this.soundexMapping, 0, mapping.length);
/* 155 */     this.specialCaseHW = !hasMarker(this.soundexMapping);
/*     */   }
/*     */   
/*     */   private boolean hasMarker(char[] mapping) {
/* 159 */     for (char ch : mapping) {
/* 160 */       if (ch == '-') {
/* 161 */         return true;
/*     */       }
/*     */     } 
/* 164 */     return false;
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
/*     */   public Soundex(String mapping) {
/* 178 */     this.soundexMapping = mapping.toCharArray();
/* 179 */     this.specialCaseHW = !hasMarker(this.soundexMapping);
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
/*     */   public Soundex(String mapping, boolean specialCaseHW) {
/* 192 */     this.soundexMapping = mapping.toCharArray();
/* 193 */     this.specialCaseHW = specialCaseHW;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int difference(String s1, String s2) throws EncoderException {
/* 216 */     return SoundexUtils.difference(this, s1, s2);
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
/* 234 */     if (!(obj instanceof String)) {
/* 235 */       throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
/*     */     }
/* 237 */     return soundex((String)obj);
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
/* 251 */     return soundex(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getMaxLength() {
/* 262 */     return this.maxLength;
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
/*     */   private char map(char ch) {
/* 275 */     int index = ch - 65;
/* 276 */     if (index < 0 || index >= this.soundexMapping.length) {
/* 277 */       throw new IllegalArgumentException("The character is not mapped: " + ch + " (index=" + index + ")");
/*     */     }
/* 279 */     return this.soundexMapping[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setMaxLength(int maxLength) {
/* 291 */     this.maxLength = maxLength;
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
/*     */   public String soundex(String str) {
/* 304 */     if (str == null) {
/* 305 */       return null;
/*     */     }
/* 307 */     str = SoundexUtils.clean(str);
/* 308 */     if (str.length() == 0) {
/* 309 */       return str;
/*     */     }
/* 311 */     char[] out = { '0', '0', '0', '0' };
/* 312 */     int count = 0;
/* 313 */     char first = str.charAt(0);
/* 314 */     out[count++] = first;
/* 315 */     char lastDigit = map(first);
/* 316 */     for (int i = 1; i < str.length() && count < out.length; i++) {
/* 317 */       char ch = str.charAt(i);
/* 318 */       if (!this.specialCaseHW || (ch != 'H' && ch != 'W')) {
/*     */ 
/*     */         
/* 321 */         char digit = map(ch);
/* 322 */         if (digit != '-')
/*     */         
/*     */         { 
/* 325 */           if (digit != '0' && digit != lastDigit) {
/* 326 */             out[count++] = digit;
/*     */           }
/* 328 */           lastDigit = digit; } 
/*     */       } 
/* 330 */     }  return new String(out);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\Soundex.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */