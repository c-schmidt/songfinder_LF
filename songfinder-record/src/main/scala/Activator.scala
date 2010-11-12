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
package record

import com.weiglewilczek.scalamodules._
import org.osgi.framework.{ BundleActivator, BundleContext }

class Activator extends BundleActivator {
  override def start(context: BundleContext) {
    println("[radio/songfinder-record] is starting")
    import java.io._
    import java.util._
    println("[radio/songfinder-record] you have one minute to login with your browser")
    val process = Runtime.getRuntime().exec("./../" + Config.start)
    val r = new BufferedReader(new InputStreamReader(process.getInputStream()))
    while ((r.readLine()) != null) {}
    r.close()
    println("[radio/songfinder-record] lastfm-proxy and streamripper are working")
  }
  override def stop(context: BundleContext) {
    println("[radio/songfinder-record] lastfm-proxy and streamripper are stopping")
    import java.io._
    import java.util._
    val process = Runtime.getRuntime().exec("./../" + Config.stop)
    val r = new BufferedReader(new InputStreamReader(process.getInputStream()))
    while ((r.readLine()) != null) {}
    r.close()
    println("[radio/songfinder-record] is stopping")
  }
}
