package cn.hutool.db.sql;

import cn.hutool.core.util.StrUtil;

public enum LogicalOperator {
   AND,
   OR;

   public boolean isSame(String logicalOperatorStr) {
      return StrUtil.isBlank(logicalOperatorStr) ? false : this.name().equalsIgnoreCase(logicalOperatorStr.trim());
   }
}
