package org.wildfly.common.expression;

import java.io.File;
import java.util.HashSet;
import org.wildfly.common.function.ExceptionBiConsumer;

class LiteralNode extends Node {
   static final LiteralNode DOLLAR = new LiteralNode("$");
   static final LiteralNode CLOSE_BRACE = new LiteralNode("}");
   static final LiteralNode FILE_SEP;
   static final LiteralNode COLON;
   static final LiteralNode NEWLINE;
   static final LiteralNode CARRIAGE_RETURN;
   static final LiteralNode TAB;
   static final LiteralNode BACKSPACE;
   static final LiteralNode FORM_FEED;
   static final LiteralNode BACKSLASH;
   private final String literalValue;
   private final int start;
   private final int end;
   private String toString;

   LiteralNode(String literalValue, int start, int end) {
      this.literalValue = literalValue;
      this.start = start;
      this.end = end;
   }

   LiteralNode(String literalValue) {
      this(literalValue, 0, literalValue.length());
   }

   <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
      context.getStringBuilder().append(this.literalValue, this.start, this.end);
   }

   void catalog(HashSet<String> strings) {
   }

   public String toString() {
      String toString = this.toString;
      return toString != null ? toString : (this.toString = this.literalValue.substring(this.start, this.end));
   }

   static {
      FILE_SEP = new LiteralNode(File.separator);
      COLON = new LiteralNode(":");
      NEWLINE = new LiteralNode("\n");
      CARRIAGE_RETURN = new LiteralNode("\r");
      TAB = new LiteralNode("\t");
      BACKSPACE = new LiteralNode("\b");
      FORM_FEED = new LiteralNode("\f");
      BACKSLASH = new LiteralNode("\\");
   }
}
