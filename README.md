A general purpose admin center for CMS-like website. Built on my fork of PlayFramework1 and AngularJS.

How to try it
-------------

1. `git clone git@github.com:freewind/replay.git`, add the `replay.bat` to your path
2. Install postgres 9.x, and create a database named `wind_base`
3. run `replay deps --sync`
4. run `replay run`

It will automatically create the tables and init data, and you can visit it from:
http://localhost:9000, and the default username/password is admin/admin.

This is a demo
--------------

This is just a demo of how to use AngularJS and PlayFramework.
It's a single-page application with great AngularJS, which gets json data from server side via AJAX.
The play framework just to provide json data.

It's not provide REST api in this demo, since I think which is very useful for this demo.
Instead, I generate a JsRoutes.js which contains the action entries for the client to invoke.
It may be improved in some way in the future.

Online demo
-----------

TODO
