/*    */ package org.wildfly.common.expression;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.HashSet;
/*    */ import org.wildfly.common.function.ExceptionBiConsumer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class LiteralNode
/*    */   extends Node
/*    */ {
/* 30 */   static final LiteralNode DOLLAR = new LiteralNode("$");
/* 31 */   static final LiteralNode CLOSE_BRACE = new LiteralNode("}");
/* 32 */   static final LiteralNode FILE_SEP = new LiteralNode(File.separator);
/* 33 */   static final LiteralNode COLON = new LiteralNode(":");
/* 34 */   static final LiteralNode NEWLINE = new LiteralNode("\n");
/* 35 */   static final LiteralNode CARRIAGE_RETURN = new LiteralNode("\r");
/* 36 */   static final LiteralNode TAB = new LiteralNode("\t");
/* 37 */   static final LiteralNode BACKSPACE = new LiteralNode("\b");
/* 38 */   static final LiteralNode FORM_FEED = new LiteralNode("\f");
/* 39 */   static final LiteralNode BACKSLASH = new LiteralNode("\\");
/*    */   
/*    */   private final String literalValue;
/*    */   private final int start;
/*    */   private final int end;
/*    */   private String toString;
/*    */   
/*    */   LiteralNode(String literalValue, int start, int end) {
/* 47 */     this.literalValue = literalValue;
/* 48 */     this.start = start;
/* 49 */     this.end = end;
/*    */   }
/*    */   
/*    */   LiteralNode(String literalValue) {
/* 53 */     this(literalValue, 0, literalValue.length());
/*    */   }
/*    */   
/*    */   <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
/* 57 */     context.getStringBuilder().append(this.literalValue, this.start, this.end);
/*    */   }
/*    */ 
/*    */   
/*    */   void catalog(HashSet<String> strings) {}
/*    */   
/*    */   public String toString() {
/* 64 */     String toString = this.toString;
/* 65 */     return (toString != null) ? toString : (this.toString = this.literalValue.substring(this.start, this.end));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\expression\LiteralNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */