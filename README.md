### TetrisFX
Tetris game engine using the JavaFX platform.

<img src="https://user-images.githubusercontent.com/17854950/89811297-c05d0b80-db3e-11ea-9931-d61e134e61e7.png" width="250px"> <img src="https://user-images.githubusercontent.com/17854950/89811323-ca7f0a00-db3e-11ea-8aa5-b3a7ce90b58f.png" width="250px">

### Building .jar package
To compile and run this source, you need an implementation of  [Java Development Kit](https://cs.wikipedia.org/wiki/JDK) at least version 11 and appropriate [JavaFX](https://gluonhq.com/products/javafx) installed in your system.

Firstly create environmental variable containing path to `javafx/lib` for later usage:
```
$ FX_LIB="/path/to/javafx/lib"
```

Then compile sources into the build directory:
```
$ javac -d ./build --module-path=$FX_LIB --add-modules=javafx.controls src/*.java
```

Finally create a .jar package:
```
$ cd build
$ jar cvfm TetrisFX.jar ../src/Manifest.txt *
```

Now you can run application using created **TetrisFX.jar** file:
```
$ java --module-path=$FX_LIB --add-modules=javafx.controls -jar TetrisFX.jar
```
