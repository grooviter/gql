package gql.dsl

import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLObjectType
import groovy.util.logging.Slf4j

/**
 * Builds a new {@link GraphQLSchema}
 *
 * @examples <a target="_blank" href="/gql/docs/html5/index.html#_schemas">Creating GraphQL schemas</a>
 * @since 0.1.0
 */
@Slf4j
class SchemaBuilder {

  GraphQLSchema.Builder builder = GraphQLSchema.newSchema()

  /**
   * Adds a query root to the current schema
   *
   * @param dsl the dsl body
   * @return the current schema builder with the added query root
   * @since 0.1.0
   */
  SchemaBuilder query(String name, @DelegatesTo(ObjectTypeBuilder) Closure<ObjectTypeBuilder> dsl) {
    log.debug("Adding query [$name]")

    Closure<ObjectTypeBuilder> clos = dsl.clone() as Closure<ObjectTypeBuilder>
    ObjectTypeBuilder builderSource = new ObjectTypeBuilder()
      .name(name)
      .description("description of type $name")
    ObjectTypeBuilder builderResult = builderSource.with(clos) ?: builderSource
    GraphQLObjectType qurs = builderResult.build()

    builder.query(qurs)
    return this
  }

  /**
   * Adds a mutation root to the current schema
   *
   * @param dsl the dsl body
   * @return the current schema builder with the added mutation root
   * @since 0.1.1
   */
  SchemaBuilder mutation(String name, @DelegatesTo(ObjectTypeBuilder) Closure<ObjectTypeBuilder> dsl) {
    log.debug("Adding mutation [$name]")

    Closure<ObjectTypeBuilder> clos = dsl.clone() as Closure<ObjectTypeBuilder>
    ObjectTypeBuilder builderSource = new ObjectTypeBuilder()
      .name(name)
      .description("description of type $name")
    ObjectTypeBuilder builderResult = builderSource.with(clos) ?: builderSource
    GraphQLObjectType mutation = builderResult.build()

    builder.mutation(mutation)
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
