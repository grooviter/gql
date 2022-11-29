package gql

import gql.dsl.SchemaBuilder
import gql.relay.ConnectionBuilder
import gql.relay.NodeBuilder
import gql.relay.RelaySchemaBuilder
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema

/**
 * DSL for building Relay abstractions
 *
 * @since 0.1.8
 */
class Relay {

  /**
   * Builds a new {@link GraphQLSchema} using a closure which
   * delegates to {@link SchemaBuilder}
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_schemas">Creating GraphQL schemas</a>
   * @param dsl closure with the schema elements. It should follow
   * rules of {@link SchemaBuilder}
   * @return an instance of {@link SchemaBuilder}
   * @since 0.1.8
   */
  static GraphQLSchema schema(@DelegatesTo(RelaySchemaBuilder) Closure dsl) {
    Closure<RelaySchemaBuilder> closure = dsl.clone() as Closure<RelaySchemaBuilder>
    RelaySchemaBuilder builderSource = new RelaySchemaBuilder()
    RelaySchemaBuilder builderResult = builderSource.with(closure) ?: builderSource

    return builderResult.build()
  }

  /**
   * Builds a new Relay node
   *
   * @param name the name of the node type
   * @param dsl the definition of the node. It follows the contract defined in {@link NodeBuilder}
   * @return an instance of {@link GraphQLObjectType} with the characteristics of a Relay node
   * @since 0.1.8
   */
  static GraphQLObjectType node(String name, @DelegatesTo(NodeBuilder) Closure dsl) {
    Closure<NodeBuilder> clos = dsl.clone() as Closure<NodeBuilder>
    NodeBuilder sourceBuilder = new NodeBuilder().name(name) as NodeBuilder
    NodeBuilder resultBuilder = sourceBuilder.with(clos) ?: sourceBuilder

    return resultBuilder.build()
  }

  /**
   * Builds a new Relay connection
   *
   * @param name the name of the connection type
   * @param dsl the definition of the connection. It follows the contract defined in {@link ConnectionBuilder}
   * @return an instance of {@link GraphQLObjectType} with the characteristics of a Relay connection
   * @since 0.1.8
   */
  static GraphQLObjectType connection(String name, @DelegatesTo(ConnectionBuilder) Closure dsl) {
    Closure<ConnectionBuilder> clos = dsl.clone() as Closure<ConnectionBuilder>
    ConnectionBuilder sourceBuilder = new ConnectionBuilder().name(name) as ConnectionBuilder
    ConnectionBuilder resultBuilder = sourceBuilder.with(clos) ?: sourceBuilder

    return resultBuilder.build()
  }
}
