<!--
This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send
a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
-->


# Yvertx: Yeti on Vertx

*Please help: the manual is work in progress if you encounter any errors or questions please let me know on the vertx mailinglist https://groups.google.com/forum/#!forum/vertx*

Yvertx is a yeti-lang api for vertx 2.0 (it does not work with prior versions
of vertx). 

[Yeti](http://mth.github.com/yeti/) is a statically typed functional 
language for the JVM. To get an intro to yeti read the following 
[blog-post](http://thebreakfastpost.com/2013/01/08/functional-programming-and-the-joy-of-learning-something-again/) 
or better the [yeti tutorial](http://linux.ee/~mzz/yeti/intro.html)

Yvertx is just a yeti wrapper around the vertx Java api. It is itself written in 
Yeti only. So everything Yvertx provides can be acomplished by using the vertx
Java api direclty - although less convinient. 

Often it is even necessary to use the Java api directly because Yvertx
only wraps the parts of the Java api, where it pays of in terms of convinience.

## Getting started

The easiest way to get started is to use the yvertx-project-template 

Alternatively you can use yvertx like any language-module for the vert.x 
platform.

### Using the yvertx-project-template

#### Quicksart with the REPL

Git clone the project-template from [yvertx-project-template](https://github.com/chrisichris/yvertx-project-template)

Update the ´/project.yeti´ file with your project's groupId artifactId and 
version

run from the root directory of your new project:

    >java -jar ybuilder.jar vertx:repl

This will start a yeti-repl which runs inside a verticle. 

You can now enter your test code ie:

    >yv = load yeb.yvertx;
    .... loads of output just ignore
    >server = yv.createHttpServerWithHandler do req: println "req received" done;
    server is ~org.vertx.java.core.http.HttpServer = org.vertx.java.core.http.impl.DefaultHttpServer@5c7db89a
    
    >server#listen(8080,"localhost");
    server is ~org.vertx.java.core.http.HttpServer = org.vertx.java.core.http.impl.DefaultHttpServer@5c7db89a

Point your browser to localhost:8080 and you server will print in the console

    req received

To enter multi-line input in the repl end the lines with \.

From the repl
you can try all the code snippets from this manual. You have to stop
the repl from time to time, so that resources are freed (ie servers listening
on a port are stopped)

#### Writing Verticles

Of course you can also start writing vertiles using the project-tempalte

The `project-template` uses [ybuilder](https://github.com/chrisichris/ybuilder)
which is a build-tool for yeti.

To write your first vericle edit the ´mods/main~main~1/main.yeti´ file.  

Your module code goes to `mods/main~main~1`. Please leave the name of the
module directories unchanged. Don't worry because of the strange
module name. When deploying (zipping) up you module ybuilder will give it the
right name based on your artifactId groupId and version you have set in
project.yeti

As an example we'll write a simple TCP echo server. The server just accepts 
connections and any data received by it is echoed back on the connection.

Copy the following into a text editor and save it as `main.yeti`

    load yeb.yvertx;

    verticle do:
        server = createAndConnectNetServer do sock:
            (newPump sock sock)#start();
        done;
        server#listen(1234, "localhost");
        
        \(server#close();)
    done;
    
Now, go to the projects root directory and run

    java -jar ybuilder.jar vertx:main
    
The server will now be running. Connect to it using telnet:

    telnet localhost 1234
    
And notice how data you send (and hit enter) is echoed back to you.           

Congratulations! You've written your first verticle.


### Using the language-module

You can of course use yvertx without the project-template. Just as a normal
language-module for vert.x.

Note: Yvertx needs vert.x 2.0 and the following steps are only necessery if 
you do not use the yvertx-project-template

Yvertx is installed in vert.x like any other lang-module: add the following 
lines to your vert.x conf/langs.properties file:

    yeti=com.github.chrisichris~yvertx-module~0.9.8-SNAPSHOT:yeb.yvertx.YetiVerticleFactory
    .yeti=yeti

Now you can use all .yeti files as any other supported language on the vert.x
platform.

## Working with JSONObject

JSON is used all over the vertx api - from configuration to sending messages 
on the bus. Of course yvertx has therefore special JSON support.

In statically typed languages working with Json if often not that convinient 
as in dynamic languages. Furtunately not so in yeti.

Vert.x offers the `toJson` and `fromJson` functions to transform structs 
automatically from and to vert.x `JsonObject`.

Type on the repl (java -jar yubilder.jar vertx:repl)

    >yvertx = load yeb.yvertx;
    ....
    >testStruct = { \
        for_json = E(),\
        name = "foo",\
        nested = {id = "nested obj"},\
        hash = ["Julian":1, "Susan":2],\
        namesList = ["Julian", "Susan"],\
        trueOrFalse = true,\
        brother = Some "John",\
        sister = none\
    };
    ....
    >obj = yvertx.toJson testStruct;
    obj is ~org.vertx.java.core.json.JsonObject = {"sister":null,"nested":{"fieldNames":["id"],"array":false,"object":true},"hash":{"fieldNames":["Julian","Susan"],"array":false,"object":true},"name":"foo","trueOrFalse":true,"brother":"John","namesList":{"array":true,"object":false}}

As you see the struct is mapped to a vert.x JsonObject

The struct to encode must have a `for_json` field to mark it to be ready to be
encoded as json. It may contain all the JsonObject values: 
number, string, boolean, byte[] and nested lists hashes and structs of this 
values them. Hashes may only have strings as keys.  
None() is encoded as json-null.  Some is encoded as the plain value ie Some 1
becomes in JsonObject 1. The E() tag as value means that the struct field 
should not be encoded. E() is typically used on `for_json` field.

### Convinient not typesafe converstion from JSONObject
To transfer a JsonObject back to a struct just use:

    {stru} = yvertx.fromJson obj;

Now you can use the jsonStruct as any other yeti struct.

    println jsonStruct.name;

Note that this struct is (contrary to everything else in yeti) not typesafe.
So you should use it very contained or check it with the functions of 
yeb.std.

    std = load yeb.std;

    {stru} = yvertx.fromJson obj;
    name = std.maybeString fail id stru.name;
    sister = std.maybeString None Some stru.sister;

### Typesafe conversiont from JSONObject

The direct conversion to a struct is often all you need, but it is not 
typesafe and therefor potentially dangerous. Therfore there is also a second
form to convert JSONObjects to yeti values:

    js = yvertx.fromJson obj;
    name = std.maybe' fail js.obj["name"].str;
    sister = js.obj["sister"].str;

A Json object is repesented as a hash which can be gotten by using
the .obj function and every value can be gotten by using .str, .num. bool and
.bytes function on the json object all these return None () or Some v 
depending whether they are set and contain that value.

Again with the `maybe` function from the yeb.std module you can read 
these values.

## Loading the yvertx module.

If you want to access the vert.x core API from within your verticle 
(which you almost certainly want to do), you need to call `load yeb.yvertx` at 
the top of your script. Normally this will be the first thing at the 
top of your verticle main. `yeb.yvertx` is just the name of the module that 
contains the core API.

## Verticle clean-up

Servers, clients and event bus handlers will be automatically closed when the 
verticles is stopped. However if you need to provide any custom clean-up code 
you put it into the function returned from the verticle creation function. 
This function will be invoked when the verticle stops. 
        
    yvertx = load yeb.yvertx;

    yvertx.verticle do:
        //verticle start code goes here
        
        //after start code, return the stop-function which gets called
        //when the verticle is called
        do: 
            //stop code goes here
        done
    done;
    
## Getting Configuration in a Verticle

If JSON configuration has been passed when deploying a verticle from either 
the command line using `vertx run` or `vertx deploy` and specifying 
a configuration file, or when deploying programmatically, that configuration 
is available to the verticle in the `vertx.config` variable. For example:

    config = yvertx.config ();
    
    println "Config is \(config)";
    
The config returned is a struct as explained in the chapter about json. 
You can use this struct to configure the verticle. Allowing verticles 
to be configured in a consistent way like this allows configuration to be 
easily passed to them irrespective of the language.

## Logging from a Verticle

Each verticle is given its own logger. :

    logger = yvertx.logger;
    
    logger#info("I am logging something");
    
The logger has the functions:

* trace
* debug
* info
* warn
* error
* fatal           

Which have the normal meanings you would expect.

The log files by default go in a file called `vertx.log` in the system temp 
directory. On my Linux box this is `\tmp`.

For more information on configuring logging, please see the main manual.

## Accessing environment variables from a Verticle

You can access enviroment veriables using `yvertx.enviroment` which
returns a hash<string,string> of all the environment variables.

    >yvertx.enviroment
    ["USERPROFILE":.....

# Deploying and Undeploying Verticles Programmatically

You can deploy and undeploy verticles programmatically from inside another 
verticle. Any verticles deployed programmatically inherit the path of 
the parent verticle. 

## Deploying a simple verticle

To deploy a verticle programmatically call the function 
`yvertx.deployVerticle`. The return value of `vertx.deployVerticle` is a
promise which is fullfilled when the verticle is deploy. The value of the 
promise is the unique id of the deployment. 

To deploy a verticle :

    prom = yvertx.deployVerticle name config numberOfInstances;

Ie to deploy one instance of the verticle `server.yeti`

    prom = yvertx.deployVerticle 
        "server.yeti" 
        {for_json=E()}
        1;
    prom >>-  do: println "finished deploy" done;
    
## Deploying a module programmatically

Use `deployModule` to deploy a module, for example:

    prom = yvertx.deployModule name config numberOfInstances;

It works like deployVerticle just for modules. Please see the modules manual 
for more information about modules.
    
## Passing configuration to a verticle programmatically   
  
Configuration can be passed to a verticle that is deployed 
programmatically. The configuration is written as a struct which is converted
by yvertx to JSON. Inside the deployed verticle the configuration is accessed 
with the `yvertx.config` function. For example:

    config = { name= 'foo', age = 234, for_json = E() };
    _ = yvertx.deployVerticle
        'server.yeti'
        config
        1; 
            
Then, in `server.yeti` you can access the config via `yvertx.config ()` 
as previously explained.
    
## Specifying number of instances

You can specify the number of instances of a verticle to deploy, when you 
deploy a verticle:

    _ = yvertx.deployVerticle 'my_verticle.js' emptyJS 5;   
  
The above example would deploy 5 instances.

## Getting Notified when Deployment is complete

The actual verticle deployment is asynchronous and might not complete until 
some time after the call to `deployVerticle` has returned. When the verticle 
has completed being deployed, you get notified through the returned promise`:

    yvertx.deployVerticle 'my_verticle.js' emptyJS 10 >>- do:
        yvertx.logger#info("It's been deployed!");
    done;  
    
## Deploying Worker Verticles

The `yvertx.deployVerticle` method deploys standard (non worker) verticles. 
If you want to deploy worker verticles use the `yvertx.deployWorkerVerticle` 
function. This function takes the same parameters as `yvertx.deployVerticle` 
with the same meanings.

## Undeploying a Verticle

Any verticles that you deploy programmatically from within a verticle, 
and all of their children are automatically undeployed when the parent 
verticle is undeployed, so in most cases you will not need to undeploy a 
verticle manually, however if you do want to do this, it can be done by 
calling the function `vertx.undeployVerticle` passing in the deployment id 
that is contained in the promise returned by `deployVerticle`

    prom = vertx.deployVerticle 'my_verticle.js' emptyJS 1;
    prom >>- \case of
        None ex: throw ex;
        Some deploymentId:
            yvertx.undeployVerticle deploymentID ;    
    esac;

    // alternatively you can also use the maybe' and fail functions from 
    // yeb.std
    load yeb.set;
    prom >>- maybe fail do deploymentId:
        yvertx.undeployVerticle deploymentId
    done;


# The Event Bus

The event bus is the nervous system of vert.x.

It allows verticles to communicate with each other irrespective of what 
language they are written in, and whether they're in the same vert.x instance, 
or in a different vert.x instance. It even allows client side JavaScript 
running in a browser to communicate on the same event bus. 
(More on that later).

It creates a distributed polyglot overlay network spanning multiple server 
nodes and multiple browsers.

The event bus API is incredibly simple. It basically involves registering 
handlers, unregistering handlers and sending messages.

First some theory:

## The Theory

### Addressing

Messages are sent on the event bus to an *address*.

Vert.x doesn't bother with any fancy addressing schemes. In vert.x an address 
is simply a string, any string is valid. However it is wise to use some kind 
of scheme, e.g. using periods to demarcate a namespace.

Some examples of valid addresses are `europe.news.feed1`, 
`acme.games.pacman`, `sausages`, and `X`.

### Handlers

A handler is function that receives messages from the bus. 
You register a handler at an address.

Many different handlers from the same or different verticles can be 
registered at the same address. A single handler can be registered by the 
verticle at many different addresses.

### Publish / subscribe messaging

The event bus supports *publishing* messages. Messages are published to an 
address. Publishing means delivering the message to all handlers that are 
registered at that address. This is the familiar *publish/subscribe* messaging 
pattern.

### Point to point messaging

The event bus supports *point to point* messaging. Messages are sent to 
an address. This means a message is delivered to *at most* one of the handlers 
registered at that address. If there is more than one handler regsitered at 
the address, one will be chosen using a non-strict round-robin algorithm.

With point to point messaging, an optional reply handler can be specified 
when sending the message. When a message is received by a recipient, and has 
been *processed*, the recipient can optionally decide to reply to the message.
If they do so that reply handler will be called.

When the reply is received back at the sender, it too can be replied to. 
This can be repeated ad-infinitum, and allows a dialog to be set-up between 
two different verticles. 
This is a common messaging pattern called the *Request-Response* pattern.

### Transient

*All messages in the event bus are transient, and in case of failure of all 
or parts of the event bus, there is a possibility messages will be lost. If 
your application cares about lost messages, you should code your handlers to 
be idempotent, and your senders to retry after recovery.*

If you want to persist your messages you can use a persistent work queue m
odule for that.

### Types of messages

Messages that you send on the event bus can be as simple as a string, 
a number or a boolean. You can also send vert.x buffers or JSON messages.

However the yvertx api only supports JSON messages. If you want to send one
of the other message-types vert.x supports you need to use the java api
directly.

It's highly recommended you use JSON messages to communicate between verticles.
JSON is easy to create and parse in all the languages that vert.x supports.

## Event Bus API

Let's jump into the API

In most cases you do not have to use the eventbus object directly, because
the yvertx functions wrap. However if you need the java EventBus-Object 
(ie for sending other message-types than JSON) than it can be gotten
from `eventBus` property of yvertx.

### Registering and Unregistering Handlers

To set a message handler for the whole cluster on the address `test.address`, 
you do the following:

    _ = yvertx.registerBusHandler (Global "test.address") do {body, reply}:
        logger#info("I received a message \(body)");
    done;

It's as simple as that. The handler will then receive any messages sent to 
that address.

If you want to register the handler only for the local maschine use 
`(Local "test.address")` as address.

When you register a handler on an address and you're in a cluster it can take 
some time for the knowledge of that new handler to be propagated across the 
entire cluster. Therefore `registerBusHanlder` returns a promise which gets 
fulfilled when the handler is registered and it contains the handlerkey
which is used to uninstall it.  E.g. :

    prom = yvertx.registerBusHandler 'test.address' do {body, reply}:
            //message handling
        done;
        
    prom >>-
        \(logger#info 
            'Yippee! The handler info has been propagated across the cluster')

To unregister a handler it's just as straightforward. You simply call 
`unregisterBusHandler` passing in the structure contained in the returned 
promise or given as `handlerId` to the callbacks of 

    prom = yvertx.registerBusHandler 'test.address' \()
        do {body}:
            //handler message;
        done;
    prom >>- \case of
        None ex:
        Some handlerId: yvertx.unregisterBusHandler handlerId;
    esac;

    //or using the callback
    _ = yvertx.registerBusHandler 'test.address' \()
        do {body, handlerId}:
            yvertx.unregisterBusHandler handlerId \();
        done;


As with registering, when you unregister a handler and you're in a cluster 
it can also take some time for the knowledge of that unregistration to be 
propagated across the entire to cluster. Therefore `unregisterBusHandler` also
returns a promise :

    yvertx.unregisterBusHandler handlerId >>-
    \(logger#info('Yippee! The handler unregister has been propagated across the cluster'));
    
If you want your handler to live for the full lifetime of your verticle there 
is no need to unregister it explicitly - vert.x will automatically unregister 
any handlers when the verticle is stopped.    

### Publishing messages

Publishing a message is also trivially easy. Just publish it specifying the 
address, for example:

    yvertx.publishToBus 'test.address' {msg='hello world', for_json=E()};

That message will then be delivered to any handlers registered against the 
address "test.address".

### Sending messages

Sending a message will result in at most one handler registered at the address 
receiving the message. This is the point to point messaging pattern.

    yvertx.sendToBus 'test.address"  {msg='hello world', for_json=E()};

### Replying to messages

Sometimes after you send a message you want to receive a reply from the 
recipient. This is known as the *request-response pattern*.

To do this you use the `busRequest` function. It sends a message to an address
and returns a promise with the response argument. When the receiver receives 
the message they are passed a replier 
function as the second parameter to the handler. When this function is invoked 
it causes a reply to be sent back to the sender where the promise is filled. 
An example will make this clear:

The receiver:

    _ = yvertx.registerBusHandler (Global 'test.address') do {body, reply}:
        //Do some stuff

        //Now reply to it
        reply {msg="This is a reply", for_json=E()};
    done;
    
    
The sender:

    answer = yvertx.requestFromBus 'test.address' 
            {msg='This is a message', for_json=E()};
    answer >>- \case of
        None ex: logger#exception(string ex,ex);
        Some msg: logger#info("I received a reply \(msg)");
        esac;
    
It is important to note that yvertx only supports JSON request/replies. If
you want to use other message-types you have use the Java api.

the reply etc use the 
The replies themselves can also be replied to so you can create a dialog 
between two different verticles consisting of multiple rounds. See the 
`sendToBusWithHandler` function for that.

### Getting the java EventBus

If you need access to the Java EventBus instance, ie to send other 
message-types than JSON you can use the eventBus property

    eb = yvertx.eventBus;
    eb#send('test.address', 1234);


## Distributed event bus

To make each vert.x instance on your network participate on the same event bus,
start each vert.x instance with the `-cluster` command line switch.

See the chapter in the main manual on *running vert.x* for more information on
this. 

Once you've done that, any vert.x instances started in cluster mode will merge
to form a distributed event bus.   
      
# Shared Data

Yvertx has no special support for shared data beside the normal java api.

Please refer to the java api documentation for more information about shared
data.

To get the `SharedData` instance use the  `sharedData` property:

    map = (yvertx.sharedData)#getMap("somemap");
    

# Buffers

Most data in vert.x is shuffled around using buffers.

A Buffer represents a sequence of zero or more bytes that can be written to or
read from, and which expands automatically as necessary to accomodate any
bytes written to it. You can perhaps think of a buffer as smart byte array.

## Creating Buffers

Create a buffer from a String. The String will be encoded in the buffer 
using UTF-8.

    buff = yvertx.newStringBuffer "some-string";
    
Create a buffer from a String: The String will be encoded using the specified
encoding, e.g:

    buff = yvertx.newEncodedBuffer 'UTF-16' 'some-string';
    
Create a buffer with an initial size hint. If you know your buffer will have a
certain amount of data written to it you can create the buffer and specify 
this size. This makes the buffer initially allocate that much memory and is 
more efficient than the buffer automatically resizing multiple times as data 
is written to it.

Note that buffers created this way *are empty*. It does not create a buffer filled with zeros up to the specified size.
        
    buff = yvertx.newBuffer 100000;   

You can create a Buffer which is filled with a certain length of random bytes. 
This is useful for testing:

    buff = yvertx.newRandomBuffer 10000;

All buffer creation functions return a Buffer Object.

## Writing to and Reading from a Buffer

Beside functions for creating Buffer Objects yvertx does not provide any 
special Buffer support-functions. So for reading and writing you should use 
the normal java api.

Please consult the java manual or java-docs for further info


## One-shot Timers

A one shot timer calls an event handler after a certain delay, expressed in 
milliseconds. 

To set a timer to fire once you use the `yvertx.setTimer` function passing in 
the delay in millisceonds and the handler

    _ = yvertx.setTimer 1000
        \(logger#info('And one second later this is printed'));
    
    logger#info('First this is printed');
     
## Periodic Timers

You can also set a timer to fire periodically by using the `setPeriodic` 
function. There will be an initial delay equal to the period. The return value 
of `setPeriodic` is a unique timer id (number). This can be later used if the 
timer needs to be cancelled. The argument passed into the timer event handler 
is also the unique timer id:

    id = vyertx.setPeriodic 1000, do:
        logger#info('And every second this is printed for');
    done;
    
    logger#info('First this is printed');
    
## Cancelling timers

To cancel a periodic timer, call the `cancelTimer` function specifying the 
timer id. For example:

    id = yvertx.setPeriodic 1000 do: 
        logger#info('This is not gonna be printed');
    done;
    
    // And immediately cancel it
    
    _ = yvertx.cancelTimer id;
    
Or you can cancel it from inside the event handler. The following example 
cancels the timer after it has fired 10 times.

    var count = 0;
    
    _ = yvertx.setPeriodic 1000 do id:
        logger#info("In event handler \(count)"); 
        count := count + 1;
        if count == 10 then
            _ = vertx.cancelTimer(id);
        fi
    done;         
      
    
# Writing TCP Servers and Clients

Creating TCP servers and clients is incredibly easy with vert.x.

## Net Server

### Creating a Net Server

To create a TCP server you invoke the `createNetServer` function 

    server = yvertx.createNetServer();

The server returned ist java NetServer object.

You connect a connection handler with the connectNetServer function

    server = yvertx.connectNetServer server do netSocket:
        logger#info("A client has connected");
    done;

The connection handler you have specified with `connectNetHandler` or
`createAndConnectNetServer` is notified when a connection is made. 

To create a TCP server and connect a connection-handler at the same time - 
what you will usually do - use the `createAndConnectNetServer` function

    server = yvertx.createAndConnectNetServer do netSocket:
        logger#info("A client has connected!")
    done;

    
### Start the Server Listening    
    
To tell that server to listen for connections we do:    

    server = vertx.createNetServer();

    server#listen(1234, 'myhost');
    
The first parameter to `listen` is the port. The second parameter is the 
hostname or ip address (this is the normal java api). If it is omitted it will 
default to `0.0.0.0` which means it will listen at all available interfaces.

### Closing a Net Server

To close a net server just call the `close` function.

    server#close();

The close is actually asynchronous and might not complete until some time 
after the `close` function has returned. If you want to be notified when the 
actual close has completed then you can use the `cloaseNetServer` function 
which returns a promise.

 
    yvertx.closeNetServer server >>- do:
      logger#info('The server is now fully closed.');
    done;
    
If you want your net server to last the entire lifetime of your verticle, you 
don't need to call `close` explicitly, the Vert.x container will automatically 
close any servers that you created when the verticle is stopped.    
    
### NetServer Properties

NetServer has a set of properties you can set which affect its behaviour and
also to enalbe SSL.

All this properties are set through the normal Java api. Please consult the java
manual for furhter information.

### Handling Data

So far we have seen how to create a NetServer, and accept incoming connections, 
but not how to do anything interesting with the connections. 
Let's remedy that now.

When a connection is made, the connect handler is called passing in an 
instance of `NetSocket`. This is a socket-like interface to the actual 
connection, and allows you to read and write data as well as do various other 
things like close the socket.


#### Reading Data from the Socket

To read data from the socket you need to set the `dataHandler` on the socket. 
This handler will be called with a `Buffer` every time data is received on the 
socket. You could try the following code and telnet to it to send some data:

    server = yvertx.createAndConnectNetServer do socket:
        yvertx.dataHandler sock do buffer:
            logger#info("I received \(buffer#length()) bytes of data");
        done;
    done;
    _ = server#listen(1234,"localhost");

    
#### Writing Data to a Socket

To write data to a socket, you invoke the `write` method of the Socket class.
This is the normal java- api. Please consult the java documentation.

#### Putting it all together.

Here's an example of a simple TCP echo server which simply writes back (echoes)
everything that it receives on the socket:

    server = yvertx.createAndConnectNetServer do socket:
        yvertx.dataHandler socket do buffer:
            _ = sock#write(buffer);
        done;
    done;

    server#listen(1234,"localhost");
    
### Closing a socket

You can close a socket by invoking the `close` method on the socket. 
This will close the underlying TCP connection.

### Closed Handler

If you want to be notified when a socket is closed, you can use the
`netsocketClosed` function to close the socket and get informed:


    server = yvertx.createAndConnectNetServer do socket:
        yvertx.netsocketClosed socket 
            \(logger#info("The socket is now closed"));
    done;

The closed handler will be called irrespective of whether the close was 
initiated by the client or server.

### Exception handler

You can set an exception handler on the socket that will be called if an 
exception occurs:

    server = yvertx.createAndConnectNetServer do socket:
        yvertx.exceptionHandler socket 
            \(logger#error("Oops. Something went wrong"));
    done;
    
    
## NetClient

A NetClient is used to make TCP connections to servers.

### Creating a Net Client

To create a TCP client you invoke the `createNetClient` function.

    client = yvertx.createNetClient();

### Making a Connection

To actually connect to a server you invoke the `connect` method:

    client = yvertx.createNetClient();
    
    client = yvertx.connectNetClient client 'localhost:1234' do socket:
        logger#info("We have connected");
    done;
    
The `connectNetClient` method takes as first argument the netClient. Than the
host:port as a one string argument. The third argument is a connect handler. 
This handler will be called when the connection actually occurs.

Like with the net server you can create the server and register
the handler in on path.

    client = yvertx.createAndConnectNetClient "localhost:1234" do socket:
        logger#info("We have connected");
    done;

The argument passed into the connect handler is an instance of `NetSocket`, 
exactly the same as what is passed into the server side connect handler. 
Once given the `NetSocket` you can read and write data from the socket in 
exactly the same way as you do on the server side.

You can also close it, set the closed handler, set the exception handler and 
use it as a `ReadStream` or `WriteStream` exactly the same as the server 
side `NetSocket`.

### Catching exceptions on the Net Client

You can set an exception handler on the `NetClient`. This will catch any 
exceptions that occur during connection.

    var client = vertx.createNetClient();
    
    client#exceptionHandler(yvertx.toExceptionHandler do ex:
        logger#info("Cannot connect since the host does not exist!");
    done;
    
    yvertx.connectNetClient client "host-doesnt-exist:4242" do sock:
        logger#info("will not happen");
    done;


### Configuring Reconnection, SSL and other properties

Beside the described functions yvertx does not wrap the Java api. So to
set Properties, configure reconnection and SSL, please consult the 
java manual and api-docs.

# Flow Control - Streams and Pumps

There are several objects in vert.x that allow data to be read from and 
written to in the form of Buffers.

All operations in the vert.x API are non blocking; calls to write data return 
immediately and writes are internally queued.

It's not hard to see that if you write to an object faster than it can actually 
write the data to its underlying resource then the write queue could grow 
without bound - eventually resulting in exhausting available memory.

To solve this problem a simple flow control capability is provided by some 
objects in the vert.x API.

Any flow control aware object that can be written to is said to implement 
`ReadStream`, and any flow control object that can be read from is said to 
implement `WriteStream`.

Let's take an example where we want to read from a `ReadStream` and write the 
data to a `WriteStream`.

A very simple example would be reading from a `NetSocket` on a server and 
writing back to the same `NetSocket` - since `NetSocket` implements both 
`ReadStream` and `WriteStream`, but you can do this between any `ReadStream` 
and any `WriteStream`, including HTTP requests and response, 
async files, WebSockets, etc.

A naive way to do this would be to directly take the data that's been read 
and immediately write it to the NetSocket, for example:

    server = yvertx.createAndConnectNetServer do sock:
        yvertx.dataHandler do buffer:
            _ = sock#write(buffer);
        done;
    done;
    
    server#listen(1234, "localhost");
    
There's a problem with the above example: If data is read from the socket 
faster than it can be written back to the socket, it will build up in the 
write queue of the AsyncFile, eventually running out of RAM. 
This might happen, for example if the client at the other end of the socket 
wasn't reading very fast, effectively putting back-pressure on the connection.

Since `NetSocket` implements `WriteStream`, we can check if the `WriteStream` 
is full before writing to it:

    server = yvertx.createAndConnectNetServer do sock:
        yvertx.dataHandler do buffer:
            if not sock#writeQueueFull() then
                _ = sock#write(buffer);
            fi
        done;
    done;
    
    server#listen(1234, "localhost");
    
This example won't run out of RAM but we'll end up losing data if the write 
queue gets full. What we really want to do is pause the `NetSocket` when the 
write queue is full. Let's do that:

    server = yvertx.createAndConnectNetServer do sock:
        yvertx.dataHandler do buffer:
            if not sock#writeQueueFull() then
                _ = sock#write(buffer);
            else
                _ = sock#pause();
            fi
        done;
    done;
    
    server#listen(1234, "localhost");

We're almost there, but not quite. The `NetSocket` now gets paused when the 
file is full, but we also need to *unpause* it when the write queue has 
processed its backlog:

    server = yvertx.createAndConnectNetServer do sock:
        yvertx.dataHandler do buffer:
            if not sock#writeQueueFull() then
                _ = sock#write(buffer);
            else
                _ = sock#pause();
                yvertx.drainHandler sock \(sock#resume());
            fi
        done;
    done;
    
    server#listen(1234, "localhost");

And there we have it. The `drainHandler` event handler will get called when 
the write queue is ready to accept more data, this resumes the `NetSocket` 
which allows it to read more data.

It's very common to want to do this when writing vert.x applications, so we 
provide a helper class called `Pump` which does all this hard work for you. 
You just feed it the `ReadStream` and the `WriteStream` and it tell it to start:

    server = yvertx.createAndConnectNetServer do sock:
        yvertx.dataHandler do buffer:
            pump = yvertx.createPump sock sock;
            pump#start();
        done;
    done;
    
    server#listen(1234, "localhost");
    
Which does exactly the same thing as the more verbose example.

As always there are some functions provided by yvertx which make working 
with `ReadStream` and `WriteStream` more easy. However a wrapper functions
would not bring any benefit than there is none and the normal java-api should
be used:

## ReadStream and WriteStream functions to register Handler

There are differnt functions to register Handlers on streams.

* dataHandler to register a dataHandler
* drainHanlder to register a drainHandler
* endHandler to register an endHandler
* exceptionHandler to register an excpetionHandler on a ReadStream
* wsExcpetionHandler to register an excpetionHandler on a WriteStream

There is also the `createPump` function to create a pump

Beside that there are no wrapper functions. Please consult the java manual
for further information an ReadStream, WriteStream and Pump


# Writing HTTP Servers and Clients

## Writing HTTP servers

Vert.x allows you to easily write full featured, highly performant and 
scalable HTTP servers.

### Creating an HTTP Server

To create an HTTP server you invoke the `createHttpServer` function

    server = yvertx.createHttpServer();

To create an Http server and register request-handler at the same time use
the `createHttpServerWithHandler` function 

    server = yvertx.createHttpServerWithHandler do req:
        logger#info("Request received");
    done;
    
### Start the Server Listening    
    
To tell that server to listen for incoming requests you use the `listen` method:

    server = vertx.createHttpServer();

    server#listen(8080, 'myhost');
    
The first parameter to `listen` is the port. The second parameter is the 
hostname or ip address. If the hostname is omitted it will default to 
`0.0.0.0` which means it will listen at all available interfaces.

### Getting Notified of Incoming Requests
    
To be notified when a request arrives you need to set a request handler. This 
is done by calling the `requestHandler` function of the server, passing in 
the handler:

    server = vertx.createHttpServer();

    server = vertx.httpServerRequestHandler server do request:
        logger#info("An HTTP request has been received");
    done;

    server#listen(8080, 'localhost');

Alternatively you can create the server and register the handler in one pass:

    server = yvertx.createHttpServerWithHandler do req:
        logger#info("Request received");
    done;

    server#listen(8080, "localhost");
    
This displays 'An HTTP request has been received!' every time an HTTP request 
arrives on the server. You can try it by running the verticle and pointing 
your browser at `http://localhost:8080`.

Similarly to `NetServer`, the return value of the `requestHandler` method is 
the server itself, so multiple invocations can be chained together. 
That means we can rewrite the above with:

    
    server = (yvertx.createHttpServerWithHandler do req:
            logger#info("Request received");
        done)
        #listen(8080,"localhost");
       
### Handling HTTP Requests

So far we have seen how to create an 'HttpServer' and be notified of requests. 
Lets take a look at how to handle the requests and do something useful with them.

When a request arrives, the request handler is called passing in an instance 
of `HttpServerRequest`. This object represents the server side HTTP request.

The handler is called when the headers of the request have been fully read. 
If the request contains a body, that body may arrive at the server some time 
after the request handler has been called.

It contains functions to get the URI, path, request headers and request 
parameters. It also contains a `response` property which is a reference to an 
object that represents the server side HTTP response for the object.

#### The HttpServerRequest object 

The HttpServerRequest object passed to the request-handler contains information
about the request, like request-method, path, parameters, headers etc.

It also contains a HttpServerResponse object which is used to send the 
response.

For a detailed discussion of all the field and methods please refer to 
the java manual and api.

#### Params and requestHeaders

To get a hash<string,string> of the headers use the `requestHeaders` function.
To get a hash<string,string> of the params use the `params` function.

    server = yvertx.createHttpServerWithHandler do req:
        p = yvertx.params req;
        if "name" in p then
            logger#info("Name is \(p["name"])")
        else
            logger#info("No name was sent");
        fi;
        
        forHash (yvertx.requestHeaders req) do k v:
            logger#info("\(k) => \(v)");
        done;
    done;
    
#### Reading Data from the Request Body

Sometimes an HTTP request contains a request body that we want to read. 
As previously mentioned the request handler is called when only the headers 
of the request have arrived so the `HttpServerRequest` object does not contain 
the body. This is because the body may be very large and we don't want to 
create problems with exceeding available memory.

To receive the body, you set the `dataHandler` on the request object. 
This will then get called every time a chunk of the request body arrives. 
Here's an example:

    server = yvertx.createHttpServerWithHandler do request:
        yvertx.dataHandler reqeust do buffer:
            logger#info("I received \(buffer#length()) bytes");
        done;
    done;
      
    _ = server#listen(8080, 'localhost'); 
    
The `dataHandler` may be called more than once depending on the size of the 
body.    

You'll notice this is very similar to how data from `NetSocket` is read. 

The request object implements the `ReadStream` interface so you can pump the 
request body to a `WriteStream`. See the chapter on streams and pumps for a 
detailed explanation. 

In many cases, you know the body is not large and you just want to receive it 
in one go. To do this you could do something like the following:

    server = yvertx.createHttpServerWithHandler do request:
    
      // Create a buffer to hold the body
      body = yvertx.newBuffer 100;;  
    
      yvertx.dataHandler request do buffer:
        // Append the chunk to the buffer
        _ = body#appendBuffer(buffer);
      done;
      
      yvertx.endHandler request do:
        // The entire body has now been received
        logger#info("The total body received was \(body#length()) bytes");
      done;
      
    done;
    _ =server#listen(8080, 'localhost');   
    
Like any `ReadStream` the end handler is invoked when the end of stream is 
reached - in this case at the end of the request.

If the HTTP request is using HTTP chunking, then each HTTP chunk of the 
request body will correspond to a single call of the data handler.

It's a very common use case to want to read the entire body before processing 
it, so vert.x allows a `bodyHandler` to be set on the request object.

The body handler is called only once when the *entire* request body has 
been read.

*Beware of doing this with very large requests since the entire request body 
will be stored in memory.*

Here's an example using `bodyHandler`:

    server = yvertx.createHttpServerWithHandler do request:
        yvertx.serverBodyHandler request do body:
            logger#info("The total body received was \(body#length())");
        done;
    done;

    _ =server#listen(8080, 'localhost');   
    
Simples, innit?    
    
### HTTP Server Responses 

As previously mentioned, the HTTP request object contains a property 
`response`. This is the HTTP response for the request. You use it to write 
the response back to the client.

Yvertx provides only functions to write response-headers and response-trailers
otherwise use the methods of the response object.

### Setting Status Code and Message

To set the HTTP status code for the response use the `statusCode` property, e.g.

    server = yvertx.createHttpServerWithHandler do request:
        request#response#statusCode := 404;
        request#response#end();
    done;

    _ =server#listen(8080, 'localhost');   
    
You can also use the `statusMessage` property to set the status message. 
If you do not set the status message a default message will be used.    
  
The default value for `statusCode` is `200`.    

#### Writing HTTP responses

To write data to an HTTP response, you invoke the `write` method of the 
response object.  This method can be invoked multiple times before the 
response is ended. It can be invoked in a few ways:

With a single buffer:

    myBuffer = ...
    _ = request#response#write(myBuffer);
    
A string. In this case the string will encoded using UTF-8 and the result 
written to the wire.

    _ = request#response#write('hello');    
    
A string and an encoding. In this case the string will encoded using the 
specified encoding and the result written to the wire.     

    _ = request#response#write('hello', 'UTF-16');
    
The `write` function is asynchronous and always returns immediately after the write has been queued.

The actual write might complete some time later. If you want to be informed 
when the actual write has completed you can pass in a function as a final 
argument. This function will then be invoked when the write has completed:

    _ = request#response#write('hello', 
        yvertx.toSimpleHandler \(logger#info('It has actually been written')));
    
If you are just writing a single string or Buffer to the HTTP response you can 
write it and end the response in a single call to the `end` function.   

The first call to `write` results in the response header being being written 
to the response.

Consequently, if you are not using HTTP chunking then you must set the 
`Content-Length` header before writing to the response, since it will be too 
late otherwise. If you are using HTTP chunking you do not have to worry. 
   
#### Ending HTTP responses

Once you have finished with the HTTP response you must call the `end()` 
method on it.

This method can be invoked in several ways:

With no arguments, the response is simply ended. 

    request#response#end();
    
The function can also be called with a string or Buffer in the same way `write`
is called. In this case it's just the same as calling write with a string or 
Buffer followed by calling `end` with no arguments. For example:

    request#response#end("That's all folks");

#### Closing the underlying connection

You can close the underlying TCP connection of the request by calling the 
`close` function.

    request#response#close();

#### Response headers

You can write headers to the response with `putResponseHeaders` function.

    yvertx.putResponseHeaders request 
                        ["Some-Header":"bar", 
                        "Another-Header":"foo"]

The headers in the hash will be added to the response (they do not replace the
existing ones).

Response headers must all be added before any parts of the response body 
are written.

#### Chunked HTTP Responses and Trailers

Vert.x supports 
[HTTP Chunked Transfer Encoding](http://en.wikipedia.org/wiki/Chunked_transfer_encoding). 
This allows the HTTP response body to be written in chunks, and is normally 
used when a large response body is being streamed to a client, 
whose size is not known in advance.

You put the HTTP response into chunked mode as follows:

    _ = req#response#setChunked(true);
    
Default is non-chunked. When in chunked mode, each call to 
`response#write(...)` will result in a new HTTP chunk being written out.  

When in chunked mode you can also write HTTP response trailers to the response.
These are actually written in the final chunk of the response.

As with headers, you can write trailers to the response by simply adding them 
to the trailers hash on the response object:

    yvertx.putResponseTrailers request ['Some-Trailer': 'quux'];

### Serving files directly from disk

If you were writing a web server, one way to serve a file from disk would be 
to open it as an `AsyncFile` and pump it to the HTTP response. Or you could 
load it it one go using the file system API and write that to the HTTP response.

Alternatively, vert.x provides a method which allows you to send serve a file 
from disk to HTTP response in one operation. Where supported by the underlying 
operating system this may result in the OS directly transferring bytes from 
the file to the socket without being copied through userspace at all.

Using `sendFile` is usually more efficient for large files, but may be slower 
than using `readFile` to manually read the file as a buffer and write it 
directly to the response.

To do this use the `sendFile` function on the HTTP response. Here's a simple 
HTTP web server that serves static files from the local `web` directory:

    server = vertx.createHttpServerWithHandler do req:
        file = '';
        file = if req#path == '/' then 
            'index.html';
        elif strIndexOf req#path '..' 0 == -1 then
            file = req#path;
        else
             ""
        fi;
        _ = req#response#sendFile("web/\(file)");   
    done;
    _ = server#listen(8080, 'localhost');
    
*Note: If you use `sendFile` while using HTTPS it will copy through userspace, 
since if the kernel is copying data directly from disk to socket 
it doesn't give us an opportunity to apply any encryption.*

**If you're going to write web servers using vert.x be careful that users 
cannot exploit the path to access files outside the directory from which you 
want to serve them.**

### Pumping Responses

Since the HTTP Response implements `WriteStream` you can pump to it from any 
`ReadStream`, e.g. an `AsyncFile`, `NetSocket` or `HttpServerRequest`.

Here's an example which echoes HttpRequest headers and body back in the 
HttpResponse. It uses a pump for the body, so it will work even if the 
HTTP request body is much larger than can fit in memory at any one time:

    server = yvertx.createHttpServerWithHandler do req:
        yvertx.putResponseHeaders req (yvertx.requestHeaders req);
        p = yvertx.newPump req req#response;
        p#start();
        yvertx.endHandler req \(req#response#end());
    done;

    _ = server#listen(8080, 'localhost');
    
## Writing HTTP Clients

### Creating an HTTP Client

To create an HTTP client you invoke the `createHttpClient` function giving it
the host:port as a string-argument.

    client = yvertx.createHttpClient [] "host.org:8181";
    
A single `HTTPClient` always connects to the same host and port. 
If you want to connect to different servers, create more instances.

### Pooling and Keep Alive

By default the `HTTPClient` pools HTTP connections. As you make requests a 
connection is borrowed from the pool and returned when the HTTP response 
has ended.

If you do not want connections to be pooled you can set `setKeepAlive` to
false on the underlying vert.x HttpClient Object:

    client = (yvertx.createHttpClient [] "foo.com:8181");
    _ = client.vertxClient#setKeepAlive(false);

In this case a new connection will be created for each HTTP request and 
closed once the response has ended.

You can set the maximum number of connections that the client will pool 
as follows:

    client = (yvertx.createHttpClient [] "foo.com:8181");
    _ = client.vertxClient#setMaxPoolSize(10);
                   
The default value is `1`.         

### Closing the client

Vert.x will automatically close any clients when the verticle is stopped, 
but if you want to close it explicitly you can:

    client.vertxClient#close()            
                         
### Making Requests

To make a request using the client you use the `httpRequest` function, giving
it the HttpClient, the method and uri, some options and an response handler as
arguments:

For example, to make a `POST` request:

    client = yvertx.createHttpClient [] "localhost:8080";
    
    req = yvertx.httpRequest client (Post '/some-path/') [] 
        do resp:
            logger#info(
                "Got a response, status code: \(resp#statusCode)");
        done;

    req#end();
    
To make a PUT request use the `Put` tag with the path, to make a GET request 
use the `Get` tag with the path etc

Legal request methods are: `Get`, `Put`, `Post`, `Delete`, `Head`, `Options`, 
`Connect`, `Trace` and `Patch`.

The response handler will get called when the corresponding response arrives or
when an excpetion was raised on the HttpClientRequest#exceptionHandler.

The function returns a HttpClientRequest object.

The request is not executed immidiately but only after the `end` method is
called on the returned `HttpClientRequest` object.

The value specified in the request URI corresponds to the Request-URI as 
specified in [Section 5.1.2 of the HTTP specification](http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html). 
In most cases it will be a relative URI.

*Please note that the domain/port that the client connects to is determined 
by `setPort` and `setHost`, and is not parsed from the uri.*

To set headers on the request use the Headers options

    yvertx.httpRequest client (Get '/some-path/') 
        [Headers ["foo-header":"some value", 
                 "foo-header2":"othervalue"]
        do res:
            logger#info(
                "Got a response, status code: \(resp#statusCode)");
        done; 

To write content on the request use one of the JsonContent, FormEncoded,
TextContent options. They will set the Conent-Type and Content-Length header.

#### HTTP chunked requests

Vert.x supports [HTTP Chunked Transfer Encoding](http://en.wikipedia.org/wiki/Chunked_transfer_encoding) for requests. 

This uses the normal java api please consult the java manual

### HTTP Client Responses

Client responses are received as an argument to the response handler that is 
passed into one of the request methods on the HTTP client.

The response object implements `ReadStream`, so it can be pumped to a 
`WriteStream` like any other `ReadStream`.

To query the status code of the response use the `statusCode` property. 
The `statusMessage` property contains the status message. For example:

    client = yvertx.createHttpClient [] "foo.com:80";
    
    req = yvertx.httpRequest client '/some-path' [] do resp:
      log#info('server returned status code: ' ^ resp#statusCode);   
      log#info('server returned status message: ' ^ resp#statusMessage);   
    done;
    req#end()


#### Reading Data from the Response Body

The API for reading a http client response body is very similar to the API for 
reading a http server request body.

Sometimes an HTTP response contains a request body that we want to read. 
Like an HTTP request, the client response handler is called when all the 
response headers have arrived, not when the entire response body has arrived.

To receive the response body, you set a `dataHandler` on the response object 
which gets called as parts of the HTTP response arrive. Here's an example:


    client = yvertx.createHttpClient [] "foo.com:80";
    
    req = yvertx.httpRequest client '/some-path' [] do resp:
        yvertx.dataHandler resp do buffer:
            log#info("I received \(buffer#length()) bytes");
        done
    done;
    req#end();
    
The response object implements the `ReadStream` interface so you can pump 
the response body to a `WriteStream`. See the chapter on streams and pump 
for a detailed explanation. 

The `dataHandler` can be called multiple times for a single HTTP response.

As with a server request, if you wanted to read the entire response body 
before doing something with it you could collect the response and register an
endHandler to now when the resposne is finished.

It's a very common use case to want to read the entire body in one go, 
so vert.x allows a `bodyHandler` to be set on the response object.

The body handler is called only once when the *entire* response body 
has been read.

*Beware of doing this with very large responses since the entire 
response body will be stored in memory.*

Here's an example using `bodyHandler`:

    client = yvertx.createHttpClient [] 'foo.com:80';
    
    yvertx.httpRequest client '/some-uri' [] do resp:
        yvertx.cleintBodyHandler resp do body:
            log.info("The total body reveived was \(body#length())");
        done
   done;

And there is even a simpler form: 


### Executing the Request immidiately and receiving the body

To get the body and execute the request in one go use the `httpRequestNow` 
function. It works like the `httpRequest` function but executes the
the request immidately and returns a promise containing the 
response and body content.

    client = yvertx.createHttpClient [] "localhost:8080";
    
    prom = yvertx.httpRequestNow client (Post '/some-path/') [];
    
    prom >>- \case of
        None ex: logger#error("An excpetion happended \(ex)");
        Some {response, body}:
            logger#info(
                "Got a response, status code: \(resp#statusCode)");
        esac;

If the request was succesful Some HttpClientResponse and a Buffer containing
the body will be in the promise. If an excpetion was raised with 
HttpClientRequest#exceptionHandler or HttpClientResponse#excpetionHandler 
from the java-api than a None Exception will be given.

If you want to manipulate the HttpClientRequest before sending (ending) it 
use the WithRequest option. 

    client = yvertx.createHttpClient "localhost:8080";
    
    yvertx.httpRequestNow client (Post '/some-path/') 
        [WithRequest do req: req#setTimeout(2000)] 
        >>-
        \case of
        None ex: logger#error("An excpetion happended \(ex)");
        Some {response, body}:
            logger#info(
                "Got a response, status code: \(resp#statusCode)");
        done;


In this case the httpRequestNow function will exeucte the given 
function before invoking the end method.

## Pumping Requests and Responses, 100-Continue Handling, HTTPS

For 100-Coninute Handling and HTTPS consult the java manual. It is handled
through the normal Java api

# WebSockets

[WebSockets](http://en.wikipedia.org/wiki/WebSocket) are a feature of HTML 5 
that allows a full duplex socket-like connection between HTTP servers and 
HTTP clients (typically browsers).

## WebSockets on the server

To use WebSockets on the server you create an HTTP server as normal, but 
instead of setting a `requestHandler` you set a `websocketHandler` on the server.

    server = yvertx.createHttpServer();

    yvertx.connectWebSocketServer server do websocket:
        logger#info("Websocket connection received");    
    done;

    server#listen(8080,"localhost");

    
### Reading from and Writing to WebSockets    
    
The `websocket` instance passed into the handler implements both `ReadStream` 
and `WriteStream`, so you can read and write data to it in the normal ways. 
I.e by setting a `dataHandler` and calling the `writeBuffer` method.

See the chapter on `NetSocket` and streams and pumps for more information.

For example, to echo all data received on a WebSocket:

    server = yvertx.createHttpServer();

    yvertx.connectWebSocketServer server do websocket:
      
        p = yvertx.newPump websocket websocket;
        p#start();
      
    });
    server#listen(8080, 'localhost');
    
The `websocket` instance also has method `writeBinaryFrame` for writing 
binary data. This has the same effect as calling `writeBuffer`.

Another method `writeTextFrame` also exists for writing text data. 
This is equivalent to calling 

    websocket#writeBuffer(new vertx.Buffer('some-string'));    

### Rejecting WebSockets

Sometimes you may only want to accept WebSockets which connect at a 
specific path.

To check the path, you can query the `path` property of the `websocket`. 
You can then call the `reject` function to reject the websocket.

    server = yvertx.createHttpServer();

    yvertx.connectWebSocketServer server do websocket:
      
        if websocket#path == "/services/echo" then
            p = yvertx.newPump websocket websocket;
            p#start();
        else
            websocket#reject();
        fi;
    done;

    server#listen(8080, 'localhost');
    
## WebSockets on the HTTP client

To use WebSockets from the HTTP client, you create the HTTP client as normal, 
then call the `connectWebsocket` function, passing in the URI that you wish 
to connect to at the server, and a handler.

The handler will then get called if the WebSocket successfully connects. 
If the WebSocket does not connect - perhaps the server rejects it, then any 
exception handler on the HTTP client will be called.

Here's an example of WebSocket connection;

    client = yvertx.createHttpClient [] "localhost:8080";
    
    yvertx.connectWebSocketClient server '/some-uri' do websocket:
      
      // WebSocket has connected!
      
    done; 
    
Again, the client side WebSocket implements `ReadStream` and `WriteStream`, so 
you can read and write to it in the same way as any other stream object. 

## WebSockets in the browser

To use WebSockets from a compliant browser, you use the standard WebSocket API. 
Here's some example client side JavaScript which uses a WebSocket. 

    <script>
    
        var socket = new WebSocket("ws://localhost:8080/services/echo");

        socket.onmessage = function(event) {
            alert("Received data from websocket: " + event.data);
        }
        
        socket.onopen = function(event) {
            alert("Web Socket opened");
            socket.send("Hello World");
        };
        
        socket.onclose = function(event) {
            alert("Web Socket closed");
        };
    
    </script>
    
For more information see the [WebSocket API documentation](http://dev.w3.org/html5/websockets/) 

## Routing WebSockets with Pattern Matching

**TODO**   
    
# SockJS

WebSockets are a new technology, and many users are still using browsers that 
do not support them, or which support older, pre-final, versions.

Moreover, WebSockets do not work well with many corporate proxies. This means 
that's it's not possible to guarantee a WebSocket connection is going to 
succeed for every user.

Enter SockJS.

SockJS is a client side JavaScript library and protocol which provides 
a simple WebSocket-like interface to the client side JavaScript developer 
irrespective of whether the actual browser or network will allow real WebSockets.

It does this by supporting various different transports between browser and 
server, and choosing one at runtime according to browser and network 
capabilities. All this is transparent to you - you are simply presented 
with the WebSocket-like interface which *just works*.

Please see the [SockJS website](https://github.com/sockjs/sockjs-client)
for more information.

## SockJS Server

Vert.x provides a complete server side SockJS implementation.

This enables vert.x to be used for modern, so-called *real-time* (this is the 
*modern* meaning of *real-time*, not to be confused by the more formal 
pre-existing definitions of soft and hard real-time systems) web applications 
that push data to and from rich client-side JavaScript applications, without 
having to worry about the details of the transport.

To create a SockJS server you simply create a HTTP server as normal and then 
use the `createSockJSServer` function specifying the HTTP server:

    httpServer = yvertx.createHttpServer();
    
    sockJSServer = yvertx.createSockJSServer httpServer;
    
Each SockJS server can host multiple *applications*.

Each application is defined by some configuration, and provides a handler which
gets called when incoming SockJS connections arrive at the server.     

For example, to create a SockJS echo application:

    httpServer = yvertx.createHttpServer ();
    
    sockJSServer = yvertx.createSockJSServer httpServer;
    
    config = { prefix: '/echo', for_json = E() };
    
    yvertx.installSockJSApp sockJSServer config do sock:
        p = yvertx newPump sock sock;
        p#start();
    done;
    
    httpServer#listen(8080);
    
The configuration is a JSON object which takes the same values as the one
of the java api. Please consult the java api and java-docs for a detailed
description.that takes the following fields:

## Reading and writing data from a SockJS server

The object passed into the SockJS handler implements `ReadStream` and 
`WriteStream` much like `NetSocket` or `WebSocket`. You can therefore use 
the standard API for reading and writing to the SockJS socket or using it 
in pumps.

See the chapter on Streams and Pumps for more information.

    httpServer = yvertx.createHttpServer ();
    
    sockJSServer = yvertx.createSockJSServer httpServer;
    
    config = { prefix: '/echo', for_json = E() };
    
    yvertx.installSockJSApp sockJSServer config do sock:
        yvertx.dataHandler sock do buffer:
            sock#writeBuffer(buffer);
        done
    done;
    
    httpServer#listen(8080);
    
## SockJS client

For full information on using the SockJS client library please see the SockJS 
website. A simple example:

    <script>
       var sock = new SockJS('http://mydomain.com/my_prefix');
       
       sock.onopen = function() {
           console.log('open');
       };
       
       sock.onmessage = function(e) {
           console.log('message', e.data);
       };
       
       sock.onclose = function() {
           console.log('close');
       };
    </script>   
    
As you can see the API is very similar to the WebSockets API.    
            
# SockJS - EventBus Bridge

## Setting up the Bridge

By connecting up SockJS and the vert.x event bus we create a distributed event 
bus which not only spans multiple vert.x instances on the server side, but can 
also include client side JavaScript running in browsers.

We can therefore create a huge distributed bus encompassing many browsers and 
servers. The browsers don't have to be connected to the same server as long 
as the servers are connected.

On the server side we have already discussed the event bus API.

We also provide a client side JavaScript library called `vertxbus.js` which 
provides the same event bus API, but on the client side.

This library internally uses SockJS to send and receive data to a SockJS 
vert.x server called the SockJS bridge. It's the bridge's responsibility 
to bridge data between SockJS sockets and the event bus on the server side.

Creating a Sock JS bridge is simple. You just call the `bridgeSockJS` function 
using the SockJS server.

You will also need to secure the bridge (see below).

The following example creates and starts a SockJS bridge which will bridge any 
events sent to the path `eventbus` on to the server side event bus.

    httpServer = yvertx.createHttpServer();
    
    sockJSServer = yvertx.createSockJSServer httpServer;

    bridgeSockJS sockJSServer [] {prefix : '/eventbus'} [] [];

    server.listen(8080);
    
The SockJS bridge currently only works with JSON event bus messages.    

## Using the Event Bus from client side JavaScript

Once you've set up a bridge, you can use the event bus from the client side as follows:

In your web page, you need to load the script `vertxbus.js`, then you can access the vert.x event bus API. Here's a rough idea of how to use it. For a full working examples, please consult the bundled examples.

    <script src="http://cdn.sockjs.org/sockjs-0.2.1.min.js"></script>
    <script src='vertxbus.js'></script>

    <script>

        var eb = new vertx.EventBus('http://localhost:8080/eventbus');
        
        eb.onopen = function() {
        
          eb.registerHandler('some-address', function(message) {

            console.log('received a message: ' + JSON.stringify(message);

          });

          eb.send('some-address', {name: 'tim', age: 587});
        
        }
       
    </script>

You can find `vertxbus.js` in the `client` directory of the vert.x distribution.

The first thing the example does is to create a instance of the event bus

    var eb = new vertx.EventBus('http://localhost:8080/eventbus'); 
    
The parameter to the constructor is the URI where to connect to the event bus. Since we create our bridge with the prefix `eventbus` we will connect there.

You can't actually do anything with the bridge until it is opened. When it is open the `onopen` handler will be called.

The client side event bus API for registering and unregistering handlers and for sending messages is exactly the same as the server side one. Please consult the chapter on the event bus for full information.    

**There is one more thing to do before getting this working, please read the following section....**

## Securing the Bridge

If you started a bridge like in the above example without securing it, 
and attempted to send messages through it you'd find that the messages 
mysteriously disappeared. What happened to them?

For most applications you probably don't want client side JavaScript being 
able to send just any message to any verticle on the server side or to all 
other browsers.

For example, you may have a persistor verticle on the event bus which allows 
data to be accessed or deleted. We don't want badly behaved or malicious 
clients being able to delete all the data in your database! Also, we don't 
necessarily want any client to be able to listen in on any topic.

To deal with this, a SockJS bridge will, by default refuse to let through any 
messages. It's up to you to tell the bridge what messages are ok for it to 
pass through. (There is an exception for reply messages which are always 
allowed through).

In other words the bridge acts like a kind of firewall which has a default 
*deny-all* policy.

Configuring the bridge to tell it what messages it should pass through is easy.
You pass in two arrays of JSON objects that represent *matches*, as the final 
argument in the call to `bridge`.

The first array is the *inbound* list and represents the messages that you want
to allow through from the client to the server. The second array is the 
*outbound* list and represents the messages that you want to allow through from
the server to the client.

Each match can have up to three fields:

1. `address`: This represents the exact address the message is being sent to. 
If you want to filter messages based on an exact address you use this field.
2. `address_re`: This is a regular expression that will be matched against 
the address. If you want to filter messages based on a regular expression you 
use this field. If the `address` field is specified this field will be ignored.
3. `match`: This allows you to filter messages based on their structure. 
Any fields in the match must exist in the message with the same values for 
them to be passed. This currently only works with JSON messages.

When a message arrives at the bridge, it will look through the available 
permitted entries.

* If an `address` field has been specified then the `address` must match 
exactly with the address of the message for it to be considered matched.

* If an `address` field has not been specified and an `address_re` field has 
been specified then the regular expression in `address_re` must match with 
the address of the message for it to be considered matched.

* If a `match` field has been specified, then also the structure of the 
message must match.

Here is an example:

    httpServer = yvertx.createHttpServer();
    
    sockJSServer = yvertx.createSockJSServer httpServer;

    yvertx.bridgeSockJS sockJSServer []
        {prefix = '/eventbus',for_json=E()}
        [
        // Let through any messages sent to 'demo.orderMgr'
        {
            address = 'demo.orderMgr',
            for_json = E()
        },
        // Allow calls to the address 'demo.persistor' as long as the messages
        // have an action field with value 'find' and a collection field with value
        // 'albums'
        {
            address = 'demo.persistor',
            match = {
                action = 'find',
                collection = 'albums'
            }
            for_json=E(),
        },
        // Allow through any message with a field `wibble` with value `foo`.
        {
            match = {
                wibble= 'foo'
            }
            for_json=E(),
        }
        ],

        [
        // Let through any messages coming from address 'ticker.mystock'
        {
            address = 'ticker.mystock',
            for_json = E(),
        },
        // Let through any messages from addresses starting with "news." 
        //(e.g. news.europe, news.usa, etc)
        {
            address_re = 'news\\..+',
            for_json=E()
        }
        ];
      


    server#listen(8080);
    
To let all messages through you can specify two arrays with a single empty 
JSON object which will match all messages.

    ...

    yvertx.bridgeSockJS sockJSServer [] 
                {prefix = '/eventbus', for_json=E()} [] [];
    
    ...    
     
**Be very careful!**

## Adding an EventBusBridgeHook

You can add an EventBusBridgeHook, which gets informed of various 
events on the EventBusBridge.

    hook = createBridgeHook \case of
        Closed sock: println "socked was closed";
        Send {sock,msg,address} : println "msg was send";
        Pub {sock,msg,address}: pritnln "msg was pub";
        PreRegister {sock,address}: println "sock will register at";
        PostRegister {sock,address}: println "address was registered";
        Unregister {soc,address}: println "address is unregistered";
    esac;
    yvertx.brigdeSockJS sockJSServer 
        [Hook hook]
        {prefix = "/eventbus", for_json=E()} [] [];

## Messages that require authorisation

The bridge can also refuse to let certain messages through if the user is 
not authorised.

To enable this you need to make sure an instance of the `vertx.auth-mgr` 
module is available on the event bus. 
(Please see the modules manual for a full description of modules).

To sepcify the the bus-address of the auth-mgr and the authentication-timeout
use the the AuthTimeout and AuthAddress option respectively

    yvertx.bridgeSockJS 
        sockJSServer 
        [AuthAddress "basicauthenticatiomgr", 
         AuthTimeout 5 * 60 * 1000]
        config
        inboundPermitted
        outbaundPermitted;

To tell the bridge that certain messages require authorisation before being 
passed, you add the field `requires_auth` with the value of `true` in the 
match. The default value is `false`. For example, the following match:

    {
        address = 'demo.persistor',
        match = {
            action = 'find',
            collection = 'albums'
        },
        requires_auth= true,
        for_json = E(),
    }
    
This tells the bridge that any messages to save orders in the `orders` 
collection, will only be passed if the user is successful authenticated 
(i.e. logged in ok) first.    
    
When a message is sent from the client that requires authorisation, the client 
must pass a field `sessionID` with the message that contains the unique 
session ID that they obtained when they logged in with the `auth-mgr`.

When the bridge receives such a message, it will send a message to the 
`auth-mgr` to see if the session is authorised for that message. If the 
session is authorised the bridge will cache the authorisation for a certain 
amount of time (five minutes by default)

# File System

Vert.x lets you manipulate files on the file system. File system operations are 
asynchronous and return a promise, which gets fulfilled when the operation is
finished.

The yvertx api wraps all the async functions of the vert.x FileSystem Object. 
They are exactly the same as the ones on the java FileSystem object except 
that they return a promise instead of taking an AsyncHandler. So for 
documentation of the functions please consult the java api.

The vert.x java-api also supports synchronous forms of that functions, however
the yvertx api does not, because a yeti wrapper is there only of little benefit.

## Accessing the filesystem functions and object

To access the filesystem functions use the `yvertx.fileSystem` property.

    yvertx = load yeb.yvertx;
    fs = yvertx.fileSystem;
    fs.copy ...;

To access the underlying vert.x `FileSystem` object us the `fs` property
of fileSystem

    fsO is ~FileSystem = yvertx.fileSystem.fs;
    fsO#copySync(...);

## Promise

All the fileSystem functions return a promise which replaces the
`AsyncResultHandler` in the java api. The promise has a `Some value` if the 
operation was succesful or an `None ~Exception` if there was a problem.

Otherwise the functions all work exactly the same as their java counterpart.

    

   


