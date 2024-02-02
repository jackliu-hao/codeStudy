package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Http extends BaseModel {
   String name;
   String value;
   String unit;
   @JsonIgnore
   Long seq;

   public Http() {
   }

   public Http(String name, String value, Long seq) {
      this.name = name;
      this.value = value;
      this.seq = seq;
   }

   public Long getSeq() {
      return this.seq;
   }

   public void setSeq(Long seq) {
      this.seq = seq;
   }

   public String getUnit() {
      return this.unit;
   }

   public void setUnit(String unit) {
      this.unit = unit;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
