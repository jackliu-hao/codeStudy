package com.cym.utils;

import com.cym.config.HomeConfig;
import com.cym.model.Admin;
import com.cym.service.AdminService;
import com.cym.sqlhelper.utils.SqlHelper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;

public class BaseController {
   @Inject
   protected SqlHelper sqlHelper;
   @Inject
   protected AdminService adminService;
   @Inject
   protected MessageUtils m;
   @Inject
   protected HomeConfig homeConfig;

   protected JsonResult renderError() {
      JsonResult result = new JsonResult();
      result.setSuccess(false);
      result.setStatus("500");
      return result;
   }

   protected JsonResult renderAuthError() {
      JsonResult result = new JsonResult();
      result.setSuccess(false);
      result.setStatus("401");
      return result;
   }

   protected JsonResult renderError(String msg) {
      JsonResult result = this.renderError();
      result.setMsg(msg);
      return result;
   }

   protected JsonResult renderSuccess() {
      JsonResult result = new JsonResult();
      result.setSuccess(true);
      result.setStatus("200");
      return result;
   }

   protected JsonResult renderSuccess(Object obj) {
      JsonResult result = this.renderSuccess();
      result.setObj(obj);
      return result;
   }

   public Admin getAdmin() {
      Admin admin = (Admin)Context.current().session("admin");
      String creditKey;
      if (admin == null) {
         creditKey = Context.current().header("token");
         admin = this.adminService.getByToken(creditKey);
      }

      if (admin == null) {
         creditKey = Context.current().param("creditKey");
         admin = this.adminService.getByCreditKey(creditKey);
      }

      return admin;
   }
}
