# SampleMailer
 
## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Features](#features)

## General info
This a SMTP client application created in JavaFX. The application is mainly intended to be used for testing purposes (e.g. testing of email security technologies)

## Technologies
* JavaFX Version: 8.0.291
* JavaFX Runtime Version: 8.0.291-b09
* JavaMail 1.4.7

## Setup
No special instructions are required to use the application. You can export the project as a runnable Jar file and execute it.

## Features
* Users can send emails by customizing different fields (From, Mail from, To, CC, BCC, Subject, Body)
* Users can send attachments.
* Users can create email templates so they can be saved and retrieved at a later time.
* Users can select the number of emails to be sent at once and also modify the delay between each email.
* Users can add custom headers to the email.
* Users can schedule the email delivery so the emails can be sent at a later date/time.
* Users can enable/disable TLS when sending emails.
* Users can customise different parameters like for example, the TLS version used to send the email, the codification of the email, enable debugging, etc.
* Users can see the status of the emails sent by using a log console.

### To Do
* Add option to send emails based on the resolution of the MX records of the recipient domains.
* Include HTML Editor.
* Add option for SMTP authentication.
* Add option for adding a DKIM signature.

## Status
This project is still in In Progress status
