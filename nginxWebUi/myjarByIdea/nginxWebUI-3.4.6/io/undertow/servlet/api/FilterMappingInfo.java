package io.undertow.servlet.api;

import javax.servlet.DispatcherType;

public class FilterMappingInfo {
   private final String filterName;
   private final MappingType mappingType;
   private final String mapping;
   private final DispatcherType dispatcher;

   public FilterMappingInfo(String filterName, MappingType mappingType, String mapping, DispatcherType dispatcher) {
      this.filterName = filterName;
      this.mappingType = mappingType;
      this.mapping = mapping;
      this.dispatcher = dispatcher;
   }

   public MappingType getMappingType() {
      return this.mappingType;
   }

   public String getMapping() {
      return this.mapping;
   }

   public DispatcherType getDispatcher() {
      return this.dispatcher;
   }

   public String getFilterName() {
      return this.filterName;
   }

   public static enum MappingType {
      URL,
      SERVLET;
   }
}
