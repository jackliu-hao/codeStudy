package cn.hutool.extra.template;

public interface TemplateEngine {
  TemplateEngine init(TemplateConfig paramTemplateConfig);
  
  Template getTemplate(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\TemplateEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */