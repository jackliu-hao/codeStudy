package com.cym.controller.adminPage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cym.model.Password;
import com.cym.service.PasswordService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.BaseController;
import com.cym.utils.Crypt;
import com.cym.utils.JsonResult;
import com.cym.utils.SystemTool;
import java.io.IOException;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Mapping("/adminPage/password")
@Controller
public class PasswordController extends BaseController {
   @Inject
   PasswordService passwordService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView, Page page) {
      page = this.passwordService.search(page);
      modelAndView.put("page", page);
      modelAndView.view("/adminPage/password/index.html");
      return modelAndView;
   }

   @Mapping("addOver")
   public JsonResult addOver(Password password) throws IOException {
      Long count;
      if (StrUtil.isEmpty(password.getId())) {
         count = this.passwordService.getCountByName(password.getName());
         if (count > 0L) {
            return this.renderError(this.m.get("adminStr.nameRepetition"));
         }
      } else {
         count = this.passwordService.getCountByNameWithOutId(password.getName(), password.getId());
         if (count > 0L) {
            return this.renderError(this.m.get("adminStr.nameRepetition"));
         }

         Password passwordOrg = (Password)this.sqlHelper.findById(password.getId(), Password.class);
         FileUtil.del(passwordOrg.getPath());
      }

      password.setPath(this.homeConfig.home + "password/" + password.getName());
      String value = "";
      if (SystemTool.isWindows()) {
         value = password.getName() + ":" + password.getPass();
      } else {
         value = Crypt.getString(password.getName(), password.getPass());
      }

      FileUtil.writeString(value, password.getPath(), "UTF-8");
      this.sqlHelper.insertOrUpdate(password);
      return this.renderSuccess();
   }

   @Mapping("del")
   public JsonResult del(String id) {
      Password password = (Password)this.sqlHelper.findById(id, Password.class);
      this.sqlHelper.deleteById(id, Password.class);
      FileUtil.del(password.getPath());
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      return this.renderSuccess(this.sqlHelper.findById(id, Password.class));
   }
}
