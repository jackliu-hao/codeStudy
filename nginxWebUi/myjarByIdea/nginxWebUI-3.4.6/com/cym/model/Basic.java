package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Basic extends BaseModel {
   String name;
   String value;
   @JsonIgnore
   Long seq;

   public Basic() {
   }

   public Basic(String name, String value, Long seq) {
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
