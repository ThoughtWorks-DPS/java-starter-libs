# Error Handling Library
Simple library that utilizes @ControllerAdvise to handle exceptions thrown in your application.

## Installation
Error handling library is currently stored in [maven repository](s3://maven.k8starter.twdps.io).
You'll need to have the proper AWS keys injected in the CircleCI server, or build the libraries locally for local development.

Once you include the repository as a repository in your project, you can add the library as a dependency, as shown below:

1. TWDPS S3 repo as a repository in your `build.gradle` file
```
    repositories {
        maven {
            url "s3://maven.k8starter.twdps.io/releases"
            authentication {
                awsIm(AwsImAuthentication) // load from EC2 role or env var
            }
        }
        mavenCentral()
    }

```
* Add logger library to a list of dependencies in `build.gradle` file.
```
    dependencies {
        compile('io.twdps.starter.errors:error-handling:0.0.1-RELEASE')
    }
```

## Usage
The error-handling library utilizes @ControllerAdvise to intercept exceptions and transform them into API error responses.

## IMPORTANT
This library works with the exceptions defined in [exceptions](https://gitserver.xtonet.com/SMKT-CST/libraries/exceptions). Have your own
exceptions extend these common exceptions in order to use this library. If you need specific error handling for custom exceptions that don't
extend these common exceptions you need to write another class that uses @ControllerAdvise.

## Mapping exceptions to API responses
This library has intercept exceptions that are thrown, and use the message to look for code|message in a properties file
in your codebase. So, in order to successfully implement this library you need to have a properties file in
your application properties that defines different code and messages for different types of violations(example below):

```properties
customerLengthViolation=length_violation|The customer number can be 10 characters long.
emptyValue=empty_value|The value for this field needs to be present.
digitValueRequired=digit_value_required|The value for this field needs to be a digit.
internalErrorMessage=SYSTEM_ERROR|The server encountered an unexpected condition that prevented it from fulfilling the request.
```

Also the library expects a defined bean that it uses to read these properties:

```java
@Configuration
public class ErrorMessages {

  @Bean
  public PropertiesFactoryBean errorMessageMap() throws IOException {
    PropertiesFactoryBean bean = new PropertiesFactoryBean();
    bean.setLocation(new ClassPathResource("errors.properties"));
    bean.afterPropertiesSet();
    return bean;
  }
}
```
