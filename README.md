# Groovlet Server

The purpose of this project is to dynamically load and execute Groovy
classes, providing "services" to those classes. The classes are prevented
from import arbitrary libraries via an AST (Abstract Syntax Tree).

The services provided are leveraged via annotations on the Groovy classes.
The services are automatically detected and injected, and the appropriate
imports are automatically added to the Groovy class.

## Features

The "Groovlet" to be invoked is specified in a Kafka message. Both runtime
parameters and configuration parameters are supported, with the configuration
parameters existing in the same Github tree as the Groovlet.

## Services

* Logging to GCL (Google Cloud Logging)

![nav](images/gcl.png)
