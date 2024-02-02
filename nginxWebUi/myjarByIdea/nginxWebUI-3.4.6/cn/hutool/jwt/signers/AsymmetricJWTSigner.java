package cn.hutool.jwt.signers;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.Sign;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class AsymmetricJWTSigner implements JWTSigner {
   private Charset charset;
   private final Sign sign;

   public AsymmetricJWTSigner(String algorithm, Key key) {
      this.charset = CharsetUtil.CHARSET_UTF_8;
      PublicKey publicKey = key instanceof PublicKey ? (PublicKey)key : null;
      PrivateKey privateKey = key instanceof PrivateKey ? (PrivateKey)key : null;
      this.sign = new Sign(algorithm, privateKey, publicKey);
   }

   public AsymmetricJWTSigner(String algorithm, KeyPair keyPair) {
      this.charset = CharsetUtil.CHARSET_UTF_8;
      this.sign = new Sign(algorithm, keyPair);
   }

   public AsymmetricJWTSigner setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public String sign(String headerBase64, String payloadBase64) {
      return Base64.encodeUrlSafe(this.sign.sign(StrUtil.format("{}.{}", new Object[]{headerBase64, payloadBase64})));
   }

   public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
      return this.sign.verify(StrUtil.bytes(StrUtil.format("{}.{}", new Object[]{headerBase64, payloadBase64}), this.charset), Base64.decode((CharSequence)signBase64));
   }

   public String getAlgorithm() {
      return this.sign.getSignature().getAlgorithm();
   }
}
