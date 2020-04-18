package gql

import gql.dsl.EnumTypeBuilder
import gql.dsl.ExecutionBuilder
import gql.dsl.GraphQLErrorBuilder
import gql.dsl.InputTypeBuilder
import gql.dsl.InterfaceBuilder
import gql.dsl.MutationBuilder
import gql.dsl.ObjectTypeBuilder
import gql.dsl.QueryBuilder
import gql.dsl.ScalarTypeBuilder
import gql.dsl.SchemaBuilder
import gql.dsl.SchemaMergerBuilder
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphQLError
import graphql.execution.ExecutionPath
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema
import graphql.schema.TypeResolver
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

import java.util.concurrent.CompletableFuture

/**
 * Functions in this class ease the creation of different GraphQL
 * schema elements
 *
 * @examples <a target="_blank" href="/gql/docs/html5/index.html#_dsl">Using GQL DSL</a>
 * @since 0.1.0
 */
final class DSL {

  /**
   * Builds the structure of a {@link GraphQLObjectType} using a closure
   * which delegates to {@link ObjectTypeBuilder}
   * <br/>
   * When specifying scalar types in a field it's not neccessary to
   * import basic scalar types, the DSL already knows basic scalar
   * types.
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_types">Creating GraphQL types</a>
   * @param name name of the object to build
   * @param dsl dsl delegating its calls to an instance of type {@link
   * ObjectTypeBuilder}
   * @return an instance of {@link GraphQLObjectType}
   * @since 0.1.0
   */
  static GraphQLObjectType type(String name, @DelegatesTo(value = ObjectTypeBuilder) Closure<ObjectTypeBuilder> dsl) {
    Closure<ObjectTypeBuilder> closure = dsl.clone() as Closure<ObjectTypeBuilder>
    ObjectTypeBuilder builderSource = new ObjectTypeBuilder().name(name).description("description of $name")
    ObjectTypeBuilder builderResult = builderSource.with(closure) ?: builderSource

    return builderResult.build()
  }

  /**
   * Builds a new {@link GraphQLSchema} using a closure which
   * delegates to {@link SchemaBuilder}
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_schemas">Creating GraphQL schemas</a>
   * @param dsl closure with the schema elements. It should follow
   * rules of {@link SchemaBuilder}
   * @return an instance of {@link SchemaBuilder}
   * @since 0.1.0
   */
  static GraphQLSchema schema(@DelegatesTo(SchemaBuilder) Closure<SchemaBuilder> dsl) {
    Closure<SchemaBuilder> closure = dsl.clone() as Closure<SchemaBuilder>
    SchemaBuilder builderSource = new SchemaBuilder()
    SchemaBuilder builderResult = builderSource.with(closure) ?: builderSource

    return builderResult.build()
  }

  /**
   * Executes the queryString against the underlying schema without any
   * specific context.
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_queries">Executing GraphQL queries</a>
   * @param schema the schema defining the queryString
   * @param query the query string
   * @param arguments optional arguments passed to the query
   * @return an instance of {@link ExecutionResult}
   * @deprecated it will be removed in version 1.0.0
   * @since 0.1.0
   */
  static ExecutionResult execute(GraphQLSchema schema, String query, Map<String,Object> arguments = [:]) {
    GraphQL graphQL = new GraphQL(schema)

    /* GraphQL java assumes arguments can't be empty if you're using
       the method that allows arguments */
    return arguments ?
      graphQL.execute(query, null as DataFetchingEnvironment, arguments) :
      graphQL.execute(query, null)
  }

  /**
   * Builds a GraphQL query using a DSL and execute it
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_queries">Executing GraphQL queries</a>
   * @param variables variables used in nested queries
   * @param queries closure wrapping different queries to be executed remotely
   * @return a map with all the response
   * @deprecated it will be removed in version 1.0.0
   * @since 0.1.0
   */
  static ExecutionResult execute(GraphQLSchema schema, @DelegatesTo(QueryBuilder) Closure queries) {
    return execute(schema, buildQuery(queries))
  }

  /**
   * Executes a given query against a given schema
   *
   * @param schema schema the query will be executed against
   * @param query the GraphQL query string
   * @param builder builder to add extra information to the execution context
   * @return an instance of {@link ExecutionResult result} with the result of executing the query
   * @deprecated it will be removed in version 1.0.0
   * @since 0.3.0
   */
  static ExecutionResult execute(GraphQLSchema schema, String query, @DelegatesTo(ExecutionBuilder) Closure builder) {
    Closure<ExecutionBuilder> clos = builder.clone() as Closure<ExecutionBuilder>
    ExecutionBuilder builderSource = new ExecutionBuilder()
    ExecutionBuilder builderResult = builderSource.with(clos) ?: builderSource
    ExecutionBuilder.Result result = builderResult.withQuery(query).build()

    return GraphQL
      .newGraphQL(schema)
      .instrumentation(result.instrumentation)
      .build()
      .execute(result.input)
  }

  /**
   * Executes the queryString asynchronously against the underlying
   * schema without any specific context.
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_queries">Executing GraphQL queries</a>
   * @param schema the schema defining the queryString
   * @param query the query string
   * @param arguments optional arguments passed to the query
   * @return an instance of {@link CompletableFuture} that will be resolved in a {@link ExecutionResult}
   * @deprecated it will be removed in version 1.0.0
   * @since 0.1.9
   */
  static CompletableFuture<ExecutionResult> executeAsync(
    GraphQLSchema schema, String query, Map<String,Object> arguments = [:]) {
    GraphQL graphQL = new GraphQL(schema)
    ExecutionInput executionInput = ExecutionInput
      .newExecutionInput()
      .query(query)
      .variables(arguments)
      .build()

    return graphQL.executeAsync(executionInput)
  }

  /**
   * Executes a GraphQL query using a DSL and execute it asynchronously
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_queries">Executing GraphQL queries</a>
   * @param variables variables used in nested queries
   * @param queries closure wrapping different queries to be executed remotely
   * @return a map with all the response
   * @deprecated it will be removed in version 1.0.0
   * @since 0.1.9
   */
  static CompletableFuture<ExecutionResult> executeAsync(
    GraphQLSchema schema, @DelegatesTo(QueryBuilder) Closure queries) {
    return executeAsync(schema, buildQuery(queries))
  }

  /**
   * Executes a GraphQL query using a DSL and execute it asynchronously
   *
   * @param schema schema the query will be executed against
   * @param query the GraphQL query string
   * @param builder builder to add extra information to the execution context
   * @deprecated it will be removed in version 1.0.0
   * @since 0.3.0
   */
  static CompletableFuture<ExecutionResult> executeAsync(
    GraphQLSchema schema, String query, @DelegatesTo(ExecutionBuilder) Closure builder) {
    Closure<ExecutionBuilder> clos = builder.clone() as Closure<ExecutionBuilder>
    ExecutionBuilder builderSource = new ExecutionBuilder()
    ExecutionBuilder builderResult = builderSource.with(clos) ?: builderSource
    ExecutionBuilder.Result result = builderResult.withQuery(query).build()

    return GraphQL
      .newGraphQL(schema)
      .instrumentation(result.instrumentation)
      .build()
      .executeAsync(result.input)
  }

  /**
   * Builds a valid GraphQL query string
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_query_string">Building GraphQL queries</a>
   * @param builder DSL building the query based on {@link QueryBuilder}
   * @return a {@link String} containing a valid GraphQL query
   * @since 0.1.0
   */
  static String buildQuery(@DelegatesTo(QueryBuilder) Closure builder) {
    Closure<QueryBuilder> clos = builder.clone() as Closure<QueryBuilder>
    QueryBuilder builderSource = new QueryBuilder()
    QueryBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * Builds a valid GraphQL mutation string
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_mutation_string">Building GraphQL mutations</a>
   * @param builder builder DSL building the mutation based on {@link MutationBuilder}
   * @return a {@link String} containing a valid GraphQL mutation
   * @since 0.3.4
   */
  static String buildMutation(@DelegatesTo(MutationBuilder) Closure builder) {
    Closure<MutationBuilder> clos = builder.clone() as Closure<MutationBuilder>
    MutationBuilder builderSource = new MutationBuilder()
    MutationBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * Builds an instance of {@link GraphQLFieldDefinition}. You may want to create an standalone field
   * definition to reuse that field, or maybe just to have more flexibility when creating your schema.
   *
   * @examples <a target="_blank" href="/gql/docs/html5/index.html#_adding_external_fields">Adding external fields</a>
   * @param name the name of the field
   * @return an instance of {@link GraphQLFieldDefinition}
   * @since 0.1.1
   */
  static GraphQLFieldDefinition field(String name, @DelegatesTo(ObjectTypeBuilder.FieldBuilder) Closure dsl) {
    def type = type('MockType') {
      delegate.field(name, dsl)
    }

    return type.getFieldDefinition(name)
  }

  /**
   * Builds an enumeration type. Enumeration types are a special kind of type that is restricted to a particular
   * set of allowed values.
   *
   * @param name name of the enumeration type
   * @param builder builder to create a {@link GraphQLEnumType}
   * @since 0.1.3
   */
  static GraphQLEnumType 'enum'(String name, @DelegatesTo(EnumTypeBuilder) Closure builder) {
    Closure<EnumTypeBuilder> clos = builder.clone() as Closure<EnumTypeBuilder>
    EnumTypeBuilder builderSource = new EnumTypeBuilder().name(name)
    EnumTypeBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * Builds an interface type.
   *
   * @param name the name of the interface type
   * @param builder the inner dsl responsible for defining the interface
   * @return an instance of {@link GraphQLInterfaceType}
   * @see InterfaceBuilder
   * @since 0.1.8
   */
  static GraphQLInterfaceType 'interface'(String name, @DelegatesTo(InterfaceBuilder) Closure builder) {
    Closure<InterfaceBuilder> clos = builder.clone() as Closure<InterfaceBuilder>
    InterfaceBuilder sourceBuilder = new InterfaceBuilder().name(name)
    InterfaceBuilder resultBuilder = sourceBuilder.with(clos) ?: sourceBuilder

    return resultBuilder.build()
  }

  /**
   * Builds a scalar type.
   *
   * @param name of the scalar type
   * @param builder builder holding scalar constructs
   * @return an instance of {@link GraphQLScalarType}
   * @since 0.1.3
   */
  static GraphQLScalarType scalar(String name, @DelegatesTo(ScalarTypeBuilder) Closure builder) {
    Closure<ScalarTypeBuilder> clos = builder.clone() as Closure<ScalarTypeBuilder>
    ScalarTypeBuilder builderSource = new ScalarTypeBuilder().name(name)
    ScalarTypeBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * Builds an input type
   *
   * @param name name of the input type
   * @param builder builder to create a {@link GraphQLInputObjectType}
   * @return an instance of {@link GraphQLInputObjectType}
   * @since 0.1.4
   */
  static GraphQLInputObjectType input(String name, @DelegatesTo(InputTypeBuilder) Closure dsl) {
    Closure<InputTypeBuilder> clos = dsl.clone() as Closure<InputTypeBuilder>
    InputTypeBuilder builderSource = new InputTypeBuilder().name(name)
    InputTypeBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * It merges a series of GraphQL schemas and their respective data fetchers
   * into one only schema.
   *
   * @param dsl DSL to define which schemas are going to be merged into one
   * @return a {@link GraphQLSchema} as the result of merging several IDL
   * schema definitions
   * @since 0.1.7
   */
  static GraphQLSchema mergeSchemas(@DelegatesTo(SchemaMergerBuilder) Closure dsl) {
    Closure<SchemaMergerBuilder> clos = dsl.clone() as Closure<SchemaMergerBuilder>
    SchemaMergerBuilder builderSource = new SchemaMergerBuilder()
    SchemaMergerBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * Creates an instance of {@link TypeResolver}. {@link TypeResolver} only has one method, the closure
   * passed as parameter will act as an implementation of an instance of that type.
   * <br/>
   * The only valid parameter for that closure must be of type {@link graphql.TypeResolutionEnvironment}.
   *
   * @return an instance of {@link TypeResolver}
   * @since 0.1.8
   */
  static TypeResolver typeResolver(
    @ClosureParams(value = SimpleType, options = 'graphql.TypeResolutionEnvironment') Closure typeResolverClosure) {
    return typeResolverClosure as TypeResolver
  }

  /**
   * Builds a new instance of type {@link GraphQLError}
   *
   * @param options different options for a {@link GraphQLError} instance
   * @return an instance of {@link GraphQLError}
   * @since 0.3.0
   */
  static GraphQLError error(@DelegatesTo(GraphQLErrorBuilder) Closure options) {
    Closure<GraphQLErrorBuilder> clos = options.clone() as Closure<GraphQLErrorBuilder>
    GraphQLErrorBuilder builderSource = new GraphQLErrorBuilder()
    GraphQLErrorBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * When instrumenting a {@link DataFetcher} via {@link
   * Instrumentation#instrumentDataFetcher} you should return whether
   * the current data fetcher with no changes or a modified data fetcher.
   *
   * Sometimes you may want to return an error as a result of a given
   * condition. This method creates an instance of a {@link
   * DataFetcher} which sets a {@link GraphQLError} to the current
   * execution context and returns no data
   *
   * @param parameters current instrumentation parameters
   * @param options used to create the {@link GraphQLError}
   * @return an instance of {@link DataFetcher}
   * @since 0.3.0
   */
  static DataFetcher<?> errorFetcher(
    InstrumentationFieldFetchParameters parameters,
    @DelegatesTo(GraphQLErrorBuilder) Closure options) {
    final GraphQLError error = error(options)

    return { DataFetchingEnvironment env ->
      ExecutionPath path = parameters
        .getEnvironment()
        .getExecutionStepInfo()
        .getPath()

      parameters
        .getExecutionContext()
        .addError(error, path)

      return null
    } as DataFetcher<?>
  }
}
