package gql.relay

import gql.dsl.ObjectTypeBuilder
import graphql.schema.GraphQLObjectType

/**
 * A `Node` type is just an instance of {@link GraphQLObjectType} that
 * must have an identifier. The current implementation of a node is
 * a {@link GraphQLObjectType} instance that has an id field.
 *
 * @since 0.1.8
 */
class NodeBuilder extends ObjectTypeBuilder implements RelayPrimitivesAware {

  /**
   * Declares a new connection field
   *
   * @param name the name of the connection type
   * @param dsl the definition of the connection field
   * @return the current builder instance
   * @since 0.1.8
   */
  NodeBuilder connection(String name, @DelegatesTo(ConnectionFieldBuilder) Closure dsl) {
    Closure<ConnectionFieldBuilder> clos = dsl.clone() as Closure<ConnectionFieldBuilder>
    ConnectionFieldBuilder sourceBuilder = new ConnectionFieldBuilder().name(name) as ConnectionFieldBuilder
    ConnectionFieldBuilder resultBuilder = sourceBuilder.with(clos) ?: sourceBuilder

    this.type.field(resultBuilder.build())

    return this
  }

  @Override
  GraphQLObjectType build() {
    return this.type
      .field(IdField)
      .build()
  }
}
