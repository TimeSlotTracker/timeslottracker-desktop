TimeSlotTracker (TST) is a program for registering time spent on some work.
The main rule is to provide a flexible way to construct a task tree composed of projects, (sub)projects and simple task inside that projects.
Time is registered in slots, it means if you had worked more then once per day on some task you will have several slots registered, not only summarized time.

Project is founded by Bartlomiej Marciniak (qqbart), then joined Zbigniew Ogledzki (zgibek)
Then we are very pleased that Felicja Suska (fellunia) joined to us!
Then joined Andrey Glazachev (aglazachev).

Main points:
-- ---------------
* Files should be saved in UTF-8 format
* Files should use unix end of line markers
* Files should use spaces (ident = 2)
* Source files should be well documented to generate javadoc
* Maven3 pom
* Ant has useful targets (build, run, ...), to show run "ant -p"

Dependencies:
-- ---------------
* Java 1.8+ (currently does not compile on OpenJdk15 due to libraries removed in from jdk)
* Ant 1.7+
* Maven 3+
* packages: debhelper, ant-optional, ant-contrib, libjsch-java

Setup:
-- ---------------
* Ant requires build.properties file with following params:
** username=<your sf name>, userpass=<your sf password>, keystorepass=<key store password>, keypass=<key password>
* Generate tst-key with gen-key.sh

Main ideas:
-- ---------------
* User can register some task and subtask
* Every task has properties:
  - name
  - description (can be even a large one, maybe in html format in the future)
* Task are realized in time slots:
  - start & finish datetime 
  - description if given
* Time is summarized up in tree up to main branch
* Only one task can be timed in one time. If one task is active and somebody starts another one the previous active should be finished and then the new one started.
* Data should be keep in xml format
* In the future we should use some kind of transaction log
* Time slots can be manually edited to correct (or just enter) values
* Data should be saved in configurable amount of time (for example 5 minutes).
  At the beginning the whole xml file should be regenerated. In the future only transaction log should be written immediately and the xml file should be written every one hour (for example)
* To detect inactive time TT should ask a question every X minutes (for example 15 minutes). If break is detected user have to decided what to do with break time.
* TST should be a localized friendly software, so every message should be got from properties file.


Roadmap:
-- ---------------
* see ChangeLog file
