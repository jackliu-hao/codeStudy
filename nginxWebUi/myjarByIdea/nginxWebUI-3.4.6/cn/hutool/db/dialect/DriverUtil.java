package cn.hutool.db.dialect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.ds.DataSourceWrapper;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DriverUtil {
   public static String identifyDriver(String nameContainsProductInfo) {
      return DialectFactory.identifyDriver(nameContainsProductInfo);
   }

   public static String identifyDriver(DataSource ds) {
      if (ds instanceof DataSourceWrapper) {
         String driver = ((DataSourceWrapper)ds).getDriver();
         if (StrUtil.isNotBlank(driver)) {
            return driver;
         }
      }

      Connection conn = null;

      String driver;
      try {
         try {
            conn = ds.getConnection();
         } catch (SQLException var8) {
            throw new DbRuntimeException("Get Connection error !", var8);
         } catch (NullPointerException var9) {
            throw new DbRuntimeException("Unexpected NullPointException, maybe [jdbcUrl] or [url] is empty!", var9);
         }

         driver = identifyDriver(conn);
      } finally {
         DbUtil.close(conn);
      }

      return driver;
   }

   public static String identifyDriver(Connection conn) throws DbRuntimeException {
      try {
         DatabaseMetaData meta = conn.getMetaData();
         String driver = identifyDriver(meta.getDatabaseProductName());
         if (StrUtil.isBlank(driver)) {
            driver = identifyDriver(meta.getDriverName());
         }

         return driver;
      } catch (SQLException var4) {
         throw new DbRuntimeException("Identify driver error!", var4);
      }
   }
}
