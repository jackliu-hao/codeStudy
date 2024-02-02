/*    */ package cn.hutool.jwt.signers;
/*    */ 
/*    */ import cn.hutool.core.codec.Base64;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.crypto.asymmetric.Sign;
/*    */ import java.nio.charset.Charset;
/*    */ import java.security.Key;
/*    */ import java.security.KeyPair;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AsymmetricJWTSigner
/*    */   implements JWTSigner
/*    */ {
/* 22 */   private Charset charset = CharsetUtil.CHARSET_UTF_8;
/*    */ 
/*    */ 
/*    */   
/*    */   private final Sign sign;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AsymmetricJWTSigner(String algorithm, Key key) {
/* 32 */     PublicKey publicKey = (key instanceof PublicKey) ? (PublicKey)key : null;
/* 33 */     PrivateKey privateKey = (key instanceof PrivateKey) ? (PrivateKey)key : null;
/* 34 */     this.sign = new Sign(algorithm, privateKey, publicKey);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AsymmetricJWTSigner(String algorithm, KeyPair keyPair) {
/* 44 */     this.sign = new Sign(algorithm, keyPair);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AsymmetricJWTSigner setCharset(Charset charset) {
/* 54 */     this.charset = charset;
/* 55 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String sign(String headerBase64, String payloadBase64) {
/* 60 */     return Base64.encodeUrlSafe(this.sign.sign(StrUtil.format("{}.{}", new Object[] { headerBase64, payloadBase64 })));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
/* 65 */     return this.sign.verify(
/* 66 */         StrUtil.bytes(StrUtil.format("{}.{}", new Object[] { headerBase64, payloadBase64 }), this.charset), 
/* 67 */         Base64.decode(signBase64));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAlgorithm() {
/* 72 */     return this.sign.getSignature().getAlgorithm();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\signers\AsymmetricJWTSigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */