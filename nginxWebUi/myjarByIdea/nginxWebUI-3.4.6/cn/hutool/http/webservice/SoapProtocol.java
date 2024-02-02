package cn.hutool.http.webservice;

public enum SoapProtocol {
   SOAP_1_1("SOAP 1.1 Protocol"),
   SOAP_1_2("SOAP 1.2 Protocol");

   private final String value;

   private SoapProtocol(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
