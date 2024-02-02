/*     */ package cn.hutool.core.net;
/*     */ 
/*     */ import cn.hutool.core.util.HexUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.BitSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class URLEncoder
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  44 */   public static final URLEncoder DEFAULT = createDefault();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   public static final URLEncoder PATH_SEGMENT = createPathSegment();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   public static final URLEncoder FRAGMENT = createFragment();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final URLEncoder QUERY = createQuery();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   public static final URLEncoder ALL = createAll();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final BitSet safeCharacters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URLEncoder createDefault() {
/* 115 */     URLEncoder encoder = new URLEncoder();
/* 116 */     encoder.addSafeCharacter('-');
/* 117 */     encoder.addSafeCharacter('.');
/* 118 */     encoder.addSafeCharacter('_');
/* 119 */     encoder.addSafeCharacter('~');
/*     */ 
/*     */     
/* 122 */     addSubDelims(encoder);
/*     */ 
/*     */     
/* 125 */     encoder.addSafeCharacter(':');
/* 126 */     encoder.addSafeCharacter('@');
/*     */ 
/*     */     
/* 129 */     encoder.addSafeCharacter('/');
/*     */     
/* 131 */     return encoder;
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
/*     */   public static URLEncoder createPathSegment() {
/* 149 */     URLEncoder encoder = new URLEncoder();
/*     */ 
/*     */     
/* 152 */     encoder.addSafeCharacter('-');
/* 153 */     encoder.addSafeCharacter('.');
/* 154 */     encoder.addSafeCharacter('_');
/* 155 */     encoder.addSafeCharacter('~');
/*     */ 
/*     */     
/* 158 */     addSubDelims(encoder);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 163 */     encoder.addSafeCharacter('@');
/*     */     
/* 165 */     return encoder;
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
/*     */   public static URLEncoder createFragment() {
/* 185 */     URLEncoder encoder = new URLEncoder();
/* 186 */     encoder.addSafeCharacter('-');
/* 187 */     encoder.addSafeCharacter('.');
/* 188 */     encoder.addSafeCharacter('_');
/* 189 */     encoder.addSafeCharacter('~');
/*     */ 
/*     */     
/* 192 */     addSubDelims(encoder);
/*     */ 
/*     */     
/* 195 */     encoder.addSafeCharacter(':');
/* 196 */     encoder.addSafeCharacter('@');
/*     */     
/* 198 */     encoder.addSafeCharacter('/');
/* 199 */     encoder.addSafeCharacter('?');
/*     */     
/* 201 */     return encoder;
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
/*     */   public static URLEncoder createQuery() {
/* 220 */     URLEncoder encoder = new URLEncoder();
/*     */     
/* 222 */     encoder.setEncodeSpaceAsPlus(true);
/*     */ 
/*     */     
/* 225 */     encoder.addSafeCharacter('*');
/* 226 */     encoder.addSafeCharacter('-');
/* 227 */     encoder.addSafeCharacter('.');
/* 228 */     encoder.addSafeCharacter('_');
/*     */     
/* 230 */     encoder.addSafeCharacter('=');
/* 231 */     encoder.addSafeCharacter('&');
/*     */     
/* 233 */     return encoder;
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
/*     */   public static URLEncoder createAll() {
/* 251 */     URLEncoder encoder = new URLEncoder();
/* 252 */     encoder.addSafeCharacter('*');
/* 253 */     encoder.addSafeCharacter('-');
/* 254 */     encoder.addSafeCharacter('.');
/* 255 */     encoder.addSafeCharacter('_');
/*     */     
/* 257 */     return encoder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean encodeSpaceAsPlus = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URLEncoder() {
/* 275 */     this(new BitSet(256));
/*     */ 
/*     */     
/* 278 */     addAlpha();
/* 279 */     addDigit();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private URLEncoder(BitSet safeCharacters) {
/* 288 */     this.safeCharacters = safeCharacters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSafeCharacter(char c) {
/* 298 */     this.safeCharacters.set(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeSafeCharacter(char c) {
/* 308 */     this.safeCharacters.clear(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
/* 317 */     this.encodeSpaceAsPlus = encodeSpaceAsPlus;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(String path, Charset charset) {
/* 328 */     if (null == charset || StrUtil.isEmpty(path)) {
/* 329 */       return path;
/*     */     }
/*     */     
/* 332 */     StringBuilder rewrittenPath = new StringBuilder(path.length());
/* 333 */     ByteArrayOutputStream buf = new ByteArrayOutputStream();
/* 334 */     OutputStreamWriter writer = new OutputStreamWriter(buf, charset);
/*     */ 
/*     */     
/* 337 */     for (int i = 0; i < path.length(); i++) {
/* 338 */       int c = path.charAt(i);
/* 339 */       if (this.safeCharacters.get(c)) {
/* 340 */         rewrittenPath.append((char)c);
/* 341 */       } else if (this.encodeSpaceAsPlus && c == 32) {
/*     */         
/* 343 */         rewrittenPath.append('+');
/*     */       } else {
/*     */         
/*     */         try {
/* 347 */           writer.write((char)c);
/* 348 */           writer.flush();
/* 349 */         } catch (IOException e) {
/* 350 */           buf.reset();
/*     */         } 
/*     */ 
/*     */         
/* 354 */         byte[] ba = buf.toByteArray();
/* 355 */         for (byte toEncode : ba) {
/*     */           
/* 357 */           rewrittenPath.append('%');
/* 358 */           HexUtil.appendHex(rewrittenPath, toEncode, false);
/*     */         } 
/* 360 */         buf.reset();
/*     */       } 
/*     */     } 
/* 363 */     return rewrittenPath.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void addAlpha() {
/*     */     char i;
/* 370 */     for (i = 'a'; i <= 'z'; i = (char)(i + 1)) {
/* 371 */       addSafeCharacter(i);
/*     */     }
/* 373 */     for (i = 'A'; i <= 'Z'; i = (char)(i + 1)) {
/* 374 */       addSafeCharacter(i);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addDigit() {
/* 382 */     for (char i = '0'; i <= '9'; i = (char)(i + 1)) {
/* 383 */       addSafeCharacter(i);
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
/*     */   private static void addSubDelims(URLEncoder encoder) {
/* 395 */     encoder.addSafeCharacter('!');
/* 396 */     encoder.addSafeCharacter('$');
/* 397 */     encoder.addSafeCharacter('&');
/* 398 */     encoder.addSafeCharacter('\'');
/* 399 */     encoder.addSafeCharacter('(');
/* 400 */     encoder.addSafeCharacter(')');
/* 401 */     encoder.addSafeCharacter('*');
/* 402 */     encoder.addSafeCharacter('+');
/* 403 */     encoder.addSafeCharacter(',');
/* 404 */     encoder.addSafeCharacter(';');
/* 405 */     encoder.addSafeCharacter('=');
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\URLEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */