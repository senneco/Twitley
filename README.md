Twitley
=======

Sample application that show how to work with Twitter API v1.1 on Android with Volley library

This application show twits of @AndroidDev (with avatars). Also, load new data when list scrolled to end.


How to run
========

1. Get latest Volley library from [here](https://android.googlesource.com/platform/frameworks/volley).

2. Add Volley library as module of this project.

3. Set Base64 encoded bearer token credentials for BASE_URL field at src/net/senneco/twitley/request/ObtainTokenRequest.java. How to get Base64 encoded bearer token credentials you can see at [twitter api documentation](https://dev.twitter.com/docs/auth/application-only-auth).


Developed By
==========

* Yuri Shmakov - senneco@gmail.com


Other samples
===========
* [Folly](https://github.com/evancharlton/folly)
* [android\_volley\_examples](https://github.com/ogrebgr/android_volley_examples)

License
======

	Copyright 2012 Jake Wharton
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.