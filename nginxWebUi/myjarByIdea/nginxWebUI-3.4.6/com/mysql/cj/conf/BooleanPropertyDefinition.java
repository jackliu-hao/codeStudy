package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.util.StringUtils;
import java.util.Arrays;

public class BooleanPropertyDefinition extends AbstractPropertyDefinition<Boolean> {
   private static final long serialVersionUID = -7288366734350231540L;

   public BooleanPropertyDefinition(PropertyKey key, Boolean defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
      super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
   }

   public String[] getAllowableValues() {
      return getBooleanAllowableValues();
   }

   public Boolean parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
      return booleanFrom(this.getName(), value, exceptionInterceptor);
   }

   public RuntimeProperty<Boolean> createRuntimeProperty() {
      return new BooleanProperty(this);
   }

   public static Boolean booleanFrom(String name, String value, ExceptionInterceptor exceptionInterceptor) {
      try {
         return BooleanPropertyDefinition.AllowableValues.valueOf(value.toUpperCase()).asBoolean();
      } catch (Exception var4) {
         throw ExceptionFactory.createException((String)Messages.getString("PropertyDefinition.1", new Object[]{name, StringUtils.stringArrayToString(getBooleanAllowableValues(), "'", "', '", "' or '", "'"), value}), (Throwable)var4, (ExceptionInterceptor)exceptionInterceptor);
      }
   }

   public static String[] getBooleanAllowableValues() {
      return (String[])Arrays.stream(BooleanPropertyDefinition.AllowableValues.values()).map(Enum::toString).toArray((x$0) -> {
         return new String[x$0];
      });
   }

   public static enum AllowableValues {
      TRUE(true),
      FALSE(false),
      YES(true),
      NO(false);

      private boolean asBoolean;

      private AllowableValues(boolean booleanValue) {
         this.asBoolean = booleanValue;
      }

      public boolean asBoolean() {
         return this.asBoolean;
      }
   }
}
