package gql.dsl

import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType

/**
 * Builds a new {@link GraphQLObjectType}
 *
 * @since 0.1.4
 */
@SuppressWarnings('PropertyName')
class ObjectTypeBuilder implements ScalarsAware, NonNullAware, ListAware {

  GraphQLObjectType.Builder type = GraphQLObjectType.newObject()

  /**
   * Sets the type name
   *
   * @param name the type name
   * @return the current builder instance
   * @since 0.1.0
   */
  ObjectTypeBuilder name(String name) {
    this.type = type.name(name)
    return this
  }

  /**
   * Sets the type description
   *
   * @param description the type description
   * @return the current builder instance
   * @since 0.1.0
   */
  ObjectTypeBuilder description(String description) {
    this.type = type.description(description)
    return this
  }

  /**
   * Adds a new {@link GraphQLInterfaceType} to the resulting
   * {@GraphQLObjectType}
   *
   * @param interfaceType the interface type added
   * @return the current builder instance
   * @since 0.1.0
   */
  ObjectTypeBuilder addInterface(GraphQLInterfaceType interfaceType) {
    this.type = type.withInterface(interfaceType)
    return this
  }

  /**
   * Adds a new field to the resulting {@link GraphQLObjectType} instance
   *
   * @param fieldDefinition the field definition
   * @return the current builder instance
   * @since 0.1.0
   */
  ObjectTypeBuilder addField(GraphQLFieldDefinition fieldDefinition) {
    this.type = type.field(fieldDefinition)
    return this
  }

  /**
   * A dsl to add interfaces to the curren type
   *
   * @param interfaces a dsl used to add more interfaces to resulting type
   * @return the current builder instance
   * @since 0.1.0
   */
  ObjectTypeBuilder interfaces(@DelegatesTo(InterfacesBuilder) Closure interfaces) {
    Closure<InterfacesBuilder> clos = interfaces.clone() as Closure<InterfacesBuilder>
    InterfacesBuilder builderSource = new InterfacesBuilder()
    InterfacesBuilder builderResult = builderSource.with(clos) ?: builderSource

    this.type = type.withInterfaces(builderResult.build() as GraphQLInterfaceType[])
    return this
  }

  /**
   * Adds a new field to the resulting {@link GraphQLObjectType} using the dsl
   * passed as second parameter
   *
   * @param name the field name
   * @param dsl the nested builder responsible to build a new {@link GraphQLObjectType}
   * @return the current builder instance
   * @since 0.1.0
   */
  ObjectTypeBuilder field(String name, @DelegatesTo(FieldBuilder) Closure dsl) {
    Closure<FieldBuilder> clos = dsl.clone() as Closure<FieldBuilder>
    FieldBuilder builderSource = new FieldBuilder()
      .name(name)
      .description("description of field $name")
    FieldBuilder builderResult = builderSource.with(clos) ?: builderSource

    this.type = type.field(builderResult.build())
    return this
  }

  /**
   * Adds a new field, of type {@link GraphQLFieldDefinition} to the resulting {@link GraphQLObjectType}
   *
   * @param name the field name
   * @param fieldType the type of the field
   * @return the current builder instance
   * @since 0.1.0
   */
  ObjectTypeBuilder field(String name, GraphQLOutputType fieldType) {
    FieldBuilder builderSource = new FieldBuilder()
      .name(name)
      .description("description of field $name")
      .type(fieldType)

    this.type = type.field(builderSource.build())
    return this
  }

  /**
   * Returns the resulting {@link GraphQLObjectType}
   *
   * @since 0.1.0
   */
  GraphQLObjectType build() {
    return this.type.build()
  }

  /**
   * Builder that makes possible to create a field of type
   * {@link GraphQLFieldDefinition}
   *
   * @since 0.1.0
   */
  static class FieldBuilder implements ScalarsAware {

    GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()

    /**
     * Sets the field name
     *
     * @param name the field name
     * @return current builder
     * @since 0.1.0
     */
    FieldBuilder name(String name) {
      builder.name(name)
      return this
    }

    /**
     * Sets field description
     *
     * @param description the type description
     * @return current builder instance
     * @since 0.1.0
     */
    FieldBuilder description(String description) {
      builder.description(description)
      return this
    }

    /**
     * Sets field type
     *
     * @param type the type's type
     * @return current builder instance
     * @since 0.1.0
     */
    FieldBuilder type(GraphQLOutputType type) {
      builder.type(type)
      return this
    }

    /**
     * Sets field fetcher
     *
     * @param fetcher the type's fetcher
     * @return the current builder instance
     * @since 0.1.0
     */
    FieldBuilder fetcher(Closure<?> fetcher) {
      builder.dataFetcher(fetcher as DataFetcher)
      return this
    }

    /**
     * Adds a new argument to the field
     *
     * @param name the name of the argument
     * @param dsl the nested builder to build a new {@link GraphQLArgument}
     * @return current builder instance
     * @since 0.1.0
     */
    FieldBuilder argument(String name, @DelegatesTo(value = GraphQLArgument.Builder, strategy = Closure.DELEGATE_FIRST) Closure dsl) {
      Closure<GraphQLArgument.Builder> clos = dsl.clone() as Closure<GraphQLArgument.Builder>
      GraphQLArgument.Builder builderSource = GraphQLArgument
        .newArgument()
        .name(name)
        .description(name)

      GraphQLArgument.Builder builderResult = builderSource.with(clos) ?: builderSource

      builder.argument(builderResult.build())
      return this
    }

    /**
     * Adds field fetcher. Fetcher is responsible to retrieve this field data.
     * It will be of type {@link DataFetcher}
     *
     * @param fetcher an instance of {@link DataFetcher}
     * @return current builder instance
     * @since 0.1.0
     */
    FieldBuilder fetcher(DataFetcher fetcher) {
      builder.dataFetcher(fetcher)
      return this
    }

    /**
     * Sets an static value returned by the field built by this
     * builder
     *
     * @param object static value
     * @return current builder instance
     * @since 0.1.0
     */
    FieldBuilder staticValue(Object object) {
      builder.staticValue(object)
      return this
    }

    /**
     * Returns the resulting {@link GraphQLFieldDefinition} built
     * by this builder
     *
     * @return an instance of {@link GraphQLFieldDefinition}
     * @since 0.1.0
     */
    GraphQLFieldDefinition build() {
      return builder.build()
    }
  }

  /**
   * Builder to aggregate a given type object's interfaces
   *
   * @since 0.1.0
   */
  static class InterfacesBuilder {

    /**
     * Interfaces of the current instance type
     *
     * @since 0.1.0
     */
    List<GraphQLInterfaceType> interfaces = []

    /**
     * Adds an interfaces to the resulting field
     *
     * @param interfaceDefinition the interface definition added
     * @return current builder instance
     * @since 0.1.0
     */
    InterfacesBuilder add(final GraphQLInterfaceType interfaceDefinition) {
      interfaces << interfaceDefinition
      return this
    }

    /**
     * Returns a list of interface definitions
     *
     * @return a list of {@link GraphQLInterfaceType}
     * @since 0.1.0
     */
    List<GraphQLInterfaceType> build() {
      return interfaces
    }
  }
}
