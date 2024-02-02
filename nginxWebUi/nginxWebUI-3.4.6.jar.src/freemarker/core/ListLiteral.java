/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
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
/*     */ final class ListLiteral
/*     */   extends Expression
/*     */ {
/*     */   final ArrayList<Expression> items;
/*     */   
/*     */   ListLiteral(ArrayList<Expression> items) {
/*  41 */     this.items = items;
/*  42 */     items.trimToSize();
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  47 */     SimpleSequence list = new SimpleSequence(this.items.size(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*  48 */     for (Expression exp : this.items) {
/*  49 */       TemplateModel tm = exp.eval(env);
/*  50 */       if (env == null || !env.isClassicCompatible()) {
/*  51 */         exp.assertNonNull(tm, env);
/*     */       }
/*  53 */       list.add(tm);
/*     */     } 
/*  55 */     return (TemplateModel)list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List getValueList(Environment env) throws TemplateException {
/*  63 */     int size = this.items.size();
/*  64 */     switch (size) {
/*     */       case 0:
/*  66 */         return Collections.EMPTY_LIST;
/*     */       
/*     */       case 1:
/*  69 */         return Collections.singletonList(((Expression)this.items.get(0)).evalAndCoerceToPlainText(env));
/*     */     } 
/*     */     
/*  72 */     List<String> result = new ArrayList(this.items.size());
/*  73 */     for (ListIterator<Expression> iterator = this.items.listIterator(); iterator.hasNext(); ) {
/*  74 */       Expression exp = iterator.next();
/*  75 */       result.add(exp.evalAndCoerceToPlainText(env));
/*     */     } 
/*  77 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List getModelList(Environment env) throws TemplateException {
/*  86 */     int size = this.items.size();
/*  87 */     switch (size) {
/*     */       case 0:
/*  89 */         return Collections.EMPTY_LIST;
/*     */       
/*     */       case 1:
/*  92 */         return Collections.singletonList(((Expression)this.items.get(0)).eval(env));
/*     */     } 
/*     */     
/*  95 */     List<TemplateModel> result = new ArrayList(this.items.size());
/*  96 */     for (ListIterator<Expression> iterator = this.items.listIterator(); iterator.hasNext(); ) {
/*  97 */       Expression exp = iterator.next();
/*  98 */       result.add(exp.eval(env));
/*     */     } 
/* 100 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/* 107 */     StringBuilder buf = new StringBuilder("[");
/* 108 */     int size = this.items.size();
/* 109 */     for (int i = 0; i < size; i++) {
/* 110 */       Expression value = this.items.get(i);
/* 111 */       buf.append(value.getCanonicalForm());
/* 112 */       if (i != size - 1) {
/* 113 */         buf.append(", ");
/*     */       }
/*     */     } 
/* 116 */     buf.append("]");
/* 117 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 122 */     return "[...]";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/* 127 */     if (this.constantValue != null) {
/* 128 */       return true;
/*     */     }
/* 130 */     for (int i = 0; i < this.items.size(); i++) {
/* 131 */       Expression exp = this.items.get(i);
/* 132 */       if (!exp.isLiteral()) {
/* 133 */         return false;
/*     */       }
/*     */     } 
/* 136 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateSequenceModel evaluateStringsToNamespaces(Environment env) throws TemplateException {
/* 142 */     TemplateSequenceModel val = (TemplateSequenceModel)eval(env);
/* 143 */     SimpleSequence result = new SimpleSequence(val.size(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 144 */     for (int i = 0; i < this.items.size(); i++) {
/* 145 */       Object itemExpr = this.items.get(i);
/* 146 */       if (itemExpr instanceof StringLiteral) {
/* 147 */         String s = ((StringLiteral)itemExpr).getAsString();
/*     */         try {
/* 149 */           Environment.Namespace ns = env.importLib(s, (String)null);
/* 150 */           result.add(ns);
/* 151 */         } catch (IOException ioe) {
/* 152 */           throw new _MiscTemplateException((StringLiteral)itemExpr, new Object[] { "Couldn't import library ", new _DelayedJQuote(s), ": ", new _DelayedGetMessage(ioe) });
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 157 */         result.add(val.get(i));
/*     */       } 
/*     */     } 
/* 160 */     return (TemplateSequenceModel)result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 166 */     ArrayList<Expression> clonedValues = (ArrayList)this.items.clone();
/* 167 */     for (ListIterator<Expression> iter = clonedValues.listIterator(); iter.hasNext();) {
/* 168 */       iter.set(((Expression)iter.next()).deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */     }
/*     */     
/* 171 */     return new ListLiteral(clonedValues);
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 176 */     return (this.items != null) ? this.items.size() : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 181 */     checkIndex(idx);
/* 182 */     return this.items.get(idx);
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 187 */     checkIndex(idx);
/* 188 */     return ParameterRole.ITEM_VALUE;
/*     */   }
/*     */   
/*     */   private void checkIndex(int idx) {
/* 192 */     if (this.items == null || idx >= this.items.size())
/* 193 */       throw new IndexOutOfBoundsException(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ListLiteral.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */