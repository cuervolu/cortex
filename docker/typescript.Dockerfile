# Use a node image as base image
FROM node:20-alpine

# The exercises use pnpm as package manager
RUN npm install -g pnpm

WORKDIR /app

# Copy the package.json and pnpm-lock.yaml files from cortex-exercises
COPY ../../cortex-exercises/exercises/typescript/package.json cortex-exercises/exercises/typescript/pnpm-lock.yaml ./
COPY ../../cortex-exercises/exercises/typescript/tsconfig.json cortex-exercises/exercises/typescript/vitest.config.ts ./
COPY ../../cortex-exercises/exercises/typescript/biome.json ./

# Install dependencies
RUN pnpm install --frozen-lockfile

# Copy the exercises
COPY ../../cortex-exercises/exercises/typescript /app/exercises

# Install the dependencies of the exercises
RUN cd /app/exercises && pnpm install --frozen-lockfile

# Add node_modules/.bin to PATH
ENV PATH="/app/node_modules/.bin:${PATH}"

# Set the working directory to the typescript exercises
WORKDIR /app/exercises

# Default command (can be overwritten at runtime)
CMD ["pnpm", "test"]
