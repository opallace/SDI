# GNU Makefile

JC = javac
JFLAGS = #-Xlint
TARGET = Server.class Client.class

all: $(TARGET)

Server.class: Server.java
	$(JC) $(JFLAGS) Server.java 

Client.class: Client.java
	$(JC) $(JFLAGS) Client.java 

clean:
	rm -f *~ $(TARGET)
