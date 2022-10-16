import scala.language.postfixOps
import scala.sys.process._

organization := "org.scorexfoundation"

name := "sigma-state"

lazy val scala213 = "2.13.8"
lazy val scala212 = "2.12.15"
lazy val scala211 = "2.11.12"

lazy val allConfigDependency = "compile->compile;test->test"

lazy val commonSettings = Seq(
  organization := "org.scorexfoundation",
  crossScalaVersions := Seq(scala213, scala212, scala211),
  scalaVersion := scala211,
  resolvers += Resolver.sonatypeRepo("public"),
  licenses := Seq("CC0" -> url("https://creativecommons.org/publicdomain/zero/1.0/legalcode")),
  homepage := Some(url("https://github.com/ScorexFoundation/sigmastate-interpreter")),
  description := "Interpreter of a Sigma-State language",
  pomExtra :=
      <developers>
        <developer>
          <id>kushti</id>
          <name>Alexander Chepurnoy</name>
          <url>http://chepurnoy.org/</url>
        </developer>
        <developer>
          <id>aslesarenko</id>
          <name>Alexander Slesarenko</name>
          <url>https://github.com/aslesarenko/</url>
        </developer>
        <developer>
          <id>greenhat</id>
          <name>Denys Zadorozhnyi</name>
          <url>https://github.com/greenhat/</url>
        </developer>
      </developers>,
  publishMavenStyle := true,
  publishTo := sonatypePublishToBundle.value,
  scmInfo := Some(
      ScmInfo(
          url("https://github.com/ScorexFoundation/sigmastate-interpreter"),
          "scm:git@github.com:ScorexFoundation/sigmastate-interpreter.git"
      )
  ),
)

// prefix version with "-SNAPSHOT" for builds without a git tag
dynverSonatypeSnapshots in ThisBuild := true
// use "-" instead of default "+"
dynverSeparator in ThisBuild := "-"

val bouncycastleBcprov = "org.bouncycastle" % "bcprov-jdk15on" % "1.66"
val scrypto            = "org.scorexfoundation" %% "scrypto" % "2.2.1-25-dafca54c-SNAPSHOT"
//val scorexUtil         = "org.scorexfoundation" %% "scorex-util" % "0.1.8"
//val debox              = "org.scorexfoundation" %% "debox" % "0.9.0"
val scorexUtil         = "org.scorexfoundation" %% "scorex-util" % "0.1.8-19-0331a3d9-SNAPSHOT"
val debox              = "org.scorexfoundation" %% "debox" % "0.9.0-8-3da95c40-SNAPSHOT"
val spireMacros        = "org.typelevel" %% "spire-macros" % "0.17.0-M1"
val fastparse          = "com.lihaoyi" %% "fastparse" % "2.3.3"
val scalaCompat        = "org.scala-lang.modules" %% "scala-collection-compat" % "2.7.0"

lazy val circeCore211 = "io.circe" %% "circe-core" % "0.10.0"
lazy val circeGeneric211 = "io.circe" %% "circe-generic" % "0.10.0"
lazy val circeParser211 = "io.circe" %% "circe-parser" % "0.10.0"

lazy val circeCore = "io.circe" %% "circe-core" % "0.13.0"
lazy val circeGeneric = "io.circe" %% "circe-generic" % "0.13.0"
lazy val circeParser = "io.circe" %% "circe-parser" % "0.13.0"

def circeDeps(scalaVersion: String) = if (scalaVersion == scala211)
  Seq(circeCore211, circeGeneric211, circeParser211)
else
  Seq(circeCore, circeGeneric, circeParser)


val testingDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.scalactic" %% "scalactic" % "3.2.14" % Test,
  "org.scalacheck" %% "scalacheck" % "1.15.2" % Test,          // last supporting Scala 2.11
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.3.0" % Test, // last supporting Scala 2.11
  "com.lihaoyi" %% "pprint" % "0.6.3" % Test,
  "com.storm-enroute" %% "scalameter" % "0.19" % Test,
  "junit" % "junit" % "4.12" % Test
)

lazy val testSettings = Seq(
  libraryDependencies ++= testingDependencies,
  parallelExecution in Test := false,
  baseDirectory in Test := file("."),
  publishArtifact in Test := true,
  publishArtifact in(Test, packageSrc) := true,
  publishArtifact in(Test, packageDoc) := false,
  test in assembly := {})

libraryDependencies ++= Seq(
  scrypto,
  scorexUtil,
  "org.bouncycastle" % "bcprov-jdk15on" % "1.+",
  fastparse, debox, spireMacros, scalaCompat
) ++ testingDependencies ++ circeDeps(scalaVersion.value)

scalacOptions ++= Seq("-feature", "-deprecation")

parallelExecution in Test := false
publishArtifact in Test := true

pomIncludeRepository := { _ => false }

val credentialFile = Path.userHome / ".sbt" / ".sigma-sonatype-credentials"
credentials ++= (for {
  file <- if (credentialFile.exists) Some(credentialFile) else None
} yield Credentials(file)).toSeq

credentials ++= (for {
  username <- Option(System.getenv().get("SONATYPE_USERNAME"))
  password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq


// PGP key for signing a release build published to sonatype
// signing is done by sbt-pgp plugin
// how to generate a key - https://central.sonatype.org/pages/working-with-pgp-signatures.html
// how to export a key and use it with Travis - https://docs.scala-lang.org/overviews/contributors/index.html#export-your-pgp-key-pair
pgpPublicRing := file("ci/pubring.asc")
pgpSecretRing := file("ci/secring.asc")
pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toArray)
usePgpKeyHex("C1FD62B4D44BDF702CDF2B726FF59DA944B150DD")

def libraryDefSettings = commonSettings ++ testSettings 

lazy val common = Project("common", file("common"))
  .settings(libraryDefSettings,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      debox, scalaCompat
    ))
  .settings(publish / skip := true)

lazy val libraryimpl = Project("library-impl", file("library-impl"))
  .dependsOn(common % allConfigDependency)
  .settings(libraryDefSettings,
    libraryDependencies ++= Seq( debox, scrypto ))
  .settings(publish / skip := true)

//lazy val core = Project("core", file("core"))
//  .dependsOn(common % allConfigDependency, libraryimpl % allConfigDependency)
//  .settings(libraryDefSettings,
//    libraryDependencies ++= Seq( debox ))
//  .settings(publish / skip := true)

lazy val graphIR = Project("graph-ir", file("graph-ir"))
  .dependsOn(common % allConfigDependency, libraryimpl)
  .settings(
    libraryDefSettings,
    libraryDependencies ++= Seq( debox, scrypto, bouncycastleBcprov ))
  .settings(publish / skip := true)

lazy val sigmastate = (project in file("sigmastate"))
  .dependsOn(graphIR % allConfigDependency)
  .settings(libraryDefSettings)
  .settings(libraryDependencies ++=
      Seq(scorexUtil, fastparse) ++ circeDeps(scalaVersion.value)
  )
  .settings(publish / skip := true)

lazy val ergoscript = (project in file("ergoscript"))
  .dependsOn(graphIR % allConfigDependency)
  .settings(libraryDefSettings)
  .settings(libraryDependencies ++=
      Seq(scorexUtil, fastparse) ++ circeDeps(scalaVersion.value)
  )
  .settings(publish / skip := true)

lazy val sigma = (project in file("."))
  .aggregate(sigmastate, common, libraryimpl, graphIR, ergoscript)
  .settings(libraryDefSettings, rootSettings)
  .settings(publish / aggregate := false)
  .settings(publishLocal / aggregate := false)

lazy val aggregateCompile = ScopeFilter(
  inProjects(common, libraryimpl, graphIR, sigmastate, ergoscript),
  inConfigurations(Compile))

lazy val rootSettings = Seq(
  sources in Compile := sources.all(aggregateCompile).value.flatten,
  sourceDirectories in Compile := sourceDirectories.all(aggregateCompile).value.flatten,
  libraryDependencies := libraryDependencies.all(aggregateCompile).value.flatten,
  mappings in (Compile, packageSrc) ++= (mappings in(Compile, packageSrc)).all(aggregateCompile).value.flatten,
  mappings in (Test, packageBin) ++= (mappings in(Test, packageBin)).all(aggregateCompile).value.flatten,
  mappings in(Test, packageSrc) ++= (mappings in(Test, packageSrc)).all(aggregateCompile).value.flatten
)
