import socket
import sys
import thread
import netifaces as ni
import datetime

sock = None
th = None
conn = None
conns = []
addresses = []
verbose = True
server_address = ""

def setVerbose(newVerbose):
	global verbose
	verbose = newVerbose

def connect(ip, port):
	global server_address, sock
	try:
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

		server_address = (ip, port)
		sock.bind(server_address)

		th = thread.start_new_thread(conn_listener, (None,))

		print 'Starting server on %s port %s' % server_address
	except:
		print "Could not start TCP server:"
		print "\t" + str(sys.exc_info())

def disconnect():
	global sock
	global th
	try:
		sock.close()
	except:
		pass
	sock = None
	th = None
	
def send(data):
	global sock, conns
	for conn in conns:
		try:
			conn.send(data)
		except:
			pass

def conn_listener(arg):
	global conn, sock, enabled, conns
	#try:
	while(True):
		sock.listen(1)
		conn, addr = sock.accept()
		conns.append(conn)
		addresses.append(str(addr[0]))
		thread.start_new_thread(listener, (conn, addr,))

def listener(conn, addr):
	global verbose, addresses
	print "App Client connected: " + str(addr[0]) + "\n"
	while True:
		data = conn.recv(1024)

		now = str(datetime.datetime.utcnow())
		if(verbose):
			print now + " Received from app: " + data
			print ""

		if not data:
			print "App client disconnected: " + str(addr[0]) + "\n"
			conn.close()
			conns.remove(conn)
			addresses.remove(str(addr[0]))
			break		   
	#except:
		#print "leaving TCP listener"
