package com.wf.captcha.servlet;

import com.wf.captcha.utils.CaptchaUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaServlet extends HttpServlet {
   private static final long serialVersionUID = -90304944339413093L;

   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      CaptchaUtil.out(request, response);
   }

   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      this.doGet(request, response);
   }
}
