---
title: crow-webapp running a local vm-box
layout:
---
#CROW-WebApp

###What's in the box?

  1. GlassFish4-1
  2. JAVA 7 Build env
  3. nginx
  4. uWSGI

###Required Prerequisites

  1. [Virtualbox](https://www.virtualbox.org/)
  2. [Vagrant](https://www.vagrantup.com/)

   **Important:** You'll have to edit the host's /etc/hosts file to resolve crownyc-ubuntu-trusty64 to localhost.

   ```crownyc-ubuntu-trusty64	127.0.0.1```

   ```Vagrantfile``` Vagrant virtualbox config file

###Running Standalone

  1. In the terminal type

  ```
  vagrant up --provider=virtualbox
  ```
  <pre>
  ==> default: Machine booted and ready!
  ==> default: Checking for guest additions in VM...
  ==> default: Mounting shared folders
      default: /vagrant => /Users/marc/Sites/crow-vm-box...
      default: /vagrant_data => /Users/marc/Sites/crow-vm-box/data...
  ==> default: Machine already provisioned. Run `vagrant provision` or use the `--provision`...
  ==> default: to force provisioning. Provisioners marked to run always will still run
  </pre>

  ```
  # SSH into the vm-box

  vagrant ssh

  # Now dump the contents of the following to see config options:

  cat /etc/init/addressparser.conf

  # and this one to view default site info:

  cat /etc/nginx/sites-available/addressparser

  # Switch to the addressparser 

  cd ~/dev/parsers/addressparser

  # Update the addressparser with the latest version

  git pull
  ```

  2. Isolate and test the addressparser

  ```
  pwd --> /home/vagrant/dev/parsers/addressparser

  # Create ENV and ddd the DOITT API_ID and APP_KEY
   
   touch ENV
   sudo vi ENV

    <pre>
    export DOITT_CROL_APP_ID=[Your DOITT ID]
    export DOITT_CROL_APP_KEY=[Your DOITT KEY]
    </pre>

   Check they are indeed available

   cat ENV

    <pre>
    export DOITT_CROL_APP_ID=[*******]
    export DOITT_CROL_APP_KEY=[****************]
    </pre>

   # **Source and activate**

   source ENV
   source env_addressparser/bin/activate

  # Stop the nginx service

   sudo service nginx stop

  # Start up the web server

   python webserver.py
   ```

   **Now from your HOST browser navigate to:** ```http://crownyc-ubuntu-trusty64:5000/api```

### Endpoints:
  1. crol-interface: [http://crownyc-ubuntu-trusty64:8080/crol/v12](http://crownyc-ubuntu-trusty64:8080/crol/v12)
  2. GlassFish Admin:[http://crownyc-ubuntu-trusty64:4848](http://crownyc-ubuntu-trusty64:4848)
  3. Address Parser 
     - JSON Spec [GET]: [http://crownyc-ubuntu-trusty64:5000/spec](http://crownyc-ubuntu-trusty64:5000/spec)
     - Swagger [GET]: [http://crownyc-ubuntu-trusty64:5000/api](http://crownyc-ubuntu-trusty64:5000/api)
     - Parser URL [POST]: [http://crownyc-ubuntu-trusty64:5000/parseaddresses](http://crownyc-ubuntu-trusty64:5000/parseaddresses)

## Some useful commands: 

```
nginx: sudo service nginx (stop | start | restart)
addressparser: sudo (stop | start | restart) addressparser
```

  ```
  # Check that the correct Java version is installed

  java -version
  ```
   <pre>
   java version "1.7.0_79"

   OpenJDK Runtime Environment (IcedTea 2.5.5) (7u79-2.5.5-0ubuntu0.14.04.2)
   OpenJDK 64-Bit Server VM (build 24.79-b02, mixed mode)
   </pre>
