package io.undertow.util;

public interface ConduitFactory<C extends org.xnio.conduits.Conduit> {
  C create();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ConduitFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */