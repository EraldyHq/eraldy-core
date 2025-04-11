# HTTP module (Rest)

## About
This is the HTTP module.

It provides a https FileSystem NIO.


## Authentication Management for users

We should implement an Oauth server to store the client token
in order to retrieve them locally or in a batch run (Vertx offers them luckily).

Why ?
  * To make it easy for user to get access to third rest api. For instance, [Linkedin is pretty difficult](https://learn.microsoft.com/en-us/linkedin/shared/authentication/client-credentials-flow)
and not easy to found.
  * Not all services offer an application token for native application.



