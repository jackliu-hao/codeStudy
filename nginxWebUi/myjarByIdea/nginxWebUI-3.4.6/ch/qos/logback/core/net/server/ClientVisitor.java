package ch.qos.logback.core.net.server;

public interface ClientVisitor<T extends Client> {
   void visit(T var1);
}
