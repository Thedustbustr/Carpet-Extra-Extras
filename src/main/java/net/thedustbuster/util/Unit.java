package net.thedustbuster.util;

import java.util.function.Consumer;

public final class Unit {
  private Unit() { }

  public final static Unit Unit = new Unit();

  public static Unit cast(Runnable ydowehavedothisjava) {
    ydowehavedothisjava.run();
    return Unit;
  }
}
