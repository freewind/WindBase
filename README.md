A general purpose admin center for CMS-like website. Built on my fork of PlayFramework1 and AngularJS.

How to try it
-------------

1. `git clone git@github.com:freewind/replay.git`, add it to the path
1. build it with ant: `cd replay/framework; ant`, it will generate play jar.
1. Install postgres 9.x, and create a database named `wind_base`
1. run `replay deps --sync`
1. run `replay run`

It will automatically create the tables and init data, and you can visit it from:
http://localhost:9000, and the default username/password is admin/admin.

This is a demo
--------------

This is just a demo of how to use AngularJS and PlayFramework.
It's a single-page application with great AngularJS, which gets json data from server side via AJAX.
The play framework just to provide json data.

It's not provide REST api in this demo, since I think which is not very useful for this demo.
Instead, I generate a JsRoutes.js which contains the action entries for the client to invoke.
It may be improved in some way in the future.

Why use my fork of Playframework
--------------------------------

Just because I want a `separator` attribute to `list` tag:

```
#{list uesrs, as:'user', separator: ','}
{
  "id" : ${user.id},
  "name: ${user.name}
}
#{/list}
```

Which is very useful to generate json data, but not available in origin play.

So you can just use my fork to try it on you computer, but don't use it in production mode.
My changes have not been tested.

Online demo
-----------

http://shuzu.org:9000

It maybe only available for several days.
