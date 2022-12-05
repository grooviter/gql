[![license](https://img.shields.io/github/license/grooviter/gql.svg)]() [![main](https://github.com/grooviter/gql/actions/workflows/gql-release.yml/badge.svg)](https://github.com/grooviter/gql/actions/workflows/gql-release.yml) ![Maven Central](https://img.shields.io/maven-central/v/com.github.grooviter/gql-core) ![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.grooviter/gql-core?server=https%3A%2F%2Fs01.oss.sonatype.org)

## Description

**GQL** is a set of [Groovy](http://www.groovy-lang.org) DSLs and AST
transformations built on top
of [GraphQL-java](https://github.com/graphql-java/graphql-java) that
make it easier to build GraphQL schemas and execute **GraphQL**
queries without losing type safety.

## Getting Started

You can directly execute the following example in your Groovy console:

```groovy
@Grab('com.github.grooviter:gql-core:0.5.0')
import gql.DSL

def GraphQLFilm = DSL.type('Film') {
  field 'title', GraphQLString
  field 'year', GraphQLInt
}

def schema = DSL.schema {
  queries {
    field('lastFilm') {
      type GraphQLFilm
      staticValue(title: 'SPECTRE', year: 2015)
    }
  }
}

def query = """
  {
    lastFilm {
      year
      title
    }
  }
"""

def result = DSL.newExecutor(schema).execute(query)

assert result.data.lastFilm.year == 2015
assert result.data.lastFilm.title == 'SPECTRE'
```

## Gradle

### Releases
In order to use `GQL` in your Groovy code you can find it in maven central:

```groovy
repositories {
    mavenCentral()
}
```

Then you can add the dependency to your project:

```groovy
implementation 'com.github.grooviter:gql-core:0.5.0'
```

### Snapshots
Snapshots are published to Sonatype. You can evaluate a snapshot version adding the snapshot repository to
your gradle dependencies:

```groovy
repositories {
    maven {
        url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
    }
}
```

And then add the implementation dependency:

```groovy
implementation 'com.github.grooviter:gql-core:1.0.0-SNAPSHOT'
```

## Documentation

Current documentation is available at: http://grooviter.github.io/gql/
