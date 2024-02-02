package com.mysql.cj.jdbc.ha;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.mysql.cj.util.StringUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class StandardLoadBalanceExceptionChecker implements LoadBalanceExceptionChecker {
   private List<String> sqlStateList;
   private List<Class<?>> sqlExClassList;

   public boolean shouldExceptionTriggerFailover(Throwable ex) {
      String sqlState = ex instanceof SQLException ? ((SQLException)ex).getSQLState() : null;
      Iterator i;
      if (sqlState != null) {
         if (sqlState.startsWith("08")) {
            return true;
         }

         if (this.sqlStateList != null) {
            i = this.sqlStateList.iterator();

            while(i.hasNext()) {
               if (sqlState.startsWith(((String)i.next()).toString())) {
                  return true;
               }
            }
         }
      }

      if (!(ex instanceof CommunicationsException) && !(ex instanceof CJCommunicationsException)) {
         if (this.sqlExClassList != null) {
            i = this.sqlExClassList.iterator();

            while(i.hasNext()) {
               if (((Class)i.next()).isInstance(ex)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void destroy() {
   }

   public void init(Properties props) {
      this.configureSQLStateList(props.getProperty(PropertyKey.loadBalanceSQLStateFailover.getKeyName(), (String)null));
      this.configureSQLExceptionSubclassList(props.getProperty(PropertyKey.loadBalanceSQLExceptionSubclassFailover.getKeyName(), (String)null));
   }

   private void configureSQLStateList(String sqlStates) {
      if (sqlStates != null && !"".equals(sqlStates)) {
         List<String> states = StringUtils.split(sqlStates, ",", true);
         List<String> newStates = new ArrayList();
         Iterator var4 = states.iterator();

         while(var4.hasNext()) {
            String state = (String)var4.next();
            if (state.length() > 0) {
               newStates.add(state);
            }
         }

         if (newStates.size() > 0) {
            this.sqlStateList = newStates;
         }

      }
   }

   private void configureSQLExceptionSubclassList(String sqlExClasses) {
      if (sqlExClasses != null && !"".equals(sqlExClasses)) {
         List<String> classes = StringUtils.split(sqlExClasses, ",", true);
         List<Class<?>> newClasses = new ArrayList();
         Iterator var4 = classes.iterator();

         while(var4.hasNext()) {
            String exClass = (String)var4.next();

            try {
               Class<?> c = Class.forName(exClass);
               newClasses.add(c);
            } catch (Exception var7) {
            }
         }

         if (newClasses.size() > 0) {
            this.sqlExClassList = newClasses;
         }

      }
   }
}
