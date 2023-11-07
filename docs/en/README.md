# Build Instructions

The project uses `GNU make` for the build process.

If `make` is already installed on your system you can skip the first step.

## Step 1: Install `make`

### Windows

> It's recommended to download chocolatey (a package manager for Windows) as a much easier way to get started.
>
> Install [chocolatey](https://chocolatey.org/install).

##### chocolatey:

Run `choco install make` to install `GNU make` with chocolatey.

##### not chocolatey:

I'm lazy right now so you're just gonna have to GTSY.

### Mac

> The easiest way is to install `make` with Homebrew.

##### Homebrew:

`brew install make`

## Step 2: Run the code

Once `make` is installed, running the code is easy. Simply navigate to the project directory and run `make`.

When running correctly, the command line will look something like this:

```shell
javac -d bin src/app/*.java --module-path ./utils/lib/ --add-modules javafx.controls
java -cp ./src/app:bin:.src/app --module-path ./utils/lib/ --add-modules javafx.controls app.Main
```

# Bug Report

At present, the program is quite buggy (I'm sorry, I kinda suck 😥).

A major bug to note is the fact that the `Course Name` field can only accept a single non-space-separated string value.

If more than one space-separated string value is entered, the program will literally crumble into a million pieces on relaunch.
(Hyperbole, it'll just throw a parse error.)

If you do, however, choose to not heed instructions and try it anyway, a quick hack around the problem is to delete the `store` folder
and all its contents.
