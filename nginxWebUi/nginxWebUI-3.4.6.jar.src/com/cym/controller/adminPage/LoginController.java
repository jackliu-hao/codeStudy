/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.config.VersionConfig;
/*     */ import com.cym.model.Admin;
/*     */ import com.cym.model.Remote;
/*     */ import com.cym.service.AdminService;
/*     */ import com.cym.service.CreditService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.utils.AuthUtils;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.EncodePassUtils;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.PwdCheckUtil;
/*     */ import com.cym.utils.SystemTool;
/*     */ import com.wf.captcha.SpecCaptcha;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Mapping("/adminPage/login")
/*     */ @Controller
/*     */ public class LoginController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   AdminService adminService;
/*     */   @Inject
/*     */   CreditService creditService;
/*     */   @Inject
/*     */   VersionConfig versionConfig;
/*     */   @Inject
/*     */   AuthUtils authUtils;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView admin(ModelAndView modelAndView, String adminId) {
/*  52 */     modelAndView.put("adminCount", this.sqlHelper.findAllCount(Admin.class));
/*  53 */     modelAndView.view("/adminPage/login/index.html");
/*  54 */     return modelAndView;
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("loginOut")
/*     */   public ModelAndView loginOut(ModelAndView modelAndView) {
/*  60 */     Context.current().sessionRemove("isLogin");
/*     */     
/*  62 */     modelAndView.view("/adminPage/index.html");
/*  63 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("noServer")
/*     */   public ModelAndView noServer(ModelAndView modelAndView) {
/*  68 */     modelAndView.view("/adminPage/login/noServer.html");
/*  69 */     return modelAndView;
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("login")
/*     */   public JsonResult submitLogin(String name, String pass, String code, String authCode, String remember) {
/*  75 */     if (StrUtil.isNotEmpty(name)) {
/*  76 */       name = Base64.decodeStr(Base64.decodeStr(name));
/*     */     }
/*  78 */     if (StrUtil.isNotEmpty(pass)) {
/*  79 */       pass = Base64.decodeStr(Base64.decodeStr(pass));
/*     */     }
/*  81 */     if (StrUtil.isNotEmpty(code)) {
/*  82 */       code = Base64.decodeStr(Base64.decodeStr(code));
/*     */     }
/*  84 */     if (StrUtil.isNotEmpty(authCode)) {
/*  85 */       authCode = Base64.decodeStr(Base64.decodeStr(authCode));
/*     */     }
/*     */ 
/*     */     
/*  89 */     String captcha = (String)Context.current().session("captcha");
/*  90 */     if (!code.equals(captcha)) {
/*  91 */       Context.current().sessionRemove("captcha");
/*  92 */       return renderError(this.m.get("loginStr.backError1"));
/*     */     } 
/*  94 */     Context.current().sessionRemove("captcha");
/*     */ 
/*     */     
/*  97 */     Admin admin = this.adminService.login(name, pass);
/*  98 */     if (admin == null) {
/*  99 */       return renderError(this.m.get("loginStr.backError2"));
/*     */     }
/*     */ 
/*     */     
/* 103 */     if (admin.getAuth().booleanValue() && !this.authUtils.testKey(admin.getKey(), authCode).booleanValue()) {
/* 104 */       return renderError(this.m.get("loginStr.backError6"));
/*     */     }
/*     */ 
/*     */     
/* 108 */     Context.current().sessionSet("localType", "local");
/* 109 */     Context.current().sessionSet("isLogin", Boolean.valueOf(true));
/* 110 */     Context.current().sessionSet("admin", admin);
/* 111 */     Context.current().sessionRemove("imgCode");
/*     */ 
/*     */     
/* 114 */     this.versionConfig.checkVersion();
/*     */     
/* 116 */     return renderSuccess(admin);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("autoLogin")
/*     */   public JsonResult autoLogin(String adminId) {
/* 123 */     Admin admin = (Admin)this.sqlHelper.findById(adminId, Admin.class);
/* 124 */     if (admin != null) {
/*     */       
/* 126 */       Context.current().sessionSet("localType", "local");
/* 127 */       Context.current().sessionSet("isLogin", Boolean.valueOf(true));
/* 128 */       Context.current().sessionSet("admin", admin);
/* 129 */       Context.current().sessionRemove("imgCode");
/*     */ 
/*     */       
/* 132 */       this.versionConfig.checkVersion();
/*     */       
/* 134 */       return renderSuccess(admin);
/*     */     } 
/* 136 */     return renderError();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getAuth")
/*     */   public JsonResult getAuth(String name, String pass, String code, Integer remote) {
/* 145 */     if (StrUtil.isNotEmpty(name)) {
/* 146 */       name = Base64.decodeStr(Base64.decodeStr(name));
/*     */     }
/* 148 */     if (StrUtil.isNotEmpty(pass)) {
/* 149 */       pass = Base64.decodeStr(Base64.decodeStr(pass));
/*     */     }
/* 151 */     if (StrUtil.isNotEmpty(code)) {
/* 152 */       code = Base64.decodeStr(Base64.decodeStr(code));
/*     */     }
/*     */ 
/*     */     
/* 156 */     if (remote == null) {
/* 157 */       String captcha = (String)Context.current().session("captcha");
/* 158 */       if (!code.equals(captcha)) {
/* 159 */         Context.current().sessionRemove("captcha");
/* 160 */         return renderError(this.m.get("loginStr.backError1"));
/*     */       } 
/*     */     } 
/*     */     
/* 164 */     Admin admin = this.adminService.login(name, pass);
/* 165 */     if (admin == null) {
/* 166 */       return renderError(this.m.get("loginStr.backError2"));
/*     */     }
/*     */     
/* 169 */     Admin ad = new Admin();
/* 170 */     ad.setAuth(admin.getAuth());
/* 171 */     ad.setKey(admin.getKey());
/*     */     
/* 173 */     return renderSuccess(ad);
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("getCredit")
/*     */   public JsonResult getCredit(String name, String pass, String code, String auth) {
/* 179 */     if (StrUtil.isNotEmpty(name)) {
/* 180 */       name = Base64.decodeStr(Base64.decodeStr(name));
/*     */     }
/* 182 */     if (StrUtil.isNotEmpty(pass)) {
/* 183 */       pass = Base64.decodeStr(Base64.decodeStr(pass));
/*     */     }
/* 185 */     if (StrUtil.isNotEmpty(code)) {
/* 186 */       code = Base64.decodeStr(Base64.decodeStr(code));
/*     */     }
/*     */ 
/*     */     
/* 190 */     Admin admin = this.adminService.login(name, pass);
/* 191 */     if (admin == null) {
/* 192 */       return renderError(this.m.get("loginStr.backError2"));
/*     */     }
/*     */     
/* 195 */     if (!admin.getAuth().booleanValue()) {
/* 196 */       String imgCode = this.settingService.get("remoteCode");
/* 197 */       if (StrUtil.isEmpty(imgCode) || (StrUtil.isNotEmpty(imgCode) && !imgCode.equalsIgnoreCase(code))) {
/* 198 */         return renderError(this.m.get("loginStr.backError1"));
/*     */       }
/*     */     }
/* 201 */     else if (!this.authUtils.testKey(admin.getKey(), auth).booleanValue()) {
/* 202 */       return renderError(this.m.get("loginStr.backError6"));
/*     */     } 
/*     */ 
/*     */     
/* 206 */     this.settingService.remove("remoteCode");
/*     */     
/* 208 */     Map<String, String> map = new HashMap<>();
/* 209 */     map.put("creditKey", this.creditService.make(admin.getId()));
/* 210 */     map.put("system", SystemTool.getSystem());
/* 211 */     return renderSuccess(map);
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("getLocalType")
/*     */   public JsonResult getLocalType() {
/* 217 */     String localType = (String)Context.current().session("localType");
/* 218 */     if (StrUtil.isNotEmpty(localType)) {
/* 219 */       if ("local".equals(localType)) {
/* 220 */         return renderSuccess(this.m.get("remoteStr.local"));
/*     */       }
/* 222 */       Remote remote = (Remote)Context.current().session("remote");
/* 223 */       if (StrUtil.isNotEmpty(remote.getDescr())) {
/* 224 */         return renderSuccess(remote.getDescr());
/*     */       }
/*     */       
/* 227 */       return renderSuccess(remote.getIp() + ":" + remote.getPort());
/*     */     } 
/*     */ 
/*     */     
/* 231 */     return renderSuccess("");
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("addAdmin")
/*     */   public JsonResult addAdmin(String name, String pass) {
/* 237 */     Long adminCount = this.sqlHelper.findAllCount(Admin.class);
/* 238 */     if (adminCount.longValue() > 0L) {
/* 239 */       return renderError(this.m.get("loginStr.backError4"));
/*     */     }
/*     */     
/* 242 */     if (!PwdCheckUtil.checkContainUpperCase(pass) || !PwdCheckUtil.checkContainLowerCase(pass) || !PwdCheckUtil.checkContainDigit(pass) || !PwdCheckUtil.checkPasswordLength(pass, "8", "100")) {
/* 243 */       return renderError(this.m.get("loginStr.tips"));
/*     */     }
/*     */     
/* 246 */     Admin admin = new Admin();
/* 247 */     admin.setName(name);
/* 248 */     admin.setPass(EncodePassUtils.encode(pass));
/* 249 */     admin.setAuth(Boolean.valueOf(false));
/* 250 */     admin.setType(Integer.valueOf(0));
/*     */     
/* 252 */     this.sqlHelper.insert(admin);
/*     */     
/* 254 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("/getCode")
/*     */   public void getCode() throws Exception {
/* 259 */     Context.current().headerAdd("Pragma", "No-cache");
/* 260 */     Context.current().headerAdd("Cache-Control", "no-cache");
/* 261 */     Context.current().headerAdd("Expires", "0");
/* 262 */     Context.current().contentType("image/gif");
/*     */     
/* 264 */     SpecCaptcha specCaptcha = new SpecCaptcha(100, 40, 4);
/* 265 */     specCaptcha.setCharType(2);
/* 266 */     Context.current().sessionSet("captcha", specCaptcha.text().toLowerCase());
/* 267 */     specCaptcha.out(Context.current().outputStream());
/*     */   }
/*     */   
/*     */   @Mapping("/getRemoteCode")
/*     */   public void getRemoteCode() throws Exception {
/* 272 */     Context.current().headerAdd("Pragma", "No-cache");
/* 273 */     Context.current().headerAdd("Cache-Control", "no-cache");
/* 274 */     Context.current().headerAdd("Expires", "0");
/* 275 */     Context.current().contentType("image/gif");
/*     */     
/* 277 */     SpecCaptcha specCaptcha = new SpecCaptcha(100, 40, 4);
/* 278 */     specCaptcha.setCharType(2);
/* 279 */     this.settingService.set("remoteCode", specCaptcha.text());
/* 280 */     specCaptcha.out(Context.current().outputStream());
/*     */   }
/*     */   
/*     */   @Mapping("/changeLang")
/*     */   public JsonResult changeLang() {
/* 285 */     Long adminCount = this.sqlHelper.findAllCount(Admin.class);
/* 286 */     if (adminCount.longValue() == 0L)
/*     */     {
/* 288 */       if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
/* 289 */         this.settingService.set("lang", "");
/*     */       } else {
/* 291 */         this.settingService.set("lang", "en_US");
/*     */       } 
/*     */     }
/*     */     
/* 295 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\LoginController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */