// sbt-build-metadata -- Copyright 2018-2021 -- Justin Patterson
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

sbtPlugin := true

name := "sbt-build-metadata"
organization := "org.scalawag.sbt"

scalaVersion := "2.12.13"

scalacOptions ++= Seq("-feature", "-deprecation")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

ThisBuild / versionScheme := Some("semver-spec")

travisMode := true

// Publishing configuration

Test / publishArtifact := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }
homepage := Some(url("https://github.com/scalawag/sbt-build-metadata"))
startYear := Some(2018)
licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
scmInfo := Some(ScmInfo(
  url("https://github.com/scalawag/sbt-build-metadata"),
  "scm:git:git://github.com/scalawag/sbt-build-metadata.git"
))
developers := List(
  Developer("justinp", "Justin Patterson", "justin@scalawag.org", url("https://github.com/justinp"))
)
useGpg := false
usePgpKeyHex("439444E02ED9335F91C538455283F6A358FB8629")
pgpPublicRing := baseDirectory.value / "project" / "public.gpg"
pgpSecretRing := baseDirectory.value / "project" / "private.gpg"
pgpPassphrase := Some(sys.env.getOrElse("PGP_PASSPHRASE", travisFail("missing $PGP_PASSPHRASE")).toArray)
credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  sys.env.getOrElse("SONATYPE_USER", travisFail("missing $SONATYPE_USER")),
  sys.env.getOrElse("SONATYPE_PASSWORD", travisFail("missing $SONATYPE_PASSWORD"))
)

def travisFail(msg: String) =
  sys.env.get("TRAVIS") match {
    case Some("true") => throw new Exception(msg)
    case _ => ""
  }
