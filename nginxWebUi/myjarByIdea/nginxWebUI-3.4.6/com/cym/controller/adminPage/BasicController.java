package com.cym.controller.adminPage;

import cn.hutool.core.util.StrUtil;
import com.cym.model.Basic;
import com.cym.service.BasicService;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SnowFlakeUtils;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Controller
@Mapping("/adminPage/basic")
public class BasicController extends BaseController {
   @Inject
   BasicService basicService;

   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      List<Basic> basicList = this.basicService.findAll();
      modelAndView.put("basicList", basicList);
      modelAndView.view("/adminPage/basic/index.html");
      return modelAndView;
   }

   @Mapping("addOver")
   public JsonResult addOver(Basic basic) {
      if (StrUtil.isEmpty(basic.getId())) {
         basic.setSeq(SnowFlakeUtils.getId());
      }

      this.sqlHelper.insertOrUpdate(basic);
      return this.renderSuccess();
   }

   @Mapping("setOrder")
   public JsonResult setOrder(String id, Integer count) {
      this.basicService.setSeq(id, count);
      return this.renderSuccess();
   }

   @Mapping("detail")
   public JsonResult detail(String id) {
      return this.renderSuccess(this.sqlHelper.findById(id, Basic.class));
   }

   @Mapping("del")
   public JsonResult del(String id) {
      this.sqlHelper.deleteById(id, Basic.class);
      return this.renderSuccess();
   }
}
