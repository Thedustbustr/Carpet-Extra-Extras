package net.thedustbuster.util.func.checked;

@FunctionalInterface
public interface CheckedSupplier<T> {
  T get() throws Exception;
}
