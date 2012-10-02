yvertx
======

[yeti](http://mth.github.com/yeti/) language support for vert.x

building and installing
=====

You need JDK7 installed and the VERTX_HOME enviroment variable set to your vertx dir.

in the root of the project dir call:

>    >java -jar ybuilder.jar clean, jar

This will create the yvertx.jar in the 'target' dir.

copy the yvertx.jar and the yeti.jar from 'lib/managed/compile' to your vert.x lib directory.

now you can run uncompiled (or compiled) yeti modules.

>   >vertx run myModule.yeti

for examples of yeti modules look at 'samples' directory.

