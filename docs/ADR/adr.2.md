# ADR 2

## TOPIC : Choosing the stack of the web app.

### Authors : Loriane HILDERAL, Clarence HIRSCH, Alan DIOT

### Date : 30/03/2026

## CONTEXT

We need to create a web app and store data.

## HYPOTHESES

- Monolithic backend JAVA - React frontend => The team has a bit less experiences with this stack. Faster to develop.
  Easier to debug and deploy.
- Microservices JAVA / React (Reservation service and emailing service) => The team has experiences in this stack,
  independance of the services
- Monolithic backend NestJS - React => The team has much experiences with this stack. Easier to debug and deploy.
- Microservices NestJS / React (Reservation service and emailing service) => Independance of the services

- PostgreSQL as the main database (large ecosystem, json supported)
- MySQL as the main database
- Not a Relational database (less structure)

1. Monolithic backend in Java with React frontend
    - The team has slightly less experience with this stack than with NestJS.
    - Faster to develop for a first version because all business logic is centralized in a single application.
    - Easier to debug, test, and deploy than a distributed architecture.
    - Spring provides a mature and robust ecosystem for backend development, security, validation, and database access.
    - This option is well suited for projects with limited initial functional scope and a moderate number of developers.
2. Java / React microservices architecture(Reservation service and emailing service)
    - The team already has experience with this architecture.
    - Services are independent, which improves separation of concerns.
    - Easier to evolve specific parts of the system independently in the long term.
    - Better scalability potential if some features require different load handling.
    - However, this architecture introduces more complexity: service communication, monitoring, deployment pipelines,
      error
      handling, and local development become harder to manage.
    - For a project of this size, the operational overhead may outweigh the benefits.
3. Monolithic backend in NestJS with React frontend
    - The team has strong experience with this stack.
    - Easier to debug and deploy because the application remains centralized.
    - Development speed can be high thanks to team familiarity and the productivity of TypeScript.
    - Good fit for modern web development and easy integration with a React frontend.
    - However, Java and Spring may provide stronger guarantees in terms of long-term robustness, ecosystem maturity, and
      enterprise-oriented architecture.
4. NestJS / React microservices architecture(Reservation service and emailing service)
    - Offers independence between services and clear separation of domains.
    - Can support future scaling and independent deployment if the application grows significantly.
    - Matches the team’s existing experience with NestJS.
    - Still introduces additional architectural and operational complexity.
    - This option may be excessive for an initial version where simplicity, delivery speed, and maintainability are more
      important than fine-grained service separation.

## Database hypotheses

1. PostgreSQL as the main database
    - Large ecosystem and strong community support.
    - Reliable and well suited for relational business data.
    - Supports JSON/JSONB, which gives flexibility for semi-structured data when needed.
    - Good balance between strict relational modeling and modern features.
    - Well integrated with Java Spring and React-based application stacks.

2. MySQL as the main database
    - Popular and widely used relational database.
    - Good performance and broad tooling support.
    - Easier to find hosting and deployment options.
    - However, PostgreSQL generally offers richer advanced features and more flexibility for complex queries and
      structured
      business applications.

3. Non-relational database
    - May provide more flexibility for unstructured or evolving data models.
    - Can be useful when schema constraints must remain loose.
    - However, the application domain relies on structured entities and clear relationships, such as users,
      reservations,
      and notifications.
    - A non-relational approach would reduce consistency guarantees and make some data relations harder to manage.

## SOLUTION ACCEPTED

- A monolithic architecture is the most appropriate choice for the current scope of the project because it reduces
  technical and operational complexity.
- It simplifies development, testing, debugging, and deployment, which is important for delivering a stable first
  version quickly.
- Although the team has slightly less experience with Java Spring than with NestJS, Spring is a mature and widely
  adopted framework with strong support for enterprise-grade backend development.
- PostgreSQL is retained because it is reliable, well documented, feature-rich.
- Compared with a microservices architecture, this solution avoids premature complexity for a project that does not yet
  require independent scaling or deployment of multiple services.
- This architecture suit the timeline and resource constraints of the project while providing a solid foundation for
  future evolution if needed.

## CONSEQUENCES

- Less modularity on the backend compared with a microservices architecture.
- The application may be harder to split later if strong domain separation becomes necessary.
- Deployment and maintenance are simpler in the short term.
- Debugging and testing are easier because all backend logic is centralized.
- The team can rely on a stable, robust, and industry-standard stack.
- The solution remains scalable enough for the current needs of the project, while leaving open the possibility of
  future architectural evolution if required.