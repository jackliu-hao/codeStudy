/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.EmptyMap;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class TransformBlock
/*     */   extends TemplateElement
/*     */ {
/*     */   private Expression transformExpression;
/*     */   Map namedArgs;
/*     */   private volatile transient SoftReference sortedNamedArgsCache;
/*     */   
/*     */   TransformBlock(Expression transformExpression, Map namedArgs, TemplateElements children) {
/*  52 */     this.transformExpression = transformExpression;
/*  53 */     this.namedArgs = namedArgs;
/*  54 */     setChildren(children);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  60 */     TemplateTransformModel ttm = env.getTransform(this.transformExpression);
/*  61 */     if (ttm != null) {
/*     */       EmptyMap emptyMap;
/*  63 */       if (this.namedArgs != null && !this.namedArgs.isEmpty()) {
/*  64 */         Map<Object, Object> args = new HashMap<>();
/*  65 */         for (Iterator<Map.Entry> it = this.namedArgs.entrySet().iterator(); it.hasNext(); ) {
/*  66 */           Map.Entry entry = it.next();
/*  67 */           String key = (String)entry.getKey();
/*  68 */           Expression valueExp = (Expression)entry.getValue();
/*  69 */           TemplateModel value = valueExp.eval(env);
/*  70 */           args.put(key, value);
/*     */         } 
/*     */       } else {
/*  73 */         emptyMap = EmptyMap.instance;
/*     */       } 
/*  75 */       env.visitAndTransform(getChildBuffer(), ttm, (Map)emptyMap);
/*     */     } else {
/*  77 */       TemplateModel tm = this.transformExpression.eval(env);
/*  78 */       throw new UnexpectedTypeException(this.transformExpression, tm, "transform", new Class[] { TemplateTransformModel.class }, env);
/*     */     } 
/*     */ 
/*     */     
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  87 */     StringBuilder sb = new StringBuilder();
/*  88 */     if (canonical) sb.append('<'); 
/*  89 */     sb.append(getNodeTypeSymbol());
/*  90 */     sb.append(' ');
/*  91 */     sb.append(this.transformExpression);
/*  92 */     if (this.namedArgs != null) {
/*  93 */       for (Iterator<Map.Entry> it = getSortedNamedArgs().iterator(); it.hasNext(); ) {
/*  94 */         Map.Entry entry = it.next();
/*  95 */         sb.append(' ');
/*  96 */         sb.append(entry.getKey());
/*  97 */         sb.append('=');
/*  98 */         _MessageUtil.appendExpressionAsUntearable(sb, (Expression)entry.getValue());
/*     */       } 
/*     */     }
/* 101 */     if (canonical) {
/* 102 */       sb.append(">");
/* 103 */       sb.append(getChildrenCanonicalForm());
/* 104 */       sb.append("</").append(getNodeTypeSymbol()).append('>');
/*     */     } 
/* 106 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 111 */     return "#transform";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 116 */     return 1 + ((this.namedArgs != null) ? (this.namedArgs.size() * 2) : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 121 */     if (idx == 0)
/* 122 */       return this.transformExpression; 
/* 123 */     if (this.namedArgs != null && idx - 1 < this.namedArgs.size() * 2) {
/* 124 */       Map.Entry namedArg = getSortedNamedArgs().get((idx - 1) / 2);
/* 125 */       return ((idx - 1) % 2 == 0) ? namedArg.getKey() : namedArg.getValue();
/*     */     } 
/* 127 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 133 */     if (idx == 0)
/* 134 */       return ParameterRole.CALLEE; 
/* 135 */     if (idx - 1 < this.namedArgs.size() * 2) {
/* 136 */       return ((idx - 1) % 2 == 0) ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
/*     */     }
/* 138 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List getSortedNamedArgs() {
/* 147 */     Reference<List> ref = this.sortedNamedArgsCache;
/* 148 */     if (ref != null) {
/* 149 */       List list = ref.get();
/* 150 */       if (list != null) return list;
/*     */     
/*     */     } 
/* 153 */     List res = MiscUtil.sortMapOfExpressions(this.namedArgs);
/* 154 */     this.sortedNamedArgsCache = new SoftReference<>(res);
/* 155 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 160 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isShownInStackTrace() {
/* 165 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TransformBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */