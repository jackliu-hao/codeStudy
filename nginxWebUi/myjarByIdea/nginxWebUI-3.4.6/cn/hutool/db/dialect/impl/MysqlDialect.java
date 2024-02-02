package cn.hutool.db.dialect.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlDialect extends AnsiSqlDialect {
   private static final long serialVersionUID = -3734718212043823636L;

   public MysqlDialect() {
      this.wrapper = new Wrapper('`');
   }

   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
      return find.append(" LIMIT ").append(page.getStartPosition()).append(", ").append(page.getPageSize());
   }

   public String dialectName() {
      return DialectName.MYSQL.toString();
   }

   public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
      SqlBuilder.validateEntity(entity);
      SqlBuilder builder = SqlBuilder.create(this.wrapper);
      StringBuilder fieldsPart = new StringBuilder();
      StringBuilder placeHolder = new StringBuilder();
      StringBuilder updateHolder = new StringBuilder();
      entity.forEach((field, value) -> {
         if (StrUtil.isNotBlank(field)) {
            if (fieldsPart.length() > 0) {
               fieldsPart.append(", ");
               placeHolder.append(", ");
               updateHolder.append(", ");
            }

            field = null != this.wrapper ? this.wrapper.wrap(field) : field;
            fieldsPart.append(field);
            updateHolder.append(field).append("=values(").append(field).append(")");
            placeHolder.append("?");
            builder.addParams(value);
         }

      });
      String tableName = entity.getTableName();
      if (null != this.wrapper) {
         tableName = this.wrapper.wrap(tableName);
      }

      builder.append("INSERT INTO ").append(tableName).append(" (").append(fieldsPart).append(") VALUES (").append(placeHolder).append(") ON DUPLICATE KEY UPDATE ").append(updateHolder);
      return StatementUtil.prepareStatement(conn, builder);
   }
}
