package org.noear.solon.web.servlet.holder;

import java.util.Objects;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;

public class ServletHolder {
   public final WebServlet anno;
   public final Servlet servlet;

   public ServletHolder(WebServlet anno, Servlet servlet) {
      this.anno = anno;
      this.servlet = servlet;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ServletHolder that = (ServletHolder)o;
         return Objects.equals(this.anno, that.anno) && Objects.equals(this.servlet, that.servlet);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.anno, this.servlet});
   }
}
