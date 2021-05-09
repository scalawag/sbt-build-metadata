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

package org.scalawag.sbt.buildmetadata

import com.typesafe.sbt.SbtGit.git.{gitCurrentBranch, gitCurrentTags, gitHeadCommit, gitHeadCommitDate, gitUncommittedChanges}
import com.typesafe.sbt.SbtGit.useJGit
import sbt.{AutoPlugin, Def, File, plugins, settingKey}
import sbt.Keys.{name, organization, resourceGenerators, resourceManaged, version}
import sbt.io.IO
import sbt._

object BuildMetadataPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {
    val buildMetadata = settingKey[File]("generated metadata about this build")
  }

  import autoImport._

  override def projectSettings = Seq(
    useJGit,
    buildMetadata := (Compile / resourceManaged).value / "META-INF" / "build.properties",
    Compile / resourceGenerators += Def.task {
      IO.write(buildMetadata.value, s"""
        |build-metadata.version = ${version.value}
        |sbt.organization = ${organization.value}
        |sbt.name = ${name.value}
        |sbt.version = ${version.value}
        |git.branch = ${gitCurrentBranch.value}
        |git.tags = ${gitCurrentTags.value.mkString(" ")}
        |git.commit = ${gitHeadCommit.value.getOrElse("")}
        |git.timestamp = ${gitHeadCommitDate.value.getOrElse("")}
        |git.uncommittedChanges = ${gitUncommittedChanges.value}
        |build.timestamp = ${java.time.Instant.now}
      """.stripMargin.trim)
      Seq(buildMetadata.value)
    }.taskValue
  )
}
