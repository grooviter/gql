package gql.dsl.query

import groovy.transform.CompileDynamic
import groovy.transform.TailRecursive

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
class VariablesProcessor {

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
