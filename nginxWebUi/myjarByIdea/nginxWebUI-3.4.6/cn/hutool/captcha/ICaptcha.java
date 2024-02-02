package cn.hutool.captcha;

import java.io.OutputStream;
import java.io.Serializable;

public interface ICaptcha extends Serializable {
   void createCode();

   String getCode();

   boolean verify(String var1);

   void write(OutputStream var1);
}
