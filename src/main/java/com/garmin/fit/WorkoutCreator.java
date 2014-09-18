package com.garmin.fit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutCreator {
  public static void main(String[] args) {
    System.out.println("FIT Encode Example Application");

    FileEncoder encode;

    try {
      encode = new FileEncoder(new java.io.File("/home/benzonico/Development/Garmin/semaine4.fit"));
    } catch (FitRuntimeException e) {
      System.err.println("Error opening file test.fit");
      return;
    }

    FileIdMesg fileIdMesg = new FileIdMesg();
    fileIdMesg.setManufacturer(Manufacturer.GARMIN);
    fileIdMesg.setProduct(1328);
    fileIdMesg.setSerialNumber(3880115687L);
    fileIdMesg.setType(File.WORKOUT);
    fileIdMesg.setTimeCreated(new DateTime(new Date()));
    encode.write(fileIdMesg);

    FileCreatorMesg fileCreatorMesg = new FileCreatorMesg();
    fileCreatorMesg.setSoftwareVersion(300);
    encode.write(fileCreatorMesg);

    WorkoutMesg workoutMesg = new WorkoutMesg();
    List<WorkoutStepMesg> workoutStepMesgs = new ArrayList<WorkoutStepMesg>();
    WorkoutStepMesg step = new WorkoutStepMesg();
    step.setDurationTime(1200.0f);
    step.setDurationType(WktStepDuration.TIME);
    step.setTargetType(WktStepTarget.OPEN);
    step.setIntensity(Intensity.WARMUP);
    workoutStepMesgs.add(step);

    step = new WorkoutStepMesg();
    step.setDurationType(WktStepDuration.DISTANCE);
    step.setDurationDistance(200f);
    step.setTargetType(WktStepTarget.OPEN);
    step.setIntensity(Intensity.ACTIVE);
    workoutStepMesgs.add(step);

    step = new WorkoutStepMesg();
    step.setDurationType(WktStepDuration.TIME);
    step.setDurationTime(30.0f);
    step.setTargetType(WktStepTarget.OPEN);
    step.setIntensity(Intensity.REST);
    workoutStepMesgs.add(step);

    step = new WorkoutStepMesg();
    step.setRepeatSteps(10L);
    step.setDurationType(WktStepDuration.REPEAT_UNTIL_STEPS_CMPLT);
    step.setDurationStep(1L);
    workoutStepMesgs.add(step);

    step = new WorkoutStepMesg();
    step.setDurationType(WktStepDuration.TIME);
    step.setDurationTime(150.0f);
    step.setTargetType(WktStepTarget.OPEN);
    step.setIntensity(Intensity.REST);
    workoutStepMesgs.add(step);

    step = new WorkoutStepMesg();
    step.setRepeatSteps(2L);
    step.setDurationType(WktStepDuration.REPEAT_UNTIL_STEPS_CMPLT);
    step.setDurationStep(1L);
    workoutStepMesgs.add(step);

    step = new WorkoutStepMesg();
    step.setDurationType(WktStepDuration.OPEN);
    step.setTargetType(WktStepTarget.OPEN);
    step.setIntensity(Intensity.WARMUP);
    workoutStepMesgs.add(step);

    workoutMesg.setWktName("Semaine 4");
    workoutMesg.setNumValidSteps(workoutStepMesgs.size());
    workoutMesg.setSport(Sport.RUNNING);
    encode.write(workoutMesg);

    int i = 0;
    for (WorkoutStepMesg workoutStepMesg : workoutStepMesgs) {
      workoutStepMesg.setMessageIndex(i);
      i++;
      encode.write(workoutStepMesg);
    }


    try {
      encode.close();
    } catch(FitRuntimeException e) {
      System.err.println("Error closing encode.");
      return;
    }

    System.out.println("Encoded FIT file test.fit.");
  }

}
