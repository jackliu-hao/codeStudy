package cn.hutool.jwt.signers;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import java.nio.charset.Charset;
import java.security.Key;

public class HMacJWTSigner implements JWTSigner {
   private Charset charset;
   private final HMac hMac;

   public HMacJWTSigner(String algorithm, byte[] key) {
      this.charset = CharsetUtil.CHARSET_UTF_8;
      this.hMac = new HMac(algorithm, key);
   }

   public HMacJWTSigner(String algorithm, Key key) {
      this.charset = CharsetUtil.CHARSET_UTF_8;
      this.hMac = new HMac(algorithm, key);
   }

   public HMacJWTSigner setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public String sign(String headerBase64, String payloadBase64) {
      return this.hMac.digestBase64(StrUtil.format("{}.{}", new Object[]{headerBase64, payloadBase64}), this.charset, true);
   }

   public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
      String sign = this.sign(headerBase64, payloadBase64);
      return this.hMac.verify(StrUtil.bytes(sign, this.charset), StrUtil.bytes(signBase64, this.charset));
   }

   public String getAlgorithm() {
      return this.hMac.getAlgorithm();
   }
}
