# Determine the operating system
ifeq ($(OS), Windows_NT)
    # Windows commands and settings
    RM := del /Q /F
    PATH_SEPARATOR := ;
	# Set JavaFX module path
	JFX := ./utils/win/javafx-sdk-21.0.1/lib/
else
    # macOS and Linux commands and settings
    RM := rm -rf
    PATH_SEPARATOR := :
	# Set JavaFX module path
	JFX := ./utils/mac/javafx-sdk-22/lib/
endif

# Set Java compiler
JAVAC := javac

# Set Java runtime
JAVA := java

# Set classpath separator
ifeq ($(OS),Windows_NT)
    CLASSPATH_SEPARATOR := ;
else
    CLASSPATH_SEPARATOR := :
endif

# Default target
run: Main
	$(JAVA) -cp ./src/app$(CLASSPATH_SEPARATOR)bin$(CLASSPATH_SEPARATOR).src/app --module-path $(JFX) --add-modules javafx.controls app.Main

# Build the application
Main:
	$(JAVAC) -d bin src/app/*.java --module-path $(JFX) --add-modules javafx.controls

# Clean up generated files
clean:
	$(RM) store/* store* logs/
	find . -type f -name "*%*" -exec rm -f {} +
