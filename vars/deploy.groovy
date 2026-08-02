def call(){

    sh """
    docker stop weather-app || true
    docker rm weather-app || true

    docker run -d \
      --name weather-app \
      -p 3000:3000 \
      weather-app:latest
    """

}