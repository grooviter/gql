package gql.dsl

import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLObjectType

/**
 * Builds a new {@link GraphQLSchema}
 * <br/>
 * <pre><code class="groovy">
 * GraphQLSchema schema = DSL.schema {
 *     queries {
 *         type('helloWorldQuery') {
 *             fields {
 *                 field('hello') {
 *                     type GraphQLString
 *                     staticValue 'world'
 *                 }
 *             }
 *         }
 *     }
 * }
 * </code></pre>
 *
 * @since 0.1.0
 */
class SchemaBuilder {

  GraphQLSchema.Builder builder = GraphQLSchema.newSchema()

  /**
   * Declares queries for the current schema
   * <br/>
   * <pre><code>
   * DSL.schema {
   *     queries {
   *         //...
   *     }
   * }
   * </code></pre>
   *
   * @param dsl
   * @return
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
