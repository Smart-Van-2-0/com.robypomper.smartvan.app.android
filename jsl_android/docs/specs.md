# JSL4Android Specifications

[README](../README.md) | [SPECS](specs.md) | [GUIDES](guides.md) | [CHANGELOG](../CHANGELOG.md) | [TODOs](../TODOs.md) | [LICENCE](../LICENCE.md)

**Summary**

| Spec                                                  | Classes                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|-------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **[Android Application](jsl_application.md)**         | [JSLApplication](../src/main/java/com/robypomper/josp/jsl/android/app/JSLApplication.java), [JSLClient](../src/main/java/com/robypomper/josp/jsl/android/app/JSLClient.java)                                                                                                                                                                                                                                                                                                                                       |
| **[Android Service](jsl_service.md)**                 | [JSLService](../src/main/java/com/robypomper/josp/jsl/android/service/JSLService.java), [JSLServiceAutoStart](../src/main/java/com/robypomper/josp/jsl/android/service/JSLServiceAutoStart.java)                                                                                                                                                                                                                                                                                                                   |
| **[Android Activities](activities.md)** (Inheritable) | [BaseObjectsActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/BaseObjectsActivity.java), [BaseRemoteObjectActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/BaseRemoteObjectActivity.java)                                                                                                                                                                                                                                                                                 |
| **[Android Activities](activities.md)** (ReadyToUse)  | [JSLStartupActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/JSLStartupActivity.java), [JSLSelectObjectActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/JSLSelectObjectActivity.java), [JSLObjectDetailsActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/JSLObjectDetailsActivity.java)                                                                                                                                                              |
| **[Android Fragments](fragments.md)**                 | [EventDetailsBottomSheet](../src/main/java/com/robypomper/josp/jsl/android/components/EventDetailsBottomSheet.java)                                                                                                                                                                                                                                                                                                                                                                                                |
| **[Android Views](views.md)**                         | [JSLBooleanStateView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLBooleanStateView.java), [JSLBooleanActionView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLBooleanActionView.java), [JSLRangeStateView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLRangeStateView.java), [JSLRangeActionView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLRangeActionView.java)                                                                     |
| **[Android Adapters](adapters.md)**                   | [RemoteObjectAdapter](../src/main/java/com/robypomper/josp/jsl/android/adapters/RemoteObjectAdapter.java), [RemoteObjectEventsAdapter](../src/main/java/com/robypomper/josp/jsl/android/adapters/RemoteObjectEventsAdapter.java)                                                                                                                                                                                                                                                                                   |
| **[Handlers](handlers.md)** (RemoteObject)            | [JSLRemoteObjectCommunicationHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLRemoteObjectCommunicationHandler.java)                                                                                                                                                                                                                                                                                                                                                                     |
| **[Handlers](handlers.md)** (Pillars)                 | [JSLBooleanStateHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLBooleanStateHandler.java), [JSLBooleanActionHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLBooleanActionHandler.java), [JSLRangeStateHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLRangeStateHandler.java), [JSLRangeActionHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLRangeActionHandler.java)                                 |
| **[View Handlers](view_handlers.md)** (RemoteObject)  | [JSLRemoteObjectCommunicationViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLRemoteObjectCommunicationViewHandler.java)                                                                                                                                                                                                                                                                                                                                                             |
| **[View Handlers](view_handlers.md)** (Pillars)       | [JSLBooleanStateViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLBooleanStateViewHandler.java), [JSLBooleanActionViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLBooleanActionViewHandler.java), [JSLRangeStateViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLRangeStateViewHandler.java), [JSLRangeActionViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLRangeActionViewHandler.java) |
| **[Implementations](impls.md)**                       | [DiscoverAndroid](../src/main/java/com/robypomper/josp/jsl/android/impls/DiscoverAndroid.java)                                                                                                                                                                                                                                                                                                                                                                                                                     |
| **[Utils](utils.md)**                                 | [ThemeUtils](../src/main/java/com/robypomper/josp/jsl/android/utils/ThemeUtils.java)                                                                                                                                                                                                                                                                                                                                                                                                                               |
| [Library Configs](configs.md)                         | Settings for JSL4Android inclusion into 3rd party Android application.                                                                                                                                                                                                                                                                                                                                                                                                                                             |


## [Android Application](jsl_application.md)

* [JSLApplication](../src/main/java/com/robypomper/josp/jsl/android/app/JSLApplication.java)
* [JSLClient](../src/main/java/com/robypomper/josp/jsl/android/app/JSLClient.java)                                                                                                                                                                                                                                                                                                                                       |

## [Android Service](jsl_service.md)

* [JSLService](../src/main/java/com/robypomper/josp/jsl/android/service/JSLService.java)
* [JSLServiceAutoStart](../src/main/java/com/robypomper/josp/jsl/android/service/JSLServiceAutoStart.java)                                                                                                                                                                                                                                                                                                                   |

## [Android Activities](activities.md)

* (Inheritable)
  * [BaseObjectsActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/BaseObjectsActivity.java)
  * [BaseRemoteObjectActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/BaseRemoteObjectActivity.java)                                                                                                                                                                                                                                                                                 |
* (ReadyToUse)
  * [JSLStartupActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/JSLStartupActivity.java)
  * [JSLSelectObjectActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/JSLSelectObjectActivity.java)
  * [JSLObjectDetailsActivity](../src/main/java/com/robypomper/josp/jsl/android/activities/JSLObjectDetailsActivity.java)                                                                                                                                                              |

## [Android Fragments](fragments.md)

* [EventDetailsBottomSheet](../src/main/java/com/robypomper/josp/jsl/android/components/EventDetailsBottomSheet.java)                                                                                                                                                                                                                                                                                                                                                                                                |

## [Android Views](views.md)

* [JSLBooleanStateView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLBooleanStateView.java)
* [JSLBooleanActionView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLBooleanActionView.java)
* [JSLRangeStateView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLRangeStateView.java)
* [JSLRangeActionView](../src/main/java/com/robypomper/josp/jsl/android/components/JSLRangeActionView.java)  

## [Android Adapters](adapters.md)

* [RemoteObjectAdapter](../src/main/java/com/robypomper/josp/jsl/android/adapters/RemoteObjectAdapter.java),
* [RemoteObjectEventsAdapter](../src/main/java/com/robypomper/josp/jsl/android/adapters/RemoteObjectEventsAdapter.java)

## [Handlers](handlers.md)

* RemoteObject
  * [JSLRemoteObjectCommunicationHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLRemoteObjectCommunicationHandler.java)                                                                                                                                                                                                                                                                                                                                                                     |
* Pillars
  * [JSLBooleanStateHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLBooleanStateHandler.java)
  * [JSLBooleanActionHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLBooleanActionHandler.java)
  * [JSLRangeStateHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLRangeStateHandler.java)
  * [JSLRangeActionHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/base/JSLRangeActionHandler.java)     

## [View Handlers](view_handlers.md)

* RemoteObject
  * [JSLRemoteObjectCommunicationViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLRemoteObjectCommunicationViewHandler.java)
* Pillars
  * [JSLBooleanStateViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLBooleanStateViewHandler.java)
  * [JSLBooleanActionViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLBooleanActionViewHandler.java)
  * [JSLRangeStateViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLRangeStateViewHandler.java)
  * [JSLRangeActionViewHandler](../src/main/java/com/robypomper/josp/jsl/android/handlers/view/JSLRangeActionViewHandler.java)

## [Implementations](impls.md)

* [DiscoverAndroid](../src/main/java/com/robypomper/josp/jsl/android/impls/DiscoverAndroid.java)

## [Utils](utils.md)

* [ThemeUtils](../src/main/java/com/robypomper/josp/jsl/android/utils/ThemeUtils.java)                                                                                                                                                                                                                                                                                                                                                                                                                               |

## [Library Configs](configs.md)

Settings for JSL4Android inclusion into 3rd party Android application.
