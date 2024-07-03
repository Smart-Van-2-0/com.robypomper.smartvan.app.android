# SV Mobile App 4 Android Changelog

[README](README.md) | [CHANGELOG](CHANGELOG.md) | [TODOs](TODOs.md) | [LICENCE](LICENCE.md)

## 1.0.0

* SVApplication inherits from JSLApplication (and SVJSLClient from JSLClient)
* SVService inherits from JSLService (and SVServiceAutoStart from JSLServiceAutoStart)
* Defined SV Specs within the SVSpecs class (SVSpec and SVSpecGroup)
* Added following activities:
  * Init process: SVStartupActivity, SVSelectObjectActivity
  * Main activities: SVMainActivity, SVEnergyActivity, SVServicesActivity
  * Object's Activities: SVObjectSpecsActivity, SVObjectDetailsActivity
  * Standard activities: SVAboutActivity, SVFeedbackActivity, SVSettingsActivity
* Added following views:
  * SV Specs: SVSpecsListAdapter, SVSpecView
  * JSL Components: SVBinaryControllerView, SVSwitchActuatorView, SVPercentControllerView, SVDimmerActuatorView
  * SV Services: SVServiceBottomSheet, SVServiceEditSimpleView
  * Settings: SVSettingsMainFragment, SVSettingsObjectFragment
  * Various: SVBoxIconView, SVDonutView
* Storage solutions based on SV Box ids
  * SVStorage main class and SVStorageSingleton as entry point
  * SVPreferences and SVPreferencesServices as SubStorages components
  * Local implementation of storage solution
* Utils
  * DataStoreUtils: utils class for androidx.datastore.rxjava3.RxDataStore
* Commons
    * SVDefinitions
    * SVServiceIcons: icon set for SVServiceEditSimpleView

## 0.1.0

* Created SVApplication as JSLApplication sub-class
* Created SVService as JSLService sub-class
* Created SVStartupActivity as JSLStartupActivity sub-class
* Created SVSelectObjectActivity to select a valid "JOD Smart Van" object
* Added "JOD Smart Van" component's definitions
* Implemented basic SVMainActivity to show simple data from the remote object


## 0.0.1

* Initialized from Android Studio project
* Applied some Smart van customization to the project
* Updated the `ic_luncher` resource with Smart Van logo
* Added a simple SVMainActivity
