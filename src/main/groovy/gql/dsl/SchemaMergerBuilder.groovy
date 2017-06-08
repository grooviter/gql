package gql.dsl

import graphql.schema.DataFetcher
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.TypeRuntimeWiring
import groovy.transform.TupleConstructor

/**
 * Builder responsible to merge several GraphQL IDL definitions into a
 * single {@link GraphQLSchema} instance and make possible to map those
 * definitions to their correspondent {@link DataFetcher} instances
 * afterwards
 *
 * @since 0.1.7
 */
class SchemaMergerBuilder {

  private final SchemaParser schemaParser = new SchemaParser()
  private final SchemaGenerator schemaGenerator = new SchemaGenerator()
  private final TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry()
  private final List<TypeDefinitionRegistry> registries = []
  private final List<TypeRuntimeWiring> wiringList = []

  /**
   * Loads a given schema definition from a given {@link URI} instance
   *
   * @param uri where the schema is located at
   * @return the current instance of {@link SchemaMergerBuilder}
   * @since 0.1.7
   */
  SchemaMergerBuilder byURI(URI uri){
    registries << schemaParser.parse(uri.toURL().text)
    return this
  }

  /**
   * Loads a given schema definition from a given {@link URI} instance
   *
   * @param uri where the schema is located at
   * @param closure the closure will define how the mappings between Type->Field->DataFetcher
   * are linked
   * @return the current {@link SchemaMergerBuilder} instance
   * @since 0.1.7
   */
  SchemaMergerBuilder byURI(URI uri, @DelegatesTo(SchemaMappingBuilder) Closure closure) {
    Closure<SchemaMappingBuilder> clos = closure.clone() as Closure<SchemaMappingBuilder>
    SchemaMappingBuilder sourceBuilder = new SchemaMappingBuilder()
    SchemaMappingBuilder resultBuilder = sourceBuilder.with(clos) ?: sourceBuilder
    List<TypeRuntimeWiring> typeRuntimeWiringList = resultBuilder.build()

    registries << schemaParser.parse(uri.toURL().text)
    wiringList.addAll(typeRuntimeWiringList)

    return this
  }

  /**
   * Loads a given schema definition from a classpath resource
   *
   * @param classpathResource the resource file path within the class loader
   * @return the current instance of {@link SchemaMergerBuilder}
   * @since 0.1.7
   */
  SchemaMergerBuilder byResource(String classpathResource) {
    return byURI(ClassLoader.getSystemResource(classpathResource).toURI())
  }

  /**
   * Loads a given schema definition from a classpath resource
   *
   * @param classpathResource the resource file path within the class loader
   * @return the current instance of {@link SchemaMergerBuilder}
   * @since 0.1.7
   */
  SchemaMergerBuilder byResource(String classpathResource, @DelegatesTo(SchemaMappingBuilder) Closure closure) {
    return byURI(ClassLoader.systemClassLoader.getResource(classpathResource).toURI(), closure)
  }

  /**
   * Returns an schema of the merged definitions
   *
   * @return an instance of {@link GraphQLSchema} with all the merged definitions
   * @since 0.1.7
   */
  GraphQLSchema build() {
    TypeDefinitionRegistry registry = registries
      .inject(typeRegistry, this.&merge)

    RuntimeWiring.Builder builder = wiringList
      .inject(RuntimeWiring.newRuntimeWiring(), this.&aggregateWirings)

    return schemaGenerator.makeExecutableSchema(registry, builder.build())
  }

  private static RuntimeWiring.Builder aggregateWirings(RuntimeWiring.Builder builder, TypeRuntimeWiring wiring) {
    return builder.type(wiring)
  }

  private static TypeDefinitionRegistry merge(TypeDefinitionRegistry seed, TypeDefinitionRegistry next) {
    return seed.merge(next)
  }

  /**
   * This type defines the relationships between the types defined in the IDL documents
   * and the data fetchers responsible to fetch the data
   *
   * @since 0.1.7
   */
  static class SchemaMappingBuilder {

    List<TypeRuntimeWiring> typeRuntimeWiringList = []

    /**
     * @param typeName the name of the type we are going to map
     * @param dsl
     * @return the current {@link SchemaMappingBuilder} instance
     * @since 0.1.7
     */
    SchemaMappingBuilder mapType(String typeName, @DelegatesTo(SchemaMappingFieldBuilder) Closure dsl) {
      Closure<SchemaMappingFieldBuilder> clos = dsl.clone() as Closure<SchemaMappingFieldBuilder>
      SchemaMappingFieldBuilder sourceBuilder = new SchemaMappingFieldBuilder(TypeRuntimeWiring.newTypeWiring(typeName))
      SchemaMappingFieldBuilder resultBuilder = sourceBuilder.with(clos) ?: sourceBuilder

      typeRuntimeWiringList << resultBuilder.build()

      return this
    }

    /**
     * Returns the built Type->Field->DataFetcher relationships defined in
     * this part of the DSL
     *
     * @return an list of {@link TypeRuntimeWiring}
     * @since 0.1.7
     */
    List<TypeRuntimeWiring> build() {
      return typeRuntimeWiringList
    }
  }

  /**
   * This class has functions to map type fields to fetchers. In order to be able to
   * create an instance of this class you should pass a given {@link TypeRuntimeWiring}
   * in order to map all field names registered with this class to that type.
   *
   * @since 0.1.7
   */
  @TupleConstructor
  static class SchemaMappingFieldBuilder {

    /**
     * The {@link TypeRuntimeWiring} used for a specific type. All fields
     * mapped to a fetcher using an instance of this class will be added
     * to this field
     *
     * @since 0.1.7
     */
    TypeRuntimeWiring.Builder typeWiring

    /**
     * Links a given field under a given type with a specific {@link DataFetcher}
     *
     * @param fieldName the name of the field we're going to link to a data fetcher
     * @param fetcher the {@link DataFetcher} responsible to get the data for that field
     * @since 0.1.7
     */
    SchemaMappingFieldBuilder link(String fieldName, DataFetcher<?> fetcher) {
      typeWiring = typeWiring.dataFetcher(fieldName, fetcher)
      return this
    }

    /**
     * Links a given field under a given type with a specific {@link DataFetcher}
     *
     * @param fieldName the name of the field we're going to link to a data fetcher
     * @param fetcher a closure acting as a {@link DataFetcher} responsible to get the data for that field
     * @since 0.1.7
     */
    public <T> SchemaMappingFieldBuilder link(String fieldName, Closure<T> fetcher) {
      typeWiring = typeWiring.dataFetcher(fieldName, fetcher as DataFetcher<T>)
      return this
    }

    /**
     * Finally returns an instance of {@link TypeRuntimeWiring} with the fields mapped to
     * their data fetchers
     *
     * @return an instance of {@link TypeRuntimeWiring}
     * @since 0.1.7
     */
    TypeRuntimeWiring build() {
      return this.typeWiring.build()
    }
  }
}
