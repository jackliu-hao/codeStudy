/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Password;
/*    */ import com.cym.service.PasswordService;
/*    */ import com.cym.sqlhelper.bean.Page;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.Crypt;
/*    */ import com.cym.utils.JsonResult;
/*    */ import com.cym.utils.SystemTool;
/*    */ import java.io.IOException;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ import org.noear.solon.core.handle.ModelAndView;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Mapping("/adminPage/password")
/*    */ @Controller
/*    */ public class PasswordController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   PasswordService passwordService;
/*    */   
/*    */   @Mapping("")
/*    */   public ModelAndView index(ModelAndView modelAndView, Page page) {
/* 30 */     page = this.passwordService.search(page);
/*    */     
/* 32 */     modelAndView.put("page", page);
/* 33 */     modelAndView.view("/adminPage/password/index.html");
/* 34 */     return modelAndView;
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("addOver")
/*    */   public JsonResult addOver(Password password) throws IOException {
/* 40 */     if (StrUtil.isEmpty(password.getId())) {
/* 41 */       Long count = this.passwordService.getCountByName(password.getName());
/* 42 */       if (count.longValue() > 0L) {
/* 43 */         return renderError(this.m.get("adminStr.nameRepetition"));
/*    */       }
/*    */     } else {
/* 46 */       Long count = this.passwordService.getCountByNameWithOutId(password.getName(), password.getId());
/* 47 */       if (count.longValue() > 0L) {
/* 48 */         return renderError(this.m.get("adminStr.nameRepetition"));
/*    */       }
/*    */       
/* 51 */       Password passwordOrg = (Password)this.sqlHelper.findById(password.getId(), Password.class);
/* 52 */       FileUtil.del(passwordOrg.getPath());
/*    */     } 
/*    */     
/* 55 */     password.setPath(this.homeConfig.home + "password/" + password.getName());
/*    */     
/* 57 */     String value = "";
/* 58 */     if (SystemTool.isWindows().booleanValue()) {
/*    */       
/* 60 */       value = password.getName() + ":" + password.getPass();
/*    */     } else {
/*    */       
/* 63 */       value = Crypt.getString(password.getName(), password.getPass());
/*    */     } 
/* 65 */     FileUtil.writeString(value, password.getPath(), "UTF-8");
/*    */     
/* 67 */     this.sqlHelper.insertOrUpdate(password);
/*    */     
/* 69 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("del")
/*    */   public JsonResult del(String id) {
/* 74 */     Password password = (Password)this.sqlHelper.findById(id, Password.class);
/* 75 */     this.sqlHelper.deleteById(id, Password.class);
/* 76 */     FileUtil.del(password.getPath());
/*    */     
/* 78 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("detail")
/*    */   public JsonResult detail(String id) {
/* 83 */     return renderSuccess(this.sqlHelper.findById(id, Password.class));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\PasswordController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */