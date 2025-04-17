# Email

## About

A library to send an email and create template that wraps [SimpleJavaMail]( http://www.simplejavamail.org/)


## Jakarta

Dependencies: Jakarta the new mail api needs Angus SMTPTransport
ie `com.sun.mail.smtp.SMTPTransport` is now `org.eclipse.angus.mail.smtp.SMTPTransport`
See https://eclipse-ee4j.github.io/angus-mail/

Jakarta Mail is only a test runtime dependency (ie gradle api) and is not added in the path
because every web framework has its own email module (ie vertx has a non-blocking email module for instance)


## Template

There is a collection of template in the resource directory.


# Test

We are using [Wiser](https://github.com/davidmoten/subethasmtp/tree/master)

It starts a fake SMTP server and let us get the email back.

At the same time, the test will also send an email to a local SMTP port server with GUI. The port is 25 which
is the default of [Papercut](https://github.com/ChangemakerStudios/Papercut-SMTP/releases)

For more information on email test, see [How to test email](https://datacadamia.com/marketing/email/test)


## Note
### Other Library

  * https://commons.apache.org/proper/commons-email/
