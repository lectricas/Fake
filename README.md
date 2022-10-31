# Fake
GNU Make Kotlin clone

### Description

This clone written in Koltin and supoorts incremental build.


This project is done as a test task for the Paddle project:
https://github.com/TanVD/paddle

### Usage

First, you need to create a **jar** artifact with this command:
```sh
./gradlew FakeJar
```

After that you need to create a **fakefile.yaml** near fake bash script.

Then run 
```sh
fake [taskname]
```
to run any task that is inside your **fakefile.yaml**

There is a **testgccSimple** folder with **main.c** source files alongside with **fakefile.yaml**.

To compile this main.c file, you can run 

```sh
fake build
```

#### TODO
 - Negative tests
 - Better YAML file parsing

