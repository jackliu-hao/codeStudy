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
/*     */ public class RefinedSoundex
/*     */   implements StringEncoder
/*     */ {
/*     */   public static final String US_ENGLISH_MAPPING_STRING = "01360240043788015936020505";
/*  57 */   private static final char[] US_ENGLISH_MAPPING = "01360240043788015936020505".toCharArray();
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
/*  70 */   public static final RefinedSoundex US_ENGLISH = new RefinedSoundex();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RefinedSoundex() {
/*  77 */     this.soundexMapping = US_ENGLISH_MAPPING;
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
/*     */   public RefinedSoundex(char[] mapping) {
/*  90 */     this.soundexMapping = new char[mapping.length];
/*  91 */     System.arraycopy(mapping, 0, this.soundexMapping, 0, mapping.length);
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
/*     */   public RefinedSoundex(String mapping) {
/* 103 */     this.soundexMapping = mapping.toCharArray();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public int difference(String s1, String s2) throws EncoderException {
/* 129 */     return SoundexUtils.difference(this, s1, s2);
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
/* 147 */     if (!(obj instanceof String)) {
/* 148 */       throw new EncoderException("Parameter supplied to RefinedSoundex encode is not of type java.lang.String");
/*     */     }
/* 150 */     return soundex((String)obj);
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
/*     */   public String encode(String str) {
/* 162 */     return soundex(str);
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
/*     */   char getMappingCode(char c) {
/* 175 */     if (!Character.isLetter(c)) {
/* 176 */       return Character.MIN_VALUE;
/*     */     }
/* 178 */     return this.soundexMapping[Character.toUpperCase(c) - 65];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String soundex(String str) {
/* 189 */     if (str == null) {
/* 190 */       return null;
/*     */     }
/* 192 */     str = SoundexUtils.clean(str);
/* 193 */     if (str.length() == 0) {
/* 194 */       return str;
/*     */     }
/*     */     
/* 197 */     StringBuilder sBuf = new StringBuilder();
/* 198 */     sBuf.append(str.charAt(0));
/*     */ 
/*     */     
/* 201 */     char last = '*';
/*     */     
/* 203 */     for (int i = 0; i < str.length(); i++) {
/*     */       
/* 205 */       char current = getMappingCode(str.charAt(i));
/* 206 */       if (current != last) {
/*     */         
/* 208 */         if (current != '\000') {
/* 209 */           sBuf.append(current);
/*     */         }
/*     */         
/* 212 */         last = current;
/*     */       } 
/*     */     } 
/*     */     
/* 216 */     return sBuf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\RefinedSoundex.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */