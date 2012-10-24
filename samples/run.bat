SET CP=.;..\..\target\classes;..\..\lib\managed\compile\yeti-0.9.6+-20121018.150338-3.jar
echo %CP%
vertx run %1 -cp "%CP%"
