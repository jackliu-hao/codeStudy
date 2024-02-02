/*     */ package com.cym.model;
/*     */ 
/*     */ import com.cym.sqlhelper.bean.BaseModel;
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
/*     */ @Table
/*     */ public class Param
/*     */   extends BaseModel
/*     */ {
/*     */   String serverId;
/*     */   String locationId;
/*     */   String upstreamId;
/*     */   @JsonIgnore
/*     */   String templateId;
/*     */   String name;
/*     */   String value;
/*     */   @JsonIgnore
/*     */   String templateValue;
/*     */   @JsonIgnore
/*     */   String templateName;
/*     */   
/*     */   public String getTemplateValue() {
/*  44 */     return this.templateValue;
/*     */   }
/*     */   
/*     */   public void setTemplateValue(String templateValue) {
/*  48 */     this.templateValue = templateValue;
/*     */   }
/*     */   
/*     */   public String getTemplateName() {
/*  52 */     return this.templateName;
/*     */   }
/*     */   
/*     */   public void setTemplateName(String templateName) {
/*  56 */     this.templateName = templateName;
/*     */   }
/*     */   
/*     */   public String getTemplateId() {
/*  60 */     return this.templateId;
/*     */   }
/*     */   
/*     */   public void setTemplateId(String templateId) {
/*  64 */     this.templateId = templateId;
/*     */   }
/*     */   
/*     */   public String getUpstreamId() {
/*  68 */     return this.upstreamId;
/*     */   }
/*     */   
/*     */   public void setUpstreamId(String upstreamId) {
/*  72 */     this.upstreamId = upstreamId;
/*     */   }
/*     */   
/*     */   public String getServerId() {
/*  76 */     return this.serverId;
/*     */   }
/*     */   
/*     */   public void setServerId(String serverId) {
/*  80 */     this.serverId = serverId;
/*     */   }
/*     */   
/*     */   public String getLocationId() {
/*  84 */     return this.locationId;
/*     */   }
/*     */   
/*     */   public void setLocationId(String locationId) {
/*  88 */     this.locationId = locationId;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  92 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/*  96 */     this.name = name;
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 100 */     return this.value;
/*     */   }
/*     */   
/*     */   public void setValue(String value) {
/* 104 */     this.value = value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Param.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */