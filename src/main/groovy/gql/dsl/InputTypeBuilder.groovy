package gql.dsl

import graphql.schema.DataFetcher
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLScalarType

/**
 * Builds a new {@link graphql.schema.GraphQLInputObjectType}
 *
 * @since 0.1.4
 */
class InputTypeBuilder implements ScalarsAware {

  GraphQLInputObjectType.Builder type = GraphQLInputObjectType.newInputObject()

  /**
   * @param name
   * @return
   * @since 0.1.4
   */
  InputTypeBuilder name(String name) {
    this.type = type.name(name)
    return this
  }

  /**
   *
   * @param description
   * @return
   * @since 0.1.4
   */
  InputTypeBuilder description(String description) {
    this.type = type.description(description)
    return this
  }

  /**
   * @since 0.1.4
   */
  InputTypeBuilder addField(GraphQLInputObjectField fieldDefinition) {
    this.type = type.field(fieldDefinition)
    return this
  }

  /**
   *
   * @param name
   * @param dsl
   * @return
   * @since 0.1.4
   */
  InputTypeBuilder field(String name, @DelegatesTo(FieldBuilder) Closure dsl) {
    Closure<FieldBuilder> clos = dsl.clone() as Closure<FieldBuilder>
    FieldBuilder builderSource = new FieldBuilder()
      .name(name)
      .description("description of field $name")
    FieldBuilder builderResult = builderSource.with(clos) ?: builderSource

    this.type = type.field(builderResult.build())
    return this
  }

  /**
   *
   * @param name
   * @param fieldType
   * @return
   * @since 0.1.4
   */
  InputTypeBuilder field(String name, GraphQLInputType fieldType) {
    FieldBuilder builderSource = new FieldBuilder()
      .name(name)
      .description("description of field $name")
      .type(fieldType)

    this.type = type.field(builderSource.build())
    return this
  }

  /**
   *
   * @param name
   * @param fieldType
   * @return
   * @since 0.1.4
   */
  InputTypeBuilder field(String name, GraphQLScalarType fieldType) {
    FieldBuilder builderSource = new FieldBuilder()
      .name(name)
      .description("description of field $name")
      .type(fieldType)

    this.type = type.field(builderSource.build())
    return this
  }

  /**
   * @since 0.1.4
   */
  GraphQLInputObjectType build() {
    return this.type.build()
  }

  /**
   *
   * @param type
   * @return
   * @since 0.1.4
   */
  static <T extends GraphQLInputType> T nonNull(T type) {
    return new GraphQLNonNull(type)
  }

  /**
   * @param type
   * @return
   * @since 0.1.4
   */
  static <T extends GraphQLInputType> T list(T type) {
    return new GraphQLList(type)
  }

  /**
   * @since 0.1.4
   */
  static class FieldBuilder implements ScalarsAware {

    GraphQLInputObjectField.Builder builder = GraphQLInputObjectField.newInputObjectField()

    /**
     * @since 0.1.4
     */
    FieldBuilder name(String name) {
      builder.name(name)
      return this
    }

    /**
     * @since 0.1.4
     */
    FieldBuilder description(String description) {
      builder.description(description)
      return this
    }

    /**
     * @since 0.1.4
     */
    FieldBuilder type(GraphQLScalarType type) {
      builder.type(type)
      return this
    }

    /**
     * @since 0.1.4
     */
    FieldBuilder type(GraphQLInputType type) {
      builder.type(type)
      return this
    }

    /**
     * @since 0.1.4
     */
    GraphQLInputObjectField build() {
      return builder.build()
    }
  }
}
