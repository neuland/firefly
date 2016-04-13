# Project Firefly

![Firefly Logo](https://raw.githubusercontent.com/neuland/firefly/master/web/webroot/img/logo-bg.svg)

* Perform hybris DB updates automatically on startup if required.
* Supports the use of feature branches, so a hybris DB is not updated after branch change.
 
## Planned Features

* Tracks DB migrations in your extention and executes them if migration is needed.

## Prerequisites

* Java VM 1.7+
* Installed hybris

## Installation

1. git clone https://github.com/neuland/firefly.git

2. Add the path to firefly into your `localextensions.xml` (in Hybris config folder):
 ```xml
    <extension dir="${HYBRIS_BIN_DIR}/firefly"/>
 ```
3. Configure firefly in your `local.properties` (in Hybris config folder):
 ```properties
    # Start update on system start automatically.
    firefly.migrationOnStartup=false
 ```

4. Build hybris and start

5. Start the DB update for the last time manually

6. Stop hybris

7. Configure firefly in your `local.properties` (in Hybris config folder):
 ```properties
    # Start update on system start automatically.
    firefly.migrationOnStartup=true
    
    # If set to true, no update will be triggered if current version of the items.xml or hmc.xml 
    # has been used during an update before. Use this on your local dev enviroment to prefent updates 
    # after branch change.
    firefly.relaxedMode=false
 ```

8. start hybris

9. Access the HMC (Hybris Management Console) to check firefly setup. You should see a Firefly-Node in the tree on the left hand side.

# Documentation

Read more in the Wiki (https://github.com/neuland/firefly/wiki) 
