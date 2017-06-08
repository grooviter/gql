package gql.dsl

import gql.DSL
import graphql.ExecutionResult
import graphql.schema.DataFetcher
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
    URI uriOne = ClassLoader.getSystemResource('gql/dsl/bands.graphqls').toURI()
    URI uriTwo = ClassLoader.getSystemResource('gql/dsl/films.graphqls').toURI()
    URI uriTop = ClassLoader.getSystemResource('gql/dsl/qandm.graphqls').toURI()

    and: 'an schema made of three different schema definitions'
    GraphQLSchema proxySchema = DSL.mergeSchemas {
      byURI(uriOne)
      byURI(uriTwo)
      byURI(uriTop) {
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

  void 'check merging three different schemas byResource'() {
    given: 'an schema made of three different schema definitions'
    GraphQLSchema proxySchema = DSL.mergeSchemas {
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
}
