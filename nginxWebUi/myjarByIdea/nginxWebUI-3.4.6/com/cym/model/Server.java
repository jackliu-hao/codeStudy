package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Server extends BaseModel {
   String serverName;
   String listen;
   @InitValue("0")
   Integer def;
   @InitValue("0")
   Integer rewrite;
   @InitValue("80")
   String rewriteListen;
   @InitValue("0")
   Integer ssl;
   @InitValue("0")
   Integer http2;
   @InitValue("0")
   Integer proxyProtocol;
   String pem;
   String key;
   @InitValue("0")
   Integer proxyType;
   String proxyUpstreamId;
   @JsonIgnore
   String pemStr;
   @JsonIgnore
   String keyStr;
   @InitValue("true")
   Boolean enable;
   String descr;
   @InitValue("TLSv1 TLSv1.1 TLSv1.2 TLSv1.3")
   String protocols;
   String passwordId;
   @JsonIgnore
   Long seq;

   public Long getSeq() {
      return this.seq;
   }

   public void setSeq(Long seq) {
      this.seq = seq;
   }

   public String getPasswordId() {
      return this.passwordId;
   }

   public void setPasswordId(String passwordId) {
      this.passwordId = passwordId;
   }

   public String getProtocols() {
      return this.protocols;
   }

   public void setProtocols(String protocols) {
      this.protocols = protocols;
   }

   public String getDescr() {
      return this.descr;
   }

   public void setDescr(String descr) {
      this.descr = descr;
   }

   public Integer getDef() {
      return this.def;
   }

   public void setDef(Integer def) {
      this.def = def;
   }

   public Boolean getEnable() {
      return this.enable;
   }

   public void setEnable(Boolean enable) {
      this.enable = enable;
   }

   public Integer getHttp2() {
      return this.http2;
   }

   public void setHttp2(Integer http2) {
      this.http2 = http2;
   }

   public String getPemStr() {
      return this.pemStr;
   }

   public void setPemStr(String pemStr) {
      this.pemStr = pemStr;
   }

   public String getKeyStr() {
      return this.keyStr;
   }

   public void setKeyStr(String keyStr) {
      this.keyStr = keyStr;
   }

   public String getProxyUpstreamId() {
      return this.proxyUpstreamId;
   }

   public void setProxyUpstreamId(String proxyUpstreamId) {
      this.proxyUpstreamId = proxyUpstreamId;
   }

   public Integer getProxyType() {
      return this.proxyType;
   }

   public void setProxyType(Integer proxyType) {
      this.proxyType = proxyType;
   }

   public Integer getSsl() {
      return this.ssl;
   }

   public void setSsl(Integer ssl) {
      this.ssl = ssl;
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

   public String getServerName() {
      return this.serverName;
   }

   public void setServerName(String serverName) {
      this.serverName = serverName;
   }

   public String getListen() {
      return this.listen;
   }

   public void setListen(String listen) {
      this.listen = listen;
   }

   public Integer getRewrite() {
      return this.rewrite;
   }

   public void setRewrite(Integer rewrite) {
      this.rewrite = rewrite;
   }

   public String getRewriteListen() {
      return this.rewriteListen;
   }

   public Integer getProxyProtocol() {
      return this.proxyProtocol;
   }

   public void setProxyProtocol(Integer proxyProtocol) {
      this.proxyProtocol = proxyProtocol;
   }

   public void setRewriteListen(String rewriteListen) {
      this.rewriteListen = rewriteListen;
   }
}
