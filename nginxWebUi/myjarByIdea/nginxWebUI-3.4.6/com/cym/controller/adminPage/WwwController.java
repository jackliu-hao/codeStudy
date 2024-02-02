package com.cym.controller.adminPage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.cym.model.Www;
import com.cym.service.WwwService;
import com.cym.sqlhelper.bean.Sort;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import java.net.URL;
import java.nio.charset.Charset;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapping("/adminPage/www")
@Controller
public class WwwController extends BaseController {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   WwwService wwwService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      modelAndView.put("list", this.sqlHelper.findAll(new Sort("dir", Sort.Direction.ASC), Www.class));
      modelAndView.view("/adminPage/www/index.html");
      return modelAndView;
   }

   @Mapping("addOver")
   public JsonResult addOver(Www www, String dirTemp) {
      if (this.wwwService.hasDir(www.getDir(), www.getId())) {
         return this.renderError(this.m.get("wwwStr.sameDir"));
      } else {
         try {
            try {
               ZipUtil.unzip(dirTemp, www.getDir());
            } catch (Exception var4) {
               ZipUtil.unzip(dirTemp, www.getDir(), Charset.forName("GBK"));
            }

            FileUtil.del(dirTemp);
            this.sqlHelper.insertOrUpdate(www);
            return this.renderSuccess();
         } catch (Exception var5) {
            this.logger.error((String)var5.getMessage(), (Throwable)var5);
            return this.renderError(this.m.get("wwwStr.zipError"));
         }
      }
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.sqlHelper.deleteById(id, Www.class);
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      Www www = (Www)this.sqlHelper.findById(id, Www.class);
      return this.renderSuccess(www);
   }

   public String getClassPath() throws Exception {
      try {
         String strClassName = this.getClass().getName();
         String strPackageName = "";
         if (this.getClass().getPackage() != null) {
            strPackageName = this.getClass().getPackage().getName();
         }

         String strClassFileName = "";
         if (!"".equals(strPackageName)) {
            strClassFileName = strClassName.substring(strPackageName.length() + 1, strClassName.length());
         } else {
            strClassFileName = strClassName;
         }

         URL url = null;
         url = this.getClass().getResource(strClassFileName + ".class");
         String strURL = url.toString();
         strURL = strURL.substring(strURL.indexOf(47) + 1, strURL.lastIndexOf(47));
         return strURL.replaceAll("%20", " ");
      } catch (Exception var6) {
         this.logger.error((String)var6.getMessage(), (Throwable)var6);
         throw var6;
      }
   }
}
