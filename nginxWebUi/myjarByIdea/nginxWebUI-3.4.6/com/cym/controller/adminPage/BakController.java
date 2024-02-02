package com.cym.controller.adminPage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cym.model.Bak;
import com.cym.model.BakSub;
import com.cym.service.BakService;
import com.cym.service.SettingService;
import com.cym.sqlhelper.bean.Page;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Controller
@Mapping("/adminPage/bak")
public class BakController extends BaseController {
   @Inject
   SettingService settingService;
   @Inject
   BakService bakService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView, Page page) {
      page = this.bakService.getList(page);
      modelAndView.put("page", page);
      modelAndView.view("/adminPage/bak/index.html");
      return modelAndView;
   }

   @Mapping("getCompare")
   public JsonResult getCompare(String id) {
      Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
      Bak pre = this.bakService.getPre(id);
      if (pre == null) {
         return this.renderError("没有更早的备份文件");
      } else {
         Map map = new HashMap();
         map.put("bak", bak);
         map.put("pre", pre);
         return this.renderSuccess(map);
      }
   }

   @Mapping("content")
   public JsonResult content(String id) {
      Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
      return this.renderSuccess(bak);
   }

   @Mapping("replace")
   public JsonResult replace(String id) {
      Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
      String nginxPath = this.settingService.get("nginxPath");
      if (!StrUtil.isNotEmpty(nginxPath)) {
         return this.renderError(this.m.get("bakStr.pathNotice"));
      } else {
         File pathFile = new File(nginxPath);
         FileUtil.writeString(bak.getContent(), pathFile, StandardCharsets.UTF_8);
         String confd = pathFile.getParent() + File.separator + "conf.d" + File.separator;
         FileUtil.del(confd);
         FileUtil.mkdir(confd);
         List<BakSub> subList = this.bakService.getSubList(bak.getId());
         Iterator var7 = subList.iterator();

         while(var7.hasNext()) {
            BakSub bakSub = (BakSub)var7.next();
            FileUtil.writeString(bakSub.getContent(), confd + bakSub.getName(), StandardCharsets.UTF_8);
         }

         return this.renderSuccess();
      }
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.bakService.del(id);
      return this.renderSuccess();
   }

   @Mapping("delAll")
   public JsonResult delAll() {
      this.bakService.delAll();
      return this.renderSuccess();
   }
}
