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
/*     */ @Table
/*     */ public class Location
/*     */   extends BaseModel
/*     */ {
/*     */   String serverId;
/*     */   String path;
/*     */   @InitValue("0")
/*     */   Integer type;
/*     */   @JsonIgnore
/*     */   String locationParamJson;
/*     */   String value;
/*     */   @InitValue("http")
/*     */   String upstreamType;
/*     */   String upstreamId;
/*     */   String upstreamPath;
/*     */   String rootPath;
/*     */   String rootPage;
/*     */   String rootType;
/*     */   @InitValue("1")
/*     */   Integer header;
/*     */   @InitValue("0")
/*     */   Integer websocket;
/*     */   String descr;
/*     */   
/*     */   public String getDescr() {
/*  79 */     return this.descr;
/*     */   }
/*     */   
/*     */   public void setDescr(String descr) {
/*  83 */     this.descr = descr;
/*     */   }
/*     */   
/*     */   public String getUpstreamType() {
/*  87 */     return this.upstreamType;
/*     */   }
/*     */   
/*     */   public void setUpstreamType(String upstreamType) {
/*  91 */     this.upstreamType = upstreamType;
/*     */   }
/*     */   
/*     */   public Integer getWebsocket() {
/*  95 */     return this.websocket;
/*     */   }
/*     */   
/*     */   public void setWebsocket(Integer websocket) {
/*  99 */     this.websocket = websocket;
/*     */   }
/*     */   
/*     */   public Integer getHeader() {
/* 103 */     return this.header;
/*     */   }
/*     */   
/*     */   public void setHeader(Integer header) {
/* 107 */     this.header = header;
/*     */   }
/*     */   
/*     */   public String getRootType() {
/* 111 */     return this.rootType;
/*     */   }
/*     */   
/*     */   public void setRootType(String rootType) {
/* 115 */     this.rootType = rootType;
/*     */   }
/*     */   
/*     */   public String getRootPath() {
/* 119 */     return this.rootPath;
/*     */   }
/*     */   
/*     */   public void setRootPath(String rootPath) {
/* 123 */     this.rootPath = rootPath;
/*     */   }
/*     */   
/*     */   public String getRootPage() {
/* 127 */     return this.rootPage;
/*     */   }
/*     */   
/*     */   public void setRootPage(String rootPage) {
/* 131 */     this.rootPage = rootPage;
/*     */   }
/*     */   
/*     */   public String getUpstreamPath() {
/* 135 */     return this.upstreamPath;
/*     */   }
/*     */   
/*     */   public void setUpstreamPath(String upstreamPath) {
/* 139 */     this.upstreamPath = upstreamPath;
/*     */   }
/*     */   
/*     */   public String getLocationParamJson() {
/* 143 */     return this.locationParamJson;
/*     */   }
/*     */   
/*     */   public void setLocationParamJson(String locationParamJson) {
/* 147 */     this.locationParamJson = locationParamJson;
/*     */   }
/*     */   
/*     */   public String getUpstreamId() {
/* 151 */     return this.upstreamId;
/*     */   }
/*     */   
/*     */   public void setUpstreamId(String upstreamId) {
/* 155 */     this.upstreamId = upstreamId;
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 159 */     return this.path;
/*     */   }
/*     */   
/*     */   public void setPath(String path) {
/* 163 */     this.path = path;
/*     */   }
/*     */   
/*     */   public String getServerId() {
/* 167 */     return this.serverId;
/*     */   }
/*     */   
/*     */   public void setServerId(String serverId) {
/* 171 */     this.serverId = serverId;
/*     */   }
/*     */   
/*     */   public Integer getType() {
/* 175 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(Integer type) {
/* 179 */     this.type = type;
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 183 */     return this.value;
/*     */   }
/*     */   
/*     */   public void setValue(String value) {
/* 187 */     this.value = value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Location.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */