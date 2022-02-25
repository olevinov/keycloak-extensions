Tarantool HTTP-server for testing spi-event-listening

1. Installation

Tarantool is already installed in sandbox.

Install http module for Tarantool:

sudo yum install tarantool-http

2. Starting Tarantool

tarantool http.lua

3. Testing

3.1. Testing with curl

To test with curl just execute:

curl -X POST --data "email=test@company.com" localhost:8089

You will receive:

{"status":"Success!"}

and you will see in Tarantool output

handler
email = test@company.com

3.2. Testing with spi-event-listener

To test with spi-event-listener change user data via admin console.

As the result you have to receive user email in Tarantool (see the output).
