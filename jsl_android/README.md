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
