package gql.dsl

import gql.DSL
import graphql.ExecutionResult
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
    ExecutionResult resultOne = DSL.execute(proxySchema,'{ randomBand { name } }')

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
    ExecutionResult resultOne = DSL.execute(proxySchema,'{ randomBand { name } }')

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
    ExecutionResult resultOne = DSL.execute(proxySchema,'{ randomBand { name } }')

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
              createdAt: Date.parse('dd-MM-yyyy','01-01-1977')
            ]
          }
        }
      }
    }
    // end::mergeSchemasWithCustomScalar[]

    when: 'executing a query related to the first schema'
    ExecutionResult resultOne = DSL.execute(
      proxySchema,'''{
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
}
