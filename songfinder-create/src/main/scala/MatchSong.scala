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

package org.unsane.radio.songfinder
package create

import Config._

class MatchSong {
  private[this] val emptySonglist = <songs></songs>

  import scala.xml.XML
  val wishSongs = synchronized {
    loadWishSongsXMLFile \ "title" map (radioPath + "/" + _.text + ".mp3")
  } toList

  private[this] def loadWishSongsXMLFile(): scala.xml.Elem =
    try {
      XML.loadFile(wishSongsPath)
    }
    catch {
      case e: java.io.FileNotFoundException =>
        println("[radio/songfinder-create] File does not exist, try to solve the Problem")
        XML.save(wishSongsPath,emptySonglist,"UTF-8",true,null)
        emptySonglist // loadWishSongsXMLFile
    }

  def loadPlayedSongs() :List[Song]= {
    import java.io.File
    val playedSongsList = (new File(radioPath) listFiles).toList map {
      _.toString
    }
    val playedSongs = playedSongsList map {
      new Song(_)
    }
    playedSongs
  }

  val playedSongs = loadPlayedSongs()
  def songs = playedSongs filter (wishSongs contains _.name)
}
