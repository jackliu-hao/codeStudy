/*    */ package com.cym.ext;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DataGroup
/*    */ {
/*    */   List<KeyValue> pv;
/*    */   List<KeyValue> uv;
/*    */   List<KeyValue> status;
/*    */   List<KeyValue> browser;
/*    */   List<KeyValue> httpReferer;
/*    */   
/*    */   public List<KeyValue> getHttpReferer() {
/* 18 */     return this.httpReferer;
/*    */   }
/*    */   
/*    */   public void setHttpReferer(List<KeyValue> httpReferer) {
/* 22 */     this.httpReferer = httpReferer;
/*    */   }
/*    */   
/*    */   public List<KeyValue> getBrowser() {
/* 26 */     return this.browser;
/*    */   }
/*    */   
/*    */   public void setBrowser(List<KeyValue> browser) {
/* 30 */     this.browser = browser;
/*    */   }
/*    */   
/*    */   public List<KeyValue> getStatus() {
/* 34 */     return this.status;
/*    */   }
/*    */   
/*    */   public void setStatus(List<KeyValue> status) {
/* 38 */     this.status = status;
/*    */   }
/*    */   
/*    */   public List<KeyValue> getPv() {
/* 42 */     return this.pv;
/*    */   }
/*    */   
/*    */   public void setPv(List<KeyValue> pv) {
/* 46 */     this.pv = pv;
/*    */   }
/*    */   
/*    */   public List<KeyValue> getUv() {
/* 50 */     return this.uv;
/*    */   }
/*    */   
/*    */   public void setUv(List<KeyValue> uv) {
/* 54 */     this.uv = uv;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\DataGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */