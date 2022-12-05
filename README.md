[![license](https://img.shields.io/github/license/grooviter/gql.svg)]() [![main](https://github.com/grooviter/gql/actions/workflows/gql-release.yml/badge.svg)](https://github.com/grooviter/gql/actions/workflows/gql-release.yml) ![Maven Central](https://img.shields.io/maven-central/v/com.github.grooviter/gql-core) ![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.grooviter/gql-core?server=https%3A%2F%2Fs01.oss.sonatype.org)

## Description

**GQL** is a set of [Groovy](http://www.groovy-lang.org) DSLs and AST
transformations built on top
of [GraphQL-java](https://github.com/graphql-java/graphql-java) that
make it easier to build GraphQL schemas and execute **GraphQL**
queries without losing type safety.

## Gradle
In order to use `GQL` in your Groovy code you can find it in maven central:

    repositories {
        mavenCentral()
    }

Then you can add the dependency to your project:

    implementation 'com.github.grooviter:gql-core:0.5.0'

## Documentation

Current documentation is available at: http://grooviter.github.io/gql/
