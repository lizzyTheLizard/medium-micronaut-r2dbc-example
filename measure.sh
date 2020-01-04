#!/bin/bash

function start(){
    #Recreate the container to always have a startup from null
    disposeContainer "$1"
    docker-compose build "$1"

    #Start the container and measure how long it takes untill we get a valid result
    startNS=$(date +"%s%N")
    startContainer "$1"
    endNS=$(date +"%s%N")
    startuptime=$(echo "scale=2;($endNS-$startNS)/1000000000" | bc)
    echo "$1, Startup time, $startuptime" >> results.csv

    #Measure memory
    memory=$(docker stats --format "{{.MemUsage}}" --no-stream "medium-micronaut-r2dbc-example_$1_1")
    echo "$1, Memory Usage (Startup), $memory" >> results.csv

    #Make sure container runs normally
    checkContainer "$1"

    #Stop container again
    disposeContainer "$1"
}

function disposeContainer() {
    docker-compose stop $1
    docker-compose rm -f $1
}

function startContainer() {
    docker-compose up -d $1
    cameUp=0
    for (( i=0; i<100; i++))
    do
        sleep 0.3
        curl -s http://localhost:8080/issue/550e8400-e29b-11d4-a716-446655440000/ | grep "This is a test" > /dev/null
        if [ $? -eq 0 ]
        then
            return;
        fi;
    done
    curl http://localhost:8080/issue/550e8400-e29b-11d4-a716-446655440000/ -v
    fail "Container could not start"
}

function checkContainer() {
    curl -s http://localhost:8080/issue/ | grep "This is a test" > /dev/null
    if [ $? -ne 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed GET ALL for $1"
    fi;

    #Create a new entry
    curl -X POST http://localhost:8080/issue/ \
        -d '{"id":"550e8400-e29b-11d4-a728-446655440000","name":"Test 123", "description":"Test 28"}' \
        -H "Content-Type: application/json" 
    curl -s http://localhost:8080/issue/ | grep "Test 28" > /dev/null
    if [ $? -ne 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed CREATE for $1"
    fi;

    #Patch new entry
    curl -X PATCH http://localhost:8080/issue/550e8400-e29b-11d4-a728-446655440000/ \
        -d '{"description":"Test NEW"}' \
       	-H "Content-Type: application/json" 
    curl -s http://localhost:8080/issue/ | grep "Test NEW" > /dev/null
    if [ $? -ne 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed PATCH for $1"
    fi;

    #Delete new entry
    curl -X DELETE http://localhost:8080/issue/550e8400-e29b-11d4-a728-446655440000/
    curl -s http://localhost:8080/issue/ | grep "Test NEW" > /dev/null
    if [ $? -eq 0 ]
    then
        curl http://localhost:8080/issue/ -v
        fail "Failed DELETE for $1"
    fi;
}

function fail() {
    echo "$1"  1>&2;
    exit -1
}

rm -f results.csv
docker-compose stop
docker-compose rm --force
docker-compose up -d postgres
./gradlew clean assemble
start "example"
start "example-graal"
start "spring-compare"
cat results.csv;
