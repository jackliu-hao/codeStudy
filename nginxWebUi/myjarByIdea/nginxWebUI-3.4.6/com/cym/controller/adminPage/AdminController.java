package com.cym.controller.adminPage;

import cn.hutool.core.util.StrUtil;
import com.cym.ext.AdminExt;
import com.cym.ext.Tree;
import com.cym.model.Admin;
import com.cym.model.Group;
import com.cym.service.AdminService;
import com.cym.service.GroupService;
import com.cym.service.SettingService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.AuthUtils;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SendMailUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Mapping("/adminPage/admin")
public class AdminController extends BaseController {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   AdminService adminService;
   @Inject
   SettingService settingService;
   @Inject
   SendMailUtils sendCloudUtils;
   @Inject
   AuthUtils authUtils;
   @Inject
   GroupService groupService;
   @Inject
   RemoteController remoteController;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView, Page page) {
      page = this.adminService.search(page);
      modelAndView.put("page", page);
      modelAndView.view("/adminPage/admin/index.html");
      return modelAndView;
   }

   @Mapping("addOver")
   public JsonResult addOver(Admin admin, String[] parentId) {
      Long count;
      if (StrUtil.isEmpty(admin.getId())) {
         count = this.adminService.getCountByName(admin.getName());
         if (count > 0L) {
            return this.renderError(this.m.get("adminStr.nameRepetition"));
         }
      } else {
         count = this.adminService.getCountByNameWithOutId(admin.getName(), admin.getId());
         if (count > 0L) {
            return this.renderError(this.m.get("adminStr.nameRepetition"));
         }
      }

      if (admin.getAuth()) {
         admin.setKey(this.authUtils.makeKey());
      } else {
         admin.setKey("");
      }

      this.adminService.addOver(admin, parentId);
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      AdminExt adminExt = new AdminExt();
      adminExt.setAdmin((Admin)this.sqlHelper.findById(id, Admin.class));
      adminExt.setGroupIds(this.adminService.getGroupIds(adminExt.getAdmin().getId()));
      adminExt.getAdmin().setPass("");
      return this.renderSuccess(adminExt);
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.sqlHelper.deleteById(id, Admin.class);
      return this.renderSuccess();
   }

   @Mapping("getMailSetting")
   public JsonResult getMailSetting() {
      Map<String, String> map = new HashMap();
      map.put("mail_host", this.settingService.get("mail_host"));
      map.put("mail_port", this.settingService.get("mail_port"));
      map.put("mail_from", this.settingService.get("mail_from"));
      map.put("mail_user", this.settingService.get("mail_user"));
      map.put("mail_pass", this.settingService.get("mail_pass"));
      map.put("mail_ssl", this.settingService.get("mail_ssl"));
      map.put("mail_interval", this.settingService.get("mail_interval"));
      return this.renderSuccess(map);
   }

   @Mapping("updateMailSetting")
   public JsonResult updateMailSetting(String mailType, String mail_user, String mail_host, String mail_port, String mail_from, String mail_pass, String mail_ssl, String mail_interval) {
      this.settingService.set("mail_host", mail_host);
      this.settingService.set("mail_port", mail_port);
      this.settingService.set("mail_user", mail_user);
      this.settingService.set("mail_from", mail_from);
      this.settingService.set("mail_pass", mail_pass);
      this.settingService.set("mail_ssl", mail_ssl);
      this.settingService.set("mail_interval", mail_interval);
      return this.renderSuccess();
   }

   @Mapping("testMail")
   public JsonResult testMail(String mail) {
      if (StrUtil.isEmpty(mail)) {
         return this.renderError(this.m.get("adminStr.emailEmpty"));
      } else {
         try {
            this.sendCloudUtils.sendMailSmtp(mail, this.m.get("adminStr.emailTest"), this.m.get("adminStr.emailTest"));
            return this.renderSuccess();
         } catch (Exception var3) {
            this.logger.error((String)var3.getMessage(), (Throwable)var3);
            return this.renderError(this.m.get("commonStr.error") + ": " + var3.getMessage());
         }
      }
   }

   @Mapping("testAuth")
   public JsonResult testAuth(String key, String code) {
      Boolean rs = this.authUtils.testKey(key, code);
      return this.renderSuccess(rs);
   }

   @Mapping("qr")
   public void getqcode(String url, Integer w, Integer h) throws IOException {
      if (url != null && !"".equals(url)) {
         if (w == null) {
            w = 300;
         }

         if (h == null) {
            h = 300;
         }

         try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix matrix = (new MultiFormatWriter()).encode(url, BarcodeFormat.QR_CODE, w, h, hints);
            MatrixToImageWriter.writeToStream(matrix, "png", Context.current().outputStream());
         } catch (WriterException var6) {
            this.logger.error((String)var6.getMessage(), (Throwable)var6);
         }
      }

   }

   @Mapping("getGroupTree")
   public JsonResult getGroupTree() {
      List<Group> groups = this.groupService.getListByParent((String)null);
      List<Tree> treeList = new ArrayList();
      this.remoteController.fillTree(groups, treeList);
      return this.renderSuccess(treeList);
   }
}
