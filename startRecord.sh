#!/bin/sh

screen -d -m -S songfinder-lastfm python ../lastfmproxy-1.4d/main.py
sleep 60
screen -d -m -S songfinder-streamripper streamripper http://localhost:1881/lastfm.mp3 -r -d $HOME/Songfinder 

