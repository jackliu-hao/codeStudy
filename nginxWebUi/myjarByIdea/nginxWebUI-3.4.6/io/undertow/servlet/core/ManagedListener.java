package io.undertow.servlet.core;

import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.ListenerInfo;
import java.util.EventListener;
import javax.servlet.ServletException;

public class ManagedListener implements Lifecycle {
   private final ListenerInfo listenerInfo;
   private final boolean programatic;
   private volatile boolean started = false;
   private volatile InstanceHandle<? extends EventListener> handle;

   public ManagedListener(ListenerInfo listenerInfo, boolean programatic) {
      this.listenerInfo = listenerInfo;
      this.programatic = programatic;
   }

   public synchronized void start() throws ServletException {
      if (!this.started) {
         try {
            this.handle = this.listenerInfo.getInstanceFactory().createInstance();
         } catch (Exception var2) {
            throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.listenerInfo.getListenerClass().getName(), var2);
         }

         this.started = true;
      }

   }

   public synchronized void stop() {
      this.started = false;
      if (this.handle != null) {
         this.handle.release();
      }

   }

   public ListenerInfo getListenerInfo() {
      return this.listenerInfo;
   }

   public boolean isStarted() {
      return this.started;
   }

   public EventListener instance() {
      if (!this.started) {
         throw UndertowServletMessages.MESSAGES.listenerIsNotStarted();
      } else {
         return (EventListener)this.handle.getInstance();
      }
   }

   public boolean isProgramatic() {
      return this.programatic;
   }

   public String toString() {
      return "ManagedListener{listenerInfo=" + this.listenerInfo + '}';
   }
}
