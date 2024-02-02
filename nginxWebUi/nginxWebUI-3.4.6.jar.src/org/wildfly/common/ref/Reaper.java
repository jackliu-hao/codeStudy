package org.wildfly.common.ref;

public interface Reaper<T, A> {
  void reap(Reference<T, A> paramReference);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\Reaper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */