package com.garmin.fit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class WorkoutCreator {

  public static void main(String[] args) {
    Options options = new Options();
    options.addOption(OptionBuilder.withLongOpt("help").withDescription("Prints this help").create('h'));
    options.addOption(OptionBuilder.withLongOpt("setup").withDescription("Setup Configuration")
        .hasArg().withArgName("FIT_FILE").create('s'));
    options.addOption(OptionBuilder.withLongOpt("fit").withDescription("Transform to fit file")
        .hasArg().withArgName("WORKOUT_TXT_FILE").create('f'));
    CommandLineParser commandLineParser = new BasicParser();
    try {
      CommandLine commandLine = commandLineParser.parse(options, args);
      if (commandLine.hasOption("fit")) {
        String filename = commandLine.getOptionValue("fit");
        WorkoutToFit.transformToFit(filename);
      } else if (commandLine.hasOption("setup")) {
        String fitFilename = commandLine.getOptionValue("s");
        new ConfCreator(fitFilename);
      } else if (commandLine.getArgs().length == 0 || commandLine.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        String header = "Transform txt format input file to .fit file workout";
        String footer = "created during SonarSource BrainDevDays 2014 by @tomverin and @benzonico";
        formatter.printHelp("Workout Creator 1.0", header, options, footer, true);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}
