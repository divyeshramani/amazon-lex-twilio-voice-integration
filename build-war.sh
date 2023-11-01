#!/bin/bash
set -e # stop script in case of error at any step.

# Clean and build package
mvn clean compile package

# Create temp directory
rm -rf dist/ && mkdir dist

# Copy jar file with all dependencies
cp target/*dependencies.jar dist/

# Build war file
ant war

# Remove temp files
rm -rf dist/ target/
