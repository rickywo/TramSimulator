COSC 1170 / COSC 1197
Foundations of Distributed Systems
Assignment 1
Remote Procedure Calling
======================================
Aims
--------------------------------------
The purpose of this assignment is to exemplify and explore some important issues of remote procedure
calling. This project requires an understanding of the basics of remote procedure calling.


Menu
--------------------------------------
[0]. Manual Operation
    Manually input tram id and route id to start simulation.
[1]. Demo1: Exceed maximum route capacity
    Demonstrate a scenario that more than 5 trams have been put in a route
[2]. Demo2: Duplicate Tram id
    Demonstrate a scenario that two trams with identical id cannot put into this system
[3]. Demo3: Cannot find route id
    Demonstrate a scenario that the route id not exist.
[4]. Demo4: Normal demo
    Demonstrate the situation when trackingService is running in its full capacity.

Usage
--------------------------------------
1. To run demo locally:
    Execute Main.java to run application locally.

2. To run a server remotely
    Execute TrackingService on remote server, then set the address in Main.java to create a stub.
    Then execute Main.java to run simulator on client site.

Classes
--------------------------------------
1. TrackingServiceInterface
2. TrackingService
3. Route
4. TramClient
5. RPCMessage
6. Message
