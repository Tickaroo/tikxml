# Annotating Model Classes
`TikXml` uses annotation processing to generate the xml serializer / deserializer (parser) for java model classes (POJO).
We have to provide a _mapping_ from java class to xml. This is done by annotating the java class. Basically you can annotate fields of your model class.
Since the generated serializer / deserializer will be in the same package as the original java model class your fields must be either:

- not _private_ or _protected_. In other words fields must be _public_ or have _default_ (package) visibility. Furthermore fields can not be _static_ or _final_.
 ```java
   public class Book {
   
     String id;  // package visibility is ok
     public String title; // public visibility is ok
   }
 ```

**or**

- fields can be private but must provide a non _private or protected getter and setter_ methods following java method naming convention (`setFoo(Foo foo), getFoo()`)
  ```java
     public class Book {
     
       private String id;  // package visibility is ok
       
       public void setId(String id) { this.id = id; }
       public String getId() { return id; }
     
     }
   ```

## Mark a class as model class
To mark a class as serializeable / deserializeable by `TikXml` you have to annotate your model class with `@Xml`.

```java
@Xml(nameAsRoot = "book") // name is optional. Per default we use class name in lowercase
public class Book {

  String id; 
}
```

Per default we use the class name in lowercase, but you can customize it within the `@Xml( nameAsRoot = "foo")` annotation.
We use this name for both writing xml (root xml tag will be named according this), but also for reading elements to map a xml element name to a certain type by this name as we will see later.


## XML Element attributes
Reading and writing the following xml:

```xml
<book id="123"></book>
```

```java
@Xml
public class Book {

  @Attribute(name = "id") // name is optional, per default the field name will be used as name
  String id; 
}
```

## Type Converter
`@Attribute` can be read and write primitives like `int`, `long`, `double`, `boolean` and `String` (and wrapper classes like `Integer`, `Long`, `Double`). Additionally, to this build in types you can specify your own type converter that takes the xml attribute's String value as input and convert it to the desired type:
 

```xml
<book id="123" publish_date="2015-11-25"></book>
```

```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @Attribute(name = "publish_date", converter = MyDateConverter.class)
  Date published; 
}
```

```java
public class MyDateConverter implements Converter<Date> {

  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd"); // SimpleDateFormat is not thread safe!

  @Override
  public Date read(String value) throws Exception {
    return formatter.parse(value);
  }
  
  @Override
  public String write(Date value) throws Exception {
    return formatter.format(value);
  }
  
}
```

Your custom `Converter` must provide an empty (parameter less) constructor).
As you see, you can specify a custom converter for each field with `@Attribute(converter = MyConverter.class)`. 
Additionally, you can set default converter for all your xml feeds directly in `TikXml`. 

```java
TikXml parser = new TikXml.Builder()
               .addTypeConverter(Date.class, new MyDateConverter() ); // all fields of type Date will be serialized / deserialized by using MyDateConverter
               .build();
```

If you set a default converter you can still apply another converter on a specific field via annotation.
The converter specified in the annotation will be used instead of the default converter.

**Please note that the MyDateConverter shown above is not thread safe** because `SimpleDateFormat` is not thread safe. TikXml already provides some `TypeConverter` like `DateRfc3339TypeConverter` (additional dependency) for parsing dates (thread safe).

Many times we have to encode and decode html/xml characters like `<` with `&lt;` or `"` with `&quot;` etc. Wouldn't it be nice to be able to register `TypeConverters` for primitives application wide as well?
With TikXml you can do that:

```java
TikXml parser = new TikXml.Builder()
               .addTypeConverter(String.class, new HtmlEscapeStringConverter() ); // HtmlEscapeStringConverter encode / decode html characters. This class ships as optional dependency
               .build();
```
Since `TikXml` is highly optimized for performance primitives (we count `String` as a primitive as well) don't do a lookup for a `TypeConverter`. You have to specify explicitly which primitives (and primitives wrapper) should do the lookup for a type converter. This has to be done by the annotation processor argument `primitiveTypeConverters` which accepts a list of full qualified class names for those primitives (and primitives wrappers) you want to use TypeConverters for:

```groovy
apt {
    arguments {
        primitiveTypeConverters "java.lang.String, java.lang.int, java.lang.Integer"
    }
}
```

Note that you have to specify both, the primitive itself `java.lang.int` and his wrapper class `java.lang.Integer`, to say `TikXml` that a `TypeConverter` should be used for those types.

## Property Elements
In XML not only attributes can be used to model properties but also nested elements like this:
```xml
<book id="123">
  <title>Effective Java</title>
  <author>Joshua Bloch</author>
  <publish_date>2015-11-25</publish_date>
</book>
```

In java we have to annotate the `Book` class by using `@PropertyElement`:

```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @PropertyElement
  String title;
  
  @PropertyElement
  String author;
  
  @PropertyElement(name = "publish_date", converter = MyDateConverter.class)
  Date published; 
}
```

The `@PropertyElement` annotation is similar to the `@Attribute` annotation. You can optionally specify a `name` (otherwise field name will be used) and a `converter`. 
The converters can be shared between `@Attribute` and `@PropertyElement`. So you can use your custom converter like `MyDateConverter` for both, `@Attribute` and `@PropertyElement`.
Also, a default converter set with `tikXml.setConverter(Date.class, new MyDateConverter() );` will be used for both as well.

## Child Elements
In XML you can nest child element in elements. You have already seen that in `@PropertyElement`. 
However, property elements are there read just the text content of an element and meant to be used 
for primitives like `int`, `double`, `String` etc. (eventually other "simple" data types like `Date` via custom `TypeConverter`).

If you want to parse or write child elements (or child objects) then `@Element` is the annotation you are looking for:

```xml
<book id="123">
  <title>Effective Java</title>
  <author>     <!-- child element -->
    <firstname>Joshua</firstname>
    <lastname>Bloch</lastname>
  </author>
</book>
```

```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @PropertyElement
  String title;
 
  @Element
  Author author;
}


@Xml
public class Author {
  
  @PropertyElement
  String firstname;
  
  @PropertyElement
  String lastname;
}
```

`TikXml` will write and parse an instance of `Author` automatically for you. You may now ask yourself how `TikXml` knows that `<author>` maps to java class `Author.class`.
This is done by detecting that class `Author` is annotated with `@Xml`. Since `Author` doesn't specify a custom name `@Xml(nameAsRoot="foo")` TikXml is using the class name with the first character to lower case.

We can override this mapping by specifying a name:
```java
@Xml(nameAsRoo="foo")
public class Author {
  
  @PropertyElement
  String firstname;
  
  @PropertyElement
  String lastname;
}
```

Then the following xml will be parsed as well:

```xml
<book id="123">
  <title>Effective Java</title>
  <foo>     <!-- foo maps to Author.class -->
    <firstname>Joshua</firstname>
    <lastname>Bloch</lastname>
  </foo>
</book>
```

But that would mean that in our whole application all `<foo>` tags will map to `Author.class`. Usually this is the common case.
However, we can also override the name of an element in `Book` class like this:

```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @PropertyElement
  String title;
 
  @Element(name = "foo2")
  Author author;
}

@Xml // not nameAsRoot specified
public class Author {
  
  @PropertyElement
  String firstname;
  
  @PropertyElement
  String lastname;
}
```

That means, that globally we are still mapping `<author>` to `Author` class, but while reading a `<book>` element we map `<foo2>` elements to `Author` class. So reading the following xml would be possible:

```xml
<book id="123">
  <title>Effective Java</title>
  <foo2>     <!-- foo2 maps to Author.class -->
    <firstname>Joshua</firstname>
    <lastname>Bloch</lastname>
  </foo2>
</book>
```



## Polymorphism and inheritance
`TikXml` supports inheriting attributes, properties and elements from super class

```java
@Xml
class Author {

  @PropertyElement
  String firstname;
  
  @PropertyElement
  String lastname;
  
}


@Xml(inheritance = true) // inherits firstname and lastname xml properties. Default value is true
public class Journalist extends Author {

  @PropertyElement(name = "newspaper_publisher")
  String newspaperPublisher; // the name of the newspaper the Journalist works for
}
```

`Author` has firstname and lastname fields. Since `Journalist extends Author` Journalist (java class) of course  
has inherited firstname and lastname fields as well. That also means that the xml representation of an Journalist 
has firstname and lastname xml elements. This is usually the desired behaviour and therefore the default behaviour. However, you can 
disable that via `@Xml(inheritance = false)`. If you set `inheritance = false` Journalist's xml representation
will not have the inherited properties from super class (firstname and lastname), but only the properties 
defined in the Journalist class itself (only newspaperPublisher). 

Now let's take a look how to resolve polymorphism while reading xml.
Lets say that a book can be written by either a `Author` or an `Journalist`:

```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @PropertyElement
  String title;
 
  @Element(
    typesByElement = {
      @ElementNameMatcher(type = Author.class),
      @ElementNameMatcher(type = Journalist.class)
    }
  )
  Author author;
}
```

So`@Element(typesByElement = @ElementNameMatcher)` is where we have to define how we determine polymorphism while reading xml.
With `@ElementNameMatcher(type = Journalist.class)` we define that, xml element `<journalist>` is parsed into an `Journalist` object (class Journalist has to be annotated with `@Xml` and you can specify a `rootAsName` as described above).

```xml
<book id="111">
  <title>Android for Dummies</title>
  <journalist>
    <firstname>Hannes</firstname>
    <lastname>Dorfmann</lastname>
    <newspaper_publisher>New York Times</newspaper_publisher>
  </journalist>
</book>
```

Additionally, `TikXml` is able to read an `Author`. We expect an xml element with the name `author` (class Author has to be annotated with `@Xml`, `nameAsRoot` can be used to specify the name)

```xml
<book id="123">
  <title>Effective Java</title>
  <author>
    <firstname>Joshua</firstname>
    <lastname>Bloch</lastname>
  </author>
</book>
```

So TikXml will use the same mechanism as alrady mentioned to map `@Xml(nameAsRoot="foo")` annotated classes to `<author>` or `<journalist>` tag to `<author>`. We have already seen that we can override this mapping with `@Element(name="foo")`. 
We can do the same with `@ElementNameMatcher( name="foo")` like this:

```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @PropertyElement
  String title;
 
  @Element(
    typesByElement = {
      @ElementNameMatcher(type = Author.class),
      @ElementNameMatcher(type = Journalist.class, name = "journ",)
    }
  )
  Author author;
}
```

to read a xml document like this:
```xml
<book id="111">
  <title>Android for Dummies</title>
  <journ>  <!-- maps to clas Journalist -->
    <firstname>Hannes</firstname>
    <lastname>Dorfmann</lastname>
    <newspaper_publisher>New York Times</newspaper_publisher>
  </journ>
</book>
```

This kind of polymorphism resolving also works with `Interfaces`:

```java
interface Writer {
  int getId();
}

@Xml
class Author implements Writer {

  @Attribute
  String id; 
    
  @PropertyElement
  String firstname;
  
  @PropertyElement
  String lastname;

}

@Xml
class Organization implements Writer {

  @PropertyElement
  String id; 
    
  @PropertyElement
  String name;
 
}
```

Now a Book expects an `Writer`:
```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @PropertyElement
  String title;
 
  @Element(
    typesByElement = {
      @ElementNameMatcher(type = Author.class),
      @ElementNameMatcher(type = Organization.class),
    }
  )
  Writer writer;
}
```

Now `TikXml` can read both xml variants:
```xml
<book id="123">
  <title>Effective Java</title>
  <author id="1">
    <firstname>Joshua</firstname>
    <lastname>Bloch</lastname>
  </author>
</book>
```

and

```xml
<book id="123">
  <title>end-of-year review</title>
  <organization>
    <id>23</id>
    <name>New York Times</name>
  </organization>
</book>
```

You can define arbitrary many `@ElementNameMatcher` to resolve polymorphism. 
Since resolution of polymorphism is done by **checking the xml element name** the `<book>` can only have 
one single `<author />` tag, because we can't use xml element's name as property anymore (as we did with `@PropertyElement`).

Therefore something like this is not valid:

```java
@Xml
public class Book {

  @Attribute
  String id; 
  
  @PropertyElement
  String title;
 
  @Element(
    typesByElement = {
      @ElementNameMatcher( name = "author", type = Author.class),
      @ElementNameMatcher( name = "organization", type = Organization.class),
    }
  )
  Writer writer1;
  
  @Element(
      typesByElement = {
        @ElementNameMatcher( name = "author", type = Author.class),
        @ElementNameMatcher( name = "organization", type = Organization.class),
      }
    )
    Writer writer2;
}
```

```xml
<book id="123">
  <title>Effective Java</title>
  <author id="1">
    <firstname>Joshua</firstname>
    <lastname>Bloch</lastname>
  </author>
  
  <author id="2">
    <firstname>Hannes</firstname>
    <lastname>Dorfmann</lastname>
  </author>
</book>
```

The parser can't know whether `<author>` maps to `writer1` or `writer2`. However, we can parse the same xml tags into a list as we will see in the next chapter.


## List of elements
If we want to have a List of child elements we use `@Element` on `java.util.List`:

```java
@Xml
class Catalogue {

  @Element
  List<Book> books;

}
```

which will read and write the following xml:

```xml
<catalog>
    <book id="1">...</book>
    <book id="2">...</book>
    <book id="3">...</book>
</catalog>
```

As you see, all Lists are treated as inline lists (there is no extra xml tag for `<books>` containing a list of `<book>` element like this:

```xml
<catalog>
  <books>
    <book id="1">...</book>
    <book id="2">...</book>
    <book id="3">...</book>
  </books>
</catalog>
```
We can add this extra `<books>` xml tag by adding a `@Path` annotation which "simulates" a xml node as we will see later in the next chapter:

```java
@Xml
class Catalogue {

  @Path("books")
  @Element
  List<Book> books;

}
```


With `@Element( typesByElement = @ElementNameMatcher(...) )` you can deal with polymorphism for lists the same way as already shown for other elements.

## Paths 
Have a look at the following example: Imagine we have a xml representation of a bookstore with only one single book and one single newspaper:
```xml
<shop>
  <bookstore name="Lukes bookstore">
    <inventory>
      <book title="Effective Java" />
      <newspaper>
          <title>New York Times n. 192</title>
      </newspaper>
    </inventory>
  </bookstore>
</shop>
```

To parse that into java classes we would have to add a `Bookstore` class and a `Inventory` 
class to be able to parse that kind of blown up xml. This isn't really memory efficient because we 
have to instantiate `Bookstore` and `Inventory` to access `<book>` and `<newspaper>`.
With `@Path` we can **emulate** this xml nodes:

```java
@Xml
class Shop {

  @Path("bookstore") // means <bookstore name="value" />
  @Attribute
  String name;
  
  @Path("bookstore/inventory")  //  '/' indicates child element
  @Element
  Book book;
  
  @Path("bookstore/inventory")
  @Element
  Newspaper newspaper;
    
}
```

`TikXml` will read `<bookstore>` and `<inventory>` without the extra cost of allocating such an object.
It will also take that "virtual emulated" nodes into account when writing xml.

Please note that this is not **XPath**. It looks similar to XPath, but XPath is not supported by `TikXml`.

However, with `@Path` you **can't access xml attribute or child elements that belongs to another type**, because for each type an own `TypeAdapter` will be generated who is responsible to read and write xml.
Let's take a look at the same xml as shown before but change the structure we want to parse this data into:

```xml
<shop>
  <bookstore name="Lukes bookstore">
    <inventory>
      <book title="Effective Java" />
      <newspaper>
        <title>New York Times n. 192</title>
      </newspaper>
    </inventory>
  </bookstore>
</shop>
```

```java
@Xml
class Shop {

  @Path("bookstore") // ERROR: attribute name can't be accesses because belongs to TypeAdapter of BookStore and not to TypeAdapter of Shop
  @Attribute
  String name;
  
  @Element
  BookStore bookstore;
    
}

@Xml
class BookStore {

   @Element
   Inventory inventory;
   
   @Path("inventory")    // ERROR: Element can't be accessed because belongs to TypeAdapter of Inventory
   @Element
   Book book;
   
   
   @Path("inventory")    // ERROR: Element can't be accessed because belongs to TypeAdapter of Inventory
   @Element
   Newspaper newspaper;

}
```

Step by step explanation:
`Shop` contains an `@Element BookStore`. `@Element` means that this is a child element. Therefore `TikXml` will generate a TypeAdapter (a xml reader/parser and xml writer) for `BookStore`. 
When `TikXml` is reading the xml it will start with `ShopTypeAdapter`. Then when `<bookstore>` xml tag will be detected, the `ShopTypeAdapter` wont continue to parse this `<bookstore>` xml tag. 
Rather he asks the `BookStoreTypeAdapter` to continue. Then the `BookStoreTypeAdapter` will continue parsing the `<bookstore>` xml tag. 
Therefore the attribute name of `<bookstore name="Lukes bookstore">` will be consumed by `BookStoreTypeAdapter`. Thus, `ShopTypeAdapter` can't consume this attribute again because it's already consumed by `BookStoreTypeAdapter`.
Exactly the same is true for `<inventory>` xml tag. As the field `Inventory inventory` in class `BookStore` is annotated with `@Element` `TikXml` will generate a `InventoryTypeAdapter` that consumes the whole `<inventory>` xml tag.

**Basically the content of an xml tag that already maps to an java class field annotated with `@Element` (incl. lists) can't be accessed via `@Path` from outside.**

So when is `@Path` useful? As already described at the beginning of this section, with `@Path` we can't get rich of unnecessary object allocation for xml tags that just wraps the real data we are interested in.
Performance with `@Path` is still the same as parsing each xml tag into its own java class.
i.e parsing `<shop>` into Shop.class, `<bookstore>` into BookStore.class, `<inventory>` into Inventory.class, `<book>` into Book.class and `<newspaper>` into  Newspaper.class will have the same performance as pasing the same xml data into the following class annotated with `@Path`:

```java
@Xml
class Shop {

  @Path("bookstore")
  @Attribute("name")   
  String bookstoreName; //  "Lukes bookstore"
  
  @Path("bookstore/inventory/book")
  @Attribute("title")
  String bookName;     // "Effective Java"
  
  @Path("bookstore/inventory/newspaper")
  @PropertyElement("title")
  String newspaperTitle;     // "New York Times n. 192"
    
}
```

Instead of allocating an java objects for `Shop` `Bookstore`, `Inventory`, `Book` and `Newspaper` (5 objects)
we are just allocating one `Shop` object. So the only reason `@Path` is useful is if you have memory concerns. Usually this should be fixed on backend side by providing a more flat xml document. Unfortunately, we have experienced that in practice this is not always possible for various reasons. `@Path` is exactly for such situations.

Please note that the Shop example is a very dumb example. Usually you would prefer having an object `Book` and `Newspaper` (with more attributes etc.). This example is just to illustrate (exaggerated) when `@Path` could be useful (memory concerns when dealing with very very large xml documents with lot of "wrapper" xml tags).


## Text Content
In XML the content of an xml element can be a mix of text and child elements:
```xml
<book>
  <title>Effective Java</title>
  <author>
    <name>Joshua Bloch</name>
  </author>
  This book talks about tips and tricks and best practices for java developers
</book>
```

This is valid XML. We have an description text directly embedded `<book></book>`. But how do we read that description text? 
In that use case we can use `@TextContent` for reading and writing such xml:

```java
@Xml
class Book {

  @PropertyElement
  String title;
  
  @Element
  Author author;
  
  @TextContent
  String description; // Contains the text "This book talks about ..."
}
```

You can think of `@TextContent` as some kind of inline `@PropertyElement`. `@TextContent` 
reads the whole text content of a XML element even if there are other xml elements in between:

```xml
<book>

  This book talks 
  
  <title>Effective Java</title>
  
  about tips and tricks
  
  <author>
    <name>Joshua Bloch</name>
  </author>
 
  and best practices for java developers
  
</book>
```

If you have multiple `@TextContent` annotations along your inheritance hierarchy, only the `@TextContent` annotated field in the "leaf class" will be taken into account.

```java
@Xml
class Person {

  @Attribute
  String id;

  @TextContent
  String description;

}

@Xml
class Author extends Person {

  @TextContent
  String authorDescription;
}
```

If we parse an `Author` from a XML document then the text content will be parsed only into `Author.authorDescription`, whereas if we parse a `Person` the text content will be parsed into `Person.description`.

## Scan Modes
As you see there are quite some annotations. Usually programmers are lazy people. Therefore we provide two modes.
 1. **ANNOTATION_ONLY**: This means that only fields with annotations like `@Attribute`, `@Element`, `@PropertyElement`, `@TextContent` will be used. Any other fields are not be taken into account when scanning for xml mappings.
 2. **COMMON_CASE**: The "common case" means all primitive java data types (like int, double, string) are mapped to xml attributes (is equal to
annotating class fields with `@Attribute`. All non primitive types (in other words objects) are mapped to child objects (is equal to annotating class fields with {@link
  Example: 
 
  ```xml
   <book id="123" title="Effective java"> `
    <author>...</author> 
   </book> ``
  ```
  By using COMMON_CASE you don't have to write much annotations:
  
  ```java
  @Xml(mode = ScanMode.COMMON_CASE) 
  class Book { 
    int id;          // Doesn't need an @Attribute
    String title;    // Doesn't need an @Attribute 
    Author author;   // Doesn't need an @Element
    
    @IgnoreXml
    double calculatedPrice; // Will be ignored
  }
  ```

Per default **COMMON_CASE** will be used. You can specify default Scan Mode globally for all your `@Xml` annotated classes by setting up the annotation processor option like this:
```groovy 
apt {
  arguments {
    defaultScanMode "ANNOTATION_ONLY"  // or "COMMON_CASE"
  }
}
```

## Required mapping
Per default a mapping from XML to java class is required. That means, if you have the following java class:

```java
@Xml 
class Book {
  @PropertyElement 
  String title;
}
```

but are reading the following xml

```xml
<book id="123">  <!-- id is not defined in Book.java -->
  <title>Android for Dummies</title>
</book>
```

TikXml will throw an exception because there is no mapping from xml attribute `id` to a java field in `Book` class.
Usually you want an exception to be thrown because usually you need all data from xml field (or may talk to the backend developers that this information is not needed and they should remove it). But there might be scenarios where this is not the desired behaviour. Hence, you can configure that a mapping is required in `TikXml`:

```java
TikXml tikXml = TikXml.Builder()
                      .exceptionOnUnreadXml(true) // set this to false if you don't want that an exception is thrown
                      .build();
```

For performance reason, the other way around is not implemented: Let's say you have a class `Book` and want to ensure that all fields are filled from xml:

```java
@Xml 
class Book {

  @Attribute
  int id;
  
  @PropertyElement 
  String title;
}
```

```xml
<book>  <!-- No attribute id  -->
  <title>Android for Dummies</title>
</book>
```
As already said, this is not supported (yet) because of performance reasons.

# Proguard
```
-keep class com.tickaroo.tikxml.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$TypeAdapter { *; }

-keepclasseswithmembernames class * {
    @com.tickaroo.tikxml.* <fields>;
}

-keepclasseswithmembernames class * {
    @com.tickaroo.tikxml.* <methods>;
}
```

# Kotlin
Kotlin is supported, except `data classes`

```kotlin
@Xml
class Book {
  @Attribute 
  var id : Integer = ""
  
  @Element 
  lateinit var author : Author  // Also works with lateinit
}
```
