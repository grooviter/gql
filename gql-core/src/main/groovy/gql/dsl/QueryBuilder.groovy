package gql.dsl

import static groovy.lang.Closure.DELEGATE_FIRST

import groovy.transform.TailRecursive
import groovy.transform.CompileDynamic

/**
 * Builder aimed to create a compliant GraphQL queryString using
 * a static typed DSL
 *
 * @since 0.1.0
 */
class QueryBuilder {

  String queryString = ""

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
  public <T> QueryBuilder query(String name,
                                @DelegatesTo(strategy = DELEGATE_FIRST, value = ReturnsBlockBuilder) Closure fields) {
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
  public <T> QueryBuilder query(String name,
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
    return "{ $queryString }"
  }

  private String processVariables(Map<String, ?> variables) {
    String processed = new VariablesProcessor().process(variables.entrySet())

    return processed ? "($processed)" : ""
  }

  /**
   * Processes query variables. It should be able to process variables, and nested variables as well.
   *
   * <h2>Nested maps</h2>
   * For example, a query like the following containing nested maps:
   * <pre><code class="groovy"> query('searchOrders', [filter: [status: 'COMPLETED', type: 'NEW']]) {
   *    returns(Order) {
   *      id
   *      status
   *    }
   *  }</code></pre>
   *
   * Should be translated to a syntactic valid GraphQL query:
   *
   * <pre><code class="graphql"> {
   *   searchOrders (filter: {status: "COMPLETED",type: "NEW"}) {
   *     id
   *     status
   *   }
   * }</code></pre>
   *
   * <h2>Nested Lists</h2>
   * <pre><code class="groovy"> query('searchInvoices', [ids: [1, 2, 3, 4]]) {
   *     returns(Invoice) {
   *       id
   *       status
   *     }
   * }</code></pre>
   *
   * Which produces
   * <pre><code class="graphql"> {
   *   searchInvoices (ids: [1, 2, 3, 4]) {
   *     id
   *     status
   *   }
   *  }</code></pre>
   *
   * @since 0.3.4
   */
  @CompileDynamic
  static class VariablesProcessor {

    /**
     * Builds the variable part of a given query
     *
     * @param values variable values
     * @param acc query string
     * @return the complete variable query string
     * @since 0.3.4
     */
    @TailRecursive
    String process(Collection<Map.Entry<String,?>> values, String acc = "") {
      if (values.isEmpty()) {
        return acc
      } else {
        Map.Entry<String, ?> next = values.head()
        String value = processValue(next.key, next.value)
        String fragment = acc
          ? [acc, value].join(',')
          : value

        return process(values.tail(), fragment)
      }
    }

    private String processValue(String key, Map<String, ?> map) {
      String value = process(map.entrySet())

      return "$key: {$value}"
    }

    private String processValue(String key, String value) {
      return "$key: \"$value\""
    }

    private String processValue(String key, Object object) {
      return "$key: $object"
    }
  }

  /**
   * @since 0.1.0
   */
  static class ReturnsBlockBuilder {

    String name = ""
    String variables = ""
    String aliasFragment = ""
    String fieldFragment = ""

    @CompileDynamic
    public <T> ReturnsBlockBuilder returns(@DelegatesTo.Target Class<T> clazz,
                                           @DelegatesTo(
                                             strategy = DELEGATE_FIRST,
                                             genericTypeIndex = 0) Closure fields){
      DynamicFieldCapture fieldCapture = new DynamicFieldCapture(clazz: clazz)
      Closure<DynamicFieldCapture> clos = fields.clone() as Closure<DynamicFieldCapture>

      clos.delegate = fieldCapture
      clos.resolveStrategy = DELEGATE_FIRST
      clos()

      fieldCapture
        .fields
        .each { String name ->
        fieldFragment += "\t$name\n"
      }

      return this
    }

    /**
     * @param fields
     * @return
     * @since 0.1.0
     */
    @CompileDynamic
    public ReturnsBlockBuilder returns(
      @DelegatesTo(strategy = DELEGATE_FIRST, value = DynamicFieldCapture) Closure fields) {
      DynamicFieldCapture fieldCapture = new DynamicFieldCapture()
      Closure<DynamicFieldCapture> clos = fields.clone() as Closure<DynamicFieldCapture>

      clos.delegate = fieldCapture
      clos.resolveStrategy = DELEGATE_FIRST
      clos()

      fieldCapture
        .fields
        .each { String name ->
          fieldFragment += "\t$name\n"
        }

      return this
    }

    /**
     * @param alias
     * @return
     * @since 0.1.0
     */
    ReturnsBlockBuilder alias(String alias) {
      this.aliasFragment = "$alias:"
      return this
    }

    /**
     * @return
     * @since 0.1.0
     */
    String build() {
      return "$aliasFragment $name $variables { \n $fieldFragment } \n"
    }
  }
}
