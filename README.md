# Playing with artificial intelligence

Kotlin Springboot application. Solution based on **Ollama** , **langchain4j** and **pgvector**  
This project is just a PoC for testing the integration of these technologies. No good practices are applied, no security, no tests, etc.

## Run the project
Ollama is not included in this solution, so you need to run it separately. 
You can use a docker image or run it locally. 
Keep in mind that GPU is not virtualized in mac, so if you are using a mac is better to run it locally instead of use a docker image (this is the reason Ollama is not integrated in this solution).

Right now I'm testing with Google Gemma model for ChatBot feature and nomic-embed-text for vectorial searches , so you need to download latest version of this two models.

    - Start Ollama: ollama serve
    - Download latest gemma model: ollama pull gemma:latest
    - Download latest nomic-embed-text: ollama pull nomic-embed-text:latest
    - Start dockerized infra: docker compose up
    - Run the project: ./gradlew bootRun
    - There are some test endpoints that you can use from your InteliJ in file testEndpoints.http

##Feature 1 : ChatBot

##Feature 2 : Vectorial Searches


## Stop the project
Once you have finished:

    - Kill springboot application
    - kill Ollama server process (there are not a stop server command)
    - Stop dockerized infra: docker compose down

