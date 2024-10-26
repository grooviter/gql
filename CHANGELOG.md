# Change log
All notable changes to this project will be documented in this file.

## [1.1.0] - [2024-10-28]
### Added
- Building nested queries: now you create nested queries using `DSL.buildQuery {}`.

## [1.0.0] - [2022-12-06]
### Breaking changes
- `DataFetchingEnvironment#getContext()` is deprecated by `graphql-java`. Now it's recommended to use
  `DataFetchingEnvironment#getGraphQLContext()` and then access values with `GraphQLContext@get(key)` or
  use GQL `DataFetchingEnvironment#contextAsMap` and then access values as a map
- All numeric scalar types are narrowed down to GraphQLInt and GraphQLFloat (Deprecated)
- All string-like scalar types are narrowed to GraphQLString (Deprecated)

### Added
- gql-core: `DataFetchingEnvironment.getContextAsMap()` extension module function
- gql-ratpack: `DataFetchingEnvironment.getRatpackContext()` extension module function

### Updated
- graphql-java 19.2
- ratpack 1.9.0
- groovy 3.0.13

## [0.5.0] - [2022-11-21]
### Updated
- Ratpack version to 1.6.0
- Groovy version to 2.5.19
- Spock version to 2.3-groovy-2.5
- Gradle plugin org.asciidoctor.jvm.convert to 2.4.0
- Gradle plugin org.ajoberstar.git-publish to 3.0.1

### Removed
- Bintray repositories/plugins

### Fixed
- Links in javadocs

## [0.4.0] - [2020-06-16]
### Added
- GraphQLExecutor to reuse underlying execution instances [#26](https://github.com/grooviter/gql/issues/26)
### Changed
- Docs to reflect GraphQLExecutor preference at executing GraphQL queries [#26](https://github.com/grooviter/gql/issues/26)

## [0.3.5] - [2020-04-11]
### Changed
- Moving from Travis CI to Github Actions

### Fixed
- Empty variables map in Ratpack integration [#27](https://github.com/grooviter/gql/issues/27) (Thanks to @michaelschlies)
- Resolve deploy variables from both gradle properties and environment variables [#29](https://github.com/grooviter/gql/issues/29) (Thanks to @michaelschlies)

## [0.3.4] - [2019-02-07]
### Added
- DSL.buildMutation
- DSL.buildMutation documentation
- Groovydoc completion

### Fixed
- DSL.buildQuery doesn't render nested maps properly

### Changed
- Project's site background

## [0.3.3] - [2018-06-06]
### Added

- A way to add a typeResolver in the `DSL.mergeSchema` DSL
- Travis CI release pipeline

## [0.3.2] - [2018-06-02]

### Added

- Documentation to `gql-ratpack` 

### Fixed

- Major fix. Now we reuse the `graphql.GraphQL` making `gql-ratpack` ready for production Yey!!! :-)
- Minor refactoring

### Changed

- Now `gql-ratpack` can be used without carrying dependencies to `gql-core`, `Groovy` or 
`Ratpack`

## [0.3.1] - [2018-05-28]

### Added

- Pac4j aware handler under package `gql.ratpack.pac4j`
- Refactor and cleaning up of handlers taking out common code
- Added `gql.ratpack.exec.Futures` to `gql-ratpack` module to integrate CompletableFutures in Ratpack's execution model

### Fixed

- UTF-8 encoding issue with GraphiQL UI

## [0.3.0] - [2018-04-04]

### Added

- Context param in new `execute(...)` and `executeAsync(...)` calls
- Ratpack context as execution context in `gql-ratpack` integration
- Ratpack's integration aware of instrumentation added to registry
- `DSL.error` and `DSL.errorFetcher` to ease the creation of
  internationalized error messages.

### Fixed

- `gql-core` Codenarc issues

### Deprecated

- All `execute(...)` and `executeAsync(...)`. In version 1.0.0 is planned
to exist a unifying way to execute queries against a given schema.

## [0.2.0] - [2018-01-??]

### Added

- Documentation moved to its own Gradle module
- Ratpack GraphQL module
- Add docs of Ratpack module in the GQL user guide

### Changed

- GQL project estructure
- `gql` artifact is now `gql-core`
- Upgraded Gradle 3.5 => 4 due to Groovy compilation problems

## [0.1.9-alpha] - [2018-01-23]

### Added

- Asynchronous execution
- Now it's possible to add custom scalar implementation when merging
  IDL schemas

### Removed

- ASTs are removed

## [0.1.8-alpha] - [2017-06-13]

### Added

- Added `DSL.interface()` method to create interfaces
- First Relay classic implementation draft
- Relay documentation

## [0.1.7-alpha] - [2017-06-09]

### Added

- Now is possible to modularising GraphQL schemas using GraphQL language

### Updated

- Update gradle wrapper to 3.5
- Move documentation configuration to its own gradle file

## [0.1.6-alpha] - [2017-06-08]

### Updated

- Updated `graphql-java` to version 3.0.0

### Changed

- Removed ambiguity: The type() method has now being removed from the argument node. This
introduces a breaking change.

## [0.1.5-alpha] - [2017-04-03]

### Changed

- Schema nodes: `query` and `mutation` become `queries` and `mutations` and have default names to reduce verbosity
- Hello world example: a GraphQL query string is easier to understand than query builders (#13)

### Added

- Added more documentation about queries

### Fixed

- NonNull and List modifiers refactored to be shared between input and output types (#12)
- NonNull and List modifiers not found in standalone field declaration (#14)

## [0.1.4-alpha] - [2017-04-27]

### Added

- Input types

### Other

- Removing unused code

## [0.1.3-alpha] - [2017-04-27]

### Added

- Add Enum type creation to DSL
- Scalar definition
- Add more documentation to existent sections
- Some minor fixes

## [0.1.2-alpha] - [2017-04-24]

### Added

- `mutation` root node in the schema defintion DSL
- `DSL.field()` method to create standalone field definitions
- Add release version to asciidoc documentation

## [0.1.1-alpha] - [2017-04-21]

### Added

- Updated version of graphql-java to 2.4.0

### Fixed

- Fetcher as closure: variable schema closure was dehydrated() and could resolve context variables
- Fetcher as closure: Static compile checking complains when Closure return type was other than Object
- Use CompilePhase from Groovy in GraphQLImpl to avoid Intellij complaints

## [0.1.0-alpha] - [2017-04-20]

First alpha release

### Added

- DSL with most of the basic functionality covered: types, queries, schema, execution
- Documentation to use the DSL
- Groovydoc linked to the documentation examples
- AST draft
