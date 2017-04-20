package gql

import gql.dsl.QueryBuilder
import gql.dsl.SchemaBuilder
import gql.dsl.ObjectTypeBuilder

import graphql.GraphQL
import graphql.ExecutionResult
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLObjectType

/**
 * Functions in this class ease the creation of different GraphQL
 * schema elements
 *
 * @examples <a target="_blank" href="/docs/index.html#_dsl">Using GQL DSL</a>
 * @since 0.1.0
 */
final class DSL {

  /**
   * Builds the estructure of a {@link GraphQLObjectType} using a closure
   * which delegates to {@link ObjectTypeBuilder}
   * <br/>
   * When specifying scalar types in a field it's not neccessary to
   * import basic scalar types, the DSL already knows basic scalar
   * types.
   *
   * @examples <a target="_blank" href="/docs/index.html#_types">Creating GraphQL types</a>
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
   * @examples <a target="_blank" href="/docs/index.html#_schemas">Creating GraphQL schemas</a>
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
   * @examples <a target="_blank" href="/docs/index.html#_queries">Executing GraphQL queries</a>
   * @param schema the schema defining the queryString
   * @param query the query string
   * @param
   * @return an instance of {@link ExecutionResult}
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
   * Builds GraphQL queries top wrapper
   *
   * @examples <a target="_blank" href="/docs/index.html#_queries">Executing GraphQL queries</a>
   * @param variables variables used in nested queries
   * @param queries closure wrapping different queries to be executed remotely
   * @return a map with all the response
   * @since 0.1.0
   */
  static ExecutionResult execute(GraphQLSchema schema, @DelegatesTo(QueryBuilder) Closure queries) {
    return execute(schema, buildQuery(queries))
  }

  /**
   * Builds a valid GraphQL query string
   *
   * @examples <a target="_blank" href="/docs/index.html#_query_string">Building GraphQL queries</a>
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
}
