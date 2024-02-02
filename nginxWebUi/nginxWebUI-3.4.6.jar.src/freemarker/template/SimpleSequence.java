/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.ext.beans.BeansWrapper;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
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
/*     */ public class SimpleSequence
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateSequenceModel, Serializable
/*     */ {
/*     */   protected final List list;
/*     */   private List unwrappedList;
/*     */   
/*     */   @Deprecated
/*     */   public SimpleSequence() {
/*  86 */     this((ObjectWrapper)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public SimpleSequence(int capacity) {
/*  98 */     this.list = new ArrayList(capacity);
/*     */   }
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
/*     */   @Deprecated
/*     */   public SimpleSequence(Collection collection) {
/* 113 */     this(collection, (ObjectWrapper)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleSequence(TemplateCollectionModel tcm) throws TemplateModelException {
/* 123 */     ArrayList<TemplateModel> alist = new ArrayList();
/* 124 */     for (TemplateModelIterator it = tcm.iterator(); it.hasNext();) {
/* 125 */       alist.add(it.next());
/*     */     }
/* 127 */     alist.trimToSize();
/* 128 */     this.list = alist;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleSequence(ObjectWrapper wrapper) {
/* 140 */     super(wrapper);
/* 141 */     this.list = new ArrayList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleSequence(int capacity, ObjectWrapper wrapper) {
/* 153 */     super(wrapper);
/* 154 */     this.list = new ArrayList(capacity);
/*     */   }
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
/*     */   public SimpleSequence(Collection<?> collection, ObjectWrapper wrapper) {
/* 170 */     super(wrapper);
/* 171 */     this.list = new ArrayList(collection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Object obj) {
/* 183 */     this.list.add(obj);
/* 184 */     this.unwrappedList = null;
/*     */   }
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
/*     */   @Deprecated
/*     */   public void add(boolean b) {
/* 198 */     add(b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public List toList() throws TemplateModelException {
/* 210 */     if (this.unwrappedList == null) {
/* 211 */       Class<?> listClass = this.list.getClass();
/* 212 */       List<Object> result = null;
/*     */       try {
/* 214 */         result = (List)listClass.newInstance();
/* 215 */       } catch (Exception e) {
/* 216 */         throw new TemplateModelException("Error instantiating an object of type " + listClass.getName(), e);
/*     */       } 
/*     */       
/* 219 */       BeansWrapper bw = BeansWrapper.getDefaultInstance();
/* 220 */       for (int i = 0; i < this.list.size(); i++) {
/* 221 */         Object elem = this.list.get(i);
/* 222 */         if (elem instanceof TemplateModel) {
/* 223 */           elem = bw.unwrap((TemplateModel)elem);
/*     */         }
/* 225 */         result.add(elem);
/*     */       } 
/* 227 */       this.unwrappedList = result;
/*     */     } 
/* 229 */     return this.unwrappedList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(int index) throws TemplateModelException {
/*     */     try {
/* 239 */       Object value = this.list.get(index);
/* 240 */       if (value instanceof TemplateModel) {
/* 241 */         return (TemplateModel)value;
/*     */       }
/* 243 */       TemplateModel tm = wrap(value);
/* 244 */       this.list.set(index, tm);
/* 245 */       return tm;
/* 246 */     } catch (IndexOutOfBoundsException e) {
/* 247 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 253 */     return this.list.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleSequence synchronizedWrapper() {
/* 260 */     return new SynchronizedSequence();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 265 */     return this.list.toString();
/*     */   }
/*     */   
/*     */   private class SynchronizedSequence extends SimpleSequence {
/*     */     private SynchronizedSequence() {
/* 270 */       super(SimpleSequence.this.getObjectWrapper());
/*     */     }
/*     */ 
/*     */     
/*     */     public void add(Object obj) {
/* 275 */       synchronized (SimpleSequence.this) {
/* 276 */         SimpleSequence.this.add(obj);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int i) throws TemplateModelException {
/* 282 */       synchronized (SimpleSequence.this) {
/* 283 */         return SimpleSequence.this.get(i);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 289 */       synchronized (SimpleSequence.this) {
/* 290 */         return SimpleSequence.this.size();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public List toList() throws TemplateModelException {
/* 296 */       synchronized (SimpleSequence.this) {
/* 297 */         return SimpleSequence.this.toList();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public SimpleSequence synchronizedWrapper() {
/* 303 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\SimpleSequence.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */