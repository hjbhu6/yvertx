yvertx
======

[yeti](http://mth.github.com/yeti/) language support for vert.x

Note this are still first steps and the module is in development.

The module is also compile to vert.x to the vert.x version which is in current
vert.x master (higher than 1.2.3final)

building
======

You need JDK7 installed and the VERTX_HOME enviroment variable set to your vertx dir.

in the root of the project dir call:

    java -jar ybuilder.jar clean, vertx-module

This will create the vertx module `org.yvertx.yvertx-yeti-lang-impl-vXXX` in 
the `target` directory.


install for vert.x versions higher than 1.2.3.final (including current master)
=======

1.) add following line to your `%VERTX_HOME%/conf/langs.properties` file:
    
    yeti=org.yvertx.deploy.YetiVerticleFactory

2.) copy the `org.yvertx.yvertx-yeti-lang-impl-vXXX` from the `target`
direcotory to your `mod` directory

3.) add a dependency to your mod.json file to the yvertx module:
    
    {
       main: "myapp.yeti",
       includes: "org.yvertx.yvertx-yeti-lang-impl-vXXX"
    }    

4.) run your module as described in vertx

install for vert.x 1.2.3.final  
=======

This should work but is not tested:

Copy the 'target/vertx-lang-yeti-deploy.jar' to your vert.x 'lib' directory.

Install the module as described in steps 2. to 4. above;


Examples
========

To see how to code verticles in yeti take a look at the 'samples' directory.

