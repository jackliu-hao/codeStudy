package com.cym.controller.api;

import cn.hutool.core.io.FileUtil;
import com.cym.controller.adminPage.PasswordController;
import com.cym.model.Password;
import com.cym.service.PasswordService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import java.io.IOException;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("/api/password")
@Controller
public class PasswordApiController extends BaseController {
   @Inject
   PasswordService passwordService;
   @Inject
   PasswordController passwordController;

   @Mapping("getList")
   public JsonResult<List<Password>> getList() {
      List<Password> list = this.sqlHelper.findAll(Password.class);
      return this.renderSuccess(list);
   }

   @Mapping("insertOrUpdate")
   public JsonResult<?> insertOrUpdate(Password password) throws IOException {
      return this.renderSuccess(this.passwordController.addOver(password));
   }

   @Mapping("del")
   public JsonResult<?> del(String id) {
      Password password = (Password)this.sqlHelper.findById(id, Password.class);
      this.sqlHelper.deleteById(id, Password.class);
      FileUtil.del(password.getPath());
      return this.renderSuccess();
   }
}
