/*    */ package com.cym.controller.api;
/*    */ 
/*    */ import com.cym.model.Admin;
/*    */ import com.cym.service.AdminService;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
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
/*    */ 
/*    */ 
/*    */ @Mapping("token")
/*    */ @Controller
/*    */ public class TokenController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   AdminService adminService;
/*    */   
/*    */   @Mapping("getToken")
/*    */   public JsonResult getToken(String name, String pass) {
/* 34 */     Admin admin = this.adminService.login(name, pass);
/* 35 */     if (admin == null) {
/* 36 */       return renderError(this.m.get("loginStr.backError2"));
/*    */     }
/* 38 */     if (!admin.getApi().booleanValue()) {
/* 39 */       return renderError(this.m.get("loginStr.backError7"));
/*    */     }
/*    */     
/* 42 */     Map<String, String> map = new HashMap<>();
/* 43 */     map.put("token", this.adminService.makeToken(admin.getId()));
/*    */     
/* 45 */     return renderSuccess(map);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\TokenController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */