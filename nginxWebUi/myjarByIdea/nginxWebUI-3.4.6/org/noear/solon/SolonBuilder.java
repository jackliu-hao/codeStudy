package org.noear.solon;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.event.AppInitEndEvent;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.BeanLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.event.PluginLoadEndEvent;
import org.noear.solon.ext.ConsumerEx;

public class SolonBuilder {
   public <T> SolonBuilder onEvent(Class<T> type, EventListener<T> handler) {
      EventBus.subscribe(type, handler);
      return this;
   }

   public SolonBuilder onError(EventListener<Throwable> handler) {
      return this.onEvent(Throwable.class, handler);
   }

   @Note("1.")
   public SolonBuilder onAppInitEnd(EventListener<AppInitEndEvent> handler) {
      return this.onEvent(AppInitEndEvent.class, handler);
   }

   @Note("2.")
   public SolonBuilder onPluginLoadEnd(EventListener<PluginLoadEndEvent> handler) {
      return this.onEvent(PluginLoadEndEvent.class, handler);
   }

   @Note("3.")
   public SolonBuilder onBeanLoadEnd(EventListener<BeanLoadEndEvent> handler) {
      return this.onEvent(BeanLoadEndEvent.class, handler);
   }

   @Note("4.")
   public SolonBuilder onAppLoadEnd(EventListener<AppLoadEndEvent> handler) {
      return this.onEvent(AppLoadEndEvent.class, handler);
   }

   public SolonApp start(Class<?> source, String[] args) {
      return Solon.start(source, args);
   }

   public SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
      return Solon.start(source, args, initialize);
   }

   public SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
      return Solon.start(source, argx, initialize);
   }
}
