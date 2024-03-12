# JSL4Android Specs: View Handlers

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

