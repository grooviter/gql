package gql.relay

import gql.DSL
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType

/**
 * Basic primitives of a Relay implementation
 *
 * @since 0.1.8
 */
interface RelayPrimitivesAware {

  /**
   * Every node must have an id field
   *
   * @since 0.1.8
   */
  @SuppressWarnings('FieldName')
  static final GraphQLFieldDefinition IdField = DSL.field('id') {
    description 'The id of an object'
    type nonNull(GraphQLID)
  }

  /**
   * Pagination info normally used as a field in a connection
   *
   * @since 0.1.8
   */
  @SuppressWarnings('FieldName')
  static final GraphQLObjectType PageInfo = DSL.type('PageInfo') {
    description 'Information about pagination in a connection.'

    field('hasNextPage') {
      description 'When paginating forwards, are there more items?'
      type GraphQLBoolean
    }

    field('hasPreviousPage') {
      description 'When paginating backwards, are there more items?'
      type GraphQLBoolean
    }

    field('startCursor') {
      description 'When paginating backwards, the cursor to continue.'
      type GraphQLString
    }

    field('endCursor') {
      description 'When paginating forwards, the cursor to continue.'
      type GraphQLString
    }
  }

  /**
   * Page info field definition
   *
   * @since 0.1.8
   */
  @SuppressWarnings('FieldName')
  static final GraphQLFieldDefinition PageInfoField = DSL.field('pageInfo') {
    description 'Information about pagination in the current connection.'
    type nonNull(PageInfo)
  }
}
