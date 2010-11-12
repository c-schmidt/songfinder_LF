
import httpclient
import time
import datetime
import string
import playlist
import sys
import hashlib
import webbrowser
import xml.dom.minidom
import os.path
import config

class lastfm:

    def __init__(self):
        self.version = "1.5.4.24567"
        self.platform = "win32"
        self.platformversion = "Windows%20XP"
        self.player = "LastFMProxy"
        self.api_key = config.api_key
	self.api_secret = config.api_secret
        self.host = "ws.audioscrobbler.com"
        self.port = 80
        self.info = None
        self.playlist = playlist.playlist()
        self.debug = 0
	self.tmpResp = None
	self.sessionKey = None
	self.s = None

    def parselines(self, str):
        res = {}
        vars = string.split(str, "\n")
        numerrors = 0
        for v in vars:
            x = string.split(string.rstrip(v), "=", 1)
            if len(x) == 2:
                res[x[0]] = x[1]
            elif x != [""]:
                print "(urk?", x, ")"
                numerrors = numerrors + 1
            if numerrors > 5:
                print "Too many errors parsing response."
                return {}
        return res

    def parselinesXml(self, str):
        res = {}
	dom = xml.dom.minidom.parseString(str)
	numerrors = 0
	
	def getText(nodelist):
           rc = ""
           for node in nodelist:
              if node.nodeType == node.TEXT_NODE:
                  rc = rc + node.data
           return rc
	   
	def handleSession(sessions, res):
	   for session in sessions:
	      if session.hasChildNodes():
		 #n = session
	         for n in session.childNodes:
		    if n.nodeType == n.ELEMENT_NODE:
		       value = getText(n.childNodes)
		       if self.debug:
	                  sys.stderr.write( "name="+n.tagName+" has value="+value+"\n")
	                  print "name="+n.tagName+" has value="+value
		       res[n.tagName] = value
	   return res
	
	def handleError(errors):
	    for e in errors:
	       sys.stderr.write("error: "+e.getAttribute("code")+e.childNodes[0].data+"\n") 
	       print "error: "+e.getAttribute("code")+e.childNodes[0].data
	    return
	
	def handleLfm(lfm, res, numerrors):
	   status = lfm.getAttribute("status")
	   if status != "ok":
	      numerrors = numerrors + 1
	   res["status"] = status
	   session = lfm.getElementsByTagName("session")
	   if session.length > 0:
	      res = handleSession(session, res)
	   error = lfm.getElementsByTagName("error")
	   if error.length > 0:
	      numerrors = numerrors + error.length
	      handleError(error)
	   return res

        handleLfm(dom.getElementsByTagName("lfm")[0], res, numerrors)
	
	if numerrors > 1:
	   sys.stderr.write("status is not OK.\n")
	   print "Status is not OK."
	   return {}
        return res


    def connect(self, username, password):

        s = httpclient.httpclient(self.host, self.port)
        s.req("/radio/handshake.php?version=" + self.version + "&platform=" + self.platform + "&username=" + username + "&passwordmd5=" + password + "&language=en&api_key=" + self.api_key + "&player=" + self.player)

        self.info = self.parselines(s.response)

	# if the file does not exist, this is the first usage -> auth for web service needed from user
	# if os.path.isfile(os.path.join(".", "ws-auth.txt"))  == False:
	print "starting web service auth"
	s = httpclient.httpclient(self.host, self.port)
	# s.req("http://www.last.fm/api/auth?api_key="+self.api_key)
        url = "http://www.last.fm/api/auth?api_key="+self.api_key
	print "Please point your browser to the following url for authentication: "
	print "  "+url
	webbrowser.open_new(url)
	#f = open(os.path.join(".", "ws-auth.txt"), "w")
	#now = datetime.datetime.now()
	#f.write(now.strftime("%d/%m/%y"))
	#f.close()
	     
	

    def getplaylist(self):

        if self.debug:
            sys.stderr.write("Fetching playlist...\n")

        s = httpclient.httpclient(self.info["base_url"])
        s.req(self.info["base_path"] + "/xspf.php?sk=" + self.info["session"] + "&discovery=0&desktop=" + self.version)

        self.playlist.parse(s.response)

        # debug
        if self.debug:
            sys.stderr.write("Saving playlist...\n")
            if len(self.playlist.data.tracks):
                f = open("playlist.xspf", "w")
                f.write(s.response)
                f.close()
            elif False:
                print "No playlist?? Using cached version instead..."
                f = open("playlist.xspf", "r")
                cache = f.read()
                f.close()
                self.playlist.parse(cache)
                self.playlist.pos = 0 #len(self.playlist.data.tracks) -1

        return len(self.playlist.data.tracks)

    def command(self, cmd):
        # commands = skip, love, ban, rtp, nortp
	
	# track.love or track.ban
	# track, artist, api_key, api_sig, sessionKey (sk)
	if cmd == "ban" or cmd == "love":
	    if self.debug:
	       sys.stderr.write("Banning or Loving...\n")
	       print "ban or love special"
	    if (self.sessionKey == None):
		sys.stderr.write("NO SESSION KEY\n")
	        print "NO SESSION KEY"
		return {}
	    m = self.playlist.data.tracks[self.playlist.pos]
	    artist = m["creator"]
	    track = m["title"]
	    artist = self.utf8(artist)
	    track = self.utf8(track)
	    if self.debug:
	       sys.stderr.write("artist..:"+artist+"\n")
	       sys.stderr.write("track...:"+track+"\n")
	       print "artist..: "+artist
	       print "track...: "+track 
	    
	    # re-use httpclient
	    s = self.s
	    method =  ""
	    if cmd == "ban":
	       method = "track.ban"
	    elif cmd == "love":
               method = "track.love"
	    else:
		print "unknown method, shouldn't happen."
		
	    # all params except api_sig ordered alphabetically 
	    # for the postdata request
            params = "api_key="+self.api_key+"&artist="+artist+"&method="+method+"&sk="+self.sessionKey+"&track="+track
	    # for the signature without = and ?
	    p = "api_key"+self.api_key+"artist"+artist+"method"+method+"sk"+self.sessionKey + "track"+track	    
	    if self.debug:
	       sys.stderr.write("params are: "+params+"\n")
	       print params
	    
	    # this is a write request -> use postdata
	    url = "http://ws.audioscrobbler.com/2.0/"
	    postdata = params+"&api_sig="+self.sig(p)
	    if self.debug:
	       sys.stderr.write("url: "+url+"\n")
	       sys.stderr.write("postdata: "+postdata+"\n")
	       print url
	       print postdata
	    
	    s.req(url, postdata)
	    
	    resp = s.response
	    if self.debug:
	       sys.stderr.write("response: "+resp+"\n")
	       print resp
	    
	    res = self.parselinesXml(resp)
	    return res

        if self.debug:
            sys.stderr.write("command " + cmd + "\n")

        s = httpclient.httpclient(self.info["base_url"], 80)
        s.req(self.info["base_path"] + "/control.php?command=" + cmd + "&session=" + self.info["session"])
        res = self.parselines(s.response)
        
        if not res.has_key("response") or res["response"] != "OK":
            sys.stderr.write("command " + cmd + " returned:" + repr(res) + "\n")
        
        return res

    def changestation(self, url):
        
        if self.debug:
            sys.stderr.write("changestation " + url + "\n")
        
        s = httpclient.httpclient(self.info["base_url"], 80)
        s.req(self.info["base_path"] + "/adjust.php?session=" + self.info["session"] + "&url=" + url)
        res = self.parselines(s.response)

        if not res.has_key("response") or res["response"] != "OK":
            sys.stderr.write("changestation " + url + " returned:" + repr(res) + "\n")
        
        return res


    def urlencode(self, s):
	#print "urlencode"
        result = ""
        for c in s:
            o = ord(c)
	    if (o >= ord('a') and o <= ord('z')) or (o >= ord('A') and o <= ord('Z')) or (o >= ord('0') and o <= ord('1')):
                result = result + c
            else:
                result = result + ("%%%02x" % o)
        return result

    def urlencode2(self, s):
	#print "urlencode2"
        result = ""
        for c in s:
            o = ord(c)
	    if (o == ord(' ')):
		result = result + '+'
            elif (o >= ord('a') and o <= ord('z')) or (o >= ord('A') and o <= ord('Z')) or (o >= ord('0') and o <= ord('1')):
                result = result + c
            else:
		result = result + c
        return result

    def utf8(self, s):
	#print "utf8"
        result = ""
	u = unicode( s, "utf-8" )
	result = u.encode( "utf-8" )
        return result


    def sig(self, params):
	tmp = params+self.api_secret
        m = hashlib.md5()
	m.update(tmp)
	res = m.hexdigest()
	if self.debug:
	   sys.stderr.write("signing string: "+tmp+"\n")
	   sys.stderr.write("sig: "+res+"\n")
	   print "m.hexdigest is "+res
	
	return res
	
    def wsSession(self, token):
	
	s = httpclient.httpclient(self.host, self.port)
	params = "api_key="+self.api_key+"&method=auth.getSession&token="+token
	p = "api_key"+self.api_key+"methodauth.getSessiontoken"+token
	if self.debug:
	   sys.stderr.write(params+"\n")
	   print params
	   
	#no write request -> can use url-style
	url = "http://ws.audioscrobbler.com/2.0/?"+params+"&api_sig="+self.sig(p)
	if self.debug:
	   sys.stderr.write(url+"\n")
	   print url
	   
	s.req(url)
	resp = s.response
	if self.debug:
	   sys.stderr.write(resp+"\n")
	   print resp
	res = self.parselinesXml(resp)
	sessionKey = res["key"]
	if self.debug:
	   print "sessionKey is "+sessionKey
	self.sessionKey = sessionKey
	self.s = s

        
	return 

