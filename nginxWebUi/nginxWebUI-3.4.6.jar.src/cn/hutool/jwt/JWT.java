/*     */ package cn.hutool.jwt;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.exceptions.ValidateException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONObject;
/*     */ import cn.hutool.jwt.signers.AlgorithmUtil;
/*     */ import cn.hutool.jwt.signers.JWTSigner;
/*     */ import cn.hutool.jwt.signers.JWTSignerUtil;
/*     */ import cn.hutool.jwt.signers.NoneJWTSigner;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.Key;
/*     */ import java.security.KeyPair;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JWT
/*     */   implements RegisteredPayload<JWT>
/*     */ {
/*     */   private final JWTHeader header;
/*     */   private final JWTPayload payload;
/*     */   private Charset charset;
/*     */   private JWTSigner signer;
/*     */   private List<String> tokens;
/*     */   
/*     */   public static JWT create() {
/*  56 */     return new JWT();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWT of(String token) {
/*  66 */     return new JWT(token);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT() {
/*  73 */     this.header = new JWTHeader();
/*  74 */     this.payload = new JWTPayload();
/*  75 */     this.charset = CharsetUtil.CHARSET_UTF_8;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT(String token) {
/*  84 */     this();
/*  85 */     parse(token);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT parse(String token) {
/*  95 */     List<String> tokens = splitToken(token);
/*  96 */     this.tokens = tokens;
/*  97 */     this.header.parse(tokens.get(0), this.charset);
/*  98 */     this.payload.parse(tokens.get(1), this.charset);
/*  99 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT setCharset(Charset charset) {
/* 109 */     this.charset = charset;
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT setKey(byte[] key) {
/* 120 */     return setSigner(JWTSignerUtil.hs256(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT setSigner(String algorithmId, byte[] key) {
/* 131 */     return setSigner(JWTSignerUtil.createSigner(algorithmId, key));
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
/*     */   public JWT setSigner(String algorithmId, Key key) {
/* 143 */     return setSigner(JWTSignerUtil.createSigner(algorithmId, key));
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
/*     */   public JWT setSigner(String algorithmId, KeyPair keyPair) {
/* 155 */     return setSigner(JWTSignerUtil.createSigner(algorithmId, keyPair));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT setSigner(JWTSigner signer) {
/* 165 */     this.signer = signer;
/* 166 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWTSigner getSigner() {
/* 175 */     return this.signer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject getHeaders() {
/* 184 */     return this.header.getClaimsJson();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWTHeader getHeader() {
/* 194 */     return this.header;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getHeader(String name) {
/* 204 */     return this.header.getClaim(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAlgorithm() {
/* 214 */     return (String)this.header.getClaim(JWTHeader.ALGORITHM);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT setHeader(String name, Object value) {
/* 225 */     this.header.setClaim(name, value);
/* 226 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT addHeaders(Map<String, ?> headers) {
/* 236 */     this.header.addHeaders(headers);
/* 237 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject getPayloads() {
/* 246 */     return this.payload.getClaimsJson();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWTPayload getPayload() {
/* 256 */     return this.payload;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getPayload(String name) {
/* 266 */     return getPayload().getClaim(name);
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
/*     */   public JWT setPayload(String name, Object value) {
/* 278 */     this.payload.setClaim(name, value);
/* 279 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWT addPayloads(Map<String, ?> payloads) {
/* 289 */     this.payload.addPayloads(payloads);
/* 290 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String sign() {
/* 299 */     return sign(this.signer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String sign(JWTSigner signer) {
/* 309 */     Assert.notNull(signer, () -> new JWTException("No Signer provided!"));
/*     */ 
/*     */     
/* 312 */     String claim = (String)this.header.getClaim(JWTHeader.ALGORITHM);
/* 313 */     if (StrUtil.isBlank(claim)) {
/* 314 */       this.header.setClaim(JWTHeader.ALGORITHM, 
/* 315 */           AlgorithmUtil.getId(signer.getAlgorithm()));
/*     */     }
/*     */     
/* 318 */     String headerBase64 = Base64.encodeUrlSafe(this.header.toString(), this.charset);
/* 319 */     String payloadBase64 = Base64.encodeUrlSafe(this.payload.toString(), this.charset);
/* 320 */     String sign = signer.sign(headerBase64, payloadBase64);
/*     */     
/* 322 */     return StrUtil.format("{}.{}.{}", new Object[] { headerBase64, payloadBase64, sign });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean verify() {
/* 331 */     return verify(this.signer);
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
/*     */   public boolean validate(long leeway) {
/* 350 */     if (false == verify()) {
/* 351 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 356 */       JWTValidator.of(this).validateDate((Date)DateUtil.date(), leeway);
/* 357 */     } catch (ValidateException e) {
/* 358 */       return false;
/*     */     } 
/*     */     
/* 361 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean verify(JWTSigner signer) {
/*     */     NoneJWTSigner noneJWTSigner;
/* 371 */     if (null == signer)
/*     */     {
/* 373 */       noneJWTSigner = NoneJWTSigner.NONE;
/*     */     }
/*     */     
/* 376 */     List<String> tokens = this.tokens;
/* 377 */     if (CollUtil.isEmpty(tokens)) {
/* 378 */       throw new JWTException("No token to verify!");
/*     */     }
/* 380 */     return noneJWTSigner.verify(tokens.get(0), tokens.get(1), tokens.get(2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<String> splitToken(String token) {
/* 390 */     List<String> tokens = StrUtil.split(token, '.');
/* 391 */     if (3 != tokens.size()) {
/* 392 */       throw new JWTException("The token was expected 3 parts, but got {}.", new Object[] { Integer.valueOf(tokens.size()) });
/*     */     }
/* 394 */     return tokens;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\JWT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */