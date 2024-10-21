package net.thedustbuster.util;

public final class Unit {
  private Unit() { }

  public final static Unit INSTANCE = new Unit();

  public static Unit cast(Runnable ydowehavedothisjava) {
    ydowehavedothisjava.run();
    return INSTANCE;
  }
}
