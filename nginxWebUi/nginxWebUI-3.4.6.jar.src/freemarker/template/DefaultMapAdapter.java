/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.core._DelayedJQuote;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.utility.ObjectWrapperWithAPISupport;
/*     */ import java.io.Serializable;
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
/*     */ public class DefaultMapAdapter
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateHashModelEx2, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable
/*     */ {
/*     */   private final Map map;
/*     */   
/*     */   public static DefaultMapAdapter adapt(Map map, ObjectWrapperWithAPISupport wrapper) {
/*  62 */     return new DefaultMapAdapter(map, (ObjectWrapper)wrapper);
/*     */   }
/*     */   
/*     */   private DefaultMapAdapter(Map map, ObjectWrapper wrapper) {
/*  66 */     super(wrapper);
/*  67 */     this.map = map;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*     */     Object val;
/*     */     try {
/*  74 */       val = this.map.get(key);
/*  75 */     } catch (ClassCastException e) {
/*  76 */       throw new _TemplateModelException(e, new Object[] { "ClassCastException while getting Map entry with String key ", new _DelayedJQuote(key) });
/*     */     
/*     */     }
/*  79 */     catch (NullPointerException e) {
/*  80 */       throw new _TemplateModelException(e, new Object[] { "NullPointerException while getting Map entry with String key ", new _DelayedJQuote(key) });
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  85 */     if (val == null)
/*     */     {
/*     */       
/*  88 */       if (key.length() == 1 && !(this.map instanceof java.util.SortedMap)) {
/*  89 */         Character charKey = Character.valueOf(key.charAt(0));
/*     */         try {
/*  91 */           val = this.map.get(charKey);
/*  92 */           if (val == null) {
/*  93 */             TemplateModel wrappedNull = wrap(null);
/*  94 */             if (wrappedNull == null || (!this.map.containsKey(key) && !this.map.containsKey(charKey))) {
/*  95 */               return null;
/*     */             }
/*  97 */             return wrappedNull;
/*     */           }
/*     */         
/* 100 */         } catch (ClassCastException e) {
/* 101 */           throw new _TemplateModelException(e, new Object[] { "Class casting exception while getting Map entry with Character key ", new _DelayedJQuote(charKey) });
/*     */         
/*     */         }
/* 104 */         catch (NullPointerException e) {
/* 105 */           throw new _TemplateModelException(e, new Object[] { "NullPointerException while getting Map entry with Character key ", new _DelayedJQuote(charKey) });
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 110 */         TemplateModel wrappedNull = wrap(null);
/* 111 */         if (wrappedNull == null || !this.map.containsKey(key)) {
/* 112 */           return null;
/*     */         }
/* 114 */         return wrappedNull;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 119 */     return wrap(val);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 124 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 129 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() {
/* 134 */     return new SimpleCollection(this.map.keySet(), getObjectWrapper());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() {
/* 139 */     return new SimpleCollection(this.map.values(), getObjectWrapper());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
/* 144 */     return new MapKeyValuePairIterator(this.map, getObjectWrapper());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/* 149 */     return this.map;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/* 154 */     return this.map;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel getAPI() throws TemplateModelException {
/* 159 */     return ((ObjectWrapperWithAPISupport)getObjectWrapper()).wrapAsAPI(this.map);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultMapAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */