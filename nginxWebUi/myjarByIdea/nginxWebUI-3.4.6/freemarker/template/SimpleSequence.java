package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleSequence extends WrappingTemplateModel implements TemplateSequenceModel, Serializable {
   protected final List list;
   private List unwrappedList;

   /** @deprecated */
   @Deprecated
   public SimpleSequence() {
      this((ObjectWrapper)null);
   }

   /** @deprecated */
   @Deprecated
   public SimpleSequence(int capacity) {
      this.list = new ArrayList(capacity);
   }

   /** @deprecated */
   @Deprecated
   public SimpleSequence(Collection collection) {
      this(collection, (ObjectWrapper)null);
   }

   public SimpleSequence(TemplateCollectionModel tcm) throws TemplateModelException {
      ArrayList alist = new ArrayList();
      TemplateModelIterator it = tcm.iterator();

      while(it.hasNext()) {
         alist.add(it.next());
      }

      alist.trimToSize();
      this.list = alist;
   }

   public SimpleSequence(ObjectWrapper wrapper) {
      super(wrapper);
      this.list = new ArrayList();
   }

   public SimpleSequence(int capacity, ObjectWrapper wrapper) {
      super(wrapper);
      this.list = new ArrayList(capacity);
   }

   public SimpleSequence(Collection collection, ObjectWrapper wrapper) {
      super(wrapper);
      this.list = new ArrayList(collection);
   }

   public void add(Object obj) {
      this.list.add(obj);
      this.unwrappedList = null;
   }

   /** @deprecated */
   @Deprecated
   public void add(boolean b) {
      this.add(b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
   }

   /** @deprecated */
   @Deprecated
   public List toList() throws TemplateModelException {
      if (this.unwrappedList == null) {
         Class listClass = this.list.getClass();
         List result = null;

         try {
            result = (List)listClass.newInstance();
         } catch (Exception var6) {
            throw new TemplateModelException("Error instantiating an object of type " + listClass.getName(), var6);
         }

         BeansWrapper bw = BeansWrapper.getDefaultInstance();

         for(int i = 0; i < this.list.size(); ++i) {
            Object elem = this.list.get(i);
            if (elem instanceof TemplateModel) {
               elem = bw.unwrap((TemplateModel)elem);
            }

            result.add(elem);
         }

         this.unwrappedList = result;
      }

      return this.unwrappedList;
   }

   public TemplateModel get(int index) throws TemplateModelException {
      try {
         Object value = this.list.get(index);
         if (value instanceof TemplateModel) {
            return (TemplateModel)value;
         } else {
            TemplateModel tm = this.wrap(value);
            this.list.set(index, tm);
            return tm;
         }
      } catch (IndexOutOfBoundsException var4) {
         return null;
      }
   }

   public int size() {
      return this.list.size();
   }

   public SimpleSequence synchronizedWrapper() {
      return new SynchronizedSequence();
   }

   public String toString() {
      return this.list.toString();
   }

   private class SynchronizedSequence extends SimpleSequence {
      private SynchronizedSequence() {
         super(SimpleSequence.this.getObjectWrapper());
      }

      public void add(Object obj) {
         synchronized(SimpleSequence.this) {
            SimpleSequence.this.add(obj);
         }
      }

      public TemplateModel get(int i) throws TemplateModelException {
         synchronized(SimpleSequence.this) {
            return SimpleSequence.this.get(i);
         }
      }

      public int size() {
         synchronized(SimpleSequence.this) {
            return SimpleSequence.this.size();
         }
      }

      public List toList() throws TemplateModelException {
         synchronized(SimpleSequence.this) {
            return SimpleSequence.this.toList();
         }
      }

      public SimpleSequence synchronizedWrapper() {
         return this;
      }

      // $FF: synthetic method
      SynchronizedSequence(Object x1) {
         this();
      }
   }
}
