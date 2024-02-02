/*     */ package com.sun.mail.auth;
/*     */ 
/*     */ import com.sun.mail.util.BASE64DecoderStream;
/*     */ import com.sun.mail.util.BASE64EncoderStream;
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Locale;
/*     */ import java.util.logging.Level;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.NoSuchPaddingException;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.SecretKeyFactory;
/*     */ import javax.crypto.spec.DESKeySpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Ntlm
/*     */ {
/*     */   private byte[] type1;
/*     */   private byte[] type3;
/*     */   private SecretKeyFactory fac;
/*     */   private Cipher cipher;
/*     */   private MD4 md4;
/*     */   private String hostname;
/*     */   private String ntdomain;
/*     */   private String username;
/*     */   private String password;
/*     */   private MailLogger logger;
/*     */   
/*     */   private void init0() {
/*  87 */     this.type1 = new byte[256];
/*  88 */     this.type3 = new byte[256];
/*  89 */     System.arraycopy(new byte[] { 78, 84, 76, 77, 83, 83, 80, 0, 1 }, 0, this.type1, 0, 9);
/*     */     
/*  91 */     this.type1[12] = 3;
/*  92 */     this.type1[13] = -78;
/*  93 */     this.type1[28] = 32;
/*  94 */     System.arraycopy(new byte[] { 78, 84, 76, 77, 83, 83, 80, 0, 3 }, 0, this.type3, 0, 9);
/*     */     
/*  96 */     this.type3[12] = 24;
/*  97 */     this.type3[14] = 24;
/*  98 */     this.type3[20] = 24;
/*  99 */     this.type3[22] = 24;
/* 100 */     this.type3[32] = 64;
/* 101 */     this.type3[60] = 1;
/* 102 */     this.type3[61] = -126;
/*     */     
/*     */     try {
/* 105 */       this.fac = SecretKeyFactory.getInstance("DES");
/* 106 */       this.cipher = Cipher.getInstance("DES/ECB/NoPadding");
/* 107 */       this.md4 = new MD4();
/* 108 */     } catch (NoSuchPaddingException e) {
/*     */       assert false;
/* 110 */     } catch (NoSuchAlgorithmException e) {
/*     */       assert false;
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
/*     */   public Ntlm(String ntdomain, String hostname, String username, String password, MailLogger logger) {
/* 123 */     int i = hostname.indexOf('.');
/* 124 */     if (i != -1) {
/* 125 */       hostname = hostname.substring(0, i);
/*     */     }
/* 127 */     i = username.indexOf('\\');
/* 128 */     if (i != -1) {
/* 129 */       ntdomain = username.substring(0, i).toUpperCase();
/* 130 */       username = username.substring(i + 1);
/* 131 */     } else if (ntdomain == null) {
/* 132 */       ntdomain = "";
/*     */     } 
/* 134 */     this.ntdomain = ntdomain;
/* 135 */     this.hostname = hostname;
/* 136 */     this.username = username;
/* 137 */     this.password = password;
/* 138 */     this.logger = logger.getLogger(getClass(), "DEBUG NTLM");
/* 139 */     init0();
/*     */   }
/*     */   
/*     */   private void copybytes(byte[] dest, int destpos, String src, String enc) {
/*     */     try {
/* 144 */       byte[] x = src.getBytes(enc);
/* 145 */       System.arraycopy(x, 0, dest, destpos, x.length);
/* 146 */     } catch (UnsupportedEncodingException e) {
/*     */       assert false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String generateType1Msg(int flags) {
/* 153 */     int dlen = this.ntdomain.length();
/* 154 */     this.type1[16] = (byte)(dlen % 256);
/* 155 */     this.type1[17] = (byte)(dlen / 256);
/* 156 */     this.type1[18] = this.type1[16];
/* 157 */     this.type1[19] = this.type1[17];
/* 158 */     if (dlen == 0) {
/* 159 */       this.type1[13] = (byte)(this.type1[13] & 0xFFFFFFEF);
/*     */     }
/* 161 */     int hlen = this.hostname.length();
/* 162 */     this.type1[24] = (byte)(hlen % 256);
/* 163 */     this.type1[25] = (byte)(hlen / 256);
/* 164 */     this.type1[26] = this.type1[24];
/* 165 */     this.type1[27] = this.type1[25];
/*     */     
/* 167 */     copybytes(this.type1, 32, this.hostname, "iso-8859-1");
/* 168 */     copybytes(this.type1, hlen + 32, this.ntdomain, "iso-8859-1");
/* 169 */     this.type1[20] = (byte)((hlen + 32) % 256);
/* 170 */     this.type1[21] = (byte)((hlen + 32) / 256);
/*     */     
/* 172 */     byte[] msg = new byte[32 + hlen + dlen];
/* 173 */     System.arraycopy(this.type1, 0, msg, 0, 32 + hlen + dlen);
/* 174 */     if (this.logger.isLoggable(Level.FINE)) {
/* 175 */       this.logger.fine("type 1 message: " + toHex(msg));
/*     */     }
/* 177 */     String result = null;
/*     */     try {
/* 179 */       result = new String(BASE64EncoderStream.encode(msg), "iso-8859-1");
/* 180 */     } catch (UnsupportedEncodingException e) {
/*     */       assert false;
/*     */     } 
/* 183 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] makeDesKey(byte[] input, int off) {
/* 191 */     int[] in = new int[input.length];
/* 192 */     for (int i = 0; i < in.length; i++) {
/* 193 */       in[i] = (input[i] < 0) ? (input[i] + 256) : input[i];
/*     */     }
/* 195 */     byte[] out = new byte[8];
/* 196 */     out[0] = (byte)in[off + 0];
/* 197 */     out[1] = (byte)(in[off + 0] << 7 & 0xFF | in[off + 1] >> 1);
/* 198 */     out[2] = (byte)(in[off + 1] << 6 & 0xFF | in[off + 2] >> 2);
/* 199 */     out[3] = (byte)(in[off + 2] << 5 & 0xFF | in[off + 3] >> 3);
/* 200 */     out[4] = (byte)(in[off + 3] << 4 & 0xFF | in[off + 4] >> 4);
/* 201 */     out[5] = (byte)(in[off + 4] << 3 & 0xFF | in[off + 5] >> 5);
/* 202 */     out[6] = (byte)(in[off + 5] << 2 & 0xFF | in[off + 6] >> 6);
/* 203 */     out[7] = (byte)(in[off + 6] << 1 & 0xFF);
/* 204 */     return out;
/*     */   }
/*     */   
/*     */   private byte[] calcLMHash() throws GeneralSecurityException {
/* 208 */     byte[] magic = { 75, 71, 83, 33, 64, 35, 36, 37 };
/* 209 */     byte[] pwb = null;
/*     */     try {
/* 211 */       pwb = this.password.toUpperCase(Locale.ENGLISH).getBytes("iso-8859-1");
/* 212 */     } catch (UnsupportedEncodingException ex) {
/*     */       assert false;
/*     */     } 
/*     */     
/* 216 */     byte[] pwb1 = new byte[14];
/* 217 */     int len = this.password.length();
/* 218 */     if (len > 14)
/* 219 */       len = 14; 
/* 220 */     System.arraycopy(pwb, 0, pwb1, 0, len);
/*     */     
/* 222 */     DESKeySpec dks1 = new DESKeySpec(makeDesKey(pwb1, 0));
/* 223 */     DESKeySpec dks2 = new DESKeySpec(makeDesKey(pwb1, 7));
/*     */     
/* 225 */     SecretKey key1 = this.fac.generateSecret(dks1);
/* 226 */     SecretKey key2 = this.fac.generateSecret(dks2);
/* 227 */     this.cipher.init(1, key1);
/* 228 */     byte[] out1 = this.cipher.doFinal(magic, 0, 8);
/* 229 */     this.cipher.init(1, key2);
/* 230 */     byte[] out2 = this.cipher.doFinal(magic, 0, 8);
/*     */     
/* 232 */     byte[] result = new byte[21];
/* 233 */     System.arraycopy(out1, 0, result, 0, 8);
/* 234 */     System.arraycopy(out2, 0, result, 8, 8);
/* 235 */     return result;
/*     */   }
/*     */   
/*     */   private byte[] calcNTHash() throws GeneralSecurityException {
/* 239 */     byte[] pw = null;
/*     */     try {
/* 241 */       pw = this.password.getBytes("UnicodeLittleUnmarked");
/* 242 */     } catch (UnsupportedEncodingException e) {
/*     */       assert false;
/*     */     } 
/* 245 */     byte[] out = this.md4.digest(pw);
/* 246 */     byte[] result = new byte[21];
/* 247 */     System.arraycopy(out, 0, result, 0, 16);
/* 248 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] calcResponse(byte[] key, byte[] text) throws GeneralSecurityException {
/* 258 */     assert key.length == 21;
/* 259 */     DESKeySpec dks1 = new DESKeySpec(makeDesKey(key, 0));
/* 260 */     DESKeySpec dks2 = new DESKeySpec(makeDesKey(key, 7));
/* 261 */     DESKeySpec dks3 = new DESKeySpec(makeDesKey(key, 14));
/* 262 */     SecretKey key1 = this.fac.generateSecret(dks1);
/* 263 */     SecretKey key2 = this.fac.generateSecret(dks2);
/* 264 */     SecretKey key3 = this.fac.generateSecret(dks3);
/* 265 */     this.cipher.init(1, key1);
/* 266 */     byte[] out1 = this.cipher.doFinal(text, 0, 8);
/* 267 */     this.cipher.init(1, key2);
/* 268 */     byte[] out2 = this.cipher.doFinal(text, 0, 8);
/* 269 */     this.cipher.init(1, key3);
/* 270 */     byte[] out3 = this.cipher.doFinal(text, 0, 8);
/* 271 */     byte[] result = new byte[24];
/* 272 */     System.arraycopy(out1, 0, result, 0, 8);
/* 273 */     System.arraycopy(out2, 0, result, 8, 8);
/* 274 */     System.arraycopy(out3, 0, result, 16, 8);
/* 275 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String generateType3Msg(String challenge) {
/*     */     try {
/* 283 */       byte[] type2 = null;
/*     */       try {
/* 285 */         type2 = BASE64DecoderStream.decode(challenge.getBytes("us-ascii"));
/* 286 */       } catch (UnsupportedEncodingException ex) {
/*     */         assert false;
/*     */       } 
/*     */       
/* 290 */       byte[] nonce = new byte[8];
/* 291 */       System.arraycopy(type2, 24, nonce, 0, 8);
/*     */       
/* 293 */       int ulen = this.username.length() * 2;
/* 294 */       this.type3[38] = (byte)(ulen % 256); this.type3[36] = (byte)(ulen % 256);
/* 295 */       this.type3[39] = (byte)(ulen / 256); this.type3[37] = (byte)(ulen / 256);
/* 296 */       int dlen = this.ntdomain.length() * 2;
/* 297 */       this.type3[30] = (byte)(dlen % 256); this.type3[28] = (byte)(dlen % 256);
/* 298 */       this.type3[31] = (byte)(dlen / 256); this.type3[29] = (byte)(dlen / 256);
/* 299 */       int hlen = this.hostname.length() * 2;
/* 300 */       this.type3[46] = (byte)(hlen % 256); this.type3[44] = (byte)(hlen % 256);
/* 301 */       this.type3[47] = (byte)(hlen / 256); this.type3[45] = (byte)(hlen / 256);
/*     */       
/* 303 */       int l = 64;
/* 304 */       copybytes(this.type3, l, this.ntdomain, "UnicodeLittleUnmarked");
/* 305 */       this.type3[32] = (byte)(l % 256);
/* 306 */       this.type3[33] = (byte)(l / 256);
/* 307 */       l += dlen;
/* 308 */       copybytes(this.type3, l, this.username, "UnicodeLittleUnmarked");
/* 309 */       this.type3[40] = (byte)(l % 256);
/* 310 */       this.type3[41] = (byte)(l / 256);
/* 311 */       l += ulen;
/* 312 */       copybytes(this.type3, l, this.hostname, "UnicodeLittleUnmarked");
/* 313 */       this.type3[48] = (byte)(l % 256);
/* 314 */       this.type3[49] = (byte)(l / 256);
/* 315 */       l += hlen;
/*     */       
/* 317 */       byte[] lmhash = calcLMHash();
/* 318 */       byte[] lmresponse = calcResponse(lmhash, nonce);
/* 319 */       byte[] nthash = calcNTHash();
/* 320 */       byte[] ntresponse = calcResponse(nthash, nonce);
/* 321 */       System.arraycopy(lmresponse, 0, this.type3, l, 24);
/* 322 */       this.type3[16] = (byte)(l % 256);
/* 323 */       this.type3[17] = (byte)(l / 256);
/* 324 */       l += 24;
/* 325 */       System.arraycopy(ntresponse, 0, this.type3, l, 24);
/* 326 */       this.type3[24] = (byte)(l % 256);
/* 327 */       this.type3[25] = (byte)(l / 256);
/* 328 */       l += 24;
/* 329 */       this.type3[56] = (byte)(l % 256);
/* 330 */       this.type3[57] = (byte)(l / 256);
/*     */       
/* 332 */       byte[] msg = new byte[l];
/* 333 */       System.arraycopy(this.type3, 0, msg, 0, l);
/* 334 */       if (this.logger.isLoggable(Level.FINE)) {
/* 335 */         this.logger.fine("type 3 message: " + toHex(msg));
/*     */       }
/* 337 */       String result = null;
/*     */       try {
/* 339 */         result = new String(BASE64EncoderStream.encode(msg), "iso-8859-1");
/* 340 */       } catch (UnsupportedEncodingException e) {
/*     */         assert false;
/*     */       } 
/* 343 */       return result;
/*     */     }
/* 345 */     catch (GeneralSecurityException ex) {
/*     */       
/* 347 */       this.logger.log(Level.FINE, "GeneralSecurityException", ex);
/* 348 */       return "";
/*     */     } 
/*     */   }
/*     */   
/* 352 */   private static char[] hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */   static final boolean $assertionsDisabled;
/*     */   
/*     */   private static String toHex(byte[] b) {
/* 356 */     StringBuffer sb = new StringBuffer(b.length * 3);
/* 357 */     for (int i = 0; i < b.length; i++)
/* 358 */       sb.append(hex[b[i] >> 4 & 0xF]).append(hex[b[i] & 0xF]).append(' '); 
/* 359 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\auth\Ntlm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */