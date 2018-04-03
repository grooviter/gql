package gql

// tag::importExecutionResult[]
import graphql.ExecutionResult
import graphql.schema.DataFetchingEnvironment

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
      queries('helloQuery') { // <2>
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

  @SuppressWarnings('UnusedVariable')
  void 'execute query with fetcher (function)'() {
    given: 'a type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
      }
    }

    and: 'building the schema'
    // tag::schemaWithFetcherAsFunctionReference[]
    GraphQLSchema schema = DSL.schema {
      queries('QueryRoot') {
        description'queries over James Bond'
        field('lastFilm') {
          description'last film'
          type filmType
          fetcher Queries.&findLastFilm
        }
      }
    }
    // end::schemaWithFetcherAsFunctionReference[]

    and: 'the query'
    // tag::executeQueryStringNoArgumentsQuery[]
    def queryString = '''
      {
        lastFilm {
          title
        }
      }
    '''
    // end::executeQueryStringNoArgumentsQuery[]

    when: 'executing a queryString against that schema'
    // tag::executeQueryStringNoArguments[]
    ExecutionResult result = DSL.execute(schema, queryString) // <1>
    Map<String, ?> dataMap = result.data // <2>
    List<?> errors = result.errors // <3>
    // end::executeQueryStringNoArguments[]

    then: 'we should get the expected name'
    dataMap.lastFilm.title == 'SPECTRE'
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
      queries('QueryRoot') {
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
      queries('QueryRoot') {
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
    given: 'a type'
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

    and: 'a schema'
    GraphQLSchema schema = DSL.schema {
      queries {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument 'year', GraphQLString
        }
      }
    }

    and: 'a query'
    // tag::queryWithArguments[]
    def queryString = '''
      query FindBondByYear($year: String) {
        byYear(year: $year) {
          year
          title
        }
      }
    '''
    // end::queryWithArguments[]

    when: 'executing a queryString against that schema'
    // tag::queryWithArgumentsExecution[]
    def result = DSL.execute(schema, queryString, [year: "1962"])
    // end::queryWithArgumentsExecution[]

    then: 'we should get the expected name'
    !result.errors
    result.data.byYear.title == 'DR. NO'
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
      queries {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument 'year', GraphQLString
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
    given: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field 'title', GraphQLString
      field 'year', GraphQLString
      field 'bond', GraphQLString
    }

    and: 'building the schema'
    // tag::findByYearSchema[]
    GraphQLSchema schema = DSL.schema {
      queries {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument 'year', GraphQLString
        }
      }
    }
    // end::findByYearSchema[]

    when: 'executing a queryString against that schema'
    // tag::staticQueryChecked[]
    ExecutionResult result = DSL.execute(schema) {
      query('byYear', [year: '1962']) { // <1>
        returns(Film) { // <2>
          title
          year
        }

        alias 'first' // <3>
      }
    }
    // end::staticQueryChecked[]

    then: 'we should get the expected values'
    result.data.first.title == 'DR. NO'
    result.data.first.year == '1962'

    when:
    // tag::staticQueryUnchecked[]
    ExecutionResult result2 = DSL.execute(schema) {
      query('byYear', [year: '2015']) { // <1>
        returns { // <2>
          title
          year
          bond
        }

        alias 'last' // <3>
      }
    }
    // end::staticQueryUnchecked[]

    then:
    result2.data.last.title == 'SPECTRE'
    result2.data.last.year == '2015'
    result2.data.last.bond == 'Daniel Craig'
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
      queries {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument 'year', GraphQLString
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
      queries {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument 'year', GraphQLString
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

      def GraphQLFilm = DSL.type('Film') { // <1>
        field 'title', GraphQLString
        field 'year', GraphQLInt
      }

      def schema = DSL.schema { // <2>
        queries {
          field('lastFilm') {
            type GraphQLFilm
            staticValue(title: 'SPECTRE', year: 2015)
          }
        }
      }

      def query = """
        {
          lastFilm {
            year
            title
          }
        }
      """

      def result = DSL.execute(schema, query) // <3>

      assert result.data.lastFilm.year == 2015
      assert result.data.lastFilm.title == 'SPECTRE'
     // end::grabExample[]
    '''
  }

  void 'adding a mutation root to schema'() {
    setup: 'building the type'
    GraphQLObjectType filmType = DSL.type('People') {
      field 'name', GraphQLString
    }
    and: 'setting a memory list'
    List<String> people = []

    and: 'building the schema'
    // tag::queriesAndMutations[]
    GraphQLSchema schema = DSL.schema {
      queries { // <1>
        // no queries
      }
      mutations { // <2>
        field('insert') {
          type filmType
          fetcher { DataFetchingEnvironment env ->
            def name = env.arguments.name
            people << name
            return [name: name]
          }
          argument 'name', GraphQLString
        }
      }
    }
    // end::queriesAndMutations[]

    and: 'executing a queryString against that schema'
    def mutation = '''
    mutation insertNewPerson {
      insert(name: "Johnny") {
        name
      }
    }
    '''
    when:
    def result = DSL.execute(schema, mutation)

    then: 'there should not be any error'
    !result.errors

    and: 'we should get the expected value'
    result.data.insert.name == 'Johnny'

    and: 'people should have one item'
    people.size() == 1
  }

  void 'build a field definition with standalone field DSL'() {
    given: 'a standalone/reusable field definition'
    // tag::standaloneFieldDefinition[]
    def nameToUpperCaseField = DSL.field('name') {
      type GraphQLString
      fetcher { DataFetchingEnvironment env ->
        return "${env.source.name}".toUpperCase()
      }
    }
    // end::standaloneFieldDefinition[]
    // tag::fieldStaticCompilation[]
    def ageMinusOne = DSL.field('age') {
      type GraphQLInt
      fetcher { DataFetchingEnvironment env ->
        Map<String, Integer> data = env.getSource() // <1>

        return data.age - 1 // <2>
      }
    }
    // end::fieldStaticCompilation[]

    and: 'a type definition'
    // tag::addField[]
    def people = DSL.type('People') {
      addField nameToUpperCaseField
      addField ageMinusOne
    }
    // end::addField[]

    and: 'a schema that returns a static person value'
    GraphQLSchema schema = DSL.schema {
      queries {
        field('director') {
          type people
          staticValue([name: 'Peter', age: 22])
        }
      }
    }

    when: 'executing a simple query against the schema'
    def result = DSL.execute(schema, '{ director { name } }')

    then: 'the query should return the name in upper case'
    result.data.director.name == 'PETER'
  }
}
