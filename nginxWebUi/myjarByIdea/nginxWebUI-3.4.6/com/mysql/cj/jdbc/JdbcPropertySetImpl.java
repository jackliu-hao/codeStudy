package com.mysql.cj.jdbc;

import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.PropertyDefinition;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.util.StringUtils;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcPropertySetImpl extends DefaultPropertySet implements JdbcPropertySet {
   private static final long serialVersionUID = -8223499903182568260L;

   public void postInitialization() {
      if ((Integer)this.getIntegerProperty(PropertyKey.maxRows).getValue() == 0) {
         super.getProperty(PropertyKey.maxRows).setValue(-1, (ExceptionInterceptor)null);
      }

      String testEncoding = (String)this.getStringProperty(PropertyKey.characterEncoding).getValue();
      if (testEncoding != null) {
         String testString = "abc";
         StringUtils.getBytes(testString, testEncoding);
      }

      if ((Boolean)this.getBooleanProperty(PropertyKey.useCursorFetch).getValue()) {
         super.getProperty(PropertyKey.useServerPrepStmts).setValue(true);
      }

   }

   public List<DriverPropertyInfo> exposeAsDriverPropertyInfo() throws SQLException {
      return (List)PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.entrySet().stream().filter((e) -> {
         return !((PropertyDefinition)e.getValue()).getCategory().equals(PropertyDefinitions.CATEGORY_XDEVAPI);
      }).map(Map.Entry::getKey).map(this::getProperty).map(this::getAsDriverPropertyInfo).collect(Collectors.toList());
   }

   private DriverPropertyInfo getAsDriverPropertyInfo(RuntimeProperty<?> pr) {
      PropertyDefinition<?> pdef = pr.getPropertyDefinition();
      DriverPropertyInfo dpi = new DriverPropertyInfo(pdef.getName(), (String)null);
      dpi.choices = pdef.getAllowableValues();
      dpi.value = pr.getStringValue() != null ? pr.getStringValue() : null;
      dpi.required = false;
      dpi.description = pdef.getDescription();
      return dpi;
   }
}
