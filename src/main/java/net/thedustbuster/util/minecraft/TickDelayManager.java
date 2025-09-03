package net.thedustbuster.util.minecraft;

import java.util.HashSet;
import java.util.Set;

public final class TickDelayManager {
    private static final Set<TickDelayTask> tasks = new HashSet<>();

    public static void tick() {
        tasks.forEach(TickDelayTask::decrement);
        tasks.removeIf(TickDelayTask::isComplete);
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
        private boolean complete = false;

        private TickDelayTask(int ticks, Runnable action) {
            this.ticks = ticks;
            this.action = action;
        }

        public void refresh(int ticks) {
            this.ticks = ticks;
        }

        public void cancel() {
            complete = true;
        }

        public void complete() {
            cancel();
            action.run();
        }

        private void decrement() {
            if (--ticks <= 0 && !complete) {
                action.run();
                complete = true;
            }
        }

        public boolean isComplete() {
            return ticks <= 0 || complete;
        }
    }
}
