# JSL4Android Specs: Configs

## Android permissions required:

```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Sf4j logging configs

[config.properties](../src/main/resources/eu/lp0/slf4j/android/config.properties)
level   = WARN

Packages:
- com.robypomper                  J_RP        WARN
- com.robypomper.josp             J_Lib       WARN
- com.robypomper.josp.jsl.android J_Android   INFO
- com.robypomper.josp.jsl.app     J_App       DEBUG
- javax.jmdns.impl                JmDNS       WARN