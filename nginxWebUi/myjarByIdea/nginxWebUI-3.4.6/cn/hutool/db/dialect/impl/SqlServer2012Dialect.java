package cn.hutool.db.dialect.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Page;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;

public class SqlServer2012Dialect extends AnsiSqlDialect {
   private static final long serialVersionUID = -37598166015777797L;

   public SqlServer2012Dialect() {
      this.wrapper = new Wrapper('"');
   }

   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
      if (!StrUtil.containsIgnoreCase(find.toString(), "order by")) {
         find.append(" order by current_timestamp");
      }

      return find.append(" offset ").append(page.getStartPosition()).append(" row fetch next ").append(page.getPageSize()).append(" row only");
   }

   public String dialectName() {
      return DialectName.SQLSERVER2012.name();
   }
}
