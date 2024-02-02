package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Stream extends BaseModel {
   String name;
   String value;
   @JsonIgnore
   Long seq;

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

   public Long getSeq() {
      return this.seq;
   }

   public void setSeq(Long seq) {
      this.seq = seq;
   }
}
