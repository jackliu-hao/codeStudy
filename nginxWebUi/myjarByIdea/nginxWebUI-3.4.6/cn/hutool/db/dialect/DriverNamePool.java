package cn.hutool.db.dialect;

public interface DriverNamePool {
   String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
   String DRIVER_MYSQL_V6 = "com.mysql.cj.jdbc.Driver";
   String DRIVER_MARIADB = "org.mariadb.jdbc.Driver";
   String DRIVER_ORACLE = "oracle.jdbc.OracleDriver";
   String DRIVER_ORACLE_OLD = "oracle.jdbc.driver.OracleDriver";
   String DRIVER_POSTGRESQL = "org.postgresql.Driver";
   String DRIVER_SQLLITE3 = "org.sqlite.JDBC";
   String DRIVER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
   String DRIVER_HIVE = "org.apache.hadoop.hive.jdbc.HiveDriver";
   String DRIVER_HIVE2 = "org.apache.hive.jdbc.HiveDriver";
   String DRIVER_H2 = "org.h2.Driver";
   String DRIVER_DERBY = "org.apache.derby.jdbc.AutoloadedDriver";
   String DRIVER_HSQLDB = "org.hsqldb.jdbc.JDBCDriver";
   String DRIVER_DM7 = "dm.jdbc.driver.DmDriver";
   String DRIVER_KINGBASE8 = "com.kingbase8.Driver";
   String DRIVER_IGNITE_THIN = "org.apache.ignite.IgniteJdbcThinDriver";
   String DRIVER_CLICK_HOUSE = "ru.yandex.clickhouse.ClickHouseDriver";
   String DRIVER_HIGHGO = "com.highgo.jdbc.Driver";
   String DRIVER_DB2 = "com.ibm.db2.jdbc.app.DB2Driver";
   String DRIVER_XUGU = "com.xugu.cloudjdbc.Driver";
   String DRIVER_PHOENIX = "org.apache.phoenix.jdbc.PhoenixDriver";
   String DRIVER_GAUSS = "com.huawei.gauss.jdbc.ZenithDriver";
   String DRIVER_GBASE = "com.gbase.jdbc.Driver";
   String DRIVER_OSCAR = "com.oscar.Driver";
   String DRIVER_SYBASE = "com.sybase.jdbc4.jdbc.SybDriver";
   String DRIVER_XUGO = "com.xugu.cloudjdbc.Driver";
}
