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
  GraphQLScalarType GraphQLBoolean = Scalars.GraphQLBoolean
  GraphQLScalarType GraphQLID = Scalars.GraphQLID

  @Deprecated
  GraphQLScalarType GraphQLLong = Scalars.GraphQLInt
  @Deprecated
  GraphQLScalarType GraphQLShort = Scalars.GraphQLInt
  @Deprecated
  GraphQLScalarType GraphQLByte = Scalars.GraphQLInt
  @Deprecated
  GraphQLScalarType GraphQLFloat = Scalars.GraphQLFloat
  @Deprecated
  GraphQLScalarType GraphQLBigInteger = Scalars.GraphQLInt
  @Deprecated
  GraphQLScalarType GraphQLBigDecimal = Scalars.GraphQLFloat
  @Deprecated
  GraphQLScalarType GraphQLChar = Scalars.GraphQLString

}
