package org.h2.jdbcx;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.h2.engine.SysProperties;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;

public final class JdbcDataSourceFactory implements ObjectFactory {
   private static final TraceSystem traceSystem;
   private final Trace trace;

   public JdbcDataSourceFactory() {
      this.trace = traceSystem.getTrace(14);
   }

   public synchronized Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) {
      if (this.trace.isDebugEnabled()) {
         this.trace.debug("getObjectInstance obj={0} name={1} nameCtx={2} environment={3}", var1, var2, var3, var4);
      }

      if (var1 instanceof Reference) {
         Reference var5 = (Reference)var1;
         if (var5.getClassName().equals(JdbcDataSource.class.getName())) {
            JdbcDataSource var6 = new JdbcDataSource();
            var6.setURL((String)var5.get("url").getContent());
            var6.setUser((String)var5.get("user").getContent());
            var6.setPassword((String)var5.get("password").getContent());
            var6.setDescription((String)var5.get("description").getContent());
            String var7 = (String)var5.get("loginTimeout").getContent();
            var6.setLoginTimeout(Integer.parseInt(var7));
            return var6;
         }
      }

      return null;
   }

   public static TraceSystem getTraceSystem() {
      return traceSystem;
   }

   Trace getTrace() {
      return this.trace;
   }

   static {
      traceSystem = new TraceSystem(SysProperties.CLIENT_TRACE_DIRECTORY + "h2datasource" + ".trace.db");
      traceSystem.setLevelFile(SysProperties.DATASOURCE_TRACE_LEVEL);
   }
}
