# Change log
All notable changes to this project will be documented in this file.

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
