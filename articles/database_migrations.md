# Database Migrations for Micronaut/Spring With Liquibase

Wheter you are starting on a new project or mantaining an existing codebase, mantaining database changes as controlled and reproducible as possible is a **must**, there are a varieity of tools for achieving this goal but today we going to focus on [liquibase](https://www.liquibase.org/), an opensource(and paid) database migration/versioning tool.

# Database Migration 101

A database migration(or schema migration) is a software engineering technique based on a structured and incremental version control of a database schema, can be compared to a git repository, where you can perform incremental commits adding new functionality to a codebase.

Given that in mind we are ensuring that every version(or commit) to our database can be traced and reproduced in different environments(Local development, UAT, Testing Sandbox).


# Liquibase In a Nutshell

In liquibase database migrations are structured in a **changelog** a file to track every version of your database, usually a changelog contains relevant information about every version such as: version number, comments, author and of course the changes themselves.

Liquibase enables a variety of formats for your changelog(sql, xml, properties and yaml files) in this case and since we are using spring and micronaut, we are going to create yaml files as our changelog.

The following example is a changelog(yaml based) with sqlFiles: 

```yaml
databaseChangeLog:
  - changeSet:
      id: v_1_0_0
      author: "Marco Villarreal"
      comment: "A comment for your version"
      sqlFile:
        encoding: utf8
        relativeToChangelogFile: true
        stripComments: true
        path: "v_1_0_0/main-changelog.sql"
```

On a closer look we can identify the following properties:

* **comment**: Description for your database version
* **author**: Author of the current version
* **id**: The id of the version, you are totally free to use whatever format you like
* **sqlFile**: An object with the sql file configuration
    * **encoding**: Encoding of the sql file
    * **relativeToChangelogFile**: Determines if the sql file path is relative to the changelog path
    * **stripComments**: Remove any comments from the sql file
    * **path**: The path for the sql file itself


With this in mind we can use the following directory structure:


```
+--db
|   +--changelog.yaml
|   +--v_1_0_0
|   |   +--main-changelog.sql
```

And of course we need an initial database version.

```sql
--main-changelog.sql

CREATE TABLE author(
  author_id BIGSERIAL,
  author_name VARCHAR(200) NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  PRIMARY KEY(author_id)
);

CREATE TABLE book(
  book_id BIGSERIAL,
  author_id BIGINT NOT NULL,
  book_isbn VARCHAR(200) NOT NULL,
  book_name TEXT NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
  PRIMARY KEY(book_id),
  FOREIGN KEY(author_id)
  REFERENCES author(author_id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT,
  CONSTRAINT unq_book_isbn UNIQUE(book_isbn)
);

```
**Note**: We are not using a CREATE DATABASE statement, is a required step to have an empty database to execute a changelog.

# Creating a new Database Versions

To create a new database version the following steps are required:

1. Add a new directory for your version

```
+--db
|   +--changelog.yaml
|   +--v_1_0_0
|   |   +--main-changelog.sql
|   +--v_1_0_1 # new Directory
|   |
```

2. Create a sql file for your version

```
+--db
|   +--changelog.yaml
|   +--v_1_0_0
|   |   +--main-changelog.sql
|   +--v_1_0_1
|   |   +--adding-comments.sql # new version file
```

```sql
-- adding-comments.sql
COMMENT ON COLUMN author.author_name IS 'Author name';
COMMENT ON COLUMN author.author_id IS 'Author numeric identifier';

COMMENT ON COLUMN book.book_id IS 'Book numeric identifier';
COMMENT ON COLUMN book.book_isbn IS 'Book International Standard Book Number';
COMMENT ON COLUMN book.book_name IS 'Book name';
COMMENT ON COLUMN book.author_id IS 'Author numeric identifier(author reference)';
```

3. Add a reference to your new version in the changelog

```yaml
#changelog.yaml
databaseChangeLog:
  - changeSet:
      id: v_1_0_0
      author: "Marco Villarreal"
      comment: "Initial dummy library database schema"
      sqlFile:
        encoding: utf8
        relativeToChangelogFile: true
        stripComments: true
        path: "v_1_0_0/main-changelog.sql"
  - changeSet:
      id: v_1_0_1
      author: "Marco Villarreal"
      comment: | # We can use yaml multi-line syntax for more descriptive changelog comments
        * Adding comments for author's table
        * Adding comments for book's table
      sqlFile:
        encoding: utf8
        relativeToChangelogFile: true
        stripComments: true
        path: "v_1_0_1/adding-comments.sql"

```

# Configuring automatic migrations with SpringBoot & Micronaut


Now that we have our database changelog organized and ready to go, we can implement it automatically in our java projects:

## SpringBoot configuration

We need to add liquibase-core, spring data jpa(datasource connection) and postgresql driver to our project dependencies:

```groovy
implementation 'org.liquibase:liquibase-core'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'org.postgresql:postgresql'
```

And enable liquibase beans in our project's configuration:

```yaml
spring:
  liquibase:
    enabled: true
    change-log: "classpath:/db/changelog.yaml"
```

To download my setup check the [spring initializr project](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=2.5.0.RELEASE&packaging=jar&jvmVersion=11&groupId=org.mvillabe.books&artifactId=book-demo&name=book-demo&description=Book%20Demo%20for%20spring&packageName=org.mvillabe.books&dependencies=liquibase,postgresql,testcontainers,webflux,data-jpa)

## Micronaut configuration

We need to add micronaut-liquibase and micronaut-data jpa(datasource connection)

```groovy
implementation("io.micronaut.liquibase:micronaut-liquibase")
implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
```

```yaml
liquibase:
  enabled: true
  datasources:
    default:
      change-log: 'classpath:db/changelog.yaml'
```

To download my setup check the [micronaut launch project](https://micronaut.io/launch?javaVersion=JDK_11&lang=JAVA&build=GRADLE&test=JUNIT&name=book-demo-micronaut&package=org.mvillabe.books&type=DEFAULT&features=liquibase&features=postgres&features=data-jpa&features=testcontainers&version=2.5.4&activity=preview&showing=README.md)


# Creating Database Migrations for a existing Database

If you are on a project with an existing database with no versioning at all, fear not, we can create a changelog with liquibase, in this case we need to have [liquibase installed](https://www.liquibase.org/download). 

Once installed we have to execute the following steps:

## Create the recomended folder structure

In this case we create the db folder, an empty changelog.yaml file and v_1_0_0 directory.

```
+--db
|   +--changelog.yaml
|   +--v_1_0_0
|   |
```

## Get the changes from your existing database

```sh
liquibase \
--driver=org.postgresql.Driver \
--classpath=~/classpath/postgresql-42.2.20.jar \
--url="jdbc:postgresql://127.0.0.1/existing_database" \
--changeLogFile=db/v_1_0_0/main-changelog.postgresql.sql \
--username=postgres \
--password=casa1234 \
generateChangeLog
```

This command will create a sql based changelog, however since we are using yaml files to organize our changelog, we can use the generated sql file as an input for a yaml changelog.

## Create a changelog

Now we create a changelog v_1_0_0 targeting the generated *v_1_0_0/main-changelog.postgresql.sql* file

```yaml
#changelog.yaml
databaseChangeLog:
  - changeSet:
      id: v_1_0_0
      author: "Marco Villarreal"
      comment: "Inherited changelog from existing database"
      sqlFile:
        encoding: utf8
        relativeToChangelogFile: true
        stripComments: true
        path: "v_1_0_0/main-changelog.postgresql.sql"
```

## Sync the changelog to your database

```sh
liquibase \
--driver=org.postgresql.Driver \
--classpath=~/classpath/postgresql-42.2.20.jar \
--url="jdbc:postgresql://127.0.0.1/existing_database" \
--changeLogFile=db/v_1_0_0/main-changelog.postgresql.sql \
--username=postgres \
--password=casa1234 \
changelogSync
```

With the executed command we successfully referenced a changelog for the existing database.

# Conclusions and Caveats

* Liquibase creates 2 additional tables; databasechangelog and databasechangeloglock:
  * databasechangelog: Keep a track of each executed version
  * databasechangeloglock: Used to lock executions and avoid conflicts at runtime.

* When using liquibase, each executed version is **inmutable**, each version creates a hash of the file, if you modify it it will change the hash causing a corrupted changelog(we need to avoid this).

* Having a versioned database is as good as having a versioned codebase, enables a clean tracking of your schema's evolution.

* If you feel uncomfortable adding liquibase to your project's runtime, you can use gradle or maven plugin, or even the liquibase cli itself.

* Enabling tools like liquibase can be considered a "vendor locking", this is because we are creating sql scripts with vendor specific syntax. If this is a problem for you, consider enabling jpa automatic changes(of course this has it's own caveats).



You can find the used projects in the [github repository](https://github.com/mvillarrealb/liquibase-demo)

