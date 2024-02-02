package cn.hutool.db.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BeanListHandler<E> implements RsHandler<List<E>> {
   private static final long serialVersionUID = 4510569754766197707L;
   private final Class<E> elementBeanType;

   public static <E> BeanListHandler<E> create(Class<E> beanType) {
      return new BeanListHandler(beanType);
   }

   public BeanListHandler(Class<E> beanType) {
      this.elementBeanType = beanType;
   }

   public List<E> handle(ResultSet rs) throws SQLException {
      return (List)HandleHelper.handleRsToBeanList(rs, new ArrayList(), this.elementBeanType);
   }
}
