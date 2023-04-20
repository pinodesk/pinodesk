run: 
	./mvnw clean javafx:run 
format:
	./mvnw clean spotless:apply pmd:check
test:
	./mvnw clean spotless:apply pmd:check test
