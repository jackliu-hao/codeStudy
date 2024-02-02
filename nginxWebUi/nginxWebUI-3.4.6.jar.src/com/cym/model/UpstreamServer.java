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
/*     */ @Table
/*     */ public class UpstreamServer
/*     */   extends BaseModel
/*     */ {
/*     */   String upstreamId;
/*     */   String server;
/*     */   Integer port;
/*     */   Integer weight;
/*     */   Integer failTimeout;
/*     */   Integer maxFails;
/*     */   Integer maxConns;
/*     */   @InitValue("none")
/*     */   String status;
/*     */   @JsonIgnore
/*     */   @InitValue("-1")
/*     */   Integer monitorStatus;
/*     */   
/*     */   public Integer getMaxConns() {
/*  56 */     return this.maxConns;
/*     */   }
/*     */   
/*     */   public void setMaxConns(Integer maxConns) {
/*  60 */     this.maxConns = maxConns;
/*     */   }
/*     */   
/*     */   public Integer getMonitorStatus() {
/*  64 */     return this.monitorStatus;
/*     */   }
/*     */   
/*     */   public void setMonitorStatus(Integer monitorStatus) {
/*  68 */     this.monitorStatus = monitorStatus;
/*     */   }
/*     */   
/*     */   public Integer getFailTimeout() {
/*  72 */     return this.failTimeout;
/*     */   }
/*     */   
/*     */   public void setFailTimeout(Integer failTimeout) {
/*  76 */     this.failTimeout = failTimeout;
/*     */   }
/*     */   
/*     */   public Integer getMaxFails() {
/*  80 */     return this.maxFails;
/*     */   }
/*     */   
/*     */   public void setMaxFails(Integer maxFails) {
/*  84 */     this.maxFails = maxFails;
/*     */   }
/*     */   
/*     */   public String getStatus() {
/*  88 */     return this.status;
/*     */   }
/*     */   
/*     */   public void setStatus(String status) {
/*  92 */     this.status = status;
/*     */   }
/*     */   
/*     */   public Integer getPort() {
/*  96 */     return this.port;
/*     */   }
/*     */   
/*     */   public void setPort(Integer port) {
/* 100 */     this.port = port;
/*     */   }
/*     */   
/*     */   public String getUpstreamId() {
/* 104 */     return this.upstreamId;
/*     */   }
/*     */   
/*     */   public void setUpstreamId(String upstreamId) {
/* 108 */     this.upstreamId = upstreamId;
/*     */   }
/*     */   
/*     */   public String getServer() {
/* 112 */     return this.server;
/*     */   }
/*     */   
/*     */   public void setServer(String server) {
/* 116 */     this.server = server;
/*     */   }
/*     */   
/*     */   public Integer getWeight() {
/* 120 */     return this.weight;
/*     */   }
/*     */   
/*     */   public void setWeight(Integer weight) {
/* 124 */     this.weight = weight;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\UpstreamServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */