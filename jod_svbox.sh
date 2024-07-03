#!/bin/bash

# This script starts, stops or shows the state of the JOD Smart Van Distribution.
# and it is used to start the JOD Smart Van Distribution from the command line.
#
# It is assumed that the JOD Smart Van Distribution was cloned into the same
# directory as the "Smart Van 4 Android" project.
#
# Usage:
#   jod_svbox.sh <start|stop|state|log> [true]
#
#   init: initializes the JOD Smart Van Distribution and his dependencies
#   start: starts the JOD Smart Van Distribution
#   stop: stops the JOD Smart Van Distribution
#   state: shows the state of the JOD Smart Van Distribution
#   log: shows the log of the JOD Smart Van Distribution
#
#   true: use the development version of the JOD Smart Van Distribution

# Requirements: git curl


# Check if at least one argument is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <init|start|stop|state|log> [true]"
    exit 1
fi

# Set the action and the development mode
action=$1
dev_mode=$2

# Save directories paths
current_dir=$(pwd)
script_dir=$(dirname "$0")

# Set the target directories
sv_ma_dir="$script_dir"
jod_sv_dir="$sv_ma_dir/../com.robypomper.smartvan.jod_smart_van"
jod_sv_version="1.0.0"
jod_sv_dir_dist="$jod_sv_dir/build/JOD_Smart_Van/$jod_sv_version"
if [ "$dev_mode" == "true" ]; then
  echo "DEVELOPMENT Enabled"
  jod_sv_version="1.0.0-DEV"
  jod_sv_dir_dist="$jod_sv_dir/build/JOD_Smart_Van/$jod_sv_version"
fi

# Set the firmware repositories
firmware_repos=(
    "https://github.com/Smart-Van-2-0/com.robypomper.smartvan.fw.victron.git"
    "https://github.com/Smart-Van-2-0/com.robypomper.smartvan.fw.sim7600.git"
    "https://github.com/Smart-Van-2-0/com.robypomper.smartvan.fw.upspack_v3.git"
    "https://github.com/Smart-Van-2-0/com.robypomper.smartvan.fw.sensehat.git"
    "https://github.com/Smart-Van-2-0/com.robypomper.smartvan.fw.ioexp.git"
)


# Check if the JOD Smart Van project's directory exists and if the action is 'init'
if [ -d "$jod_sv_dir" ]; then
    if [ "init" == "$action" ]; then
        echo "Error: JOD Smart Van project's directory '$jod_sv_dir' already exists."
        echo "       Please, remove the JOD Smart Van project and his dependencies' directory first,"
        echo "       executing the 'rm -rf ../com.robypomper.smartvan.jod_smart_van/ ../com.robypomper.smartvan.fw.*' command."
        exit 1
    fi
fi


# Check if the target directory exists
if [ ! -d "$jod_sv_dir" ]; then
    if [ "init" == "$action" ]; then
        echo "Initializing JOD Smart Van project"
        cd "$script_dir/.."
        echo "1. Cloning JOD Smart Van project"
        git clone https://github.com/Smart-Van-2-0/com.robypomper.smartvan.jod_smart_van.git &> /dev/null || { echo "Error on cloning JOD Smart Van project"; exit 1; }
        if [ "$2" == "true" ]; then
            echo "2. Cloning JOD Smart Van project's dependencies"
            for repo in "${firmware_repos[@]}"; do
                git clone $repo &> /dev/null || { echo "Error on cloning $repo project"; exit 1; }
            done
        fi
        cd "$current_dir"
        echo "JOD Smart Van project cloned successfully."
        exit 0
    else
        echo "Error: JOD Smart Van project's directory '$jod_sv_dir' not found."
        echo "       Please, clone the JOD Smart Van project in the same directory as the 'Smart Van 4 Android' project."
        echo "       Or execute this script again with the 'init' sub-command."
        exit 1
    fi
fi


# Check if the JOD Smart Van distribution exists, if not build it
if [ ! -d "$jod_sv_dir_dist" ]; then
    if [ "start" == "$action" ]; then
        echo "JOD Smart Van distribution's directory '$jod_sv_dir_dist' not found, build it"
        cd "$jod_sv_dir"
        if [ "$dev_mode" == "true" ]; then
          echo "1. Building JOD Smart Van distribution"
          bash scripts/build.sh configs/jod_dist_configs-DEV.sh
        else
          echo "1. Building JOD Smart Van distribution"
          bash scripts/build.sh
        fi
        cd "$current_dir"
        echo "JOD Smart Van distribution built successfully."
    else
        echo "Error: JOD Smart Van distribution's directory '$jod_sv_dir_dist' not found."
        echo "       The JOD Smart Van has not been built yet."
        echo "       Please, build the JOD Smart Van distribution first"
        echo "       or execute this script again with the 'start' sub-command."
        exit 1
    fi
fi


# Change to the target directory
cd "$jod_sv_dir_dist"
# Execute the appropriate action
case $action in
    "start")
        bash start.sh
        ;;
    "stop")
        bash stop.sh
        ;;
    "state")
        echo "JOD Smart Van Distribution state:"
        echo "-----------------------------------"
        echo "Directory: $(pwd)"
        bash state.sh
        ;;
    "log")
        tail -f logs/jospJOD.log
        ;;
    *)
        echo "Unsupported action: $action. Use 'init', 'start', 'stop', 'state' or 'log'."
        cd "$current_dir"
        exit 1
        ;;
esac


# Return to the original directory
cd "$current_dir"
exit 0
