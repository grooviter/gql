package gql.dsl

import static graphql.schema.GraphQLEnumType.Builder
import static graphql.schema.GraphQLEnumType.newEnum

import graphql.schema.GraphQLEnumType
import groovy.transform.TupleConstructor

/**
 * Builds a {@link GraphQLEnumType}
 *
 * @since 0.1.3
 */
class EnumTypeBuilder {

  /**
   * Represents every enum type key/value pair
   *
   * @since 0.1.3
   */
  @TupleConstructor
  static class Entry {
    String name
    Object value
  }

  /**
   * Enum type name
   *
   * @since 0.1.3
   */
  String name

  /**
   * Enum type description
   *
   * @since 0.1.3
   */
  String description

  /**
   * Enum type key/value entries
   *
   * @since 0.1.3
   */
  List<Entry> entries = []

  /**
   * Defines a new enum entry
   *
   * @param key entry key
   * @param value entry value
   * @return current enum type builder
   * @since 0.1.3
   */
  EnumTypeBuilder value(String key, Object value) {
    this.entries << new Entry(key, value)
    return this
  }

  /**
   * Adds enum type name
   *
   * @param name enum type name
   * @return current enum type builder
   * @since 0.1.3
   */
  @SuppressWarnings('ConfusingMethodName')
  EnumTypeBuilder name(String name) {
    this.name = name
    return this
  }

  /**
   * Adds enum type description
   *
   * @param description enum type description
   * @return current enum type builder
   * @since 0.1.3
   */
  @SuppressWarnings('ConfusingMethodName')
  EnumTypeBuilder description(String description) {
    this.description = description
    return this
  }

  /**
   * Returns the built {@link GraphQLEnumType}
   *
   * @return an instance of {@link GraphQLEnumType}
   * @since 0.1.3
   */
  GraphQLEnumType build() {
    Builder source = newEnum()
      .name(name)
      .description(description ?: name)

    Builder result = entries.inject(source, this.&processEntry)

    return result.build()
  }

  private Builder processEntry(Builder builder, Entry entry) {
    return builder.value(entry.name, entry.value)
  }
}
