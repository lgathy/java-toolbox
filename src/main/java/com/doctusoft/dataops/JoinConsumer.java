package com.doctusoft.dataops;

@FunctionalInterface
public interface JoinConsumer<L, R, K> {
  
  void accept(L left, R right, K key);
  
}
