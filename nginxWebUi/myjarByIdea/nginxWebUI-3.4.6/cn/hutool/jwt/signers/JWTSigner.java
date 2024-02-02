package cn.hutool.jwt.signers;

public interface JWTSigner {
   String sign(String var1, String var2);

   boolean verify(String var1, String var2, String var3);

   String getAlgorithm();

   default String getAlgorithmId() {
      return AlgorithmUtil.getId(this.getAlgorithm());
   }
}
