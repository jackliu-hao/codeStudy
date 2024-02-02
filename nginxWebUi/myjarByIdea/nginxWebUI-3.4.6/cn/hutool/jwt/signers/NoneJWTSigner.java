package cn.hutool.jwt.signers;

import cn.hutool.core.util.StrUtil;

public class NoneJWTSigner implements JWTSigner {
   public static final String ID_NONE = "none";
   public static NoneJWTSigner NONE = new NoneJWTSigner();

   public String sign(String headerBase64, String payloadBase64) {
      return "";
   }

   public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
      return StrUtil.isEmpty(signBase64);
   }

   public String getAlgorithm() {
      return "none";
   }
}
