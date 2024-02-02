package com.mysql.cj.conf;

public class EnumProperty<T extends Enum<T>> extends AbstractRuntimeProperty<T> {
   private static final long serialVersionUID = -60853080911910124L;

   protected EnumProperty(PropertyDefinition<T> propertyDefinition) {
      super(propertyDefinition);
   }
}
