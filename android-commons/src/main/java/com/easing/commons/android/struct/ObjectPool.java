package com.easing.commons.android.struct;

import java.util.LinkedList;

import lombok.Getter;

public class ObjectPool<T>
{
  private int size = 0;
  @Getter
  private LinkedList<T> activeItems = new LinkedList();
  @Getter
  private LinkedList<T> idleItems = new LinkedList();

  private ObjectPool() {}

  public static <T> ObjectPool<T> init(int size, ObjectBuilder<T> builder)
  {
    ObjectPool<T> pool = new ObjectPool();
    pool.size = size;
    for (int i = 0; i < size; i++)
      pool.idleItems.addLast(builder.build());
    return pool;
  }

  public T obtainToFirst()
  {
    if (idleItems.isEmpty()) return null;
    T item = idleItems.removeLast();
    activeItems.addFirst(item);
    return item;
  }

  public T obtainToLast()
  {
    if (idleItems.isEmpty()) return null;
    T item = idleItems.removeLast();
    activeItems.addLast(item);
    return item;
  }

  public T recycle(T item)
  {
    activeItems.remove(item);
    idleItems.addLast(item);
    return item;
  }

  public T recycleFromFirst()
  {
    T item = activeItems.removeFirst();
    idleItems.addLast(item);
    return item;
  }

  public T recycleFromLast()
  {
    T item = activeItems.removeLast();
    idleItems.addLast(item);
    return item;
  }

  public static interface ObjectBuilder<T>
  {
    public T build();
  }
}
