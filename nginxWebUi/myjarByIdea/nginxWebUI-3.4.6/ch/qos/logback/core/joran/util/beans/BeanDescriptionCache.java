package ch.qos.logback.core.joran.util.beans;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.HashMap;
import java.util.Map;

public class BeanDescriptionCache extends ContextAwareBase {
   private Map<Class<?>, BeanDescription> classToBeanDescription = new HashMap();
   private BeanDescriptionFactory beanDescriptionFactory;

   public BeanDescriptionCache(Context context) {
      this.setContext(context);
   }

   private BeanDescriptionFactory getBeanDescriptionFactory() {
      if (this.beanDescriptionFactory == null) {
         this.beanDescriptionFactory = new BeanDescriptionFactory(this.getContext());
      }

      return this.beanDescriptionFactory;
   }

   public BeanDescription getBeanDescription(Class<?> clazz) {
      if (!this.classToBeanDescription.containsKey(clazz)) {
         BeanDescription beanDescription = this.getBeanDescriptionFactory().create(clazz);
         this.classToBeanDescription.put(clazz, beanDescription);
      }

      return (BeanDescription)this.classToBeanDescription.get(clazz);
   }
}
