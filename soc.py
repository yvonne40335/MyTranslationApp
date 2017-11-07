import socket
import sys
import threading
from nltk.stem import WordNetLemmatizer


#############here
#HOST = socket.gethostbyname(socket.gethostname()) #this is your localhost
#172.20.10.9
HOST = '192.168.1.111'
PORT = 80

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#socket.socket: must use to create a socket.
#socket.AF_INET: Address Format, Internet = IP Addresses.
#socket.SOCK_STREAM: two-way, connection-based byte streams.
print 'socket created'

#Bind socket to Host and Port
try:
    s.bind((HOST, PORT))
    print HOST
except socket.error as err:
    print 'Bind Failed, Error Code: ' + str(err[0]) + ', Message: ' + err[1]
    sys.exit()
 
print 'Socket Bind Success!'
wordnet_lemmatizer = WordNetLemmatizer()
#print wordnet_lemmatizer.lemmatize('assume')
 


 
#listen(): This method sets up and start TCP listener.
s.listen(10)
print 'Socket is now listening'
    

while 1:
    conn, addr = s.accept()
    print 'Connect with ' + addr[0] + ':' + str(addr[1])
    buf = conn.recv(1024)

    if(buf[0]=="v"):   
        tmp = wordnet_lemmatizer.lemmatize(buf.split(" ")[1],'v')
    else:
        tmp = wordnet_lemmatizer.lemmatize(buf.split(" ")[1],'n')
    print tmp
    if len(tmp)>9:
        conn.send("2"+str(len(tmp))+tmp)
    else:
        conn.send("1"+str(len(tmp))+tmp)
    
