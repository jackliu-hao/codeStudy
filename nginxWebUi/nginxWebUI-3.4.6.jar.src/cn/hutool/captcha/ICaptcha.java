package cn.hutool.captcha;

import java.io.OutputStream;
import java.io.Serializable;

public interface ICaptcha extends Serializable {
  void createCode();
  
  String getCode();
  
  boolean verify(String paramString);
  
  void write(OutputStream paramOutputStream);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\ICaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */