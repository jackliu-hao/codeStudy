package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class BeanHandler<E> implements RsHandler<E> {
   private static final long serialVersionUID = -5491214744966544475L;
   private final Class<E> elementBeanType;

   public static <E> BeanHandler<E> create(Class<E> beanType) {
      return new BeanHandler(beanType);
   }

   public BeanHandler(Class<E> beanType) {
      this.elementBeanType = beanType;
   }

   public E handle(ResultSet rs) throws SQLException {
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount();
      return rs.next() ? HandleHelper.handleRow(columnCount, meta, rs, this.elementBeanType) : null;
   }
}
