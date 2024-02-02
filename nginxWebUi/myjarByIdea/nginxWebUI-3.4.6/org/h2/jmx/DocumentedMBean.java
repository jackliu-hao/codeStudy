package org.h2.jmx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import org.h2.util.Utils;

public class DocumentedMBean extends StandardMBean {
   private final String interfaceName;
   private Properties resources;

   public <T> DocumentedMBean(T var1, Class<T> var2) throws NotCompliantMBeanException {
      super(var1, var2);
      this.interfaceName = var1.getClass().getName() + "MBean";
   }

   private Properties getResources() {
      if (this.resources == null) {
         this.resources = new Properties();
         String var1 = "/org/h2/res/javadoc.properties";

         try {
            byte[] var2 = Utils.getResource(var1);
            if (var2 != null) {
               this.resources.load(new ByteArrayInputStream(var2));
            }
         } catch (IOException var3) {
         }
      }

      return this.resources;
   }

   protected String getDescription(MBeanInfo var1) {
      String var2 = this.getResources().getProperty(this.interfaceName);
      return var2 == null ? super.getDescription(var1) : var2;
   }

   protected String getDescription(MBeanOperationInfo var1) {
      String var2 = this.getResources().getProperty(this.interfaceName + "." + var1.getName());
      return var2 == null ? super.getDescription(var1) : var2;
   }

   protected String getDescription(MBeanAttributeInfo var1) {
      String var2 = var1.isIs() ? "is" : "get";
      String var3 = this.getResources().getProperty(this.interfaceName + "." + var2 + var1.getName());
      return var3 == null ? super.getDescription(var1) : var3;
   }

   protected int getImpact(MBeanOperationInfo var1) {
      return var1.getName().startsWith("list") ? 0 : 1;
   }
}
