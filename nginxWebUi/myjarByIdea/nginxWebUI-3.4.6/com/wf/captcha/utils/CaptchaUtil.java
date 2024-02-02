package com.wf.captcha.utils;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import java.awt.Font;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaUtil {
   private static final String SESSION_KEY = "captcha";
   private static final int DEFAULT_LEN = 4;
   private static final int DEFAULT_WIDTH = 130;
   private static final int DEFAULT_HEIGHT = 48;

   public static void out(HttpServletRequest request, HttpServletResponse response) throws IOException {
      out(4, request, response);
   }

   public static void out(int len, HttpServletRequest request, HttpServletResponse response) throws IOException {
      out(130, 48, len, request, response);
   }

   public static void out(int width, int height, int len, HttpServletRequest request, HttpServletResponse response) throws IOException {
      out(width, height, len, (Font)null, request, response);
   }

   public static void out(Font font, HttpServletRequest request, HttpServletResponse response) throws IOException {
      out(130, 48, 4, font, request, response);
   }

   public static void out(int len, Font font, HttpServletRequest request, HttpServletResponse response) throws IOException {
      out(130, 48, len, font, request, response);
   }

   public static void out(int width, int height, int len, Font font, HttpServletRequest request, HttpServletResponse response) throws IOException {
      SpecCaptcha specCaptcha = new SpecCaptcha(width, height, len);
      if (font != null) {
         specCaptcha.setFont(font);
      }

      out((Captcha)specCaptcha, request, response);
   }

   public static void out(Captcha captcha, HttpServletRequest request, HttpServletResponse response) throws IOException {
      setHeader(response);
      request.getSession().setAttribute("captcha", captcha.text().toLowerCase());
      captcha.out(response.getOutputStream());
   }

   public static boolean ver(String code, HttpServletRequest request) {
      if (code != null) {
         String captcha = (String)request.getSession().getAttribute("captcha");
         return code.trim().toLowerCase().equals(captcha);
      } else {
         return false;
      }
   }

   public static void clear(HttpServletRequest request) {
      request.getSession().removeAttribute("captcha");
   }

   public static void setHeader(HttpServletResponse response) {
      response.setContentType("image/gif");
      response.setHeader("Pragma", "No-cache");
      response.setHeader("Cache-Control", "no-cache");
      response.setDateHeader("Expires", 0L);
   }
}
