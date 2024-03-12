# JSL4Android Changelog

[README](README.md) | [SPECS](docs/specs.md) | [GUIDES](docs/guides.md) | [CHANGELOG](CHANGELOG.md) | [TODOs](TODOs.md) | [LICENCE](LICENCE.md)

## 2.2.4-DEV

* Added JSL4Android module to SmartVan4Android project
* Support for slf4j logging and Android multidex
* Added JSL 2.2.4-DEV library as Gradle dependency
* Added JSLService as Android service to handle the JSL instance into a background thread
* Added JSLApplication as Android application to handle the JSLClient as a global object
* Added JSLServiceAutoStart as android BroadcastReceiver for "android.intent.action.BOOT_COMPLETED"
* Added JSLStartupActivity, JSLSelectObjectActivity Activities and all their resources
* Added JSLObjectDetailsActivity Object's Activities and all their resources
* Added BaseJSLActivities as helper class for any activity that show remote object(s)
* Added JSLObjectAdapter as RecyclerView adapter to show Remote Objects
* Added JSLBooleanActionView, JSLBooleanStateView, JSLRangeActionView and JSLRangeStateView as custom views to show Remote Object's states and actions
* Created repository documentation README.md, TODOs.md, CHANGELOG.md and LICENCE.md
