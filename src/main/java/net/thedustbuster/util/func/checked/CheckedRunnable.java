package net.thedustbuster.util.func.checked;

@FunctionalInterface
public interface CheckedRunnable {
  void run() throws Exception;
}
