package gql.dsl

import gql.DSL
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLObjectType
import spock.lang.Specification

class EnumTypeSpec extends Specification {

  void 'build an enum type'() {
    when: 'building the type'
    GraphQLEnumType countries = DSL.enum('Countries') {
      description 'european countries'

      value 'SPAIN', 'es'
      value 'FRANCE', 'fr'
      value 'GERMANY', 'de'
      value 'UK', 'uk'
    }

    then: 'getting enum basic information'
    countries.name == 'Countries'
    countries.description == 'european countries'

    and: 'enum entries'
    countries.values.size() == 4
    countries.values*.value == ['es', 'fr', 'de', 'uk']
  }

  void 'build a type using an enum type'() {
    given:
    // tag::declaringEnum[]
    GraphQLEnumType CountryEnumType = DSL.enum('Countries') {
      description 'european countries'

      value 'SPAIN', 'es'
      value 'FRANCE', 'fr'
      value 'GERMANY', 'de'
      value 'UK', 'uk'
    }
    // end::declaringEnum[]

    when: 'building the type'
    // tag::enumTypeInclusion[]
    GraphQLObjectType journey = DSL.type('Journey') {
      field 'country', CountryEnumType
    }
    // end::enumTypeInclusion[]

    then: 'we should be able to see the enum type'
    journey.getFieldDefinition('country')
  }
}
