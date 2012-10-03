yvertx
======

[yeti](http://mth.github.com/yeti/) language support for vert.x

building
======

You need JDK7 installed and the VERTX_HOME enviroment variable set to your vertx dir.

in the root of the project dir call:

>    >java -jar ybuilder.jar clean, jar

This will create the yvertx.jar in the 'target' dir.


install
=======

Copy the 'target/vertx-lang-yeti-deploy.jar' to your vert.x 'lib' directory.

Put the 'target/yvertx.jar' and 'lib/managed/compile/yeti-xxxx.jar' on your
vericles classpath (either with the -cp option with vertx run or in you modules
lib directory)

or

If you just want to take a quick look copy the 'target/yvertx.jar' and 
the 'lib/managed/compile/yeti-xxxx.jar' to your verx 'lib' directory.

Than you can run the samples 

>   vertx run echoClient.yeti       

However this is not recommended because yvertx is in development and from time
to time there are new yeti versions and if you copy them to the verx/lib directory
than all modules share the same which might cause compatibility problems.


Examples
========

To see how to code verticles in yeti take a look at the 'samples' directory.

