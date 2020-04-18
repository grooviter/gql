package gql.relay

import gql.dsl.ArgumentBuilder
import gql.dsl.ObjectTypeBuilder
import graphql.relay.SimpleListConnection
import graphql.relay.Connection
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

/**
 * Builds a new field of type connection. A connection field have
 * always some optional fields aimed to make easier to paginate
 * over a collection (edges)
 *
 * @since 0.1.8
 */
class ConnectionFieldBuilder extends ObjectTypeBuilder.FieldBuilder {

  private static final String STRING_FIRST = 'first'
  private static final String STRING_LAST = 'last'

  /**
   * Argument used to indicate how many items we want to get going
   * forwards
   *
   * @since 0.1.8
   */
  @SuppressWarnings('PropertyName')
  final GraphQLArgument FIRST = new ArgumentBuilder(GraphQLInt)
    .name(STRING_FIRST)
    .description('take next number items')
    .build()

  /**
   * The `after` argument should be used along with the `first` argument
   * to indicate at which point should the cursor start going forwards
   *
   * @since 0.1.8
   */
  @SuppressWarnings('PropertyName')
  final GraphQLArgument AFTER = new ArgumentBuilder(GraphQLString)
    .name('after')
    .description('new items will be fetched after the item identified by this id')
    .build()

  /**
   * Argument used to indicate how many items we want to get going
   * backwards
   *
   * @since 0.1.8
   */
  @SuppressWarnings('PropertyName')
  final GraphQLArgument LAST = new ArgumentBuilder(GraphQLInt)
    .name(STRING_LAST)
    .description('take previous number of items')
    .build()

  /**
   * The `before` argument should be used along with the `last` argument
   * to indicate at which point should the cursor start going backwards
   *
   * @since 0.1.8
   */
  @SuppressWarnings('PropertyName')
  final GraphQLArgument BEFORE = new ArgumentBuilder(GraphQLString)
    .name('before')
    .description('new items will be fetched before the item identified by this id')
    .build()

  /**
   * This method tries to ease converting from a given result {@link List} to a {@link Connection} taking away from
   * the programer the burden of creating each edge node/cursor pair, and how to build the <b>pageInfo</b> field.
   * The programmer can now be focused on how to get the data instead of building the response structure.
   *
   * @relay <a target="_blank"
   *  href="https://facebook.github.io/relay/graphql/connections.htm">Relay
   *  Cursor Connections Specification</a>
   * @param closure the body of the {@link DataFetcher#get} method
   * @return the current {@link ConnectionBuilder} instance
   * @since 0.1.8
   */
  ConnectionFieldBuilder listFetcher(
    @ClosureParams(value = SimpleType, options = 'graphql.schema.DataFetchingEnvironment') Closure<List<?>> closure) {
    this.builder = builder.dataFetcher(connectionFetcher(closure))

    return this
  }

  private <T> DataFetcher<List<T>> connectionFetcher(
    @ClosureParams(value = SimpleType, options = 'graphql.schema.DataFetchingEnvironment') Closure<List<T>> closure) {
    return { DataFetchingEnvironment env ->
      Map<String,Object> newArguments = env
        .arguments
        .collectEntries(this.&convertEntry) as Map<String,Object>

      DataFetchingEnvironment newEnvironment = new DataFetchingEnvironmentImpl.Builder()
        .source(env.getSource())
        .arguments(newArguments)
        .context(env.getContext())
        .root(env.getRoot())
        .fieldDefinition(env.getFieldDefinition())
        .mergedField(env.getMergedField())
        .fieldType(env.getFieldType())
        .parentType(env.getParentType())
        .graphQLSchema(env.getGraphQLSchema())
        .fragmentsByName(env.getFragmentsByName())
        .executionId(env.getExecutionId())
        .executionStepInfo(env.getExecutionStepInfo())
        .build()

      List<T> data = closure(newEnvironment)

      return new SimpleListConnection<T>(data).get(env)
    } as DataFetcher<T>
  }

  private static Map<String,? extends Object> convertEntry(String key, Object value) {
    return (key in [STRING_FIRST, STRING_LAST] && value) ?
      [(key): ((Integer) value) + 1] :
      [(key): value]
  }

  @Override
  GraphQLFieldDefinition build() {
    builder = builder
      .argument(FIRST)
      .argument(LAST)
      .argument(AFTER)
      .argument(BEFORE)

    return super.build()
  }
}
