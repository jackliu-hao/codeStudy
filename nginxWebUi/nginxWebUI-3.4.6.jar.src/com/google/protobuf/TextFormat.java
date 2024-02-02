/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.math.BigInteger;
/*      */ import java.nio.CharBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.logging.Logger;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
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
/*      */ public final class TextFormat
/*      */ {
/*   58 */   private static final Logger logger = Logger.getLogger(TextFormat.class.getName());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void print(MessageOrBuilder message, Appendable output) throws IOException {
/*   70 */     printer().print(message, output);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void print(UnknownFieldSet fields, Appendable output) throws IOException {
/*   81 */     printer().print(fields, output);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void printUnicode(MessageOrBuilder message, Appendable output) throws IOException {
/*   92 */     printer().escapingNonAscii(false).print(message, output);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void printUnicode(UnknownFieldSet fields, Appendable output) throws IOException {
/*  103 */     printer().escapingNonAscii(false).print(fields, output);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String shortDebugString(MessageOrBuilder message) {
/*  112 */     return printer().shortDebugString(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String shortDebugString(Descriptors.FieldDescriptor field, Object value) {
/*  123 */     return printer().shortDebugString(field, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String shortDebugString(UnknownFieldSet fields) {
/*  134 */     return printer().shortDebugString(fields);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String printToString(MessageOrBuilder message) {
/*  144 */     return printer().printToString(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String printToString(UnknownFieldSet fields) {
/*  154 */     return printer().printToString(fields);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String printToUnicodeString(MessageOrBuilder message) {
/*  165 */     return printer().escapingNonAscii(false).printToString(message);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String printToUnicodeString(UnknownFieldSet fields) {
/*  176 */     return printer().escapingNonAscii(false).printToString(fields);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void printField(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
/*  184 */     printer().printField(field, value, output);
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String printFieldToString(Descriptors.FieldDescriptor field, Object value) {
/*  190 */     return printer().printFieldToString(field, value);
/*      */   }
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
/*      */   @Deprecated
/*      */   public static void printUnicodeFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
/*  211 */     printer().escapingNonAscii(false).printFieldValue(field, value, output);
/*      */   }
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
/*      */   @Deprecated
/*      */   public static void printFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
/*  227 */     printer().printFieldValue(field, value, output);
/*      */   }
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
/*      */   public static void printUnknownFieldValue(int tag, Object value, Appendable output) throws IOException {
/*  241 */     printUnknownFieldValue(tag, value, multiLineOutput(output));
/*      */   }
/*      */ 
/*      */   
/*      */   private static void printUnknownFieldValue(int tag, Object value, TextGenerator generator) throws IOException {
/*  246 */     switch (WireFormat.getTagWireType(tag)) {
/*      */       case 0:
/*  248 */         generator.print(unsignedToString(((Long)value).longValue()));
/*      */         return;
/*      */       case 5:
/*  251 */         generator.print(String.format((Locale)null, "0x%08x", new Object[] { value }));
/*      */         return;
/*      */       case 1:
/*  254 */         generator.print(String.format((Locale)null, "0x%016x", new Object[] { value }));
/*      */         return;
/*      */       
/*      */       case 2:
/*      */         try {
/*  259 */           UnknownFieldSet message = UnknownFieldSet.parseFrom((ByteString)value);
/*  260 */           generator.print("{");
/*  261 */           generator.eol();
/*  262 */           generator.indent();
/*  263 */           Printer.printUnknownFields(message, generator);
/*  264 */           generator.outdent();
/*  265 */           generator.print("}");
/*  266 */         } catch (InvalidProtocolBufferException e) {
/*      */           
/*  268 */           generator.print("\"");
/*  269 */           generator.print(escapeBytes((ByteString)value));
/*  270 */           generator.print("\"");
/*      */         } 
/*      */         return;
/*      */       case 3:
/*  274 */         Printer.printUnknownFields((UnknownFieldSet)value, generator);
/*      */         return;
/*      */     } 
/*  277 */     throw new IllegalArgumentException("Bad tag: " + tag);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Printer printer() {
/*  283 */     return Printer.DEFAULT;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Printer
/*      */   {
/*  290 */     private static final Printer DEFAULT = new Printer(true, TypeRegistry.getEmptyTypeRegistry());
/*      */     
/*      */     private final boolean escapeNonAscii;
/*      */     
/*      */     private final TypeRegistry typeRegistry;
/*      */ 
/*      */     
/*      */     private Printer(boolean escapeNonAscii, TypeRegistry typeRegistry) {
/*  298 */       this.escapeNonAscii = escapeNonAscii;
/*  299 */       this.typeRegistry = typeRegistry;
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
/*      */     public Printer escapingNonAscii(boolean escapeNonAscii) {
/*  312 */       return new Printer(escapeNonAscii, this.typeRegistry);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Printer usingTypeRegistry(TypeRegistry typeRegistry) {
/*  322 */       if (this.typeRegistry != TypeRegistry.getEmptyTypeRegistry()) {
/*  323 */         throw new IllegalArgumentException("Only one typeRegistry is allowed.");
/*      */       }
/*  325 */       return new Printer(this.escapeNonAscii, typeRegistry);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void print(MessageOrBuilder message, Appendable output) throws IOException {
/*  334 */       print(message, TextFormat.multiLineOutput(output));
/*      */     }
/*      */ 
/*      */     
/*      */     public void print(UnknownFieldSet fields, Appendable output) throws IOException {
/*  339 */       printUnknownFields(fields, TextFormat.multiLineOutput(output));
/*      */     }
/*      */ 
/*      */     
/*      */     private void print(MessageOrBuilder message, TextFormat.TextGenerator generator) throws IOException {
/*  344 */       if (message.getDescriptorForType().getFullName().equals("google.protobuf.Any") && 
/*  345 */         printAny(message, generator)) {
/*      */         return;
/*      */       }
/*  348 */       printMessage(message, generator);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean printAny(MessageOrBuilder message, TextFormat.TextGenerator generator) throws IOException {
/*  358 */       Descriptors.Descriptor messageType = message.getDescriptorForType();
/*  359 */       Descriptors.FieldDescriptor typeUrlField = messageType.findFieldByNumber(1);
/*  360 */       Descriptors.FieldDescriptor valueField = messageType.findFieldByNumber(2);
/*  361 */       if (typeUrlField == null || typeUrlField
/*  362 */         .getType() != Descriptors.FieldDescriptor.Type.STRING || valueField == null || valueField
/*      */         
/*  364 */         .getType() != Descriptors.FieldDescriptor.Type.BYTES)
/*      */       {
/*      */         
/*  367 */         return false;
/*      */       }
/*  369 */       String typeUrl = (String)message.getField(typeUrlField);
/*      */ 
/*      */       
/*  372 */       if (typeUrl.isEmpty()) {
/*  373 */         return false;
/*      */       }
/*  375 */       Object value = message.getField(valueField);
/*      */       
/*  377 */       Message.Builder contentBuilder = null;
/*      */       try {
/*  379 */         Descriptors.Descriptor contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
/*  380 */         if (contentType == null) {
/*  381 */           return false;
/*      */         }
/*  383 */         contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
/*  384 */         contentBuilder.mergeFrom((ByteString)value);
/*  385 */       } catch (InvalidProtocolBufferException e) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  390 */         return false;
/*      */       } 
/*  392 */       generator.print("[");
/*  393 */       generator.print(typeUrl);
/*  394 */       generator.print("] {");
/*  395 */       generator.eol();
/*  396 */       generator.indent();
/*  397 */       print(contentBuilder, generator);
/*  398 */       generator.outdent();
/*  399 */       generator.print("}");
/*  400 */       generator.eol();
/*  401 */       return true;
/*      */     }
/*      */     
/*      */     public String printFieldToString(Descriptors.FieldDescriptor field, Object value) {
/*      */       try {
/*  406 */         StringBuilder text = new StringBuilder();
/*  407 */         printField(field, value, text);
/*  408 */         return text.toString();
/*  409 */       } catch (IOException e) {
/*  410 */         throw new IllegalStateException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void printField(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
/*  416 */       printField(field, value, TextFormat.multiLineOutput(output));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void printField(Descriptors.FieldDescriptor field, Object value, TextFormat.TextGenerator generator) throws IOException {
/*  422 */       if (field.isRepeated()) {
/*      */         
/*  424 */         for (Object element : value) {
/*  425 */           printSingleField(field, element, generator);
/*      */         }
/*      */       } else {
/*  428 */         printSingleField(field, value, generator);
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
/*      */ 
/*      */     
/*      */     public void printFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
/*  444 */       printFieldValue(field, value, TextFormat.multiLineOutput(output));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void printFieldValue(Descriptors.FieldDescriptor field, Object value, TextFormat.TextGenerator generator) throws IOException {
/*  450 */       switch (field.getType()) {
/*      */         case INT32:
/*      */         case SINT32:
/*      */         case SFIXED32:
/*  454 */           generator.print(((Integer)value).toString());
/*      */           break;
/*      */         
/*      */         case INT64:
/*      */         case SINT64:
/*      */         case SFIXED64:
/*  460 */           generator.print(((Long)value).toString());
/*      */           break;
/*      */         
/*      */         case BOOL:
/*  464 */           generator.print(((Boolean)value).toString());
/*      */           break;
/*      */         
/*      */         case FLOAT:
/*  468 */           generator.print(((Float)value).toString());
/*      */           break;
/*      */         
/*      */         case DOUBLE:
/*  472 */           generator.print(((Double)value).toString());
/*      */           break;
/*      */         
/*      */         case UINT32:
/*      */         case FIXED32:
/*  477 */           generator.print(TextFormat.unsignedToString(((Integer)value).intValue()));
/*      */           break;
/*      */         
/*      */         case UINT64:
/*      */         case FIXED64:
/*  482 */           generator.print(TextFormat.unsignedToString(((Long)value).longValue()));
/*      */           break;
/*      */         
/*      */         case STRING:
/*  486 */           generator.print("\"");
/*  487 */           generator.print(this.escapeNonAscii ? 
/*      */               
/*  489 */               TextFormatEscaper.escapeText((String)value) : 
/*  490 */               TextFormat.escapeDoubleQuotesAndBackslashes((String)value).replace("\n", "\\n"));
/*  491 */           generator.print("\"");
/*      */           break;
/*      */         
/*      */         case BYTES:
/*  495 */           generator.print("\"");
/*  496 */           if (value instanceof ByteString) {
/*  497 */             generator.print(TextFormat.escapeBytes((ByteString)value));
/*      */           } else {
/*  499 */             generator.print(TextFormat.escapeBytes((byte[])value));
/*      */           } 
/*  501 */           generator.print("\"");
/*      */           break;
/*      */         
/*      */         case ENUM:
/*  505 */           generator.print(((Descriptors.EnumValueDescriptor)value).getName());
/*      */           break;
/*      */         
/*      */         case MESSAGE:
/*      */         case GROUP:
/*  510 */           print((Message)value, generator);
/*      */           break;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public String printToString(MessageOrBuilder message) {
/*      */       try {
/*  518 */         StringBuilder text = new StringBuilder();
/*  519 */         print(message, text);
/*  520 */         return text.toString();
/*  521 */       } catch (IOException e) {
/*  522 */         throw new IllegalStateException(e);
/*      */       } 
/*      */     }
/*      */     
/*      */     public String printToString(UnknownFieldSet fields) {
/*      */       try {
/*  528 */         StringBuilder text = new StringBuilder();
/*  529 */         print(fields, text);
/*  530 */         return text.toString();
/*  531 */       } catch (IOException e) {
/*  532 */         throw new IllegalStateException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String shortDebugString(MessageOrBuilder message) {
/*      */       try {
/*  542 */         StringBuilder text = new StringBuilder();
/*  543 */         print(message, TextFormat.singleLineOutput(text));
/*  544 */         return text.toString();
/*  545 */       } catch (IOException e) {
/*  546 */         throw new IllegalStateException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String shortDebugString(Descriptors.FieldDescriptor field, Object value) {
/*      */       try {
/*  556 */         StringBuilder text = new StringBuilder();
/*  557 */         printField(field, value, TextFormat.singleLineOutput(text));
/*  558 */         return text.toString();
/*  559 */       } catch (IOException e) {
/*  560 */         throw new IllegalStateException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String shortDebugString(UnknownFieldSet fields) {
/*      */       try {
/*  570 */         StringBuilder text = new StringBuilder();
/*  571 */         printUnknownFields(fields, TextFormat.singleLineOutput(text));
/*  572 */         return text.toString();
/*  573 */       } catch (IOException e) {
/*  574 */         throw new IllegalStateException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private static void printUnknownFieldValue(int tag, Object value, TextFormat.TextGenerator generator) throws IOException {
/*  580 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         case 0:
/*  582 */           generator.print(TextFormat.unsignedToString(((Long)value).longValue()));
/*      */           return;
/*      */         case 5:
/*  585 */           generator.print(String.format((Locale)null, "0x%08x", new Object[] { value }));
/*      */           return;
/*      */         case 1:
/*  588 */           generator.print(String.format((Locale)null, "0x%016x", new Object[] { value }));
/*      */           return;
/*      */         
/*      */         case 2:
/*      */           try {
/*  593 */             UnknownFieldSet message = UnknownFieldSet.parseFrom((ByteString)value);
/*  594 */             generator.print("{");
/*  595 */             generator.eol();
/*  596 */             generator.indent();
/*  597 */             printUnknownFields(message, generator);
/*  598 */             generator.outdent();
/*  599 */             generator.print("}");
/*  600 */           } catch (InvalidProtocolBufferException e) {
/*      */             
/*  602 */             generator.print("\"");
/*  603 */             generator.print(TextFormat.escapeBytes((ByteString)value));
/*  604 */             generator.print("\"");
/*      */           } 
/*      */           return;
/*      */         case 3:
/*  608 */           printUnknownFields((UnknownFieldSet)value, generator);
/*      */           return;
/*      */       } 
/*  611 */       throw new IllegalArgumentException("Bad tag: " + tag);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void printMessage(MessageOrBuilder message, TextFormat.TextGenerator generator) throws IOException {
/*  617 */       for (Map.Entry<Descriptors.FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
/*  618 */         printField(field.getKey(), field.getValue(), generator);
/*      */       }
/*  620 */       printUnknownFields(message.getUnknownFields(), generator);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void printSingleField(Descriptors.FieldDescriptor field, Object value, TextFormat.TextGenerator generator) throws IOException {
/*  626 */       if (field.isExtension()) {
/*  627 */         generator.print("[");
/*      */         
/*  629 */         if (field.getContainingType().getOptions().getMessageSetWireFormat() && field
/*  630 */           .getType() == Descriptors.FieldDescriptor.Type.MESSAGE && field
/*  631 */           .isOptional() && field
/*      */           
/*  633 */           .getExtensionScope() == field.getMessageType()) {
/*  634 */           generator.print(field.getMessageType().getFullName());
/*      */         } else {
/*  636 */           generator.print(field.getFullName());
/*      */         } 
/*  638 */         generator.print("]");
/*      */       }
/*  640 */       else if (field.getType() == Descriptors.FieldDescriptor.Type.GROUP) {
/*      */         
/*  642 */         generator.print(field.getMessageType().getName());
/*      */       } else {
/*  644 */         generator.print(field.getName());
/*      */       } 
/*      */ 
/*      */       
/*  648 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*  649 */         generator.print(" {");
/*  650 */         generator.eol();
/*  651 */         generator.indent();
/*      */       } else {
/*  653 */         generator.print(": ");
/*      */       } 
/*      */       
/*  656 */       printFieldValue(field, value, generator);
/*      */       
/*  658 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*  659 */         generator.outdent();
/*  660 */         generator.print("}");
/*      */       } 
/*  662 */       generator.eol();
/*      */     }
/*      */ 
/*      */     
/*      */     private static void printUnknownFields(UnknownFieldSet unknownFields, TextFormat.TextGenerator generator) throws IOException {
/*  667 */       for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
/*  668 */         int number = ((Integer)entry.getKey()).intValue();
/*  669 */         UnknownFieldSet.Field field = entry.getValue();
/*  670 */         printUnknownField(number, 0, field.getVarintList(), generator);
/*  671 */         printUnknownField(number, 5, field.getFixed32List(), generator);
/*  672 */         printUnknownField(number, 1, field.getFixed64List(), generator);
/*  673 */         printUnknownField(number, 2, field
/*      */ 
/*      */             
/*  676 */             .getLengthDelimitedList(), generator);
/*      */         
/*  678 */         for (UnknownFieldSet value : field.getGroupList()) {
/*  679 */           generator.print(((Integer)entry.getKey()).toString());
/*  680 */           generator.print(" {");
/*  681 */           generator.eol();
/*  682 */           generator.indent();
/*  683 */           printUnknownFields(value, generator);
/*  684 */           generator.outdent();
/*  685 */           generator.print("}");
/*  686 */           generator.eol();
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private static void printUnknownField(int number, int wireType, List<?> values, TextFormat.TextGenerator generator) throws IOException {
/*  694 */       for (Object value : values) {
/*  695 */         generator.print(String.valueOf(number));
/*  696 */         generator.print(": ");
/*  697 */         printUnknownFieldValue(wireType, value, generator);
/*  698 */         generator.eol();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static String unsignedToString(int value) {
/*  705 */     if (value >= 0) {
/*  706 */       return Integer.toString(value);
/*      */     }
/*  708 */     return Long.toString(value & 0xFFFFFFFFL);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unsignedToString(long value) {
/*  714 */     if (value >= 0L) {
/*  715 */       return Long.toString(value);
/*      */     }
/*      */ 
/*      */     
/*  719 */     return BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63).toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private static TextGenerator multiLineOutput(Appendable output) {
/*  724 */     return new TextGenerator(output, false);
/*      */   }
/*      */   
/*      */   private static TextGenerator singleLineOutput(Appendable output) {
/*  728 */     return new TextGenerator(output, true);
/*      */   }
/*      */   
/*      */   private static final class TextGenerator
/*      */   {
/*      */     private final Appendable output;
/*  734 */     private final StringBuilder indent = new StringBuilder();
/*      */     
/*      */     private final boolean singleLineMode;
/*      */     
/*      */     private boolean atStartOfLine = false;
/*      */ 
/*      */     
/*      */     private TextGenerator(Appendable output, boolean singleLineMode) {
/*  742 */       this.output = output;
/*  743 */       this.singleLineMode = singleLineMode;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void indent() {
/*  752 */       this.indent.append("  ");
/*      */     }
/*      */ 
/*      */     
/*      */     public void outdent() {
/*  757 */       int length = this.indent.length();
/*  758 */       if (length == 0) {
/*  759 */         throw new IllegalArgumentException(" Outdent() without matching Indent().");
/*      */       }
/*  761 */       this.indent.setLength(length - 2);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void print(CharSequence text) throws IOException {
/*  769 */       if (this.atStartOfLine) {
/*  770 */         this.atStartOfLine = false;
/*  771 */         this.output.append(this.singleLineMode ? " " : this.indent);
/*      */       } 
/*  773 */       this.output.append(text);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void eol() throws IOException {
/*  782 */       if (!this.singleLineMode) {
/*  783 */         this.output.append("\n");
/*      */       }
/*  785 */       this.atStartOfLine = true;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class Tokenizer
/*      */   {
/*      */     private final CharSequence text;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final Matcher matcher;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private String currentToken;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  822 */     private int pos = 0;
/*      */ 
/*      */     
/*  825 */     private int line = 0;
/*  826 */     private int column = 0;
/*      */ 
/*      */ 
/*      */     
/*  830 */     private int previousLine = 0;
/*  831 */     private int previousColumn = 0;
/*      */ 
/*      */ 
/*      */     
/*  835 */     private static final Pattern WHITESPACE = Pattern.compile("(\\s|(#.*$))++", 8);
/*      */     
/*  837 */     private static final Pattern TOKEN = Pattern.compile("[a-zA-Z_][0-9a-zA-Z_+-]*+|[.]?[0-9+-][0-9a-zA-Z_.+-]*+|\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|'([^'\n\\\\]|\\\\.)*+('|\\\\?$)", 8);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  845 */     private static final Pattern DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?", 2);
/*      */     
/*  847 */     private static final Pattern FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?", 2);
/*  848 */     private static final Pattern FLOAT_NAN = Pattern.compile("nanf?", 2);
/*      */ 
/*      */     
/*      */     private Tokenizer(CharSequence text) {
/*  852 */       this.text = text;
/*  853 */       this.matcher = WHITESPACE.matcher(text);
/*  854 */       skipWhitespace();
/*  855 */       nextToken();
/*      */     }
/*      */     
/*      */     int getPreviousLine() {
/*  859 */       return this.previousLine;
/*      */     }
/*      */     
/*      */     int getPreviousColumn() {
/*  863 */       return this.previousColumn;
/*      */     }
/*      */     
/*      */     int getLine() {
/*  867 */       return this.line;
/*      */     }
/*      */     
/*      */     int getColumn() {
/*  871 */       return this.column;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean atEnd() {
/*  876 */       return (this.currentToken.length() == 0);
/*      */     }
/*      */ 
/*      */     
/*      */     public void nextToken() {
/*  881 */       this.previousLine = this.line;
/*  882 */       this.previousColumn = this.column;
/*      */ 
/*      */       
/*  885 */       while (this.pos < this.matcher.regionStart()) {
/*  886 */         if (this.text.charAt(this.pos) == '\n') {
/*  887 */           this.line++;
/*  888 */           this.column = 0;
/*      */         } else {
/*  890 */           this.column++;
/*      */         } 
/*  892 */         this.pos++;
/*      */       } 
/*      */ 
/*      */       
/*  896 */       if (this.matcher.regionStart() == this.matcher.regionEnd()) {
/*      */         
/*  898 */         this.currentToken = "";
/*      */       } else {
/*  900 */         this.matcher.usePattern(TOKEN);
/*  901 */         if (this.matcher.lookingAt()) {
/*  902 */           this.currentToken = this.matcher.group();
/*  903 */           this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
/*      */         } else {
/*      */           
/*  906 */           this.currentToken = String.valueOf(this.text.charAt(this.pos));
/*  907 */           this.matcher.region(this.pos + 1, this.matcher.regionEnd());
/*      */         } 
/*      */         
/*  910 */         skipWhitespace();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void skipWhitespace() {
/*  916 */       this.matcher.usePattern(WHITESPACE);
/*  917 */       if (this.matcher.lookingAt()) {
/*  918 */         this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean tryConsume(String token) {
/*  927 */       if (this.currentToken.equals(token)) {
/*  928 */         nextToken();
/*  929 */         return true;
/*      */       } 
/*  931 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void consume(String token) throws TextFormat.ParseException {
/*  940 */       if (!tryConsume(token)) {
/*  941 */         throw parseException("Expected \"" + token + "\".");
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean lookingAtInteger() {
/*  947 */       if (this.currentToken.length() == 0) {
/*  948 */         return false;
/*      */       }
/*      */       
/*  951 */       char c = this.currentToken.charAt(0);
/*  952 */       return (('0' <= c && c <= '9') || c == '-' || c == '+');
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean lookingAt(String text) {
/*  957 */       return this.currentToken.equals(text);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String consumeIdentifier() throws TextFormat.ParseException {
/*  965 */       for (int i = 0; i < this.currentToken.length(); ) {
/*  966 */         char c = this.currentToken.charAt(i);
/*  967 */         if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || c == '_' || c == '.') {
/*      */           i++;
/*      */ 
/*      */           
/*      */           continue;
/*      */         } 
/*      */         
/*  974 */         throw parseException("Expected identifier. Found '" + this.currentToken + "'");
/*      */       } 
/*      */ 
/*      */       
/*  978 */       String result = this.currentToken;
/*  979 */       nextToken();
/*  980 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean tryConsumeIdentifier() {
/*      */       try {
/*  989 */         consumeIdentifier();
/*  990 */         return true;
/*  991 */       } catch (ParseException e) {
/*  992 */         return false;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int consumeInt32() throws TextFormat.ParseException {
/*      */       try {
/* 1002 */         int result = TextFormat.parseInt32(this.currentToken);
/* 1003 */         nextToken();
/* 1004 */         return result;
/* 1005 */       } catch (NumberFormatException e) {
/* 1006 */         throw integerParseException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int consumeUInt32() throws TextFormat.ParseException {
/*      */       try {
/* 1016 */         int result = TextFormat.parseUInt32(this.currentToken);
/* 1017 */         nextToken();
/* 1018 */         return result;
/* 1019 */       } catch (NumberFormatException e) {
/* 1020 */         throw integerParseException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long consumeInt64() throws TextFormat.ParseException {
/*      */       try {
/* 1030 */         long result = TextFormat.parseInt64(this.currentToken);
/* 1031 */         nextToken();
/* 1032 */         return result;
/* 1033 */       } catch (NumberFormatException e) {
/* 1034 */         throw integerParseException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean tryConsumeInt64() {
/*      */       try {
/* 1044 */         consumeInt64();
/* 1045 */         return true;
/* 1046 */       } catch (ParseException e) {
/* 1047 */         return false;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long consumeUInt64() throws TextFormat.ParseException {
/*      */       try {
/* 1057 */         long result = TextFormat.parseUInt64(this.currentToken);
/* 1058 */         nextToken();
/* 1059 */         return result;
/* 1060 */       } catch (NumberFormatException e) {
/* 1061 */         throw integerParseException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean tryConsumeUInt64() {
/*      */       try {
/* 1071 */         consumeUInt64();
/* 1072 */         return true;
/* 1073 */       } catch (ParseException e) {
/* 1074 */         return false;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public double consumeDouble() throws TextFormat.ParseException {
/* 1085 */       if (DOUBLE_INFINITY.matcher(this.currentToken).matches()) {
/* 1086 */         boolean negative = this.currentToken.startsWith("-");
/* 1087 */         nextToken();
/* 1088 */         return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
/*      */       } 
/* 1090 */       if (this.currentToken.equalsIgnoreCase("nan")) {
/* 1091 */         nextToken();
/* 1092 */         return Double.NaN;
/*      */       } 
/*      */       try {
/* 1095 */         double result = Double.parseDouble(this.currentToken);
/* 1096 */         nextToken();
/* 1097 */         return result;
/* 1098 */       } catch (NumberFormatException e) {
/* 1099 */         throw floatParseException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean tryConsumeDouble() {
/*      */       try {
/* 1109 */         consumeDouble();
/* 1110 */         return true;
/* 1111 */       } catch (ParseException e) {
/* 1112 */         return false;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float consumeFloat() throws TextFormat.ParseException {
/* 1123 */       if (FLOAT_INFINITY.matcher(this.currentToken).matches()) {
/* 1124 */         boolean negative = this.currentToken.startsWith("-");
/* 1125 */         nextToken();
/* 1126 */         return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
/*      */       } 
/* 1128 */       if (FLOAT_NAN.matcher(this.currentToken).matches()) {
/* 1129 */         nextToken();
/* 1130 */         return Float.NaN;
/*      */       } 
/*      */       try {
/* 1133 */         float result = Float.parseFloat(this.currentToken);
/* 1134 */         nextToken();
/* 1135 */         return result;
/* 1136 */       } catch (NumberFormatException e) {
/* 1137 */         throw floatParseException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean tryConsumeFloat() {
/*      */       try {
/* 1147 */         consumeFloat();
/* 1148 */         return true;
/* 1149 */       } catch (ParseException e) {
/* 1150 */         return false;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean consumeBoolean() throws TextFormat.ParseException {
/* 1159 */       if (this.currentToken.equals("true") || this.currentToken
/* 1160 */         .equals("True") || this.currentToken
/* 1161 */         .equals("t") || this.currentToken
/* 1162 */         .equals("1")) {
/* 1163 */         nextToken();
/* 1164 */         return true;
/* 1165 */       }  if (this.currentToken.equals("false") || this.currentToken
/* 1166 */         .equals("False") || this.currentToken
/* 1167 */         .equals("f") || this.currentToken
/* 1168 */         .equals("0")) {
/* 1169 */         nextToken();
/* 1170 */         return false;
/*      */       } 
/* 1172 */       throw parseException("Expected \"true\" or \"false\". Found \"" + this.currentToken + "\".");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String consumeString() throws TextFormat.ParseException {
/* 1181 */       return consumeByteString().toStringUtf8();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean tryConsumeString() {
/*      */       try {
/* 1187 */         consumeString();
/* 1188 */         return true;
/* 1189 */       } catch (ParseException e) {
/* 1190 */         return false;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ByteString consumeByteString() throws TextFormat.ParseException {
/* 1199 */       List<ByteString> list = new ArrayList<>();
/* 1200 */       consumeByteString(list);
/* 1201 */       while (this.currentToken.startsWith("'") || this.currentToken.startsWith("\"")) {
/* 1202 */         consumeByteString(list);
/*      */       }
/* 1204 */       return ByteString.copyFrom(list);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void consumeByteString(List<ByteString> list) throws TextFormat.ParseException {
/* 1213 */       char quote = (this.currentToken.length() > 0) ? this.currentToken.charAt(0) : Character.MIN_VALUE;
/* 1214 */       if (quote != '"' && quote != '\'') {
/* 1215 */         throw parseException("Expected string.");
/*      */       }
/*      */       
/* 1218 */       if (this.currentToken.length() < 2 || this.currentToken.charAt(this.currentToken.length() - 1) != quote) {
/* 1219 */         throw parseException("String missing ending quote.");
/*      */       }
/*      */       
/*      */       try {
/* 1223 */         String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
/* 1224 */         ByteString result = TextFormat.unescapeBytes(escaped);
/* 1225 */         nextToken();
/* 1226 */         list.add(result);
/* 1227 */       } catch (InvalidEscapeSequenceException e) {
/* 1228 */         throw parseException(e.getMessage());
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TextFormat.ParseException parseException(String description) {
/* 1238 */       return new TextFormat.ParseException(this.line + 1, this.column + 1, description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TextFormat.ParseException parseExceptionPreviousToken(String description) {
/* 1247 */       return new TextFormat.ParseException(this.previousLine + 1, this.previousColumn + 1, description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private TextFormat.ParseException integerParseException(NumberFormatException e) {
/* 1255 */       return parseException("Couldn't parse integer: " + e.getMessage());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private TextFormat.ParseException floatParseException(NumberFormatException e) {
/* 1263 */       return parseException("Couldn't parse number: " + e.getMessage());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TextFormat.UnknownFieldParseException unknownFieldParseExceptionPreviousToken(String unknownField, String description) {
/* 1273 */       return new TextFormat.UnknownFieldParseException(this.previousLine + 1, this.previousColumn + 1, unknownField, description);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class ParseException
/*      */     extends IOException
/*      */   {
/*      */     private static final long serialVersionUID = 3196188060225107702L;
/*      */     
/*      */     private final int line;
/*      */     private final int column;
/*      */     
/*      */     public ParseException(String message) {
/* 1287 */       this(-1, -1, message);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ParseException(int line, int column, String message) {
/* 1297 */       super(Integer.toString(line) + ":" + column + ": " + message);
/* 1298 */       this.line = line;
/* 1299 */       this.column = column;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getLine() {
/* 1307 */       return this.line;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getColumn() {
/* 1315 */       return this.column;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class UnknownFieldParseException
/*      */     extends ParseException
/*      */   {
/*      */     private final String unknownField;
/*      */ 
/*      */     
/*      */     public UnknownFieldParseException(String message) {
/* 1328 */       this(-1, -1, "", message);
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
/*      */     public UnknownFieldParseException(int line, int column, String unknownField, String message) {
/* 1340 */       super(line, column, message);
/* 1341 */       this.unknownField = unknownField;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getUnknownField() {
/* 1348 */       return this.unknownField;
/*      */     }
/*      */   }
/*      */   
/* 1352 */   private static final Parser PARSER = Parser.newBuilder().build();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Parser getParser() {
/* 1359 */     return PARSER;
/*      */   }
/*      */ 
/*      */   
/*      */   public static void merge(Readable input, Message.Builder builder) throws IOException {
/* 1364 */     PARSER.merge(input, builder);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void merge(CharSequence input, Message.Builder builder) throws ParseException {
/* 1370 */     PARSER.merge(input, builder);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Message> T parse(CharSequence input, Class<T> protoClass) throws ParseException {
/* 1380 */     Message.Builder builder = ((Message)Internal.<Message>getDefaultInstance(protoClass)).newBuilderForType();
/* 1381 */     merge(input, builder);
/*      */     
/* 1383 */     return (T)builder.build();
/*      */   }
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
/*      */   public static void merge(Readable input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {
/* 1396 */     PARSER.merge(input, extensionRegistry, builder);
/*      */   }
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
/*      */   public static void merge(CharSequence input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws ParseException {
/* 1409 */     PARSER.merge(input, extensionRegistry, builder);
/*      */   }
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
/*      */   public static <T extends Message> T parse(CharSequence input, ExtensionRegistry extensionRegistry, Class<T> protoClass) throws ParseException {
/* 1423 */     Message.Builder builder = ((Message)Internal.<Message>getDefaultInstance(protoClass)).newBuilderForType();
/* 1424 */     merge(input, extensionRegistry, builder);
/*      */     
/* 1426 */     return (T)builder.build();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Parser
/*      */   {
/*      */     private final TypeRegistry typeRegistry;
/*      */ 
/*      */     
/*      */     private final boolean allowUnknownFields;
/*      */ 
/*      */     
/*      */     private final boolean allowUnknownEnumValues;
/*      */ 
/*      */     
/*      */     private final boolean allowUnknownExtensions;
/*      */ 
/*      */     
/*      */     private final SingularOverwritePolicy singularOverwritePolicy;
/*      */ 
/*      */     
/*      */     private TextFormatParseInfoTree.Builder parseInfoTreeBuilder;
/*      */     
/*      */     private static final int BUFFER_SIZE = 4096;
/*      */ 
/*      */     
/*      */     public enum SingularOverwritePolicy
/*      */     {
/* 1455 */       ALLOW_SINGULAR_OVERWRITES,
/*      */       
/* 1457 */       FORBID_SINGULAR_OVERWRITES;
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
/*      */     private Parser(TypeRegistry typeRegistry, boolean allowUnknownFields, boolean allowUnknownEnumValues, boolean allowUnknownExtensions, SingularOverwritePolicy singularOverwritePolicy, TextFormatParseInfoTree.Builder parseInfoTreeBuilder) {
/* 1474 */       this.typeRegistry = typeRegistry;
/* 1475 */       this.allowUnknownFields = allowUnknownFields;
/* 1476 */       this.allowUnknownEnumValues = allowUnknownEnumValues;
/* 1477 */       this.allowUnknownExtensions = allowUnknownExtensions;
/* 1478 */       this.singularOverwritePolicy = singularOverwritePolicy;
/* 1479 */       this.parseInfoTreeBuilder = parseInfoTreeBuilder;
/*      */     }
/*      */ 
/*      */     
/*      */     public static Builder newBuilder() {
/* 1484 */       return new Builder();
/*      */     }
/*      */     
/*      */     public static class Builder
/*      */     {
/*      */       private boolean allowUnknownFields = false;
/*      */       private boolean allowUnknownEnumValues = false;
/*      */       private boolean allowUnknownExtensions = false;
/* 1492 */       private TextFormat.Parser.SingularOverwritePolicy singularOverwritePolicy = TextFormat.Parser.SingularOverwritePolicy.ALLOW_SINGULAR_OVERWRITES;
/*      */       
/* 1494 */       private TextFormatParseInfoTree.Builder parseInfoTreeBuilder = null;
/* 1495 */       private TypeRegistry typeRegistry = TypeRegistry.getEmptyTypeRegistry();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setTypeRegistry(TypeRegistry typeRegistry) {
/* 1504 */         this.typeRegistry = typeRegistry;
/* 1505 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setAllowUnknownFields(boolean allowUnknownFields) {
/* 1517 */         this.allowUnknownFields = allowUnknownFields;
/* 1518 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setAllowUnknownExtensions(boolean allowUnknownExtensions) {
/* 1528 */         this.allowUnknownExtensions = allowUnknownExtensions;
/* 1529 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder setSingularOverwritePolicy(TextFormat.Parser.SingularOverwritePolicy p) {
/* 1534 */         this.singularOverwritePolicy = p;
/* 1535 */         return this;
/*      */       }
/*      */       
/*      */       public Builder setParseInfoTreeBuilder(TextFormatParseInfoTree.Builder parseInfoTreeBuilder) {
/* 1539 */         this.parseInfoTreeBuilder = parseInfoTreeBuilder;
/* 1540 */         return this;
/*      */       }
/*      */       
/*      */       public TextFormat.Parser build() {
/* 1544 */         return new TextFormat.Parser(this.typeRegistry, this.allowUnknownFields, this.allowUnknownEnumValues, this.allowUnknownExtensions, this.singularOverwritePolicy, this.parseInfoTreeBuilder);
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
/*      */     public void merge(Readable input, Message.Builder builder) throws IOException {
/* 1558 */       merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void merge(CharSequence input, Message.Builder builder) throws TextFormat.ParseException {
/* 1566 */       merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
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
/*      */     public void merge(Readable input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {
/* 1586 */       merge(toStringBuilder(input), extensionRegistry, builder);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static StringBuilder toStringBuilder(Readable input) throws IOException {
/* 1595 */       StringBuilder text = new StringBuilder();
/* 1596 */       CharBuffer buffer = CharBuffer.allocate(4096);
/*      */       while (true) {
/* 1598 */         int n = input.read(buffer);
/* 1599 */         if (n == -1) {
/*      */           break;
/*      */         }
/* 1602 */         buffer.flip();
/* 1603 */         text.append(buffer, 0, n);
/*      */       } 
/* 1605 */       return text;
/*      */     }
/*      */     static final class UnknownField { final String message;
/*      */       final Type type;
/*      */       
/* 1610 */       enum Type { FIELD, EXTENSION; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       UnknownField(String message, Type type) {
/* 1617 */         this.message = message;
/* 1618 */         this.type = type;
/*      */       } }
/*      */ 
/*      */     
/*      */     enum Type { FIELD, EXTENSION; }
/*      */     
/*      */     private void checkUnknownFields(List<UnknownField> unknownFields) throws TextFormat.ParseException {
/* 1625 */       if (unknownFields.isEmpty()) {
/*      */         return;
/*      */       }
/*      */       
/* 1629 */       StringBuilder msg = new StringBuilder("Input contains unknown fields and/or extensions:");
/* 1630 */       for (UnknownField field : unknownFields) {
/* 1631 */         msg.append('\n').append(field.message);
/*      */       }
/*      */       
/* 1634 */       if (this.allowUnknownFields) {
/* 1635 */         TextFormat.logger.warning(msg.toString());
/*      */         
/*      */         return;
/*      */       } 
/* 1639 */       int firstErrorIndex = 0;
/* 1640 */       if (this.allowUnknownExtensions) {
/* 1641 */         boolean allUnknownExtensions = true;
/* 1642 */         for (UnknownField field : unknownFields) {
/* 1643 */           if (field.type == UnknownField.Type.FIELD) {
/* 1644 */             allUnknownExtensions = false;
/*      */             break;
/*      */           } 
/* 1647 */           firstErrorIndex++;
/*      */         } 
/* 1649 */         if (allUnknownExtensions) {
/* 1650 */           TextFormat.logger.warning(msg.toString());
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/* 1655 */       String[] lineColumn = ((UnknownField)unknownFields.get(firstErrorIndex)).message.split(":");
/* 1656 */       throw new TextFormat.ParseException(
/* 1657 */           Integer.parseInt(lineColumn[0]), Integer.parseInt(lineColumn[1]), msg.toString());
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
/*      */     public void merge(CharSequence input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws TextFormat.ParseException {
/* 1669 */       TextFormat.Tokenizer tokenizer = new TextFormat.Tokenizer(input);
/* 1670 */       MessageReflection.BuilderAdapter target = new MessageReflection.BuilderAdapter(builder);
/*      */       
/* 1672 */       List<UnknownField> unknownFields = new ArrayList<>();
/*      */       
/* 1674 */       while (!tokenizer.atEnd()) {
/* 1675 */         mergeField(tokenizer, extensionRegistry, target, unknownFields);
/*      */       }
/*      */       
/* 1678 */       checkUnknownFields(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void mergeField(TextFormat.Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, List<UnknownField> unknownFields) throws TextFormat.ParseException {
/* 1689 */       mergeField(tokenizer, extensionRegistry, target, this.parseInfoTreeBuilder, unknownFields);
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
/*      */     private void mergeField(TextFormat.Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws TextFormat.ParseException {
/* 1705 */       Descriptors.FieldDescriptor field = null;
/* 1706 */       int startLine = tokenizer.getLine();
/* 1707 */       int startColumn = tokenizer.getColumn();
/* 1708 */       Descriptors.Descriptor type = target.getDescriptorForType();
/* 1709 */       ExtensionRegistry.ExtensionInfo extension = null;
/*      */       
/* 1711 */       if (tokenizer.tryConsume("[")) {
/*      */         
/* 1713 */         StringBuilder name = new StringBuilder(tokenizer.consumeIdentifier());
/* 1714 */         while (tokenizer.tryConsume(".")) {
/* 1715 */           name.append('.');
/* 1716 */           name.append(tokenizer.consumeIdentifier());
/*      */         } 
/*      */         
/* 1719 */         extension = target.findExtensionByName(extensionRegistry, name.toString());
/*      */         
/* 1721 */         if (extension == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1727 */           String message = (tokenizer.getPreviousLine() + 1) + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + ".[" + name + "]";
/*      */ 
/*      */ 
/*      */           
/* 1731 */           unknownFields.add(new UnknownField(message, UnknownField.Type.EXTENSION));
/*      */         } else {
/* 1733 */           if (extension.descriptor.getContainingType() != type) {
/* 1734 */             throw tokenizer.parseExceptionPreviousToken("Extension \"" + name + "\" does not extend message type \"" + type
/*      */ 
/*      */ 
/*      */                 
/* 1738 */                 .getFullName() + "\".");
/*      */           }
/*      */           
/* 1741 */           field = extension.descriptor;
/*      */         } 
/*      */         
/* 1744 */         tokenizer.consume("]");
/*      */       } else {
/* 1746 */         String name = tokenizer.consumeIdentifier();
/* 1747 */         field = type.findFieldByName(name);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1752 */         if (field == null) {
/*      */ 
/*      */           
/* 1755 */           String lowerName = name.toLowerCase(Locale.US);
/* 1756 */           field = type.findFieldByName(lowerName);
/*      */           
/* 1758 */           if (field != null && field.getType() != Descriptors.FieldDescriptor.Type.GROUP) {
/* 1759 */             field = null;
/*      */           }
/*      */         } 
/*      */         
/* 1763 */         if (field != null && field
/* 1764 */           .getType() == Descriptors.FieldDescriptor.Type.GROUP && 
/* 1765 */           !field.getMessageType().getName().equals(name)) {
/* 1766 */           field = null;
/*      */         }
/*      */         
/* 1769 */         if (field == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1774 */           String message = (tokenizer.getPreviousLine() + 1) + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + "." + name;
/*      */ 
/*      */           
/* 1777 */           unknownFields.add(new UnknownField(message, UnknownField.Type.FIELD));
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1782 */       if (field == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1789 */         if (tokenizer.tryConsume(":") && !tokenizer.lookingAt("{") && !tokenizer.lookingAt("<")) {
/* 1790 */           skipFieldValue(tokenizer);
/*      */         } else {
/* 1792 */           skipFieldMessage(tokenizer);
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 1798 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 1799 */         tokenizer.tryConsume(":");
/* 1800 */         if (parseTreeBuilder != null)
/*      */         {
/* 1802 */           TextFormatParseInfoTree.Builder childParseTreeBuilder = parseTreeBuilder.getBuilderForSubMessageField(field);
/* 1803 */           consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, childParseTreeBuilder, unknownFields);
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */           
/* 1812 */           consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/* 1822 */         tokenizer.consume(":");
/* 1823 */         consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1833 */       if (parseTreeBuilder != null) {
/* 1834 */         parseTreeBuilder.setLocation(field, TextFormatParseLocation.create(startLine, startColumn));
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1839 */       if (!tokenizer.tryConsume(";")) {
/* 1840 */         tokenizer.tryConsume(",");
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
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void consumeFieldValues(TextFormat.Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, Descriptors.FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws TextFormat.ParseException {
/* 1858 */       if (field.isRepeated() && tokenizer.tryConsume("[")) {
/* 1859 */         if (!tokenizer.tryConsume("]")) {
/*      */           while (true) {
/* 1861 */             consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1869 */             if (tokenizer.tryConsume("]")) {
/*      */               break;
/*      */             }
/*      */             
/* 1873 */             tokenizer.consume(",");
/*      */           } 
/*      */         }
/*      */       } else {
/* 1877 */         consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void consumeFieldValue(TextFormat.Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, Descriptors.FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws TextFormat.ParseException {
/* 1898 */       if (this.singularOverwritePolicy == SingularOverwritePolicy.FORBID_SINGULAR_OVERWRITES && 
/* 1899 */         !field.isRepeated()) {
/* 1900 */         if (target.hasField(field))
/* 1901 */           throw tokenizer.parseExceptionPreviousToken("Non-repeated field \"" + field
/* 1902 */               .getFullName() + "\" cannot be overwritten."); 
/* 1903 */         if (field.getContainingOneof() != null && target
/* 1904 */           .hasOneof(field.getContainingOneof())) {
/* 1905 */           Descriptors.OneofDescriptor oneof = field.getContainingOneof();
/* 1906 */           throw tokenizer.parseExceptionPreviousToken("Field \"" + field
/*      */               
/* 1908 */               .getFullName() + "\" is specified along with field \"" + target
/*      */               
/* 1910 */               .getOneofFieldDescriptor(oneof).getFullName() + "\", another member of oneof \"" + oneof
/*      */               
/* 1912 */               .getName() + "\".");
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/* 1917 */       Object value = null;
/*      */       
/* 1919 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*      */         String endToken;
/* 1921 */         if (tokenizer.tryConsume("<")) {
/* 1922 */           endToken = ">";
/*      */         } else {
/* 1924 */           tokenizer.consume("{");
/* 1925 */           endToken = "}";
/*      */         } 
/*      */ 
/*      */         
/* 1929 */         if (field.getMessageType().getFullName().equals("google.protobuf.Any") && tokenizer
/* 1930 */           .tryConsume("[")) {
/*      */           
/* 1932 */           value = consumeAnyFieldValue(tokenizer, extensionRegistry, field, parseTreeBuilder, unknownFields);
/*      */           
/* 1934 */           tokenizer.consume(endToken);
/*      */         } else {
/* 1936 */           Message defaultInstance = (extension == null) ? null : extension.defaultInstance;
/*      */           
/* 1938 */           MessageReflection.MergeTarget subField = target.newMergeTargetForField(field, defaultInstance);
/*      */           
/* 1940 */           while (!tokenizer.tryConsume(endToken)) {
/* 1941 */             if (tokenizer.atEnd()) {
/* 1942 */               throw tokenizer.parseException("Expected \"" + endToken + "\".");
/*      */             }
/* 1944 */             mergeField(tokenizer, extensionRegistry, subField, parseTreeBuilder, unknownFields);
/*      */           } 
/*      */           
/* 1947 */           value = subField.finish();
/*      */         } 
/*      */       } else {
/*      */         Descriptors.EnumDescriptor enumType; String id;
/* 1951 */         switch (field.getType()) {
/*      */           case INT32:
/*      */           case SINT32:
/*      */           case SFIXED32:
/* 1955 */             value = Integer.valueOf(tokenizer.consumeInt32());
/*      */             break;
/*      */           
/*      */           case INT64:
/*      */           case SINT64:
/*      */           case SFIXED64:
/* 1961 */             value = Long.valueOf(tokenizer.consumeInt64());
/*      */             break;
/*      */           
/*      */           case UINT32:
/*      */           case FIXED32:
/* 1966 */             value = Integer.valueOf(tokenizer.consumeUInt32());
/*      */             break;
/*      */           
/*      */           case UINT64:
/*      */           case FIXED64:
/* 1971 */             value = Long.valueOf(tokenizer.consumeUInt64());
/*      */             break;
/*      */           
/*      */           case FLOAT:
/* 1975 */             value = Float.valueOf(tokenizer.consumeFloat());
/*      */             break;
/*      */           
/*      */           case DOUBLE:
/* 1979 */             value = Double.valueOf(tokenizer.consumeDouble());
/*      */             break;
/*      */           
/*      */           case BOOL:
/* 1983 */             value = Boolean.valueOf(tokenizer.consumeBoolean());
/*      */             break;
/*      */           
/*      */           case STRING:
/* 1987 */             value = tokenizer.consumeString();
/*      */             break;
/*      */           
/*      */           case BYTES:
/* 1991 */             value = tokenizer.consumeByteString();
/*      */             break;
/*      */           
/*      */           case ENUM:
/* 1995 */             enumType = field.getEnumType();
/*      */             
/* 1997 */             if (tokenizer.lookingAtInteger()) {
/* 1998 */               int number = tokenizer.consumeInt32();
/* 1999 */               value = enumType.findValueByNumber(number);
/* 2000 */               if (value == null) {
/*      */ 
/*      */                 
/* 2003 */                 String unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.';
/*      */ 
/*      */ 
/*      */                 
/* 2007 */                 if (this.allowUnknownEnumValues) {
/* 2008 */                   TextFormat.logger.warning(unknownValueMsg);
/*      */                   return;
/*      */                 } 
/* 2011 */                 throw tokenizer.parseExceptionPreviousToken("Enum type \"" + enumType
/*      */                     
/* 2013 */                     .getFullName() + "\" has no value with number " + number + '.');
/*      */               } 
/*      */ 
/*      */               
/*      */               break;
/*      */             } 
/*      */             
/* 2020 */             id = tokenizer.consumeIdentifier();
/* 2021 */             value = enumType.findValueByName(id);
/* 2022 */             if (value == null) {
/*      */ 
/*      */               
/* 2025 */               String unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value named \"" + id + "\".";
/*      */ 
/*      */ 
/*      */               
/* 2029 */               if (this.allowUnknownEnumValues) {
/* 2030 */                 TextFormat.logger.warning(unknownValueMsg);
/*      */                 return;
/*      */               } 
/* 2033 */               throw tokenizer.parseExceptionPreviousToken(unknownValueMsg);
/*      */             } 
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case MESSAGE:
/*      */           case GROUP:
/* 2042 */             throw new RuntimeException("Can't get here.");
/*      */         } 
/*      */       
/*      */       } 
/* 2046 */       if (field.isRepeated()) {
/*      */ 
/*      */         
/* 2049 */         target.addRepeatedField(field, value);
/*      */       } else {
/* 2051 */         target.setField(field, value);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Object consumeAnyFieldValue(TextFormat.Tokenizer tokenizer, ExtensionRegistry extensionRegistry, Descriptors.FieldDescriptor field, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields) throws TextFormat.ParseException {
/*      */       String anyEndToken;
/* 2063 */       StringBuilder typeUrlBuilder = new StringBuilder();
/*      */       
/*      */       while (true) {
/* 2066 */         typeUrlBuilder.append(tokenizer.consumeIdentifier());
/* 2067 */         if (tokenizer.tryConsume("]")) {
/*      */           break;
/*      */         }
/* 2070 */         if (tokenizer.tryConsume("/")) {
/* 2071 */           typeUrlBuilder.append("/"); continue;
/* 2072 */         }  if (tokenizer.tryConsume(".")) {
/* 2073 */           typeUrlBuilder.append("."); continue;
/*      */         } 
/* 2075 */         throw tokenizer.parseExceptionPreviousToken("Expected a valid type URL.");
/*      */       } 
/*      */       
/* 2078 */       tokenizer.tryConsume(":");
/*      */       
/* 2080 */       if (tokenizer.tryConsume("<")) {
/* 2081 */         anyEndToken = ">";
/*      */       } else {
/* 2083 */         tokenizer.consume("{");
/* 2084 */         anyEndToken = "}";
/*      */       } 
/* 2086 */       String typeUrl = typeUrlBuilder.toString();
/* 2087 */       Descriptors.Descriptor contentType = null;
/*      */       try {
/* 2089 */         contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
/* 2090 */       } catch (InvalidProtocolBufferException e) {
/* 2091 */         throw tokenizer.parseException("Invalid valid type URL. Found: " + typeUrl);
/*      */       } 
/* 2093 */       if (contentType == null) {
/* 2094 */         throw tokenizer.parseException("Unable to parse Any of type: " + typeUrl + ". Please make sure that the TypeRegistry contains the descriptors for the given types.");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2101 */       Message.Builder contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
/* 2102 */       MessageReflection.BuilderAdapter contentTarget = new MessageReflection.BuilderAdapter(contentBuilder);
/*      */       
/* 2104 */       while (!tokenizer.tryConsume(anyEndToken)) {
/* 2105 */         mergeField(tokenizer, extensionRegistry, contentTarget, parseTreeBuilder, unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2111 */       Descriptors.Descriptor anyDescriptor = field.getMessageType();
/*      */       
/* 2113 */       Message.Builder anyBuilder = DynamicMessage.getDefaultInstance(anyDescriptor).newBuilderForType();
/* 2114 */       anyBuilder.setField(anyDescriptor.findFieldByName("type_url"), typeUrlBuilder.toString());
/* 2115 */       anyBuilder.setField(anyDescriptor
/* 2116 */           .findFieldByName("value"), contentBuilder.build().toByteString());
/*      */       
/* 2118 */       return anyBuilder.build();
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
/*      */     private void skipField(TextFormat.Tokenizer tokenizer) throws TextFormat.ParseException {
/*      */       // Byte code:
/*      */       //   0: aload_1
/*      */       //   1: ldc '['
/*      */       //   3: invokevirtual tryConsume : (Ljava/lang/String;)Z
/*      */       //   6: ifeq -> 32
/*      */       //   9: aload_1
/*      */       //   10: invokevirtual consumeIdentifier : ()Ljava/lang/String;
/*      */       //   13: pop
/*      */       //   14: aload_1
/*      */       //   15: ldc '.'
/*      */       //   17: invokevirtual tryConsume : (Ljava/lang/String;)Z
/*      */       //   20: ifne -> 9
/*      */       //   23: aload_1
/*      */       //   24: ldc ']'
/*      */       //   26: invokevirtual consume : (Ljava/lang/String;)V
/*      */       //   29: goto -> 37
/*      */       //   32: aload_1
/*      */       //   33: invokevirtual consumeIdentifier : ()Ljava/lang/String;
/*      */       //   36: pop
/*      */       //   37: aload_1
/*      */       //   38: ldc ':'
/*      */       //   40: invokevirtual tryConsume : (Ljava/lang/String;)Z
/*      */       //   43: ifeq -> 72
/*      */       //   46: aload_1
/*      */       //   47: ldc '<'
/*      */       //   49: invokevirtual lookingAt : (Ljava/lang/String;)Z
/*      */       //   52: ifne -> 72
/*      */       //   55: aload_1
/*      */       //   56: ldc '{'
/*      */       //   58: invokevirtual lookingAt : (Ljava/lang/String;)Z
/*      */       //   61: ifne -> 72
/*      */       //   64: aload_0
/*      */       //   65: aload_1
/*      */       //   66: invokespecial skipFieldValue : (Lcom/google/protobuf/TextFormat$Tokenizer;)V
/*      */       //   69: goto -> 77
/*      */       //   72: aload_0
/*      */       //   73: aload_1
/*      */       //   74: invokespecial skipFieldMessage : (Lcom/google/protobuf/TextFormat$Tokenizer;)V
/*      */       //   77: aload_1
/*      */       //   78: ldc ';'
/*      */       //   80: invokevirtual tryConsume : (Ljava/lang/String;)Z
/*      */       //   83: ifne -> 93
/*      */       //   86: aload_1
/*      */       //   87: ldc ','
/*      */       //   89: invokevirtual tryConsume : (Ljava/lang/String;)Z
/*      */       //   92: pop
/*      */       //   93: return
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #2123	-> 0
/*      */       //   #2126	-> 9
/*      */       //   #2127	-> 14
/*      */       //   #2128	-> 23
/*      */       //   #2130	-> 32
/*      */       //   #2139	-> 37
/*      */       //   #2140	-> 64
/*      */       //   #2142	-> 72
/*      */       //   #2146	-> 77
/*      */       //   #2147	-> 86
/*      */       //   #2149	-> 93
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/*      */       //   0	94	0	this	Lcom/google/protobuf/TextFormat$Parser;
/*      */       //   0	94	1	tokenizer	Lcom/google/protobuf/TextFormat$Tokenizer;
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
/*      */     private void skipFieldMessage(TextFormat.Tokenizer tokenizer) throws TextFormat.ParseException {
/*      */       String delimiter;
/* 2156 */       if (tokenizer.tryConsume("<")) {
/* 2157 */         delimiter = ">";
/*      */       } else {
/* 2159 */         tokenizer.consume("{");
/* 2160 */         delimiter = "}";
/*      */       } 
/* 2162 */       while (!tokenizer.lookingAt(">") && !tokenizer.lookingAt("}")) {
/* 2163 */         skipField(tokenizer);
/*      */       }
/* 2165 */       tokenizer.consume(delimiter);
/*      */     }
/*      */     
/*      */     private void skipFieldValue(TextFormat.Tokenizer tokenizer) throws TextFormat.ParseException
/*      */     {
/* 2170 */       if (tokenizer.tryConsumeString()) {
/* 2171 */         while (tokenizer.tryConsumeString());
/*      */         return;
/*      */       } 
/* 2174 */       if (!tokenizer.tryConsumeIdentifier() && 
/* 2175 */         !tokenizer.tryConsumeInt64() && 
/* 2176 */         !tokenizer.tryConsumeUInt64() && 
/* 2177 */         !tokenizer.tryConsumeDouble() && 
/* 2178 */         !tokenizer.tryConsumeFloat()) {
/* 2179 */         throw tokenizer.parseException("Invalid field value: " + tokenizer.currentToken);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String escapeBytes(ByteString input) {
/* 2197 */     return TextFormatEscaper.escapeBytes(input);
/*      */   }
/*      */   public static class Builder {
/*      */     private boolean allowUnknownFields;
/*      */     private boolean allowUnknownEnumValues;
/* 2202 */     private boolean allowUnknownExtensions; private TextFormat.Parser.SingularOverwritePolicy singularOverwritePolicy; private TextFormatParseInfoTree.Builder parseInfoTreeBuilder; private TypeRegistry typeRegistry; public Builder() { this.allowUnknownFields = false; this.allowUnknownEnumValues = false; this.allowUnknownExtensions = false; this.singularOverwritePolicy = TextFormat.Parser.SingularOverwritePolicy.ALLOW_SINGULAR_OVERWRITES; this.parseInfoTreeBuilder = null; this.typeRegistry = TypeRegistry.getEmptyTypeRegistry(); } public Builder setTypeRegistry(TypeRegistry typeRegistry) { this.typeRegistry = typeRegistry; return this; } public Builder setAllowUnknownFields(boolean allowUnknownFields) { this.allowUnknownFields = allowUnknownFields; return this; } public Builder setAllowUnknownExtensions(boolean allowUnknownExtensions) { this.allowUnknownExtensions = allowUnknownExtensions; return this; } public Builder setSingularOverwritePolicy(TextFormat.Parser.SingularOverwritePolicy p) { this.singularOverwritePolicy = p; return this; } public Builder setParseInfoTreeBuilder(TextFormatParseInfoTree.Builder parseInfoTreeBuilder) { this.parseInfoTreeBuilder = parseInfoTreeBuilder; return this; } public TextFormat.Parser build() { return new TextFormat.Parser(this.typeRegistry, this.allowUnknownFields, this.allowUnknownEnumValues, this.allowUnknownExtensions, this.singularOverwritePolicy, this.parseInfoTreeBuilder); } } public static String escapeBytes(byte[] input) { return TextFormatEscaper.escapeBytes(input); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteString unescapeBytes(CharSequence charString) throws InvalidEscapeSequenceException {
/* 2212 */     ByteString input = ByteString.copyFromUtf8(charString.toString());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2220 */     byte[] result = new byte[input.size()];
/* 2221 */     int pos = 0;
/* 2222 */     for (int i = 0; i < input.size(); i++) {
/* 2223 */       byte c = input.byteAt(i);
/* 2224 */       if (c == 92) {
/* 2225 */         if (i + 1 < input.size()) {
/* 2226 */           i++;
/* 2227 */           c = input.byteAt(i);
/* 2228 */           if (isOctal(c)) {
/*      */             
/* 2230 */             int code = digitValue(c);
/* 2231 */             if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
/* 2232 */               i++;
/* 2233 */               code = code * 8 + digitValue(input.byteAt(i));
/*      */             } 
/* 2235 */             if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
/* 2236 */               i++;
/* 2237 */               code = code * 8 + digitValue(input.byteAt(i));
/*      */             } 
/*      */             
/* 2240 */             result[pos++] = (byte)code;
/*      */           } else {
/* 2242 */             int code; switch (c) {
/*      */               case 97:
/* 2244 */                 result[pos++] = 7;
/*      */                 break;
/*      */               case 98:
/* 2247 */                 result[pos++] = 8;
/*      */                 break;
/*      */               case 102:
/* 2250 */                 result[pos++] = 12;
/*      */                 break;
/*      */               case 110:
/* 2253 */                 result[pos++] = 10;
/*      */                 break;
/*      */               case 114:
/* 2256 */                 result[pos++] = 13;
/*      */                 break;
/*      */               case 116:
/* 2259 */                 result[pos++] = 9;
/*      */                 break;
/*      */               case 118:
/* 2262 */                 result[pos++] = 11;
/*      */                 break;
/*      */               case 92:
/* 2265 */                 result[pos++] = 92;
/*      */                 break;
/*      */               case 39:
/* 2268 */                 result[pos++] = 39;
/*      */                 break;
/*      */               case 34:
/* 2271 */                 result[pos++] = 34;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 120:
/* 2276 */                 code = 0;
/* 2277 */                 if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
/* 2278 */                   i++;
/* 2279 */                   code = digitValue(input.byteAt(i));
/*      */                 } else {
/* 2281 */                   throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\x' with no digits");
/*      */                 } 
/*      */                 
/* 2284 */                 if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
/* 2285 */                   i++;
/* 2286 */                   code = code * 16 + digitValue(input.byteAt(i));
/*      */                 } 
/* 2288 */                 result[pos++] = (byte)code;
/*      */                 break;
/*      */               
/*      */               default:
/* 2292 */                 throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\" + (char)c + '\'');
/*      */             } 
/*      */           
/*      */           } 
/*      */         } else {
/* 2297 */           throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\' at end of string.");
/*      */         } 
/*      */       } else {
/*      */         
/* 2301 */         result[pos++] = c;
/*      */       } 
/*      */     } 
/*      */     
/* 2305 */     return (result.length == pos) ? 
/* 2306 */       ByteString.wrap(result) : 
/* 2307 */       ByteString.copyFrom(result, 0, pos);
/*      */   }
/*      */ 
/*      */   
/*      */   public static class InvalidEscapeSequenceException
/*      */     extends IOException
/*      */   {
/*      */     private static final long serialVersionUID = -8164033650142593304L;
/*      */ 
/*      */     
/*      */     InvalidEscapeSequenceException(String description) {
/* 2318 */       super(description);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String escapeText(String input) {
/* 2328 */     return escapeBytes(ByteString.copyFromUtf8(input));
/*      */   }
/*      */ 
/*      */   
/*      */   public static String escapeDoubleQuotesAndBackslashes(String input) {
/* 2333 */     return TextFormatEscaper.escapeDoubleQuotesAndBackslashes(input);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String unescapeText(String input) throws InvalidEscapeSequenceException {
/* 2341 */     return unescapeBytes(input).toStringUtf8();
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isOctal(byte c) {
/* 2346 */     return (48 <= c && c <= 55);
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isHex(byte c) {
/* 2351 */     return ((48 <= c && c <= 57) || (97 <= c && c <= 102) || (65 <= c && c <= 70));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int digitValue(byte c) {
/* 2359 */     if (48 <= c && c <= 57)
/* 2360 */       return c - 48; 
/* 2361 */     if (97 <= c && c <= 122) {
/* 2362 */       return c - 97 + 10;
/*      */     }
/* 2364 */     return c - 65 + 10;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int parseInt32(String text) throws NumberFormatException {
/* 2374 */     return (int)parseInteger(text, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int parseUInt32(String text) throws NumberFormatException {
/* 2384 */     return (int)parseInteger(text, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static long parseInt64(String text) throws NumberFormatException {
/* 2393 */     return parseInteger(text, true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static long parseUInt64(String text) throws NumberFormatException {
/* 2403 */     return parseInteger(text, false, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private static long parseInteger(String text, boolean isSigned, boolean isLong) throws NumberFormatException {
/* 2408 */     int pos = 0;
/*      */     
/* 2410 */     boolean negative = false;
/* 2411 */     if (text.startsWith("-", pos)) {
/* 2412 */       if (!isSigned) {
/* 2413 */         throw new NumberFormatException("Number must be positive: " + text);
/*      */       }
/* 2415 */       pos++;
/* 2416 */       negative = true;
/*      */     } 
/*      */     
/* 2419 */     int radix = 10;
/* 2420 */     if (text.startsWith("0x", pos)) {
/* 2421 */       pos += 2;
/* 2422 */       radix = 16;
/* 2423 */     } else if (text.startsWith("0", pos)) {
/* 2424 */       radix = 8;
/*      */     } 
/*      */     
/* 2427 */     String numberText = text.substring(pos);
/*      */     
/* 2429 */     long result = 0L;
/* 2430 */     if (numberText.length() < 16) {
/*      */       
/* 2432 */       result = Long.parseLong(numberText, radix);
/* 2433 */       if (negative) {
/* 2434 */         result = -result;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2440 */       if (!isLong) {
/* 2441 */         if (isSigned) {
/* 2442 */           if (result > 2147483647L || result < -2147483648L) {
/* 2443 */             throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
/*      */           
/*      */           }
/*      */         }
/* 2447 */         else if (result >= 4294967296L || result < 0L) {
/* 2448 */           throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
/*      */         }
/*      */       
/*      */       }
/*      */     } else {
/*      */       
/* 2454 */       BigInteger bigValue = new BigInteger(numberText, radix);
/* 2455 */       if (negative) {
/* 2456 */         bigValue = bigValue.negate();
/*      */       }
/*      */ 
/*      */       
/* 2460 */       if (!isLong) {
/* 2461 */         if (isSigned) {
/* 2462 */           if (bigValue.bitLength() > 31) {
/* 2463 */             throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
/*      */           
/*      */           }
/*      */         }
/* 2467 */         else if (bigValue.bitLength() > 32) {
/* 2468 */           throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
/*      */         
/*      */         }
/*      */       
/*      */       }
/* 2473 */       else if (isSigned) {
/* 2474 */         if (bigValue.bitLength() > 63) {
/* 2475 */           throw new NumberFormatException("Number out of range for 64-bit signed integer: " + text);
/*      */         
/*      */         }
/*      */       }
/* 2479 */       else if (bigValue.bitLength() > 64) {
/* 2480 */         throw new NumberFormatException("Number out of range for 64-bit unsigned integer: " + text);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2486 */       result = bigValue.longValue();
/*      */     } 
/*      */     
/* 2489 */     return result;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\TextFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */