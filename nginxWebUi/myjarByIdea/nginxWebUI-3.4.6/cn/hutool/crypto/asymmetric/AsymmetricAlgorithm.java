package cn.hutool.crypto.asymmetric;

public enum AsymmetricAlgorithm {
   RSA("RSA"),
   RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
   RSA_ECB("RSA/ECB/NoPadding"),
   RSA_None("RSA/None/NoPadding");

   private final String value;

   private AsymmetricAlgorithm(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
