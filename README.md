[![license](https://img.shields.io/github/license/grooviter/gql.svg)]() [![main](https://github.com/grooviter/gql/actions/workflows/gql-release.yml/badge.svg)](https://github.com/grooviter/gql/actions/workflows/gql-release.yml) ![Maven Central](https://img.shields.io/maven-central/v/com.github.grooviter/gql-core) ![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.grooviter/gql-core?server=https%3A%2F%2Fs01.oss.sonatype.org)

## Description

**GQL** is a set of [Groovy](http://www.groovy-lang.org) DSLs and AST
transformations built on top
of [GraphQL-java](https://github.com/graphql-java/graphql-java) that
make it easier to build GraphQL schemas and execute **GraphQL**
queries without losing type safety.

## Gradle

In order to use `GQL` releases in your Groovy code add the maven central repository and if you'd like to evaluate some
snapshots add the sonatype snapshots repository as well:

```groovy
repositories {
    mavenCentral()
    maven { url 'https://s01.oss.sonatype.org/content/repositories/snapshots/' } // FOR SNAPSHOTS
}
```

Once you've declared the maven repositories then add the dependencies to your project:

```groovy
dependencies {
    implementation 'com.github.grooviter:gql-core:VERSION'
    implementation 'com.github.grooviter:gql-ratpack:VERSION'
}
```

## Getting Started

You can directly execute the following example in your Groovy console:

```groovy
@Grab('com.github.grooviter:gql-core:1.1.0')
import gql.DSL

def GraphQLFilm = DSL.type('Film') {
  field 'title', GraphQLString
  field 'year', GraphQLInt
}

def schema = DSL.schema {
  queries {
    field('lastFilm') {
      type GraphQLFilm
      staticValue(title: 'No Time to die', year: 2021)
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

assert result.data.lastFilm.year == 2021
assert result.data.lastFilm.title == 'No time to die'
```

## Documentation

Current documentation is available at: http://grooviter.github.io/gql/
