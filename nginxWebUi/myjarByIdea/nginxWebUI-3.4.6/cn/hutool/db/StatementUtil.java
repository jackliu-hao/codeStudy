package cn.hutool.db;

import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.handler.HandleHelper;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.NamedSql;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlLog;
import cn.hutool.db.sql.SqlUtil;
import java.lang.invoke.SerializedLambda;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StatementUtil {
   public static PreparedStatement fillParams(PreparedStatement ps, Object... params) throws SQLException {
      return ArrayUtil.isEmpty(params) ? ps : fillParams(ps, (Iterable)(new ArrayIter(params)));
   }

   public static PreparedStatement fillParams(PreparedStatement ps, Iterable<?> params) throws SQLException {
      return fillParams(ps, params, (Map)null);
   }

   public static PreparedStatement fillParams(PreparedStatement ps, Iterable<?> params, Map<Integer, Integer> nullTypeCache) throws SQLException {
      if (null == params) {
         return ps;
      } else {
         int paramIndex = 1;
         Iterator var4 = params.iterator();

         while(var4.hasNext()) {
            Object param = var4.next();
            setParam(ps, paramIndex++, param, nullTypeCache);
         }

         return ps;
      }
   }

   public static PreparedStatement prepareStatement(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
      return prepareStatement(conn, sqlBuilder.build(), sqlBuilder.getParamValueArray());
   }

   public static PreparedStatement prepareStatement(Connection conn, String sql, Collection<Object> params) throws SQLException {
      return prepareStatement(conn, sql, params.toArray(new Object[0]));
   }

   public static PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
      Assert.notBlank(sql, "Sql String must be not blank!");
      sql = sql.trim();
      if (ArrayUtil.isNotEmpty(params) && 1 == params.length && params[0] instanceof Map) {
         NamedSql namedSql = new NamedSql(sql, Convert.toMap(String.class, Object.class, params[0]));
         sql = namedSql.getSql();
         params = namedSql.getParams();
      }

      SqlLog.INSTANCE.log(sql, ArrayUtil.isEmpty(params) ? null : params);
      PreparedStatement ps;
      if (GlobalDbConfig.returnGeneratedKey && StrUtil.startWithIgnoreCase(sql, "insert")) {
         ps = conn.prepareStatement(sql, 1);
      } else {
         ps = conn.prepareStatement(sql);
      }

      return fillParams(ps, params);
   }

   public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Object[]... paramsBatch) throws SQLException {
      return prepareStatementForBatch(conn, sql, (Iterable)(new ArrayIter(paramsBatch)));
   }

   public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Iterable<Object[]> paramsBatch) throws SQLException {
      Assert.notBlank(sql, "Sql String must be not blank!");
      sql = sql.trim();
      SqlLog.INSTANCE.log(sql, paramsBatch);
      PreparedStatement ps = conn.prepareStatement(sql);
      Map<Integer, Integer> nullTypeMap = new HashMap();
      Iterator var5 = paramsBatch.iterator();

      while(var5.hasNext()) {
         Object[] params = (Object[])var5.next();
         fillParams(ps, new ArrayIter(params), nullTypeMap);
         ps.addBatch();
      }

      return ps;
   }

   public static PreparedStatement prepareStatementForBatch(Connection conn, String sql, Iterable<String> fields, Entity... entities) throws SQLException {
      Assert.notBlank(sql, "Sql String must be not blank!");
      sql = sql.trim();
      SqlLog.INSTANCE.logForBatch(sql);
      PreparedStatement ps = conn.prepareStatement(sql);
      Map<Integer, Integer> nullTypeMap = new HashMap();
      Entity[] var6 = entities;
      int var7 = entities.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Entity entity = var6[var8];
         fillParams(ps, CollUtil.valuesOfKeys(entity, (Iterable)fields), nullTypeMap);
         ps.addBatch();
      }

      return ps;
   }

   public static CallableStatement prepareCall(Connection conn, String sql, Object... params) throws SQLException {
      Assert.notBlank(sql, "Sql String must be not blank!");
      sql = sql.trim();
      SqlLog.INSTANCE.log(sql, params);
      CallableStatement call = conn.prepareCall(sql);
      fillParams(call, (Object[])params);
      return call;
   }

   public static Long getGeneratedKeyOfLong(Statement ps) throws SQLException {
      return (Long)getGeneratedKeys(ps, (rs) -> {
         Long generatedKey = null;
         if (rs != null && rs.next()) {
            try {
               generatedKey = rs.getLong(1);
            } catch (SQLException var3) {
            }
         }

         return generatedKey;
      });
   }

   public static List<Object> getGeneratedKeys(Statement ps) throws SQLException {
      return (List)getGeneratedKeys(ps, HandleHelper::handleRowToList);
   }

   public static <T> T getGeneratedKeys(Statement statement, RsHandler<T> rsHandler) throws SQLException {
      ResultSet rs = statement.getGeneratedKeys();
      Throwable var3 = null;

      Object var4;
      try {
         var4 = rsHandler.handle(rs);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (rs != null) {
            if (var3 != null) {
               try {
                  rs.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               rs.close();
            }
         }

      }

      return var4;
   }

   public static int getTypeOfNull(PreparedStatement ps, int paramIndex) {
      int sqlType = 12;

      try {
         ParameterMetaData pmd = ps.getParameterMetaData();
         sqlType = pmd.getParameterType(paramIndex);
      } catch (SQLException var5) {
      }

      return sqlType;
   }

   public static void setParam(PreparedStatement ps, int paramIndex, Object param) throws SQLException {
      setParam(ps, paramIndex, param, (Map)null);
   }

   private static void setParam(PreparedStatement ps, int paramIndex, Object param, Map<Integer, Integer> nullTypeCache) throws SQLException {
      if (null == param) {
         Integer type = null == nullTypeCache ? null : (Integer)nullTypeCache.get(paramIndex);
         if (null == type) {
            type = getTypeOfNull(ps, paramIndex);
            if (null != nullTypeCache) {
               nullTypeCache.put(paramIndex, type);
            }
         }

         ps.setNull(paramIndex, type);
      }

      if (param instanceof Date) {
         if (param instanceof java.sql.Date) {
            ps.setDate(paramIndex, (java.sql.Date)param);
         } else if (param instanceof Time) {
            ps.setTime(paramIndex, (Time)param);
         } else {
            ps.setTimestamp(paramIndex, SqlUtil.toSqlTimestamp((Date)param));
         }

      } else {
         if (param instanceof Number) {
            if (param instanceof BigDecimal) {
               ps.setBigDecimal(paramIndex, (BigDecimal)param);
               return;
            }

            if (param instanceof BigInteger) {
               ps.setBigDecimal(paramIndex, new BigDecimal((BigInteger)param));
               return;
            }
         }

         ps.setObject(paramIndex, param);
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "handleRowToList":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/handler/HandleHelper") && lambda.getImplMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/util/List;")) {
               return HandleHelper::handleRowToList;
            }
            break;
         case "lambda$getGeneratedKeyOfLong$d32a099d$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/StatementUtil") && lambda.getImplMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Long;")) {
               return (rs) -> {
                  Long generatedKey = null;
                  if (rs != null && rs.next()) {
                     try {
                        generatedKey = rs.getLong(1);
                     } catch (SQLException var3) {
                     }
                  }

                  return generatedKey;
               };
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
