package org.h2.server.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.h2.Driver;
import org.h2.tools.Server;
import org.h2.util.StringUtils;

public class DbStarter implements ServletContextListener {
   private Connection conn;
   private Server server;

   public void contextInitialized(ServletContextEvent var1) {
      try {
         Driver.load();
         ServletContext var2 = var1.getServletContext();
         String var3 = getParameter(var2, "db.url", "jdbc:h2:~/test");
         String var4 = getParameter(var2, "db.user", "sa");
         String var5 = getParameter(var2, "db.password", "sa");
         String var6 = getParameter(var2, "db.tcpServer", (String)null);
         if (var6 != null) {
            String[] var7 = StringUtils.arraySplit(var6, ' ', true);
            this.server = Server.createTcpServer(var7);
            this.server.start();
         }

         this.conn = DriverManager.getConnection(var3, var4, var5);
         var2.setAttribute("connection", this.conn);
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   private static String getParameter(ServletContext var0, String var1, String var2) {
      String var3 = var0.getInitParameter(var1);
      return var3 == null ? var2 : var3;
   }

   public Connection getConnection() {
      return this.conn;
   }

   public void contextDestroyed(ServletContextEvent var1) {
      try {
         Statement var2 = this.conn.createStatement();
         var2.execute("SHUTDOWN");
         var2.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      try {
         this.conn.close();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      if (this.server != null) {
         this.server.stop();
         this.server = null;
      }

   }
}
