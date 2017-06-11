package gql.relay

import gql.DSL
import gql.dsl.SchemaBuilder
import graphql.schema.GraphQLInterfaceType
import graphql.schema.TypeResolver
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

/**
 * Builds a new GraphQL schema but adding to the schema context the basic
 * Relay primitives
 *
 * @since 0.1.8
 * @see RelayPrimitivesAware
 */
class RelaySchemaBuilder extends SchemaBuilder implements RelayPrimitivesAware {

  /**
   * Creates an instance of a {@link GraphQLInterfaceType} declaring what every Relay node should
   * implement
   *
   * @param typeResolver a closure that resolves which type the execution has to return
   * @return an instance of {@link GraphQLInterfaceType}
   * @since 0.1.8
   */
  GraphQLInterfaceType NodeInterface(
    @ClosureParams(value = SimpleType, options = 'graphql.TypeResolutionEnvironment') Closure typeResolver) {
    return DSL.interface('Node') {
      addField(IdField)
      resolver(typeResolver as TypeResolver)
    }
  }

  /**
   * Creates an instance of a {@link GraphQLInterfaceType} declaring what every Relay connection should
   * implement
   *
   * @param typeResolver a closure that resolves which type the execution has to return
   * @return an instance of {@link GraphQLInterfaceType}
   * @since 0.1.8
   */
  GraphQLInterfaceType ConnectionInterface(
    @ClosureParams(value = SimpleType, options = 'graphql.TypeResolutionEnvironment') Closure typeResolver) {
    return DSL.interface('Connection') {
      addField(PageInfoField)
      resolver(typeResolver as TypeResolver)
    }
  }
}
