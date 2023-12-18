#!/bin/bash

# This script starts, stops or shows the state of the JOD Smart Van Distribution.
# and it is used to start the JOD Smart Van Distribution from the command line.
#
# It is assumed that the JOD Smart Van Distribution was cloned into the same
# directory as the "Smart Van 4 Android" project.
#
# Usage:
#   jod_sv_start.sh <start|stop|state|log> [true]
#
#   start: starts the JOD Smart Van Distribution
#   stop: stops the JOD Smart Van Distribution
#   state: shows the state of the JOD Smart Van Distribution
#   log: shows the log of the JOD Smart Van Distribution
#   true: use the development version of the JOD Smart Van Distribution


# Check if at least one argument is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <start|stop|state|log> [true]"
    exit 1
fi

action=$1


# Set the target directory
jod_sv_dir="../com.robypomper.smartvan.jod_smart_van"
jod_sv_version="1.0.0"
jod_sv_dir_dist="$jod_sv_dir/build/JOD_Smart_Van/$jod_sv_version"
if [ "$2" == "true" ]; then
  echo "Using development version"
  jod_sv_version="1.0.0-DEV"
  jod_sv_dir_dist="$jod_sv_dir/build/JOD_Smart_Van/$jod_sv_version"
fi


# Save the current directory
current_dir=$(pwd)


# Check if the target directory exists
if [ ! -d "$jod_sv_dir" ]; then
    echo "Error: JOD Smart Van project's directory '$jod_sv_dir' not found."
    exit 1
fi
if [ ! -d "$jod_sv_dir_dist" ]; then
    if [ "start" == "$action" ]; then
        echo "Warning: JOD Smart Van distribution's directory '$jod_sv_dir_dist' not found, build it"
        cd "$jod_sv_dir"
        bash scripts/build.sh
        cd "$current_dir"
    else
        echo "Error: JOD Smart Van distribution's directory '$jod_sv_dir_dist' not found."
        exit 1
    fi
fi


# Change to the target directory
cd "$jod_sv_dir_dist"
pwd
ls
# Execute the appropriate action
case $action in
    "start")
        bash start.sh
        ;;
    "stop")
        bash stop.sh
        ;;
    "state")
        bash state.sh
        ;;
    "log")
        tail -f logs/jospJOD.log
        ;;
    *)
        echo "Unsupported action: $action. Use 'start', 'stop' or 'state'."
        exit 1
        ;;
esac


# Return to the original directory
cd "$current_dir"
