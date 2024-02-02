package freemarker.template;

import java.io.Serializable;

public final class SimpleScalar implements TemplateScalarModel, Serializable {
   private final String value;

   public SimpleScalar(String value) {
      this.value = value;
   }

   public String getAsString() {
      return this.value == null ? "" : this.value;
   }

   public String toString() {
      return this.value;
   }

   public static SimpleScalar newInstanceOrNull(String s) {
      return s != null ? new SimpleScalar(s) : null;
   }
}
