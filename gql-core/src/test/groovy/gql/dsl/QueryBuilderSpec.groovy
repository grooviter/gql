package gql.dsl

import gql.DSL
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import spock.lang.Shared
import spock.lang.Specification

/**
 * Checking {@link QueryBuilder}
 *
 * @since 0.3.4
 */
class QueryBuilderSpec extends Specification {

  static class IdAware {
    Long id
    String status
  }

  static class Order extends IdAware { }

  static class Invoice extends IdAware { }

  @Shared GraphQLSchema schema

  void setup() {
    schema = DSL.mergeSchemas {
      byResource('gql/dsl/orders.graphqls') {
        mapType('Queries') {
          link('searchOrders') { DataFetchingEnvironment env ->
            return [id: 1, status: 'ACTIVE']
          }
          link('searchInvoices') { DataFetchingEnvironment env ->
            List<Long> ids = env.arguments.ids as List<Long>

            return [id: ids.last(), status: 'DISABLED']
          }
        }
      }
    }
  }

  void 'nested parameters: Map'() {
    given: 'parameters'
    String queryString = DSL.buildQuery {
      query('searchOrders', [filter: [status: 'COMPLETED', type: 'NEW']]) {
        returns(Order) {
          id
          status
        }
      }
    }

    when: 'executing the query'
    Map<String,Map> result = DSL
      .execute(schema, queryString)
      .data

    then: 'we should get the expected results'
    with(result) {
      searchOrders.id == 1
      searchOrders.status == 'ACTIVE'
    }
  }

  void 'nested parameters: List'() {
    given: 'query with parameters'
    String queryString = DSL.buildQuery {
      query('searchInvoices', [ids: [1, 2, 3, 4]]) {
        returns(Invoice) {
          id
          status
        }
      }
    }

    println "$queryString"

    when: 'executing the query'
    Map<String,Map> result = DSL
      .execute(schema, queryString)
      .data

    then: 'we should get the expected results'
    with(result) {
      searchInvoices.id     == 4
      searchInvoices.status == 'DISABLED'
    }
  }
}
