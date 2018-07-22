mvn release:clean release:prepare release:perform

cp ./build.properties ./target/checkout
cp ./tst-keys ./target/checkout
cd ./target/checkout
ant release-make release-deploy
cd $CURDIR
