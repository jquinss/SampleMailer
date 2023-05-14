# SampleMailer

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Features](#features)
* [Status](#status)

## General info
This a SMTP client application created in JavaFX. The application is mainly intended to be used for testing purposes (e.g. testing of email security technologies).

## Technologies
* Java version: 17.0.6
* JavaFX version: 17.0.2
* jakarta.mail version: 2.1.1
* dnsjava version: 3.3.1

## Setup
No special instructions are required to use the application. You can export the project as a runnable Jar file and execute it.
Alternatively, you can directly use the samplemailer.jar located at the out/artifacts/samplemailer_jar directory. 

## Features
* Users can send emails by customising different fields (From, Mail from, To, CC, BCC, Subject, Body).
* Users can send attachments.
* Users can create email templates so they can be saved and retrieved at a later time.
* Users can select the number of emails to be sent at once and also modify the delay between each email.
* Users can add custom headers to the email.
* Users can schedule the email delivery so the emails can be sent at a later date/time.
* Users can enable/disable TLS when sending emails.
* Users can customise different parameters like for example, the TLS version used to send the email, the codification of the email, enable debugging, etc.
* Users can see the status of the emails sent by using a log console.
* Users can create emails using a HTML Editor.
* Users can add custom headers from a text file or an existing email.
* Users can send emails based on the resolution of the MX records of the recipient domains.
* Users can add custom Message-IDs.
* Users are able to see additional information in the Status field logs (e.g. sender, recipients and server).
* Users can export the Status field logs.
* Users can exclude specific headers when importing them from a file.
* Add tooltips with information for some fields.
* Users can use SMTP authentication.
* Users can import and export templates.

### To Do

* Add option for adding a DKIM signature.
* Add documentation.

## Status
This project is still in "In Progress" status.