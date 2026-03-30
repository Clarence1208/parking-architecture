# ADR 1 
## TOPIC : Choosing a kind of application for the parking reservation process.
### Authors : Loriane HILDERAL, Clarence HIRSCH, Alan DIOT
### Date : 30/03/2026

## CONTEXT
We need to create a real replacement app for this parking reservation process. 
This application is intended to be used by non-technical people, so, it has to be simple, practical and adapted to their specific needs.

## HYPOTHESES
- A desktop app for administrators (so they can set everything up on their work computer) and a mobile app for any employees that wants to make a parking booking.
- A web app for administrators (so they can set everything up) and a mobile app for any employees that wants to make a parking booking. => Might be easier for employees when they are not on their work time. BUT employees must have the app.
- A web app that handles both way to use the app (set up and booking). => Available on any devices.

## SOLUTION ACCEPTED

- A web app that handles both way to use the app (set up and booking).

## CONSEQUENCES

- Only one app so it will be easier to maintain.
- Need to have different roles that let's you have two different mode on the web app
- Need to decide if the web app is widely accessible on the web or if we install a VPN or something like that.
