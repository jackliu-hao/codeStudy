package org.noear.solon.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;

public class SolonServletHandler extends HttpServlet {
   protected void preHandle(Context ctx) {
   }

   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      SolonServletContext ctx = new SolonServletContext(request, response);
      ctx.contentType("text/plain;charset=UTF-8");
      this.preHandle(ctx);
      Solon.app().tryHandle(ctx);
      if (ctx.getHandled() && ctx.status() != 404) {
         ctx.commit();
      } else {
         response.setStatus(404);
      }

   }
}
