package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.net.URL;

public class ResourceExistsPropertyDefiner extends PropertyDefinerBase {
   String resourceStr;

   public String getResource() {
      return this.resourceStr;
   }

   public void setResource(String resource) {
      this.resourceStr = resource;
   }

   public String getPropertyValue() {
      if (OptionHelper.isEmpty(this.resourceStr)) {
         this.addError("The \"resource\" property must be set.");
         return null;
      } else {
         URL resourceURL = Loader.getResourceBySelfClassLoader(this.resourceStr);
         return booleanAsStr(resourceURL != null);
      }
   }
}
