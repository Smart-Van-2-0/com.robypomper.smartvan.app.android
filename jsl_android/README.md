# JOSP Service Library 4 Android

Into this module are contained all sources for the JSL4Android library from the
[John O.S. Project](https://www.johnosproject.com)

**NB:** In future, the JSL4Android library will be published as a separate
library, and the Smart Van for Android Mobile App will use it as a dependency.

**Artifact Name:** jospJSL4Android<br />
**Artifact Group:** com.robypomper.josp<br />
**Artifact Version:** 2.2.4-DEV

[README](README.md) | [SPECS](docs/specs.md) | [GUIDES](docs/guides.md) | [CHANGELOG](CHANGELOG.md) | [TODOs](TODOs.md) | [LICENCE](LICENCE.md)

The JOSP Service Library 4 Android is a simple software library that can be included into
3rd party Android applications and libraries and provide easy access to the JOSP EcoSystem.<br/>
The library's main component is the `JSLService` that provides a background
service (24/7) that can maintain a connection with a JOSP Eco System and
interact with it. In order to support Android developers, the library provides
also the `JSLApplication` class that can be used as base class for the main
application class and handle the `JSLService` lifecycle.
Moreover, the library provides a set of base classes (Views, Handlers, Observers...)
that can be inherited and customized to use JSL entities like (Remote Objects,
Structures, Components, Permissions...).


## Run

This is a Java library for Android and developers can include it into their own
applications following the [JSL4Android Getting started](docs/jsl4android_getting_started.md)
guide.

**NB:** Actually the JSL4Android library is developed and hosted into the
[Smart Van for Android Mobile App](https://github.com/Smart-Van-2-0/com.robypomper.smartvan.app.android)
repository. In future, the JSL4Android library will be published as a separate
library, and then used as a dependency.


## Develop

### Organization and conventions

This project is based on the Android Gradle plugin version 8.2 and the Gradle
build system and then include his 8.6 wrapper.

For a complete list of library's components checkout the [JSL4Android Specs](docs/specs.md)
page.


## Resources

### Example: Smart Van Mobile App

The [Smart Van](https://smartvan.johnosproject.org) is a project that aims to
provide a set of tools to monitor and control the Smart Van Boxes. The Smart Van
Mobile App is the main application that users interact with. It uses the
`JSL4Android` module to provide its functionality.

### Dependencies

This module depends on following libraries and artifacts:

* [org.slf4j:slf4j-api](https://www.slf4j.org/) - [MIT](https://www.slf4j.org/license.html)
* [uk.uuid.slf4j:slf4j-android](https://github.com/nomis/slf4j-android) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* [androidx.multidex:multidex](https://developer.android.com/jetpack/androidx/releases/multidex) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* [com.google.android.material:material](https://developer.android.com/reference/com/google/android/material/packages) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

### Publication repository

Actually the JSL4Android library is developed and hosted into the
[Smart Van for Android Mobile App](https://github.com/Smart-Van-2-0/com.robypomper.smartvan.app.android)
repository, so it's not published as a separate library. In future, the
JSL4Android library will be moved on his own repository and published as a
separate library.


## Versions

The JSL4Android has been introduced with the JOSP EcoSystem 2.2.4, then his first
version will be the 2.2.4.
Actually, because it's developed and hosted into the Smart Van for Android Mobile
App repository, the JSL4Android library has a special versioning that is
`2.2.4-DEV (release SV: 1.0.0)`. This version is based on the JOSP EcoSystem
2.2.4 and it's the first release dedicated to the Smart Van for Android Mobile App.

**Older version of JOSP source code:**

Previous versions are hosted on [com.robypomper.josp]() Git repository.

* v [2.2.3](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.2.3/)
* v [2.2.2](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.2.2/)
* v [2.2.1](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.2.1/)
* v [2.2.0](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.2.0/)
* v [2.1.0](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.1.0/)
* v [2.0.0](https://bitbucket.org/johnosproject_shared/com.robypomper.josp/src/2.0.0/)


## Licences

The JSL4Android library contained in the current repository is distributed using the
[Apache v2.0](LICENCE.md) licence.


## Collaborate

**Any kind of collaboration is welcome!** This is an Open Source project, so we
are happy to share our experience with other developers, makers and users. Bug
reporting, extension development, documentation and guides etc... are activities
where anybody can help to improve this project.

One of the John O.S. Project’s goals is to release more John Objects Utils & Apps
to allow connecting even more connected objects from other standards and protocols.
Checkout the Utils & Apps extensions list and start collaborating with a development
team or create your own extension.

At the same time we are always looking for new use cases and demos. So, whether
you have just an idea or are already implementing your IoT solution, don't
hesitate to contact us. We will be happy to discuss with you about technical
decisions and help build your solution with John’s component.

Please email [tech@johnosproject.com](mailto:tech@johnosproject.com).
