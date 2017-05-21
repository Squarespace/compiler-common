package com.squarespace.compiler.parse;


public class Pair<T, S> {

  public final T _1;

  public final S _2;

  private Pair(T _1, S _2) {
      this._1 = _1;
      this._2 = _2;
  }

  public static <T, S> Pair<T, S> pair(T k, S v) {
      return new Pair<>(k, v);
  }

}
