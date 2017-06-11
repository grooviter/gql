package gql.dsl

import gql.DSL
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.TypeResolver

/**
 * Builds a new {@link GraphQLInterfaceType}
 *
 * @since 0.1.8
 */
class InterfaceBuilder implements ScalarsAware, NonNullAware, ListAware {

  /**
   * The underlying builder used to build the {@link GraphQLInterfaceType} instance
   *
   * @since 0.1.8
   */
  GraphQLInterfaceType.Builder builder = GraphQLInterfaceType.newInterface()

  /**
   * Sets the name of the interface type
   *
   * @param name the name of the interface type
   * @return an instance of the current builder
   * @since 0.1.8
   */
  InterfaceBuilder name(String name) {
    this.builder = builder.name(name)
    return this
  }

  /**
   * Sets the description of the interface type
   *
   * @param description the description of the interface type
   * @return an instance of the current builder
   * @since 0.1.8
   */
  InterfaceBuilder description(String description) {
    this.builder = builder.description(description)
    return this
  }

  /**
   * Adds a new field definition to the interface type
   *
   * @param fieldDefinition the new field added to the interface type
   * @return an instance of the current builder
   * @since 0.1.8
   */
  InterfaceBuilder addField(GraphQLFieldDefinition fieldDefinition) {
    this.builder = builder.field(fieldDefinition)
    return this
  }

  /**
   * Adds a new field definition to the interface type
   * @param name the name of the new field
   * @param dsl the dsl used to define the new field
   * @return an instance of the current builder
   * @see ObjectTypeBuilder.FieldBuilder
   * @since 0.1.8
   */
  InterfaceBuilder field(String name, @DelegatesTo(ObjectTypeBuilder.FieldBuilder) Closure dsl) {
    this.builder = builder.field(DSL.field(name, dsl))
    return this
  }

  InterfaceBuilder resolver(TypeResolver resolver) {
    this.builder = builder.typeResolver(resolver)
    return this
  }

  /**
   * Returns the instance of {@link GraphQLInterfaceType} built by this builder
   *
   * @return an instance of {@link GraphQLInterfaceType}
   * @since 0.1.8
   */
  GraphQLInterfaceType build() {
    return builder.build()
  }
}
