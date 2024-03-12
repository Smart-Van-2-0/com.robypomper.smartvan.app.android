# Smart Van Mobile App 4 Android

This module contains the Smart Van Mobile App for Android. It's part of the Smart Van
Project and it's published under an Open Source licence to allow the community to
contribute to the project.

**Dist Name:** Smart Van<br />
**Dist Artifact:** SmartVanAndroid<br />
**Dist Version:** 1.0.0-DEV

[README](README.md) | [CHANGELOG](CHANGELOG.md) | [TODOs](TODOs.md) | [LICENCE](LICENCE.md)

This mobile app allows connect to Smart Van object as defined from
the [Smart Van Project](https://smartvan.johnosproject.org/)
website.

It's based on the 'JSLAndroid' library (actually it's a module from current
repository), that allows access easily to the JOSP Eco System.


## Build and run

Fastest way to try out this mobile app
is [download](https://github.com/Smart-Van-2-0/com.robypomper.smartvan.app.android/releases)
it from his git repository and install it manually on your Android device.
Otherwise you can compile it and run on emulators/physical device as any
other [Android Studio](https://developer.android.com/studio/run)
project.

Once the mobile application is launched for the first time, it will list all
available SV Box devices. Select one to proceed.

If you do not have an SV Box at your disposal, you can always **emulate one
virtual SV Box from your PC**. Use the `$SV_MOBILE_APP/jod_svbox.sh` script to
clone, build and run a virtual SV Box instance on your machine.

```shell
$ cd $SV_MOBILE_APP
$ ./jod_svbox.sh init
$ ./jod_svbox.sh start
$ ./jod_svbox.sh state
...
$ ./jod_svbox.sh stop
```

**Please note!** The SV application can currently only display SV Boxes
connected to the same local network, because User authentication is not yet
implemented.


## Develop

This project is based on the Android Gradle plugin version 8.2 and the Gradle
build system and then include his 8.6 wrapper.

In order to run, test or edit the sources, please open the main dir with the
[Android Studio](https://developer.android.com/studio/projects) IDE, then you
can edit, compile and run those source files.


## Resources

### Dependencies

This module depends on following libraries and artifacts:

* [JSL4Android](../jsl_android/README.md) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) (local)
* [ChartViews](../chart_views/README.md) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) (local)
* [app.futured.donut:donut](https://github.com/futuredapp/donut) - [MIT](https://github.com/futuredapp/donut/blob/master/LICENSE)
* [androidx.datastore:datastore-preferences-rxjava3](https://developer.android.com/jetpack/androidx/releases/datastore) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* [androidx.preference:preference](https://developer.android.com/jetpack/androidx/releases/preference) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* [com.github.kizitonwose.colorpreference:core](https://github.com/kizitonwose/colorpreference) - [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

### Publication repository

The Smart Van for Android Mobile App is published on the
[GitHub - Releases](https://github.com/Smart-Van-2-0/com.robypomper.smartvan.app.android/releases)
page. You can download the latest version from there.


## Versions

The current version is the 1.0.0. See the [CHANGELOG](CHANGELOG.md) page for more
details about the release.


## Licences

The Smart Van for Android Mobile App contained in the current repository is
distributed using the [Apache v2.0](LICENCE.md) licence.


## Collaborate

This project is part of the [Smart Van Project](https://smartvan.johnosproject.org),
and it's published under an Open Source licence to allow the community to
contribute to the project.

If you want to contribute to the project, you can start by reading the
[Contribution Guidelines](https://smartvan.johnosproject.org/collaborate) page.
Otherwise, you can clone current repository and start to customize your own
JOD Smart Van distribution. Check out the project's structure and 'how to work
on it', on the [Development](/docs/development.md) page.
