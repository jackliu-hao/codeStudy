package org.wildfly.common.ref;

interface Reapable<T, A> {
  Reaper<T, A> getReaper();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\Reapable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */