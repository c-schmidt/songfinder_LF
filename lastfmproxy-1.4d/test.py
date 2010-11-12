#!/usr/bin/python

import socket

print socket.getaddrinfo("www.python.org", 80, 0, 0, socket.SOL_TCP)
bind_address = "nora"
# 192.168.178.79
print bind_address.find(".") > -1
if bind_address.find(".") <0 :
  tupel = socket.getaddrinfo(bind_address,1881)
  #print tupel
  #print tupel[0]
  print tupel[0][4][0]
else :
  print bind_address
print "done."
