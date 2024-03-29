package com.cym.controller.adminPage;

import com.cym.utils.BaseController;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Mapping("/adminPage/about")
@Controller
public class AboutController extends BaseController {
   @Mapping("")
   public ModelAndView index(ModelAndView modelAndView) {
      modelAndView.view("/adminPage/about/index.html");
      return modelAndView;
   }
}
