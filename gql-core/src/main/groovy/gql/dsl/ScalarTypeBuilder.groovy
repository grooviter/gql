package gql.dsl

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType

/**
 * Builds a new {@link graphql.schema.GraphQLScalarType}. Apart from the name and description, a scalar type
 * is made out of three functions:
 * <ul>
 *     <li>serialize: serializes data coming from a fetcher result to a valid output</li>
 *     <li>parseValue: converts an incoming query input to a specific type</li>
 *     <li>parseLiteral: converts an incoming literal to a specific type</li>
 * </ul>
 *
 * @since 0.1.3
 */
class ScalarTypeBuilder {

  private static final Closure<?> ID = { value -> value }

  private String name
  private String description
  private Closure serialization = ID
  private Closure fromValue = ID
  private Closure fromLiteral = ID

  /**
   * Sets scalar type's name
   *
   * @param name scalar's name
   * @return current builder instance
   * @since 0.1.3
   */
  @SuppressWarnings('ConfusingMethodName')
  ScalarTypeBuilder name(String name) {
    this.name = name
    return this
  }

  /**
   * Sets scalar type's description
   *
   * @param description scalar description
   * @return current builder instance
   * @since 0.1.3
   */
  @SuppressWarnings('ConfusingMethodName')
  ScalarTypeBuilder description(String description) {
    this.description = description
    return this
  }

  /**
   * Sets the function that serializes data coming from fetcher/staticValue.
   *
   * @param serialization function that converts from fetcher/staticValue output to a valid client output
   * @return current builder instance
   * @since 0.1.3
   */
  ScalarTypeBuilder serialize(Closure serialization) {
    this.serialization = serialization
    return this
  }

  /**
   * Sets the function that parses a value from a query input to a specific type
   *
   * @param fromValue function converting a value from a query input to a specific type
   * @return current builder instance
   * @since 0.1.3
   */
  ScalarTypeBuilder parseValue(Closure fromValue) {
    this.fromValue = fromValue
    return this
  }

  /**
   * Sets the function that parses a literal from a query to a specific type
   *
   * @param fromLiteral
   * @return current builder instance
   * @since 0.1.3
   */
  ScalarTypeBuilder parseLiteral(Closure fromLiteral) {
    this.fromLiteral = fromLiteral
    return this
  }

  /**
   * Returns the {@link GraphQLScalarType} built by this builder
   *
   * @return an instance of {@link GraphQLScalarType}
   * @since 0.1.3
   */
  GraphQLScalarType build() {
    return new GraphQLScalarType(name, description ?: name, new Coercing() {
      @Override
      Object serialize(Object input) {
        return serialization.call(input)
      }

      @Override
      Object parseValue(Object input) {
        return fromValue.call(input)
      }

      @Override
      Object parseLiteral(Object input) {
        return fromLiteral.call(input)
      }
    })
  }
}
