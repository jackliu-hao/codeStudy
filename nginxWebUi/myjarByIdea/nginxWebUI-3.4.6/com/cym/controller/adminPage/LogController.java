package com.cym.controller.adminPage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.cym.config.AppFilter;
import com.cym.model.Log;
import com.cym.service.LogService;
import com.cym.service.SettingService;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.utils.BLogFileTailer;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SystemTool;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Mapping("/adminPage/log")
public class LogController extends BaseController {
   Logger logger = LoggerFactory.getLogger(this.getClass());
   @Inject
   SettingService settingService;
   @Inject
   LogService logService;
   @Inject
   AppFilter appFilter;
   @Inject
   BLogFileTailer bLogFileTailer;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView, Page page) {
      page = this.logService.search(page);
      modelAndView.put("page", page);
      modelAndView.put("isLinux", SystemTool.isLinux());
      modelAndView.view("/adminPage/log/index.html");
      return modelAndView;
   }

   @Mapping("addOver")
   public JsonResult addOver(Log log) {
      if (this.logService.hasDir(log.getPath(), log.getId())) {
         return this.renderError(this.m.get("logStr.sameDir"));
      } else if (FileUtil.isDirectory(log.getPath())) {
         return this.renderError(this.m.get("logStr.notFile"));
      } else {
         this.sqlHelper.insertOrUpdate(log);
         return this.renderSuccess();
      }
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      return this.renderSuccess(this.sqlHelper.findById(id, Log.class));
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.sqlHelper.deleteById(id, Log.class);
      return this.renderSuccess();
   }

   @Mapping("clean")
   public JsonResult clean() {
      this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), Log.class);
      return this.renderSuccess();
   }

   @Mapping("tail")
   public ModelAndView tail(ModelAndView modelAndView, String id, String protocol) {
      modelAndView.put("id", id);
      modelAndView.view("/adminPage/log/tail.html");
      return modelAndView;
   }

   @Mapping("down")
   public void down(ModelAndView modelAndView, String id) throws FileNotFoundException {
      Log log = (Log)this.sqlHelper.findById(id, Log.class);
      File file = new File(log.getPath());
      Context.current().contentType("application/octet-stream");
      String headerKey = "Content-Disposition";
      String headerValue = "attachment; filename=" + URLUtil.encode(file.getName());
      Context.current().header(headerKey, headerValue);
      InputStream inputStream = new FileInputStream(file);
      Context.current().output((InputStream)inputStream);
   }

   @Mapping("tailCmd")
   public JsonResult tailCmd(String id, String guid) throws Exception {
      Log log = (Log)this.sqlHelper.findById(id, Log.class);
      if (!FileUtil.exist(log.getPath())) {
         return this.renderSuccess("");
      } else {
         String rs = this.bLogFileTailer.run(guid, log.getPath());
         return this.renderSuccess(rs);
      }
   }
}
