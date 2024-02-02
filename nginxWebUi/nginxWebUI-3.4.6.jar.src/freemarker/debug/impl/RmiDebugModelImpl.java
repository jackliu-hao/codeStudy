/*     */ package freemarker.debug.impl;
/*     */ 
/*     */ import freemarker.debug.DebugModel;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.rmi.RemoteException;
/*     */ import java.rmi.server.UnicastRemoteObject;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
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
/*     */ class RmiDebugModelImpl
/*     */   extends UnicastRemoteObject
/*     */   implements DebugModel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final TemplateModel model;
/*     */   private final int type;
/*     */   
/*     */   RmiDebugModelImpl(TemplateModel model, int extraTypes) throws RemoteException {
/*  54 */     this.model = model;
/*  55 */     this.type = calculateType(model) + extraTypes;
/*     */   }
/*     */   
/*     */   private static DebugModel getDebugModel(TemplateModel tm) throws RemoteException {
/*  59 */     return (DebugModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(tm);
/*     */   }
/*     */   
/*     */   public String getAsString() throws TemplateModelException {
/*  63 */     return ((TemplateScalarModel)this.model).getAsString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Number getAsNumber() throws TemplateModelException {
/*  68 */     return ((TemplateNumberModel)this.model).getAsNumber();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getAsDate() throws TemplateModelException {
/*  73 */     return ((TemplateDateModel)this.model).getAsDate();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDateType() {
/*  78 */     return ((TemplateDateModel)this.model).getDateType();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getAsBoolean() throws TemplateModelException {
/*  83 */     return ((TemplateBooleanModel)this.model).getAsBoolean();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() throws TemplateModelException {
/*  88 */     if (this.model instanceof TemplateSequenceModel) {
/*  89 */       return ((TemplateSequenceModel)this.model).size();
/*     */     }
/*  91 */     return ((TemplateHashModelEx)this.model).size();
/*     */   }
/*     */ 
/*     */   
/*     */   public DebugModel get(int index) throws TemplateModelException, RemoteException {
/*  96 */     return getDebugModel(((TemplateSequenceModel)this.model).get(index));
/*     */   }
/*     */ 
/*     */   
/*     */   public DebugModel[] get(int fromIndex, int toIndex) throws TemplateModelException, RemoteException {
/* 101 */     DebugModel[] dm = new DebugModel[toIndex - fromIndex];
/* 102 */     TemplateSequenceModel s = (TemplateSequenceModel)this.model;
/* 103 */     for (int i = fromIndex; i < toIndex; i++) {
/* 104 */       dm[i - fromIndex] = getDebugModel(s.get(i));
/*     */     }
/* 106 */     return dm;
/*     */   }
/*     */ 
/*     */   
/*     */   public DebugModel[] getCollection() throws TemplateModelException, RemoteException {
/* 111 */     List<DebugModel> list = new ArrayList();
/* 112 */     TemplateModelIterator i = ((TemplateCollectionModel)this.model).iterator();
/* 113 */     while (i.hasNext()) {
/* 114 */       list.add(getDebugModel(i.next()));
/*     */     }
/* 116 */     return list.<DebugModel>toArray(new DebugModel[list.size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   public DebugModel get(String key) throws TemplateModelException, RemoteException {
/* 121 */     return getDebugModel(((TemplateHashModel)this.model).get(key));
/*     */   }
/*     */ 
/*     */   
/*     */   public DebugModel[] get(String[] keys) throws TemplateModelException, RemoteException {
/* 126 */     DebugModel[] dm = new DebugModel[keys.length];
/* 127 */     TemplateHashModel h = (TemplateHashModel)this.model;
/* 128 */     for (int i = 0; i < keys.length; i++) {
/* 129 */       dm[i] = getDebugModel(h.get(keys[i]));
/*     */     }
/* 131 */     return dm;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] keys() throws TemplateModelException {
/* 136 */     TemplateHashModelEx h = (TemplateHashModelEx)this.model;
/* 137 */     List<String> list = new ArrayList();
/* 138 */     TemplateModelIterator i = h.keys().iterator();
/* 139 */     while (i.hasNext()) {
/* 140 */       list.add(((TemplateScalarModel)i.next()).getAsString());
/*     */     }
/* 142 */     return list.<String>toArray(new String[list.size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getModelTypes() {
/* 147 */     return this.type;
/*     */   }
/*     */   
/*     */   private static int calculateType(TemplateModel model) {
/* 151 */     int type = 0;
/* 152 */     if (model instanceof TemplateScalarModel) type++; 
/* 153 */     if (model instanceof TemplateNumberModel) type += 2; 
/* 154 */     if (model instanceof TemplateDateModel) type += 4; 
/* 155 */     if (model instanceof TemplateBooleanModel) type += 8; 
/* 156 */     if (model instanceof TemplateSequenceModel) type += 16; 
/* 157 */     if (model instanceof TemplateCollectionModel) type += 32; 
/* 158 */     if (model instanceof TemplateHashModelEx) { type += 128; }
/* 159 */     else if (model instanceof TemplateHashModel) { type += 64; }
/* 160 */      if (model instanceof freemarker.template.TemplateMethodModelEx) { type += 512; }
/* 161 */     else if (model instanceof freemarker.template.TemplateMethodModel) { type += 256; }
/* 162 */      if (model instanceof freemarker.template.TemplateTransformModel) type += 1024; 
/* 163 */     return type;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\impl\RmiDebugModelImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */