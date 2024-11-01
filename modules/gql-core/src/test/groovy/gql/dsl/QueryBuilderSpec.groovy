package gql.dsl

import gql.DSL
import graphql.ExecutionResult
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import spock.lang.Issue
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

  static class Order extends IdAware {
    String subject
    List<OrderEntry> entries
  }

  static class OrderEntry {
    Integer id
    String subject
    Integer count
    BigDecimal price
  }

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
        mapType('Order') {
          link('entries') { DataFetchingEnvironment env ->
            return [
              [id: 1, subject: 't-shirt', count: 1, price: 10.50],
              [id: 2, subject: 'jeans', count: 2, price: 44.99]
            ]
          }
        }
      }
    }
  }

  void 'executing same query with different alias'() {
    given: 'parameters'
    String queryString = DSL.buildQuery {
      query('searchOrders', [filter: [status: 'COMPLETED', type: 'NEW']]) {
        returns(Order) {
          id
          status
        }

        alias('first')
      }
      query('searchOrders', [filter: [status: 'COMPLETED', type: 'NEW']]) {
        returns(Order) {
          id
          status
        }

        alias('second')
      }
    }

    when: 'executing the query'
    Map<String,Map> result = DSL
      .execute(schema, queryString)
      .data

    then: 'we should get the expected results for first query'
    with(result.first) {
      id == 1
      status == 'ACTIVE'
    }
    and: 'expected results for the second query as well'
    with(result.second) {
      id == 1
      status == 'ACTIVE'
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

  @Issue("https://github.com/grooviter/gql/issues/36")
  void "create nested Queries"() {
    setup:
    // tag::nestedQueries[]
    String queryString = DSL.buildQuery {
      query('searchOrders', [filter: [status: 'ACTIVE']]) {
        returns(Order) {
          id
          status
          query('entries', [first: 2]) {
            returns(OrderEntry) {
              id
              subject
              count
              price
            }
          }
        }
      }
    }
    // end::nestedQueries[]
    when:
    Map<String,Map> result = DSL.execute(schema, queryString).data

    then:
    with(result){
      searchOrders.id == 1
      searchOrders.status == 'ACTIVE'
      searchOrders.entries.size() == 2
    }
  }

  @Issue("https://github.com/grooviter/gql/issues/36")
  void "create nested Queries (executor)"() {
    when:
    ExecutionResult result = DSL.newExecutor(schema).execute {
      query('searchOrders', [filter: [status: 'ACTIVE']]) {
        returns(Order) {
          id
          status
          query('entries', [first: 2]) {
            returns(OrderEntry) {
              id
              subject
              count
              price
            }
          }
        }
      }
    }
    and:
    Map<String, Map> data = result.data

    then:
    with(data){
      searchOrders.id == 1
      searchOrders.status == 'ACTIVE'
      searchOrders.entries.size() == 2
    }
  }
}
