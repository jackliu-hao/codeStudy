package cn.hutool.captcha.generator;

import java.io.Serializable;

public interface CodeGenerator extends Serializable {
   String generate();

   boolean verify(String var1, String var2);
}
