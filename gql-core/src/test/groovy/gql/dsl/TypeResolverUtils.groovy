package gql.dsl

import gql.dsl.TypeResolverUtils.Driver
import graphql.GraphQLException
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.TypeResolver

/**
 * Utility functions to provide type resolvers for tests
 *
 * @since 0.3.3
 */
class TypeResolverUtils {

  /**
   * Class to use in type resolved related tests
   *
   * @since 0.3.3
   */
  static class Driver {
    String name
    Integer age
    String engine
    String team
  }

  /**
   * Class to use in type resolved related tests
   *
   * @since 0.3.3
   */
  static class FormulaOneDriver extends Driver {
    String bodywork
  }

  /**
   * Class to use in type resolved related tests
   *
   * @since 0.3.3
   */
  static class MotoGPDriver extends Driver {
    String bike
  }

  /**
   * TypeResolver
   *
   * @since 0.3.3
   */
  static class MyTypeResolver implements TypeResolver {
    @Override
    GraphQLObjectType getType(TypeResolutionEnvironment env) {
      switch (env.object) {
        case FormulaOneDriver:
          return resolveType(env, FormulaOneDriver.simpleName)
        case MotoGPDriver:
          return resolveType(env, MotoGPDriver.simpleName)
        default:
          throw new GraphQLException('Unexpected type of driver')
      }
    }
  }

  /**
   * Creates a type resolver
   *
   * @return
   * @since 0.3.3
   */
  static TypeResolver driversResolver() {
    return new MyTypeResolver()
  }

  /**
   * Resolves a type by its name
   *
   * @param env instance of {@link TypeResolutionEnvironment}
   * @param type type you want to retrieve as a String
   * @return an instance of {@link GraphQLObjectType}
   * @since 0.3.3
   */
  private static GraphQLObjectType resolveType(TypeResolutionEnvironment env, String type) {
    return env.schema.getObjectType(type)
  }

  /**
   * Returns a list of drivers having a name which starts by the string passed as parameter
   *
   * @param env an instance of {@link DataFetchingEnvironment}
   * @return a list of {@link Driver}
   * @since 0.3.3
   */
  static <T extends Driver> List<T> findAllDriversByNameStartsWith(DataFetchingEnvironment env) {
    List<T> resultList = findAllDriversBy({ T driver ->
      driver.name.startsWith("${env.arguments.startsWith}")
    }) as List<T>

    return resultList
  }

  private static <T extends Driver> List<T> loadDrivers() {
    return ClassLoader
      .getSystemResource('data/drivers.csv')
      .readLines()
      .drop(1) // headers
      .collect(TypeResolverUtils.&toMap >> TypeResolverUtils.&toDriver) as List<T>
  }

  private static Map toMap(String line) {
    String[] records = line.split(',')

    return [
      name: nvl(records, 0, String),
      age: nvl(records, 1, Integer),
      team: nvl(records, 2, String),
      bike: nvl(records, 3, String),
      engine: nvl(records, 4, String),
      bodywork: nvl(records, 5, String)
    ]
  }

  private static <T> T nvl(String[] records, Integer column, Class<T> type) {
    if (records.size() > column) {
      return records[column].asType(type)
    }

    return null
  }

  private static <T extends Driver> T toDriver(Map map) {
    String[] commonFields = ['name', 'age', 'team', 'engine']
    return map.bike ?
      new MotoGPDriver(map.subMap(commonFields + ['bike'])) :
      new FormulaOneDriver(map.subMap(commonFields + ['bodywork']))
  }

  private static <T extends Driver> List<T> findAllDriversBy(Closure<Boolean> filter) {
    return loadDrivers().findAll(filter) as List<T>
  }
}
