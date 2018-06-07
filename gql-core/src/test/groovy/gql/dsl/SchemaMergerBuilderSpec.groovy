package gql.dsl

import gql.DSL
import graphql.ExecutionResult
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetcher
import graphql.schema.GraphQLScalarType
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLSchema
import spock.lang.Specification

/**
 * Checks GQL is capable of merging different number of GraphQL raw definitions
 * and then mapping those definitions with {@link DataFetcher} instances
 *
 * @since 0.1.7
 */
@SuppressWarnings('PropertyName')
class SchemaMergerBuilderSpec extends Specification {

  void 'check merging three different schemas byURI'() {
    given: 'two different schema URIs'
    // tag::uris[]
    URI uriOne = ClassLoader.getSystemResource('gql/dsl/bands.graphqls').toURI()
    URI uriTwo = ClassLoader.getSystemResource('gql/dsl/films.graphqls').toURI()
    URI uriTop = ClassLoader.getSystemResource('gql/dsl/qandm.graphqls').toURI()
    // end::uris[]

    and: 'an schema made of three different schema definitions'
    // tag::urisSchema[]
    GraphQLSchema proxySchema = DSL.mergeSchemas {
      scalar(CustomDate)

      byURI(uriOne)   // <1>
      byURI(uriTwo)
      byURI(uriTop) { // <2>
        mapType('Queries') { // <3>
          link('randomFilm') { DataFetchingEnvironment env -> // <4>
            return [title: 'Spectre']
          }
          link('randomBand') { DataFetchingEnvironment env ->
            return [name: 'Whitesnake']
          }
        }
      }
    }
    // end::urisSchema[]

    when: 'executing a query related to the first schema'
    ExecutionResult resultOne = DSL.execute(proxySchema, '{ randomBand { name } }')

    then: 'we should succeed and find it'
    resultOne.data.randomBand.name == 'Whitesnake'

    when: 'executing a query related to the second schema'
    ExecutionResult resultTwo = DSL.execute(proxySchema, '{ randomFilm { title } }')

    then: 'we should succeed too'
    resultTwo.data.randomFilm.title == 'Spectre'
  }

  void 'check merging three different schemas byResource'() {
    given: 'an schema made of three different schema definitions'
    // tag::resourcesSchema[]
    GraphQLSchema proxySchema = DSL.mergeSchemas {
      scalar(CustomDate)
      byResource('gql/dsl/bands.graphqls')
      byResource('gql/dsl/films.graphqls')
      byResource('gql/dsl/qandm.graphqls') {
        mapType('Queries') {
          link('randomFilm') { DataFetchingEnvironment env ->
            return [title: 'Spectre']
          }
          link('randomBand') { DataFetchingEnvironment env ->
            return [name: 'Whitesnake']
          }
        }
      }
    }
    // end::resourcesSchema[]

    when: 'executing a query related to the first schema'
    ExecutionResult resultOne = DSL.execute(proxySchema, '{ randomBand { name } }')

    then: 'we should succeed and find it'
    resultOne.data.randomBand.name == 'Whitesnake'

    when: 'executing a query related to the second schema'
    ExecutionResult resultTwo = DSL.execute(proxySchema, '{ randomFilm { title } }')

    then: 'we should succeed too'
    resultTwo.data.randomFilm.title == 'Spectre'
  }

  void 'check merging three different schemas by different methods'() {
    given: 'an schema made of three different schema definitions'
    URI schemaRootUri = ClassLoader.getSystemResource('gql/dsl/qandm.graphqls').toURI()

    GraphQLSchema proxySchema = DSL.mergeSchemas {
      scalar(CustomDate)
      byResource('gql/dsl/bands.graphqls')
      byResource('gql/dsl/films.graphqls')
      byURI(schemaRootUri) {
        mapType('Queries') {
          link('randomFilm') { DataFetchingEnvironment env ->
            return [title: 'Spectre']
          }
          link('randomBand') { DataFetchingEnvironment env ->
            return [name: 'Whitesnake']
          }
        }
      }
    }

    when: 'executing a query related to the first schema'
    ExecutionResult resultOne = DSL.execute(proxySchema, '{ randomBand { name } }')

    then: 'we should succeed and find it'
    resultOne.data.randomBand.name == 'Whitesnake'

    when: 'executing a query related to the second schema'
    ExecutionResult resultTwo = DSL.execute(proxySchema, '{ randomFilm { title } }')

    then: 'we should succeed too'
    resultTwo.data.randomFilm.title == 'Spectre'
  }

  void 'check merging custom scalar'() {
    given: 'an schema made of three different schema definitions'
    URI schemaRootUri = ClassLoader.getSystemResource('gql/dsl/qandm.graphqls').toURI()

    // tag::mergeSchemasWithCustomScalar[]
    GraphQLSchema proxySchema = DSL.mergeSchemas {
      scalar(CustomDate)

      byResource('gql/dsl/bands.graphqls')
      byResource('gql/dsl/films.graphqls')
      byURI(schemaRootUri) {
        mapType('Queries') {
          link('randomBand') { DataFetchingEnvironment env ->
            return [
              name: 'Whitesnake',
              createdAt: Date.parse('dd-MM-yyyy', '01-01-1977')
            ]
          }
        }
      }
    }
    // end::mergeSchemasWithCustomScalar[]

    when: 'executing a query related to the first schema'
    ExecutionResult resultOne = DSL.execute(
      proxySchema, '''{
        randomBand {
          name
          createdAt
        }
      }''')

    then: 'we should succeed and find it'
    resultOne.data.randomBand.name == 'Whitesnake'
    resultOne.data.randomBand.createdAt == '01/01/1977'
  }

  // tag::customScalar[]
  static GraphQLScalarType CustomDate = DSL.scalar('CustomDate') {
    serialize { Date date ->
      date.format('dd/MM/yyyy')
    }
  }
  // end::customScalar[]

  void 'Apply a type resolver: interface'() {
    given: 'a schema'
    GraphQLSchema proxySchema = DSL.mergeSchemas {
      byResource('gql/dsl/Interfaces.graphqls') {
        mapType('Queries') {
          link('raffles') {
            return [sample]
          }
        }
        mapType('Raffle') {
          typeResolver { TypeResolutionEnvironment env ->
            def raffle = env.getObject() as Map
            def schema = env.schema

            return raffle.containsKey('hashTag') ?
              schema.getObjectType('TwitterRaffle') :
              schema.getObjectType('SimpleRaffle')
          }
        }
      }
    }

    and: 'a query with different type resolution'
    def query = """{
      raffles(max: 2) {
        title
        ... on $typeResolved {
          $typeField
        }
      }
    }"""

    when: 'executing the query against the schema'
    def result = DSL.execute(proxySchema, query)

    then: 'there should be no errors'
    !result.errors

    and: 'and the result should have the type field'
    result.data.raffles.every {
      it[typeField]
    }
    where:
                     sample                      | typeResolved      |  typeField
    [title: 'T-Shirt', hashTag: '#greachconf']   | "TwitterRaffle"   |    "hashTag"
    [title: 'T-Shirt', owner: "me"]              | "SimpleRaffle"    |    "owner"
  }

  // tag::typeResolver[]
  void 'Apply a type resolver: interface (example)'() {
    given: 'a schema'
    GraphQLSchema proxySchema = DSL.mergeSchemas {
      byResource('gql/dsl/Interfaces.graphqls') {

        mapType('Queries') {
          link('raffles') {
            return [[title: 'T-Shirt', hashTag: '#greachconf']] // <1>
          }
        }

        mapType('Raffle') {
          // <2>
          typeResolver { TypeResolutionEnvironment env ->
            def raffle = env.getObject() as Map  // <3>
            def schema = env.schema

            return raffle.containsKey('hashTag') ?
              schema.getObjectType('TwitterRaffle') : // <4>
              schema.getObjectType('SimpleRaffle')
          }
        }

      }
    }

    and: 'a query with different type resolution'
    def query = """{
      raffles(max: 2) {
        title
        ... on TwitterRaffle { # <5>
          hashTag
        }
      }
    }
    """

    when: 'executing the query against the schema'
    def result = DSL.execute(proxySchema, query)

    then: 'the result should have the type field'
    result.data.raffles.every {
      it.hashTag
    }
  }
  // end::typeResolver[]

  void 'Apply a type resolver: union'() {
    given: 'a schema'
    // tag::typeResolverToUnionSchema[]
    def schema = DSL.mergeSchemas {
      byResource('gql/dsl/UnionTypes.graphqls') {
        mapType('Driver') {
          typeResolver(TypeResolverUtils.driversResolver())
        }
        mapType('SearchResult') {
          typeResolver(TypeResolverUtils.driversResolver())
        }
        mapType('Queries') {
          link('searchDriversByName', TypeResolverUtils.&findAllDriversByNameStartsWith)
        }
      }
    }
    // end::typeResolverToUnionSchema[]

    and: 'a query with different type resolution'
    // tag::typeResolverToUnionQuery[]
    def query = """{
      searchDriversByName(startsWith: \"$driverName\") {
        ... on MotoGPDriver {
          name
          age
          bike
        }
        ... on FormulaOneDriver {
          name
          age
          bodywork
        }
      }
    }"""
    // end::typeResolverToUnionQuery[]

    when: 'executing the query against the schema'
    def result = DSL.execute(schema, query)
    def driver = result?.data?.searchDriversByName?.find()

    then: 'there should be no errors'
    !result.errors

    and: 'and the result should have the type field'
    driver.name.startsWith driverName
    driver.age == age
    driver.bike == bike
    driver.bodywork == bodywork

    where:
    driverName  |  age |   bike   | bodywork
    'Lewis'     |  33  |  null    |  'Mercedes'
    'Jorge'     |  30  | 'Ducati' | null
    'Fernando'  |  36  |  null    | 'McLaren'
    'Valentino' |  38  | 'Yamaha' | null
  }
}
