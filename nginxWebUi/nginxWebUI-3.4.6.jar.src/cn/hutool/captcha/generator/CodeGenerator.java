package cn.hutool.captcha.generator;

import java.io.Serializable;

public interface CodeGenerator extends Serializable {
  String generate();
  
  boolean verify(String paramString1, String paramString2);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\generator\CodeGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */