package com.mysql.cj.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Driver extends NonRegisteringDriver implements java.sql.Driver {
   public Driver() throws SQLException {
   }

   static {
      try {
         DriverManager.registerDriver(new Driver());
      } catch (SQLException var1) {
         throw new RuntimeException("Can't register driver!");
      }
   }
}
