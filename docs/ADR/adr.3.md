# ADR 3 
## TOPIC : How to handle the emailing part of the specs.
### Authors : Loriane HILDERAL, Clarence HIRSCH, Alan DIOT
### Date : 30/03/2026

## CONTEXT
We need to send emails once someone has booked a parking spot. It doesn't need to receive the email right away. We want to use an extern email service app.

## HYPOTHESES
- Making everything ourselves. => No time for that. More time and financial costs.
- Using a REDIS queue and a worker to create the email template and then call the external service API => Simplicity of configuration and high performance. Can be used for other problems.
- Using RabbitMQ as a service. => Harder to implement but a better security system.

## SOLUTION ACCEPTED
- Using a REDIS queue

## CONSEQUENCES

- Less formation for the team
- Better performances and reactivity for sending emails.
- Could use REDIS for caching later on.
