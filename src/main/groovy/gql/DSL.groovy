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
 * @since 0.1.0
 */
final class DSL {

  /**
   * Builds the estructure of a {@link GraphQLObjectType} using a closure
   * which delegates to {@link ObjectTypeBuilder}
   * <br/>
   * <pre><code class="groovy">
   * GraphQLObjectType helloQuery = DSL.type('helloWorldQuery') {
   *   field('hello') {
   *       type GraphQLString
   *       staticValue 'world'
   *   }
   * }
   * </code></pre>
   * <br/>
   * When specifying scalar types in a field it's not neccessary to
   * import basic scalar types, the DSL already knows basic scalar
   * types.
   *
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
   * <br/>
   * <pre><code class="groovy">
   * GraphQLSchema schema = DSL.schema {
   *   query('QueryRoot') {
   *     field('hello') {
   *         type GraphQLString
   *         staticValue 'world'
   *     }
   *   }
   * }
   * </code></pre>
   *
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
   * <br/>
   * <pre><code class="groovy">
   * GraphQLObjectType filmType = DSL.type('film') {
   *   field 'year' , GraphQLString
   *   field 'title', GraphQLString
   * }
   *
   * GraphQLSchema schema = DSL.schema {
   *   query('QueryRoot') {
   *     field('byYear') {
   *       type filmType
   *       fetcher Queries.&findLastFilm
   *       argument('year') {
   *         type GraphQLString
   *       }
   *     }
   *   }
   * }
   *
   * String queryString = '''
   *   query FinBondFilmByYear($year: String){
   *     byYear(year: $year) {
   *        title
   *        year
   *     }
   *   }
   * '''
   *
   * Map<String,Map> dataMap = DSL
   *  .execute(schema, queryString, [year: "1962"])
   *  .data
   * </code></pre>
   *
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
   * <br/>
   * <pre><code class="groovy"
   * DSL.execute(schema) {
   *   query('byYear', [year: 1962]) {
   *     returns(Film) { // typed properties
   *       title
   *       year
   *     }
   *   }
   *   query('lastFilm') {
   *     returns { // untyped properties
   *       title
   *     }
   *   }
   * }
   * </code></pre>
   *
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
