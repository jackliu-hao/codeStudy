package cn.hutool.crypto.asymmetric;

public enum SignAlgorithm {
   NONEwithRSA("NONEwithRSA"),
   MD2withRSA("MD2withRSA"),
   MD5withRSA("MD5withRSA"),
   SHA1withRSA("SHA1withRSA"),
   SHA256withRSA("SHA256withRSA"),
   SHA384withRSA("SHA384withRSA"),
   SHA512withRSA("SHA512withRSA"),
   NONEwithDSA("NONEwithDSA"),
   SHA1withDSA("SHA1withDSA"),
   NONEwithECDSA("NONEwithECDSA"),
   SHA1withECDSA("SHA1withECDSA"),
   SHA256withECDSA("SHA256withECDSA"),
   SHA384withECDSA("SHA384withECDSA"),
   SHA512withECDSA("SHA512withECDSA"),
   SHA256withRSA_PSS("SHA256WithRSA/PSS"),
   SHA384withRSA_PSS("SHA384WithRSA/PSS"),
   SHA512withRSA_PSS("SHA512WithRSA/PSS");

   private final String value;

   private SignAlgorithm(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
