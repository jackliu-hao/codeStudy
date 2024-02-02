package com.google.protobuf;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;

public final class Descriptors {
   private static final Logger logger = Logger.getLogger(Descriptors.class.getName());

   private static String computeFullName(FileDescriptor file, Descriptor parent, String name) {
      if (parent != null) {
         return parent.getFullName() + '.' + name;
      } else {
         String packageName = file.getPackage();
         return !packageName.isEmpty() ? packageName + '.' + name : name;
      }
   }

   public static final class OneofDescriptor extends GenericDescriptor {
      private final int index;
      private DescriptorProtos.OneofDescriptorProto proto;
      private final String fullName;
      private final FileDescriptor file;
      private Descriptor containingType;
      private int fieldCount;
      private FieldDescriptor[] fields;

      public int getIndex() {
         return this.index;
      }

      public String getName() {
         return this.proto.getName();
      }

      public FileDescriptor getFile() {
         return this.file;
      }

      public String getFullName() {
         return this.fullName;
      }

      public Descriptor getContainingType() {
         return this.containingType;
      }

      public int getFieldCount() {
         return this.fieldCount;
      }

      public DescriptorProtos.OneofOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<FieldDescriptor> getFields() {
         return Collections.unmodifiableList(Arrays.asList(this.fields));
      }

      public FieldDescriptor getField(int index) {
         return this.fields[index];
      }

      public DescriptorProtos.OneofDescriptorProto toProto() {
         return this.proto;
      }

      private void setProto(DescriptorProtos.OneofDescriptorProto proto) {
         this.proto = proto;
      }

      private OneofDescriptor(DescriptorProtos.OneofDescriptorProto proto, FileDescriptor file, Descriptor parent, int index) throws DescriptorValidationException {
         super(null);
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
         this.file = file;
         this.index = index;
         this.containingType = parent;
         this.fieldCount = 0;
      }

      // $FF: synthetic method
      OneofDescriptor(DescriptorProtos.OneofDescriptorProto x0, FileDescriptor x1, Descriptor x2, int x3, Object x4) throws DescriptorValidationException {
         this(x0, x1, x2, x3);
      }
   }

   private static final class DescriptorPool {
      private final Set<FileDescriptor> dependencies = new HashSet();
      private boolean allowUnknownDependencies;
      private final Map<String, GenericDescriptor> descriptorsByName = new HashMap();
      private final Map<DescriptorIntPair, FieldDescriptor> fieldsByNumber = new HashMap();
      private final Map<DescriptorIntPair, EnumValueDescriptor> enumValuesByNumber = new HashMap();

      DescriptorPool(FileDescriptor[] dependencies, boolean allowUnknownDependencies) {
         this.allowUnknownDependencies = allowUnknownDependencies;

         for(int i = 0; i < dependencies.length; ++i) {
            this.dependencies.add(dependencies[i]);
            this.importPublicDependencies(dependencies[i]);
         }

         Iterator var7 = this.dependencies.iterator();

         while(var7.hasNext()) {
            FileDescriptor dependency = (FileDescriptor)var7.next();

            try {
               this.addPackage(dependency.getPackage(), dependency);
            } catch (DescriptorValidationException var6) {
               throw new AssertionError(var6);
            }
         }

      }

      private void importPublicDependencies(FileDescriptor file) {
         Iterator var2 = file.getPublicDependencies().iterator();

         while(var2.hasNext()) {
            FileDescriptor dependency = (FileDescriptor)var2.next();
            if (this.dependencies.add(dependency)) {
               this.importPublicDependencies(dependency);
            }
         }

      }

      GenericDescriptor findSymbol(String fullName) {
         return this.findSymbol(fullName, Descriptors.DescriptorPool.SearchFilter.ALL_SYMBOLS);
      }

      GenericDescriptor findSymbol(String fullName, SearchFilter filter) {
         GenericDescriptor result = (GenericDescriptor)this.descriptorsByName.get(fullName);
         if (result == null || filter != Descriptors.DescriptorPool.SearchFilter.ALL_SYMBOLS && (filter != Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY || !this.isType(result)) && (filter != Descriptors.DescriptorPool.SearchFilter.AGGREGATES_ONLY || !this.isAggregate(result))) {
            Iterator var4 = this.dependencies.iterator();

            do {
               do {
                  if (!var4.hasNext()) {
                     return null;
                  }

                  FileDescriptor dependency = (FileDescriptor)var4.next();
                  result = (GenericDescriptor)dependency.pool.descriptorsByName.get(fullName);
               } while(result == null);
            } while(filter != Descriptors.DescriptorPool.SearchFilter.ALL_SYMBOLS && (filter != Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY || !this.isType(result)) && (filter != Descriptors.DescriptorPool.SearchFilter.AGGREGATES_ONLY || !this.isAggregate(result)));

            return result;
         } else {
            return result;
         }
      }

      boolean isType(GenericDescriptor descriptor) {
         return descriptor instanceof Descriptor || descriptor instanceof EnumDescriptor;
      }

      boolean isAggregate(GenericDescriptor descriptor) {
         return descriptor instanceof Descriptor || descriptor instanceof EnumDescriptor || descriptor instanceof PackageDescriptor || descriptor instanceof ServiceDescriptor;
      }

      GenericDescriptor lookupSymbol(String name, GenericDescriptor relativeTo, SearchFilter filter) throws DescriptorValidationException {
         GenericDescriptor result;
         String fullname;
         if (name.startsWith(".")) {
            fullname = name.substring(1);
            result = this.findSymbol(fullname, filter);
         } else {
            int firstPartLength = name.indexOf(46);
            String firstPart;
            if (firstPartLength == -1) {
               firstPart = name;
            } else {
               firstPart = name.substring(0, firstPartLength);
            }

            StringBuilder scopeToTry = new StringBuilder(relativeTo.getFullName());

            while(true) {
               int dotpos = scopeToTry.lastIndexOf(".");
               if (dotpos == -1) {
                  fullname = name;
                  result = this.findSymbol(name, filter);
                  break;
               }

               scopeToTry.setLength(dotpos + 1);
               scopeToTry.append(firstPart);
               result = this.findSymbol(scopeToTry.toString(), Descriptors.DescriptorPool.SearchFilter.AGGREGATES_ONLY);
               if (result != null) {
                  if (firstPartLength != -1) {
                     scopeToTry.setLength(dotpos + 1);
                     scopeToTry.append(name);
                     result = this.findSymbol(scopeToTry.toString(), filter);
                  }

                  fullname = scopeToTry.toString();
                  break;
               }

               scopeToTry.setLength(dotpos);
            }
         }

         if (result == null) {
            if (this.allowUnknownDependencies && filter == Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY) {
               Descriptors.logger.warning("The descriptor for message type \"" + name + "\" can not be found and a placeholder is created for it");
               GenericDescriptor result = new Descriptor(fullname);
               this.dependencies.add(result.getFile());
               return result;
            } else {
               throw new DescriptorValidationException(relativeTo, '"' + name + "\" is not defined.");
            }
         } else {
            return result;
         }
      }

      void addSymbol(GenericDescriptor descriptor) throws DescriptorValidationException {
         validateSymbolName(descriptor);
         String fullName = descriptor.getFullName();
         GenericDescriptor old = (GenericDescriptor)this.descriptorsByName.put(fullName, descriptor);
         if (old != null) {
            this.descriptorsByName.put(fullName, old);
            if (descriptor.getFile() == old.getFile()) {
               int dotpos = fullName.lastIndexOf(46);
               if (dotpos == -1) {
                  throw new DescriptorValidationException(descriptor, '"' + fullName + "\" is already defined.");
               } else {
                  throw new DescriptorValidationException(descriptor, '"' + fullName.substring(dotpos + 1) + "\" is already defined in \"" + fullName.substring(0, dotpos) + "\".");
               }
            } else {
               throw new DescriptorValidationException(descriptor, '"' + fullName + "\" is already defined in file \"" + old.getFile().getName() + "\".");
            }
         }
      }

      void addPackage(String fullName, FileDescriptor file) throws DescriptorValidationException {
         int dotpos = fullName.lastIndexOf(46);
         String name;
         if (dotpos == -1) {
            name = fullName;
         } else {
            this.addPackage(fullName.substring(0, dotpos), file);
            name = fullName.substring(dotpos + 1);
         }

         GenericDescriptor old = (GenericDescriptor)this.descriptorsByName.put(fullName, new PackageDescriptor(name, fullName, file));
         if (old != null) {
            this.descriptorsByName.put(fullName, old);
            if (!(old instanceof PackageDescriptor)) {
               throw new DescriptorValidationException(file, '"' + name + "\" is already defined (as something other than a package) in file \"" + old.getFile().getName() + "\".");
            }
         }

      }

      void addFieldByNumber(FieldDescriptor field) throws DescriptorValidationException {
         DescriptorIntPair key = new DescriptorIntPair(field.getContainingType(), field.getNumber());
         FieldDescriptor old = (FieldDescriptor)this.fieldsByNumber.put(key, field);
         if (old != null) {
            this.fieldsByNumber.put(key, old);
            throw new DescriptorValidationException(field, "Field number " + field.getNumber() + " has already been used in \"" + field.getContainingType().getFullName() + "\" by field \"" + old.getName() + "\".");
         }
      }

      void addEnumValueByNumber(EnumValueDescriptor value) {
         DescriptorIntPair key = new DescriptorIntPair(value.getType(), value.getNumber());
         EnumValueDescriptor old = (EnumValueDescriptor)this.enumValuesByNumber.put(key, value);
         if (old != null) {
            this.enumValuesByNumber.put(key, old);
         }

      }

      static void validateSymbolName(GenericDescriptor descriptor) throws DescriptorValidationException {
         String name = descriptor.getName();
         if (name.length() == 0) {
            throw new DescriptorValidationException(descriptor, "Missing name.");
         } else {
            for(int i = 0; i < name.length(); ++i) {
               char c = name.charAt(i);
               if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && c != '_' && ('0' > c || c > '9' || i <= 0)) {
                  throw new DescriptorValidationException(descriptor, '"' + name + "\" is not a valid identifier.");
               }
            }

         }
      }

      private static final class DescriptorIntPair {
         private final GenericDescriptor descriptor;
         private final int number;

         DescriptorIntPair(GenericDescriptor descriptor, int number) {
            this.descriptor = descriptor;
            this.number = number;
         }

         public int hashCode() {
            return this.descriptor.hashCode() * '\uffff' + this.number;
         }

         public boolean equals(Object obj) {
            if (!(obj instanceof DescriptorIntPair)) {
               return false;
            } else {
               DescriptorIntPair other = (DescriptorIntPair)obj;
               return this.descriptor == other.descriptor && this.number == other.number;
            }
         }
      }

      private static final class PackageDescriptor extends GenericDescriptor {
         private final String name;
         private final String fullName;
         private final FileDescriptor file;

         public Message toProto() {
            return this.file.toProto();
         }

         public String getName() {
            return this.name;
         }

         public String getFullName() {
            return this.fullName;
         }

         public FileDescriptor getFile() {
            return this.file;
         }

         PackageDescriptor(String name, String fullName, FileDescriptor file) {
            super(null);
            this.file = file;
            this.fullName = fullName;
            this.name = name;
         }
      }

      static enum SearchFilter {
         TYPES_ONLY,
         AGGREGATES_ONLY,
         ALL_SYMBOLS;
      }
   }

   public static class DescriptorValidationException extends Exception {
      private static final long serialVersionUID = 5750205775490483148L;
      private final String name;
      private final Message proto;
      private final String description;

      public String getProblemSymbolName() {
         return this.name;
      }

      public Message getProblemProto() {
         return this.proto;
      }

      public String getDescription() {
         return this.description;
      }

      private DescriptorValidationException(GenericDescriptor problemDescriptor, String description) {
         super(problemDescriptor.getFullName() + ": " + description);
         this.name = problemDescriptor.getFullName();
         this.proto = problemDescriptor.toProto();
         this.description = description;
      }

      private DescriptorValidationException(GenericDescriptor problemDescriptor, String description, Throwable cause) {
         this(problemDescriptor, description);
         this.initCause(cause);
      }

      private DescriptorValidationException(FileDescriptor problemDescriptor, String description) {
         super(problemDescriptor.getName() + ": " + description);
         this.name = problemDescriptor.getName();
         this.proto = problemDescriptor.toProto();
         this.description = description;
      }

      // $FF: synthetic method
      DescriptorValidationException(FileDescriptor x0, String x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      DescriptorValidationException(GenericDescriptor x0, String x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      DescriptorValidationException(GenericDescriptor x0, String x1, Throwable x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   public abstract static class GenericDescriptor {
      private GenericDescriptor() {
      }

      public abstract Message toProto();

      public abstract String getName();

      public abstract String getFullName();

      public abstract FileDescriptor getFile();

      // $FF: synthetic method
      GenericDescriptor(Object x0) {
         this();
      }
   }

   public static final class MethodDescriptor extends GenericDescriptor {
      private final int index;
      private DescriptorProtos.MethodDescriptorProto proto;
      private final String fullName;
      private final FileDescriptor file;
      private final ServiceDescriptor service;
      private Descriptor inputType;
      private Descriptor outputType;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.MethodDescriptorProto toProto() {
         return this.proto;
      }

      public String getName() {
         return this.proto.getName();
      }

      public String getFullName() {
         return this.fullName;
      }

      public FileDescriptor getFile() {
         return this.file;
      }

      public ServiceDescriptor getService() {
         return this.service;
      }

      public Descriptor getInputType() {
         return this.inputType;
      }

      public Descriptor getOutputType() {
         return this.outputType;
      }

      public boolean isClientStreaming() {
         return this.proto.getClientStreaming();
      }

      public boolean isServerStreaming() {
         return this.proto.getServerStreaming();
      }

      public DescriptorProtos.MethodOptions getOptions() {
         return this.proto.getOptions();
      }

      private MethodDescriptor(DescriptorProtos.MethodDescriptorProto proto, FileDescriptor file, ServiceDescriptor parent, int index) throws DescriptorValidationException {
         super(null);
         this.index = index;
         this.proto = proto;
         this.file = file;
         this.service = parent;
         this.fullName = parent.getFullName() + '.' + proto.getName();
         file.pool.addSymbol(this);
      }

      private void crossLink() throws DescriptorValidationException {
         GenericDescriptor input = this.file.pool.lookupSymbol(this.proto.getInputType(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
         if (!(input instanceof Descriptor)) {
            throw new DescriptorValidationException(this, '"' + this.proto.getInputType() + "\" is not a message type.");
         } else {
            this.inputType = (Descriptor)input;
            GenericDescriptor output = this.file.pool.lookupSymbol(this.proto.getOutputType(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!(output instanceof Descriptor)) {
               throw new DescriptorValidationException(this, '"' + this.proto.getOutputType() + "\" is not a message type.");
            } else {
               this.outputType = (Descriptor)output;
            }
         }
      }

      private void setProto(DescriptorProtos.MethodDescriptorProto proto) {
         this.proto = proto;
      }

      // $FF: synthetic method
      MethodDescriptor(DescriptorProtos.MethodDescriptorProto x0, FileDescriptor x1, ServiceDescriptor x2, int x3, Object x4) throws DescriptorValidationException {
         this(x0, x1, x2, x3);
      }
   }

   public static final class ServiceDescriptor extends GenericDescriptor {
      private final int index;
      private DescriptorProtos.ServiceDescriptorProto proto;
      private final String fullName;
      private final FileDescriptor file;
      private MethodDescriptor[] methods;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.ServiceDescriptorProto toProto() {
         return this.proto;
      }

      public String getName() {
         return this.proto.getName();
      }

      public String getFullName() {
         return this.fullName;
      }

      public FileDescriptor getFile() {
         return this.file;
      }

      public DescriptorProtos.ServiceOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<MethodDescriptor> getMethods() {
         return Collections.unmodifiableList(Arrays.asList(this.methods));
      }

      public MethodDescriptor findMethodByName(String name) {
         GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
         return result != null && result instanceof MethodDescriptor ? (MethodDescriptor)result : null;
      }

      private ServiceDescriptor(DescriptorProtos.ServiceDescriptorProto proto, FileDescriptor file, int index) throws DescriptorValidationException {
         super(null);
         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, (Descriptor)null, proto.getName());
         this.file = file;
         this.methods = new MethodDescriptor[proto.getMethodCount()];

         for(int i = 0; i < proto.getMethodCount(); ++i) {
            this.methods[i] = new MethodDescriptor(proto.getMethod(i), file, this, i);
         }

         file.pool.addSymbol(this);
      }

      private void crossLink() throws DescriptorValidationException {
         MethodDescriptor[] var1 = this.methods;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            MethodDescriptor method = var1[var3];
            method.crossLink();
         }

      }

      private void setProto(DescriptorProtos.ServiceDescriptorProto proto) {
         this.proto = proto;

         for(int i = 0; i < this.methods.length; ++i) {
            this.methods[i].setProto(proto.getMethod(i));
         }

      }

      // $FF: synthetic method
      ServiceDescriptor(DescriptorProtos.ServiceDescriptorProto x0, FileDescriptor x1, int x2, Object x3) throws DescriptorValidationException {
         this(x0, x1, x2);
      }
   }

   public static final class EnumValueDescriptor extends GenericDescriptor implements Internal.EnumLite {
      private final int index;
      private DescriptorProtos.EnumValueDescriptorProto proto;
      private final String fullName;
      private final FileDescriptor file;
      private final EnumDescriptor type;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.EnumValueDescriptorProto toProto() {
         return this.proto;
      }

      public String getName() {
         return this.proto.getName();
      }

      public int getNumber() {
         return this.proto.getNumber();
      }

      public String toString() {
         return this.proto.getName();
      }

      public String getFullName() {
         return this.fullName;
      }

      public FileDescriptor getFile() {
         return this.file;
      }

      public EnumDescriptor getType() {
         return this.type;
      }

      public DescriptorProtos.EnumValueOptions getOptions() {
         return this.proto.getOptions();
      }

      private EnumValueDescriptor(DescriptorProtos.EnumValueDescriptorProto proto, FileDescriptor file, EnumDescriptor parent, int index) throws DescriptorValidationException {
         super(null);
         this.index = index;
         this.proto = proto;
         this.file = file;
         this.type = parent;
         this.fullName = parent.getFullName() + '.' + proto.getName();
         file.pool.addSymbol(this);
         file.pool.addEnumValueByNumber(this);
      }

      private EnumValueDescriptor(FileDescriptor file, EnumDescriptor parent, Integer number) {
         super(null);
         String name = "UNKNOWN_ENUM_VALUE_" + parent.getName() + "_" + number;
         DescriptorProtos.EnumValueDescriptorProto proto = DescriptorProtos.EnumValueDescriptorProto.newBuilder().setName(name).setNumber(number).build();
         this.index = -1;
         this.proto = proto;
         this.file = file;
         this.type = parent;
         this.fullName = parent.getFullName() + '.' + proto.getName();
      }

      private void setProto(DescriptorProtos.EnumValueDescriptorProto proto) {
         this.proto = proto;
      }

      // $FF: synthetic method
      EnumValueDescriptor(FileDescriptor x0, EnumDescriptor x1, Integer x2, Object x3) {
         this(x0, x1, x2);
      }

      // $FF: synthetic method
      EnumValueDescriptor(DescriptorProtos.EnumValueDescriptorProto x0, FileDescriptor x1, EnumDescriptor x2, int x3, Object x4) throws DescriptorValidationException {
         this(x0, x1, x2, x3);
      }
   }

   public static final class EnumDescriptor extends GenericDescriptor implements Internal.EnumLiteMap<EnumValueDescriptor> {
      private final int index;
      private DescriptorProtos.EnumDescriptorProto proto;
      private final String fullName;
      private final FileDescriptor file;
      private final Descriptor containingType;
      private EnumValueDescriptor[] values;
      private final WeakHashMap<Integer, WeakReference<EnumValueDescriptor>> unknownValues;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.EnumDescriptorProto toProto() {
         return this.proto;
      }

      public String getName() {
         return this.proto.getName();
      }

      public String getFullName() {
         return this.fullName;
      }

      public FileDescriptor getFile() {
         return this.file;
      }

      public Descriptor getContainingType() {
         return this.containingType;
      }

      public DescriptorProtos.EnumOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<EnumValueDescriptor> getValues() {
         return Collections.unmodifiableList(Arrays.asList(this.values));
      }

      public EnumValueDescriptor findValueByName(String name) {
         GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
         return result != null && result instanceof EnumValueDescriptor ? (EnumValueDescriptor)result : null;
      }

      public EnumValueDescriptor findValueByNumber(int number) {
         return (EnumValueDescriptor)this.file.pool.enumValuesByNumber.get(new DescriptorPool.DescriptorIntPair(this, number));
      }

      public EnumValueDescriptor findValueByNumberCreatingIfUnknown(int number) {
         EnumValueDescriptor result = this.findValueByNumber(number);
         if (result != null) {
            return result;
         } else {
            synchronized(this) {
               Integer key = new Integer(number);
               WeakReference<EnumValueDescriptor> reference = (WeakReference)this.unknownValues.get(key);
               if (reference != null) {
                  result = (EnumValueDescriptor)reference.get();
               }

               if (result == null) {
                  result = new EnumValueDescriptor(this.file, this, key);
                  this.unknownValues.put(key, new WeakReference(result));
               }

               return result;
            }
         }
      }

      int getUnknownEnumValueDescriptorCount() {
         return this.unknownValues.size();
      }

      private EnumDescriptor(DescriptorProtos.EnumDescriptorProto proto, FileDescriptor file, Descriptor parent, int index) throws DescriptorValidationException {
         super(null);
         this.unknownValues = new WeakHashMap();
         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
         this.file = file;
         this.containingType = parent;
         if (proto.getValueCount() == 0) {
            throw new DescriptorValidationException(this, "Enums must contain at least one value.");
         } else {
            this.values = new EnumValueDescriptor[proto.getValueCount()];

            for(int i = 0; i < proto.getValueCount(); ++i) {
               this.values[i] = new EnumValueDescriptor(proto.getValue(i), file, this, i);
            }

            file.pool.addSymbol(this);
         }
      }

      private void setProto(DescriptorProtos.EnumDescriptorProto proto) {
         this.proto = proto;

         for(int i = 0; i < this.values.length; ++i) {
            this.values[i].setProto(proto.getValue(i));
         }

      }

      // $FF: synthetic method
      EnumDescriptor(DescriptorProtos.EnumDescriptorProto x0, FileDescriptor x1, Descriptor x2, int x3, Object x4) throws DescriptorValidationException {
         this(x0, x1, x2, x3);
      }
   }

   public static final class FieldDescriptor extends GenericDescriptor implements Comparable<FieldDescriptor>, FieldSet.FieldDescriptorLite<FieldDescriptor> {
      private static final WireFormat.FieldType[] table = WireFormat.FieldType.values();
      private final int index;
      private DescriptorProtos.FieldDescriptorProto proto;
      private final String fullName;
      private final String jsonName;
      private final FileDescriptor file;
      private final Descriptor extensionScope;
      private Type type;
      private Descriptor containingType;
      private Descriptor messageType;
      private OneofDescriptor containingOneof;
      private EnumDescriptor enumType;
      private Object defaultValue;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.FieldDescriptorProto toProto() {
         return this.proto;
      }

      public String getName() {
         return this.proto.getName();
      }

      public int getNumber() {
         return this.proto.getNumber();
      }

      public String getFullName() {
         return this.fullName;
      }

      public String getJsonName() {
         return this.jsonName;
      }

      public JavaType getJavaType() {
         return this.type.getJavaType();
      }

      public WireFormat.JavaType getLiteJavaType() {
         return this.getLiteType().getJavaType();
      }

      public FileDescriptor getFile() {
         return this.file;
      }

      public Type getType() {
         return this.type;
      }

      public WireFormat.FieldType getLiteType() {
         return table[this.type.ordinal()];
      }

      public boolean needsUtf8Check() {
         if (this.type != Descriptors.FieldDescriptor.Type.STRING) {
            return false;
         } else if (this.getContainingType().getOptions().getMapEntry()) {
            return true;
         } else {
            return this.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3 ? true : this.getFile().getOptions().getJavaStringCheckUtf8();
         }
      }

      public boolean isMapField() {
         return this.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && this.isRepeated() && this.getMessageType().getOptions().getMapEntry();
      }

      public boolean isRequired() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED;
      }

      public boolean isOptional() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
      }

      public boolean isRepeated() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
      }

      public boolean isPacked() {
         if (!this.isPackable()) {
            return false;
         } else if (this.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2) {
            return this.getOptions().getPacked();
         } else {
            return !this.getOptions().hasPacked() || this.getOptions().getPacked();
         }
      }

      public boolean isPackable() {
         return this.isRepeated() && this.getLiteType().isPackable();
      }

      public boolean hasDefaultValue() {
         return this.proto.hasDefaultValue();
      }

      public Object getDefaultValue() {
         if (this.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException("FieldDescriptor.getDefaultValue() called on an embedded message field.");
         } else {
            return this.defaultValue;
         }
      }

      public DescriptorProtos.FieldOptions getOptions() {
         return this.proto.getOptions();
      }

      public boolean isExtension() {
         return this.proto.hasExtendee();
      }

      public Descriptor getContainingType() {
         return this.containingType;
      }

      public OneofDescriptor getContainingOneof() {
         return this.containingOneof;
      }

      public Descriptor getExtensionScope() {
         if (!this.isExtension()) {
            throw new UnsupportedOperationException(String.format("This field is not an extension. (%s)", this.fullName));
         } else {
            return this.extensionScope;
         }
      }

      public Descriptor getMessageType() {
         if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException(String.format("This field is not of message type. (%s)", this.fullName));
         } else {
            return this.messageType;
         }
      }

      public EnumDescriptor getEnumType() {
         if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
            throw new UnsupportedOperationException(String.format("This field is not of enum type. (%s)", this.fullName));
         } else {
            return this.enumType;
         }
      }

      public int compareTo(FieldDescriptor other) {
         if (other.containingType != this.containingType) {
            throw new IllegalArgumentException("FieldDescriptors can only be compared to other FieldDescriptors for fields of the same message type.");
         } else {
            return this.getNumber() - other.getNumber();
         }
      }

      public String toString() {
         return this.getFullName();
      }

      private static String fieldNameToJsonName(String name) {
         int length = name.length();
         StringBuilder result = new StringBuilder(length);
         boolean isNextUpperCase = false;

         for(int i = 0; i < length; ++i) {
            char ch = name.charAt(i);
            if (ch == '_') {
               isNextUpperCase = true;
            } else if (isNextUpperCase) {
               if ('a' <= ch && ch <= 'z') {
                  ch = (char)(ch - 97 + 65);
               }

               result.append(ch);
               isNextUpperCase = false;
            } else {
               result.append(ch);
            }
         }

         return result.toString();
      }

      private FieldDescriptor(DescriptorProtos.FieldDescriptorProto proto, FileDescriptor file, Descriptor parent, int index, boolean isExtension) throws DescriptorValidationException {
         super(null);
         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
         this.file = file;
         if (proto.hasJsonName()) {
            this.jsonName = proto.getJsonName();
         } else {
            this.jsonName = fieldNameToJsonName(proto.getName());
         }

         if (proto.hasType()) {
            this.type = Descriptors.FieldDescriptor.Type.valueOf(proto.getType());
         }

         if (this.getNumber() <= 0) {
            throw new DescriptorValidationException(this, "Field numbers must be positive integers.");
         } else {
            if (isExtension) {
               if (!proto.hasExtendee()) {
                  throw new DescriptorValidationException(this, "FieldDescriptorProto.extendee not set for extension field.");
               }

               this.containingType = null;
               if (parent != null) {
                  this.extensionScope = parent;
               } else {
                  this.extensionScope = null;
               }

               if (proto.hasOneofIndex()) {
                  throw new DescriptorValidationException(this, "FieldDescriptorProto.oneof_index set for extension field.");
               }

               this.containingOneof = null;
            } else {
               if (proto.hasExtendee()) {
                  throw new DescriptorValidationException(this, "FieldDescriptorProto.extendee set for non-extension field.");
               }

               this.containingType = parent;
               if (proto.hasOneofIndex()) {
                  if (proto.getOneofIndex() < 0 || proto.getOneofIndex() >= parent.toProto().getOneofDeclCount()) {
                     throw new DescriptorValidationException(this, "FieldDescriptorProto.oneof_index is out of range for type " + parent.getName());
                  }

                  this.containingOneof = (OneofDescriptor)parent.getOneofs().get(proto.getOneofIndex());
                  this.containingOneof.fieldCount++;
               } else {
                  this.containingOneof = null;
               }

               this.extensionScope = null;
            }

            file.pool.addSymbol(this);
         }
      }

      private void crossLink() throws DescriptorValidationException {
         GenericDescriptor typeDescriptor;
         if (this.proto.hasExtendee()) {
            typeDescriptor = this.file.pool.lookupSymbol(this.proto.getExtendee(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!(typeDescriptor instanceof Descriptor)) {
               throw new DescriptorValidationException(this, '"' + this.proto.getExtendee() + "\" is not a message type.");
            }

            this.containingType = (Descriptor)typeDescriptor;
            if (!this.getContainingType().isExtensionNumber(this.getNumber())) {
               throw new DescriptorValidationException(this, '"' + this.getContainingType().getFullName() + "\" does not declare " + this.getNumber() + " as an extension number.");
            }
         }

         if (this.proto.hasTypeName()) {
            typeDescriptor = this.file.pool.lookupSymbol(this.proto.getTypeName(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!this.proto.hasType()) {
               if (typeDescriptor instanceof Descriptor) {
                  this.type = Descriptors.FieldDescriptor.Type.MESSAGE;
               } else {
                  if (!(typeDescriptor instanceof EnumDescriptor)) {
                     throw new DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not a type.");
                  }

                  this.type = Descriptors.FieldDescriptor.Type.ENUM;
               }
            }

            if (this.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               if (!(typeDescriptor instanceof Descriptor)) {
                  throw new DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not a message type.");
               }

               this.messageType = (Descriptor)typeDescriptor;
               if (this.proto.hasDefaultValue()) {
                  throw new DescriptorValidationException(this, "Messages can't have default values.");
               }
            } else {
               if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
                  throw new DescriptorValidationException(this, "Field with primitive type has type_name.");
               }

               if (!(typeDescriptor instanceof EnumDescriptor)) {
                  throw new DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not an enum type.");
               }

               this.enumType = (EnumDescriptor)typeDescriptor;
            }
         } else if (this.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE || this.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
            throw new DescriptorValidationException(this, "Field with message or enum type missing type_name.");
         }

         if (this.proto.getOptions().getPacked() && !this.isPackable()) {
            throw new DescriptorValidationException(this, "[packed = true] can only be specified for repeated primitive fields.");
         } else {
            if (this.proto.hasDefaultValue()) {
               if (this.isRepeated()) {
                  throw new DescriptorValidationException(this, "Repeated fields cannot have default values.");
               }

               try {
                  switch (this.getType()) {
                     case INT32:
                     case SINT32:
                     case SFIXED32:
                        this.defaultValue = TextFormat.parseInt32(this.proto.getDefaultValue());
                        break;
                     case UINT32:
                     case FIXED32:
                        this.defaultValue = TextFormat.parseUInt32(this.proto.getDefaultValue());
                        break;
                     case INT64:
                     case SINT64:
                     case SFIXED64:
                        this.defaultValue = TextFormat.parseInt64(this.proto.getDefaultValue());
                        break;
                     case UINT64:
                     case FIXED64:
                        this.defaultValue = TextFormat.parseUInt64(this.proto.getDefaultValue());
                        break;
                     case FLOAT:
                        if (this.proto.getDefaultValue().equals("inf")) {
                           this.defaultValue = Float.POSITIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("-inf")) {
                           this.defaultValue = Float.NEGATIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("nan")) {
                           this.defaultValue = Float.NaN;
                        } else {
                           this.defaultValue = Float.valueOf(this.proto.getDefaultValue());
                        }
                        break;
                     case DOUBLE:
                        if (this.proto.getDefaultValue().equals("inf")) {
                           this.defaultValue = Double.POSITIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("-inf")) {
                           this.defaultValue = Double.NEGATIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("nan")) {
                           this.defaultValue = Double.NaN;
                        } else {
                           this.defaultValue = Double.valueOf(this.proto.getDefaultValue());
                        }
                        break;
                     case BOOL:
                        this.defaultValue = Boolean.valueOf(this.proto.getDefaultValue());
                        break;
                     case STRING:
                        this.defaultValue = this.proto.getDefaultValue();
                        break;
                     case BYTES:
                        try {
                           this.defaultValue = TextFormat.unescapeBytes(this.proto.getDefaultValue());
                           break;
                        } catch (TextFormat.InvalidEscapeSequenceException var2) {
                           throw new DescriptorValidationException(this, "Couldn't parse default value: " + var2.getMessage(), var2);
                        }
                     case ENUM:
                        this.defaultValue = this.enumType.findValueByName(this.proto.getDefaultValue());
                        if (this.defaultValue == null) {
                           throw new DescriptorValidationException(this, "Unknown enum default value: \"" + this.proto.getDefaultValue() + '"');
                        }
                        break;
                     case MESSAGE:
                     case GROUP:
                        throw new DescriptorValidationException(this, "Message type had default value.");
                  }
               } catch (NumberFormatException var3) {
                  throw new DescriptorValidationException(this, "Could not parse default value: \"" + this.proto.getDefaultValue() + '"', var3);
               }
            } else if (this.isRepeated()) {
               this.defaultValue = Collections.emptyList();
            } else {
               switch (this.getJavaType()) {
                  case ENUM:
                     this.defaultValue = this.enumType.getValues().get(0);
                     break;
                  case MESSAGE:
                     this.defaultValue = null;
                     break;
                  default:
                     this.defaultValue = this.getJavaType().defaultDefault;
               }
            }

            if (!this.isExtension()) {
               this.file.pool.addFieldByNumber(this);
            }

            if (this.containingType != null && this.containingType.getOptions().getMessageSetWireFormat()) {
               if (!this.isExtension()) {
                  throw new DescriptorValidationException(this, "MessageSets cannot have fields, only extensions.");
               }

               if (!this.isOptional() || this.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
                  throw new DescriptorValidationException(this, "Extensions of MessageSets must be optional messages.");
               }
            }

         }
      }

      private void setProto(DescriptorProtos.FieldDescriptorProto proto) {
         this.proto = proto;
      }

      public MessageLite.Builder internalMergeFrom(MessageLite.Builder to, MessageLite from) {
         return ((Message.Builder)to).mergeFrom((Message)from);
      }

      // $FF: synthetic method
      FieldDescriptor(DescriptorProtos.FieldDescriptorProto x0, FileDescriptor x1, Descriptor x2, int x3, boolean x4, Object x5) throws DescriptorValidationException {
         this(x0, x1, x2, x3, x4);
      }

      static {
         if (Descriptors.FieldDescriptor.Type.values().length != DescriptorProtos.FieldDescriptorProto.Type.values().length) {
            throw new RuntimeException("descriptor.proto has a new declared type but Descriptors.java wasn't updated.");
         }
      }

      public static enum JavaType {
         INT(0),
         LONG(0L),
         FLOAT(0.0F),
         DOUBLE(0.0),
         BOOLEAN(false),
         STRING(""),
         BYTE_STRING(ByteString.EMPTY),
         ENUM((Object)null),
         MESSAGE((Object)null);

         private final Object defaultDefault;

         private JavaType(Object defaultDefault) {
            this.defaultDefault = defaultDefault;
         }
      }

      public static enum Type {
         DOUBLE(Descriptors.FieldDescriptor.JavaType.DOUBLE),
         FLOAT(Descriptors.FieldDescriptor.JavaType.FLOAT),
         INT64(Descriptors.FieldDescriptor.JavaType.LONG),
         UINT64(Descriptors.FieldDescriptor.JavaType.LONG),
         INT32(Descriptors.FieldDescriptor.JavaType.INT),
         FIXED64(Descriptors.FieldDescriptor.JavaType.LONG),
         FIXED32(Descriptors.FieldDescriptor.JavaType.INT),
         BOOL(Descriptors.FieldDescriptor.JavaType.BOOLEAN),
         STRING(Descriptors.FieldDescriptor.JavaType.STRING),
         GROUP(Descriptors.FieldDescriptor.JavaType.MESSAGE),
         MESSAGE(Descriptors.FieldDescriptor.JavaType.MESSAGE),
         BYTES(Descriptors.FieldDescriptor.JavaType.BYTE_STRING),
         UINT32(Descriptors.FieldDescriptor.JavaType.INT),
         ENUM(Descriptors.FieldDescriptor.JavaType.ENUM),
         SFIXED32(Descriptors.FieldDescriptor.JavaType.INT),
         SFIXED64(Descriptors.FieldDescriptor.JavaType.LONG),
         SINT32(Descriptors.FieldDescriptor.JavaType.INT),
         SINT64(Descriptors.FieldDescriptor.JavaType.LONG);

         private JavaType javaType;

         private Type(JavaType javaType) {
            this.javaType = javaType;
         }

         public DescriptorProtos.FieldDescriptorProto.Type toProto() {
            return DescriptorProtos.FieldDescriptorProto.Type.forNumber(this.ordinal() + 1);
         }

         public JavaType getJavaType() {
            return this.javaType;
         }

         public static Type valueOf(DescriptorProtos.FieldDescriptorProto.Type type) {
            return values()[type.getNumber() - 1];
         }
      }
   }

   public static final class Descriptor extends GenericDescriptor {
      private final int index;
      private DescriptorProtos.DescriptorProto proto;
      private final String fullName;
      private final FileDescriptor file;
      private final Descriptor containingType;
      private final Descriptor[] nestedTypes;
      private final EnumDescriptor[] enumTypes;
      private final FieldDescriptor[] fields;
      private final FieldDescriptor[] extensions;
      private final OneofDescriptor[] oneofs;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.DescriptorProto toProto() {
         return this.proto;
      }

      public String getName() {
         return this.proto.getName();
      }

      public String getFullName() {
         return this.fullName;
      }

      public FileDescriptor getFile() {
         return this.file;
      }

      public Descriptor getContainingType() {
         return this.containingType;
      }

      public DescriptorProtos.MessageOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<FieldDescriptor> getFields() {
         return Collections.unmodifiableList(Arrays.asList(this.fields));
      }

      public List<OneofDescriptor> getOneofs() {
         return Collections.unmodifiableList(Arrays.asList(this.oneofs));
      }

      public List<FieldDescriptor> getExtensions() {
         return Collections.unmodifiableList(Arrays.asList(this.extensions));
      }

      public List<Descriptor> getNestedTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.nestedTypes));
      }

      public List<EnumDescriptor> getEnumTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
      }

      public boolean isExtensionNumber(int number) {
         Iterator var2 = this.proto.getExtensionRangeList().iterator();

         DescriptorProtos.DescriptorProto.ExtensionRange range;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            range = (DescriptorProtos.DescriptorProto.ExtensionRange)var2.next();
         } while(range.getStart() > number || number >= range.getEnd());

         return true;
      }

      public boolean isReservedNumber(int number) {
         Iterator var2 = this.proto.getReservedRangeList().iterator();

         DescriptorProtos.DescriptorProto.ReservedRange range;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            range = (DescriptorProtos.DescriptorProto.ReservedRange)var2.next();
         } while(range.getStart() > number || number >= range.getEnd());

         return true;
      }

      public boolean isReservedName(String name) {
         Internal.checkNotNull(name);
         Iterator var2 = this.proto.getReservedNameList().iterator();

         String reservedName;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            reservedName = (String)var2.next();
         } while(!reservedName.equals(name));

         return true;
      }

      public boolean isExtendable() {
         return this.proto.getExtensionRangeList().size() != 0;
      }

      public FieldDescriptor findFieldByName(String name) {
         GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
         return result != null && result instanceof FieldDescriptor ? (FieldDescriptor)result : null;
      }

      public FieldDescriptor findFieldByNumber(int number) {
         return (FieldDescriptor)this.file.pool.fieldsByNumber.get(new DescriptorPool.DescriptorIntPair(this, number));
      }

      public Descriptor findNestedTypeByName(String name) {
         GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
         return result != null && result instanceof Descriptor ? (Descriptor)result : null;
      }

      public EnumDescriptor findEnumTypeByName(String name) {
         GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
         return result != null && result instanceof EnumDescriptor ? (EnumDescriptor)result : null;
      }

      Descriptor(String fullname) throws DescriptorValidationException {
         super(null);
         String name = fullname;
         String packageName = "";
         int pos = fullname.lastIndexOf(46);
         if (pos != -1) {
            name = fullname.substring(pos + 1);
            packageName = fullname.substring(0, pos);
         }

         this.index = 0;
         this.proto = DescriptorProtos.DescriptorProto.newBuilder().setName(name).addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange.newBuilder().setStart(1).setEnd(536870912).build()).build();
         this.fullName = fullname;
         this.containingType = null;
         this.nestedTypes = new Descriptor[0];
         this.enumTypes = new EnumDescriptor[0];
         this.fields = new FieldDescriptor[0];
         this.extensions = new FieldDescriptor[0];
         this.oneofs = new OneofDescriptor[0];
         this.file = new FileDescriptor(packageName, this);
      }

      private Descriptor(DescriptorProtos.DescriptorProto proto, FileDescriptor file, Descriptor parent, int index) throws DescriptorValidationException {
         super(null);
         this.index = index;
         this.proto = proto;
         this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
         this.file = file;
         this.containingType = parent;
         this.oneofs = new OneofDescriptor[proto.getOneofDeclCount()];

         int i;
         for(i = 0; i < proto.getOneofDeclCount(); ++i) {
            this.oneofs[i] = new OneofDescriptor(proto.getOneofDecl(i), file, this, i);
         }

         this.nestedTypes = new Descriptor[proto.getNestedTypeCount()];

         for(i = 0; i < proto.getNestedTypeCount(); ++i) {
            this.nestedTypes[i] = new Descriptor(proto.getNestedType(i), file, this, i);
         }

         this.enumTypes = new EnumDescriptor[proto.getEnumTypeCount()];

         for(i = 0; i < proto.getEnumTypeCount(); ++i) {
            this.enumTypes[i] = new EnumDescriptor(proto.getEnumType(i), file, this, i);
         }

         this.fields = new FieldDescriptor[proto.getFieldCount()];

         for(i = 0; i < proto.getFieldCount(); ++i) {
            this.fields[i] = new FieldDescriptor(proto.getField(i), file, this, i, false);
         }

         this.extensions = new FieldDescriptor[proto.getExtensionCount()];

         for(i = 0; i < proto.getExtensionCount(); ++i) {
            this.extensions[i] = new FieldDescriptor(proto.getExtension(i), file, this, i, true);
         }

         for(i = 0; i < proto.getOneofDeclCount(); ++i) {
            this.oneofs[i].fields = new FieldDescriptor[this.oneofs[i].getFieldCount()];
            this.oneofs[i].fieldCount = 0;
         }

         for(i = 0; i < proto.getFieldCount(); ++i) {
            OneofDescriptor oneofDescriptor = this.fields[i].getContainingOneof();
            if (oneofDescriptor != null) {
               oneofDescriptor.fields[oneofDescriptor.fieldCount++] = this.fields[i];
            }
         }

         file.pool.addSymbol(this);
      }

      private void crossLink() throws DescriptorValidationException {
         Descriptor[] var1 = this.nestedTypes;
         int var2 = var1.length;

         int var3;
         for(var3 = 0; var3 < var2; ++var3) {
            Descriptor nestedType = var1[var3];
            nestedType.crossLink();
         }

         FieldDescriptor[] var5 = this.fields;
         var2 = var5.length;

         FieldDescriptor extension;
         for(var3 = 0; var3 < var2; ++var3) {
            extension = var5[var3];
            extension.crossLink();
         }

         var5 = this.extensions;
         var2 = var5.length;

         for(var3 = 0; var3 < var2; ++var3) {
            extension = var5[var3];
            extension.crossLink();
         }

      }

      private void setProto(DescriptorProtos.DescriptorProto proto) {
         this.proto = proto;

         int i;
         for(i = 0; i < this.nestedTypes.length; ++i) {
            this.nestedTypes[i].setProto(proto.getNestedType(i));
         }

         for(i = 0; i < this.oneofs.length; ++i) {
            this.oneofs[i].setProto(proto.getOneofDecl(i));
         }

         for(i = 0; i < this.enumTypes.length; ++i) {
            this.enumTypes[i].setProto(proto.getEnumType(i));
         }

         for(i = 0; i < this.fields.length; ++i) {
            this.fields[i].setProto(proto.getField(i));
         }

         for(i = 0; i < this.extensions.length; ++i) {
            this.extensions[i].setProto(proto.getExtension(i));
         }

      }

      // $FF: synthetic method
      Descriptor(DescriptorProtos.DescriptorProto x0, FileDescriptor x1, Descriptor x2, int x3, Object x4) throws DescriptorValidationException {
         this(x0, x1, x2, x3);
      }
   }

   public static final class FileDescriptor extends GenericDescriptor {
      private DescriptorProtos.FileDescriptorProto proto;
      private final Descriptor[] messageTypes;
      private final EnumDescriptor[] enumTypes;
      private final ServiceDescriptor[] services;
      private final FieldDescriptor[] extensions;
      private final FileDescriptor[] dependencies;
      private final FileDescriptor[] publicDependencies;
      private final DescriptorPool pool;

      public DescriptorProtos.FileDescriptorProto toProto() {
         return this.proto;
      }

      public String getName() {
         return this.proto.getName();
      }

      public FileDescriptor getFile() {
         return this;
      }

      public String getFullName() {
         return this.proto.getName();
      }

      public String getPackage() {
         return this.proto.getPackage();
      }

      public DescriptorProtos.FileOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<Descriptor> getMessageTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.messageTypes));
      }

      public List<EnumDescriptor> getEnumTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
      }

      public List<ServiceDescriptor> getServices() {
         return Collections.unmodifiableList(Arrays.asList(this.services));
      }

      public List<FieldDescriptor> getExtensions() {
         return Collections.unmodifiableList(Arrays.asList(this.extensions));
      }

      public List<FileDescriptor> getDependencies() {
         return Collections.unmodifiableList(Arrays.asList(this.dependencies));
      }

      public List<FileDescriptor> getPublicDependencies() {
         return Collections.unmodifiableList(Arrays.asList(this.publicDependencies));
      }

      public Syntax getSyntax() {
         return Descriptors.FileDescriptor.Syntax.PROTO3.name.equals(this.proto.getSyntax()) ? Descriptors.FileDescriptor.Syntax.PROTO3 : Descriptors.FileDescriptor.Syntax.PROTO2;
      }

      public Descriptor findMessageTypeByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            GenericDescriptor result = this.pool.findSymbol(name);
            return result != null && result instanceof Descriptor && result.getFile() == this ? (Descriptor)result : null;
         }
      }

      public EnumDescriptor findEnumTypeByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            GenericDescriptor result = this.pool.findSymbol(name);
            return result != null && result instanceof EnumDescriptor && result.getFile() == this ? (EnumDescriptor)result : null;
         }
      }

      public ServiceDescriptor findServiceByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            GenericDescriptor result = this.pool.findSymbol(name);
            return result != null && result instanceof ServiceDescriptor && result.getFile() == this ? (ServiceDescriptor)result : null;
         }
      }

      public FieldDescriptor findExtensionByName(String name) {
         if (name.indexOf(46) != -1) {
            return null;
         } else {
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
               name = packageName + '.' + name;
            }

            GenericDescriptor result = this.pool.findSymbol(name);
            return result != null && result instanceof FieldDescriptor && result.getFile() == this ? (FieldDescriptor)result : null;
         }
      }

      public static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies) throws DescriptorValidationException {
         return buildFrom(proto, dependencies, false);
      }

      public static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies, boolean allowUnknownDependencies) throws DescriptorValidationException {
         DescriptorPool pool = new DescriptorPool(dependencies, allowUnknownDependencies);
         FileDescriptor result = new FileDescriptor(proto, dependencies, pool, allowUnknownDependencies);
         result.crossLink();
         return result;
      }

      private static byte[] latin1Cat(String[] strings) {
         if (strings.length == 1) {
            return strings[0].getBytes(Internal.ISO_8859_1);
         } else {
            StringBuilder descriptorData = new StringBuilder();
            String[] var2 = strings;
            int var3 = strings.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String part = var2[var4];
               descriptorData.append(part);
            }

            return descriptorData.toString().getBytes(Internal.ISO_8859_1);
         }
      }

      private static FileDescriptor[] findDescriptors(Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames) {
         List<FileDescriptor> descriptors = new ArrayList();

         for(int i = 0; i < dependencyClassNames.length; ++i) {
            try {
               Class<?> clazz = descriptorOuterClass.getClassLoader().loadClass(dependencyClassNames[i]);
               descriptors.add((FileDescriptor)clazz.getField("descriptor").get((Object)null));
            } catch (Exception var6) {
               Descriptors.logger.warning("Descriptors for \"" + dependencyFileNames[i] + "\" can not be found.");
            }
         }

         return (FileDescriptor[])descriptors.toArray(new FileDescriptor[0]);
      }

      /** @deprecated */
      @Deprecated
      public static void internalBuildGeneratedFileFrom(String[] descriptorDataParts, FileDescriptor[] dependencies, InternalDescriptorAssigner descriptorAssigner) {
         byte[] descriptorBytes = latin1Cat(descriptorDataParts);

         DescriptorProtos.FileDescriptorProto proto;
         try {
            proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
         } catch (InvalidProtocolBufferException var10) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var10);
         }

         FileDescriptor result;
         try {
            result = buildFrom(proto, dependencies, true);
         } catch (DescriptorValidationException var9) {
            throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto.getName() + "\".", var9);
         }

         ExtensionRegistry registry = descriptorAssigner.assignDescriptors(result);
         if (registry != null) {
            try {
               proto = DescriptorProtos.FileDescriptorProto.parseFrom((byte[])descriptorBytes, registry);
            } catch (InvalidProtocolBufferException var8) {
               throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var8);
            }

            result.setProto(proto);
         }

      }

      public static FileDescriptor internalBuildGeneratedFileFrom(String[] descriptorDataParts, FileDescriptor[] dependencies) {
         byte[] descriptorBytes = latin1Cat(descriptorDataParts);

         DescriptorProtos.FileDescriptorProto proto;
         try {
            proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
         } catch (InvalidProtocolBufferException var6) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var6);
         }

         try {
            return buildFrom(proto, dependencies, true);
         } catch (DescriptorValidationException var5) {
            throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto.getName() + "\".", var5);
         }
      }

      /** @deprecated */
      @Deprecated
      public static void internalBuildGeneratedFileFrom(String[] descriptorDataParts, Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames, InternalDescriptorAssigner descriptorAssigner) {
         FileDescriptor[] dependencies = findDescriptors(descriptorOuterClass, dependencyClassNames, dependencyFileNames);
         internalBuildGeneratedFileFrom(descriptorDataParts, dependencies, descriptorAssigner);
      }

      public static FileDescriptor internalBuildGeneratedFileFrom(String[] descriptorDataParts, Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames) {
         FileDescriptor[] dependencies = findDescriptors(descriptorOuterClass, dependencyClassNames, dependencyFileNames);
         return internalBuildGeneratedFileFrom(descriptorDataParts, dependencies);
      }

      public static void internalUpdateFileDescriptor(FileDescriptor descriptor, ExtensionRegistry registry) {
         ByteString bytes = descriptor.proto.toByteString();

         DescriptorProtos.FileDescriptorProto proto;
         try {
            proto = DescriptorProtos.FileDescriptorProto.parseFrom((ByteString)bytes, registry);
         } catch (InvalidProtocolBufferException var5) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var5);
         }

         descriptor.setProto(proto);
      }

      private FileDescriptor(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies, DescriptorPool pool, boolean allowUnknownDependencies) throws DescriptorValidationException {
         super(null);
         this.pool = pool;
         this.proto = proto;
         this.dependencies = (FileDescriptor[])dependencies.clone();
         HashMap<String, FileDescriptor> nameToFileMap = new HashMap();
         FileDescriptor[] var6 = dependencies;
         int i = dependencies.length;

         int index;
         for(index = 0; index < i; ++index) {
            FileDescriptor file = var6[index];
            nameToFileMap.put(file.getName(), file);
         }

         List<FileDescriptor> publicDependencies = new ArrayList();

         for(i = 0; i < proto.getPublicDependencyCount(); ++i) {
            index = proto.getPublicDependency(i);
            if (index < 0 || index >= proto.getDependencyCount()) {
               throw new DescriptorValidationException(this, "Invalid public dependency index.");
            }

            String name = proto.getDependency(index);
            FileDescriptor file = (FileDescriptor)nameToFileMap.get(name);
            if (file == null) {
               if (!allowUnknownDependencies) {
                  throw new DescriptorValidationException(this, "Invalid public dependency: " + name);
               }
            } else {
               publicDependencies.add(file);
            }
         }

         this.publicDependencies = new FileDescriptor[publicDependencies.size()];
         publicDependencies.toArray(this.publicDependencies);
         pool.addPackage(this.getPackage(), this);
         this.messageTypes = new Descriptor[proto.getMessageTypeCount()];

         for(i = 0; i < proto.getMessageTypeCount(); ++i) {
            this.messageTypes[i] = new Descriptor(proto.getMessageType(i), this, (Descriptor)null, i);
         }

         this.enumTypes = new EnumDescriptor[proto.getEnumTypeCount()];

         for(i = 0; i < proto.getEnumTypeCount(); ++i) {
            this.enumTypes[i] = new EnumDescriptor(proto.getEnumType(i), this, (Descriptor)null, i);
         }

         this.services = new ServiceDescriptor[proto.getServiceCount()];

         for(i = 0; i < proto.getServiceCount(); ++i) {
            this.services[i] = new ServiceDescriptor(proto.getService(i), this, i);
         }

         this.extensions = new FieldDescriptor[proto.getExtensionCount()];

         for(i = 0; i < proto.getExtensionCount(); ++i) {
            this.extensions[i] = new FieldDescriptor(proto.getExtension(i), this, (Descriptor)null, i, true);
         }

      }

      FileDescriptor(String packageName, Descriptor message) throws DescriptorValidationException {
         super(null);
         this.pool = new DescriptorPool(new FileDescriptor[0], true);
         this.proto = DescriptorProtos.FileDescriptorProto.newBuilder().setName(message.getFullName() + ".placeholder.proto").setPackage(packageName).addMessageType(message.toProto()).build();
         this.dependencies = new FileDescriptor[0];
         this.publicDependencies = new FileDescriptor[0];
         this.messageTypes = new Descriptor[]{message};
         this.enumTypes = new EnumDescriptor[0];
         this.services = new ServiceDescriptor[0];
         this.extensions = new FieldDescriptor[0];
         this.pool.addPackage(packageName, this);
         this.pool.addSymbol(message);
      }

      private void crossLink() throws DescriptorValidationException {
         Descriptor[] var1 = this.messageTypes;
         int var2 = var1.length;

         int var3;
         for(var3 = 0; var3 < var2; ++var3) {
            Descriptor messageType = var1[var3];
            messageType.crossLink();
         }

         ServiceDescriptor[] var5 = this.services;
         var2 = var5.length;

         for(var3 = 0; var3 < var2; ++var3) {
            ServiceDescriptor service = var5[var3];
            service.crossLink();
         }

         FieldDescriptor[] var6 = this.extensions;
         var2 = var6.length;

         for(var3 = 0; var3 < var2; ++var3) {
            FieldDescriptor extension = var6[var3];
            extension.crossLink();
         }

      }

      private void setProto(DescriptorProtos.FileDescriptorProto proto) {
         this.proto = proto;

         int i;
         for(i = 0; i < this.messageTypes.length; ++i) {
            this.messageTypes[i].setProto(proto.getMessageType(i));
         }

         for(i = 0; i < this.enumTypes.length; ++i) {
            this.enumTypes[i].setProto(proto.getEnumType(i));
         }

         for(i = 0; i < this.services.length; ++i) {
            this.services[i].setProto(proto.getService(i));
         }

         for(i = 0; i < this.extensions.length; ++i) {
            this.extensions[i].setProto(proto.getExtension(i));
         }

      }

      boolean supportsUnknownEnumValue() {
         return this.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3;
      }

      /** @deprecated */
      @Deprecated
      public interface InternalDescriptorAssigner {
         ExtensionRegistry assignDescriptors(FileDescriptor var1);
      }

      public static enum Syntax {
         UNKNOWN("unknown"),
         PROTO2("proto2"),
         PROTO3("proto3");

         private final String name;

         private Syntax(String name) {
            this.name = name;
         }
      }
   }
}
