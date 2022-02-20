
generate a keystore.jks (from https://community.pivotal.io/s/article/Generating-a-self-signed-SSL-certificate-using-the-Java-keytool-command?language=en_US)
```shell
keytool -genkey -keyalg RSA -alias tomcat -keystore keystore.jks -validity 3000 -keysize 2048
```

convert to Certificates.p12 (from https://stackoverflow.com/questions/2846828/converting-jks-to-p12)

```shell
keytool \
  -importkeystore \
  -srckeystore keystore.jks \
  -destkeystore Certificates.p12 \
  -srcstoretype JKS \
  -deststoretype PKCS12 \
  -srcstorepass shadow-cljs \
  -deststorepass shadow-cljs \
  -srcalias tomcat \
  -destalias tomcat \
  -srckeypass shadow-cljs \
  -destkeypass shadow-cljs \
  -noprompt
```