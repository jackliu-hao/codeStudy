package cn.hutool.extra.ssh;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.Session;
import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum JschSessionPool {
   INSTANCE;

   private final SimpleCache<String, Session> cache = new SimpleCache(new HashMap());

   public Session get(String key) {
      return (Session)this.cache.get(key);
   }

   public Session getSession(String sshHost, int sshPort, String sshUser, String sshPass) {
      String key = StrUtil.format("{}@{}:{}", new Object[]{sshUser, sshHost, sshPort});
      return (Session)this.cache.get(key, Session::isConnected, () -> {
         return JschUtil.openSession(sshHost, sshPort, sshUser, sshPass);
      });
   }

   public Session getSession(String sshHost, int sshPort, String sshUser, String prvkey, byte[] passphrase) {
      String key = StrUtil.format("{}@{}:{}", new Object[]{sshUser, sshHost, sshPort});
      return (Session)this.cache.get(key, Session::isConnected, () -> {
         return JschUtil.openSession(sshHost, sshPort, sshUser, prvkey, passphrase);
      });
   }

   public void put(String key, Session session) {
      this.cache.put(key, session);
   }

   public void close(String key) {
      Session session = this.get(key);
      if (session != null && session.isConnected()) {
         session.disconnect();
      }

      this.cache.remove(key);
   }

   public void remove(Session session) {
      if (null != session) {
         Iterator<Map.Entry<String, Session>> iterator = this.cache.iterator();

         while(iterator.hasNext()) {
            Map.Entry<String, Session> entry = (Map.Entry)iterator.next();
            if (session.equals(entry.getValue())) {
               iterator.remove();
               break;
            }
         }
      }

   }

   public void closeAll() {
      Iterator var2 = this.cache.iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Session> entry = (Map.Entry)var2.next();
         Session session = (Session)entry.getValue();
         if (session != null && session.isConnected()) {
            session.disconnect();
         }
      }

      this.cache.clear();
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$getSession$b6481cf0$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/ssh/JschSessionPool") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)Lcom/jcraft/jsch/Session;")) {
               return () -> {
                  return JschUtil.openSession(sshHost, sshPort, sshUser, sshPass);
               };
            }
            break;
         case "lambda$getSession$59bcceb4$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/ssh/JschSessionPool") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;[B)Lcom/jcraft/jsch/Session;")) {
               return () -> {
                  return JschUtil.openSession(sshHost, sshPort, sshUser, prvkey, passphrase);
               };
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
