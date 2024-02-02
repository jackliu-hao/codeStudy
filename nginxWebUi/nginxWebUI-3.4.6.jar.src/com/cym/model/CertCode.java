/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class CertCode
/*    */   extends BaseModel
/*    */ {
/*    */   String certId;
/*    */   String domain;
/*    */   String type;
/*    */   String value;
/*    */   
/*    */   public String getCertId() {
/* 30 */     return this.certId;
/*    */   }
/*    */   public void setCertId(String certId) {
/* 33 */     this.certId = certId;
/*    */   }
/*    */   public String getDomain() {
/* 36 */     return this.domain;
/*    */   }
/*    */   public void setDomain(String domain) {
/* 39 */     this.domain = domain;
/*    */   }
/*    */   public String getType() {
/* 42 */     return this.type;
/*    */   }
/*    */   public void setType(String type) {
/* 45 */     this.type = type;
/*    */   }
/*    */   public String getValue() {
/* 48 */     return this.value;
/*    */   }
/*    */   public void setValue(String value) {
/* 51 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\CertCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */