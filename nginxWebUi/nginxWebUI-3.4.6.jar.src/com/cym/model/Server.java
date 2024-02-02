/*     */ package com.cym.model;
/*     */ 
/*     */ import com.cym.sqlhelper.bean.BaseModel;
/*     */ import com.cym.sqlhelper.config.InitValue;
/*     */ import com.cym.sqlhelper.config.Table;
/*     */ import com.fasterxml.jackson.annotation.JsonIgnore;
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
/*     */ public class Server
/*     */   extends BaseModel
/*     */ {
/*     */   String serverName;
/*     */   String listen;
/*     */   @InitValue("0")
/*     */   Integer def;
/*     */   @InitValue("0")
/*     */   Integer rewrite;
/*     */   @InitValue("80")
/*     */   String rewriteListen;
/*     */   @InitValue("0")
/*     */   Integer ssl;
/*     */   @InitValue("0")
/*     */   Integer http2;
/*     */   @InitValue("0")
/*     */   Integer proxyProtocol;
/*     */   String pem;
/*     */   String key;
/*     */   @InitValue("0")
/*     */   Integer proxyType;
/*     */   String proxyUpstreamId;
/*     */   @JsonIgnore
/*     */   String pemStr;
/*     */   @JsonIgnore
/*     */   String keyStr;
/*     */   @InitValue("true")
/*     */   Boolean enable;
/*     */   String descr;
/*     */   @InitValue("TLSv1 TLSv1.1 TLSv1.2 TLSv1.3")
/*     */   String protocols;
/*     */   String passwordId;
/*     */   @JsonIgnore
/*     */   Long seq;
/*     */   
/*     */   public Long getSeq() {
/*  97 */     return this.seq;
/*     */   }
/*     */   
/*     */   public void setSeq(Long seq) {
/* 101 */     this.seq = seq;
/*     */   }
/*     */   
/*     */   public String getPasswordId() {
/* 105 */     return this.passwordId;
/*     */   }
/*     */   
/*     */   public void setPasswordId(String passwordId) {
/* 109 */     this.passwordId = passwordId;
/*     */   }
/*     */   
/*     */   public String getProtocols() {
/* 113 */     return this.protocols;
/*     */   }
/*     */   
/*     */   public void setProtocols(String protocols) {
/* 117 */     this.protocols = protocols;
/*     */   }
/*     */   
/*     */   public String getDescr() {
/* 121 */     return this.descr;
/*     */   }
/*     */   
/*     */   public void setDescr(String descr) {
/* 125 */     this.descr = descr;
/*     */   }
/*     */   
/*     */   public Integer getDef() {
/* 129 */     return this.def;
/*     */   }
/*     */   
/*     */   public void setDef(Integer def) {
/* 133 */     this.def = def;
/*     */   }
/*     */   
/*     */   public Boolean getEnable() {
/* 137 */     return this.enable;
/*     */   }
/*     */   
/*     */   public void setEnable(Boolean enable) {
/* 141 */     this.enable = enable;
/*     */   }
/*     */   
/*     */   public Integer getHttp2() {
/* 145 */     return this.http2;
/*     */   }
/*     */   
/*     */   public void setHttp2(Integer http2) {
/* 149 */     this.http2 = http2;
/*     */   }
/*     */   
/*     */   public String getPemStr() {
/* 153 */     return this.pemStr;
/*     */   }
/*     */   
/*     */   public void setPemStr(String pemStr) {
/* 157 */     this.pemStr = pemStr;
/*     */   }
/*     */   
/*     */   public String getKeyStr() {
/* 161 */     return this.keyStr;
/*     */   }
/*     */   
/*     */   public void setKeyStr(String keyStr) {
/* 165 */     this.keyStr = keyStr;
/*     */   }
/*     */   
/*     */   public String getProxyUpstreamId() {
/* 169 */     return this.proxyUpstreamId;
/*     */   }
/*     */   
/*     */   public void setProxyUpstreamId(String proxyUpstreamId) {
/* 173 */     this.proxyUpstreamId = proxyUpstreamId;
/*     */   }
/*     */   
/*     */   public Integer getProxyType() {
/* 177 */     return this.proxyType;
/*     */   }
/*     */   
/*     */   public void setProxyType(Integer proxyType) {
/* 181 */     this.proxyType = proxyType;
/*     */   }
/*     */   
/*     */   public Integer getSsl() {
/* 185 */     return this.ssl;
/*     */   }
/*     */   
/*     */   public void setSsl(Integer ssl) {
/* 189 */     this.ssl = ssl;
/*     */   }
/*     */   
/*     */   public String getPem() {
/* 193 */     return this.pem;
/*     */   }
/*     */   
/*     */   public void setPem(String pem) {
/* 197 */     this.pem = pem;
/*     */   }
/*     */   
/*     */   public String getKey() {
/* 201 */     return this.key;
/*     */   }
/*     */   
/*     */   public void setKey(String key) {
/* 205 */     this.key = key;
/*     */   }
/*     */   
/*     */   public String getServerName() {
/* 209 */     return this.serverName;
/*     */   }
/*     */   
/*     */   public void setServerName(String serverName) {
/* 213 */     this.serverName = serverName;
/*     */   }
/*     */   
/*     */   public String getListen() {
/* 217 */     return this.listen;
/*     */   }
/*     */   
/*     */   public void setListen(String listen) {
/* 221 */     this.listen = listen;
/*     */   }
/*     */   
/*     */   public Integer getRewrite() {
/* 225 */     return this.rewrite;
/*     */   }
/*     */   
/*     */   public void setRewrite(Integer rewrite) {
/* 229 */     this.rewrite = rewrite;
/*     */   }
/*     */   
/*     */   public String getRewriteListen() {
/* 233 */     return this.rewriteListen;
/*     */   }
/*     */   
/*     */   public Integer getProxyProtocol() {
/* 237 */     return this.proxyProtocol;
/*     */   }
/*     */   
/*     */   public void setProxyProtocol(Integer proxyProtocol) {
/* 241 */     this.proxyProtocol = proxyProtocol;
/*     */   }
/*     */   
/*     */   public void setRewriteListen(String rewriteListen) {
/* 245 */     this.rewriteListen = rewriteListen;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */