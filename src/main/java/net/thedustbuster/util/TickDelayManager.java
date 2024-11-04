package net.thedustbuster.util;

import java.util.HashSet;
import java.util.Set;

public final class TickDelayManager {
  private static final Set<TickDelayTask> tasks = new HashSet<>();

  public static void tick() {
    tasks.removeIf(TickDelayTask::isComplete);
    tasks.forEach(TickDelayTask::decrement);
  }

  public static TickDelayTask executeIn(int ticks, Runnable action) {
    if (ticks < 0) throw new IllegalArgumentException("Tick count must be non-negative");

    TickDelayTask task = new TickDelayTask(ticks, action);
    tasks.add(task);

    return task;
  }

  public static final class TickDelayTask {
    private final Runnable action;
    private int ticks;

    private TickDelayTask(int ticks, Runnable action) {
      this.ticks = ticks;
      this.action = action;
    }

    public void refresh(int ticks) {
      this.ticks = ticks;
    }

    private void decrement() {
      if (--ticks <= 0) {
        action.run();
      }
    }

    private boolean isComplete() {
      return ticks <= 0;
    }
  }
}
