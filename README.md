# Playing with artificial intelligence

Kotlin Springboot application. Solution based on **Ollama** and **langchain4j**

## Run the project
Ollama is not included in this solution, so you need to run it separately. 
You can use a docker image or run it locally. 
Keep in mind that GPU is not virtualized in mac, so if you are using a mac is better to run it locally instead of use a docker image (this is the reason Ollama is not integrated in this solution).
Right now I'm testing with Google Gemma model, so you need to download latest version of Gemma model.

    - Start Ollama: ollama serve
    - Download latest gemma model: ollama pull gemma:latest
    - Run the project: ./gradlew bootRun
    - There are some test endpoints that you can use from your InteliJ in file testEndpoints.http



Once you have finished, you will need to kill Ollama server process (there are not a stop server command)