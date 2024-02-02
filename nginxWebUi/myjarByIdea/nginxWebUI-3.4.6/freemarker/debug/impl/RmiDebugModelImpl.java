package freemarker.debug.impl;

import freemarker.debug.DebugModel;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class RmiDebugModelImpl extends UnicastRemoteObject implements DebugModel {
   private static final long serialVersionUID = 1L;
   private final TemplateModel model;
   private final int type;

   RmiDebugModelImpl(TemplateModel model, int extraTypes) throws RemoteException {
      this.model = model;
      this.type = calculateType(model) + extraTypes;
   }

   private static DebugModel getDebugModel(TemplateModel tm) throws RemoteException {
      return (DebugModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(tm);
   }

   public String getAsString() throws TemplateModelException {
      return ((TemplateScalarModel)this.model).getAsString();
   }

   public Number getAsNumber() throws TemplateModelException {
      return ((TemplateNumberModel)this.model).getAsNumber();
   }

   public Date getAsDate() throws TemplateModelException {
      return ((TemplateDateModel)this.model).getAsDate();
   }

   public int getDateType() {
      return ((TemplateDateModel)this.model).getDateType();
   }

   public boolean getAsBoolean() throws TemplateModelException {
      return ((TemplateBooleanModel)this.model).getAsBoolean();
   }

   public int size() throws TemplateModelException {
      return this.model instanceof TemplateSequenceModel ? ((TemplateSequenceModel)this.model).size() : ((TemplateHashModelEx)this.model).size();
   }

   public DebugModel get(int index) throws TemplateModelException, RemoteException {
      return getDebugModel(((TemplateSequenceModel)this.model).get(index));
   }

   public DebugModel[] get(int fromIndex, int toIndex) throws TemplateModelException, RemoteException {
      DebugModel[] dm = new DebugModel[toIndex - fromIndex];
      TemplateSequenceModel s = (TemplateSequenceModel)this.model;

      for(int i = fromIndex; i < toIndex; ++i) {
         dm[i - fromIndex] = getDebugModel(s.get(i));
      }

      return dm;
   }

   public DebugModel[] getCollection() throws TemplateModelException, RemoteException {
      List list = new ArrayList();
      TemplateModelIterator i = ((TemplateCollectionModel)this.model).iterator();

      while(i.hasNext()) {
         list.add(getDebugModel(i.next()));
      }

      return (DebugModel[])((DebugModel[])list.toArray(new DebugModel[list.size()]));
   }

   public DebugModel get(String key) throws TemplateModelException, RemoteException {
      return getDebugModel(((TemplateHashModel)this.model).get(key));
   }

   public DebugModel[] get(String[] keys) throws TemplateModelException, RemoteException {
      DebugModel[] dm = new DebugModel[keys.length];
      TemplateHashModel h = (TemplateHashModel)this.model;

      for(int i = 0; i < keys.length; ++i) {
         dm[i] = getDebugModel(h.get(keys[i]));
      }

      return dm;
   }

   public String[] keys() throws TemplateModelException {
      TemplateHashModelEx h = (TemplateHashModelEx)this.model;
      List list = new ArrayList();
      TemplateModelIterator i = h.keys().iterator();

      while(i.hasNext()) {
         list.add(((TemplateScalarModel)i.next()).getAsString());
      }

      return (String[])((String[])list.toArray(new String[list.size()]));
   }

   public int getModelTypes() {
      return this.type;
   }

   private static int calculateType(TemplateModel model) {
      int type = 0;
      if (model instanceof TemplateScalarModel) {
         ++type;
      }

      if (model instanceof TemplateNumberModel) {
         type += 2;
      }

      if (model instanceof TemplateDateModel) {
         type += 4;
      }

      if (model instanceof TemplateBooleanModel) {
         type += 8;
      }

      if (model instanceof TemplateSequenceModel) {
         type += 16;
      }

      if (model instanceof TemplateCollectionModel) {
         type += 32;
      }

      if (model instanceof TemplateHashModelEx) {
         type += 128;
      } else if (model instanceof TemplateHashModel) {
         type += 64;
      }

      if (model instanceof TemplateMethodModelEx) {
         type += 512;
      } else if (model instanceof TemplateMethodModel) {
         type += 256;
      }

      if (model instanceof TemplateTransformModel) {
         type += 1024;
      }

      return type;
   }
}
