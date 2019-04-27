FROM clojure:tools-deps-alpine as deps
WORKDIR /app
COPY deps.edn .
RUN clojure -A:flyway:test:runner -Stree

FROM deps as system
WORKDIR /app
COPY . .

# tests

FROM system
WORKDIR /app
RUN clojure -A:flyway:test:runner

# production

FROM system
EXPOSE 80
WORKDIR /app
CMD ["clojure", "-m", "auth-provider.server"]