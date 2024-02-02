/*    */ package com.cym.ext;
/*    */ 
/*    */ import com.cym.model.Upstream;
/*    */ import com.cym.model.UpstreamServer;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ public class UpstreamExt
/*    */ {
/*    */   Upstream upstream;
/*    */   List<UpstreamServer> upstreamServerList;
/*    */   String serverStr;
/*    */   String paramJson;
/*    */   
/*    */   public String getParamJson() {
/* 16 */     return this.paramJson;
/*    */   }
/*    */   
/*    */   public void setParamJson(String paramJson) {
/* 20 */     this.paramJson = paramJson;
/*    */   }
/*    */   
/*    */   public String getServerStr() {
/* 24 */     return this.serverStr;
/*    */   }
/*    */   
/*    */   public void setServerStr(String serverStr) {
/* 28 */     this.serverStr = serverStr;
/*    */   }
/*    */   
/*    */   public Upstream getUpstream() {
/* 32 */     return this.upstream;
/*    */   }
/*    */   
/*    */   public void setUpstream(Upstream upstream) {
/* 36 */     this.upstream = upstream;
/*    */   }
/*    */   
/*    */   public List<UpstreamServer> getUpstreamServerList() {
/* 40 */     return this.upstreamServerList;
/*    */   }
/*    */   
/*    */   public void setUpstreamServerList(List<UpstreamServer> upstreamServerList) {
/* 44 */     this.upstreamServerList = upstreamServerList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\UpstreamExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */