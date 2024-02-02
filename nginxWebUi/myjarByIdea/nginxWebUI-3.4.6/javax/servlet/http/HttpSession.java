package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.ServletContext;

public interface HttpSession {
   long getCreationTime();

   String getId();

   long getLastAccessedTime();

   ServletContext getServletContext();

   void setMaxInactiveInterval(int var1);

   int getMaxInactiveInterval();

   /** @deprecated */
   @Deprecated
   HttpSessionContext getSessionContext();

   Object getAttribute(String var1);

   /** @deprecated */
   @Deprecated
   Object getValue(String var1);

   Enumeration<String> getAttributeNames();

   /** @deprecated */
   @Deprecated
   String[] getValueNames();

   void setAttribute(String var1, Object var2);

   /** @deprecated */
   @Deprecated
   void putValue(String var1, Object var2);

   void removeAttribute(String var1);

   /** @deprecated */
   @Deprecated
   void removeValue(String var1);

   void invalidate();

   boolean isNew();
}
