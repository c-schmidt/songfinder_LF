/**
 * Copyright (c) 2010 Christoph Schmidt
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of his contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import com.weiglewilczek.bnd4sbt.BNDPlugin
import sbt._

class RadioParentProject(info: ProjectInfo) extends ParentProject(info) {

  // ===================================================================================================================
  // Dependencies
  // ===================================================================================================================

  object Dependencies {

    // Versions
    val osgiVersion = "4.2.0"
    val paxExamVersion = "1.2.0"

    // Compile
    val scalaModulesCore = "com.weiglewilczek.scalamodules" %% "scalamodules-core" % projectVersion.value.toString

    // Provided
    val osgiCore = "org.osgi" % "org.osgi.core" % osgiVersion % "provided"
  }

  // ===================================================================================================================
  // songfinder-api subproject
  // ===================================================================================================================

  val songfinderAPIProject = project("songfinder-api", "radio-songfinder-api", new SongfinderAPIProject(_))

  class SongfinderAPIProject(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    override def bndExportPackage =
      "org.unsane.radio.songfinder;version=%s".format(projectVersion.value) :: Nil
    override def bndVersionPolicy = Some("[$(@),$(version;=+;$(@)))")
  }

  // ===================================================================================================================
  // songfinder-create subproject
  // ===================================================================================================================

  val songfinderCreateProject =
    project("songfinder-create", "radio-songfinder-create", new SongfinderCreateProject(_), songfinderAPIProject)

  class SongfinderCreateProject(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    import Dependencies._
    override def libraryDependencies = Set(scalaModulesCore, osgiCore)
    override def bndBundleActivator = Some("org.unsane.radio.songfinder.create.Activator")
    override def bndImportPackage =
      "org.unsane.radio.songfinder.*;version=\"[%1$s,%1$s]\"".format(projectVersion.value) ::
      super.bndImportPackage.toList
    override def bndVersionPolicy = Some("[$(@),$(version;=+;$(@)))")
  }
  
  // ===================================================================================================================
  // songfinder-watch subproject
  // ===================================================================================================================

  val songfinderWatchProject =
    project("songfinder-watch", "radio-songfinder-watch", new SongfinderWatchProject(_), songfinderAPIProject)

  class SongfinderWatchProject(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    import Dependencies._
    override def libraryDependencies = Set(scalaModulesCore, osgiCore)
    override def bndBundleActivator = Some("org.unsane.radio.songfinder.watch.Activator")
    override def bndImportPackage =
      "org.unsane.radio.songfinder.*;version=\"[%1$s,%1$s]\"".format(projectVersion.value) ::
      super.bndImportPackage.toList
    override def bndVersionPolicy = Some("[$(@),$(version;=+;$(@)))")
  }

  // ===================================================================================================================
  // songfinder-save subproject
  // ===================================================================================================================

  val songfinderSaveProject =
    project("songfinder-save", "radio-songfinder-save", new SongfinderSaveProject(_), songfinderAPIProject)

  class SongfinderSaveProject(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    import Dependencies._
    override def libraryDependencies = Set(scalaModulesCore, osgiCore)
    override def bndBundleActivator = Some("org.unsane.radio.songfinder.save.Activator")
    override def bndImportPackage =
      "org.unsane.radio.songfinder.*;version=\"[%1$s,%1$s]\"".format(projectVersion.value) ::
      super.bndImportPackage.toList
    override def bndVersionPolicy = Some("[$(@),$(version;=+;$(@)))")
  }

  // ===================================================================================================================
  // songfinder-record subproject
  // ===================================================================================================================

  val songfinderRecordProject =
    project("songfinder-record", "radio-songfinder-record", new SongfinderRecordProject(_), songfinderAPIProject)

  class SongfinderRecordProject(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    import Dependencies._
    override def libraryDependencies = Set(scalaModulesCore, osgiCore)
    override def bndBundleActivator = Some("org.unsane.radio.songfinder.record.Activator")
    override def bndImportPackage =
      "org.unsane.radio.songfinder.*;version=\"[%1$s,%1$s]\"".format(projectVersion.value) ::
      super.bndImportPackage.toList
    override def bndVersionPolicy = Some("[$(@),$(version;=+;$(@)))")
  }
}
