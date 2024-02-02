package cn.hutool.jwt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.signers.AlgorithmUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.hutool.jwt.signers.NoneJWTSigner;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyPair;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JWT implements RegisteredPayload<JWT> {
   private final JWTHeader header;
   private final JWTPayload payload;
   private Charset charset;
   private JWTSigner signer;
   private List<String> tokens;

   public static JWT create() {
      return new JWT();
   }

   public static JWT of(String token) {
      return new JWT(token);
   }

   public JWT() {
      this.header = new JWTHeader();
      this.payload = new JWTPayload();
      this.charset = CharsetUtil.CHARSET_UTF_8;
   }

   public JWT(String token) {
      this();
      this.parse(token);
   }

   public JWT parse(String token) {
      List<String> tokens = splitToken(token);
      this.tokens = tokens;
      this.header.parse((String)tokens.get(0), this.charset);
      this.payload.parse((String)tokens.get(1), this.charset);
      return this;
   }

   public JWT setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public JWT setKey(byte[] key) {
      return this.setSigner(JWTSignerUtil.hs256(key));
   }

   public JWT setSigner(String algorithmId, byte[] key) {
      return this.setSigner(JWTSignerUtil.createSigner(algorithmId, key));
   }

   public JWT setSigner(String algorithmId, Key key) {
      return this.setSigner(JWTSignerUtil.createSigner(algorithmId, key));
   }

   public JWT setSigner(String algorithmId, KeyPair keyPair) {
      return this.setSigner(JWTSignerUtil.createSigner(algorithmId, keyPair));
   }

   public JWT setSigner(JWTSigner signer) {
      this.signer = signer;
      return this;
   }

   public JWTSigner getSigner() {
      return this.signer;
   }

   public JSONObject getHeaders() {
      return this.header.getClaimsJson();
   }

   public JWTHeader getHeader() {
      return this.header;
   }

   public Object getHeader(String name) {
      return this.header.getClaim(name);
   }

   public String getAlgorithm() {
      return (String)this.header.getClaim(JWTHeader.ALGORITHM);
   }

   public JWT setHeader(String name, Object value) {
      this.header.setClaim(name, value);
      return this;
   }

   public JWT addHeaders(Map<String, ?> headers) {
      this.header.addHeaders(headers);
      return this;
   }

   public JSONObject getPayloads() {
      return this.payload.getClaimsJson();
   }

   public JWTPayload getPayload() {
      return this.payload;
   }

   public Object getPayload(String name) {
      return this.getPayload().getClaim(name);
   }

   public JWT setPayload(String name, Object value) {
      this.payload.setClaim(name, value);
      return this;
   }

   public JWT addPayloads(Map<String, ?> payloads) {
      this.payload.addPayloads(payloads);
      return this;
   }

   public String sign() {
      return this.sign(this.signer);
   }

   public String sign(JWTSigner signer) {
      Assert.notNull(signer, () -> {
         return new JWTException("No Signer provided!");
      });
      String claim = (String)this.header.getClaim(JWTHeader.ALGORITHM);
      if (StrUtil.isBlank(claim)) {
         this.header.setClaim(JWTHeader.ALGORITHM, AlgorithmUtil.getId(signer.getAlgorithm()));
      }

      String headerBase64 = Base64.encodeUrlSafe(this.header.toString(), (Charset)this.charset);
      String payloadBase64 = Base64.encodeUrlSafe(this.payload.toString(), (Charset)this.charset);
      String sign = signer.sign(headerBase64, payloadBase64);
      return StrUtil.format("{}.{}.{}", new Object[]{headerBase64, payloadBase64, sign});
   }

   public boolean verify() {
      return this.verify(this.signer);
   }

   public boolean validate(long leeway) {
      if (!this.verify()) {
         return false;
      } else {
         try {
            JWTValidator.of(this).validateDate(DateUtil.date(), leeway);
            return true;
         } catch (ValidateException var4) {
            return false;
         }
      }
   }

   public boolean verify(JWTSigner signer) {
      if (null == signer) {
         signer = NoneJWTSigner.NONE;
      }

      List<String> tokens = this.tokens;
      if (CollUtil.isEmpty((Collection)tokens)) {
         throw new JWTException("No token to verify!");
      } else {
         return ((JWTSigner)signer).verify((String)tokens.get(0), (String)tokens.get(1), (String)tokens.get(2));
      }
   }

   private static List<String> splitToken(String token) {
      List<String> tokens = StrUtil.split(token, '.');
      if (3 != tokens.size()) {
         throw new JWTException("The token was expected 3 parts, but got {}.", new Object[]{tokens.size()});
      } else {
         return tokens;
      }
   }
}
