package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;

@Table
public class CertCode extends BaseModel {
   String certId;
   String domain;
   String type;
   String value;

   public String getCertId() {
      return this.certId;
   }

   public void setCertId(String certId) {
      this.certId = certId;
   }

   public String getDomain() {
      return this.domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
