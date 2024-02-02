package freemarker.cache;

import java.io.IOException;

public abstract class TemplateLookupStrategy {
   public static final TemplateLookupStrategy DEFAULT_2_3_0 = new Default020300();

   public abstract TemplateLookupResult lookup(TemplateLookupContext var1) throws IOException;

   private static class Default020300 extends TemplateLookupStrategy {
      private Default020300() {
      }

      public TemplateLookupResult lookup(TemplateLookupContext ctx) throws IOException {
         return ctx.lookupWithLocalizedThenAcquisitionStrategy(ctx.getTemplateName(), ctx.getTemplateLocale());
      }

      public String toString() {
         return "TemplateLookupStrategy.DEFAULT_2_3_0";
      }

      // $FF: synthetic method
      Default020300(Object x0) {
         this();
      }
   }
}
