/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.ext.beans.BeansWrapper;
/*     */ import freemarker.ext.beans.BeansWrapperConfiguration;
/*     */ import freemarker.ext.beans.MemberAccessPolicy;
/*     */ import freemarker.ext.dom.NodeModel;
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.utility.ObjectWrapperWithAPISupport;
/*     */ import freemarker.template.utility.RichObjectWrapper;
/*     */ import java.lang.reflect.Array;
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.w3c.dom.Node;
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
/*     */ public class DefaultObjectWrapper
/*     */   extends BeansWrapper
/*     */ {
/*     */   @Deprecated
/*  67 */   static final DefaultObjectWrapper instance = new DefaultObjectWrapper();
/*     */   
/*     */   private static final Class<?> JYTHON_OBJ_CLASS;
/*     */   
/*     */   private static final ObjectWrapper JYTHON_WRAPPER;
/*     */   
/*     */   private boolean useAdaptersForContainers;
/*     */   
/*     */   private boolean forceLegacyNonListCollections;
/*     */   
/*     */   private boolean iterableSupport;
/*     */   private boolean domNodeSupport;
/*     */   private boolean jythonSupport;
/*     */   private final boolean useAdapterForEnumerations;
/*     */   
/*     */   static {
/*     */     Class<?> cl;
/*     */     ObjectWrapper ow;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public DefaultObjectWrapper() {
/*  89 */     this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
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
/*     */   public DefaultObjectWrapper(Version incompatibleImprovements) {
/* 120 */     this(new DefaultObjectWrapperConfiguration(incompatibleImprovements) {  }, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected DefaultObjectWrapper(BeansWrapperConfiguration bwCfg, boolean writeProtected) {
/* 130 */     super(bwCfg, writeProtected, false);
/*     */ 
/*     */     
/* 133 */     DefaultObjectWrapperConfiguration dowDowCfg = (bwCfg instanceof DefaultObjectWrapperConfiguration) ? (DefaultObjectWrapperConfiguration)bwCfg : new DefaultObjectWrapperConfiguration(bwCfg.getIncompatibleImprovements()) {  };
/* 134 */     this.useAdaptersForContainers = dowDowCfg.getUseAdaptersForContainers();
/* 135 */     this
/* 136 */       .useAdapterForEnumerations = (this.useAdaptersForContainers && getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_26);
/* 137 */     this.forceLegacyNonListCollections = dowDowCfg.getForceLegacyNonListCollections();
/* 138 */     this.iterableSupport = dowDowCfg.getIterableSupport();
/* 139 */     this.domNodeSupport = dowDowCfg.getDOMNodeSupport();
/* 140 */     this.jythonSupport = dowDowCfg.getJythonSupport();
/* 141 */     finalizeConstruction(writeProtected);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected DefaultObjectWrapper(DefaultObjectWrapperConfiguration dowCfg, boolean writeProtected) {
/* 151 */     this(dowCfg, writeProtected);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/* 158 */       cl = Class.forName("org.python.core.PyObject");
/*     */ 
/*     */       
/* 161 */       ow = (ObjectWrapper)Class.forName("freemarker.ext.jython.JythonWrapper").getField("INSTANCE").get(null);
/* 162 */     } catch (Throwable e) {
/* 163 */       cl = null;
/* 164 */       ow = null;
/* 165 */       if (!(e instanceof ClassNotFoundException)) {
/*     */         try {
/* 167 */           Logger.getLogger("freemarker.template.DefaultObjectWrapper")
/* 168 */             .error("Failed to init Jython support, so it was disabled.", e);
/* 169 */         } catch (Throwable throwable) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 174 */     JYTHON_OBJ_CLASS = cl;
/* 175 */     JYTHON_WRAPPER = ow;
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
/*     */   public TemplateModel wrap(Object obj) throws TemplateModelException {
/* 189 */     if (obj == null) {
/* 190 */       return super.wrap(null);
/*     */     }
/* 192 */     if (obj instanceof TemplateModel) {
/* 193 */       return (TemplateModel)obj;
/*     */     }
/* 195 */     if (obj instanceof String) {
/* 196 */       return new SimpleScalar((String)obj);
/*     */     }
/* 198 */     if (obj instanceof Number) {
/* 199 */       return new SimpleNumber((Number)obj);
/*     */     }
/* 201 */     if (obj instanceof Date) {
/* 202 */       if (obj instanceof Date) {
/* 203 */         return new SimpleDate((Date)obj);
/*     */       }
/* 205 */       if (obj instanceof Time) {
/* 206 */         return new SimpleDate((Time)obj);
/*     */       }
/* 208 */       if (obj instanceof Timestamp) {
/* 209 */         return new SimpleDate((Timestamp)obj);
/*     */       }
/* 211 */       return new SimpleDate((Date)obj, getDefaultDateType());
/*     */     } 
/* 213 */     Class<?> objClass = obj.getClass();
/* 214 */     if (objClass.isArray()) {
/* 215 */       if (this.useAdaptersForContainers) {
/* 216 */         return DefaultArrayAdapter.adapt(obj, (ObjectWrapperAndUnwrapper)this);
/*     */       }
/* 218 */       obj = convertArray(obj);
/*     */     } 
/*     */ 
/*     */     
/* 222 */     if (obj instanceof Collection) {
/* 223 */       if (this.useAdaptersForContainers) {
/* 224 */         if (obj instanceof List) {
/* 225 */           return DefaultListAdapter.adapt((List)obj, (RichObjectWrapper)this);
/*     */         }
/* 227 */         return this.forceLegacyNonListCollections ? new SimpleSequence((Collection)obj, (ObjectWrapper)this) : 
/*     */           
/* 229 */           DefaultNonListCollectionAdapter.adapt((Collection)obj, (ObjectWrapperWithAPISupport)this);
/*     */       } 
/*     */       
/* 232 */       return new SimpleSequence((Collection)obj, (ObjectWrapper)this);
/*     */     } 
/*     */     
/* 235 */     if (obj instanceof Map) {
/* 236 */       return this.useAdaptersForContainers ? 
/* 237 */         DefaultMapAdapter.adapt((Map)obj, (ObjectWrapperWithAPISupport)this) : new SimpleHash((Map)obj, (ObjectWrapper)this);
/*     */     }
/*     */     
/* 240 */     if (obj instanceof Boolean) {
/* 241 */       return obj.equals(Boolean.TRUE) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/*     */     }
/* 243 */     if (obj instanceof Iterator) {
/* 244 */       return this.useAdaptersForContainers ? 
/* 245 */         DefaultIteratorAdapter.adapt((Iterator)obj, (ObjectWrapper)this) : new SimpleCollection((Iterator)obj, (ObjectWrapper)this);
/*     */     }
/*     */     
/* 248 */     if (this.useAdapterForEnumerations && obj instanceof Enumeration) {
/* 249 */       return DefaultEnumerationAdapter.adapt((Enumeration)obj, (ObjectWrapper)this);
/*     */     }
/* 251 */     if (this.iterableSupport && obj instanceof Iterable) {
/* 252 */       return DefaultIterableAdapter.adapt((Iterable)obj, (ObjectWrapperWithAPISupport)this);
/*     */     }
/*     */     
/* 255 */     return handleUnknownType(obj);
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
/*     */ 
/*     */   
/*     */   protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
/* 273 */     if (this.domNodeSupport && obj instanceof Node) {
/* 274 */       return wrapDomNode(obj);
/*     */     }
/*     */     
/* 277 */     if (this.jythonSupport) {
/* 278 */       MemberAccessPolicy memberAccessPolicy = getMemberAccessPolicy();
/* 279 */       if (memberAccessPolicy instanceof freemarker.ext.beans.DefaultMemberAccessPolicy || memberAccessPolicy instanceof freemarker.ext.beans.LegacyDefaultMemberAccessPolicy)
/*     */       {
/* 281 */         if (JYTHON_WRAPPER != null && JYTHON_OBJ_CLASS.isInstance(obj)) {
/* 282 */           return JYTHON_WRAPPER.wrap(obj);
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/* 287 */     return super.wrap(obj);
/*     */   }
/*     */   
/*     */   public TemplateModel wrapDomNode(Object obj) {
/* 291 */     return (TemplateModel)NodeModel.wrap((Node)obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object convertArray(Object arr) {
/* 299 */     int size = Array.getLength(arr);
/* 300 */     ArrayList<Object> list = new ArrayList(size);
/* 301 */     for (int i = 0; i < size; i++) {
/* 302 */       list.add(Array.get(arr, i));
/*     */     }
/* 304 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getUseAdaptersForContainers() {
/* 313 */     return this.useAdaptersForContainers;
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
/*     */   public void setUseAdaptersForContainers(boolean useAdaptersForContainers) {
/* 343 */     checkModifiable();
/* 344 */     this.useAdaptersForContainers = useAdaptersForContainers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getForceLegacyNonListCollections() {
/* 353 */     return this.forceLegacyNonListCollections;
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
/*     */ 
/*     */   
/*     */   public void setForceLegacyNonListCollections(boolean forceLegacyNonListCollections) {
/* 371 */     checkModifiable();
/* 372 */     this.forceLegacyNonListCollections = forceLegacyNonListCollections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getIterableSupport() {
/* 381 */     return this.iterableSupport;
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
/*     */   public void setIterableSupport(boolean iterableSupport) {
/* 396 */     checkModifiable();
/* 397 */     this.iterableSupport = iterableSupport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean getDOMNodeSupport() {
/* 406 */     return this.domNodeSupport;
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
/*     */   public void setDOMNodeSupport(boolean domNodeSupport) {
/* 418 */     checkModifiable();
/* 419 */     this.domNodeSupport = domNodeSupport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean getJythonSupport() {
/* 428 */     return this.jythonSupport;
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
/*     */   public void setJythonSupport(boolean jythonSupport) {
/* 441 */     checkModifiable();
/* 442 */     this.jythonSupport = jythonSupport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
/* 451 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/* 452 */     Version bwIcI = BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
/* 453 */     return (incompatibleImprovements.intValue() < _TemplateAPI.VERSION_INT_2_3_22 || bwIcI
/* 454 */       .intValue() >= _TemplateAPI.VERSION_INT_2_3_22) ? bwIcI : Configuration.VERSION_2_3_22;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String toPropertiesString() {
/* 463 */     String bwProps = super.toPropertiesString();
/*     */ 
/*     */     
/* 466 */     if (bwProps.startsWith("simpleMapWrapper")) {
/* 467 */       int smwEnd = bwProps.indexOf(',');
/* 468 */       if (smwEnd != -1) {
/* 469 */         bwProps = bwProps.substring(smwEnd + 1).trim();
/*     */       }
/*     */     } 
/*     */     
/* 473 */     return "useAdaptersForContainers=" + this.useAdaptersForContainers + ", forceLegacyNonListCollections=" + this.forceLegacyNonListCollections + ", iterableSupport=" + this.iterableSupport + ", domNodeSupport=" + this.domNodeSupport + ", jythonSupport=" + this.jythonSupport + bwProps;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultObjectWrapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */