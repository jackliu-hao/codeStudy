/*     */ package com.cym.ext;
/*     */ 
/*     */ import com.cym.model.Basic;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.CertCode;
/*     */ import com.cym.model.Http;
/*     */ import com.cym.model.Location;
/*     */ import com.cym.model.Param;
/*     */ import com.cym.model.Password;
/*     */ import com.cym.model.Server;
/*     */ import com.cym.model.Stream;
/*     */ import com.cym.model.Template;
/*     */ import com.cym.model.Upstream;
/*     */ import com.cym.model.UpstreamServer;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AsycPack
/*     */ {
/*     */   List<Basic> basicList;
/*     */   List<Http> httpList;
/*     */   List<Server> serverList;
/*     */   List<Location> locationList;
/*     */   List<Upstream> upstreamList;
/*     */   List<UpstreamServer> upstreamServerList;
/*     */   List<Stream> streamList;
/*     */   List<Template> templateList;
/*     */   List<Param> paramList;
/*     */   List<Password> passwordList;
/*     */   List<Cert> certList;
/*     */   List<CertCode> certCodeList;
/*     */   String acmeZip;
/*     */   
/*     */   public String getAcmeZip() {
/*  36 */     return this.acmeZip;
/*     */   }
/*     */   
/*     */   public void setAcmeZip(String acmeZip) {
/*  40 */     this.acmeZip = acmeZip;
/*     */   }
/*     */   
/*     */   public List<Cert> getCertList() {
/*  44 */     return this.certList;
/*     */   }
/*     */   
/*     */   public void setCertList(List<Cert> certList) {
/*  48 */     this.certList = certList;
/*     */   }
/*     */   
/*     */   public List<CertCode> getCertCodeList() {
/*  52 */     return this.certCodeList;
/*     */   }
/*     */   
/*     */   public void setCertCodeList(List<CertCode> certCodeList) {
/*  56 */     this.certCodeList = certCodeList;
/*     */   }
/*     */   
/*     */   public List<Template> getTemplateList() {
/*  60 */     return this.templateList;
/*     */   }
/*     */   
/*     */   public void setTemplateList(List<Template> templateList) {
/*  64 */     this.templateList = templateList;
/*     */   }
/*     */   
/*     */   public List<Password> getPasswordList() {
/*  68 */     return this.passwordList;
/*     */   }
/*     */   
/*     */   public void setPasswordList(List<Password> passwordList) {
/*  72 */     this.passwordList = passwordList;
/*     */   }
/*     */   
/*     */   public List<Basic> getBasicList() {
/*  76 */     return this.basicList;
/*     */   }
/*     */   
/*     */   public void setBasicList(List<Basic> basicList) {
/*  80 */     this.basicList = basicList;
/*     */   }
/*     */   
/*     */   public List<Param> getParamList() {
/*  84 */     return this.paramList;
/*     */   }
/*     */   
/*     */   public void setParamList(List<Param> paramList) {
/*  88 */     this.paramList = paramList;
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
/*     */   public List<Stream> getStreamList() {
/* 108 */     return this.streamList;
/*     */   }
/*     */   
/*     */   public void setStreamList(List<Stream> streamList) {
/* 112 */     this.streamList = streamList;
/*     */   }
/*     */   
/*     */   public List<Location> getLocationList() {
/* 116 */     return this.locationList;
/*     */   }
/*     */   
/*     */   public void setLocationList(List<Location> locationList) {
/* 120 */     this.locationList = locationList;
/*     */   }
/*     */   
/*     */   public List<Http> getHttpList() {
/* 124 */     return this.httpList;
/*     */   }
/*     */   
/*     */   public void setHttpList(List<Http> httpList) {
/* 128 */     this.httpList = httpList;
/*     */   }
/*     */   
/*     */   public List<Server> getServerList() {
/* 132 */     return this.serverList;
/*     */   }
/*     */   
/*     */   public void setServerList(List<Server> serverList) {
/* 136 */     this.serverList = serverList;
/*     */   }
/*     */   
/*     */   public List<Upstream> getUpstreamList() {
/* 140 */     return this.upstreamList;
/*     */   }
/*     */   
/*     */   public void setUpstreamList(List<Upstream> upstreamList) {
/* 144 */     this.upstreamList = upstreamList;
/*     */   }
/*     */   
/*     */   public List<UpstreamServer> getUpstreamServerList() {
/* 148 */     return this.upstreamServerList;
/*     */   }
/*     */   
/*     */   public void setUpstreamServerList(List<UpstreamServer> upstreamServerList) {
/* 152 */     this.upstreamServerList = upstreamServerList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\AsycPack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */