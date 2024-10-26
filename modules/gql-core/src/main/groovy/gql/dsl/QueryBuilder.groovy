package gql.dsl

import groovy.transform.InheritConstructors

import static groovy.lang.Closure.DELEGATE_FIRST

import gql.DSL
import gql.dsl.query.ReturnsBlockBuilder
import gql.dsl.query.VariablesProcessor

/**
 * Builder aimed to create a compliant GraphQL queryString using
 * a static typed DSL
 *
 * @see {@link DSL#buildQuery}
 * @since 0.1.0
 */
@InheritConstructors
class QueryBuilder extends Leveled {

  /**
   * Queries that should appear at the same level
   *
   * @since 1.1.0
   */
  protected List<String> queries = []

  /**
   * Builds a GraphQL queryString with a given name. The class passed
   * as parameter helps the compiler to know which fields are
   * available for the returning type.
   *
   * @param name name of the queryString as it is defined in the schema
   * @param clazz return type to help the compiler to know which fields are available
   * @param fields fields to return in the response
   * @return an instance of {@link QueryBuilder}
   * @since 0.1.0
   */
  QueryBuilder query(
    String name,
    @DelegatesTo(strategy = DELEGATE_FIRST, value = ReturnsBlockBuilder) Closure fields) {
    return this.query(name, [:], fields)
  }

  /**
   * Builds a GraphQL queryString with a given name. The class passed
   * as parameter helps the compiler to know which fields are
   * available for the returning type.
   *
   * @param name name of the queryString as it is defined in the schema
   * @param clazz return type to help the compiler to know which fields are available
   * @param fields fields to return in the response
   * @return an instance of {@link QueryBuilder}
   * @since 0.1.0
   */
  QueryBuilder query(
    String name,
    Map<String,?> variables,
    @DelegatesTo(strategy = DELEGATE_FIRST, value = ReturnsBlockBuilder) Closure fields) {
    String variablesString = processVariables(variables)
    ReturnsBlockBuilder builderSource =
      new ReturnsBlockBuilder(name: name, variables: variablesString, level: level + 1)
    Closure<ReturnsBlockBuilder> clos = fields.clone() as Closure<ReturnsBlockBuilder>
    ReturnsBlockBuilder builderResult = builderSource.with(clos) ?: builderSource
    this.queries << builderResult.build()
    return this
  }

  /**
   * Returns the {@link String} representation of the GraphQL queryString
   *
   * @return the queryString resulting of parsing the DSL
   * @since 0.1.0
   */
  String build() {
    return "{\n${queries.join("\n")}\n}"
  }

  /**
   * Return the queries aggregated one level deeper
   *
   * @return the aggregated nested queries
   * @since 1.1.0
   */
  String buildNested() {
    return queries.find()
  }

  /**
   * Processes query variables applying a {@link VariablesProcessor} and returns
   * the {@link String} representing those processed variables
   *
   * @return a {@link String} representation of the processed variables
   * @since 1.0.0
   */
  protected static String processVariables(Map<String, ?> variables) {
    String processed = new VariablesProcessor().process(variables.entrySet())
    if (!processed) {
      return StringUtils.EMPTY
    }
    return "($processed)"
  }
}
