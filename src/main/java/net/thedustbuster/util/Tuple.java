package net.thedustbuster.util;

public class Tuple<A, B> {
  private final A first;
  private final B second;

  public Tuple(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public A _1() {
    return first;
  }

  public B _2() {
    return second;
  }
}