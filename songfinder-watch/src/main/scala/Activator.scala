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
package watch

import com.weiglewilczek.scalamodules._
import org.osgi.framework.{ BundleActivator, BundleContext }

class Activator extends BundleActivator {
  override def start(context: BundleContext) {
    println("[radio/songfinder-watch] is starting")
    import scala.actors.Actor._
    actor {
      loop {
          println("[radio/songfinder-watch] is startig Actor")
          context watchServices withInterface[Songfinder] andHandle {
           case AddingService(songfinder, _) => songfinder.find
           case ServiceRemoved(songfinder, _) => println(songfinder.leaveMsg)
          }
          Thread.sleep(10000)
         }
      println("[radio/songfinder-watch] is stopping Actor")
    }
  }
  override def stop(context: BundleContext) {
    println("[radio/songfinder-watch] is stopping")
  }
}
