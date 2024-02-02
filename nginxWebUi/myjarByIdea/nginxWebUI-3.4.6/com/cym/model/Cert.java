package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;

@Table
public class Cert extends BaseModel {
   String domain;
   String pem;
   String key;
   @InitValue("0")
   Integer type;
   @InitValue("RSA")
   String encryption;
   Long makeTime;
   Long endTime;
   @InitValue("0")
   Integer autoRenew;
   String dnsType;
   String dpId;
   String dpKey;
   String aliKey;
   String aliSecret;
   String cfEmail;
   String cfKey;
   String gdKey;
   String gdSecret;
   String hwUsername;
   String hwPassword;
   String hwProjectId;
   String hwDomainName;

   public String getHwDomainName() {
      return this.hwDomainName;
   }

   public void setHwDomainName(String hwDomainName) {
      this.hwDomainName = hwDomainName;
   }

   public Long getEndTime() {
      return this.endTime;
   }

   public void setEndTime(Long endTime) {
      this.endTime = endTime;
   }

   public String getEncryption() {
      return this.encryption;
   }

   public void setEncryption(String encryption) {
      this.encryption = encryption;
   }

   public String getHwUsername() {
      return this.hwUsername;
   }

   public void setHwUsername(String hwUsername) {
      this.hwUsername = hwUsername;
   }

   public String getHwPassword() {
      return this.hwPassword;
   }

   public void setHwPassword(String hwPassword) {
      this.hwPassword = hwPassword;
   }

   public String getHwProjectId() {
      return this.hwProjectId;
   }

   public void setHwProjectId(String hwProjectId) {
      this.hwProjectId = hwProjectId;
   }

   public String getGdKey() {
      return this.gdKey;
   }

   public void setGdKey(String gdKey) {
      this.gdKey = gdKey;
   }

   public String getGdSecret() {
      return this.gdSecret;
   }

   public void setGdSecret(String gdSecret) {
      this.gdSecret = gdSecret;
   }

   public String getCfEmail() {
      return this.cfEmail;
   }

   public void setCfEmail(String cfEmail) {
      this.cfEmail = cfEmail;
   }

   public String getCfKey() {
      return this.cfKey;
   }

   public void setCfKey(String cfKey) {
      this.cfKey = cfKey;
   }

   public Integer getType() {
      return this.type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public String getDnsType() {
      return this.dnsType;
   }

   public void setDnsType(String dnsType) {
      this.dnsType = dnsType;
   }

   public String getDpId() {
      return this.dpId;
   }

   public void setDpId(String dpId) {
      this.dpId = dpId;
   }

   public String getDpKey() {
      return this.dpKey;
   }

   public void setDpKey(String dpKey) {
      this.dpKey = dpKey;
   }

   public String getAliKey() {
      return this.aliKey;
   }

   public void setAliKey(String aliKey) {
      this.aliKey = aliKey;
   }

   public String getAliSecret() {
      return this.aliSecret;
   }

   public void setAliSecret(String aliSecret) {
      this.aliSecret = aliSecret;
   }

   public Integer getAutoRenew() {
      return this.autoRenew;
   }

   public void setAutoRenew(Integer autoRenew) {
      this.autoRenew = autoRenew;
   }

   public Long getMakeTime() {
      return this.makeTime;
   }

   public void setMakeTime(Long makeTime) {
      this.makeTime = makeTime;
   }

   public String getDomain() {
      return this.domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public String getPem() {
      return this.pem;
   }

   public void setPem(String pem) {
      this.pem = pem;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }
}
