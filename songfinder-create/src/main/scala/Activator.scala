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

import com.weiglewilczek.scalamodules._
import org.osgi.framework.{ BundleActivator, BundleContext }
import Config._


class Activator extends BundleActivator {
  override def start(context: BundleContext) {
    val songfinder = new Songfinder {
      override def find = {
        checkConfig
        new MatchSong songs match {
         case Nil =>
           println("[radio/songfinder-create] no songs to record found")
           println("[radio/songfinder-create] clean radio-directory")
           import java.io._
           val songsToDel = (new File(radioPath) listFiles).toList map {
              _.toString
           }
           songsToDel map {
             song => new ProcessBuilder(del,song).start
           }
         case songs =>
          println( "[radio/songfinder-create] " + (songs map (_.name)) + " found")
          context watchServices withInterface[Songsaver] andHandle {
            case AddingService(songsaver,  _) => songsaver songsToSave songs
            case ServiceRemoved(songsaver, _) => println(songsaver.leaveMsg)
          }
        }
      }

      override def remove(title: String) {
        println("[radio/songfinder-create] clean WishSongs")
        synchronized {
          import scala.xml.XML
          val wishList = <songs> {
             XML.loadFile(wishSongsPath) \ "title" filter
              (_.text != title)
             } </songs>
          XML.save(wishSongsPath,wishList,"UTF-8",true,null)
        }
      }
      override def leaveMsg = "[radio/songfinder-create] shutdown now!"
    }
    context createService songfinder
  }
  override def stop(context: BundleContext) {}
}
