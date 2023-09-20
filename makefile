run : Main 
	java -cp bin --module-path $$PATH_TO_FX --add-modules javafx.controls app.Main

Main : 
	javac -d bin src/app/*.java --module-path $$PATH_TO_FX --add-modules javafx.controls

clean :
	rm -rf store/* store*; find . -type f -name "*%*" -exec rm -f {} +
