# What's in the box?

  1. GlassFish4-1
  2. JAVA 7 Build env
  3. nginx
  4. uWSGI

# Required Prerequisites

  1. [Virtualbox](https://www.virtualbox.org/)
  2. [Vagrant](https://www.vagrantup.com/)

**Important:** You'll have to edit the host's /etc/hosts to resolve crownyc-ubuntu-trusty64 to 127.0.0.1

crownyc-ubuntu-trusty64	127.0.0.1

  1. Vagrantfile

  2. Boot the virtual box

`vagrant@crownyc-ubuntu-trusty64:$ vagrant up --provider=virtualbox

==> default: Machine booted and ready!
==> default: Checking for guest additions in VM...
==> default: Mounting shared folders...
    default: /vagrant => /Users/marc/Sites/crow-vm-box
    default: /vagrant_data => /Users/marc/Sites/crow-vm-box/data
==> default: Machine already provisioned. Run `vagrant provision` or use the `--provision`
==> default: to force provisioning. Provisioners marked to run always will still run.`

  3. SSH into the vm-bpx

`vagrant@crownyc-ubuntu-trusty64:$ vagrant ssh`

Dump the contents of the following to see config options:

`cat /etc/init/addressparser.conf 
cat /etc/nginx/sites-available/addressparser`

  4. Switch to the addresparser 

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ cd ~/dev/parsers/addressparser`

  5. Update the addressparser with the latest version

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ git pull`

  6. Check that the correct Java version is installed

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ java -version
                                                                                       
java version "1.7.0_79"
OpenJDK Runtime Environment (IcedTea 2.5.5) (7u79-2.5.5-0ubuntu0.14.04.2)
OpenJDK 64-Bit Server VM (build 24.79-b02, mixed mode)`

  7. Isolate and test the addressparser

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ pwd 

/home/vagrant/dev/parsers/addressparser`

  8. Create ENV and paste in DOITT API_ID and APP_KEY

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ touch ENV`

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ sudo vi ENV 

`export DOITT_CROL_APP_ID=**************
export DOITT_CROL_APP_KEY=*******************`

Check they are indeed availabe

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ cat ENV
export DOITT_CROL_APP_ID=**************
export DOITT_CROL_APP_KEY=*******************`

Source the file and activate

`vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ source ENV

vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ source env_addressparser/bin/activate`

  9. Stop the nginx service

`(env_addressparser)vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ sudo service nginx stop`

  10. Start the web server

`(env_addressparser)vagrant@crownyc-ubuntu-trusty64:~/dev/parsers/addressparser$ python webserver.py`

Then from HOST browser navigate to: http://crownyc-ubuntu-trusty64:5000/api

# Endpoints 
  1. crol-interface: [http://crownyc-ubuntu-trusty64:8080/crol/v12](http://crownyc-ubuntu-trusty64:8080/crol/v12)
  2. GlassFish Admin:[http://crownyc-ubuntu-trusty64:4848](http://crownyc-ubuntu-trusty64:4848)
  3. Address Parser 
     - JSON Spec [GET]: [http://crownyc-ubuntu-trusty64:5000/spec](http://crownyc-ubuntu-trusty64:5000/spec)
     - Swagger [GET]: [http://crownyc-ubuntu-trusty64:5000/api](http://crownyc-ubuntu-trusty64:5000/api)
     - Parser URL [POST]: [http://crownyc-ubuntu-trusty64:5000/parseaddresses](http://crownyc-ubuntu-trusty64:5000/parseaddresses)

## Some useful commands: 

`nginx: sudo service nginx (stop | start | restart)
addressparser: sudo (stop | start | restart) addressparser`
