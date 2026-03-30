# ─────────────────────────────────────────────────────────────────────────────
# Dockerfile — Swag Labs Selenide Tests
#
# Multi-stage build:
#   Stage 1 (deps)  : cache ALL Maven dependencies AND plugin dependencies
#                     so the runner stage needs zero network access
#   Stage 2 (runner): install Chrome, copy source, run tests + generate report
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: full dependency cache ───────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS deps
WORKDIR /build
COPY pom.xml .
COPY src ./src

# Cache library deps, plugin deps, AND pre-download the Allure CLI binary
# by doing a dry-run compile + test-compile + allure:report on an empty result set.
# || true ensures the stage does not fail if there are no test results yet.
RUN mvn dependency:go-offline -B --no-transfer-progress && \
    mvn dependency:resolve-plugins -B --no-transfer-progress && \
    mvn test-compile -B --no-transfer-progress && \
    mvn allure:report --no-transfer-progress || true

# ── Stage 2: test runner ──────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS runner

# Copy the fully-populated Maven cache (libs + plugins + Allure CLI)
COPY --from=deps /root/.m2 /root/.m2

WORKDIR /app
COPY pom.xml .
COPY src ./src

ENV HEADLESS=true
ENV BROWSER=chrome
ENV SUITE=""

# Run tests then immediately generate the Allure HTML report
ENTRYPOINT ["sh", "-c", \
  "if [ -n \"$SUITE\" ]; then \
     mvn test -P$SUITE -Dheadless=$HEADLESS -Dbrowser=$BROWSER \
       -Dselenide.remote=$SELENIDE_REMOTE --no-transfer-progress; \
   else \
     mvn test -Dheadless=$HEADLESS -Dbrowser=$BROWSER \
       -Dselenide.remote=$SELENIDE_REMOTE --no-transfer-progress; \
   fi; \
   echo '--- Generating Allure HTML report ---'; \
   mvn allure:report --no-transfer-progress; \
   echo '--- Done. Report is in /app/target/allure-report ---';"]