package org.noear.solon.data.tranImp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.sql.DataSource;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.ext.RunnableEx;

public abstract class DbTran extends DbTranNode implements TranNode {
   private final Tran meta;
   private final Map<DataSource, Connection> conMap = new HashMap();

   public Tran getMeta() {
      return this.meta;
   }

   public Connection getConnection(DataSource ds) throws SQLException {
      if (this.conMap.containsKey(ds)) {
         return (Connection)this.conMap.get(ds);
      } else {
         Connection con = ds.getConnection();
         con.setAutoCommit(false);
         con.setReadOnly(this.meta.readOnly());
         if (this.meta.isolation().level > 0) {
            con.setTransactionIsolation(this.meta.isolation().level);
         }

         this.conMap.putIfAbsent(ds, con);
         return con;
      }
   }

   public DbTran(Tran meta) {
      this.meta = meta;
   }

   public void execute(RunnableEx runnable) throws Throwable {
      try {
         TranManager.currentSet(this);
         runnable.run();
         if (this.parent == null) {
            this.commit();
         }
      } catch (Throwable var6) {
         if (this.parent == null) {
            this.rollback();
         }

         throw Utils.throwableUnwrap(var6);
      } finally {
         TranManager.currentRemove();
         if (this.parent == null) {
            this.close();
         }

      }

   }

   public void commit() throws Throwable {
      super.commit();
      Iterator var1 = this.conMap.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<DataSource, Connection> kv = (Map.Entry)var1.next();
         ((Connection)kv.getValue()).commit();
      }

   }

   public void rollback() throws Throwable {
      super.rollback();
      Iterator var1 = this.conMap.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<DataSource, Connection> kv = (Map.Entry)var1.next();
         ((Connection)kv.getValue()).rollback();
      }

   }

   public void close() throws Throwable {
      super.close();
      Iterator var1 = this.conMap.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<DataSource, Connection> kv = (Map.Entry)var1.next();

         try {
            if (!((Connection)kv.getValue()).isClosed()) {
               ((Connection)kv.getValue()).close();
            }
         } catch (Throwable var4) {
            EventBus.push(var4);
         }
      }

   }
}
