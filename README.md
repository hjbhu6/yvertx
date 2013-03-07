yvertx
======

[yeti](http://mth.github.com/yeti/) language support for vert.x

Note this are still first steps and the module is in development.

The module is for vertx 1.3.1 

building
======

You need JDK7 installed and the VERTX_HOME enviroment variable set to your vertx dir.

in the root of the project dir call:

    java -jar ybuilder.jar retrieveLibs

    java -jar ybuilder.jar clean, vertx:compile


experimenting
======

After you have build yvertx run vertx with from the project root. 

    vertx runmod main

This will start the main module found in the mods directory.

Now you can change the code in the mods/main/server.yeti file and 
the module will autoreload


Examples
========

To see how to code verticles in yeti take a look at the 'samples' directory.

testing
========

The test-module is in mods/test to run it use

    vertx runmod test

