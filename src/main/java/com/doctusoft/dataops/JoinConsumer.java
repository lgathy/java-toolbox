package com.doctusoft.dataops;

public interface JoinConsumer<L, R, K> {
  
  void accept(L left, R right, K key);
  
}
