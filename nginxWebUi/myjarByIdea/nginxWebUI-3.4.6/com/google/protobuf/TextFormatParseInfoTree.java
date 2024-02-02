package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TextFormatParseInfoTree {
   private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField;
   Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subtreesFromField;

   private TextFormatParseInfoTree(Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField, Map<Descriptors.FieldDescriptor, List<Builder>> subtreeBuildersFromField) {
      Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locs = new HashMap();
      Iterator var4 = locationsFromField.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> kv = (Map.Entry)var4.next();
         locs.put(kv.getKey(), Collections.unmodifiableList((List)kv.getValue()));
      }

      this.locationsFromField = Collections.unmodifiableMap(locs);
      Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subs = new HashMap();
      Iterator var11 = subtreeBuildersFromField.entrySet().iterator();

      while(var11.hasNext()) {
         Map.Entry<Descriptors.FieldDescriptor, List<Builder>> kv = (Map.Entry)var11.next();
         List<TextFormatParseInfoTree> submessagesOfField = new ArrayList();
         Iterator var8 = ((List)kv.getValue()).iterator();

         while(var8.hasNext()) {
            Builder subBuilder = (Builder)var8.next();
            submessagesOfField.add(subBuilder.build());
         }

         subs.put(kv.getKey(), Collections.unmodifiableList(submessagesOfField));
      }

      this.subtreesFromField = Collections.unmodifiableMap(subs);
   }

   public List<TextFormatParseLocation> getLocations(Descriptors.FieldDescriptor fieldDescriptor) {
      List<TextFormatParseLocation> result = (List)this.locationsFromField.get(fieldDescriptor);
      return result == null ? Collections.emptyList() : result;
   }

   public TextFormatParseLocation getLocation(Descriptors.FieldDescriptor fieldDescriptor, int index) {
      return (TextFormatParseLocation)getFromList(this.getLocations(fieldDescriptor), index, fieldDescriptor);
   }

   public List<TextFormatParseInfoTree> getNestedTrees(Descriptors.FieldDescriptor fieldDescriptor) {
      List<TextFormatParseInfoTree> result = (List)this.subtreesFromField.get(fieldDescriptor);
      return result == null ? Collections.emptyList() : result;
   }

   public TextFormatParseInfoTree getNestedTree(Descriptors.FieldDescriptor fieldDescriptor, int index) {
      return (TextFormatParseInfoTree)getFromList(this.getNestedTrees(fieldDescriptor), index, fieldDescriptor);
   }

   public static Builder builder() {
      return new Builder();
   }

   private static <T> T getFromList(List<T> list, int index, Descriptors.FieldDescriptor fieldDescriptor) {
      if (index < list.size() && index >= 0) {
         return list.get(index);
      } else {
         throw new IllegalArgumentException(String.format("Illegal index field: %s, index %d", fieldDescriptor == null ? "<null>" : fieldDescriptor.getName(), index));
      }
   }

   // $FF: synthetic method
   TextFormatParseInfoTree(Map x0, Map x1, Object x2) {
      this(x0, x1);
   }

   public static class Builder {
      private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField;
      private Map<Descriptors.FieldDescriptor, List<Builder>> subtreeBuildersFromField;

      private Builder() {
         this.locationsFromField = new HashMap();
         this.subtreeBuildersFromField = new HashMap();
      }

      public Builder setLocation(Descriptors.FieldDescriptor fieldDescriptor, TextFormatParseLocation location) {
         List<TextFormatParseLocation> fieldLocations = (List)this.locationsFromField.get(fieldDescriptor);
         if (fieldLocations == null) {
            fieldLocations = new ArrayList();
            this.locationsFromField.put(fieldDescriptor, fieldLocations);
         }

         ((List)fieldLocations).add(location);
         return this;
      }

      public Builder getBuilderForSubMessageField(Descriptors.FieldDescriptor fieldDescriptor) {
         List<Builder> submessageBuilders = (List)this.subtreeBuildersFromField.get(fieldDescriptor);
         if (submessageBuilders == null) {
            submessageBuilders = new ArrayList();
            this.subtreeBuildersFromField.put(fieldDescriptor, submessageBuilders);
         }

         Builder subtreeBuilder = new Builder();
         ((List)submessageBuilders).add(subtreeBuilder);
         return subtreeBuilder;
      }

      public TextFormatParseInfoTree build() {
         return new TextFormatParseInfoTree(this.locationsFromField, this.subtreeBuildersFromField);
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }
}
