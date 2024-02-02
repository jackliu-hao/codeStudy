/*     */ package com.cym.model;
/*     */ 
/*     */ import com.cym.sqlhelper.bean.BaseModel;
/*     */ import com.cym.sqlhelper.config.InitValue;
/*     */ import com.cym.sqlhelper.config.Table;
/*     */ 
/*     */ 
/*     */ @Table
/*     */ public class Remote
/*     */   extends BaseModel
/*     */ {
/*     */   String protocol;
/*     */   String ip;
/*     */   Integer port;
/*     */   @InitValue("0")
/*     */   Integer status;
/*     */   String creditKey;
/*     */   String name;
/*     */   String pass;
/*     */   String version;
/*     */   String system;
/*     */   String descr;
/*     */   @InitValue("0")
/*     */   Integer monitor;
/*     */   String parentId;
/*     */   Integer type;
/*     */   Integer nginx;
/*     */   
/*     */   public Integer getMonitor() {
/*  30 */     return this.monitor;
/*     */   }
/*     */   
/*     */   public void setMonitor(Integer monitor) {
/*  34 */     this.monitor = monitor;
/*     */   }
/*     */   
/*     */   public Integer getNginx() {
/*  38 */     return this.nginx;
/*     */   }
/*     */   
/*     */   public void setNginx(Integer nginx) {
/*  42 */     this.nginx = nginx;
/*     */   }
/*     */   
/*     */   public Integer getType() {
/*  46 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(Integer type) {
/*  50 */     this.type = type;
/*     */   }
/*     */   
/*     */   public String getParentId() {
/*  54 */     return this.parentId;
/*     */   }
/*     */   
/*     */   public void setParentId(String parentId) {
/*  58 */     this.parentId = parentId;
/*     */   }
/*     */   
/*     */   public String getDescr() {
/*  62 */     return this.descr;
/*     */   }
/*     */   
/*     */   public void setDescr(String descr) {
/*  66 */     this.descr = descr;
/*     */   }
/*     */   
/*     */   public String getSystem() {
/*  70 */     return this.system;
/*     */   }
/*     */   
/*     */   public void setSystem(String system) {
/*  74 */     this.system = system;
/*     */   }
/*     */   
/*     */   public String getVersion() {
/*  78 */     return this.version;
/*     */   }
/*     */   
/*     */   public void setVersion(String version) {
/*  82 */     this.version = version;
/*     */   }
/*     */   
/*     */   public String getProtocol() {
/*  86 */     return this.protocol;
/*     */   }
/*     */   
/*     */   public void setProtocol(String protocol) {
/*  90 */     this.protocol = protocol;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  94 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/*  98 */     this.name = name;
/*     */   }
/*     */   
/*     */   public String getPass() {
/* 102 */     return this.pass;
/*     */   }
/*     */   
/*     */   public void setPass(String pass) {
/* 106 */     this.pass = pass;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getStatus() {
/* 111 */     return this.status;
/*     */   }
/*     */   
/*     */   public void setStatus(Integer status) {
/* 115 */     this.status = status;
/*     */   }
/*     */   
/*     */   public Integer getPort() {
/* 119 */     return this.port;
/*     */   }
/*     */   
/*     */   public void setPort(Integer port) {
/* 123 */     this.port = port;
/*     */   }
/*     */   
/*     */   public String getIp() {
/* 127 */     return this.ip;
/*     */   }
/*     */   
/*     */   public void setIp(String ip) {
/* 131 */     this.ip = ip;
/*     */   }
/*     */   
/*     */   public String getCreditKey() {
/* 135 */     return this.creditKey;
/*     */   }
/*     */   
/*     */   public void setCreditKey(String creditKey) {
/* 139 */     this.creditKey = creditKey;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Remote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */