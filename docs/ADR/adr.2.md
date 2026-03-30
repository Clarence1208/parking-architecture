# ADR 2 
## TOPIC : Choosing the stack of the web app.
### Authors : Loriane HILDERAL, Clarence HIRSCH, Alan DIOT
### Date : 30/03/2026

## CONTEXT
We need to create a web app and store data. 

## HYPOTHESES
- Monolithic backend JAVA - React frontend => The team has a bit less experiences with this stack. Faster to develop. Easier to debug and deploy.
- Microservices JAVA / React (Reservation service and emailing service) => The team has experiences in this stack, independance of the services
- Monolithic backend NestJS - React => The team has much experiences with this stack. Easier to debug and deploy.
- Microservices NestJS / React (Reservation service and emailing service) => Independance of the services

- PostgreSQL as the main database (large ecosystem, json supported)
- MySQL as the main database 
- Not a Relational database (less structure)

## SOLUTION ACCEPTED

- Monolithic backend Java Spring with a SPA ReactJS backed with a PostgreSQL database.
  

## CONSEQUENCES
- Less modularity on the backend
- Team is more comfortable with the stack of the project
