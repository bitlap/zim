import sbt._

/** @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.0
 */
object Dependencies {

  object Version {
    val zio                           = "1.0.15"
    val `zio-logging`                 = "0.5.14"
    val tapir                         = "0.20.2"
    val `akka-http`                   = "10.2.9"
    val akka                          = "2.6.19"
    val circe                         = "0.14.2"
    val scalikejdbc                   = "3.5.0"
    val logback                       = "1.2.11"
    val config                        = "1.4.1"
    val `zio-interop-reactiveStreams` = "1.3.12"
    val mysql                         = "8.0.29"
    val `simple-java-mail`            = "7.1.1"
    val h2                            = "2.1.214"
    val scalaTest                     = "3.2.12"
    val `zio-actors`                  = "0.0.9"
    val refined                       = "0.9.29"
    val `zio-schema`                  = "0.1.9"
    val `smt-cacheable`               = "0.7.4"
  }

  lazy val zioDeps: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio"                         % Version.zio,
    "dev.zio" %% "zio-interop-reactivestreams" % Version.`zio-interop-reactiveStreams`,
    "dev.zio" %% "zio-logging"                 % Version.`zio-logging`,
    "dev.zio" %% "zio-test"                    % Version.zio % Test,
    "dev.zio" %% "zio-test-sbt"                % Version.zio % Test
  )

  lazy val tapirDeps: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"               % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server"   % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % Version.tapir
  )

  lazy val tapirAsyncDocDeps: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-docs"       % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-circe-yaml" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-circe"      % Version.tapir
  )

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
    "dev.zio"                     %% "zio-actors-akka-interop"   % Version.`zio-actors`,
    "dev.zio"                     %% "zio-actors"                % Version.`zio-actors`,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-docs"       % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-circe-yaml" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-circe"      % Version.tapir,
    "org.bitlap"                  %% "smt-cacheable-caffeine"    % Version.`smt-cacheable`,
    "dev.zio"                     %% "zio-redis"                 % "0.0.0+381-86c20614-SNAPSHOT" // 实验性质的
  ) ++ domainDeps ++ akkaDeps ++ otherDeps ++ zioDeps ++ tapirDeps

  lazy val tapirApiDeps: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio"                         % Version.zio,
    "dev.zio" %% "zio-interop-reactivestreams" % Version.`zio-interop-reactiveStreams`
  ) ++ akkaDeps ++ tapirDeps ++ domainDeps

  lazy val domainDeps: Seq[ModuleID] = Seq(
    "org.scalikejdbc"   %% "scalikejdbc"                      % Version.scalikejdbc % Compile,
    "org.scalikejdbc"   %% "scalikejdbc-syntax-support-macro" % Version.scalikejdbc % Compile,
    "dev.zio"           %% "zio-streams"                      % Version.zio         % Compile,
    "eu.timepit"        %% "refined"                          % Version.refined,
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
    "dev.zio"           %% "zio-crypto"           % "0.0.0+92-5672c642-SNAPSHOT" // 实验性质的
  )

  lazy val cacheDeps: Seq[ModuleID] = Seq(
    "com.typesafe" % "config"              % Version.config,
    "dev.zio"     %% "zio-redis"           % "0.0.0+381-86c20614-SNAPSHOT",
    "dev.zio"     %% "zio"                 % Version.zio,
    "dev.zio"     %% "zio-schema"          % Version.`zio-schema`,
    "dev.zio"     %% "zio-schema-protobuf" % Version.`zio-schema`
  )
}
