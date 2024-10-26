package gql.extensions

import graphql.GraphQLContext
import groovy.transform.TupleConstructor

@TupleConstructor
class GraphQLContextMap implements Map<String, Object> {
  private static final RuntimeException NOT_IMPLEMENTED = new RuntimeException("map function not implemented")

  GraphQLContext context
  @Override
  int size() {
    return context.stream().count()
  }

  @Override
  boolean isEmpty() {
    return context.stream().count() == 0
  }

  @Override
  boolean containsKey(Object key) {
    return context.hasKey(key)
  }

  @Override
  boolean containsValue(Object value) {
    throw NOT_IMPLEMENTED
  }

  @Override
  Object get(Object key) {
    return context.get(key)
  }

  @Override
  Object put(String key, Object value) {
    throw NOT_IMPLEMENTED
  }

  @Override
  Object remove(Object key) {
    throw NOT_IMPLEMENTED
  }

  @Override
  void putAll(Map<? extends String, ?> m) {
    throw NOT_IMPLEMENTED
  }

  @Override
  void clear() {
    throw NOT_IMPLEMENTED
  }

  @Override
  Set<String> keySet() {
    throw NOT_IMPLEMENTED
  }

  @Override
  Collection<Object> values() {
    throw NOT_IMPLEMENTED
  }

  @Override
  Set<Entry<String, Object>> entrySet() {
    throw NOT_IMPLEMENTED
  }
}
