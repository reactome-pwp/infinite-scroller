[<img src=https://user-images.githubusercontent.com/6883670/31999264-976dfb86-b98a-11e7-9432-0316345a72ea.png height=75 />](https://reactome.org)

# Infinite Scroller Widget
This list provides infinite scroll capability. This can be particularly useful in cases where the client has to show a large number of list items.

<img src="img/scroller.png " align="center" alt="scroller example">

## How to use the widget?

First add EBI Nexus repository in your pom.xml file

```xml
<repositories>
    ...
    <!-- EBI repo -->
    <repository>
        <id>nexus-ebi-repo</id>
        <name>The EBI internal repository</name>
        <url>http://www.ebi.ac.uk/Tools/maven/repos/content/groups/ebi-repo/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
    <!-- EBI SNAPSHOT repo -->
    <repository>
        <id>nexus-ebi-snapshot-repo</id>
        <name>The EBI internal snapshot repository</name>
        <url>http://www.ebi.ac.uk/Tools/maven/repos/content/groups/ebi-snapshots/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

Then add the scroller dependency

```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.reactome.web</groupId>
        <artifactId>scroller</artifactId>
        <version>1.0.2</version>
    </dependency>
<dependencies>
```

Finally you need to inherit the module in your ```.gwt.xml``` file

```xml
<inherits name="org.reactome.web.scroller.Scroller" />
```
      
Please check org.reactome.web.scroller.test for an example of how to use the list. 