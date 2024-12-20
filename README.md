# Notebook LLM Project

## Overview
The Notebook LLM Project is a Spring AI-based application that allows users to upload PDF documents and ask questions about the content. The application leverages two large language models (LLMs) for its functionality: **Groq OpenAI** and **Ollama**. It is built using Spring Boot and follows a Gradle project structure.

## Features
- **Upload PDFs**: Users can upload PDF documents for processing.
- **Ask Questions**: Users can interact with the application to query the content of the uploaded PDF.
- **Multiple Model Support**: The application integrates with two LLMs:
  - **Groq OpenAI**
  - **Ollama**
- **Spring AI Integration**: Simplifies interaction with the language models.
- **Cassandra**: As a VectorStore and to store user's chat.
- **PostgreSQL**: To store user's information

## Prerequisites
To run this project, ensure you have the following installed:
- Java 21 or higher
- Gradle 7.6 or higher
- A valid API key for OpenAI and Ollama models

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/KushParsaniya/notebook-llm
   cd notebook-llm
   ```

2. **Add API key**:
Add api key for openaAI, and install ollama in your local machine.

3. Build the Project: Use Gradle to build the project:
  ```bash
  ./gradlew build
  ```

4. Run the Application: Start the Spring Boot application:
  ```bash
  ./gradlew bootRun
  ```

## Project Structure
- src/main/java: Contains the Java source code for the application.
- src/main/resources: Contains configuration files like application.yml.
- build.gradle: The Gradle build script.

## How It works:
1. **PDF Upload:**
   - The user uploads a PDF document via the UI.
   - The application processes the uploaded file and extracts its content.
   - and store it in vector store with metadata of user.
     
2. **Query Handling:**
   - The user inputs a question related to the PDF content.
   - The question is processed by the integrated LLMs (Groq OpenAI and Ollama).
     
3. **Information Storage:**
   - Ollama extracts useful information from user interactions and stores it in the Cassandra vector store.
   - User information and chat history are stored in PostgreSQL.
     
4. **Response Generation:**
   - The application uses the LLMs to generate a response based on the PDF content.
   - The response is displayed to the user.
