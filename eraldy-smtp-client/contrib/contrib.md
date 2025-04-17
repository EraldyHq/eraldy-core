# Contrib

This library started before discovering [SimpleJavaMail]( http://www.simplejavamail.org/)

The goal is to wrap [SimpleJavaMail]( http://www.simplejavamail.org/).


## Note
### Lib Jakarta


Jakarta replaces the old api.

Dependencies: Jakarta the new mail api needs Angus SMTPTransport
ie `com.sun.mail.smtp.SMTPTransport` is now `org.eclipse.angus.mail.smtp.SMTPTransport`
See https://eclipse-ee4j.github.io/angus-mail/



### Template

There is a collection of template in the resource directory.


### Test

We are using [Wiser](https://github.com/davidmoten/subethasmtp/tree/master)

It starts a fake SMTP server and let us get the email back.

At the same time, the test will also email a local SMTP port server with GUI. The port is 25 which
is the default of

For more information on email test, see [How to test email](https://datacadamia.com/marketing/email/test)

## MailPit

You can configure a mailpit address (ie a server that takes all email)
with the following env variables (for your IDE)
```bash
MAILPIT_SMTP_HOST=xx
# default to 25
MAILPIT_SMTP_PORT=xx
MAILPIT_SMTP_USER=xx
MAILPIT_SMTP_PASSWORD=xx
# TLS is true by default if PORT=465
MAILPIT_SMTP_TLS=true
```

Example:
* [MailPit](https://mailpit.axllent.org/)
* [Papercut](https://github.com/ChangemakerStudios/Papercut-SMTP/releases)

## Note
### Other Library

* https://commons.apache.org/proper/commons-email/
