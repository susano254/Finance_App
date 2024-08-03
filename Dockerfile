FROM openjdk:17
ADD target/*.jar FinanceSpring.jar
ENTRYPOINT ["java","-jar","FinanceSpring.jar"]
