# JSL4Android Specs: Handlers

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

