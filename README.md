# Grape Expectations
## Your Guide to Wine
Welcome to _Grape Expectations_ a desktop application which aims "to eliminate
the uncertainty and confusion by providing an organised and cohesive guide to the
complex world of wine tasting" (Finn Brown et al., 2024). This application will
enable you to explore wine data, take notes, rate wines, and keep track of your favourites.

## Authors
- Finn Brown
- Martyn Gascoigne
- Amiele Miguel
- Sean Reitsma
- Caitlin Tam
- Matthew Wills

## Prerequisites
- JDK >= 21 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Gradle [Download](https://gradle.org/releases/) and [Install](https://gradle.org/install/)


## Dependencies
This project requires the following (use gradle to download dependencies).
- JavaFX
- Logging (with Log4J)
- Junit 5
- Mockito (mocking unit tests)
- Cucumber (for acceptance testing)

## Importing Project (Using IntelliJ)
IntelliJ has built-in support for Gradle. To import your project:

- Launch IntelliJ and choose `Open` from the start up window.
- Select the project and click open
- At this point in the bottom right notifications you may be prompted to 'load gradle scripts', If so, click load

**Note:** *If you run into dependency issues when running the app or the Gradle pop up doesn't appear then open the Gradle sidebar and click the Refresh icon.*

## Build Project 
1. Open a command line interface inside the project directory and run `./gradlew jar` to build a .jar file. 
The file is located at target/wino-1.0.jar

## Run App
1. If you haven't already, Build the project.
2. Open a command line interface inside the project directory and run `cd target` to change into the target directory.
3. Run the command `java -jar wino-1.0.jar` to open the application.

## Configuration
In order to select your dataset, make a configuration file name `config.yaml`
in the same directory as the built jar file. A bundled New Zealand dataset will
be used by default if no config file is present.

### Config Example
```yaml
# config.yaml
csvPath: /home/me/files/data.csv
```

## CSV Format
The CSV file used must conform to the schema of the WineMag dataset, which has
header values (note the first blank column):
```
,country,description,designation,points,price,province,region_1,region_2,taster_name,taster_twitter_handle,title,variety,winery
```

## References
Brown, F., Gascoigne, M., Miguel, A., Reitsma, S., Tam, C., & Wills, M. (2023).
_Grape Expectations - Phase II_. SENG202 Team 5.