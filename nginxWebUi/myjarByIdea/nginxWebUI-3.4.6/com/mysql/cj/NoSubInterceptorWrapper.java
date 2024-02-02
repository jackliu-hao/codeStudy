package com.mysql.cj;

import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import java.util.Properties;
import java.util.function.Supplier;

public class NoSubInterceptorWrapper implements QueryInterceptor {
   private final QueryInterceptor underlyingInterceptor;

   public NoSubInterceptorWrapper(QueryInterceptor underlyingInterceptor) {
      if (underlyingInterceptor == null) {
         throw new RuntimeException(Messages.getString("NoSubInterceptorWrapper.0"));
      } else {
         this.underlyingInterceptor = underlyingInterceptor;
      }
   }

   public void destroy() {
      this.underlyingInterceptor.destroy();
   }

   public boolean executeTopLevelOnly() {
      return this.underlyingInterceptor.executeTopLevelOnly();
   }

   public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
      this.underlyingInterceptor.init(conn, props, log);
      return this;
   }

   public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
      this.underlyingInterceptor.postProcess(sql, interceptedQuery, originalResultSet, serverSession);
      return null;
   }

   public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
      this.underlyingInterceptor.preProcess(sql, interceptedQuery);
      return null;
   }

   public <M extends Message> M preProcess(M queryPacket) {
      this.underlyingInterceptor.preProcess(queryPacket);
      return null;
   }

   public <M extends Message> M postProcess(M queryPacket, M originalResponsePacket) {
      this.underlyingInterceptor.postProcess(queryPacket, originalResponsePacket);
      return null;
   }

   public QueryInterceptor getUnderlyingInterceptor() {
      return this.underlyingInterceptor;
   }
}
