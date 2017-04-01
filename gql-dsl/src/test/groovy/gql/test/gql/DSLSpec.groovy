package gql

import spock.lang.Specification

import graphql.GraphQLError
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLObjectType
import graphql.validation.ValidationError

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
    GraphQLObjectType type = DSL.type('Droid') {
      description'simple droid'
      field('name') {
        description'name of the droid'
        type GraphQLString
      }
    }

    then: 'the type should have the expected features'
    type.name == 'Droid'
    type.description == 'simple droid'
    type.fieldDefinitions.size() == 1
    type.fieldDefinitions.first().name == 'name'
    type.fieldDefinitions.first().description == 'name of the droid'
  }

  void 'build a simple type with one field (shortest)'() {
    when: 'building the type'
    GraphQLObjectType type = DSL.type('Droid') {
      field 'name', GraphQLString
    }

    then: 'the type should have the expected features'
    type.name == 'Droid'
    type.description == 'description of Droid'
    type.fieldDefinitions.size() == 1
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
    GraphQLSchema schema = DSL.schema {
      query('helloQuery') {
        description'simple droid'
        field('hello') {
          description'name of the droid'
          type GraphQLString
          staticValue 'world'
        }
      }
    }

    and: 'executing a query against that schema'
    Map<String,Map> dataMap = DSL
      .execute(schema, '{ hello }')
      .data

    then: 'we should get the expected name'
    dataMap.hello == 'world'
  }

  void 'execute query with fetcher'() {
    when: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field('title') {
        description 'title of the film'
        type GraphQLString
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

    and: 'executing a query against that schema'
    Map<String,Map> dataMap = DSL
      .execute(schema, queryString)
      .data

    then: 'we should get the expected name'
    dataMap.lastFilm.title == 'SPECTRE'

    where: 'executed query is'
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

    and: 'executing a query against that schema'
    List<GraphQLError> errors = DSL
      .execute(schema, queryString)
      .errors

    then: 'we should get the expected name'
    errors.find() instanceof ValidationError

    where: 'executed query is'
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

    and: 'executing a query against that schema'
    def result = DSL.execute(schema, queryString, [year: "1962"])

    then: 'we should get the expected name'
    !result.errors
    result.data.byYear.title == 'DR. NO'

    where: 'executed query is'
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

    and: 'executing a query against that schema'
    def result = DSL.execute(schema, queryString)

    then: 'we should get the expected name'
    !result.errors
    result.data.byYear.title == 'DR. NO'

    where: 'executed query is'
    queryString = '''
      query FindBondByYear {
        byYear(year: "1962") {
          year
          title
        }
      }
    '''
  }
}
