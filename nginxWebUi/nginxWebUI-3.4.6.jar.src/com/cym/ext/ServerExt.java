/*    */ package com.cym.ext;
/*    */ 
/*    */ import com.cym.model.Location;
/*    */ import com.cym.model.Server;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServerExt
/*    */ {
/*    */   Server server;
/*    */   List<Location> locationList;
/*    */   String locationStr;
/*    */   String paramJson;
/*    */   
/*    */   public String getParamJson() {
/* 17 */     return this.paramJson;
/*    */   }
/*    */   
/*    */   public void setParamJson(String paramJson) {
/* 21 */     this.paramJson = paramJson;
/*    */   }
/*    */   
/*    */   public String getLocationStr() {
/* 25 */     return this.locationStr;
/*    */   }
/*    */   
/*    */   public void setLocationStr(String locationStr) {
/* 29 */     this.locationStr = locationStr;
/*    */   }
/*    */   
/*    */   public Server getServer() {
/* 33 */     return this.server;
/*    */   }
/*    */   
/*    */   public void setServer(Server server) {
/* 37 */     this.server = server;
/*    */   }
/*    */   
/*    */   public List<Location> getLocationList() {
/* 41 */     return this.locationList;
/*    */   }
/*    */   
/*    */   public void setLocationList(List<Location> locationList) {
/* 45 */     this.locationList = locationList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\ServerExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */