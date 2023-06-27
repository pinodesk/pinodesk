#!/bin/bash

function fix {
	./mvnw -V clean spotless:apply pmd:check
}

function run_test {
	./mvnw -V clean spotless:apply pmd:check test
}

function run {
	./mvnw -V clean javafx:run
}

function build_dev {
	./mvnw -V clean package -DskipTests
}

function build_dist {
	./mvnw -V clean package -DskipTests -Pdist
}

if [ ! -z $1 ]; then
	if [ $1 == "fix" ]; then
		echo "Fix static code analytics"
		fix
	elif [ $1 == "test" ]; then
		echo "Fix static code analytics and run tests"
		run_test
	elif [ $1 == "run" ]; then
		echo "Start running in development mode"
		run
	elif [ $1 == "build" ]; then
		echo "Build application with 'dist' profile"
		build_dist
	elif [ $1 == "build:dev" ]; then
		echo "Build application with 'dev' profile"
		build_dev
	fi
fi
