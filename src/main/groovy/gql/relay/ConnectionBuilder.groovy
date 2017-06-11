package gql.relay

import gql.DSL
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
  ConnectionBuilder edges(String name, @DelegatesTo(EdgesBuilder) Closure dsl) {
    Closure<EdgesBuilder> clos = dsl.clone() as Closure<EdgesBuilder>
    EdgesBuilder sourceBuilder = new EdgesBuilder().name(name) as EdgesBuilder
    EdgesBuilder resultBuilder = sourceBuilder.with(clos) ?: sourceBuilder

    GraphQLObjectType edgeType = resultBuilder.build()
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
