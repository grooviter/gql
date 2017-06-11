package gql.relay

import gql.DSL
import gql.Relay
import gql.dsl.ObjectTypeBuilder
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType

/**
 * An edge represents a collection of items that can be paginated and it should have
 * information on how to paginate that collection
 *
 * @since 0.1.8
 */
class EdgesBuilder extends ObjectTypeBuilder {

  /**
   * Adds two fields to the edge type: node and cursor
   *
   * @param name the name of the node type
   * @param dsl the definition of the node added to the edge
   * @return the instance of the current builder
   * @see NodeBuilder
   * @since 0.1.8
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_adding_external_fields">Adding external fields</a>
   */
  EdgesBuilder node(String name, @DelegatesTo(NodeBuilder) Closure dsl) {
    GraphQLObjectType Node = Relay.node(name, dsl)

    GraphQLFieldDefinition nodeField = DSL.field('node') {
      description Node.description
      type Node
    }

    GraphQLFieldDefinition cursorField = DSL.field('cursor') {
      description ''
      type GraphQLString
    }

    addField nodeField
    addField cursorField
    return this
  }
}
