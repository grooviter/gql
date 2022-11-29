package gql.dsl

import graphql.Scalars
import graphql.schema.GraphQLScalarType

/**
 * This interface is just a container for basic Scalar types defined in
 * graphql-java {@link Scalars} class. Because it is an interface its fields
 * can be inherited.
 *
 * @since 0.1.1
 */
@SuppressWarnings('FieldName')
interface ScalarsAware {

  GraphQLScalarType GraphQLInt = Scalars.GraphQLInt
  GraphQLScalarType GraphQLString = Scalars.GraphQLString
  GraphQLScalarType GraphQLLong = Scalars.GraphQLLong
  GraphQLScalarType GraphQLShort = Scalars.GraphQLShort
  GraphQLScalarType GraphQLByte = Scalars.GraphQLByte
  GraphQLScalarType GraphQLFloat = Scalars.GraphQLFloat
  GraphQLScalarType GraphQLBigInteger = Scalars.GraphQLBigInteger
  GraphQLScalarType GraphQLBigDecimal = Scalars.GraphQLBigDecimal
  GraphQLScalarType GraphQLBoolean = Scalars.GraphQLBoolean
  GraphQLScalarType GraphQLID = Scalars.GraphQLID
  GraphQLScalarType GraphQLChar = Scalars.GraphQLChar

}
