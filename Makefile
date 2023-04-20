run: 
	./mvnw --show-version clean javafx:run 
format:
	./mvnw --show-version clean spotless:apply pmd:check
test:
	./mvnw --show-version clean spotless:apply pmd:check test
