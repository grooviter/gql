package gql

// tag::importExecutionResult[]
import graphql.ExecutionResult
// end::importExecutionResult[]

import groovy.test.GroovyAssert
import spock.lang.Specification

import graphql.GraphQLError

// tag::importGraphQLSchema[]
import graphql.schema.GraphQLSchema
// end::importGraphQLSchema[]

// tag::importGraphQLObjectType[]
import graphql.schema.GraphQLObjectType
// end::importGraphQLObjectType[]

// tag::importValidationError[]
import graphql.validation.ValidationError
// end::importValidationError[]

import gql.test.util.Queries

/**
 * @since 0.1.0
 */
class DSLSpec extends Specification {

  void 'build a simple type with a name'() {
    when: 'building the type'
    GraphQLObjectType type = DSL.type('Droid') {
      // nothing
    }

    then: 'the type should have the expected name'
    type.name == 'Droid'
  }

  void 'build a simple type with a name inside dsl'() {
    when: 'building the type'
    GraphQLObjectType type = DSL.type('Droid') {
      name 'DroidX'
    }

    then: 'the type should have the expected name'
    type.name == 'DroidX'
  }

  void 'build a simple type with a name and description'() {
    when: 'building the type'
    GraphQLObjectType type = DSL.type('Droid') {
      description'a simple droid'
    }

    then: 'the type should have the expected name'
    type.name == 'Droid'
    type.description == 'a simple droid'
  }

  void 'build a simple type with one field'() {
    when: 'building the type'
    // tag::fullType[]
    def type = DSL.type('Droid') { // <1>
      description'simple droid' // <2>

      field('name') { // <3>
        description'name of the droid'
        type GraphQLString
      }

    }
    // end::fullType[]

    then: 'the type should have the expected features'
    type.name == 'Droid'
    type.description == 'simple droid'
    type.fieldDefinitions.size() == 1
    type.fieldDefinitions.first().name == 'name'
    type.fieldDefinitions.first().description == 'name of the droid'
  }

  void 'build a simple type with one field (shortest)'() {
    when: 'building the type'
    // tag::shortest[]
    GraphQLObjectType type = DSL.type('Droid') {
      field 'name', GraphQLString
      field 'type', GraphQLString
      field 'age', GraphQLInt
    }
    // end::shortest[]

    then: 'the type should have the expected features'
    type.name == 'Droid'
    type.description == 'description of Droid'
    type.fieldDefinitions.size() == 3
    type.fieldDefinitions.first().name == 'name'
    type.fieldDefinitions.first().description == 'description of field name'
  }

  void 'build a simple type with more than one field'() {
    when: 'building the type'
    GraphQLObjectType type = DSL.type('Droid') {
      description'simple droid'
      field('name') {
        description'name of the droid'
        type GraphQLString
      }
      field('type') {
        description'type of the droid'
        type GraphQLString
      }
      field('age') {
        description 'age of the droid'
        type GraphQLInt
      }
    }

    then: 'the type should have the name and desc'
    type.name == 'Droid'
    type.description == 'simple droid'

    and: 'there should be two fields'
    type.fieldDefinitions.size() == 3

    and: 'we should expect some field names'
    type.fieldDefinitions*.name == ['name', 'type', 'age']

    and: 'some descriptions'
    type.fieldDefinitions*.description == [
      'name of the droid',
      'type of the droid',
      'age of the droid',
    ]
  }

  void 'build a simple type with more than one field (shortest version)'() {
    when: 'building the type'
    GraphQLObjectType type = DSL.type('Droid') {
      field 'age' , GraphQLInt
      field 'name', GraphQLString
      field 'type', GraphQLString
    }

    then: 'the type should have the name and desc'
    type.name == 'Droid'
    type.description == 'description of Droid'

    and: 'there should be two fields'
    type.fieldDefinitions.size() == 3

    and: 'we should expect some field names'
    type.fieldDefinitions*.name == ['age', 'name', 'type']

    and: 'some descriptions'
    type.fieldDefinitions*.description == [
      'description of field age',
      'description of field name',
      'description of field type',
    ]
  }

  void 'execute query with static value'() {
    when: 'building the type'
    // tag::simpleSchema[]
    GraphQLSchema schema = DSL.schema { // <1>
      query('helloQuery') { // <2>
        description'simple droid'// <3>

        field('hello') { // <4>
          description'name of the droid'
          type GraphQLString
          staticValue 'world'
        }

      }
    }
    // end::simpleSchema[]

    and: 'executing a queryString against that schema'
    Map<String,Map> dataMap = DSL
      .execute(schema, '{ hello }')
      .data

    then: 'we should get the expected name'
    dataMap.hello == 'world'
  }

  void 'execute query with fetcher (function)'() {
    when: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
    }

    and: 'building the schema'
    // tag::schemaWithFetcherAsFunctionReference[]
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        description'queries over James Bond'
        field('lastFilm') {
          description'last film'
          type filmType
          fetcher Queries.&findLastFilm
        }
      }
    }
    // end::schemaWithFetcherAsFunctionReference[]

    and: 'executing a queryString against that schema'
    Map<String,Map> dataMap = DSL
      .execute(schema, queryString)
      .data

    then: 'we should get the expected name'
    dataMap.lastFilm.title == 'SPECTRE'

    where: 'executed queryString is'
    queryString = '''
      {
        lastFilm {
          title
        }
      }
    '''
  }

  void 'execute query with fetcher (closure)'() {
    when: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
    }

    and: 'building the schema'
    // tag::schemaWithFetcherAsClosure[]
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        description'queries over James Bond'
        field('lastFilm') {
          description'last film'
          type filmType
          fetcher { env -> [title: 'SPECTRE'] }
        }
      }
    }
    // end::schemaWithFetcherAsClosure[]

    and: 'executing a queryString against that schema'
    Map<String,Map> dataMap = DSL
      .execute(schema, queryString)
      .data

    then: 'we should get the expected name'
    dataMap.lastFilm.title == 'SPECTRE'

    where: 'executed queryString is'
    queryString = '''
      {
        lastFilm {
          title
        }
      }
    '''
  }

  void 'validate mandatory field'() {
    when: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type nonNull(GraphQLString)
      }
    }

    and: 'building the schema'
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        description'queries over James Bond'
        field('lastFilm') {
          description'last film'
          type filmType
          fetcher Queries.&findLastFilm
        }
      }
    }

    and: 'executing a queryString against that schema'
    List<GraphQLError> errors = DSL
      .execute(schema, queryString)
      .errors

    then: 'we should get the expected name'
    errors.find() instanceof ValidationError

    where: 'executed queryString is'
    queryString = '''
      {
        lastFilm
      }
    '''
  }

  void 'execute parametrized query'() {
    when: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
      field('year') {
        description 'title of the film'
        type GraphQLString
      }
    }

    and: 'building the schema'
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument('year') {
            type GraphQLString
          }
        }
      }
    }

    and: 'executing a queryString against that schema'
    def result = DSL.execute(schema, queryString, [year: "1962"])

    then: 'we should get the expected name'
    !result.errors
    result.data.byYear.title == 'DR. NO'

    where: 'executed queryString is'
    queryString = '''
      query FindBondByYear($year: String) {
        byYear(year: $year) {
          year
          title
        }
      }
    '''
  }

  void 'execute parametrized query embedding args'() {
    when: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
      field('year') {
        description 'title of the film'
        type GraphQLString
      }
    }

    and: 'building the schema'
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument('year') {
            type GraphQLString
          }
        }
      }
    }

    and: 'executing a queryString against that schema'
    def result = DSL.execute(schema, queryString)

    then: 'we should get the expected name'
    !result.errors
    result.data.byYear.title == 'DR. NO'

    where: 'executed queryString is'
    queryString = '''
      query FindBondByYear {
        byYear(year: "1962") {
          year
          title
        }
      }
    '''
  }

  void 'execute static typed query'() {
    when: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
      field('year') {
        description 'title of the film'
        type GraphQLString
      }
      field('bond') {
        description 'Actor playing James Bond'
        type GraphQLString
      }
    }

    and: 'building the schema'
    // tag::findByYearSchema[]
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument('year') {
            type GraphQLString
          }
        }
      }
    }
    // end::findByYearSchema[]

    and: 'executing a queryString against that schema'
    // tag::staticQuery[]
    ExecutionResult result = DSL.execute(schema) {
      query('byYear', [year: '1962']) {
        returns(Film) {
          title
          year
        }
        alias 'first'
      }

      query('byYear', [year: '2015']) {
        returns {
          title
          year
          bond
        }
        alias 'last'
      }
    }
    // end::staticQuery[]

    then: 'there should not be any error'
    !result.errors

    and: 'we should get the expected values'
    result.data.first.title == 'DR. NO'
    result.data.first.year == '1962'
    result.data.last.title == 'SPECTRE'
    result.data.last.year == '2015'
    result.data.last.bond == 'Daniel Craig'
  }

  void 'fails to retrieve wrong type fields'() {
    given: 'a schema'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
      field('year') {
        description 'title of the film'
        type GraphQLString
      }
      field('bond') {
        description 'Actor playing James Bond'
        type GraphQLString
      }
    }

    and: 'building the schema'
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument('year') {
            type GraphQLString
          }
        }
      }
    }

    when: 'trying to retrieve a wrong field'
    DSL.execute(schema) {
      query('byYear', [year: "1962"]) {
        returns(Film) {
          wrongField
        }
      }
    }

    then: 'an exception should be thrown before executing the query'
    thrown(IllegalStateException)
  }

  class Film {
    String title
    String bond
    Integer year
  }

  void 'build a valid GraphQL query string'() {
    setup: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
      field('year') {
        description 'title of the film'
        type GraphQLString
      }
      field('bond') {
        description 'Actor playing James Bond'
        type GraphQLString
      }
    }

    and: 'building the schema'
    GraphQLSchema schema = DSL.schema {
      query('QueryRoot') {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument('year') {
            type GraphQLString
          }
        }
      }
    }

    and: 'executing a queryString against that schema'
    // tag::queryString[]
    String queryString = DSL.buildQuery {
      query('byYear', [year: '1962']) {
        returns(Film) {
          title
          year
        }
        alias 'first'
      }

      query('byYear', [year: '2015']) {
        returns {
          title
          year
          bond
        }
        alias 'last'
      }
    }
    // end::queryString[]
    when:
    def result = DSL.execute(schema, queryString)

    then: 'there should not be any error'
    !result.errors

    and: 'we should get the expected values'
    result.data.first.title == 'DR. NO'
    result.data.first.year == '1962'
    result.data.last.title == 'SPECTRE'
    result.data.last.year == '2015'
    result.data.last.bond == 'Daniel Craig'
  }

  @SuppressWarnings('TrailingWhitespace')
  void 'check getting started script'() {
    expect:
    GroovyAssert.assertScript '''
      // tag::grabExample[]
      import gql.DSL

      def filmType = DSL.type('Film') { // <1>
        field 'title', GraphQLString
        field 'year', GraphQLInt
      }

      def schema = DSL.schema { // <2>
        query('queryRoot') {
          field('lastFilm') {
            type filmType
            staticValue(title: 'SPECTRE', year: 2015)
          }
        }
      }

      def result = DSL.execute(schema) { // <3>
          query('lastFilm') {
            returns {
              title
            }
          }
      }

      assert result.data.lastFilm.title == 'SPECTRE'
     // end::grabExample[]
    '''
  }
}
