# JSL4Android: Generate a JSL local keystore

The JSL 4 Android, by default looks for the `local_ks.jks` file in order to load his
SSL certificate for a secure communication.

To generate a new certificate for your JOSP/Android app, please use the
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

```shell
keytool -genkey -noprompt -keyalg RSA -keysize 2048 -validity 3650 \
-alias {CERT_ALIAS} \
-dname '{DNAME}' \
-keystore {KEYSTORE_PATH} \
-deststoretype pkcs12 \
-storepass '{KEYSTORE_PASS}' -keypass '{KEYSTORE_PASS}'
```

Where the parameters are:
* `{CERT_ALIAS}`: the alias of the certificate
  (e.g. `smart-van-android-LocalCert`)
* `{DNAME}`: the Distinguished Name of the certificate
  where the `CN` must be the same as the `jsl.srv.id` in the `jsl.yml` file
  (e.g. `'CN=smart-van-android,OU=com.robypomper.smartvan,O=John,L=Trento,S=TN,C=IT'`)
* `{KEYSTORE_PATH}`: the path of the keystore file
  (e.g. `./app/src/main/res/raw/local_ks.jks`)
* `{KEYSTORE_PASS}`: the password of the keystore


```shell
keytool -genkey -noprompt -keyalg RSA -keysize 2048 -validity 3650 \
-alias CN=test-android-srv-LocalCert \
-dname 'CN=test-android-srv,OU=com.robypomper,O=John,L=Trento,S=TN,C=IT' \
-keystore ./jsl_android/src/main/res/raw/local_ks.jks \
-deststoretype pkcs12 \
-storepass 'johnny123' -keypass 'johnny123'
```
