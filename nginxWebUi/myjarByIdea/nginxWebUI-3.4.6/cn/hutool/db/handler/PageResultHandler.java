package cn.hutool.db.handler;

import cn.hutool.db.Entity;
import cn.hutool.db.PageResult;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PageResultHandler implements RsHandler<PageResult<Entity>> {
   private static final long serialVersionUID = -1474161855834070108L;
   private final PageResult<Entity> pageResult;
   private final boolean caseInsensitive;

   public static PageResultHandler create(PageResult<Entity> pageResult) {
      return new PageResultHandler(pageResult);
   }

   public PageResultHandler(PageResult<Entity> pageResult) {
      this(pageResult, false);
   }

   public PageResultHandler(PageResult<Entity> pageResult, boolean caseInsensitive) {
      this.pageResult = pageResult;
      this.caseInsensitive = caseInsensitive;
   }

   public PageResult<Entity> handle(ResultSet rs) throws SQLException {
      return (PageResult)HandleHelper.handleRs(rs, this.pageResult, this.caseInsensitive);
   }
}
