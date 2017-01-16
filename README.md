Cucumber Smoothie
======================

![alt text](http://oi63.tinypic.com/zkobjq.jpg "Cucumber smoothie")

Cucumber Smoothie is an alternative to cucumber-jvm that is designed specifically for the Android instrumentation framework. Step definitions are defined in the same way as cucumber-jvm, the annotations are preprocessed to generate a CucumberRunner that contains a series of tests that execute the step definitions.

###Android gradle tools < 2.2.0 (https://bitbucket.org/hvisser/android-apt)###
```groovy
dependencies {
    androidTestCompile 'com.memtrip.cucumber.smoothie:gherkin-binding:1.0.0'
    androidTestApt 'com.memtrip.cucumber.smoothie:gherkin-parser:1.0.0'
}
```

###Android gradle tools 2.2.0+###
```groovy
dependencies {
    androidTestCompile 'com.memtrip.cucumber.smoothie:gherkin-binding:1.0.0'
    androidTestAnnotationProcessor 'com.memtrip.cucumber.smoothie:gherkin-parser:1.0.0'
}
```

###Define a Gherkin feature file###
```
Feature: Cow

    Background:
        Given a cow that was born on 2016-12-01 is called "Nelly Newton"

    Scenario Outline: feeding a suckler cow
        Given the cow weighs <weight> kg
        When we calculate the feeding requirements
        Then the energy should be <energy> MJ
        And the protein should be <protein> kg
        And the cow alive is true
        And the cow initial is 'S'

        Examples:
            | weight  | energy | protein |
            |    4.5  |  265   |     215 |
            |    5.0  |  295   |     245 |
            |    57.5 |  315   |     255 |
            |    6.31 |  370   |     305 |
```

###Bind the Gherkin behaviour to step definitions###
The step definitions are bound in the same way as cucumber-jvm. However, the Feature annotation must supply the name of the projects root folder and the relative path to the feature file. *These values are used internally by the preprocessor to work out the absolute path of the .feature file*

```java
@Feature(
        projectRootFolderName="gherkin-parser",
        featureFilePath="src/test/resources/cow.feature"
)
public class Cow {

    @Background
    public static class cow_setup {

        @Given("a cow that was born on $dob is called $name")
        public void a_cow_that_was_born_on_dob_is_called_name(Date dob, String name) {
            // test setup
        }
    }

    @Scenario("feeding a suckler cow")
    public static class feeding_a_suckler_cow {

        @Given("the cow weighs <weight> kg")
        public void the_cow_weighs_x_kg(double weight) {
            // test code
        }

        @When("we calculate the feeding requirements")
        public void we_calculate_the_feeding_requirements() {
            // test code
        }

        @Then("the energy should be <energy> MJ")
        public void the_energy_should_be_x_MJ(long energy) {
            // test code
        }

        @And("the protein should be <protein> kg")
        public void the_protein_should_be_x_kg(int protein) {
            // test code
        }

        @And("the cow alive is $alive")
        public void the_cow_is_alive(boolean alive) {
            // test code
        }

        @And("the cow initial is $initial")
        public void the_cow_initial_is(char initial) {
            // test code
        }
    }
}
```

###Usage###
The annotation preprocessor will generate a class at `com.memtrip.cucumber.smoothie.CucumberRunner`. To run the step definitions as tests, a child of CucumberRunner must be created and annotated with `@RunWith(AndroidJUnit4.class)`

```
import com.memtrip.cucumber.smoothie.CucumberRunner;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MyCucumberRunner extends CucumberRunner {
    // this will run all the step definitions that are bound to .feature files
}
``` 

###Gherkin reference###
- https://cucumber.io/docs/reference

###TODO###
- Tags
- List arguments
- Match with regular expressions
- Validation: At least one scenario annotation is required per feature annotation
- Validation: At least one behaviour annotation is required per scenario annotation
- Validation: The arguments of the Gherkin pickles match those found in the behaviour annotations