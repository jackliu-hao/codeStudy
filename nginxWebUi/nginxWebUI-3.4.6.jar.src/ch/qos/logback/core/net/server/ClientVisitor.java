package ch.qos.logback.core.net.server;

public interface ClientVisitor<T extends Client> {
  void visit(T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\ClientVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */