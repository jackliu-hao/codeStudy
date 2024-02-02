package org.h2.server.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import org.h2.util.NetworkConnectionInfo;

public class JakartaWebServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;
   private transient WebServer server;

   public void init() {
      ServletConfig var1 = this.getServletConfig();
      Enumeration var2 = var1.getInitParameterNames();
      ArrayList var3 = new ArrayList();

      while(var2.hasMoreElements()) {
         String var4 = var2.nextElement().toString();
         String var5 = var1.getInitParameter(var4);
         if (!var4.startsWith("-")) {
            var4 = "-" + var4;
         }

         var3.add(var4);
         if (var5.length() > 0) {
            var3.add(var5);
         }
      }

      String[] var6 = (String[])var3.toArray(new String[0]);
      this.server = new WebServer();
      this.server.setAllowChunked(false);
      this.server.init(var6);
   }

   public void destroy() {
      this.server.stop();
   }

   private boolean allow(HttpServletRequest var1) {
      if (this.server.getAllowOthers()) {
         return true;
      } else {
         String var2 = var1.getRemoteAddr();

         try {
            InetAddress var3 = InetAddress.getByName(var2);
            return var3.isLoopbackAddress();
         } catch (NoClassDefFoundError | UnknownHostException var4) {
            return false;
         }
      }
   }

   private String getAllowedFile(HttpServletRequest var1, String var2) {
      if (!this.allow(var1)) {
         return "notAllowed.jsp";
      } else {
         return var2.length() == 0 ? "index.do" : var2;
      }
   }

   public void doGet(HttpServletRequest var1, HttpServletResponse var2) throws IOException {
      var1.setCharacterEncoding("utf-8");
      String var3 = var1.getPathInfo();
      if (var3 == null) {
         var2.sendRedirect(var1.getRequestURI() + "/");
      } else {
         if (var3.startsWith("/")) {
            var3 = var3.substring(1);
         }

         var3 = this.getAllowedFile(var1, var3);
         Properties var4 = new Properties();
         Enumeration var5 = var1.getAttributeNames();

         String var6;
         String var7;
         while(var5.hasMoreElements()) {
            var6 = var5.nextElement().toString();
            var7 = var1.getAttribute(var6).toString();
            var4.put(var6, var7);
         }

         var5 = var1.getParameterNames();

         while(var5.hasMoreElements()) {
            var6 = var5.nextElement().toString();
            var7 = var1.getParameter(var6);
            var4.put(var6, var7);
         }

         WebSession var18 = null;
         var7 = var4.getProperty("jsessionid");
         if (var7 != null) {
            var18 = this.server.getSession(var7);
         }

         WebApp var8 = new WebApp(this.server);
         var8.setSession(var18, var4);
         String var9 = var1.getHeader("if-modified-since");
         String var10 = var1.getScheme();
         StringBuilder var11 = (new StringBuilder(var10)).append("://").append(var1.getServerName());
         int var12 = var1.getServerPort();
         if ((var12 != 80 || !var10.equals("http")) && (var12 != 443 || !var10.equals("https"))) {
            var11.append(':').append(var12);
         }

         String var13 = var11.append(var1.getContextPath()).toString();
         var3 = var8.processRequest(var3, new NetworkConnectionInfo(var13, var1.getRemoteAddr(), var1.getRemotePort()));
         var18 = var8.getSession();
         String var14 = var8.getMimeType();
         boolean var15 = var8.getCache();
         if (var15 && this.server.getStartDateTime().equals(var9)) {
            var2.setStatus(304);
         } else {
            byte[] var16 = this.server.getFile(var3);
            if (var16 == null) {
               var2.sendError(404);
               var16 = ("File not found: " + var3).getBytes(StandardCharsets.UTF_8);
            } else {
               if (var18 != null && var3.endsWith(".jsp")) {
                  String var17 = new String(var16, StandardCharsets.UTF_8);
                  var17 = PageParser.parse(var17, var18.map);
                  var16 = var17.getBytes(StandardCharsets.UTF_8);
               }

               var2.setContentType(var14);
               if (!var15) {
                  var2.setHeader("Cache-Control", "no-cache");
               } else {
                  var2.setHeader("Cache-Control", "max-age=10");
                  var2.setHeader("Last-Modified", this.server.getStartDateTime());
               }
            }

            if (var16 != null) {
               ServletOutputStream var19 = var2.getOutputStream();
               var19.write(var16);
            }

         }
      }
   }

   public void doPost(HttpServletRequest var1, HttpServletResponse var2) throws IOException {
      this.doGet(var1, var2);
   }
}
