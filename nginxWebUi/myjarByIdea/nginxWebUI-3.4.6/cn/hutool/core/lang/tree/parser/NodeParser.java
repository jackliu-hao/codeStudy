package cn.hutool.core.lang.tree.parser;

import cn.hutool.core.lang.tree.Tree;

@FunctionalInterface
public interface NodeParser<T, E> {
   void parse(T var1, Tree<E> var2);
}
