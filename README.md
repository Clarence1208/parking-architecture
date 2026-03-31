# Parking Architecture

The projects contains a frontend in React and Typescript and a backend part in Java Spring.


# Launch project
- Dev environnement (with a watch mode): 
```bash
docker compose -f docker-compose.dev.yml up --build
```

- Prod environnement: 
```bash
docker compose -f docker-compose.yml up --build
```

### Contributing

- Make a branch 
- Run tests before pushing your code with : 
```bash
./scripts/run-tests.sh
```

- With test coverage: 
```bash
./scripts/run-tests.sh --coverage
```
It will run frontend and backend tests in one go.

### Authors
Alan DIOT
Loriane HILDERAL
Clarence HIRSCH