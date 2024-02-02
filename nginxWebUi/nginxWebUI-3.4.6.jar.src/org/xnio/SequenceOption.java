/*    */ package org.xnio;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import org.xnio._private.Messages;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class SequenceOption<T>
/*    */   extends Option<Sequence<T>>
/*    */ {
/*    */   private static final long serialVersionUID = -4328676629293125136L;
/*    */   private final transient Class<T> elementType;
/*    */   private final transient Option.ValueParser<T> parser;
/*    */   
/*    */   SequenceOption(Class<?> declClass, String name, Class<T> elementType) {
/* 39 */     super(declClass, name);
/* 40 */     if (elementType == null) {
/* 41 */       throw Messages.msg.nullParameter("elementType");
/*    */     }
/* 43 */     this.elementType = elementType;
/* 44 */     this.parser = Option.getParser(elementType);
/*    */   }
/*    */   
/*    */   public Sequence<T> cast(Object o) {
/* 48 */     if (o == null)
/* 49 */       return null; 
/* 50 */     if (o instanceof Sequence)
/* 51 */       return ((Sequence)o).cast(this.elementType); 
/* 52 */     if (o instanceof Object[])
/* 53 */       return Sequence.<Object>of((Object[])o).cast(this.elementType); 
/* 54 */     if (o instanceof Collection) {
/* 55 */       return Sequence.of((Collection)o).cast(this.elementType);
/*    */     }
/* 57 */     throw new ClassCastException("Not a sequence");
/*    */   }
/*    */ 
/*    */   
/*    */   public Sequence<T> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 62 */     List<T> list = new ArrayList<>();
/* 63 */     if (string.isEmpty()) {
/* 64 */       return Sequence.empty();
/*    */     }
/* 66 */     for (String value : string.split(",")) {
/* 67 */       list.add(this.parser.parseValue(value, classLoader));
/*    */     }
/* 69 */     return Sequence.of(list);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\SequenceOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */