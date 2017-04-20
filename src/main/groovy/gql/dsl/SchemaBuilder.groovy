package gql.dsl

import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLObjectType

/**
 * Builds a new {@link GraphQLSchema}
 *
 * @examples <a target="_blank" href="/gql/docs/html5/index.html#_schemas">Creating GraphQL schemas</a>
 * @since 0.1.0
 */
class SchemaBuilder {

  GraphQLSchema.Builder builder = GraphQLSchema.newSchema()

  /**
   * Adds a query to the current schema
   *
   * @param dsl the dsl body
   * @return the current schema builder with the added query
   * @since 0.1.0
   */
  SchemaBuilder query(String name, @DelegatesTo(ObjectTypeBuilder) Closure<ObjectTypeBuilder> dsl) {
    Closure<ObjectTypeBuilder> clos = dsl.dehydrate().clone() as Closure<ObjectTypeBuilder>
    ObjectTypeBuilder builderSource = new ObjectTypeBuilder()
      .name(name)
      .description("description of type $name")
    ObjectTypeBuilder builderResult = builderSource.with(clos) ?: builderSource
    GraphQLObjectType qurs = builderResult.build()

    builder.query(qurs)
    return this
  }

  /**
   * Returns {@GraphQLSchema} built by the builder
   *
   * @return an instance of {@link GraphQLSchema}
   * @since 0.1.0
   */
  GraphQLSchema build() {
    return builder.build()
  }
}
