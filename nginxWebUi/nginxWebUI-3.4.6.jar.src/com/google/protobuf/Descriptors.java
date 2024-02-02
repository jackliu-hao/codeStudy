/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Descriptors
/*      */ {
/*   81 */   private static final Logger logger = Logger.getLogger(Descriptors.class.getName());
/*      */   public static final class FileDescriptor extends GenericDescriptor { private DescriptorProtos.FileDescriptorProto proto; private final Descriptors.Descriptor[] messageTypes;
/*      */     private final Descriptors.EnumDescriptor[] enumTypes;
/*      */     private final Descriptors.ServiceDescriptor[] services;
/*      */     private final Descriptors.FieldDescriptor[] extensions;
/*      */     private final FileDescriptor[] dependencies;
/*      */     private final FileDescriptor[] publicDependencies;
/*      */     private final Descriptors.DescriptorPool pool;
/*      */     
/*      */     public DescriptorProtos.FileDescriptorProto toProto() {
/*   91 */       return this.proto;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/*   97 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public FileDescriptor getFile() {
/*  103 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFullName() {
/*  109 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getPackage() {
/*  117 */       return this.proto.getPackage();
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.FileOptions getOptions() {
/*  122 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.Descriptor> getMessageTypes() {
/*  127 */       return Collections.unmodifiableList(Arrays.asList(this.messageTypes));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.EnumDescriptor> getEnumTypes() {
/*  132 */       return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.ServiceDescriptor> getServices() {
/*  137 */       return Collections.unmodifiableList(Arrays.asList(this.services));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.FieldDescriptor> getExtensions() {
/*  142 */       return Collections.unmodifiableList(Arrays.asList(this.extensions));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<FileDescriptor> getDependencies() {
/*  147 */       return Collections.unmodifiableList(Arrays.asList(this.dependencies));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<FileDescriptor> getPublicDependencies() {
/*  152 */       return Collections.unmodifiableList(Arrays.asList(this.publicDependencies));
/*      */     }
/*      */     @Deprecated
/*      */     public static interface InternalDescriptorAssigner {
/*      */       ExtensionRegistry assignDescriptors(Descriptors.FileDescriptor param2FileDescriptor); }
/*  157 */     public enum Syntax { UNKNOWN("unknown"),
/*  158 */       PROTO2("proto2"),
/*  159 */       PROTO3("proto3"); private final String name;
/*      */       
/*      */       Syntax(String name) {
/*  162 */         this.name = name;
/*      */       } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Syntax getSyntax() {
/*  170 */       if (Syntax.PROTO3.name.equals(this.proto.getSyntax())) {
/*  171 */         return Syntax.PROTO3;
/*      */       }
/*  173 */       return Syntax.PROTO2;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.Descriptor findMessageTypeByName(String name) {
/*  185 */       if (name.indexOf('.') != -1) {
/*  186 */         return null;
/*      */       }
/*  188 */       String packageName = getPackage();
/*  189 */       if (!packageName.isEmpty()) {
/*  190 */         name = packageName + '.' + name;
/*      */       }
/*  192 */       Descriptors.GenericDescriptor result = this.pool.findSymbol(name);
/*  193 */       if (result != null && result instanceof Descriptors.Descriptor && result.getFile() == this) {
/*  194 */         return (Descriptors.Descriptor)result;
/*      */       }
/*  196 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.EnumDescriptor findEnumTypeByName(String name) {
/*  209 */       if (name.indexOf('.') != -1) {
/*  210 */         return null;
/*      */       }
/*  212 */       String packageName = getPackage();
/*  213 */       if (!packageName.isEmpty()) {
/*  214 */         name = packageName + '.' + name;
/*      */       }
/*  216 */       Descriptors.GenericDescriptor result = this.pool.findSymbol(name);
/*  217 */       if (result != null && result instanceof Descriptors.EnumDescriptor && result.getFile() == this) {
/*  218 */         return (Descriptors.EnumDescriptor)result;
/*      */       }
/*  220 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.ServiceDescriptor findServiceByName(String name) {
/*  233 */       if (name.indexOf('.') != -1) {
/*  234 */         return null;
/*      */       }
/*  236 */       String packageName = getPackage();
/*  237 */       if (!packageName.isEmpty()) {
/*  238 */         name = packageName + '.' + name;
/*      */       }
/*  240 */       Descriptors.GenericDescriptor result = this.pool.findSymbol(name);
/*  241 */       if (result != null && result instanceof Descriptors.ServiceDescriptor && result.getFile() == this) {
/*  242 */         return (Descriptors.ServiceDescriptor)result;
/*      */       }
/*  244 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FieldDescriptor findExtensionByName(String name) {
/*  255 */       if (name.indexOf('.') != -1) {
/*  256 */         return null;
/*      */       }
/*  258 */       String packageName = getPackage();
/*  259 */       if (!packageName.isEmpty()) {
/*  260 */         name = packageName + '.' + name;
/*      */       }
/*  262 */       Descriptors.GenericDescriptor result = this.pool.findSymbol(name);
/*  263 */       if (result != null && result instanceof Descriptors.FieldDescriptor && result.getFile() == this) {
/*  264 */         return (Descriptors.FieldDescriptor)result;
/*      */       }
/*  266 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies) throws Descriptors.DescriptorValidationException {
/*  282 */       return buildFrom(proto, dependencies, false);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies, boolean allowUnknownDependencies) throws Descriptors.DescriptorValidationException {
/*  310 */       Descriptors.DescriptorPool pool = new Descriptors.DescriptorPool(dependencies, allowUnknownDependencies);
/*  311 */       FileDescriptor result = new FileDescriptor(proto, dependencies, pool, allowUnknownDependencies);
/*      */       
/*  313 */       result.crossLink();
/*  314 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static byte[] latin1Cat(String[] strings) {
/*  327 */       if (strings.length == 1) {
/*  328 */         return strings[0].getBytes(Internal.ISO_8859_1);
/*      */       }
/*  330 */       StringBuilder descriptorData = new StringBuilder();
/*  331 */       for (String part : strings) {
/*  332 */         descriptorData.append(part);
/*      */       }
/*  334 */       return descriptorData.toString().getBytes(Internal.ISO_8859_1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static FileDescriptor[] findDescriptors(Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames) {
/*  341 */       List<FileDescriptor> descriptors = new ArrayList<>();
/*  342 */       for (int i = 0; i < dependencyClassNames.length; i++) {
/*      */         try {
/*  344 */           Class<?> clazz = descriptorOuterClass.getClassLoader().loadClass(dependencyClassNames[i]);
/*  345 */           descriptors.add((FileDescriptor)clazz.getField("descriptor").get(null));
/*  346 */         } catch (Exception e) {
/*      */ 
/*      */           
/*  349 */           Descriptors.logger.warning("Descriptors for \"" + dependencyFileNames[i] + "\" can not be found.");
/*      */         } 
/*      */       } 
/*  352 */       return descriptors.<FileDescriptor>toArray(new FileDescriptor[0]);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public static void internalBuildGeneratedFileFrom(String[] descriptorDataParts, FileDescriptor[] dependencies, InternalDescriptorAssigner descriptorAssigner) {
/*      */       DescriptorProtos.FileDescriptorProto proto;
/*      */       FileDescriptor result;
/*  364 */       byte[] descriptorBytes = latin1Cat(descriptorDataParts);
/*      */ 
/*      */       
/*      */       try {
/*  368 */         proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
/*  369 */       } catch (InvalidProtocolBufferException e) {
/*  370 */         throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  378 */         result = buildFrom(proto, dependencies, true);
/*  379 */       } catch (DescriptorValidationException e) {
/*  380 */         throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto
/*  381 */             .getName() + "\".", e);
/*      */       } 
/*      */       
/*  384 */       ExtensionRegistry registry = descriptorAssigner.assignDescriptors(result);
/*      */       
/*  386 */       if (registry != null) {
/*      */         
/*      */         try {
/*  389 */           proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes, registry);
/*  390 */         } catch (InvalidProtocolBufferException e) {
/*  391 */           throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
/*      */         } 
/*      */ 
/*      */         
/*  395 */         result.setProto(proto);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static FileDescriptor internalBuildGeneratedFileFrom(String[] descriptorDataParts, FileDescriptor[] dependencies) {
/*      */       DescriptorProtos.FileDescriptorProto proto;
/*  406 */       byte[] descriptorBytes = latin1Cat(descriptorDataParts);
/*      */ 
/*      */       
/*      */       try {
/*  410 */         proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
/*  411 */       } catch (InvalidProtocolBufferException e) {
/*  412 */         throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  419 */         return buildFrom(proto, dependencies, true);
/*  420 */       } catch (DescriptorValidationException e) {
/*  421 */         throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto
/*  422 */             .getName() + "\".", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public static void internalBuildGeneratedFileFrom(String[] descriptorDataParts, Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames, InternalDescriptorAssigner descriptorAssigner) {
/*  437 */       FileDescriptor[] dependencies = findDescriptors(descriptorOuterClass, dependencyClassNames, dependencyFileNames);
/*      */       
/*  439 */       internalBuildGeneratedFileFrom(descriptorDataParts, dependencies, descriptorAssigner);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static FileDescriptor internalBuildGeneratedFileFrom(String[] descriptorDataParts, Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames) {
/*  452 */       FileDescriptor[] dependencies = findDescriptors(descriptorOuterClass, dependencyClassNames, dependencyFileNames);
/*      */       
/*  454 */       return internalBuildGeneratedFileFrom(descriptorDataParts, dependencies);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static void internalUpdateFileDescriptor(FileDescriptor descriptor, ExtensionRegistry registry) {
/*      */       DescriptorProtos.FileDescriptorProto proto;
/*  464 */       ByteString bytes = descriptor.proto.toByteString();
/*      */       
/*      */       try {
/*  467 */         proto = DescriptorProtos.FileDescriptorProto.parseFrom(bytes, registry);
/*  468 */       } catch (InvalidProtocolBufferException e) {
/*  469 */         throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
/*      */       } 
/*      */       
/*  472 */       descriptor.setProto(proto);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private FileDescriptor(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies, Descriptors.DescriptorPool pool, boolean allowUnknownDependencies) throws Descriptors.DescriptorValidationException {
/*  507 */       this.pool = pool;
/*  508 */       this.proto = proto;
/*  509 */       this.dependencies = (FileDescriptor[])dependencies.clone();
/*  510 */       HashMap<String, FileDescriptor> nameToFileMap = new HashMap<>();
/*  511 */       for (FileDescriptor file : dependencies) {
/*  512 */         nameToFileMap.put(file.getName(), file);
/*      */       }
/*  514 */       List<FileDescriptor> publicDependencies = new ArrayList<>(); int i;
/*  515 */       for (i = 0; i < proto.getPublicDependencyCount(); i++) {
/*  516 */         int index = proto.getPublicDependency(i);
/*  517 */         if (index < 0 || index >= proto.getDependencyCount()) {
/*  518 */           throw new Descriptors.DescriptorValidationException(this, "Invalid public dependency index.");
/*      */         }
/*  520 */         String name = proto.getDependency(index);
/*  521 */         FileDescriptor file = nameToFileMap.get(name);
/*  522 */         if (file == null) {
/*  523 */           if (!allowUnknownDependencies) {
/*  524 */             throw new Descriptors.DescriptorValidationException(this, "Invalid public dependency: " + name);
/*      */           }
/*      */         } else {
/*      */           
/*  528 */           publicDependencies.add(file);
/*      */         } 
/*      */       } 
/*  531 */       this.publicDependencies = new FileDescriptor[publicDependencies.size()];
/*  532 */       publicDependencies.toArray(this.publicDependencies);
/*      */       
/*  534 */       pool.addPackage(getPackage(), this);
/*      */       
/*  536 */       this.messageTypes = new Descriptors.Descriptor[proto.getMessageTypeCount()];
/*  537 */       for (i = 0; i < proto.getMessageTypeCount(); i++) {
/*  538 */         this.messageTypes[i] = new Descriptors.Descriptor(proto.getMessageType(i), this, null, i);
/*      */       }
/*      */       
/*  541 */       this.enumTypes = new Descriptors.EnumDescriptor[proto.getEnumTypeCount()];
/*  542 */       for (i = 0; i < proto.getEnumTypeCount(); i++) {
/*  543 */         this.enumTypes[i] = new Descriptors.EnumDescriptor(proto.getEnumType(i), this, null, i);
/*      */       }
/*      */       
/*  546 */       this.services = new Descriptors.ServiceDescriptor[proto.getServiceCount()];
/*  547 */       for (i = 0; i < proto.getServiceCount(); i++) {
/*  548 */         this.services[i] = new Descriptors.ServiceDescriptor(proto.getService(i), this, i);
/*      */       }
/*      */       
/*  551 */       this.extensions = new Descriptors.FieldDescriptor[proto.getExtensionCount()];
/*  552 */       for (i = 0; i < proto.getExtensionCount(); i++) {
/*  553 */         this.extensions[i] = new Descriptors.FieldDescriptor(proto.getExtension(i), this, null, i, true);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     FileDescriptor(String packageName, Descriptors.Descriptor message) throws Descriptors.DescriptorValidationException {
/*  559 */       this.pool = new Descriptors.DescriptorPool(new FileDescriptor[0], true);
/*  560 */       this
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  565 */         .proto = DescriptorProtos.FileDescriptorProto.newBuilder().setName(message.getFullName() + ".placeholder.proto").setPackage(packageName).addMessageType(message.toProto()).build();
/*  566 */       this.dependencies = new FileDescriptor[0];
/*  567 */       this.publicDependencies = new FileDescriptor[0];
/*      */       
/*  569 */       this.messageTypes = new Descriptors.Descriptor[] { message };
/*  570 */       this.enumTypes = new Descriptors.EnumDescriptor[0];
/*  571 */       this.services = new Descriptors.ServiceDescriptor[0];
/*  572 */       this.extensions = new Descriptors.FieldDescriptor[0];
/*      */       
/*  574 */       this.pool.addPackage(packageName, this);
/*  575 */       this.pool.addSymbol(message);
/*      */     }
/*      */ 
/*      */     
/*      */     private void crossLink() throws Descriptors.DescriptorValidationException {
/*  580 */       for (Descriptors.Descriptor messageType : this.messageTypes) {
/*  581 */         messageType.crossLink();
/*      */       }
/*      */       
/*  584 */       for (Descriptors.ServiceDescriptor service : this.services) {
/*  585 */         service.crossLink();
/*      */       }
/*      */       
/*  588 */       for (Descriptors.FieldDescriptor extension : this.extensions) {
/*  589 */         extension.crossLink();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void setProto(DescriptorProtos.FileDescriptorProto proto) {
/*  602 */       this.proto = proto;
/*      */       int i;
/*  604 */       for (i = 0; i < this.messageTypes.length; i++) {
/*  605 */         this.messageTypes[i].setProto(proto.getMessageType(i));
/*      */       }
/*      */       
/*  608 */       for (i = 0; i < this.enumTypes.length; i++) {
/*  609 */         this.enumTypes[i].setProto(proto.getEnumType(i));
/*      */       }
/*      */       
/*  612 */       for (i = 0; i < this.services.length; i++) {
/*  613 */         this.services[i].setProto(proto.getService(i));
/*      */       }
/*      */       
/*  616 */       for (i = 0; i < this.extensions.length; i++) {
/*  617 */         this.extensions[i].setProto(proto.getExtension(i));
/*      */       }
/*      */     }
/*      */     
/*      */     boolean supportsUnknownEnumValue() {
/*  622 */       return (getSyntax() == Syntax.PROTO3);
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Descriptor
/*      */     extends GenericDescriptor
/*      */   {
/*      */     private final int index;
/*      */     
/*      */     private DescriptorProtos.DescriptorProto proto;
/*      */     
/*      */     private final String fullName;
/*      */     
/*      */     private final Descriptors.FileDescriptor file;
/*      */     
/*      */     private final Descriptor containingType;
/*      */     
/*      */     private final Descriptor[] nestedTypes;
/*      */     private final Descriptors.EnumDescriptor[] enumTypes;
/*      */     private final Descriptors.FieldDescriptor[] fields;
/*      */     private final Descriptors.FieldDescriptor[] extensions;
/*      */     private final Descriptors.OneofDescriptor[] oneofs;
/*      */     
/*      */     public int getIndex() {
/*  647 */       return this.index;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public DescriptorProtos.DescriptorProto toProto() {
/*  653 */       return this.proto;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/*  659 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFullName() {
/*  676 */       return this.fullName;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FileDescriptor getFile() {
/*  682 */       return this.file;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptor getContainingType() {
/*  687 */       return this.containingType;
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.MessageOptions getOptions() {
/*  692 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.FieldDescriptor> getFields() {
/*  697 */       return Collections.unmodifiableList(Arrays.asList(this.fields));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.OneofDescriptor> getOneofs() {
/*  702 */       return Collections.unmodifiableList(Arrays.asList(this.oneofs));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.FieldDescriptor> getExtensions() {
/*  707 */       return Collections.unmodifiableList(Arrays.asList(this.extensions));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptor> getNestedTypes() {
/*  712 */       return Collections.unmodifiableList(Arrays.asList(this.nestedTypes));
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.EnumDescriptor> getEnumTypes() {
/*  717 */       return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isExtensionNumber(int number) {
/*  722 */       for (DescriptorProtos.DescriptorProto.ExtensionRange range : this.proto.getExtensionRangeList()) {
/*  723 */         if (range.getStart() <= number && number < range.getEnd()) {
/*  724 */           return true;
/*      */         }
/*      */       } 
/*  727 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isReservedNumber(int number) {
/*  732 */       for (DescriptorProtos.DescriptorProto.ReservedRange range : this.proto.getReservedRangeList()) {
/*  733 */         if (range.getStart() <= number && number < range.getEnd()) {
/*  734 */           return true;
/*      */         }
/*      */       } 
/*  737 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isReservedName(String name) {
/*  742 */       Internal.checkNotNull(name);
/*  743 */       for (String reservedName : this.proto.getReservedNameList()) {
/*  744 */         if (reservedName.equals(name)) {
/*  745 */           return true;
/*      */         }
/*      */       } 
/*  748 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isExtendable() {
/*  756 */       return (this.proto.getExtensionRangeList().size() != 0);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FieldDescriptor findFieldByName(String name) {
/*  766 */       Descriptors.GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
/*  767 */       if (result != null && result instanceof Descriptors.FieldDescriptor) {
/*  768 */         return (Descriptors.FieldDescriptor)result;
/*      */       }
/*  770 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FieldDescriptor findFieldByNumber(int number) {
/*  781 */       return (Descriptors.FieldDescriptor)this.file.pool.fieldsByNumber.get(new Descriptors.DescriptorPool.DescriptorIntPair(this, number));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptor findNestedTypeByName(String name) {
/*  791 */       Descriptors.GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
/*  792 */       if (result != null && result instanceof Descriptor) {
/*  793 */         return (Descriptor)result;
/*      */       }
/*  795 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.EnumDescriptor findEnumTypeByName(String name) {
/*  806 */       Descriptors.GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
/*  807 */       if (result != null && result instanceof Descriptors.EnumDescriptor) {
/*  808 */         return (Descriptors.EnumDescriptor)result;
/*      */       }
/*  810 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Descriptor(String fullname) throws Descriptors.DescriptorValidationException {
/*  827 */       String name = fullname;
/*  828 */       String packageName = "";
/*  829 */       int pos = fullname.lastIndexOf('.');
/*  830 */       if (pos != -1) {
/*  831 */         name = fullname.substring(pos + 1);
/*  832 */         packageName = fullname.substring(0, pos);
/*      */       } 
/*  834 */       this.index = 0;
/*  835 */       this
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  840 */         .proto = DescriptorProtos.DescriptorProto.newBuilder().setName(name).addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange.newBuilder().setStart(1).setEnd(536870912).build()).build();
/*  841 */       this.fullName = fullname;
/*  842 */       this.containingType = null;
/*      */       
/*  844 */       this.nestedTypes = new Descriptor[0];
/*  845 */       this.enumTypes = new Descriptors.EnumDescriptor[0];
/*  846 */       this.fields = new Descriptors.FieldDescriptor[0];
/*  847 */       this.extensions = new Descriptors.FieldDescriptor[0];
/*  848 */       this.oneofs = new Descriptors.OneofDescriptor[0];
/*      */ 
/*      */       
/*  851 */       this.file = new Descriptors.FileDescriptor(packageName, this);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Descriptor(DescriptorProtos.DescriptorProto proto, Descriptors.FileDescriptor file, Descriptor parent, int index) throws Descriptors.DescriptorValidationException {
/*  860 */       this.index = index;
/*  861 */       this.proto = proto;
/*  862 */       this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
/*  863 */       this.file = file;
/*  864 */       this.containingType = parent;
/*      */       
/*  866 */       this.oneofs = new Descriptors.OneofDescriptor[proto.getOneofDeclCount()]; int i;
/*  867 */       for (i = 0; i < proto.getOneofDeclCount(); i++) {
/*  868 */         this.oneofs[i] = new Descriptors.OneofDescriptor(proto.getOneofDecl(i), file, this, i);
/*      */       }
/*      */       
/*  871 */       this.nestedTypes = new Descriptor[proto.getNestedTypeCount()];
/*  872 */       for (i = 0; i < proto.getNestedTypeCount(); i++) {
/*  873 */         this.nestedTypes[i] = new Descriptor(proto.getNestedType(i), file, this, i);
/*      */       }
/*      */       
/*  876 */       this.enumTypes = new Descriptors.EnumDescriptor[proto.getEnumTypeCount()];
/*  877 */       for (i = 0; i < proto.getEnumTypeCount(); i++) {
/*  878 */         this.enumTypes[i] = new Descriptors.EnumDescriptor(proto.getEnumType(i), file, this, i);
/*      */       }
/*      */       
/*  881 */       this.fields = new Descriptors.FieldDescriptor[proto.getFieldCount()];
/*  882 */       for (i = 0; i < proto.getFieldCount(); i++) {
/*  883 */         this.fields[i] = new Descriptors.FieldDescriptor(proto.getField(i), file, this, i, false);
/*      */       }
/*      */       
/*  886 */       this.extensions = new Descriptors.FieldDescriptor[proto.getExtensionCount()];
/*  887 */       for (i = 0; i < proto.getExtensionCount(); i++) {
/*  888 */         this.extensions[i] = new Descriptors.FieldDescriptor(proto.getExtension(i), file, this, i, true);
/*      */       }
/*      */       
/*  891 */       for (i = 0; i < proto.getOneofDeclCount(); i++) {
/*  892 */         (this.oneofs[i]).fields = new Descriptors.FieldDescriptor[this.oneofs[i].getFieldCount()];
/*  893 */         (this.oneofs[i]).fieldCount = 0;
/*      */       } 
/*  895 */       for (i = 0; i < proto.getFieldCount(); i++) {
/*  896 */         Descriptors.OneofDescriptor oneofDescriptor = this.fields[i].getContainingOneof();
/*  897 */         if (oneofDescriptor != null) {
/*  898 */           oneofDescriptor.fields[oneofDescriptor.fieldCount++] = this.fields[i];
/*      */         }
/*      */       } 
/*      */       
/*  902 */       file.pool.addSymbol(this);
/*      */     }
/*      */ 
/*      */     
/*      */     private void crossLink() throws Descriptors.DescriptorValidationException {
/*  907 */       for (Descriptor nestedType : this.nestedTypes) {
/*  908 */         nestedType.crossLink();
/*      */       }
/*      */       
/*  911 */       for (Descriptors.FieldDescriptor field : this.fields) {
/*  912 */         field.crossLink();
/*      */       }
/*      */       
/*  915 */       for (Descriptors.FieldDescriptor extension : this.extensions) {
/*  916 */         extension.crossLink();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void setProto(DescriptorProtos.DescriptorProto proto) {
/*  922 */       this.proto = proto;
/*      */       int i;
/*  924 */       for (i = 0; i < this.nestedTypes.length; i++) {
/*  925 */         this.nestedTypes[i].setProto(proto.getNestedType(i));
/*      */       }
/*      */       
/*  928 */       for (i = 0; i < this.oneofs.length; i++) {
/*  929 */         this.oneofs[i].setProto(proto.getOneofDecl(i));
/*      */       }
/*      */       
/*  932 */       for (i = 0; i < this.enumTypes.length; i++) {
/*  933 */         this.enumTypes[i].setProto(proto.getEnumType(i));
/*      */       }
/*      */       
/*  936 */       for (i = 0; i < this.fields.length; i++) {
/*  937 */         this.fields[i].setProto(proto.getField(i));
/*      */       }
/*      */       
/*  940 */       for (i = 0; i < this.extensions.length; i++) {
/*  941 */         this.extensions[i].setProto(proto.getExtension(i));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class FieldDescriptor
/*      */     extends GenericDescriptor
/*      */     implements Comparable<FieldDescriptor>, FieldSet.FieldDescriptorLite<FieldDescriptor>
/*      */   {
/*      */     public int getIndex() {
/*  957 */       return this.index;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public DescriptorProtos.FieldDescriptorProto toProto() {
/*  963 */       return this.proto;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/*  969 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getNumber() {
/*  975 */       return this.proto.getNumber();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFullName() {
/*  985 */       return this.fullName;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getJsonName() {
/*  990 */       return this.jsonName;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public JavaType getJavaType() {
/*  998 */       return this.type.getJavaType();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public WireFormat.JavaType getLiteJavaType() {
/* 1004 */       return getLiteType().getJavaType();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FileDescriptor getFile() {
/* 1010 */       return this.file;
/*      */     }
/*      */ 
/*      */     
/*      */     public Type getType() {
/* 1015 */       return this.type;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public WireFormat.FieldType getLiteType() {
/* 1021 */       return table[this.type.ordinal()];
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean needsUtf8Check() {
/* 1026 */       if (this.type != Type.STRING) {
/* 1027 */         return false;
/*      */       }
/* 1029 */       if (getContainingType().getOptions().getMapEntry())
/*      */       {
/* 1031 */         return true;
/*      */       }
/* 1033 */       if (getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3) {
/* 1034 */         return true;
/*      */       }
/* 1036 */       return getFile().getOptions().getJavaStringCheckUtf8();
/*      */     }
/*      */     
/*      */     public boolean isMapField() {
/* 1040 */       return (getType() == Type.MESSAGE && 
/* 1041 */         isRepeated() && 
/* 1042 */         getMessageType().getOptions().getMapEntry());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1048 */     private static final WireFormat.FieldType[] table = WireFormat.FieldType.values(); private final int index; private DescriptorProtos.FieldDescriptorProto proto; private final String fullName; private final String jsonName; private final Descriptors.FileDescriptor file; private final Descriptors.Descriptor extensionScope; private Type type; private Descriptors.Descriptor containingType; private Descriptors.Descriptor messageType; private Descriptors.OneofDescriptor containingOneof; private Descriptors.EnumDescriptor enumType;
/*      */     private Object defaultValue;
/*      */     
/*      */     public boolean isRequired() {
/* 1052 */       return (this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isOptional() {
/* 1057 */       return (this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isRepeated() {
/* 1063 */       return (this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isPacked() {
/* 1072 */       if (!isPackable()) {
/* 1073 */         return false;
/*      */       }
/* 1075 */       if (getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2) {
/* 1076 */         return getOptions().getPacked();
/*      */       }
/* 1078 */       return (!getOptions().hasPacked() || getOptions().getPacked());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isPackable() {
/* 1084 */       return (isRepeated() && getLiteType().isPackable());
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasDefaultValue() {
/* 1089 */       return this.proto.hasDefaultValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getDefaultValue() {
/* 1098 */       if (getJavaType() == JavaType.MESSAGE) {
/* 1099 */         throw new UnsupportedOperationException("FieldDescriptor.getDefaultValue() called on an embedded message field.");
/*      */       }
/*      */       
/* 1102 */       return this.defaultValue;
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.FieldOptions getOptions() {
/* 1107 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isExtension() {
/* 1112 */       return this.proto.hasExtendee();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.Descriptor getContainingType() {
/* 1120 */       return this.containingType;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.OneofDescriptor getContainingOneof() {
/* 1125 */       return this.containingOneof;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.Descriptor getExtensionScope() {
/* 1150 */       if (!isExtension()) {
/* 1151 */         throw new UnsupportedOperationException(
/* 1152 */             String.format("This field is not an extension. (%s)", new Object[] { this.fullName }));
/*      */       }
/* 1154 */       return this.extensionScope;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.Descriptor getMessageType() {
/* 1159 */       if (getJavaType() != JavaType.MESSAGE) {
/* 1160 */         throw new UnsupportedOperationException(
/* 1161 */             String.format("This field is not of message type. (%s)", new Object[] { this.fullName }));
/*      */       }
/* 1163 */       return this.messageType;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.EnumDescriptor getEnumType() {
/* 1169 */       if (getJavaType() != JavaType.ENUM) {
/* 1170 */         throw new UnsupportedOperationException(
/* 1171 */             String.format("This field is not of enum type. (%s)", new Object[] { this.fullName }));
/*      */       }
/* 1173 */       return this.enumType;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int compareTo(FieldDescriptor other) {
/* 1186 */       if (other.containingType != this.containingType) {
/* 1187 */         throw new IllegalArgumentException("FieldDescriptors can only be compared to other FieldDescriptors for fields of the same message type.");
/*      */       }
/*      */ 
/*      */       
/* 1191 */       return getNumber() - other.getNumber();
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1196 */       return getFullName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public enum Type
/*      */     {
/* 1216 */       DOUBLE((String)Descriptors.FieldDescriptor.JavaType.DOUBLE),
/* 1217 */       FLOAT((String)Descriptors.FieldDescriptor.JavaType.FLOAT),
/* 1218 */       INT64((String)Descriptors.FieldDescriptor.JavaType.LONG),
/* 1219 */       UINT64((String)Descriptors.FieldDescriptor.JavaType.LONG),
/* 1220 */       INT32((String)Descriptors.FieldDescriptor.JavaType.INT),
/* 1221 */       FIXED64((String)Descriptors.FieldDescriptor.JavaType.LONG),
/* 1222 */       FIXED32((String)Descriptors.FieldDescriptor.JavaType.INT),
/* 1223 */       BOOL((String)Descriptors.FieldDescriptor.JavaType.BOOLEAN),
/* 1224 */       STRING((String)Descriptors.FieldDescriptor.JavaType.STRING),
/* 1225 */       GROUP((String)Descriptors.FieldDescriptor.JavaType.MESSAGE),
/* 1226 */       MESSAGE((String)Descriptors.FieldDescriptor.JavaType.MESSAGE),
/* 1227 */       BYTES((String)Descriptors.FieldDescriptor.JavaType.BYTE_STRING),
/* 1228 */       UINT32((String)Descriptors.FieldDescriptor.JavaType.INT),
/* 1229 */       ENUM((String)Descriptors.FieldDescriptor.JavaType.ENUM),
/* 1230 */       SFIXED32((String)Descriptors.FieldDescriptor.JavaType.INT),
/* 1231 */       SFIXED64((String)Descriptors.FieldDescriptor.JavaType.LONG),
/* 1232 */       SINT32((String)Descriptors.FieldDescriptor.JavaType.INT),
/* 1233 */       SINT64((String)Descriptors.FieldDescriptor.JavaType.LONG); private Descriptors.FieldDescriptor.JavaType javaType;
/*      */       
/*      */       Type(Descriptors.FieldDescriptor.JavaType javaType) {
/* 1236 */         this.javaType = javaType;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public DescriptorProtos.FieldDescriptorProto.Type toProto() {
/* 1242 */         return DescriptorProtos.FieldDescriptorProto.Type.forNumber(ordinal() + 1);
/*      */       }
/*      */       
/*      */       public Descriptors.FieldDescriptor.JavaType getJavaType() {
/* 1246 */         return this.javaType;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static {
/* 1256 */       if ((Type.values()).length != (DescriptorProtos.FieldDescriptorProto.Type.values()).length) {
/* 1257 */         throw new RuntimeException("descriptor.proto has a new declared type but Descriptors.java wasn't updated.");
/*      */       }
/*      */     }
/*      */     
/*      */     public enum JavaType
/*      */     {
/* 1263 */       INT((String)Integer.valueOf(0)),
/* 1264 */       LONG((String)Long.valueOf(0L)),
/* 1265 */       FLOAT((String)Float.valueOf(0.0F)),
/* 1266 */       DOUBLE((String)Double.valueOf(0.0D)),
/* 1267 */       BOOLEAN((String)Boolean.valueOf(false)),
/* 1268 */       STRING(""),
/* 1269 */       BYTE_STRING((String)ByteString.EMPTY),
/* 1270 */       ENUM(null),
/* 1271 */       MESSAGE(null); private final Object defaultDefault;
/*      */       
/*      */       JavaType(Object defaultDefault) {
/* 1274 */         this.defaultDefault = defaultDefault;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static String fieldNameToJsonName(String name) {
/* 1287 */       int length = name.length();
/* 1288 */       StringBuilder result = new StringBuilder(length);
/* 1289 */       boolean isNextUpperCase = false;
/* 1290 */       for (int i = 0; i < length; i++) {
/* 1291 */         char ch = name.charAt(i);
/* 1292 */         if (ch == '_') {
/* 1293 */           isNextUpperCase = true;
/* 1294 */         } else if (isNextUpperCase) {
/*      */ 
/*      */           
/* 1297 */           if ('a' <= ch && ch <= 'z') {
/* 1298 */             ch = (char)(ch - 97 + 65);
/*      */           }
/* 1300 */           result.append(ch);
/* 1301 */           isNextUpperCase = false;
/*      */         } else {
/* 1303 */           result.append(ch);
/*      */         } 
/*      */       } 
/* 1306 */       return result.toString();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private FieldDescriptor(DescriptorProtos.FieldDescriptorProto proto, Descriptors.FileDescriptor file, Descriptors.Descriptor parent, int index, boolean isExtension) throws Descriptors.DescriptorValidationException {
/* 1316 */       this.index = index;
/* 1317 */       this.proto = proto;
/* 1318 */       this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
/* 1319 */       this.file = file;
/* 1320 */       if (proto.hasJsonName()) {
/* 1321 */         this.jsonName = proto.getJsonName();
/*      */       } else {
/* 1323 */         this.jsonName = fieldNameToJsonName(proto.getName());
/*      */       } 
/*      */       
/* 1326 */       if (proto.hasType()) {
/* 1327 */         this.type = Type.valueOf(proto.getType());
/*      */       }
/*      */       
/* 1330 */       if (getNumber() <= 0) {
/* 1331 */         throw new Descriptors.DescriptorValidationException(this, "Field numbers must be positive integers.");
/*      */       }
/*      */       
/* 1334 */       if (isExtension) {
/* 1335 */         if (!proto.hasExtendee()) {
/* 1336 */           throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.extendee not set for extension field.");
/*      */         }
/*      */         
/* 1339 */         this.containingType = null;
/* 1340 */         if (parent != null) {
/* 1341 */           this.extensionScope = parent;
/*      */         } else {
/* 1343 */           this.extensionScope = null;
/*      */         } 
/*      */         
/* 1346 */         if (proto.hasOneofIndex()) {
/* 1347 */           throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.oneof_index set for extension field.");
/*      */         }
/*      */         
/* 1350 */         this.containingOneof = null;
/*      */       } else {
/* 1352 */         if (proto.hasExtendee()) {
/* 1353 */           throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.extendee set for non-extension field.");
/*      */         }
/*      */         
/* 1356 */         this.containingType = parent;
/*      */         
/* 1358 */         if (proto.hasOneofIndex()) {
/* 1359 */           if (proto.getOneofIndex() < 0 || proto
/* 1360 */             .getOneofIndex() >= parent.toProto().getOneofDeclCount()) {
/* 1361 */             throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.oneof_index is out of range for type " + parent
/*      */                 
/* 1363 */                 .getName());
/*      */           }
/* 1365 */           this.containingOneof = parent.getOneofs().get(proto.getOneofIndex());
/* 1366 */           this.containingOneof.fieldCount++;
/*      */         } else {
/* 1368 */           this.containingOneof = null;
/*      */         } 
/* 1370 */         this.extensionScope = null;
/*      */       } 
/*      */       
/* 1373 */       file.pool.addSymbol(this);
/*      */     }
/*      */ 
/*      */     
/*      */     private void crossLink() throws Descriptors.DescriptorValidationException {
/* 1378 */       if (this.proto.hasExtendee()) {
/*      */         
/* 1380 */         Descriptors.GenericDescriptor extendee = this.file.pool.lookupSymbol(this.proto
/* 1381 */             .getExtendee(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
/* 1382 */         if (!(extendee instanceof Descriptors.Descriptor)) {
/* 1383 */           throw new Descriptors.DescriptorValidationException(this, '"' + this.proto
/* 1384 */               .getExtendee() + "\" is not a message type.");
/*      */         }
/* 1386 */         this.containingType = (Descriptors.Descriptor)extendee;
/*      */         
/* 1388 */         if (!getContainingType().isExtensionNumber(getNumber())) {
/* 1389 */           throw new Descriptors.DescriptorValidationException(this, '"' + 
/*      */ 
/*      */               
/* 1392 */               getContainingType().getFullName() + "\" does not declare " + 
/*      */               
/* 1394 */               getNumber() + " as an extension number.");
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1399 */       if (this.proto.hasTypeName()) {
/*      */         
/* 1401 */         Descriptors.GenericDescriptor typeDescriptor = this.file.pool.lookupSymbol(this.proto
/* 1402 */             .getTypeName(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
/*      */         
/* 1404 */         if (!this.proto.hasType())
/*      */         {
/* 1406 */           if (typeDescriptor instanceof Descriptors.Descriptor) {
/* 1407 */             this.type = Type.MESSAGE;
/* 1408 */           } else if (typeDescriptor instanceof Descriptors.EnumDescriptor) {
/* 1409 */             this.type = Type.ENUM;
/*      */           } else {
/* 1411 */             throw new Descriptors.DescriptorValidationException(this, '"' + this.proto
/* 1412 */                 .getTypeName() + "\" is not a type.");
/*      */           } 
/*      */         }
/*      */         
/* 1416 */         if (getJavaType() == JavaType.MESSAGE) {
/* 1417 */           if (!(typeDescriptor instanceof Descriptors.Descriptor)) {
/* 1418 */             throw new Descriptors.DescriptorValidationException(this, '"' + this.proto
/* 1419 */                 .getTypeName() + "\" is not a message type.");
/*      */           }
/* 1421 */           this.messageType = (Descriptors.Descriptor)typeDescriptor;
/*      */           
/* 1423 */           if (this.proto.hasDefaultValue()) {
/* 1424 */             throw new Descriptors.DescriptorValidationException(this, "Messages can't have default values.");
/*      */           }
/* 1426 */         } else if (getJavaType() == JavaType.ENUM) {
/* 1427 */           if (!(typeDescriptor instanceof Descriptors.EnumDescriptor)) {
/* 1428 */             throw new Descriptors.DescriptorValidationException(this, '"' + this.proto
/* 1429 */                 .getTypeName() + "\" is not an enum type.");
/*      */           }
/* 1431 */           this.enumType = (Descriptors.EnumDescriptor)typeDescriptor;
/*      */         } else {
/* 1433 */           throw new Descriptors.DescriptorValidationException(this, "Field with primitive type has type_name.");
/*      */         }
/*      */       
/* 1436 */       } else if (getJavaType() == JavaType.MESSAGE || getJavaType() == JavaType.ENUM) {
/* 1437 */         throw new Descriptors.DescriptorValidationException(this, "Field with message or enum type missing type_name.");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1443 */       if (this.proto.getOptions().getPacked() && !isPackable()) {
/* 1444 */         throw new Descriptors.DescriptorValidationException(this, "[packed = true] can only be specified for repeated primitive fields.");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1450 */       if (this.proto.hasDefaultValue()) {
/* 1451 */         if (isRepeated()) {
/* 1452 */           throw new Descriptors.DescriptorValidationException(this, "Repeated fields cannot have default values.");
/*      */         }
/*      */ 
/*      */         
/*      */         try {
/* 1457 */           switch (getType()) {
/*      */             case ENUM:
/*      */             case MESSAGE:
/*      */             case null:
/* 1461 */               this.defaultValue = Integer.valueOf(TextFormat.parseInt32(this.proto.getDefaultValue()));
/*      */               break;
/*      */             case null:
/*      */             case null:
/* 1465 */               this.defaultValue = Integer.valueOf(TextFormat.parseUInt32(this.proto.getDefaultValue()));
/*      */               break;
/*      */             case null:
/*      */             case null:
/*      */             case null:
/* 1470 */               this.defaultValue = Long.valueOf(TextFormat.parseInt64(this.proto.getDefaultValue()));
/*      */               break;
/*      */             case null:
/*      */             case null:
/* 1474 */               this.defaultValue = Long.valueOf(TextFormat.parseUInt64(this.proto.getDefaultValue()));
/*      */               break;
/*      */             case null:
/* 1477 */               if (this.proto.getDefaultValue().equals("inf")) {
/* 1478 */                 this.defaultValue = Float.valueOf(Float.POSITIVE_INFINITY); break;
/* 1479 */               }  if (this.proto.getDefaultValue().equals("-inf")) {
/* 1480 */                 this.defaultValue = Float.valueOf(Float.NEGATIVE_INFINITY); break;
/* 1481 */               }  if (this.proto.getDefaultValue().equals("nan")) {
/* 1482 */                 this.defaultValue = Float.valueOf(Float.NaN); break;
/*      */               } 
/* 1484 */               this.defaultValue = Float.valueOf(this.proto.getDefaultValue());
/*      */               break;
/*      */             
/*      */             case null:
/* 1488 */               if (this.proto.getDefaultValue().equals("inf")) {
/* 1489 */                 this.defaultValue = Double.valueOf(Double.POSITIVE_INFINITY); break;
/* 1490 */               }  if (this.proto.getDefaultValue().equals("-inf")) {
/* 1491 */                 this.defaultValue = Double.valueOf(Double.NEGATIVE_INFINITY); break;
/* 1492 */               }  if (this.proto.getDefaultValue().equals("nan")) {
/* 1493 */                 this.defaultValue = Double.valueOf(Double.NaN); break;
/*      */               } 
/* 1495 */               this.defaultValue = Double.valueOf(this.proto.getDefaultValue());
/*      */               break;
/*      */             
/*      */             case null:
/* 1499 */               this.defaultValue = Boolean.valueOf(this.proto.getDefaultValue());
/*      */               break;
/*      */             case null:
/* 1502 */               this.defaultValue = this.proto.getDefaultValue();
/*      */               break;
/*      */             case null:
/*      */               try {
/* 1506 */                 this.defaultValue = TextFormat.unescapeBytes(this.proto.getDefaultValue());
/* 1507 */               } catch (InvalidEscapeSequenceException e) {
/* 1508 */                 throw new Descriptors.DescriptorValidationException(this, "Couldn't parse default value: " + e
/* 1509 */                     .getMessage(), e);
/*      */               } 
/*      */               break;
/*      */             case null:
/* 1513 */               this.defaultValue = this.enumType.findValueByName(this.proto.getDefaultValue());
/* 1514 */               if (this.defaultValue == null) {
/* 1515 */                 throw new Descriptors.DescriptorValidationException(this, "Unknown enum default value: \"" + this.proto
/* 1516 */                     .getDefaultValue() + '"');
/*      */               }
/*      */               break;
/*      */             case null:
/*      */             case null:
/* 1521 */               throw new Descriptors.DescriptorValidationException(this, "Message type had default value.");
/*      */           } 
/* 1523 */         } catch (NumberFormatException e) {
/* 1524 */           throw new Descriptors.DescriptorValidationException(this, "Could not parse default value: \"" + this.proto
/* 1525 */               .getDefaultValue() + '"', e);
/*      */         }
/*      */       
/*      */       }
/* 1529 */       else if (isRepeated()) {
/* 1530 */         this.defaultValue = Collections.emptyList();
/*      */       } else {
/* 1532 */         switch (getJavaType()) {
/*      */ 
/*      */           
/*      */           case ENUM:
/* 1536 */             this.defaultValue = this.enumType.getValues().get(0);
/*      */             break;
/*      */           case MESSAGE:
/* 1539 */             this.defaultValue = null;
/*      */             break;
/*      */           default:
/* 1542 */             this.defaultValue = (getJavaType()).defaultDefault;
/*      */             break;
/*      */         } 
/*      */ 
/*      */       
/*      */       } 
/* 1548 */       if (!isExtension()) {
/* 1549 */         this.file.pool.addFieldByNumber(this);
/*      */       }
/*      */       
/* 1552 */       if (this.containingType != null && this.containingType.getOptions().getMessageSetWireFormat()) {
/* 1553 */         if (isExtension()) {
/* 1554 */           if (!isOptional() || getType() != Type.MESSAGE) {
/* 1555 */             throw new Descriptors.DescriptorValidationException(this, "Extensions of MessageSets must be optional messages.");
/*      */           }
/*      */         } else {
/*      */           
/* 1559 */           throw new Descriptors.DescriptorValidationException(this, "MessageSets cannot have fields, only extensions.");
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void setProto(DescriptorProtos.FieldDescriptorProto proto) {
/* 1567 */       this.proto = proto;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public MessageLite.Builder internalMergeFrom(MessageLite.Builder to, MessageLite from) {
/* 1575 */       return ((Message.Builder)to).mergeFrom((Message)from);
/*      */     }
/*      */   }
/*      */   
/*      */   public static final class EnumDescriptor
/*      */     extends GenericDescriptor
/*      */     implements Internal.EnumLiteMap<EnumValueDescriptor>
/*      */   {
/*      */     private final int index;
/*      */     private DescriptorProtos.EnumDescriptorProto proto;
/*      */     private final String fullName;
/*      */     private final Descriptors.FileDescriptor file;
/*      */     private final Descriptors.Descriptor containingType;
/*      */     private Descriptors.EnumValueDescriptor[] values;
/*      */     
/*      */     public int getIndex() {
/* 1591 */       return this.index;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public DescriptorProtos.EnumDescriptorProto toProto() {
/* 1597 */       return this.proto;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/* 1603 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFullName() {
/* 1613 */       return this.fullName;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FileDescriptor getFile() {
/* 1619 */       return this.file;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.Descriptor getContainingType() {
/* 1624 */       return this.containingType;
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.EnumOptions getOptions() {
/* 1629 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.EnumValueDescriptor> getValues() {
/* 1634 */       return Collections.unmodifiableList(Arrays.asList(this.values));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.EnumValueDescriptor findValueByName(String name) {
/* 1644 */       Descriptors.GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
/* 1645 */       if (result != null && result instanceof Descriptors.EnumValueDescriptor) {
/* 1646 */         return (Descriptors.EnumValueDescriptor)result;
/*      */       }
/* 1648 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.EnumValueDescriptor findValueByNumber(int number) {
/* 1661 */       return (Descriptors.EnumValueDescriptor)this.file.pool.enumValuesByNumber.get(new Descriptors.DescriptorPool.DescriptorIntPair(this, number));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.EnumValueDescriptor findValueByNumberCreatingIfUnknown(int number) {
/* 1669 */       Descriptors.EnumValueDescriptor result = findValueByNumber(number);
/* 1670 */       if (result != null) {
/* 1671 */         return result;
/*      */       }
/*      */       
/* 1674 */       synchronized (this) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1704 */         Integer key = new Integer(number);
/* 1705 */         WeakReference<Descriptors.EnumValueDescriptor> reference = this.unknownValues.get(key);
/* 1706 */         if (reference != null) {
/* 1707 */           result = reference.get();
/*      */         }
/* 1709 */         if (result == null) {
/* 1710 */           result = new Descriptors.EnumValueDescriptor(this.file, this, key);
/* 1711 */           this.unknownValues.put(key, new WeakReference<>(result));
/*      */         } 
/*      */       } 
/* 1714 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     int getUnknownEnumValueDescriptorCount() {
/* 1719 */       return this.unknownValues.size();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1728 */     private final WeakHashMap<Integer, WeakReference<Descriptors.EnumValueDescriptor>> unknownValues = new WeakHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private EnumDescriptor(DescriptorProtos.EnumDescriptorProto proto, Descriptors.FileDescriptor file, Descriptors.Descriptor parent, int index) throws Descriptors.DescriptorValidationException {
/* 1737 */       this.index = index;
/* 1738 */       this.proto = proto;
/* 1739 */       this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
/* 1740 */       this.file = file;
/* 1741 */       this.containingType = parent;
/*      */       
/* 1743 */       if (proto.getValueCount() == 0)
/*      */       {
/*      */         
/* 1746 */         throw new Descriptors.DescriptorValidationException(this, "Enums must contain at least one value.");
/*      */       }
/*      */       
/* 1749 */       this.values = new Descriptors.EnumValueDescriptor[proto.getValueCount()];
/* 1750 */       for (int i = 0; i < proto.getValueCount(); i++) {
/* 1751 */         this.values[i] = new Descriptors.EnumValueDescriptor(proto.getValue(i), file, this, i);
/*      */       }
/*      */       
/* 1754 */       file.pool.addSymbol(this);
/*      */     }
/*      */ 
/*      */     
/*      */     private void setProto(DescriptorProtos.EnumDescriptorProto proto) {
/* 1759 */       this.proto = proto;
/*      */       
/* 1761 */       for (int i = 0; i < this.values.length; i++) {
/* 1762 */         this.values[i].setProto(proto.getValue(i));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static final class EnumValueDescriptor
/*      */     extends GenericDescriptor
/*      */     implements Internal.EnumLite
/*      */   {
/*      */     private final int index;
/*      */     
/*      */     private DescriptorProtos.EnumValueDescriptorProto proto;
/*      */     
/*      */     private final String fullName;
/*      */     
/*      */     private final Descriptors.FileDescriptor file;
/*      */     private final Descriptors.EnumDescriptor type;
/*      */     
/*      */     public int getIndex() {
/* 1782 */       return this.index;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public DescriptorProtos.EnumValueDescriptorProto toProto() {
/* 1788 */       return this.proto;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/* 1794 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getNumber() {
/* 1800 */       return this.proto.getNumber();
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1805 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFullName() {
/* 1815 */       return this.fullName;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FileDescriptor getFile() {
/* 1821 */       return this.file;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.EnumDescriptor getType() {
/* 1826 */       return this.type;
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.EnumValueOptions getOptions() {
/* 1831 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private EnumValueDescriptor(DescriptorProtos.EnumValueDescriptorProto proto, Descriptors.FileDescriptor file, Descriptors.EnumDescriptor parent, int index) throws Descriptors.DescriptorValidationException {
/* 1846 */       this.index = index;
/* 1847 */       this.proto = proto;
/* 1848 */       this.file = file;
/* 1849 */       this.type = parent;
/*      */       
/* 1851 */       this.fullName = parent.getFullName() + '.' + proto.getName();
/*      */       
/* 1853 */       file.pool.addSymbol(this);
/* 1854 */       file.pool.addEnumValueByNumber(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private EnumValueDescriptor(Descriptors.FileDescriptor file, Descriptors.EnumDescriptor parent, Integer number) {
/* 1860 */       String name = "UNKNOWN_ENUM_VALUE_" + parent.getName() + "_" + number;
/*      */       
/* 1862 */       DescriptorProtos.EnumValueDescriptorProto proto = DescriptorProtos.EnumValueDescriptorProto.newBuilder().setName(name).setNumber(number.intValue()).build();
/* 1863 */       this.index = -1;
/* 1864 */       this.proto = proto;
/* 1865 */       this.file = file;
/* 1866 */       this.type = parent;
/* 1867 */       this.fullName = parent.getFullName() + '.' + proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void setProto(DescriptorProtos.EnumValueDescriptorProto proto) {
/* 1874 */       this.proto = proto;
/*      */     }
/*      */   }
/*      */   
/*      */   public static final class ServiceDescriptor extends GenericDescriptor {
/*      */     private final int index;
/*      */     private DescriptorProtos.ServiceDescriptorProto proto;
/*      */     private final String fullName;
/*      */     private final Descriptors.FileDescriptor file;
/*      */     private Descriptors.MethodDescriptor[] methods;
/*      */     
/*      */     public int getIndex() {
/* 1886 */       return this.index;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public DescriptorProtos.ServiceDescriptorProto toProto() {
/* 1892 */       return this.proto;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/* 1898 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFullName() {
/* 1908 */       return this.fullName;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FileDescriptor getFile() {
/* 1914 */       return this.file;
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.ServiceOptions getOptions() {
/* 1919 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.MethodDescriptor> getMethods() {
/* 1924 */       return Collections.unmodifiableList(Arrays.asList(this.methods));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.MethodDescriptor findMethodByName(String name) {
/* 1934 */       Descriptors.GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
/* 1935 */       if (result != null && result instanceof Descriptors.MethodDescriptor) {
/* 1936 */         return (Descriptors.MethodDescriptor)result;
/*      */       }
/* 1938 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ServiceDescriptor(DescriptorProtos.ServiceDescriptorProto proto, Descriptors.FileDescriptor file, int index) throws Descriptors.DescriptorValidationException {
/* 1951 */       this.index = index;
/* 1952 */       this.proto = proto;
/* 1953 */       this.fullName = Descriptors.computeFullName(file, null, proto.getName());
/* 1954 */       this.file = file;
/*      */       
/* 1956 */       this.methods = new Descriptors.MethodDescriptor[proto.getMethodCount()];
/* 1957 */       for (int i = 0; i < proto.getMethodCount(); i++) {
/* 1958 */         this.methods[i] = new Descriptors.MethodDescriptor(proto.getMethod(i), file, this, i);
/*      */       }
/*      */       
/* 1961 */       file.pool.addSymbol(this);
/*      */     }
/*      */     
/*      */     private void crossLink() throws Descriptors.DescriptorValidationException {
/* 1965 */       for (Descriptors.MethodDescriptor method : this.methods) {
/* 1966 */         method.crossLink();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void setProto(DescriptorProtos.ServiceDescriptorProto proto) {
/* 1972 */       this.proto = proto;
/*      */       
/* 1974 */       for (int i = 0; i < this.methods.length; i++)
/* 1975 */         this.methods[i].setProto(proto.getMethod(i)); 
/*      */     } }
/*      */   
/*      */   public static final class MethodDescriptor extends GenericDescriptor {
/*      */     private final int index;
/*      */     private DescriptorProtos.MethodDescriptorProto proto;
/*      */     private final String fullName;
/*      */     private final Descriptors.FileDescriptor file;
/*      */     private final Descriptors.ServiceDescriptor service;
/*      */     private Descriptors.Descriptor inputType;
/*      */     private Descriptors.Descriptor outputType;
/*      */     
/*      */     public int getIndex() {
/* 1988 */       return this.index;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public DescriptorProtos.MethodDescriptorProto toProto() {
/* 1994 */       return this.proto;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/* 2000 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFullName() {
/* 2010 */       return this.fullName;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Descriptors.FileDescriptor getFile() {
/* 2016 */       return this.file;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.ServiceDescriptor getService() {
/* 2021 */       return this.service;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.Descriptor getInputType() {
/* 2026 */       return this.inputType;
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.Descriptor getOutputType() {
/* 2031 */       return this.outputType;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isClientStreaming() {
/* 2036 */       return this.proto.getClientStreaming();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isServerStreaming() {
/* 2041 */       return this.proto.getServerStreaming();
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.MethodOptions getOptions() {
/* 2046 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private MethodDescriptor(DescriptorProtos.MethodDescriptorProto proto, Descriptors.FileDescriptor file, Descriptors.ServiceDescriptor parent, int index) throws Descriptors.DescriptorValidationException {
/* 2065 */       this.index = index;
/* 2066 */       this.proto = proto;
/* 2067 */       this.file = file;
/* 2068 */       this.service = parent;
/*      */       
/* 2070 */       this.fullName = parent.getFullName() + '.' + proto.getName();
/*      */       
/* 2072 */       file.pool.addSymbol(this);
/*      */     }
/*      */ 
/*      */     
/*      */     private void crossLink() throws Descriptors.DescriptorValidationException {
/* 2077 */       Descriptors.GenericDescriptor input = this.file.pool.lookupSymbol(this.proto
/* 2078 */           .getInputType(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
/* 2079 */       if (!(input instanceof Descriptors.Descriptor)) {
/* 2080 */         throw new Descriptors.DescriptorValidationException(this, '"' + this.proto
/* 2081 */             .getInputType() + "\" is not a message type.");
/*      */       }
/* 2083 */       this.inputType = (Descriptors.Descriptor)input;
/*      */ 
/*      */       
/* 2086 */       Descriptors.GenericDescriptor output = this.file.pool.lookupSymbol(this.proto
/* 2087 */           .getOutputType(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
/* 2088 */       if (!(output instanceof Descriptors.Descriptor)) {
/* 2089 */         throw new Descriptors.DescriptorValidationException(this, '"' + this.proto
/* 2090 */             .getOutputType() + "\" is not a message type.");
/*      */       }
/* 2092 */       this.outputType = (Descriptors.Descriptor)output;
/*      */     }
/*      */ 
/*      */     
/*      */     private void setProto(DescriptorProtos.MethodDescriptorProto proto) {
/* 2097 */       this.proto = proto;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String computeFullName(FileDescriptor file, Descriptor parent, String name) {
/* 2105 */     if (parent != null) {
/* 2106 */       return parent.getFullName() + '.' + name;
/*      */     }
/*      */     
/* 2109 */     String packageName = file.getPackage();
/* 2110 */     if (!packageName.isEmpty()) {
/* 2111 */       return packageName + '.' + name;
/*      */     }
/*      */     
/* 2114 */     return name;
/*      */   }
/*      */ 
/*      */   
/*      */   public static abstract class GenericDescriptor
/*      */   {
/*      */     public abstract Descriptors.FileDescriptor getFile();
/*      */ 
/*      */     
/*      */     public abstract String getFullName();
/*      */ 
/*      */     
/*      */     public abstract String getName();
/*      */ 
/*      */     
/*      */     public abstract Message toProto();
/*      */     
/*      */     private GenericDescriptor() {}
/*      */   }
/*      */   
/*      */   public static class DescriptorValidationException
/*      */     extends Exception
/*      */   {
/*      */     private static final long serialVersionUID = 5750205775490483148L;
/*      */     private final String name;
/*      */     private final Message proto;
/*      */     private final String description;
/*      */     
/*      */     public String getProblemSymbolName() {
/* 2143 */       return this.name;
/*      */     }
/*      */ 
/*      */     
/*      */     public Message getProblemProto() {
/* 2148 */       return this.proto;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getDescription() {
/* 2153 */       return this.description;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private DescriptorValidationException(Descriptors.GenericDescriptor problemDescriptor, String description) {
/* 2162 */       super(problemDescriptor.getFullName() + ": " + description);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2167 */       this.name = problemDescriptor.getFullName();
/* 2168 */       this.proto = problemDescriptor.toProto();
/* 2169 */       this.description = description;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private DescriptorValidationException(Descriptors.GenericDescriptor problemDescriptor, String description, Throwable cause) {
/* 2176 */       this(problemDescriptor, description);
/* 2177 */       initCause(cause);
/*      */     }
/*      */ 
/*      */     
/*      */     private DescriptorValidationException(Descriptors.FileDescriptor problemDescriptor, String description) {
/* 2182 */       super(problemDescriptor.getName() + ": " + description);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2187 */       this.name = problemDescriptor.getName();
/* 2188 */       this.proto = problemDescriptor.toProto();
/* 2189 */       this.description = description;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class DescriptorPool
/*      */   {
/*      */     private final Set<Descriptors.FileDescriptor> dependencies;
/*      */     private boolean allowUnknownDependencies;
/*      */     private final Map<String, Descriptors.GenericDescriptor> descriptorsByName;
/*      */     private final Map<DescriptorIntPair, Descriptors.FieldDescriptor> fieldsByNumber;
/*      */     private final Map<DescriptorIntPair, Descriptors.EnumValueDescriptor> enumValuesByNumber;
/*      */     
/*      */     enum SearchFilter
/*      */     {
/* 2203 */       TYPES_ONLY,
/* 2204 */       AGGREGATES_ONLY,
/* 2205 */       ALL_SYMBOLS;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     DescriptorPool(Descriptors.FileDescriptor[] dependencies, boolean allowUnknownDependencies) {
/* 2241 */       this.descriptorsByName = new HashMap<>();
/*      */       
/* 2243 */       this.fieldsByNumber = new HashMap<>();
/*      */       
/* 2245 */       this.enumValuesByNumber = new HashMap<>(); this.dependencies = new HashSet<>(); this.allowUnknownDependencies = allowUnknownDependencies; for (int i = 0; i < dependencies.length; i++) { this.dependencies.add(dependencies[i]); importPublicDependencies(dependencies[i]); }
/*      */        for (Descriptors.FileDescriptor dependency : this.dependencies) { try { addPackage(dependency.getPackage(), dependency); }
/*      */         catch (DescriptorValidationException e)
/*      */         { throw new AssertionError(e); }
/*      */          }
/* 2250 */        } Descriptors.GenericDescriptor findSymbol(String fullName) { return findSymbol(fullName, SearchFilter.ALL_SYMBOLS); }
/*      */      private void importPublicDependencies(Descriptors.FileDescriptor file) {
/*      */       for (Descriptors.FileDescriptor dependency : file.getPublicDependencies()) {
/*      */         if (this.dependencies.add(dependency))
/*      */           importPublicDependencies(dependency); 
/*      */       } 
/*      */     }
/*      */     Descriptors.GenericDescriptor findSymbol(String fullName, SearchFilter filter) {
/* 2258 */       Descriptors.GenericDescriptor result = this.descriptorsByName.get(fullName);
/* 2259 */       if (result != null && (
/* 2260 */         filter == SearchFilter.ALL_SYMBOLS || (filter == SearchFilter.TYPES_ONLY && 
/* 2261 */         isType(result)) || (filter == SearchFilter.AGGREGATES_ONLY && 
/* 2262 */         isAggregate(result)))) {
/* 2263 */         return result;
/*      */       }
/*      */ 
/*      */       
/* 2267 */       for (Descriptors.FileDescriptor dependency : this.dependencies) {
/* 2268 */         result = dependency.pool.descriptorsByName.get(fullName);
/* 2269 */         if (result != null && (
/* 2270 */           filter == SearchFilter.ALL_SYMBOLS || (filter == SearchFilter.TYPES_ONLY && 
/* 2271 */           isType(result)) || (filter == SearchFilter.AGGREGATES_ONLY && 
/* 2272 */           isAggregate(result)))) {
/* 2273 */           return result;
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 2278 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     boolean isType(Descriptors.GenericDescriptor descriptor) {
/* 2283 */       return (descriptor instanceof Descriptors.Descriptor || descriptor instanceof Descriptors.EnumDescriptor);
/*      */     }
/*      */ 
/*      */     
/*      */     boolean isAggregate(Descriptors.GenericDescriptor descriptor) {
/* 2288 */       return (descriptor instanceof Descriptors.Descriptor || descriptor instanceof Descriptors.EnumDescriptor || descriptor instanceof PackageDescriptor || descriptor instanceof Descriptors.ServiceDescriptor);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Descriptors.GenericDescriptor lookupSymbol(String name, Descriptors.GenericDescriptor relativeTo, SearchFilter filter) throws Descriptors.DescriptorValidationException {
/*      */       Descriptors.GenericDescriptor result;
/*      */       String fullname;
/* 2308 */       if (name.startsWith(".")) {
/*      */         
/* 2310 */         fullname = name.substring(1);
/* 2311 */         result = findSymbol(fullname, filter);
/*      */       } else {
/*      */         String firstPart;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2326 */         int firstPartLength = name.indexOf('.');
/*      */         
/* 2328 */         if (firstPartLength == -1) {
/* 2329 */           firstPart = name;
/*      */         } else {
/* 2331 */           firstPart = name.substring(0, firstPartLength);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 2336 */         StringBuilder scopeToTry = new StringBuilder(relativeTo.getFullName());
/*      */ 
/*      */         
/*      */         while (true) {
/* 2340 */           int dotpos = scopeToTry.lastIndexOf(".");
/* 2341 */           if (dotpos == -1) {
/* 2342 */             fullname = name;
/* 2343 */             Descriptors.GenericDescriptor genericDescriptor = findSymbol(name, filter);
/*      */             break;
/*      */           } 
/* 2346 */           scopeToTry.setLength(dotpos + 1);
/*      */ 
/*      */           
/* 2349 */           scopeToTry.append(firstPart);
/* 2350 */           result = findSymbol(scopeToTry.toString(), SearchFilter.AGGREGATES_ONLY);
/*      */           
/* 2352 */           if (result != null) {
/* 2353 */             if (firstPartLength != -1) {
/*      */ 
/*      */ 
/*      */               
/* 2357 */               scopeToTry.setLength(dotpos + 1);
/* 2358 */               scopeToTry.append(name);
/* 2359 */               result = findSymbol(scopeToTry.toString(), filter);
/*      */             } 
/* 2361 */             fullname = scopeToTry.toString();
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2366 */           scopeToTry.setLength(dotpos);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 2371 */       if (result == null) {
/* 2372 */         if (this.allowUnknownDependencies && filter == SearchFilter.TYPES_ONLY) {
/* 2373 */           Descriptors.logger.warning("The descriptor for message type \"" + name + "\" can not be found and a placeholder is created for it");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2383 */           result = new Descriptors.Descriptor(fullname);
/*      */ 
/*      */           
/* 2386 */           this.dependencies.add(result.getFile());
/* 2387 */           return result;
/*      */         } 
/* 2389 */         throw new Descriptors.DescriptorValidationException(relativeTo, '"' + name + "\" is not defined.");
/*      */       } 
/*      */       
/* 2392 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void addSymbol(Descriptors.GenericDescriptor descriptor) throws Descriptors.DescriptorValidationException {
/* 2401 */       validateSymbolName(descriptor);
/*      */       
/* 2403 */       String fullName = descriptor.getFullName();
/*      */       
/* 2405 */       Descriptors.GenericDescriptor old = this.descriptorsByName.put(fullName, descriptor);
/* 2406 */       if (old != null) {
/* 2407 */         this.descriptorsByName.put(fullName, old);
/*      */         
/* 2409 */         if (descriptor.getFile() == old.getFile()) {
/* 2410 */           int dotpos = fullName.lastIndexOf('.');
/* 2411 */           if (dotpos == -1) {
/* 2412 */             throw new Descriptors.DescriptorValidationException(descriptor, '"' + fullName + "\" is already defined.");
/*      */           }
/*      */           
/* 2415 */           throw new Descriptors.DescriptorValidationException(descriptor, '"' + fullName
/*      */ 
/*      */               
/* 2418 */               .substring(dotpos + 1) + "\" is already defined in \"" + fullName
/*      */               
/* 2420 */               .substring(0, dotpos) + "\".");
/*      */         } 
/*      */ 
/*      */         
/* 2424 */         throw new Descriptors.DescriptorValidationException(descriptor, '"' + fullName + "\" is already defined in file \"" + old
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2429 */             .getFile().getName() + "\".");
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private static final class PackageDescriptor
/*      */       extends Descriptors.GenericDescriptor
/*      */     {
/*      */       private final String name;
/*      */       
/*      */       private final String fullName;
/*      */       private final Descriptors.FileDescriptor file;
/*      */       
/*      */       public Message toProto() {
/* 2443 */         return this.file.toProto();
/*      */       }
/*      */ 
/*      */       
/*      */       public String getName() {
/* 2448 */         return this.name;
/*      */       }
/*      */ 
/*      */       
/*      */       public String getFullName() {
/* 2453 */         return this.fullName;
/*      */       }
/*      */ 
/*      */       
/*      */       public Descriptors.FileDescriptor getFile() {
/* 2458 */         return this.file;
/*      */       }
/*      */       
/*      */       PackageDescriptor(String name, String fullName, Descriptors.FileDescriptor file) {
/* 2462 */         this.file = file;
/* 2463 */         this.fullName = fullName;
/* 2464 */         this.name = name;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void addPackage(String fullName, Descriptors.FileDescriptor file) throws Descriptors.DescriptorValidationException {
/*      */       String name;
/* 2479 */       int dotpos = fullName.lastIndexOf('.');
/*      */       
/* 2481 */       if (dotpos == -1) {
/* 2482 */         name = fullName;
/*      */       } else {
/* 2484 */         addPackage(fullName.substring(0, dotpos), file);
/* 2485 */         name = fullName.substring(dotpos + 1);
/*      */       } 
/*      */ 
/*      */       
/* 2489 */       Descriptors.GenericDescriptor old = this.descriptorsByName.put(fullName, new PackageDescriptor(name, fullName, file));
/* 2490 */       if (old != null) {
/* 2491 */         this.descriptorsByName.put(fullName, old);
/* 2492 */         if (!(old instanceof PackageDescriptor)) {
/* 2493 */           throw new Descriptors.DescriptorValidationException(file, '"' + name + "\" is already defined (as something other than a package) in file \"" + old
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2499 */               .getFile().getName() + "\".");
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private static final class DescriptorIntPair
/*      */     {
/*      */       private final Descriptors.GenericDescriptor descriptor;
/*      */       private final int number;
/*      */       
/*      */       DescriptorIntPair(Descriptors.GenericDescriptor descriptor, int number) {
/* 2511 */         this.descriptor = descriptor;
/* 2512 */         this.number = number;
/*      */       }
/*      */ 
/*      */       
/*      */       public int hashCode() {
/* 2517 */         return this.descriptor.hashCode() * 65535 + this.number;
/*      */       }
/*      */ 
/*      */       
/*      */       public boolean equals(Object obj) {
/* 2522 */         if (!(obj instanceof DescriptorIntPair)) {
/* 2523 */           return false;
/*      */         }
/* 2525 */         DescriptorIntPair other = (DescriptorIntPair)obj;
/* 2526 */         return (this.descriptor == other.descriptor && this.number == other.number);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void addFieldByNumber(Descriptors.FieldDescriptor field) throws Descriptors.DescriptorValidationException {
/* 2536 */       DescriptorIntPair key = new DescriptorIntPair(field.getContainingType(), field.getNumber());
/* 2537 */       Descriptors.FieldDescriptor old = this.fieldsByNumber.put(key, field);
/* 2538 */       if (old != null) {
/* 2539 */         this.fieldsByNumber.put(key, old);
/* 2540 */         throw new Descriptors.DescriptorValidationException(field, "Field number " + field
/*      */ 
/*      */             
/* 2543 */             .getNumber() + " has already been used in \"" + field
/*      */             
/* 2545 */             .getContainingType().getFullName() + "\" by field \"" + old
/*      */             
/* 2547 */             .getName() + "\".");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void addEnumValueByNumber(Descriptors.EnumValueDescriptor value) {
/* 2558 */       DescriptorIntPair key = new DescriptorIntPair(value.getType(), value.getNumber());
/* 2559 */       Descriptors.EnumValueDescriptor old = this.enumValuesByNumber.put(key, value);
/* 2560 */       if (old != null) {
/* 2561 */         this.enumValuesByNumber.put(key, old);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static void validateSymbolName(Descriptors.GenericDescriptor descriptor) throws Descriptors.DescriptorValidationException {
/* 2573 */       String name = descriptor.getName();
/* 2574 */       if (name.length() == 0) {
/* 2575 */         throw new Descriptors.DescriptorValidationException(descriptor, "Missing name.");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2582 */       for (int i = 0; i < name.length(); ) {
/* 2583 */         char c = name.charAt(i);
/* 2584 */         if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_' || ('0' <= c && c <= '9' && i > 0)) {
/*      */           i++;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/* 2590 */         throw new Descriptors.DescriptorValidationException(descriptor, '"' + name + "\" is not a valid identifier.");
/*      */       } 
/*      */     } }
/*      */   public static final class OneofDescriptor extends GenericDescriptor { private final int index; private DescriptorProtos.OneofDescriptorProto proto; private final String fullName;
/*      */     private final Descriptors.FileDescriptor file;
/*      */     private Descriptors.Descriptor containingType;
/*      */     private int fieldCount;
/*      */     private Descriptors.FieldDescriptor[] fields;
/*      */     
/*      */     public int getIndex() {
/* 2600 */       return this.index;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getName() {
/* 2605 */       return this.proto.getName();
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.FileDescriptor getFile() {
/* 2610 */       return this.file;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getFullName() {
/* 2615 */       return this.fullName;
/*      */     }
/*      */     
/*      */     public Descriptors.Descriptor getContainingType() {
/* 2619 */       return this.containingType;
/*      */     }
/*      */     
/*      */     public int getFieldCount() {
/* 2623 */       return this.fieldCount;
/*      */     }
/*      */     
/*      */     public DescriptorProtos.OneofOptions getOptions() {
/* 2627 */       return this.proto.getOptions();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Descriptors.FieldDescriptor> getFields() {
/* 2632 */       return Collections.unmodifiableList(Arrays.asList(this.fields));
/*      */     }
/*      */     
/*      */     public Descriptors.FieldDescriptor getField(int index) {
/* 2636 */       return this.fields[index];
/*      */     }
/*      */ 
/*      */     
/*      */     public DescriptorProtos.OneofDescriptorProto toProto() {
/* 2641 */       return this.proto;
/*      */     }
/*      */     
/*      */     private void setProto(DescriptorProtos.OneofDescriptorProto proto) {
/* 2645 */       this.proto = proto;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private OneofDescriptor(DescriptorProtos.OneofDescriptorProto proto, Descriptors.FileDescriptor file, Descriptors.Descriptor parent, int index) throws Descriptors.DescriptorValidationException {
/* 2654 */       this.proto = proto;
/* 2655 */       this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
/* 2656 */       this.file = file;
/* 2657 */       this.index = index;
/*      */       
/* 2659 */       this.containingType = parent;
/* 2660 */       this.fieldCount = 0;
/*      */     } }
/*      */ 
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Descriptors.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */