#!/usr/bin/env python

import time
import Server

Server.connect('wlan0', 2000)

while True:
	
	time.sleep(1)
