import sbt._

/** @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.0
 */
object Dependencies {

  object Version {
    val zio                           = "2.0.9"
    val `zio-logging`                 = "2.1.7"
    val tapir                         = "1.2.11"
    val `akka-http`                   = "10.2.10"
    val akka                          = "2.6.20"
    val circe                         = "0.14.3"
    val scalikejdbc                   = "3.5.0"
    val logback                       = "1.4.5"
    val config                        = "1.4.1"
    val `zio-interop-reactiveStreams` = "2.0.0"
    val mysql                         = "8.0.30"
    val `simple-java-mail`            = "7.5.0"
    val h2                            = "2.1.214"
    val scalaTest                     = "3.2.14"
    val `zio-actors`                  = "0.1.0"
    val refined                       = "0.10.1"
    val `zio-schema`                  = "0.3.1"
    val `sttp-apispec`                = "0.3.1"
    val redis4cats                    = "1.3.0"
    val `zio-interop-cats`            = "23.0.0.0"
    val `log4cats-slf4j`              = "2.5.0"
    val `zio-redis`                   = "0.0.0+453-a4649d3b-SNAPSHOT" // Experimental
    val `zio-crypto`                  = "0.0.0+102-23de4b47-SNAPSHOT" // Experimental
  }

  lazy val zioDeps: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio"                         % Version.zio,
    "dev.zio" %% "zio-interop-reactivestreams" % Version.`zio-interop-reactiveStreams`,
    "dev.zio" %% "zio-logging"                 % Version.`zio-logging`,
    "dev.zio" %% "zio-test"                    % Version.zio % Test,
    "dev.zio" %% "zio-test-sbt"                % Version.zio % Test
  )

  lazy val tapirApiSpec: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.apispec" %% "apispec-model"       % Version.`sttp-apispec`,
    "com.softwaremill.sttp.apispec" %% "openapi-model"       % Version.`sttp-apispec`,
    "com.softwaremill.sttp.apispec" %% "openapi-circe"       % Version.`sttp-apispec`,
    "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"  % Version.`sttp-apispec`,
    "com.softwaremill.sttp.apispec" %% "asyncapi-model"      % Version.`sttp-apispec`,
    "com.softwaremill.sttp.apispec" %% "asyncapi-circe"      % Version.`sttp-apispec`,
    "com.softwaremill.sttp.apispec" %% "asyncapi-circe-yaml" % Version.`sttp-apispec`
  )

  lazy val tapirDeps: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"              % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"      % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"  % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-docs"     % Version.tapir
  ) ++ tapirApiSpec

  lazy val akkaDeps: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor-typed"    % Version.akka,
    "com.typesafe.akka" %% "akka-http"           % Version.`akka-http`,
    "com.typesafe.akka" %% "akka-stream"         % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j"          % Version.akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka        % Test,
    "com.typesafe.akka" %% "akka-http-testkit"   % Version.`akka-http` % Test
  )

  lazy val circeDeps: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-generic"        % Version.circe,
    "io.circe" %% "circe-generic-extras" % Version.circe,
    "io.circe" %% "circe-parser"         % Version.circe
  )

  lazy val otherDeps: Seq[ModuleID] = Seq(
    "ch.qos.logback"     % "logback-classic"  % Version.logback,
    "org.simplejavamail" % "simple-java-mail" % Version.`simple-java-mail`,
    "com.h2database"     % "h2"               % Version.h2        % Test,
    "org.scalatest"     %% "scalatest"        % Version.scalaTest % Test
  )

  /** ----------------Module deps------------------ */
  lazy val serverDeps: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio-actors-akka-interop" % Version.`zio-actors`,
    "dev.zio" %% "zio-actors"              % Version.`zio-actors`,
    "dev.zio" %% "zio-interop-cats"        % Version.`zio-interop-cats`,
    "dev.zio" %% "zio-redis"               % Version.`zio-redis`
  ) ++ domainDeps ++ akkaDeps ++ otherDeps ++ zioDeps ++ tapirDeps

  lazy val apiDeps: Seq[ModuleID] = Seq(
    "dev.zio"    %% "zio"                         % Version.zio,
    "dev.zio"    %% "zio-interop-reactivestreams" % Version.`zio-interop-reactiveStreams`,
    "eu.timepit" %% "refined"                     % Version.refined
  ) ++ akkaDeps ++ tapirDeps ++ domainDeps

  lazy val domainDeps: Seq[ModuleID] = Seq(
    "org.scalikejdbc"   %% "scalikejdbc"                      % Version.scalikejdbc % Compile,
    "org.scalikejdbc"   %% "scalikejdbc-syntax-support-macro" % Version.scalikejdbc % Compile,
    "dev.zio"           %% "zio-schema-derivation"            % Version.`zio-schema`,
    "com.typesafe.akka" %% "akka-actor-typed"                 % Version.akka,
    "dev.zio"           %% "zio-schema"                       % Version.`zio-schema`,
    "dev.zio"           %% "zio-schema-protobuf"              % Version.`zio-schema`,
    "io.circe"          %% "circe-generic"                    % Version.circe,
    "io.circe"          %% "circe-generic-extras"             % Version.circe,
    "io.circe"          %% "circe-parser"                     % Version.circe
  )

  lazy val infrastructureDeps: Seq[ModuleID] = zioDeps ++ Seq(
    "org.simplejavamail" % "simple-java-mail"     % Version.`simple-java-mail`,
    "org.scalikejdbc"   %% "scalikejdbc-streams"  % Version.scalikejdbc,
    "com.typesafe"       % "config"               % Version.config,
    "mysql"              % "mysql-connector-java" % Version.mysql,
    "dev.zio"           %% "zio-crypto"           % Version.`zio-crypto`
  )

  lazy val cacheRedis4zioDeps: Seq[ModuleID] = Seq(
    "com.typesafe" % "config"              % Version.config,
    "dev.zio"     %% "zio-redis"           % Version.`zio-redis`,
    "dev.zio"     %% "zio"                 % Version.zio,
    "dev.zio"     %% "zio-schema"          % Version.`zio-schema`,
    "dev.zio"     %% "zio-schema-protobuf" % Version.`zio-schema`,
    "io.circe"    %% "circe-core"          % Version.circe,
    "io.circe"    %% "circe-parser"        % Version.circe
  )

  lazy val cacheRedis4catsDeps: Seq[ModuleID] = Seq(
    "com.typesafe"    % "config"              % Version.config,
    "dev.profunktor" %% "redis4cats-effects"  % Version.redis4cats,
    "dev.profunktor" %% "redis4cats-log4cats" % Version.redis4cats,
    "dev.profunktor" %% "redis4cats-streams"  % Version.redis4cats,
    "io.circe"       %% "circe-core"          % Version.circe,
    "io.circe"       %% "circe-parser"        % Version.circe,
    "org.typelevel"  %% "log4cats-slf4j"      % Version.`log4cats-slf4j`
  )
}
