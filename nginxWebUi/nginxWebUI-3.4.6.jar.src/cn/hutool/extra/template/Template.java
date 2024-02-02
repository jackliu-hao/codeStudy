package cn.hutool.extra.template;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

public interface Template {
  void render(Map<?, ?> paramMap, Writer paramWriter);
  
  void render(Map<?, ?> paramMap, OutputStream paramOutputStream);
  
  void render(Map<?, ?> paramMap, File paramFile);
  
  String render(Map<?, ?> paramMap);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\Template.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */