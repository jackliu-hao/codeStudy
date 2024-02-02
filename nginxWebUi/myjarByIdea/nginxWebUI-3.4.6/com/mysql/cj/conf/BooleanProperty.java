package com.mysql.cj.conf;

public class BooleanProperty extends AbstractRuntimeProperty<Boolean> {
   private static final long serialVersionUID = 1102859411443650569L;

   protected BooleanProperty(PropertyDefinition<Boolean> propertyDefinition) {
      super(propertyDefinition);
   }
}
