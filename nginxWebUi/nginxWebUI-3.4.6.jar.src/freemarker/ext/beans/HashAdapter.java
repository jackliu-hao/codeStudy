/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelAdapter;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class HashAdapter
/*     */   extends AbstractMap
/*     */   implements TemplateModelAdapter
/*     */ {
/*     */   private final BeansWrapper wrapper;
/*     */   private final TemplateHashModel model;
/*     */   private Set entrySet;
/*     */   
/*     */   HashAdapter(TemplateHashModel model, BeansWrapper wrapper) {
/*  44 */     this.model = model;
/*  45 */     this.wrapper = wrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel getTemplateModel() {
/*  50 */     return (TemplateModel)this.model;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*     */     try {
/*  56 */       return this.model.isEmpty();
/*  57 */     } catch (TemplateModelException e) {
/*  58 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*     */     try {
/*  65 */       return getModelEx().size();
/*  66 */     } catch (TemplateModelException e) {
/*  67 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(Object key) {
/*     */     try {
/*  74 */       return this.wrapper.unwrap(this.model.get(String.valueOf(key)));
/*  75 */     } catch (TemplateModelException e) {
/*  76 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  83 */     if (get(key) != null) {
/*  84 */       return true;
/*     */     }
/*  86 */     return super.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public Set entrySet() {
/*  91 */     if (this.entrySet != null) {
/*  92 */       return this.entrySet;
/*     */     }
/*  94 */     return this.entrySet = new AbstractSet()
/*     */       {
/*     */         public Iterator iterator() {
/*     */           final TemplateModelIterator i;
/*     */           try {
/*  99 */             i = HashAdapter.this.getModelEx().keys().iterator();
/* 100 */           } catch (TemplateModelException e) {
/* 101 */             throw new UndeclaredThrowableException(e);
/*     */           } 
/* 103 */           return new Iterator()
/*     */             {
/*     */               public boolean hasNext() {
/*     */                 try {
/* 107 */                   return i.hasNext();
/* 108 */                 } catch (TemplateModelException e) {
/* 109 */                   throw new UndeclaredThrowableException(e);
/*     */                 } 
/*     */               }
/*     */ 
/*     */               
/*     */               public Object next() {
/*     */                 final Object key;
/*     */                 try {
/* 117 */                   key = HashAdapter.this.wrapper.unwrap(i.next());
/* 118 */                 } catch (TemplateModelException e) {
/* 119 */                   throw new UndeclaredThrowableException(e);
/*     */                 } 
/* 121 */                 return new Map.Entry<Object, Object>()
/*     */                   {
/*     */                     public Object getKey() {
/* 124 */                       return key;
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public Object getValue() {
/* 129 */                       return HashAdapter.this.get(key);
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public Object setValue(Object value) {
/* 134 */                       throw new UnsupportedOperationException();
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public boolean equals(Object o) {
/* 139 */                       if (!(o instanceof Map.Entry))
/* 140 */                         return false; 
/* 141 */                       Map.Entry e = (Map.Entry)o;
/* 142 */                       Object k1 = getKey();
/* 143 */                       Object k2 = e.getKey();
/* 144 */                       if (k1 == k2 || (k1 != null && k1.equals(k2))) {
/* 145 */                         Object v1 = getValue();
/* 146 */                         Object v2 = e.getValue();
/* 147 */                         if (v1 == v2 || (v1 != null && v1.equals(v2)))
/* 148 */                           return true; 
/*     */                       } 
/* 150 */                       return false;
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public int hashCode() {
/* 155 */                       Object value = getValue();
/* 156 */                       return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value
/* 157 */                         .hashCode());
/*     */                     }
/*     */                   };
/*     */               }
/*     */ 
/*     */               
/*     */               public void remove() {
/* 164 */                 throw new UnsupportedOperationException();
/*     */               }
/*     */             };
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/*     */           try {
/* 172 */             return HashAdapter.this.getModelEx().size();
/* 173 */           } catch (TemplateModelException e) {
/* 174 */             throw new UndeclaredThrowableException(e);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private TemplateHashModelEx getModelEx() {
/* 181 */     if (this.model instanceof TemplateHashModelEx) {
/* 182 */       return (TemplateHashModelEx)this.model;
/*     */     }
/* 184 */     throw new UnsupportedOperationException("Operation supported only on TemplateHashModelEx. " + this.model
/*     */         
/* 186 */         .getClass().getName() + " does not implement it though.");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\HashAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */