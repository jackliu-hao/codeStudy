package cn.hutool.core.lang.tree.parser;

import cn.hutool.core.lang.tree.Tree;

@FunctionalInterface
public interface NodeParser<T, E> {
  void parse(T paramT, Tree<E> paramTree);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\parser\NodeParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */