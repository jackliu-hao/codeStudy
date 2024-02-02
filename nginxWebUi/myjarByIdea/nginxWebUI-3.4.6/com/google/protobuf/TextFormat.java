package com.google.protobuf;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFormat {
   private static final Logger logger = Logger.getLogger(TextFormat.class.getName());
   private static final Parser PARSER = TextFormat.Parser.newBuilder().build();

   private TextFormat() {
   }

   /** @deprecated */
   @Deprecated
   public static void print(MessageOrBuilder message, Appendable output) throws IOException {
      printer().print(message, output);
   }

   /** @deprecated */
   @Deprecated
   public static void print(UnknownFieldSet fields, Appendable output) throws IOException {
      printer().print(fields, output);
   }

   /** @deprecated */
   @Deprecated
   public static void printUnicode(MessageOrBuilder message, Appendable output) throws IOException {
      printer().escapingNonAscii(false).print(message, output);
   }

   /** @deprecated */
   @Deprecated
   public static void printUnicode(UnknownFieldSet fields, Appendable output) throws IOException {
      printer().escapingNonAscii(false).print(fields, output);
   }

   public static String shortDebugString(MessageOrBuilder message) {
      return printer().shortDebugString(message);
   }

   /** @deprecated */
   @Deprecated
   public static String shortDebugString(Descriptors.FieldDescriptor field, Object value) {
      return printer().shortDebugString(field, value);
   }

   /** @deprecated */
   @Deprecated
   public static String shortDebugString(UnknownFieldSet fields) {
      return printer().shortDebugString(fields);
   }

   /** @deprecated */
   @Deprecated
   public static String printToString(MessageOrBuilder message) {
      return printer().printToString(message);
   }

   /** @deprecated */
   @Deprecated
   public static String printToString(UnknownFieldSet fields) {
      return printer().printToString(fields);
   }

   /** @deprecated */
   @Deprecated
   public static String printToUnicodeString(MessageOrBuilder message) {
      return printer().escapingNonAscii(false).printToString(message);
   }

   /** @deprecated */
   @Deprecated
   public static String printToUnicodeString(UnknownFieldSet fields) {
      return printer().escapingNonAscii(false).printToString(fields);
   }

   /** @deprecated */
   @Deprecated
   public static void printField(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
      printer().printField(field, value, output);
   }

   /** @deprecated */
   @Deprecated
   public static String printFieldToString(Descriptors.FieldDescriptor field, Object value) {
      return printer().printFieldToString(field, value);
   }

   /** @deprecated */
   @Deprecated
   public static void printUnicodeFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
      printer().escapingNonAscii(false).printFieldValue(field, value, output);
   }

   /** @deprecated */
   @Deprecated
   public static void printFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
      printer().printFieldValue(field, value, output);
   }

   public static void printUnknownFieldValue(int tag, Object value, Appendable output) throws IOException {
      printUnknownFieldValue(tag, value, multiLineOutput(output));
   }

   private static void printUnknownFieldValue(int var0, Object var1, TextGenerator var2) throws IOException {
      // $FF: Couldn't be decompiled
   }

   public static Printer printer() {
      return TextFormat.Printer.DEFAULT;
   }

   public static String unsignedToString(int value) {
      return value >= 0 ? Integer.toString(value) : Long.toString((long)value & 4294967295L);
   }

   public static String unsignedToString(long value) {
      return value >= 0L ? Long.toString(value) : BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63).toString();
   }

   private static TextGenerator multiLineOutput(Appendable output) {
      return new TextGenerator(output, false);
   }

   private static TextGenerator singleLineOutput(Appendable output) {
      return new TextGenerator(output, true);
   }

   public static Parser getParser() {
      return PARSER;
   }

   public static void merge(Readable input, Message.Builder builder) throws IOException {
      PARSER.merge(input, builder);
   }

   public static void merge(CharSequence input, Message.Builder builder) throws ParseException {
      PARSER.merge(input, builder);
   }

   public static <T extends Message> T parse(CharSequence input, Class<T> protoClass) throws ParseException {
      Message.Builder builder = ((Message)Internal.getDefaultInstance(protoClass)).newBuilderForType();
      merge(input, builder);
      T output = builder.build();
      return output;
   }

   public static void merge(Readable input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {
      PARSER.merge(input, extensionRegistry, builder);
   }

   public static void merge(CharSequence input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws ParseException {
      PARSER.merge(input, extensionRegistry, builder);
   }

   public static <T extends Message> T parse(CharSequence input, ExtensionRegistry extensionRegistry, Class<T> protoClass) throws ParseException {
      Message.Builder builder = ((Message)Internal.getDefaultInstance(protoClass)).newBuilderForType();
      merge(input, extensionRegistry, builder);
      T output = builder.build();
      return output;
   }

   public static String escapeBytes(ByteString input) {
      return TextFormatEscaper.escapeBytes(input);
   }

   public static String escapeBytes(byte[] input) {
      return TextFormatEscaper.escapeBytes(input);
   }

   public static ByteString unescapeBytes(CharSequence charString) throws InvalidEscapeSequenceException {
      ByteString input = ByteString.copyFromUtf8(charString.toString());
      byte[] result = new byte[input.size()];
      int pos = 0;

      for(int i = 0; i < input.size(); ++i) {
         byte c = input.byteAt(i);
         if (c == 92) {
            if (i + 1 >= input.size()) {
               throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\' at end of string.");
            }

            ++i;
            c = input.byteAt(i);
            int code;
            if (isOctal(c)) {
               code = digitValue(c);
               if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                  ++i;
                  code = code * 8 + digitValue(input.byteAt(i));
               }

               if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                  ++i;
                  code = code * 8 + digitValue(input.byteAt(i));
               }

               result[pos++] = (byte)code;
            } else {
               switch (c) {
                  case 34:
                     result[pos++] = 34;
                     break;
                  case 39:
                     result[pos++] = 39;
                     break;
                  case 92:
                     result[pos++] = 92;
                     break;
                  case 97:
                     result[pos++] = 7;
                     break;
                  case 98:
                     result[pos++] = 8;
                     break;
                  case 102:
                     result[pos++] = 12;
                     break;
                  case 110:
                     result[pos++] = 10;
                     break;
                  case 114:
                     result[pos++] = 13;
                     break;
                  case 116:
                     result[pos++] = 9;
                     break;
                  case 118:
                     result[pos++] = 11;
                     break;
                  case 120:
                     int code = false;
                     if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
                        ++i;
                        code = digitValue(input.byteAt(i));
                        if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
                           ++i;
                           code = code * 16 + digitValue(input.byteAt(i));
                        }

                        result[pos++] = (byte)code;
                        break;
                     }

                     throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\x' with no digits");
                  default:
                     throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\" + (char)c + '\'');
               }
            }
         } else {
            result[pos++] = c;
         }
      }

      return result.length == pos ? ByteString.wrap(result) : ByteString.copyFrom(result, 0, pos);
   }

   static String escapeText(String input) {
      return escapeBytes(ByteString.copyFromUtf8(input));
   }

   public static String escapeDoubleQuotesAndBackslashes(String input) {
      return TextFormatEscaper.escapeDoubleQuotesAndBackslashes(input);
   }

   static String unescapeText(String input) throws InvalidEscapeSequenceException {
      return unescapeBytes(input).toStringUtf8();
   }

   private static boolean isOctal(byte c) {
      return 48 <= c && c <= 55;
   }

   private static boolean isHex(byte c) {
      return 48 <= c && c <= 57 || 97 <= c && c <= 102 || 65 <= c && c <= 70;
   }

   private static int digitValue(byte c) {
      if (48 <= c && c <= 57) {
         return c - 48;
      } else {
         return 97 <= c && c <= 122 ? c - 97 + 10 : c - 65 + 10;
      }
   }

   static int parseInt32(String text) throws NumberFormatException {
      return (int)parseInteger(text, true, false);
   }

   static int parseUInt32(String text) throws NumberFormatException {
      return (int)parseInteger(text, false, false);
   }

   static long parseInt64(String text) throws NumberFormatException {
      return parseInteger(text, true, true);
   }

   static long parseUInt64(String text) throws NumberFormatException {
      return parseInteger(text, false, true);
   }

   private static long parseInteger(String text, boolean isSigned, boolean isLong) throws NumberFormatException {
      int pos = 0;
      boolean negative = false;
      if (text.startsWith("-", pos)) {
         if (!isSigned) {
            throw new NumberFormatException("Number must be positive: " + text);
         }

         ++pos;
         negative = true;
      }

      int radix = 10;
      if (text.startsWith("0x", pos)) {
         pos += 2;
         radix = 16;
      } else if (text.startsWith("0", pos)) {
         radix = 8;
      }

      String numberText = text.substring(pos);
      long result = 0L;
      if (numberText.length() < 16) {
         result = Long.parseLong(numberText, radix);
         if (negative) {
            result = -result;
         }

         if (!isLong) {
            if (isSigned) {
               if (result > 2147483647L || result < -2147483648L) {
                  throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
               }
            } else if (result >= 4294967296L || result < 0L) {
               throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
            }
         }
      } else {
         BigInteger bigValue = new BigInteger(numberText, radix);
         if (negative) {
            bigValue = bigValue.negate();
         }

         if (!isLong) {
            if (isSigned) {
               if (bigValue.bitLength() > 31) {
                  throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
               }
            } else if (bigValue.bitLength() > 32) {
               throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
            }
         } else if (isSigned) {
            if (bigValue.bitLength() > 63) {
               throw new NumberFormatException("Number out of range for 64-bit signed integer: " + text);
            }
         } else if (bigValue.bitLength() > 64) {
            throw new NumberFormatException("Number out of range for 64-bit unsigned integer: " + text);
         }

         result = bigValue.longValue();
      }

      return result;
   }

   public static class InvalidEscapeSequenceException extends IOException {
      private static final long serialVersionUID = -8164033650142593304L;

      InvalidEscapeSequenceException(String description) {
         super(description);
      }
   }

   public static class Parser {
      private final TypeRegistry typeRegistry;
      private final boolean allowUnknownFields;
      private final boolean allowUnknownEnumValues;
      private final boolean allowUnknownExtensions;
      private final SingularOverwritePolicy singularOverwritePolicy;
      private TextFormatParseInfoTree.Builder parseInfoTreeBuilder;
      private static final int BUFFER_SIZE = 4096;

      private Parser(TypeRegistry typeRegistry, boolean allowUnknownFields, boolean allowUnknownEnumValues, boolean allowUnknownExtensions, SingularOverwritePolicy singularOverwritePolicy, TextFormatParseInfoTree.Builder parseInfoTreeBuilder) {
         this.typeRegistry = typeRegistry;
         this.allowUnknownFields = allowUnknownFields;
         this.allowUnknownEnumValues = allowUnknownEnumValues;
         this.allowUnknownExtensions = allowUnknownExtensions;
         this.singularOverwritePolicy = singularOverwritePolicy;
         this.parseInfoTreeBuilder = parseInfoTreeBuilder;
      }

      public static Builder newBuilder() {
         return new Builder();
      }

      public void merge(Readable input, Message.Builder builder) throws IOException {
         this.merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
      }

      public void merge(CharSequence input, Message.Builder builder) throws ParseException {
         this.merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
      }

      public void merge(Readable input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {
         this.merge((CharSequence)toStringBuilder(input), extensionRegistry, builder);
      }

      private static StringBuilder toStringBuilder(Readable input) throws IOException {
         StringBuilder text = new StringBuilder();
         CharBuffer buffer = CharBuffer.allocate(4096);

         while(true) {
            int n = input.read(buffer);
            if (n == -1) {
               return text;
            }

            buffer.flip();
            text.append(buffer, 0, n);
         }
      }

      private void checkUnknownFields(List<UnknownField> unknownFields) throws ParseException {
         if (!unknownFields.isEmpty()) {
            StringBuilder msg = new StringBuilder("Input contains unknown fields and/or extensions:");
            Iterator var3 = unknownFields.iterator();

            while(var3.hasNext()) {
               UnknownField field = (UnknownField)var3.next();
               msg.append('\n').append(field.message);
            }

            if (this.allowUnknownFields) {
               TextFormat.logger.warning(msg.toString());
            } else {
               int firstErrorIndex = 0;
               if (this.allowUnknownExtensions) {
                  boolean allUnknownExtensions = true;

                  for(Iterator var5 = unknownFields.iterator(); var5.hasNext(); ++firstErrorIndex) {
                     UnknownField field = (UnknownField)var5.next();
                     if (field.type == TextFormat.Parser.UnknownField.Type.FIELD) {
                        allUnknownExtensions = false;
                        break;
                     }
                  }

                  if (allUnknownExtensions) {
                     TextFormat.logger.warning(msg.toString());
                     return;
                  }
               }

               String[] lineColumn = ((UnknownField)unknownFields.get(firstErrorIndex)).message.split(":");
               throw new ParseException(Integer.parseInt(lineColumn[0]), Integer.parseInt(lineColumn[1]), msg.toString());
            }
         }
      }

      public void merge(CharSequence input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws ParseException {
         Tokenizer tokenizer = new Tokenizer(input);
         MessageReflection.BuilderAdapter target = new MessageReflection.BuilderAdapter(builder);
         List<UnknownField> unknownFields = new ArrayList();

         while(!tokenizer.atEnd()) {
            this.mergeField(tokenizer, extensionRegistry, target, unknownFields);
         }

         this.checkUnknownFields(unknownFields);
      }

      private void mergeField(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, List<UnknownField> unknownFields) throws ParseException {
         this.mergeField(tokenizer, extensionRegistry, target, this.parseInfoTreeBuilder, unknownFields);
      }

      private void mergeField(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws ParseException {
         Descriptors.FieldDescriptor field = null;
         int startLine = tokenizer.getLine();
         int startColumn = tokenizer.getColumn();
         Descriptors.Descriptor type = target.getDescriptorForType();
         ExtensionRegistry.ExtensionInfo extension = null;
         String message;
         if (tokenizer.tryConsume("[")) {
            StringBuilder name = new StringBuilder(tokenizer.consumeIdentifier());

            while(tokenizer.tryConsume(".")) {
               name.append('.');
               name.append(tokenizer.consumeIdentifier());
            }

            extension = target.findExtensionByName(extensionRegistry, name.toString());
            if (extension == null) {
               message = tokenizer.getPreviousLine() + 1 + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + ".[" + name + "]";
               unknownFields.add(new UnknownField(message, TextFormat.Parser.UnknownField.Type.EXTENSION));
            } else {
               if (extension.descriptor.getContainingType() != type) {
                  throw tokenizer.parseExceptionPreviousToken("Extension \"" + name + "\" does not extend message type \"" + type.getFullName() + "\".");
               }

               field = extension.descriptor;
            }

            tokenizer.consume("]");
         } else {
            String name = tokenizer.consumeIdentifier();
            field = type.findFieldByName(name);
            if (field == null) {
               message = name.toLowerCase(Locale.US);
               field = type.findFieldByName(message);
               if (field != null && field.getType() != Descriptors.FieldDescriptor.Type.GROUP) {
                  field = null;
               }
            }

            if (field != null && field.getType() == Descriptors.FieldDescriptor.Type.GROUP && !field.getMessageType().getName().equals(name)) {
               field = null;
            }

            if (field == null) {
               message = tokenizer.getPreviousLine() + 1 + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + "." + name;
               unknownFields.add(new UnknownField(message, TextFormat.Parser.UnknownField.Type.FIELD));
            }
         }

         if (field != null) {
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               tokenizer.tryConsume(":");
               if (parseTreeBuilder != null) {
                  TextFormatParseInfoTree.Builder childParseTreeBuilder = parseTreeBuilder.getBuilderForSubMessageField(field);
                  this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, childParseTreeBuilder, unknownFields);
               } else {
                  this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
               }
            } else {
               tokenizer.consume(":");
               this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
            }

            if (parseTreeBuilder != null) {
               parseTreeBuilder.setLocation(field, TextFormatParseLocation.create(startLine, startColumn));
            }

            if (!tokenizer.tryConsume(";")) {
               tokenizer.tryConsume(",");
            }

         } else {
            if (tokenizer.tryConsume(":") && !tokenizer.lookingAt("{") && !tokenizer.lookingAt("<")) {
               this.skipFieldValue(tokenizer);
            } else {
               this.skipFieldMessage(tokenizer);
            }

         }
      }

      private void consumeFieldValues(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, Descriptors.FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws ParseException {
         if (field.isRepeated() && tokenizer.tryConsume("[")) {
            if (!tokenizer.tryConsume("]")) {
               while(true) {
                  this.consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
                  if (tokenizer.tryConsume("]")) {
                     break;
                  }

                  tokenizer.consume(",");
               }
            }
         } else {
            this.consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
         }

      }

      private void consumeFieldValue(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, Descriptors.FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws ParseException {
         if (this.singularOverwritePolicy == TextFormat.Parser.SingularOverwritePolicy.FORBID_SINGULAR_OVERWRITES && !field.isRepeated()) {
            if (target.hasField(field)) {
               throw tokenizer.parseExceptionPreviousToken("Non-repeated field \"" + field.getFullName() + "\" cannot be overwritten.");
            }

            if (field.getContainingOneof() != null && target.hasOneof(field.getContainingOneof())) {
               Descriptors.OneofDescriptor oneof = field.getContainingOneof();
               throw tokenizer.parseExceptionPreviousToken("Field \"" + field.getFullName() + "\" is specified along with field \"" + target.getOneofFieldDescriptor(oneof).getFullName() + "\", another member of oneof \"" + oneof.getName() + "\".");
            }
         }

         Object value = null;
         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            String endToken;
            if (tokenizer.tryConsume("<")) {
               endToken = ">";
            } else {
               tokenizer.consume("{");
               endToken = "}";
            }

            if (field.getMessageType().getFullName().equals("google.protobuf.Any") && tokenizer.tryConsume("[")) {
               value = this.consumeAnyFieldValue(tokenizer, extensionRegistry, field, parseTreeBuilder, unknownFields);
               tokenizer.consume(endToken);
            } else {
               Message defaultInstance = extension == null ? null : extension.defaultInstance;
               MessageReflection.MergeTarget subField = target.newMergeTargetForField(field, defaultInstance);

               while(!tokenizer.tryConsume(endToken)) {
                  if (tokenizer.atEnd()) {
                     throw tokenizer.parseException("Expected \"" + endToken + "\".");
                  }

                  this.mergeField(tokenizer, extensionRegistry, subField, parseTreeBuilder, unknownFields);
               }

               value = subField.finish();
            }
         } else {
            switch (field.getType()) {
               case INT32:
               case SINT32:
               case SFIXED32:
                  value = tokenizer.consumeInt32();
                  break;
               case INT64:
               case SINT64:
               case SFIXED64:
                  value = tokenizer.consumeInt64();
                  break;
               case BOOL:
                  value = tokenizer.consumeBoolean();
                  break;
               case FLOAT:
                  value = tokenizer.consumeFloat();
                  break;
               case DOUBLE:
                  value = tokenizer.consumeDouble();
                  break;
               case UINT32:
               case FIXED32:
                  value = tokenizer.consumeUInt32();
                  break;
               case UINT64:
               case FIXED64:
                  value = tokenizer.consumeUInt64();
                  break;
               case STRING:
                  value = tokenizer.consumeString();
                  break;
               case BYTES:
                  value = tokenizer.consumeByteString();
                  break;
               case ENUM:
                  Descriptors.EnumDescriptor enumType = field.getEnumType();
                  String unknownValueMsg;
                  if (tokenizer.lookingAtInteger()) {
                     int number = tokenizer.consumeInt32();
                     value = enumType.findValueByNumber(number);
                     if (value == null) {
                        unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.';
                        if (this.allowUnknownEnumValues) {
                           TextFormat.logger.warning(unknownValueMsg);
                           return;
                        }

                        throw tokenizer.parseExceptionPreviousToken("Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.');
                     }
                  } else {
                     String id = tokenizer.consumeIdentifier();
                     value = enumType.findValueByName(id);
                     if (value == null) {
                        unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value named \"" + id + "\".";
                        if (this.allowUnknownEnumValues) {
                           TextFormat.logger.warning(unknownValueMsg);
                           return;
                        }

                        throw tokenizer.parseExceptionPreviousToken(unknownValueMsg);
                     }
                  }
                  break;
               case MESSAGE:
               case GROUP:
                  throw new RuntimeException("Can't get here.");
            }
         }

         if (field.isRepeated()) {
            target.addRepeatedField(field, value);
         } else {
            target.setField(field, value);
         }

      }

      private Object consumeAnyFieldValue(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, Descriptors.FieldDescriptor field, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws ParseException {
         StringBuilder typeUrlBuilder = new StringBuilder();

         while(true) {
            typeUrlBuilder.append(tokenizer.consumeIdentifier());
            if (tokenizer.tryConsume("]")) {
               tokenizer.tryConsume(":");
               String anyEndToken;
               if (tokenizer.tryConsume("<")) {
                  anyEndToken = ">";
               } else {
                  tokenizer.consume("{");
                  anyEndToken = "}";
               }

               String typeUrl = typeUrlBuilder.toString();
               Descriptors.Descriptor contentType = null;

               try {
                  contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
               } catch (InvalidProtocolBufferException var14) {
                  throw tokenizer.parseException("Invalid valid type URL. Found: " + typeUrl);
               }

               if (contentType == null) {
                  throw tokenizer.parseException("Unable to parse Any of type: " + typeUrl + ". Please make sure that the TypeRegistry contains the descriptors for the given types.");
               } else {
                  Message.Builder contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
                  MessageReflection.BuilderAdapter contentTarget = new MessageReflection.BuilderAdapter(contentBuilder);

                  while(!tokenizer.tryConsume(anyEndToken)) {
                     this.mergeField(tokenizer, extensionRegistry, contentTarget, parseTreeBuilder, unknownFields);
                  }

                  Descriptors.Descriptor anyDescriptor = field.getMessageType();
                  Message.Builder anyBuilder = DynamicMessage.getDefaultInstance(anyDescriptor).newBuilderForType();
                  anyBuilder.setField(anyDescriptor.findFieldByName("type_url"), typeUrlBuilder.toString());
                  anyBuilder.setField(anyDescriptor.findFieldByName("value"), contentBuilder.build().toByteString());
                  return anyBuilder.build();
               }
            }

            if (tokenizer.tryConsume("/")) {
               typeUrlBuilder.append("/");
            } else {
               if (!tokenizer.tryConsume(".")) {
                  throw tokenizer.parseExceptionPreviousToken("Expected a valid type URL.");
               }

               typeUrlBuilder.append(".");
            }
         }
      }

      private void skipField(Tokenizer tokenizer) throws ParseException {
         if (tokenizer.tryConsume("[")) {
            do {
               tokenizer.consumeIdentifier();
            } while(tokenizer.tryConsume("."));

            tokenizer.consume("]");
         } else {
            tokenizer.consumeIdentifier();
         }

         if (tokenizer.tryConsume(":") && !tokenizer.lookingAt("<") && !tokenizer.lookingAt("{")) {
            this.skipFieldValue(tokenizer);
         } else {
            this.skipFieldMessage(tokenizer);
         }

         if (!tokenizer.tryConsume(";")) {
            tokenizer.tryConsume(",");
         }

      }

      private void skipFieldMessage(Tokenizer tokenizer) throws ParseException {
         String delimiter;
         if (tokenizer.tryConsume("<")) {
            delimiter = ">";
         } else {
            tokenizer.consume("{");
            delimiter = "}";
         }

         while(!tokenizer.lookingAt(">") && !tokenizer.lookingAt("}")) {
            this.skipField(tokenizer);
         }

         tokenizer.consume(delimiter);
      }

      private void skipFieldValue(Tokenizer tokenizer) throws ParseException {
         if (!tokenizer.tryConsumeString()) {
            if (!tokenizer.tryConsumeIdentifier() && !tokenizer.tryConsumeInt64() && !tokenizer.tryConsumeUInt64() && !tokenizer.tryConsumeDouble() && !tokenizer.tryConsumeFloat()) {
               throw tokenizer.parseException("Invalid field value: " + tokenizer.currentToken);
            }
         } else {
            while(tokenizer.tryConsumeString()) {
            }

         }
      }

      // $FF: synthetic method
      Parser(TypeRegistry x0, boolean x1, boolean x2, boolean x3, SingularOverwritePolicy x4, TextFormatParseInfoTree.Builder x5, Object x6) {
         this(x0, x1, x2, x3, x4, x5);
      }

      static final class UnknownField {
         final String message;
         final Type type;

         UnknownField(String message, Type type) {
            this.message = message;
            this.type = type;
         }

         static enum Type {
            FIELD,
            EXTENSION;
         }
      }

      public static class Builder {
         private boolean allowUnknownFields = false;
         private boolean allowUnknownEnumValues = false;
         private boolean allowUnknownExtensions = false;
         private SingularOverwritePolicy singularOverwritePolicy;
         private TextFormatParseInfoTree.Builder parseInfoTreeBuilder;
         private TypeRegistry typeRegistry;

         public Builder() {
            this.singularOverwritePolicy = TextFormat.Parser.SingularOverwritePolicy.ALLOW_SINGULAR_OVERWRITES;
            this.parseInfoTreeBuilder = null;
            this.typeRegistry = TypeRegistry.getEmptyTypeRegistry();
         }

         public Builder setTypeRegistry(TypeRegistry typeRegistry) {
            this.typeRegistry = typeRegistry;
            return this;
         }

         public Builder setAllowUnknownFields(boolean allowUnknownFields) {
            this.allowUnknownFields = allowUnknownFields;
            return this;
         }

         public Builder setAllowUnknownExtensions(boolean allowUnknownExtensions) {
            this.allowUnknownExtensions = allowUnknownExtensions;
            return this;
         }

         public Builder setSingularOverwritePolicy(SingularOverwritePolicy p) {
            this.singularOverwritePolicy = p;
            return this;
         }

         public Builder setParseInfoTreeBuilder(TextFormatParseInfoTree.Builder parseInfoTreeBuilder) {
            this.parseInfoTreeBuilder = parseInfoTreeBuilder;
            return this;
         }

         public Parser build() {
            return new Parser(this.typeRegistry, this.allowUnknownFields, this.allowUnknownEnumValues, this.allowUnknownExtensions, this.singularOverwritePolicy, this.parseInfoTreeBuilder);
         }
      }

      public static enum SingularOverwritePolicy {
         ALLOW_SINGULAR_OVERWRITES,
         FORBID_SINGULAR_OVERWRITES;
      }
   }

   public static class UnknownFieldParseException extends ParseException {
      private final String unknownField;

      public UnknownFieldParseException(String message) {
         this(-1, -1, "", message);
      }

      public UnknownFieldParseException(int line, int column, String unknownField, String message) {
         super(line, column, message);
         this.unknownField = unknownField;
      }

      public String getUnknownField() {
         return this.unknownField;
      }
   }

   public static class ParseException extends IOException {
      private static final long serialVersionUID = 3196188060225107702L;
      private final int line;
      private final int column;

      public ParseException(String message) {
         this(-1, -1, message);
      }

      public ParseException(int line, int column, String message) {
         super(Integer.toString(line) + ":" + column + ": " + message);
         this.line = line;
         this.column = column;
      }

      public int getLine() {
         return this.line;
      }

      public int getColumn() {
         return this.column;
      }
   }

   private static final class Tokenizer {
      private final CharSequence text;
      private final Matcher matcher;
      private String currentToken;
      private int pos;
      private int line;
      private int column;
      private int previousLine;
      private int previousColumn;
      private static final Pattern WHITESPACE = Pattern.compile("(\\s|(#.*$))++", 8);
      private static final Pattern TOKEN = Pattern.compile("[a-zA-Z_][0-9a-zA-Z_+-]*+|[.]?[0-9+-][0-9a-zA-Z_.+-]*+|\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|'([^'\n\\\\]|\\\\.)*+('|\\\\?$)", 8);
      private static final Pattern DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?", 2);
      private static final Pattern FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?", 2);
      private static final Pattern FLOAT_NAN = Pattern.compile("nanf?", 2);

      private Tokenizer(CharSequence text) {
         this.pos = 0;
         this.line = 0;
         this.column = 0;
         this.previousLine = 0;
         this.previousColumn = 0;
         this.text = text;
         this.matcher = WHITESPACE.matcher(text);
         this.skipWhitespace();
         this.nextToken();
      }

      int getPreviousLine() {
         return this.previousLine;
      }

      int getPreviousColumn() {
         return this.previousColumn;
      }

      int getLine() {
         return this.line;
      }

      int getColumn() {
         return this.column;
      }

      public boolean atEnd() {
         return this.currentToken.length() == 0;
      }

      public void nextToken() {
         this.previousLine = this.line;

         for(this.previousColumn = this.column; this.pos < this.matcher.regionStart(); ++this.pos) {
            if (this.text.charAt(this.pos) == '\n') {
               ++this.line;
               this.column = 0;
            } else {
               ++this.column;
            }
         }

         if (this.matcher.regionStart() == this.matcher.regionEnd()) {
            this.currentToken = "";
         } else {
            this.matcher.usePattern(TOKEN);
            if (this.matcher.lookingAt()) {
               this.currentToken = this.matcher.group();
               this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
            } else {
               this.currentToken = String.valueOf(this.text.charAt(this.pos));
               this.matcher.region(this.pos + 1, this.matcher.regionEnd());
            }

            this.skipWhitespace();
         }

      }

      private void skipWhitespace() {
         this.matcher.usePattern(WHITESPACE);
         if (this.matcher.lookingAt()) {
            this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
         }

      }

      public boolean tryConsume(String token) {
         if (this.currentToken.equals(token)) {
            this.nextToken();
            return true;
         } else {
            return false;
         }
      }

      public void consume(String token) throws ParseException {
         if (!this.tryConsume(token)) {
            throw this.parseException("Expected \"" + token + "\".");
         }
      }

      public boolean lookingAtInteger() {
         if (this.currentToken.length() == 0) {
            return false;
         } else {
            char c = this.currentToken.charAt(0);
            return '0' <= c && c <= '9' || c == '-' || c == '+';
         }
      }

      public boolean lookingAt(String text) {
         return this.currentToken.equals(text);
      }

      public String consumeIdentifier() throws ParseException {
         for(int i = 0; i < this.currentToken.length(); ++i) {
            char c = this.currentToken.charAt(i);
            if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && ('0' > c || c > '9') && c != '_' && c != '.') {
               throw this.parseException("Expected identifier. Found '" + this.currentToken + "'");
            }
         }

         String result = this.currentToken;
         this.nextToken();
         return result;
      }

      public boolean tryConsumeIdentifier() {
         try {
            this.consumeIdentifier();
            return true;
         } catch (ParseException var2) {
            return false;
         }
      }

      public int consumeInt32() throws ParseException {
         try {
            int result = TextFormat.parseInt32(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var2) {
            throw this.integerParseException(var2);
         }
      }

      public int consumeUInt32() throws ParseException {
         try {
            int result = TextFormat.parseUInt32(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var2) {
            throw this.integerParseException(var2);
         }
      }

      public long consumeInt64() throws ParseException {
         try {
            long result = TextFormat.parseInt64(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var3) {
            throw this.integerParseException(var3);
         }
      }

      public boolean tryConsumeInt64() {
         try {
            this.consumeInt64();
            return true;
         } catch (ParseException var2) {
            return false;
         }
      }

      public long consumeUInt64() throws ParseException {
         try {
            long result = TextFormat.parseUInt64(this.currentToken);
            this.nextToken();
            return result;
         } catch (NumberFormatException var3) {
            throw this.integerParseException(var3);
         }
      }

      public boolean tryConsumeUInt64() {
         try {
            this.consumeUInt64();
            return true;
         } catch (ParseException var2) {
            return false;
         }
      }

      public double consumeDouble() throws ParseException {
         if (DOUBLE_INFINITY.matcher(this.currentToken).matches()) {
            boolean negative = this.currentToken.startsWith("-");
            this.nextToken();
            return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
         } else if (this.currentToken.equalsIgnoreCase("nan")) {
            this.nextToken();
            return Double.NaN;
         } else {
            try {
               double result = Double.parseDouble(this.currentToken);
               this.nextToken();
               return result;
            } catch (NumberFormatException var3) {
               throw this.floatParseException(var3);
            }
         }
      }

      public boolean tryConsumeDouble() {
         try {
            this.consumeDouble();
            return true;
         } catch (ParseException var2) {
            return false;
         }
      }

      public float consumeFloat() throws ParseException {
         if (FLOAT_INFINITY.matcher(this.currentToken).matches()) {
            boolean negative = this.currentToken.startsWith("-");
            this.nextToken();
            return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
         } else if (FLOAT_NAN.matcher(this.currentToken).matches()) {
            this.nextToken();
            return Float.NaN;
         } else {
            try {
               float result = Float.parseFloat(this.currentToken);
               this.nextToken();
               return result;
            } catch (NumberFormatException var2) {
               throw this.floatParseException(var2);
            }
         }
      }

      public boolean tryConsumeFloat() {
         try {
            this.consumeFloat();
            return true;
         } catch (ParseException var2) {
            return false;
         }
      }

      public boolean consumeBoolean() throws ParseException {
         if (!this.currentToken.equals("true") && !this.currentToken.equals("True") && !this.currentToken.equals("t") && !this.currentToken.equals("1")) {
            if (!this.currentToken.equals("false") && !this.currentToken.equals("False") && !this.currentToken.equals("f") && !this.currentToken.equals("0")) {
               throw this.parseException("Expected \"true\" or \"false\". Found \"" + this.currentToken + "\".");
            } else {
               this.nextToken();
               return false;
            }
         } else {
            this.nextToken();
            return true;
         }
      }

      public String consumeString() throws ParseException {
         return this.consumeByteString().toStringUtf8();
      }

      public boolean tryConsumeString() {
         try {
            this.consumeString();
            return true;
         } catch (ParseException var2) {
            return false;
         }
      }

      public ByteString consumeByteString() throws ParseException {
         List<ByteString> list = new ArrayList();
         this.consumeByteString(list);

         while(this.currentToken.startsWith("'") || this.currentToken.startsWith("\"")) {
            this.consumeByteString(list);
         }

         return ByteString.copyFrom((Iterable)list);
      }

      private void consumeByteString(List<ByteString> list) throws ParseException {
         char quote = this.currentToken.length() > 0 ? this.currentToken.charAt(0) : 0;
         if (quote != '"' && quote != '\'') {
            throw this.parseException("Expected string.");
         } else if (this.currentToken.length() >= 2 && this.currentToken.charAt(this.currentToken.length() - 1) == quote) {
            try {
               String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
               ByteString result = TextFormat.unescapeBytes(escaped);
               this.nextToken();
               list.add(result);
            } catch (InvalidEscapeSequenceException var5) {
               throw this.parseException(var5.getMessage());
            }
         } else {
            throw this.parseException("String missing ending quote.");
         }
      }

      public ParseException parseException(String description) {
         return new ParseException(this.line + 1, this.column + 1, description);
      }

      public ParseException parseExceptionPreviousToken(String description) {
         return new ParseException(this.previousLine + 1, this.previousColumn + 1, description);
      }

      private ParseException integerParseException(NumberFormatException e) {
         return this.parseException("Couldn't parse integer: " + e.getMessage());
      }

      private ParseException floatParseException(NumberFormatException e) {
         return this.parseException("Couldn't parse number: " + e.getMessage());
      }

      public UnknownFieldParseException unknownFieldParseExceptionPreviousToken(String unknownField, String description) {
         return new UnknownFieldParseException(this.previousLine + 1, this.previousColumn + 1, unknownField, description);
      }

      // $FF: synthetic method
      Tokenizer(CharSequence x0, Object x1) {
         this(x0);
      }
   }

   private static final class TextGenerator {
      private final Appendable output;
      private final StringBuilder indent;
      private final boolean singleLineMode;
      private boolean atStartOfLine;

      private TextGenerator(Appendable output, boolean singleLineMode) {
         this.indent = new StringBuilder();
         this.atStartOfLine = false;
         this.output = output;
         this.singleLineMode = singleLineMode;
      }

      public void indent() {
         this.indent.append("  ");
      }

      public void outdent() {
         int length = this.indent.length();
         if (length == 0) {
            throw new IllegalArgumentException(" Outdent() without matching Indent().");
         } else {
            this.indent.setLength(length - 2);
         }
      }

      public void print(CharSequence text) throws IOException {
         if (this.atStartOfLine) {
            this.atStartOfLine = false;
            this.output.append((CharSequence)(this.singleLineMode ? " " : this.indent));
         }

         this.output.append(text);
      }

      public void eol() throws IOException {
         if (!this.singleLineMode) {
            this.output.append("\n");
         }

         this.atStartOfLine = true;
      }

      // $FF: synthetic method
      TextGenerator(Appendable x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }

   public static final class Printer {
      private static final Printer DEFAULT = new Printer(true, TypeRegistry.getEmptyTypeRegistry());
      private final boolean escapeNonAscii;
      private final TypeRegistry typeRegistry;

      private Printer(boolean escapeNonAscii, TypeRegistry typeRegistry) {
         this.escapeNonAscii = escapeNonAscii;
         this.typeRegistry = typeRegistry;
      }

      public Printer escapingNonAscii(boolean escapeNonAscii) {
         return new Printer(escapeNonAscii, this.typeRegistry);
      }

      public Printer usingTypeRegistry(TypeRegistry typeRegistry) {
         if (this.typeRegistry != TypeRegistry.getEmptyTypeRegistry()) {
            throw new IllegalArgumentException("Only one typeRegistry is allowed.");
         } else {
            return new Printer(this.escapeNonAscii, typeRegistry);
         }
      }

      public void print(MessageOrBuilder message, Appendable output) throws IOException {
         this.print(message, TextFormat.multiLineOutput(output));
      }

      public void print(UnknownFieldSet fields, Appendable output) throws IOException {
         printUnknownFields(fields, TextFormat.multiLineOutput(output));
      }

      private void print(MessageOrBuilder message, TextGenerator generator) throws IOException {
         if (!message.getDescriptorForType().getFullName().equals("google.protobuf.Any") || !this.printAny(message, generator)) {
            this.printMessage(message, generator);
         }
      }

      private boolean printAny(MessageOrBuilder message, TextGenerator generator) throws IOException {
         Descriptors.Descriptor messageType = message.getDescriptorForType();
         Descriptors.FieldDescriptor typeUrlField = messageType.findFieldByNumber(1);
         Descriptors.FieldDescriptor valueField = messageType.findFieldByNumber(2);
         if (typeUrlField != null && typeUrlField.getType() == Descriptors.FieldDescriptor.Type.STRING && valueField != null && valueField.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
            String typeUrl = (String)message.getField(typeUrlField);
            if (typeUrl.isEmpty()) {
               return false;
            } else {
               Object value = message.getField(valueField);
               Message.Builder contentBuilder = null;

               try {
                  Descriptors.Descriptor contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
                  if (contentType == null) {
                     return false;
                  }

                  contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
                  contentBuilder.mergeFrom((ByteString)value);
               } catch (InvalidProtocolBufferException var10) {
                  return false;
               }

               generator.print("[");
               generator.print(typeUrl);
               generator.print("] {");
               generator.eol();
               generator.indent();
               this.print((MessageOrBuilder)contentBuilder, (TextGenerator)generator);
               generator.outdent();
               generator.print("}");
               generator.eol();
               return true;
            }
         } else {
            return false;
         }
      }

      public String printFieldToString(Descriptors.FieldDescriptor field, Object value) {
         try {
            StringBuilder text = new StringBuilder();
            this.printField(field, value, (Appendable)text);
            return text.toString();
         } catch (IOException var4) {
            throw new IllegalStateException(var4);
         }
      }

      public void printField(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
         this.printField(field, value, TextFormat.multiLineOutput(output));
      }

      private void printField(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
         if (field.isRepeated()) {
            Iterator var4 = ((List)value).iterator();

            while(var4.hasNext()) {
               Object element = var4.next();
               this.printSingleField(field, element, generator);
            }
         } else {
            this.printSingleField(field, value, generator);
         }

      }

      public void printFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
         this.printFieldValue(field, value, TextFormat.multiLineOutput(output));
      }

      private void printFieldValue(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
         switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
               generator.print(((Integer)value).toString());
               break;
            case INT64:
            case SINT64:
            case SFIXED64:
               generator.print(((Long)value).toString());
               break;
            case BOOL:
               generator.print(((Boolean)value).toString());
               break;
            case FLOAT:
               generator.print(((Float)value).toString());
               break;
            case DOUBLE:
               generator.print(((Double)value).toString());
               break;
            case UINT32:
            case FIXED32:
               generator.print(TextFormat.unsignedToString((Integer)value));
               break;
            case UINT64:
            case FIXED64:
               generator.print(TextFormat.unsignedToString((Long)value));
               break;
            case STRING:
               generator.print("\"");
               generator.print(this.escapeNonAscii ? TextFormatEscaper.escapeText((String)value) : TextFormat.escapeDoubleQuotesAndBackslashes((String)value).replace("\n", "\\n"));
               generator.print("\"");
               break;
            case BYTES:
               generator.print("\"");
               if (value instanceof ByteString) {
                  generator.print(TextFormat.escapeBytes((ByteString)value));
               } else {
                  generator.print(TextFormat.escapeBytes((byte[])((byte[])value)));
               }

               generator.print("\"");
               break;
            case ENUM:
               generator.print(((Descriptors.EnumValueDescriptor)value).getName());
               break;
            case MESSAGE:
            case GROUP:
               this.print((MessageOrBuilder)((Message)value), (TextGenerator)generator);
         }

      }

      public String printToString(MessageOrBuilder message) {
         try {
            StringBuilder text = new StringBuilder();
            this.print((MessageOrBuilder)message, (Appendable)text);
            return text.toString();
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      public String printToString(UnknownFieldSet fields) {
         try {
            StringBuilder text = new StringBuilder();
            this.print((UnknownFieldSet)fields, (Appendable)text);
            return text.toString();
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      public String shortDebugString(MessageOrBuilder message) {
         try {
            StringBuilder text = new StringBuilder();
            this.print(message, TextFormat.singleLineOutput(text));
            return text.toString();
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      public String shortDebugString(Descriptors.FieldDescriptor field, Object value) {
         try {
            StringBuilder text = new StringBuilder();
            this.printField(field, value, TextFormat.singleLineOutput(text));
            return text.toString();
         } catch (IOException var4) {
            throw new IllegalStateException(var4);
         }
      }

      public String shortDebugString(UnknownFieldSet fields) {
         try {
            StringBuilder text = new StringBuilder();
            printUnknownFields(fields, TextFormat.singleLineOutput(text));
            return text.toString();
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      private static void printUnknownFieldValue(int var0, Object var1, TextGenerator var2) throws IOException {
         // $FF: Couldn't be decompiled
      }

      private void printMessage(MessageOrBuilder message, TextGenerator generator) throws IOException {
         Iterator var3 = message.getAllFields().entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<Descriptors.FieldDescriptor, Object> field = (Map.Entry)var3.next();
            this.printField((Descriptors.FieldDescriptor)field.getKey(), field.getValue(), generator);
         }

         printUnknownFields(message.getUnknownFields(), generator);
      }

      private void printSingleField(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
         if (field.isExtension()) {
            generator.print("[");
            if (field.getContainingType().getOptions().getMessageSetWireFormat() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && field.isOptional() && field.getExtensionScope() == field.getMessageType()) {
               generator.print(field.getMessageType().getFullName());
            } else {
               generator.print(field.getFullName());
            }

            generator.print("]");
         } else if (field.getType() == Descriptors.FieldDescriptor.Type.GROUP) {
            generator.print(field.getMessageType().getName());
         } else {
            generator.print(field.getName());
         }

         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            generator.print(" {");
            generator.eol();
            generator.indent();
         } else {
            generator.print(": ");
         }

         this.printFieldValue(field, value, generator);
         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            generator.outdent();
            generator.print("}");
         }

         generator.eol();
      }

      private static void printUnknownFields(UnknownFieldSet unknownFields, TextGenerator generator) throws IOException {
         Iterator var2 = unknownFields.asMap().entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<Integer, UnknownFieldSet.Field> entry = (Map.Entry)var2.next();
            int number = (Integer)entry.getKey();
            UnknownFieldSet.Field field = (UnknownFieldSet.Field)entry.getValue();
            printUnknownField(number, 0, field.getVarintList(), generator);
            printUnknownField(number, 5, field.getFixed32List(), generator);
            printUnknownField(number, 1, field.getFixed64List(), generator);
            printUnknownField(number, 2, field.getLengthDelimitedList(), generator);
            Iterator var6 = field.getGroupList().iterator();

            while(var6.hasNext()) {
               UnknownFieldSet value = (UnknownFieldSet)var6.next();
               generator.print(((Integer)entry.getKey()).toString());
               generator.print(" {");
               generator.eol();
               generator.indent();
               printUnknownFields(value, generator);
               generator.outdent();
               generator.print("}");
               generator.eol();
            }
         }

      }

      private static void printUnknownField(int number, int wireType, List<?> values, TextGenerator generator) throws IOException {
         Iterator var4 = values.iterator();

         while(var4.hasNext()) {
            Object value = var4.next();
            generator.print(String.valueOf(number));
            generator.print(": ");
            printUnknownFieldValue(wireType, value, generator);
            generator.eol();
         }

      }

      // $FF: synthetic method
      static void access$000(UnknownFieldSet x0, TextGenerator x1) throws IOException {
         printUnknownFields(x0, x1);
      }
   }
}
