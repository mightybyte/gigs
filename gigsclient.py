#/usr/bin/python

# run with tcptron.py 213.3.30.106 9999 username ./MyTronBot
# see http://www.benzedrine.cx/tron.html for ELO ratings.

import fcntl
import os
import signal
import socket
import sys

from threading import Thread, Event
from Queue import Queue, Empty
from subprocess import Popen, PIPE

class _ProcCom(Thread):
    def __init__(self, proc):
        Thread.__init__(self)
        self.proc = proc
        self.outq = Queue()
        self.stop = Event()
        self.setDaemon(True)

    def run(self):
        while not self.stop.isSet() and self.proc.poll() is None:
            msg = self.proc.stdout.readline()
            self.outq.put(msg)

class Engine:
    def __init__(self, cmdline):
        self.proc = Popen(cmdline, stdin = PIPE, stdout = PIPE)
        self.proc_com = _ProcCom(self.proc)
        self.proc_com.start()

    def send(self, msg):
        self.proc.stdin.write(msg)
        self.proc.stdin.flush()

    def readline(self, timeout=None):
        if self.proc.poll():
            return ""
        try:
            msg = self.proc_com.outq.get(timeout=timeout)
        except Empty:
            raise socket.timeout()
        return msg

    def cleanup(self):
        self.proc_com.stop.set()
        if self.proc.poll() is None:
            if sys.platform == 'win32':
                import ctypes
                handle = int(self.proc._handle)
                ctypes.windll.kernel32.TerminateProcess(handle, 0)
            else:
                os.kill(self.proc.pid, signal.SIGTERM)

class Server:
    def __init__(self, address):
        self.sock = socket.socket()
        self.sock.connect(address)
        self.buf = []
        self.partial = ""

    def send(self, msg):
        self.sock.sendall(msg)

    def readline(self, timeout=None):
        if len(self.buf) == 0:
            self.sock.settimeout(timeout)
            packet = self.partial + self.sock.recv(4096)
            self.buf = packet.splitlines(True)
            if self.buf == []:
                return ""
#            if self.buf[-1][-1] not in "\n\r":
#                self.partial = self.buf.pop()
#            else:
#                self.partial = ""
        return self.buf.pop(0)

    def cleanup(self):
        self.sock.close()

#server_address = (sys.argv[1], int(sys.argv[2]))
#cmdline = sys.argv[3:]
server_address = ("127.0.0.1", 8000)
cmdline = "mytron/MyTronBot"

fd = sys.stdin.fileno()
fl = fcntl.fcntl(fd, fcntl.F_GETFL)
fcntl.fcntl(fd, fcntl.F_SETFL, fl | os.O_NONBLOCK)

engine = Engine(cmdline)
server = Server(server_address)
print "connected to %s, waiting for game" % (server_address,)
while True:
    try:
        line = server.readline(0.01)
        if line != "":
            sys.stdout.write(line)
            sys.stdout.flush()
            if line.startswith("~~~"):
                engine.send(line[3:])
    except socket.timeout:
        pass
    except:
      print "socket exception"

    try:
        line = engine.readline(0)
        if line == "":
            break
        server.send(line)
    except socket.timeout:
        pass
    except:
      print "engine exception"

#    userInput = os.read(sys.stdin.fileno(), 256)
    try:
        userInput = sys.stdin.read()
        if userInput != "":
            server.send(userInput)
    except: pass

server.cleanup()
engine.cleanup()

