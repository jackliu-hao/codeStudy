package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class ExtensionSchemaLite extends ExtensionSchema<GeneratedMessageLite.ExtensionDescriptor> {
   boolean hasExtensions(MessageLite prototype) {
      return prototype instanceof GeneratedMessageLite.ExtendableMessage;
   }

   FieldSet<GeneratedMessageLite.ExtensionDescriptor> getExtensions(Object message) {
      return ((GeneratedMessageLite.ExtendableMessage)message).extensions;
   }

   void setExtensions(Object message, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
      ((GeneratedMessageLite.ExtendableMessage)message).extensions = extensions;
   }

   FieldSet<GeneratedMessageLite.ExtensionDescriptor> getMutableExtensions(Object message) {
      return ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
   }

   void makeImmutable(Object message) {
      this.getExtensions(message).makeImmutable();
   }

   <UT, UB> UB parseExtension(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) throws IOException {
      GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension)extensionObject;
      int fieldNumber = extension.getNumber();
      Object value;
      if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
         value = null;
         ArrayList list;
         ArrayList value;
         switch (extension.getLiteType()) {
            case DOUBLE:
               list = new ArrayList();
               reader.readDoubleList(list);
               value = list;
               break;
            case FLOAT:
               list = new ArrayList();
               reader.readFloatList(list);
               value = list;
               break;
            case INT64:
               list = new ArrayList();
               reader.readInt64List(list);
               value = list;
               break;
            case UINT64:
               list = new ArrayList();
               reader.readUInt64List(list);
               value = list;
               break;
            case INT32:
               list = new ArrayList();
               reader.readInt32List(list);
               value = list;
               break;
            case FIXED64:
               list = new ArrayList();
               reader.readFixed64List(list);
               value = list;
               break;
            case FIXED32:
               list = new ArrayList();
               reader.readFixed32List(list);
               value = list;
               break;
            case BOOL:
               list = new ArrayList();
               reader.readBoolList(list);
               value = list;
               break;
            case UINT32:
               list = new ArrayList();
               reader.readUInt32List(list);
               value = list;
               break;
            case SFIXED32:
               list = new ArrayList();
               reader.readSFixed32List(list);
               value = list;
               break;
            case SFIXED64:
               list = new ArrayList();
               reader.readSFixed64List(list);
               value = list;
               break;
            case SINT32:
               list = new ArrayList();
               reader.readSInt32List(list);
               value = list;
               break;
            case SINT64:
               list = new ArrayList();
               reader.readSInt64List(list);
               value = list;
               break;
            case ENUM:
               list = new ArrayList();
               reader.readEnumList(list);
               unknownFields = SchemaUtil.filterUnknownEnumList(fieldNumber, list, (Internal.EnumLiteMap)extension.descriptor.getEnumType(), unknownFields, unknownFieldSchema);
               value = list;
               break;
            default:
               throw new IllegalStateException("Type cannot be packed: " + extension.descriptor.getLiteType());
         }

         extensions.setField(extension.descriptor, value);
      } else {
         value = null;
         if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
            int number = reader.readInt32();
            Object enumValue = extension.descriptor.getEnumType().findValueByNumber(number);
            if (enumValue == null) {
               return SchemaUtil.storeUnknownEnum(fieldNumber, number, unknownFields, unknownFieldSchema);
            }

            value = number;
         } else {
            switch (extension.getLiteType()) {
               case DOUBLE:
                  value = reader.readDouble();
                  break;
               case FLOAT:
                  value = reader.readFloat();
                  break;
               case INT64:
                  value = reader.readInt64();
                  break;
               case UINT64:
                  value = reader.readUInt64();
                  break;
               case INT32:
                  value = reader.readInt32();
                  break;
               case FIXED64:
                  value = reader.readFixed64();
                  break;
               case FIXED32:
                  value = reader.readFixed32();
                  break;
               case BOOL:
                  value = reader.readBool();
                  break;
               case UINT32:
                  value = reader.readUInt32();
                  break;
               case SFIXED32:
                  value = reader.readSFixed32();
                  break;
               case SFIXED64:
                  value = reader.readSFixed64();
                  break;
               case SINT32:
                  value = reader.readSInt32();
                  break;
               case SINT64:
                  value = reader.readSInt64();
                  break;
               case ENUM:
                  throw new IllegalStateException("Shouldn't reach here.");
               case BYTES:
                  value = reader.readBytes();
                  break;
               case STRING:
                  value = reader.readString();
                  break;
               case GROUP:
                  value = reader.readGroup(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
                  break;
               case MESSAGE:
                  value = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
            }
         }

         if (extension.isRepeated()) {
            extensions.addRepeatedField(extension.descriptor, value);
         } else {
            switch (extension.getLiteType()) {
               case GROUP:
               case MESSAGE:
                  Object oldValue = extensions.getField(extension.descriptor);
                  if (oldValue != null) {
                     value = Internal.mergeMessage(oldValue, value);
                  }
               default:
                  extensions.setField(extension.descriptor, value);
            }
         }
      }

      return unknownFields;
   }

   int extensionNumber(Map.Entry<?, ?> extension) {
      GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
      return descriptor.getNumber();
   }

   void serializeExtension(Writer writer, Map.Entry<?, ?> extension) throws IOException {
      GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
      if (descriptor.isRepeated()) {
         List data;
         switch (descriptor.getLiteType()) {
            case DOUBLE:
               SchemaUtil.writeDoubleList(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case FLOAT:
               SchemaUtil.writeFloatList(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case INT64:
               SchemaUtil.writeInt64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case UINT64:
               SchemaUtil.writeUInt64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case INT32:
               SchemaUtil.writeInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case FIXED64:
               SchemaUtil.writeFixed64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case FIXED32:
               SchemaUtil.writeFixed32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case BOOL:
               SchemaUtil.writeBoolList(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case UINT32:
               SchemaUtil.writeUInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case SFIXED32:
               SchemaUtil.writeSFixed32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case SFIXED64:
               SchemaUtil.writeSFixed64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case SINT32:
               SchemaUtil.writeSInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case SINT64:
               SchemaUtil.writeSInt64List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case ENUM:
               SchemaUtil.writeInt32List(descriptor.getNumber(), (List)extension.getValue(), writer, descriptor.isPacked());
               break;
            case BYTES:
               SchemaUtil.writeBytesList(descriptor.getNumber(), (List)extension.getValue(), writer);
               break;
            case STRING:
               SchemaUtil.writeStringList(descriptor.getNumber(), (List)extension.getValue(), writer);
               break;
            case GROUP:
               data = (List)extension.getValue();
               if (data != null && !data.isEmpty()) {
                  SchemaUtil.writeGroupList(descriptor.getNumber(), (List)extension.getValue(), writer, Protobuf.getInstance().schemaFor(data.get(0).getClass()));
               }
               break;
            case MESSAGE:
               data = (List)extension.getValue();
               if (data != null && !data.isEmpty()) {
                  SchemaUtil.writeMessageList(descriptor.getNumber(), (List)extension.getValue(), writer, Protobuf.getInstance().schemaFor(data.get(0).getClass()));
               }
         }
      } else {
         switch (descriptor.getLiteType()) {
            case DOUBLE:
               writer.writeDouble(descriptor.getNumber(), (Double)extension.getValue());
               break;
            case FLOAT:
               writer.writeFloat(descriptor.getNumber(), (Float)extension.getValue());
               break;
            case INT64:
               writer.writeInt64(descriptor.getNumber(), (Long)extension.getValue());
               break;
            case UINT64:
               writer.writeUInt64(descriptor.getNumber(), (Long)extension.getValue());
               break;
            case INT32:
               writer.writeInt32(descriptor.getNumber(), (Integer)extension.getValue());
               break;
            case FIXED64:
               writer.writeFixed64(descriptor.getNumber(), (Long)extension.getValue());
               break;
            case FIXED32:
               writer.writeFixed32(descriptor.getNumber(), (Integer)extension.getValue());
               break;
            case BOOL:
               writer.writeBool(descriptor.getNumber(), (Boolean)extension.getValue());
               break;
            case UINT32:
               writer.writeUInt32(descriptor.getNumber(), (Integer)extension.getValue());
               break;
            case SFIXED32:
               writer.writeSFixed32(descriptor.getNumber(), (Integer)extension.getValue());
               break;
            case SFIXED64:
               writer.writeSFixed64(descriptor.getNumber(), (Long)extension.getValue());
               break;
            case SINT32:
               writer.writeSInt32(descriptor.getNumber(), (Integer)extension.getValue());
               break;
            case SINT64:
               writer.writeSInt64(descriptor.getNumber(), (Long)extension.getValue());
               break;
            case ENUM:
               writer.writeInt32(descriptor.getNumber(), (Integer)extension.getValue());
               break;
            case BYTES:
               writer.writeBytes(descriptor.getNumber(), (ByteString)extension.getValue());
               break;
            case STRING:
               writer.writeString(descriptor.getNumber(), (String)extension.getValue());
               break;
            case GROUP:
               writer.writeGroup(descriptor.getNumber(), extension.getValue(), Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
               break;
            case MESSAGE:
               writer.writeMessage(descriptor.getNumber(), extension.getValue(), Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
         }
      }

   }

   Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number) {
      return extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
   }

   void parseLengthPrefixedMessageSetItem(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
      GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension)extensionObject;
      Object value = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
      extensions.setField(extension.descriptor, value);
   }

   void parseMessageSetItem(ByteString data, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
      GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension)extensionObject;
      Object value = extension.getMessageDefaultInstance().newBuilderForType().buildPartial();
      Reader reader = BinaryReader.newInstance(ByteBuffer.wrap(data.toByteArray()), true);
      Protobuf.getInstance().mergeFrom(value, reader, extensionRegistry);
      extensions.setField(extension.descriptor, value);
      if (reader.getFieldNumber() != Integer.MAX_VALUE) {
         throw InvalidProtocolBufferException.invalidEndTag();
      }
   }
}
