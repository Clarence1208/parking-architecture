#!/bin/sh
# Continuously recompile sources in the background.
# DevTools monitors build/classes/ and restarts the app on any .class change.
./gradlew classes -t --no-daemon &

./gradlew bootRun --no-daemon
