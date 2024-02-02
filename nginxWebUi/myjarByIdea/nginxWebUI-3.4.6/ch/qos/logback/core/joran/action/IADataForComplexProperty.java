package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.AggregationType;

public class IADataForComplexProperty {
   final PropertySetter parentBean;
   final AggregationType aggregationType;
   final String complexPropertyName;
   private Object nestedComplexProperty;
   boolean inError;

   public IADataForComplexProperty(PropertySetter parentBean, AggregationType aggregationType, String complexPropertyName) {
      this.parentBean = parentBean;
      this.aggregationType = aggregationType;
      this.complexPropertyName = complexPropertyName;
   }

   public AggregationType getAggregationType() {
      return this.aggregationType;
   }

   public Object getNestedComplexProperty() {
      return this.nestedComplexProperty;
   }

   public String getComplexPropertyName() {
      return this.complexPropertyName;
   }

   public void setNestedComplexProperty(Object nestedComplexProperty) {
      this.nestedComplexProperty = nestedComplexProperty;
   }
}
