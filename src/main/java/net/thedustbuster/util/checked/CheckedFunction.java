package net.thedustbuster.util.checked;

@FunctionalInterface
public interface CheckedFunction<T, S> {
  S apply(T t) throws Exception;
}
