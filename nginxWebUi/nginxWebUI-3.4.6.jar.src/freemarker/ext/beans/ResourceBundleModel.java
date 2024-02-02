/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core._DelayedJQuote;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
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
/*     */ public class ResourceBundleModel
/*     */   extends BeanModel
/*     */   implements TemplateMethodModelEx
/*     */ {
/*  60 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  65 */         return (TemplateModel)new ResourceBundleModel((ResourceBundle)object, (BeansWrapper)wrapper);
/*     */       }
/*     */     };
/*     */   
/*  69 */   private Hashtable formats = null;
/*     */   
/*     */   public ResourceBundleModel(ResourceBundle bundle, BeansWrapper wrapper) {
/*  72 */     super(bundle, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key) throws TemplateModelException {
/*     */     try {
/*  82 */       return wrap(((ResourceBundle)this.object).getObject(key));
/*  83 */     } catch (MissingResourceException e) {
/*  84 */       throw new _TemplateModelException(e, new Object[] { "No ", new _DelayedJQuote(key), " key in the ResourceBundle. Note that conforming to the ResourceBundle Java API, this is an error and not just a missing sub-variable (a null)." });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  96 */     return (!((ResourceBundle)this.object).getKeys().hasMoreElements() && super
/*  97 */       .isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 102 */     return keySet().size();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Set keySet() {
/* 107 */     Set set = super.keySet();
/* 108 */     Enumeration<String> e = ((ResourceBundle)this.object).getKeys();
/* 109 */     while (e.hasMoreElements()) {
/* 110 */       set.add(e.nextElement());
/*     */     }
/* 112 */     return set;
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
/*     */   public Object exec(List arguments) throws TemplateModelException {
/* 124 */     if (arguments.size() < 1) {
/* 125 */       throw new TemplateModelException("No message key was specified");
/*     */     }
/* 127 */     Iterator<TemplateModel> it = arguments.iterator();
/* 128 */     String key = unwrap(it.next()).toString();
/*     */     try {
/* 130 */       if (!it.hasNext()) {
/* 131 */         return wrap(((ResourceBundle)this.object).getObject(key));
/*     */       }
/*     */ 
/*     */       
/* 135 */       int args = arguments.size() - 1;
/* 136 */       Object[] params = new Object[args];
/* 137 */       for (int i = 0; i < args; i++) {
/* 138 */         params[i] = unwrap(it.next());
/*     */       }
/*     */       
/* 141 */       return new StringModel(format(key, params), this.wrapper);
/* 142 */     } catch (MissingResourceException e) {
/* 143 */       throw new TemplateModelException("No such key: " + key);
/* 144 */     } catch (Exception e) {
/* 145 */       throw new TemplateModelException(e.getMessage());
/*     */     } 
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
/*     */   public String format(String key, Object[] params) throws MissingResourceException {
/* 161 */     if (this.formats == null) {
/* 162 */       this.formats = new Hashtable<>();
/*     */     }
/*     */     
/* 165 */     MessageFormat format = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 173 */     format = (MessageFormat)this.formats.get(key);
/* 174 */     if (format == null) {
/* 175 */       format = new MessageFormat(((ResourceBundle)this.object).getString(key));
/* 176 */       format.setLocale(getBundle().getLocale());
/* 177 */       this.formats.put(key, format);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     synchronized (format) {
/* 184 */       return format.format(params);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ResourceBundle getBundle() {
/* 189 */     return (ResourceBundle)this.object;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ResourceBundleModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */