/*    */ package com.cym.utils;
/*    */ 
/*    */ import com.cym.config.HomeConfig;
/*    */ import com.cym.model.Admin;
/*    */ import com.cym.service.AdminService;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BaseController
/*    */ {
/*    */   @Inject
/*    */   protected SqlHelper sqlHelper;
/*    */   @Inject
/*    */   protected AdminService adminService;
/*    */   @Inject
/*    */   protected MessageUtils m;
/*    */   @Inject
/*    */   protected HomeConfig homeConfig;
/*    */   
/*    */   protected JsonResult renderError() {
/* 26 */     JsonResult result = new JsonResult();
/* 27 */     result.setSuccess(false);
/* 28 */     result.setStatus("500");
/* 29 */     return result;
/*    */   }
/*    */   
/*    */   protected JsonResult renderAuthError() {
/* 33 */     JsonResult result = new JsonResult();
/* 34 */     result.setSuccess(false);
/* 35 */     result.setStatus("401");
/* 36 */     return result;
/*    */   }
/*    */   
/*    */   protected JsonResult renderError(String msg) {
/* 40 */     JsonResult result = renderError();
/* 41 */     result.setMsg(msg);
/* 42 */     return result;
/*    */   }
/*    */   
/*    */   protected JsonResult renderSuccess() {
/* 46 */     JsonResult result = new JsonResult();
/* 47 */     result.setSuccess(true);
/* 48 */     result.setStatus("200");
/* 49 */     return result;
/*    */   }
/*    */   
/*    */   protected JsonResult renderSuccess(Object obj) {
/* 53 */     JsonResult<Object> result = renderSuccess();
/* 54 */     result.setObj(obj);
/* 55 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public Admin getAdmin() {
/* 60 */     Admin admin = (Admin)Context.current().session("admin");
/* 61 */     if (admin == null) {
/* 62 */       String token = Context.current().header("token");
/* 63 */       admin = this.adminService.getByToken(token);
/*    */     } 
/* 65 */     if (admin == null) {
/* 66 */       String creditKey = Context.current().param("creditKey");
/* 67 */       admin = this.adminService.getByCreditKey(creditKey);
/*    */     } 
/*    */     
/* 70 */     return admin;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\BaseController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */