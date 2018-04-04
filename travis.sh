echo "Setup JDK 1.8.0_162"
jdkVersion=1.8.0_162
jdkInstallationFolder=jdk$jdkVersion
jdkBuild=$jdkVersion-b12
jdkArchive=jdk-8u162-linux-x64.tar.gz

mkdir -p ~/jvm
pushd ~/jvm > /dev/null

if [ ! -d "$jdkInstallationFolder" ]; then
	echo "Downloading JDK $jdkVersion"
	wget --quiet --continue --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u162-b12/0da788060d494f5095bf8624735fa2f1/$jdkArchive
	tar xzf $jdkArchive
	rm $jdkArchive
	pushd $jdkInstallationFolder > /dev/null
	rm -f src.zip
	rm -f javafx-src.zip
	rm -f COPYRIGHT
	rm -f LICENSE
	rm -f README.html
	rm -f release
	rm -f THIRDPARTY*
	echo "JDK $jdkVersion installed in `pwd`"
	popd > /dev/null
else
	echo "JDK $jdkVersion already installed in `pwd`/$jdkInstallationFolder"
fi

popd > /dev/null