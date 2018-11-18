## Logger
Simple library that utilizes AOP to enable logging of spring-boot components.


## Installation
Logger library is currently stored in [maven repository](s3://maven.k8starter.twdps.io/).
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

2. Add logger library to a list of dependencies in `build.gradle` file.
```
    dependencies {
        compile('io.twdps.starter.errors:logger:0.0.1-RELEASE')
    }
```

## Usage
The logger library utilizes AOP to log methods and classes.


### Adding debug logs to methods and classes
We can leverage the `@Loggable` annotation to annotate classes and methods. When the `@Loggable` is applied to a class, 
it will add debug logs to all method in the class.
For example;

```
package com.test

@Loggable
public class Heyo {
    public String sayHello(String name) {
        return "Heyo " + name;
    }
}
```

When the `sayHello("DPS")` is called, log output will be;
```
LOG DEBUG Entering method com.test.Hallo.logSuccessFulEvent(Tim) {}
LOG DEBUG Exiting method com.test.Hallo.logSuccessFulEvent; execution time (ms): 20
```
The debug log will print following;
* Class Name
* Method Name
* Arguments passed to the method
* Execution time in milliseconds

### Adding event logs for monitoring in kibana
To facilitate monitoring in kibana, we need to add event logs to controllers and repositories. We can 
 achieve this by leveraging the `@LoggableEvent`.
 
Controller event logging example;
```
    public class CustomerController {
    
    	@GetMapping(value = "/customers/{customerId}")
        @LoggableEvent(applicationTier = ApplicationTier.CONTROLLER, action = "GET_CUSTOMER")
    	public ResponseContainer<Customer> findCustomerByNumber(String customerId) {
    		...
    	}
    	...
    }
```
When a request is made to `/customers/{customerId}` endpoint and the response is successful, log output will be;
```
INFO Successful call: CustomerController.findCustomerByNumber  {action=GET_CUSTOMER, application_tier=CONTROLLER, duration_millis=6, status=Success}
```
If the method throws a error OR the response status code is a 4XX or 5XX then log output will be;
```
INFO Status Code: 500, Error: some error  {action=GET_CUSTOMER, application_tier=CONTROLLER, duration_millis=6, status=Failed}
```

Repository event logging example;
```
public class CustomerRepository {
    
    ...
    
    @LoggableEvent(applicationTier = ApplicationTier.REPOSITORY, action = "GET_CUSTOMER_BY_NUMBER")
    public Optional<Customer> getCustomerByNumber(String number) {
        ...    
    }
    
    ...
}

```
When `getCustomerByNumber` method in customer repository is called and the response is successful, log output will be;
```
INFO Successful call: CustomerRepository.getCustomerByNumber  {action=GET_CUSTOMER_BY_NUMBER, application_tier=REPOSITORY, duration_millis=6, status=Success}
```
If the method throws a error then log output will be;
```
INFO java.lang.Exception: something  {action=GET_CUSTOMER_BY_NUMBER, application_tier=REPOSITORY, duration_millis=6, status=Failed}
```



