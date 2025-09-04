package net.thedustbuster.util.func;

public final class Unit {
  private Unit() { }

  public static final Unit Unit = new Unit();

  public static Unit Unit(Runnable action) {
    action.run();
    return Unit;
  }
}