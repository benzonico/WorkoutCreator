package com.garmin.fit;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class WorkoutToFit {

  public static void transformToFit(String filename) {
    FileEncoder encode;
    java.io.File file = new java.io.File(filename);
    String fitName = file.getName().substring(0, file.getName().lastIndexOf('.')) + ".fit";
    try {
      encode = new FileEncoder(new java.io.File(file.getParentFile(), fitName));
    } catch (FitRuntimeException e) {
      System.err.println("Error opening file " + fitName);
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

    try {
      List<String> lines = FileUtils.readLines(file);
      int lineNb = 0;
      for (String line : lines) {
        if (lineNb == 0) {
          int stepNb = lines.size() - 1;
          encode.write(createWorkoutMsg(line, stepNb));
        } else {
          WorkoutStepMesg workoutStep = createWorkoutStep(line);
          workoutStep.setMessageIndex(lineNb - 2);
          encode.write(workoutStep);
        }

        lineNb++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      encode.close();
    } catch (FitRuntimeException e) {
      System.err.println("Error closing encode.");
      return;
    }

    System.out.println("Encoded FIT file " + fitName);
  }



  private static WorkoutStepMesg createWorkoutStep(String line) {
    WorkoutStepMesg step = new WorkoutStepMesg();
    if (line.startsWith("open")) {
      step.setDurationType(WktStepDuration.OPEN);
      step.setTargetType(WktStepTarget.OPEN);
    } else if (line.startsWith("repeat")) {
      String[] repeatInstruction = line.split(" ");
      step.setRepeatSteps(Long.parseLong(repeatInstruction[1]));
      step.setDurationType(WktStepDuration.REPEAT_UNTIL_STEPS_CMPLT);
      long stepToLoopBackTo = Long.parseLong(repeatInstruction[2]) - 2; //-2 because index start at 0 and there is title in 1st line
      step.setDurationStep(stepToLoopBackTo);
    } else {
      String[] stepInstructions = line.split(" ");
      Unit unit = Unit.getUnit(stepInstructions[1]);
      if (unit == null) {
        throw new UnsupportedOperationException("Unit was not found");
      }
      unit.setValue(stepInstructions[0], step);
      step.setTargetType(WktStepTarget.OPEN);
    }
    return step;
  }

  private static enum Unit {
    MIN("min", WktStepDuration.TIME) {
      @Override
      public Float getValue(String value) {
        return Float.parseFloat(value) * 60;
      }
    },
    SECONDS("seconds", WktStepDuration.TIME),
    METERS("meters", WktStepDuration.DISTANCE);

    private String unitName;
    private WktStepDuration wktStepDuration;

    private Unit(String unitName, WktStepDuration wktStepDuration) {
      this.unitName = unitName;
      this.wktStepDuration = wktStepDuration;
    }

    public void setValue(String value, WorkoutStepMesg step) {
      step.setDurationType(wktStepDuration);
      if (wktStepDuration.equals(WktStepDuration.DISTANCE)) {
        step.setDurationDistance(getValue(value));
      } else if (wktStepDuration.equals(WktStepDuration.TIME)) {
        step.setDurationTime(getValue(value));
      }

    }

    public Float getValue(String value) {
      return Float.parseFloat(value);
    }

    public static Unit getUnit(String unitName) {
      for (Unit unit : values()) {
        if (unit.unitName.equals(unitName)) {
          return unit;
        }
      }
      return null;
    }

  }

  private static WorkoutMesg createWorkoutMsg(String line, int stepNumber) {
    WorkoutMesg workoutMesg = new WorkoutMesgPatched();
    assert WorkoutMesg.workoutMesg.getNumFields() == 5;
    workoutMesg.setWktName(line);
    workoutMesg.setNumValidSteps(stepNumber);
    workoutMesg.setSport(Sport.RUNNING);
    return workoutMesg;
  }

}
