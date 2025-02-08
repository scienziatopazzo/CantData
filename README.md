Hey, i am the dev of CantData, is the most simple data managment.
This is a beta.

# Create the database
We have to create the database class
```java
@Getter
public class Database extends CantDatabase<StoragePlayer> {

    public Database(Auth auth, boolean cached) {
        super(auth, cached, "test");
    }

    protected StoragePlayer createData(UUID id, String data) {
        return new StoragePlayer(id, data);
    }

}
```
This is the usage in the Main
```java
Database database = new Database(new Auth("database_cant", "localhost", 3306, "root", ""), true); 
```
The costructor of Database is:
- Auth (Database Name, IP, Port, UserName, Password)
- Cached: if true, all tha data will be cache. this is use false only for multi server database system.

# Lets create the Storage Player
I want tell you that you can create every thing you want, like team etc..
```java
public class StoragePlayer extends CantData<StorageValue> {

    public StoragePlayer(UUID uuid, String data) {
        super(uuid, StorageValue.class, data);
    }

    public StoragePlayer(UUID uuid) {
        super(uuid, StorageValue.class, null);
    }

    public void save() {
        Main.getDatabase().save(this);
    }

}
```
# Create the Storage Value, Where the magic happen
```java
@RequiredArgsConstructor @Getter
public enum StorageValue implements DataEnum {

    NAME(String.class),
    KILLS(Integer.class),
    ;

    private final Class<?> type;

}
```
Of default you can use
- String.class
- Integer.class
- Long.class
- Float.class
- Double.class

And... if you want use create your own? For example an UUID storage system
```java
public class DataUUID extends DataType<UUID> implements Comparable<DataUUID> {

    @Override
    public String serialize() {
        return get().toString();
    }

    @Override
    public void apply(String string) {
        set(UUID.fromString(string));
    }

    @Override
    public UUID empty() {
        return new UUID(0L, 0L);
    }

    @Override
    public int compareTo(DataUUID other) { 
        return this.get().compareTo(other.get());
    }
}
```
And for finish registration: `DataTypes.register(UUID.class, DataUUID.class);`


# Now the better question, how i use it? 
Create:
```java
StoragePlayer storagePlayer = new StoragePlayer(uuid);
```
Update a value:
```java
storagePlayer.from(StorageValue.STRING).set("database can't communicate");
```
Save (only if you have do the method like up):
```java
storagePlayer.save();
```
Use native Data Type for more method:
```java
DataInteger dataInteger = (DataInteger) ((Object)storagePlayer.from(StorageValue.INT));
dataInteger.add(random.nextInt());
```
Get a value:
```java
storagePlayer.from(StorageValue.STRING).get()
```
Get an existing Storage Player:
```java
database.find(uuid, false);
```
- UUID is the primary value
- false is the `queryIfNull` that if is null, find on the database. (use it like the first time when you get the player)
Find By Onther parameter: (Return the storage player)
```java 
database.find(StorageValue.NAME, "abc");
```

# The feature
```java
database.find(StorageValue.INT, 10, true)
```
This is not a feature??? bha, see the parameter
- Value
- Top
- Reverse (true like 99, 39, 10. if false 10, 39, 99)
this is the best things i ever made in a database managment system.

If you want to see my olther public database managment system, see https://github.com/SkyNest-Studios/SkyDatabase (The maven repo dont go, but if you want self host, contact me)
