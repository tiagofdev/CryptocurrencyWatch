# Use an official Node.js runtime as a parent image
FROM node:19


# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the /client directory in the container
COPY ./package*.json /client/

# Update package list and install npm
RUN apt-get update && \
    apt-get install -y npm

# Install Angular CLI globally
RUN npm install -g @angular/cli

# Set the working directory to /client
WORKDIR /client

# Install project dependencies
RUN npm install
