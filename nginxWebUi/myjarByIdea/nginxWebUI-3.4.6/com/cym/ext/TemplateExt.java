package com.cym.ext;

import com.cym.model.Param;
import com.cym.model.Template;
import java.util.List;

public class TemplateExt {
   Template template;
   Integer count;
   List<Param> paramList;

   public List<Param> getParamList() {
      return this.paramList;
   }

   public void setParamList(List<Param> paramList) {
      this.paramList = paramList;
   }

   public Template getTemplate() {
      return this.template;
   }

   public void setTemplate(Template template) {
      this.template = template;
   }

   public Integer getCount() {
      return this.count;
   }

   public void setCount(Integer count) {
      this.count = count;
   }
}
