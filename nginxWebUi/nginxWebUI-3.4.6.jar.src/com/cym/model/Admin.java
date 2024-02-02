/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.InitValue;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class Admin
/*    */   extends BaseModel
/*    */ {
/*    */   String name;
/*    */   String pass;
/*    */   String key;
/*    */   @InitValue("false")
/*    */   Boolean auth;
/*    */   @InitValue("false")
/*    */   Boolean api;
/*    */   String token;
/*    */   @InitValue("0")
/*    */   Integer type;
/*    */   
/*    */   public String getToken() {
/* 29 */     return this.token;
/*    */   }
/*    */   
/*    */   public void setToken(String token) {
/* 33 */     this.token = token;
/*    */   }
/*    */   
/*    */   public Boolean getApi() {
/* 37 */     return this.api;
/*    */   }
/*    */   
/*    */   public void setApi(Boolean api) {
/* 41 */     this.api = api;
/*    */   }
/*    */   
/*    */   public String getKey() {
/* 45 */     return this.key;
/*    */   }
/*    */   
/*    */   public void setKey(String key) {
/* 49 */     this.key = key;
/*    */   }
/*    */   
/*    */   public Boolean getAuth() {
/* 53 */     return this.auth;
/*    */   }
/*    */   
/*    */   public void setAuth(Boolean auth) {
/* 57 */     this.auth = auth;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 61 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 65 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getPass() {
/* 69 */     return this.pass;
/*    */   }
/*    */   
/*    */   public void setPass(String pass) {
/* 73 */     this.pass = pass;
/*    */   }
/*    */   
/*    */   public Integer getType() {
/* 77 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(Integer type) {
/* 81 */     this.type = type;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Admin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */