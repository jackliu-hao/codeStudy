/*     */ package cn.hutool.crypto.symmetric.fpe;
/*     */ 
/*     */ import cn.hutool.crypto.KeyUtil;
/*     */ import cn.hutool.crypto.Padding;
/*     */ import cn.hutool.crypto.symmetric.AES;
/*     */ import java.io.Serializable;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import org.bouncycastle.crypto.AlphabetMapper;
/*     */ import org.bouncycastle.jcajce.spec.FPEParameterSpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FPE
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final AES aes;
/*     */   private final AlphabetMapper mapper;
/*     */   
/*     */   public FPE(FPEMode mode, byte[] key, AlphabetMapper mapper) {
/*  48 */     this(mode, key, mapper, null);
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
/*     */   public FPE(FPEMode mode, byte[] key, AlphabetMapper mapper, byte[] tweak) {
/*  60 */     if (null == mode) {
/*  61 */       mode = FPEMode.FF1;
/*     */     }
/*     */     
/*  64 */     if (null == tweak)
/*  65 */       switch (mode) {
/*     */         case FF1:
/*  67 */           tweak = new byte[0];
/*     */           break;
/*     */         
/*     */         case FF3_1:
/*  71 */           tweak = new byte[7];
/*     */           break;
/*     */       }  
/*  74 */     this
/*     */       
/*  76 */       .aes = new AES(mode.value, Padding.NoPadding.name(), KeyUtil.generateKey(mode.value, key), (AlgorithmParameterSpec)new FPEParameterSpec(mapper.getRadix(), tweak));
/*  77 */     this.mapper = mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encrypt(String data) {
/*  87 */     if (null == data) {
/*  88 */       return null;
/*     */     }
/*  90 */     return new String(encrypt(data.toCharArray()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] encrypt(char[] data) {
/* 100 */     if (null == data) {
/* 101 */       return null;
/*     */     }
/*     */     
/* 104 */     return this.mapper.convertToChars(this.aes.encrypt(this.mapper.convertToIndexes(data)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String decrypt(String data) {
/* 114 */     if (null == data) {
/* 115 */       return null;
/*     */     }
/* 117 */     return new String(decrypt(data.toCharArray()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] decrypt(char[] data) {
/* 127 */     if (null == data) {
/* 128 */       return null;
/*     */     }
/*     */     
/* 131 */     return this.mapper.convertToChars(this.aes.decrypt(this.mapper.convertToIndexes(data)));
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
/*     */   public enum FPEMode
/*     */   {
/* 144 */     FF1("FF1"),
/*     */ 
/*     */ 
/*     */     
/* 148 */     FF3_1("FF3-1");
/*     */     
/*     */     private final String value;
/*     */     
/*     */     FPEMode(String name) {
/* 153 */       this.value = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getValue() {
/* 162 */       return this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\fpe\FPE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */