package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Upstream extends BaseModel {
   String name;
   String tactics;
   @InitValue("0")
   Integer proxyType;
   @InitValue("0")
   Integer monitor;
   String descr;
   @JsonIgnore
   Long seq;

   public String getDescr() {
      return this.descr;
   }

   public void setDescr(String descr) {
      this.descr = descr;
   }

   public Long getSeq() {
      return this.seq;
   }

   public void setSeq(Long seq) {
      this.seq = seq;
   }

   public Integer getMonitor() {
      return this.monitor;
   }

   public void setMonitor(Integer monitor) {
      this.monitor = monitor;
   }

   public Integer getProxyType() {
      return this.proxyType;
   }

   public void setProxyType(Integer proxyType) {
      this.proxyType = proxyType;
   }

   public String getTactics() {
      return this.tactics;
   }

   public void setTactics(String tactics) {
      this.tactics = tactics;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
