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
/*    */ final class TypeSequenceOption<T>
/*    */   extends Option<Sequence<Class<? extends T>>>
/*    */ {
/*    */   private static final long serialVersionUID = -4328676629293125136L;
/*    */   private final transient Class<T> elementDeclType;
/*    */   private final transient Option.ValueParser<Class<? extends T>> parser;
/*    */   
/*    */   TypeSequenceOption(Class<?> declClass, String name, Class<T> elementDeclType) {
/* 39 */     super(declClass, name);
/* 40 */     if (elementDeclType == null) {
/* 41 */       throw Messages.msg.nullParameter("elementDeclType");
/*    */     }
/* 43 */     this.elementDeclType = elementDeclType;
/* 44 */     this.parser = Option.getClassParser(elementDeclType);
/*    */   }
/*    */   
/*    */   public Sequence<Class<? extends T>> cast(Object o) {
/* 48 */     if (o == null)
/* 49 */       return null; 
/* 50 */     if (o instanceof Sequence)
/* 51 */       return castSeq((Sequence)o, this.elementDeclType); 
/* 52 */     if (o instanceof Object[])
/* 53 */       return castSeq(Sequence.of((Object[])o), this.elementDeclType); 
/* 54 */     if (o instanceof Collection) {
/* 55 */       return castSeq(Sequence.of((Collection)o), this.elementDeclType);
/*    */     }
/* 57 */     throw new ClassCastException("Not a sequence");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static <T> Sequence<Class<? extends T>> castSeq(Sequence<?> seq, Class<T> type) {
/* 63 */     for (Object o : seq) {
/* 64 */       ((Class)o).asSubclass(type);
/*    */     }
/* 66 */     return (Sequence)seq;
/*    */   }
/*    */   
/*    */   public Sequence<Class<? extends T>> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 70 */     List<Class<? extends T>> list = new ArrayList<>();
/* 71 */     if (string.isEmpty()) {
/* 72 */       return Sequence.empty();
/*    */     }
/* 74 */     for (String value : string.split(",")) {
/* 75 */       list.add(this.parser.parseValue(value, classLoader));
/*    */     }
/* 77 */     return Sequence.of(list);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\TypeSequenceOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */