import com.jsuereth.sbtpgp.SbtPgp
import com.jsuereth.sbtpgp.SbtPgp.autoImport._
import sbt.Keys._
import sbt.{Def, _}

object ScalawagPublishing extends AutoPlugin {
  override def requires = plugins.JvmPlugin && SbtPgp && Sonatype
  override def trigger = allRequirements

  private def travisFail(msg: String): Option[String] =
    sys.env.get("TRAVIS") match {
      case Some("true") => throw new Exception(msg)
      case _ => None
    }

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    Test / publishArtifact := false,

    sonatypeProfileName := "org.scalawag",
    publishTo := sonatypePublishTo.value,

    pomIncludeRepository := { _ => false },
    homepage := Some(url(s"https://github.com/scalawag/${name.value}")),
    startYear := Some(2018),
    licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    scmInfo := Some(ScmInfo(
      url("https://github.com/scalawag/${name.value}"),
      "scm:git:git://github.com/scalawag/${name.value}.git"
    )),
    developers := List(
      Developer("justinp", "Justin Patterson", "justin@scalawag.org", url("https://github.com/justinp"))
    ),
    useGpg := false,
    usePgpKeyHex("439444E02ED9335F91C538455283F6A358FB8629"),
    pgpPublicRing := baseDirectory.value / "project" / "public.gpg",
    pgpSecretRing := baseDirectory.value / "project" / "private.gpg",
    pgpPassphrase := sys.env.get("PGP_PASSPHRASE").orElse(travisFail("missing $PGP_PASSPHRASE")).map(_.toArray),
    credentials ++= {
      for {
        user <- sys.env.get("SONATYPE_USER").orElse(travisFail("missing $SONATYPE_USER"))
        password <- sys.env.get("SONATYPE_PASSWORD").orElse(travisFail("missing $SONATYPE_PASSWORD"))
      } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, password)
    }
  )

}