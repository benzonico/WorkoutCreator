#!/bin/bash
GARMIN_EXTRACTOR=/opt/Garmin-Forerunner-610-Extractor
workout=$1
filename="$HOME/.workoutCreator"
SERIAL=''
while read -r line
do
    name=$line
    SERIAL=$name
done < "$filename"

echo "SERIAL: $SERIAL"
java -jar target/WorkoutCreator-1.0-SNAPSHOT-jar-with-dependencies.jar -f $workout

workoutfit=${workout%.*}.fit
echo $workoutfit

sudo mv $workoutfit $HOME/.config/garmin-extractor/$SERIAL/workouts

sudo python $GARMIN_EXTRACTOR/garmin.py --upload
