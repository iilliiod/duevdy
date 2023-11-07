# Determine the operating system
ifeq ($(OSTYPE), msys)
    # Windows commands and settings
    RM := del /Q /F
    PATH_SEPARATOR := ;
else
    # macOS and Linux commands and settings
    RM := rm -rf
    PATH_SEPARATOR := :
endif

# Set Java compiler
JAVAC := javac

# Set Java runtime
JAVA := java

# Set JavaFX module path
JFX_SDK_PATH := ./utils/lib/

# Set classpath separator
ifeq ($(OS),Windows_NT)
    CLASSPATH_SEPARATOR := ;
else
    CLASSPATH_SEPARATOR := :
endif

# Default target
run: Main
	$(JAVA) -cp ./src/app$(CLASSPATH_SEPARATOR)bin$(CLASSPATH_SEPARATOR).src/app --module-path $(JFX_SDK_PATH) --add-modules javafx.controls app.Main

# Build the application
Main:
	$(JAVAC) -d bin src/app/*.java --module-path $(JFX_SDK_PATH) --add-modules javafx.controls

# Clean up generated files
clean:
	$(RM) store/* store* logs/
	find . -type f -name "*%*" -exec rm -f {} +

##############################
# original makefile contents #
##############################
# run : Main 
# 	java -cp ./src/app:bin:/src/app --module-path $(JFX_SDK_PATH) --add-modules javafx.controls app.Main
#
# Main : 
# 	javac -d bin src/app/*.java --module-path $(JFX_SDK_PATH) --add-modules javafx.controls
#
# clean :
# 	rm -rf store/* store* logs/; find . -type f -name "*%*" -exec rm -f {} +
