export IMAGE_NAME="pagopa/bs-banking-services"
docker build -t $IMAGE_NAME .
docker container run -d --name bs --rm -p 8080:8080 -p 5005:5005 $IMAGE_NAME