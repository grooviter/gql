package gql.dsl

import gql.DSL
import graphql.schema.GraphQLSchema
import spock.lang.Shared
import spock.lang.Specification

/**
 * Tests how to build mutations using {@link DSL#buildMutation} which
 * underneath uses {@link MutationBuilder}
 *
 * @since 0.3.4
 */
class MutationBuilderSpec extends Specification {

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
        mapType('Mutations') {
          link('saveOrder') { env ->
            return env.arguments.order
          }
          link('saveInvoice') { env ->
            return env.arguments.invoice
          }
        }
      }
    }
  }

  void 'simple mutation'() {
    given: 'input data'
    Map order = [id: 1000, status: 'ACTIVE']

    and: 'a mutation string'
    // tag::simple_mutation[]
    String mutation = DSL.buildMutation {
      mutation('saveOrder', [order: order]) {
        returns(Order) {
          id
          status
        }
      }
    }
    // end::simple_mutation[]

    println "$mutation"

    when: 'executing the mutation'
    Map<String,?> data = DSL
      .execute(schema, mutation)
      .data

    then: 'we should get the expected result back'
    data.saveOrder.id     == 1000
    data.saveOrder.status == 'ACTIVE'
  }

  void 'more than one mutation'() {
    given: 'input data'
    Map order = [id: 1000, status: 'ACTIVE']
    Map invoice = [id: 2000, status: 'DISABLED']

    and: 'a string containing two mutations'
    String mutation = DSL.buildMutation {
      mutation('saveOrder', [order: order]) {
        returns(Order) {
          id
          status
        }
      }
      mutation('saveInvoice', [invoice: invoice]) {
        returns(Invoice) {
          id
          status
        }
      }
    }

    when: 'executing both mutations one after another'
    Map<String,?> data = DSL
      .execute(schema, mutation)
      .data

    then: 'we should get the expected result back for order mutation'
    with(data.saveOrder) {
      id     == 1000
      status == 'ACTIVE'
    }

    and: 'also expected result for invoice mutation'
    with(data.saveInvoice) {
      id     == 2000
      status == 'DISABLED'
    }
  }

  void 'using alias'() {
    given: 'input data'
    Map order = [id: 1000, status: 'ACTIVE']

    and: 'a string containing two mutations'
    String mutation = DSL.buildMutation {
      mutation('saveOrder', [order: order]) {
        returns(Order) {
          id
          status
        }

        alias('first')
      }
      mutation('saveOrder', [order: order]) {
        returns(Order) {
          id
          status
        }

        alias('second')
      }
    }
    println mutation

    when: 'executing both mutations one after another'
    Map<String,?> data = DSL
      .execute(schema, mutation)
      .data

    then: 'we should get the expected result back for order mutation'
    with(data.first) {
      id     == 1000
      status == 'ACTIVE'
    }

    and: 'also expected result for invoice mutation'
    with(data.second) {
      id     == 1000
      status == 'ACTIVE'
    }
  }
}
