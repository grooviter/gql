package gql.dsl

import static groovy.lang.Closure.DELEGATE_FIRST

import gql.DSL
import gql.dsl.query.ReturnsBlockBuilder
import gql.dsl.query.VariablesProcessor

/**
 *
 * Builder helping to create a DSL capable of building GraphQL query strings.
 *
 * @see {@link DSL#buildMutation}
 * @since 0.3.4
 */
class MutationBuilder {

  private String queryString = ""

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
  public <T> MutationBuilder mutation(String name,
                                      @DelegatesTo(
                                        strategy = DELEGATE_FIRST,
                                        value = ReturnsBlockBuilder) Closure fields) {
    Closure<ReturnsBlockBuilder> clos = fields.clone() as Closure<ReturnsBlockBuilder>
    ReturnsBlockBuilder builderSource = new ReturnsBlockBuilder(name: name)
    ReturnsBlockBuilder builderResult = builderSource.with(clos) ?: builderSource

    this.queryString += builderResult.build()

    return this
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
  public <T> MutationBuilder mutation(String name,
                                Map<String,?> variables,
                                @DelegatesTo(strategy = DELEGATE_FIRST, value = ReturnsBlockBuilder) Closure fields) {
    Closure<ReturnsBlockBuilder> clos = fields.clone() as Closure<ReturnsBlockBuilder>
    String variablesString = processVariables(variables)
    ReturnsBlockBuilder builderSource = new ReturnsBlockBuilder(name: name, variables: variablesString)
    ReturnsBlockBuilder builderResult = builderSource.with(clos) ?: builderSource

    this.queryString += builderResult.build()

    return this
  }

  /**
   * Returns the {@link String} representation of the GraphQL queryString
   *
   * @return the queryString resulting of parsing the DSL
   * @since 0.1.0
   */
  String build() {
    return "mutation { $queryString }"
  }

  private String processVariables(Map<String, ?> variables) {
    String processed = new VariablesProcessor().process(variables.entrySet())

    return processed ? "($processed)" : ""
  }
}
