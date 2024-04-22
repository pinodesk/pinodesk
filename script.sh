#!/bin/bash

function fix {
	mvn -V clean spotless:apply pmd:check
}

function run_test {
	mvn -V clean spotless:apply pmd:check test
}

function run {
	mvn -V clean javafx:run
}

function build_exe {
	mvn -V clean package -DskipTests -Pexe
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
	fi
fi
