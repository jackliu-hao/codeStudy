package com.cym.controller.api;

import com.cym.model.Admin;
import com.cym.service.AdminService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import java.util.HashMap;
import java.util.Map;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("token")
@Controller
public class TokenController extends BaseController {
   @Inject
   AdminService adminService;

   @Mapping("getToken")
   public JsonResult getToken(String name, String pass) {
      Admin admin = this.adminService.login(name, pass);
      if (admin == null) {
         return this.renderError(this.m.get("loginStr.backError2"));
      } else if (!admin.getApi()) {
         return this.renderError(this.m.get("loginStr.backError7"));
      } else {
         Map<String, String> map = new HashMap();
         map.put("token", this.adminService.makeToken(admin.getId()));
         return this.renderSuccess(map);
      }
   }
}
