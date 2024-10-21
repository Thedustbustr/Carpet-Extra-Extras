package net.thedustbuster.util.checked;

@FunctionalInterface
public interface CheckedSupplier<T> {
  T get() throws Exception;
}
