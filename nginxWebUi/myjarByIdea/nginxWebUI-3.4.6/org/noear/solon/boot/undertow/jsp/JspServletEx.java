package org.noear.solon.boot.undertow.jsp;

import io.undertow.servlet.api.ServletInfo;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.jasper.servlet.JspServlet;
import org.noear.solon.core.handle.Context;

public class JspServletEx extends JspServlet {
   public static ServletInfo createServlet(String name, String path) {
      ServletInfo servlet = new ServletInfo(name, JspServletEx.class);
      servlet.addMapping(path);
      servlet.setRequireWelcomeFileMapping(true);
      return servlet;
   }

   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
      if (Context.current() != null) {
         super.service(req, res);
      }
   }
}
