# Stage 1: Build WAR with Ant
FROM openjdk:17 AS build

WORKDIR /app

# Copy entire project (src, build.xml, web folder, etc.)
COPY . .

# Install ant
RUN apt-get update && apt-get install -y ant

# Run Ant build to generate WAR file
RUN ant -f build.xml

# Stage 2: Tomcat + deploy WAR
FROM tomcat:10.1-jdk17

# Optional: clear default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file from Ant build (assumes war in /app/dist or /app/build)
COPY out/artifacts/swp391_clubs_management_war/swp391-clubs-management_war.war /usr/local/tomcat/webapps/clubs.war

# Expose port
EXPOSE 8080
