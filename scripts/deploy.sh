#!/bin/bash

REPOSITORY=/home/ubuntu  #1
PROJECT_NAME=Backend

cd $REPOSITORY/$PROJECT_NAME/  #2

echo "> 프로젝트 build 시작"
./gradlew build --exclude-task test    #4

echo "> 홈 디렉토리로 이동"
cd $REPOSITORY

echo "> Build 파일 복사"
cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/  #5

echo "> 현재 구동중인 애플리케이션 pid 확인"
CURRENT_PID=$(lsof -ti:80)     #6

echo "현재 구동중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then            #7
        echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -9 $CURRENT_PID"
    kill -9 $CURRENT_PID
    sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/ | grep jar | tail -n 1)    #8

echo "> JAR Name : $JAR_NAME"

sudo nohup java -jar $REPOSITORY/$JAR_NAME 2>&1 &       #9