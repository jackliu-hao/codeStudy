package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class TypeRegistry {
   private static final Logger logger = Logger.getLogger(TypeRegistry.class.getName());
   private final Map<String, Descriptors.Descriptor> types;

   public static TypeRegistry getEmptyTypeRegistry() {
      return TypeRegistry.EmptyTypeRegistryHolder.EMPTY;
   }

   public static Builder newBuilder() {
      return new Builder();
   }

   public Descriptors.Descriptor find(String name) {
      return (Descriptors.Descriptor)this.types.get(name);
   }

   public final Descriptors.Descriptor getDescriptorForTypeUrl(String typeUrl) throws InvalidProtocolBufferException {
      return this.find(getTypeName(typeUrl));
   }

   TypeRegistry(Map<String, Descriptors.Descriptor> types) {
      this.types = types;
   }

   private static String getTypeName(String typeUrl) throws InvalidProtocolBufferException {
      String[] parts = typeUrl.split("/");
      if (parts.length == 1) {
         throw new InvalidProtocolBufferException("Invalid type url found: " + typeUrl);
      } else {
         return parts[parts.length - 1];
      }
   }

   public static final class Builder {
      private final Set<String> files;
      private Map<String, Descriptors.Descriptor> types;

      private Builder() {
         this.files = new HashSet();
         this.types = new HashMap();
      }

      public Builder add(Descriptors.Descriptor messageType) {
         if (this.types == null) {
            throw new IllegalStateException("A TypeRegistry.Builder can only be used once.");
         } else {
            this.addFile(messageType.getFile());
            return this;
         }
      }

      public Builder add(Iterable<Descriptors.Descriptor> messageTypes) {
         if (this.types == null) {
            throw new IllegalStateException("A TypeRegistry.Builder can only be used once.");
         } else {
            Iterator var2 = messageTypes.iterator();

            while(var2.hasNext()) {
               Descriptors.Descriptor type = (Descriptors.Descriptor)var2.next();
               this.addFile(type.getFile());
            }

            return this;
         }
      }

      public TypeRegistry build() {
         TypeRegistry result = new TypeRegistry(this.types);
         this.types = null;
         return result;
      }

      private void addFile(Descriptors.FileDescriptor file) {
         if (this.files.add(file.getFullName())) {
            Iterator var2 = file.getDependencies().iterator();

            while(var2.hasNext()) {
               Descriptors.FileDescriptor dependency = (Descriptors.FileDescriptor)var2.next();
               this.addFile(dependency);
            }

            var2 = file.getMessageTypes().iterator();

            while(var2.hasNext()) {
               Descriptors.Descriptor message = (Descriptors.Descriptor)var2.next();
               this.addMessage(message);
            }

         }
      }

      private void addMessage(Descriptors.Descriptor message) {
         Iterator var2 = message.getNestedTypes().iterator();

         while(var2.hasNext()) {
            Descriptors.Descriptor nestedType = (Descriptors.Descriptor)var2.next();
            this.addMessage(nestedType);
         }

         if (this.types.containsKey(message.getFullName())) {
            TypeRegistry.logger.warning("Type " + message.getFullName() + " is added multiple times.");
         } else {
            this.types.put(message.getFullName(), message);
         }
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }

   private static class EmptyTypeRegistryHolder {
      private static final TypeRegistry EMPTY = new TypeRegistry(Collections.emptyMap());
   }
}
