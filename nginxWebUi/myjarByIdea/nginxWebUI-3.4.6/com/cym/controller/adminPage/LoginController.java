package com.cym.controller.adminPage;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.cym.config.VersionConfig;
import com.cym.model.Admin;
import com.cym.model.Remote;
import com.cym.service.AdminService;
import com.cym.service.CreditService;
import com.cym.service.SettingService;
import com.cym.utils.AuthUtils;
import com.cym.utils.BaseController;
import com.cym.utils.EncodePassUtils;
import com.cym.utils.JsonResult;
import com.cym.utils.PwdCheckUtil;
import com.cym.utils.SystemTool;
import com.wf.captcha.SpecCaptcha;
import java.util.HashMap;
import java.util.Map;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;

@Mapping("/adminPage/login")
@Controller
public class LoginController extends BaseController {
   @Inject
   AdminService adminService;
   @Inject
   CreditService creditService;
   @Inject
   VersionConfig versionConfig;
   @Inject
   AuthUtils authUtils;
   @Inject
   SettingService settingService;

   @Mapping("")
   public ModelAndView admin(ModelAndView modelAndView, String adminId) {
      modelAndView.put("adminCount", this.sqlHelper.findAllCount(Admin.class));
      modelAndView.view("/adminPage/login/index.html");
      return modelAndView;
   }

   @Mapping("loginOut")
   public ModelAndView loginOut(ModelAndView modelAndView) {
      Context.current().sessionRemove("isLogin");
      modelAndView.view("/adminPage/index.html");
      return modelAndView;
   }

   @Mapping("noServer")
   public ModelAndView noServer(ModelAndView modelAndView) {
      modelAndView.view("/adminPage/login/noServer.html");
      return modelAndView;
   }

   @Mapping("login")
   public JsonResult submitLogin(String name, String pass, String code, String authCode, String remember) {
      if (StrUtil.isNotEmpty(name)) {
         name = Base64.decodeStr(Base64.decodeStr(name));
      }

      if (StrUtil.isNotEmpty(pass)) {
         pass = Base64.decodeStr(Base64.decodeStr(pass));
      }

      if (StrUtil.isNotEmpty(code)) {
         code = Base64.decodeStr(Base64.decodeStr(code));
      }

      if (StrUtil.isNotEmpty(authCode)) {
         authCode = Base64.decodeStr(Base64.decodeStr(authCode));
      }

      String captcha = (String)Context.current().session("captcha");
      if (!code.equals(captcha)) {
         Context.current().sessionRemove("captcha");
         return this.renderError(this.m.get("loginStr.backError1"));
      } else {
         Context.current().sessionRemove("captcha");
         Admin admin = this.adminService.login(name, pass);
         if (admin == null) {
            return this.renderError(this.m.get("loginStr.backError2"));
         } else if (admin.getAuth() && !this.authUtils.testKey(admin.getKey(), authCode)) {
            return this.renderError(this.m.get("loginStr.backError6"));
         } else {
            Context.current().sessionSet("localType", "local");
            Context.current().sessionSet("isLogin", true);
            Context.current().sessionSet("admin", admin);
            Context.current().sessionRemove("imgCode");
            this.versionConfig.checkVersion();
            return this.renderSuccess(admin);
         }
      }
   }

   @Mapping("autoLogin")
   public JsonResult autoLogin(String adminId) {
      Admin admin = (Admin)this.sqlHelper.findById(adminId, Admin.class);
      if (admin != null) {
         Context.current().sessionSet("localType", "local");
         Context.current().sessionSet("isLogin", true);
         Context.current().sessionSet("admin", admin);
         Context.current().sessionRemove("imgCode");
         this.versionConfig.checkVersion();
         return this.renderSuccess(admin);
      } else {
         return this.renderError();
      }
   }

   @Mapping("getAuth")
   public JsonResult getAuth(String name, String pass, String code, Integer remote) {
      if (StrUtil.isNotEmpty(name)) {
         name = Base64.decodeStr(Base64.decodeStr(name));
      }

      if (StrUtil.isNotEmpty(pass)) {
         pass = Base64.decodeStr(Base64.decodeStr(pass));
      }

      if (StrUtil.isNotEmpty(code)) {
         code = Base64.decodeStr(Base64.decodeStr(code));
      }

      if (remote == null) {
         String captcha = (String)Context.current().session("captcha");
         if (!code.equals(captcha)) {
            Context.current().sessionRemove("captcha");
            return this.renderError(this.m.get("loginStr.backError1"));
         }
      }

      Admin admin = this.adminService.login(name, pass);
      if (admin == null) {
         return this.renderError(this.m.get("loginStr.backError2"));
      } else {
         Admin ad = new Admin();
         ad.setAuth(admin.getAuth());
         ad.setKey(admin.getKey());
         return this.renderSuccess(ad);
      }
   }

   @Mapping("getCredit")
   public JsonResult getCredit(String name, String pass, String code, String auth) {
      if (StrUtil.isNotEmpty(name)) {
         name = Base64.decodeStr(Base64.decodeStr(name));
      }

      if (StrUtil.isNotEmpty(pass)) {
         pass = Base64.decodeStr(Base64.decodeStr(pass));
      }

      if (StrUtil.isNotEmpty(code)) {
         code = Base64.decodeStr(Base64.decodeStr(code));
      }

      Admin admin = this.adminService.login(name, pass);
      if (admin == null) {
         return this.renderError(this.m.get("loginStr.backError2"));
      } else {
         if (!admin.getAuth()) {
            String imgCode = this.settingService.get("remoteCode");
            if (StrUtil.isEmpty(imgCode) || StrUtil.isNotEmpty(imgCode) && !imgCode.equalsIgnoreCase(code)) {
               return this.renderError(this.m.get("loginStr.backError1"));
            }
         } else if (!this.authUtils.testKey(admin.getKey(), auth)) {
            return this.renderError(this.m.get("loginStr.backError6"));
         }

         this.settingService.remove("remoteCode");
         Map<String, String> map = new HashMap();
         map.put("creditKey", this.creditService.make(admin.getId()));
         map.put("system", SystemTool.getSystem());
         return this.renderSuccess(map);
      }
   }

   @Mapping("getLocalType")
   public JsonResult getLocalType() {
      String localType = (String)Context.current().session("localType");
      if (StrUtil.isNotEmpty(localType)) {
         if ("local".equals(localType)) {
            return this.renderSuccess(this.m.get("remoteStr.local"));
         } else {
            Remote remote = (Remote)Context.current().session("remote");
            return StrUtil.isNotEmpty(remote.getDescr()) ? this.renderSuccess(remote.getDescr()) : this.renderSuccess(remote.getIp() + ":" + remote.getPort());
         }
      } else {
         return this.renderSuccess("");
      }
   }

   @Mapping("addAdmin")
   public JsonResult addAdmin(String name, String pass) {
      Long adminCount = this.sqlHelper.findAllCount(Admin.class);
      if (adminCount > 0L) {
         return this.renderError(this.m.get("loginStr.backError4"));
      } else if (PwdCheckUtil.checkContainUpperCase(pass) && PwdCheckUtil.checkContainLowerCase(pass) && PwdCheckUtil.checkContainDigit(pass) && PwdCheckUtil.checkPasswordLength(pass, "8", "100")) {
         Admin admin = new Admin();
         admin.setName(name);
         admin.setPass(EncodePassUtils.encode(pass));
         admin.setAuth(false);
         admin.setType(0);
         this.sqlHelper.insert(admin);
         return this.renderSuccess();
      } else {
         return this.renderError(this.m.get("loginStr.tips"));
      }
   }

   @Mapping("/getCode")
   public void getCode() throws Exception {
      Context.current().headerAdd("Pragma", "No-cache");
      Context.current().headerAdd("Cache-Control", "no-cache");
      Context.current().headerAdd("Expires", "0");
      Context.current().contentType("image/gif");
      SpecCaptcha specCaptcha = new SpecCaptcha(100, 40, 4);
      specCaptcha.setCharType(2);
      Context.current().sessionSet("captcha", specCaptcha.text().toLowerCase());
      specCaptcha.out(Context.current().outputStream());
   }

   @Mapping("/getRemoteCode")
   public void getRemoteCode() throws Exception {
      Context.current().headerAdd("Pragma", "No-cache");
      Context.current().headerAdd("Cache-Control", "no-cache");
      Context.current().headerAdd("Expires", "0");
      Context.current().contentType("image/gif");
      SpecCaptcha specCaptcha = new SpecCaptcha(100, 40, 4);
      specCaptcha.setCharType(2);
      this.settingService.set("remoteCode", specCaptcha.text());
      specCaptcha.out(Context.current().outputStream());
   }

   @Mapping("/changeLang")
   public JsonResult changeLang() {
      Long adminCount = this.sqlHelper.findAllCount(Admin.class);
      if (adminCount == 0L) {
         if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
            this.settingService.set("lang", "");
         } else {
            this.settingService.set("lang", "en_US");
         }
      }

      return this.renderSuccess();
   }
}
