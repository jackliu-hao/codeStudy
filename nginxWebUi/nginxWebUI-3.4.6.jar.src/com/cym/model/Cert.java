/*     */ package com.cym.model;
/*     */ 
/*     */ import com.cym.sqlhelper.bean.BaseModel;
/*     */ import com.cym.sqlhelper.config.InitValue;
/*     */ import com.cym.sqlhelper.config.Table;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Table
/*     */ public class Cert
/*     */   extends BaseModel
/*     */ {
/*     */   String domain;
/*     */   String pem;
/*     */   String key;
/*     */   @InitValue("0")
/*     */   Integer type;
/*     */   @InitValue("RSA")
/*     */   String encryption;
/*     */   Long makeTime;
/*     */   Long endTime;
/*     */   @InitValue("0")
/*     */   Integer autoRenew;
/*     */   String dnsType;
/*     */   String dpId;
/*     */   String dpKey;
/*     */   String aliKey;
/*     */   String aliSecret;
/*     */   String cfEmail;
/*     */   String cfKey;
/*     */   String gdKey;
/*     */   String gdSecret;
/*     */   String hwUsername;
/*     */   String hwPassword;
/*     */   String hwProjectId;
/*     */   String hwDomainName;
/*     */   
/*     */   public String getHwDomainName() {
/* 105 */     return this.hwDomainName;
/*     */   }
/*     */   
/*     */   public void setHwDomainName(String hwDomainName) {
/* 109 */     this.hwDomainName = hwDomainName;
/*     */   }
/*     */   
/*     */   public Long getEndTime() {
/* 113 */     return this.endTime;
/*     */   }
/*     */   
/*     */   public void setEndTime(Long endTime) {
/* 117 */     this.endTime = endTime;
/*     */   }
/*     */   
/*     */   public String getEncryption() {
/* 121 */     return this.encryption;
/*     */   }
/*     */   
/*     */   public void setEncryption(String encryption) {
/* 125 */     this.encryption = encryption;
/*     */   }
/*     */   
/*     */   public String getHwUsername() {
/* 129 */     return this.hwUsername;
/*     */   }
/*     */   
/*     */   public void setHwUsername(String hwUsername) {
/* 133 */     this.hwUsername = hwUsername;
/*     */   }
/*     */   
/*     */   public String getHwPassword() {
/* 137 */     return this.hwPassword;
/*     */   }
/*     */   
/*     */   public void setHwPassword(String hwPassword) {
/* 141 */     this.hwPassword = hwPassword;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHwProjectId() {
/* 146 */     return this.hwProjectId;
/*     */   }
/*     */   
/*     */   public void setHwProjectId(String hwProjectId) {
/* 150 */     this.hwProjectId = hwProjectId;
/*     */   }
/*     */   
/*     */   public String getGdKey() {
/* 154 */     return this.gdKey;
/*     */   }
/*     */   
/*     */   public void setGdKey(String gdKey) {
/* 158 */     this.gdKey = gdKey;
/*     */   }
/*     */   
/*     */   public String getGdSecret() {
/* 162 */     return this.gdSecret;
/*     */   }
/*     */   
/*     */   public void setGdSecret(String gdSecret) {
/* 166 */     this.gdSecret = gdSecret;
/*     */   }
/*     */   
/*     */   public String getCfEmail() {
/* 170 */     return this.cfEmail;
/*     */   }
/*     */   
/*     */   public void setCfEmail(String cfEmail) {
/* 174 */     this.cfEmail = cfEmail;
/*     */   }
/*     */   
/*     */   public String getCfKey() {
/* 178 */     return this.cfKey;
/*     */   }
/*     */   
/*     */   public void setCfKey(String cfKey) {
/* 182 */     this.cfKey = cfKey;
/*     */   }
/*     */   
/*     */   public Integer getType() {
/* 186 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(Integer type) {
/* 190 */     this.type = type;
/*     */   }
/*     */   
/*     */   public String getDnsType() {
/* 194 */     return this.dnsType;
/*     */   }
/*     */   
/*     */   public void setDnsType(String dnsType) {
/* 198 */     this.dnsType = dnsType;
/*     */   }
/*     */   
/*     */   public String getDpId() {
/* 202 */     return this.dpId;
/*     */   }
/*     */   
/*     */   public void setDpId(String dpId) {
/* 206 */     this.dpId = dpId;
/*     */   }
/*     */   
/*     */   public String getDpKey() {
/* 210 */     return this.dpKey;
/*     */   }
/*     */   
/*     */   public void setDpKey(String dpKey) {
/* 214 */     this.dpKey = dpKey;
/*     */   }
/*     */   
/*     */   public String getAliKey() {
/* 218 */     return this.aliKey;
/*     */   }
/*     */   
/*     */   public void setAliKey(String aliKey) {
/* 222 */     this.aliKey = aliKey;
/*     */   }
/*     */   
/*     */   public String getAliSecret() {
/* 226 */     return this.aliSecret;
/*     */   }
/*     */   
/*     */   public void setAliSecret(String aliSecret) {
/* 230 */     this.aliSecret = aliSecret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getAutoRenew() {
/* 250 */     return this.autoRenew;
/*     */   }
/*     */   
/*     */   public void setAutoRenew(Integer autoRenew) {
/* 254 */     this.autoRenew = autoRenew;
/*     */   }
/*     */   
/*     */   public Long getMakeTime() {
/* 258 */     return this.makeTime;
/*     */   }
/*     */   
/*     */   public void setMakeTime(Long makeTime) {
/* 262 */     this.makeTime = makeTime;
/*     */   }
/*     */   
/*     */   public String getDomain() {
/* 266 */     return this.domain;
/*     */   }
/*     */   
/*     */   public void setDomain(String domain) {
/* 270 */     this.domain = domain;
/*     */   }
/*     */   
/*     */   public String getPem() {
/* 274 */     return this.pem;
/*     */   }
/*     */   
/*     */   public void setPem(String pem) {
/* 278 */     this.pem = pem;
/*     */   }
/*     */   
/*     */   public String getKey() {
/* 282 */     return this.key;
/*     */   }
/*     */   
/*     */   public void setKey(String key) {
/* 286 */     this.key = key;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Cert.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */