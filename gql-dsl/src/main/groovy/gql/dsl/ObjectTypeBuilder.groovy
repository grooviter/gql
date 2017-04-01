package gql.dsl

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull

/**
 * @since 0.1.0
 */
@SuppressWarnings('PropertyName')
class ObjectTypeBuilder {

  GraphQLObjectType.Builder type = GraphQLObjectType.newObject()

  GraphQLScalarType GraphQLInt = Scalars.GraphQLInt
  GraphQLScalarType GraphQLString = Scalars.GraphQLString
  GraphQLScalarType GraphQLLong = Scalars.GraphQLLong
  GraphQLScalarType GraphQLShort = Scalars.GraphQLShort
  GraphQLScalarType GraphQLByte = Scalars.GraphQLByte
  GraphQLScalarType GraphQLFloat = Scalars.GraphQLFloat
  GraphQLScalarType GraphQLBigInteger = Scalars.GraphQLBigInteger
  GraphQLScalarType GraphQLBigDecimal = Scalars.GraphQLBigDecimal
  GraphQLScalarType GraphQLBoolean = Scalars.GraphQLBoolean
  GraphQLScalarType GraphQLID = Scalars.GraphQLID
  GraphQLScalarType GraphQLChar = Scalars.GraphQLChar

  /**
   * @param name
   * @return
   * @since 0.1.0
   */
  ObjectTypeBuilder name(String name) {
    this.type = type.name(name)
    return this
  }

  /**
   *
   * @param description
   * @return
   * @since 0.1.0
   */
  ObjectTypeBuilder description(String description) {
    this.type = type.description(description)
    return this
  }

  /**
   *
   * @param interfaceType
   * @return
   * @since 0.1.0
   */
  ObjectTypeBuilder addInterface(GraphQLInterfaceType interfaceType) {
    this.type = type.withInterface(interfaceType)
    return this
  }

  /**
   * @since 0.1.0
   */
  ObjectTypeBuilder addField(GraphQLFieldDefinition fieldDefinition) {
    this.type = type.field(fieldDefinition)
    return this
  }

  /**
   *
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
   *
   * @param name
   * @param dsl
   * @return
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
   *
   * @param name
   * @param fieldType
   * @return
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
   * @since 0.1.0
   */
  GraphQLObjectType build() {
    return this.type.build()
  }

  /**
   * @since 0.1.0
   */
  static ObjectTypeBuilder newObject() {
    return new ObjectTypeBuilder()
  }

  /**
   *
   * @param type
   * @return
   * @since 0.1.0
   */
  static <T extends GraphQLOutputType> T nonNull(T type) {
    return new GraphQLNonNull(type)
  }

  /**
   * @param type
   * @return
   * @since 0.1.0
   */
  static <T extends GraphQLOutputType> T list(T type) {
    return new GraphQLList(type)
  }

  /**
   * @since 0.1.0
   */
  static class FieldBuilder {

    GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()

    /**
     * @since 0.1.0
     */
    FieldBuilder name(String name) {
      builder.name(name)
      return this
    }

    /**
     * @since 0.1.0
     */
    FieldBuilder description(String description) {
      builder.description(description)
      return this
    }

    /**
     * @since 0.1.0
     */
    FieldBuilder type(GraphQLScalarType type) {
      builder.type(type)
      return this
    }

    /**
     * @since 0.1.0
     */
    FieldBuilder type(GraphQLOutputType type) {
      builder.type(type)
      return this
    }

    /**
     * @since 0.1.0
     */
    FieldBuilder fetcher(Closure<Object> fetcher) {
      builder.dataFetcher(fetcher as DataFetcher)
      return this
    }

    /**
     * @since 0.1.0
     */
    FieldBuilder argument(String name, @DelegatesTo(GraphQLArgument) Closure dsl) {
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
     * @since 0.1.0
     */
    FieldBuilder fetcher(DataFetcher fetcher) {
      builder.dataFetcher(fetcher)
      return this
    }

    /**
     * @since 0.1.0
     */
    FieldBuilder staticValue(Object object) {
      builder.staticValue(object)
      return this
    }

    /**
     * @since 0.1.0
     */
    GraphQLFieldDefinition build() {
      return builder.build()
    }
  }

  /**
   * @since 0.1.0
   */
  static class InterfacesBuilder {

    /**
     * @since 0.1.0
     */
    List<GraphQLInterfaceType> interfaces = []

    /**
     * @since 0.1.0
     */
    InterfacesBuilder add(final GraphQLInterfaceType interfaceDefinition) {
      interfaces << interfaceDefinition
      return this
    }

    /**
     * @since 0.1.0
     */
    List<GraphQLInterfaceType> build() {
      return interfaces
    }
  }
}
