# Command to generate JSL local keystore

The JSL 4 Android looks for the `local_ks.jks` file in order to load his
SSL certificate for a secure communication.

To generate a new certificate for your JSOP/Android app, please use the
following command:

```shell
file		./new_ks.jks
password	johnny123
cert. id	test-android-srv/00000-00000-00000/9999             <-- it must be always on {srv-id}/{user_id}/{instance_id}. instance_id can be a random number
alias		test-android-srv-LocalCert


keytool -genkey -noprompt -keyalg RSA -keysize 2048 -validity 3650 \
    -alias test-android-srv-LocalCert \
    -dname 'CN=test-android-srv/00000-00000-00000/9999,OU=com.robypomper.comm,O=John,L=Trento,S=TN,C=IT' \
    -keystore ./new_ks.jks \
    -deststoretype pkcs12 \
    -storepass 'johnny123' -keypass 'johnny123'
```




## JSL Android Content

**List:**

- Android Application       JSLApplication, JSLClient
- Android Service           JSLService, JSLServiceAutoStart
- Android Activities
  - Base                    BaseObjectsActivity, BaseRemoteObjectActivity
  - Ready                   JSLStartupActivity, JSLSelectObjectActivity, JSLObjectDetailsActivity
- Android Views
  - [ComponentViews](#componentviews)
  - [ViewHandlers](#viewhandlers)
  - Various                 EventDetailsBottomSheet
- [Handlers](#handlers)
- Implementations
  - DiscoverAndroid
- Utils
  - ThemeUtils

### Handlers

Classes that handle a specific aspect of a specific instance from the JSL library
(such as RemObject, Structure, Components, Permissions...).
Each one of those classes provides an Observer interface to be notified when the
handler detect some changes in the instance.

**Usage:**

Check out the ViewHandlers classes, that use the Handlers to get the current
status and keep it synchronized.

**Implementation:**

- `JSLBaseHandler` is the base class for all the handlers
- `JSLRemoteObectCommunicationHandler` is an basic example of a handler that manage
  the remote object's communication status
- `JSLBaseComponentViewHandler > JSLBaseStateViewHandler > JSLBooleanStateViewHandler`
  is an example of a handler implemented using several levels of inheritance


### ViewHandlers

Classes that handle the view of a specific instance from the JSL library
(such as RemObject, Structure, Components, Permissions...).
Those classes are used to update a given ViewGroup (see `JSLBaseViewHandler::getMainView()`)
according to the current status of the instance and keep it synchronized.
The given ViewGroup must be a custom layout that can be loaded by the
ComponentViews and must contain the views that will be updated by the specific
ViewHandler. Each ViewHandlers class defines his own views' ids into a
placeholder layout file.
 
Those classes use the JSL Handlers to get the current status and keep it
synchronized with the main ViewGroup.

**Usage:**

Check out the ComponentViews classes, that use the Handlers to get the current
status and keep it synchronized.

**Implementation:**

- `JSLBaseViewHandler` is the base class for all the view handlers
- `JSLRemoteObjectCommunicationViewHandler` is an basic example of a view handler that
  manage the remote object's communication status
- `JSLBaseComponentViewHandler > JSLBaseStateViewHandler > JSLBooleanStateViewHandler`
  is an example of a view handler implemented using several levels of inheritance


### ComponentViews

Android views' classes ready to use. Applications can use those classes to
load custom layouts and show interactive (if needed) views.

Each ComponentView class is a ViewGroup that contains a ViewHandler and a
Handler. The ViewHandler is used to update the ViewGroup's views according to
the current status of the Handler. The Handler is used to get the current status
and keep it synchronized with the ViewGroup.

For each ComponentView class, there is a default layout file that can be used
to load the ViewGroup. Otherwise, the custom layout file must contain the views
that will be updated by the specific ViewHandler.

**Usage:**

TODO make an example of ComponentViews instantiation

