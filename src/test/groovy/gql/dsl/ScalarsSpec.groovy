package gql.dsl

import gql.DSL
import graphql.language.StringValue
import spock.lang.Specification

/**
 * @since 0.1.3
 */
class ScalarsSpec extends Specification {

  void 'serializing an scalar type output'() {
    given: 'a scalar type'
    // tag::serializingScalar[]
    def GraphQLPrice = DSL.scalar('Price') { // <1>
      description 'currency unit' // <2>

      // serialization
      serialize { money -> // [unit: 1.23, key: '$'] <3>
        "${money.unit}${money.key}" // "1.23$" <4>
      }
    }
    // end::serializingScalar[]

    and: 'a type'
    def orderType = DSL.type('Order') {
      field 'subject', GraphQLString // <1>
      field 'units', GraphQLInt // <2>
      field 'price', GraphQLPrice // <3>
    }

    and: 'a schema'
    def schema = DSL.schema {
      queries {
        field('orders') {
          type list(orderType)
          fetcher { env -> orderList }
        }
      }
    }

    when: 'executing a given query'
    def result = DSL.execute schema, '''
      {
        orders {
          price
        }
      }
    '''

    then: 'we should get expected serialized output'
    result.data.orders*.price == ['2.23$', '0.45E']
  }

  Map stringValueToMap(String value) {
    return [
      unit: "$value"[0..-2].toDouble(), // 1.23
      key: "$value"[-1..-1] // 'Y'
    ]
  }

  List<Map> getOrderList() {
    return [
      [subject: 'beer', units: 2, price: [unit: 2.23, key: '$']],
      [subject: 'bread', units: 1, price: [unit: 0.45, key: 'E']]
    ]
  }

  void 'parsing a query parameter literal'() {
    given: 'a scalar definition'
    // tag::definingParseLiteral[]
    def GraphQLPrice = DSL.scalar('Price') {
      parseLiteral { money -> // 1.23Y
        String value = money.value // from a StringValue
        return [
          unit: "$value"[0..-2].toDouble(), // 1.23
          key: "$value"[-1..-1] // 'Y'
        ]
      }
    }
    // end::definingParseLiteral[]

    and: 'a type'
    def orderType = DSL.type('Order') {
      field 'subject', GraphQLString
      field 'units', GraphQLInt
      field 'price', GraphQLPrice
    }

    and: 'a schema'
    // tag::parseLiteralFetcher[]
    def schema = DSL.schema {
      queries {
        field('changeOrderPrice') {
          type orderType
          fetcher { env ->
            // already converted :)
            def price = env.arguments.defaultPrice // [unit: 1.23, key: 'Y']

            [subject: 'paper', price: price]
          }
          argument 'defaultPrice', GraphQLPrice
        }
      }
    }
    // end::parseLiteralFetcher[]

    when: 'executing a given query'
    // tag::queryParseLiteral[]
    def query = '''
      {
        order:changeOrderPrice(defaultPrice: "1.23Y") {
          price
        }
      }
    '''
    // end::queryParseLiteral[]
    def result = DSL.execute schema, query

    then: 'we should get expected serialized output'
    result.data.order.price == [unit:1.23, key: 'Y']
  }

  String fromMapToString(Map money) {
    "${money.unit}${money.key}" // 1.23Y
  }

  void 'parsing a query input'() {
    given: 'a scalar definition'
    // tag::queryWithVariablesScalar[]
    def scalar = DSL.scalar('Price') {
      parseValue { String value -> // '1.25PTA'
        return [
          unit: value[0..-4].toDouble(), // 1.25
          key: value[-1..-3].reverse() // 'PTA'
        ]
      }
    }
    // end::queryWithVariablesScalar[]

    and: 'a type'
    def orderType = DSL.type('Order') {
      field 'subject', GraphQLString
      field 'units', GraphQLInt
      field 'price', scalar
    }

    and: 'a schema'
    // tag::queryWithVariablesFetcher[]
    def schema = DSL.schema {
      queries {
        field('changeOrderPrice') {
          type orderType
          fetcher { env ->
            def price = env.arguments.defaultPrice // already converted :)

            [subject: 'paper', price: price]
          }
          argument 'defaultPrice', scalar
        }
      }
    }
    // end::queryWithVariablesFetcher[]

    when: 'executing a given query'
    // tag::queryWithVariables[]
    def query = '''
      query ChangeOrderPrice($price: Price){
        order:changeOrderPrice(defaultPrice: $price) {
          price
        }
      }
    '''
    // end::queryWithVariables[]
    // tag::queryWithVariableExecution[]
    def result = DSL.execute(
      schema,
      query,
      [price: '1.25PTA']
    )
    // end::queryWithVariableExecution[]

    then: 'we should get expected serialized output'
    result.data.order.price == [unit: 1.25, key: 'PTA']
  }

  void 'full scalar type'() {
    given: 'a scalar type'
    // tag::fullScalarDeclaration[]
    def GraphQLPrice = DSL.scalar('Price') { // <1>
      description 'currency unit' // <2>

      // deserialization
      parseLiteral this.&stringValueToMap // <3>
      parseValue this.&stringValueToMap // <4>

      // serialization
      serialize this.&fromMapToString // <5>
    }
    // end::fullScalarDeclaration[]

    and: 'a type'
    // tag::usingScalarInType[]
    def orderType = DSL.type('Order') {
      field 'subject', GraphQLString // <1>
      field 'units', GraphQLInt // <2>
      field 'price', GraphQLPrice // <3>
    }
    // end::usingScalarInType[]

    and: 'a schema'
    def schema = DSL.schema {
      queries {
        field('orders') {
          type list(orderType)
          fetcher { env -> orderList }
        }
      }
    }

    when: 'executing a given query'
    def result = DSL.execute schema, '''
      {
        orders {
          price
        }
      }
    '''

    then: 'we should get expected serialized output'
    result.data.orders*.price == ['2.23$', '0.45E']
  }
}
