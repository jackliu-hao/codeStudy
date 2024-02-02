package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxCrud {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Column_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Column_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Projection_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Projection_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Collection_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Collection_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Limit_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Limit_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_LimitExpr_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Order_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Order_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Find_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Find_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Update_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Update_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_Delete_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_Delete_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_CreateView_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_ModifyView_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Crud_DropView_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Crud_DropView_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxCrud() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] descriptorData = new String[]{"\n\u0011mysqlx_crud.proto\u0012\u000bMysqlx.Crud\u001a\fmysqlx.proto\u001a\u0011mysqlx_expr.proto\u001a\u0016mysqlx_datatypes.proto\"[\n\u0006Column\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\r\n\u0005alias\u0018\u0002 \u0001(\t\u00124\n\rdocument_path\u0018\u0003 \u0003(\u000b2\u001d.Mysqlx.Expr.DocumentPathItem\">\n\nProjection\u0012!\n\u0006source\u0018\u0001 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\u0012\r\n\u0005alias\u0018\u0002 \u0001(\t\"*\n\nCollection\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012\u000e\n\u0006schema\u0018\u0002 \u0001(\t\"*\n\u0005Limit\u0012\u0011\n\trow_count\u0018\u0001 \u0002(\u0004\u0012\u000e\n\u0006offset\u0018\u0002 \u0001(\u0004\"T\n\tLimitExpr\u0012$\n\trow_count\u0018\u0001 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0006offset\u0018\u0002 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\"~\n\u0005Order\u0012\u001f\n\u0004expr\u0018\u0001 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\u00124\n\tdirection\u0018\u0002 \u0001(\u000e2\u001c.Mysqlx.Crud.Order.Direction:\u0003ASC\"\u001e\n\tDirection\u0012\u0007\n\u0003ASC\u0010\u0001\u0012\b\n\u0004DESC\u0010\u0002\"¬\u0002\n\u000fUpdateOperation\u0012-\n\u0006source\u0018\u0001 \u0002(\u000b2\u001d.Mysqlx.Expr.ColumnIdentifier\u0012:\n\toperation\u0018\u0002 \u0002(\u000e2'.Mysqlx.Crud.UpdateOperation.UpdateType\u0012 \n\u0005value\u0018\u0003 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\"\u008b\u0001\n\nUpdateType\u0012\u0007\n\u0003SET\u0010\u0001\u0012\u000f\n\u000bITEM_REMOVE\u0010\u0002\u0012\f\n\bITEM_SET\u0010\u0003\u0012\u0010\n\fITEM_REPLACE\u0010\u0004\u0012\u000e\n\nITEM_MERGE\u0010\u0005\u0012\u0010\n\fARRAY_INSERT\u0010\u0006\u0012\u0010\n\fARRAY_APPEND\u0010\u0007\u0012\u000f\n\u000bMERGE_PATCH\u0010\b\"ê\u0004\n\u0004Find\u0012+\n\ncollection\u0018\u0002 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0003 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012+\n\nprojection\u0018\u0004 \u0003(\u000b2\u0017.Mysqlx.Crud.Projection\u0012&\n\u0004args\u0018\u000b \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012#\n\bcriteria\u0018\u0005 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0005limit\u0018\u0006 \u0001(\u000b2\u0012.Mysqlx.Crud.Limit\u0012!\n\u0005order\u0018\u0007 \u0003(\u000b2\u0012.Mysqlx.Crud.Order\u0012#\n\bgrouping\u0018\b \u0003(\u000b2\u0011.Mysqlx.Expr.Expr\u0012,\n\u0011grouping_criteria\u0018\t \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012*\n\u0007locking\u0018\f \u0001(\u000e2\u0019.Mysqlx.Crud.Find.RowLock\u00129\n\u000flocking_options\u0018\r \u0001(\u000e2 .Mysqlx.Crud.Find.RowLockOptions\u0012*\n\nlimit_expr\u0018\u000e \u0001(\u000b2\u0016.Mysqlx.Crud.LimitExpr\".\n\u0007RowLock\u0012\u000f\n\u000bSHARED_LOCK\u0010\u0001\u0012\u0012\n\u000eEXCLUSIVE_LOCK\u0010\u0002\"-\n\u000eRowLockOptions\u0012\n\n\u0006NOWAIT\u0010\u0001\u0012\u000f\n\u000bSKIP_LOCKED\u0010\u0002:\u0004\u0088ê0\u0011\"¨\u0002\n\u0006Insert\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0002 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012'\n\nprojection\u0018\u0003 \u0003(\u000b2\u0013.Mysqlx.Crud.Column\u0012)\n\u0003row\u0018\u0004 \u0003(\u000b2\u001c.Mysqlx.Crud.Insert.TypedRow\u0012&\n\u0004args\u0018\u0005 \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012\u0015\n\u0006upsert\u0018\u0006 \u0001(\b:\u0005false\u001a,\n\bTypedRow\u0012 \n\u0005field\u0018\u0001 \u0003(\u000b2\u0011.Mysqlx.Expr.Expr:\u0004\u0088ê0\u0012\"×\u0002\n\u0006Update\u0012+\n\ncollection\u0018\u0002 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0003 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012#\n\bcriteria\u0018\u0004 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0005limit\u0018\u0005 \u0001(\u000b2\u0012.Mysqlx.Crud.Limit\u0012!\n\u0005order\u0018\u0006 \u0003(\u000b2\u0012.Mysqlx.Crud.Order\u0012/\n\toperation\u0018\u0007 \u0003(\u000b2\u001c.Mysqlx.Crud.UpdateOperation\u0012&\n\u0004args\u0018\b \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012*\n\nlimit_expr\u0018\t \u0001(\u000b2\u0016.Mysqlx.Crud.LimitExpr:\u0004\u0088ê0\u0013\"¦\u0002\n\u0006Delete\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012*\n\ndata_model\u0018\u0002 \u0001(\u000e2\u0016.Mysqlx.Crud.DataModel\u0012#\n\bcriteria\u0018\u0003 \u0001(\u000b2\u0011.Mysqlx.Expr.Expr\u0012!\n\u0005limit\u0018\u0004 \u0001(\u000b2\u0012.Mysqlx.Crud.Limit\u0012!\n\u0005order\u0018\u0005 \u0003(\u000b2\u0012.Mysqlx.Crud.Order\u0012&\n\u0004args\u0018\u0006 \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u0012*\n\nlimit_expr\u0018\u0007 \u0001(\u000b2\u0016.Mysqlx.Crud.LimitExpr:\u0004\u0088ê0\u0014\"Â\u0002\n\nCreateView\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012\u000f\n\u0007definer\u0018\u0002 \u0001(\t\u00128\n\talgorithm\u0018\u0003 \u0001(\u000e2\u001a.Mysqlx.Crud.ViewAlgorithm:\tUNDEFINED\u00127\n\bsecurity\u0018\u0004 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewSqlSecurity:\u0007DEFINER\u0012+\n\u0005check\u0018\u0005 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewCheckOption\u0012\u000e\n\u0006column\u0018\u0006 \u0003(\t\u0012\u001f\n\u0004stmt\u0018\u0007 \u0002(\u000b2\u0011.Mysqlx.Crud.Find\u0012\u001f\n\u0010replace_existing\u0018\b \u0001(\b:\u0005false:\u0004\u0088ê0\u001e\"\u008d\u0002\n\nModifyView\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012\u000f\n\u0007definer\u0018\u0002 \u0001(\t\u0012-\n\talgorithm\u0018\u0003 \u0001(\u000e2\u001a.Mysqlx.Crud.ViewAlgorithm\u0012.\n\bsecurity\u0018\u0004 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewSqlSecurity\u0012+\n\u0005check\u0018\u0005 \u0001(\u000e2\u001c.Mysqlx.Crud.ViewCheckOption\u0012\u000e\n\u0006column\u0018\u0006 \u0003(\t\u0012\u001f\n\u0004stmt\u0018\u0007 \u0001(\u000b2\u0011.Mysqlx.Crud.Find:\u0004\u0088ê0\u001f\"W\n\bDropView\u0012+\n\ncollection\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Crud.Collection\u0012\u0018\n\tif_exists\u0018\u0002 \u0001(\b:\u0005false:\u0004\u0088ê0 *$\n\tDataModel\u0012\f\n\bDOCUMENT\u0010\u0001\u0012\t\n\u0005TABLE\u0010\u0002*8\n\rViewAlgorithm\u0012\r\n\tUNDEFINED\u0010\u0001\u0012\t\n\u0005MERGE\u0010\u0002\u0012\r\n\tTEMPTABLE\u0010\u0003*+\n\u000fViewSqlSecurity\u0012\u000b\n\u0007INVOKER\u0010\u0001\u0012\u000b\n\u0007DEFINER\u0010\u0002**\n\u000fViewCheckOption\u0012\t\n\u0005LOCAL\u0010\u0001\u0012\f\n\bCASCADED\u0010\u0002B\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxExpr.getDescriptor(), MysqlxDatatypes.getDescriptor()});
      internal_static_Mysqlx_Crud_Column_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Crud_Column_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Column_descriptor, new String[]{"Name", "Alias", "DocumentPath"});
      internal_static_Mysqlx_Crud_Projection_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Crud_Projection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Projection_descriptor, new String[]{"Source", "Alias"});
      internal_static_Mysqlx_Crud_Collection_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_Mysqlx_Crud_Collection_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Collection_descriptor, new String[]{"Name", "Schema"});
      internal_static_Mysqlx_Crud_Limit_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(3);
      internal_static_Mysqlx_Crud_Limit_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Limit_descriptor, new String[]{"RowCount", "Offset"});
      internal_static_Mysqlx_Crud_LimitExpr_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(4);
      internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_LimitExpr_descriptor, new String[]{"RowCount", "Offset"});
      internal_static_Mysqlx_Crud_Order_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(5);
      internal_static_Mysqlx_Crud_Order_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Order_descriptor, new String[]{"Expr", "Direction"});
      internal_static_Mysqlx_Crud_UpdateOperation_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(6);
      internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_UpdateOperation_descriptor, new String[]{"Source", "Operation", "Value"});
      internal_static_Mysqlx_Crud_Find_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(7);
      internal_static_Mysqlx_Crud_Find_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Find_descriptor, new String[]{"Collection", "DataModel", "Projection", "Args", "Criteria", "Limit", "Order", "Grouping", "GroupingCriteria", "Locking", "LockingOptions", "LimitExpr"});
      internal_static_Mysqlx_Crud_Insert_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(8);
      internal_static_Mysqlx_Crud_Insert_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Insert_descriptor, new String[]{"Collection", "DataModel", "Projection", "Row", "Args", "Upsert"});
      internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor = (Descriptors.Descriptor)internal_static_Mysqlx_Crud_Insert_descriptor.getNestedTypes().get(0);
      internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor, new String[]{"Field"});
      internal_static_Mysqlx_Crud_Update_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(9);
      internal_static_Mysqlx_Crud_Update_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Update_descriptor, new String[]{"Collection", "DataModel", "Criteria", "Limit", "Order", "Operation", "Args", "LimitExpr"});
      internal_static_Mysqlx_Crud_Delete_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(10);
      internal_static_Mysqlx_Crud_Delete_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_Delete_descriptor, new String[]{"Collection", "DataModel", "Criteria", "Limit", "Order", "Args", "LimitExpr"});
      internal_static_Mysqlx_Crud_CreateView_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(11);
      internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_CreateView_descriptor, new String[]{"Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt", "ReplaceExisting"});
      internal_static_Mysqlx_Crud_ModifyView_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(12);
      internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_ModifyView_descriptor, new String[]{"Collection", "Definer", "Algorithm", "Security", "Check", "Column", "Stmt"});
      internal_static_Mysqlx_Crud_DropView_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(13);
      internal_static_Mysqlx_Crud_DropView_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Crud_DropView_descriptor, new String[]{"Collection", "IfExists"});
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxExpr.getDescriptor();
      MysqlxDatatypes.getDescriptor();
   }

   public static final class DropView extends GeneratedMessageV3 implements DropViewOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private Collection collection_;
      public static final int IF_EXISTS_FIELD_NUMBER = 2;
      private boolean ifExists_;
      private byte memoizedIsInitialized;
      private static final DropView DEFAULT_INSTANCE = new DropView();
      /** @deprecated */
      @Deprecated
      public static final Parser<DropView> PARSER = new AbstractParser<DropView>() {
         public DropView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new DropView(input, extensionRegistry);
         }
      };

      private DropView(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private DropView() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new DropView();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private DropView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = (Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.bitField0_ |= 2;
                        this.ifExists_ = input.readBool();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable.ensureFieldAccessorsInitialized(DropView.class, Builder.class);
      }

      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      public Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public boolean hasIfExists() {
         return (this.bitField0_ & 2) != 0;
      }

      public boolean getIfExists() {
         return this.ifExists_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeBool(2, this.ifExists_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeBoolSize(2, this.ifExists_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof DropView)) {
            return super.equals(obj);
         } else {
            DropView other = (DropView)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasIfExists() != other.hasIfExists()) {
               return false;
            } else if (this.hasIfExists() && this.getIfExists() != other.getIfExists()) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasIfExists()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + Internal.hashBoolean(this.getIfExists());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static DropView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (DropView)PARSER.parseFrom(data);
      }

      public static DropView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (DropView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static DropView parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (DropView)PARSER.parseFrom(data);
      }

      public static DropView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (DropView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static DropView parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (DropView)PARSER.parseFrom(data);
      }

      public static DropView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (DropView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static DropView parseFrom(InputStream input) throws IOException {
         return (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static DropView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static DropView parseDelimitedFrom(InputStream input) throws IOException {
         return (DropView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static DropView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (DropView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static DropView parseFrom(CodedInputStream input) throws IOException {
         return (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static DropView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (DropView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(DropView prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static DropView getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DropView> parser() {
         return PARSER;
      }

      public Parser<DropView> getParserForType() {
         return PARSER;
      }

      public DropView getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      DropView(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      DropView(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DropViewOrBuilder {
         private int bitField0_;
         private Collection collection_;
         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> collectionBuilder_;
         private boolean ifExists_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_fieldAccessorTable.ensureFieldAccessorsInitialized(DropView.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.DropView.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.ifExists_ = false;
            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_DropView_descriptor;
         }

         public DropView getDefaultInstanceForType() {
            return MysqlxCrud.DropView.getDefaultInstance();
         }

         public DropView build() {
            DropView result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public DropView buildPartial() {
            DropView result = new DropView(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = (Collection)this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.ifExists_ = this.ifExists_;
               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof DropView) {
               return this.mergeFrom((DropView)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(DropView other) {
            if (other == MysqlxCrud.DropView.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasIfExists()) {
                  this.setIfExists(other.getIfExists());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else {
               return this.getCollection().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            DropView parsedMessage = null;

            try {
               parsedMessage = (DropView)MysqlxCrud.DropView.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (DropView)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         public Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return (Collection)this.collectionBuilder_.getMessage();
            }
         }

         public Builder setCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCollection(Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Collection.Builder)this.getCollectionFieldBuilder().getBuilder();
         }

         public CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return (CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         public boolean hasIfExists() {
            return (this.bitField0_ & 2) != 0;
         }

         public boolean getIfExists() {
            return this.ifExists_;
         }

         public Builder setIfExists(boolean value) {
            this.bitField0_ |= 2;
            this.ifExists_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearIfExists() {
            this.bitField0_ &= -3;
            this.ifExists_ = false;
            this.onChanged();
            return this;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface DropViewOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      Collection getCollection();

      CollectionOrBuilder getCollectionOrBuilder();

      boolean hasIfExists();

      boolean getIfExists();
   }

   public static final class ModifyView extends GeneratedMessageV3 implements ModifyViewOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private Collection collection_;
      public static final int DEFINER_FIELD_NUMBER = 2;
      private volatile Object definer_;
      public static final int ALGORITHM_FIELD_NUMBER = 3;
      private int algorithm_;
      public static final int SECURITY_FIELD_NUMBER = 4;
      private int security_;
      public static final int CHECK_FIELD_NUMBER = 5;
      private int check_;
      public static final int COLUMN_FIELD_NUMBER = 6;
      private LazyStringList column_;
      public static final int STMT_FIELD_NUMBER = 7;
      private Find stmt_;
      private byte memoizedIsInitialized;
      private static final ModifyView DEFAULT_INSTANCE = new ModifyView();
      /** @deprecated */
      @Deprecated
      public static final Parser<ModifyView> PARSER = new AbstractParser<ModifyView>() {
         public ModifyView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new ModifyView(input, extensionRegistry);
         }
      };

      private ModifyView(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private ModifyView() {
         this.memoizedIsInitialized = -1;
         this.definer_ = "";
         this.algorithm_ = 1;
         this.security_ = 1;
         this.check_ = 1;
         this.column_ = LazyStringArrayList.EMPTY;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new ModifyView();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ModifyView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  ByteString bs;
                  int rawValue;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = (Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.definer_ = bs;
                        break;
                     case 24:
                        rawValue = input.readEnum();
                        ViewAlgorithm value = MysqlxCrud.ViewAlgorithm.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(3, rawValue);
                        } else {
                           this.bitField0_ |= 4;
                           this.algorithm_ = rawValue;
                        }
                        break;
                     case 32:
                        rawValue = input.readEnum();
                        ViewSqlSecurity value = MysqlxCrud.ViewSqlSecurity.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(4, rawValue);
                        } else {
                           this.bitField0_ |= 8;
                           this.security_ = rawValue;
                        }
                        break;
                     case 40:
                        rawValue = input.readEnum();
                        ViewCheckOption value = MysqlxCrud.ViewCheckOption.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(5, rawValue);
                        } else {
                           this.bitField0_ |= 16;
                           this.check_ = rawValue;
                        }
                        break;
                     case 50:
                        bs = input.readBytes();
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.column_ = new LazyStringArrayList();
                           mutable_bitField0_ |= 32;
                        }

                        this.column_.add(bs);
                        break;
                     case 58:
                        Find.Builder subBuilder = null;
                        if ((this.bitField0_ & 32) != 0) {
                           subBuilder = this.stmt_.toBuilder();
                        }

                        this.stmt_ = (Find)input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.stmt_);
                           this.stmt_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 32;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 32) != 0) {
                  this.column_ = this.column_.getUnmodifiableView();
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable.ensureFieldAccessorsInitialized(ModifyView.class, Builder.class);
      }

      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      public Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public boolean hasDefiner() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getDefiner() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.definer_ = s;
            }

            return s;
         }
      }

      public ByteString getDefinerBytes() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.definer_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasAlgorithm() {
         return (this.bitField0_ & 4) != 0;
      }

      public ViewAlgorithm getAlgorithm() {
         ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
         return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
      }

      public boolean hasSecurity() {
         return (this.bitField0_ & 8) != 0;
      }

      public ViewSqlSecurity getSecurity() {
         ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
         return result == null ? MysqlxCrud.ViewSqlSecurity.INVOKER : result;
      }

      public boolean hasCheck() {
         return (this.bitField0_ & 16) != 0;
      }

      public ViewCheckOption getCheck() {
         ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
         return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
      }

      public ProtocolStringList getColumnList() {
         return this.column_;
      }

      public int getColumnCount() {
         return this.column_.size();
      }

      public String getColumn(int index) {
         return (String)this.column_.get(index);
      }

      public ByteString getColumnBytes(int index) {
         return this.column_.getByteString(index);
      }

      public boolean hasStmt() {
         return (this.bitField0_ & 32) != 0;
      }

      public Find getStmt() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      public FindOrBuilder getStmtOrBuilder() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasStmt() && !this.getStmt().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.definer_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeEnum(3, this.algorithm_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeEnum(4, this.security_);
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeEnum(5, this.check_);
         }

         for(int i = 0; i < this.column_.size(); ++i) {
            GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i));
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeMessage(7, this.getStmt());
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.definer_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.algorithm_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeEnumSize(4, this.security_);
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeEnumSize(5, this.check_);
            }

            int dataSize = 0;

            for(int i = 0; i < this.column_.size(); ++i) {
               dataSize += computeStringSizeNoTag(this.column_.getRaw(i));
            }

            size += dataSize;
            size += 1 * this.getColumnList().size();
            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeMessageSize(7, this.getStmt());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof ModifyView)) {
            return super.equals(obj);
         } else {
            ModifyView other = (ModifyView)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDefiner() != other.hasDefiner()) {
               return false;
            } else if (this.hasDefiner() && !this.getDefiner().equals(other.getDefiner())) {
               return false;
            } else if (this.hasAlgorithm() != other.hasAlgorithm()) {
               return false;
            } else if (this.hasAlgorithm() && this.algorithm_ != other.algorithm_) {
               return false;
            } else if (this.hasSecurity() != other.hasSecurity()) {
               return false;
            } else if (this.hasSecurity() && this.security_ != other.security_) {
               return false;
            } else if (this.hasCheck() != other.hasCheck()) {
               return false;
            } else if (this.hasCheck() && this.check_ != other.check_) {
               return false;
            } else if (!this.getColumnList().equals(other.getColumnList())) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else if (this.hasStmt() && !this.getStmt().equals(other.getStmt())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDefiner()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getDefiner().hashCode();
            }

            if (this.hasAlgorithm()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.algorithm_;
            }

            if (this.hasSecurity()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.security_;
            }

            if (this.hasCheck()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.check_;
            }

            if (this.getColumnCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getColumnList().hashCode();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static ModifyView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (ModifyView)PARSER.parseFrom(data);
      }

      public static ModifyView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ModifyView parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (ModifyView)PARSER.parseFrom(data);
      }

      public static ModifyView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ModifyView parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (ModifyView)PARSER.parseFrom(data);
      }

      public static ModifyView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ModifyView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ModifyView parseFrom(InputStream input) throws IOException {
         return (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ModifyView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static ModifyView parseDelimitedFrom(InputStream input) throws IOException {
         return (ModifyView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static ModifyView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ModifyView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static ModifyView parseFrom(CodedInputStream input) throws IOException {
         return (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ModifyView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ModifyView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(ModifyView prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static ModifyView getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<ModifyView> parser() {
         return PARSER;
      }

      public Parser<ModifyView> getParserForType() {
         return PARSER;
      }

      public ModifyView getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      ModifyView(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      ModifyView(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ModifyViewOrBuilder {
         private int bitField0_;
         private Collection collection_;
         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> collectionBuilder_;
         private Object definer_;
         private int algorithm_;
         private int security_;
         private int check_;
         private LazyStringList column_;
         private Find stmt_;
         private SingleFieldBuilderV3<Find, Find.Builder, FindOrBuilder> stmtBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_fieldAccessorTable.ensureFieldAccessorsInitialized(ModifyView.class, Builder.class);
         }

         private Builder() {
            this.definer_ = "";
            this.algorithm_ = 1;
            this.security_ = 1;
            this.check_ = 1;
            this.column_ = LazyStringArrayList.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.definer_ = "";
            this.algorithm_ = 1;
            this.security_ = 1;
            this.check_ = 1;
            this.column_ = LazyStringArrayList.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.ModifyView.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getStmtFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.definer_ = "";
            this.bitField0_ &= -3;
            this.algorithm_ = 1;
            this.bitField0_ &= -5;
            this.security_ = 1;
            this.bitField0_ &= -9;
            this.check_ = 1;
            this.bitField0_ &= -17;
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_ModifyView_descriptor;
         }

         public ModifyView getDefaultInstanceForType() {
            return MysqlxCrud.ModifyView.getDefaultInstance();
         }

         public ModifyView build() {
            ModifyView result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public ModifyView buildPartial() {
            ModifyView result = new ModifyView(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = (Collection)this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.definer_ = this.definer_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.algorithm_ = this.algorithm_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.security_ = this.security_;
            if ((from_bitField0_ & 16) != 0) {
               to_bitField0_ |= 16;
            }

            result.check_ = this.check_;
            if ((this.bitField0_ & 32) != 0) {
               this.column_ = this.column_.getUnmodifiableView();
               this.bitField0_ &= -33;
            }

            result.column_ = this.column_;
            if ((from_bitField0_ & 64) != 0) {
               if (this.stmtBuilder_ == null) {
                  result.stmt_ = this.stmt_;
               } else {
                  result.stmt_ = (Find)this.stmtBuilder_.build();
               }

               to_bitField0_ |= 32;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof ModifyView) {
               return this.mergeFrom((ModifyView)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(ModifyView other) {
            if (other == MysqlxCrud.ModifyView.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDefiner()) {
                  this.bitField0_ |= 2;
                  this.definer_ = other.definer_;
                  this.onChanged();
               }

               if (other.hasAlgorithm()) {
                  this.setAlgorithm(other.getAlgorithm());
               }

               if (other.hasSecurity()) {
                  this.setSecurity(other.getSecurity());
               }

               if (other.hasCheck()) {
                  this.setCheck(other.getCheck());
               }

               if (!other.column_.isEmpty()) {
                  if (this.column_.isEmpty()) {
                     this.column_ = other.column_;
                     this.bitField0_ &= -33;
                  } else {
                     this.ensureColumnIsMutable();
                     this.column_.addAll(other.column_);
                  }

                  this.onChanged();
               }

               if (other.hasStmt()) {
                  this.mergeStmt(other.getStmt());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else {
               return !this.hasStmt() || this.getStmt().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            ModifyView parsedMessage = null;

            try {
               parsedMessage = (ModifyView)MysqlxCrud.ModifyView.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (ModifyView)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         public Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return (Collection)this.collectionBuilder_.getMessage();
            }
         }

         public Builder setCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCollection(Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Collection.Builder)this.getCollectionFieldBuilder().getBuilder();
         }

         public CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return (CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         public boolean hasDefiner() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getDefiner() {
            Object ref = this.definer_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.definer_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getDefinerBytes() {
            Object ref = this.definer_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.definer_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setDefiner(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearDefiner() {
            this.bitField0_ &= -3;
            this.definer_ = MysqlxCrud.ModifyView.getDefaultInstance().getDefiner();
            this.onChanged();
            return this;
         }

         public Builder setDefinerBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasAlgorithm() {
            return (this.bitField0_ & 4) != 0;
         }

         public ViewAlgorithm getAlgorithm() {
            ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
            return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
         }

         public Builder setAlgorithm(ViewAlgorithm value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.algorithm_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearAlgorithm() {
            this.bitField0_ &= -5;
            this.algorithm_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasSecurity() {
            return (this.bitField0_ & 8) != 0;
         }

         public ViewSqlSecurity getSecurity() {
            ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
            return result == null ? MysqlxCrud.ViewSqlSecurity.INVOKER : result;
         }

         public Builder setSecurity(ViewSqlSecurity value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.security_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearSecurity() {
            this.bitField0_ &= -9;
            this.security_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasCheck() {
            return (this.bitField0_ & 16) != 0;
         }

         public ViewCheckOption getCheck() {
            ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
            return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
         }

         public Builder setCheck(ViewCheckOption value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 16;
               this.check_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearCheck() {
            this.bitField0_ &= -17;
            this.check_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureColumnIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.column_ = new LazyStringArrayList(this.column_);
               this.bitField0_ |= 32;
            }

         }

         public ProtocolStringList getColumnList() {
            return this.column_.getUnmodifiableView();
         }

         public int getColumnCount() {
            return this.column_.size();
         }

         public String getColumn(int index) {
            return (String)this.column_.get(index);
         }

         public ByteString getColumnBytes(int index) {
            return this.column_.getByteString(index);
         }

         public Builder setColumn(int index, String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.set(index, (Object)value);
               this.onChanged();
               return this;
            }
         }

         public Builder addColumn(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add((Object)value);
               this.onChanged();
               return this;
            }
         }

         public Builder addAllColumn(Iterable<String> values) {
            this.ensureColumnIsMutable();
            AbstractMessageLite.Builder.addAll(values, (List)this.column_);
            this.onChanged();
            return this;
         }

         public Builder clearColumn() {
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            this.onChanged();
            return this;
         }

         public Builder addColumnBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add(value);
               this.onChanged();
               return this;
            }
         }

         public boolean hasStmt() {
            return (this.bitField0_ & 64) != 0;
         }

         public Find getStmt() {
            if (this.stmtBuilder_ == null) {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            } else {
               return (Find)this.stmtBuilder_.getMessage();
            }
         }

         public Builder setStmt(Find value) {
            if (this.stmtBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.stmt_ = value;
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder setStmt(Find.Builder builderForValue) {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = builderForValue.build();
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder mergeStmt(Find value) {
            if (this.stmtBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0 && this.stmt_ != null && this.stmt_ != MysqlxCrud.Find.getDefaultInstance()) {
                  this.stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
               } else {
                  this.stmt_ = value;
               }

               this.onChanged();
            } else {
               this.stmtBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder clearStmt() {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
               this.onChanged();
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public Find.Builder getStmtBuilder() {
            this.bitField0_ |= 64;
            this.onChanged();
            return (Find.Builder)this.getStmtFieldBuilder().getBuilder();
         }

         public FindOrBuilder getStmtOrBuilder() {
            if (this.stmtBuilder_ != null) {
               return (FindOrBuilder)this.stmtBuilder_.getMessageOrBuilder();
            } else {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            }
         }

         private SingleFieldBuilderV3<Find, Find.Builder, FindOrBuilder> getStmtFieldBuilder() {
            if (this.stmtBuilder_ == null) {
               this.stmtBuilder_ = new SingleFieldBuilderV3(this.getStmt(), this.getParentForChildren(), this.isClean());
               this.stmt_ = null;
            }

            return this.stmtBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface ModifyViewOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      Collection getCollection();

      CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDefiner();

      String getDefiner();

      ByteString getDefinerBytes();

      boolean hasAlgorithm();

      ViewAlgorithm getAlgorithm();

      boolean hasSecurity();

      ViewSqlSecurity getSecurity();

      boolean hasCheck();

      ViewCheckOption getCheck();

      List<String> getColumnList();

      int getColumnCount();

      String getColumn(int var1);

      ByteString getColumnBytes(int var1);

      boolean hasStmt();

      Find getStmt();

      FindOrBuilder getStmtOrBuilder();
   }

   public static final class CreateView extends GeneratedMessageV3 implements CreateViewOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private Collection collection_;
      public static final int DEFINER_FIELD_NUMBER = 2;
      private volatile Object definer_;
      public static final int ALGORITHM_FIELD_NUMBER = 3;
      private int algorithm_;
      public static final int SECURITY_FIELD_NUMBER = 4;
      private int security_;
      public static final int CHECK_FIELD_NUMBER = 5;
      private int check_;
      public static final int COLUMN_FIELD_NUMBER = 6;
      private LazyStringList column_;
      public static final int STMT_FIELD_NUMBER = 7;
      private Find stmt_;
      public static final int REPLACE_EXISTING_FIELD_NUMBER = 8;
      private boolean replaceExisting_;
      private byte memoizedIsInitialized;
      private static final CreateView DEFAULT_INSTANCE = new CreateView();
      /** @deprecated */
      @Deprecated
      public static final Parser<CreateView> PARSER = new AbstractParser<CreateView>() {
         public CreateView parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new CreateView(input, extensionRegistry);
         }
      };

      private CreateView(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private CreateView() {
         this.memoizedIsInitialized = -1;
         this.definer_ = "";
         this.algorithm_ = 1;
         this.security_ = 2;
         this.check_ = 1;
         this.column_ = LazyStringArrayList.EMPTY;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new CreateView();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CreateView(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  ByteString bs;
                  int rawValue;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = (Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.definer_ = bs;
                        break;
                     case 24:
                        rawValue = input.readEnum();
                        ViewAlgorithm value = MysqlxCrud.ViewAlgorithm.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(3, rawValue);
                        } else {
                           this.bitField0_ |= 4;
                           this.algorithm_ = rawValue;
                        }
                        break;
                     case 32:
                        rawValue = input.readEnum();
                        ViewSqlSecurity value = MysqlxCrud.ViewSqlSecurity.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(4, rawValue);
                        } else {
                           this.bitField0_ |= 8;
                           this.security_ = rawValue;
                        }
                        break;
                     case 40:
                        rawValue = input.readEnum();
                        ViewCheckOption value = MysqlxCrud.ViewCheckOption.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(5, rawValue);
                        } else {
                           this.bitField0_ |= 16;
                           this.check_ = rawValue;
                        }
                        break;
                     case 50:
                        bs = input.readBytes();
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.column_ = new LazyStringArrayList();
                           mutable_bitField0_ |= 32;
                        }

                        this.column_.add(bs);
                        break;
                     case 58:
                        Find.Builder subBuilder = null;
                        if ((this.bitField0_ & 32) != 0) {
                           subBuilder = this.stmt_.toBuilder();
                        }

                        this.stmt_ = (Find)input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.stmt_);
                           this.stmt_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 32;
                        break;
                     case 64:
                        this.bitField0_ |= 64;
                        this.replaceExisting_ = input.readBool();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 32) != 0) {
                  this.column_ = this.column_.getUnmodifiableView();
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable.ensureFieldAccessorsInitialized(CreateView.class, Builder.class);
      }

      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      public Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public boolean hasDefiner() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getDefiner() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.definer_ = s;
            }

            return s;
         }
      }

      public ByteString getDefinerBytes() {
         Object ref = this.definer_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.definer_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasAlgorithm() {
         return (this.bitField0_ & 4) != 0;
      }

      public ViewAlgorithm getAlgorithm() {
         ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
         return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
      }

      public boolean hasSecurity() {
         return (this.bitField0_ & 8) != 0;
      }

      public ViewSqlSecurity getSecurity() {
         ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
         return result == null ? MysqlxCrud.ViewSqlSecurity.DEFINER : result;
      }

      public boolean hasCheck() {
         return (this.bitField0_ & 16) != 0;
      }

      public ViewCheckOption getCheck() {
         ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
         return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
      }

      public ProtocolStringList getColumnList() {
         return this.column_;
      }

      public int getColumnCount() {
         return this.column_.size();
      }

      public String getColumn(int index) {
         return (String)this.column_.get(index);
      }

      public ByteString getColumnBytes(int index) {
         return this.column_.getByteString(index);
      }

      public boolean hasStmt() {
         return (this.bitField0_ & 32) != 0;
      }

      public Find getStmt() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      public FindOrBuilder getStmtOrBuilder() {
         return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
      }

      public boolean hasReplaceExisting() {
         return (this.bitField0_ & 64) != 0;
      }

      public boolean getReplaceExisting() {
         return this.replaceExisting_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasStmt()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getStmt().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.definer_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeEnum(3, this.algorithm_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeEnum(4, this.security_);
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeEnum(5, this.check_);
         }

         for(int i = 0; i < this.column_.size(); ++i) {
            GeneratedMessageV3.writeString(output, 6, this.column_.getRaw(i));
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeMessage(7, this.getStmt());
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeBool(8, this.replaceExisting_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.definer_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.algorithm_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeEnumSize(4, this.security_);
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeEnumSize(5, this.check_);
            }

            int dataSize = 0;

            for(int i = 0; i < this.column_.size(); ++i) {
               dataSize += computeStringSizeNoTag(this.column_.getRaw(i));
            }

            size += dataSize;
            size += 1 * this.getColumnList().size();
            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeMessageSize(7, this.getStmt());
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeBoolSize(8, this.replaceExisting_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof CreateView)) {
            return super.equals(obj);
         } else {
            CreateView other = (CreateView)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDefiner() != other.hasDefiner()) {
               return false;
            } else if (this.hasDefiner() && !this.getDefiner().equals(other.getDefiner())) {
               return false;
            } else if (this.hasAlgorithm() != other.hasAlgorithm()) {
               return false;
            } else if (this.hasAlgorithm() && this.algorithm_ != other.algorithm_) {
               return false;
            } else if (this.hasSecurity() != other.hasSecurity()) {
               return false;
            } else if (this.hasSecurity() && this.security_ != other.security_) {
               return false;
            } else if (this.hasCheck() != other.hasCheck()) {
               return false;
            } else if (this.hasCheck() && this.check_ != other.check_) {
               return false;
            } else if (!this.getColumnList().equals(other.getColumnList())) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else if (this.hasStmt() && !this.getStmt().equals(other.getStmt())) {
               return false;
            } else if (this.hasReplaceExisting() != other.hasReplaceExisting()) {
               return false;
            } else if (this.hasReplaceExisting() && this.getReplaceExisting() != other.getReplaceExisting()) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDefiner()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getDefiner().hashCode();
            }

            if (this.hasAlgorithm()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.algorithm_;
            }

            if (this.hasSecurity()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.security_;
            }

            if (this.hasCheck()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.check_;
            }

            if (this.getColumnCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getColumnList().hashCode();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            if (this.hasReplaceExisting()) {
               hash = 37 * hash + 8;
               hash = 53 * hash + Internal.hashBoolean(this.getReplaceExisting());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static CreateView parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (CreateView)PARSER.parseFrom(data);
      }

      public static CreateView parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CreateView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CreateView parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (CreateView)PARSER.parseFrom(data);
      }

      public static CreateView parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CreateView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CreateView parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (CreateView)PARSER.parseFrom(data);
      }

      public static CreateView parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (CreateView)PARSER.parseFrom(data, extensionRegistry);
      }

      public static CreateView parseFrom(InputStream input) throws IOException {
         return (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static CreateView parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static CreateView parseDelimitedFrom(InputStream input) throws IOException {
         return (CreateView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static CreateView parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CreateView)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static CreateView parseFrom(CodedInputStream input) throws IOException {
         return (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static CreateView parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (CreateView)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(CreateView prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static CreateView getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<CreateView> parser() {
         return PARSER;
      }

      public Parser<CreateView> getParserForType() {
         return PARSER;
      }

      public CreateView getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      CreateView(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      CreateView(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CreateViewOrBuilder {
         private int bitField0_;
         private Collection collection_;
         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> collectionBuilder_;
         private Object definer_;
         private int algorithm_;
         private int security_;
         private int check_;
         private LazyStringList column_;
         private Find stmt_;
         private SingleFieldBuilderV3<Find, Find.Builder, FindOrBuilder> stmtBuilder_;
         private boolean replaceExisting_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_fieldAccessorTable.ensureFieldAccessorsInitialized(CreateView.class, Builder.class);
         }

         private Builder() {
            this.definer_ = "";
            this.algorithm_ = 1;
            this.security_ = 2;
            this.check_ = 1;
            this.column_ = LazyStringArrayList.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.definer_ = "";
            this.algorithm_ = 1;
            this.security_ = 2;
            this.check_ = 1;
            this.column_ = LazyStringArrayList.EMPTY;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.CreateView.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getStmtFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.definer_ = "";
            this.bitField0_ &= -3;
            this.algorithm_ = 1;
            this.bitField0_ &= -5;
            this.security_ = 2;
            this.bitField0_ &= -9;
            this.check_ = 1;
            this.bitField0_ &= -17;
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            this.replaceExisting_ = false;
            this.bitField0_ &= -129;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_CreateView_descriptor;
         }

         public CreateView getDefaultInstanceForType() {
            return MysqlxCrud.CreateView.getDefaultInstance();
         }

         public CreateView build() {
            CreateView result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public CreateView buildPartial() {
            CreateView result = new CreateView(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = (Collection)this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.definer_ = this.definer_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.algorithm_ = this.algorithm_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.security_ = this.security_;
            if ((from_bitField0_ & 16) != 0) {
               to_bitField0_ |= 16;
            }

            result.check_ = this.check_;
            if ((this.bitField0_ & 32) != 0) {
               this.column_ = this.column_.getUnmodifiableView();
               this.bitField0_ &= -33;
            }

            result.column_ = this.column_;
            if ((from_bitField0_ & 64) != 0) {
               if (this.stmtBuilder_ == null) {
                  result.stmt_ = this.stmt_;
               } else {
                  result.stmt_ = (Find)this.stmtBuilder_.build();
               }

               to_bitField0_ |= 32;
            }

            if ((from_bitField0_ & 128) != 0) {
               result.replaceExisting_ = this.replaceExisting_;
               to_bitField0_ |= 64;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof CreateView) {
               return this.mergeFrom((CreateView)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(CreateView other) {
            if (other == MysqlxCrud.CreateView.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDefiner()) {
                  this.bitField0_ |= 2;
                  this.definer_ = other.definer_;
                  this.onChanged();
               }

               if (other.hasAlgorithm()) {
                  this.setAlgorithm(other.getAlgorithm());
               }

               if (other.hasSecurity()) {
                  this.setSecurity(other.getSecurity());
               }

               if (other.hasCheck()) {
                  this.setCheck(other.getCheck());
               }

               if (!other.column_.isEmpty()) {
                  if (this.column_.isEmpty()) {
                     this.column_ = other.column_;
                     this.bitField0_ &= -33;
                  } else {
                     this.ensureColumnIsMutable();
                     this.column_.addAll(other.column_);
                  }

                  this.onChanged();
               }

               if (other.hasStmt()) {
                  this.mergeStmt(other.getStmt());
               }

               if (other.hasReplaceExisting()) {
                  this.setReplaceExisting(other.getReplaceExisting());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.hasStmt()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else {
               return this.getStmt().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            CreateView parsedMessage = null;

            try {
               parsedMessage = (CreateView)MysqlxCrud.CreateView.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (CreateView)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         public Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return (Collection)this.collectionBuilder_.getMessage();
            }
         }

         public Builder setCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCollection(Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Collection.Builder)this.getCollectionFieldBuilder().getBuilder();
         }

         public CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return (CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         public boolean hasDefiner() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getDefiner() {
            Object ref = this.definer_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.definer_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getDefinerBytes() {
            Object ref = this.definer_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.definer_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setDefiner(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearDefiner() {
            this.bitField0_ &= -3;
            this.definer_ = MysqlxCrud.CreateView.getDefaultInstance().getDefiner();
            this.onChanged();
            return this;
         }

         public Builder setDefinerBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.definer_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasAlgorithm() {
            return (this.bitField0_ & 4) != 0;
         }

         public ViewAlgorithm getAlgorithm() {
            ViewAlgorithm result = MysqlxCrud.ViewAlgorithm.valueOf(this.algorithm_);
            return result == null ? MysqlxCrud.ViewAlgorithm.UNDEFINED : result;
         }

         public Builder setAlgorithm(ViewAlgorithm value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.algorithm_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearAlgorithm() {
            this.bitField0_ &= -5;
            this.algorithm_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasSecurity() {
            return (this.bitField0_ & 8) != 0;
         }

         public ViewSqlSecurity getSecurity() {
            ViewSqlSecurity result = MysqlxCrud.ViewSqlSecurity.valueOf(this.security_);
            return result == null ? MysqlxCrud.ViewSqlSecurity.DEFINER : result;
         }

         public Builder setSecurity(ViewSqlSecurity value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.security_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearSecurity() {
            this.bitField0_ &= -9;
            this.security_ = 2;
            this.onChanged();
            return this;
         }

         public boolean hasCheck() {
            return (this.bitField0_ & 16) != 0;
         }

         public ViewCheckOption getCheck() {
            ViewCheckOption result = MysqlxCrud.ViewCheckOption.valueOf(this.check_);
            return result == null ? MysqlxCrud.ViewCheckOption.LOCAL : result;
         }

         public Builder setCheck(ViewCheckOption value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 16;
               this.check_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearCheck() {
            this.bitField0_ &= -17;
            this.check_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureColumnIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.column_ = new LazyStringArrayList(this.column_);
               this.bitField0_ |= 32;
            }

         }

         public ProtocolStringList getColumnList() {
            return this.column_.getUnmodifiableView();
         }

         public int getColumnCount() {
            return this.column_.size();
         }

         public String getColumn(int index) {
            return (String)this.column_.get(index);
         }

         public ByteString getColumnBytes(int index) {
            return this.column_.getByteString(index);
         }

         public Builder setColumn(int index, String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.set(index, (Object)value);
               this.onChanged();
               return this;
            }
         }

         public Builder addColumn(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add((Object)value);
               this.onChanged();
               return this;
            }
         }

         public Builder addAllColumn(Iterable<String> values) {
            this.ensureColumnIsMutable();
            AbstractMessageLite.Builder.addAll(values, (List)this.column_);
            this.onChanged();
            return this;
         }

         public Builder clearColumn() {
            this.column_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -33;
            this.onChanged();
            return this;
         }

         public Builder addColumnBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureColumnIsMutable();
               this.column_.add(value);
               this.onChanged();
               return this;
            }
         }

         public boolean hasStmt() {
            return (this.bitField0_ & 64) != 0;
         }

         public Find getStmt() {
            if (this.stmtBuilder_ == null) {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            } else {
               return (Find)this.stmtBuilder_.getMessage();
            }
         }

         public Builder setStmt(Find value) {
            if (this.stmtBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.stmt_ = value;
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder setStmt(Find.Builder builderForValue) {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = builderForValue.build();
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder mergeStmt(Find value) {
            if (this.stmtBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0 && this.stmt_ != null && this.stmt_ != MysqlxCrud.Find.getDefaultInstance()) {
                  this.stmt_ = MysqlxCrud.Find.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
               } else {
                  this.stmt_ = value;
               }

               this.onChanged();
            } else {
               this.stmtBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder clearStmt() {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
               this.onChanged();
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public Find.Builder getStmtBuilder() {
            this.bitField0_ |= 64;
            this.onChanged();
            return (Find.Builder)this.getStmtFieldBuilder().getBuilder();
         }

         public FindOrBuilder getStmtOrBuilder() {
            if (this.stmtBuilder_ != null) {
               return (FindOrBuilder)this.stmtBuilder_.getMessageOrBuilder();
            } else {
               return this.stmt_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.stmt_;
            }
         }

         private SingleFieldBuilderV3<Find, Find.Builder, FindOrBuilder> getStmtFieldBuilder() {
            if (this.stmtBuilder_ == null) {
               this.stmtBuilder_ = new SingleFieldBuilderV3(this.getStmt(), this.getParentForChildren(), this.isClean());
               this.stmt_ = null;
            }

            return this.stmtBuilder_;
         }

         public boolean hasReplaceExisting() {
            return (this.bitField0_ & 128) != 0;
         }

         public boolean getReplaceExisting() {
            return this.replaceExisting_;
         }

         public Builder setReplaceExisting(boolean value) {
            this.bitField0_ |= 128;
            this.replaceExisting_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearReplaceExisting() {
            this.bitField0_ &= -129;
            this.replaceExisting_ = false;
            this.onChanged();
            return this;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface CreateViewOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      Collection getCollection();

      CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDefiner();

      String getDefiner();

      ByteString getDefinerBytes();

      boolean hasAlgorithm();

      ViewAlgorithm getAlgorithm();

      boolean hasSecurity();

      ViewSqlSecurity getSecurity();

      boolean hasCheck();

      ViewCheckOption getCheck();

      List<String> getColumnList();

      int getColumnCount();

      String getColumn(int var1);

      ByteString getColumnBytes(int var1);

      boolean hasStmt();

      Find getStmt();

      FindOrBuilder getStmtOrBuilder();

      boolean hasReplaceExisting();

      boolean getReplaceExisting();
   }

   public static final class Delete extends GeneratedMessageV3 implements DeleteOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 2;
      private int dataModel_;
      public static final int CRITERIA_FIELD_NUMBER = 3;
      private MysqlxExpr.Expr criteria_;
      public static final int LIMIT_FIELD_NUMBER = 4;
      private Limit limit_;
      public static final int ORDER_FIELD_NUMBER = 5;
      private List<Order> order_;
      public static final int ARGS_FIELD_NUMBER = 6;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int LIMIT_EXPR_FIELD_NUMBER = 7;
      private LimitExpr limitExpr_;
      private byte memoizedIsInitialized;
      private static final Delete DEFAULT_INSTANCE = new Delete();
      /** @deprecated */
      @Deprecated
      public static final Parser<Delete> PARSER = new AbstractParser<Delete>() {
         public Delete parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Delete(input, extensionRegistry);
         }
      };

      private Delete(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Delete() {
         this.memoizedIsInitialized = -1;
         this.dataModel_ = 1;
         this.order_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Delete();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Delete(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = (Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        DataModel value = MysqlxCrud.DataModel.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValue;
                        }
                        break;
                     case 26:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilder = this.criteria_.toBuilder();
                        }

                        this.criteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.criteria_);
                           this.criteria_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        Limit.Builder subBuilder = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilder = this.limit_.toBuilder();
                        }

                        this.limit_ = (Limit)input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limit_);
                           this.limit_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 42:
                        if ((mutable_bitField0_ & 16) == 0) {
                           this.order_ = new ArrayList();
                           mutable_bitField0_ |= 16;
                        }

                        this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry));
                        break;
                     case 50:
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.args_ = new ArrayList();
                           mutable_bitField0_ |= 32;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 58:
                        LimitExpr.Builder subBuilder = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilder = this.limitExpr_.toBuilder();
                        }

                        this.limitExpr_ = (LimitExpr)input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limitExpr_);
                           this.limitExpr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
               }

               if ((mutable_bitField0_ & 32) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable.ensureFieldAccessorsInitialized(Delete.class, Builder.class);
      }

      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      public Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      public DataModel getDataModel() {
         DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      public boolean hasCriteria() {
         return (this.bitField0_ & 4) != 0;
      }

      public MysqlxExpr.Expr getCriteria() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      public boolean hasLimit() {
         return (this.bitField0_ & 8) != 0;
      }

      public Limit getLimit() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      public LimitOrBuilder getLimitOrBuilder() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      public List<Order> getOrderList() {
         return this.order_;
      }

      public List<? extends OrderOrBuilder> getOrderOrBuilderList() {
         return this.order_;
      }

      public int getOrderCount() {
         return this.order_.size();
      }

      public Order getOrder(int index) {
         return (Order)this.order_.get(index);
      }

      public OrderOrBuilder getOrderOrBuilder(int index) {
         return (OrderOrBuilder)this.order_.get(index);
      }

      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      public int getArgsCount() {
         return this.args_.size();
      }

      public MysqlxDatatypes.Scalar getArgs(int index) {
         return (MysqlxDatatypes.Scalar)this.args_.get(index);
      }

      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index);
      }

      public boolean hasLimitExpr() {
         return (this.bitField0_ & 16) != 0;
      }

      public LimitExpr getLimitExpr() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      public LimitExprOrBuilder getLimitExprOrBuilder() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            int i;
            for(i = 0; i < this.getOrderCount(); ++i) {
               if (!this.getOrder(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for(i = 0; i < this.getArgsCount(); ++i) {
               if (!this.getArgs(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.dataModel_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(3, this.getCriteria());
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(4, this.getLimit());
         }

         int i;
         for(i = 0; i < this.order_.size(); ++i) {
            output.writeMessage(5, (MessageLite)this.order_.get(i));
         }

         for(i = 0; i < this.args_.size(); ++i) {
            output.writeMessage(6, (MessageLite)this.args_.get(i));
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(7, this.getLimitExpr());
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.dataModel_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(3, this.getCriteria());
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getLimit());
            }

            int i;
            for(i = 0; i < this.order_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(5, (MessageLite)this.order_.get(i));
            }

            for(i = 0; i < this.args_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(6, (MessageLite)this.args_.get(i));
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(7, this.getLimitExpr());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Delete)) {
            return super.equals(obj);
         } else {
            Delete other = (Delete)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (this.hasCriteria() != other.hasCriteria()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().equals(other.getCriteria())) {
               return false;
            } else if (this.hasLimit() != other.hasLimit()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().equals(other.getLimit())) {
               return false;
            } else if (!this.getOrderList().equals(other.getOrderList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasLimitExpr() != other.hasLimitExpr()) {
               return false;
            } else if (this.hasLimitExpr() && !this.getLimitExpr().equals(other.getLimitExpr())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.hasCriteria()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getCriteria().hashCode();
            }

            if (this.hasLimit()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getLimit().hashCode();
            }

            if (this.getOrderCount() > 0) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getOrderList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasLimitExpr()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getLimitExpr().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Delete parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Delete)PARSER.parseFrom(data);
      }

      public static Delete parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Delete)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Delete parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Delete)PARSER.parseFrom(data);
      }

      public static Delete parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Delete)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Delete parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Delete)PARSER.parseFrom(data);
      }

      public static Delete parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Delete)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Delete parseFrom(InputStream input) throws IOException {
         return (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Delete parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Delete parseDelimitedFrom(InputStream input) throws IOException {
         return (Delete)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Delete parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Delete)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Delete parseFrom(CodedInputStream input) throws IOException {
         return (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Delete parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Delete)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Delete prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Delete getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Delete> parser() {
         return PARSER;
      }

      public Parser<Delete> getParserForType() {
         return PARSER;
      }

      public Delete getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Delete(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Delete(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DeleteOrBuilder {
         private int bitField0_;
         private Collection collection_;
         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> collectionBuilder_;
         private int dataModel_;
         private MysqlxExpr.Expr criteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
         private Limit limit_;
         private SingleFieldBuilderV3<Limit, Limit.Builder, LimitOrBuilder> limitBuilder_;
         private List<Order> order_;
         private RepeatedFieldBuilderV3<Order, Order.Builder, OrderOrBuilder> orderBuilder_;
         private List<MysqlxDatatypes.Scalar> args_;
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private LimitExpr limitExpr_;
         private SingleFieldBuilderV3<LimitExpr, LimitExpr.Builder, LimitExprOrBuilder> limitExprBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_fieldAccessorTable.ensureFieldAccessorsInitialized(Delete.class, Builder.class);
         }

         private Builder() {
            this.dataModel_ = 1;
            this.order_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.dataModel_ = 1;
            this.order_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Delete.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getCriteriaFieldBuilder();
               this.getLimitFieldBuilder();
               this.getOrderFieldBuilder();
               this.getArgsFieldBuilder();
               this.getLimitExprFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
            } else {
               this.orderBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -33;
            } else {
               this.argsBuilder_.clear();
            }

            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Delete_descriptor;
         }

         public Delete getDefaultInstanceForType() {
            return MysqlxCrud.Delete.getDefaultInstance();
         }

         public Delete build() {
            Delete result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Delete buildPartial() {
            Delete result = new Delete(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = (Collection)this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if ((from_bitField0_ & 4) != 0) {
               if (this.criteriaBuilder_ == null) {
                  result.criteria_ = this.criteria_;
               } else {
                  result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 8) != 0) {
               if (this.limitBuilder_ == null) {
                  result.limit_ = this.limit_;
               } else {
                  result.limit_ = (Limit)this.limitBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if (this.orderBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
                  this.bitField0_ &= -17;
               }

               result.order_ = this.order_;
            } else {
               result.order_ = this.orderBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -33;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 64) != 0) {
               if (this.limitExprBuilder_ == null) {
                  result.limitExpr_ = this.limitExpr_;
               } else {
                  result.limitExpr_ = (LimitExpr)this.limitExprBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Delete) {
               return this.mergeFrom((Delete)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Delete other) {
            if (other == MysqlxCrud.Delete.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (other.hasCriteria()) {
                  this.mergeCriteria(other.getCriteria());
               }

               if (other.hasLimit()) {
                  this.mergeLimit(other.getLimit());
               }

               if (this.orderBuilder_ == null) {
                  if (!other.order_.isEmpty()) {
                     if (this.order_.isEmpty()) {
                        this.order_ = other.order_;
                        this.bitField0_ &= -17;
                     } else {
                        this.ensureOrderIsMutable();
                        this.order_.addAll(other.order_);
                     }

                     this.onChanged();
                  }
               } else if (!other.order_.isEmpty()) {
                  if (this.orderBuilder_.isEmpty()) {
                     this.orderBuilder_.dispose();
                     this.orderBuilder_ = null;
                     this.order_ = other.order_;
                     this.bitField0_ &= -17;
                     this.orderBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? this.getOrderFieldBuilder() : null;
                  } else {
                     this.orderBuilder_.addAllMessages(other.order_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -33;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -33;
                     this.argsBuilder_ = MysqlxCrud.Delete.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasLimitExpr()) {
                  this.mergeLimitExpr(other.getLimitExpr());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
               return false;
            } else {
               int i;
               for(i = 0; i < this.getOrderCount(); ++i) {
                  if (!this.getOrder(i).isInitialized()) {
                     return false;
                  }
               }

               for(i = 0; i < this.getArgsCount(); ++i) {
                  if (!this.getArgs(i).isInitialized()) {
                     return false;
                  }
               }

               if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
                  return false;
               } else {
                  return true;
               }
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Delete parsedMessage = null;

            try {
               parsedMessage = (Delete)MysqlxCrud.Delete.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Delete)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         public Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return (Collection)this.collectionBuilder_.getMessage();
            }
         }

         public Builder setCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCollection(Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Collection.Builder)this.getCollectionFieldBuilder().getBuilder();
         }

         public CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return (CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         public DataModel getDataModel() {
            DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public Builder setDataModel(DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasCriteria() {
            return (this.bitField0_ & 4) != 0;
         }

         public MysqlxExpr.Expr getCriteria() {
            if (this.criteriaBuilder_ == null) {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            } else {
               return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage();
            }
         }

         public Builder setCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.criteria_ = value;
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder mergeCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
               } else {
                  this.criteria_ = value;
               }

               this.onChanged();
            } else {
               this.criteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder clearCriteria() {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
               this.onChanged();
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
            this.bitField0_ |= 4;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getCriteriaFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
            if (this.criteriaBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
            if (this.criteriaBuilder_ == null) {
               this.criteriaBuilder_ = new SingleFieldBuilderV3(this.getCriteria(), this.getParentForChildren(), this.isClean());
               this.criteria_ = null;
            }

            return this.criteriaBuilder_;
         }

         public boolean hasLimit() {
            return (this.bitField0_ & 8) != 0;
         }

         public Limit getLimit() {
            if (this.limitBuilder_ == null) {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            } else {
               return (Limit)this.limitBuilder_.getMessage();
            }
         }

         public Builder setLimit(Limit value) {
            if (this.limitBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limit_ = value;
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder setLimit(Limit.Builder builderForValue) {
            if (this.limitBuilder_ == null) {
               this.limit_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder mergeLimit(Limit value) {
            if (this.limitBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
                  this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
               } else {
                  this.limit_ = value;
               }

               this.onChanged();
            } else {
               this.limitBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder clearLimit() {
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
               this.onChanged();
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         public Limit.Builder getLimitBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return (Limit.Builder)this.getLimitFieldBuilder().getBuilder();
         }

         public LimitOrBuilder getLimitOrBuilder() {
            if (this.limitBuilder_ != null) {
               return (LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder();
            } else {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            }
         }

         private SingleFieldBuilderV3<Limit, Limit.Builder, LimitOrBuilder> getLimitFieldBuilder() {
            if (this.limitBuilder_ == null) {
               this.limitBuilder_ = new SingleFieldBuilderV3(this.getLimit(), this.getParentForChildren(), this.isClean());
               this.limit_ = null;
            }

            return this.limitBuilder_;
         }

         private void ensureOrderIsMutable() {
            if ((this.bitField0_ & 16) == 0) {
               this.order_ = new ArrayList(this.order_);
               this.bitField0_ |= 16;
            }

         }

         public List<Order> getOrderList() {
            return this.orderBuilder_ == null ? Collections.unmodifiableList(this.order_) : this.orderBuilder_.getMessageList();
         }

         public int getOrderCount() {
            return this.orderBuilder_ == null ? this.order_.size() : this.orderBuilder_.getCount();
         }

         public Order getOrder(int index) {
            return this.orderBuilder_ == null ? (Order)this.order_.get(index) : (Order)this.orderBuilder_.getMessage(index);
         }

         public Builder setOrder(int index, Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.set(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setOrder(int index, Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addOrder(Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addOrder(int index, Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addOrder(Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addOrder(int index, Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllOrder(Iterable<? extends Order> values) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.order_);
               this.onChanged();
            } else {
               this.orderBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearOrder() {
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
               this.onChanged();
            } else {
               this.orderBuilder_.clear();
            }

            return this;
         }

         public Builder removeOrder(int index) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.remove(index);
               this.onChanged();
            } else {
               this.orderBuilder_.remove(index);
            }

            return this;
         }

         public Order.Builder getOrderBuilder(int index) {
            return (Order.Builder)this.getOrderFieldBuilder().getBuilder(index);
         }

         public OrderOrBuilder getOrderOrBuilder(int index) {
            return this.orderBuilder_ == null ? (OrderOrBuilder)this.order_.get(index) : (OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends OrderOrBuilder> getOrderOrBuilderList() {
            return this.orderBuilder_ != null ? this.orderBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.order_);
         }

         public Order.Builder addOrderBuilder() {
            return (Order.Builder)this.getOrderFieldBuilder().addBuilder(MysqlxCrud.Order.getDefaultInstance());
         }

         public Order.Builder addOrderBuilder(int index) {
            return (Order.Builder)this.getOrderFieldBuilder().addBuilder(index, MysqlxCrud.Order.getDefaultInstance());
         }

         public List<Order.Builder> getOrderBuilderList() {
            return this.getOrderFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Order, Order.Builder, OrderOrBuilder> getOrderFieldBuilder() {
            if (this.orderBuilder_ == null) {
               this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, (this.bitField0_ & 16) != 0, this.getParentForChildren(), this.isClean());
               this.order_ = null;
            }

            return this.orderBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.args_ = new ArrayList(this.args_);
               this.bitField0_ |= 32;
            }

         }

         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.Scalar)this.args_.get(index) : (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -33;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().getBuilder(index);
         }

         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index) : (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, (this.bitField0_ & 32) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         public boolean hasLimitExpr() {
            return (this.bitField0_ & 64) != 0;
         }

         public LimitExpr getLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            } else {
               return (LimitExpr)this.limitExprBuilder_.getMessage();
            }
         }

         public Builder setLimitExpr(LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limitExpr_ = value;
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder setLimitExpr(LimitExpr.Builder builderForValue) {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder mergeLimitExpr(LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0 && this.limitExpr_ != null && this.limitExpr_ != MysqlxCrud.LimitExpr.getDefaultInstance()) {
                  this.limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
               } else {
                  this.limitExpr_ = value;
               }

               this.onChanged();
            } else {
               this.limitExprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 64;
            return this;
         }

         public Builder clearLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
               this.onChanged();
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -65;
            return this;
         }

         public LimitExpr.Builder getLimitExprBuilder() {
            this.bitField0_ |= 64;
            this.onChanged();
            return (LimitExpr.Builder)this.getLimitExprFieldBuilder().getBuilder();
         }

         public LimitExprOrBuilder getLimitExprOrBuilder() {
            if (this.limitExprBuilder_ != null) {
               return (LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder();
            } else {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            }
         }

         private SingleFieldBuilderV3<LimitExpr, LimitExpr.Builder, LimitExprOrBuilder> getLimitExprFieldBuilder() {
            if (this.limitExprBuilder_ == null) {
               this.limitExprBuilder_ = new SingleFieldBuilderV3(this.getLimitExpr(), this.getParentForChildren(), this.isClean());
               this.limitExpr_ = null;
            }

            return this.limitExprBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface DeleteOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      Collection getCollection();

      CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      DataModel getDataModel();

      boolean hasCriteria();

      MysqlxExpr.Expr getCriteria();

      MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();

      boolean hasLimit();

      Limit getLimit();

      LimitOrBuilder getLimitOrBuilder();

      List<Order> getOrderList();

      Order getOrder(int var1);

      int getOrderCount();

      List<? extends OrderOrBuilder> getOrderOrBuilderList();

      OrderOrBuilder getOrderOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasLimitExpr();

      LimitExpr getLimitExpr();

      LimitExprOrBuilder getLimitExprOrBuilder();
   }

   public static final class Update extends GeneratedMessageV3 implements UpdateOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 2;
      private Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 3;
      private int dataModel_;
      public static final int CRITERIA_FIELD_NUMBER = 4;
      private MysqlxExpr.Expr criteria_;
      public static final int LIMIT_FIELD_NUMBER = 5;
      private Limit limit_;
      public static final int ORDER_FIELD_NUMBER = 6;
      private List<Order> order_;
      public static final int OPERATION_FIELD_NUMBER = 7;
      private List<UpdateOperation> operation_;
      public static final int ARGS_FIELD_NUMBER = 8;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int LIMIT_EXPR_FIELD_NUMBER = 9;
      private LimitExpr limitExpr_;
      private byte memoizedIsInitialized;
      private static final Update DEFAULT_INSTANCE = new Update();
      /** @deprecated */
      @Deprecated
      public static final Parser<Update> PARSER = new AbstractParser<Update>() {
         public Update parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Update(input, extensionRegistry);
         }
      };

      private Update(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Update() {
         this.memoizedIsInitialized = -1;
         this.dataModel_ = 1;
         this.order_ = Collections.emptyList();
         this.operation_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Update();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Update(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 18:
                        Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = (Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 24:
                        int rawValue = input.readEnum();
                        DataModel value = MysqlxCrud.DataModel.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(3, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValue;
                        }
                        break;
                     case 34:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilder = this.criteria_.toBuilder();
                        }

                        this.criteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.criteria_);
                           this.criteria_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     case 42:
                        Limit.Builder subBuilder = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilder = this.limit_.toBuilder();
                        }

                        this.limit_ = (Limit)input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limit_);
                           this.limit_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 50:
                        if ((mutable_bitField0_ & 16) == 0) {
                           this.order_ = new ArrayList();
                           mutable_bitField0_ |= 16;
                        }

                        this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry));
                        break;
                     case 58:
                        if ((mutable_bitField0_ & 32) == 0) {
                           this.operation_ = new ArrayList();
                           mutable_bitField0_ |= 32;
                        }

                        this.operation_.add(input.readMessage(MysqlxCrud.UpdateOperation.PARSER, extensionRegistry));
                        break;
                     case 66:
                        if ((mutable_bitField0_ & 64) == 0) {
                           this.args_ = new ArrayList();
                           mutable_bitField0_ |= 64;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 74:
                        LimitExpr.Builder subBuilder = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilder = this.limitExpr_.toBuilder();
                        }

                        this.limitExpr_ = (LimitExpr)input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limitExpr_);
                           this.limitExpr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
               }

               if ((mutable_bitField0_ & 32) != 0) {
                  this.operation_ = Collections.unmodifiableList(this.operation_);
               }

               if ((mutable_bitField0_ & 64) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable.ensureFieldAccessorsInitialized(Update.class, Builder.class);
      }

      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      public Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      public DataModel getDataModel() {
         DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      public boolean hasCriteria() {
         return (this.bitField0_ & 4) != 0;
      }

      public MysqlxExpr.Expr getCriteria() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      public boolean hasLimit() {
         return (this.bitField0_ & 8) != 0;
      }

      public Limit getLimit() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      public LimitOrBuilder getLimitOrBuilder() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      public List<Order> getOrderList() {
         return this.order_;
      }

      public List<? extends OrderOrBuilder> getOrderOrBuilderList() {
         return this.order_;
      }

      public int getOrderCount() {
         return this.order_.size();
      }

      public Order getOrder(int index) {
         return (Order)this.order_.get(index);
      }

      public OrderOrBuilder getOrderOrBuilder(int index) {
         return (OrderOrBuilder)this.order_.get(index);
      }

      public List<UpdateOperation> getOperationList() {
         return this.operation_;
      }

      public List<? extends UpdateOperationOrBuilder> getOperationOrBuilderList() {
         return this.operation_;
      }

      public int getOperationCount() {
         return this.operation_.size();
      }

      public UpdateOperation getOperation(int index) {
         return (UpdateOperation)this.operation_.get(index);
      }

      public UpdateOperationOrBuilder getOperationOrBuilder(int index) {
         return (UpdateOperationOrBuilder)this.operation_.get(index);
      }

      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      public int getArgsCount() {
         return this.args_.size();
      }

      public MysqlxDatatypes.Scalar getArgs(int index) {
         return (MysqlxDatatypes.Scalar)this.args_.get(index);
      }

      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index);
      }

      public boolean hasLimitExpr() {
         return (this.bitField0_ & 16) != 0;
      }

      public LimitExpr getLimitExpr() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      public LimitExprOrBuilder getLimitExprOrBuilder() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            int i;
            for(i = 0; i < this.getOrderCount(); ++i) {
               if (!this.getOrder(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for(i = 0; i < this.getOperationCount(); ++i) {
               if (!this.getOperation(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for(i = 0; i < this.getArgsCount(); ++i) {
               if (!this.getArgs(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(2, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(3, this.dataModel_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(4, this.getCriteria());
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(5, this.getLimit());
         }

         int i;
         for(i = 0; i < this.order_.size(); ++i) {
            output.writeMessage(6, (MessageLite)this.order_.get(i));
         }

         for(i = 0; i < this.operation_.size(); ++i) {
            output.writeMessage(7, (MessageLite)this.operation_.get(i));
         }

         for(i = 0; i < this.args_.size(); ++i) {
            output.writeMessage(8, (MessageLite)this.args_.get(i));
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(9, this.getLimitExpr());
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.dataModel_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getCriteria());
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(5, this.getLimit());
            }

            int i;
            for(i = 0; i < this.order_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(6, (MessageLite)this.order_.get(i));
            }

            for(i = 0; i < this.operation_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(7, (MessageLite)this.operation_.get(i));
            }

            for(i = 0; i < this.args_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(8, (MessageLite)this.args_.get(i));
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(9, this.getLimitExpr());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Update)) {
            return super.equals(obj);
         } else {
            Update other = (Update)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (this.hasCriteria() != other.hasCriteria()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().equals(other.getCriteria())) {
               return false;
            } else if (this.hasLimit() != other.hasLimit()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().equals(other.getLimit())) {
               return false;
            } else if (!this.getOrderList().equals(other.getOrderList())) {
               return false;
            } else if (!this.getOperationList().equals(other.getOperationList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasLimitExpr() != other.hasLimitExpr()) {
               return false;
            } else if (this.hasLimitExpr() && !this.getLimitExpr().equals(other.getLimitExpr())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.hasCriteria()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getCriteria().hashCode();
            }

            if (this.hasLimit()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getLimit().hashCode();
            }

            if (this.getOrderCount() > 0) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getOrderList().hashCode();
            }

            if (this.getOperationCount() > 0) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getOperationList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 8;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasLimitExpr()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getLimitExpr().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Update parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Update)PARSER.parseFrom(data);
      }

      public static Update parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Update)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Update parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Update)PARSER.parseFrom(data);
      }

      public static Update parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Update)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Update parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Update)PARSER.parseFrom(data);
      }

      public static Update parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Update)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Update parseFrom(InputStream input) throws IOException {
         return (Update)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Update parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Update)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Update parseDelimitedFrom(InputStream input) throws IOException {
         return (Update)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Update parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Update)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Update parseFrom(CodedInputStream input) throws IOException {
         return (Update)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Update parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Update)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Update prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Update getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Update> parser() {
         return PARSER;
      }

      public Parser<Update> getParserForType() {
         return PARSER;
      }

      public Update getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Update(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Update(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements UpdateOrBuilder {
         private int bitField0_;
         private Collection collection_;
         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> collectionBuilder_;
         private int dataModel_;
         private MysqlxExpr.Expr criteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
         private Limit limit_;
         private SingleFieldBuilderV3<Limit, Limit.Builder, LimitOrBuilder> limitBuilder_;
         private List<Order> order_;
         private RepeatedFieldBuilderV3<Order, Order.Builder, OrderOrBuilder> orderBuilder_;
         private List<UpdateOperation> operation_;
         private RepeatedFieldBuilderV3<UpdateOperation, UpdateOperation.Builder, UpdateOperationOrBuilder> operationBuilder_;
         private List<MysqlxDatatypes.Scalar> args_;
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private LimitExpr limitExpr_;
         private SingleFieldBuilderV3<LimitExpr, LimitExpr.Builder, LimitExprOrBuilder> limitExprBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Update_fieldAccessorTable.ensureFieldAccessorsInitialized(Update.class, Builder.class);
         }

         private Builder() {
            this.dataModel_ = 1;
            this.order_ = Collections.emptyList();
            this.operation_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.dataModel_ = 1;
            this.order_ = Collections.emptyList();
            this.operation_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Update.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getCriteriaFieldBuilder();
               this.getLimitFieldBuilder();
               this.getOrderFieldBuilder();
               this.getOperationFieldBuilder();
               this.getArgsFieldBuilder();
               this.getLimitExprFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
            } else {
               this.orderBuilder_.clear();
            }

            if (this.operationBuilder_ == null) {
               this.operation_ = Collections.emptyList();
               this.bitField0_ &= -33;
            } else {
               this.operationBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -65;
            } else {
               this.argsBuilder_.clear();
            }

            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -129;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Update_descriptor;
         }

         public Update getDefaultInstanceForType() {
            return MysqlxCrud.Update.getDefaultInstance();
         }

         public Update build() {
            Update result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Update buildPartial() {
            Update result = new Update(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = (Collection)this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if ((from_bitField0_ & 4) != 0) {
               if (this.criteriaBuilder_ == null) {
                  result.criteria_ = this.criteria_;
               } else {
                  result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 8) != 0) {
               if (this.limitBuilder_ == null) {
                  result.limit_ = this.limit_;
               } else {
                  result.limit_ = (Limit)this.limitBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if (this.orderBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
                  this.bitField0_ &= -17;
               }

               result.order_ = this.order_;
            } else {
               result.order_ = this.orderBuilder_.build();
            }

            if (this.operationBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0) {
                  this.operation_ = Collections.unmodifiableList(this.operation_);
                  this.bitField0_ &= -33;
               }

               result.operation_ = this.operation_;
            } else {
               result.operation_ = this.operationBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -65;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 128) != 0) {
               if (this.limitExprBuilder_ == null) {
                  result.limitExpr_ = this.limitExpr_;
               } else {
                  result.limitExpr_ = (LimitExpr)this.limitExprBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Update) {
               return this.mergeFrom((Update)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Update other) {
            if (other == MysqlxCrud.Update.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (other.hasCriteria()) {
                  this.mergeCriteria(other.getCriteria());
               }

               if (other.hasLimit()) {
                  this.mergeLimit(other.getLimit());
               }

               if (this.orderBuilder_ == null) {
                  if (!other.order_.isEmpty()) {
                     if (this.order_.isEmpty()) {
                        this.order_ = other.order_;
                        this.bitField0_ &= -17;
                     } else {
                        this.ensureOrderIsMutable();
                        this.order_.addAll(other.order_);
                     }

                     this.onChanged();
                  }
               } else if (!other.order_.isEmpty()) {
                  if (this.orderBuilder_.isEmpty()) {
                     this.orderBuilder_.dispose();
                     this.orderBuilder_ = null;
                     this.order_ = other.order_;
                     this.bitField0_ &= -17;
                     this.orderBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? this.getOrderFieldBuilder() : null;
                  } else {
                     this.orderBuilder_.addAllMessages(other.order_);
                  }
               }

               if (this.operationBuilder_ == null) {
                  if (!other.operation_.isEmpty()) {
                     if (this.operation_.isEmpty()) {
                        this.operation_ = other.operation_;
                        this.bitField0_ &= -33;
                     } else {
                        this.ensureOperationIsMutable();
                        this.operation_.addAll(other.operation_);
                     }

                     this.onChanged();
                  }
               } else if (!other.operation_.isEmpty()) {
                  if (this.operationBuilder_.isEmpty()) {
                     this.operationBuilder_.dispose();
                     this.operationBuilder_ = null;
                     this.operation_ = other.operation_;
                     this.bitField0_ &= -33;
                     this.operationBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? this.getOperationFieldBuilder() : null;
                  } else {
                     this.operationBuilder_.addAllMessages(other.operation_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -65;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -65;
                     this.argsBuilder_ = MysqlxCrud.Update.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasLimitExpr()) {
                  this.mergeLimitExpr(other.getLimitExpr());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
               return false;
            } else {
               int i;
               for(i = 0; i < this.getOrderCount(); ++i) {
                  if (!this.getOrder(i).isInitialized()) {
                     return false;
                  }
               }

               for(i = 0; i < this.getOperationCount(); ++i) {
                  if (!this.getOperation(i).isInitialized()) {
                     return false;
                  }
               }

               for(i = 0; i < this.getArgsCount(); ++i) {
                  if (!this.getArgs(i).isInitialized()) {
                     return false;
                  }
               }

               if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
                  return false;
               } else {
                  return true;
               }
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Update parsedMessage = null;

            try {
               parsedMessage = (Update)MysqlxCrud.Update.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Update)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         public Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return (Collection)this.collectionBuilder_.getMessage();
            }
         }

         public Builder setCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCollection(Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Collection.Builder)this.getCollectionFieldBuilder().getBuilder();
         }

         public CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return (CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         public DataModel getDataModel() {
            DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public Builder setDataModel(DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasCriteria() {
            return (this.bitField0_ & 4) != 0;
         }

         public MysqlxExpr.Expr getCriteria() {
            if (this.criteriaBuilder_ == null) {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            } else {
               return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage();
            }
         }

         public Builder setCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.criteria_ = value;
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder mergeCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
               } else {
                  this.criteria_ = value;
               }

               this.onChanged();
            } else {
               this.criteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder clearCriteria() {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
               this.onChanged();
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
            this.bitField0_ |= 4;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getCriteriaFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
            if (this.criteriaBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
            if (this.criteriaBuilder_ == null) {
               this.criteriaBuilder_ = new SingleFieldBuilderV3(this.getCriteria(), this.getParentForChildren(), this.isClean());
               this.criteria_ = null;
            }

            return this.criteriaBuilder_;
         }

         public boolean hasLimit() {
            return (this.bitField0_ & 8) != 0;
         }

         public Limit getLimit() {
            if (this.limitBuilder_ == null) {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            } else {
               return (Limit)this.limitBuilder_.getMessage();
            }
         }

         public Builder setLimit(Limit value) {
            if (this.limitBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limit_ = value;
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder setLimit(Limit.Builder builderForValue) {
            if (this.limitBuilder_ == null) {
               this.limit_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder mergeLimit(Limit value) {
            if (this.limitBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
                  this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
               } else {
                  this.limit_ = value;
               }

               this.onChanged();
            } else {
               this.limitBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder clearLimit() {
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
               this.onChanged();
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         public Limit.Builder getLimitBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return (Limit.Builder)this.getLimitFieldBuilder().getBuilder();
         }

         public LimitOrBuilder getLimitOrBuilder() {
            if (this.limitBuilder_ != null) {
               return (LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder();
            } else {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            }
         }

         private SingleFieldBuilderV3<Limit, Limit.Builder, LimitOrBuilder> getLimitFieldBuilder() {
            if (this.limitBuilder_ == null) {
               this.limitBuilder_ = new SingleFieldBuilderV3(this.getLimit(), this.getParentForChildren(), this.isClean());
               this.limit_ = null;
            }

            return this.limitBuilder_;
         }

         private void ensureOrderIsMutable() {
            if ((this.bitField0_ & 16) == 0) {
               this.order_ = new ArrayList(this.order_);
               this.bitField0_ |= 16;
            }

         }

         public List<Order> getOrderList() {
            return this.orderBuilder_ == null ? Collections.unmodifiableList(this.order_) : this.orderBuilder_.getMessageList();
         }

         public int getOrderCount() {
            return this.orderBuilder_ == null ? this.order_.size() : this.orderBuilder_.getCount();
         }

         public Order getOrder(int index) {
            return this.orderBuilder_ == null ? (Order)this.order_.get(index) : (Order)this.orderBuilder_.getMessage(index);
         }

         public Builder setOrder(int index, Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.set(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setOrder(int index, Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addOrder(Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addOrder(int index, Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addOrder(Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addOrder(int index, Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllOrder(Iterable<? extends Order> values) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.order_);
               this.onChanged();
            } else {
               this.orderBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearOrder() {
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -17;
               this.onChanged();
            } else {
               this.orderBuilder_.clear();
            }

            return this;
         }

         public Builder removeOrder(int index) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.remove(index);
               this.onChanged();
            } else {
               this.orderBuilder_.remove(index);
            }

            return this;
         }

         public Order.Builder getOrderBuilder(int index) {
            return (Order.Builder)this.getOrderFieldBuilder().getBuilder(index);
         }

         public OrderOrBuilder getOrderOrBuilder(int index) {
            return this.orderBuilder_ == null ? (OrderOrBuilder)this.order_.get(index) : (OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends OrderOrBuilder> getOrderOrBuilderList() {
            return this.orderBuilder_ != null ? this.orderBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.order_);
         }

         public Order.Builder addOrderBuilder() {
            return (Order.Builder)this.getOrderFieldBuilder().addBuilder(MysqlxCrud.Order.getDefaultInstance());
         }

         public Order.Builder addOrderBuilder(int index) {
            return (Order.Builder)this.getOrderFieldBuilder().addBuilder(index, MysqlxCrud.Order.getDefaultInstance());
         }

         public List<Order.Builder> getOrderBuilderList() {
            return this.getOrderFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Order, Order.Builder, OrderOrBuilder> getOrderFieldBuilder() {
            if (this.orderBuilder_ == null) {
               this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, (this.bitField0_ & 16) != 0, this.getParentForChildren(), this.isClean());
               this.order_ = null;
            }

            return this.orderBuilder_;
         }

         private void ensureOperationIsMutable() {
            if ((this.bitField0_ & 32) == 0) {
               this.operation_ = new ArrayList(this.operation_);
               this.bitField0_ |= 32;
            }

         }

         public List<UpdateOperation> getOperationList() {
            return this.operationBuilder_ == null ? Collections.unmodifiableList(this.operation_) : this.operationBuilder_.getMessageList();
         }

         public int getOperationCount() {
            return this.operationBuilder_ == null ? this.operation_.size() : this.operationBuilder_.getCount();
         }

         public UpdateOperation getOperation(int index) {
            return this.operationBuilder_ == null ? (UpdateOperation)this.operation_.get(index) : (UpdateOperation)this.operationBuilder_.getMessage(index);
         }

         public Builder setOperation(int index, UpdateOperation value) {
            if (this.operationBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOperationIsMutable();
               this.operation_.set(index, value);
               this.onChanged();
            } else {
               this.operationBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setOperation(int index, UpdateOperation.Builder builderForValue) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.operationBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addOperation(UpdateOperation value) {
            if (this.operationBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOperationIsMutable();
               this.operation_.add(value);
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addOperation(int index, UpdateOperation value) {
            if (this.operationBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOperationIsMutable();
               this.operation_.add(index, value);
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addOperation(UpdateOperation.Builder builderForValue) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addOperation(int index, UpdateOperation.Builder builderForValue) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.operationBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllOperation(Iterable<? extends UpdateOperation> values) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.operation_);
               this.onChanged();
            } else {
               this.operationBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearOperation() {
            if (this.operationBuilder_ == null) {
               this.operation_ = Collections.emptyList();
               this.bitField0_ &= -33;
               this.onChanged();
            } else {
               this.operationBuilder_.clear();
            }

            return this;
         }

         public Builder removeOperation(int index) {
            if (this.operationBuilder_ == null) {
               this.ensureOperationIsMutable();
               this.operation_.remove(index);
               this.onChanged();
            } else {
               this.operationBuilder_.remove(index);
            }

            return this;
         }

         public UpdateOperation.Builder getOperationBuilder(int index) {
            return (UpdateOperation.Builder)this.getOperationFieldBuilder().getBuilder(index);
         }

         public UpdateOperationOrBuilder getOperationOrBuilder(int index) {
            return this.operationBuilder_ == null ? (UpdateOperationOrBuilder)this.operation_.get(index) : (UpdateOperationOrBuilder)this.operationBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends UpdateOperationOrBuilder> getOperationOrBuilderList() {
            return this.operationBuilder_ != null ? this.operationBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.operation_);
         }

         public UpdateOperation.Builder addOperationBuilder() {
            return (UpdateOperation.Builder)this.getOperationFieldBuilder().addBuilder(MysqlxCrud.UpdateOperation.getDefaultInstance());
         }

         public UpdateOperation.Builder addOperationBuilder(int index) {
            return (UpdateOperation.Builder)this.getOperationFieldBuilder().addBuilder(index, MysqlxCrud.UpdateOperation.getDefaultInstance());
         }

         public List<UpdateOperation.Builder> getOperationBuilderList() {
            return this.getOperationFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<UpdateOperation, UpdateOperation.Builder, UpdateOperationOrBuilder> getOperationFieldBuilder() {
            if (this.operationBuilder_ == null) {
               this.operationBuilder_ = new RepeatedFieldBuilderV3(this.operation_, (this.bitField0_ & 32) != 0, this.getParentForChildren(), this.isClean());
               this.operation_ = null;
            }

            return this.operationBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 64) == 0) {
               this.args_ = new ArrayList(this.args_);
               this.bitField0_ |= 64;
            }

         }

         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.Scalar)this.args_.get(index) : (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -65;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().getBuilder(index);
         }

         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index) : (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, (this.bitField0_ & 64) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         public boolean hasLimitExpr() {
            return (this.bitField0_ & 128) != 0;
         }

         public LimitExpr getLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            } else {
               return (LimitExpr)this.limitExprBuilder_.getMessage();
            }
         }

         public Builder setLimitExpr(LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limitExpr_ = value;
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public Builder setLimitExpr(LimitExpr.Builder builderForValue) {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 128;
            return this;
         }

         public Builder mergeLimitExpr(LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if ((this.bitField0_ & 128) != 0 && this.limitExpr_ != null && this.limitExpr_ != MysqlxCrud.LimitExpr.getDefaultInstance()) {
                  this.limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
               } else {
                  this.limitExpr_ = value;
               }

               this.onChanged();
            } else {
               this.limitExprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public Builder clearLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
               this.onChanged();
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -129;
            return this;
         }

         public LimitExpr.Builder getLimitExprBuilder() {
            this.bitField0_ |= 128;
            this.onChanged();
            return (LimitExpr.Builder)this.getLimitExprFieldBuilder().getBuilder();
         }

         public LimitExprOrBuilder getLimitExprOrBuilder() {
            if (this.limitExprBuilder_ != null) {
               return (LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder();
            } else {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            }
         }

         private SingleFieldBuilderV3<LimitExpr, LimitExpr.Builder, LimitExprOrBuilder> getLimitExprFieldBuilder() {
            if (this.limitExprBuilder_ == null) {
               this.limitExprBuilder_ = new SingleFieldBuilderV3(this.getLimitExpr(), this.getParentForChildren(), this.isClean());
               this.limitExpr_ = null;
            }

            return this.limitExprBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface UpdateOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      Collection getCollection();

      CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      DataModel getDataModel();

      boolean hasCriteria();

      MysqlxExpr.Expr getCriteria();

      MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();

      boolean hasLimit();

      Limit getLimit();

      LimitOrBuilder getLimitOrBuilder();

      List<Order> getOrderList();

      Order getOrder(int var1);

      int getOrderCount();

      List<? extends OrderOrBuilder> getOrderOrBuilderList();

      OrderOrBuilder getOrderOrBuilder(int var1);

      List<UpdateOperation> getOperationList();

      UpdateOperation getOperation(int var1);

      int getOperationCount();

      List<? extends UpdateOperationOrBuilder> getOperationOrBuilderList();

      UpdateOperationOrBuilder getOperationOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasLimitExpr();

      LimitExpr getLimitExpr();

      LimitExprOrBuilder getLimitExprOrBuilder();
   }

   public static final class Insert extends GeneratedMessageV3 implements InsertOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 1;
      private Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 2;
      private int dataModel_;
      public static final int PROJECTION_FIELD_NUMBER = 3;
      private List<Column> projection_;
      public static final int ROW_FIELD_NUMBER = 4;
      private List<TypedRow> row_;
      public static final int ARGS_FIELD_NUMBER = 5;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int UPSERT_FIELD_NUMBER = 6;
      private boolean upsert_;
      private byte memoizedIsInitialized;
      private static final Insert DEFAULT_INSTANCE = new Insert();
      /** @deprecated */
      @Deprecated
      public static final Parser<Insert> PARSER = new AbstractParser<Insert>() {
         public Insert parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Insert(input, extensionRegistry);
         }
      };

      private Insert(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Insert() {
         this.memoizedIsInitialized = -1;
         this.dataModel_ = 1;
         this.projection_ = Collections.emptyList();
         this.row_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Insert();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Insert(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = (Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        DataModel value = MysqlxCrud.DataModel.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValue;
                        }
                        break;
                     case 26:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.projection_ = new ArrayList();
                           mutable_bitField0_ |= 4;
                        }

                        this.projection_.add(input.readMessage(MysqlxCrud.Column.PARSER, extensionRegistry));
                        break;
                     case 34:
                        if ((mutable_bitField0_ & 8) == 0) {
                           this.row_ = new ArrayList();
                           mutable_bitField0_ |= 8;
                        }

                        this.row_.add(input.readMessage(MysqlxCrud.Insert.TypedRow.PARSER, extensionRegistry));
                        break;
                     case 42:
                        if ((mutable_bitField0_ & 16) == 0) {
                           this.args_ = new ArrayList();
                           mutable_bitField0_ |= 16;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 48:
                        this.bitField0_ |= 4;
                        this.upsert_ = input.readBool();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
               }

               if ((mutable_bitField0_ & 8) != 0) {
                  this.row_ = Collections.unmodifiableList(this.row_);
               }

               if ((mutable_bitField0_ & 16) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable.ensureFieldAccessorsInitialized(Insert.class, Builder.class);
      }

      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      public Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      public DataModel getDataModel() {
         DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      public List<Column> getProjectionList() {
         return this.projection_;
      }

      public List<? extends ColumnOrBuilder> getProjectionOrBuilderList() {
         return this.projection_;
      }

      public int getProjectionCount() {
         return this.projection_.size();
      }

      public Column getProjection(int index) {
         return (Column)this.projection_.get(index);
      }

      public ColumnOrBuilder getProjectionOrBuilder(int index) {
         return (ColumnOrBuilder)this.projection_.get(index);
      }

      public List<TypedRow> getRowList() {
         return this.row_;
      }

      public List<? extends TypedRowOrBuilder> getRowOrBuilderList() {
         return this.row_;
      }

      public int getRowCount() {
         return this.row_.size();
      }

      public TypedRow getRow(int index) {
         return (TypedRow)this.row_.get(index);
      }

      public TypedRowOrBuilder getRowOrBuilder(int index) {
         return (TypedRowOrBuilder)this.row_.get(index);
      }

      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      public int getArgsCount() {
         return this.args_.size();
      }

      public MysqlxDatatypes.Scalar getArgs(int index) {
         return (MysqlxDatatypes.Scalar)this.args_.get(index);
      }

      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index);
      }

      public boolean hasUpsert() {
         return (this.bitField0_ & 4) != 0;
      }

      public boolean getUpsert() {
         return this.upsert_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            int i;
            for(i = 0; i < this.getProjectionCount(); ++i) {
               if (!this.getProjection(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for(i = 0; i < this.getRowCount(); ++i) {
               if (!this.getRow(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for(i = 0; i < this.getArgsCount(); ++i) {
               if (!this.getArgs(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.dataModel_);
         }

         int i;
         for(i = 0; i < this.projection_.size(); ++i) {
            output.writeMessage(3, (MessageLite)this.projection_.get(i));
         }

         for(i = 0; i < this.row_.size(); ++i) {
            output.writeMessage(4, (MessageLite)this.row_.get(i));
         }

         for(i = 0; i < this.args_.size(); ++i) {
            output.writeMessage(5, (MessageLite)this.args_.get(i));
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBool(6, this.upsert_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.dataModel_);
            }

            int i;
            for(i = 0; i < this.projection_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(3, (MessageLite)this.projection_.get(i));
            }

            for(i = 0; i < this.row_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(4, (MessageLite)this.row_.get(i));
            }

            for(i = 0; i < this.args_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(5, (MessageLite)this.args_.get(i));
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBoolSize(6, this.upsert_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Insert)) {
            return super.equals(obj);
         } else {
            Insert other = (Insert)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (!this.getProjectionList().equals(other.getProjectionList())) {
               return false;
            } else if (!this.getRowList().equals(other.getRowList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasUpsert() != other.hasUpsert()) {
               return false;
            } else if (this.hasUpsert() && this.getUpsert() != other.getUpsert()) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.getProjectionCount() > 0) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getProjectionList().hashCode();
            }

            if (this.getRowCount() > 0) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getRowList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasUpsert()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + Internal.hashBoolean(this.getUpsert());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Insert parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Insert)PARSER.parseFrom(data);
      }

      public static Insert parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Insert)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Insert parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Insert)PARSER.parseFrom(data);
      }

      public static Insert parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Insert)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Insert parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Insert)PARSER.parseFrom(data);
      }

      public static Insert parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Insert)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Insert parseFrom(InputStream input) throws IOException {
         return (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Insert parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Insert parseDelimitedFrom(InputStream input) throws IOException {
         return (Insert)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Insert parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Insert)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Insert parseFrom(CodedInputStream input) throws IOException {
         return (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Insert parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Insert)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Insert prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Insert getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Insert> parser() {
         return PARSER;
      }

      public Parser<Insert> getParserForType() {
         return PARSER;
      }

      public Insert getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Insert(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Insert(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements InsertOrBuilder {
         private int bitField0_;
         private Collection collection_;
         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> collectionBuilder_;
         private int dataModel_;
         private List<Column> projection_;
         private RepeatedFieldBuilderV3<Column, Column.Builder, ColumnOrBuilder> projectionBuilder_;
         private List<TypedRow> row_;
         private RepeatedFieldBuilderV3<TypedRow, TypedRow.Builder, TypedRowOrBuilder> rowBuilder_;
         private List<MysqlxDatatypes.Scalar> args_;
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private boolean upsert_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_fieldAccessorTable.ensureFieldAccessorsInitialized(Insert.class, Builder.class);
         }

         private Builder() {
            this.dataModel_ = 1;
            this.projection_ = Collections.emptyList();
            this.row_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.dataModel_ = 1;
            this.projection_ = Collections.emptyList();
            this.row_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Insert.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getProjectionFieldBuilder();
               this.getRowFieldBuilder();
               this.getArgsFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.projectionBuilder_.clear();
            }

            if (this.rowBuilder_ == null) {
               this.row_ = Collections.emptyList();
               this.bitField0_ &= -9;
            } else {
               this.rowBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -17;
            } else {
               this.argsBuilder_.clear();
            }

            this.upsert_ = false;
            this.bitField0_ &= -33;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_descriptor;
         }

         public Insert getDefaultInstanceForType() {
            return MysqlxCrud.Insert.getDefaultInstance();
         }

         public Insert build() {
            Insert result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Insert buildPartial() {
            Insert result = new Insert(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = (Collection)this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if (this.projectionBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
                  this.bitField0_ &= -5;
               }

               result.projection_ = this.projection_;
            } else {
               result.projection_ = this.projectionBuilder_.build();
            }

            if (this.rowBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0) {
                  this.row_ = Collections.unmodifiableList(this.row_);
                  this.bitField0_ &= -9;
               }

               result.row_ = this.row_;
            } else {
               result.row_ = this.rowBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -17;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 32) != 0) {
               result.upsert_ = this.upsert_;
               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Insert) {
               return this.mergeFrom((Insert)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Insert other) {
            if (other == MysqlxCrud.Insert.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (this.projectionBuilder_ == null) {
                  if (!other.projection_.isEmpty()) {
                     if (this.projection_.isEmpty()) {
                        this.projection_ = other.projection_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureProjectionIsMutable();
                        this.projection_.addAll(other.projection_);
                     }

                     this.onChanged();
                  }
               } else if (!other.projection_.isEmpty()) {
                  if (this.projectionBuilder_.isEmpty()) {
                     this.projectionBuilder_.dispose();
                     this.projectionBuilder_ = null;
                     this.projection_ = other.projection_;
                     this.bitField0_ &= -5;
                     this.projectionBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? this.getProjectionFieldBuilder() : null;
                  } else {
                     this.projectionBuilder_.addAllMessages(other.projection_);
                  }
               }

               if (this.rowBuilder_ == null) {
                  if (!other.row_.isEmpty()) {
                     if (this.row_.isEmpty()) {
                        this.row_ = other.row_;
                        this.bitField0_ &= -9;
                     } else {
                        this.ensureRowIsMutable();
                        this.row_.addAll(other.row_);
                     }

                     this.onChanged();
                  }
               } else if (!other.row_.isEmpty()) {
                  if (this.rowBuilder_.isEmpty()) {
                     this.rowBuilder_.dispose();
                     this.rowBuilder_ = null;
                     this.row_ = other.row_;
                     this.bitField0_ &= -9;
                     this.rowBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? this.getRowFieldBuilder() : null;
                  } else {
                     this.rowBuilder_.addAllMessages(other.row_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -17;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -17;
                     this.argsBuilder_ = MysqlxCrud.Insert.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasUpsert()) {
                  this.setUpsert(other.getUpsert());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else {
               int i;
               for(i = 0; i < this.getProjectionCount(); ++i) {
                  if (!this.getProjection(i).isInitialized()) {
                     return false;
                  }
               }

               for(i = 0; i < this.getRowCount(); ++i) {
                  if (!this.getRow(i).isInitialized()) {
                     return false;
                  }
               }

               for(i = 0; i < this.getArgsCount(); ++i) {
                  if (!this.getArgs(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Insert parsedMessage = null;

            try {
               parsedMessage = (Insert)MysqlxCrud.Insert.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Insert)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         public Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return (Collection)this.collectionBuilder_.getMessage();
            }
         }

         public Builder setCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCollection(Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Collection.Builder)this.getCollectionFieldBuilder().getBuilder();
         }

         public CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return (CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         public DataModel getDataModel() {
            DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public Builder setDataModel(DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureProjectionIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.projection_ = new ArrayList(this.projection_);
               this.bitField0_ |= 4;
            }

         }

         public List<Column> getProjectionList() {
            return this.projectionBuilder_ == null ? Collections.unmodifiableList(this.projection_) : this.projectionBuilder_.getMessageList();
         }

         public int getProjectionCount() {
            return this.projectionBuilder_ == null ? this.projection_.size() : this.projectionBuilder_.getCount();
         }

         public Column getProjection(int index) {
            return this.projectionBuilder_ == null ? (Column)this.projection_.get(index) : (Column)this.projectionBuilder_.getMessage(index);
         }

         public Builder setProjection(int index, Column value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.set(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setProjection(int index, Column.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addProjection(Column value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addProjection(int index, Column value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addProjection(Column.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addProjection(int index, Column.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllProjection(Iterable<? extends Column> values) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.projection_);
               this.onChanged();
            } else {
               this.projectionBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearProjection() {
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.projectionBuilder_.clear();
            }

            return this;
         }

         public Builder removeProjection(int index) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.remove(index);
               this.onChanged();
            } else {
               this.projectionBuilder_.remove(index);
            }

            return this;
         }

         public Column.Builder getProjectionBuilder(int index) {
            return (Column.Builder)this.getProjectionFieldBuilder().getBuilder(index);
         }

         public ColumnOrBuilder getProjectionOrBuilder(int index) {
            return this.projectionBuilder_ == null ? (ColumnOrBuilder)this.projection_.get(index) : (ColumnOrBuilder)this.projectionBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends ColumnOrBuilder> getProjectionOrBuilderList() {
            return this.projectionBuilder_ != null ? this.projectionBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.projection_);
         }

         public Column.Builder addProjectionBuilder() {
            return (Column.Builder)this.getProjectionFieldBuilder().addBuilder(MysqlxCrud.Column.getDefaultInstance());
         }

         public Column.Builder addProjectionBuilder(int index) {
            return (Column.Builder)this.getProjectionFieldBuilder().addBuilder(index, MysqlxCrud.Column.getDefaultInstance());
         }

         public List<Column.Builder> getProjectionBuilderList() {
            return this.getProjectionFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Column, Column.Builder, ColumnOrBuilder> getProjectionFieldBuilder() {
            if (this.projectionBuilder_ == null) {
               this.projectionBuilder_ = new RepeatedFieldBuilderV3(this.projection_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.projection_ = null;
            }

            return this.projectionBuilder_;
         }

         private void ensureRowIsMutable() {
            if ((this.bitField0_ & 8) == 0) {
               this.row_ = new ArrayList(this.row_);
               this.bitField0_ |= 8;
            }

         }

         public List<TypedRow> getRowList() {
            return this.rowBuilder_ == null ? Collections.unmodifiableList(this.row_) : this.rowBuilder_.getMessageList();
         }

         public int getRowCount() {
            return this.rowBuilder_ == null ? this.row_.size() : this.rowBuilder_.getCount();
         }

         public TypedRow getRow(int index) {
            return this.rowBuilder_ == null ? (TypedRow)this.row_.get(index) : (TypedRow)this.rowBuilder_.getMessage(index);
         }

         public Builder setRow(int index, TypedRow value) {
            if (this.rowBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureRowIsMutable();
               this.row_.set(index, value);
               this.onChanged();
            } else {
               this.rowBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setRow(int index, TypedRow.Builder builderForValue) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.rowBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addRow(TypedRow value) {
            if (this.rowBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureRowIsMutable();
               this.row_.add(value);
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addRow(int index, TypedRow value) {
            if (this.rowBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureRowIsMutable();
               this.row_.add(index, value);
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addRow(TypedRow.Builder builderForValue) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addRow(int index, TypedRow.Builder builderForValue) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.rowBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllRow(Iterable<? extends TypedRow> values) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.row_);
               this.onChanged();
            } else {
               this.rowBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearRow() {
            if (this.rowBuilder_ == null) {
               this.row_ = Collections.emptyList();
               this.bitField0_ &= -9;
               this.onChanged();
            } else {
               this.rowBuilder_.clear();
            }

            return this;
         }

         public Builder removeRow(int index) {
            if (this.rowBuilder_ == null) {
               this.ensureRowIsMutable();
               this.row_.remove(index);
               this.onChanged();
            } else {
               this.rowBuilder_.remove(index);
            }

            return this;
         }

         public TypedRow.Builder getRowBuilder(int index) {
            return (TypedRow.Builder)this.getRowFieldBuilder().getBuilder(index);
         }

         public TypedRowOrBuilder getRowOrBuilder(int index) {
            return this.rowBuilder_ == null ? (TypedRowOrBuilder)this.row_.get(index) : (TypedRowOrBuilder)this.rowBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends TypedRowOrBuilder> getRowOrBuilderList() {
            return this.rowBuilder_ != null ? this.rowBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.row_);
         }

         public TypedRow.Builder addRowBuilder() {
            return (TypedRow.Builder)this.getRowFieldBuilder().addBuilder(MysqlxCrud.Insert.TypedRow.getDefaultInstance());
         }

         public TypedRow.Builder addRowBuilder(int index) {
            return (TypedRow.Builder)this.getRowFieldBuilder().addBuilder(index, MysqlxCrud.Insert.TypedRow.getDefaultInstance());
         }

         public List<TypedRow.Builder> getRowBuilderList() {
            return this.getRowFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<TypedRow, TypedRow.Builder, TypedRowOrBuilder> getRowFieldBuilder() {
            if (this.rowBuilder_ == null) {
               this.rowBuilder_ = new RepeatedFieldBuilderV3(this.row_, (this.bitField0_ & 8) != 0, this.getParentForChildren(), this.isClean());
               this.row_ = null;
            }

            return this.rowBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 16) == 0) {
               this.args_ = new ArrayList(this.args_);
               this.bitField0_ |= 16;
            }

         }

         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.Scalar)this.args_.get(index) : (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -17;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().getBuilder(index);
         }

         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index) : (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, (this.bitField0_ & 16) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         public boolean hasUpsert() {
            return (this.bitField0_ & 32) != 0;
         }

         public boolean getUpsert() {
            return this.upsert_;
         }

         public Builder setUpsert(boolean value) {
            this.bitField0_ |= 32;
            this.upsert_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearUpsert() {
            this.bitField0_ &= -33;
            this.upsert_ = false;
            this.onChanged();
            return this;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }

      public static final class TypedRow extends GeneratedMessageV3 implements TypedRowOrBuilder {
         private static final long serialVersionUID = 0L;
         public static final int FIELD_FIELD_NUMBER = 1;
         private List<MysqlxExpr.Expr> field_;
         private byte memoizedIsInitialized;
         private static final TypedRow DEFAULT_INSTANCE = new TypedRow();
         /** @deprecated */
         @Deprecated
         public static final Parser<TypedRow> PARSER = new AbstractParser<TypedRow>() {
            public TypedRow parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new TypedRow(input, extensionRegistry);
            }
         };

         private TypedRow(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
         }

         private TypedRow() {
            this.memoizedIsInitialized = -1;
            this.field_ = Collections.emptyList();
         }

         protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new TypedRow();
         }

         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private TypedRow(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
               throw new NullPointerException();
            } else {
               int mutable_bitField0_ = false;
               UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

               try {
                  boolean done = false;

                  while(!done) {
                     int tag = input.readTag();
                     switch (tag) {
                        case 0:
                           done = true;
                           break;
                        case 10:
                           if (!(mutable_bitField0_ & true)) {
                              this.field_ = new ArrayList();
                              mutable_bitField0_ |= true;
                           }

                           this.field_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                           break;
                        default:
                           if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var11) {
                  throw var11.setUnfinishedMessage(this);
               } catch (IOException var12) {
                  throw (new InvalidProtocolBufferException(var12)).setUnfinishedMessage(this);
               } finally {
                  if (mutable_bitField0_ & true) {
                     this.field_ = Collections.unmodifiableList(this.field_);
                  }

                  this.unknownFields = unknownFields.build();
                  this.makeExtensionsImmutable();
               }

            }
         }

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable.ensureFieldAccessorsInitialized(TypedRow.class, Builder.class);
         }

         public List<MysqlxExpr.Expr> getFieldList() {
            return this.field_;
         }

         public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() {
            return this.field_;
         }

         public int getFieldCount() {
            return this.field_.size();
         }

         public MysqlxExpr.Expr getField(int index) {
            return (MysqlxExpr.Expr)this.field_.get(index);
         }

         public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) {
            return (MysqlxExpr.ExprOrBuilder)this.field_.get(index);
         }

         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else {
               for(int i = 0; i < this.getFieldCount(); ++i) {
                  if (!this.getField(i).isInitialized()) {
                     this.memoizedIsInitialized = 0;
                     return false;
                  }
               }

               this.memoizedIsInitialized = 1;
               return true;
            }
         }

         public void writeTo(CodedOutputStream output) throws IOException {
            for(int i = 0; i < this.field_.size(); ++i) {
               output.writeMessage(1, (MessageLite)this.field_.get(i));
            }

            this.unknownFields.writeTo(output);
         }

         public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
               return size;
            } else {
               size = 0;

               for(int i = 0; i < this.field_.size(); ++i) {
                  size += CodedOutputStream.computeMessageSize(1, (MessageLite)this.field_.get(i));
               }

               size += this.unknownFields.getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         public boolean equals(Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof TypedRow)) {
               return super.equals(obj);
            } else {
               TypedRow other = (TypedRow)obj;
               if (!this.getFieldList().equals(other.getFieldList())) {
                  return false;
               } else {
                  return this.unknownFields.equals(other.unknownFields);
               }
            }
         }

         public int hashCode() {
            if (this.memoizedHashCode != 0) {
               return this.memoizedHashCode;
            } else {
               int hash = 41;
               hash = 19 * hash + getDescriptor().hashCode();
               if (this.getFieldCount() > 0) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.getFieldList().hashCode();
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static TypedRow parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (TypedRow)PARSER.parseFrom(data);
         }

         public static TypedRow parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (TypedRow)PARSER.parseFrom(data, extensionRegistry);
         }

         public static TypedRow parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (TypedRow)PARSER.parseFrom(data);
         }

         public static TypedRow parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (TypedRow)PARSER.parseFrom(data, extensionRegistry);
         }

         public static TypedRow parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (TypedRow)PARSER.parseFrom(data);
         }

         public static TypedRow parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (TypedRow)PARSER.parseFrom(data, extensionRegistry);
         }

         public static TypedRow parseFrom(InputStream input) throws IOException {
            return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static TypedRow parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static TypedRow parseDelimitedFrom(InputStream input) throws IOException {
            return (TypedRow)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static TypedRow parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (TypedRow)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static TypedRow parseFrom(CodedInputStream input) throws IOException {
            return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static TypedRow parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (TypedRow)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public Builder newBuilderForType() {
            return newBuilder();
         }

         public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static Builder newBuilder(TypedRow prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
         }

         protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
         }

         public static TypedRow getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<TypedRow> parser() {
            return PARSER;
         }

         public Parser<TypedRow> getParserForType() {
            return PARSER;
         }

         public TypedRow getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         // $FF: synthetic method
         TypedRow(GeneratedMessageV3.Builder x0, Object x1) {
            this(x0);
         }

         // $FF: synthetic method
         TypedRow(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
            this(x0, x1);
         }

         public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements TypedRowOrBuilder {
            private int bitField0_;
            private List<MysqlxExpr.Expr> field_;
            private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> fieldBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_fieldAccessorTable.ensureFieldAccessorsInitialized(TypedRow.class, Builder.class);
            }

            private Builder() {
               this.field_ = Collections.emptyList();
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.field_ = Collections.emptyList();
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxCrud.Insert.TypedRow.alwaysUseFieldBuilders) {
                  this.getFieldFieldBuilder();
               }

            }

            public Builder clear() {
               super.clear();
               if (this.fieldBuilder_ == null) {
                  this.field_ = Collections.emptyList();
                  this.bitField0_ &= -2;
               } else {
                  this.fieldBuilder_.clear();
               }

               return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxCrud.internal_static_Mysqlx_Crud_Insert_TypedRow_descriptor;
            }

            public TypedRow getDefaultInstanceForType() {
               return MysqlxCrud.Insert.TypedRow.getDefaultInstance();
            }

            public TypedRow build() {
               TypedRow result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public TypedRow buildPartial() {
               TypedRow result = new TypedRow(this);
               int from_bitField0_ = this.bitField0_;
               if (this.fieldBuilder_ == null) {
                  if ((this.bitField0_ & 1) != 0) {
                     this.field_ = Collections.unmodifiableList(this.field_);
                     this.bitField0_ &= -2;
                  }

                  result.field_ = this.field_;
               } else {
                  result.field_ = this.fieldBuilder_.build();
               }

               this.onBuilt();
               return result;
            }

            public Builder clone() {
               return (Builder)super.clone();
            }

            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
               return (Builder)super.setField(field, value);
            }

            public Builder clearField(Descriptors.FieldDescriptor field) {
               return (Builder)super.clearField(field);
            }

            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
               return (Builder)super.clearOneof(oneof);
            }

            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
               return (Builder)super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
               return (Builder)super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(Message other) {
               if (other instanceof TypedRow) {
                  return this.mergeFrom((TypedRow)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public Builder mergeFrom(TypedRow other) {
               if (other == MysqlxCrud.Insert.TypedRow.getDefaultInstance()) {
                  return this;
               } else {
                  if (this.fieldBuilder_ == null) {
                     if (!other.field_.isEmpty()) {
                        if (this.field_.isEmpty()) {
                           this.field_ = other.field_;
                           this.bitField0_ &= -2;
                        } else {
                           this.ensureFieldIsMutable();
                           this.field_.addAll(other.field_);
                        }

                        this.onChanged();
                     }
                  } else if (!other.field_.isEmpty()) {
                     if (this.fieldBuilder_.isEmpty()) {
                        this.fieldBuilder_.dispose();
                        this.fieldBuilder_ = null;
                        this.field_ = other.field_;
                        this.bitField0_ &= -2;
                        this.fieldBuilder_ = MysqlxCrud.Insert.TypedRow.alwaysUseFieldBuilders ? this.getFieldFieldBuilder() : null;
                     } else {
                        this.fieldBuilder_.addAllMessages(other.field_);
                     }
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            public final boolean isInitialized() {
               for(int i = 0; i < this.getFieldCount(); ++i) {
                  if (!this.getField(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               TypedRow parsedMessage = null;

               try {
                  parsedMessage = (TypedRow)MysqlxCrud.Insert.TypedRow.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (TypedRow)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (parsedMessage != null) {
                     this.mergeFrom(parsedMessage);
                  }

               }

               return this;
            }

            private void ensureFieldIsMutable() {
               if ((this.bitField0_ & 1) == 0) {
                  this.field_ = new ArrayList(this.field_);
                  this.bitField0_ |= 1;
               }

            }

            public List<MysqlxExpr.Expr> getFieldList() {
               return this.fieldBuilder_ == null ? Collections.unmodifiableList(this.field_) : this.fieldBuilder_.getMessageList();
            }

            public int getFieldCount() {
               return this.fieldBuilder_ == null ? this.field_.size() : this.fieldBuilder_.getCount();
            }

            public MysqlxExpr.Expr getField(int index) {
               return this.fieldBuilder_ == null ? (MysqlxExpr.Expr)this.field_.get(index) : (MysqlxExpr.Expr)this.fieldBuilder_.getMessage(index);
            }

            public Builder setField(int index, MysqlxExpr.Expr value) {
               if (this.fieldBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.ensureFieldIsMutable();
                  this.field_.set(index, value);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.setMessage(index, value);
               }

               return this;
            }

            public Builder setField(int index, MysqlxExpr.Expr.Builder builderForValue) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.set(index, builderForValue.build());
                  this.onChanged();
               } else {
                  this.fieldBuilder_.setMessage(index, builderForValue.build());
               }

               return this;
            }

            public Builder addField(MysqlxExpr.Expr value) {
               if (this.fieldBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.ensureFieldIsMutable();
                  this.field_.add(value);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(value);
               }

               return this;
            }

            public Builder addField(int index, MysqlxExpr.Expr value) {
               if (this.fieldBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.ensureFieldIsMutable();
                  this.field_.add(index, value);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(index, value);
               }

               return this;
            }

            public Builder addField(MysqlxExpr.Expr.Builder builderForValue) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.add(builderForValue.build());
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(builderForValue.build());
               }

               return this;
            }

            public Builder addField(int index, MysqlxExpr.Expr.Builder builderForValue) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.add(index, builderForValue.build());
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addMessage(index, builderForValue.build());
               }

               return this;
            }

            public Builder addAllField(Iterable<? extends MysqlxExpr.Expr> values) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  AbstractMessageLite.Builder.addAll(values, this.field_);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.addAllMessages(values);
               }

               return this;
            }

            public Builder clearField() {
               if (this.fieldBuilder_ == null) {
                  this.field_ = Collections.emptyList();
                  this.bitField0_ &= -2;
                  this.onChanged();
               } else {
                  this.fieldBuilder_.clear();
               }

               return this;
            }

            public Builder removeField(int index) {
               if (this.fieldBuilder_ == null) {
                  this.ensureFieldIsMutable();
                  this.field_.remove(index);
                  this.onChanged();
               } else {
                  this.fieldBuilder_.remove(index);
               }

               return this;
            }

            public MysqlxExpr.Expr.Builder getFieldBuilder(int index) {
               return (MysqlxExpr.Expr.Builder)this.getFieldFieldBuilder().getBuilder(index);
            }

            public MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int index) {
               return this.fieldBuilder_ == null ? (MysqlxExpr.ExprOrBuilder)this.field_.get(index) : (MysqlxExpr.ExprOrBuilder)this.fieldBuilder_.getMessageOrBuilder(index);
            }

            public List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList() {
               return this.fieldBuilder_ != null ? this.fieldBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.field_);
            }

            public MysqlxExpr.Expr.Builder addFieldBuilder() {
               return (MysqlxExpr.Expr.Builder)this.getFieldFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
            }

            public MysqlxExpr.Expr.Builder addFieldBuilder(int index) {
               return (MysqlxExpr.Expr.Builder)this.getFieldFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
            }

            public List<MysqlxExpr.Expr.Builder> getFieldBuilderList() {
               return this.getFieldFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getFieldFieldBuilder() {
               if (this.fieldBuilder_ == null) {
                  this.fieldBuilder_ = new RepeatedFieldBuilderV3(this.field_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
                  this.field_ = null;
               }

               return this.fieldBuilder_;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
               return (Builder)super.setUnknownFields(unknownFields);
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
               return (Builder)super.mergeUnknownFields(unknownFields);
            }

            // $FF: synthetic method
            Builder(Object x0) {
               this();
            }

            // $FF: synthetic method
            Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
               this(x0);
            }
         }
      }

      public interface TypedRowOrBuilder extends MessageOrBuilder {
         List<MysqlxExpr.Expr> getFieldList();

         MysqlxExpr.Expr getField(int var1);

         int getFieldCount();

         List<? extends MysqlxExpr.ExprOrBuilder> getFieldOrBuilderList();

         MysqlxExpr.ExprOrBuilder getFieldOrBuilder(int var1);
      }
   }

   public interface InsertOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      Collection getCollection();

      CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      DataModel getDataModel();

      List<Column> getProjectionList();

      Column getProjection(int var1);

      int getProjectionCount();

      List<? extends ColumnOrBuilder> getProjectionOrBuilderList();

      ColumnOrBuilder getProjectionOrBuilder(int var1);

      List<Insert.TypedRow> getRowList();

      Insert.TypedRow getRow(int var1);

      int getRowCount();

      List<? extends Insert.TypedRowOrBuilder> getRowOrBuilderList();

      Insert.TypedRowOrBuilder getRowOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasUpsert();

      boolean getUpsert();
   }

   public static final class Find extends GeneratedMessageV3 implements FindOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int COLLECTION_FIELD_NUMBER = 2;
      private Collection collection_;
      public static final int DATA_MODEL_FIELD_NUMBER = 3;
      private int dataModel_;
      public static final int PROJECTION_FIELD_NUMBER = 4;
      private List<Projection> projection_;
      public static final int ARGS_FIELD_NUMBER = 11;
      private List<MysqlxDatatypes.Scalar> args_;
      public static final int CRITERIA_FIELD_NUMBER = 5;
      private MysqlxExpr.Expr criteria_;
      public static final int LIMIT_FIELD_NUMBER = 6;
      private Limit limit_;
      public static final int ORDER_FIELD_NUMBER = 7;
      private List<Order> order_;
      public static final int GROUPING_FIELD_NUMBER = 8;
      private List<MysqlxExpr.Expr> grouping_;
      public static final int GROUPING_CRITERIA_FIELD_NUMBER = 9;
      private MysqlxExpr.Expr groupingCriteria_;
      public static final int LOCKING_FIELD_NUMBER = 12;
      private int locking_;
      public static final int LOCKING_OPTIONS_FIELD_NUMBER = 13;
      private int lockingOptions_;
      public static final int LIMIT_EXPR_FIELD_NUMBER = 14;
      private LimitExpr limitExpr_;
      private byte memoizedIsInitialized;
      private static final Find DEFAULT_INSTANCE = new Find();
      /** @deprecated */
      @Deprecated
      public static final Parser<Find> PARSER = new AbstractParser<Find>() {
         public Find parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Find(input, extensionRegistry);
         }
      };

      private Find(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Find() {
         this.memoizedIsInitialized = -1;
         this.dataModel_ = 1;
         this.projection_ = Collections.emptyList();
         this.args_ = Collections.emptyList();
         this.order_ = Collections.emptyList();
         this.grouping_ = Collections.emptyList();
         this.locking_ = 1;
         this.lockingOptions_ = 1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Find();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Find(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  int rawValue;
                  MysqlxExpr.Expr.Builder subBuilder;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 18:
                        Collection.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.collection_.toBuilder();
                        }

                        this.collection_ = (Collection)input.readMessage(MysqlxCrud.Collection.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.collection_);
                           this.collection_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 24:
                        rawValue = input.readEnum();
                        DataModel value = MysqlxCrud.DataModel.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(3, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.dataModel_ = rawValue;
                        }
                        break;
                     case 34:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.projection_ = new ArrayList();
                           mutable_bitField0_ |= 4;
                        }

                        this.projection_.add(input.readMessage(MysqlxCrud.Projection.PARSER, extensionRegistry));
                        break;
                     case 42:
                        subBuilder = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilder = this.criteria_.toBuilder();
                        }

                        this.criteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.criteria_);
                           this.criteria_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     case 50:
                        Limit.Builder subBuilder = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilder = this.limit_.toBuilder();
                        }

                        this.limit_ = (Limit)input.readMessage(MysqlxCrud.Limit.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limit_);
                           this.limit_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 58:
                        if ((mutable_bitField0_ & 64) == 0) {
                           this.order_ = new ArrayList();
                           mutable_bitField0_ |= 64;
                        }

                        this.order_.add(input.readMessage(MysqlxCrud.Order.PARSER, extensionRegistry));
                        break;
                     case 66:
                        if ((mutable_bitField0_ & 128) == 0) {
                           this.grouping_ = new ArrayList();
                           mutable_bitField0_ |= 128;
                        }

                        this.grouping_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     case 74:
                        subBuilder = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilder = this.groupingCriteria_.toBuilder();
                        }

                        this.groupingCriteria_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.groupingCriteria_);
                           this.groupingCriteria_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     case 90:
                        if ((mutable_bitField0_ & 8) == 0) {
                           this.args_ = new ArrayList();
                           mutable_bitField0_ |= 8;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                        break;
                     case 96:
                        rawValue = input.readEnum();
                        RowLock value = MysqlxCrud.Find.RowLock.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(12, rawValue);
                        } else {
                           this.bitField0_ |= 32;
                           this.locking_ = rawValue;
                        }
                        break;
                     case 104:
                        rawValue = input.readEnum();
                        RowLockOptions value = MysqlxCrud.Find.RowLockOptions.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(13, rawValue);
                        } else {
                           this.bitField0_ |= 64;
                           this.lockingOptions_ = rawValue;
                        }
                        break;
                     case 114:
                        LimitExpr.Builder subBuilder = null;
                        if ((this.bitField0_ & 128) != 0) {
                           subBuilder = this.limitExpr_.toBuilder();
                        }

                        this.limitExpr_ = (LimitExpr)input.readMessage(MysqlxCrud.LimitExpr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.limitExpr_);
                           this.limitExpr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 128;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
               }

               if ((mutable_bitField0_ & 64) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
               }

               if ((mutable_bitField0_ & 128) != 0) {
                  this.grouping_ = Collections.unmodifiableList(this.grouping_);
               }

               if ((mutable_bitField0_ & 8) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable.ensureFieldAccessorsInitialized(Find.class, Builder.class);
      }

      public boolean hasCollection() {
         return (this.bitField0_ & 1) != 0;
      }

      public Collection getCollection() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public CollectionOrBuilder getCollectionOrBuilder() {
         return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
      }

      public boolean hasDataModel() {
         return (this.bitField0_ & 2) != 0;
      }

      public DataModel getDataModel() {
         DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
         return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
      }

      public List<Projection> getProjectionList() {
         return this.projection_;
      }

      public List<? extends ProjectionOrBuilder> getProjectionOrBuilderList() {
         return this.projection_;
      }

      public int getProjectionCount() {
         return this.projection_.size();
      }

      public Projection getProjection(int index) {
         return (Projection)this.projection_.get(index);
      }

      public ProjectionOrBuilder getProjectionOrBuilder(int index) {
         return (ProjectionOrBuilder)this.projection_.get(index);
      }

      public List<MysqlxDatatypes.Scalar> getArgsList() {
         return this.args_;
      }

      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      public int getArgsCount() {
         return this.args_.size();
      }

      public MysqlxDatatypes.Scalar getArgs(int index) {
         return (MysqlxDatatypes.Scalar)this.args_.get(index);
      }

      public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
         return (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index);
      }

      public boolean hasCriteria() {
         return (this.bitField0_ & 4) != 0;
      }

      public MysqlxExpr.Expr getCriteria() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
         return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
      }

      public boolean hasLimit() {
         return (this.bitField0_ & 8) != 0;
      }

      public Limit getLimit() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      public LimitOrBuilder getLimitOrBuilder() {
         return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
      }

      public List<Order> getOrderList() {
         return this.order_;
      }

      public List<? extends OrderOrBuilder> getOrderOrBuilderList() {
         return this.order_;
      }

      public int getOrderCount() {
         return this.order_.size();
      }

      public Order getOrder(int index) {
         return (Order)this.order_.get(index);
      }

      public OrderOrBuilder getOrderOrBuilder(int index) {
         return (OrderOrBuilder)this.order_.get(index);
      }

      public List<MysqlxExpr.Expr> getGroupingList() {
         return this.grouping_;
      }

      public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() {
         return this.grouping_;
      }

      public int getGroupingCount() {
         return this.grouping_.size();
      }

      public MysqlxExpr.Expr getGrouping(int index) {
         return (MysqlxExpr.Expr)this.grouping_.get(index);
      }

      public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) {
         return (MysqlxExpr.ExprOrBuilder)this.grouping_.get(index);
      }

      public boolean hasGroupingCriteria() {
         return (this.bitField0_ & 16) != 0;
      }

      public MysqlxExpr.Expr getGroupingCriteria() {
         return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
      }

      public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() {
         return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
      }

      public boolean hasLocking() {
         return (this.bitField0_ & 32) != 0;
      }

      public RowLock getLocking() {
         RowLock result = MysqlxCrud.Find.RowLock.valueOf(this.locking_);
         return result == null ? MysqlxCrud.Find.RowLock.SHARED_LOCK : result;
      }

      public boolean hasLockingOptions() {
         return (this.bitField0_ & 64) != 0;
      }

      public RowLockOptions getLockingOptions() {
         RowLockOptions result = MysqlxCrud.Find.RowLockOptions.valueOf(this.lockingOptions_);
         return result == null ? MysqlxCrud.Find.RowLockOptions.NOWAIT : result;
      }

      public boolean hasLimitExpr() {
         return (this.bitField0_ & 128) != 0;
      }

      public LimitExpr getLimitExpr() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      public LimitExprOrBuilder getLimitExprOrBuilder() {
         return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCollection()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCollection().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            int i;
            for(i = 0; i < this.getProjectionCount(); ++i) {
               if (!this.getProjection(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for(i = 0; i < this.getArgsCount(); ++i) {
               if (!this.getArgs(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               for(i = 0; i < this.getOrderCount(); ++i) {
                  if (!this.getOrder(i).isInitialized()) {
                     this.memoizedIsInitialized = 0;
                     return false;
                  }
               }

               for(i = 0; i < this.getGroupingCount(); ++i) {
                  if (!this.getGrouping(i).isInitialized()) {
                     this.memoizedIsInitialized = 0;
                     return false;
                  }
               }

               if (this.hasGroupingCriteria() && !this.getGroupingCriteria().isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               } else if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               } else {
                  this.memoizedIsInitialized = 1;
                  return true;
               }
            }
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(2, this.getCollection());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(3, this.dataModel_);
         }

         int i;
         for(i = 0; i < this.projection_.size(); ++i) {
            output.writeMessage(4, (MessageLite)this.projection_.get(i));
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(5, this.getCriteria());
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(6, this.getLimit());
         }

         for(i = 0; i < this.order_.size(); ++i) {
            output.writeMessage(7, (MessageLite)this.order_.get(i));
         }

         for(i = 0; i < this.grouping_.size(); ++i) {
            output.writeMessage(8, (MessageLite)this.grouping_.get(i));
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(9, this.getGroupingCriteria());
         }

         for(i = 0; i < this.args_.size(); ++i) {
            output.writeMessage(11, (MessageLite)this.args_.get(i));
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeEnum(12, this.locking_);
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeEnum(13, this.lockingOptions_);
         }

         if ((this.bitField0_ & 128) != 0) {
            output.writeMessage(14, this.getLimitExpr());
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getCollection());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.dataModel_);
            }

            int i;
            for(i = 0; i < this.projection_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(4, (MessageLite)this.projection_.get(i));
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(5, this.getCriteria());
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(6, this.getLimit());
            }

            for(i = 0; i < this.order_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(7, (MessageLite)this.order_.get(i));
            }

            for(i = 0; i < this.grouping_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(8, (MessageLite)this.grouping_.get(i));
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(9, this.getGroupingCriteria());
            }

            for(i = 0; i < this.args_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(11, (MessageLite)this.args_.get(i));
            }

            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeEnumSize(12, this.locking_);
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeEnumSize(13, this.lockingOptions_);
            }

            if ((this.bitField0_ & 128) != 0) {
               size += CodedOutputStream.computeMessageSize(14, this.getLimitExpr());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Find)) {
            return super.equals(obj);
         } else {
            Find other = (Find)obj;
            if (this.hasCollection() != other.hasCollection()) {
               return false;
            } else if (this.hasCollection() && !this.getCollection().equals(other.getCollection())) {
               return false;
            } else if (this.hasDataModel() != other.hasDataModel()) {
               return false;
            } else if (this.hasDataModel() && this.dataModel_ != other.dataModel_) {
               return false;
            } else if (!this.getProjectionList().equals(other.getProjectionList())) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasCriteria() != other.hasCriteria()) {
               return false;
            } else if (this.hasCriteria() && !this.getCriteria().equals(other.getCriteria())) {
               return false;
            } else if (this.hasLimit() != other.hasLimit()) {
               return false;
            } else if (this.hasLimit() && !this.getLimit().equals(other.getLimit())) {
               return false;
            } else if (!this.getOrderList().equals(other.getOrderList())) {
               return false;
            } else if (!this.getGroupingList().equals(other.getGroupingList())) {
               return false;
            } else if (this.hasGroupingCriteria() != other.hasGroupingCriteria()) {
               return false;
            } else if (this.hasGroupingCriteria() && !this.getGroupingCriteria().equals(other.getGroupingCriteria())) {
               return false;
            } else if (this.hasLocking() != other.hasLocking()) {
               return false;
            } else if (this.hasLocking() && this.locking_ != other.locking_) {
               return false;
            } else if (this.hasLockingOptions() != other.hasLockingOptions()) {
               return false;
            } else if (this.hasLockingOptions() && this.lockingOptions_ != other.lockingOptions_) {
               return false;
            } else if (this.hasLimitExpr() != other.hasLimitExpr()) {
               return false;
            } else if (this.hasLimitExpr() && !this.getLimitExpr().equals(other.getLimitExpr())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasCollection()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCollection().hashCode();
            }

            if (this.hasDataModel()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.dataModel_;
            }

            if (this.getProjectionCount() > 0) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getProjectionList().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 11;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasCriteria()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getCriteria().hashCode();
            }

            if (this.hasLimit()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getLimit().hashCode();
            }

            if (this.getOrderCount() > 0) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getOrderList().hashCode();
            }

            if (this.getGroupingCount() > 0) {
               hash = 37 * hash + 8;
               hash = 53 * hash + this.getGroupingList().hashCode();
            }

            if (this.hasGroupingCriteria()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getGroupingCriteria().hashCode();
            }

            if (this.hasLocking()) {
               hash = 37 * hash + 12;
               hash = 53 * hash + this.locking_;
            }

            if (this.hasLockingOptions()) {
               hash = 37 * hash + 13;
               hash = 53 * hash + this.lockingOptions_;
            }

            if (this.hasLimitExpr()) {
               hash = 37 * hash + 14;
               hash = 53 * hash + this.getLimitExpr().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Find parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Find)PARSER.parseFrom(data);
      }

      public static Find parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Find)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Find parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Find)PARSER.parseFrom(data);
      }

      public static Find parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Find)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Find parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Find)PARSER.parseFrom(data);
      }

      public static Find parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Find)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Find parseFrom(InputStream input) throws IOException {
         return (Find)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Find parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Find)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Find parseDelimitedFrom(InputStream input) throws IOException {
         return (Find)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Find parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Find)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Find parseFrom(CodedInputStream input) throws IOException {
         return (Find)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Find parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Find)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Find prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Find getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Find> parser() {
         return PARSER;
      }

      public Parser<Find> getParserForType() {
         return PARSER;
      }

      public Find getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Find(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Find(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FindOrBuilder {
         private int bitField0_;
         private Collection collection_;
         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> collectionBuilder_;
         private int dataModel_;
         private List<Projection> projection_;
         private RepeatedFieldBuilderV3<Projection, Projection.Builder, ProjectionOrBuilder> projectionBuilder_;
         private List<MysqlxDatatypes.Scalar> args_;
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> argsBuilder_;
         private MysqlxExpr.Expr criteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> criteriaBuilder_;
         private Limit limit_;
         private SingleFieldBuilderV3<Limit, Limit.Builder, LimitOrBuilder> limitBuilder_;
         private List<Order> order_;
         private RepeatedFieldBuilderV3<Order, Order.Builder, OrderOrBuilder> orderBuilder_;
         private List<MysqlxExpr.Expr> grouping_;
         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingBuilder_;
         private MysqlxExpr.Expr groupingCriteria_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> groupingCriteriaBuilder_;
         private int locking_;
         private int lockingOptions_;
         private LimitExpr limitExpr_;
         private SingleFieldBuilderV3<LimitExpr, LimitExpr.Builder, LimitExprOrBuilder> limitExprBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Find_fieldAccessorTable.ensureFieldAccessorsInitialized(Find.class, Builder.class);
         }

         private Builder() {
            this.dataModel_ = 1;
            this.projection_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.order_ = Collections.emptyList();
            this.grouping_ = Collections.emptyList();
            this.locking_ = 1;
            this.lockingOptions_ = 1;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.dataModel_ = 1;
            this.projection_ = Collections.emptyList();
            this.args_ = Collections.emptyList();
            this.order_ = Collections.emptyList();
            this.grouping_ = Collections.emptyList();
            this.locking_ = 1;
            this.lockingOptions_ = 1;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Find.alwaysUseFieldBuilders) {
               this.getCollectionFieldBuilder();
               this.getProjectionFieldBuilder();
               this.getArgsFieldBuilder();
               this.getCriteriaFieldBuilder();
               this.getLimitFieldBuilder();
               this.getOrderFieldBuilder();
               this.getGroupingFieldBuilder();
               this.getGroupingCriteriaFieldBuilder();
               this.getLimitExprFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.dataModel_ = 1;
            this.bitField0_ &= -3;
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.projectionBuilder_.clear();
            }

            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -9;
            } else {
               this.argsBuilder_.clear();
            }

            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -17;
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -33;
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -65;
            } else {
               this.orderBuilder_.clear();
            }

            if (this.groupingBuilder_ == null) {
               this.grouping_ = Collections.emptyList();
               this.bitField0_ &= -129;
            } else {
               this.groupingBuilder_.clear();
            }

            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteria_ = null;
            } else {
               this.groupingCriteriaBuilder_.clear();
            }

            this.bitField0_ &= -257;
            this.locking_ = 1;
            this.bitField0_ &= -513;
            this.lockingOptions_ = 1;
            this.bitField0_ &= -1025;
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -2049;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Find_descriptor;
         }

         public Find getDefaultInstanceForType() {
            return MysqlxCrud.Find.getDefaultInstance();
         }

         public Find build() {
            Find result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Find buildPartial() {
            Find result = new Find(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.collectionBuilder_ == null) {
                  result.collection_ = this.collection_;
               } else {
                  result.collection_ = (Collection)this.collectionBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.dataModel_ = this.dataModel_;
            if (this.projectionBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.projection_ = Collections.unmodifiableList(this.projection_);
                  this.bitField0_ &= -5;
               }

               result.projection_ = this.projection_;
            } else {
               result.projection_ = this.projectionBuilder_.build();
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -9;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 16) != 0) {
               if (this.criteriaBuilder_ == null) {
                  result.criteria_ = this.criteria_;
               } else {
                  result.criteria_ = (MysqlxExpr.Expr)this.criteriaBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 32) != 0) {
               if (this.limitBuilder_ == null) {
                  result.limit_ = this.limit_;
               } else {
                  result.limit_ = (Limit)this.limitBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if (this.orderBuilder_ == null) {
               if ((this.bitField0_ & 64) != 0) {
                  this.order_ = Collections.unmodifiableList(this.order_);
                  this.bitField0_ &= -65;
               }

               result.order_ = this.order_;
            } else {
               result.order_ = this.orderBuilder_.build();
            }

            if (this.groupingBuilder_ == null) {
               if ((this.bitField0_ & 128) != 0) {
                  this.grouping_ = Collections.unmodifiableList(this.grouping_);
                  this.bitField0_ &= -129;
               }

               result.grouping_ = this.grouping_;
            } else {
               result.grouping_ = this.groupingBuilder_.build();
            }

            if ((from_bitField0_ & 256) != 0) {
               if (this.groupingCriteriaBuilder_ == null) {
                  result.groupingCriteria_ = this.groupingCriteria_;
               } else {
                  result.groupingCriteria_ = (MysqlxExpr.Expr)this.groupingCriteriaBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            if ((from_bitField0_ & 512) != 0) {
               to_bitField0_ |= 32;
            }

            result.locking_ = this.locking_;
            if ((from_bitField0_ & 1024) != 0) {
               to_bitField0_ |= 64;
            }

            result.lockingOptions_ = this.lockingOptions_;
            if ((from_bitField0_ & 2048) != 0) {
               if (this.limitExprBuilder_ == null) {
                  result.limitExpr_ = this.limitExpr_;
               } else {
                  result.limitExpr_ = (LimitExpr)this.limitExprBuilder_.build();
               }

               to_bitField0_ |= 128;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Find) {
               return this.mergeFrom((Find)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Find other) {
            if (other == MysqlxCrud.Find.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCollection()) {
                  this.mergeCollection(other.getCollection());
               }

               if (other.hasDataModel()) {
                  this.setDataModel(other.getDataModel());
               }

               if (this.projectionBuilder_ == null) {
                  if (!other.projection_.isEmpty()) {
                     if (this.projection_.isEmpty()) {
                        this.projection_ = other.projection_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureProjectionIsMutable();
                        this.projection_.addAll(other.projection_);
                     }

                     this.onChanged();
                  }
               } else if (!other.projection_.isEmpty()) {
                  if (this.projectionBuilder_.isEmpty()) {
                     this.projectionBuilder_.dispose();
                     this.projectionBuilder_ = null;
                     this.projection_ = other.projection_;
                     this.bitField0_ &= -5;
                     this.projectionBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getProjectionFieldBuilder() : null;
                  } else {
                     this.projectionBuilder_.addAllMessages(other.projection_);
                  }
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -9;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -9;
                     this.argsBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasCriteria()) {
                  this.mergeCriteria(other.getCriteria());
               }

               if (other.hasLimit()) {
                  this.mergeLimit(other.getLimit());
               }

               if (this.orderBuilder_ == null) {
                  if (!other.order_.isEmpty()) {
                     if (this.order_.isEmpty()) {
                        this.order_ = other.order_;
                        this.bitField0_ &= -65;
                     } else {
                        this.ensureOrderIsMutable();
                        this.order_.addAll(other.order_);
                     }

                     this.onChanged();
                  }
               } else if (!other.order_.isEmpty()) {
                  if (this.orderBuilder_.isEmpty()) {
                     this.orderBuilder_.dispose();
                     this.orderBuilder_ = null;
                     this.order_ = other.order_;
                     this.bitField0_ &= -65;
                     this.orderBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getOrderFieldBuilder() : null;
                  } else {
                     this.orderBuilder_.addAllMessages(other.order_);
                  }
               }

               if (this.groupingBuilder_ == null) {
                  if (!other.grouping_.isEmpty()) {
                     if (this.grouping_.isEmpty()) {
                        this.grouping_ = other.grouping_;
                        this.bitField0_ &= -129;
                     } else {
                        this.ensureGroupingIsMutable();
                        this.grouping_.addAll(other.grouping_);
                     }

                     this.onChanged();
                  }
               } else if (!other.grouping_.isEmpty()) {
                  if (this.groupingBuilder_.isEmpty()) {
                     this.groupingBuilder_.dispose();
                     this.groupingBuilder_ = null;
                     this.grouping_ = other.grouping_;
                     this.bitField0_ &= -129;
                     this.groupingBuilder_ = MysqlxCrud.Find.alwaysUseFieldBuilders ? this.getGroupingFieldBuilder() : null;
                  } else {
                     this.groupingBuilder_.addAllMessages(other.grouping_);
                  }
               }

               if (other.hasGroupingCriteria()) {
                  this.mergeGroupingCriteria(other.getGroupingCriteria());
               }

               if (other.hasLocking()) {
                  this.setLocking(other.getLocking());
               }

               if (other.hasLockingOptions()) {
                  this.setLockingOptions(other.getLockingOptions());
               }

               if (other.hasLimitExpr()) {
                  this.mergeLimitExpr(other.getLimitExpr());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasCollection()) {
               return false;
            } else if (!this.getCollection().isInitialized()) {
               return false;
            } else {
               int i;
               for(i = 0; i < this.getProjectionCount(); ++i) {
                  if (!this.getProjection(i).isInitialized()) {
                     return false;
                  }
               }

               for(i = 0; i < this.getArgsCount(); ++i) {
                  if (!this.getArgs(i).isInitialized()) {
                     return false;
                  }
               }

               if (this.hasCriteria() && !this.getCriteria().isInitialized()) {
                  return false;
               } else if (this.hasLimit() && !this.getLimit().isInitialized()) {
                  return false;
               } else {
                  for(i = 0; i < this.getOrderCount(); ++i) {
                     if (!this.getOrder(i).isInitialized()) {
                        return false;
                     }
                  }

                  for(i = 0; i < this.getGroupingCount(); ++i) {
                     if (!this.getGrouping(i).isInitialized()) {
                        return false;
                     }
                  }

                  if (this.hasGroupingCriteria() && !this.getGroupingCriteria().isInitialized()) {
                     return false;
                  } else if (this.hasLimitExpr() && !this.getLimitExpr().isInitialized()) {
                     return false;
                  } else {
                     return true;
                  }
               }
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Find parsedMessage = null;

            try {
               parsedMessage = (Find)MysqlxCrud.Find.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Find)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasCollection() {
            return (this.bitField0_ & 1) != 0;
         }

         public Collection getCollection() {
            if (this.collectionBuilder_ == null) {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            } else {
               return (Collection)this.collectionBuilder_.getMessage();
            }
         }

         public Builder setCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.collection_ = value;
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setCollection(Collection.Builder builderForValue) {
            if (this.collectionBuilder_ == null) {
               this.collection_ = builderForValue.build();
               this.onChanged();
            } else {
               this.collectionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeCollection(Collection value) {
            if (this.collectionBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.collection_ != null && this.collection_ != MysqlxCrud.Collection.getDefaultInstance()) {
                  this.collection_ = MysqlxCrud.Collection.newBuilder(this.collection_).mergeFrom(value).buildPartial();
               } else {
                  this.collection_ = value;
               }

               this.onChanged();
            } else {
               this.collectionBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearCollection() {
            if (this.collectionBuilder_ == null) {
               this.collection_ = null;
               this.onChanged();
            } else {
               this.collectionBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Collection.Builder getCollectionBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Collection.Builder)this.getCollectionFieldBuilder().getBuilder();
         }

         public CollectionOrBuilder getCollectionOrBuilder() {
            if (this.collectionBuilder_ != null) {
               return (CollectionOrBuilder)this.collectionBuilder_.getMessageOrBuilder();
            } else {
               return this.collection_ == null ? MysqlxCrud.Collection.getDefaultInstance() : this.collection_;
            }
         }

         private SingleFieldBuilderV3<Collection, Collection.Builder, CollectionOrBuilder> getCollectionFieldBuilder() {
            if (this.collectionBuilder_ == null) {
               this.collectionBuilder_ = new SingleFieldBuilderV3(this.getCollection(), this.getParentForChildren(), this.isClean());
               this.collection_ = null;
            }

            return this.collectionBuilder_;
         }

         public boolean hasDataModel() {
            return (this.bitField0_ & 2) != 0;
         }

         public DataModel getDataModel() {
            DataModel result = MysqlxCrud.DataModel.valueOf(this.dataModel_);
            return result == null ? MysqlxCrud.DataModel.DOCUMENT : result;
         }

         public Builder setDataModel(DataModel value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.dataModel_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearDataModel() {
            this.bitField0_ &= -3;
            this.dataModel_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureProjectionIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.projection_ = new ArrayList(this.projection_);
               this.bitField0_ |= 4;
            }

         }

         public List<Projection> getProjectionList() {
            return this.projectionBuilder_ == null ? Collections.unmodifiableList(this.projection_) : this.projectionBuilder_.getMessageList();
         }

         public int getProjectionCount() {
            return this.projectionBuilder_ == null ? this.projection_.size() : this.projectionBuilder_.getCount();
         }

         public Projection getProjection(int index) {
            return this.projectionBuilder_ == null ? (Projection)this.projection_.get(index) : (Projection)this.projectionBuilder_.getMessage(index);
         }

         public Builder setProjection(int index, Projection value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.set(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setProjection(int index, Projection.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addProjection(Projection value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addProjection(int index, Projection value) {
            if (this.projectionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProjectionIsMutable();
               this.projection_.add(index, value);
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addProjection(Projection.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addProjection(int index, Projection.Builder builderForValue) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.projectionBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllProjection(Iterable<? extends Projection> values) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.projection_);
               this.onChanged();
            } else {
               this.projectionBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearProjection() {
            if (this.projectionBuilder_ == null) {
               this.projection_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.projectionBuilder_.clear();
            }

            return this;
         }

         public Builder removeProjection(int index) {
            if (this.projectionBuilder_ == null) {
               this.ensureProjectionIsMutable();
               this.projection_.remove(index);
               this.onChanged();
            } else {
               this.projectionBuilder_.remove(index);
            }

            return this;
         }

         public Projection.Builder getProjectionBuilder(int index) {
            return (Projection.Builder)this.getProjectionFieldBuilder().getBuilder(index);
         }

         public ProjectionOrBuilder getProjectionOrBuilder(int index) {
            return this.projectionBuilder_ == null ? (ProjectionOrBuilder)this.projection_.get(index) : (ProjectionOrBuilder)this.projectionBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends ProjectionOrBuilder> getProjectionOrBuilderList() {
            return this.projectionBuilder_ != null ? this.projectionBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.projection_);
         }

         public Projection.Builder addProjectionBuilder() {
            return (Projection.Builder)this.getProjectionFieldBuilder().addBuilder(MysqlxCrud.Projection.getDefaultInstance());
         }

         public Projection.Builder addProjectionBuilder(int index) {
            return (Projection.Builder)this.getProjectionFieldBuilder().addBuilder(index, MysqlxCrud.Projection.getDefaultInstance());
         }

         public List<Projection.Builder> getProjectionBuilderList() {
            return this.getProjectionFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Projection, Projection.Builder, ProjectionOrBuilder> getProjectionFieldBuilder() {
            if (this.projectionBuilder_ == null) {
               this.projectionBuilder_ = new RepeatedFieldBuilderV3(this.projection_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.projection_ = null;
            }

            return this.projectionBuilder_;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 8) == 0) {
               this.args_ = new ArrayList(this.args_);
               this.bitField0_ |= 8;
            }

         }

         public List<MysqlxDatatypes.Scalar> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         public MysqlxDatatypes.Scalar getArgs(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.Scalar)this.args_.get(index) : (MysqlxDatatypes.Scalar)this.argsBuilder_.getMessage(index);
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addArgs(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addArgs(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -9;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().getBuilder(index);
         }

         public MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? (MysqlxDatatypes.ScalarOrBuilder)this.args_.get(index) : (MysqlxDatatypes.ScalarOrBuilder)this.argsBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder() {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addArgsBuilder(int index) {
            return (MysqlxDatatypes.Scalar.Builder)this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3(this.args_, (this.bitField0_ & 8) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         public boolean hasCriteria() {
            return (this.bitField0_ & 16) != 0;
         }

         public MysqlxExpr.Expr getCriteria() {
            if (this.criteriaBuilder_ == null) {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            } else {
               return (MysqlxExpr.Expr)this.criteriaBuilder_.getMessage();
            }
         }

         public Builder setCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.criteria_ = value;
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public Builder setCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.criteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 16;
            return this;
         }

         public Builder mergeCriteria(MysqlxExpr.Expr value) {
            if (this.criteriaBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0 && this.criteria_ != null && this.criteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.criteria_ = MysqlxExpr.Expr.newBuilder(this.criteria_).mergeFrom(value).buildPartial();
               } else {
                  this.criteria_ = value;
               }

               this.onChanged();
            } else {
               this.criteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public Builder clearCriteria() {
            if (this.criteriaBuilder_ == null) {
               this.criteria_ = null;
               this.onChanged();
            } else {
               this.criteriaBuilder_.clear();
            }

            this.bitField0_ &= -17;
            return this;
         }

         public MysqlxExpr.Expr.Builder getCriteriaBuilder() {
            this.bitField0_ |= 16;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getCriteriaFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder() {
            if (this.criteriaBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.criteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.criteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.criteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getCriteriaFieldBuilder() {
            if (this.criteriaBuilder_ == null) {
               this.criteriaBuilder_ = new SingleFieldBuilderV3(this.getCriteria(), this.getParentForChildren(), this.isClean());
               this.criteria_ = null;
            }

            return this.criteriaBuilder_;
         }

         public boolean hasLimit() {
            return (this.bitField0_ & 32) != 0;
         }

         public Limit getLimit() {
            if (this.limitBuilder_ == null) {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            } else {
               return (Limit)this.limitBuilder_.getMessage();
            }
         }

         public Builder setLimit(Limit value) {
            if (this.limitBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limit_ = value;
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public Builder setLimit(Limit.Builder builderForValue) {
            if (this.limitBuilder_ == null) {
               this.limit_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 32;
            return this;
         }

         public Builder mergeLimit(Limit value) {
            if (this.limitBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0 && this.limit_ != null && this.limit_ != MysqlxCrud.Limit.getDefaultInstance()) {
                  this.limit_ = MysqlxCrud.Limit.newBuilder(this.limit_).mergeFrom(value).buildPartial();
               } else {
                  this.limit_ = value;
               }

               this.onChanged();
            } else {
               this.limitBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public Builder clearLimit() {
            if (this.limitBuilder_ == null) {
               this.limit_ = null;
               this.onChanged();
            } else {
               this.limitBuilder_.clear();
            }

            this.bitField0_ &= -33;
            return this;
         }

         public Limit.Builder getLimitBuilder() {
            this.bitField0_ |= 32;
            this.onChanged();
            return (Limit.Builder)this.getLimitFieldBuilder().getBuilder();
         }

         public LimitOrBuilder getLimitOrBuilder() {
            if (this.limitBuilder_ != null) {
               return (LimitOrBuilder)this.limitBuilder_.getMessageOrBuilder();
            } else {
               return this.limit_ == null ? MysqlxCrud.Limit.getDefaultInstance() : this.limit_;
            }
         }

         private SingleFieldBuilderV3<Limit, Limit.Builder, LimitOrBuilder> getLimitFieldBuilder() {
            if (this.limitBuilder_ == null) {
               this.limitBuilder_ = new SingleFieldBuilderV3(this.getLimit(), this.getParentForChildren(), this.isClean());
               this.limit_ = null;
            }

            return this.limitBuilder_;
         }

         private void ensureOrderIsMutable() {
            if ((this.bitField0_ & 64) == 0) {
               this.order_ = new ArrayList(this.order_);
               this.bitField0_ |= 64;
            }

         }

         public List<Order> getOrderList() {
            return this.orderBuilder_ == null ? Collections.unmodifiableList(this.order_) : this.orderBuilder_.getMessageList();
         }

         public int getOrderCount() {
            return this.orderBuilder_ == null ? this.order_.size() : this.orderBuilder_.getCount();
         }

         public Order getOrder(int index) {
            return this.orderBuilder_ == null ? (Order)this.order_.get(index) : (Order)this.orderBuilder_.getMessage(index);
         }

         public Builder setOrder(int index, Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.set(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setOrder(int index, Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addOrder(Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addOrder(int index, Order value) {
            if (this.orderBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureOrderIsMutable();
               this.order_.add(index, value);
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addOrder(Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addOrder(int index, Order.Builder builderForValue) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.orderBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllOrder(Iterable<? extends Order> values) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.order_);
               this.onChanged();
            } else {
               this.orderBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearOrder() {
            if (this.orderBuilder_ == null) {
               this.order_ = Collections.emptyList();
               this.bitField0_ &= -65;
               this.onChanged();
            } else {
               this.orderBuilder_.clear();
            }

            return this;
         }

         public Builder removeOrder(int index) {
            if (this.orderBuilder_ == null) {
               this.ensureOrderIsMutable();
               this.order_.remove(index);
               this.onChanged();
            } else {
               this.orderBuilder_.remove(index);
            }

            return this;
         }

         public Order.Builder getOrderBuilder(int index) {
            return (Order.Builder)this.getOrderFieldBuilder().getBuilder(index);
         }

         public OrderOrBuilder getOrderOrBuilder(int index) {
            return this.orderBuilder_ == null ? (OrderOrBuilder)this.order_.get(index) : (OrderOrBuilder)this.orderBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends OrderOrBuilder> getOrderOrBuilderList() {
            return this.orderBuilder_ != null ? this.orderBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.order_);
         }

         public Order.Builder addOrderBuilder() {
            return (Order.Builder)this.getOrderFieldBuilder().addBuilder(MysqlxCrud.Order.getDefaultInstance());
         }

         public Order.Builder addOrderBuilder(int index) {
            return (Order.Builder)this.getOrderFieldBuilder().addBuilder(index, MysqlxCrud.Order.getDefaultInstance());
         }

         public List<Order.Builder> getOrderBuilderList() {
            return this.getOrderFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Order, Order.Builder, OrderOrBuilder> getOrderFieldBuilder() {
            if (this.orderBuilder_ == null) {
               this.orderBuilder_ = new RepeatedFieldBuilderV3(this.order_, (this.bitField0_ & 64) != 0, this.getParentForChildren(), this.isClean());
               this.order_ = null;
            }

            return this.orderBuilder_;
         }

         private void ensureGroupingIsMutable() {
            if ((this.bitField0_ & 128) == 0) {
               this.grouping_ = new ArrayList(this.grouping_);
               this.bitField0_ |= 128;
            }

         }

         public List<MysqlxExpr.Expr> getGroupingList() {
            return this.groupingBuilder_ == null ? Collections.unmodifiableList(this.grouping_) : this.groupingBuilder_.getMessageList();
         }

         public int getGroupingCount() {
            return this.groupingBuilder_ == null ? this.grouping_.size() : this.groupingBuilder_.getCount();
         }

         public MysqlxExpr.Expr getGrouping(int index) {
            return this.groupingBuilder_ == null ? (MysqlxExpr.Expr)this.grouping_.get(index) : (MysqlxExpr.Expr)this.groupingBuilder_.getMessage(index);
         }

         public Builder setGrouping(int index, MysqlxExpr.Expr value) {
            if (this.groupingBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureGroupingIsMutable();
               this.grouping_.set(index, value);
               this.onChanged();
            } else {
               this.groupingBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.groupingBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addGrouping(MysqlxExpr.Expr value) {
            if (this.groupingBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureGroupingIsMutable();
               this.grouping_.add(value);
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addGrouping(int index, MysqlxExpr.Expr value) {
            if (this.groupingBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureGroupingIsMutable();
               this.grouping_.add(index, value);
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addGrouping(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addGrouping(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.groupingBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllGrouping(Iterable<? extends MysqlxExpr.Expr> values) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.grouping_);
               this.onChanged();
            } else {
               this.groupingBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearGrouping() {
            if (this.groupingBuilder_ == null) {
               this.grouping_ = Collections.emptyList();
               this.bitField0_ &= -129;
               this.onChanged();
            } else {
               this.groupingBuilder_.clear();
            }

            return this;
         }

         public Builder removeGrouping(int index) {
            if (this.groupingBuilder_ == null) {
               this.ensureGroupingIsMutable();
               this.grouping_.remove(index);
               this.onChanged();
            } else {
               this.groupingBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.Expr.Builder getGroupingBuilder(int index) {
            return (MysqlxExpr.Expr.Builder)this.getGroupingFieldBuilder().getBuilder(index);
         }

         public MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int index) {
            return this.groupingBuilder_ == null ? (MysqlxExpr.ExprOrBuilder)this.grouping_.get(index) : (MysqlxExpr.ExprOrBuilder)this.groupingBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList() {
            return this.groupingBuilder_ != null ? this.groupingBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.grouping_);
         }

         public MysqlxExpr.Expr.Builder addGroupingBuilder() {
            return (MysqlxExpr.Expr.Builder)this.getGroupingFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public MysqlxExpr.Expr.Builder addGroupingBuilder(int index) {
            return (MysqlxExpr.Expr.Builder)this.getGroupingFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<MysqlxExpr.Expr.Builder> getGroupingBuilderList() {
            return this.getGroupingFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingFieldBuilder() {
            if (this.groupingBuilder_ == null) {
               this.groupingBuilder_ = new RepeatedFieldBuilderV3(this.grouping_, (this.bitField0_ & 128) != 0, this.getParentForChildren(), this.isClean());
               this.grouping_ = null;
            }

            return this.groupingBuilder_;
         }

         public boolean hasGroupingCriteria() {
            return (this.bitField0_ & 256) != 0;
         }

         public MysqlxExpr.Expr getGroupingCriteria() {
            if (this.groupingCriteriaBuilder_ == null) {
               return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
            } else {
               return (MysqlxExpr.Expr)this.groupingCriteriaBuilder_.getMessage();
            }
         }

         public Builder setGroupingCriteria(MysqlxExpr.Expr value) {
            if (this.groupingCriteriaBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.groupingCriteria_ = value;
               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.setMessage(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public Builder setGroupingCriteria(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteria_ = builderForValue.build();
               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 256;
            return this;
         }

         public Builder mergeGroupingCriteria(MysqlxExpr.Expr value) {
            if (this.groupingCriteriaBuilder_ == null) {
               if ((this.bitField0_ & 256) != 0 && this.groupingCriteria_ != null && this.groupingCriteria_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.groupingCriteria_ = MysqlxExpr.Expr.newBuilder(this.groupingCriteria_).mergeFrom(value).buildPartial();
               } else {
                  this.groupingCriteria_ = value;
               }

               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public Builder clearGroupingCriteria() {
            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteria_ = null;
               this.onChanged();
            } else {
               this.groupingCriteriaBuilder_.clear();
            }

            this.bitField0_ &= -257;
            return this;
         }

         public MysqlxExpr.Expr.Builder getGroupingCriteriaBuilder() {
            this.bitField0_ |= 256;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getGroupingCriteriaFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder() {
            if (this.groupingCriteriaBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.groupingCriteriaBuilder_.getMessageOrBuilder();
            } else {
               return this.groupingCriteria_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.groupingCriteria_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getGroupingCriteriaFieldBuilder() {
            if (this.groupingCriteriaBuilder_ == null) {
               this.groupingCriteriaBuilder_ = new SingleFieldBuilderV3(this.getGroupingCriteria(), this.getParentForChildren(), this.isClean());
               this.groupingCriteria_ = null;
            }

            return this.groupingCriteriaBuilder_;
         }

         public boolean hasLocking() {
            return (this.bitField0_ & 512) != 0;
         }

         public RowLock getLocking() {
            RowLock result = MysqlxCrud.Find.RowLock.valueOf(this.locking_);
            return result == null ? MysqlxCrud.Find.RowLock.SHARED_LOCK : result;
         }

         public Builder setLocking(RowLock value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 512;
               this.locking_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearLocking() {
            this.bitField0_ &= -513;
            this.locking_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasLockingOptions() {
            return (this.bitField0_ & 1024) != 0;
         }

         public RowLockOptions getLockingOptions() {
            RowLockOptions result = MysqlxCrud.Find.RowLockOptions.valueOf(this.lockingOptions_);
            return result == null ? MysqlxCrud.Find.RowLockOptions.NOWAIT : result;
         }

         public Builder setLockingOptions(RowLockOptions value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1024;
               this.lockingOptions_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearLockingOptions() {
            this.bitField0_ &= -1025;
            this.lockingOptions_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasLimitExpr() {
            return (this.bitField0_ & 2048) != 0;
         }

         public LimitExpr getLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            } else {
               return (LimitExpr)this.limitExprBuilder_.getMessage();
            }
         }

         public Builder setLimitExpr(LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.limitExpr_ = value;
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2048;
            return this;
         }

         public Builder setLimitExpr(LimitExpr.Builder builderForValue) {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.limitExprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2048;
            return this;
         }

         public Builder mergeLimitExpr(LimitExpr value) {
            if (this.limitExprBuilder_ == null) {
               if ((this.bitField0_ & 2048) != 0 && this.limitExpr_ != null && this.limitExpr_ != MysqlxCrud.LimitExpr.getDefaultInstance()) {
                  this.limitExpr_ = MysqlxCrud.LimitExpr.newBuilder(this.limitExpr_).mergeFrom(value).buildPartial();
               } else {
                  this.limitExpr_ = value;
               }

               this.onChanged();
            } else {
               this.limitExprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2048;
            return this;
         }

         public Builder clearLimitExpr() {
            if (this.limitExprBuilder_ == null) {
               this.limitExpr_ = null;
               this.onChanged();
            } else {
               this.limitExprBuilder_.clear();
            }

            this.bitField0_ &= -2049;
            return this;
         }

         public LimitExpr.Builder getLimitExprBuilder() {
            this.bitField0_ |= 2048;
            this.onChanged();
            return (LimitExpr.Builder)this.getLimitExprFieldBuilder().getBuilder();
         }

         public LimitExprOrBuilder getLimitExprOrBuilder() {
            if (this.limitExprBuilder_ != null) {
               return (LimitExprOrBuilder)this.limitExprBuilder_.getMessageOrBuilder();
            } else {
               return this.limitExpr_ == null ? MysqlxCrud.LimitExpr.getDefaultInstance() : this.limitExpr_;
            }
         }

         private SingleFieldBuilderV3<LimitExpr, LimitExpr.Builder, LimitExprOrBuilder> getLimitExprFieldBuilder() {
            if (this.limitExprBuilder_ == null) {
               this.limitExprBuilder_ = new SingleFieldBuilderV3(this.getLimitExpr(), this.getParentForChildren(), this.isClean());
               this.limitExpr_ = null;
            }

            return this.limitExprBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }

      public static enum RowLockOptions implements ProtocolMessageEnum {
         NOWAIT(1),
         SKIP_LOCKED(2);

         public static final int NOWAIT_VALUE = 1;
         public static final int SKIP_LOCKED_VALUE = 2;
         private static final Internal.EnumLiteMap<RowLockOptions> internalValueMap = new Internal.EnumLiteMap<RowLockOptions>() {
            public RowLockOptions findValueByNumber(int number) {
               return MysqlxCrud.Find.RowLockOptions.forNumber(number);
            }
         };
         private static final RowLockOptions[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static RowLockOptions valueOf(int value) {
            return forNumber(value);
         }

         public static RowLockOptions forNumber(int value) {
            switch (value) {
               case 1:
                  return NOWAIT;
               case 2:
                  return SKIP_LOCKED;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<RowLockOptions> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxCrud.Find.getDescriptor().getEnumTypes().get(1);
         }

         public static RowLockOptions valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private RowLockOptions(int value) {
            this.value = value;
         }
      }

      public static enum RowLock implements ProtocolMessageEnum {
         SHARED_LOCK(1),
         EXCLUSIVE_LOCK(2);

         public static final int SHARED_LOCK_VALUE = 1;
         public static final int EXCLUSIVE_LOCK_VALUE = 2;
         private static final Internal.EnumLiteMap<RowLock> internalValueMap = new Internal.EnumLiteMap<RowLock>() {
            public RowLock findValueByNumber(int number) {
               return MysqlxCrud.Find.RowLock.forNumber(number);
            }
         };
         private static final RowLock[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static RowLock valueOf(int value) {
            return forNumber(value);
         }

         public static RowLock forNumber(int value) {
            switch (value) {
               case 1:
                  return SHARED_LOCK;
               case 2:
                  return EXCLUSIVE_LOCK;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<RowLock> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxCrud.Find.getDescriptor().getEnumTypes().get(0);
         }

         public static RowLock valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private RowLock(int value) {
            this.value = value;
         }
      }
   }

   public interface FindOrBuilder extends MessageOrBuilder {
      boolean hasCollection();

      Collection getCollection();

      CollectionOrBuilder getCollectionOrBuilder();

      boolean hasDataModel();

      DataModel getDataModel();

      List<Projection> getProjectionList();

      Projection getProjection(int var1);

      int getProjectionCount();

      List<? extends ProjectionOrBuilder> getProjectionOrBuilderList();

      ProjectionOrBuilder getProjectionOrBuilder(int var1);

      List<MysqlxDatatypes.Scalar> getArgsList();

      MysqlxDatatypes.Scalar getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getArgsOrBuilder(int var1);

      boolean hasCriteria();

      MysqlxExpr.Expr getCriteria();

      MysqlxExpr.ExprOrBuilder getCriteriaOrBuilder();

      boolean hasLimit();

      Limit getLimit();

      LimitOrBuilder getLimitOrBuilder();

      List<Order> getOrderList();

      Order getOrder(int var1);

      int getOrderCount();

      List<? extends OrderOrBuilder> getOrderOrBuilderList();

      OrderOrBuilder getOrderOrBuilder(int var1);

      List<MysqlxExpr.Expr> getGroupingList();

      MysqlxExpr.Expr getGrouping(int var1);

      int getGroupingCount();

      List<? extends MysqlxExpr.ExprOrBuilder> getGroupingOrBuilderList();

      MysqlxExpr.ExprOrBuilder getGroupingOrBuilder(int var1);

      boolean hasGroupingCriteria();

      MysqlxExpr.Expr getGroupingCriteria();

      MysqlxExpr.ExprOrBuilder getGroupingCriteriaOrBuilder();

      boolean hasLocking();

      Find.RowLock getLocking();

      boolean hasLockingOptions();

      Find.RowLockOptions getLockingOptions();

      boolean hasLimitExpr();

      LimitExpr getLimitExpr();

      LimitExprOrBuilder getLimitExprOrBuilder();
   }

   public static final class UpdateOperation extends GeneratedMessageV3 implements UpdateOperationOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int SOURCE_FIELD_NUMBER = 1;
      private MysqlxExpr.ColumnIdentifier source_;
      public static final int OPERATION_FIELD_NUMBER = 2;
      private int operation_;
      public static final int VALUE_FIELD_NUMBER = 3;
      private MysqlxExpr.Expr value_;
      private byte memoizedIsInitialized;
      private static final UpdateOperation DEFAULT_INSTANCE = new UpdateOperation();
      /** @deprecated */
      @Deprecated
      public static final Parser<UpdateOperation> PARSER = new AbstractParser<UpdateOperation>() {
         public UpdateOperation parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new UpdateOperation(input, extensionRegistry);
         }
      };

      private UpdateOperation(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private UpdateOperation() {
         this.memoizedIsInitialized = -1;
         this.operation_ = 1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new UpdateOperation();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private UpdateOperation(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.ColumnIdentifier.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.source_.toBuilder();
                        }

                        this.source_ = (MysqlxExpr.ColumnIdentifier)input.readMessage(MysqlxExpr.ColumnIdentifier.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.source_);
                           this.source_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        UpdateType value = MysqlxCrud.UpdateOperation.UpdateType.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.operation_ = rawValue;
                        }
                        break;
                     case 26:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 4) != 0) {
                           subBuilder = this.value_.toBuilder();
                        }

                        this.value_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.value_);
                           this.value_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 4;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateOperation.class, Builder.class);
      }

      public boolean hasSource() {
         return (this.bitField0_ & 1) != 0;
      }

      public MysqlxExpr.ColumnIdentifier getSource() {
         return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
      }

      public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() {
         return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
      }

      public boolean hasOperation() {
         return (this.bitField0_ & 2) != 0;
      }

      public UpdateType getOperation() {
         UpdateType result = MysqlxCrud.UpdateOperation.UpdateType.valueOf(this.operation_);
         return result == null ? MysqlxCrud.UpdateOperation.UpdateType.SET : result;
      }

      public boolean hasValue() {
         return (this.bitField0_ & 4) != 0;
      }

      public MysqlxExpr.Expr getValue() {
         return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
      }

      public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
         return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasSource()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasOperation()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getSource().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasValue() && !this.getValue().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getSource());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.operation_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeMessage(3, this.getValue());
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getSource());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.operation_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeMessageSize(3, this.getValue());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof UpdateOperation)) {
            return super.equals(obj);
         } else {
            UpdateOperation other = (UpdateOperation)obj;
            if (this.hasSource() != other.hasSource()) {
               return false;
            } else if (this.hasSource() && !this.getSource().equals(other.getSource())) {
               return false;
            } else if (this.hasOperation() != other.hasOperation()) {
               return false;
            } else if (this.hasOperation() && this.operation_ != other.operation_) {
               return false;
            } else if (this.hasValue() != other.hasValue()) {
               return false;
            } else if (this.hasValue() && !this.getValue().equals(other.getValue())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasSource()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getSource().hashCode();
            }

            if (this.hasOperation()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.operation_;
            }

            if (this.hasValue()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getValue().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static UpdateOperation parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (UpdateOperation)PARSER.parseFrom(data);
      }

      public static UpdateOperation parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
      }

      public static UpdateOperation parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (UpdateOperation)PARSER.parseFrom(data);
      }

      public static UpdateOperation parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
      }

      public static UpdateOperation parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (UpdateOperation)PARSER.parseFrom(data);
      }

      public static UpdateOperation parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (UpdateOperation)PARSER.parseFrom(data, extensionRegistry);
      }

      public static UpdateOperation parseFrom(InputStream input) throws IOException {
         return (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static UpdateOperation parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static UpdateOperation parseDelimitedFrom(InputStream input) throws IOException {
         return (UpdateOperation)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static UpdateOperation parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (UpdateOperation)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static UpdateOperation parseFrom(CodedInputStream input) throws IOException {
         return (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static UpdateOperation parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (UpdateOperation)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(UpdateOperation prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static UpdateOperation getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<UpdateOperation> parser() {
         return PARSER;
      }

      public Parser<UpdateOperation> getParserForType() {
         return PARSER;
      }

      public UpdateOperation getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      UpdateOperation(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      UpdateOperation(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements UpdateOperationOrBuilder {
         private int bitField0_;
         private MysqlxExpr.ColumnIdentifier source_;
         private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> sourceBuilder_;
         private int operation_;
         private MysqlxExpr.Expr value_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateOperation.class, Builder.class);
         }

         private Builder() {
            this.operation_ = 1;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.operation_ = 1;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.UpdateOperation.alwaysUseFieldBuilders) {
               this.getSourceFieldBuilder();
               this.getValueFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.operation_ = 1;
            this.bitField0_ &= -3;
            if (this.valueBuilder_ == null) {
               this.value_ = null;
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_UpdateOperation_descriptor;
         }

         public UpdateOperation getDefaultInstanceForType() {
            return MysqlxCrud.UpdateOperation.getDefaultInstance();
         }

         public UpdateOperation build() {
            UpdateOperation result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public UpdateOperation buildPartial() {
            UpdateOperation result = new UpdateOperation(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.sourceBuilder_ == null) {
                  result.source_ = this.source_;
               } else {
                  result.source_ = (MysqlxExpr.ColumnIdentifier)this.sourceBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.operation_ = this.operation_;
            if ((from_bitField0_ & 4) != 0) {
               if (this.valueBuilder_ == null) {
                  result.value_ = this.value_;
               } else {
                  result.value_ = (MysqlxExpr.Expr)this.valueBuilder_.build();
               }

               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof UpdateOperation) {
               return this.mergeFrom((UpdateOperation)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(UpdateOperation other) {
            if (other == MysqlxCrud.UpdateOperation.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasSource()) {
                  this.mergeSource(other.getSource());
               }

               if (other.hasOperation()) {
                  this.setOperation(other.getOperation());
               }

               if (other.hasValue()) {
                  this.mergeValue(other.getValue());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasSource()) {
               return false;
            } else if (!this.hasOperation()) {
               return false;
            } else if (!this.getSource().isInitialized()) {
               return false;
            } else {
               return !this.hasValue() || this.getValue().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            UpdateOperation parsedMessage = null;

            try {
               parsedMessage = (UpdateOperation)MysqlxCrud.UpdateOperation.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (UpdateOperation)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasSource() {
            return (this.bitField0_ & 1) != 0;
         }

         public MysqlxExpr.ColumnIdentifier getSource() {
            if (this.sourceBuilder_ == null) {
               return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
            } else {
               return (MysqlxExpr.ColumnIdentifier)this.sourceBuilder_.getMessage();
            }
         }

         public Builder setSource(MysqlxExpr.ColumnIdentifier value) {
            if (this.sourceBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.source_ = value;
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setSource(MysqlxExpr.ColumnIdentifier.Builder builderForValue) {
            if (this.sourceBuilder_ == null) {
               this.source_ = builderForValue.build();
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeSource(MysqlxExpr.ColumnIdentifier value) {
            if (this.sourceBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
                  this.source_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.source_).mergeFrom(value).buildPartial();
               } else {
                  this.source_ = value;
               }

               this.onChanged();
            } else {
               this.sourceBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearSource() {
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
               this.onChanged();
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder getSourceBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (MysqlxExpr.ColumnIdentifier.Builder)this.getSourceFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder() {
            if (this.sourceBuilder_ != null) {
               return (MysqlxExpr.ColumnIdentifierOrBuilder)this.sourceBuilder_.getMessageOrBuilder();
            } else {
               return this.source_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.source_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> getSourceFieldBuilder() {
            if (this.sourceBuilder_ == null) {
               this.sourceBuilder_ = new SingleFieldBuilderV3(this.getSource(), this.getParentForChildren(), this.isClean());
               this.source_ = null;
            }

            return this.sourceBuilder_;
         }

         public boolean hasOperation() {
            return (this.bitField0_ & 2) != 0;
         }

         public UpdateType getOperation() {
            UpdateType result = MysqlxCrud.UpdateOperation.UpdateType.valueOf(this.operation_);
            return result == null ? MysqlxCrud.UpdateOperation.UpdateType.SET : result;
         }

         public Builder setOperation(UpdateType value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.operation_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearOperation() {
            this.bitField0_ &= -3;
            this.operation_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasValue() {
            return (this.bitField0_ & 4) != 0;
         }

         public MysqlxExpr.Expr getValue() {
            if (this.valueBuilder_ == null) {
               return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
            } else {
               return (MysqlxExpr.Expr)this.valueBuilder_.getMessage();
            }
         }

         public Builder setValue(MysqlxExpr.Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.value_ = value;
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder setValue(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.value_ = builderForValue.build();
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder mergeValue(MysqlxExpr.Expr value) {
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0 && this.value_ != null && this.value_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial();
               } else {
                  this.value_ = value;
               }

               this.onChanged();
            } else {
               this.valueBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 4;
            return this;
         }

         public Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = null;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -5;
            return this;
         }

         public MysqlxExpr.Expr.Builder getValueBuilder() {
            this.bitField0_ |= 4;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getValueFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
            if (this.valueBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder();
            } else {
               return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
            if (this.valueBuilder_ == null) {
               this.valueBuilder_ = new SingleFieldBuilderV3(this.getValue(), this.getParentForChildren(), this.isClean());
               this.value_ = null;
            }

            return this.valueBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }

      public static enum UpdateType implements ProtocolMessageEnum {
         SET(1),
         ITEM_REMOVE(2),
         ITEM_SET(3),
         ITEM_REPLACE(4),
         ITEM_MERGE(5),
         ARRAY_INSERT(6),
         ARRAY_APPEND(7),
         MERGE_PATCH(8);

         public static final int SET_VALUE = 1;
         public static final int ITEM_REMOVE_VALUE = 2;
         public static final int ITEM_SET_VALUE = 3;
         public static final int ITEM_REPLACE_VALUE = 4;
         public static final int ITEM_MERGE_VALUE = 5;
         public static final int ARRAY_INSERT_VALUE = 6;
         public static final int ARRAY_APPEND_VALUE = 7;
         public static final int MERGE_PATCH_VALUE = 8;
         private static final Internal.EnumLiteMap<UpdateType> internalValueMap = new Internal.EnumLiteMap<UpdateType>() {
            public UpdateType findValueByNumber(int number) {
               return MysqlxCrud.UpdateOperation.UpdateType.forNumber(number);
            }
         };
         private static final UpdateType[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static UpdateType valueOf(int value) {
            return forNumber(value);
         }

         public static UpdateType forNumber(int value) {
            switch (value) {
               case 1:
                  return SET;
               case 2:
                  return ITEM_REMOVE;
               case 3:
                  return ITEM_SET;
               case 4:
                  return ITEM_REPLACE;
               case 5:
                  return ITEM_MERGE;
               case 6:
                  return ARRAY_INSERT;
               case 7:
                  return ARRAY_APPEND;
               case 8:
                  return MERGE_PATCH;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<UpdateType> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxCrud.UpdateOperation.getDescriptor().getEnumTypes().get(0);
         }

         public static UpdateType valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private UpdateType(int value) {
            this.value = value;
         }
      }
   }

   public interface UpdateOperationOrBuilder extends MessageOrBuilder {
      boolean hasSource();

      MysqlxExpr.ColumnIdentifier getSource();

      MysqlxExpr.ColumnIdentifierOrBuilder getSourceOrBuilder();

      boolean hasOperation();

      UpdateOperation.UpdateType getOperation();

      boolean hasValue();

      MysqlxExpr.Expr getValue();

      MysqlxExpr.ExprOrBuilder getValueOrBuilder();
   }

   public static final class Order extends GeneratedMessageV3 implements OrderOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int EXPR_FIELD_NUMBER = 1;
      private MysqlxExpr.Expr expr_;
      public static final int DIRECTION_FIELD_NUMBER = 2;
      private int direction_;
      private byte memoizedIsInitialized;
      private static final Order DEFAULT_INSTANCE = new Order();
      /** @deprecated */
      @Deprecated
      public static final Parser<Order> PARSER = new AbstractParser<Order>() {
         public Order parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Order(input, extensionRegistry);
         }
      };

      private Order(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Order() {
         this.memoizedIsInitialized = -1;
         this.direction_ = 1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Order();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Order(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.expr_.toBuilder();
                        }

                        this.expr_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.expr_);
                           this.expr_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        Direction value = MysqlxCrud.Order.Direction.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.direction_ = rawValue;
                        }
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable.ensureFieldAccessorsInitialized(Order.class, Builder.class);
      }

      public boolean hasExpr() {
         return (this.bitField0_ & 1) != 0;
      }

      public MysqlxExpr.Expr getExpr() {
         return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
      }

      public MysqlxExpr.ExprOrBuilder getExprOrBuilder() {
         return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
      }

      public boolean hasDirection() {
         return (this.bitField0_ & 2) != 0;
      }

      public Direction getDirection() {
         Direction result = MysqlxCrud.Order.Direction.valueOf(this.direction_);
         return result == null ? MysqlxCrud.Order.Direction.ASC : result;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasExpr()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getExpr().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getExpr());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.direction_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getExpr());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.direction_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Order)) {
            return super.equals(obj);
         } else {
            Order other = (Order)obj;
            if (this.hasExpr() != other.hasExpr()) {
               return false;
            } else if (this.hasExpr() && !this.getExpr().equals(other.getExpr())) {
               return false;
            } else if (this.hasDirection() != other.hasDirection()) {
               return false;
            } else if (this.hasDirection() && this.direction_ != other.direction_) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasExpr()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getExpr().hashCode();
            }

            if (this.hasDirection()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.direction_;
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Order parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Order)PARSER.parseFrom(data);
      }

      public static Order parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Order)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Order parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Order)PARSER.parseFrom(data);
      }

      public static Order parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Order)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Order parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Order)PARSER.parseFrom(data);
      }

      public static Order parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Order)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Order parseFrom(InputStream input) throws IOException {
         return (Order)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Order parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Order)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Order parseDelimitedFrom(InputStream input) throws IOException {
         return (Order)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Order parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Order)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Order parseFrom(CodedInputStream input) throws IOException {
         return (Order)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Order parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Order)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Order prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Order getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Order> parser() {
         return PARSER;
      }

      public Parser<Order> getParserForType() {
         return PARSER;
      }

      public Order getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Order(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Order(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements OrderOrBuilder {
         private int bitField0_;
         private MysqlxExpr.Expr expr_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> exprBuilder_;
         private int direction_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Order_fieldAccessorTable.ensureFieldAccessorsInitialized(Order.class, Builder.class);
         }

         private Builder() {
            this.direction_ = 1;
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.direction_ = 1;
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Order.alwaysUseFieldBuilders) {
               this.getExprFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.exprBuilder_ == null) {
               this.expr_ = null;
            } else {
               this.exprBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.direction_ = 1;
            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Order_descriptor;
         }

         public Order getDefaultInstanceForType() {
            return MysqlxCrud.Order.getDefaultInstance();
         }

         public Order build() {
            Order result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Order buildPartial() {
            Order result = new Order(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.exprBuilder_ == null) {
                  result.expr_ = this.expr_;
               } else {
                  result.expr_ = (MysqlxExpr.Expr)this.exprBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.direction_ = this.direction_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Order) {
               return this.mergeFrom((Order)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Order other) {
            if (other == MysqlxCrud.Order.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasExpr()) {
                  this.mergeExpr(other.getExpr());
               }

               if (other.hasDirection()) {
                  this.setDirection(other.getDirection());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasExpr()) {
               return false;
            } else {
               return this.getExpr().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Order parsedMessage = null;

            try {
               parsedMessage = (Order)MysqlxCrud.Order.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Order)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasExpr() {
            return (this.bitField0_ & 1) != 0;
         }

         public MysqlxExpr.Expr getExpr() {
            if (this.exprBuilder_ == null) {
               return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
            } else {
               return (MysqlxExpr.Expr)this.exprBuilder_.getMessage();
            }
         }

         public Builder setExpr(MysqlxExpr.Expr value) {
            if (this.exprBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.expr_ = value;
               this.onChanged();
            } else {
               this.exprBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setExpr(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.exprBuilder_ == null) {
               this.expr_ = builderForValue.build();
               this.onChanged();
            } else {
               this.exprBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeExpr(MysqlxExpr.Expr value) {
            if (this.exprBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.expr_ != null && this.expr_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.expr_ = MysqlxExpr.Expr.newBuilder(this.expr_).mergeFrom(value).buildPartial();
               } else {
                  this.expr_ = value;
               }

               this.onChanged();
            } else {
               this.exprBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearExpr() {
            if (this.exprBuilder_ == null) {
               this.expr_ = null;
               this.onChanged();
            } else {
               this.exprBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.Expr.Builder getExprBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getExprFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getExprOrBuilder() {
            if (this.exprBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.exprBuilder_.getMessageOrBuilder();
            } else {
               return this.expr_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.expr_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getExprFieldBuilder() {
            if (this.exprBuilder_ == null) {
               this.exprBuilder_ = new SingleFieldBuilderV3(this.getExpr(), this.getParentForChildren(), this.isClean());
               this.expr_ = null;
            }

            return this.exprBuilder_;
         }

         public boolean hasDirection() {
            return (this.bitField0_ & 2) != 0;
         }

         public Direction getDirection() {
            Direction result = MysqlxCrud.Order.Direction.valueOf(this.direction_);
            return result == null ? MysqlxCrud.Order.Direction.ASC : result;
         }

         public Builder setDirection(Direction value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.direction_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearDirection() {
            this.bitField0_ &= -3;
            this.direction_ = 1;
            this.onChanged();
            return this;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }

      public static enum Direction implements ProtocolMessageEnum {
         ASC(1),
         DESC(2);

         public static final int ASC_VALUE = 1;
         public static final int DESC_VALUE = 2;
         private static final Internal.EnumLiteMap<Direction> internalValueMap = new Internal.EnumLiteMap<Direction>() {
            public Direction findValueByNumber(int number) {
               return MysqlxCrud.Order.Direction.forNumber(number);
            }
         };
         private static final Direction[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static Direction valueOf(int value) {
            return forNumber(value);
         }

         public static Direction forNumber(int value) {
            switch (value) {
               case 1:
                  return ASC;
               case 2:
                  return DESC;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<Direction> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxCrud.Order.getDescriptor().getEnumTypes().get(0);
         }

         public static Direction valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Direction(int value) {
            this.value = value;
         }
      }
   }

   public interface OrderOrBuilder extends MessageOrBuilder {
      boolean hasExpr();

      MysqlxExpr.Expr getExpr();

      MysqlxExpr.ExprOrBuilder getExprOrBuilder();

      boolean hasDirection();

      Order.Direction getDirection();
   }

   public static final class LimitExpr extends GeneratedMessageV3 implements LimitExprOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int ROW_COUNT_FIELD_NUMBER = 1;
      private MysqlxExpr.Expr rowCount_;
      public static final int OFFSET_FIELD_NUMBER = 2;
      private MysqlxExpr.Expr offset_;
      private byte memoizedIsInitialized;
      private static final LimitExpr DEFAULT_INSTANCE = new LimitExpr();
      /** @deprecated */
      @Deprecated
      public static final Parser<LimitExpr> PARSER = new AbstractParser<LimitExpr>() {
         public LimitExpr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new LimitExpr(input, extensionRegistry);
         }
      };

      private LimitExpr(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private LimitExpr() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new LimitExpr();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private LimitExpr(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  MysqlxExpr.Expr.Builder subBuilder;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.rowCount_.toBuilder();
                        }

                        this.rowCount_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.rowCount_);
                           this.rowCount_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.offset_.toBuilder();
                        }

                        this.offset_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.offset_);
                           this.offset_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 2;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable.ensureFieldAccessorsInitialized(LimitExpr.class, Builder.class);
      }

      public boolean hasRowCount() {
         return (this.bitField0_ & 1) != 0;
      }

      public MysqlxExpr.Expr getRowCount() {
         return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
      }

      public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() {
         return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
      }

      public boolean hasOffset() {
         return (this.bitField0_ & 2) != 0;
      }

      public MysqlxExpr.Expr getOffset() {
         return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
      }

      public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() {
         return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasRowCount()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getRowCount().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasOffset() && !this.getOffset().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getRowCount());
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(2, this.getOffset());
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getRowCount());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getOffset());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof LimitExpr)) {
            return super.equals(obj);
         } else {
            LimitExpr other = (LimitExpr)obj;
            if (this.hasRowCount() != other.hasRowCount()) {
               return false;
            } else if (this.hasRowCount() && !this.getRowCount().equals(other.getRowCount())) {
               return false;
            } else if (this.hasOffset() != other.hasOffset()) {
               return false;
            } else if (this.hasOffset() && !this.getOffset().equals(other.getOffset())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasRowCount()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getRowCount().hashCode();
            }

            if (this.hasOffset()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getOffset().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static LimitExpr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (LimitExpr)PARSER.parseFrom(data);
      }

      public static LimitExpr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
      }

      public static LimitExpr parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (LimitExpr)PARSER.parseFrom(data);
      }

      public static LimitExpr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
      }

      public static LimitExpr parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (LimitExpr)PARSER.parseFrom(data);
      }

      public static LimitExpr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (LimitExpr)PARSER.parseFrom(data, extensionRegistry);
      }

      public static LimitExpr parseFrom(InputStream input) throws IOException {
         return (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static LimitExpr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static LimitExpr parseDelimitedFrom(InputStream input) throws IOException {
         return (LimitExpr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static LimitExpr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (LimitExpr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static LimitExpr parseFrom(CodedInputStream input) throws IOException {
         return (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static LimitExpr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (LimitExpr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(LimitExpr prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static LimitExpr getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<LimitExpr> parser() {
         return PARSER;
      }

      public Parser<LimitExpr> getParserForType() {
         return PARSER;
      }

      public LimitExpr getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      LimitExpr(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      LimitExpr(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements LimitExprOrBuilder {
         private int bitField0_;
         private MysqlxExpr.Expr rowCount_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> rowCountBuilder_;
         private MysqlxExpr.Expr offset_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> offsetBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_fieldAccessorTable.ensureFieldAccessorsInitialized(LimitExpr.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.LimitExpr.alwaysUseFieldBuilders) {
               this.getRowCountFieldBuilder();
               this.getOffsetFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.rowCountBuilder_ == null) {
               this.rowCount_ = null;
            } else {
               this.rowCountBuilder_.clear();
            }

            this.bitField0_ &= -2;
            if (this.offsetBuilder_ == null) {
               this.offset_ = null;
            } else {
               this.offsetBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_LimitExpr_descriptor;
         }

         public LimitExpr getDefaultInstanceForType() {
            return MysqlxCrud.LimitExpr.getDefaultInstance();
         }

         public LimitExpr build() {
            LimitExpr result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public LimitExpr buildPartial() {
            LimitExpr result = new LimitExpr(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.rowCountBuilder_ == null) {
                  result.rowCount_ = this.rowCount_;
               } else {
                  result.rowCount_ = (MysqlxExpr.Expr)this.rowCountBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               if (this.offsetBuilder_ == null) {
                  result.offset_ = this.offset_;
               } else {
                  result.offset_ = (MysqlxExpr.Expr)this.offsetBuilder_.build();
               }

               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof LimitExpr) {
               return this.mergeFrom((LimitExpr)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(LimitExpr other) {
            if (other == MysqlxCrud.LimitExpr.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasRowCount()) {
                  this.mergeRowCount(other.getRowCount());
               }

               if (other.hasOffset()) {
                  this.mergeOffset(other.getOffset());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasRowCount()) {
               return false;
            } else if (!this.getRowCount().isInitialized()) {
               return false;
            } else {
               return !this.hasOffset() || this.getOffset().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            LimitExpr parsedMessage = null;

            try {
               parsedMessage = (LimitExpr)MysqlxCrud.LimitExpr.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (LimitExpr)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasRowCount() {
            return (this.bitField0_ & 1) != 0;
         }

         public MysqlxExpr.Expr getRowCount() {
            if (this.rowCountBuilder_ == null) {
               return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
            } else {
               return (MysqlxExpr.Expr)this.rowCountBuilder_.getMessage();
            }
         }

         public Builder setRowCount(MysqlxExpr.Expr value) {
            if (this.rowCountBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.rowCount_ = value;
               this.onChanged();
            } else {
               this.rowCountBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setRowCount(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.rowCountBuilder_ == null) {
               this.rowCount_ = builderForValue.build();
               this.onChanged();
            } else {
               this.rowCountBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeRowCount(MysqlxExpr.Expr value) {
            if (this.rowCountBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.rowCount_ != null && this.rowCount_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.rowCount_ = MysqlxExpr.Expr.newBuilder(this.rowCount_).mergeFrom(value).buildPartial();
               } else {
                  this.rowCount_ = value;
               }

               this.onChanged();
            } else {
               this.rowCountBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearRowCount() {
            if (this.rowCountBuilder_ == null) {
               this.rowCount_ = null;
               this.onChanged();
            } else {
               this.rowCountBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.Expr.Builder getRowCountBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getRowCountFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getRowCountOrBuilder() {
            if (this.rowCountBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.rowCountBuilder_.getMessageOrBuilder();
            } else {
               return this.rowCount_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.rowCount_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getRowCountFieldBuilder() {
            if (this.rowCountBuilder_ == null) {
               this.rowCountBuilder_ = new SingleFieldBuilderV3(this.getRowCount(), this.getParentForChildren(), this.isClean());
               this.rowCount_ = null;
            }

            return this.rowCountBuilder_;
         }

         public boolean hasOffset() {
            return (this.bitField0_ & 2) != 0;
         }

         public MysqlxExpr.Expr getOffset() {
            if (this.offsetBuilder_ == null) {
               return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
            } else {
               return (MysqlxExpr.Expr)this.offsetBuilder_.getMessage();
            }
         }

         public Builder setOffset(MysqlxExpr.Expr value) {
            if (this.offsetBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.offset_ = value;
               this.onChanged();
            } else {
               this.offsetBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder setOffset(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.offsetBuilder_ == null) {
               this.offset_ = builderForValue.build();
               this.onChanged();
            } else {
               this.offsetBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder mergeOffset(MysqlxExpr.Expr value) {
            if (this.offsetBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.offset_ != null && this.offset_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.offset_ = MysqlxExpr.Expr.newBuilder(this.offset_).mergeFrom(value).buildPartial();
               } else {
                  this.offset_ = value;
               }

               this.onChanged();
            } else {
               this.offsetBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder clearOffset() {
            if (this.offsetBuilder_ == null) {
               this.offset_ = null;
               this.onChanged();
            } else {
               this.offsetBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxExpr.Expr.Builder getOffsetBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getOffsetFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getOffsetOrBuilder() {
            if (this.offsetBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.offsetBuilder_.getMessageOrBuilder();
            } else {
               return this.offset_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.offset_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getOffsetFieldBuilder() {
            if (this.offsetBuilder_ == null) {
               this.offsetBuilder_ = new SingleFieldBuilderV3(this.getOffset(), this.getParentForChildren(), this.isClean());
               this.offset_ = null;
            }

            return this.offsetBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface LimitExprOrBuilder extends MessageOrBuilder {
      boolean hasRowCount();

      MysqlxExpr.Expr getRowCount();

      MysqlxExpr.ExprOrBuilder getRowCountOrBuilder();

      boolean hasOffset();

      MysqlxExpr.Expr getOffset();

      MysqlxExpr.ExprOrBuilder getOffsetOrBuilder();
   }

   public static final class Limit extends GeneratedMessageV3 implements LimitOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int ROW_COUNT_FIELD_NUMBER = 1;
      private long rowCount_;
      public static final int OFFSET_FIELD_NUMBER = 2;
      private long offset_;
      private byte memoizedIsInitialized;
      private static final Limit DEFAULT_INSTANCE = new Limit();
      /** @deprecated */
      @Deprecated
      public static final Parser<Limit> PARSER = new AbstractParser<Limit>() {
         public Limit parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Limit(input, extensionRegistry);
         }
      };

      private Limit(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Limit() {
         this.memoizedIsInitialized = -1;
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Limit();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Limit(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        this.bitField0_ |= 1;
                        this.rowCount_ = input.readUInt64();
                        break;
                     case 16:
                        this.bitField0_ |= 2;
                        this.offset_ = input.readUInt64();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var11) {
               throw var11.setUnfinishedMessage(this);
            } catch (IOException var12) {
               throw (new InvalidProtocolBufferException(var12)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable.ensureFieldAccessorsInitialized(Limit.class, Builder.class);
      }

      public boolean hasRowCount() {
         return (this.bitField0_ & 1) != 0;
      }

      public long getRowCount() {
         return this.rowCount_;
      }

      public boolean hasOffset() {
         return (this.bitField0_ & 2) != 0;
      }

      public long getOffset() {
         return this.offset_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasRowCount()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeUInt64(1, this.rowCount_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeUInt64(2, this.offset_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeUInt64Size(1, this.rowCount_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeUInt64Size(2, this.offset_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Limit)) {
            return super.equals(obj);
         } else {
            Limit other = (Limit)obj;
            if (this.hasRowCount() != other.hasRowCount()) {
               return false;
            } else if (this.hasRowCount() && this.getRowCount() != other.getRowCount()) {
               return false;
            } else if (this.hasOffset() != other.hasOffset()) {
               return false;
            } else if (this.hasOffset() && this.getOffset() != other.getOffset()) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasRowCount()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + Internal.hashLong(this.getRowCount());
            }

            if (this.hasOffset()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + Internal.hashLong(this.getOffset());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Limit parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Limit)PARSER.parseFrom(data);
      }

      public static Limit parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Limit)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Limit parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Limit)PARSER.parseFrom(data);
      }

      public static Limit parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Limit)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Limit parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Limit)PARSER.parseFrom(data);
      }

      public static Limit parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Limit)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Limit parseFrom(InputStream input) throws IOException {
         return (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Limit parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Limit parseDelimitedFrom(InputStream input) throws IOException {
         return (Limit)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Limit parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Limit)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Limit parseFrom(CodedInputStream input) throws IOException {
         return (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Limit parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Limit)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Limit prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Limit getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Limit> parser() {
         return PARSER;
      }

      public Parser<Limit> getParserForType() {
         return PARSER;
      }

      public Limit getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Limit(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Limit(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements LimitOrBuilder {
         private int bitField0_;
         private long rowCount_;
         private long offset_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_fieldAccessorTable.ensureFieldAccessorsInitialized(Limit.class, Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Limit.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.rowCount_ = 0L;
            this.bitField0_ &= -2;
            this.offset_ = 0L;
            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Limit_descriptor;
         }

         public Limit getDefaultInstanceForType() {
            return MysqlxCrud.Limit.getDefaultInstance();
         }

         public Limit build() {
            Limit result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Limit buildPartial() {
            Limit result = new Limit(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.rowCount_ = this.rowCount_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.offset_ = this.offset_;
               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Limit) {
               return this.mergeFrom((Limit)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Limit other) {
            if (other == MysqlxCrud.Limit.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasRowCount()) {
                  this.setRowCount(other.getRowCount());
               }

               if (other.hasOffset()) {
                  this.setOffset(other.getOffset());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasRowCount();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Limit parsedMessage = null;

            try {
               parsedMessage = (Limit)MysqlxCrud.Limit.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Limit)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasRowCount() {
            return (this.bitField0_ & 1) != 0;
         }

         public long getRowCount() {
            return this.rowCount_;
         }

         public Builder setRowCount(long value) {
            this.bitField0_ |= 1;
            this.rowCount_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearRowCount() {
            this.bitField0_ &= -2;
            this.rowCount_ = 0L;
            this.onChanged();
            return this;
         }

         public boolean hasOffset() {
            return (this.bitField0_ & 2) != 0;
         }

         public long getOffset() {
            return this.offset_;
         }

         public Builder setOffset(long value) {
            this.bitField0_ |= 2;
            this.offset_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearOffset() {
            this.bitField0_ &= -3;
            this.offset_ = 0L;
            this.onChanged();
            return this;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface LimitOrBuilder extends MessageOrBuilder {
      boolean hasRowCount();

      long getRowCount();

      boolean hasOffset();

      long getOffset();
   }

   public static final class Collection extends GeneratedMessageV3 implements CollectionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile Object name_;
      public static final int SCHEMA_FIELD_NUMBER = 2;
      private volatile Object schema_;
      private byte memoizedIsInitialized;
      private static final Collection DEFAULT_INSTANCE = new Collection();
      /** @deprecated */
      @Deprecated
      public static final Parser<Collection> PARSER = new AbstractParser<Collection>() {
         public Collection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Collection(input, extensionRegistry);
         }
      };

      private Collection(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Collection() {
         this.memoizedIsInitialized = -1;
         this.name_ = "";
         this.schema_ = "";
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Collection();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Collection(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  ByteString bs;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     case 18:
                        bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.schema_ = bs;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable.ensureFieldAccessorsInitialized(Collection.class, Builder.class);
      }

      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getName() {
         Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      public ByteString getNameBytes() {
         Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasSchema() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getSchema() {
         Object ref = this.schema_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.schema_ = s;
            }

            return s;
         }
      }

      public ByteString getSchemaBytes() {
         Object ref = this.schema_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.schema_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.schema_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.schema_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Collection)) {
            return super.equals(obj);
         } else {
            Collection other = (Collection)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasSchema() != other.hasSchema()) {
               return false;
            } else if (this.hasSchema() && !this.getSchema().equals(other.getSchema())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasSchema()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getSchema().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Collection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Collection)PARSER.parseFrom(data);
      }

      public static Collection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Collection)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Collection parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Collection)PARSER.parseFrom(data);
      }

      public static Collection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Collection)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Collection parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Collection)PARSER.parseFrom(data);
      }

      public static Collection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Collection)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Collection parseFrom(InputStream input) throws IOException {
         return (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Collection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Collection parseDelimitedFrom(InputStream input) throws IOException {
         return (Collection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Collection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Collection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Collection parseFrom(CodedInputStream input) throws IOException {
         return (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Collection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Collection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Collection prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Collection getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Collection> parser() {
         return PARSER;
      }

      public Parser<Collection> getParserForType() {
         return PARSER;
      }

      public Collection getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Collection(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Collection(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements CollectionOrBuilder {
         private int bitField0_;
         private Object name_;
         private Object schema_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_fieldAccessorTable.ensureFieldAccessorsInitialized(Collection.class, Builder.class);
         }

         private Builder() {
            this.name_ = "";
            this.schema_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.name_ = "";
            this.schema_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Collection.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            this.schema_ = "";
            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Collection_descriptor;
         }

         public Collection getDefaultInstanceForType() {
            return MysqlxCrud.Collection.getDefaultInstance();
         }

         public Collection build() {
            Collection result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Collection buildPartial() {
            Collection result = new Collection(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.schema_ = this.schema_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Collection) {
               return this.mergeFrom((Collection)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Collection other) {
            if (other == MysqlxCrud.Collection.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasSchema()) {
                  this.bitField0_ |= 2;
                  this.schema_ = other.schema_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasName();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Collection parsedMessage = null;

            try {
               parsedMessage = (Collection)MysqlxCrud.Collection.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Collection)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getName() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getNameBytes() {
            Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxCrud.Collection.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasSchema() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getSchema() {
            Object ref = this.schema_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.schema_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getSchemaBytes() {
            Object ref = this.schema_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.schema_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setSchema(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schema_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearSchema() {
            this.bitField0_ &= -3;
            this.schema_ = MysqlxCrud.Collection.getDefaultInstance().getSchema();
            this.onChanged();
            return this;
         }

         public Builder setSchemaBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schema_ = value;
               this.onChanged();
               return this;
            }
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface CollectionOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasSchema();

      String getSchema();

      ByteString getSchemaBytes();
   }

   public static final class Projection extends GeneratedMessageV3 implements ProjectionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int SOURCE_FIELD_NUMBER = 1;
      private MysqlxExpr.Expr source_;
      public static final int ALIAS_FIELD_NUMBER = 2;
      private volatile Object alias_;
      private byte memoizedIsInitialized;
      private static final Projection DEFAULT_INSTANCE = new Projection();
      /** @deprecated */
      @Deprecated
      public static final Parser<Projection> PARSER = new AbstractParser<Projection>() {
         public Projection parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Projection(input, extensionRegistry);
         }
      };

      private Projection(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Projection() {
         this.memoizedIsInitialized = -1;
         this.alias_ = "";
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Projection();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Projection(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.Expr.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.source_.toBuilder();
                        }

                        this.source_ = (MysqlxExpr.Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.source_);
                           this.source_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.alias_ = bs;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable.ensureFieldAccessorsInitialized(Projection.class, Builder.class);
      }

      public boolean hasSource() {
         return (this.bitField0_ & 1) != 0;
      }

      public MysqlxExpr.Expr getSource() {
         return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
      }

      public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() {
         return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
      }

      public boolean hasAlias() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getAlias() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.alias_ = s;
            }

            return s;
         }
      }

      public ByteString getAliasBytes() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.alias_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasSource()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getSource().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getSource());
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.alias_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getSource());
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.alias_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Projection)) {
            return super.equals(obj);
         } else {
            Projection other = (Projection)obj;
            if (this.hasSource() != other.hasSource()) {
               return false;
            } else if (this.hasSource() && !this.getSource().equals(other.getSource())) {
               return false;
            } else if (this.hasAlias() != other.hasAlias()) {
               return false;
            } else if (this.hasAlias() && !this.getAlias().equals(other.getAlias())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasSource()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getSource().hashCode();
            }

            if (this.hasAlias()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getAlias().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Projection parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Projection)PARSER.parseFrom(data);
      }

      public static Projection parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Projection)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Projection parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Projection)PARSER.parseFrom(data);
      }

      public static Projection parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Projection)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Projection parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Projection)PARSER.parseFrom(data);
      }

      public static Projection parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Projection)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Projection parseFrom(InputStream input) throws IOException {
         return (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Projection parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Projection parseDelimitedFrom(InputStream input) throws IOException {
         return (Projection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Projection parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Projection)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Projection parseFrom(CodedInputStream input) throws IOException {
         return (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Projection parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Projection)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Projection prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Projection getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Projection> parser() {
         return PARSER;
      }

      public Parser<Projection> getParserForType() {
         return PARSER;
      }

      public Projection getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Projection(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Projection(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ProjectionOrBuilder {
         private int bitField0_;
         private MysqlxExpr.Expr source_;
         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> sourceBuilder_;
         private Object alias_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_fieldAccessorTable.ensureFieldAccessorsInitialized(Projection.class, Builder.class);
         }

         private Builder() {
            this.alias_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.alias_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Projection.alwaysUseFieldBuilders) {
               this.getSourceFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            this.alias_ = "";
            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Projection_descriptor;
         }

         public Projection getDefaultInstanceForType() {
            return MysqlxCrud.Projection.getDefaultInstance();
         }

         public Projection build() {
            Projection result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Projection buildPartial() {
            Projection result = new Projection(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.sourceBuilder_ == null) {
                  result.source_ = this.source_;
               } else {
                  result.source_ = (MysqlxExpr.Expr)this.sourceBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.alias_ = this.alias_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Projection) {
               return this.mergeFrom((Projection)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Projection other) {
            if (other == MysqlxCrud.Projection.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasSource()) {
                  this.mergeSource(other.getSource());
               }

               if (other.hasAlias()) {
                  this.bitField0_ |= 2;
                  this.alias_ = other.alias_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasSource()) {
               return false;
            } else {
               return this.getSource().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Projection parsedMessage = null;

            try {
               parsedMessage = (Projection)MysqlxCrud.Projection.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Projection)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasSource() {
            return (this.bitField0_ & 1) != 0;
         }

         public MysqlxExpr.Expr getSource() {
            if (this.sourceBuilder_ == null) {
               return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
            } else {
               return (MysqlxExpr.Expr)this.sourceBuilder_.getMessage();
            }
         }

         public Builder setSource(MysqlxExpr.Expr value) {
            if (this.sourceBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.source_ = value;
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setSource(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.sourceBuilder_ == null) {
               this.source_ = builderForValue.build();
               this.onChanged();
            } else {
               this.sourceBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeSource(MysqlxExpr.Expr value) {
            if (this.sourceBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.source_ != null && this.source_ != MysqlxExpr.Expr.getDefaultInstance()) {
                  this.source_ = MysqlxExpr.Expr.newBuilder(this.source_).mergeFrom(value).buildPartial();
               } else {
                  this.source_ = value;
               }

               this.onChanged();
            } else {
               this.sourceBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearSource() {
            if (this.sourceBuilder_ == null) {
               this.source_ = null;
               this.onChanged();
            } else {
               this.sourceBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.Expr.Builder getSourceBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (MysqlxExpr.Expr.Builder)this.getSourceFieldBuilder().getBuilder();
         }

         public MysqlxExpr.ExprOrBuilder getSourceOrBuilder() {
            if (this.sourceBuilder_ != null) {
               return (MysqlxExpr.ExprOrBuilder)this.sourceBuilder_.getMessageOrBuilder();
            } else {
               return this.source_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.source_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getSourceFieldBuilder() {
            if (this.sourceBuilder_ == null) {
               this.sourceBuilder_ = new SingleFieldBuilderV3(this.getSource(), this.getParentForChildren(), this.isClean());
               this.source_ = null;
            }

            return this.sourceBuilder_;
         }

         public boolean hasAlias() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getAlias() {
            Object ref = this.alias_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.alias_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getAliasBytes() {
            Object ref = this.alias_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.alias_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setAlias(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearAlias() {
            this.bitField0_ &= -3;
            this.alias_ = MysqlxCrud.Projection.getDefaultInstance().getAlias();
            this.onChanged();
            return this;
         }

         public Builder setAliasBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface ProjectionOrBuilder extends MessageOrBuilder {
      boolean hasSource();

      MysqlxExpr.Expr getSource();

      MysqlxExpr.ExprOrBuilder getSourceOrBuilder();

      boolean hasAlias();

      String getAlias();

      ByteString getAliasBytes();
   }

   public static final class Column extends GeneratedMessageV3 implements ColumnOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile Object name_;
      public static final int ALIAS_FIELD_NUMBER = 2;
      private volatile Object alias_;
      public static final int DOCUMENT_PATH_FIELD_NUMBER = 3;
      private List<MysqlxExpr.DocumentPathItem> documentPath_;
      private byte memoizedIsInitialized;
      private static final Column DEFAULT_INSTANCE = new Column();
      /** @deprecated */
      @Deprecated
      public static final Parser<Column> PARSER = new AbstractParser<Column>() {
         public Column parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Column(input, extensionRegistry);
         }
      };

      private Column(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Column() {
         this.memoizedIsInitialized = -1;
         this.name_ = "";
         this.alias_ = "";
         this.documentPath_ = Collections.emptyList();
      }

      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Column();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Column(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  ByteString bs;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     case 18:
                        bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.alias_ = bs;
                        break;
                     case 26:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.documentPath_ = new ArrayList();
                           mutable_bitField0_ |= 4;
                        }

                        this.documentPath_.add(input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 4) != 0) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable.ensureFieldAccessorsInitialized(Column.class, Builder.class);
      }

      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getName() {
         Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      public ByteString getNameBytes() {
         Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasAlias() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getAlias() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.alias_ = s;
            }

            return s;
         }
      }

      public ByteString getAliasBytes() {
         Object ref = this.alias_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.alias_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
         return this.documentPath_;
      }

      public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
         return this.documentPath_;
      }

      public int getDocumentPathCount() {
         return this.documentPath_.size();
      }

      public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
         return (MysqlxExpr.DocumentPathItem)this.documentPath_.get(index);
      }

      public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
         return (MysqlxExpr.DocumentPathItemOrBuilder)this.documentPath_.get(index);
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for(int i = 0; i < this.getDocumentPathCount(); ++i) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.alias_);
         }

         for(int i = 0; i < this.documentPath_.size(); ++i) {
            output.writeMessage(3, (MessageLite)this.documentPath_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.alias_);
            }

            for(int i = 0; i < this.documentPath_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(3, (MessageLite)this.documentPath_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Column)) {
            return super.equals(obj);
         } else {
            Column other = (Column)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasAlias() != other.hasAlias()) {
               return false;
            } else if (this.hasAlias() && !this.getAlias().equals(other.getAlias())) {
               return false;
            } else if (!this.getDocumentPathList().equals(other.getDocumentPathList())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasAlias()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getAlias().hashCode();
            }

            if (this.getDocumentPathCount() > 0) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getDocumentPathList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Column parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Column)PARSER.parseFrom(data);
      }

      public static Column parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Column)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Column parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Column)PARSER.parseFrom(data);
      }

      public static Column parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Column)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Column parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Column)PARSER.parseFrom(data);
      }

      public static Column parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Column)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Column parseFrom(InputStream input) throws IOException {
         return (Column)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Column parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Column)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Column parseDelimitedFrom(InputStream input) throws IOException {
         return (Column)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Column parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Column)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Column parseFrom(CodedInputStream input) throws IOException {
         return (Column)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Column parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Column)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Column prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Column getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Column> parser() {
         return PARSER;
      }

      public Parser<Column> getParserForType() {
         return PARSER;
      }

      public Column getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Column(GeneratedMessageV3.Builder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Column(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ColumnOrBuilder {
         private int bitField0_;
         private Object name_;
         private Object alias_;
         private List<MysqlxExpr.DocumentPathItem> documentPath_;
         private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> documentPathBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Column_fieldAccessorTable.ensureFieldAccessorsInitialized(Column.class, Builder.class);
         }

         private Builder() {
            this.name_ = "";
            this.alias_ = "";
            this.documentPath_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.name_ = "";
            this.alias_ = "";
            this.documentPath_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCrud.Column.alwaysUseFieldBuilders) {
               this.getDocumentPathFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            this.alias_ = "";
            this.bitField0_ &= -3;
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.documentPathBuilder_.clear();
            }

            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCrud.internal_static_Mysqlx_Crud_Column_descriptor;
         }

         public Column getDefaultInstanceForType() {
            return MysqlxCrud.Column.getDefaultInstance();
         }

         public Column build() {
            Column result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Column buildPartial() {
            Column result = new Column(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.alias_ = this.alias_;
            if (this.documentPathBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
                  this.bitField0_ &= -5;
               }

               result.documentPath_ = this.documentPath_;
            } else {
               result.documentPath_ = this.documentPathBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Column) {
               return this.mergeFrom((Column)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Column other) {
            if (other == MysqlxCrud.Column.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasAlias()) {
                  this.bitField0_ |= 2;
                  this.alias_ = other.alias_;
                  this.onChanged();
               }

               if (this.documentPathBuilder_ == null) {
                  if (!other.documentPath_.isEmpty()) {
                     if (this.documentPath_.isEmpty()) {
                        this.documentPath_ = other.documentPath_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureDocumentPathIsMutable();
                        this.documentPath_.addAll(other.documentPath_);
                     }

                     this.onChanged();
                  }
               } else if (!other.documentPath_.isEmpty()) {
                  if (this.documentPathBuilder_.isEmpty()) {
                     this.documentPathBuilder_.dispose();
                     this.documentPathBuilder_ = null;
                     this.documentPath_ = other.documentPath_;
                     this.bitField0_ &= -5;
                     this.documentPathBuilder_ = MysqlxCrud.Column.alwaysUseFieldBuilders ? this.getDocumentPathFieldBuilder() : null;
                  } else {
                     this.documentPathBuilder_.addAllMessages(other.documentPath_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            for(int i = 0; i < this.getDocumentPathCount(); ++i) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Column parsedMessage = null;

            try {
               parsedMessage = (Column)MysqlxCrud.Column.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Column)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getName() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getNameBytes() {
            Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxCrud.Column.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasAlias() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getAlias() {
            Object ref = this.alias_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.alias_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getAliasBytes() {
            Object ref = this.alias_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.alias_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setAlias(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearAlias() {
            this.bitField0_ &= -3;
            this.alias_ = MysqlxCrud.Column.getDefaultInstance().getAlias();
            this.onChanged();
            return this;
         }

         public Builder setAliasBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.alias_ = value;
               this.onChanged();
               return this;
            }
         }

         private void ensureDocumentPathIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.documentPath_ = new ArrayList(this.documentPath_);
               this.bitField0_ |= 4;
            }

         }

         public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
            return this.documentPathBuilder_ == null ? Collections.unmodifiableList(this.documentPath_) : this.documentPathBuilder_.getMessageList();
         }

         public int getDocumentPathCount() {
            return this.documentPathBuilder_ == null ? this.documentPath_.size() : this.documentPathBuilder_.getCount();
         }

         public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
            return this.documentPathBuilder_ == null ? (MysqlxExpr.DocumentPathItem)this.documentPath_.get(index) : (MysqlxExpr.DocumentPathItem)this.documentPathBuilder_.getMessage(index);
         }

         public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addDocumentPath(MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addDocumentPath(MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllDocumentPath(Iterable<? extends MysqlxExpr.DocumentPathItem> values) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.documentPath_);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearDocumentPath() {
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.documentPathBuilder_.clear();
            }

            return this;
         }

         public Builder removeDocumentPath(int index) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.remove(index);
               this.onChanged();
            } else {
               this.documentPathBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.DocumentPathItem.Builder getDocumentPathBuilder(int index) {
            return (MysqlxExpr.DocumentPathItem.Builder)this.getDocumentPathFieldBuilder().getBuilder(index);
         }

         public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
            return this.documentPathBuilder_ == null ? (MysqlxExpr.DocumentPathItemOrBuilder)this.documentPath_.get(index) : (MysqlxExpr.DocumentPathItemOrBuilder)this.documentPathBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
            return this.documentPathBuilder_ != null ? this.documentPathBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.documentPath_);
         }

         public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder() {
            return (MysqlxExpr.DocumentPathItem.Builder)this.getDocumentPathFieldBuilder().addBuilder(MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder(int index) {
            return (MysqlxExpr.DocumentPathItem.Builder)this.getDocumentPathFieldBuilder().addBuilder(index, MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public List<MysqlxExpr.DocumentPathItem.Builder> getDocumentPathBuilderList() {
            return this.getDocumentPathFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() {
            if (this.documentPathBuilder_ == null) {
               this.documentPathBuilder_ = new RepeatedFieldBuilderV3(this.documentPath_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.documentPath_ = null;
            }

            return this.documentPathBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
            this(x0);
         }
      }
   }

   public interface ColumnOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasAlias();

      String getAlias();

      ByteString getAliasBytes();

      List<MysqlxExpr.DocumentPathItem> getDocumentPathList();

      MysqlxExpr.DocumentPathItem getDocumentPath(int var1);

      int getDocumentPathCount();

      List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();

      MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int var1);
   }

   public static enum ViewCheckOption implements ProtocolMessageEnum {
      LOCAL(1),
      CASCADED(2);

      public static final int LOCAL_VALUE = 1;
      public static final int CASCADED_VALUE = 2;
      private static final Internal.EnumLiteMap<ViewCheckOption> internalValueMap = new Internal.EnumLiteMap<ViewCheckOption>() {
         public ViewCheckOption findValueByNumber(int number) {
            return MysqlxCrud.ViewCheckOption.forNumber(number);
         }
      };
      private static final ViewCheckOption[] VALUES = values();
      private final int value;

      public final int getNumber() {
         return this.value;
      }

      /** @deprecated */
      @Deprecated
      public static ViewCheckOption valueOf(int value) {
         return forNumber(value);
      }

      public static ViewCheckOption forNumber(int value) {
         switch (value) {
            case 1:
               return LOCAL;
            case 2:
               return CASCADED;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<ViewCheckOption> internalGetValueMap() {
         return internalValueMap;
      }

      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
      }

      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return (Descriptors.EnumDescriptor)MysqlxCrud.getDescriptor().getEnumTypes().get(3);
      }

      public static ViewCheckOption valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ViewCheckOption(int value) {
         this.value = value;
      }
   }

   public static enum ViewSqlSecurity implements ProtocolMessageEnum {
      INVOKER(1),
      DEFINER(2);

      public static final int INVOKER_VALUE = 1;
      public static final int DEFINER_VALUE = 2;
      private static final Internal.EnumLiteMap<ViewSqlSecurity> internalValueMap = new Internal.EnumLiteMap<ViewSqlSecurity>() {
         public ViewSqlSecurity findValueByNumber(int number) {
            return MysqlxCrud.ViewSqlSecurity.forNumber(number);
         }
      };
      private static final ViewSqlSecurity[] VALUES = values();
      private final int value;

      public final int getNumber() {
         return this.value;
      }

      /** @deprecated */
      @Deprecated
      public static ViewSqlSecurity valueOf(int value) {
         return forNumber(value);
      }

      public static ViewSqlSecurity forNumber(int value) {
         switch (value) {
            case 1:
               return INVOKER;
            case 2:
               return DEFINER;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<ViewSqlSecurity> internalGetValueMap() {
         return internalValueMap;
      }

      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
      }

      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return (Descriptors.EnumDescriptor)MysqlxCrud.getDescriptor().getEnumTypes().get(2);
      }

      public static ViewSqlSecurity valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ViewSqlSecurity(int value) {
         this.value = value;
      }
   }

   public static enum ViewAlgorithm implements ProtocolMessageEnum {
      UNDEFINED(1),
      MERGE(2),
      TEMPTABLE(3);

      public static final int UNDEFINED_VALUE = 1;
      public static final int MERGE_VALUE = 2;
      public static final int TEMPTABLE_VALUE = 3;
      private static final Internal.EnumLiteMap<ViewAlgorithm> internalValueMap = new Internal.EnumLiteMap<ViewAlgorithm>() {
         public ViewAlgorithm findValueByNumber(int number) {
            return MysqlxCrud.ViewAlgorithm.forNumber(number);
         }
      };
      private static final ViewAlgorithm[] VALUES = values();
      private final int value;

      public final int getNumber() {
         return this.value;
      }

      /** @deprecated */
      @Deprecated
      public static ViewAlgorithm valueOf(int value) {
         return forNumber(value);
      }

      public static ViewAlgorithm forNumber(int value) {
         switch (value) {
            case 1:
               return UNDEFINED;
            case 2:
               return MERGE;
            case 3:
               return TEMPTABLE;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<ViewAlgorithm> internalGetValueMap() {
         return internalValueMap;
      }

      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
      }

      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return (Descriptors.EnumDescriptor)MysqlxCrud.getDescriptor().getEnumTypes().get(1);
      }

      public static ViewAlgorithm valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ViewAlgorithm(int value) {
         this.value = value;
      }
   }

   public static enum DataModel implements ProtocolMessageEnum {
      DOCUMENT(1),
      TABLE(2);

      public static final int DOCUMENT_VALUE = 1;
      public static final int TABLE_VALUE = 2;
      private static final Internal.EnumLiteMap<DataModel> internalValueMap = new Internal.EnumLiteMap<DataModel>() {
         public DataModel findValueByNumber(int number) {
            return MysqlxCrud.DataModel.forNumber(number);
         }
      };
      private static final DataModel[] VALUES = values();
      private final int value;

      public final int getNumber() {
         return this.value;
      }

      /** @deprecated */
      @Deprecated
      public static DataModel valueOf(int value) {
         return forNumber(value);
      }

      public static DataModel forNumber(int value) {
         switch (value) {
            case 1:
               return DOCUMENT;
            case 2:
               return TABLE;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<DataModel> internalGetValueMap() {
         return internalValueMap;
      }

      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
      }

      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return (Descriptors.EnumDescriptor)MysqlxCrud.getDescriptor().getEnumTypes().get(0);
      }

      public static DataModel valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private DataModel(int value) {
         this.value = value;
      }
   }
}
