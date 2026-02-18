# Ollama: Local LLMs with Docker

Ollama makes it easy to run local large language models (LLMs) on your own machine. This README shows you how to quickly set up and run Ollama locally using Docker.

## Prerequisites

- [Docker installed](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/) (optional, for simplified management)
- Sufficient system resources:
  - **Minimum:** 8GB RAM
  - **Recommended:** 16GB+ RAM for better performance
  - GPU support recommended for faster inference

## Quick Start

### 1. Pull the Ollama Docker Image

```sh
docker pull ollama/ollama

docker run -d -p 11434:11434 --name ollama ollama/ollama
