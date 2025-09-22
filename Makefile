.PHONY: up down up3n be run test smoke

up:
cd infra && docker compose -f docker-compose.dev.yml up -d --remove-orphans

down:
cd infra && docker compose -f docker-compose.dev.yml down -v

up3n:
cd infra && docker compose -f docker-compose.dev-3n.yml up -d --remove-orphans

be:
cd backend-java && ./mvnw -q compile

run:
cd backend-java && ./mvnw -q quarkus:dev

test:
cd backend-java && ./mvnw -q -DskipTests=false test

smoke:
curl -sS localhost:8080/posts?limit=1 | jq . || true
