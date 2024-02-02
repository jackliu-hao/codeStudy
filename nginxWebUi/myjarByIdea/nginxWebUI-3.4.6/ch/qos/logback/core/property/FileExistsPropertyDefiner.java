package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;

public class FileExistsPropertyDefiner extends PropertyDefinerBase {
   String path;

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String getPropertyValue() {
      if (OptionHelper.isEmpty(this.path)) {
         this.addError("The \"path\" property must be set.");
         return null;
      } else {
         File file = new File(this.path);
         return booleanAsStr(file.exists());
      }
   }
}
