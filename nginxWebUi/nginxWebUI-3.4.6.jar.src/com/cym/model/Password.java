/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ import com.fasterxml.jackson.annotation.JsonIgnore;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class Password
/*    */   extends BaseModel
/*    */ {
/*    */   String name;
/*    */   String pass;
/*    */   @JsonIgnore
/*    */   String path;
/*    */   String descr;
/*    */   @JsonIgnore
/*    */   String pathStr;
/*    */   
/*    */   public String getPathStr() {
/* 29 */     return this.pathStr;
/*    */   }
/*    */   
/*    */   public void setPathStr(String pathStr) {
/* 33 */     this.pathStr = pathStr;
/*    */   }
/*    */   
/*    */   public String getDescr() {
/* 37 */     return this.descr;
/*    */   }
/*    */   
/*    */   public void setDescr(String descr) {
/* 41 */     this.descr = descr;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 45 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 49 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getPass() {
/* 53 */     return this.pass;
/*    */   }
/*    */   
/*    */   public void setPass(String pass) {
/* 57 */     this.pass = pass;
/*    */   }
/*    */   
/*    */   public String getPath() {
/* 61 */     return this.path;
/*    */   }
/*    */   
/*    */   public void setPath(String path) {
/* 65 */     this.path = path;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Password.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */