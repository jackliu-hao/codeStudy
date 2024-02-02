/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.ext.AdminExt;
/*     */ import com.cym.ext.Tree;
/*     */ import com.cym.model.Admin;
/*     */ import com.cym.model.Group;
/*     */ import com.cym.service.AdminService;
/*     */ import com.cym.service.GroupService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.utils.AuthUtils;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.SendMailUtils;
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.MultiFormatWriter;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.client.j2se.MatrixToImageWriter;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/admin")
/*     */ public class AdminController
/*     */   extends BaseController
/*     */ {
/*  43 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   @Inject
/*     */   AdminService adminService;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   SendMailUtils sendCloudUtils;
/*     */   @Inject
/*     */   AuthUtils authUtils;
/*     */   @Inject
/*     */   GroupService groupService;
/*     */   @Inject
/*     */   RemoteController remoteController;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView, Page page) {
/*  59 */     page = this.adminService.search(page);
/*  60 */     modelAndView.put("page", page);
/*  61 */     modelAndView.view("/adminPage/admin/index.html");
/*  62 */     return modelAndView;
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(Admin admin, String[] parentId) {
/*  67 */     if (StrUtil.isEmpty(admin.getId())) {
/*  68 */       Long count = this.adminService.getCountByName(admin.getName());
/*  69 */       if (count.longValue() > 0L) {
/*  70 */         return renderError(this.m.get("adminStr.nameRepetition"));
/*     */       }
/*     */     } else {
/*  73 */       Long count = this.adminService.getCountByNameWithOutId(admin.getName(), admin.getId());
/*  74 */       if (count.longValue() > 0L) {
/*  75 */         return renderError(this.m.get("adminStr.nameRepetition"));
/*     */       }
/*     */     } 
/*     */     
/*  79 */     if (admin.getAuth().booleanValue()) {
/*  80 */       admin.setKey(this.authUtils.makeKey());
/*     */     } else {
/*  82 */       admin.setKey("");
/*     */     } 
/*     */     
/*  85 */     this.adminService.addOver(admin, parentId);
/*     */     
/*  87 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/*  92 */     AdminExt adminExt = new AdminExt();
/*  93 */     adminExt.setAdmin((Admin)this.sqlHelper.findById(id, Admin.class));
/*  94 */     adminExt.setGroupIds(this.adminService.getGroupIds(adminExt.getAdmin().getId()));
/*  95 */     adminExt.getAdmin().setPass("");
/*  96 */     return renderSuccess(adminExt);
/*     */   }
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/* 101 */     this.sqlHelper.deleteById(id, Admin.class);
/*     */     
/* 103 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("getMailSetting")
/*     */   public JsonResult getMailSetting() {
/* 108 */     Map<String, String> map = new HashMap<>();
/*     */     
/* 110 */     map.put("mail_host", this.settingService.get("mail_host"));
/* 111 */     map.put("mail_port", this.settingService.get("mail_port"));
/* 112 */     map.put("mail_from", this.settingService.get("mail_from"));
/* 113 */     map.put("mail_user", this.settingService.get("mail_user"));
/* 114 */     map.put("mail_pass", this.settingService.get("mail_pass"));
/* 115 */     map.put("mail_ssl", this.settingService.get("mail_ssl"));
/* 116 */     map.put("mail_interval", this.settingService.get("mail_interval"));
/*     */     
/* 118 */     return renderSuccess(map);
/*     */   }
/*     */   
/*     */   @Mapping("updateMailSetting")
/*     */   public JsonResult updateMailSetting(String mailType, String mail_user, String mail_host, String mail_port, String mail_from, String mail_pass, String mail_ssl, String mail_interval) {
/* 123 */     this.settingService.set("mail_host", mail_host);
/* 124 */     this.settingService.set("mail_port", mail_port);
/* 125 */     this.settingService.set("mail_user", mail_user);
/* 126 */     this.settingService.set("mail_from", mail_from);
/* 127 */     this.settingService.set("mail_pass", mail_pass);
/* 128 */     this.settingService.set("mail_ssl", mail_ssl);
/* 129 */     this.settingService.set("mail_interval", mail_interval);
/*     */     
/* 131 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("testMail")
/*     */   public JsonResult testMail(String mail) {
/* 136 */     if (StrUtil.isEmpty(mail)) {
/* 137 */       return renderError(this.m.get("adminStr.emailEmpty"));
/*     */     }
/*     */     try {
/* 140 */       this.sendCloudUtils.sendMailSmtp(mail, this.m.get("adminStr.emailTest"), this.m.get("adminStr.emailTest"));
/* 141 */       return renderSuccess();
/* 142 */     } catch (Exception e) {
/* 143 */       this.logger.error(e.getMessage(), e);
/* 144 */       return renderError(this.m.get("commonStr.error") + ": " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("testAuth")
/*     */   public JsonResult testAuth(String key, String code) {
/* 151 */     Boolean rs = this.authUtils.testKey(key, code);
/* 152 */     return renderSuccess(rs);
/*     */   }
/*     */   
/*     */   @Mapping("qr")
/*     */   public void getqcode(String url, Integer w, Integer h) throws IOException {
/* 157 */     if (url != null && !"".equals(url)) {
/*     */       
/* 159 */       if (w == null) {
/* 160 */         w = Integer.valueOf(300);
/*     */       }
/* 162 */       if (h == null) {
/* 163 */         h = Integer.valueOf(300);
/*     */       }
/*     */       try {
/* 166 */         Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
/* 167 */         hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
/* 168 */         hints.put(EncodeHintType.MARGIN, Integer.valueOf(0));
/*     */         
/* 170 */         BitMatrix matrix = (new MultiFormatWriter()).encode(url, BarcodeFormat.QR_CODE, w.intValue(), h.intValue(), hints);
/* 171 */         MatrixToImageWriter.writeToStream(matrix, "png", Context.current().outputStream());
/* 172 */       } catch (WriterException e) {
/* 173 */         this.logger.error(e.getMessage(), (Throwable)e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("getGroupTree")
/*     */   public JsonResult getGroupTree() {
/* 181 */     List<Group> groups = this.groupService.getListByParent(null);
/* 182 */     List<Tree> treeList = new ArrayList<>();
/* 183 */     this.remoteController.fillTree(groups, treeList);
/*     */     
/* 185 */     return renderSuccess(treeList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\AdminController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */