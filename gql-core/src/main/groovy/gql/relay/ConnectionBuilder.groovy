package gql.relay

import gql.DSL
import gql.Relay
import gql.dsl.ObjectTypeBuilder
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType

/**
 * A Connection is a way of manipulating one-to-many relationships easy,
 * using a standardized way of expressing these one-to-many relationships.
 * This standard connection model offers ways of slicing and paginating
 * through the connection.
 *
 * @since 0.1.8
 */
class ConnectionBuilder extends ObjectTypeBuilder implements RelayPrimitivesAware {

  /**
   * The edges represent a collection of nodes
   *
   * @param name the name of the collection of nodes
   * @param dsl the definition of the collection items
   * @return and instance of the current {@link ConnectionBuilder}
   * @since 0.1.8
   */
  ConnectionBuilder edges(String name, @DelegatesTo(NodeBuilder) Closure dsl) {
    GraphQLObjectType Node = Relay.node(name, dsl)
    GraphQLFieldDefinition nodeField = DSL.field('node') {
      description Node.description
      type Node
    }

    GraphQLFieldDefinition cursorField = DSL.field('cursor') {
      description ''
      type GraphQLString
    }

    GraphQLObjectType edgeType = new ObjectTypeBuilder()
       .name("${name}Edge")
       .addField(nodeField)
       .addField(cursorField)
       .build()

    GraphQLFieldDefinition edgesField = DSL.field('edges') {
      type list(edgeType)
    }

    addField(edgesField)
    return this
  }

  /**
   * Adds a <b>PageInfoField</b> field to build a {@link GraphQLObjectType}
   *
   * @return an instance of {@link GraphQLObjectType}
   * @see RelayPrimitivesAware#PageInfoField
   * @since 0.1.8
   */
  @Override
  GraphQLObjectType build() {
    addField(PageInfoField)

    return super.build()
  }
}
