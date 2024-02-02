package org.apache.http.params;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class SyncBasicHttpParams extends BasicHttpParams {
   private static final long serialVersionUID = 5387834869062660642L;

   public synchronized boolean removeParameter(String name) {
      return super.removeParameter(name);
   }

   public synchronized HttpParams setParameter(String name, Object value) {
      return super.setParameter(name, value);
   }

   public synchronized Object getParameter(String name) {
      return super.getParameter(name);
   }

   public synchronized boolean isParameterSet(String name) {
      return super.isParameterSet(name);
   }

   public synchronized boolean isParameterSetLocally(String name) {
      return super.isParameterSetLocally(name);
   }

   public synchronized void setParameters(String[] names, Object value) {
      super.setParameters(names, value);
   }

   public synchronized void clear() {
      super.clear();
   }

   public synchronized Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
