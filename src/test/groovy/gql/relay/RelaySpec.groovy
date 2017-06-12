package gql.relay

import gql.DSL
import gql.Relay
import graphql.ExecutionResult
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLSchema
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @since 0.1.8
 */
class RelaySpec extends Specification {

  static final List<Map> SHIPS = [
    [id: 'WC1XaW5nCg==', name: 'X-Wing'],
    [id: 'U3RhckZpZ2h0ZXIK', name: 'StarFighter'],
    [id: 'TGFtYmRhCg==', name: 'Lambda'],
    [id: 'Q2xhd2NyYWZ0Cg==', name: 'Clawcraft']
  ]

  void 'create a simple Relay schema'() {
    given: 'a schema with one node'
    GraphQLOutputType ShipConnection = Relay.connection('ShipConnection') {
      description 'a way of dealing with ship collections'
      edges('Ship') {
        description 'a starship'
        field 'name', GraphQLString
      }
    }

    GraphQLOutputType Faction = Relay.node('Faction') {
      description 'party spirit especially when marked by dissension'

      field 'name', GraphQLString
      field 'ships', ShipConnection
    }

    GraphQLSchema schema = Relay.schema {
      queries {
        field ('rebels') {
          type Faction
          staticValue(id: 'RmFjdGlvbjox', name: 'Alliance to Restore the Republic')
        }
      }
    }

    when: 'executing a query against the defined relay schema'
    ExecutionResult executionResult = DSL.execute(schema,
      '''
         {
           rebels {
             id
             name
           }           
         }
      '''
    )

    then: 'we should get the rebels id'
    executionResult.data.rebels.id

    and: 'we should get the rebels name'
    executionResult.data.rebels.name
  }

  @Unroll
  void 'create a Relay schema with connections to get #noResults items'() {
    given:
    // tag::connection[]
    GraphQLOutputType ShipConnection = Relay.connection('ShipConnection') { // <1>
      edges('Ship') { // <2>
        description 'a starship'
        field 'name', GraphQLString
      }
    }
    // end::connection[]

    // tag::node[]
    GraphQLOutputType Faction = Relay.node('Faction') {
      field 'name', GraphQLString // <1>
      connection('ships'){ // <2>
        type ShipConnection
        listFetcher { // <3>
          Integer limit = it.getArgument('first')

          return SHIPS.take(limit)
        }
      }
    }
    // end::node[]

    // tag::schema[]
    GraphQLSchema schema = Relay.schema {
      queries {
        field('rebels') {
          type Faction
          fetcher {
            return [id: 'RmFjdGlvbjox', name: 'Alliance to Restore the Republic']
          }
        }
      }
    }
    // end::schema[]

    and:
    // tag::query[]
    def query = """
         {
           rebels {             
             name
             ships(first: $noResults) {
               pageInfo {
                 hasNextPage
               }
               edges {
                 cursor
                 node {
                   name
                 }
               }
             }
           }           
         }
      """
    // end::query[]

    when:''
    // tag::execution[]
    ExecutionResult executionResult = DSL.execute(schema, query)
    // end::execution[]

    then: 'we should get the expected number of edges'
    executionResult.data.rebels.ships.edges.size() == noResults

    and: 'we should get the correct expectations over the next page'
    executionResult.data.rebels.ships.pageInfo.hasNextPage == hasNext

    where: 'possible number of results are'
    noResults | hasNext
    1         | true
    2         | true
    3         | true
    4         | false
  }
}
