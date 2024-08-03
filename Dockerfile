FROM openjdk:17
#ADD target/*.jar FinanceSpring.jar

# Add the project files
ADD . .

# Expose the port
EXPOSE 8080

# build the application
RUN ./mvnw install

# Run the jar file
ENTRYPOINT ["java","-jar","FinanceSpring.jar"]
