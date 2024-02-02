/*       */ package com.mysql.cj.x.protobuf;
/*       */ import com.google.protobuf.CodedInputStream;
/*       */ import com.google.protobuf.Descriptors;
/*       */ import com.google.protobuf.ExtensionRegistryLite;
/*       */ import com.google.protobuf.GeneratedMessageV3;
/*       */ import com.google.protobuf.Message;
/*       */ 
/*       */ public final class MysqlxCrud {
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Column_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Column_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Projection_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Projection_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Collection_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Collection_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Limit_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Limit_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_LimitExpr_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Order_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Order_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Find_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Find_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Update_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Update_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Delete_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Delete_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_CreateView_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_ModifyView_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable;
/*       */   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_DropView_descriptor;
/*       */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_DropView_fieldAccessorTable;
/*       */   private static Descriptors.FileDescriptor descriptor;
/*       */   
/*       */   public static void registerAllExtensions(ExtensionRegistryLite registry) {}
/*       */   
/*       */   public static void registerAllExtensions(ExtensionRegistry registry) {
/*    44 */     registerAllExtensions((ExtensionRegistryLite)registry);
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public enum DataModel
/*       */     implements ProtocolMessageEnum
/*       */   {
/*    60 */     DOCUMENT(1),
/*       */ 
/*       */ 
/*       */     
/*    64 */     TABLE(2);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DOCUMENT_VALUE = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int TABLE_VALUE = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   108 */     private static final Internal.EnumLiteMap<DataModel> internalValueMap = new Internal.EnumLiteMap<DataModel>()
/*       */       {
/*       */         public MysqlxCrud.DataModel findValueByNumber(int number) {
/*   111 */           return MysqlxCrud.DataModel.forNumber(number);
/*       */         }
/*       */       };
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   128 */     private static final DataModel[] VALUES = values(); private final int value; public final int getNumber() { return this.value; }
/*       */     public static DataModel forNumber(int value) { switch (value) {
/*       */         case 1:
/*       */           return DOCUMENT;
/*       */         case 2:
/*       */           return TABLE;
/*       */       } 
/*       */       return null; }
/*       */     public static Internal.EnumLiteMap<DataModel> internalGetValueMap() { return internalValueMap; }
/*       */     static {  }
/*       */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*       */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*       */     public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.getDescriptor().getEnumTypes().get(0); }
/*       */     DataModel(int value) {
/*   142 */       this.value = value;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public enum ViewAlgorithm
/*       */     implements ProtocolMessageEnum
/*       */   {
/*   165 */     UNDEFINED(1),
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   174 */     MERGE(2),
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   182 */     TEMPTABLE(3);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int UNDEFINED_VALUE = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int MERGE_VALUE = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int TEMPTABLE_VALUE = 3;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   244 */     private static final Internal.EnumLiteMap<ViewAlgorithm> internalValueMap = new Internal.EnumLiteMap<ViewAlgorithm>()
/*       */       {
/*       */         public MysqlxCrud.ViewAlgorithm findValueByNumber(int number) {
/*   247 */           return MysqlxCrud.ViewAlgorithm.forNumber(number);
/*       */         }
/*       */       };
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   264 */     private static final ViewAlgorithm[] VALUES = values(); private final int value; public final int getNumber() { return this.value; }
/*       */     public static ViewAlgorithm forNumber(int value) { switch (value) {
/*       */         case 1:
/*       */           return UNDEFINED;
/*       */         case 2:
/*       */           return MERGE;
/*       */         case 3:
/*       */           return TEMPTABLE;
/*       */       }  return null; }
/*       */     public static Internal.EnumLiteMap<ViewAlgorithm> internalGetValueMap() { return internalValueMap; }
/*       */     static {  }
/*       */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*       */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*       */     public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.getDescriptor().getEnumTypes().get(1); }
/*   278 */     ViewAlgorithm(int value) { this.value = value; }
/*       */   
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public enum ViewSqlSecurity
/*       */     implements ProtocolMessageEnum
/*       */   {
/*   303 */     INVOKER(1),
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   311 */     DEFINER(2);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int INVOKER_VALUE = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DEFINER_VALUE = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   363 */     private static final Internal.EnumLiteMap<ViewSqlSecurity> internalValueMap = new Internal.EnumLiteMap<ViewSqlSecurity>()
/*       */       {
/*       */         public MysqlxCrud.ViewSqlSecurity findValueByNumber(int number) {
/*   366 */           return MysqlxCrud.ViewSqlSecurity.forNumber(number);
/*       */         }
/*       */       };
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   383 */     private static final ViewSqlSecurity[] VALUES = values(); private final int value; public final int getNumber() { return this.value; }
/*       */     public static ViewSqlSecurity forNumber(int value) { switch (value) {
/*       */         case 1:
/*       */           return INVOKER;
/*       */         case 2:
/*       */           return DEFINER;
/*       */       } 
/*       */       return null; }
/*       */     public static Internal.EnumLiteMap<ViewSqlSecurity> internalGetValueMap() { return internalValueMap; }
/*       */     static {  }
/*       */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*       */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*       */     public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.getDescriptor().getEnumTypes().get(2); }
/*       */     ViewSqlSecurity(int value) {
/*   397 */       this.value = value;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public enum ViewCheckOption
/*       */     implements ProtocolMessageEnum
/*       */   {
/*   421 */     LOCAL(1),
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   430 */     CASCADED(2);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LOCAL_VALUE = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int CASCADED_VALUE = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   483 */     private static final Internal.EnumLiteMap<ViewCheckOption> internalValueMap = new Internal.EnumLiteMap<ViewCheckOption>()
/*       */       {
/*       */         public MysqlxCrud.ViewCheckOption findValueByNumber(int number) {
/*   486 */           return MysqlxCrud.ViewCheckOption.forNumber(number);
/*       */         }
/*       */       };
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*   503 */     private static final ViewCheckOption[] VALUES = values(); private final int value; public final int getNumber() { return this.value; }
/*       */     public static ViewCheckOption forNumber(int value) { switch (value) {
/*       */         case 1:
/*       */           return LOCAL;
/*       */         case 2:
/*       */           return CASCADED;
/*       */       } 
/*       */       return null; }
/*       */     public static Internal.EnumLiteMap<ViewCheckOption> internalGetValueMap() { return internalValueMap; }
/*       */     static {  }
/*       */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*       */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*       */     public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.getDescriptor().getEnumTypes().get(3); }
/*       */     ViewCheckOption(int value) {
/*   517 */       this.value = value;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface ColumnOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasName();
/*       */ 
/*       */ 
/*       */     
/*       */     String getName();
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getNameBytes();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasAlias();
/*       */ 
/*       */ 
/*       */     
/*       */     String getAlias();
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getAliasBytes();
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxExpr.DocumentPathItem> getDocumentPathList();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.DocumentPathItem getDocumentPath(int param1Int);
/*       */ 
/*       */ 
/*       */     
/*       */     int getDocumentPathCount();
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int param1Int);
/*       */   }
/*       */ 
/*       */   
/*       */   public static final class Column
/*       */     extends GeneratedMessageV3
/*       */     implements ColumnOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */     
/*       */     private int bitField0_;
/*       */     
/*       */     public static final int NAME_FIELD_NUMBER = 1;
/*       */     
/*       */     private volatile Object name_;
/*       */     
/*       */     public static final int ALIAS_FIELD_NUMBER = 2;
/*       */     
/*       */     private volatile Object alias_;
/*       */     
/*       */     public static final int DOCUMENT_PATH_FIELD_NUMBER = 3;
/*       */     
/*       */     private List<MysqlxExpr.DocumentPathItem> documentPath_;
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */     
/*       */     private Column(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*   595 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*   816 */       this.memoizedIsInitialized = -1; } private Column() { this.memoizedIsInitialized = -1; this.name_ = ""; this.alias_ = ""; this.documentPath_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Column(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Column(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.name_ = bs; continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x2; this.alias_ = bs; continue;case 26: if ((mutable_bitField0_ & 0x4) == 0) { this.documentPath_ = new ArrayList<>(); mutable_bitField0_ |= 0x4; }  this.documentPath_.add(input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x4) != 0) this.documentPath_ = Collections.unmodifiableList(this.documentPath_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable.ensureFieldAccessorsInitialized(Column.class, Builder.class); } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public boolean hasAlias() { return ((this.bitField0_ & 0x2) != 0); } public String getAlias() { Object ref = this.alias_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.alias_ = s;  return s; } public ByteString getAliasBytes() { Object ref = this.alias_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.alias_ = b; return b; }  return (ByteString)ref; } public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() { return this.documentPath_; } public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() { return (List)this.documentPath_; } public int getDocumentPathCount() { return this.documentPath_.size(); }
/*       */     public MysqlxExpr.DocumentPathItem getDocumentPath(int index) { return this.documentPath_.get(index); }
/*       */     public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) { return this.documentPath_.get(index); }
/*   819 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*   820 */       if (isInitialized == 1) return true; 
/*   821 */       if (isInitialized == 0) return false;
/*       */       
/*   823 */       for (int i = 0; i < getDocumentPathCount(); i++) {
/*   824 */         if (!getDocumentPath(i).isInitialized()) {
/*   825 */           this.memoizedIsInitialized = 0;
/*   826 */           return false;
/*       */         } 
/*       */       } 
/*   829 */       this.memoizedIsInitialized = 1;
/*   830 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*   836 */       if ((this.bitField0_ & 0x1) != 0) {
/*   837 */         GeneratedMessageV3.writeString(output, 1, this.name_);
/*       */       }
/*   839 */       if ((this.bitField0_ & 0x2) != 0) {
/*   840 */         GeneratedMessageV3.writeString(output, 2, this.alias_);
/*       */       }
/*   842 */       for (int i = 0; i < this.documentPath_.size(); i++) {
/*   843 */         output.writeMessage(3, (MessageLite)this.documentPath_.get(i));
/*       */       }
/*   845 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*   850 */       int size = this.memoizedSize;
/*   851 */       if (size != -1) return size;
/*       */       
/*   853 */       size = 0;
/*   854 */       if ((this.bitField0_ & 0x1) != 0) {
/*   855 */         size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*       */       }
/*   857 */       if ((this.bitField0_ & 0x2) != 0) {
/*   858 */         size += GeneratedMessageV3.computeStringSize(2, this.alias_);
/*       */       }
/*   860 */       for (int i = 0; i < this.documentPath_.size(); i++) {
/*   861 */         size += 
/*   862 */           CodedOutputStream.computeMessageSize(3, (MessageLite)this.documentPath_.get(i));
/*       */       }
/*   864 */       size += this.unknownFields.getSerializedSize();
/*   865 */       this.memoizedSize = size;
/*   866 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*   871 */       if (obj == this) {
/*   872 */         return true;
/*       */       }
/*   874 */       if (!(obj instanceof Column)) {
/*   875 */         return super.equals(obj);
/*       */       }
/*   877 */       Column other = (Column)obj;
/*       */       
/*   879 */       if (hasName() != other.hasName()) return false; 
/*   880 */       if (hasName() && 
/*       */         
/*   882 */         !getName().equals(other.getName())) return false;
/*       */       
/*   884 */       if (hasAlias() != other.hasAlias()) return false; 
/*   885 */       if (hasAlias() && 
/*       */         
/*   887 */         !getAlias().equals(other.getAlias())) return false;
/*       */ 
/*       */       
/*   890 */       if (!getDocumentPathList().equals(other.getDocumentPathList())) return false; 
/*   891 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*   892 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*   897 */       if (this.memoizedHashCode != 0) {
/*   898 */         return this.memoizedHashCode;
/*       */       }
/*   900 */       int hash = 41;
/*   901 */       hash = 19 * hash + getDescriptor().hashCode();
/*   902 */       if (hasName()) {
/*   903 */         hash = 37 * hash + 1;
/*   904 */         hash = 53 * hash + getName().hashCode();
/*       */       } 
/*   906 */       if (hasAlias()) {
/*   907 */         hash = 37 * hash + 2;
/*   908 */         hash = 53 * hash + getAlias().hashCode();
/*       */       } 
/*   910 */       if (getDocumentPathCount() > 0) {
/*   911 */         hash = 37 * hash + 3;
/*   912 */         hash = 53 * hash + getDocumentPathList().hashCode();
/*       */       } 
/*   914 */       hash = 29 * hash + this.unknownFields.hashCode();
/*   915 */       this.memoizedHashCode = hash;
/*   916 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Column parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*   922 */       return (Column)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Column parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*   928 */       return (Column)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Column parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*   933 */       return (Column)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Column parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*   939 */       return (Column)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Column parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*   943 */       return (Column)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Column parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*   949 */       return (Column)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Column parseFrom(InputStream input) throws IOException {
/*   953 */       return 
/*   954 */         (Column)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Column parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*   960 */       return 
/*   961 */         (Column)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Column parseDelimitedFrom(InputStream input) throws IOException {
/*   965 */       return 
/*   966 */         (Column)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Column parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*   972 */       return 
/*   973 */         (Column)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Column parseFrom(CodedInputStream input) throws IOException {
/*   978 */       return 
/*   979 */         (Column)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Column parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*   985 */       return 
/*   986 */         (Column)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*   990 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*   992 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Column prototype) {
/*   995 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*   999 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  1000 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  1006 */       Builder builder = new Builder(parent);
/*  1007 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.ColumnOrBuilder {
/*       */       private int bitField0_;
/*       */       private Object name_;
/*       */       private Object alias_;
/*       */       private List<MysqlxExpr.DocumentPathItem> documentPath_;
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> documentPathBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  1018 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  1024 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable
/*  1025 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Column.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  1223 */         this.name_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  1307 */         this.alias_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  1391 */         this
/*  1392 */           .documentPath_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.alias_ = ""; this.documentPath_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Column.alwaysUseFieldBuilders) getDocumentPathFieldBuilder();  } public Builder clear() { super.clear(); this.name_ = ""; this.bitField0_ &= 0xFFFFFFFE; this.alias_ = ""; this.bitField0_ &= 0xFFFFFFFD; if (this.documentPathBuilder_ == null) { this.documentPath_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; } else { this.documentPathBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor; } public MysqlxCrud.Column getDefaultInstanceForType() { return MysqlxCrud.Column.getDefaultInstance(); } public MysqlxCrud.Column build() { MysqlxCrud.Column result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Column buildPartial() { MysqlxCrud.Column result = new MysqlxCrud.Column(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.name_ = this.name_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.alias_ = this.alias_; if (this.documentPathBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0) { this.documentPath_ = Collections.unmodifiableList(this.documentPath_); this.bitField0_ &= 0xFFFFFFFB; }  result.documentPath_ = this.documentPath_; } else { result.documentPath_ = this.documentPathBuilder_.build(); }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Column) return mergeFrom((MysqlxCrud.Column)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Column other) { if (other == MysqlxCrud.Column.getDefaultInstance()) return this;  if (other.hasName()) { this.bitField0_ |= 0x1; this.name_ = other.name_; onChanged(); }  if (other.hasAlias()) { this.bitField0_ |= 0x2; this.alias_ = other.alias_; onChanged(); }  if (this.documentPathBuilder_ == null) { if (!other.documentPath_.isEmpty()) { if (this.documentPath_.isEmpty()) { this.documentPath_ = other.documentPath_; this.bitField0_ &= 0xFFFFFFFB; } else { ensureDocumentPathIsMutable(); this.documentPath_.addAll(other.documentPath_); }  onChanged(); }  } else if (!other.documentPath_.isEmpty()) { if (this.documentPathBuilder_.isEmpty()) { this.documentPathBuilder_.dispose(); this.documentPathBuilder_ = null; this.documentPath_ = other.documentPath_; this.bitField0_ &= 0xFFFFFFFB; this.documentPathBuilder_ = MysqlxCrud.Column.alwaysUseFieldBuilders ? getDocumentPathFieldBuilder() : null; } else { this.documentPathBuilder_.addAllMessages(other.documentPath_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getDocumentPathCount(); i++) { if (!getDocumentPath(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Column parsedMessage = null; try { parsedMessage = (MysqlxCrud.Column)MysqlxCrud.Column.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Column)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; }  return (String)ref; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; } public Builder clearName() { this.bitField0_ &= 0xFFFFFFFE; this.name_ = MysqlxCrud.Column.getDefaultInstance().getName(); onChanged(); return this; } public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; } public boolean hasAlias() { return ((this.bitField0_ & 0x2) != 0); } public String getAlias() { Object ref = this.alias_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.alias_ = s;  return s; }  return (String)ref; } public ByteString getAliasBytes() { Object ref = this.alias_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.alias_ = b; return b; }  return (ByteString)ref; } public Builder setAlias(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.alias_ = value; onChanged(); return this; } public Builder clearAlias() { this.bitField0_ &= 0xFFFFFFFD; this.alias_ = MysqlxCrud.Column.getDefaultInstance().getAlias(); onChanged(); return this; }
/*       */       public Builder setAliasBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.alias_ = value; onChanged(); return this; }
/*  1394 */       private void ensureDocumentPathIsMutable() { if ((this.bitField0_ & 0x4) == 0) {
/*  1395 */           this.documentPath_ = new ArrayList<>(this.documentPath_);
/*  1396 */           this.bitField0_ |= 0x4;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
/*  1407 */         if (this.documentPathBuilder_ == null) {
/*  1408 */           return Collections.unmodifiableList(this.documentPath_);
/*       */         }
/*  1410 */         return this.documentPathBuilder_.getMessageList();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getDocumentPathCount() {
/*  1417 */         if (this.documentPathBuilder_ == null) {
/*  1418 */           return this.documentPath_.size();
/*       */         }
/*  1420 */         return this.documentPathBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
/*  1427 */         if (this.documentPathBuilder_ == null) {
/*  1428 */           return this.documentPath_.get(index);
/*       */         }
/*  1430 */         return (MysqlxExpr.DocumentPathItem)this.documentPathBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
/*  1438 */         if (this.documentPathBuilder_ == null) {
/*  1439 */           if (value == null) {
/*  1440 */             throw new NullPointerException();
/*       */           }
/*  1442 */           ensureDocumentPathIsMutable();
/*  1443 */           this.documentPath_.set(index, value);
/*  1444 */           onChanged();
/*       */         } else {
/*  1446 */           this.documentPathBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/*  1448 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
/*  1455 */         if (this.documentPathBuilder_ == null) {
/*  1456 */           ensureDocumentPathIsMutable();
/*  1457 */           this.documentPath_.set(index, builderForValue.build());
/*  1458 */           onChanged();
/*       */         } else {
/*  1460 */           this.documentPathBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  1462 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addDocumentPath(MysqlxExpr.DocumentPathItem value) {
/*  1468 */         if (this.documentPathBuilder_ == null) {
/*  1469 */           if (value == null) {
/*  1470 */             throw new NullPointerException();
/*       */           }
/*  1472 */           ensureDocumentPathIsMutable();
/*  1473 */           this.documentPath_.add(value);
/*  1474 */           onChanged();
/*       */         } else {
/*  1476 */           this.documentPathBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/*  1478 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
/*  1485 */         if (this.documentPathBuilder_ == null) {
/*  1486 */           if (value == null) {
/*  1487 */             throw new NullPointerException();
/*       */           }
/*  1489 */           ensureDocumentPathIsMutable();
/*  1490 */           this.documentPath_.add(index, value);
/*  1491 */           onChanged();
/*       */         } else {
/*  1493 */           this.documentPathBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/*  1495 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addDocumentPath(MysqlxExpr.DocumentPathItem.Builder builderForValue) {
/*  1502 */         if (this.documentPathBuilder_ == null) {
/*  1503 */           ensureDocumentPathIsMutable();
/*  1504 */           this.documentPath_.add(builderForValue.build());
/*  1505 */           onChanged();
/*       */         } else {
/*  1507 */           this.documentPathBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  1509 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
/*  1516 */         if (this.documentPathBuilder_ == null) {
/*  1517 */           ensureDocumentPathIsMutable();
/*  1518 */           this.documentPath_.add(index, builderForValue.build());
/*  1519 */           onChanged();
/*       */         } else {
/*  1521 */           this.documentPathBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/*  1523 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllDocumentPath(Iterable<? extends MysqlxExpr.DocumentPathItem> values) {
/*  1530 */         if (this.documentPathBuilder_ == null) {
/*  1531 */           ensureDocumentPathIsMutable();
/*  1532 */           AbstractMessageLite.Builder.addAll(values, this.documentPath_);
/*       */           
/*  1534 */           onChanged();
/*       */         } else {
/*  1536 */           this.documentPathBuilder_.addAllMessages(values);
/*       */         } 
/*  1538 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearDocumentPath() {
/*  1544 */         if (this.documentPathBuilder_ == null) {
/*  1545 */           this.documentPath_ = Collections.emptyList();
/*  1546 */           this.bitField0_ &= 0xFFFFFFFB;
/*  1547 */           onChanged();
/*       */         } else {
/*  1549 */           this.documentPathBuilder_.clear();
/*       */         } 
/*  1551 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeDocumentPath(int index) {
/*  1557 */         if (this.documentPathBuilder_ == null) {
/*  1558 */           ensureDocumentPathIsMutable();
/*  1559 */           this.documentPath_.remove(index);
/*  1560 */           onChanged();
/*       */         } else {
/*  1562 */           this.documentPathBuilder_.remove(index);
/*       */         } 
/*  1564 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.DocumentPathItem.Builder getDocumentPathBuilder(int index) {
/*  1571 */         return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
/*  1578 */         if (this.documentPathBuilder_ == null)
/*  1579 */           return this.documentPath_.get(index); 
/*  1580 */         return (MysqlxExpr.DocumentPathItemOrBuilder)this.documentPathBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
/*  1588 */         if (this.documentPathBuilder_ != null) {
/*  1589 */           return this.documentPathBuilder_.getMessageOrBuilderList();
/*       */         }
/*  1591 */         return Collections.unmodifiableList((List)this.documentPath_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder() {
/*  1598 */         return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder(
/*  1599 */             (AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder(int index) {
/*  1606 */         return (MysqlxExpr.DocumentPathItem.Builder)getDocumentPathFieldBuilder().addBuilder(index, 
/*  1607 */             (AbstractMessage)MysqlxExpr.DocumentPathItem.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxExpr.DocumentPathItem.Builder> getDocumentPathBuilderList() {
/*  1614 */         return getDocumentPathFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() {
/*  1619 */         if (this.documentPathBuilder_ == null) {
/*  1620 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/*  1625 */             .documentPathBuilder_ = new RepeatedFieldBuilderV3(this.documentPath_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  1626 */           this.documentPath_ = null;
/*       */         } 
/*  1628 */         return this.documentPathBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  1633 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  1639 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  1649 */     private static final Column DEFAULT_INSTANCE = new Column();
/*       */ 
/*       */     
/*       */     public static Column getDefaultInstance() {
/*  1653 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  1657 */     public static final Parser<Column> PARSER = (Parser<Column>)new AbstractParser<Column>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Column parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  1663 */           return new MysqlxCrud.Column(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Column> parser() {
/*  1668 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Column> getParserForType() {
/*  1673 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Column getDefaultInstanceForType() {
/*  1678 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface ProjectionOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasSource();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getSource();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getSourceOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasAlias();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getAlias();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getAliasBytes();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Projection
/*       */     extends GeneratedMessageV3
/*       */     implements ProjectionOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int SOURCE_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr source_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ALIAS_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */     
/*       */     private volatile Object alias_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private Projection(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  1759 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  1946 */       this.memoizedIsInitialized = -1; } private Projection() { this.memoizedIsInitialized = -1; this.alias_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Projection(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Projection(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxExpr.Expr.Builder subBuilder; ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: subBuilder = null; if ((this.bitField0_ & 0x1) != 0) subBuilder = this.source_.toBuilder();  this.source_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.source_); this.source_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x2; this.alias_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable.ensureFieldAccessorsInitialized(Projection.class, Builder.class); } public boolean hasSource() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.Expr getSource() { return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_; } public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() { return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_; } public boolean hasAlias() { return ((this.bitField0_ & 0x2) != 0); }
/*       */     public String getAlias() { Object ref = this.alias_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.alias_ = s;  return s; }
/*       */     public ByteString getAliasBytes() { Object ref = this.alias_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.alias_ = b; return b; }  return (ByteString)ref; }
/*  1949 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  1950 */       if (isInitialized == 1) return true; 
/*  1951 */       if (isInitialized == 0) return false;
/*       */       
/*  1953 */       if (!hasSource()) {
/*  1954 */         this.memoizedIsInitialized = 0;
/*  1955 */         return false;
/*       */       } 
/*  1957 */       if (!getSource().isInitialized()) {
/*  1958 */         this.memoizedIsInitialized = 0;
/*  1959 */         return false;
/*       */       } 
/*  1961 */       this.memoizedIsInitialized = 1;
/*  1962 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  1968 */       if ((this.bitField0_ & 0x1) != 0) {
/*  1969 */         output.writeMessage(1, (MessageLite)getSource());
/*       */       }
/*  1971 */       if ((this.bitField0_ & 0x2) != 0) {
/*  1972 */         GeneratedMessageV3.writeString(output, 2, this.alias_);
/*       */       }
/*  1974 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  1979 */       int size = this.memoizedSize;
/*  1980 */       if (size != -1) return size;
/*       */       
/*  1982 */       size = 0;
/*  1983 */       if ((this.bitField0_ & 0x1) != 0) {
/*  1984 */         size += 
/*  1985 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getSource());
/*       */       }
/*  1987 */       if ((this.bitField0_ & 0x2) != 0) {
/*  1988 */         size += GeneratedMessageV3.computeStringSize(2, this.alias_);
/*       */       }
/*  1990 */       size += this.unknownFields.getSerializedSize();
/*  1991 */       this.memoizedSize = size;
/*  1992 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  1997 */       if (obj == this) {
/*  1998 */         return true;
/*       */       }
/*  2000 */       if (!(obj instanceof Projection)) {
/*  2001 */         return super.equals(obj);
/*       */       }
/*  2003 */       Projection other = (Projection)obj;
/*       */       
/*  2005 */       if (hasSource() != other.hasSource()) return false; 
/*  2006 */       if (hasSource() && 
/*       */         
/*  2008 */         !getSource().equals(other.getSource())) return false;
/*       */       
/*  2010 */       if (hasAlias() != other.hasAlias()) return false; 
/*  2011 */       if (hasAlias() && 
/*       */         
/*  2013 */         !getAlias().equals(other.getAlias())) return false;
/*       */       
/*  2015 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  2016 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  2021 */       if (this.memoizedHashCode != 0) {
/*  2022 */         return this.memoizedHashCode;
/*       */       }
/*  2024 */       int hash = 41;
/*  2025 */       hash = 19 * hash + getDescriptor().hashCode();
/*  2026 */       if (hasSource()) {
/*  2027 */         hash = 37 * hash + 1;
/*  2028 */         hash = 53 * hash + getSource().hashCode();
/*       */       } 
/*  2030 */       if (hasAlias()) {
/*  2031 */         hash = 37 * hash + 2;
/*  2032 */         hash = 53 * hash + getAlias().hashCode();
/*       */       } 
/*  2034 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  2035 */       this.memoizedHashCode = hash;
/*  2036 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  2042 */       return (Projection)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2048 */       return (Projection)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  2053 */       return (Projection)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2059 */       return (Projection)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Projection parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  2063 */       return (Projection)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2069 */       return (Projection)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Projection parseFrom(InputStream input) throws IOException {
/*  2073 */       return 
/*  2074 */         (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  2080 */       return 
/*  2081 */         (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Projection parseDelimitedFrom(InputStream input) throws IOException {
/*  2085 */       return 
/*  2086 */         (Projection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Projection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  2092 */       return 
/*  2093 */         (Projection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(CodedInputStream input) throws IOException {
/*  2098 */       return 
/*  2099 */         (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Projection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  2105 */       return 
/*  2106 */         (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  2110 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  2112 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Projection prototype) {
/*  2115 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  2119 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  2120 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  2126 */       Builder builder = new Builder(parent);
/*  2127 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.ProjectionOrBuilder {
/*       */       private int bitField0_;
/*       */       private MysqlxExpr.Expr source_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> sourceBuilder_;
/*       */       private Object alias_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  2138 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  2144 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable
/*  2145 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Projection.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  2474 */         this.alias_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.alias_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Projection.alwaysUseFieldBuilders) getSourceFieldBuilder();  } public Builder clear() { super.clear(); if (this.sourceBuilder_ == null) { this.source_ = null; } else { this.sourceBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.alias_ = ""; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor; } public MysqlxCrud.Projection getDefaultInstanceForType() { return MysqlxCrud.Projection.getDefaultInstance(); } public MysqlxCrud.Projection build() { MysqlxCrud.Projection result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Projection buildPartial() { MysqlxCrud.Projection result = new MysqlxCrud.Projection(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.sourceBuilder_ == null) { result.source_ = this.source_; } else { result.source_ = (MysqlxExpr.Expr)this.sourceBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.alias_ = this.alias_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Projection) return mergeFrom((MysqlxCrud.Projection)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Projection other) { if (other == MysqlxCrud.Projection.getDefaultInstance()) return this;  if (other.hasSource()) mergeSource(other.getSource());  if (other.hasAlias()) { this.bitField0_ |= 0x2; this.alias_ = other.alias_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasSource()) return false;  if (!getSource().isInitialized()) return false;  return true; }
/*       */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Projection parsedMessage = null; try { parsedMessage = (MysqlxCrud.Projection)MysqlxCrud.Projection.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Projection)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*       */       public boolean hasSource() { return ((this.bitField0_ & 0x1) != 0); }
/*       */       public MysqlxExpr.Expr getSource() { if (this.sourceBuilder_ == null) return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;  return (MysqlxExpr.Expr)this.sourceBuilder_.getMessage(); }
/*       */       public Builder setSource(MysqlxExpr.Expr value) { if (this.sourceBuilder_ == null) { if (value == null) throw new NullPointerException();  this.source_ = value; onChanged(); } else { this.sourceBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder setSource(MysqlxExpr.Expr.Builder builderForValue) { if (this.sourceBuilder_ == null) { this.source_ = builderForValue.build(); onChanged(); } else { this.sourceBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder mergeSource(MysqlxExpr.Expr value) { if (this.sourceBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.Expr.getDefaultInstance()) { this.source_ = MysqlxExpr.Expr.newBuilder(this.source_).mergeFrom(value).buildPartial(); } else { this.source_ = value; }  onChanged(); } else { this.sourceBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder clearSource() { if (this.sourceBuilder_ == null) { this.source_ = null; onChanged(); } else { this.sourceBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; }
/*       */       public MysqlxExpr.Expr.Builder getSourceBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxExpr.Expr.Builder)getSourceFieldBuilder().getBuilder(); }
/*       */       public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() { if (this.sourceBuilder_ != null) return (MysqlxExpr.ExprOrBuilder)this.sourceBuilder_.getMessageOrBuilder();  return (this.source_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.source_; }
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getSourceFieldBuilder() { if (this.sourceBuilder_ == null) { this.sourceBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getSource(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.source_ = null; }  return this.sourceBuilder_; }
/*  2485 */       public boolean hasAlias() { return ((this.bitField0_ & 0x2) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getAlias() {
/*  2497 */         Object ref = this.alias_;
/*  2498 */         if (!(ref instanceof String)) {
/*  2499 */           ByteString bs = (ByteString)ref;
/*       */           
/*  2501 */           String s = bs.toStringUtf8();
/*  2502 */           if (bs.isValidUtf8()) {
/*  2503 */             this.alias_ = s;
/*       */           }
/*  2505 */           return s;
/*       */         } 
/*  2507 */         return (String)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getAliasBytes() {
/*  2521 */         Object ref = this.alias_;
/*  2522 */         if (ref instanceof String) {
/*       */           
/*  2524 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*       */           
/*  2526 */           this.alias_ = b;
/*  2527 */           return b;
/*       */         } 
/*  2529 */         return (ByteString)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setAlias(String value) {
/*  2544 */         if (value == null) {
/*  2545 */           throw new NullPointerException();
/*       */         }
/*  2547 */         this.bitField0_ |= 0x2;
/*  2548 */         this.alias_ = value;
/*  2549 */         onChanged();
/*  2550 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearAlias() {
/*  2562 */         this.bitField0_ &= 0xFFFFFFFD;
/*  2563 */         this.alias_ = MysqlxCrud.Projection.getDefaultInstance().getAlias();
/*  2564 */         onChanged();
/*  2565 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setAliasBytes(ByteString value) {
/*  2579 */         if (value == null) {
/*  2580 */           throw new NullPointerException();
/*       */         }
/*  2582 */         this.bitField0_ |= 0x2;
/*  2583 */         this.alias_ = value;
/*  2584 */         onChanged();
/*  2585 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  2590 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  2596 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  2606 */     private static final Projection DEFAULT_INSTANCE = new Projection();
/*       */ 
/*       */     
/*       */     public static Projection getDefaultInstance() {
/*  2610 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  2614 */     public static final Parser<Projection> PARSER = (Parser<Projection>)new AbstractParser<Projection>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Projection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  2620 */           return new MysqlxCrud.Projection(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Projection> parser() {
/*  2625 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Projection> getParserForType() {
/*  2630 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Projection getDefaultInstanceForType() {
/*  2635 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface CollectionOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasName();
/*       */ 
/*       */ 
/*       */     
/*       */     String getName();
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getNameBytes();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasSchema();
/*       */ 
/*       */ 
/*       */     
/*       */     String getSchema();
/*       */ 
/*       */     
/*       */     ByteString getSchemaBytes();
/*       */   }
/*       */ 
/*       */   
/*       */   public static final class Collection
/*       */     extends GeneratedMessageV3
/*       */     implements CollectionOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */     
/*       */     private int bitField0_;
/*       */     
/*       */     public static final int NAME_FIELD_NUMBER = 1;
/*       */     
/*       */     private volatile Object name_;
/*       */     
/*       */     public static final int SCHEMA_FIELD_NUMBER = 2;
/*       */     
/*       */     private volatile Object schema_;
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */     
/*       */     private Collection(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  2688 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  2861 */       this.memoizedIsInitialized = -1; } private Collection() { this.memoizedIsInitialized = -1; this.name_ = ""; this.schema_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Collection(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Collection(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.name_ = bs; continue;case 18: bs = input.readBytes(); this.bitField0_ |= 0x2; this.schema_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable.ensureFieldAccessorsInitialized(Collection.class, Builder.class); } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); } public String getName() { Object ref = this.name_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; } public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; } public boolean hasSchema() { return ((this.bitField0_ & 0x2) != 0); }
/*       */     public String getSchema() { Object ref = this.schema_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.schema_ = s;  return s; }
/*       */     public ByteString getSchemaBytes() { Object ref = this.schema_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.schema_ = b; return b; }  return (ByteString)ref; }
/*  2864 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  2865 */       if (isInitialized == 1) return true; 
/*  2866 */       if (isInitialized == 0) return false;
/*       */       
/*  2868 */       if (!hasName()) {
/*  2869 */         this.memoizedIsInitialized = 0;
/*  2870 */         return false;
/*       */       } 
/*  2872 */       this.memoizedIsInitialized = 1;
/*  2873 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  2879 */       if ((this.bitField0_ & 0x1) != 0) {
/*  2880 */         GeneratedMessageV3.writeString(output, 1, this.name_);
/*       */       }
/*  2882 */       if ((this.bitField0_ & 0x2) != 0) {
/*  2883 */         GeneratedMessageV3.writeString(output, 2, this.schema_);
/*       */       }
/*  2885 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  2890 */       int size = this.memoizedSize;
/*  2891 */       if (size != -1) return size;
/*       */       
/*  2893 */       size = 0;
/*  2894 */       if ((this.bitField0_ & 0x1) != 0) {
/*  2895 */         size += GeneratedMessageV3.computeStringSize(1, this.name_);
/*       */       }
/*  2897 */       if ((this.bitField0_ & 0x2) != 0) {
/*  2898 */         size += GeneratedMessageV3.computeStringSize(2, this.schema_);
/*       */       }
/*  2900 */       size += this.unknownFields.getSerializedSize();
/*  2901 */       this.memoizedSize = size;
/*  2902 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  2907 */       if (obj == this) {
/*  2908 */         return true;
/*       */       }
/*  2910 */       if (!(obj instanceof Collection)) {
/*  2911 */         return super.equals(obj);
/*       */       }
/*  2913 */       Collection other = (Collection)obj;
/*       */       
/*  2915 */       if (hasName() != other.hasName()) return false; 
/*  2916 */       if (hasName() && 
/*       */         
/*  2918 */         !getName().equals(other.getName())) return false;
/*       */       
/*  2920 */       if (hasSchema() != other.hasSchema()) return false; 
/*  2921 */       if (hasSchema() && 
/*       */         
/*  2923 */         !getSchema().equals(other.getSchema())) return false;
/*       */       
/*  2925 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  2926 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  2931 */       if (this.memoizedHashCode != 0) {
/*  2932 */         return this.memoizedHashCode;
/*       */       }
/*  2934 */       int hash = 41;
/*  2935 */       hash = 19 * hash + getDescriptor().hashCode();
/*  2936 */       if (hasName()) {
/*  2937 */         hash = 37 * hash + 1;
/*  2938 */         hash = 53 * hash + getName().hashCode();
/*       */       } 
/*  2940 */       if (hasSchema()) {
/*  2941 */         hash = 37 * hash + 2;
/*  2942 */         hash = 53 * hash + getSchema().hashCode();
/*       */       } 
/*  2944 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  2945 */       this.memoizedHashCode = hash;
/*  2946 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  2952 */       return (Collection)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2958 */       return (Collection)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  2963 */       return (Collection)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2969 */       return (Collection)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Collection parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  2973 */       return (Collection)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  2979 */       return (Collection)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Collection parseFrom(InputStream input) throws IOException {
/*  2983 */       return 
/*  2984 */         (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  2990 */       return 
/*  2991 */         (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Collection parseDelimitedFrom(InputStream input) throws IOException {
/*  2995 */       return 
/*  2996 */         (Collection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Collection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3002 */       return 
/*  3003 */         (Collection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(CodedInputStream input) throws IOException {
/*  3008 */       return 
/*  3009 */         (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Collection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3015 */       return 
/*  3016 */         (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  3020 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  3022 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Collection prototype) {
/*  3025 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  3029 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  3030 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  3036 */       Builder builder = new Builder(parent);
/*  3037 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxCrud.CollectionOrBuilder {
/*       */       private int bitField0_;
/*       */       private Object name_;
/*       */       private Object schema_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  3048 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  3054 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable
/*  3055 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Collection.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  3209 */         this.name_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  3293 */         this.schema_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.name_ = ""; this.schema_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Collection.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.name_ = ""; this.bitField0_ &= 0xFFFFFFFE; this.schema_ = ""; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor; } public MysqlxCrud.Collection getDefaultInstanceForType() { return MysqlxCrud.Collection.getDefaultInstance(); } public MysqlxCrud.Collection build() { MysqlxCrud.Collection result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Collection buildPartial() { MysqlxCrud.Collection result = new MysqlxCrud.Collection(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.name_ = this.name_; if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.schema_ = this.schema_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Collection) return mergeFrom((MysqlxCrud.Collection)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Collection other) { if (other == MysqlxCrud.Collection.getDefaultInstance()) return this;  if (other.hasName()) { this.bitField0_ |= 0x1; this.name_ = other.name_; onChanged(); }  if (other.hasSchema()) { this.bitField0_ |= 0x2; this.schema_ = other.schema_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasName()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Collection parsedMessage = null; try { parsedMessage = (MysqlxCrud.Collection)MysqlxCrud.Collection.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Collection)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasName() { return ((this.bitField0_ & 0x1) != 0); }
/*       */       public String getName() { Object ref = this.name_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.name_ = s;  return s; }  return (String)ref; }
/*       */       public ByteString getNameBytes() { Object ref = this.name_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.name_ = b; return b; }  return (ByteString)ref; }
/*       */       public Builder setName(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; }
/*       */       public Builder clearName() { this.bitField0_ &= 0xFFFFFFFE; this.name_ = MysqlxCrud.Collection.getDefaultInstance().getName(); onChanged(); return this; }
/*       */       public Builder setNameBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.name_ = value; onChanged(); return this; }
/*  3299 */       public boolean hasSchema() { return ((this.bitField0_ & 0x2) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getSchema() {
/*  3306 */         Object ref = this.schema_;
/*  3307 */         if (!(ref instanceof String)) {
/*  3308 */           ByteString bs = (ByteString)ref;
/*       */           
/*  3310 */           String s = bs.toStringUtf8();
/*  3311 */           if (bs.isValidUtf8()) {
/*  3312 */             this.schema_ = s;
/*       */           }
/*  3314 */           return s;
/*       */         } 
/*  3316 */         return (String)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getSchemaBytes() {
/*  3325 */         Object ref = this.schema_;
/*  3326 */         if (ref instanceof String) {
/*       */           
/*  3328 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*       */           
/*  3330 */           this.schema_ = b;
/*  3331 */           return b;
/*       */         } 
/*  3333 */         return (ByteString)ref;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setSchema(String value) {
/*  3343 */         if (value == null) {
/*  3344 */           throw new NullPointerException();
/*       */         }
/*  3346 */         this.bitField0_ |= 0x2;
/*  3347 */         this.schema_ = value;
/*  3348 */         onChanged();
/*  3349 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearSchema() {
/*  3356 */         this.bitField0_ &= 0xFFFFFFFD;
/*  3357 */         this.schema_ = MysqlxCrud.Collection.getDefaultInstance().getSchema();
/*  3358 */         onChanged();
/*  3359 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setSchemaBytes(ByteString value) {
/*  3368 */         if (value == null) {
/*  3369 */           throw new NullPointerException();
/*       */         }
/*  3371 */         this.bitField0_ |= 0x2;
/*  3372 */         this.schema_ = value;
/*  3373 */         onChanged();
/*  3374 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  3379 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  3385 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  3395 */     private static final Collection DEFAULT_INSTANCE = new Collection();
/*       */ 
/*       */     
/*       */     public static Collection getDefaultInstance() {
/*  3399 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  3403 */     public static final Parser<Collection> PARSER = (Parser<Collection>)new AbstractParser<Collection>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Collection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  3409 */           return new MysqlxCrud.Collection(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Collection> parser() {
/*  3414 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Collection> getParserForType() {
/*  3419 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Collection getDefaultInstanceForType() {
/*  3424 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface LimitOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasRowCount();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     long getRowCount();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasOffset();
/*       */ 
/*       */ 
/*       */     
/*       */     long getOffset();
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Limit
/*       */     extends GeneratedMessageV3
/*       */     implements LimitOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */     
/*       */     public static final int ROW_COUNT_FIELD_NUMBER = 1;
/*       */ 
/*       */     
/*       */     private long rowCount_;
/*       */ 
/*       */     
/*       */     public static final int OFFSET_FIELD_NUMBER = 2;
/*       */ 
/*       */     
/*       */     private long offset_;
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */     
/*       */     private Limit(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  3481 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  3610 */       this.memoizedIsInitialized = -1; } private Limit() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Limit(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Limit(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: this.bitField0_ |= 0x1; this.rowCount_ = input.readUInt64(); continue;case 16: this.bitField0_ |= 0x2; this.offset_ = input.readUInt64(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable.ensureFieldAccessorsInitialized(Limit.class, Builder.class); } public boolean hasRowCount() { return ((this.bitField0_ & 0x1) != 0); } public long getRowCount() { return this.rowCount_; }
/*       */     public boolean hasOffset() { return ((this.bitField0_ & 0x2) != 0); }
/*       */     public long getOffset() { return this.offset_; }
/*  3613 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  3614 */       if (isInitialized == 1) return true; 
/*  3615 */       if (isInitialized == 0) return false;
/*       */       
/*  3617 */       if (!hasRowCount()) {
/*  3618 */         this.memoizedIsInitialized = 0;
/*  3619 */         return false;
/*       */       } 
/*  3621 */       this.memoizedIsInitialized = 1;
/*  3622 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  3628 */       if ((this.bitField0_ & 0x1) != 0) {
/*  3629 */         output.writeUInt64(1, this.rowCount_);
/*       */       }
/*  3631 */       if ((this.bitField0_ & 0x2) != 0) {
/*  3632 */         output.writeUInt64(2, this.offset_);
/*       */       }
/*  3634 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  3639 */       int size = this.memoizedSize;
/*  3640 */       if (size != -1) return size;
/*       */       
/*  3642 */       size = 0;
/*  3643 */       if ((this.bitField0_ & 0x1) != 0) {
/*  3644 */         size += 
/*  3645 */           CodedOutputStream.computeUInt64Size(1, this.rowCount_);
/*       */       }
/*  3647 */       if ((this.bitField0_ & 0x2) != 0) {
/*  3648 */         size += 
/*  3649 */           CodedOutputStream.computeUInt64Size(2, this.offset_);
/*       */       }
/*  3651 */       size += this.unknownFields.getSerializedSize();
/*  3652 */       this.memoizedSize = size;
/*  3653 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  3658 */       if (obj == this) {
/*  3659 */         return true;
/*       */       }
/*  3661 */       if (!(obj instanceof Limit)) {
/*  3662 */         return super.equals(obj);
/*       */       }
/*  3664 */       Limit other = (Limit)obj;
/*       */       
/*  3666 */       if (hasRowCount() != other.hasRowCount()) return false; 
/*  3667 */       if (hasRowCount() && 
/*  3668 */         getRowCount() != other
/*  3669 */         .getRowCount()) return false;
/*       */       
/*  3671 */       if (hasOffset() != other.hasOffset()) return false; 
/*  3672 */       if (hasOffset() && 
/*  3673 */         getOffset() != other
/*  3674 */         .getOffset()) return false;
/*       */       
/*  3676 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  3677 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  3682 */       if (this.memoizedHashCode != 0) {
/*  3683 */         return this.memoizedHashCode;
/*       */       }
/*  3685 */       int hash = 41;
/*  3686 */       hash = 19 * hash + getDescriptor().hashCode();
/*  3687 */       if (hasRowCount()) {
/*  3688 */         hash = 37 * hash + 1;
/*  3689 */         hash = 53 * hash + Internal.hashLong(
/*  3690 */             getRowCount());
/*       */       } 
/*  3692 */       if (hasOffset()) {
/*  3693 */         hash = 37 * hash + 2;
/*  3694 */         hash = 53 * hash + Internal.hashLong(
/*  3695 */             getOffset());
/*       */       } 
/*  3697 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  3698 */       this.memoizedHashCode = hash;
/*  3699 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  3705 */       return (Limit)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  3711 */       return (Limit)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  3716 */       return (Limit)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  3722 */       return (Limit)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Limit parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  3726 */       return (Limit)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  3732 */       return (Limit)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Limit parseFrom(InputStream input) throws IOException {
/*  3736 */       return 
/*  3737 */         (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3743 */       return 
/*  3744 */         (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Limit parseDelimitedFrom(InputStream input) throws IOException {
/*  3748 */       return 
/*  3749 */         (Limit)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Limit parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3755 */       return 
/*  3756 */         (Limit)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(CodedInputStream input) throws IOException {
/*  3761 */       return 
/*  3762 */         (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Limit parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3768 */       return 
/*  3769 */         (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  3773 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  3775 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Limit prototype) {
/*  3778 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  3782 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  3783 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  3789 */       Builder builder = new Builder(parent);
/*  3790 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxCrud.LimitOrBuilder {
/*       */       private int bitField0_;
/*       */       private long rowCount_;
/*       */       private long offset_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  3801 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  3807 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable
/*  3808 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Limit.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder() {
/*  3814 */         maybeForceBuilderInitialization();
/*       */       }
/*       */ 
/*       */       
/*       */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/*  3819 */         super(parent);
/*  3820 */         maybeForceBuilderInitialization();
/*       */       }
/*       */       
/*       */       private void maybeForceBuilderInitialization() {
/*  3824 */         if (MysqlxCrud.Limit.alwaysUseFieldBuilders);
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clear() {
/*  3829 */         super.clear();
/*  3830 */         this.rowCount_ = 0L;
/*  3831 */         this.bitField0_ &= 0xFFFFFFFE;
/*  3832 */         this.offset_ = 0L;
/*  3833 */         this.bitField0_ &= 0xFFFFFFFD;
/*  3834 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Descriptors.Descriptor getDescriptorForType() {
/*  3840 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Limit getDefaultInstanceForType() {
/*  3845 */         return MysqlxCrud.Limit.getDefaultInstance();
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Limit build() {
/*  3850 */         MysqlxCrud.Limit result = buildPartial();
/*  3851 */         if (!result.isInitialized()) {
/*  3852 */           throw newUninitializedMessageException(result);
/*       */         }
/*  3854 */         return result;
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Limit buildPartial() {
/*  3859 */         MysqlxCrud.Limit result = new MysqlxCrud.Limit(this);
/*  3860 */         int from_bitField0_ = this.bitField0_;
/*  3861 */         int to_bitField0_ = 0;
/*  3862 */         if ((from_bitField0_ & 0x1) != 0) {
/*  3863 */           result.rowCount_ = this.rowCount_;
/*  3864 */           to_bitField0_ |= 0x1;
/*       */         } 
/*  3866 */         if ((from_bitField0_ & 0x2) != 0) {
/*  3867 */           result.offset_ = this.offset_;
/*  3868 */           to_bitField0_ |= 0x2;
/*       */         } 
/*  3870 */         result.bitField0_ = to_bitField0_;
/*  3871 */         onBuilt();
/*  3872 */         return result;
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clone() {
/*  3877 */         return (Builder)super.clone();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/*  3883 */         return (Builder)super.setField(field, value);
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clearField(Descriptors.FieldDescriptor field) {
/*  3888 */         return (Builder)super.clearField(field);
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/*  3893 */         return (Builder)super.clearOneof(oneof);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*  3899 */         return (Builder)super.setRepeatedField(field, index, value);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*  3905 */         return (Builder)super.addRepeatedField(field, value);
/*       */       }
/*       */       
/*       */       public Builder mergeFrom(Message other) {
/*  3909 */         if (other instanceof MysqlxCrud.Limit) {
/*  3910 */           return mergeFrom((MysqlxCrud.Limit)other);
/*       */         }
/*  3912 */         super.mergeFrom(other);
/*  3913 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder mergeFrom(MysqlxCrud.Limit other) {
/*  3918 */         if (other == MysqlxCrud.Limit.getDefaultInstance()) return this; 
/*  3919 */         if (other.hasRowCount()) {
/*  3920 */           setRowCount(other.getRowCount());
/*       */         }
/*  3922 */         if (other.hasOffset()) {
/*  3923 */           setOffset(other.getOffset());
/*       */         }
/*  3925 */         mergeUnknownFields(other.unknownFields);
/*  3926 */         onChanged();
/*  3927 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final boolean isInitialized() {
/*  3932 */         if (!hasRowCount()) {
/*  3933 */           return false;
/*       */         }
/*  3935 */         return true;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  3943 */         MysqlxCrud.Limit parsedMessage = null;
/*       */         try {
/*  3945 */           parsedMessage = (MysqlxCrud.Limit)MysqlxCrud.Limit.PARSER.parsePartialFrom(input, extensionRegistry);
/*  3946 */         } catch (InvalidProtocolBufferException e) {
/*  3947 */           parsedMessage = (MysqlxCrud.Limit)e.getUnfinishedMessage();
/*  3948 */           throw e.unwrapIOException();
/*       */         } finally {
/*  3950 */           if (parsedMessage != null) {
/*  3951 */             mergeFrom(parsedMessage);
/*       */           }
/*       */         } 
/*  3954 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasRowCount() {
/*  3968 */         return ((this.bitField0_ & 0x1) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public long getRowCount() {
/*  3979 */         return this.rowCount_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setRowCount(long value) {
/*  3991 */         this.bitField0_ |= 0x1;
/*  3992 */         this.rowCount_ = value;
/*  3993 */         onChanged();
/*  3994 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearRowCount() {
/*  4005 */         this.bitField0_ &= 0xFFFFFFFE;
/*  4006 */         this.rowCount_ = 0L;
/*  4007 */         onChanged();
/*  4008 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasOffset() {
/*  4021 */         return ((this.bitField0_ & 0x2) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public long getOffset() {
/*  4032 */         return this.offset_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setOffset(long value) {
/*  4044 */         this.bitField0_ |= 0x2;
/*  4045 */         this.offset_ = value;
/*  4046 */         onChanged();
/*  4047 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearOffset() {
/*  4058 */         this.bitField0_ &= 0xFFFFFFFD;
/*  4059 */         this.offset_ = 0L;
/*  4060 */         onChanged();
/*  4061 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  4066 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  4072 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  4082 */     private static final Limit DEFAULT_INSTANCE = new Limit();
/*       */ 
/*       */     
/*       */     public static Limit getDefaultInstance() {
/*  4086 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  4090 */     public static final Parser<Limit> PARSER = (Parser<Limit>)new AbstractParser<Limit>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Limit parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  4096 */           return new MysqlxCrud.Limit(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Limit> parser() {
/*  4101 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Limit> getParserForType() {
/*  4106 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Limit getDefaultInstanceForType() {
/*  4111 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface LimitExprOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasRowCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getRowCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getRowCountOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasOffset();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getOffset();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getOffsetOrBuilder();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class LimitExpr
/*       */     extends GeneratedMessageV3
/*       */     implements LimitExprOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ROW_COUNT_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr rowCount_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int OFFSET_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr offset_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private LimitExpr(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  4192 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  4357 */       this.memoizedIsInitialized = -1; } private LimitExpr() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new LimitExpr(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private LimitExpr(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxExpr.Expr.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: subBuilder = null; if ((this.bitField0_ & 0x1) != 0) subBuilder = this.rowCount_.toBuilder();  this.rowCount_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.rowCount_); this.rowCount_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 18: subBuilder = null; if ((this.bitField0_ & 0x2) != 0) subBuilder = this.offset_.toBuilder();  this.offset_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.offset_); this.offset_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x2; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable.ensureFieldAccessorsInitialized(LimitExpr.class, Builder.class); } public boolean hasRowCount() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.Expr getRowCount() { return (this.rowCount_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_; } public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() { return (this.rowCount_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_; } public boolean hasOffset() { return ((this.bitField0_ & 0x2) != 0); }
/*       */     public MysqlxExpr.Expr getOffset() { return (this.offset_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_; }
/*       */     public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() { return (this.offset_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_; }
/*  4360 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  4361 */       if (isInitialized == 1) return true; 
/*  4362 */       if (isInitialized == 0) return false;
/*       */       
/*  4364 */       if (!hasRowCount()) {
/*  4365 */         this.memoizedIsInitialized = 0;
/*  4366 */         return false;
/*       */       } 
/*  4368 */       if (!getRowCount().isInitialized()) {
/*  4369 */         this.memoizedIsInitialized = 0;
/*  4370 */         return false;
/*       */       } 
/*  4372 */       if (hasOffset() && 
/*  4373 */         !getOffset().isInitialized()) {
/*  4374 */         this.memoizedIsInitialized = 0;
/*  4375 */         return false;
/*       */       } 
/*       */       
/*  4378 */       this.memoizedIsInitialized = 1;
/*  4379 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  4385 */       if ((this.bitField0_ & 0x1) != 0) {
/*  4386 */         output.writeMessage(1, (MessageLite)getRowCount());
/*       */       }
/*  4388 */       if ((this.bitField0_ & 0x2) != 0) {
/*  4389 */         output.writeMessage(2, (MessageLite)getOffset());
/*       */       }
/*  4391 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  4396 */       int size = this.memoizedSize;
/*  4397 */       if (size != -1) return size;
/*       */       
/*  4399 */       size = 0;
/*  4400 */       if ((this.bitField0_ & 0x1) != 0) {
/*  4401 */         size += 
/*  4402 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getRowCount());
/*       */       }
/*  4404 */       if ((this.bitField0_ & 0x2) != 0) {
/*  4405 */         size += 
/*  4406 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getOffset());
/*       */       }
/*  4408 */       size += this.unknownFields.getSerializedSize();
/*  4409 */       this.memoizedSize = size;
/*  4410 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  4415 */       if (obj == this) {
/*  4416 */         return true;
/*       */       }
/*  4418 */       if (!(obj instanceof LimitExpr)) {
/*  4419 */         return super.equals(obj);
/*       */       }
/*  4421 */       LimitExpr other = (LimitExpr)obj;
/*       */       
/*  4423 */       if (hasRowCount() != other.hasRowCount()) return false; 
/*  4424 */       if (hasRowCount() && 
/*       */         
/*  4426 */         !getRowCount().equals(other.getRowCount())) return false;
/*       */       
/*  4428 */       if (hasOffset() != other.hasOffset()) return false; 
/*  4429 */       if (hasOffset() && 
/*       */         
/*  4431 */         !getOffset().equals(other.getOffset())) return false;
/*       */       
/*  4433 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  4434 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  4439 */       if (this.memoizedHashCode != 0) {
/*  4440 */         return this.memoizedHashCode;
/*       */       }
/*  4442 */       int hash = 41;
/*  4443 */       hash = 19 * hash + getDescriptor().hashCode();
/*  4444 */       if (hasRowCount()) {
/*  4445 */         hash = 37 * hash + 1;
/*  4446 */         hash = 53 * hash + getRowCount().hashCode();
/*       */       } 
/*  4448 */       if (hasOffset()) {
/*  4449 */         hash = 37 * hash + 2;
/*  4450 */         hash = 53 * hash + getOffset().hashCode();
/*       */       } 
/*  4452 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  4453 */       this.memoizedHashCode = hash;
/*  4454 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  4460 */       return (LimitExpr)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  4466 */       return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  4471 */       return (LimitExpr)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  4477 */       return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static LimitExpr parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  4481 */       return (LimitExpr)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  4487 */       return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static LimitExpr parseFrom(InputStream input) throws IOException {
/*  4491 */       return 
/*  4492 */         (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  4498 */       return 
/*  4499 */         (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static LimitExpr parseDelimitedFrom(InputStream input) throws IOException {
/*  4503 */       return 
/*  4504 */         (LimitExpr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static LimitExpr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  4510 */       return 
/*  4511 */         (LimitExpr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(CodedInputStream input) throws IOException {
/*  4516 */       return 
/*  4517 */         (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static LimitExpr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  4523 */       return 
/*  4524 */         (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  4528 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  4530 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(LimitExpr prototype) {
/*  4533 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  4537 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  4538 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  4544 */       Builder builder = new Builder(parent);
/*  4545 */       return builder;
/*       */     }
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxCrud.LimitExprOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */       
/*       */       private MysqlxExpr.Expr rowCount_;
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> rowCountBuilder_;
/*       */       
/*       */       private MysqlxExpr.Expr offset_;
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> offsetBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  4564 */         return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  4570 */         return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable
/*  4571 */           .ensureFieldAccessorsInitialized(MysqlxCrud.LimitExpr.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder() {
/*  4577 */         maybeForceBuilderInitialization();
/*       */       }
/*       */ 
/*       */       
/*       */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/*  4582 */         super(parent);
/*  4583 */         maybeForceBuilderInitialization();
/*       */       }
/*       */       
/*       */       private void maybeForceBuilderInitialization() {
/*  4587 */         if (MysqlxCrud.LimitExpr.alwaysUseFieldBuilders) {
/*  4588 */           getRowCountFieldBuilder();
/*  4589 */           getOffsetFieldBuilder();
/*       */         } 
/*       */       }
/*       */       
/*       */       public Builder clear() {
/*  4594 */         super.clear();
/*  4595 */         if (this.rowCountBuilder_ == null) {
/*  4596 */           this.rowCount_ = null;
/*       */         } else {
/*  4598 */           this.rowCountBuilder_.clear();
/*       */         } 
/*  4600 */         this.bitField0_ &= 0xFFFFFFFE;
/*  4601 */         if (this.offsetBuilder_ == null) {
/*  4602 */           this.offset_ = null;
/*       */         } else {
/*  4604 */           this.offsetBuilder_.clear();
/*       */         } 
/*  4606 */         this.bitField0_ &= 0xFFFFFFFD;
/*  4607 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Descriptors.Descriptor getDescriptorForType() {
/*  4613 */         return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr getDefaultInstanceForType() {
/*  4618 */         return MysqlxCrud.LimitExpr.getDefaultInstance();
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr build() {
/*  4623 */         MysqlxCrud.LimitExpr result = buildPartial();
/*  4624 */         if (!result.isInitialized()) {
/*  4625 */           throw newUninitializedMessageException(result);
/*       */         }
/*  4627 */         return result;
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr buildPartial() {
/*  4632 */         MysqlxCrud.LimitExpr result = new MysqlxCrud.LimitExpr(this);
/*  4633 */         int from_bitField0_ = this.bitField0_;
/*  4634 */         int to_bitField0_ = 0;
/*  4635 */         if ((from_bitField0_ & 0x1) != 0) {
/*  4636 */           if (this.rowCountBuilder_ == null) {
/*  4637 */             result.rowCount_ = this.rowCount_;
/*       */           } else {
/*  4639 */             result.rowCount_ = (MysqlxExpr.Expr)this.rowCountBuilder_.build();
/*       */           } 
/*  4641 */           to_bitField0_ |= 0x1;
/*       */         } 
/*  4643 */         if ((from_bitField0_ & 0x2) != 0) {
/*  4644 */           if (this.offsetBuilder_ == null) {
/*  4645 */             result.offset_ = this.offset_;
/*       */           } else {
/*  4647 */             result.offset_ = (MysqlxExpr.Expr)this.offsetBuilder_.build();
/*       */           } 
/*  4649 */           to_bitField0_ |= 0x2;
/*       */         } 
/*  4651 */         result.bitField0_ = to_bitField0_;
/*  4652 */         onBuilt();
/*  4653 */         return result;
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clone() {
/*  4658 */         return (Builder)super.clone();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/*  4664 */         return (Builder)super.setField(field, value);
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clearField(Descriptors.FieldDescriptor field) {
/*  4669 */         return (Builder)super.clearField(field);
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/*  4674 */         return (Builder)super.clearOneof(oneof);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*  4680 */         return (Builder)super.setRepeatedField(field, index, value);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*  4686 */         return (Builder)super.addRepeatedField(field, value);
/*       */       }
/*       */       
/*       */       public Builder mergeFrom(Message other) {
/*  4690 */         if (other instanceof MysqlxCrud.LimitExpr) {
/*  4691 */           return mergeFrom((MysqlxCrud.LimitExpr)other);
/*       */         }
/*  4693 */         super.mergeFrom(other);
/*  4694 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder mergeFrom(MysqlxCrud.LimitExpr other) {
/*  4699 */         if (other == MysqlxCrud.LimitExpr.getDefaultInstance()) return this; 
/*  4700 */         if (other.hasRowCount()) {
/*  4701 */           mergeRowCount(other.getRowCount());
/*       */         }
/*  4703 */         if (other.hasOffset()) {
/*  4704 */           mergeOffset(other.getOffset());
/*       */         }
/*  4706 */         mergeUnknownFields(other.unknownFields);
/*  4707 */         onChanged();
/*  4708 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final boolean isInitialized() {
/*  4713 */         if (!hasRowCount()) {
/*  4714 */           return false;
/*       */         }
/*  4716 */         if (!getRowCount().isInitialized()) {
/*  4717 */           return false;
/*       */         }
/*  4719 */         if (hasOffset() && 
/*  4720 */           !getOffset().isInitialized()) {
/*  4721 */           return false;
/*       */         }
/*       */         
/*  4724 */         return true;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  4732 */         MysqlxCrud.LimitExpr parsedMessage = null;
/*       */         try {
/*  4734 */           parsedMessage = (MysqlxCrud.LimitExpr)MysqlxCrud.LimitExpr.PARSER.parsePartialFrom(input, extensionRegistry);
/*  4735 */         } catch (InvalidProtocolBufferException e) {
/*  4736 */           parsedMessage = (MysqlxCrud.LimitExpr)e.getUnfinishedMessage();
/*  4737 */           throw e.unwrapIOException();
/*       */         } finally {
/*  4739 */           if (parsedMessage != null) {
/*  4740 */             mergeFrom(parsedMessage);
/*       */           }
/*       */         } 
/*  4743 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasRowCount() {
/*  4759 */         return ((this.bitField0_ & 0x1) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr getRowCount() {
/*  4770 */         if (this.rowCountBuilder_ == null) {
/*  4771 */           return (this.rowCount_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
/*       */         }
/*  4773 */         return (MysqlxExpr.Expr)this.rowCountBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setRowCount(MysqlxExpr.Expr value) {
/*  4784 */         if (this.rowCountBuilder_ == null) {
/*  4785 */           if (value == null) {
/*  4786 */             throw new NullPointerException();
/*       */           }
/*  4788 */           this.rowCount_ = value;
/*  4789 */           onChanged();
/*       */         } else {
/*  4791 */           this.rowCountBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  4793 */         this.bitField0_ |= 0x1;
/*  4794 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setRowCount(MysqlxExpr.Expr.Builder builderForValue) {
/*  4805 */         if (this.rowCountBuilder_ == null) {
/*  4806 */           this.rowCount_ = builderForValue.build();
/*  4807 */           onChanged();
/*       */         } else {
/*  4809 */           this.rowCountBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  4811 */         this.bitField0_ |= 0x1;
/*  4812 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeRowCount(MysqlxExpr.Expr value) {
/*  4822 */         if (this.rowCountBuilder_ == null) {
/*  4823 */           if ((this.bitField0_ & 0x1) != 0 && this.rowCount_ != null && this.rowCount_ != 
/*       */             
/*  4825 */             MysqlxExpr.Expr.getDefaultInstance()) {
/*  4826 */             this
/*  4827 */               .rowCount_ = MysqlxExpr.Expr.newBuilder(this.rowCount_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  4829 */             this.rowCount_ = value;
/*       */           } 
/*  4831 */           onChanged();
/*       */         } else {
/*  4833 */           this.rowCountBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  4835 */         this.bitField0_ |= 0x1;
/*  4836 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearRowCount() {
/*  4846 */         if (this.rowCountBuilder_ == null) {
/*  4847 */           this.rowCount_ = null;
/*  4848 */           onChanged();
/*       */         } else {
/*  4850 */           this.rowCountBuilder_.clear();
/*       */         } 
/*  4852 */         this.bitField0_ &= 0xFFFFFFFE;
/*  4853 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder getRowCountBuilder() {
/*  4863 */         this.bitField0_ |= 0x1;
/*  4864 */         onChanged();
/*  4865 */         return (MysqlxExpr.Expr.Builder)getRowCountFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() {
/*  4875 */         if (this.rowCountBuilder_ != null) {
/*  4876 */           return (MysqlxExpr.ExprOrBuilder)this.rowCountBuilder_.getMessageOrBuilder();
/*       */         }
/*  4878 */         return (this.rowCount_ == null) ? 
/*  4879 */           MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getRowCountFieldBuilder() {
/*  4892 */         if (this.rowCountBuilder_ == null) {
/*  4893 */           this
/*       */ 
/*       */ 
/*       */             
/*  4897 */             .rowCountBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getRowCount(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  4898 */           this.rowCount_ = null;
/*       */         } 
/*  4900 */         return this.rowCountBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasOffset() {
/*  4915 */         return ((this.bitField0_ & 0x2) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr getOffset() {
/*  4926 */         if (this.offsetBuilder_ == null) {
/*  4927 */           return (this.offset_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
/*       */         }
/*  4929 */         return (MysqlxExpr.Expr)this.offsetBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setOffset(MysqlxExpr.Expr value) {
/*  4940 */         if (this.offsetBuilder_ == null) {
/*  4941 */           if (value == null) {
/*  4942 */             throw new NullPointerException();
/*       */           }
/*  4944 */           this.offset_ = value;
/*  4945 */           onChanged();
/*       */         } else {
/*  4947 */           this.offsetBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  4949 */         this.bitField0_ |= 0x2;
/*  4950 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setOffset(MysqlxExpr.Expr.Builder builderForValue) {
/*  4961 */         if (this.offsetBuilder_ == null) {
/*  4962 */           this.offset_ = builderForValue.build();
/*  4963 */           onChanged();
/*       */         } else {
/*  4965 */           this.offsetBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  4967 */         this.bitField0_ |= 0x2;
/*  4968 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeOffset(MysqlxExpr.Expr value) {
/*  4978 */         if (this.offsetBuilder_ == null) {
/*  4979 */           if ((this.bitField0_ & 0x2) != 0 && this.offset_ != null && this.offset_ != 
/*       */             
/*  4981 */             MysqlxExpr.Expr.getDefaultInstance()) {
/*  4982 */             this
/*  4983 */               .offset_ = MysqlxExpr.Expr.newBuilder(this.offset_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  4985 */             this.offset_ = value;
/*       */           } 
/*  4987 */           onChanged();
/*       */         } else {
/*  4989 */           this.offsetBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  4991 */         this.bitField0_ |= 0x2;
/*  4992 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearOffset() {
/*  5002 */         if (this.offsetBuilder_ == null) {
/*  5003 */           this.offset_ = null;
/*  5004 */           onChanged();
/*       */         } else {
/*  5006 */           this.offsetBuilder_.clear();
/*       */         } 
/*  5008 */         this.bitField0_ &= 0xFFFFFFFD;
/*  5009 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder getOffsetBuilder() {
/*  5019 */         this.bitField0_ |= 0x2;
/*  5020 */         onChanged();
/*  5021 */         return (MysqlxExpr.Expr.Builder)getOffsetFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() {
/*  5031 */         if (this.offsetBuilder_ != null) {
/*  5032 */           return (MysqlxExpr.ExprOrBuilder)this.offsetBuilder_.getMessageOrBuilder();
/*       */         }
/*  5034 */         return (this.offset_ == null) ? 
/*  5035 */           MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getOffsetFieldBuilder() {
/*  5048 */         if (this.offsetBuilder_ == null) {
/*  5049 */           this
/*       */ 
/*       */ 
/*       */             
/*  5053 */             .offsetBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getOffset(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  5054 */           this.offset_ = null;
/*       */         } 
/*  5056 */         return this.offsetBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  5061 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  5067 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  5077 */     private static final LimitExpr DEFAULT_INSTANCE = new LimitExpr();
/*       */ 
/*       */     
/*       */     public static LimitExpr getDefaultInstance() {
/*  5081 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  5085 */     public static final Parser<LimitExpr> PARSER = (Parser<LimitExpr>)new AbstractParser<LimitExpr>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.LimitExpr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  5091 */           return new MysqlxCrud.LimitExpr(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<LimitExpr> parser() {
/*  5096 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<LimitExpr> getParserForType() {
/*  5101 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public LimitExpr getDefaultInstanceForType() {
/*  5106 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface OrderOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasExpr();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getExpr();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getExprOrBuilder();
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasDirection();
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Order.Direction getDirection();
/*       */   }
/*       */ 
/*       */   
/*       */   public static final class Order
/*       */     extends GeneratedMessageV3
/*       */     implements OrderOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */     
/*       */     private int bitField0_;
/*       */     
/*       */     public static final int EXPR_FIELD_NUMBER = 1;
/*       */     
/*       */     private MysqlxExpr.Expr expr_;
/*       */     
/*       */     public static final int DIRECTION_FIELD_NUMBER = 2;
/*       */     
/*       */     private int direction_;
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */     
/*       */     private Order(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  5156 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  5389 */       this.memoizedIsInitialized = -1; } private Order() { this.memoizedIsInitialized = -1; this.direction_ = 1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Order(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Order(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxExpr.Expr.Builder subBuilder; int rawValue; Direction value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: subBuilder = null; if ((this.bitField0_ & 0x1) != 0) subBuilder = this.expr_.toBuilder();  this.expr_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.expr_); this.expr_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 16: rawValue = input.readEnum(); value = Direction.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(2, rawValue); continue; }  this.bitField0_ |= 0x2; this.direction_ = rawValue; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable.ensureFieldAccessorsInitialized(Order.class, Builder.class); } public enum Direction implements ProtocolMessageEnum {
/*       */       ASC(1), DESC(2); public static final int ASC_VALUE = 1; public static final int DESC_VALUE = 2; private static final Internal.EnumLiteMap<Direction> internalValueMap = new Internal.EnumLiteMap<Direction>() { public MysqlxCrud.Order.Direction findValueByNumber(int number) { return MysqlxCrud.Order.Direction.forNumber(number); } }
/*       */       ; private static final Direction[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Direction forNumber(int value) { switch (value) { case 1: return ASC;case 2: return DESC; }  return null; } public static Internal.EnumLiteMap<Direction> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.Order.getDescriptor().getEnumTypes().get(0); } Direction(int value) { this.value = value; } } public boolean hasExpr() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.Expr getExpr() { return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_; } public MysqlxExpr.ExprOrBuilder getExprOrBuilder() { return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_; } public boolean hasDirection() { return ((this.bitField0_ & 0x2) != 0); } public Direction getDirection() { Direction result = Direction.valueOf(this.direction_); return (result == null) ? Direction.ASC : result; }
/*  5392 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  5393 */       if (isInitialized == 1) return true; 
/*  5394 */       if (isInitialized == 0) return false;
/*       */       
/*  5396 */       if (!hasExpr()) {
/*  5397 */         this.memoizedIsInitialized = 0;
/*  5398 */         return false;
/*       */       } 
/*  5400 */       if (!getExpr().isInitialized()) {
/*  5401 */         this.memoizedIsInitialized = 0;
/*  5402 */         return false;
/*       */       } 
/*  5404 */       this.memoizedIsInitialized = 1;
/*  5405 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  5411 */       if ((this.bitField0_ & 0x1) != 0) {
/*  5412 */         output.writeMessage(1, (MessageLite)getExpr());
/*       */       }
/*  5414 */       if ((this.bitField0_ & 0x2) != 0) {
/*  5415 */         output.writeEnum(2, this.direction_);
/*       */       }
/*  5417 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  5422 */       int size = this.memoizedSize;
/*  5423 */       if (size != -1) return size;
/*       */       
/*  5425 */       size = 0;
/*  5426 */       if ((this.bitField0_ & 0x1) != 0) {
/*  5427 */         size += 
/*  5428 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getExpr());
/*       */       }
/*  5430 */       if ((this.bitField0_ & 0x2) != 0) {
/*  5431 */         size += 
/*  5432 */           CodedOutputStream.computeEnumSize(2, this.direction_);
/*       */       }
/*  5434 */       size += this.unknownFields.getSerializedSize();
/*  5435 */       this.memoizedSize = size;
/*  5436 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  5441 */       if (obj == this) {
/*  5442 */         return true;
/*       */       }
/*  5444 */       if (!(obj instanceof Order)) {
/*  5445 */         return super.equals(obj);
/*       */       }
/*  5447 */       Order other = (Order)obj;
/*       */       
/*  5449 */       if (hasExpr() != other.hasExpr()) return false; 
/*  5450 */       if (hasExpr() && 
/*       */         
/*  5452 */         !getExpr().equals(other.getExpr())) return false;
/*       */       
/*  5454 */       if (hasDirection() != other.hasDirection()) return false; 
/*  5455 */       if (hasDirection() && 
/*  5456 */         this.direction_ != other.direction_) return false;
/*       */       
/*  5458 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  5459 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  5464 */       if (this.memoizedHashCode != 0) {
/*  5465 */         return this.memoizedHashCode;
/*       */       }
/*  5467 */       int hash = 41;
/*  5468 */       hash = 19 * hash + getDescriptor().hashCode();
/*  5469 */       if (hasExpr()) {
/*  5470 */         hash = 37 * hash + 1;
/*  5471 */         hash = 53 * hash + getExpr().hashCode();
/*       */       } 
/*  5473 */       if (hasDirection()) {
/*  5474 */         hash = 37 * hash + 2;
/*  5475 */         hash = 53 * hash + this.direction_;
/*       */       } 
/*  5477 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  5478 */       this.memoizedHashCode = hash;
/*  5479 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Order parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  5485 */       return (Order)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Order parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  5491 */       return (Order)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Order parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  5496 */       return (Order)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Order parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  5502 */       return (Order)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Order parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  5506 */       return (Order)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Order parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  5512 */       return (Order)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Order parseFrom(InputStream input) throws IOException {
/*  5516 */       return 
/*  5517 */         (Order)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Order parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  5523 */       return 
/*  5524 */         (Order)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Order parseDelimitedFrom(InputStream input) throws IOException {
/*  5528 */       return 
/*  5529 */         (Order)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Order parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  5535 */       return 
/*  5536 */         (Order)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Order parseFrom(CodedInputStream input) throws IOException {
/*  5541 */       return 
/*  5542 */         (Order)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Order parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  5548 */       return 
/*  5549 */         (Order)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  5553 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  5555 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Order prototype) {
/*  5558 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  5562 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  5563 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  5569 */       Builder builder = new Builder(parent);
/*  5570 */       return builder;
/*       */     }
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxCrud.OrderOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */       
/*       */       private MysqlxExpr.Expr expr_;
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> exprBuilder_;
/*       */       private int direction_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  5586 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  5592 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable
/*  5593 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Order.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  5875 */         this.direction_ = 1; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.direction_ = 1; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Order.alwaysUseFieldBuilders) getExprFieldBuilder();  } public Builder clear() { super.clear(); if (this.exprBuilder_ == null) { this.expr_ = null; } else { this.exprBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.direction_ = 1; this.bitField0_ &= 0xFFFFFFFD; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor; } public MysqlxCrud.Order getDefaultInstanceForType() { return MysqlxCrud.Order.getDefaultInstance(); } public MysqlxCrud.Order build() { MysqlxCrud.Order result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Order buildPartial() { MysqlxCrud.Order result = new MysqlxCrud.Order(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.exprBuilder_ == null) { result.expr_ = this.expr_; } else { result.expr_ = (MysqlxExpr.Expr)this.exprBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.direction_ = this.direction_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Order) return mergeFrom((MysqlxCrud.Order)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Order other) { if (other == MysqlxCrud.Order.getDefaultInstance()) return this;  if (other.hasExpr()) mergeExpr(other.getExpr());  if (other.hasDirection()) setDirection(other.getDirection());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasExpr()) return false;  if (!getExpr().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Order parsedMessage = null; try { parsedMessage = (MysqlxCrud.Order)MysqlxCrud.Order.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Order)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasExpr() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.Expr getExpr() { if (this.exprBuilder_ == null) return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;  return (MysqlxExpr.Expr)this.exprBuilder_.getMessage(); } public Builder setExpr(MysqlxExpr.Expr value) { if (this.exprBuilder_ == null) { if (value == null) throw new NullPointerException();  this.expr_ = value; onChanged(); } else { this.exprBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setExpr(MysqlxExpr.Expr.Builder builderForValue) { if (this.exprBuilder_ == null) { this.expr_ = builderForValue.build(); onChanged(); } else { this.exprBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder mergeExpr(MysqlxExpr.Expr value) { if (this.exprBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.expr_ != null && this.expr_ != MysqlxExpr.Expr.getDefaultInstance()) { this.expr_ = MysqlxExpr.Expr.newBuilder(this.expr_).mergeFrom(value).buildPartial(); } else { this.expr_ = value; }  onChanged(); } else { this.exprBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder clearExpr() { if (this.exprBuilder_ == null) { this.expr_ = null; onChanged(); } else { this.exprBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; }
/*       */       public MysqlxExpr.Expr.Builder getExprBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxExpr.Expr.Builder)getExprFieldBuilder().getBuilder(); }
/*       */       public MysqlxExpr.ExprOrBuilder getExprOrBuilder() { if (this.exprBuilder_ != null) return (MysqlxExpr.ExprOrBuilder)this.exprBuilder_.getMessageOrBuilder();  return (this.expr_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_; }
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getExprFieldBuilder() { if (this.exprBuilder_ == null) { this.exprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.expr_ = null; }  return this.exprBuilder_; }
/*  5881 */       public boolean hasDirection() { return ((this.bitField0_ & 0x2) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Order.Direction getDirection() {
/*  5889 */         MysqlxCrud.Order.Direction result = MysqlxCrud.Order.Direction.valueOf(this.direction_);
/*  5890 */         return (result == null) ? MysqlxCrud.Order.Direction.ASC : result;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setDirection(MysqlxCrud.Order.Direction value) {
/*  5898 */         if (value == null) {
/*  5899 */           throw new NullPointerException();
/*       */         }
/*  5901 */         this.bitField0_ |= 0x2;
/*  5902 */         this.direction_ = value.getNumber();
/*  5903 */         onChanged();
/*  5904 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearDirection() {
/*  5911 */         this.bitField0_ &= 0xFFFFFFFD;
/*  5912 */         this.direction_ = 1;
/*  5913 */         onChanged();
/*  5914 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  5919 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  5925 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  5935 */     private static final Order DEFAULT_INSTANCE = new Order();
/*       */ 
/*       */     
/*       */     public static Order getDefaultInstance() {
/*  5939 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  5943 */     public static final Parser<Order> PARSER = (Parser<Order>)new AbstractParser<Order>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Order parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  5949 */           return new MysqlxCrud.Order(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Order> parser() {
/*  5954 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Order> getParserForType() {
/*  5959 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Order getDefaultInstanceForType() {
/*  5964 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface UpdateOperationOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasSource();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ColumnIdentifier getSource();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasOperation();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.UpdateOperation.UpdateType getOperation();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasValue();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getValue();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getValueOrBuilder();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class UpdateOperation
/*       */     extends GeneratedMessageV3
/*       */     implements UpdateOperationOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int SOURCE_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.ColumnIdentifier source_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int OPERATION_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */     
/*       */     private int operation_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int VALUE_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr value_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private UpdateOperation(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  6068 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  6499 */       this.memoizedIsInitialized = -1; } private UpdateOperation() { this.memoizedIsInitialized = -1; this.operation_ = 1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new UpdateOperation(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private UpdateOperation(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxExpr.ColumnIdentifier.Builder builder; int rawValue; MysqlxExpr.Expr.Builder subBuilder; UpdateType value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: builder = null; if ((this.bitField0_ & 0x1) != 0) builder = this.source_.toBuilder();  this.source_ = (MysqlxExpr.ColumnIdentifier)input.readMessage(MysqlxExpr.ColumnIdentifier.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.source_); this.source_ = builder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 16: rawValue = input.readEnum(); value = UpdateType.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(2, rawValue); continue; }  this.bitField0_ |= 0x2; this.operation_ = rawValue; continue;case 26: subBuilder = null; if ((this.bitField0_ & 0x4) != 0) subBuilder = this.value_.toBuilder();  this.value_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.value_); this.value_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x4; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateOperation.class, Builder.class); } public enum UpdateType implements ProtocolMessageEnum {
/*       */       SET(1), ITEM_REMOVE(2), ITEM_SET(3), ITEM_REPLACE(4), ITEM_MERGE(5), ARRAY_INSERT(6), ARRAY_APPEND(7), MERGE_PATCH(8); public static final int SET_VALUE = 1; public static final int ITEM_REMOVE_VALUE = 2; public static final int ITEM_SET_VALUE = 3; public static final int ITEM_REPLACE_VALUE = 4; public static final int ITEM_MERGE_VALUE = 5; public static final int ARRAY_INSERT_VALUE = 6; public static final int ARRAY_APPEND_VALUE = 7; public static final int MERGE_PATCH_VALUE = 8; private static final Internal.EnumLiteMap<UpdateType> internalValueMap = new Internal.EnumLiteMap<UpdateType>() { public MysqlxCrud.UpdateOperation.UpdateType findValueByNumber(int number) { return MysqlxCrud.UpdateOperation.UpdateType.forNumber(number); } }
/*       */       ; private static final UpdateType[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static UpdateType forNumber(int value) { switch (value) { case 1: return SET;case 2: return ITEM_REMOVE;case 3: return ITEM_SET;case 4: return ITEM_REPLACE;case 5: return ITEM_MERGE;case 6: return ARRAY_INSERT;case 7: return ARRAY_APPEND;case 8: return MERGE_PATCH; }  return null; } public static Internal.EnumLiteMap<UpdateType> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.UpdateOperation.getDescriptor().getEnumTypes().get(0); } UpdateType(int value) { this.value = value; } } public boolean hasSource() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxExpr.ColumnIdentifier getSource() { return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_; } public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() { return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_; } public boolean hasOperation() { return ((this.bitField0_ & 0x2) != 0); } public UpdateType getOperation() { UpdateType result = UpdateType.valueOf(this.operation_); return (result == null) ? UpdateType.SET : result; } public boolean hasValue() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxExpr.Expr getValue() { return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_; } public MysqlxExpr.ExprOrBuilder getValueOrBuilder() { return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_; }
/*  6502 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  6503 */       if (isInitialized == 1) return true; 
/*  6504 */       if (isInitialized == 0) return false;
/*       */       
/*  6506 */       if (!hasSource()) {
/*  6507 */         this.memoizedIsInitialized = 0;
/*  6508 */         return false;
/*       */       } 
/*  6510 */       if (!hasOperation()) {
/*  6511 */         this.memoizedIsInitialized = 0;
/*  6512 */         return false;
/*       */       } 
/*  6514 */       if (!getSource().isInitialized()) {
/*  6515 */         this.memoizedIsInitialized = 0;
/*  6516 */         return false;
/*       */       } 
/*  6518 */       if (hasValue() && 
/*  6519 */         !getValue().isInitialized()) {
/*  6520 */         this.memoizedIsInitialized = 0;
/*  6521 */         return false;
/*       */       } 
/*       */       
/*  6524 */       this.memoizedIsInitialized = 1;
/*  6525 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  6531 */       if ((this.bitField0_ & 0x1) != 0) {
/*  6532 */         output.writeMessage(1, (MessageLite)getSource());
/*       */       }
/*  6534 */       if ((this.bitField0_ & 0x2) != 0) {
/*  6535 */         output.writeEnum(2, this.operation_);
/*       */       }
/*  6537 */       if ((this.bitField0_ & 0x4) != 0) {
/*  6538 */         output.writeMessage(3, (MessageLite)getValue());
/*       */       }
/*  6540 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  6545 */       int size = this.memoizedSize;
/*  6546 */       if (size != -1) return size;
/*       */       
/*  6548 */       size = 0;
/*  6549 */       if ((this.bitField0_ & 0x1) != 0) {
/*  6550 */         size += 
/*  6551 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getSource());
/*       */       }
/*  6553 */       if ((this.bitField0_ & 0x2) != 0) {
/*  6554 */         size += 
/*  6555 */           CodedOutputStream.computeEnumSize(2, this.operation_);
/*       */       }
/*  6557 */       if ((this.bitField0_ & 0x4) != 0) {
/*  6558 */         size += 
/*  6559 */           CodedOutputStream.computeMessageSize(3, (MessageLite)getValue());
/*       */       }
/*  6561 */       size += this.unknownFields.getSerializedSize();
/*  6562 */       this.memoizedSize = size;
/*  6563 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  6568 */       if (obj == this) {
/*  6569 */         return true;
/*       */       }
/*  6571 */       if (!(obj instanceof UpdateOperation)) {
/*  6572 */         return super.equals(obj);
/*       */       }
/*  6574 */       UpdateOperation other = (UpdateOperation)obj;
/*       */       
/*  6576 */       if (hasSource() != other.hasSource()) return false; 
/*  6577 */       if (hasSource() && 
/*       */         
/*  6579 */         !getSource().equals(other.getSource())) return false;
/*       */       
/*  6581 */       if (hasOperation() != other.hasOperation()) return false; 
/*  6582 */       if (hasOperation() && 
/*  6583 */         this.operation_ != other.operation_) return false;
/*       */       
/*  6585 */       if (hasValue() != other.hasValue()) return false; 
/*  6586 */       if (hasValue() && 
/*       */         
/*  6588 */         !getValue().equals(other.getValue())) return false;
/*       */       
/*  6590 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  6591 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  6596 */       if (this.memoizedHashCode != 0) {
/*  6597 */         return this.memoizedHashCode;
/*       */       }
/*  6599 */       int hash = 41;
/*  6600 */       hash = 19 * hash + getDescriptor().hashCode();
/*  6601 */       if (hasSource()) {
/*  6602 */         hash = 37 * hash + 1;
/*  6603 */         hash = 53 * hash + getSource().hashCode();
/*       */       } 
/*  6605 */       if (hasOperation()) {
/*  6606 */         hash = 37 * hash + 2;
/*  6607 */         hash = 53 * hash + this.operation_;
/*       */       } 
/*  6609 */       if (hasValue()) {
/*  6610 */         hash = 37 * hash + 3;
/*  6611 */         hash = 53 * hash + getValue().hashCode();
/*       */       } 
/*  6613 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  6614 */       this.memoizedHashCode = hash;
/*  6615 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  6621 */       return (UpdateOperation)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  6627 */       return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  6632 */       return (UpdateOperation)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  6638 */       return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static UpdateOperation parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  6642 */       return (UpdateOperation)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  6648 */       return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static UpdateOperation parseFrom(InputStream input) throws IOException {
/*  6652 */       return 
/*  6653 */         (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  6659 */       return 
/*  6660 */         (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static UpdateOperation parseDelimitedFrom(InputStream input) throws IOException {
/*  6664 */       return 
/*  6665 */         (UpdateOperation)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  6671 */       return 
/*  6672 */         (UpdateOperation)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(CodedInputStream input) throws IOException {
/*  6677 */       return 
/*  6678 */         (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static UpdateOperation parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  6684 */       return 
/*  6685 */         (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  6689 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  6691 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(UpdateOperation prototype) {
/*  6694 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  6698 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  6699 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  6705 */       Builder builder = new Builder(parent);
/*  6706 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.UpdateOperationOrBuilder { private int bitField0_;
/*       */       private MysqlxExpr.ColumnIdentifier source_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> sourceBuilder_;
/*       */       private int operation_;
/*       */       private MysqlxExpr.Expr value_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  6717 */         return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  6723 */         return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable
/*  6724 */           .ensureFieldAccessorsInitialized(MysqlxCrud.UpdateOperation.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  7104 */         this.operation_ = 1; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.operation_ = 1; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.UpdateOperation.alwaysUseFieldBuilders) { getSourceFieldBuilder(); getValueFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.sourceBuilder_ == null) { this.source_ = null; } else { this.sourceBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.operation_ = 1; this.bitField0_ &= 0xFFFFFFFD; if (this.valueBuilder_ == null) { this.value_ = null; } else { this.valueBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor; } public MysqlxCrud.UpdateOperation getDefaultInstanceForType() { return MysqlxCrud.UpdateOperation.getDefaultInstance(); } public MysqlxCrud.UpdateOperation build() { MysqlxCrud.UpdateOperation result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.UpdateOperation buildPartial() { MysqlxCrud.UpdateOperation result = new MysqlxCrud.UpdateOperation(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.sourceBuilder_ == null) { result.source_ = this.source_; } else { result.source_ = (MysqlxExpr.ColumnIdentifier)this.sourceBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.operation_ = this.operation_; if ((from_bitField0_ & 0x4) != 0) { if (this.valueBuilder_ == null) { result.value_ = this.value_; } else { result.value_ = (MysqlxExpr.Expr)this.valueBuilder_.build(); }  to_bitField0_ |= 0x4; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.UpdateOperation) return mergeFrom((MysqlxCrud.UpdateOperation)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.UpdateOperation other) { if (other == MysqlxCrud.UpdateOperation.getDefaultInstance()) return this;  if (other.hasSource()) mergeSource(other.getSource());  if (other.hasOperation()) setOperation(other.getOperation());  if (other.hasValue()) mergeValue(other.getValue());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasSource()) return false;  if (!hasOperation()) return false;  if (!getSource().isInitialized()) return false;  if (hasValue() && !getValue().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.UpdateOperation parsedMessage = null; try { parsedMessage = (MysqlxCrud.UpdateOperation)MysqlxCrud.UpdateOperation.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.UpdateOperation)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/*       */       public boolean hasSource() { return ((this.bitField0_ & 0x1) != 0); }
/*       */       public MysqlxExpr.ColumnIdentifier getSource() { if (this.sourceBuilder_ == null) return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;  return (MysqlxExpr.ColumnIdentifier)this.sourceBuilder_.getMessage(); }
/*       */       public Builder setSource(MysqlxExpr.ColumnIdentifier value) { if (this.sourceBuilder_ == null) { if (value == null) throw new NullPointerException();  this.source_ = value; onChanged(); } else { this.sourceBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder setSource(MysqlxExpr.ColumnIdentifier.Builder builderForValue) { if (this.sourceBuilder_ == null) { this.source_ = builderForValue.build(); onChanged(); } else { this.sourceBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder mergeSource(MysqlxExpr.ColumnIdentifier value) { if (this.sourceBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) { this.source_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.source_).mergeFrom(value).buildPartial(); } else { this.source_ = value; }  onChanged(); } else { this.sourceBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; }
/*       */       public Builder clearSource() { if (this.sourceBuilder_ == null) { this.source_ = null; onChanged(); } else { this.sourceBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; }
/*       */       public MysqlxExpr.ColumnIdentifier.Builder getSourceBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxExpr.ColumnIdentifier.Builder)getSourceFieldBuilder().getBuilder(); }
/*       */       public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() { if (this.sourceBuilder_ != null) return (MysqlxExpr.ColumnIdentifierOrBuilder)this.sourceBuilder_.getMessageOrBuilder();  return (this.source_ == null) ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_; }
/*       */       private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> getSourceFieldBuilder() { if (this.sourceBuilder_ == null) { this.sourceBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getSource(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.source_ = null; }  return this.sourceBuilder_; }
/*  7114 */       public boolean hasOperation() { return ((this.bitField0_ & 0x2) != 0); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.UpdateOperation.UpdateType getOperation() {
/*  7126 */         MysqlxCrud.UpdateOperation.UpdateType result = MysqlxCrud.UpdateOperation.UpdateType.valueOf(this.operation_);
/*  7127 */         return (result == null) ? MysqlxCrud.UpdateOperation.UpdateType.SET : result;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setOperation(MysqlxCrud.UpdateOperation.UpdateType value) {
/*  7139 */         if (value == null) {
/*  7140 */           throw new NullPointerException();
/*       */         }
/*  7142 */         this.bitField0_ |= 0x2;
/*  7143 */         this.operation_ = value.getNumber();
/*  7144 */         onChanged();
/*  7145 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearOperation() {
/*  7156 */         this.bitField0_ &= 0xFFFFFFFD;
/*  7157 */         this.operation_ = 1;
/*  7158 */         onChanged();
/*  7159 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasValue() {
/*  7174 */         return ((this.bitField0_ & 0x4) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr getValue() {
/*  7185 */         if (this.valueBuilder_ == null) {
/*  7186 */           return (this.value_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
/*       */         }
/*  7188 */         return (MysqlxExpr.Expr)this.valueBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setValue(MysqlxExpr.Expr value) {
/*  7199 */         if (this.valueBuilder_ == null) {
/*  7200 */           if (value == null) {
/*  7201 */             throw new NullPointerException();
/*       */           }
/*  7203 */           this.value_ = value;
/*  7204 */           onChanged();
/*       */         } else {
/*  7206 */           this.valueBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/*  7208 */         this.bitField0_ |= 0x4;
/*  7209 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setValue(MysqlxExpr.Expr.Builder builderForValue) {
/*  7220 */         if (this.valueBuilder_ == null) {
/*  7221 */           this.value_ = builderForValue.build();
/*  7222 */           onChanged();
/*       */         } else {
/*  7224 */           this.valueBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/*  7226 */         this.bitField0_ |= 0x4;
/*  7227 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeValue(MysqlxExpr.Expr value) {
/*  7237 */         if (this.valueBuilder_ == null) {
/*  7238 */           if ((this.bitField0_ & 0x4) != 0 && this.value_ != null && this.value_ != 
/*       */             
/*  7240 */             MysqlxExpr.Expr.getDefaultInstance()) {
/*  7241 */             this
/*  7242 */               .value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial();
/*       */           } else {
/*  7244 */             this.value_ = value;
/*       */           } 
/*  7246 */           onChanged();
/*       */         } else {
/*  7248 */           this.valueBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/*  7250 */         this.bitField0_ |= 0x4;
/*  7251 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearValue() {
/*  7261 */         if (this.valueBuilder_ == null) {
/*  7262 */           this.value_ = null;
/*  7263 */           onChanged();
/*       */         } else {
/*  7265 */           this.valueBuilder_.clear();
/*       */         } 
/*  7267 */         this.bitField0_ &= 0xFFFFFFFB;
/*  7268 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.Expr.Builder getValueBuilder() {
/*  7278 */         this.bitField0_ |= 0x4;
/*  7279 */         onChanged();
/*  7280 */         return (MysqlxExpr.Expr.Builder)getValueFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
/*  7290 */         if (this.valueBuilder_ != null) {
/*  7291 */           return (MysqlxExpr.ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder();
/*       */         }
/*  7293 */         return (this.value_ == null) ? 
/*  7294 */           MysqlxExpr.Expr.getDefaultInstance() : this.value_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
/*  7307 */         if (this.valueBuilder_ == null) {
/*  7308 */           this
/*       */ 
/*       */ 
/*       */             
/*  7312 */             .valueBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getValue(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/*  7313 */           this.value_ = null;
/*       */         } 
/*  7315 */         return this.valueBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  7320 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  7326 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       } }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*  7336 */     private static final UpdateOperation DEFAULT_INSTANCE = new UpdateOperation();
/*       */ 
/*       */     
/*       */     public static UpdateOperation getDefaultInstance() {
/*  7340 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/*  7344 */     public static final Parser<UpdateOperation> PARSER = (Parser<UpdateOperation>)new AbstractParser<UpdateOperation>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.UpdateOperation parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/*  7350 */           return new MysqlxCrud.UpdateOperation(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<UpdateOperation> parser() {
/*  7355 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<UpdateOperation> getParserForType() {
/*  7360 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public UpdateOperation getDefaultInstanceForType() {
/*  7365 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface FindOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Collection getCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.DataModel getDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxCrud.Projection> getProjectionList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Projection getProjection(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getProjectionCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxDatatypes.Scalar> getArgsList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.Scalar getArgs(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getArgsCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLimit();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Limit getLimit();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitOrBuilder getLimitOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxCrud.Order> getOrderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Order getOrder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getOrderCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxExpr.Expr> getGroupingList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getGrouping(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getGroupingCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasGroupingCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getGroupingCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLocking();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Find.RowLock getLocking();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLockingOptions();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Find.RowLockOptions getLockingOptions();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLimitExpr();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitExpr getLimitExpr();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Find
/*       */     extends GeneratedMessageV3
/*       */     implements FindOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLLECTION_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Collection collection_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DATA_MODEL_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int dataModel_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int PROJECTION_FIELD_NUMBER = 4;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxCrud.Projection> projection_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ARGS_FIELD_NUMBER = 11;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxDatatypes.Scalar> args_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int CRITERIA_FIELD_NUMBER = 5;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr criteria_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LIMIT_FIELD_NUMBER = 6;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Limit limit_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ORDER_FIELD_NUMBER = 7;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxCrud.Order> order_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int GROUPING_FIELD_NUMBER = 8;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxExpr.Expr> grouping_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int GROUPING_CRITERIA_FIELD_NUMBER = 9;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr groupingCriteria_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LOCKING_FIELD_NUMBER = 12;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int locking_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LOCKING_OPTIONS_FIELD_NUMBER = 13;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int lockingOptions_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LIMIT_EXPR_FIELD_NUMBER = 14;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.LimitExpr limitExpr_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private Find(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/*  7768 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*  8703 */       this.memoizedIsInitialized = -1; } private Find() { this.memoizedIsInitialized = -1; this.dataModel_ = 1; this.projection_ = Collections.emptyList(); this.args_ = Collections.emptyList(); this.order_ = Collections.emptyList(); this.grouping_ = Collections.emptyList(); this.locking_ = 1; this.lockingOptions_ = 1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Find(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Find(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxCrud.Collection.Builder builder3; int i; MysqlxExpr.Expr.Builder builder2; MysqlxCrud.Limit.Builder builder; MysqlxExpr.Expr.Builder builder1; int rawValue; MysqlxCrud.LimitExpr.Builder subBuilder; MysqlxCrud.DataModel dataModel; RowLock rowLock; RowLockOptions value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 18: builder3 = null; if ((this.bitField0_ & 0x1) != 0) builder3 = this.collection_.toBuilder();  this.collection_ = (MysqlxCrud.Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry); if (builder3 != null) { builder3.mergeFrom(this.collection_); this.collection_ = builder3.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 24: i = input.readEnum(); dataModel = MysqlxCrud.DataModel.valueOf(i); if (dataModel == null) { unknownFields.mergeVarintField(3, i); continue; }  this.bitField0_ |= 0x2; this.dataModel_ = i; continue;case 34: if ((mutable_bitField0_ & 0x4) == 0) { this.projection_ = new ArrayList<>(); mutable_bitField0_ |= 0x4; }  this.projection_.add(input.readMessage(MysqlxCrud.Projection.PARSER, extensionRegistry)); continue;case 42: builder2 = null; if ((this.bitField0_ & 0x4) != 0) builder2 = this.criteria_.toBuilder();  this.criteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (builder2 != null) { builder2.mergeFrom(this.criteria_); this.criteria_ = builder2.buildPartial(); }  this.bitField0_ |= 0x4; continue;case 50: builder = null; if ((this.bitField0_ & 0x8) != 0) builder = this.limit_.toBuilder();  this.limit_ = (MysqlxCrud.Limit)input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.limit_); this.limit_ = builder.buildPartial(); }  this.bitField0_ |= 0x8; continue;case 58: if ((mutable_bitField0_ & 0x40) == 0) { this.order_ = new ArrayList<>(); mutable_bitField0_ |= 0x40; }  this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry)); continue;case 66: if ((mutable_bitField0_ & 0x80) == 0) { this.grouping_ = new ArrayList<>(); mutable_bitField0_ |= 0x80; }  this.grouping_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry)); continue;case 74: builder1 = null; if ((this.bitField0_ & 0x10) != 0) builder1 = this.groupingCriteria_.toBuilder();  this.groupingCriteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (builder1 != null) { builder1.mergeFrom(this.groupingCriteria_); this.groupingCriteria_ = builder1.buildPartial(); }  this.bitField0_ |= 0x10; continue;case 90: if ((mutable_bitField0_ & 0x8) == 0) { this.args_ = new ArrayList<>(); mutable_bitField0_ |= 0x8; }  this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry)); continue;case 96: rawValue = input.readEnum(); rowLock = RowLock.valueOf(rawValue); if (rowLock == null) { unknownFields.mergeVarintField(12, rawValue); continue; }  this.bitField0_ |= 0x20; this.locking_ = rawValue; continue;case 104: rawValue = input.readEnum(); value = RowLockOptions.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(13, rawValue); continue; }  this.bitField0_ |= 0x40; this.lockingOptions_ = rawValue; continue;case 114: subBuilder = null; if ((this.bitField0_ & 0x80) != 0) subBuilder = this.limitExpr_.toBuilder();  this.limitExpr_ = (MysqlxCrud.LimitExpr)input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.limitExpr_); this.limitExpr_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x80; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x4) != 0) this.projection_ = Collections.unmodifiableList(this.projection_);  if ((mutable_bitField0_ & 0x40) != 0) this.order_ = Collections.unmodifiableList(this.order_);  if ((mutable_bitField0_ & 0x80) != 0) this.grouping_ = Collections.unmodifiableList(this.grouping_);  if ((mutable_bitField0_ & 0x8) != 0) this.args_ = Collections.unmodifiableList(this.args_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable.ensureFieldAccessorsInitialized(Find.class, Builder.class); } public enum RowLock implements ProtocolMessageEnum {
/*       */       SHARED_LOCK(1), EXCLUSIVE_LOCK(2); public static final int SHARED_LOCK_VALUE = 1; public static final int EXCLUSIVE_LOCK_VALUE = 2; private static final Internal.EnumLiteMap<RowLock> internalValueMap = new Internal.EnumLiteMap<RowLock>() { public MysqlxCrud.Find.RowLock findValueByNumber(int number) { return MysqlxCrud.Find.RowLock.forNumber(number); } }
/*       */       ; private static final RowLock[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static RowLock forNumber(int value) { switch (value) { case 1: return SHARED_LOCK;case 2: return EXCLUSIVE_LOCK; }  return null; } public static Internal.EnumLiteMap<RowLock> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.Find.getDescriptor().getEnumTypes().get(0); } RowLock(int value) { this.value = value; } } public enum RowLockOptions implements ProtocolMessageEnum {
/*  8706 */       NOWAIT(1), SKIP_LOCKED(2); public static final int NOWAIT_VALUE = 1; public static final int SKIP_LOCKED_VALUE = 2; private static final Internal.EnumLiteMap<RowLockOptions> internalValueMap = new Internal.EnumLiteMap<RowLockOptions>() { public MysqlxCrud.Find.RowLockOptions findValueByNumber(int number) { return MysqlxCrud.Find.RowLockOptions.forNumber(number); } }; private static final RowLockOptions[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static RowLockOptions forNumber(int value) { switch (value) { case 1: return NOWAIT;case 2: return SKIP_LOCKED; }  return null; } public static Internal.EnumLiteMap<RowLockOptions> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return MysqlxCrud.Find.getDescriptor().getEnumTypes().get(1); } RowLockOptions(int value) { this.value = value; } } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public List<MysqlxCrud.Projection> getProjectionList() { return this.projection_; } public List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList() { return (List)this.projection_; } public int getProjectionCount() { return this.projection_.size(); } public MysqlxCrud.Projection getProjection(int index) { return this.projection_.get(index); } public MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int index) { return this.projection_.get(index); } public List<MysqlxDatatypes.Scalar> getArgsList() { return this.args_; } public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() { return (List)this.args_; } public int getArgsCount() { return this.args_.size(); } public MysqlxDatatypes.Scalar getArgs(int index) { return this.args_.get(index); } public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) { return this.args_.get(index); } public boolean hasCriteria() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxExpr.Expr getCriteria() { return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() { return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } public boolean hasLimit() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Limit getLimit() { return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() { return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } public List<MysqlxCrud.Order> getOrderList() { return this.order_; } public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() { return (List)this.order_; } public int getOrderCount() { return this.order_.size(); } public MysqlxCrud.Order getOrder(int index) { return this.order_.get(index); } public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) { return this.order_.get(index); } public List<MysqlxExpr.Expr> getGroupingList() { return this.grouping_; } public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() { return (List)this.grouping_; } public int getGroupingCount() { return this.grouping_.size(); } public MysqlxExpr.Expr getGrouping(int index) { return this.grouping_.get(index); } public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) { return this.grouping_.get(index); } public boolean hasGroupingCriteria() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxExpr.Expr getGroupingCriteria() { return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_; } public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() { return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_; } public boolean hasLocking() { return ((this.bitField0_ & 0x20) != 0); } public RowLock getLocking() { RowLock result = RowLock.valueOf(this.locking_); return (result == null) ? RowLock.SHARED_LOCK : result; } public boolean hasLockingOptions() { return ((this.bitField0_ & 0x40) != 0); } public RowLockOptions getLockingOptions() { RowLockOptions result = RowLockOptions.valueOf(this.lockingOptions_); return (result == null) ? RowLockOptions.NOWAIT : result; } public boolean hasLimitExpr() { return ((this.bitField0_ & 0x80) != 0); } public MysqlxCrud.LimitExpr getLimitExpr() { return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; } public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() { return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  8707 */       if (isInitialized == 1) return true; 
/*  8708 */       if (isInitialized == 0) return false;
/*       */       
/*  8710 */       if (!hasCollection()) {
/*  8711 */         this.memoizedIsInitialized = 0;
/*  8712 */         return false;
/*       */       } 
/*  8714 */       if (!getCollection().isInitialized()) {
/*  8715 */         this.memoizedIsInitialized = 0;
/*  8716 */         return false;
/*       */       }  int i;
/*  8718 */       for (i = 0; i < getProjectionCount(); i++) {
/*  8719 */         if (!getProjection(i).isInitialized()) {
/*  8720 */           this.memoizedIsInitialized = 0;
/*  8721 */           return false;
/*       */         } 
/*       */       } 
/*  8724 */       for (i = 0; i < getArgsCount(); i++) {
/*  8725 */         if (!getArgs(i).isInitialized()) {
/*  8726 */           this.memoizedIsInitialized = 0;
/*  8727 */           return false;
/*       */         } 
/*       */       } 
/*  8730 */       if (hasCriteria() && 
/*  8731 */         !getCriteria().isInitialized()) {
/*  8732 */         this.memoizedIsInitialized = 0;
/*  8733 */         return false;
/*       */       } 
/*       */       
/*  8736 */       if (hasLimit() && 
/*  8737 */         !getLimit().isInitialized()) {
/*  8738 */         this.memoizedIsInitialized = 0;
/*  8739 */         return false;
/*       */       } 
/*       */       
/*  8742 */       for (i = 0; i < getOrderCount(); i++) {
/*  8743 */         if (!getOrder(i).isInitialized()) {
/*  8744 */           this.memoizedIsInitialized = 0;
/*  8745 */           return false;
/*       */         } 
/*       */       } 
/*  8748 */       for (i = 0; i < getGroupingCount(); i++) {
/*  8749 */         if (!getGrouping(i).isInitialized()) {
/*  8750 */           this.memoizedIsInitialized = 0;
/*  8751 */           return false;
/*       */         } 
/*       */       } 
/*  8754 */       if (hasGroupingCriteria() && 
/*  8755 */         !getGroupingCriteria().isInitialized()) {
/*  8756 */         this.memoizedIsInitialized = 0;
/*  8757 */         return false;
/*       */       } 
/*       */       
/*  8760 */       if (hasLimitExpr() && 
/*  8761 */         !getLimitExpr().isInitialized()) {
/*  8762 */         this.memoizedIsInitialized = 0;
/*  8763 */         return false;
/*       */       } 
/*       */       
/*  8766 */       this.memoizedIsInitialized = 1;
/*  8767 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/*  8773 */       if ((this.bitField0_ & 0x1) != 0) {
/*  8774 */         output.writeMessage(2, (MessageLite)getCollection());
/*       */       }
/*  8776 */       if ((this.bitField0_ & 0x2) != 0)
/*  8777 */         output.writeEnum(3, this.dataModel_); 
/*       */       int i;
/*  8779 */       for (i = 0; i < this.projection_.size(); i++) {
/*  8780 */         output.writeMessage(4, (MessageLite)this.projection_.get(i));
/*       */       }
/*  8782 */       if ((this.bitField0_ & 0x4) != 0) {
/*  8783 */         output.writeMessage(5, (MessageLite)getCriteria());
/*       */       }
/*  8785 */       if ((this.bitField0_ & 0x8) != 0) {
/*  8786 */         output.writeMessage(6, (MessageLite)getLimit());
/*       */       }
/*  8788 */       for (i = 0; i < this.order_.size(); i++) {
/*  8789 */         output.writeMessage(7, (MessageLite)this.order_.get(i));
/*       */       }
/*  8791 */       for (i = 0; i < this.grouping_.size(); i++) {
/*  8792 */         output.writeMessage(8, (MessageLite)this.grouping_.get(i));
/*       */       }
/*  8794 */       if ((this.bitField0_ & 0x10) != 0) {
/*  8795 */         output.writeMessage(9, (MessageLite)getGroupingCriteria());
/*       */       }
/*  8797 */       for (i = 0; i < this.args_.size(); i++) {
/*  8798 */         output.writeMessage(11, (MessageLite)this.args_.get(i));
/*       */       }
/*  8800 */       if ((this.bitField0_ & 0x20) != 0) {
/*  8801 */         output.writeEnum(12, this.locking_);
/*       */       }
/*  8803 */       if ((this.bitField0_ & 0x40) != 0) {
/*  8804 */         output.writeEnum(13, this.lockingOptions_);
/*       */       }
/*  8806 */       if ((this.bitField0_ & 0x80) != 0) {
/*  8807 */         output.writeMessage(14, (MessageLite)getLimitExpr());
/*       */       }
/*  8809 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/*  8814 */       int size = this.memoizedSize;
/*  8815 */       if (size != -1) return size;
/*       */       
/*  8817 */       size = 0;
/*  8818 */       if ((this.bitField0_ & 0x1) != 0) {
/*  8819 */         size += 
/*  8820 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getCollection());
/*       */       }
/*  8822 */       if ((this.bitField0_ & 0x2) != 0)
/*  8823 */         size += 
/*  8824 */           CodedOutputStream.computeEnumSize(3, this.dataModel_); 
/*       */       int i;
/*  8826 */       for (i = 0; i < this.projection_.size(); i++) {
/*  8827 */         size += 
/*  8828 */           CodedOutputStream.computeMessageSize(4, (MessageLite)this.projection_.get(i));
/*       */       }
/*  8830 */       if ((this.bitField0_ & 0x4) != 0) {
/*  8831 */         size += 
/*  8832 */           CodedOutputStream.computeMessageSize(5, (MessageLite)getCriteria());
/*       */       }
/*  8834 */       if ((this.bitField0_ & 0x8) != 0) {
/*  8835 */         size += 
/*  8836 */           CodedOutputStream.computeMessageSize(6, (MessageLite)getLimit());
/*       */       }
/*  8838 */       for (i = 0; i < this.order_.size(); i++) {
/*  8839 */         size += 
/*  8840 */           CodedOutputStream.computeMessageSize(7, (MessageLite)this.order_.get(i));
/*       */       }
/*  8842 */       for (i = 0; i < this.grouping_.size(); i++) {
/*  8843 */         size += 
/*  8844 */           CodedOutputStream.computeMessageSize(8, (MessageLite)this.grouping_.get(i));
/*       */       }
/*  8846 */       if ((this.bitField0_ & 0x10) != 0) {
/*  8847 */         size += 
/*  8848 */           CodedOutputStream.computeMessageSize(9, (MessageLite)getGroupingCriteria());
/*       */       }
/*  8850 */       for (i = 0; i < this.args_.size(); i++) {
/*  8851 */         size += 
/*  8852 */           CodedOutputStream.computeMessageSize(11, (MessageLite)this.args_.get(i));
/*       */       }
/*  8854 */       if ((this.bitField0_ & 0x20) != 0) {
/*  8855 */         size += 
/*  8856 */           CodedOutputStream.computeEnumSize(12, this.locking_);
/*       */       }
/*  8858 */       if ((this.bitField0_ & 0x40) != 0) {
/*  8859 */         size += 
/*  8860 */           CodedOutputStream.computeEnumSize(13, this.lockingOptions_);
/*       */       }
/*  8862 */       if ((this.bitField0_ & 0x80) != 0) {
/*  8863 */         size += 
/*  8864 */           CodedOutputStream.computeMessageSize(14, (MessageLite)getLimitExpr());
/*       */       }
/*  8866 */       size += this.unknownFields.getSerializedSize();
/*  8867 */       this.memoizedSize = size;
/*  8868 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/*  8873 */       if (obj == this) {
/*  8874 */         return true;
/*       */       }
/*  8876 */       if (!(obj instanceof Find)) {
/*  8877 */         return super.equals(obj);
/*       */       }
/*  8879 */       Find other = (Find)obj;
/*       */       
/*  8881 */       if (hasCollection() != other.hasCollection()) return false; 
/*  8882 */       if (hasCollection() && 
/*       */         
/*  8884 */         !getCollection().equals(other.getCollection())) return false;
/*       */       
/*  8886 */       if (hasDataModel() != other.hasDataModel()) return false; 
/*  8887 */       if (hasDataModel() && 
/*  8888 */         this.dataModel_ != other.dataModel_) return false;
/*       */ 
/*       */       
/*  8891 */       if (!getProjectionList().equals(other.getProjectionList())) return false;
/*       */       
/*  8893 */       if (!getArgsList().equals(other.getArgsList())) return false; 
/*  8894 */       if (hasCriteria() != other.hasCriteria()) return false; 
/*  8895 */       if (hasCriteria() && 
/*       */         
/*  8897 */         !getCriteria().equals(other.getCriteria())) return false;
/*       */       
/*  8899 */       if (hasLimit() != other.hasLimit()) return false; 
/*  8900 */       if (hasLimit() && 
/*       */         
/*  8902 */         !getLimit().equals(other.getLimit())) return false;
/*       */ 
/*       */       
/*  8905 */       if (!getOrderList().equals(other.getOrderList())) return false;
/*       */       
/*  8907 */       if (!getGroupingList().equals(other.getGroupingList())) return false; 
/*  8908 */       if (hasGroupingCriteria() != other.hasGroupingCriteria()) return false; 
/*  8909 */       if (hasGroupingCriteria() && 
/*       */         
/*  8911 */         !getGroupingCriteria().equals(other.getGroupingCriteria())) return false;
/*       */       
/*  8913 */       if (hasLocking() != other.hasLocking()) return false; 
/*  8914 */       if (hasLocking() && 
/*  8915 */         this.locking_ != other.locking_) return false;
/*       */       
/*  8917 */       if (hasLockingOptions() != other.hasLockingOptions()) return false; 
/*  8918 */       if (hasLockingOptions() && 
/*  8919 */         this.lockingOptions_ != other.lockingOptions_) return false;
/*       */       
/*  8921 */       if (hasLimitExpr() != other.hasLimitExpr()) return false; 
/*  8922 */       if (hasLimitExpr() && 
/*       */         
/*  8924 */         !getLimitExpr().equals(other.getLimitExpr())) return false;
/*       */       
/*  8926 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  8927 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/*  8932 */       if (this.memoizedHashCode != 0) {
/*  8933 */         return this.memoizedHashCode;
/*       */       }
/*  8935 */       int hash = 41;
/*  8936 */       hash = 19 * hash + getDescriptor().hashCode();
/*  8937 */       if (hasCollection()) {
/*  8938 */         hash = 37 * hash + 2;
/*  8939 */         hash = 53 * hash + getCollection().hashCode();
/*       */       } 
/*  8941 */       if (hasDataModel()) {
/*  8942 */         hash = 37 * hash + 3;
/*  8943 */         hash = 53 * hash + this.dataModel_;
/*       */       } 
/*  8945 */       if (getProjectionCount() > 0) {
/*  8946 */         hash = 37 * hash + 4;
/*  8947 */         hash = 53 * hash + getProjectionList().hashCode();
/*       */       } 
/*  8949 */       if (getArgsCount() > 0) {
/*  8950 */         hash = 37 * hash + 11;
/*  8951 */         hash = 53 * hash + getArgsList().hashCode();
/*       */       } 
/*  8953 */       if (hasCriteria()) {
/*  8954 */         hash = 37 * hash + 5;
/*  8955 */         hash = 53 * hash + getCriteria().hashCode();
/*       */       } 
/*  8957 */       if (hasLimit()) {
/*  8958 */         hash = 37 * hash + 6;
/*  8959 */         hash = 53 * hash + getLimit().hashCode();
/*       */       } 
/*  8961 */       if (getOrderCount() > 0) {
/*  8962 */         hash = 37 * hash + 7;
/*  8963 */         hash = 53 * hash + getOrderList().hashCode();
/*       */       } 
/*  8965 */       if (getGroupingCount() > 0) {
/*  8966 */         hash = 37 * hash + 8;
/*  8967 */         hash = 53 * hash + getGroupingList().hashCode();
/*       */       } 
/*  8969 */       if (hasGroupingCriteria()) {
/*  8970 */         hash = 37 * hash + 9;
/*  8971 */         hash = 53 * hash + getGroupingCriteria().hashCode();
/*       */       } 
/*  8973 */       if (hasLocking()) {
/*  8974 */         hash = 37 * hash + 12;
/*  8975 */         hash = 53 * hash + this.locking_;
/*       */       } 
/*  8977 */       if (hasLockingOptions()) {
/*  8978 */         hash = 37 * hash + 13;
/*  8979 */         hash = 53 * hash + this.lockingOptions_;
/*       */       } 
/*  8981 */       if (hasLimitExpr()) {
/*  8982 */         hash = 37 * hash + 14;
/*  8983 */         hash = 53 * hash + getLimitExpr().hashCode();
/*       */       } 
/*  8985 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  8986 */       this.memoizedHashCode = hash;
/*  8987 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Find parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  8993 */       return (Find)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Find parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  8999 */       return (Find)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Find parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  9004 */       return (Find)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Find parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  9010 */       return (Find)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Find parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  9014 */       return (Find)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Find parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  9020 */       return (Find)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Find parseFrom(InputStream input) throws IOException {
/*  9024 */       return 
/*  9025 */         (Find)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Find parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  9031 */       return 
/*  9032 */         (Find)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Find parseDelimitedFrom(InputStream input) throws IOException {
/*  9036 */       return 
/*  9037 */         (Find)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Find parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  9043 */       return 
/*  9044 */         (Find)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Find parseFrom(CodedInputStream input) throws IOException {
/*  9049 */       return 
/*  9050 */         (Find)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Find parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  9056 */       return 
/*  9057 */         (Find)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/*  9061 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/*  9063 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Find prototype) {
/*  9066 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/*  9070 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  9071 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  9077 */       Builder builder = new Builder(parent);
/*  9078 */       return builder;
/*       */     }
/*       */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.FindOrBuilder { private int bitField0_; private MysqlxCrud.Collection collection_; private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_; private int dataModel_; private List<MysqlxCrud.Projection> projection_; private RepeatedFieldBuilderV3<MysqlxCrud.Projection, MysqlxCrud.Projection.Builder, MysqlxCrud.ProjectionOrBuilder> projectionBuilder_;
/*       */       private List<MysqlxDatatypes.Scalar> args_;
/*       */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
/*       */       private MysqlxExpr.Expr criteria_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
/*       */       private MysqlxCrud.Limit limit_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
/*       */       private List<MysqlxCrud.Order> order_;
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
/*       */       private List<MysqlxExpr.Expr> grouping_;
/*       */       private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingBuilder_;
/*       */       private MysqlxExpr.Expr groupingCriteria_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingCriteriaBuilder_;
/*       */       private int locking_;
/*       */       private int lockingOptions_;
/*       */       private MysqlxCrud.LimitExpr limitExpr_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/*  9099 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  9105 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable
/*  9106 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Find.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/*  9722 */         this.dataModel_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/*  9780 */         this
/*  9781 */           .projection_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 10092 */         this
/* 10093 */           .args_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 10725 */         this
/* 10726 */           .order_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 11037 */         this
/* 11038 */           .grouping_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 11505 */         this.locking_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 11563 */         this.lockingOptions_ = 1; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Find.alwaysUseFieldBuilders) { getCollectionFieldBuilder(); getProjectionFieldBuilder(); getArgsFieldBuilder(); getCriteriaFieldBuilder(); getLimitFieldBuilder(); getOrderFieldBuilder(); getGroupingFieldBuilder(); getGroupingCriteriaFieldBuilder(); getLimitExprFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.collectionBuilder_ == null) { this.collection_ = null; } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.dataModel_ = 1; this.bitField0_ &= 0xFFFFFFFD; if (this.projectionBuilder_ == null) { this.projection_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; } else { this.projectionBuilder_.clear(); }  if (this.argsBuilder_ == null) { this.args_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFF7; } else { this.argsBuilder_.clear(); }  if (this.criteriaBuilder_ == null) { this.criteria_ = null; } else { this.criteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFEF; if (this.limitBuilder_ == null) { this.limit_ = null; } else { this.limitBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFDF; if (this.orderBuilder_ == null) { this.order_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFBF; } else { this.orderBuilder_.clear(); }  if (this.groupingBuilder_ == null) { this.grouping_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFF7F; } else { this.groupingBuilder_.clear(); }  if (this.groupingCriteriaBuilder_ == null) { this.groupingCriteria_ = null; } else { this.groupingCriteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFEFF; this.locking_ = 1; this.bitField0_ &= 0xFFFFFDFF; this.lockingOptions_ = 1; this.bitField0_ &= 0xFFFFFBFF; if (this.limitExprBuilder_ == null) { this.limitExpr_ = null; } else { this.limitExprBuilder_.clear(); }  this.bitField0_ &= 0xFFFFF7FF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor; } public MysqlxCrud.Find getDefaultInstanceForType() { return MysqlxCrud.Find.getDefaultInstance(); } public MysqlxCrud.Find build() { MysqlxCrud.Find result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Find buildPartial() { MysqlxCrud.Find result = new MysqlxCrud.Find(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.collectionBuilder_ == null) { result.collection_ = this.collection_; } else { result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.dataModel_ = this.dataModel_; if (this.projectionBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0) { this.projection_ = Collections.unmodifiableList(this.projection_); this.bitField0_ &= 0xFFFFFFFB; }  result.projection_ = this.projection_; } else { result.projection_ = this.projectionBuilder_.build(); }  if (this.argsBuilder_ == null) { if ((this.bitField0_ & 0x8) != 0) { this.args_ = Collections.unmodifiableList(this.args_); this.bitField0_ &= 0xFFFFFFF7; }  result.args_ = this.args_; } else { result.args_ = this.argsBuilder_.build(); }  if ((from_bitField0_ & 0x10) != 0) { if (this.criteriaBuilder_ == null) { result.criteria_ = this.criteria_; } else { result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build(); }  to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x20) != 0) { if (this.limitBuilder_ == null) { result.limit_ = this.limit_; } else { result.limit_ = (MysqlxCrud.Limit)this.limitBuilder_.build(); }  to_bitField0_ |= 0x8; }  if (this.orderBuilder_ == null) { if ((this.bitField0_ & 0x40) != 0) { this.order_ = Collections.unmodifiableList(this.order_); this.bitField0_ &= 0xFFFFFFBF; }  result.order_ = this.order_; } else { result.order_ = this.orderBuilder_.build(); }  if (this.groupingBuilder_ == null) { if ((this.bitField0_ & 0x80) != 0) { this.grouping_ = Collections.unmodifiableList(this.grouping_); this.bitField0_ &= 0xFFFFFF7F; }  result.grouping_ = this.grouping_; } else { result.grouping_ = this.groupingBuilder_.build(); }  if ((from_bitField0_ & 0x100) != 0) { if (this.groupingCriteriaBuilder_ == null) { result.groupingCriteria_ = this.groupingCriteria_; } else { result.groupingCriteria_ = (MysqlxExpr.Expr)this.groupingCriteriaBuilder_.build(); }  to_bitField0_ |= 0x10; }  if ((from_bitField0_ & 0x200) != 0) to_bitField0_ |= 0x20;  result.locking_ = this.locking_; if ((from_bitField0_ & 0x400) != 0) to_bitField0_ |= 0x40;  result.lockingOptions_ = this.lockingOptions_; if ((from_bitField0_ & 0x800) != 0) { if (this.limitExprBuilder_ == null) { result.limitExpr_ = this.limitExpr_; } else { result.limitExpr_ = (MysqlxCrud.LimitExpr)this.limitExprBuilder_.build(); }  to_bitField0_ |= 0x80; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Find) return mergeFrom((MysqlxCrud.Find)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Find other) { if (other == MysqlxCrud.Find.getDefaultInstance()) return this;  if (other.hasCollection()) mergeCollection(other.getCollection());  if (other.hasDataModel()) setDataModel(other.getDataModel());  if (this.projectionBuilder_ == null) { if (!other.projection_.isEmpty()) { if (this.projection_.isEmpty()) { this.projection_ = other.projection_; this.bitField0_ &= 0xFFFFFFFB; } else { ensureProjectionIsMutable(); this.projection_.addAll(other.projection_); }  onChanged(); }  } else if (!other.projection_.isEmpty()) { if (this.projectionBuilder_.isEmpty()) { this.projectionBuilder_.dispose(); this.projectionBuilder_ = null; this.projection_ = other.projection_; this.bitField0_ &= 0xFFFFFFFB; this.projectionBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getProjectionFieldBuilder() : null; } else { this.projectionBuilder_.addAllMessages(other.projection_); }  }  if (this.argsBuilder_ == null) { if (!other.args_.isEmpty()) { if (this.args_.isEmpty()) { this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFF7; } else { ensureArgsIsMutable(); this.args_.addAll(other.args_); }  onChanged(); }  } else if (!other.args_.isEmpty()) { if (this.argsBuilder_.isEmpty()) { this.argsBuilder_.dispose(); this.argsBuilder_ = null; this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFF7; this.argsBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null; } else { this.argsBuilder_.addAllMessages(other.args_); }  }  if (other.hasCriteria()) mergeCriteria(other.getCriteria());  if (other.hasLimit()) mergeLimit(other.getLimit());  if (this.orderBuilder_ == null) { if (!other.order_.isEmpty()) { if (this.order_.isEmpty()) { this.order_ = other.order_; this.bitField0_ &= 0xFFFFFFBF; } else { ensureOrderIsMutable(); this.order_.addAll(other.order_); }  onChanged(); }  } else if (!other.order_.isEmpty()) { if (this.orderBuilder_.isEmpty()) { this.orderBuilder_.dispose(); this.orderBuilder_ = null; this.order_ = other.order_; this.bitField0_ &= 0xFFFFFFBF; this.orderBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getOrderFieldBuilder() : null; } else { this.orderBuilder_.addAllMessages(other.order_); }  }  if (this.groupingBuilder_ == null) { if (!other.grouping_.isEmpty()) { if (this.grouping_.isEmpty()) { this.grouping_ = other.grouping_; this.bitField0_ &= 0xFFFFFF7F; } else { ensureGroupingIsMutable(); this.grouping_.addAll(other.grouping_); }  onChanged(); }  } else if (!other.grouping_.isEmpty()) { if (this.groupingBuilder_.isEmpty()) { this.groupingBuilder_.dispose(); this.groupingBuilder_ = null; this.grouping_ = other.grouping_; this.bitField0_ &= 0xFFFFFF7F; this.groupingBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? getGroupingFieldBuilder() : null; } else { this.groupingBuilder_.addAllMessages(other.grouping_); }  }  if (other.hasGroupingCriteria()) mergeGroupingCriteria(other.getGroupingCriteria());  if (other.hasLocking()) setLocking(other.getLocking());  if (other.hasLockingOptions()) setLockingOptions(other.getLockingOptions());  if (other.hasLimitExpr()) mergeLimitExpr(other.getLimitExpr());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasCollection()) return false;  if (!getCollection().isInitialized()) return false;  int i; for (i = 0; i < getProjectionCount(); i++) { if (!getProjection(i).isInitialized()) return false;  }  for (i = 0; i < getArgsCount(); i++) { if (!getArgs(i).isInitialized()) return false;  }  if (hasCriteria() && !getCriteria().isInitialized()) return false;  if (hasLimit() && !getLimit().isInitialized()) return false;  for (i = 0; i < getOrderCount(); i++) { if (!getOrder(i).isInitialized()) return false;  }  for (i = 0; i < getGroupingCount(); i++) { if (!getGrouping(i).isInitialized()) return false;  }  if (hasGroupingCriteria() && !getGroupingCriteria().isInitialized()) return false;  if (hasLimitExpr() && !getLimitExpr().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Find parsedMessage = null; try { parsedMessage = (MysqlxCrud.Find)MysqlxCrud.Find.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Find)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { if (this.collectionBuilder_ == null) return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;  return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage(); } public Builder setCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if (value == null) throw new NullPointerException();  this.collection_ = value; onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) { if (this.collectionBuilder_ == null) { this.collection_ = builderForValue.build(); onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; } public Builder mergeCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) { this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial(); } else { this.collection_ = value; }  onChanged(); } else { this.collectionBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder clearCollection() { if (this.collectionBuilder_ == null) { this.collection_ = null; onChanged(); } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; } public MysqlxCrud.Collection.Builder getCollectionBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder(); } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { if (this.collectionBuilder_ != null) return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();  return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() { if (this.collectionBuilder_ == null) { this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.collection_ = null; }  return this.collectionBuilder_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public Builder setDataModel(MysqlxCrud.DataModel value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.dataModel_ = value.getNumber(); onChanged(); return this; } public Builder clearDataModel() { this.bitField0_ &= 0xFFFFFFFD; this.dataModel_ = 1; onChanged(); return this; } private void ensureProjectionIsMutable() { if ((this.bitField0_ & 0x4) == 0) { this.projection_ = new ArrayList<>(this.projection_); this.bitField0_ |= 0x4; }  } public List<MysqlxCrud.Projection> getProjectionList() { if (this.projectionBuilder_ == null) return Collections.unmodifiableList(this.projection_);  return this.projectionBuilder_.getMessageList(); } public int getProjectionCount() { if (this.projectionBuilder_ == null) return this.projection_.size();  return this.projectionBuilder_.getCount(); } public MysqlxCrud.Projection getProjection(int index) { if (this.projectionBuilder_ == null) return this.projection_.get(index);  return (MysqlxCrud.Projection)this.projectionBuilder_.getMessage(index); } public Builder setProjection(int index, MysqlxCrud.Projection value) { if (this.projectionBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureProjectionIsMutable(); this.projection_.set(index, value); onChanged(); } else { this.projectionBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setProjection(int index, MysqlxCrud.Projection.Builder builderForValue) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.set(index, builderForValue.build()); onChanged(); } else { this.projectionBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.dataModel_ = 1; this.projection_ = Collections.emptyList(); this.args_ = Collections.emptyList(); this.order_ = Collections.emptyList(); this.grouping_ = Collections.emptyList(); this.locking_ = 1; this.lockingOptions_ = 1; maybeForceBuilderInitialization(); }
/*       */       public Builder addProjection(MysqlxCrud.Projection value) { if (this.projectionBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureProjectionIsMutable(); this.projection_.add(value); onChanged(); } else { this.projectionBuilder_.addMessage((AbstractMessage)value); }  return this; }
/*       */       public Builder addProjection(int index, MysqlxCrud.Projection value) { if (this.projectionBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureProjectionIsMutable(); this.projection_.add(index, value); onChanged(); } else { this.projectionBuilder_.addMessage(index, (AbstractMessage)value); }  return this; }
/*       */       public Builder addProjection(MysqlxCrud.Projection.Builder builderForValue) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.add(builderForValue.build()); onChanged(); } else { this.projectionBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; }
/*       */       public Builder addProjection(int index, MysqlxCrud.Projection.Builder builderForValue) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.add(index, builderForValue.build()); onChanged(); } else { this.projectionBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; }
/*       */       public Builder addAllProjection(Iterable<? extends MysqlxCrud.Projection> values) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); AbstractMessageLite.Builder.addAll(values, this.projection_); onChanged(); } else { this.projectionBuilder_.addAllMessages(values); }  return this; }
/*       */       public Builder clearProjection() { if (this.projectionBuilder_ == null) { this.projection_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; onChanged(); } else { this.projectionBuilder_.clear(); }  return this; }
/*       */       public Builder removeProjection(int index) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.remove(index); onChanged(); } else { this.projectionBuilder_.remove(index); }  return this; }
/*       */       public MysqlxCrud.Projection.Builder getProjectionBuilder(int index) { return (MysqlxCrud.Projection.Builder)getProjectionFieldBuilder().getBuilder(index); }
/*       */       public MysqlxCrud.ProjectionOrBuilder getProjectionOrBuilder(int index) { if (this.projectionBuilder_ == null) return this.projection_.get(index);  return (MysqlxCrud.ProjectionOrBuilder)this.projectionBuilder_.getMessageOrBuilder(index); }
/* 11573 */       public List<? extends MysqlxCrud.ProjectionOrBuilder> getProjectionOrBuilderList() { if (this.projectionBuilder_ != null) return this.projectionBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.projection_); } public MysqlxCrud.Projection.Builder addProjectionBuilder() { return (MysqlxCrud.Projection.Builder)getProjectionFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Projection.getDefaultInstance()); } public MysqlxCrud.Projection.Builder addProjectionBuilder(int index) { return (MysqlxCrud.Projection.Builder)getProjectionFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Projection.getDefaultInstance()); } public List<MysqlxCrud.Projection.Builder> getProjectionBuilderList() { return getProjectionFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxCrud.Projection, MysqlxCrud.Projection.Builder, MysqlxCrud.ProjectionOrBuilder> getProjectionFieldBuilder() { if (this.projectionBuilder_ == null) { this.projectionBuilder_ = new RepeatedFieldBuilderV3(this.projection_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.projection_ = null; }  return this.projectionBuilder_; } private void ensureArgsIsMutable() { if ((this.bitField0_ & 0x8) == 0) { this.args_ = new ArrayList<>(this.args_); this.bitField0_ |= 0x8; }  } public List<MysqlxDatatypes.Scalar> getArgsList() { if (this.argsBuilder_ == null) return Collections.unmodifiableList(this.args_);  return this.argsBuilder_.getMessageList(); } public int getArgsCount() { if (this.argsBuilder_ == null) return this.args_.size();  return this.argsBuilder_.getCount(); } public MysqlxDatatypes.Scalar getArgs(int index) { if (this.argsBuilder_ == null) return this.args_.get(index);  return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index); } public Builder setArgs(int index, MysqlxDatatypes.Scalar value) { if (this.argsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureArgsIsMutable(); this.args_.set(index, value); onChanged(); } else { this.argsBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) { if (this.argsBuilder_ == null) { ensureArgsIsMutable(); this.args_.set(index, builderForValue.build()); onChanged(); } else { this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addArgs(MysqlxDatatypes.Scalar value) { if (this.argsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureArgsIsMutable(); this.args_.add(value); onChanged(); } else { this.argsBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addArgs(int index, MysqlxDatatypes.Scalar value) { if (this.argsBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureArgsIsMutable(); this.args_.add(index, value); onChanged(); } else { this.argsBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) { if (this.argsBuilder_ == null) { ensureArgsIsMutable(); this.args_.add(builderForValue.build()); onChanged(); } else { this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) { if (this.argsBuilder_ == null) { ensureArgsIsMutable(); this.args_.add(index, builderForValue.build()); onChanged(); } else { this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) { if (this.argsBuilder_ == null) { ensureArgsIsMutable(); AbstractMessageLite.Builder.addAll(values, this.args_); onChanged(); } else { this.argsBuilder_.addAllMessages(values); }  return this; } public Builder clearArgs() { if (this.argsBuilder_ == null) { this.args_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFF7; onChanged(); } else { this.argsBuilder_.clear(); }  return this; } public Builder removeArgs(int index) { if (this.argsBuilder_ == null) { ensureArgsIsMutable(); this.args_.remove(index); onChanged(); } else { this.argsBuilder_.remove(index); }  return this; } public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) { return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index); } public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) { if (this.argsBuilder_ == null) return this.args_.get(index);  return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() { if (this.argsBuilder_ != null) return this.argsBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.args_); } public MysqlxDatatypes.Scalar.Builder addArgsBuilder() { return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder((AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance()); } public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) { return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance()); } public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() { return getArgsFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() { if (this.argsBuilder_ == null) { this.argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x8) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.args_ = null; }  return this.argsBuilder_; } public boolean hasCriteria() { return ((this.bitField0_ & 0x10) != 0); } public boolean hasLockingOptions() { return ((this.bitField0_ & 0x400) != 0); } public MysqlxExpr.Expr getCriteria() { if (this.criteriaBuilder_ == null) return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;  return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage(); } public Builder setCriteria(MysqlxExpr.Expr value) { if (this.criteriaBuilder_ == null) { if (value == null) throw new NullPointerException();  this.criteria_ = value; onChanged(); } else { this.criteriaBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x10; return this; } public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) { if (this.criteriaBuilder_ == null) { this.criteria_ = builderForValue.build(); onChanged(); } else { this.criteriaBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x10; return this; } public Builder mergeCriteria(MysqlxExpr.Expr value) { if (this.criteriaBuilder_ == null) { if ((this.bitField0_ & 0x10) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) { this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial(); } else { this.criteria_ = value; }  onChanged(); } else { this.criteriaBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x10; return this; } public Builder clearCriteria() { if (this.criteriaBuilder_ == null) { this.criteria_ = null; onChanged(); } else { this.criteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFEF; return this; } public MysqlxExpr.Expr.Builder getCriteriaBuilder() { this.bitField0_ |= 0x10; onChanged(); return (MysqlxExpr.Expr.Builder)getCriteriaFieldBuilder().getBuilder(); } public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() { if (this.criteriaBuilder_ != null) return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder();  return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() { if (this.criteriaBuilder_ == null) { this.criteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.criteria_ = null; }  return this.criteriaBuilder_; } public boolean hasLimit() { return ((this.bitField0_ & 0x20) != 0); } public MysqlxCrud.Limit getLimit() { if (this.limitBuilder_ == null) return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;  return (MysqlxCrud.Limit)this.limitBuilder_.getMessage(); } public Builder setLimit(MysqlxCrud.Limit value) { if (this.limitBuilder_ == null) { if (value == null) throw new NullPointerException();  this.limit_ = value; onChanged(); } else { this.limitBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x20; return this; } public Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) { if (this.limitBuilder_ == null) { this.limit_ = builderForValue.build(); onChanged(); } else { this.limitBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x20; return this; } public Builder mergeLimit(MysqlxCrud.Limit value) { if (this.limitBuilder_ == null) { if ((this.bitField0_ & 0x20) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) { this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial(); } else { this.limit_ = value; }  onChanged(); } else { this.limitBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x20; return this; } public Builder clearLimit() { if (this.limitBuilder_ == null) { this.limit_ = null; onChanged(); } else { this.limitBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFDF; return this; } public MysqlxCrud.Limit.Builder getLimitBuilder() { this.bitField0_ |= 0x20; onChanged(); return (MysqlxCrud.Limit.Builder)getLimitFieldBuilder().getBuilder(); } public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() { if (this.limitBuilder_ != null) return (MysqlxCrud.LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder();  return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() { if (this.limitBuilder_ == null) { this.limitBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimit(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.limit_ = null; }  return this.limitBuilder_; } private void ensureOrderIsMutable() { if ((this.bitField0_ & 0x40) == 0) { this.order_ = new ArrayList<>(this.order_); this.bitField0_ |= 0x40; }  } public List<MysqlxCrud.Order> getOrderList() { if (this.orderBuilder_ == null) return Collections.unmodifiableList(this.order_);  return this.orderBuilder_.getMessageList(); } public int getOrderCount() { if (this.orderBuilder_ == null) return this.order_.size();  return this.orderBuilder_.getCount(); } public MysqlxCrud.Order getOrder(int index) { if (this.orderBuilder_ == null) return this.order_.get(index);  return (MysqlxCrud.Order)this.orderBuilder_.getMessage(index); } public Builder setOrder(int index, MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.set(index, value); onChanged(); } else { this.orderBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.set(index, builderForValue.build()); onChanged(); } else { this.orderBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addOrder(MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.add(value); onChanged(); } else { this.orderBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addOrder(int index, MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.add(index, value); onChanged(); } else { this.orderBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addOrder(MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.add(builderForValue.build()); onChanged(); } else { this.orderBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.add(index, builderForValue.build()); onChanged(); } else { this.orderBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); AbstractMessageLite.Builder.addAll(values, this.order_); onChanged(); } else { this.orderBuilder_.addAllMessages(values); }  return this; } public Builder clearOrder() { if (this.orderBuilder_ == null) { this.order_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFBF; onChanged(); } else { this.orderBuilder_.clear(); }  return this; } public Builder removeOrder(int index) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.remove(index); onChanged(); } else { this.orderBuilder_.remove(index); }  return this; } public MysqlxCrud.Order.Builder getOrderBuilder(int index) { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().getBuilder(index); } public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) { if (this.orderBuilder_ == null) return this.order_.get(index);  return (MysqlxCrud.OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() { if (this.orderBuilder_ != null) return this.orderBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.order_); } public MysqlxCrud.Order.Builder addOrderBuilder() { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Order.getDefaultInstance()); } public MysqlxCrud.Order.Builder addOrderBuilder(int index) { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Order.getDefaultInstance()); } public List<MysqlxCrud.Order.Builder> getOrderBuilderList() { return getOrderFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() { if (this.orderBuilder_ == null) { this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, ((this.bitField0_ & 0x40) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.order_ = null; }  return this.orderBuilder_; } private void ensureGroupingIsMutable() { if ((this.bitField0_ & 0x80) == 0) { this.grouping_ = new ArrayList<>(this.grouping_); this.bitField0_ |= 0x80; }  } public List<MysqlxExpr.Expr> getGroupingList() { if (this.groupingBuilder_ == null) return Collections.unmodifiableList(this.grouping_);  return this.groupingBuilder_.getMessageList(); } public int getGroupingCount() { if (this.groupingBuilder_ == null) return this.grouping_.size();  return this.groupingBuilder_.getCount(); } public MysqlxExpr.Expr getGrouping(int index) { if (this.groupingBuilder_ == null) return this.grouping_.get(index);  return (MysqlxExpr.Expr)this.groupingBuilder_.getMessage(index); } public Builder setGrouping(int index, MysqlxExpr.Expr value) { if (this.groupingBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureGroupingIsMutable(); this.grouping_.set(index, value); onChanged(); } else { this.groupingBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) { if (this.groupingBuilder_ == null) { ensureGroupingIsMutable(); this.grouping_.set(index, builderForValue.build()); onChanged(); } else { this.groupingBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addGrouping(MysqlxExpr.Expr value) { if (this.groupingBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureGroupingIsMutable(); this.grouping_.add(value); onChanged(); } else { this.groupingBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addGrouping(int index, MysqlxExpr.Expr value) { if (this.groupingBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureGroupingIsMutable(); this.grouping_.add(index, value); onChanged(); } else { this.groupingBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addGrouping(MysqlxExpr.Expr.Builder builderForValue) { if (this.groupingBuilder_ == null) { ensureGroupingIsMutable(); this.grouping_.add(builderForValue.build()); onChanged(); } else { this.groupingBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) { if (this.groupingBuilder_ == null) { ensureGroupingIsMutable(); this.grouping_.add(index, builderForValue.build()); onChanged(); } else { this.groupingBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllGrouping(Iterable<? extends MysqlxExpr.Expr> values) { if (this.groupingBuilder_ == null) { ensureGroupingIsMutable(); AbstractMessageLite.Builder.addAll(values, this.grouping_); onChanged(); } else { this.groupingBuilder_.addAllMessages(values); }  return this; } public Builder clearGrouping() { if (this.groupingBuilder_ == null) { this.grouping_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFF7F; onChanged(); } else { this.groupingBuilder_.clear(); }  return this; } public Builder removeGrouping(int index) { if (this.groupingBuilder_ == null) { ensureGroupingIsMutable(); this.grouping_.remove(index); onChanged(); } else { this.groupingBuilder_.remove(index); }  return this; } public MysqlxExpr.Expr.Builder getGroupingBuilder(int index) { return (MysqlxExpr.Expr.Builder)getGroupingFieldBuilder().getBuilder(index); } public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) { if (this.groupingBuilder_ == null) return this.grouping_.get(index);  return (MysqlxExpr.ExprOrBuilder)this.groupingBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() { if (this.groupingBuilder_ != null) return this.groupingBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.grouping_); } public MysqlxExpr.Expr.Builder addGroupingBuilder() { return (MysqlxExpr.Expr.Builder)getGroupingFieldBuilder().addBuilder((AbstractMessage)MysqlxExpr.Expr.getDefaultInstance()); } public MysqlxExpr.Expr.Builder addGroupingBuilder(int index) { return (MysqlxExpr.Expr.Builder)getGroupingFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance()); } public List<MysqlxExpr.Expr.Builder> getGroupingBuilderList() { return getGroupingFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingFieldBuilder() { if (this.groupingBuilder_ == null) { this.groupingBuilder_ = new RepeatedFieldBuilderV3(this.grouping_, ((this.bitField0_ & 0x80) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.grouping_ = null; }  return this.groupingBuilder_; } public boolean hasGroupingCriteria() { return ((this.bitField0_ & 0x100) != 0); } public MysqlxExpr.Expr getGroupingCriteria() { if (this.groupingCriteriaBuilder_ == null) return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;  return (MysqlxExpr.Expr)this.groupingCriteriaBuilder_.getMessage(); }
/*       */       public Builder setGroupingCriteria(MysqlxExpr.Expr value) { if (this.groupingCriteriaBuilder_ == null) { if (value == null) throw new NullPointerException();  this.groupingCriteria_ = value; onChanged(); } else { this.groupingCriteriaBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x100; return this; }
/*       */       public Builder setGroupingCriteria(MysqlxExpr.Expr.Builder builderForValue) { if (this.groupingCriteriaBuilder_ == null) { this.groupingCriteria_ = builderForValue.build(); onChanged(); } else { this.groupingCriteriaBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x100; return this; }
/*       */       public Builder mergeGroupingCriteria(MysqlxExpr.Expr value) { if (this.groupingCriteriaBuilder_ == null) { if ((this.bitField0_ & 0x100) != 0 && this.groupingCriteria_ != null && this.groupingCriteria_ != MysqlxExpr.Expr.getDefaultInstance()) { this.groupingCriteria_ = MysqlxExpr.Expr.newBuilder(this.groupingCriteria_).mergeFrom(value).buildPartial(); } else { this.groupingCriteria_ = value; }  onChanged(); } else { this.groupingCriteriaBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x100; return this; }
/*       */       public Builder clearGroupingCriteria() { if (this.groupingCriteriaBuilder_ == null) { this.groupingCriteria_ = null; onChanged(); } else { this.groupingCriteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFEFF; return this; }
/*       */       public MysqlxExpr.Expr.Builder getGroupingCriteriaBuilder() { this.bitField0_ |= 0x100; onChanged(); return (MysqlxExpr.Expr.Builder)getGroupingCriteriaFieldBuilder().getBuilder(); }
/*       */       public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() { if (this.groupingCriteriaBuilder_ != null) return (MysqlxExpr.ExprOrBuilder)this.groupingCriteriaBuilder_.getMessageOrBuilder();  return (this.groupingCriteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_; }
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingCriteriaFieldBuilder() { if (this.groupingCriteriaBuilder_ == null) { this.groupingCriteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getGroupingCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.groupingCriteria_ = null; }  return this.groupingCriteriaBuilder_; }
/*       */       public boolean hasLocking() { return ((this.bitField0_ & 0x200) != 0); }
/*       */       public MysqlxCrud.Find.RowLock getLocking() { MysqlxCrud.Find.RowLock result = MysqlxCrud.Find.RowLock.valueOf(this.locking_); return (result == null) ? MysqlxCrud.Find.RowLock.SHARED_LOCK : result; }
/*       */       public Builder setLocking(MysqlxCrud.Find.RowLock value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x200; this.locking_ = value.getNumber(); onChanged(); return this; }
/*       */       public Builder clearLocking() { this.bitField0_ &= 0xFFFFFDFF; this.locking_ = 1; onChanged(); return this; }
/* 11585 */       public MysqlxCrud.Find.RowLockOptions getLockingOptions() { MysqlxCrud.Find.RowLockOptions result = MysqlxCrud.Find.RowLockOptions.valueOf(this.lockingOptions_);
/* 11586 */         return (result == null) ? MysqlxCrud.Find.RowLockOptions.NOWAIT : result; }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLockingOptions(MysqlxCrud.Find.RowLockOptions value) {
/* 11598 */         if (value == null) {
/* 11599 */           throw new NullPointerException();
/*       */         }
/* 11601 */         this.bitField0_ |= 0x400;
/* 11602 */         this.lockingOptions_ = value.getNumber();
/* 11603 */         onChanged();
/* 11604 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearLockingOptions() {
/* 11615 */         this.bitField0_ &= 0xFFFFFBFF;
/* 11616 */         this.lockingOptions_ = 1;
/* 11617 */         onChanged();
/* 11618 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasLimitExpr() {
/* 11634 */         return ((this.bitField0_ & 0x800) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr getLimitExpr() {
/* 11646 */         if (this.limitExprBuilder_ == null) {
/* 11647 */           return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
/*       */         }
/* 11649 */         return (MysqlxCrud.LimitExpr)this.limitExprBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
/* 11661 */         if (this.limitExprBuilder_ == null) {
/* 11662 */           if (value == null) {
/* 11663 */             throw new NullPointerException();
/*       */           }
/* 11665 */           this.limitExpr_ = value;
/* 11666 */           onChanged();
/*       */         } else {
/* 11668 */           this.limitExprBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/* 11670 */         this.bitField0_ |= 0x800;
/* 11671 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
/* 11683 */         if (this.limitExprBuilder_ == null) {
/* 11684 */           this.limitExpr_ = builderForValue.build();
/* 11685 */           onChanged();
/*       */         } else {
/* 11687 */           this.limitExprBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 11689 */         this.bitField0_ |= 0x800;
/* 11690 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
/* 11701 */         if (this.limitExprBuilder_ == null) {
/* 11702 */           if ((this.bitField0_ & 0x800) != 0 && this.limitExpr_ != null && this.limitExpr_ != 
/*       */             
/* 11704 */             MysqlxCrud.LimitExpr.getDefaultInstance()) {
/* 11705 */             this
/* 11706 */               .limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
/*       */           } else {
/* 11708 */             this.limitExpr_ = value;
/*       */           } 
/* 11710 */           onChanged();
/*       */         } else {
/* 11712 */           this.limitExprBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/* 11714 */         this.bitField0_ |= 0x800;
/* 11715 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearLimitExpr() {
/* 11726 */         if (this.limitExprBuilder_ == null) {
/* 11727 */           this.limitExpr_ = null;
/* 11728 */           onChanged();
/*       */         } else {
/* 11730 */           this.limitExprBuilder_.clear();
/*       */         } 
/* 11732 */         this.bitField0_ &= 0xFFFFF7FF;
/* 11733 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
/* 11744 */         this.bitField0_ |= 0x800;
/* 11745 */         onChanged();
/* 11746 */         return (MysqlxCrud.LimitExpr.Builder)getLimitExprFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
/* 11757 */         if (this.limitExprBuilder_ != null) {
/* 11758 */           return (MysqlxCrud.LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder();
/*       */         }
/* 11760 */         return (this.limitExpr_ == null) ? 
/* 11761 */           MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
/* 11775 */         if (this.limitExprBuilder_ == null) {
/* 11776 */           this
/*       */ 
/*       */ 
/*       */             
/* 11780 */             .limitExprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimitExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 11781 */           this.limitExpr_ = null;
/*       */         } 
/* 11783 */         return this.limitExprBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 11788 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 11794 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       } }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 11804 */     private static final Find DEFAULT_INSTANCE = new Find();
/*       */ 
/*       */     
/*       */     public static Find getDefaultInstance() {
/* 11808 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 11812 */     public static final Parser<Find> PARSER = (Parser<Find>)new AbstractParser<Find>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Find parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 11818 */           return new MysqlxCrud.Find(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Find> parser() {
/* 11823 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Find> getParserForType() {
/* 11828 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Find getDefaultInstanceForType() {
/* 11833 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface InsertOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Collection getCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.DataModel getDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxCrud.Column> getProjectionList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Column getProjection(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getProjectionCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxCrud.Insert.TypedRow> getRowList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Insert.TypedRow getRow(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getRowCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxCrud.Insert.TypedRowOrBuilder> getRowOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Insert.TypedRowOrBuilder getRowOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxDatatypes.Scalar> getArgsList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.Scalar getArgs(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getArgsCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasUpsert();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean getUpsert();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Insert
/*       */     extends GeneratedMessageV3
/*       */     implements InsertOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLLECTION_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Collection collection_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DATA_MODEL_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int dataModel_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int PROJECTION_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxCrud.Column> projection_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ROW_FIELD_NUMBER = 4;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<TypedRow> row_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ARGS_FIELD_NUMBER = 5;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxDatatypes.Scalar> args_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int UPSERT_FIELD_NUMBER = 6;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private boolean upsert_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private Insert(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/* 12067 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/* 13277 */       this.memoizedIsInitialized = -1; } private Insert() { this.memoizedIsInitialized = -1; this.dataModel_ = 1; this.projection_ = Collections.emptyList(); this.row_ = Collections.emptyList(); this.args_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Insert(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Insert(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxCrud.Collection.Builder subBuilder; int rawValue; MysqlxCrud.DataModel value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: subBuilder = null; if ((this.bitField0_ & 0x1) != 0) subBuilder = this.collection_.toBuilder();  this.collection_ = (MysqlxCrud.Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.collection_); this.collection_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 16: rawValue = input.readEnum(); value = MysqlxCrud.DataModel.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(2, rawValue); continue; }  this.bitField0_ |= 0x2; this.dataModel_ = rawValue; continue;case 26: if ((mutable_bitField0_ & 0x4) == 0) { this.projection_ = new ArrayList<>(); mutable_bitField0_ |= 0x4; }  this.projection_.add(input.readMessage(MysqlxCrud.Column.PARSER, extensionRegistry)); continue;case 34: if ((mutable_bitField0_ & 0x8) == 0) { this.row_ = new ArrayList<>(); mutable_bitField0_ |= 0x8; }  this.row_.add(input.readMessage(TypedRow.PARSER, extensionRegistry)); continue;case 42: if ((mutable_bitField0_ & 0x10) == 0) { this.args_ = new ArrayList<>(); mutable_bitField0_ |= 0x10; }  this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry)); continue;case 48: this.bitField0_ |= 0x4; this.upsert_ = input.readBool(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x4) != 0) this.projection_ = Collections.unmodifiableList(this.projection_);  if ((mutable_bitField0_ & 0x8) != 0) this.row_ = Collections.unmodifiableList(this.row_);  if ((mutable_bitField0_ & 0x10) != 0) this.args_ = Collections.unmodifiableList(this.args_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable.ensureFieldAccessorsInitialized(Insert.class, Builder.class); } public static final class TypedRow extends GeneratedMessageV3 implements TypedRowOrBuilder {
/*       */       private static final long serialVersionUID = 0L; public static final int FIELD_FIELD_NUMBER = 1; private List<MysqlxExpr.Expr> field_; private byte memoizedIsInitialized; private TypedRow(GeneratedMessageV3.Builder<?> builder) { super(builder); this.memoizedIsInitialized = -1; } private TypedRow() { this.memoizedIsInitialized = -1; this.field_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new TypedRow(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private TypedRow(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: if ((mutable_bitField0_ & 0x1) == 0) { this.field_ = new ArrayList<>(); mutable_bitField0_ |= 0x1; }  this.field_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry)); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x1) != 0) this.field_ = Collections.unmodifiableList(this.field_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable.ensureFieldAccessorsInitialized(TypedRow.class, Builder.class); } public List<MysqlxExpr.Expr> getFieldList() { return this.field_; } public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() { return (List)this.field_; } public int getFieldCount() { return this.field_.size(); } public MysqlxExpr.Expr getField(int index) { return this.field_.get(index); } public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) { return this.field_.get(index); } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized; if (isInitialized == 1) return true;  if (isInitialized == 0) return false;  for (int i = 0; i < getFieldCount(); i++) { if (!getField(i).isInitialized()) { this.memoizedIsInitialized = 0; return false; }  }  this.memoizedIsInitialized = 1; return true; } public void writeTo(CodedOutputStream output) throws IOException { for (int i = 0; i < this.field_.size(); i++) output.writeMessage(1, (MessageLite)this.field_.get(i));  this.unknownFields.writeTo(output); } public int getSerializedSize() { int size = this.memoizedSize; if (size != -1) return size;  size = 0; for (int i = 0; i < this.field_.size(); i++) size += CodedOutputStream.computeMessageSize(1, (MessageLite)this.field_.get(i));  size += this.unknownFields.getSerializedSize(); this.memoizedSize = size; return size; } public boolean equals(Object obj) { if (obj == this) return true;  if (!(obj instanceof TypedRow)) return super.equals(obj);  TypedRow other = (TypedRow)obj; if (!getFieldList().equals(other.getFieldList())) return false;  if (!this.unknownFields.equals(other.unknownFields)) return false;  return true; } public int hashCode() { if (this.memoizedHashCode != 0) return this.memoizedHashCode;  int hash = 41; hash = 19 * hash + getDescriptor().hashCode(); if (getFieldCount() > 0) { hash = 37 * hash + 1; hash = 53 * hash + getFieldList().hashCode(); }  hash = 29 * hash + this.unknownFields.hashCode(); this.memoizedHashCode = hash; return hash; } public static TypedRow parseFrom(ByteBuffer data) throws InvalidProtocolBufferException { return (TypedRow)PARSER.parseFrom(data); } public static TypedRow parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (TypedRow)PARSER.parseFrom(data, extensionRegistry); } public static TypedRow parseFrom(ByteString data) throws InvalidProtocolBufferException { return (TypedRow)PARSER.parseFrom(data); } public static TypedRow parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (TypedRow)PARSER.parseFrom(data, extensionRegistry); } public static TypedRow parseFrom(byte[] data) throws InvalidProtocolBufferException { return (TypedRow)PARSER.parseFrom(data); } public static TypedRow parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return (TypedRow)PARSER.parseFrom(data, extensionRegistry); } public static TypedRow parseFrom(InputStream input) throws IOException { return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static TypedRow parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public static TypedRow parseDelimitedFrom(InputStream input) throws IOException { return (TypedRow)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input); } public static TypedRow parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (TypedRow)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry); } public static TypedRow parseFrom(CodedInputStream input) throws IOException { return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input); } public static TypedRow parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry); } public Builder newBuilderForType() { return newBuilder(); } public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); } public static Builder newBuilder(TypedRow prototype) { return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype); } public Builder toBuilder() { return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder()).mergeFrom(this); } protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) { Builder builder = new Builder(parent); return builder; } public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.Insert.TypedRowOrBuilder {
/*       */         private int bitField0_; private List<MysqlxExpr.Expr> field_; private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> fieldBuilder_; public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable.ensureFieldAccessorsInitialized(MysqlxCrud.Insert.TypedRow.class, Builder.class); } private Builder() { this.field_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.field_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Insert.TypedRow.alwaysUseFieldBuilders) getFieldFieldBuilder();  } public Builder clear() { super.clear(); if (this.fieldBuilder_ == null) { this.field_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; } else { this.fieldBuilder_.clear(); }  return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor; } public MysqlxCrud.Insert.TypedRow getDefaultInstanceForType() { return MysqlxCrud.Insert.TypedRow.getDefaultInstance(); } public MysqlxCrud.Insert.TypedRow build() { MysqlxCrud.Insert.TypedRow result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Insert.TypedRow buildPartial() { MysqlxCrud.Insert.TypedRow result = new MysqlxCrud.Insert.TypedRow(this); int from_bitField0_ = this.bitField0_; if (this.fieldBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0) { this.field_ = Collections.unmodifiableList(this.field_); this.bitField0_ &= 0xFFFFFFFE; }  result.field_ = this.field_; } else { result.field_ = this.fieldBuilder_.build(); }  onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Insert.TypedRow) return mergeFrom((MysqlxCrud.Insert.TypedRow)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Insert.TypedRow other) { if (other == MysqlxCrud.Insert.TypedRow.getDefaultInstance()) return this;  if (this.fieldBuilder_ == null) { if (!other.field_.isEmpty()) { if (this.field_.isEmpty()) { this.field_ = other.field_; this.bitField0_ &= 0xFFFFFFFE; } else { ensureFieldIsMutable(); this.field_.addAll(other.field_); }  onChanged(); }  } else if (!other.field_.isEmpty()) { if (this.fieldBuilder_.isEmpty()) { this.fieldBuilder_.dispose(); this.fieldBuilder_ = null; this.field_ = other.field_; this.bitField0_ &= 0xFFFFFFFE; this.fieldBuilder_ = MysqlxCrud.Insert.TypedRow.alwaysUseFieldBuilders ? getFieldFieldBuilder() : null; } else { this.fieldBuilder_.addAllMessages(other.field_); }  }  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { for (int i = 0; i < getFieldCount(); i++) { if (!getField(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Insert.TypedRow parsedMessage = null; try { parsedMessage = (MysqlxCrud.Insert.TypedRow)MysqlxCrud.Insert.TypedRow.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Insert.TypedRow)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } private void ensureFieldIsMutable() { if ((this.bitField0_ & 0x1) == 0) { this.field_ = new ArrayList<>(this.field_); this.bitField0_ |= 0x1; }  } public List<MysqlxExpr.Expr> getFieldList() { if (this.fieldBuilder_ == null) return Collections.unmodifiableList(this.field_);  return this.fieldBuilder_.getMessageList(); } public int getFieldCount() { if (this.fieldBuilder_ == null) return this.field_.size();  return this.fieldBuilder_.getCount(); } public MysqlxExpr.Expr getField(int index) { if (this.fieldBuilder_ == null) return this.field_.get(index);  return (MysqlxExpr.Expr)this.fieldBuilder_.getMessage(index); } public Builder setField(int index, MysqlxExpr.Expr value) { if (this.fieldBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureFieldIsMutable(); this.field_.set(index, value); onChanged(); } else { this.fieldBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setField(int index, MysqlxExpr.Expr.Builder builderForValue) { if (this.fieldBuilder_ == null) { ensureFieldIsMutable(); this.field_.set(index, builderForValue.build()); onChanged(); } else { this.fieldBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addField(MysqlxExpr.Expr value) { if (this.fieldBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureFieldIsMutable(); this.field_.add(value); onChanged(); } else { this.fieldBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addField(int index, MysqlxExpr.Expr value) { if (this.fieldBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureFieldIsMutable(); this.field_.add(index, value); onChanged(); } else { this.fieldBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addField(MysqlxExpr.Expr.Builder builderForValue) { if (this.fieldBuilder_ == null) { ensureFieldIsMutable(); this.field_.add(builderForValue.build()); onChanged(); } else { this.fieldBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addField(int index, MysqlxExpr.Expr.Builder builderForValue) { if (this.fieldBuilder_ == null) { ensureFieldIsMutable(); this.field_.add(index, builderForValue.build()); onChanged(); } else { this.fieldBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllField(Iterable<? extends MysqlxExpr.Expr> values) { if (this.fieldBuilder_ == null) { ensureFieldIsMutable(); AbstractMessageLite.Builder.addAll(values, this.field_); onChanged(); } else { this.fieldBuilder_.addAllMessages(values); }  return this; } public Builder clearField() { if (this.fieldBuilder_ == null) { this.field_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFE; onChanged(); } else { this.fieldBuilder_.clear(); }  return this; } public Builder removeField(int index) { if (this.fieldBuilder_ == null) { ensureFieldIsMutable(); this.field_.remove(index); onChanged(); } else { this.fieldBuilder_.remove(index); }  return this; } public MysqlxExpr.Expr.Builder getFieldBuilder(int index) { return (MysqlxExpr.Expr.Builder)getFieldFieldBuilder().getBuilder(index); } public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) { if (this.fieldBuilder_ == null) return this.field_.get(index);  return (MysqlxExpr.ExprOrBuilder)this.fieldBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() { if (this.fieldBuilder_ != null) return this.fieldBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.field_); } public MysqlxExpr.Expr.Builder addFieldBuilder() { return (MysqlxExpr.Expr.Builder)getFieldFieldBuilder().addBuilder((AbstractMessage)MysqlxExpr.Expr.getDefaultInstance()); } public MysqlxExpr.Expr.Builder addFieldBuilder(int index) { return (MysqlxExpr.Expr.Builder)getFieldFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxExpr.Expr.getDefaultInstance()); } public List<MysqlxExpr.Expr.Builder> getFieldBuilderList() { return getFieldFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getFieldFieldBuilder() { if (this.fieldBuilder_ == null) { this.fieldBuilder_ = new RepeatedFieldBuilderV3(this.field_, ((this.bitField0_ & 0x1) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.field_ = null; }  return this.fieldBuilder_; } public final Builder setUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.setUnknownFields(unknownFields); } public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) { return (Builder)super.mergeUnknownFields(unknownFields); } } private static final TypedRow DEFAULT_INSTANCE = new TypedRow(); public static TypedRow getDefaultInstance() { return DEFAULT_INSTANCE; } @Deprecated public static final Parser<TypedRow> PARSER = (Parser<TypedRow>)new AbstractParser<TypedRow>() { public MysqlxCrud.Insert.TypedRow parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { return new MysqlxCrud.Insert.TypedRow(input, extensionRegistry); } }
/* 13280 */       ; public static Parser<TypedRow> parser() { return PARSER; } public Parser<TypedRow> getParserForType() { return PARSER; } public TypedRow getDefaultInstanceForType() { return DEFAULT_INSTANCE; } } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public List<MysqlxCrud.Column> getProjectionList() { return this.projection_; } public List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList() { return (List)this.projection_; } public int getProjectionCount() { return this.projection_.size(); } public MysqlxCrud.Column getProjection(int index) { return this.projection_.get(index); } public MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int index) { return this.projection_.get(index); } public List<TypedRow> getRowList() { return this.row_; } public List<? extends TypedRowOrBuilder> getRowOrBuilderList() { return (List)this.row_; } public int getRowCount() { return this.row_.size(); } public TypedRow getRow(int index) { return this.row_.get(index); } public TypedRowOrBuilder getRowOrBuilder(int index) { return this.row_.get(index); } public List<MysqlxDatatypes.Scalar> getArgsList() { return this.args_; } public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() { return (List)this.args_; } public int getArgsCount() { return this.args_.size(); } public MysqlxDatatypes.Scalar getArgs(int index) { return this.args_.get(index); } public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) { return this.args_.get(index); } public boolean hasUpsert() { return ((this.bitField0_ & 0x4) != 0); } public boolean getUpsert() { return this.upsert_; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 13281 */       if (isInitialized == 1) return true; 
/* 13282 */       if (isInitialized == 0) return false;
/*       */       
/* 13284 */       if (!hasCollection()) {
/* 13285 */         this.memoizedIsInitialized = 0;
/* 13286 */         return false;
/*       */       } 
/* 13288 */       if (!getCollection().isInitialized()) {
/* 13289 */         this.memoizedIsInitialized = 0;
/* 13290 */         return false;
/*       */       }  int i;
/* 13292 */       for (i = 0; i < getProjectionCount(); i++) {
/* 13293 */         if (!getProjection(i).isInitialized()) {
/* 13294 */           this.memoizedIsInitialized = 0;
/* 13295 */           return false;
/*       */         } 
/*       */       } 
/* 13298 */       for (i = 0; i < getRowCount(); i++) {
/* 13299 */         if (!getRow(i).isInitialized()) {
/* 13300 */           this.memoizedIsInitialized = 0;
/* 13301 */           return false;
/*       */         } 
/*       */       } 
/* 13304 */       for (i = 0; i < getArgsCount(); i++) {
/* 13305 */         if (!getArgs(i).isInitialized()) {
/* 13306 */           this.memoizedIsInitialized = 0;
/* 13307 */           return false;
/*       */         } 
/*       */       } 
/* 13310 */       this.memoizedIsInitialized = 1;
/* 13311 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/* 13317 */       if ((this.bitField0_ & 0x1) != 0) {
/* 13318 */         output.writeMessage(1, (MessageLite)getCollection());
/*       */       }
/* 13320 */       if ((this.bitField0_ & 0x2) != 0)
/* 13321 */         output.writeEnum(2, this.dataModel_); 
/*       */       int i;
/* 13323 */       for (i = 0; i < this.projection_.size(); i++) {
/* 13324 */         output.writeMessage(3, (MessageLite)this.projection_.get(i));
/*       */       }
/* 13326 */       for (i = 0; i < this.row_.size(); i++) {
/* 13327 */         output.writeMessage(4, (MessageLite)this.row_.get(i));
/*       */       }
/* 13329 */       for (i = 0; i < this.args_.size(); i++) {
/* 13330 */         output.writeMessage(5, (MessageLite)this.args_.get(i));
/*       */       }
/* 13332 */       if ((this.bitField0_ & 0x4) != 0) {
/* 13333 */         output.writeBool(6, this.upsert_);
/*       */       }
/* 13335 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/* 13340 */       int size = this.memoizedSize;
/* 13341 */       if (size != -1) return size;
/*       */       
/* 13343 */       size = 0;
/* 13344 */       if ((this.bitField0_ & 0x1) != 0) {
/* 13345 */         size += 
/* 13346 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection());
/*       */       }
/* 13348 */       if ((this.bitField0_ & 0x2) != 0)
/* 13349 */         size += 
/* 13350 */           CodedOutputStream.computeEnumSize(2, this.dataModel_); 
/*       */       int i;
/* 13352 */       for (i = 0; i < this.projection_.size(); i++) {
/* 13353 */         size += 
/* 13354 */           CodedOutputStream.computeMessageSize(3, (MessageLite)this.projection_.get(i));
/*       */       }
/* 13356 */       for (i = 0; i < this.row_.size(); i++) {
/* 13357 */         size += 
/* 13358 */           CodedOutputStream.computeMessageSize(4, (MessageLite)this.row_.get(i));
/*       */       }
/* 13360 */       for (i = 0; i < this.args_.size(); i++) {
/* 13361 */         size += 
/* 13362 */           CodedOutputStream.computeMessageSize(5, (MessageLite)this.args_.get(i));
/*       */       }
/* 13364 */       if ((this.bitField0_ & 0x4) != 0) {
/* 13365 */         size += 
/* 13366 */           CodedOutputStream.computeBoolSize(6, this.upsert_);
/*       */       }
/* 13368 */       size += this.unknownFields.getSerializedSize();
/* 13369 */       this.memoizedSize = size;
/* 13370 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/* 13375 */       if (obj == this) {
/* 13376 */         return true;
/*       */       }
/* 13378 */       if (!(obj instanceof Insert)) {
/* 13379 */         return super.equals(obj);
/*       */       }
/* 13381 */       Insert other = (Insert)obj;
/*       */       
/* 13383 */       if (hasCollection() != other.hasCollection()) return false; 
/* 13384 */       if (hasCollection() && 
/*       */         
/* 13386 */         !getCollection().equals(other.getCollection())) return false;
/*       */       
/* 13388 */       if (hasDataModel() != other.hasDataModel()) return false; 
/* 13389 */       if (hasDataModel() && 
/* 13390 */         this.dataModel_ != other.dataModel_) return false;
/*       */ 
/*       */       
/* 13393 */       if (!getProjectionList().equals(other.getProjectionList())) return false;
/*       */       
/* 13395 */       if (!getRowList().equals(other.getRowList())) return false;
/*       */       
/* 13397 */       if (!getArgsList().equals(other.getArgsList())) return false; 
/* 13398 */       if (hasUpsert() != other.hasUpsert()) return false; 
/* 13399 */       if (hasUpsert() && 
/* 13400 */         getUpsert() != other
/* 13401 */         .getUpsert()) return false;
/*       */       
/* 13403 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 13404 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/* 13409 */       if (this.memoizedHashCode != 0) {
/* 13410 */         return this.memoizedHashCode;
/*       */       }
/* 13412 */       int hash = 41;
/* 13413 */       hash = 19 * hash + getDescriptor().hashCode();
/* 13414 */       if (hasCollection()) {
/* 13415 */         hash = 37 * hash + 1;
/* 13416 */         hash = 53 * hash + getCollection().hashCode();
/*       */       } 
/* 13418 */       if (hasDataModel()) {
/* 13419 */         hash = 37 * hash + 2;
/* 13420 */         hash = 53 * hash + this.dataModel_;
/*       */       } 
/* 13422 */       if (getProjectionCount() > 0) {
/* 13423 */         hash = 37 * hash + 3;
/* 13424 */         hash = 53 * hash + getProjectionList().hashCode();
/*       */       } 
/* 13426 */       if (getRowCount() > 0) {
/* 13427 */         hash = 37 * hash + 4;
/* 13428 */         hash = 53 * hash + getRowList().hashCode();
/*       */       } 
/* 13430 */       if (getArgsCount() > 0) {
/* 13431 */         hash = 37 * hash + 5;
/* 13432 */         hash = 53 * hash + getArgsList().hashCode();
/*       */       } 
/* 13434 */       if (hasUpsert()) {
/* 13435 */         hash = 37 * hash + 6;
/* 13436 */         hash = 53 * hash + Internal.hashBoolean(
/* 13437 */             getUpsert());
/*       */       } 
/* 13439 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 13440 */       this.memoizedHashCode = hash;
/* 13441 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 13447 */       return (Insert)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 13453 */       return (Insert)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 13458 */       return (Insert)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 13464 */       return (Insert)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Insert parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 13468 */       return (Insert)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 13474 */       return (Insert)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Insert parseFrom(InputStream input) throws IOException {
/* 13478 */       return 
/* 13479 */         (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 13485 */       return 
/* 13486 */         (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Insert parseDelimitedFrom(InputStream input) throws IOException {
/* 13490 */       return 
/* 13491 */         (Insert)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Insert parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 13497 */       return 
/* 13498 */         (Insert)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(CodedInputStream input) throws IOException {
/* 13503 */       return 
/* 13504 */         (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Insert parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 13510 */       return 
/* 13511 */         (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/* 13515 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/* 13517 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Insert prototype) {
/* 13520 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/* 13524 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 13525 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 13531 */       Builder builder = new Builder(parent);
/* 13532 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.InsertOrBuilder {
/*       */       private int bitField0_;
/*       */       private MysqlxCrud.Collection collection_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
/*       */       private int dataModel_;
/*       */       private List<MysqlxCrud.Column> projection_;
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.Column, MysqlxCrud.Column.Builder, MysqlxCrud.ColumnOrBuilder> projectionBuilder_;
/*       */       private List<MysqlxCrud.Insert.TypedRow> row_;
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.Insert.TypedRow, MysqlxCrud.Insert.TypedRow.Builder, MysqlxCrud.Insert.TypedRowOrBuilder> rowBuilder_;
/*       */       private List<MysqlxDatatypes.Scalar> args_;
/*       */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
/*       */       private boolean upsert_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/* 13549 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 13555 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable
/* 13556 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Insert.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/* 14024 */         this.dataModel_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 14082 */         this
/* 14083 */           .projection_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 14412 */         this
/* 14413 */           .row_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 14742 */         this
/* 14743 */           .args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.dataModel_ = 1; this.projection_ = Collections.emptyList(); this.row_ = Collections.emptyList(); this.args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Insert.alwaysUseFieldBuilders) { getCollectionFieldBuilder(); getProjectionFieldBuilder(); getRowFieldBuilder(); getArgsFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.collectionBuilder_ == null) { this.collection_ = null; } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.dataModel_ = 1; this.bitField0_ &= 0xFFFFFFFD; if (this.projectionBuilder_ == null) { this.projection_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; } else { this.projectionBuilder_.clear(); }  if (this.rowBuilder_ == null) { this.row_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFF7; } else { this.rowBuilder_.clear(); }  if (this.argsBuilder_ == null) { this.args_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFEF; } else { this.argsBuilder_.clear(); }  this.upsert_ = false; this.bitField0_ &= 0xFFFFFFDF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor; } public MysqlxCrud.Insert getDefaultInstanceForType() { return MysqlxCrud.Insert.getDefaultInstance(); } public MysqlxCrud.Insert build() { MysqlxCrud.Insert result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Insert buildPartial() { MysqlxCrud.Insert result = new MysqlxCrud.Insert(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.collectionBuilder_ == null) { result.collection_ = this.collection_; } else { result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.dataModel_ = this.dataModel_; if (this.projectionBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0) { this.projection_ = Collections.unmodifiableList(this.projection_); this.bitField0_ &= 0xFFFFFFFB; }  result.projection_ = this.projection_; } else { result.projection_ = this.projectionBuilder_.build(); }  if (this.rowBuilder_ == null) { if ((this.bitField0_ & 0x8) != 0) { this.row_ = Collections.unmodifiableList(this.row_); this.bitField0_ &= 0xFFFFFFF7; }  result.row_ = this.row_; } else { result.row_ = this.rowBuilder_.build(); }  if (this.argsBuilder_ == null) { if ((this.bitField0_ & 0x10) != 0) { this.args_ = Collections.unmodifiableList(this.args_); this.bitField0_ &= 0xFFFFFFEF; }  result.args_ = this.args_; } else { result.args_ = this.argsBuilder_.build(); }  if ((from_bitField0_ & 0x20) != 0) { result.upsert_ = this.upsert_; to_bitField0_ |= 0x4; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Insert) return mergeFrom((MysqlxCrud.Insert)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Insert other) { if (other == MysqlxCrud.Insert.getDefaultInstance()) return this;  if (other.hasCollection()) mergeCollection(other.getCollection());  if (other.hasDataModel()) setDataModel(other.getDataModel());  if (this.projectionBuilder_ == null) { if (!other.projection_.isEmpty()) { if (this.projection_.isEmpty()) { this.projection_ = other.projection_; this.bitField0_ &= 0xFFFFFFFB; } else { ensureProjectionIsMutable(); this.projection_.addAll(other.projection_); }  onChanged(); }  } else if (!other.projection_.isEmpty()) { if (this.projectionBuilder_.isEmpty()) { this.projectionBuilder_.dispose(); this.projectionBuilder_ = null; this.projection_ = other.projection_; this.bitField0_ &= 0xFFFFFFFB; this.projectionBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? getProjectionFieldBuilder() : null; } else { this.projectionBuilder_.addAllMessages(other.projection_); }  }  if (this.rowBuilder_ == null) { if (!other.row_.isEmpty()) { if (this.row_.isEmpty()) { this.row_ = other.row_; this.bitField0_ &= 0xFFFFFFF7; } else { ensureRowIsMutable(); this.row_.addAll(other.row_); }  onChanged(); }  } else if (!other.row_.isEmpty()) { if (this.rowBuilder_.isEmpty()) { this.rowBuilder_.dispose(); this.rowBuilder_ = null; this.row_ = other.row_; this.bitField0_ &= 0xFFFFFFF7; this.rowBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? getRowFieldBuilder() : null; } else { this.rowBuilder_.addAllMessages(other.row_); }  }  if (this.argsBuilder_ == null) { if (!other.args_.isEmpty()) { if (this.args_.isEmpty()) { this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFEF; } else { ensureArgsIsMutable(); this.args_.addAll(other.args_); }  onChanged(); }  } else if (!other.args_.isEmpty()) { if (this.argsBuilder_.isEmpty()) { this.argsBuilder_.dispose(); this.argsBuilder_ = null; this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFEF; this.argsBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null; } else { this.argsBuilder_.addAllMessages(other.args_); }  }  if (other.hasUpsert()) setUpsert(other.getUpsert());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasCollection()) return false;  if (!getCollection().isInitialized()) return false;  int i; for (i = 0; i < getProjectionCount(); i++) { if (!getProjection(i).isInitialized()) return false;  }  for (i = 0; i < getRowCount(); i++) { if (!getRow(i).isInitialized()) return false;  }  for (i = 0; i < getArgsCount(); i++) { if (!getArgs(i).isInitialized()) return false;  }  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Insert parsedMessage = null; try { parsedMessage = (MysqlxCrud.Insert)MysqlxCrud.Insert.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Insert)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { if (this.collectionBuilder_ == null) return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;  return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage(); } public Builder setCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if (value == null) throw new NullPointerException();  this.collection_ = value; onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) { if (this.collectionBuilder_ == null) { this.collection_ = builderForValue.build(); onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; } public Builder mergeCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) { this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial(); } else { this.collection_ = value; }  onChanged(); } else { this.collectionBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder clearCollection() { if (this.collectionBuilder_ == null) { this.collection_ = null; onChanged(); } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; } public MysqlxCrud.Collection.Builder getCollectionBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder(); } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { if (this.collectionBuilder_ != null) return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();  return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() { if (this.collectionBuilder_ == null) { this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.collection_ = null; }  return this.collectionBuilder_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public Builder setDataModel(MysqlxCrud.DataModel value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.dataModel_ = value.getNumber(); onChanged(); return this; } public Builder clearDataModel() { this.bitField0_ &= 0xFFFFFFFD; this.dataModel_ = 1; onChanged(); return this; } private void ensureProjectionIsMutable() { if ((this.bitField0_ & 0x4) == 0) { this.projection_ = new ArrayList<>(this.projection_); this.bitField0_ |= 0x4; }  } public List<MysqlxCrud.Column> getProjectionList() { if (this.projectionBuilder_ == null) return Collections.unmodifiableList(this.projection_);  return this.projectionBuilder_.getMessageList(); } public int getProjectionCount() { if (this.projectionBuilder_ == null) return this.projection_.size();  return this.projectionBuilder_.getCount(); } public MysqlxCrud.Column getProjection(int index) { if (this.projectionBuilder_ == null) return this.projection_.get(index);  return (MysqlxCrud.Column)this.projectionBuilder_.getMessage(index); } public Builder setProjection(int index, MysqlxCrud.Column value) { if (this.projectionBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureProjectionIsMutable(); this.projection_.set(index, value); onChanged(); } else { this.projectionBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setProjection(int index, MysqlxCrud.Column.Builder builderForValue) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.set(index, builderForValue.build()); onChanged(); } else { this.projectionBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addProjection(MysqlxCrud.Column value) { if (this.projectionBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureProjectionIsMutable(); this.projection_.add(value); onChanged(); } else { this.projectionBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addProjection(int index, MysqlxCrud.Column value) { if (this.projectionBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureProjectionIsMutable(); this.projection_.add(index, value); onChanged(); } else { this.projectionBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addProjection(MysqlxCrud.Column.Builder builderForValue) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.add(builderForValue.build()); onChanged(); } else { this.projectionBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addProjection(int index, MysqlxCrud.Column.Builder builderForValue) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.add(index, builderForValue.build()); onChanged(); } else { this.projectionBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllProjection(Iterable<? extends MysqlxCrud.Column> values) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); AbstractMessageLite.Builder.addAll(values, this.projection_); onChanged(); } else { this.projectionBuilder_.addAllMessages(values); }  return this; } public Builder clearProjection() { if (this.projectionBuilder_ == null) { this.projection_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFFB; onChanged(); } else { this.projectionBuilder_.clear(); }  return this; } public Builder removeProjection(int index) { if (this.projectionBuilder_ == null) { ensureProjectionIsMutable(); this.projection_.remove(index); onChanged(); } else { this.projectionBuilder_.remove(index); }  return this; } public MysqlxCrud.Column.Builder getProjectionBuilder(int index) { return (MysqlxCrud.Column.Builder)getProjectionFieldBuilder().getBuilder(index); } public MysqlxCrud.ColumnOrBuilder getProjectionOrBuilder(int index) { if (this.projectionBuilder_ == null) return this.projection_.get(index);  return (MysqlxCrud.ColumnOrBuilder)this.projectionBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxCrud.ColumnOrBuilder> getProjectionOrBuilderList() { if (this.projectionBuilder_ != null) return this.projectionBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.projection_); } public MysqlxCrud.Column.Builder addProjectionBuilder() { return (MysqlxCrud.Column.Builder)getProjectionFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Column.getDefaultInstance()); } public MysqlxCrud.Column.Builder addProjectionBuilder(int index) { return (MysqlxCrud.Column.Builder)getProjectionFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Column.getDefaultInstance()); } public List<MysqlxCrud.Column.Builder> getProjectionBuilderList() { return getProjectionFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxCrud.Column, MysqlxCrud.Column.Builder, MysqlxCrud.ColumnOrBuilder> getProjectionFieldBuilder() { if (this.projectionBuilder_ == null) { this.projectionBuilder_ = new RepeatedFieldBuilderV3(this.projection_, ((this.bitField0_ & 0x4) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.projection_ = null; }  return this.projectionBuilder_; } private void ensureRowIsMutable() { if ((this.bitField0_ & 0x8) == 0) { this.row_ = new ArrayList<>(this.row_); this.bitField0_ |= 0x8; }  } public List<MysqlxCrud.Insert.TypedRow> getRowList() { if (this.rowBuilder_ == null) return Collections.unmodifiableList(this.row_);  return this.rowBuilder_.getMessageList(); } public int getRowCount() { if (this.rowBuilder_ == null) return this.row_.size();  return this.rowBuilder_.getCount(); } public MysqlxCrud.Insert.TypedRow getRow(int index) { if (this.rowBuilder_ == null) return this.row_.get(index);  return (MysqlxCrud.Insert.TypedRow)this.rowBuilder_.getMessage(index); } public Builder setRow(int index, MysqlxCrud.Insert.TypedRow value) { if (this.rowBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureRowIsMutable(); this.row_.set(index, value); onChanged(); } else { this.rowBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setRow(int index, MysqlxCrud.Insert.TypedRow.Builder builderForValue) { if (this.rowBuilder_ == null) { ensureRowIsMutable(); this.row_.set(index, builderForValue.build()); onChanged(); } else { this.rowBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addRow(MysqlxCrud.Insert.TypedRow value) { if (this.rowBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureRowIsMutable(); this.row_.add(value); onChanged(); } else { this.rowBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addRow(int index, MysqlxCrud.Insert.TypedRow value) { if (this.rowBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureRowIsMutable(); this.row_.add(index, value); onChanged(); } else { this.rowBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addRow(MysqlxCrud.Insert.TypedRow.Builder builderForValue) { if (this.rowBuilder_ == null) { ensureRowIsMutable(); this.row_.add(builderForValue.build()); onChanged(); } else { this.rowBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addRow(int index, MysqlxCrud.Insert.TypedRow.Builder builderForValue) { if (this.rowBuilder_ == null) { ensureRowIsMutable(); this.row_.add(index, builderForValue.build()); onChanged(); } else { this.rowBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllRow(Iterable<? extends MysqlxCrud.Insert.TypedRow> values) { if (this.rowBuilder_ == null) { ensureRowIsMutable(); AbstractMessageLite.Builder.addAll(values, this.row_); onChanged(); } else { this.rowBuilder_.addAllMessages(values); }  return this; } public Builder clearRow() { if (this.rowBuilder_ == null) { this.row_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFF7; onChanged(); } else { this.rowBuilder_.clear(); }  return this; } public Builder removeRow(int index) { if (this.rowBuilder_ == null) { ensureRowIsMutable(); this.row_.remove(index); onChanged(); } else { this.rowBuilder_.remove(index); }  return this; } public MysqlxCrud.Insert.TypedRow.Builder getRowBuilder(int index) { return (MysqlxCrud.Insert.TypedRow.Builder)getRowFieldBuilder().getBuilder(index); } public MysqlxCrud.Insert.TypedRowOrBuilder getRowOrBuilder(int index) { if (this.rowBuilder_ == null) return this.row_.get(index);  return (MysqlxCrud.Insert.TypedRowOrBuilder)this.rowBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxCrud.Insert.TypedRowOrBuilder> getRowOrBuilderList() { if (this.rowBuilder_ != null) return this.rowBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.row_); } public MysqlxCrud.Insert.TypedRow.Builder addRowBuilder() { return (MysqlxCrud.Insert.TypedRow.Builder)getRowFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Insert.TypedRow.getDefaultInstance()); } public MysqlxCrud.Insert.TypedRow.Builder addRowBuilder(int index) { return (MysqlxCrud.Insert.TypedRow.Builder)getRowFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Insert.TypedRow.getDefaultInstance()); } public List<MysqlxCrud.Insert.TypedRow.Builder> getRowBuilderList() { return getRowFieldBuilder().getBuilderList(); }
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.Insert.TypedRow, MysqlxCrud.Insert.TypedRow.Builder, MysqlxCrud.Insert.TypedRowOrBuilder> getRowFieldBuilder() { if (this.rowBuilder_ == null) { this.rowBuilder_ = new RepeatedFieldBuilderV3(this.row_, ((this.bitField0_ & 0x8) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.row_ = null; }  return this.rowBuilder_; }
/* 14745 */       private void ensureArgsIsMutable() { if ((this.bitField0_ & 0x10) == 0) {
/* 14746 */           this.args_ = new ArrayList<>(this.args_);
/* 14747 */           this.bitField0_ |= 0x10;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxDatatypes.Scalar> getArgsList() {
/* 14762 */         if (this.argsBuilder_ == null) {
/* 14763 */           return Collections.unmodifiableList(this.args_);
/*       */         }
/* 14765 */         return this.argsBuilder_.getMessageList();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getArgsCount() {
/* 14776 */         if (this.argsBuilder_ == null) {
/* 14777 */           return this.args_.size();
/*       */         }
/* 14779 */         return this.argsBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar getArgs(int index) {
/* 14790 */         if (this.argsBuilder_ == null) {
/* 14791 */           return this.args_.get(index);
/*       */         }
/* 14793 */         return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
/* 14805 */         if (this.argsBuilder_ == null) {
/* 14806 */           if (value == null) {
/* 14807 */             throw new NullPointerException();
/*       */           }
/* 14809 */           ensureArgsIsMutable();
/* 14810 */           this.args_.set(index, value);
/* 14811 */           onChanged();
/*       */         } else {
/* 14813 */           this.argsBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/* 14815 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 14826 */         if (this.argsBuilder_ == null) {
/* 14827 */           ensureArgsIsMutable();
/* 14828 */           this.args_.set(index, builderForValue.build());
/* 14829 */           onChanged();
/*       */         } else {
/* 14831 */           this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 14833 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(MysqlxDatatypes.Scalar value) {
/* 14843 */         if (this.argsBuilder_ == null) {
/* 14844 */           if (value == null) {
/* 14845 */             throw new NullPointerException();
/*       */           }
/* 14847 */           ensureArgsIsMutable();
/* 14848 */           this.args_.add(value);
/* 14849 */           onChanged();
/*       */         } else {
/* 14851 */           this.argsBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/* 14853 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
/* 14864 */         if (this.argsBuilder_ == null) {
/* 14865 */           if (value == null) {
/* 14866 */             throw new NullPointerException();
/*       */           }
/* 14868 */           ensureArgsIsMutable();
/* 14869 */           this.args_.add(index, value);
/* 14870 */           onChanged();
/*       */         } else {
/* 14872 */           this.argsBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/* 14874 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 14885 */         if (this.argsBuilder_ == null) {
/* 14886 */           ensureArgsIsMutable();
/* 14887 */           this.args_.add(builderForValue.build());
/* 14888 */           onChanged();
/*       */         } else {
/* 14890 */           this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 14892 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 14903 */         if (this.argsBuilder_ == null) {
/* 14904 */           ensureArgsIsMutable();
/* 14905 */           this.args_.add(index, builderForValue.build());
/* 14906 */           onChanged();
/*       */         } else {
/* 14908 */           this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 14910 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
/* 14921 */         if (this.argsBuilder_ == null) {
/* 14922 */           ensureArgsIsMutable();
/* 14923 */           AbstractMessageLite.Builder.addAll(values, this.args_);
/*       */           
/* 14925 */           onChanged();
/*       */         } else {
/* 14927 */           this.argsBuilder_.addAllMessages(values);
/*       */         } 
/* 14929 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearArgs() {
/* 14939 */         if (this.argsBuilder_ == null) {
/* 14940 */           this.args_ = Collections.emptyList();
/* 14941 */           this.bitField0_ &= 0xFFFFFFEF;
/* 14942 */           onChanged();
/*       */         } else {
/* 14944 */           this.argsBuilder_.clear();
/*       */         } 
/* 14946 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeArgs(int index) {
/* 14956 */         if (this.argsBuilder_ == null) {
/* 14957 */           ensureArgsIsMutable();
/* 14958 */           this.args_.remove(index);
/* 14959 */           onChanged();
/*       */         } else {
/* 14961 */           this.argsBuilder_.remove(index);
/*       */         } 
/* 14963 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
/* 14974 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
/* 14985 */         if (this.argsBuilder_ == null)
/* 14986 */           return this.args_.get(index); 
/* 14987 */         return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
/* 14999 */         if (this.argsBuilder_ != null) {
/* 15000 */           return this.argsBuilder_.getMessageOrBuilderList();
/*       */         }
/* 15002 */         return Collections.unmodifiableList((List)this.args_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
/* 15013 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(
/* 15014 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
/* 15025 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, 
/* 15026 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
/* 15037 */         return getArgsFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
/* 15042 */         if (this.argsBuilder_ == null) {
/* 15043 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/* 15048 */             .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x10) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 15049 */           this.args_ = null;
/*       */         } 
/* 15051 */         return this.argsBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasUpsert() {
/* 15065 */         return ((this.bitField0_ & 0x20) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean getUpsert() {
/* 15077 */         return this.upsert_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setUpsert(boolean value) {
/* 15090 */         this.bitField0_ |= 0x20;
/* 15091 */         this.upsert_ = value;
/* 15092 */         onChanged();
/* 15093 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearUpsert() {
/* 15105 */         this.bitField0_ &= 0xFFFFFFDF;
/* 15106 */         this.upsert_ = false;
/* 15107 */         onChanged();
/* 15108 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 15113 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 15119 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 15129 */     private static final Insert DEFAULT_INSTANCE = new Insert();
/*       */ 
/*       */     
/*       */     public static Insert getDefaultInstance() {
/* 15133 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 15137 */     public static final Parser<Insert> PARSER = (Parser<Insert>)new AbstractParser<Insert>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Insert parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 15143 */           return new MysqlxCrud.Insert(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Insert> parser() {
/* 15148 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Insert> getParserForType() {
/* 15153 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Insert getDefaultInstanceForType() {
/* 15158 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static interface TypedRowOrBuilder
/*       */       extends MessageOrBuilder
/*       */     {
/*       */       List<MysqlxExpr.Expr> getFieldList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       MysqlxExpr.Expr getField(int param2Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       int getFieldCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int param2Int);
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface UpdateOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Collection getCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.DataModel getDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLimit();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Limit getLimit();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitOrBuilder getLimitOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxCrud.Order> getOrderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Order getOrder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getOrderCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxCrud.UpdateOperation> getOperationList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.UpdateOperation getOperation(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getOperationCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxDatatypes.Scalar> getArgsList();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.Scalar getArgs(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getArgsCount();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLimitExpr();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitExpr getLimitExpr();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Update
/*       */     extends GeneratedMessageV3
/*       */     implements UpdateOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLLECTION_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Collection collection_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DATA_MODEL_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */     
/*       */     private int dataModel_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int CRITERIA_FIELD_NUMBER = 4;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr criteria_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LIMIT_FIELD_NUMBER = 5;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Limit limit_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ORDER_FIELD_NUMBER = 6;
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxCrud.Order> order_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int OPERATION_FIELD_NUMBER = 7;
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxCrud.UpdateOperation> operation_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ARGS_FIELD_NUMBER = 8;
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxDatatypes.Scalar> args_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LIMIT_EXPR_FIELD_NUMBER = 9;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.LimitExpr limitExpr_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private Update(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/* 15453 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/* 15969 */       this.memoizedIsInitialized = -1; } private Update() { this.memoizedIsInitialized = -1; this.dataModel_ = 1; this.order_ = Collections.emptyList(); this.operation_ = Collections.emptyList(); this.args_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Update(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Update(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxCrud.Collection.Builder builder2; int rawValue; MysqlxExpr.Expr.Builder builder1; MysqlxCrud.Limit.Builder builder; MysqlxCrud.LimitExpr.Builder subBuilder; MysqlxCrud.DataModel value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 18: builder2 = null; if ((this.bitField0_ & 0x1) != 0) builder2 = this.collection_.toBuilder();  this.collection_ = (MysqlxCrud.Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry); if (builder2 != null) { builder2.mergeFrom(this.collection_); this.collection_ = builder2.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 24: rawValue = input.readEnum(); value = MysqlxCrud.DataModel.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(3, rawValue); continue; }  this.bitField0_ |= 0x2; this.dataModel_ = rawValue; continue;case 34: builder1 = null; if ((this.bitField0_ & 0x4) != 0) builder1 = this.criteria_.toBuilder();  this.criteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (builder1 != null) { builder1.mergeFrom(this.criteria_); this.criteria_ = builder1.buildPartial(); }  this.bitField0_ |= 0x4; continue;case 42: builder = null; if ((this.bitField0_ & 0x8) != 0) builder = this.limit_.toBuilder();  this.limit_ = (MysqlxCrud.Limit)input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.limit_); this.limit_ = builder.buildPartial(); }  this.bitField0_ |= 0x8; continue;case 50: if ((mutable_bitField0_ & 0x10) == 0) { this.order_ = new ArrayList<>(); mutable_bitField0_ |= 0x10; }  this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry)); continue;case 58: if ((mutable_bitField0_ & 0x20) == 0) { this.operation_ = new ArrayList<>(); mutable_bitField0_ |= 0x20; }  this.operation_.add(input.readMessage(MysqlxCrud.UpdateOperation.PARSER, extensionRegistry)); continue;case 66: if ((mutable_bitField0_ & 0x40) == 0) { this.args_ = new ArrayList<>(); mutable_bitField0_ |= 0x40; }  this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry)); continue;case 74: subBuilder = null; if ((this.bitField0_ & 0x10) != 0) subBuilder = this.limitExpr_.toBuilder();  this.limitExpr_ = (MysqlxCrud.LimitExpr)input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.limitExpr_); this.limitExpr_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x10; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x10) != 0) this.order_ = Collections.unmodifiableList(this.order_);  if ((mutable_bitField0_ & 0x20) != 0) this.operation_ = Collections.unmodifiableList(this.operation_);  if ((mutable_bitField0_ & 0x40) != 0) this.args_ = Collections.unmodifiableList(this.args_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable.ensureFieldAccessorsInitialized(Update.class, Builder.class); } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public boolean hasCriteria() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxExpr.Expr getCriteria() { return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() { return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } public boolean hasLimit() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Limit getLimit() { return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() { return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } public List<MysqlxCrud.Order> getOrderList() { return this.order_; } public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() { return (List)this.order_; } public int getOrderCount() { return this.order_.size(); } public MysqlxCrud.Order getOrder(int index) { return this.order_.get(index); } public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) { return this.order_.get(index); } public List<MysqlxCrud.UpdateOperation> getOperationList() { return this.operation_; } public List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList() { return (List)this.operation_; } public int getOperationCount() { return this.operation_.size(); } public MysqlxCrud.UpdateOperation getOperation(int index) { return this.operation_.get(index); } public MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int index) { return this.operation_.get(index); } public List<MysqlxDatatypes.Scalar> getArgsList() { return this.args_; } public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() { return (List)this.args_; } public int getArgsCount() { return this.args_.size(); } public MysqlxDatatypes.Scalar getArgs(int index) { return this.args_.get(index); } public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) { return this.args_.get(index); } public boolean hasLimitExpr() { return ((this.bitField0_ & 0x10) != 0); }
/*       */     public MysqlxCrud.LimitExpr getLimitExpr() { return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; }
/*       */     public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() { return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; }
/* 15972 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 15973 */       if (isInitialized == 1) return true; 
/* 15974 */       if (isInitialized == 0) return false;
/*       */       
/* 15976 */       if (!hasCollection()) {
/* 15977 */         this.memoizedIsInitialized = 0;
/* 15978 */         return false;
/*       */       } 
/* 15980 */       if (!getCollection().isInitialized()) {
/* 15981 */         this.memoizedIsInitialized = 0;
/* 15982 */         return false;
/*       */       } 
/* 15984 */       if (hasCriteria() && 
/* 15985 */         !getCriteria().isInitialized()) {
/* 15986 */         this.memoizedIsInitialized = 0;
/* 15987 */         return false;
/*       */       } 
/*       */       
/* 15990 */       if (hasLimit() && 
/* 15991 */         !getLimit().isInitialized()) {
/* 15992 */         this.memoizedIsInitialized = 0;
/* 15993 */         return false;
/*       */       } 
/*       */       int i;
/* 15996 */       for (i = 0; i < getOrderCount(); i++) {
/* 15997 */         if (!getOrder(i).isInitialized()) {
/* 15998 */           this.memoizedIsInitialized = 0;
/* 15999 */           return false;
/*       */         } 
/*       */       } 
/* 16002 */       for (i = 0; i < getOperationCount(); i++) {
/* 16003 */         if (!getOperation(i).isInitialized()) {
/* 16004 */           this.memoizedIsInitialized = 0;
/* 16005 */           return false;
/*       */         } 
/*       */       } 
/* 16008 */       for (i = 0; i < getArgsCount(); i++) {
/* 16009 */         if (!getArgs(i).isInitialized()) {
/* 16010 */           this.memoizedIsInitialized = 0;
/* 16011 */           return false;
/*       */         } 
/*       */       } 
/* 16014 */       if (hasLimitExpr() && 
/* 16015 */         !getLimitExpr().isInitialized()) {
/* 16016 */         this.memoizedIsInitialized = 0;
/* 16017 */         return false;
/*       */       } 
/*       */       
/* 16020 */       this.memoizedIsInitialized = 1;
/* 16021 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/* 16027 */       if ((this.bitField0_ & 0x1) != 0) {
/* 16028 */         output.writeMessage(2, (MessageLite)getCollection());
/*       */       }
/* 16030 */       if ((this.bitField0_ & 0x2) != 0) {
/* 16031 */         output.writeEnum(3, this.dataModel_);
/*       */       }
/* 16033 */       if ((this.bitField0_ & 0x4) != 0) {
/* 16034 */         output.writeMessage(4, (MessageLite)getCriteria());
/*       */       }
/* 16036 */       if ((this.bitField0_ & 0x8) != 0)
/* 16037 */         output.writeMessage(5, (MessageLite)getLimit()); 
/*       */       int i;
/* 16039 */       for (i = 0; i < this.order_.size(); i++) {
/* 16040 */         output.writeMessage(6, (MessageLite)this.order_.get(i));
/*       */       }
/* 16042 */       for (i = 0; i < this.operation_.size(); i++) {
/* 16043 */         output.writeMessage(7, (MessageLite)this.operation_.get(i));
/*       */       }
/* 16045 */       for (i = 0; i < this.args_.size(); i++) {
/* 16046 */         output.writeMessage(8, (MessageLite)this.args_.get(i));
/*       */       }
/* 16048 */       if ((this.bitField0_ & 0x10) != 0) {
/* 16049 */         output.writeMessage(9, (MessageLite)getLimitExpr());
/*       */       }
/* 16051 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/* 16056 */       int size = this.memoizedSize;
/* 16057 */       if (size != -1) return size;
/*       */       
/* 16059 */       size = 0;
/* 16060 */       if ((this.bitField0_ & 0x1) != 0) {
/* 16061 */         size += 
/* 16062 */           CodedOutputStream.computeMessageSize(2, (MessageLite)getCollection());
/*       */       }
/* 16064 */       if ((this.bitField0_ & 0x2) != 0) {
/* 16065 */         size += 
/* 16066 */           CodedOutputStream.computeEnumSize(3, this.dataModel_);
/*       */       }
/* 16068 */       if ((this.bitField0_ & 0x4) != 0) {
/* 16069 */         size += 
/* 16070 */           CodedOutputStream.computeMessageSize(4, (MessageLite)getCriteria());
/*       */       }
/* 16072 */       if ((this.bitField0_ & 0x8) != 0)
/* 16073 */         size += 
/* 16074 */           CodedOutputStream.computeMessageSize(5, (MessageLite)getLimit()); 
/*       */       int i;
/* 16076 */       for (i = 0; i < this.order_.size(); i++) {
/* 16077 */         size += 
/* 16078 */           CodedOutputStream.computeMessageSize(6, (MessageLite)this.order_.get(i));
/*       */       }
/* 16080 */       for (i = 0; i < this.operation_.size(); i++) {
/* 16081 */         size += 
/* 16082 */           CodedOutputStream.computeMessageSize(7, (MessageLite)this.operation_.get(i));
/*       */       }
/* 16084 */       for (i = 0; i < this.args_.size(); i++) {
/* 16085 */         size += 
/* 16086 */           CodedOutputStream.computeMessageSize(8, (MessageLite)this.args_.get(i));
/*       */       }
/* 16088 */       if ((this.bitField0_ & 0x10) != 0) {
/* 16089 */         size += 
/* 16090 */           CodedOutputStream.computeMessageSize(9, (MessageLite)getLimitExpr());
/*       */       }
/* 16092 */       size += this.unknownFields.getSerializedSize();
/* 16093 */       this.memoizedSize = size;
/* 16094 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/* 16099 */       if (obj == this) {
/* 16100 */         return true;
/*       */       }
/* 16102 */       if (!(obj instanceof Update)) {
/* 16103 */         return super.equals(obj);
/*       */       }
/* 16105 */       Update other = (Update)obj;
/*       */       
/* 16107 */       if (hasCollection() != other.hasCollection()) return false; 
/* 16108 */       if (hasCollection() && 
/*       */         
/* 16110 */         !getCollection().equals(other.getCollection())) return false;
/*       */       
/* 16112 */       if (hasDataModel() != other.hasDataModel()) return false; 
/* 16113 */       if (hasDataModel() && 
/* 16114 */         this.dataModel_ != other.dataModel_) return false;
/*       */       
/* 16116 */       if (hasCriteria() != other.hasCriteria()) return false; 
/* 16117 */       if (hasCriteria() && 
/*       */         
/* 16119 */         !getCriteria().equals(other.getCriteria())) return false;
/*       */       
/* 16121 */       if (hasLimit() != other.hasLimit()) return false; 
/* 16122 */       if (hasLimit() && 
/*       */         
/* 16124 */         !getLimit().equals(other.getLimit())) return false;
/*       */ 
/*       */       
/* 16127 */       if (!getOrderList().equals(other.getOrderList())) return false;
/*       */       
/* 16129 */       if (!getOperationList().equals(other.getOperationList())) return false;
/*       */       
/* 16131 */       if (!getArgsList().equals(other.getArgsList())) return false; 
/* 16132 */       if (hasLimitExpr() != other.hasLimitExpr()) return false; 
/* 16133 */       if (hasLimitExpr() && 
/*       */         
/* 16135 */         !getLimitExpr().equals(other.getLimitExpr())) return false;
/*       */       
/* 16137 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 16138 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/* 16143 */       if (this.memoizedHashCode != 0) {
/* 16144 */         return this.memoizedHashCode;
/*       */       }
/* 16146 */       int hash = 41;
/* 16147 */       hash = 19 * hash + getDescriptor().hashCode();
/* 16148 */       if (hasCollection()) {
/* 16149 */         hash = 37 * hash + 2;
/* 16150 */         hash = 53 * hash + getCollection().hashCode();
/*       */       } 
/* 16152 */       if (hasDataModel()) {
/* 16153 */         hash = 37 * hash + 3;
/* 16154 */         hash = 53 * hash + this.dataModel_;
/*       */       } 
/* 16156 */       if (hasCriteria()) {
/* 16157 */         hash = 37 * hash + 4;
/* 16158 */         hash = 53 * hash + getCriteria().hashCode();
/*       */       } 
/* 16160 */       if (hasLimit()) {
/* 16161 */         hash = 37 * hash + 5;
/* 16162 */         hash = 53 * hash + getLimit().hashCode();
/*       */       } 
/* 16164 */       if (getOrderCount() > 0) {
/* 16165 */         hash = 37 * hash + 6;
/* 16166 */         hash = 53 * hash + getOrderList().hashCode();
/*       */       } 
/* 16168 */       if (getOperationCount() > 0) {
/* 16169 */         hash = 37 * hash + 7;
/* 16170 */         hash = 53 * hash + getOperationList().hashCode();
/*       */       } 
/* 16172 */       if (getArgsCount() > 0) {
/* 16173 */         hash = 37 * hash + 8;
/* 16174 */         hash = 53 * hash + getArgsList().hashCode();
/*       */       } 
/* 16176 */       if (hasLimitExpr()) {
/* 16177 */         hash = 37 * hash + 9;
/* 16178 */         hash = 53 * hash + getLimitExpr().hashCode();
/*       */       } 
/* 16180 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 16181 */       this.memoizedHashCode = hash;
/* 16182 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Update parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 16188 */       return (Update)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Update parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 16194 */       return (Update)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Update parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 16199 */       return (Update)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Update parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 16205 */       return (Update)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Update parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 16209 */       return (Update)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Update parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 16215 */       return (Update)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Update parseFrom(InputStream input) throws IOException {
/* 16219 */       return 
/* 16220 */         (Update)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Update parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 16226 */       return 
/* 16227 */         (Update)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Update parseDelimitedFrom(InputStream input) throws IOException {
/* 16231 */       return 
/* 16232 */         (Update)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Update parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 16238 */       return 
/* 16239 */         (Update)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Update parseFrom(CodedInputStream input) throws IOException {
/* 16244 */       return 
/* 16245 */         (Update)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Update parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 16251 */       return 
/* 16252 */         (Update)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/* 16256 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/* 16258 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Update prototype) {
/* 16261 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/* 16265 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 16266 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 16272 */       Builder builder = new Builder(parent);
/* 16273 */       return builder;
/*       */     }
/*       */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.UpdateOrBuilder { private int bitField0_; private MysqlxCrud.Collection collection_; private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_; private int dataModel_;
/*       */       private MysqlxExpr.Expr criteria_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
/*       */       private MysqlxCrud.Limit limit_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
/*       */       private List<MysqlxCrud.Order> order_;
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
/*       */       private List<MysqlxCrud.UpdateOperation> operation_;
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.UpdateOperation, MysqlxCrud.UpdateOperation.Builder, MysqlxCrud.UpdateOperationOrBuilder> operationBuilder_;
/*       */       private List<MysqlxDatatypes.Scalar> args_;
/*       */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
/*       */       private MysqlxCrud.LimitExpr limitExpr_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/* 16290 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 16296 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable
/* 16297 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Update.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/* 16825 */         this.dataModel_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 17204 */         this
/* 17205 */           .order_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 17516 */         this
/* 17517 */           .operation_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 17846 */         this
/* 17847 */           .args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.dataModel_ = 1; this.order_ = Collections.emptyList(); this.operation_ = Collections.emptyList(); this.args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Update.alwaysUseFieldBuilders) { getCollectionFieldBuilder(); getCriteriaFieldBuilder(); getLimitFieldBuilder(); getOrderFieldBuilder(); getOperationFieldBuilder(); getArgsFieldBuilder(); getLimitExprFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.collectionBuilder_ == null) { this.collection_ = null; } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.dataModel_ = 1; this.bitField0_ &= 0xFFFFFFFD; if (this.criteriaBuilder_ == null) { this.criteria_ = null; } else { this.criteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; if (this.limitBuilder_ == null) { this.limit_ = null; } else { this.limitBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; if (this.orderBuilder_ == null) { this.order_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFEF; } else { this.orderBuilder_.clear(); }  if (this.operationBuilder_ == null) { this.operation_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFDF; } else { this.operationBuilder_.clear(); }  if (this.argsBuilder_ == null) { this.args_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFBF; } else { this.argsBuilder_.clear(); }  if (this.limitExprBuilder_ == null) { this.limitExpr_ = null; } else { this.limitExprBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFF7F; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor; } public MysqlxCrud.Update getDefaultInstanceForType() { return MysqlxCrud.Update.getDefaultInstance(); } public MysqlxCrud.Update build() { MysqlxCrud.Update result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Update buildPartial() { MysqlxCrud.Update result = new MysqlxCrud.Update(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.collectionBuilder_ == null) { result.collection_ = this.collection_; } else { result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.dataModel_ = this.dataModel_; if ((from_bitField0_ & 0x4) != 0) { if (this.criteriaBuilder_ == null) { result.criteria_ = this.criteria_; } else { result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build(); }  to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x8) != 0) { if (this.limitBuilder_ == null) { result.limit_ = this.limit_; } else { result.limit_ = (MysqlxCrud.Limit)this.limitBuilder_.build(); }  to_bitField0_ |= 0x8; }  if (this.orderBuilder_ == null) { if ((this.bitField0_ & 0x10) != 0) { this.order_ = Collections.unmodifiableList(this.order_); this.bitField0_ &= 0xFFFFFFEF; }  result.order_ = this.order_; } else { result.order_ = this.orderBuilder_.build(); }  if (this.operationBuilder_ == null) { if ((this.bitField0_ & 0x20) != 0) { this.operation_ = Collections.unmodifiableList(this.operation_); this.bitField0_ &= 0xFFFFFFDF; }  result.operation_ = this.operation_; } else { result.operation_ = this.operationBuilder_.build(); }  if (this.argsBuilder_ == null) { if ((this.bitField0_ & 0x40) != 0) { this.args_ = Collections.unmodifiableList(this.args_); this.bitField0_ &= 0xFFFFFFBF; }  result.args_ = this.args_; } else { result.args_ = this.argsBuilder_.build(); }  if ((from_bitField0_ & 0x80) != 0) { if (this.limitExprBuilder_ == null) { result.limitExpr_ = this.limitExpr_; } else { result.limitExpr_ = (MysqlxCrud.LimitExpr)this.limitExprBuilder_.build(); }  to_bitField0_ |= 0x10; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Update) return mergeFrom((MysqlxCrud.Update)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Update other) { if (other == MysqlxCrud.Update.getDefaultInstance()) return this;  if (other.hasCollection()) mergeCollection(other.getCollection());  if (other.hasDataModel()) setDataModel(other.getDataModel());  if (other.hasCriteria()) mergeCriteria(other.getCriteria());  if (other.hasLimit()) mergeLimit(other.getLimit());  if (this.orderBuilder_ == null) { if (!other.order_.isEmpty()) { if (this.order_.isEmpty()) { this.order_ = other.order_; this.bitField0_ &= 0xFFFFFFEF; } else { ensureOrderIsMutable(); this.order_.addAll(other.order_); }  onChanged(); }  } else if (!other.order_.isEmpty()) { if (this.orderBuilder_.isEmpty()) { this.orderBuilder_.dispose(); this.orderBuilder_ = null; this.order_ = other.order_; this.bitField0_ &= 0xFFFFFFEF; this.orderBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? getOrderFieldBuilder() : null; } else { this.orderBuilder_.addAllMessages(other.order_); }  }  if (this.operationBuilder_ == null) { if (!other.operation_.isEmpty()) { if (this.operation_.isEmpty()) { this.operation_ = other.operation_; this.bitField0_ &= 0xFFFFFFDF; } else { ensureOperationIsMutable(); this.operation_.addAll(other.operation_); }  onChanged(); }  } else if (!other.operation_.isEmpty()) { if (this.operationBuilder_.isEmpty()) { this.operationBuilder_.dispose(); this.operationBuilder_ = null; this.operation_ = other.operation_; this.bitField0_ &= 0xFFFFFFDF; this.operationBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? getOperationFieldBuilder() : null; } else { this.operationBuilder_.addAllMessages(other.operation_); }  }  if (this.argsBuilder_ == null) { if (!other.args_.isEmpty()) { if (this.args_.isEmpty()) { this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFBF; } else { ensureArgsIsMutable(); this.args_.addAll(other.args_); }  onChanged(); }  } else if (!other.args_.isEmpty()) { if (this.argsBuilder_.isEmpty()) { this.argsBuilder_.dispose(); this.argsBuilder_ = null; this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFBF; this.argsBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null; } else { this.argsBuilder_.addAllMessages(other.args_); }  }  if (other.hasLimitExpr()) mergeLimitExpr(other.getLimitExpr());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasCollection()) return false;  if (!getCollection().isInitialized()) return false;  if (hasCriteria() && !getCriteria().isInitialized()) return false;  if (hasLimit() && !getLimit().isInitialized()) return false;  int i; for (i = 0; i < getOrderCount(); i++) { if (!getOrder(i).isInitialized()) return false;  }  for (i = 0; i < getOperationCount(); i++) { if (!getOperation(i).isInitialized()) return false;  }  for (i = 0; i < getArgsCount(); i++) { if (!getArgs(i).isInitialized()) return false;  }  if (hasLimitExpr() && !getLimitExpr().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Update parsedMessage = null; try { parsedMessage = (MysqlxCrud.Update)MysqlxCrud.Update.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Update)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { if (this.collectionBuilder_ == null) return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;  return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage(); } public Builder setCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if (value == null) throw new NullPointerException();  this.collection_ = value; onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) { if (this.collectionBuilder_ == null) { this.collection_ = builderForValue.build(); onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; } public Builder mergeCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) { this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial(); } else { this.collection_ = value; }  onChanged(); } else { this.collectionBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder clearCollection() { if (this.collectionBuilder_ == null) { this.collection_ = null; onChanged(); } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; } public MysqlxCrud.Collection.Builder getCollectionBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder(); } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { if (this.collectionBuilder_ != null) return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();  return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() { if (this.collectionBuilder_ == null) { this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.collection_ = null; }  return this.collectionBuilder_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public Builder setDataModel(MysqlxCrud.DataModel value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.dataModel_ = value.getNumber(); onChanged(); return this; } public Builder clearDataModel() { this.bitField0_ &= 0xFFFFFFFD; this.dataModel_ = 1; onChanged(); return this; } public boolean hasCriteria() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxExpr.Expr getCriteria() { if (this.criteriaBuilder_ == null) return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;  return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage(); } public Builder setCriteria(MysqlxExpr.Expr value) { if (this.criteriaBuilder_ == null) { if (value == null) throw new NullPointerException();  this.criteria_ = value; onChanged(); } else { this.criteriaBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) { if (this.criteriaBuilder_ == null) { this.criteria_ = builderForValue.build(); onChanged(); } else { this.criteriaBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x4; return this; } public Builder mergeCriteria(MysqlxExpr.Expr value) { if (this.criteriaBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) { this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial(); } else { this.criteria_ = value; }  onChanged(); } else { this.criteriaBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder clearCriteria() { if (this.criteriaBuilder_ == null) { this.criteria_ = null; onChanged(); } else { this.criteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; return this; } public MysqlxExpr.Expr.Builder getCriteriaBuilder() { this.bitField0_ |= 0x4; onChanged(); return (MysqlxExpr.Expr.Builder)getCriteriaFieldBuilder().getBuilder(); } public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() { if (this.criteriaBuilder_ != null) return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder();  return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() { if (this.criteriaBuilder_ == null) { this.criteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.criteria_ = null; }  return this.criteriaBuilder_; } public boolean hasLimit() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Limit getLimit() { if (this.limitBuilder_ == null) return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;  return (MysqlxCrud.Limit)this.limitBuilder_.getMessage(); } public Builder setLimit(MysqlxCrud.Limit value) { if (this.limitBuilder_ == null) { if (value == null) throw new NullPointerException();  this.limit_ = value; onChanged(); } else { this.limitBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; } public Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) { if (this.limitBuilder_ == null) { this.limit_ = builderForValue.build(); onChanged(); } else { this.limitBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x8; return this; }
/*       */       public Builder mergeLimit(MysqlxCrud.Limit value) { if (this.limitBuilder_ == null) { if ((this.bitField0_ & 0x8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) { this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial(); } else { this.limit_ = value; }  onChanged(); } else { this.limitBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; }
/* 17849 */       private void ensureArgsIsMutable() { if ((this.bitField0_ & 0x40) == 0)
/* 17850 */         { this.args_ = new ArrayList<>(this.args_);
/* 17851 */           this.bitField0_ |= 0x40; }  } public Builder clearLimit() { if (this.limitBuilder_ == null) { this.limit_ = null; onChanged(); } else { this.limitBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; return this; } public MysqlxCrud.Limit.Builder getLimitBuilder() { this.bitField0_ |= 0x8; onChanged(); return (MysqlxCrud.Limit.Builder)getLimitFieldBuilder().getBuilder(); } public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() { if (this.limitBuilder_ != null) return (MysqlxCrud.LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder();  return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() { if (this.limitBuilder_ == null) { this.limitBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimit(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.limit_ = null; }  return this.limitBuilder_; } private void ensureOrderIsMutable() { if ((this.bitField0_ & 0x10) == 0) { this.order_ = new ArrayList<>(this.order_); this.bitField0_ |= 0x10; }  } public List<MysqlxCrud.Order> getOrderList() { if (this.orderBuilder_ == null) return Collections.unmodifiableList(this.order_);  return this.orderBuilder_.getMessageList(); } public int getOrderCount() { if (this.orderBuilder_ == null) return this.order_.size();  return this.orderBuilder_.getCount(); } public MysqlxCrud.Order getOrder(int index) { if (this.orderBuilder_ == null) return this.order_.get(index);  return (MysqlxCrud.Order)this.orderBuilder_.getMessage(index); } public Builder setOrder(int index, MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.set(index, value); onChanged(); } else { this.orderBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.set(index, builderForValue.build()); onChanged(); } else { this.orderBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addOrder(MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.add(value); onChanged(); } else { this.orderBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addOrder(int index, MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.add(index, value); onChanged(); } else { this.orderBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addOrder(MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.add(builderForValue.build()); onChanged(); } else { this.orderBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.add(index, builderForValue.build()); onChanged(); } else { this.orderBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); AbstractMessageLite.Builder.addAll(values, this.order_); onChanged(); } else { this.orderBuilder_.addAllMessages(values); }  return this; } public Builder clearOrder() { if (this.orderBuilder_ == null) { this.order_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFEF; onChanged(); } else { this.orderBuilder_.clear(); }  return this; } public Builder removeOrder(int index) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.remove(index); onChanged(); } else { this.orderBuilder_.remove(index); }  return this; } public MysqlxCrud.Order.Builder getOrderBuilder(int index) { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().getBuilder(index); } public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) { if (this.orderBuilder_ == null) return this.order_.get(index);  return (MysqlxCrud.OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() { if (this.orderBuilder_ != null) return this.orderBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.order_); } public MysqlxCrud.Order.Builder addOrderBuilder() { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Order.getDefaultInstance()); } public MysqlxCrud.Order.Builder addOrderBuilder(int index) { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Order.getDefaultInstance()); } public List<MysqlxCrud.Order.Builder> getOrderBuilderList() { return getOrderFieldBuilder().getBuilderList(); } private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() { if (this.orderBuilder_ == null) { this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, ((this.bitField0_ & 0x10) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.order_ = null; }  return this.orderBuilder_; } private void ensureOperationIsMutable() { if ((this.bitField0_ & 0x20) == 0) { this.operation_ = new ArrayList<>(this.operation_); this.bitField0_ |= 0x20; }  } public List<MysqlxCrud.UpdateOperation> getOperationList() { if (this.operationBuilder_ == null) return Collections.unmodifiableList(this.operation_);  return this.operationBuilder_.getMessageList(); } public int getOperationCount() { if (this.operationBuilder_ == null) return this.operation_.size();  return this.operationBuilder_.getCount(); } public MysqlxCrud.UpdateOperation getOperation(int index) { if (this.operationBuilder_ == null) return this.operation_.get(index);  return (MysqlxCrud.UpdateOperation)this.operationBuilder_.getMessage(index); } public Builder setOperation(int index, MysqlxCrud.UpdateOperation value) { if (this.operationBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOperationIsMutable(); this.operation_.set(index, value); onChanged(); } else { this.operationBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setOperation(int index, MysqlxCrud.UpdateOperation.Builder builderForValue) { if (this.operationBuilder_ == null) { ensureOperationIsMutable(); this.operation_.set(index, builderForValue.build()); onChanged(); } else { this.operationBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; }
/*       */       public Builder addOperation(MysqlxCrud.UpdateOperation value) { if (this.operationBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOperationIsMutable(); this.operation_.add(value); onChanged(); } else { this.operationBuilder_.addMessage((AbstractMessage)value); }  return this; }
/*       */       public Builder addOperation(int index, MysqlxCrud.UpdateOperation value) { if (this.operationBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOperationIsMutable(); this.operation_.add(index, value); onChanged(); } else { this.operationBuilder_.addMessage(index, (AbstractMessage)value); }  return this; }
/*       */       public Builder addOperation(MysqlxCrud.UpdateOperation.Builder builderForValue) { if (this.operationBuilder_ == null) { ensureOperationIsMutable(); this.operation_.add(builderForValue.build()); onChanged(); } else { this.operationBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; }
/*       */       public Builder addOperation(int index, MysqlxCrud.UpdateOperation.Builder builderForValue) { if (this.operationBuilder_ == null) { ensureOperationIsMutable(); this.operation_.add(index, builderForValue.build()); onChanged(); } else { this.operationBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; }
/*       */       public Builder addAllOperation(Iterable<? extends MysqlxCrud.UpdateOperation> values) { if (this.operationBuilder_ == null) { ensureOperationIsMutable(); AbstractMessageLite.Builder.addAll(values, this.operation_); onChanged(); } else { this.operationBuilder_.addAllMessages(values); }  return this; }
/*       */       public Builder clearOperation() { if (this.operationBuilder_ == null) { this.operation_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFDF; onChanged(); } else { this.operationBuilder_.clear(); }  return this; }
/*       */       public Builder removeOperation(int index) { if (this.operationBuilder_ == null) { ensureOperationIsMutable(); this.operation_.remove(index); onChanged(); } else { this.operationBuilder_.remove(index); }  return this; }
/*       */       public MysqlxCrud.UpdateOperation.Builder getOperationBuilder(int index) { return (MysqlxCrud.UpdateOperation.Builder)getOperationFieldBuilder().getBuilder(index); }
/*       */       public MysqlxCrud.UpdateOperationOrBuilder getOperationOrBuilder(int index) { if (this.operationBuilder_ == null) return this.operation_.get(index);  return (MysqlxCrud.UpdateOperationOrBuilder)this.operationBuilder_.getMessageOrBuilder(index); }
/*       */       public List<? extends MysqlxCrud.UpdateOperationOrBuilder> getOperationOrBuilderList() { if (this.operationBuilder_ != null) return this.operationBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.operation_); }
/*       */       public MysqlxCrud.UpdateOperation.Builder addOperationBuilder() { return (MysqlxCrud.UpdateOperation.Builder)getOperationFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.UpdateOperation.getDefaultInstance()); }
/*       */       public MysqlxCrud.UpdateOperation.Builder addOperationBuilder(int index) { return (MysqlxCrud.UpdateOperation.Builder)getOperationFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.UpdateOperation.getDefaultInstance()); }
/*       */       public List<MysqlxCrud.UpdateOperation.Builder> getOperationBuilderList() { return getOperationFieldBuilder().getBuilderList(); }
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.UpdateOperation, MysqlxCrud.UpdateOperation.Builder, MysqlxCrud.UpdateOperationOrBuilder> getOperationFieldBuilder() { if (this.operationBuilder_ == null) { this.operationBuilder_ = new RepeatedFieldBuilderV3(this.operation_, ((this.bitField0_ & 0x20) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.operation_ = null; }  return this.operationBuilder_; }
/* 17866 */       public List<MysqlxDatatypes.Scalar> getArgsList() { if (this.argsBuilder_ == null) {
/* 17867 */           return Collections.unmodifiableList(this.args_);
/*       */         }
/* 17869 */         return this.argsBuilder_.getMessageList(); }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getArgsCount() {
/* 17880 */         if (this.argsBuilder_ == null) {
/* 17881 */           return this.args_.size();
/*       */         }
/* 17883 */         return this.argsBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar getArgs(int index) {
/* 17894 */         if (this.argsBuilder_ == null) {
/* 17895 */           return this.args_.get(index);
/*       */         }
/* 17897 */         return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
/* 17909 */         if (this.argsBuilder_ == null) {
/* 17910 */           if (value == null) {
/* 17911 */             throw new NullPointerException();
/*       */           }
/* 17913 */           ensureArgsIsMutable();
/* 17914 */           this.args_.set(index, value);
/* 17915 */           onChanged();
/*       */         } else {
/* 17917 */           this.argsBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/* 17919 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 17930 */         if (this.argsBuilder_ == null) {
/* 17931 */           ensureArgsIsMutable();
/* 17932 */           this.args_.set(index, builderForValue.build());
/* 17933 */           onChanged();
/*       */         } else {
/* 17935 */           this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 17937 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(MysqlxDatatypes.Scalar value) {
/* 17947 */         if (this.argsBuilder_ == null) {
/* 17948 */           if (value == null) {
/* 17949 */             throw new NullPointerException();
/*       */           }
/* 17951 */           ensureArgsIsMutable();
/* 17952 */           this.args_.add(value);
/* 17953 */           onChanged();
/*       */         } else {
/* 17955 */           this.argsBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/* 17957 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
/* 17968 */         if (this.argsBuilder_ == null) {
/* 17969 */           if (value == null) {
/* 17970 */             throw new NullPointerException();
/*       */           }
/* 17972 */           ensureArgsIsMutable();
/* 17973 */           this.args_.add(index, value);
/* 17974 */           onChanged();
/*       */         } else {
/* 17976 */           this.argsBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/* 17978 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 17989 */         if (this.argsBuilder_ == null) {
/* 17990 */           ensureArgsIsMutable();
/* 17991 */           this.args_.add(builderForValue.build());
/* 17992 */           onChanged();
/*       */         } else {
/* 17994 */           this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 17996 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 18007 */         if (this.argsBuilder_ == null) {
/* 18008 */           ensureArgsIsMutable();
/* 18009 */           this.args_.add(index, builderForValue.build());
/* 18010 */           onChanged();
/*       */         } else {
/* 18012 */           this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 18014 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
/* 18025 */         if (this.argsBuilder_ == null) {
/* 18026 */           ensureArgsIsMutable();
/* 18027 */           AbstractMessageLite.Builder.addAll(values, this.args_);
/*       */           
/* 18029 */           onChanged();
/*       */         } else {
/* 18031 */           this.argsBuilder_.addAllMessages(values);
/*       */         } 
/* 18033 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearArgs() {
/* 18043 */         if (this.argsBuilder_ == null) {
/* 18044 */           this.args_ = Collections.emptyList();
/* 18045 */           this.bitField0_ &= 0xFFFFFFBF;
/* 18046 */           onChanged();
/*       */         } else {
/* 18048 */           this.argsBuilder_.clear();
/*       */         } 
/* 18050 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeArgs(int index) {
/* 18060 */         if (this.argsBuilder_ == null) {
/* 18061 */           ensureArgsIsMutable();
/* 18062 */           this.args_.remove(index);
/* 18063 */           onChanged();
/*       */         } else {
/* 18065 */           this.argsBuilder_.remove(index);
/*       */         } 
/* 18067 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
/* 18078 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
/* 18089 */         if (this.argsBuilder_ == null)
/* 18090 */           return this.args_.get(index); 
/* 18091 */         return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
/* 18103 */         if (this.argsBuilder_ != null) {
/* 18104 */           return this.argsBuilder_.getMessageOrBuilderList();
/*       */         }
/* 18106 */         return Collections.unmodifiableList((List)this.args_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
/* 18117 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(
/* 18118 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
/* 18129 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, 
/* 18130 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
/* 18141 */         return getArgsFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
/* 18146 */         if (this.argsBuilder_ == null) {
/* 18147 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/* 18152 */             .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x40) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 18153 */           this.args_ = null;
/*       */         } 
/* 18155 */         return this.argsBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasLimitExpr() {
/* 18171 */         return ((this.bitField0_ & 0x80) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr getLimitExpr() {
/* 18183 */         if (this.limitExprBuilder_ == null) {
/* 18184 */           return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
/*       */         }
/* 18186 */         return (MysqlxCrud.LimitExpr)this.limitExprBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
/* 18198 */         if (this.limitExprBuilder_ == null) {
/* 18199 */           if (value == null) {
/* 18200 */             throw new NullPointerException();
/*       */           }
/* 18202 */           this.limitExpr_ = value;
/* 18203 */           onChanged();
/*       */         } else {
/* 18205 */           this.limitExprBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/* 18207 */         this.bitField0_ |= 0x80;
/* 18208 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
/* 18220 */         if (this.limitExprBuilder_ == null) {
/* 18221 */           this.limitExpr_ = builderForValue.build();
/* 18222 */           onChanged();
/*       */         } else {
/* 18224 */           this.limitExprBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 18226 */         this.bitField0_ |= 0x80;
/* 18227 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
/* 18238 */         if (this.limitExprBuilder_ == null) {
/* 18239 */           if ((this.bitField0_ & 0x80) != 0 && this.limitExpr_ != null && this.limitExpr_ != 
/*       */             
/* 18241 */             MysqlxCrud.LimitExpr.getDefaultInstance()) {
/* 18242 */             this
/* 18243 */               .limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
/*       */           } else {
/* 18245 */             this.limitExpr_ = value;
/*       */           } 
/* 18247 */           onChanged();
/*       */         } else {
/* 18249 */           this.limitExprBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/* 18251 */         this.bitField0_ |= 0x80;
/* 18252 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearLimitExpr() {
/* 18263 */         if (this.limitExprBuilder_ == null) {
/* 18264 */           this.limitExpr_ = null;
/* 18265 */           onChanged();
/*       */         } else {
/* 18267 */           this.limitExprBuilder_.clear();
/*       */         } 
/* 18269 */         this.bitField0_ &= 0xFFFFFF7F;
/* 18270 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
/* 18281 */         this.bitField0_ |= 0x80;
/* 18282 */         onChanged();
/* 18283 */         return (MysqlxCrud.LimitExpr.Builder)getLimitExprFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
/* 18294 */         if (this.limitExprBuilder_ != null) {
/* 18295 */           return (MysqlxCrud.LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder();
/*       */         }
/* 18297 */         return (this.limitExpr_ == null) ? 
/* 18298 */           MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
/* 18312 */         if (this.limitExprBuilder_ == null) {
/* 18313 */           this
/*       */ 
/*       */ 
/*       */             
/* 18317 */             .limitExprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimitExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 18318 */           this.limitExpr_ = null;
/*       */         } 
/* 18320 */         return this.limitExprBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 18325 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 18331 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       } }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 18341 */     private static final Update DEFAULT_INSTANCE = new Update();
/*       */ 
/*       */     
/*       */     public static Update getDefaultInstance() {
/* 18345 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 18349 */     public static final Parser<Update> PARSER = (Parser<Update>)new AbstractParser<Update>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Update parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 18355 */           return new MysqlxCrud.Update(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Update> parser() {
/* 18360 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Update> getParserForType() {
/* 18365 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Update getDefaultInstanceForType() {
/* 18370 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface DeleteOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Collection getCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.DataModel getDataModel();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.Expr getCriteria();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLimit();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Limit getLimit();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitOrBuilder getLimitOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxCrud.Order> getOrderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Order getOrder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getOrderCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<MysqlxDatatypes.Scalar> getArgsList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.Scalar getArgs(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getArgsCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasLimitExpr();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitExpr getLimitExpr();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class Delete
/*       */     extends GeneratedMessageV3
/*       */     implements DeleteOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLLECTION_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Collection collection_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DATA_MODEL_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int dataModel_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int CRITERIA_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxExpr.Expr criteria_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LIMIT_FIELD_NUMBER = 4;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Limit limit_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ORDER_FIELD_NUMBER = 5;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxCrud.Order> order_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ARGS_FIELD_NUMBER = 6;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private List<MysqlxDatatypes.Scalar> args_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int LIMIT_EXPR_FIELD_NUMBER = 7;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.LimitExpr limitExpr_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private Delete(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/* 18616 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/* 19059 */       this.memoizedIsInitialized = -1; } private Delete() { this.memoizedIsInitialized = -1; this.dataModel_ = 1; this.order_ = Collections.emptyList(); this.args_ = Collections.emptyList(); } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Delete(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Delete(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxCrud.Collection.Builder builder2; int rawValue; MysqlxExpr.Expr.Builder builder1; MysqlxCrud.Limit.Builder builder; MysqlxCrud.LimitExpr.Builder subBuilder; MysqlxCrud.DataModel value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: builder2 = null; if ((this.bitField0_ & 0x1) != 0) builder2 = this.collection_.toBuilder();  this.collection_ = (MysqlxCrud.Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry); if (builder2 != null) { builder2.mergeFrom(this.collection_); this.collection_ = builder2.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 16: rawValue = input.readEnum(); value = MysqlxCrud.DataModel.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(2, rawValue); continue; }  this.bitField0_ |= 0x2; this.dataModel_ = rawValue; continue;case 26: builder1 = null; if ((this.bitField0_ & 0x4) != 0) builder1 = this.criteria_.toBuilder();  this.criteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry); if (builder1 != null) { builder1.mergeFrom(this.criteria_); this.criteria_ = builder1.buildPartial(); }  this.bitField0_ |= 0x4; continue;case 34: builder = null; if ((this.bitField0_ & 0x8) != 0) builder = this.limit_.toBuilder();  this.limit_ = (MysqlxCrud.Limit)input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.limit_); this.limit_ = builder.buildPartial(); }  this.bitField0_ |= 0x8; continue;case 42: if ((mutable_bitField0_ & 0x10) == 0) { this.order_ = new ArrayList<>(); mutable_bitField0_ |= 0x10; }  this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry)); continue;case 50: if ((mutable_bitField0_ & 0x20) == 0) { this.args_ = new ArrayList<>(); mutable_bitField0_ |= 0x20; }  this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry)); continue;case 58: subBuilder = null; if ((this.bitField0_ & 0x10) != 0) subBuilder = this.limitExpr_.toBuilder();  this.limitExpr_ = (MysqlxCrud.LimitExpr)input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.limitExpr_); this.limitExpr_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x10; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x10) != 0) this.order_ = Collections.unmodifiableList(this.order_);  if ((mutable_bitField0_ & 0x20) != 0) this.args_ = Collections.unmodifiableList(this.args_);  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable.ensureFieldAccessorsInitialized(Delete.class, Builder.class); } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public boolean hasCriteria() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxExpr.Expr getCriteria() { return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() { return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } public boolean hasLimit() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Limit getLimit() { return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() { return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } public List<MysqlxCrud.Order> getOrderList() { return this.order_; } public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() { return (List)this.order_; } public int getOrderCount() { return this.order_.size(); } public MysqlxCrud.Order getOrder(int index) { return this.order_.get(index); } public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) { return this.order_.get(index); } public List<MysqlxDatatypes.Scalar> getArgsList() { return this.args_; } public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() { return (List)this.args_; } public int getArgsCount() { return this.args_.size(); } public MysqlxDatatypes.Scalar getArgs(int index) { return this.args_.get(index); } public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) { return this.args_.get(index); } public boolean hasLimitExpr() { return ((this.bitField0_ & 0x10) != 0); }
/*       */     public MysqlxCrud.LimitExpr getLimitExpr() { return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; }
/*       */     public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() { return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_; }
/* 19062 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 19063 */       if (isInitialized == 1) return true; 
/* 19064 */       if (isInitialized == 0) return false;
/*       */       
/* 19066 */       if (!hasCollection()) {
/* 19067 */         this.memoizedIsInitialized = 0;
/* 19068 */         return false;
/*       */       } 
/* 19070 */       if (!getCollection().isInitialized()) {
/* 19071 */         this.memoizedIsInitialized = 0;
/* 19072 */         return false;
/*       */       } 
/* 19074 */       if (hasCriteria() && 
/* 19075 */         !getCriteria().isInitialized()) {
/* 19076 */         this.memoizedIsInitialized = 0;
/* 19077 */         return false;
/*       */       } 
/*       */       
/* 19080 */       if (hasLimit() && 
/* 19081 */         !getLimit().isInitialized()) {
/* 19082 */         this.memoizedIsInitialized = 0;
/* 19083 */         return false;
/*       */       } 
/*       */       int i;
/* 19086 */       for (i = 0; i < getOrderCount(); i++) {
/* 19087 */         if (!getOrder(i).isInitialized()) {
/* 19088 */           this.memoizedIsInitialized = 0;
/* 19089 */           return false;
/*       */         } 
/*       */       } 
/* 19092 */       for (i = 0; i < getArgsCount(); i++) {
/* 19093 */         if (!getArgs(i).isInitialized()) {
/* 19094 */           this.memoizedIsInitialized = 0;
/* 19095 */           return false;
/*       */         } 
/*       */       } 
/* 19098 */       if (hasLimitExpr() && 
/* 19099 */         !getLimitExpr().isInitialized()) {
/* 19100 */         this.memoizedIsInitialized = 0;
/* 19101 */         return false;
/*       */       } 
/*       */       
/* 19104 */       this.memoizedIsInitialized = 1;
/* 19105 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/* 19111 */       if ((this.bitField0_ & 0x1) != 0) {
/* 19112 */         output.writeMessage(1, (MessageLite)getCollection());
/*       */       }
/* 19114 */       if ((this.bitField0_ & 0x2) != 0) {
/* 19115 */         output.writeEnum(2, this.dataModel_);
/*       */       }
/* 19117 */       if ((this.bitField0_ & 0x4) != 0) {
/* 19118 */         output.writeMessage(3, (MessageLite)getCriteria());
/*       */       }
/* 19120 */       if ((this.bitField0_ & 0x8) != 0)
/* 19121 */         output.writeMessage(4, (MessageLite)getLimit()); 
/*       */       int i;
/* 19123 */       for (i = 0; i < this.order_.size(); i++) {
/* 19124 */         output.writeMessage(5, (MessageLite)this.order_.get(i));
/*       */       }
/* 19126 */       for (i = 0; i < this.args_.size(); i++) {
/* 19127 */         output.writeMessage(6, (MessageLite)this.args_.get(i));
/*       */       }
/* 19129 */       if ((this.bitField0_ & 0x10) != 0) {
/* 19130 */         output.writeMessage(7, (MessageLite)getLimitExpr());
/*       */       }
/* 19132 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/* 19137 */       int size = this.memoizedSize;
/* 19138 */       if (size != -1) return size;
/*       */       
/* 19140 */       size = 0;
/* 19141 */       if ((this.bitField0_ & 0x1) != 0) {
/* 19142 */         size += 
/* 19143 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection());
/*       */       }
/* 19145 */       if ((this.bitField0_ & 0x2) != 0) {
/* 19146 */         size += 
/* 19147 */           CodedOutputStream.computeEnumSize(2, this.dataModel_);
/*       */       }
/* 19149 */       if ((this.bitField0_ & 0x4) != 0) {
/* 19150 */         size += 
/* 19151 */           CodedOutputStream.computeMessageSize(3, (MessageLite)getCriteria());
/*       */       }
/* 19153 */       if ((this.bitField0_ & 0x8) != 0)
/* 19154 */         size += 
/* 19155 */           CodedOutputStream.computeMessageSize(4, (MessageLite)getLimit()); 
/*       */       int i;
/* 19157 */       for (i = 0; i < this.order_.size(); i++) {
/* 19158 */         size += 
/* 19159 */           CodedOutputStream.computeMessageSize(5, (MessageLite)this.order_.get(i));
/*       */       }
/* 19161 */       for (i = 0; i < this.args_.size(); i++) {
/* 19162 */         size += 
/* 19163 */           CodedOutputStream.computeMessageSize(6, (MessageLite)this.args_.get(i));
/*       */       }
/* 19165 */       if ((this.bitField0_ & 0x10) != 0) {
/* 19166 */         size += 
/* 19167 */           CodedOutputStream.computeMessageSize(7, (MessageLite)getLimitExpr());
/*       */       }
/* 19169 */       size += this.unknownFields.getSerializedSize();
/* 19170 */       this.memoizedSize = size;
/* 19171 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/* 19176 */       if (obj == this) {
/* 19177 */         return true;
/*       */       }
/* 19179 */       if (!(obj instanceof Delete)) {
/* 19180 */         return super.equals(obj);
/*       */       }
/* 19182 */       Delete other = (Delete)obj;
/*       */       
/* 19184 */       if (hasCollection() != other.hasCollection()) return false; 
/* 19185 */       if (hasCollection() && 
/*       */         
/* 19187 */         !getCollection().equals(other.getCollection())) return false;
/*       */       
/* 19189 */       if (hasDataModel() != other.hasDataModel()) return false; 
/* 19190 */       if (hasDataModel() && 
/* 19191 */         this.dataModel_ != other.dataModel_) return false;
/*       */       
/* 19193 */       if (hasCriteria() != other.hasCriteria()) return false; 
/* 19194 */       if (hasCriteria() && 
/*       */         
/* 19196 */         !getCriteria().equals(other.getCriteria())) return false;
/*       */       
/* 19198 */       if (hasLimit() != other.hasLimit()) return false; 
/* 19199 */       if (hasLimit() && 
/*       */         
/* 19201 */         !getLimit().equals(other.getLimit())) return false;
/*       */ 
/*       */       
/* 19204 */       if (!getOrderList().equals(other.getOrderList())) return false;
/*       */       
/* 19206 */       if (!getArgsList().equals(other.getArgsList())) return false; 
/* 19207 */       if (hasLimitExpr() != other.hasLimitExpr()) return false; 
/* 19208 */       if (hasLimitExpr() && 
/*       */         
/* 19210 */         !getLimitExpr().equals(other.getLimitExpr())) return false;
/*       */       
/* 19212 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 19213 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/* 19218 */       if (this.memoizedHashCode != 0) {
/* 19219 */         return this.memoizedHashCode;
/*       */       }
/* 19221 */       int hash = 41;
/* 19222 */       hash = 19 * hash + getDescriptor().hashCode();
/* 19223 */       if (hasCollection()) {
/* 19224 */         hash = 37 * hash + 1;
/* 19225 */         hash = 53 * hash + getCollection().hashCode();
/*       */       } 
/* 19227 */       if (hasDataModel()) {
/* 19228 */         hash = 37 * hash + 2;
/* 19229 */         hash = 53 * hash + this.dataModel_;
/*       */       } 
/* 19231 */       if (hasCriteria()) {
/* 19232 */         hash = 37 * hash + 3;
/* 19233 */         hash = 53 * hash + getCriteria().hashCode();
/*       */       } 
/* 19235 */       if (hasLimit()) {
/* 19236 */         hash = 37 * hash + 4;
/* 19237 */         hash = 53 * hash + getLimit().hashCode();
/*       */       } 
/* 19239 */       if (getOrderCount() > 0) {
/* 19240 */         hash = 37 * hash + 5;
/* 19241 */         hash = 53 * hash + getOrderList().hashCode();
/*       */       } 
/* 19243 */       if (getArgsCount() > 0) {
/* 19244 */         hash = 37 * hash + 6;
/* 19245 */         hash = 53 * hash + getArgsList().hashCode();
/*       */       } 
/* 19247 */       if (hasLimitExpr()) {
/* 19248 */         hash = 37 * hash + 7;
/* 19249 */         hash = 53 * hash + getLimitExpr().hashCode();
/*       */       } 
/* 19251 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 19252 */       this.memoizedHashCode = hash;
/* 19253 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 19259 */       return (Delete)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 19265 */       return (Delete)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 19270 */       return (Delete)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 19276 */       return (Delete)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Delete parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 19280 */       return (Delete)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 19286 */       return (Delete)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Delete parseFrom(InputStream input) throws IOException {
/* 19290 */       return 
/* 19291 */         (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 19297 */       return 
/* 19298 */         (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static Delete parseDelimitedFrom(InputStream input) throws IOException {
/* 19302 */       return 
/* 19303 */         (Delete)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Delete parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 19309 */       return 
/* 19310 */         (Delete)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(CodedInputStream input) throws IOException {
/* 19315 */       return 
/* 19316 */         (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static Delete parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 19322 */       return 
/* 19323 */         (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/* 19327 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/* 19329 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(Delete prototype) {
/* 19332 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/* 19336 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 19337 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 19343 */       Builder builder = new Builder(parent);
/* 19344 */       return builder;
/*       */     }
/*       */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.DeleteOrBuilder { private int bitField0_; private MysqlxCrud.Collection collection_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
/*       */       private int dataModel_;
/*       */       private MysqlxExpr.Expr criteria_;
/*       */       private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
/*       */       private MysqlxCrud.Limit limit_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> limitBuilder_;
/*       */       private List<MysqlxCrud.Order> order_;
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> orderBuilder_;
/*       */       private List<MysqlxDatatypes.Scalar> args_;
/*       */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
/*       */       private MysqlxCrud.LimitExpr limitExpr_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> limitExprBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/* 19361 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 19367 */         return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable
/* 19368 */           .ensureFieldAccessorsInitialized(MysqlxCrud.Delete.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/* 19849 */         this.dataModel_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 20228 */         this
/* 20229 */           .order_ = Collections.emptyList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 20540 */         this
/* 20541 */           .args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.dataModel_ = 1; this.order_ = Collections.emptyList(); this.args_ = Collections.emptyList(); maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.Delete.alwaysUseFieldBuilders) { getCollectionFieldBuilder(); getCriteriaFieldBuilder(); getLimitFieldBuilder(); getOrderFieldBuilder(); getArgsFieldBuilder(); getLimitExprFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.collectionBuilder_ == null) { this.collection_ = null; } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.dataModel_ = 1; this.bitField0_ &= 0xFFFFFFFD; if (this.criteriaBuilder_ == null) { this.criteria_ = null; } else { this.criteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; if (this.limitBuilder_ == null) { this.limit_ = null; } else { this.limitBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; if (this.orderBuilder_ == null) { this.order_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFEF; } else { this.orderBuilder_.clear(); }  if (this.argsBuilder_ == null) { this.args_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFDF; } else { this.argsBuilder_.clear(); }  if (this.limitExprBuilder_ == null) { this.limitExpr_ = null; } else { this.limitExprBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFBF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor; } public MysqlxCrud.Delete getDefaultInstanceForType() { return MysqlxCrud.Delete.getDefaultInstance(); } public MysqlxCrud.Delete build() { MysqlxCrud.Delete result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.Delete buildPartial() { MysqlxCrud.Delete result = new MysqlxCrud.Delete(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.collectionBuilder_ == null) { result.collection_ = this.collection_; } else { result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.dataModel_ = this.dataModel_; if ((from_bitField0_ & 0x4) != 0) { if (this.criteriaBuilder_ == null) { result.criteria_ = this.criteria_; } else { result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build(); }  to_bitField0_ |= 0x4; }  if ((from_bitField0_ & 0x8) != 0) { if (this.limitBuilder_ == null) { result.limit_ = this.limit_; } else { result.limit_ = (MysqlxCrud.Limit)this.limitBuilder_.build(); }  to_bitField0_ |= 0x8; }  if (this.orderBuilder_ == null) { if ((this.bitField0_ & 0x10) != 0) { this.order_ = Collections.unmodifiableList(this.order_); this.bitField0_ &= 0xFFFFFFEF; }  result.order_ = this.order_; } else { result.order_ = this.orderBuilder_.build(); }  if (this.argsBuilder_ == null) { if ((this.bitField0_ & 0x20) != 0) { this.args_ = Collections.unmodifiableList(this.args_); this.bitField0_ &= 0xFFFFFFDF; }  result.args_ = this.args_; } else { result.args_ = this.argsBuilder_.build(); }  if ((from_bitField0_ & 0x40) != 0) { if (this.limitExprBuilder_ == null) { result.limitExpr_ = this.limitExpr_; } else { result.limitExpr_ = (MysqlxCrud.LimitExpr)this.limitExprBuilder_.build(); }  to_bitField0_ |= 0x10; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.Delete) return mergeFrom((MysqlxCrud.Delete)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.Delete other) { if (other == MysqlxCrud.Delete.getDefaultInstance()) return this;  if (other.hasCollection()) mergeCollection(other.getCollection());  if (other.hasDataModel()) setDataModel(other.getDataModel());  if (other.hasCriteria()) mergeCriteria(other.getCriteria());  if (other.hasLimit()) mergeLimit(other.getLimit());  if (this.orderBuilder_ == null) { if (!other.order_.isEmpty()) { if (this.order_.isEmpty()) { this.order_ = other.order_; this.bitField0_ &= 0xFFFFFFEF; } else { ensureOrderIsMutable(); this.order_.addAll(other.order_); }  onChanged(); }  } else if (!other.order_.isEmpty()) { if (this.orderBuilder_.isEmpty()) { this.orderBuilder_.dispose(); this.orderBuilder_ = null; this.order_ = other.order_; this.bitField0_ &= 0xFFFFFFEF; this.orderBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? getOrderFieldBuilder() : null; } else { this.orderBuilder_.addAllMessages(other.order_); }  }  if (this.argsBuilder_ == null) { if (!other.args_.isEmpty()) { if (this.args_.isEmpty()) { this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFDF; } else { ensureArgsIsMutable(); this.args_.addAll(other.args_); }  onChanged(); }  } else if (!other.args_.isEmpty()) { if (this.argsBuilder_.isEmpty()) { this.argsBuilder_.dispose(); this.argsBuilder_ = null; this.args_ = other.args_; this.bitField0_ &= 0xFFFFFFDF; this.argsBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? getArgsFieldBuilder() : null; } else { this.argsBuilder_.addAllMessages(other.args_); }  }  if (other.hasLimitExpr()) mergeLimitExpr(other.getLimitExpr());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasCollection()) return false;  if (!getCollection().isInitialized()) return false;  if (hasCriteria() && !getCriteria().isInitialized()) return false;  if (hasLimit() && !getLimit().isInitialized()) return false;  int i; for (i = 0; i < getOrderCount(); i++) { if (!getOrder(i).isInitialized()) return false;  }  for (i = 0; i < getArgsCount(); i++) { if (!getArgs(i).isInitialized()) return false;  }  if (hasLimitExpr() && !getLimitExpr().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.Delete parsedMessage = null; try { parsedMessage = (MysqlxCrud.Delete)MysqlxCrud.Delete.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.Delete)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { if (this.collectionBuilder_ == null) return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;  return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage(); } public Builder setCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if (value == null) throw new NullPointerException();  this.collection_ = value; onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) { if (this.collectionBuilder_ == null) { this.collection_ = builderForValue.build(); onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; } public Builder mergeCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) { this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial(); } else { this.collection_ = value; }  onChanged(); } else { this.collectionBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder clearCollection() { if (this.collectionBuilder_ == null) { this.collection_ = null; onChanged(); } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; } public MysqlxCrud.Collection.Builder getCollectionBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder(); } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { if (this.collectionBuilder_ != null) return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();  return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() { if (this.collectionBuilder_ == null) { this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.collection_ = null; }  return this.collectionBuilder_; } public boolean hasDataModel() { return ((this.bitField0_ & 0x2) != 0); } public MysqlxCrud.DataModel getDataModel() { MysqlxCrud.DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_); return (result == null) ? MysqlxCrud.DataModel.DOCUMENT : result; } public Builder setDataModel(MysqlxCrud.DataModel value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.dataModel_ = value.getNumber(); onChanged(); return this; } public Builder clearDataModel() { this.bitField0_ &= 0xFFFFFFFD; this.dataModel_ = 1; onChanged(); return this; } public boolean hasCriteria() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxExpr.Expr getCriteria() { if (this.criteriaBuilder_ == null) return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;  return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage(); } public Builder setCriteria(MysqlxExpr.Expr value) { if (this.criteriaBuilder_ == null) { if (value == null) throw new NullPointerException();  this.criteria_ = value; onChanged(); } else { this.criteriaBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) { if (this.criteriaBuilder_ == null) { this.criteria_ = builderForValue.build(); onChanged(); } else { this.criteriaBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x4; return this; } public Builder mergeCriteria(MysqlxExpr.Expr value) { if (this.criteriaBuilder_ == null) { if ((this.bitField0_ & 0x4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) { this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial(); } else { this.criteria_ = value; }  onChanged(); } else { this.criteriaBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x4; return this; } public Builder clearCriteria() { if (this.criteriaBuilder_ == null) { this.criteria_ = null; onChanged(); } else { this.criteriaBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFB; return this; } public MysqlxExpr.Expr.Builder getCriteriaBuilder() { this.bitField0_ |= 0x4; onChanged(); return (MysqlxExpr.Expr.Builder)getCriteriaFieldBuilder().getBuilder(); } public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() { if (this.criteriaBuilder_ != null) return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder();  return (this.criteria_ == null) ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_; } private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() { if (this.criteriaBuilder_ == null) { this.criteriaBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCriteria(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.criteria_ = null; }  return this.criteriaBuilder_; } public boolean hasLimit() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.Limit getLimit() { if (this.limitBuilder_ == null) return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;  return (MysqlxCrud.Limit)this.limitBuilder_.getMessage(); } public Builder setLimit(MysqlxCrud.Limit value) { if (this.limitBuilder_ == null) { if (value == null) throw new NullPointerException();  this.limit_ = value; onChanged(); } else { this.limitBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; } public Builder setLimit(MysqlxCrud.Limit.Builder builderForValue) { if (this.limitBuilder_ == null) { this.limit_ = builderForValue.build(); onChanged(); } else { this.limitBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x8; return this; } public Builder mergeLimit(MysqlxCrud.Limit value) { if (this.limitBuilder_ == null) { if ((this.bitField0_ & 0x8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) { this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial(); } else { this.limit_ = value; }  onChanged(); } else { this.limitBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x8; return this; } public Builder clearLimit() { if (this.limitBuilder_ == null) { this.limit_ = null; onChanged(); } else { this.limitBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFF7; return this; } public MysqlxCrud.Limit.Builder getLimitBuilder() { this.bitField0_ |= 0x8; onChanged(); return (MysqlxCrud.Limit.Builder)getLimitFieldBuilder().getBuilder(); } public MysqlxCrud.LimitOrBuilder getLimitOrBuilder() { if (this.limitBuilder_ != null) return (MysqlxCrud.LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder();  return (this.limit_ == null) ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_; } private SingleFieldBuilderV3<MysqlxCrud.Limit, MysqlxCrud.Limit.Builder, MysqlxCrud.LimitOrBuilder> getLimitFieldBuilder() { if (this.limitBuilder_ == null) { this.limitBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimit(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.limit_ = null; }  return this.limitBuilder_; } private void ensureOrderIsMutable() { if ((this.bitField0_ & 0x10) == 0) { this.order_ = new ArrayList<>(this.order_); this.bitField0_ |= 0x10; }  } public List<MysqlxCrud.Order> getOrderList() { if (this.orderBuilder_ == null) return Collections.unmodifiableList(this.order_);  return this.orderBuilder_.getMessageList(); } public int getOrderCount() { if (this.orderBuilder_ == null) return this.order_.size();  return this.orderBuilder_.getCount(); } public MysqlxCrud.Order getOrder(int index) { if (this.orderBuilder_ == null) return this.order_.get(index);  return (MysqlxCrud.Order)this.orderBuilder_.getMessage(index); } public Builder setOrder(int index, MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.set(index, value); onChanged(); } else { this.orderBuilder_.setMessage(index, (AbstractMessage)value); }  return this; } public Builder setOrder(int index, MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.set(index, builderForValue.build()); onChanged(); } else { this.orderBuilder_.setMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addOrder(MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.add(value); onChanged(); } else { this.orderBuilder_.addMessage((AbstractMessage)value); }  return this; } public Builder addOrder(int index, MysqlxCrud.Order value) { if (this.orderBuilder_ == null) { if (value == null) throw new NullPointerException();  ensureOrderIsMutable(); this.order_.add(index, value); onChanged(); } else { this.orderBuilder_.addMessage(index, (AbstractMessage)value); }  return this; } public Builder addOrder(MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.add(builderForValue.build()); onChanged(); } else { this.orderBuilder_.addMessage((AbstractMessage)builderForValue.build()); }  return this; } public Builder addOrder(int index, MysqlxCrud.Order.Builder builderForValue) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.add(index, builderForValue.build()); onChanged(); } else { this.orderBuilder_.addMessage(index, (AbstractMessage)builderForValue.build()); }  return this; } public Builder addAllOrder(Iterable<? extends MysqlxCrud.Order> values) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); AbstractMessageLite.Builder.addAll(values, this.order_); onChanged(); } else { this.orderBuilder_.addAllMessages(values); }  return this; } public Builder clearOrder() { if (this.orderBuilder_ == null) { this.order_ = Collections.emptyList(); this.bitField0_ &= 0xFFFFFFEF; onChanged(); } else { this.orderBuilder_.clear(); }  return this; } public Builder removeOrder(int index) { if (this.orderBuilder_ == null) { ensureOrderIsMutable(); this.order_.remove(index); onChanged(); } else { this.orderBuilder_.remove(index); }  return this; } public MysqlxCrud.Order.Builder getOrderBuilder(int index) { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().getBuilder(index); } public MysqlxCrud.OrderOrBuilder getOrderOrBuilder(int index) { if (this.orderBuilder_ == null) return this.order_.get(index);  return (MysqlxCrud.OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index); } public List<? extends MysqlxCrud.OrderOrBuilder> getOrderOrBuilderList() { if (this.orderBuilder_ != null) return this.orderBuilder_.getMessageOrBuilderList();  return Collections.unmodifiableList((List)this.order_); } public MysqlxCrud.Order.Builder addOrderBuilder() { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder((AbstractMessage)MysqlxCrud.Order.getDefaultInstance()); } public MysqlxCrud.Order.Builder addOrderBuilder(int index) { return (MysqlxCrud.Order.Builder)getOrderFieldBuilder().addBuilder(index, (AbstractMessage)MysqlxCrud.Order.getDefaultInstance()); } public List<MysqlxCrud.Order.Builder> getOrderBuilderList() { return getOrderFieldBuilder().getBuilderList(); }
/*       */       private RepeatedFieldBuilderV3<MysqlxCrud.Order, MysqlxCrud.Order.Builder, MysqlxCrud.OrderOrBuilder> getOrderFieldBuilder() { if (this.orderBuilder_ == null) { this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, ((this.bitField0_ & 0x10) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.order_ = null; }  return this.orderBuilder_; }
/* 20543 */       private void ensureArgsIsMutable() { if ((this.bitField0_ & 0x20) == 0) {
/* 20544 */           this.args_ = new ArrayList<>(this.args_);
/* 20545 */           this.bitField0_ |= 0x20;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxDatatypes.Scalar> getArgsList() {
/* 20560 */         if (this.argsBuilder_ == null) {
/* 20561 */           return Collections.unmodifiableList(this.args_);
/*       */         }
/* 20563 */         return this.argsBuilder_.getMessageList();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getArgsCount() {
/* 20574 */         if (this.argsBuilder_ == null) {
/* 20575 */           return this.args_.size();
/*       */         }
/* 20577 */         return this.argsBuilder_.getCount();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar getArgs(int index) {
/* 20588 */         if (this.argsBuilder_ == null) {
/* 20589 */           return this.args_.get(index);
/*       */         }
/* 20591 */         return (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
/* 20603 */         if (this.argsBuilder_ == null) {
/* 20604 */           if (value == null) {
/* 20605 */             throw new NullPointerException();
/*       */           }
/* 20607 */           ensureArgsIsMutable();
/* 20608 */           this.args_.set(index, value);
/* 20609 */           onChanged();
/*       */         } else {
/* 20611 */           this.argsBuilder_.setMessage(index, (AbstractMessage)value);
/*       */         } 
/* 20613 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 20624 */         if (this.argsBuilder_ == null) {
/* 20625 */           ensureArgsIsMutable();
/* 20626 */           this.args_.set(index, builderForValue.build());
/* 20627 */           onChanged();
/*       */         } else {
/* 20629 */           this.argsBuilder_.setMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 20631 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(MysqlxDatatypes.Scalar value) {
/* 20641 */         if (this.argsBuilder_ == null) {
/* 20642 */           if (value == null) {
/* 20643 */             throw new NullPointerException();
/*       */           }
/* 20645 */           ensureArgsIsMutable();
/* 20646 */           this.args_.add(value);
/* 20647 */           onChanged();
/*       */         } else {
/* 20649 */           this.argsBuilder_.addMessage((AbstractMessage)value);
/*       */         } 
/* 20651 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
/* 20662 */         if (this.argsBuilder_ == null) {
/* 20663 */           if (value == null) {
/* 20664 */             throw new NullPointerException();
/*       */           }
/* 20666 */           ensureArgsIsMutable();
/* 20667 */           this.args_.add(index, value);
/* 20668 */           onChanged();
/*       */         } else {
/* 20670 */           this.argsBuilder_.addMessage(index, (AbstractMessage)value);
/*       */         } 
/* 20672 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 20683 */         if (this.argsBuilder_ == null) {
/* 20684 */           ensureArgsIsMutable();
/* 20685 */           this.args_.add(builderForValue.build());
/* 20686 */           onChanged();
/*       */         } else {
/* 20688 */           this.argsBuilder_.addMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 20690 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
/* 20701 */         if (this.argsBuilder_ == null) {
/* 20702 */           ensureArgsIsMutable();
/* 20703 */           this.args_.add(index, builderForValue.build());
/* 20704 */           onChanged();
/*       */         } else {
/* 20706 */           this.argsBuilder_.addMessage(index, (AbstractMessage)builderForValue.build());
/*       */         } 
/* 20708 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
/* 20719 */         if (this.argsBuilder_ == null) {
/* 20720 */           ensureArgsIsMutable();
/* 20721 */           AbstractMessageLite.Builder.addAll(values, this.args_);
/*       */           
/* 20723 */           onChanged();
/*       */         } else {
/* 20725 */           this.argsBuilder_.addAllMessages(values);
/*       */         } 
/* 20727 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearArgs() {
/* 20737 */         if (this.argsBuilder_ == null) {
/* 20738 */           this.args_ = Collections.emptyList();
/* 20739 */           this.bitField0_ &= 0xFFFFFFDF;
/* 20740 */           onChanged();
/*       */         } else {
/* 20742 */           this.argsBuilder_.clear();
/*       */         } 
/* 20744 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder removeArgs(int index) {
/* 20754 */         if (this.argsBuilder_ == null) {
/* 20755 */           ensureArgsIsMutable();
/* 20756 */           this.args_.remove(index);
/* 20757 */           onChanged();
/*       */         } else {
/* 20759 */           this.argsBuilder_.remove(index);
/*       */         } 
/* 20761 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
/* 20772 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().getBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
/* 20783 */         if (this.argsBuilder_ == null)
/* 20784 */           return this.args_.get(index); 
/* 20785 */         return (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
/* 20797 */         if (this.argsBuilder_ != null) {
/* 20798 */           return this.argsBuilder_.getMessageOrBuilderList();
/*       */         }
/* 20800 */         return Collections.unmodifiableList((List)this.args_);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
/* 20811 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(
/* 20812 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
/* 20823 */         return (MysqlxDatatypes.Scalar.Builder)getArgsFieldBuilder().addBuilder(index, 
/* 20824 */             (AbstractMessage)MysqlxDatatypes.Scalar.getDefaultInstance());
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
/* 20835 */         return getArgsFieldBuilder().getBuilderList();
/*       */       }
/*       */ 
/*       */       
/*       */       private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
/* 20840 */         if (this.argsBuilder_ == null) {
/* 20841 */           this
/*       */ 
/*       */ 
/*       */ 
/*       */             
/* 20846 */             .argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, ((this.bitField0_ & 0x20) != 0), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 20847 */           this.args_ = null;
/*       */         } 
/* 20849 */         return this.argsBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasLimitExpr() {
/* 20865 */         return ((this.bitField0_ & 0x40) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr getLimitExpr() {
/* 20877 */         if (this.limitExprBuilder_ == null) {
/* 20878 */           return (this.limitExpr_ == null) ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
/*       */         }
/* 20880 */         return (MysqlxCrud.LimitExpr)this.limitExprBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLimitExpr(MysqlxCrud.LimitExpr value) {
/* 20892 */         if (this.limitExprBuilder_ == null) {
/* 20893 */           if (value == null) {
/* 20894 */             throw new NullPointerException();
/*       */           }
/* 20896 */           this.limitExpr_ = value;
/* 20897 */           onChanged();
/*       */         } else {
/* 20899 */           this.limitExprBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/* 20901 */         this.bitField0_ |= 0x40;
/* 20902 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setLimitExpr(MysqlxCrud.LimitExpr.Builder builderForValue) {
/* 20914 */         if (this.limitExprBuilder_ == null) {
/* 20915 */           this.limitExpr_ = builderForValue.build();
/* 20916 */           onChanged();
/*       */         } else {
/* 20918 */           this.limitExprBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 20920 */         this.bitField0_ |= 0x40;
/* 20921 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeLimitExpr(MysqlxCrud.LimitExpr value) {
/* 20932 */         if (this.limitExprBuilder_ == null) {
/* 20933 */           if ((this.bitField0_ & 0x40) != 0 && this.limitExpr_ != null && this.limitExpr_ != 
/*       */             
/* 20935 */             MysqlxCrud.LimitExpr.getDefaultInstance()) {
/* 20936 */             this
/* 20937 */               .limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
/*       */           } else {
/* 20939 */             this.limitExpr_ = value;
/*       */           } 
/* 20941 */           onChanged();
/*       */         } else {
/* 20943 */           this.limitExprBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/* 20945 */         this.bitField0_ |= 0x40;
/* 20946 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearLimitExpr() {
/* 20957 */         if (this.limitExprBuilder_ == null) {
/* 20958 */           this.limitExpr_ = null;
/* 20959 */           onChanged();
/*       */         } else {
/* 20961 */           this.limitExprBuilder_.clear();
/*       */         } 
/* 20963 */         this.bitField0_ &= 0xFFFFFFBF;
/* 20964 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExpr.Builder getLimitExprBuilder() {
/* 20975 */         this.bitField0_ |= 0x40;
/* 20976 */         onChanged();
/* 20977 */         return (MysqlxCrud.LimitExpr.Builder)getLimitExprFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.LimitExprOrBuilder getLimitExprOrBuilder() {
/* 20988 */         if (this.limitExprBuilder_ != null) {
/* 20989 */           return (MysqlxCrud.LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder();
/*       */         }
/* 20991 */         return (this.limitExpr_ == null) ? 
/* 20992 */           MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxCrud.LimitExpr, MysqlxCrud.LimitExpr.Builder, MysqlxCrud.LimitExprOrBuilder> getLimitExprFieldBuilder() {
/* 21006 */         if (this.limitExprBuilder_ == null) {
/* 21007 */           this
/*       */ 
/*       */ 
/*       */             
/* 21011 */             .limitExprBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getLimitExpr(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 21012 */           this.limitExpr_ = null;
/*       */         } 
/* 21014 */         return this.limitExprBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 21019 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 21025 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       } }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 21035 */     private static final Delete DEFAULT_INSTANCE = new Delete();
/*       */ 
/*       */     
/*       */     public static Delete getDefaultInstance() {
/* 21039 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 21043 */     public static final Parser<Delete> PARSER = (Parser<Delete>)new AbstractParser<Delete>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.Delete parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 21049 */           return new MysqlxCrud.Delete(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<Delete> parser() {
/* 21054 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<Delete> getParserForType() {
/* 21059 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Delete getDefaultInstanceForType() {
/* 21064 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface CreateViewOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Collection getCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasDefiner();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getDefiner();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getDefinerBytes();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasAlgorithm();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ViewAlgorithm getAlgorithm();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasSecurity();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ViewSqlSecurity getSecurity();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasCheck();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ViewCheckOption getCheck();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<String> getColumnList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getColumnCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getColumn(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getColumnBytes(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasStmt();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Find getStmt();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.FindOrBuilder getStmtOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasReplaceExisting();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean getReplaceExisting();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class CreateView
/*       */     extends GeneratedMessageV3
/*       */     implements CreateViewOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLLECTION_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Collection collection_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DEFINER_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private volatile Object definer_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ALGORITHM_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int algorithm_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int SECURITY_FIELD_NUMBER = 4;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int security_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int CHECK_FIELD_NUMBER = 5;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int check_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLUMN_FIELD_NUMBER = 6;
/*       */ 
/*       */ 
/*       */     
/*       */     private LazyStringList column_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int STMT_FIELD_NUMBER = 7;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Find stmt_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int REPLACE_EXISTING_FIELD_NUMBER = 8;
/*       */ 
/*       */ 
/*       */     
/*       */     private boolean replaceExisting_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private CreateView(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/* 21296 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/* 21747 */       this.memoizedIsInitialized = -1; } private CreateView() { this.memoizedIsInitialized = -1; this.definer_ = ""; this.algorithm_ = 1; this.security_ = 2; this.check_ = 1; this.column_ = LazyStringArrayList.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new CreateView(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private CreateView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxCrud.Collection.Builder builder; ByteString byteString1; int rawValue; ByteString bs; MysqlxCrud.Find.Builder subBuilder; MysqlxCrud.ViewAlgorithm viewAlgorithm; MysqlxCrud.ViewSqlSecurity viewSqlSecurity; MysqlxCrud.ViewCheckOption value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: builder = null; if ((this.bitField0_ & 0x1) != 0) builder = this.collection_.toBuilder();  this.collection_ = (MysqlxCrud.Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.collection_); this.collection_ = builder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 18: byteString1 = input.readBytes(); this.bitField0_ |= 0x2; this.definer_ = byteString1; continue;case 24: rawValue = input.readEnum(); viewAlgorithm = MysqlxCrud.ViewAlgorithm.valueOf(rawValue); if (viewAlgorithm == null) { unknownFields.mergeVarintField(3, rawValue); continue; }  this.bitField0_ |= 0x4; this.algorithm_ = rawValue; continue;case 32: rawValue = input.readEnum(); viewSqlSecurity = MysqlxCrud.ViewSqlSecurity.valueOf(rawValue); if (viewSqlSecurity == null) { unknownFields.mergeVarintField(4, rawValue); continue; }  this.bitField0_ |= 0x8; this.security_ = rawValue; continue;case 40: rawValue = input.readEnum(); value = MysqlxCrud.ViewCheckOption.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(5, rawValue); continue; }  this.bitField0_ |= 0x10; this.check_ = rawValue; continue;case 50: bs = input.readBytes(); if ((mutable_bitField0_ & 0x20) == 0) { this.column_ = (LazyStringList)new LazyStringArrayList(); mutable_bitField0_ |= 0x20; }  this.column_.add(bs); continue;case 58: subBuilder = null; if ((this.bitField0_ & 0x20) != 0) subBuilder = this.stmt_.toBuilder();  this.stmt_ = (MysqlxCrud.Find)input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.stmt_); this.stmt_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x20; continue;case 64: this.bitField0_ |= 0x40; this.replaceExisting_ = input.readBool(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x20) != 0) this.column_ = this.column_.getUnmodifiableView();  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable.ensureFieldAccessorsInitialized(CreateView.class, Builder.class); } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public boolean hasDefiner() { return ((this.bitField0_ & 0x2) != 0); } public String getDefiner() { Object ref = this.definer_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.definer_ = s;  return s; } public ByteString getDefinerBytes() { Object ref = this.definer_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.definer_ = b; return b; }  return (ByteString)ref; } public boolean hasAlgorithm() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxCrud.ViewAlgorithm getAlgorithm() { MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_); return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result; } public boolean hasSecurity() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.ViewSqlSecurity getSecurity() { MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_); return (result == null) ? MysqlxCrud.ViewSqlSecurity.DEFINER : result; } public boolean hasCheck() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxCrud.ViewCheckOption getCheck() { MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_); return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result; } public ProtocolStringList getColumnList() { return (ProtocolStringList)this.column_; } public int getColumnCount() { return this.column_.size(); } public String getColumn(int index) { return (String)this.column_.get(index); } public ByteString getColumnBytes(int index) { return this.column_.getByteString(index); } public boolean hasStmt() { return ((this.bitField0_ & 0x20) != 0); } public MysqlxCrud.Find getStmt() { return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_; } public MysqlxCrud.FindOrBuilder getStmtOrBuilder() { return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_; }
/*       */     public boolean hasReplaceExisting() { return ((this.bitField0_ & 0x40) != 0); }
/*       */     public boolean getReplaceExisting() { return this.replaceExisting_; }
/* 21750 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 21751 */       if (isInitialized == 1) return true; 
/* 21752 */       if (isInitialized == 0) return false;
/*       */       
/* 21754 */       if (!hasCollection()) {
/* 21755 */         this.memoizedIsInitialized = 0;
/* 21756 */         return false;
/*       */       } 
/* 21758 */       if (!hasStmt()) {
/* 21759 */         this.memoizedIsInitialized = 0;
/* 21760 */         return false;
/*       */       } 
/* 21762 */       if (!getCollection().isInitialized()) {
/* 21763 */         this.memoizedIsInitialized = 0;
/* 21764 */         return false;
/*       */       } 
/* 21766 */       if (!getStmt().isInitialized()) {
/* 21767 */         this.memoizedIsInitialized = 0;
/* 21768 */         return false;
/*       */       } 
/* 21770 */       this.memoizedIsInitialized = 1;
/* 21771 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/* 21777 */       if ((this.bitField0_ & 0x1) != 0) {
/* 21778 */         output.writeMessage(1, (MessageLite)getCollection());
/*       */       }
/* 21780 */       if ((this.bitField0_ & 0x2) != 0) {
/* 21781 */         GeneratedMessageV3.writeString(output, 2, this.definer_);
/*       */       }
/* 21783 */       if ((this.bitField0_ & 0x4) != 0) {
/* 21784 */         output.writeEnum(3, this.algorithm_);
/*       */       }
/* 21786 */       if ((this.bitField0_ & 0x8) != 0) {
/* 21787 */         output.writeEnum(4, this.security_);
/*       */       }
/* 21789 */       if ((this.bitField0_ & 0x10) != 0) {
/* 21790 */         output.writeEnum(5, this.check_);
/*       */       }
/* 21792 */       for (int i = 0; i < this.column_.size(); i++) {
/* 21793 */         GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i));
/*       */       }
/* 21795 */       if ((this.bitField0_ & 0x20) != 0) {
/* 21796 */         output.writeMessage(7, (MessageLite)getStmt());
/*       */       }
/* 21798 */       if ((this.bitField0_ & 0x40) != 0) {
/* 21799 */         output.writeBool(8, this.replaceExisting_);
/*       */       }
/* 21801 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/* 21806 */       int size = this.memoizedSize;
/* 21807 */       if (size != -1) return size;
/*       */       
/* 21809 */       size = 0;
/* 21810 */       if ((this.bitField0_ & 0x1) != 0) {
/* 21811 */         size += 
/* 21812 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection());
/*       */       }
/* 21814 */       if ((this.bitField0_ & 0x2) != 0) {
/* 21815 */         size += GeneratedMessageV3.computeStringSize(2, this.definer_);
/*       */       }
/* 21817 */       if ((this.bitField0_ & 0x4) != 0) {
/* 21818 */         size += 
/* 21819 */           CodedOutputStream.computeEnumSize(3, this.algorithm_);
/*       */       }
/* 21821 */       if ((this.bitField0_ & 0x8) != 0) {
/* 21822 */         size += 
/* 21823 */           CodedOutputStream.computeEnumSize(4, this.security_);
/*       */       }
/* 21825 */       if ((this.bitField0_ & 0x10) != 0) {
/* 21826 */         size += 
/* 21827 */           CodedOutputStream.computeEnumSize(5, this.check_);
/*       */       }
/*       */       
/* 21830 */       int dataSize = 0;
/* 21831 */       for (int i = 0; i < this.column_.size(); i++) {
/* 21832 */         dataSize += computeStringSizeNoTag(this.column_.getRaw(i));
/*       */       }
/* 21834 */       size += dataSize;
/* 21835 */       size += 1 * getColumnList().size();
/*       */       
/* 21837 */       if ((this.bitField0_ & 0x20) != 0) {
/* 21838 */         size += 
/* 21839 */           CodedOutputStream.computeMessageSize(7, (MessageLite)getStmt());
/*       */       }
/* 21841 */       if ((this.bitField0_ & 0x40) != 0) {
/* 21842 */         size += 
/* 21843 */           CodedOutputStream.computeBoolSize(8, this.replaceExisting_);
/*       */       }
/* 21845 */       size += this.unknownFields.getSerializedSize();
/* 21846 */       this.memoizedSize = size;
/* 21847 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/* 21852 */       if (obj == this) {
/* 21853 */         return true;
/*       */       }
/* 21855 */       if (!(obj instanceof CreateView)) {
/* 21856 */         return super.equals(obj);
/*       */       }
/* 21858 */       CreateView other = (CreateView)obj;
/*       */       
/* 21860 */       if (hasCollection() != other.hasCollection()) return false; 
/* 21861 */       if (hasCollection() && 
/*       */         
/* 21863 */         !getCollection().equals(other.getCollection())) return false;
/*       */       
/* 21865 */       if (hasDefiner() != other.hasDefiner()) return false; 
/* 21866 */       if (hasDefiner() && 
/*       */         
/* 21868 */         !getDefiner().equals(other.getDefiner())) return false;
/*       */       
/* 21870 */       if (hasAlgorithm() != other.hasAlgorithm()) return false; 
/* 21871 */       if (hasAlgorithm() && 
/* 21872 */         this.algorithm_ != other.algorithm_) return false;
/*       */       
/* 21874 */       if (hasSecurity() != other.hasSecurity()) return false; 
/* 21875 */       if (hasSecurity() && 
/* 21876 */         this.security_ != other.security_) return false;
/*       */       
/* 21878 */       if (hasCheck() != other.hasCheck()) return false; 
/* 21879 */       if (hasCheck() && 
/* 21880 */         this.check_ != other.check_) return false;
/*       */ 
/*       */       
/* 21883 */       if (!getColumnList().equals(other.getColumnList())) return false; 
/* 21884 */       if (hasStmt() != other.hasStmt()) return false; 
/* 21885 */       if (hasStmt() && 
/*       */         
/* 21887 */         !getStmt().equals(other.getStmt())) return false;
/*       */       
/* 21889 */       if (hasReplaceExisting() != other.hasReplaceExisting()) return false; 
/* 21890 */       if (hasReplaceExisting() && 
/* 21891 */         getReplaceExisting() != other
/* 21892 */         .getReplaceExisting()) return false;
/*       */       
/* 21894 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 21895 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/* 21900 */       if (this.memoizedHashCode != 0) {
/* 21901 */         return this.memoizedHashCode;
/*       */       }
/* 21903 */       int hash = 41;
/* 21904 */       hash = 19 * hash + getDescriptor().hashCode();
/* 21905 */       if (hasCollection()) {
/* 21906 */         hash = 37 * hash + 1;
/* 21907 */         hash = 53 * hash + getCollection().hashCode();
/*       */       } 
/* 21909 */       if (hasDefiner()) {
/* 21910 */         hash = 37 * hash + 2;
/* 21911 */         hash = 53 * hash + getDefiner().hashCode();
/*       */       } 
/* 21913 */       if (hasAlgorithm()) {
/* 21914 */         hash = 37 * hash + 3;
/* 21915 */         hash = 53 * hash + this.algorithm_;
/*       */       } 
/* 21917 */       if (hasSecurity()) {
/* 21918 */         hash = 37 * hash + 4;
/* 21919 */         hash = 53 * hash + this.security_;
/*       */       } 
/* 21921 */       if (hasCheck()) {
/* 21922 */         hash = 37 * hash + 5;
/* 21923 */         hash = 53 * hash + this.check_;
/*       */       } 
/* 21925 */       if (getColumnCount() > 0) {
/* 21926 */         hash = 37 * hash + 6;
/* 21927 */         hash = 53 * hash + getColumnList().hashCode();
/*       */       } 
/* 21929 */       if (hasStmt()) {
/* 21930 */         hash = 37 * hash + 7;
/* 21931 */         hash = 53 * hash + getStmt().hashCode();
/*       */       } 
/* 21933 */       if (hasReplaceExisting()) {
/* 21934 */         hash = 37 * hash + 8;
/* 21935 */         hash = 53 * hash + Internal.hashBoolean(
/* 21936 */             getReplaceExisting());
/*       */       } 
/* 21938 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 21939 */       this.memoizedHashCode = hash;
/* 21940 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 21946 */       return (CreateView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 21952 */       return (CreateView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 21957 */       return (CreateView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 21963 */       return (CreateView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static CreateView parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 21967 */       return (CreateView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 21973 */       return (CreateView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static CreateView parseFrom(InputStream input) throws IOException {
/* 21977 */       return 
/* 21978 */         (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 21984 */       return 
/* 21985 */         (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static CreateView parseDelimitedFrom(InputStream input) throws IOException {
/* 21989 */       return 
/* 21990 */         (CreateView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static CreateView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 21996 */       return 
/* 21997 */         (CreateView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(CodedInputStream input) throws IOException {
/* 22002 */       return 
/* 22003 */         (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static CreateView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 22009 */       return 
/* 22010 */         (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/* 22014 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/* 22016 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(CreateView prototype) {
/* 22019 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/* 22023 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 22024 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 22030 */       Builder builder = new Builder(parent);
/* 22031 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.CreateViewOrBuilder { private int bitField0_;
/*       */       private MysqlxCrud.Collection collection_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
/*       */       private Object definer_;
/*       */       private int algorithm_;
/*       */       private int security_;
/*       */       private int check_;
/*       */       private LazyStringList column_;
/*       */       private MysqlxCrud.Find stmt_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> stmtBuilder_;
/*       */       private boolean replaceExisting_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/* 22047 */         return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 22053 */         return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable
/* 22054 */           .ensureFieldAccessorsInitialized(MysqlxCrud.CreateView.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/* 22451 */         this.definer_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 22565 */         this.algorithm_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 22623 */         this.security_ = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 22681 */         this.check_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 22739 */         this.column_ = LazyStringArrayList.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.definer_ = ""; this.algorithm_ = 1; this.security_ = 2; this.check_ = 1; this.column_ = LazyStringArrayList.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.CreateView.alwaysUseFieldBuilders) { getCollectionFieldBuilder(); getStmtFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.collectionBuilder_ == null) { this.collection_ = null; } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.definer_ = ""; this.bitField0_ &= 0xFFFFFFFD; this.algorithm_ = 1; this.bitField0_ &= 0xFFFFFFFB; this.security_ = 2; this.bitField0_ &= 0xFFFFFFF7; this.check_ = 1; this.bitField0_ &= 0xFFFFFFEF; this.column_ = LazyStringArrayList.EMPTY; this.bitField0_ &= 0xFFFFFFDF; if (this.stmtBuilder_ == null) { this.stmt_ = null; } else { this.stmtBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFBF; this.replaceExisting_ = false; this.bitField0_ &= 0xFFFFFF7F; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor; } public MysqlxCrud.CreateView getDefaultInstanceForType() { return MysqlxCrud.CreateView.getDefaultInstance(); } public MysqlxCrud.CreateView build() { MysqlxCrud.CreateView result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.CreateView buildPartial() { MysqlxCrud.CreateView result = new MysqlxCrud.CreateView(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.collectionBuilder_ == null) { result.collection_ = this.collection_; } else { result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.definer_ = this.definer_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.algorithm_ = this.algorithm_; if ((from_bitField0_ & 0x8) != 0) to_bitField0_ |= 0x8;  result.security_ = this.security_; if ((from_bitField0_ & 0x10) != 0) to_bitField0_ |= 0x10;  result.check_ = this.check_; if ((this.bitField0_ & 0x20) != 0) { this.column_ = this.column_.getUnmodifiableView(); this.bitField0_ &= 0xFFFFFFDF; }  result.column_ = this.column_; if ((from_bitField0_ & 0x40) != 0) { if (this.stmtBuilder_ == null) { result.stmt_ = this.stmt_; } else { result.stmt_ = (MysqlxCrud.Find)this.stmtBuilder_.build(); }  to_bitField0_ |= 0x20; }  if ((from_bitField0_ & 0x80) != 0) { result.replaceExisting_ = this.replaceExisting_; to_bitField0_ |= 0x40; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.CreateView) return mergeFrom((MysqlxCrud.CreateView)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.CreateView other) { if (other == MysqlxCrud.CreateView.getDefaultInstance()) return this;  if (other.hasCollection()) mergeCollection(other.getCollection());  if (other.hasDefiner()) { this.bitField0_ |= 0x2; this.definer_ = other.definer_; onChanged(); }  if (other.hasAlgorithm()) setAlgorithm(other.getAlgorithm());  if (other.hasSecurity()) setSecurity(other.getSecurity());  if (other.hasCheck()) setCheck(other.getCheck());  if (!other.column_.isEmpty()) { if (this.column_.isEmpty()) { this.column_ = other.column_; this.bitField0_ &= 0xFFFFFFDF; } else { ensureColumnIsMutable(); this.column_.addAll((java.util.Collection)other.column_); }  onChanged(); }  if (other.hasStmt()) mergeStmt(other.getStmt());  if (other.hasReplaceExisting()) setReplaceExisting(other.getReplaceExisting());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasCollection()) return false;  if (!hasStmt()) return false;  if (!getCollection().isInitialized()) return false;  if (!getStmt().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.CreateView parsedMessage = null; try { parsedMessage = (MysqlxCrud.CreateView)MysqlxCrud.CreateView.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.CreateView)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { if (this.collectionBuilder_ == null) return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;  return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage(); } public Builder setCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if (value == null) throw new NullPointerException();  this.collection_ = value; onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) { if (this.collectionBuilder_ == null) { this.collection_ = builderForValue.build(); onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; } public Builder mergeCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) { this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial(); } else { this.collection_ = value; }  onChanged(); } else { this.collectionBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder clearCollection() { if (this.collectionBuilder_ == null) { this.collection_ = null; onChanged(); } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; } public MysqlxCrud.Collection.Builder getCollectionBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder(); } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { if (this.collectionBuilder_ != null) return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();  return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() { if (this.collectionBuilder_ == null) { this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.collection_ = null; }  return this.collectionBuilder_; } public boolean hasDefiner() { return ((this.bitField0_ & 0x2) != 0); } public String getDefiner() { Object ref = this.definer_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.definer_ = s;  return s; }  return (String)ref; } public ByteString getDefinerBytes() { Object ref = this.definer_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.definer_ = b; return b; }  return (ByteString)ref; } public Builder setDefiner(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.definer_ = value; onChanged(); return this; } public Builder clearDefiner() { this.bitField0_ &= 0xFFFFFFFD; this.definer_ = MysqlxCrud.CreateView.getDefaultInstance().getDefiner(); onChanged(); return this; } public Builder setDefinerBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.definer_ = value; onChanged(); return this; } public boolean hasAlgorithm() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxCrud.ViewAlgorithm getAlgorithm() { MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_); return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result; } public Builder setAlgorithm(MysqlxCrud.ViewAlgorithm value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.algorithm_ = value.getNumber(); onChanged(); return this; } public Builder clearAlgorithm() { this.bitField0_ &= 0xFFFFFFFB; this.algorithm_ = 1; onChanged(); return this; } public boolean hasSecurity() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.ViewSqlSecurity getSecurity() { MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_); return (result == null) ? MysqlxCrud.ViewSqlSecurity.DEFINER : result; } public Builder setSecurity(MysqlxCrud.ViewSqlSecurity value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x8; this.security_ = value.getNumber(); onChanged(); return this; } public Builder clearSecurity() { this.bitField0_ &= 0xFFFFFFF7; this.security_ = 2; onChanged(); return this; } public boolean hasCheck() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxCrud.ViewCheckOption getCheck() { MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_); return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result; } public Builder setCheck(MysqlxCrud.ViewCheckOption value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x10; this.check_ = value.getNumber(); onChanged(); return this; }
/*       */       public Builder clearCheck() { this.bitField0_ &= 0xFFFFFFEF; this.check_ = 1; onChanged(); return this; }
/* 22741 */       private void ensureColumnIsMutable() { if ((this.bitField0_ & 0x20) == 0) {
/* 22742 */           this.column_ = (LazyStringList)new LazyStringArrayList(this.column_);
/* 22743 */           this.bitField0_ |= 0x20;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ProtocolStringList getColumnList() {
/* 22756 */         return (ProtocolStringList)this.column_.getUnmodifiableView();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getColumnCount() {
/* 22767 */         return this.column_.size();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getColumn(int index) {
/* 22779 */         return (String)this.column_.get(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getColumnBytes(int index) {
/* 22792 */         return this.column_.getByteString(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setColumn(int index, String value) {
/* 22806 */         if (value == null) {
/* 22807 */           throw new NullPointerException();
/*       */         }
/* 22809 */         ensureColumnIsMutable();
/* 22810 */         this.column_.set(index, value);
/* 22811 */         onChanged();
/* 22812 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addColumn(String value) {
/* 22825 */         if (value == null) {
/* 22826 */           throw new NullPointerException();
/*       */         }
/* 22828 */         ensureColumnIsMutable();
/* 22829 */         this.column_.add(value);
/* 22830 */         onChanged();
/* 22831 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllColumn(Iterable<String> values) {
/* 22844 */         ensureColumnIsMutable();
/* 22845 */         AbstractMessageLite.Builder.addAll(values, (List)this.column_);
/*       */         
/* 22847 */         onChanged();
/* 22848 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearColumn() {
/* 22859 */         this.column_ = LazyStringArrayList.EMPTY;
/* 22860 */         this.bitField0_ &= 0xFFFFFFDF;
/* 22861 */         onChanged();
/* 22862 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addColumnBytes(ByteString value) {
/* 22875 */         if (value == null) {
/* 22876 */           throw new NullPointerException();
/*       */         }
/* 22878 */         ensureColumnIsMutable();
/* 22879 */         this.column_.add(value);
/* 22880 */         onChanged();
/* 22881 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasStmt() {
/* 22897 */         return ((this.bitField0_ & 0x40) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Find getStmt() {
/* 22909 */         if (this.stmtBuilder_ == null) {
/* 22910 */           return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
/*       */         }
/* 22912 */         return (MysqlxCrud.Find)this.stmtBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setStmt(MysqlxCrud.Find value) {
/* 22924 */         if (this.stmtBuilder_ == null) {
/* 22925 */           if (value == null) {
/* 22926 */             throw new NullPointerException();
/*       */           }
/* 22928 */           this.stmt_ = value;
/* 22929 */           onChanged();
/*       */         } else {
/* 22931 */           this.stmtBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/* 22933 */         this.bitField0_ |= 0x40;
/* 22934 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setStmt(MysqlxCrud.Find.Builder builderForValue) {
/* 22946 */         if (this.stmtBuilder_ == null) {
/* 22947 */           this.stmt_ = builderForValue.build();
/* 22948 */           onChanged();
/*       */         } else {
/* 22950 */           this.stmtBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 22952 */         this.bitField0_ |= 0x40;
/* 22953 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeStmt(MysqlxCrud.Find value) {
/* 22964 */         if (this.stmtBuilder_ == null) {
/* 22965 */           if ((this.bitField0_ & 0x40) != 0 && this.stmt_ != null && this.stmt_ != 
/*       */             
/* 22967 */             MysqlxCrud.Find.getDefaultInstance()) {
/* 22968 */             this
/* 22969 */               .stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
/*       */           } else {
/* 22971 */             this.stmt_ = value;
/*       */           } 
/* 22973 */           onChanged();
/*       */         } else {
/* 22975 */           this.stmtBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/* 22977 */         this.bitField0_ |= 0x40;
/* 22978 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearStmt() {
/* 22989 */         if (this.stmtBuilder_ == null) {
/* 22990 */           this.stmt_ = null;
/* 22991 */           onChanged();
/*       */         } else {
/* 22993 */           this.stmtBuilder_.clear();
/*       */         } 
/* 22995 */         this.bitField0_ &= 0xFFFFFFBF;
/* 22996 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Find.Builder getStmtBuilder() {
/* 23007 */         this.bitField0_ |= 0x40;
/* 23008 */         onChanged();
/* 23009 */         return (MysqlxCrud.Find.Builder)getStmtFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
/* 23020 */         if (this.stmtBuilder_ != null) {
/* 23021 */           return (MysqlxCrud.FindOrBuilder)this.stmtBuilder_.getMessageOrBuilder();
/*       */         }
/* 23023 */         return (this.stmt_ == null) ? 
/* 23024 */           MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getStmtFieldBuilder() {
/* 23038 */         if (this.stmtBuilder_ == null) {
/* 23039 */           this
/*       */ 
/*       */ 
/*       */             
/* 23043 */             .stmtBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmt(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 23044 */           this.stmt_ = null;
/*       */         } 
/* 23046 */         return this.stmtBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasReplaceExisting() {
/* 23060 */         return ((this.bitField0_ & 0x80) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean getReplaceExisting() {
/* 23072 */         return this.replaceExisting_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setReplaceExisting(boolean value) {
/* 23085 */         this.bitField0_ |= 0x80;
/* 23086 */         this.replaceExisting_ = value;
/* 23087 */         onChanged();
/* 23088 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearReplaceExisting() {
/* 23100 */         this.bitField0_ &= 0xFFFFFF7F;
/* 23101 */         this.replaceExisting_ = false;
/* 23102 */         onChanged();
/* 23103 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 23108 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 23114 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       } }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 23124 */     private static final CreateView DEFAULT_INSTANCE = new CreateView();
/*       */ 
/*       */     
/*       */     public static CreateView getDefaultInstance() {
/* 23128 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 23132 */     public static final Parser<CreateView> PARSER = (Parser<CreateView>)new AbstractParser<CreateView>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.CreateView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 23138 */           return new MysqlxCrud.CreateView(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<CreateView> parser() {
/* 23143 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<CreateView> getParserForType() {
/* 23148 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public CreateView getDefaultInstanceForType() {
/* 23153 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface ModifyViewOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Collection getCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasDefiner();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getDefiner();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getDefinerBytes();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasAlgorithm();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ViewAlgorithm getAlgorithm();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasSecurity();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ViewSqlSecurity getSecurity();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasCheck();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.ViewCheckOption getCheck();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     List<String> getColumnList();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     int getColumnCount();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     String getColumn(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     ByteString getColumnBytes(int param1Int);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasStmt();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Find getStmt();
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.FindOrBuilder getStmtOrBuilder();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class ModifyView
/*       */     extends GeneratedMessageV3
/*       */     implements ModifyViewOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLLECTION_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Collection collection_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int DEFINER_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private volatile Object definer_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int ALGORITHM_FIELD_NUMBER = 3;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int algorithm_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int SECURITY_FIELD_NUMBER = 4;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int security_;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int CHECK_FIELD_NUMBER = 5;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private int check_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLUMN_FIELD_NUMBER = 6;
/*       */ 
/*       */ 
/*       */     
/*       */     private LazyStringList column_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int STMT_FIELD_NUMBER = 7;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Find stmt_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     private ModifyView(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/* 23365 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/* 23784 */       this.memoizedIsInitialized = -1; } private ModifyView() { this.memoizedIsInitialized = -1; this.definer_ = ""; this.algorithm_ = 1; this.security_ = 1; this.check_ = 1; this.column_ = LazyStringArrayList.EMPTY; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ModifyView(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ModifyView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxCrud.Collection.Builder builder; ByteString byteString1; int rawValue; ByteString bs; MysqlxCrud.Find.Builder subBuilder; MysqlxCrud.ViewAlgorithm viewAlgorithm; MysqlxCrud.ViewSqlSecurity viewSqlSecurity; MysqlxCrud.ViewCheckOption value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: builder = null; if ((this.bitField0_ & 0x1) != 0) builder = this.collection_.toBuilder();  this.collection_ = (MysqlxCrud.Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry); if (builder != null) { builder.mergeFrom(this.collection_); this.collection_ = builder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 18: byteString1 = input.readBytes(); this.bitField0_ |= 0x2; this.definer_ = byteString1; continue;case 24: rawValue = input.readEnum(); viewAlgorithm = MysqlxCrud.ViewAlgorithm.valueOf(rawValue); if (viewAlgorithm == null) { unknownFields.mergeVarintField(3, rawValue); continue; }  this.bitField0_ |= 0x4; this.algorithm_ = rawValue; continue;case 32: rawValue = input.readEnum(); viewSqlSecurity = MysqlxCrud.ViewSqlSecurity.valueOf(rawValue); if (viewSqlSecurity == null) { unknownFields.mergeVarintField(4, rawValue); continue; }  this.bitField0_ |= 0x8; this.security_ = rawValue; continue;case 40: rawValue = input.readEnum(); value = MysqlxCrud.ViewCheckOption.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(5, rawValue); continue; }  this.bitField0_ |= 0x10; this.check_ = rawValue; continue;case 50: bs = input.readBytes(); if ((mutable_bitField0_ & 0x20) == 0) { this.column_ = (LazyStringList)new LazyStringArrayList(); mutable_bitField0_ |= 0x20; }  this.column_.add(bs); continue;case 58: subBuilder = null; if ((this.bitField0_ & 0x20) != 0) subBuilder = this.stmt_.toBuilder();  this.stmt_ = (MysqlxCrud.Find)input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.stmt_); this.stmt_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x20; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { if ((mutable_bitField0_ & 0x20) != 0) this.column_ = this.column_.getUnmodifiableView();  this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable.ensureFieldAccessorsInitialized(ModifyView.class, Builder.class); } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public boolean hasDefiner() { return ((this.bitField0_ & 0x2) != 0); } public String getDefiner() { Object ref = this.definer_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.definer_ = s;  return s; } public ByteString getDefinerBytes() { Object ref = this.definer_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.definer_ = b; return b; }  return (ByteString)ref; } public boolean hasAlgorithm() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxCrud.ViewAlgorithm getAlgorithm() { MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_); return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result; } public boolean hasSecurity() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.ViewSqlSecurity getSecurity() { MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_); return (result == null) ? MysqlxCrud.ViewSqlSecurity.INVOKER : result; } public boolean hasCheck() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxCrud.ViewCheckOption getCheck() { MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_); return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result; } public ProtocolStringList getColumnList() { return (ProtocolStringList)this.column_; } public int getColumnCount() { return this.column_.size(); } public String getColumn(int index) { return (String)this.column_.get(index); } public ByteString getColumnBytes(int index) { return this.column_.getByteString(index); } public boolean hasStmt() { return ((this.bitField0_ & 0x20) != 0); }
/*       */     public MysqlxCrud.Find getStmt() { return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_; }
/*       */     public MysqlxCrud.FindOrBuilder getStmtOrBuilder() { return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_; }
/* 23787 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 23788 */       if (isInitialized == 1) return true; 
/* 23789 */       if (isInitialized == 0) return false;
/*       */       
/* 23791 */       if (!hasCollection()) {
/* 23792 */         this.memoizedIsInitialized = 0;
/* 23793 */         return false;
/*       */       } 
/* 23795 */       if (!getCollection().isInitialized()) {
/* 23796 */         this.memoizedIsInitialized = 0;
/* 23797 */         return false;
/*       */       } 
/* 23799 */       if (hasStmt() && 
/* 23800 */         !getStmt().isInitialized()) {
/* 23801 */         this.memoizedIsInitialized = 0;
/* 23802 */         return false;
/*       */       } 
/*       */       
/* 23805 */       this.memoizedIsInitialized = 1;
/* 23806 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/* 23812 */       if ((this.bitField0_ & 0x1) != 0) {
/* 23813 */         output.writeMessage(1, (MessageLite)getCollection());
/*       */       }
/* 23815 */       if ((this.bitField0_ & 0x2) != 0) {
/* 23816 */         GeneratedMessageV3.writeString(output, 2, this.definer_);
/*       */       }
/* 23818 */       if ((this.bitField0_ & 0x4) != 0) {
/* 23819 */         output.writeEnum(3, this.algorithm_);
/*       */       }
/* 23821 */       if ((this.bitField0_ & 0x8) != 0) {
/* 23822 */         output.writeEnum(4, this.security_);
/*       */       }
/* 23824 */       if ((this.bitField0_ & 0x10) != 0) {
/* 23825 */         output.writeEnum(5, this.check_);
/*       */       }
/* 23827 */       for (int i = 0; i < this.column_.size(); i++) {
/* 23828 */         GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i));
/*       */       }
/* 23830 */       if ((this.bitField0_ & 0x20) != 0) {
/* 23831 */         output.writeMessage(7, (MessageLite)getStmt());
/*       */       }
/* 23833 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/* 23838 */       int size = this.memoizedSize;
/* 23839 */       if (size != -1) return size;
/*       */       
/* 23841 */       size = 0;
/* 23842 */       if ((this.bitField0_ & 0x1) != 0) {
/* 23843 */         size += 
/* 23844 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection());
/*       */       }
/* 23846 */       if ((this.bitField0_ & 0x2) != 0) {
/* 23847 */         size += GeneratedMessageV3.computeStringSize(2, this.definer_);
/*       */       }
/* 23849 */       if ((this.bitField0_ & 0x4) != 0) {
/* 23850 */         size += 
/* 23851 */           CodedOutputStream.computeEnumSize(3, this.algorithm_);
/*       */       }
/* 23853 */       if ((this.bitField0_ & 0x8) != 0) {
/* 23854 */         size += 
/* 23855 */           CodedOutputStream.computeEnumSize(4, this.security_);
/*       */       }
/* 23857 */       if ((this.bitField0_ & 0x10) != 0) {
/* 23858 */         size += 
/* 23859 */           CodedOutputStream.computeEnumSize(5, this.check_);
/*       */       }
/*       */       
/* 23862 */       int dataSize = 0;
/* 23863 */       for (int i = 0; i < this.column_.size(); i++) {
/* 23864 */         dataSize += computeStringSizeNoTag(this.column_.getRaw(i));
/*       */       }
/* 23866 */       size += dataSize;
/* 23867 */       size += 1 * getColumnList().size();
/*       */       
/* 23869 */       if ((this.bitField0_ & 0x20) != 0) {
/* 23870 */         size += 
/* 23871 */           CodedOutputStream.computeMessageSize(7, (MessageLite)getStmt());
/*       */       }
/* 23873 */       size += this.unknownFields.getSerializedSize();
/* 23874 */       this.memoizedSize = size;
/* 23875 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/* 23880 */       if (obj == this) {
/* 23881 */         return true;
/*       */       }
/* 23883 */       if (!(obj instanceof ModifyView)) {
/* 23884 */         return super.equals(obj);
/*       */       }
/* 23886 */       ModifyView other = (ModifyView)obj;
/*       */       
/* 23888 */       if (hasCollection() != other.hasCollection()) return false; 
/* 23889 */       if (hasCollection() && 
/*       */         
/* 23891 */         !getCollection().equals(other.getCollection())) return false;
/*       */       
/* 23893 */       if (hasDefiner() != other.hasDefiner()) return false; 
/* 23894 */       if (hasDefiner() && 
/*       */         
/* 23896 */         !getDefiner().equals(other.getDefiner())) return false;
/*       */       
/* 23898 */       if (hasAlgorithm() != other.hasAlgorithm()) return false; 
/* 23899 */       if (hasAlgorithm() && 
/* 23900 */         this.algorithm_ != other.algorithm_) return false;
/*       */       
/* 23902 */       if (hasSecurity() != other.hasSecurity()) return false; 
/* 23903 */       if (hasSecurity() && 
/* 23904 */         this.security_ != other.security_) return false;
/*       */       
/* 23906 */       if (hasCheck() != other.hasCheck()) return false; 
/* 23907 */       if (hasCheck() && 
/* 23908 */         this.check_ != other.check_) return false;
/*       */ 
/*       */       
/* 23911 */       if (!getColumnList().equals(other.getColumnList())) return false; 
/* 23912 */       if (hasStmt() != other.hasStmt()) return false; 
/* 23913 */       if (hasStmt() && 
/*       */         
/* 23915 */         !getStmt().equals(other.getStmt())) return false;
/*       */       
/* 23917 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 23918 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/* 23923 */       if (this.memoizedHashCode != 0) {
/* 23924 */         return this.memoizedHashCode;
/*       */       }
/* 23926 */       int hash = 41;
/* 23927 */       hash = 19 * hash + getDescriptor().hashCode();
/* 23928 */       if (hasCollection()) {
/* 23929 */         hash = 37 * hash + 1;
/* 23930 */         hash = 53 * hash + getCollection().hashCode();
/*       */       } 
/* 23932 */       if (hasDefiner()) {
/* 23933 */         hash = 37 * hash + 2;
/* 23934 */         hash = 53 * hash + getDefiner().hashCode();
/*       */       } 
/* 23936 */       if (hasAlgorithm()) {
/* 23937 */         hash = 37 * hash + 3;
/* 23938 */         hash = 53 * hash + this.algorithm_;
/*       */       } 
/* 23940 */       if (hasSecurity()) {
/* 23941 */         hash = 37 * hash + 4;
/* 23942 */         hash = 53 * hash + this.security_;
/*       */       } 
/* 23944 */       if (hasCheck()) {
/* 23945 */         hash = 37 * hash + 5;
/* 23946 */         hash = 53 * hash + this.check_;
/*       */       } 
/* 23948 */       if (getColumnCount() > 0) {
/* 23949 */         hash = 37 * hash + 6;
/* 23950 */         hash = 53 * hash + getColumnList().hashCode();
/*       */       } 
/* 23952 */       if (hasStmt()) {
/* 23953 */         hash = 37 * hash + 7;
/* 23954 */         hash = 53 * hash + getStmt().hashCode();
/*       */       } 
/* 23956 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 23957 */       this.memoizedHashCode = hash;
/* 23958 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 23964 */       return (ModifyView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 23970 */       return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 23975 */       return (ModifyView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 23981 */       return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static ModifyView parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 23985 */       return (ModifyView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 23991 */       return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static ModifyView parseFrom(InputStream input) throws IOException {
/* 23995 */       return 
/* 23996 */         (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 24002 */       return 
/* 24003 */         (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static ModifyView parseDelimitedFrom(InputStream input) throws IOException {
/* 24007 */       return 
/* 24008 */         (ModifyView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ModifyView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 24014 */       return 
/* 24015 */         (ModifyView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(CodedInputStream input) throws IOException {
/* 24020 */       return 
/* 24021 */         (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static ModifyView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 24027 */       return 
/* 24028 */         (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/* 24032 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/* 24034 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(ModifyView prototype) {
/* 24037 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/* 24041 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 24042 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 24048 */       Builder builder = new Builder(parent);
/* 24049 */       return builder;
/*       */     }
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder> implements MysqlxCrud.ModifyViewOrBuilder {
/*       */       private int bitField0_;
/*       */       private MysqlxCrud.Collection collection_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
/*       */       private Object definer_;
/*       */       private int algorithm_;
/*       */       private int security_;
/*       */       private int check_;
/*       */       private LazyStringList column_;
/*       */       private MysqlxCrud.Find stmt_;
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> stmtBuilder_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/* 24066 */         return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 24072 */         return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable
/* 24073 */           .ensureFieldAccessorsInitialized(MysqlxCrud.ModifyView.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder()
/*       */       {
/* 24460 */         this.definer_ = "";
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 24574 */         this.algorithm_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 24632 */         this.security_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 24690 */         this.check_ = 1;
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */         
/* 24748 */         this.column_ = LazyStringArrayList.EMPTY; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.definer_ = ""; this.algorithm_ = 1; this.security_ = 1; this.check_ = 1; this.column_ = LazyStringArrayList.EMPTY; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (MysqlxCrud.ModifyView.alwaysUseFieldBuilders) { getCollectionFieldBuilder(); getStmtFieldBuilder(); }  } public Builder clear() { super.clear(); if (this.collectionBuilder_ == null) { this.collection_ = null; } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; this.definer_ = ""; this.bitField0_ &= 0xFFFFFFFD; this.algorithm_ = 1; this.bitField0_ &= 0xFFFFFFFB; this.security_ = 1; this.bitField0_ &= 0xFFFFFFF7; this.check_ = 1; this.bitField0_ &= 0xFFFFFFEF; this.column_ = LazyStringArrayList.EMPTY; this.bitField0_ &= 0xFFFFFFDF; if (this.stmtBuilder_ == null) { this.stmt_ = null; } else { this.stmtBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFBF; return this; } public Descriptors.Descriptor getDescriptorForType() { return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor; } public MysqlxCrud.ModifyView getDefaultInstanceForType() { return MysqlxCrud.ModifyView.getDefaultInstance(); } public MysqlxCrud.ModifyView build() { MysqlxCrud.ModifyView result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public MysqlxCrud.ModifyView buildPartial() { MysqlxCrud.ModifyView result = new MysqlxCrud.ModifyView(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) { if (this.collectionBuilder_ == null) { result.collection_ = this.collection_; } else { result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build(); }  to_bitField0_ |= 0x1; }  if ((from_bitField0_ & 0x2) != 0) to_bitField0_ |= 0x2;  result.definer_ = this.definer_; if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.algorithm_ = this.algorithm_; if ((from_bitField0_ & 0x8) != 0) to_bitField0_ |= 0x8;  result.security_ = this.security_; if ((from_bitField0_ & 0x10) != 0) to_bitField0_ |= 0x10;  result.check_ = this.check_; if ((this.bitField0_ & 0x20) != 0) { this.column_ = this.column_.getUnmodifiableView(); this.bitField0_ &= 0xFFFFFFDF; }  result.column_ = this.column_; if ((from_bitField0_ & 0x40) != 0) { if (this.stmtBuilder_ == null) { result.stmt_ = this.stmt_; } else { result.stmt_ = (MysqlxCrud.Find)this.stmtBuilder_.build(); }  to_bitField0_ |= 0x20; }  result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); } public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); } public Builder mergeFrom(Message other) { if (other instanceof MysqlxCrud.ModifyView) return mergeFrom((MysqlxCrud.ModifyView)other);  super.mergeFrom(other); return this; } public Builder mergeFrom(MysqlxCrud.ModifyView other) { if (other == MysqlxCrud.ModifyView.getDefaultInstance()) return this;  if (other.hasCollection()) mergeCollection(other.getCollection());  if (other.hasDefiner()) { this.bitField0_ |= 0x2; this.definer_ = other.definer_; onChanged(); }  if (other.hasAlgorithm()) setAlgorithm(other.getAlgorithm());  if (other.hasSecurity()) setSecurity(other.getSecurity());  if (other.hasCheck()) setCheck(other.getCheck());  if (!other.column_.isEmpty()) { if (this.column_.isEmpty()) { this.column_ = other.column_; this.bitField0_ &= 0xFFFFFFDF; } else { ensureColumnIsMutable(); this.column_.addAll((java.util.Collection)other.column_); }  onChanged(); }  if (other.hasStmt()) mergeStmt(other.getStmt());  mergeUnknownFields(other.unknownFields); onChanged(); return this; } public final boolean isInitialized() { if (!hasCollection()) return false;  if (!getCollection().isInitialized()) return false;  if (hasStmt() && !getStmt().isInitialized()) return false;  return true; } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { MysqlxCrud.ModifyView parsedMessage = null; try { parsedMessage = (MysqlxCrud.ModifyView)MysqlxCrud.ModifyView.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (MysqlxCrud.ModifyView)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { if (this.collectionBuilder_ == null) return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;  return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage(); } public Builder setCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if (value == null) throw new NullPointerException();  this.collection_ = value; onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) { if (this.collectionBuilder_ == null) { this.collection_ = builderForValue.build(); onChanged(); } else { this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build()); }  this.bitField0_ |= 0x1; return this; } public Builder mergeCollection(MysqlxCrud.Collection value) { if (this.collectionBuilder_ == null) { if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) { this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial(); } else { this.collection_ = value; }  onChanged(); } else { this.collectionBuilder_.mergeFrom((AbstractMessage)value); }  this.bitField0_ |= 0x1; return this; } public Builder clearCollection() { if (this.collectionBuilder_ == null) { this.collection_ = null; onChanged(); } else { this.collectionBuilder_.clear(); }  this.bitField0_ &= 0xFFFFFFFE; return this; } public MysqlxCrud.Collection.Builder getCollectionBuilder() { this.bitField0_ |= 0x1; onChanged(); return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder(); } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { if (this.collectionBuilder_ != null) return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();  return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() { if (this.collectionBuilder_ == null) { this.collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean()); this.collection_ = null; }  return this.collectionBuilder_; } public boolean hasDefiner() { return ((this.bitField0_ & 0x2) != 0); } public String getDefiner() { Object ref = this.definer_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.definer_ = s;  return s; }  return (String)ref; } public ByteString getDefinerBytes() { Object ref = this.definer_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.definer_ = b; return b; }  return (ByteString)ref; } public Builder setDefiner(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.definer_ = value; onChanged(); return this; } public Builder clearDefiner() { this.bitField0_ &= 0xFFFFFFFD; this.definer_ = MysqlxCrud.ModifyView.getDefaultInstance().getDefiner(); onChanged(); return this; } public Builder setDefinerBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x2; this.definer_ = value; onChanged(); return this; } public boolean hasAlgorithm() { return ((this.bitField0_ & 0x4) != 0); } public MysqlxCrud.ViewAlgorithm getAlgorithm() { MysqlxCrud.ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_); return (result == null) ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result; } public Builder setAlgorithm(MysqlxCrud.ViewAlgorithm value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.algorithm_ = value.getNumber(); onChanged(); return this; } public Builder clearAlgorithm() { this.bitField0_ &= 0xFFFFFFFB; this.algorithm_ = 1; onChanged(); return this; } public boolean hasSecurity() { return ((this.bitField0_ & 0x8) != 0); } public MysqlxCrud.ViewSqlSecurity getSecurity() { MysqlxCrud.ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_); return (result == null) ? MysqlxCrud.ViewSqlSecurity.INVOKER : result; } public Builder setSecurity(MysqlxCrud.ViewSqlSecurity value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x8; this.security_ = value.getNumber(); onChanged(); return this; } public Builder clearSecurity() { this.bitField0_ &= 0xFFFFFFF7; this.security_ = 1; onChanged(); return this; } public boolean hasCheck() { return ((this.bitField0_ & 0x10) != 0); } public MysqlxCrud.ViewCheckOption getCheck() { MysqlxCrud.ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_); return (result == null) ? MysqlxCrud.ViewCheckOption.LOCAL : result; } public Builder setCheck(MysqlxCrud.ViewCheckOption value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x10; this.check_ = value.getNumber(); onChanged(); return this; }
/*       */       public Builder clearCheck() { this.bitField0_ &= 0xFFFFFFEF; this.check_ = 1; onChanged(); return this; }
/* 24750 */       private void ensureColumnIsMutable() { if ((this.bitField0_ & 0x20) == 0) {
/* 24751 */           this.column_ = (LazyStringList)new LazyStringArrayList(this.column_);
/* 24752 */           this.bitField0_ |= 0x20;
/*       */         }  }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ProtocolStringList getColumnList() {
/* 24765 */         return (ProtocolStringList)this.column_.getUnmodifiableView();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public int getColumnCount() {
/* 24776 */         return this.column_.size();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public String getColumn(int index) {
/* 24788 */         return (String)this.column_.get(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public ByteString getColumnBytes(int index) {
/* 24801 */         return this.column_.getByteString(index);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setColumn(int index, String value) {
/* 24815 */         if (value == null) {
/* 24816 */           throw new NullPointerException();
/*       */         }
/* 24818 */         ensureColumnIsMutable();
/* 24819 */         this.column_.set(index, value);
/* 24820 */         onChanged();
/* 24821 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addColumn(String value) {
/* 24834 */         if (value == null) {
/* 24835 */           throw new NullPointerException();
/*       */         }
/* 24837 */         ensureColumnIsMutable();
/* 24838 */         this.column_.add(value);
/* 24839 */         onChanged();
/* 24840 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addAllColumn(Iterable<String> values) {
/* 24853 */         ensureColumnIsMutable();
/* 24854 */         AbstractMessageLite.Builder.addAll(values, (List)this.column_);
/*       */         
/* 24856 */         onChanged();
/* 24857 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearColumn() {
/* 24868 */         this.column_ = LazyStringArrayList.EMPTY;
/* 24869 */         this.bitField0_ &= 0xFFFFFFDF;
/* 24870 */         onChanged();
/* 24871 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addColumnBytes(ByteString value) {
/* 24884 */         if (value == null) {
/* 24885 */           throw new NullPointerException();
/*       */         }
/* 24887 */         ensureColumnIsMutable();
/* 24888 */         this.column_.add(value);
/* 24889 */         onChanged();
/* 24890 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasStmt() {
/* 24906 */         return ((this.bitField0_ & 0x40) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Find getStmt() {
/* 24918 */         if (this.stmtBuilder_ == null) {
/* 24919 */           return (this.stmt_ == null) ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
/*       */         }
/* 24921 */         return (MysqlxCrud.Find)this.stmtBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setStmt(MysqlxCrud.Find value) {
/* 24933 */         if (this.stmtBuilder_ == null) {
/* 24934 */           if (value == null) {
/* 24935 */             throw new NullPointerException();
/*       */           }
/* 24937 */           this.stmt_ = value;
/* 24938 */           onChanged();
/*       */         } else {
/* 24940 */           this.stmtBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/* 24942 */         this.bitField0_ |= 0x40;
/* 24943 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setStmt(MysqlxCrud.Find.Builder builderForValue) {
/* 24955 */         if (this.stmtBuilder_ == null) {
/* 24956 */           this.stmt_ = builderForValue.build();
/* 24957 */           onChanged();
/*       */         } else {
/* 24959 */           this.stmtBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 24961 */         this.bitField0_ |= 0x40;
/* 24962 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeStmt(MysqlxCrud.Find value) {
/* 24973 */         if (this.stmtBuilder_ == null) {
/* 24974 */           if ((this.bitField0_ & 0x40) != 0 && this.stmt_ != null && this.stmt_ != 
/*       */             
/* 24976 */             MysqlxCrud.Find.getDefaultInstance()) {
/* 24977 */             this
/* 24978 */               .stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
/*       */           } else {
/* 24980 */             this.stmt_ = value;
/*       */           } 
/* 24982 */           onChanged();
/*       */         } else {
/* 24984 */           this.stmtBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/* 24986 */         this.bitField0_ |= 0x40;
/* 24987 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearStmt() {
/* 24998 */         if (this.stmtBuilder_ == null) {
/* 24999 */           this.stmt_ = null;
/* 25000 */           onChanged();
/*       */         } else {
/* 25002 */           this.stmtBuilder_.clear();
/*       */         } 
/* 25004 */         this.bitField0_ &= 0xFFFFFFBF;
/* 25005 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Find.Builder getStmtBuilder() {
/* 25016 */         this.bitField0_ |= 0x40;
/* 25017 */         onChanged();
/* 25018 */         return (MysqlxCrud.Find.Builder)getStmtFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.FindOrBuilder getStmtOrBuilder() {
/* 25029 */         if (this.stmtBuilder_ != null) {
/* 25030 */           return (MysqlxCrud.FindOrBuilder)this.stmtBuilder_.getMessageOrBuilder();
/*       */         }
/* 25032 */         return (this.stmt_ == null) ? 
/* 25033 */           MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getStmtFieldBuilder() {
/* 25047 */         if (this.stmtBuilder_ == null) {
/* 25048 */           this
/*       */ 
/*       */ 
/*       */             
/* 25052 */             .stmtBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getStmt(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 25053 */           this.stmt_ = null;
/*       */         } 
/* 25055 */         return this.stmtBuilder_;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 25060 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 25066 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 25076 */     private static final ModifyView DEFAULT_INSTANCE = new ModifyView();
/*       */ 
/*       */     
/*       */     public static ModifyView getDefaultInstance() {
/* 25080 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 25084 */     public static final Parser<ModifyView> PARSER = (Parser<ModifyView>)new AbstractParser<ModifyView>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.ModifyView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 25090 */           return new MysqlxCrud.ModifyView(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<ModifyView> parser() {
/* 25095 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<ModifyView> getParserForType() {
/* 25100 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public ModifyView getDefaultInstanceForType() {
/* 25105 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static interface DropViewOrBuilder
/*       */     extends MessageOrBuilder
/*       */   {
/*       */     boolean hasCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.Collection getCollection();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean hasIfExists();
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     boolean getIfExists();
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static final class DropView
/*       */     extends GeneratedMessageV3
/*       */     implements DropViewOrBuilder
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */ 
/*       */     
/*       */     private int bitField0_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int COLLECTION_FIELD_NUMBER = 1;
/*       */ 
/*       */ 
/*       */     
/*       */     private MysqlxCrud.Collection collection_;
/*       */ 
/*       */ 
/*       */     
/*       */     public static final int IF_EXISTS_FIELD_NUMBER = 2;
/*       */ 
/*       */ 
/*       */     
/*       */     private boolean ifExists_;
/*       */ 
/*       */ 
/*       */     
/*       */     private byte memoizedIsInitialized;
/*       */ 
/*       */ 
/*       */     
/*       */     private DropView(GeneratedMessageV3.Builder<?> builder)
/*       */     {
/* 25175 */       super(builder);
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/* 25322 */       this.memoizedIsInitialized = -1; } private DropView() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new DropView(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private DropView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { MysqlxCrud.Collection.Builder subBuilder; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: subBuilder = null; if ((this.bitField0_ & 0x1) != 0) subBuilder = this.collection_.toBuilder();  this.collection_ = (MysqlxCrud.Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry); if (subBuilder != null) { subBuilder.mergeFrom(this.collection_); this.collection_ = subBuilder.buildPartial(); }  this.bitField0_ |= 0x1; continue;case 16: this.bitField0_ |= 0x2; this.ifExists_ = input.readBool(); continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable.ensureFieldAccessorsInitialized(DropView.class, Builder.class); } public boolean hasCollection() { return ((this.bitField0_ & 0x1) != 0); } public MysqlxCrud.Collection getCollection() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; } public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() { return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_; }
/*       */     public boolean hasIfExists() { return ((this.bitField0_ & 0x2) != 0); }
/*       */     public boolean getIfExists() { return this.ifExists_; }
/* 25325 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 25326 */       if (isInitialized == 1) return true; 
/* 25327 */       if (isInitialized == 0) return false;
/*       */       
/* 25329 */       if (!hasCollection()) {
/* 25330 */         this.memoizedIsInitialized = 0;
/* 25331 */         return false;
/*       */       } 
/* 25333 */       if (!getCollection().isInitialized()) {
/* 25334 */         this.memoizedIsInitialized = 0;
/* 25335 */         return false;
/*       */       } 
/* 25337 */       this.memoizedIsInitialized = 1;
/* 25338 */       return true; }
/*       */ 
/*       */ 
/*       */ 
/*       */     
/*       */     public void writeTo(CodedOutputStream output) throws IOException {
/* 25344 */       if ((this.bitField0_ & 0x1) != 0) {
/* 25345 */         output.writeMessage(1, (MessageLite)getCollection());
/*       */       }
/* 25347 */       if ((this.bitField0_ & 0x2) != 0) {
/* 25348 */         output.writeBool(2, this.ifExists_);
/*       */       }
/* 25350 */       this.unknownFields.writeTo(output);
/*       */     }
/*       */ 
/*       */     
/*       */     public int getSerializedSize() {
/* 25355 */       int size = this.memoizedSize;
/* 25356 */       if (size != -1) return size;
/*       */       
/* 25358 */       size = 0;
/* 25359 */       if ((this.bitField0_ & 0x1) != 0) {
/* 25360 */         size += 
/* 25361 */           CodedOutputStream.computeMessageSize(1, (MessageLite)getCollection());
/*       */       }
/* 25363 */       if ((this.bitField0_ & 0x2) != 0) {
/* 25364 */         size += 
/* 25365 */           CodedOutputStream.computeBoolSize(2, this.ifExists_);
/*       */       }
/* 25367 */       size += this.unknownFields.getSerializedSize();
/* 25368 */       this.memoizedSize = size;
/* 25369 */       return size;
/*       */     }
/*       */ 
/*       */     
/*       */     public boolean equals(Object obj) {
/* 25374 */       if (obj == this) {
/* 25375 */         return true;
/*       */       }
/* 25377 */       if (!(obj instanceof DropView)) {
/* 25378 */         return super.equals(obj);
/*       */       }
/* 25380 */       DropView other = (DropView)obj;
/*       */       
/* 25382 */       if (hasCollection() != other.hasCollection()) return false; 
/* 25383 */       if (hasCollection() && 
/*       */         
/* 25385 */         !getCollection().equals(other.getCollection())) return false;
/*       */       
/* 25387 */       if (hasIfExists() != other.hasIfExists()) return false; 
/* 25388 */       if (hasIfExists() && 
/* 25389 */         getIfExists() != other
/* 25390 */         .getIfExists()) return false;
/*       */       
/* 25392 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 25393 */       return true;
/*       */     }
/*       */ 
/*       */     
/*       */     public int hashCode() {
/* 25398 */       if (this.memoizedHashCode != 0) {
/* 25399 */         return this.memoizedHashCode;
/*       */       }
/* 25401 */       int hash = 41;
/* 25402 */       hash = 19 * hash + getDescriptor().hashCode();
/* 25403 */       if (hasCollection()) {
/* 25404 */         hash = 37 * hash + 1;
/* 25405 */         hash = 53 * hash + getCollection().hashCode();
/*       */       } 
/* 25407 */       if (hasIfExists()) {
/* 25408 */         hash = 37 * hash + 2;
/* 25409 */         hash = 53 * hash + Internal.hashBoolean(
/* 25410 */             getIfExists());
/*       */       } 
/* 25412 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 25413 */       this.memoizedHashCode = hash;
/* 25414 */       return hash;
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 25420 */       return (DropView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 25426 */       return (DropView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 25431 */       return (DropView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 25437 */       return (DropView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static DropView parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 25441 */       return (DropView)PARSER.parseFrom(data);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 25447 */       return (DropView)PARSER.parseFrom(data, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static DropView parseFrom(InputStream input) throws IOException {
/* 25451 */       return 
/* 25452 */         (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 25458 */       return 
/* 25459 */         (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public static DropView parseDelimitedFrom(InputStream input) throws IOException {
/* 25463 */       return 
/* 25464 */         (DropView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DropView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 25470 */       return 
/* 25471 */         (DropView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(CodedInputStream input) throws IOException {
/* 25476 */       return 
/* 25477 */         (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     public static DropView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 25483 */       return 
/* 25484 */         (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*       */     }
/*       */     
/*       */     public Builder newBuilderForType() {
/* 25488 */       return newBuilder();
/*       */     } public static Builder newBuilder() {
/* 25490 */       return DEFAULT_INSTANCE.toBuilder();
/*       */     }
/*       */     public static Builder newBuilder(DropView prototype) {
/* 25493 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*       */     }
/*       */     
/*       */     public Builder toBuilder() {
/* 25497 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 25498 */         .mergeFrom(this);
/*       */     }
/*       */ 
/*       */ 
/*       */     
/*       */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 25504 */       Builder builder = new Builder(parent);
/* 25505 */       return builder;
/*       */     }
/*       */ 
/*       */     
/*       */     public static final class Builder
/*       */       extends GeneratedMessageV3.Builder<Builder>
/*       */       implements MysqlxCrud.DropViewOrBuilder
/*       */     {
/*       */       private int bitField0_;
/*       */       
/*       */       private MysqlxCrud.Collection collection_;
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> collectionBuilder_;
/*       */       private boolean ifExists_;
/*       */       
/*       */       public static final Descriptors.Descriptor getDescriptor() {
/* 25521 */         return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 25527 */         return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable
/* 25528 */           .ensureFieldAccessorsInitialized(MysqlxCrud.DropView.class, Builder.class);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       private Builder() {
/* 25534 */         maybeForceBuilderInitialization();
/*       */       }
/*       */ 
/*       */       
/*       */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 25539 */         super(parent);
/* 25540 */         maybeForceBuilderInitialization();
/*       */       }
/*       */       
/*       */       private void maybeForceBuilderInitialization() {
/* 25544 */         if (MysqlxCrud.DropView.alwaysUseFieldBuilders) {
/* 25545 */           getCollectionFieldBuilder();
/*       */         }
/*       */       }
/*       */       
/*       */       public Builder clear() {
/* 25550 */         super.clear();
/* 25551 */         if (this.collectionBuilder_ == null) {
/* 25552 */           this.collection_ = null;
/*       */         } else {
/* 25554 */           this.collectionBuilder_.clear();
/*       */         } 
/* 25556 */         this.bitField0_ &= 0xFFFFFFFE;
/* 25557 */         this.ifExists_ = false;
/* 25558 */         this.bitField0_ &= 0xFFFFFFFD;
/* 25559 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Descriptors.Descriptor getDescriptorForType() {
/* 25565 */         return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.DropView getDefaultInstanceForType() {
/* 25570 */         return MysqlxCrud.DropView.getDefaultInstance();
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.DropView build() {
/* 25575 */         MysqlxCrud.DropView result = buildPartial();
/* 25576 */         if (!result.isInitialized()) {
/* 25577 */           throw newUninitializedMessageException(result);
/*       */         }
/* 25579 */         return result;
/*       */       }
/*       */ 
/*       */       
/*       */       public MysqlxCrud.DropView buildPartial() {
/* 25584 */         MysqlxCrud.DropView result = new MysqlxCrud.DropView(this);
/* 25585 */         int from_bitField0_ = this.bitField0_;
/* 25586 */         int to_bitField0_ = 0;
/* 25587 */         if ((from_bitField0_ & 0x1) != 0) {
/* 25588 */           if (this.collectionBuilder_ == null) {
/* 25589 */             result.collection_ = this.collection_;
/*       */           } else {
/* 25591 */             result.collection_ = (MysqlxCrud.Collection)this.collectionBuilder_.build();
/*       */           } 
/* 25593 */           to_bitField0_ |= 0x1;
/*       */         } 
/* 25595 */         if ((from_bitField0_ & 0x2) != 0) {
/* 25596 */           result.ifExists_ = this.ifExists_;
/* 25597 */           to_bitField0_ |= 0x2;
/*       */         } 
/* 25599 */         result.bitField0_ = to_bitField0_;
/* 25600 */         onBuilt();
/* 25601 */         return result;
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clone() {
/* 25606 */         return (Builder)super.clone();
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 25612 */         return (Builder)super.setField(field, value);
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 25617 */         return (Builder)super.clearField(field);
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 25622 */         return (Builder)super.clearOneof(oneof);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 25628 */         return (Builder)super.setRepeatedField(field, index, value);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 25634 */         return (Builder)super.addRepeatedField(field, value);
/*       */       }
/*       */       
/*       */       public Builder mergeFrom(Message other) {
/* 25638 */         if (other instanceof MysqlxCrud.DropView) {
/* 25639 */           return mergeFrom((MysqlxCrud.DropView)other);
/*       */         }
/* 25641 */         super.mergeFrom(other);
/* 25642 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public Builder mergeFrom(MysqlxCrud.DropView other) {
/* 25647 */         if (other == MysqlxCrud.DropView.getDefaultInstance()) return this; 
/* 25648 */         if (other.hasCollection()) {
/* 25649 */           mergeCollection(other.getCollection());
/*       */         }
/* 25651 */         if (other.hasIfExists()) {
/* 25652 */           setIfExists(other.getIfExists());
/*       */         }
/* 25654 */         mergeUnknownFields(other.unknownFields);
/* 25655 */         onChanged();
/* 25656 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final boolean isInitialized() {
/* 25661 */         if (!hasCollection()) {
/* 25662 */           return false;
/*       */         }
/* 25664 */         if (!getCollection().isInitialized()) {
/* 25665 */           return false;
/*       */         }
/* 25667 */         return true;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 25675 */         MysqlxCrud.DropView parsedMessage = null;
/*       */         try {
/* 25677 */           parsedMessage = (MysqlxCrud.DropView)MysqlxCrud.DropView.PARSER.parsePartialFrom(input, extensionRegistry);
/* 25678 */         } catch (InvalidProtocolBufferException e) {
/* 25679 */           parsedMessage = (MysqlxCrud.DropView)e.getUnfinishedMessage();
/* 25680 */           throw e.unwrapIOException();
/*       */         } finally {
/* 25682 */           if (parsedMessage != null) {
/* 25683 */             mergeFrom(parsedMessage);
/*       */           }
/*       */         } 
/* 25686 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasCollection() {
/* 25702 */         return ((this.bitField0_ & 0x1) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Collection getCollection() {
/* 25713 */         if (this.collectionBuilder_ == null) {
/* 25714 */           return (this.collection_ == null) ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
/*       */         }
/* 25716 */         return (MysqlxCrud.Collection)this.collectionBuilder_.getMessage();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setCollection(MysqlxCrud.Collection value) {
/* 25727 */         if (this.collectionBuilder_ == null) {
/* 25728 */           if (value == null) {
/* 25729 */             throw new NullPointerException();
/*       */           }
/* 25731 */           this.collection_ = value;
/* 25732 */           onChanged();
/*       */         } else {
/* 25734 */           this.collectionBuilder_.setMessage((AbstractMessage)value);
/*       */         } 
/* 25736 */         this.bitField0_ |= 0x1;
/* 25737 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setCollection(MysqlxCrud.Collection.Builder builderForValue) {
/* 25748 */         if (this.collectionBuilder_ == null) {
/* 25749 */           this.collection_ = builderForValue.build();
/* 25750 */           onChanged();
/*       */         } else {
/* 25752 */           this.collectionBuilder_.setMessage((AbstractMessage)builderForValue.build());
/*       */         } 
/* 25754 */         this.bitField0_ |= 0x1;
/* 25755 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder mergeCollection(MysqlxCrud.Collection value) {
/* 25765 */         if (this.collectionBuilder_ == null) {
/* 25766 */           if ((this.bitField0_ & 0x1) != 0 && this.collection_ != null && this.collection_ != 
/*       */             
/* 25768 */             MysqlxCrud.Collection.getDefaultInstance()) {
/* 25769 */             this
/* 25770 */               .collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
/*       */           } else {
/* 25772 */             this.collection_ = value;
/*       */           } 
/* 25774 */           onChanged();
/*       */         } else {
/* 25776 */           this.collectionBuilder_.mergeFrom((AbstractMessage)value);
/*       */         } 
/* 25778 */         this.bitField0_ |= 0x1;
/* 25779 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearCollection() {
/* 25789 */         if (this.collectionBuilder_ == null) {
/* 25790 */           this.collection_ = null;
/* 25791 */           onChanged();
/*       */         } else {
/* 25793 */           this.collectionBuilder_.clear();
/*       */         } 
/* 25795 */         this.bitField0_ &= 0xFFFFFFFE;
/* 25796 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.Collection.Builder getCollectionBuilder() {
/* 25806 */         this.bitField0_ |= 0x1;
/* 25807 */         onChanged();
/* 25808 */         return (MysqlxCrud.Collection.Builder)getCollectionFieldBuilder().getBuilder();
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public MysqlxCrud.CollectionOrBuilder getCollectionOrBuilder() {
/* 25818 */         if (this.collectionBuilder_ != null) {
/* 25819 */           return (MysqlxCrud.CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
/*       */         }
/* 25821 */         return (this.collection_ == null) ? 
/* 25822 */           MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       private SingleFieldBuilderV3<MysqlxCrud.Collection, MysqlxCrud.Collection.Builder, MysqlxCrud.CollectionOrBuilder> getCollectionFieldBuilder() {
/* 25835 */         if (this.collectionBuilder_ == null) {
/* 25836 */           this
/*       */ 
/*       */ 
/*       */             
/* 25840 */             .collectionBuilder_ = new SingleFieldBuilderV3((AbstractMessage)getCollection(), (AbstractMessage.BuilderParent)getParentForChildren(), isClean());
/* 25841 */           this.collection_ = null;
/*       */         } 
/* 25843 */         return this.collectionBuilder_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean hasIfExists() {
/* 25856 */         return ((this.bitField0_ & 0x2) != 0);
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public boolean getIfExists() {
/* 25867 */         return this.ifExists_;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder setIfExists(boolean value) {
/* 25879 */         this.bitField0_ |= 0x2;
/* 25880 */         this.ifExists_ = value;
/* 25881 */         onChanged();
/* 25882 */         return this;
/*       */       }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */       
/*       */       public Builder clearIfExists() {
/* 25893 */         this.bitField0_ &= 0xFFFFFFFD;
/* 25894 */         this.ifExists_ = false;
/* 25895 */         onChanged();
/* 25896 */         return this;
/*       */       }
/*       */ 
/*       */       
/*       */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 25901 */         return (Builder)super.setUnknownFields(unknownFields);
/*       */       }
/*       */ 
/*       */ 
/*       */       
/*       */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 25907 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*       */       }
/*       */     }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 25917 */     private static final DropView DEFAULT_INSTANCE = new DropView();
/*       */ 
/*       */     
/*       */     public static DropView getDefaultInstance() {
/* 25921 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */     
/*       */     @Deprecated
/* 25925 */     public static final Parser<DropView> PARSER = (Parser<DropView>)new AbstractParser<DropView>()
/*       */       {
/*       */ 
/*       */         
/*       */         public MysqlxCrud.DropView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*       */         {
/* 25931 */           return new MysqlxCrud.DropView(input, extensionRegistry);
/*       */         }
/*       */       };
/*       */     
/*       */     public static Parser<DropView> parser() {
/* 25936 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public Parser<DropView> getParserForType() {
/* 25941 */       return PARSER;
/*       */     }
/*       */ 
/*       */     
/*       */     public DropView getDefaultInstanceForType() {
/* 25946 */       return DEFAULT_INSTANCE;
/*       */     }
/*       */   }
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */   
/*       */   public static Descriptors.FileDescriptor getDescriptor() {
/* 26029 */     return descriptor;
/*       */   }
/*       */ 
/*       */   
/*       */   static {
/* 26034 */     String[] descriptorData = { "\n\021mysqlx_crud.proto\022\013Mysqlx.Crud\032\fmysqlx.proto\032\021mysqlx_expr.proto\032\026mysqlx_datatypes.proto\"[\n\006Column\022\f\n\004name\030\001 \001(\t\022\r\n\005alias\030\002 \001(\t\0224\n\rdocument_path\030\003 \003(\0132\035.Mysqlx.Expr.DocumentPathItem\">\n\nProjection\022!\n\006source\030\001 \002(\0132\021.Mysqlx.Expr.Expr\022\r\n\005alias\030\002 \001(\t\"*\n\nCollection\022\f\n\004name\030\001 \002(\t\022\016\n\006schema\030\002 \001(\t\"*\n\005Limit\022\021\n\trow_count\030\001 \002(\004\022\016\n\006offset\030\002 \001(\004\"T\n\tLimitExpr\022$\n\trow_count\030\001 \002(\0132\021.Mysqlx.Expr.Expr\022!\n\006offset\030\002 \001(\0132\021.Mysqlx.Expr.Expr\"~\n\005Order\022\037\n\004expr\030\001 \002(\0132\021.Mysqlx.Expr.Expr\0224\n\tdirection\030\002 \001(\0162\034.Mysqlx.Crud.Order.Direction:\003ASC\"\036\n\tDirection\022\007\n\003ASC\020\001\022\b\n\004DESC\020\002\"¬\002\n\017UpdateOperation\022-\n\006source\030\001 \002(\0132\035.Mysqlx.Expr.ColumnIdentifier\022:\n\toperation\030\002 \002(\0162'.Mysqlx.Crud.UpdateOperation.UpdateType\022 \n\005value\030\003 \001(\0132\021.Mysqlx.Expr.Expr\"\001\n\nUpdateType\022\007\n\003SET\020\001\022\017\n\013ITEM_REMOVE\020\002\022\f\n\bITEM_SET\020\003\022\020\n\fITEM_REPLACE\020\004\022\016\n\nITEM_MERGE\020\005\022\020\n\fARRAY_INSERT\020\006\022\020\n\fARRAY_APPEND\020\007\022\017\n\013MERGE_PATCH\020\b\"ê\004\n\004Find\022+\n\ncollection\030\002 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\003 \001(\0162\026.Mysqlx.Crud.DataModel\022+\n\nprojection\030\004 \003(\0132\027.Mysqlx.Crud.Projection\022&\n\004args\030\013 \003(\0132\030.Mysqlx.Datatypes.Scalar\022#\n\bcriteria\030\005 \001(\0132\021.Mysqlx.Expr.Expr\022!\n\005limit\030\006 \001(\0132\022.Mysqlx.Crud.Limit\022!\n\005order\030\007 \003(\0132\022.Mysqlx.Crud.Order\022#\n\bgrouping\030\b \003(\0132\021.Mysqlx.Expr.Expr\022,\n\021grouping_criteria\030\t \001(\0132\021.Mysqlx.Expr.Expr\022*\n\007locking\030\f \001(\0162\031.Mysqlx.Crud.Find.RowLock\0229\n\017locking_options\030\r \001(\0162 .Mysqlx.Crud.Find.RowLockOptions\022*\n\nlimit_expr\030\016 \001(\0132\026.Mysqlx.Crud.LimitExpr\".\n\007RowLock\022\017\n\013SHARED_LOCK\020\001\022\022\n\016EXCLUSIVE_LOCK\020\002\"-\n\016RowLockOptions\022\n\n\006NOWAIT\020\001\022\017\n\013SKIP_LOCKED\020\002:\004ê0\021\"¨\002\n\006Insert\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\002 \001(\0162\026.Mysqlx.Crud.DataModel\022'\n\nprojection\030\003 \003(\0132\023.Mysqlx.Crud.Column\022)\n\003row\030\004 \003(\0132\034.Mysqlx.Crud.Insert.TypedRow\022&\n\004args\030\005 \003(\0132\030.Mysqlx.Datatypes.Scalar\022\025\n\006upsert\030\006 \001(\b:\005false\032,\n\bTypedRow\022 \n\005field\030\001 \003(\0132\021.Mysqlx.Expr.Expr:\004ê0\022\"×\002\n\006Update\022+\n\ncollection\030\002 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\003 \001(\0162\026.Mysqlx.Crud.DataModel\022#\n\bcriteria\030\004 \001(\0132\021.Mysqlx.Expr.Expr\022!\n\005limit\030\005 \001(\0132\022.Mysqlx.Crud.Limit\022!\n\005order\030\006 \003(\0132\022.Mysqlx.Crud.Order\022/\n\toperation\030\007 \003(\0132\034.Mysqlx.Crud.UpdateOperation\022&\n\004args\030\b \003(\0132\030.Mysqlx.Datatypes.Scalar\022*\n\nlimit_expr\030\t \001(\0132\026.Mysqlx.Crud.LimitExpr:\004ê0\023\"¦\002\n\006Delete\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022*\n\ndata_model\030\002 \001(\0162\026.Mysqlx.Crud.DataModel\022#\n\bcriteria\030\003 \001(\0132\021.Mysqlx.Expr.Expr\022!\n\005limit\030\004 \001(\0132\022.Mysqlx.Crud.Limit\022!\n\005order\030\005 \003(\0132\022.Mysqlx.Crud.Order\022&\n\004args\030\006 \003(\0132\030.Mysqlx.Datatypes.Scalar\022*\n\nlimit_expr\030\007 \001(\0132\026.Mysqlx.Crud.LimitExpr:\004ê0\024\"Â\002\n\nCreateView\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022\017\n\007definer\030\002 \001(\t\0228\n\talgorithm\030\003 \001(\0162\032.Mysqlx.Crud.ViewAlgorithm:\tUNDEFINED\0227\n\bsecurity\030\004 \001(\0162\034.Mysqlx.Crud.ViewSqlSecurity:\007DEFINER\022+\n\005check\030\005 \001(\0162\034.Mysqlx.Crud.ViewCheckOption\022\016\n\006column\030\006 \003(\t\022\037\n\004stmt\030\007 \002(\0132\021.Mysqlx.Crud.Find\022\037\n\020replace_existing\030\b \001(\b:\005false:\004ê0\036\"\002\n\nModifyView\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022\017\n\007definer\030\002 \001(\t\022-\n\talgorithm\030\003 \001(\0162\032.Mysqlx.Crud.ViewAlgorithm\022.\n\bsecurity\030\004 \001(\0162\034.Mysqlx.Crud.ViewSqlSecurity\022+\n\005check\030\005 \001(\0162\034.Mysqlx.Crud.ViewCheckOption\022\016\n\006column\030\006 \003(\t\022\037\n\004stmt\030\007 \001(\0132\021.Mysqlx.Crud.Find:\004ê0\037\"W\n\bDropView\022+\n\ncollection\030\001 \002(\0132\027.Mysqlx.Crud.Collection\022\030\n\tif_exists\030\002 \001(\b:\005false:\004ê0 *$\n\tDataModel\022\f\n\bDOCUMENT\020\001\022\t\n\005TABLE\020\002*8\n\rViewAlgorithm\022\r\n\tUNDEFINED\020\001\022\t\n\005MERGE\020\002\022\r\n\tTEMPTABLE\020\003*+\n\017ViewSqlSecurity\022\013\n\007INVOKER\020\001\022\013\n\007DEFINER\020\002**\n\017ViewCheckOption\022\t\n\005LOCAL\020\001\022\f\n\bCASCADED\020\002B\031\n\027com.mysql.cj.x.protobuf" };
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26120 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*       */           
/* 26122 */           Mysqlx.getDescriptor(), 
/* 26123 */           MysqlxExpr.getDescriptor(), 
/* 26124 */           MysqlxDatatypes.getDescriptor()
/*       */         });
/*       */     
/* 26127 */     internal_static_Mysqlx_Crud_Column_descriptor = getDescriptor().getMessageTypes().get(0);
/* 26128 */     internal_static_Mysqlx_Crud_Column_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Column_descriptor, new String[] { "Name", "Alias", "DocumentPath" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26133 */     internal_static_Mysqlx_Crud_Projection_descriptor = getDescriptor().getMessageTypes().get(1);
/* 26134 */     internal_static_Mysqlx_Crud_Projection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Projection_descriptor, new String[] { "Source", "Alias" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26139 */     internal_static_Mysqlx_Crud_Collection_descriptor = getDescriptor().getMessageTypes().get(2);
/* 26140 */     internal_static_Mysqlx_Crud_Collection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Collection_descriptor, new String[] { "Name", "Schema" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26145 */     internal_static_Mysqlx_Crud_Limit_descriptor = getDescriptor().getMessageTypes().get(3);
/* 26146 */     internal_static_Mysqlx_Crud_Limit_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Limit_descriptor, new String[] { "RowCount", "Offset" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26151 */     internal_static_Mysqlx_Crud_LimitExpr_descriptor = getDescriptor().getMessageTypes().get(4);
/* 26152 */     internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_LimitExpr_descriptor, new String[] { "RowCount", "Offset" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26157 */     internal_static_Mysqlx_Crud_Order_descriptor = getDescriptor().getMessageTypes().get(5);
/* 26158 */     internal_static_Mysqlx_Crud_Order_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Order_descriptor, new String[] { "Expr", "Direction" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26163 */     internal_static_Mysqlx_Crud_UpdateOperation_descriptor = getDescriptor().getMessageTypes().get(6);
/* 26164 */     internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_UpdateOperation_descriptor, new String[] { "Source", "Operation", "Value" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26169 */     internal_static_Mysqlx_Crud_Find_descriptor = getDescriptor().getMessageTypes().get(7);
/* 26170 */     internal_static_Mysqlx_Crud_Find_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Find_descriptor, new String[] { "Collection", "DataModel", "Projection", "Args", "Criteria", "Limit", "Order", "Grouping", "GroupingCriteria", "Locking", "LockingOptions", "LimitExpr" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26175 */     internal_static_Mysqlx_Crud_Insert_descriptor = getDescriptor().getMessageTypes().get(8);
/* 26176 */     internal_static_Mysqlx_Crud_Insert_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Insert_descriptor, new String[] { "Collection", "DataModel", "Projection", "Row", "Args", "Upsert" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26181 */     internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor = internal_static_Mysqlx_Crud_Insert_descriptor.getNestedTypes().get(0);
/* 26182 */     internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor, new String[] { "Field" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26187 */     internal_static_Mysqlx_Crud_Update_descriptor = getDescriptor().getMessageTypes().get(9);
/* 26188 */     internal_static_Mysqlx_Crud_Update_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Update_descriptor, new String[] { "Collection", "DataModel", "Criteria", "Limit", "Order", "Operation", "Args", "LimitExpr" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26193 */     internal_static_Mysqlx_Crud_Delete_descriptor = getDescriptor().getMessageTypes().get(10);
/* 26194 */     internal_static_Mysqlx_Crud_Delete_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Delete_descriptor, new String[] { "Collection", "DataModel", "Criteria", "Limit", "Order", "Args", "LimitExpr" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26199 */     internal_static_Mysqlx_Crud_CreateView_descriptor = getDescriptor().getMessageTypes().get(11);
/* 26200 */     internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_CreateView_descriptor, new String[] { "Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt", "ReplaceExisting" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26205 */     internal_static_Mysqlx_Crud_ModifyView_descriptor = getDescriptor().getMessageTypes().get(12);
/* 26206 */     internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_ModifyView_descriptor, new String[] { "Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26211 */     internal_static_Mysqlx_Crud_DropView_descriptor = getDescriptor().getMessageTypes().get(13);
/* 26212 */     internal_static_Mysqlx_Crud_DropView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_DropView_descriptor, new String[] { "Collection", "IfExists" });
/*       */ 
/*       */ 
/*       */ 
/*       */     
/* 26217 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 26218 */     registry.add(Mysqlx.clientMessageId);
/*       */     
/* 26220 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 26221 */     Mysqlx.getDescriptor();
/* 26222 */     MysqlxExpr.getDescriptor();
/* 26223 */     MysqlxDatatypes.getDescriptor();
/*       */   }
/*       */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\MysqlxCrud.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */