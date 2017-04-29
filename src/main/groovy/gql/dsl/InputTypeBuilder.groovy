package gql.dsl

import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType

/**
 * Builds a new {@link graphql.schema.GraphQLInputObjectType}
 *
 * @since 0.1.4
 */
class InputTypeBuilder implements ScalarsAware, NonNullAware, ListAware {

  GraphQLInputObjectType.Builder type = GraphQLInputObjectType.newInputObject()

  /**
   * Sets the input type name
   *
   * @param name the type name
   * @return the current builder instance
   * @since 0.1.4
   */
  InputTypeBuilder name(String name) {
    this.type = type.name(name)
    return this
  }

  /**
   * Sets the input description
   *
   * @param description the type description
   * @return the current builder instance
   * @since 0.1.4
   */
  InputTypeBuilder description(String description) {
    this.type = type.description(description)
    return this
  }

  /**
   * Adds a new {@link GraphQLInputObjectField} to the resulting
   * input type
   *
   * @param fieldDefinition an instance of {@link GraphQLInputObjectField}
   * @return the current builder instance
   * @since 0.1.4
   */
  InputTypeBuilder addField(GraphQLInputObjectField fieldDefinition) {
    this.type = type.field(fieldDefinition)
    return this
  }

  /**
   * Adds a new {@link GraphQLInputObjectField} to the resulting
   * input type
   *
   * @param name the new field name
   * @param dsl the nested builder responsible for building an instance of {@link GraphQLInputObjectField}.
   * @return the current builder instance
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
   * Adds a new {@link GraphQLInputObjectField} to the resulting
   * input type
   *
   * @param name the field name
   * @param fieldType the new field type. It should be type of {@link GraphQLInputType}
   * @return the current builder instance
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
   * Returns a {@link GraphQLInputObjectType} built by this builder
   *
   * @return an instance of {@link GraphQLInputObjectType}
   * @since 0.1.4
   */
  GraphQLInputObjectType build() {
    return this.type.build()
  }

  /**
   * Builds an instance of {@link GraphQLInputObjectField}
   *
   * @since 0.1.4
   */
  static class FieldBuilder implements ScalarsAware {

    GraphQLInputObjectField.Builder builder = GraphQLInputObjectField.newInputObjectField()

    /**
     * Sets the field name
     *
     * @param name field name
     * @return an instance of current builder
     * @since 0.1.4
     */
    FieldBuilder name(String name) {
      builder.name(name)
      return this
    }

    /**
     * Sets the field description
     *
     * @param description field description
     * @return an instance of current builder
     * @since 0.1.4
     */
    FieldBuilder description(String description) {
      builder.description(description)
      return this
    }

    /**
     * Sets the field type
     *
     * @param an instance of current builder
     * @return an instance of current builder
     * @since 0.1.4
     */
    FieldBuilder type(GraphQLInputType type) {
      builder.type(type)
      return this
    }

    /**
     * Returns an instance of {@link GraphQLInputObjectField} built
     * by the current builder
     *
     * @return an instance of {@link GraphQLInputObjectField}
     * @since 0.1.4
     */
    GraphQLInputObjectField build() {
      return builder.build()
    }
  }
}
