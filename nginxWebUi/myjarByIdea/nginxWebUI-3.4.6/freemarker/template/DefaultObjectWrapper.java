package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperConfiguration;
import freemarker.ext.beans.DefaultMemberAccessPolicy;
import freemarker.ext.beans.LegacyDefaultMemberAccessPolicy;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.dom.NodeModel;
import freemarker.log.Logger;
import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public class DefaultObjectWrapper extends BeansWrapper {
   /** @deprecated */
   @Deprecated
   static final DefaultObjectWrapper instance = new DefaultObjectWrapper();
   private static final Class<?> JYTHON_OBJ_CLASS;
   private static final ObjectWrapper JYTHON_WRAPPER;
   private boolean useAdaptersForContainers;
   private boolean forceLegacyNonListCollections;
   private boolean iterableSupport;
   private boolean domNodeSupport;
   private boolean jythonSupport;
   private final boolean useAdapterForEnumerations;

   /** @deprecated */
   @Deprecated
   public DefaultObjectWrapper() {
      this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
   }

   public DefaultObjectWrapper(Version incompatibleImprovements) {
      this(new DefaultObjectWrapperConfiguration(incompatibleImprovements) {
      }, false);
   }

   protected DefaultObjectWrapper(BeansWrapperConfiguration bwCfg, boolean writeProtected) {
      super(bwCfg, writeProtected, false);
      DefaultObjectWrapperConfiguration dowDowCfg = bwCfg instanceof DefaultObjectWrapperConfiguration ? (DefaultObjectWrapperConfiguration)bwCfg : new DefaultObjectWrapperConfiguration(bwCfg.getIncompatibleImprovements()) {
      };
      this.useAdaptersForContainers = dowDowCfg.getUseAdaptersForContainers();
      this.useAdapterForEnumerations = this.useAdaptersForContainers && this.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_26;
      this.forceLegacyNonListCollections = dowDowCfg.getForceLegacyNonListCollections();
      this.iterableSupport = dowDowCfg.getIterableSupport();
      this.domNodeSupport = dowDowCfg.getDOMNodeSupport();
      this.jythonSupport = dowDowCfg.getJythonSupport();
      this.finalizeConstruction(writeProtected);
   }

   protected DefaultObjectWrapper(DefaultObjectWrapperConfiguration dowCfg, boolean writeProtected) {
      this((BeansWrapperConfiguration)dowCfg, writeProtected);
   }

   public TemplateModel wrap(Object obj) throws TemplateModelException {
      if (obj == null) {
         return super.wrap((Object)null);
      } else if (obj instanceof TemplateModel) {
         return (TemplateModel)obj;
      } else if (obj instanceof String) {
         return new SimpleScalar((String)obj);
      } else if (obj instanceof Number) {
         return new SimpleNumber((Number)obj);
      } else if (obj instanceof Date) {
         if (obj instanceof java.sql.Date) {
            return new SimpleDate((java.sql.Date)obj);
         } else if (obj instanceof Time) {
            return new SimpleDate((Time)obj);
         } else {
            return obj instanceof Timestamp ? new SimpleDate((Timestamp)obj) : new SimpleDate((Date)obj, this.getDefaultDateType());
         }
      } else {
         Class<?> objClass = obj.getClass();
         if (objClass.isArray()) {
            if (this.useAdaptersForContainers) {
               return DefaultArrayAdapter.adapt(obj, this);
            }

            obj = this.convertArray(obj);
         }

         if (obj instanceof Collection) {
            if (this.useAdaptersForContainers) {
               return (TemplateModel)(obj instanceof List ? DefaultListAdapter.adapt((List)obj, this) : (TemplateModel)(this.forceLegacyNonListCollections ? new SimpleSequence((Collection)obj, this) : DefaultNonListCollectionAdapter.adapt((Collection)obj, this)));
            } else {
               return new SimpleSequence((Collection)obj, this);
            }
         } else if (obj instanceof Map) {
            return (TemplateModel)(this.useAdaptersForContainers ? DefaultMapAdapter.adapt((Map)obj, this) : new SimpleHash((Map)obj, this));
         } else if (obj instanceof Boolean) {
            return obj.equals(Boolean.TRUE) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
         } else if (obj instanceof Iterator) {
            return (TemplateModel)(this.useAdaptersForContainers ? DefaultIteratorAdapter.adapt((Iterator)obj, this) : new SimpleCollection((Iterator)obj, this));
         } else if (this.useAdapterForEnumerations && obj instanceof Enumeration) {
            return DefaultEnumerationAdapter.adapt((Enumeration)obj, this);
         } else {
            return (TemplateModel)(this.iterableSupport && obj instanceof Iterable ? DefaultIterableAdapter.adapt((Iterable)obj, this) : this.handleUnknownType(obj));
         }
      }
   }

   protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
      if (this.domNodeSupport && obj instanceof Node) {
         return this.wrapDomNode(obj);
      } else {
         if (this.jythonSupport) {
            MemberAccessPolicy memberAccessPolicy = this.getMemberAccessPolicy();
            if ((memberAccessPolicy instanceof DefaultMemberAccessPolicy || memberAccessPolicy instanceof LegacyDefaultMemberAccessPolicy) && JYTHON_WRAPPER != null && JYTHON_OBJ_CLASS.isInstance(obj)) {
               return JYTHON_WRAPPER.wrap(obj);
            }
         }

         return super.wrap(obj);
      }
   }

   public TemplateModel wrapDomNode(Object obj) {
      return NodeModel.wrap((Node)obj);
   }

   protected Object convertArray(Object arr) {
      int size = Array.getLength(arr);
      ArrayList list = new ArrayList(size);

      for(int i = 0; i < size; ++i) {
         list.add(Array.get(arr, i));
      }

      return list;
   }

   public boolean getUseAdaptersForContainers() {
      return this.useAdaptersForContainers;
   }

   public void setUseAdaptersForContainers(boolean useAdaptersForContainers) {
      this.checkModifiable();
      this.useAdaptersForContainers = useAdaptersForContainers;
   }

   public boolean getForceLegacyNonListCollections() {
      return this.forceLegacyNonListCollections;
   }

   public void setForceLegacyNonListCollections(boolean forceLegacyNonListCollections) {
      this.checkModifiable();
      this.forceLegacyNonListCollections = forceLegacyNonListCollections;
   }

   public boolean getIterableSupport() {
      return this.iterableSupport;
   }

   public void setIterableSupport(boolean iterableSupport) {
      this.checkModifiable();
      this.iterableSupport = iterableSupport;
   }

   public final boolean getDOMNodeSupport() {
      return this.domNodeSupport;
   }

   public void setDOMNodeSupport(boolean domNodeSupport) {
      this.checkModifiable();
      this.domNodeSupport = domNodeSupport;
   }

   public final boolean getJythonSupport() {
      return this.jythonSupport;
   }

   public void setJythonSupport(boolean jythonSupport) {
      this.checkModifiable();
      this.jythonSupport = jythonSupport;
   }

   protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
      _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
      Version bwIcI = BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
      return incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_22 && bwIcI.intValue() < _TemplateAPI.VERSION_INT_2_3_22 ? Configuration.VERSION_2_3_22 : bwIcI;
   }

   protected String toPropertiesString() {
      String bwProps = super.toPropertiesString();
      if (bwProps.startsWith("simpleMapWrapper")) {
         int smwEnd = bwProps.indexOf(44);
         if (smwEnd != -1) {
            bwProps = bwProps.substring(smwEnd + 1).trim();
         }
      }

      return "useAdaptersForContainers=" + this.useAdaptersForContainers + ", forceLegacyNonListCollections=" + this.forceLegacyNonListCollections + ", iterableSupport=" + this.iterableSupport + ", domNodeSupport=" + this.domNodeSupport + ", jythonSupport=" + this.jythonSupport + bwProps;
   }

   static {
      Class cl;
      ObjectWrapper ow;
      try {
         cl = Class.forName("org.python.core.PyObject");
         ow = (ObjectWrapper)Class.forName("freemarker.ext.jython.JythonWrapper").getField("INSTANCE").get((Object)null);
      } catch (Throwable var5) {
         Throwable e = var5;
         cl = null;
         ow = null;
         if (!(var5 instanceof ClassNotFoundException)) {
            try {
               Logger.getLogger("freemarker.template.DefaultObjectWrapper").error("Failed to init Jython support, so it was disabled.", e);
            } catch (Throwable var4) {
            }
         }
      }

      JYTHON_OBJ_CLASS = cl;
      JYTHON_WRAPPER = ow;
   }
}
