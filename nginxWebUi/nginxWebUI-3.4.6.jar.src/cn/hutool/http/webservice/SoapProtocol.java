/*    */ package cn.hutool.http.webservice;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum SoapProtocol
/*    */ {
/* 13 */   SOAP_1_1("SOAP 1.1 Protocol"),
/*    */   
/* 15 */   SOAP_1_2("SOAP 1.2 Protocol");
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */   
/*    */   SoapProtocol(String value) {
/* 23 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 34 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\webservice\SoapProtocol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */