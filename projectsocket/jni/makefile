# Define a variable for classpath
CLASS_PATH = ../bin
TARGET_PATH = ../target/classes
JAVA_PATH = /usr/lib/jvm/java-8-oracle/include
# Debug flags:
# -g3=compile with extra debugg infos. 
# -ggdbg3=include things like macro defenitions. 
# -O0=turn off optimizations.
# -Wall = With all warnings.
DEBUGFLAGS = -g3 -ggdb3 -O0 -Wall

CFLAGS = `pkg-config --cflags gtk+-3.0 webkit2gtk-4.0` $(DEBUGFLAGS)
LIBS = `pkg-config --libs gtk+-3.0 webkit2gtk-4.0`

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all : arserverdetector.so

####### HelloJNI snippet
# $@ matches the target, $< matches the first dependancy
arserverdetector.so : ArServerDetectorJni.o
	gcc $(CFLAGS) -W -shared $< $(LIBS) -o $@
	

# $@ matches the target, $< matches the first dependancy
ArServerDetectorJni.o : ArServerDetectorJni.c ArServerDetectorJni.h
	gcc $(CFLAGS) -fPIC -I$(JAVA_PATH) -I$(JAVA_PATH)/linux -c $< $(LIBS) -o $@

# $* matches the target filename without the extension
# manually this would be: javah -classpath ../bin HelloJNI
ArServerDetectorJni.h : 
	@echo $(TARGET_PATH)
	javah -classpath $(TARGET_PATH) -o ArServerDetectorJni.h org.oa.websocket.jni.ArServerDetectorJni


clean :
	rm -f ArServerDetectorJni.h ArServerDetectorJni.o arserverdetector.so 