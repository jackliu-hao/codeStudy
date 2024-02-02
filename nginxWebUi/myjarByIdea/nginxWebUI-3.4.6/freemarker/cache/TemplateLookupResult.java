package freemarker.cache;

import freemarker.template.utility.NullArgumentException;

public abstract class TemplateLookupResult {
   static TemplateLookupResult createNegativeResult() {
      return TemplateLookupResult.NegativeTemplateLookupResult.INSTANCE;
   }

   static TemplateLookupResult from(String templateSourceName, Object templateSource) {
      return (TemplateLookupResult)(templateSource != null ? new PositiveTemplateLookupResult(templateSourceName, templateSource) : createNegativeResult());
   }

   private TemplateLookupResult() {
   }

   public abstract String getTemplateSourceName();

   public abstract boolean isPositive();

   abstract Object getTemplateSource();

   // $FF: synthetic method
   TemplateLookupResult(Object x0) {
      this();
   }

   private static final class NegativeTemplateLookupResult extends TemplateLookupResult {
      private static final NegativeTemplateLookupResult INSTANCE = new NegativeTemplateLookupResult();

      private NegativeTemplateLookupResult() {
         super(null);
      }

      public String getTemplateSourceName() {
         return null;
      }

      Object getTemplateSource() {
         return null;
      }

      public boolean isPositive() {
         return false;
      }
   }

   private static final class PositiveTemplateLookupResult extends TemplateLookupResult {
      private final String templateSourceName;
      private final Object templateSource;

      private PositiveTemplateLookupResult(String templateSourceName, Object templateSource) {
         super(null);
         NullArgumentException.check("templateName", templateSourceName);
         NullArgumentException.check("templateSource", templateSource);
         if (templateSource instanceof TemplateLookupResult) {
            throw new IllegalArgumentException();
         } else {
            this.templateSourceName = templateSourceName;
            this.templateSource = templateSource;
         }
      }

      public String getTemplateSourceName() {
         return this.templateSourceName;
      }

      Object getTemplateSource() {
         return this.templateSource;
      }

      public boolean isPositive() {
         return true;
      }

      // $FF: synthetic method
      PositiveTemplateLookupResult(String x0, Object x1, Object x2) {
         this(x0, x1);
      }
   }
}
