/*    */ package com.cym.controller.api;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import com.cym.controller.adminPage.PasswordController;
/*    */ import com.cym.model.Password;
/*    */ import com.cym.service.PasswordService;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
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
/*    */ @Mapping("/api/password")
/*    */ @Controller
/*    */ public class PasswordApiController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   PasswordService passwordService;
/*    */   @Inject
/*    */   PasswordController passwordController;
/*    */   
/*    */   @Mapping("getList")
/*    */   public JsonResult<List<Password>> getList() {
/* 35 */     List<Password> list = this.sqlHelper.findAll(Password.class);
/* 36 */     return renderSuccess(list);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Mapping("insertOrUpdate")
/*    */   public JsonResult<?> insertOrUpdate(Password password) throws IOException {
/* 47 */     return renderSuccess(this.passwordController.addOver(password));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Mapping("del")
/*    */   public JsonResult<?> del(String id) {
/* 58 */     Password password = (Password)this.sqlHelper.findById(id, Password.class);
/* 59 */     this.sqlHelper.deleteById(id, Password.class);
/* 60 */     FileUtil.del(password.getPath());
/*    */     
/* 62 */     return renderSuccess();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\PasswordApiController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */