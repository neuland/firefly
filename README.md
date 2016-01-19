# Project Firefly

## Prerequisites

* Java VM 1.7+
* Installed hybris

## Installation

1. git clone https://github.com/neuland/firefly.git

2. Add the path to firefly into your `localextensions.xml` (in Hybris config folder):

```xml
    <extension dir="${HYBRIS_BIN_DIR}/firefly"/>
```

3. Build hybris and start

4. Access the HMC (Hybris Management Console) to check firefly setup. You should see a Firefly-Node in the tree on the left hand side.
