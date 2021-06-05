# E2E Sandbox with Test-containers

Integration tests can be cumbersome the mayority of the times, we need to wire up a lot of services(cache servers, database engines, message brokers etc) resulting in a big ball of stuff to do in order to run a simple integration test.

These tests are the most expensive ones in the [test pyramid](https://martinfowler.com/articles/practical-test-pyramid.html#:~:text=The%20%22Test%20Pyramid%22%20is%20a,put%20it%20into%20practice%20properly.), but are the ones closer to a real World scenario. 

While unit tests ensures our business logic is ok, integration tests ensures that our platform will perform accordingly with no surprises.

In this article we are going to put hands up with testcontainers to create an e2e sandbox and run some **postman assertions** through **newman**.

# What is TestContainers?

Tescontainers is a java library to run containers in JUnit tests, it provides lightweight containers environment.

While the intended use of testcontainers is to run containers in the context of a complex Junit test, we will use it to create a sandbox environment outside junit environment.


# Tools Needed

The following tools are needed to run this example:

* Java (I am using openjdk 11.0.11 2021-04-20)
* Docker (I am using Docker version 20.10.7, build f0df350)
* Newman (I am using 5.2.3)
* Postman(I am using v7.36.5)
* Postman collection(Attached in the repo)
* [This repo :)]()

# Our Microservice

Our microservice is a continuation of my previous post on database migration, basically we are working on a really simple book api.


However as you can see there are several infraestructure services to provide a fully featured microservice:

1. Google Pub/sub to publish Events regarding to Book and author registry

2. Google Cloud Storage: To upload book covers


3. RDBMS: A postgresql instance to store books and author data.

Each of these  services  need to be  part of our sandbox environment

# Creating The Spring Profile

To create the sadbox environment we will take advantage of the spring profiles and conditional beans, to achieve that, the following steps are required:


## Configure testcontainers dependencies

```groovy
	implementation "org.testcontainers:gcloud:1.15.3"
	runtimeOnly 'org.testcontainers:postgresql'
```

## Create Profile application.yaml

```
+--resources
|   +--db
|   +--application-default.yaml
|   +--application-test.yaml #Sandbox configuration file
```

```yaml
server:
  port: 8080
  profiles: test # Establish the profile name
spring:
  liquibase: #database schema auto install
    enabled: true
    change-log: "classpath:/db/changelog.yaml"
  datasource:
    url: "jdbc:tc:postgresql:11.7-alpine:///book_demo" # Testcontainers jdbc driver uri
    driverClassName: "org.testcontainers.jdbc.ContainerDatabaseDriver"
  cloud:
    gcp:
      project-id: "test-containers"
      pubsub:
        emulator-host: "${EMULATOR_HOST:127.0.0.1:8085}" # Emulator host for  pubsub
      credentials: #Fake base64 encoded service account
        encoded-key: "ewogICJ0eXBlIjogInNlcnZpY2VfYWNjb3VudCIsCiAgInByb2plY3RfaWQiOiAidGVzdC1jb250YWluZXJzIiwKICAicHJpdmF0ZV9rZXlfaWQiOiAiOTk5OTk5OTk5OTk5OTk5OTk5OTk5OTk5OTkiLAogICJwcml2YXRlX2tleSI6ICItLS0tLUJFR0lOIFBSSVZBVEUgS0VZLS0tLS1cbk1JSUV2d0lCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQktrd2dnU2xBZ0VBQW9JQkFRRFVOZ3NyUWx5T0JpSUtcbkhGZ3JTQVhrdkVKbmgzalRZU01ZR0FETnNWMHNhU1h3eEdrM3p1WTlsUnhlZ2RtcHlZUWx0MFpMVFZnbHV3SFNcblR1NXhMVWZFVjkxM3VNNk15bzBscmR5V0prZXBLQTRoZUpWT1phTDJhTk14ZTk3dEFQOUw5emxSeEhvV2RoQ0pcbndFS3FhRTk0RmdJT0M1OHp2N0xqZTJ6UVg3dVo1bUozWml1V1RqZXBGNVFIN2RiOUh5TGlhcnV6VkJiVHFBb0VcblBXclUvSDZCRlY1NDlGMlkvM2NhZlpnQ0Y4NC90b3kvVnR2Z0o4RGg4YmduVFAvWENiaGErS3hTNlZINGRlQ0hcbmRsRGhCY1Z4SGY3dllmTTVBNS96dEpHY1hkZFdrSktmVXRQUUozWEw4TGMvYThFam1oak1OeG1TVkVsalFMR0xcbnlOYWFveG54QWdNQkFBRUNnZ0VBUVBnMHppODRoL0RmdmdtMGdyZS91aXBiQ0RoRktTNGRvUFJFVnJVOUNlbzlcblIrdlNMTmhtekNiWk15UDlJRXJHYndlZitWRDZNajhCVFVLR0pOZFFtQSt3aWNWbVUxdDJBRG10QmJrOWlrL3Rcbmw1akQyV2NyajlaREJtemVzTi96eVcyc3VsaDFhbWdHSXk1ZUdxN3AyYUdmU202TGRMdjFpRWpTNVM3U0VFa1ZcbjhYWGxKREFKYi9WTUs3NlhjWm5VSkZ6WEo2ZHBxR3FydTFqSi9lUVBzRGxGckdMeS9zNW1HL20vQVJjTW52MHpcbmlvS2hramtTS05jZGpFS29mMU5ISWZsWkpuUUdGRjF6blRYdlFwZkZWdjNtVi9iUy8ySkZhbE9pemVWYkFIckRcbkdLeTJVNzh2c081d1NuQkF1YUpFYWJZdE5yRmRWRTF4TjN3akVUNUVVd0tCZ1FEN0pLb3NyQUtuN01jcHdZOEZcbkp5THpXbS9SWENXWDdrMHNzMlU5OUxsTkNaNUtTZXdndnNLOTFxTkxIOTBnRlg3T1Z6Uittckx4QngvcFJEY0hcbkxqSmJFVDZ6VDJIRGhrQ1FVTmNZR1ZTRUdFeVpnMWtGSXZ6c1R4Y1VnSDhaZmcvOFJacFcrUlVwUWIvR3pOMFZcbjRPZXM0SEwxRTJvOG9PZjh0V0lpaUZ6QTV3S0JnUURZVUtNNUlGMXFkNUVCVTQ5SUlpMXdLS0NJRFlSOGd4N09cbmcrcjRuUXhVUTBhTHBKQ3VzS253eDhtWmFESk5LKytqQVNMZUJkTjZUU2t3UnVidXVodGJXN0tTTEhCRlp6SnNcbmVOOG1nSzBkcWRpcnV6MWM3cXNHaXR4YzEyRG0vci85MEJTaHp6WThMblFQakh1VVpFamtlc2tQNHEvL0xOdytcbldOTDBpWG43WndLQmdRQ2JzSnNlbm5QM0RqNVprSGpOUlBuUnl4ck91MFZDN3FSQk5lVnBoekRvcFRIUDlBdkNcbk1RQWhGOUtiVytHRlprMzZOTzJDSHYwWGxzY2RvUUJNSDBOd1dUeDVoSmlpMGJOc3cvRFVLNy9OTkJNb2g5akRcbjZuREpQaTZjc3h3WC9hS0RUZExrWC8zU1djUTl4ZnE0K1hnbUgrNmtKNmtRZE4vY05jWGtpb3lWdVFLQmdRRFVcbk5LY3JUNWpVN1Y4UFVnM3AwYjJKbmhGOTV1VGwwU3ZUcEk1S1BxYnRzdUh0OUE4TGtMdm1QZGlENmpnT2hOK2RcbnBXdXpLenYvYXRyUlJYMGZET3Z4Zjg5Nm5xVzFNRHZETmdDVUlQK3piZi9rMk5hbDZHMVhDYnFNU2E0Q2JqK2lcbm14TGllZ2pXbVN1NlpUS0dyS3JsbVo3Tk9yRTFQNmtBY05yaWtrdGNXUUtCZ1FEVDU1dFJnYlErTTk5MW9obGFcbklBNkY5U2RMbThGd3RVcUtQTy96TGIzSUlRYWdVZ1duaFFhcWlVNEUzL2dBeGxMUHlyZ3h0ZGZZd1ZBMENUT3JcbkUrenl5L2lGZTV4amhlS3FYVmRSWTIwa3pKeUMvbUVza1U1bk1zYXVYbmpsS2kyQjNhdkgzSWh5cVlsbEI4Rk5cblJWQTN2T01lamxoQUtYZHcrdytyeHp5OW9BPT1cbi0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS1cbiIsCiAgImNsaWVudF9lbWFpbCI6ICJ0ZXN0LWNvbnRhaW5lcnNAdGVzdC1jb250YWluZXJzLmlhbS5nc2VydmljZWFjY291bnQuY29tIiwKICAiY2xpZW50X2lkIjogIjk5OTk5OTk5OTk5OTk5OSIsCiAgImF1dGhfdXJpIjogImh0dHBzOi8vYWNjb3VudHMuZ29vZ2xlLmNvbS9vL29hdXRoMi9hdXRoIiwKICAidG9rZW5fdXJpIjogImh0dHBzOi8vb2F1dGgyLmdvb2dsZWFwaXMuY29tL3Rva2VuIiwKICAiYXV0aF9wcm92aWRlcl94NTA5X2NlcnRfdXJsIjogImh0dHBzOi8vd3d3Lmdvb2dsZWFwaXMuY29tL29hdXRoMi92MS9jZXJ0cyIsCiAgImNsaWVudF94NTA5X2NlcnRfdXJsIjogImh0dHBzOi8vd3d3Lmdvb2dsZWFwaXMuY29tL3JvYm90L3YxL21ldGFkYXRhL3g1MDkvdGVzdC1jb250YWluZXJzJTQwdGVzdC1jb250YWluZXJzLmlhbS5nc2VydmljZWFjY291bnQuY29tIgp9"


```


## Configuring Sandbox beans


# Running the application

export SPRING_PROFILES_ACTIVE=test
./gradlew bootRun

# Reporting with Newman

# What's the point of e2e testing?

You may ask why bother creating a whole emulated infraestructure just for testing? when running microservice we are supposed to be decoupled to a certain degree

# Caveats and Conclusions

* The idea of a sandbox environment is to run stuff as isolated and reproducible as posible to provide better feedback on how things work on the intended infraestructure.

* A sandbox is also nice for developers who does not want to have a lot of containers running all the time to test a microservice.

* If you are going to use a sandbox environment for your microservice architecture, consider isolating a dependency just for it with the bean configuration boilerplate(and of course adapted to your stack :D).

* Why not use JUNIT instead: As I said before, testcontainers was designed to run smoothly in junit environments, is it's natural behavior, however when you are working on a team with quality Assurance professionals, they are more familiar with tools like postman, newman, gatling etc. They are not interested on creating java code to assert basic e2e behavior.

* Wiring a successful sandbox can be difficult, and a little bit tricky.