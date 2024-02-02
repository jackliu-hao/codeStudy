/*     */ package cn.hutool.core.codec;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
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
/*     */ public class PercentCodec
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final BitSet safeCharacters;
/*     */   
/*     */   public static PercentCodec of(PercentCodec codec) {
/*  44 */     return new PercentCodec((BitSet)codec.safeCharacters.clone());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PercentCodec of(CharSequence chars) {
/*  54 */     PercentCodec codec = new PercentCodec();
/*  55 */     int length = chars.length();
/*  56 */     for (int i = 0; i < length; i++) {
/*  57 */       codec.addSafe(chars.charAt(i));
/*     */     }
/*  59 */     return codec;
/*     */   }
/*     */ 
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
/*     */   
/*     */   public PercentCodec() {
/*  79 */     this(new BitSet(256));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PercentCodec(BitSet safeCharacters) {
/*  88 */     this.safeCharacters = safeCharacters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PercentCodec addSafe(char c) {
/*  99 */     this.safeCharacters.set(c);
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PercentCodec removeSafe(char c) {
/* 111 */     this.safeCharacters.clear(c);
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PercentCodec or(PercentCodec codec) {
/* 122 */     this.safeCharacters.or(codec.safeCharacters);
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PercentCodec orNew(PercentCodec codec) {
/* 133 */     return of(this).or(codec);
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
/*     */   public PercentCodec setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
/* 145 */     this.encodeSpaceAsPlus = encodeSpaceAsPlus;
/* 146 */     return this;
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
/*     */   public String encode(CharSequence path, Charset charset, char... customSafeChar) {
/* 158 */     if (null == charset || StrUtil.isEmpty(path)) {
/* 159 */       return StrUtil.str(path);
/*     */     }
/*     */     
/* 162 */     StringBuilder rewrittenPath = new StringBuilder(path.length());
/* 163 */     ByteArrayOutputStream buf = new ByteArrayOutputStream();
/* 164 */     OutputStreamWriter writer = new OutputStreamWriter(buf, charset);
/*     */ 
/*     */     
/* 167 */     for (int i = 0; i < path.length(); i++) {
/* 168 */       char c = path.charAt(i);
/* 169 */       if (this.safeCharacters.get(c) || ArrayUtil.contains(customSafeChar, c)) {
/* 170 */         rewrittenPath.append(c);
/* 171 */       } else if (this.encodeSpaceAsPlus && c == ' ') {
/*     */         
/* 173 */         rewrittenPath.append('+');
/*     */       } else {
/*     */         
/*     */         try {
/* 177 */           writer.write(c);
/* 178 */           writer.flush();
/* 179 */         } catch (IOException e) {
/* 180 */           buf.reset();
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 185 */         byte[] ba = buf.toByteArray();
/* 186 */         for (byte toEncode : ba) {
/*     */           
/* 188 */           rewrittenPath.append('%');
/* 189 */           HexUtil.appendHex(rewrittenPath, toEncode, false);
/*     */         } 
/* 191 */         buf.reset();
/*     */       } 
/*     */     } 
/* 194 */     return rewrittenPath.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\PercentCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */