package com.cym.controller.adminPage;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cym.ext.TemplateExt;
import com.cym.model.Param;
import com.cym.model.Template;
import com.cym.service.TemplateService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Controller
@Mapping("/adminPage/template")
public class TemplateController extends BaseController {
   @Inject
   TemplateService templateService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      List<Template> templateList = this.sqlHelper.findAll(Template.class);
      List<TemplateExt> extList = new ArrayList();
      Iterator var4 = templateList.iterator();

      while(var4.hasNext()) {
         Template template = (Template)var4.next();
         TemplateExt templateExt = new TemplateExt();
         templateExt.setTemplate(template);
         templateExt.setParamList(this.templateService.getParamList(template.getId()));
         templateExt.setCount(templateExt.getParamList().size());
         extList.add(templateExt);
      }

      modelAndView.put("templateList", extList);
      modelAndView.view("/adminPage/template/index.html");
      return modelAndView;
   }

   @Mapping("addOver")
   public JsonResult addOver(Template template, String paramJson) {
      Long count;
      if (StrUtil.isEmpty(template.getId())) {
         count = this.templateService.getCountByName(template.getName());
         if (count > 0L) {
            return this.renderError(this.m.get("templateStr.sameName"));
         }
      } else {
         count = this.templateService.getCountByNameWithOutId(template.getName(), template.getId());
         if (count > 0L) {
            return this.renderError(this.m.get("templateStr.sameName"));
         }
      }

      List<Param> params = JSONUtil.toList(JSONUtil.parseArray(paramJson), Param.class);
      this.templateService.addOver(template, params);
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      Template template = (Template)this.sqlHelper.findById(id, Template.class);
      TemplateExt templateExt = new TemplateExt();
      templateExt.setTemplate(template);
      templateExt.setParamList(this.templateService.getParamList(template.getId()));
      templateExt.setCount(templateExt.getParamList().size());
      return this.renderSuccess(templateExt);
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.templateService.del(id);
      return this.renderSuccess();
   }

   @Mapping("getTemplate")
   public JsonResult getTemplate() {
      return this.renderSuccess(this.sqlHelper.findAll(Template.class));
   }
}
