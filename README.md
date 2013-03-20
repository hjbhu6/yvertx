yvertx
======

[yeti](http://mth.github.com/yeti/) language support for vert.x 2.0

As vert.x 2.0 is still in devolopment and there are sometimes api-changes, it
can happen that this project does temperorily not compile until the 
api-changes are followed.

*Your help is needed*. Please report any bugs/suggestions to the vert.x
mailing-list: https://groups.google.com/forum/#!forum/yeti-lang

Documentation
=============

For an introduction and documentation read the 
[manual](https://github.com/chrisichris/yvertx/blob/master/core_manual_yeti.md)

Quickstart
==========

For a quickstart git-clone the [yvertx-project-template](https://github.com/chrisichris/yvertx-project-template)

Language-Module
===============

Yvertx is a language module for vert.x. The module name is:

    com.github.chrisichris~yvertx-module~0.9.8-SNAPSHOT

It is hosted at [sonatype-snapshots-maven-repo](https://oss.sonatype.org/content/repositories/snapshots/com/github/chrisichris/yvertx-module/) 

To add it to you vertx/conf/langs.properties:

    yeti=com.github.chrisichris~yvertx-module~0.9.8-SNAPSHOT:yeb.yvertx.YetiVerticleFactory
    .yeti=yeti
    

Quickstart
==========

Examples
========

A reimplementation of the samples found in the standard vert.x distribution
can be found in the ´samples´ folder.

To run the samples invoke ybuilder from the root of the project.

    >java -jar ybuilder.jar vertx:cmd run echo/echoserver.yeti -dir samples

This will run the echo/echoserver.yeti verticle.

API documentation
=================

To build the api documentation run

    >java -jar ybuilder.jar doc

The documentation can be found in target/doc/yetidoc and target/doc/javadoc

Building
========

To build the project run

    >java -jar ybuilder.jar integration-test
    

    
