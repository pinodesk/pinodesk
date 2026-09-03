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

function build_exe {
	./mvnw -V clean package -DskipTests -Pexe
}

function build_deb {
	./mvnw -V clean package -DskipTests -Pdeb
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
	elif [ $1 == "build:exe" ]; then
		echo "Build application with 'exe' profile"
		build_exe
	elif [ $1 == "build:deb" ]; then
		echo "Build application with 'deb' profile"
		build_deb
	fi
fi
