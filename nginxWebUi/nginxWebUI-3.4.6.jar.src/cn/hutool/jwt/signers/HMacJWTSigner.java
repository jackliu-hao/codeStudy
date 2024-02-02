/*    */ package cn.hutool.jwt.signers;
/*    */ 
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.crypto.digest.HMac;
/*    */ import java.nio.charset.Charset;
/*    */ import java.security.Key;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HMacJWTSigner
/*    */   implements JWTSigner
/*    */ {
/* 18 */   private Charset charset = CharsetUtil.CHARSET_UTF_8;
/*    */ 
/*    */ 
/*    */   
/*    */   private final HMac hMac;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMacJWTSigner(String algorithm, byte[] key) {
/* 28 */     this.hMac = new HMac(algorithm, key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMacJWTSigner(String algorithm, Key key) {
/* 38 */     this.hMac = new HMac(algorithm, key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMacJWTSigner setCharset(Charset charset) {
/* 48 */     this.charset = charset;
/* 49 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String sign(String headerBase64, String payloadBase64) {
/* 54 */     return this.hMac.digestBase64(StrUtil.format("{}.{}", new Object[] { headerBase64, payloadBase64 }), this.charset, true);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
/* 59 */     String sign = sign(headerBase64, payloadBase64);
/* 60 */     return this.hMac.verify(
/* 61 */         StrUtil.bytes(sign, this.charset), 
/* 62 */         StrUtil.bytes(signBase64, this.charset));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAlgorithm() {
/* 67 */     return this.hMac.getAlgorithm();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\signers\HMacJWTSigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */