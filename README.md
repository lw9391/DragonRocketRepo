## Dragon Rockets Library
This is a simple Java library to handle Dragon Rockets and space missions. It uses in-memory storage.
It provides operations as follows:
* create new rocket
* create new mission
* assign rocket to mission
* assign collection of rockets to mission
* set rocket status as requiring repair
* set rocket status as repaired
* end mission
* get summary of stored data

### Stack
Java 17 (Eclipse Temurin 17.0.15)
Gradle 8.14.1

After cloning, you can build it with the Gradle wrapper using `./gradlew build`.

### Assumptions
There are a few clarifications that need to be made.

The task description requires repository implementation. The solution provided is more like a high-level
domain service as it provides business-related operations. This is due to the requirements provided - the described API,
although similar to known repository implementations, required enforcing business rules. Because of that, during the
development the solution led to a domain class which orchestrates the described operations. This could be done in a more DDD
style, but the author considers this solution as simpler and good enough for this simple task's purpose. This domain class
uses immutable domain objects and more classic repositories as data access. Despite using immutable domain objects, for
simplicity reasons the solution is not thread-safe.

The aspect of providing a single point of access was intentionally omitted. Therefore, no singleton pattern, dependency injection, 
or other instance management mechanisms were implemented. It is viable to create multiple independent dragon repository instances. 
This design choice was made for simplicity reasons in this task; we can assume such architectural concerns should be handled by 
a framework or application container as technical implementation details.

Rocket statuses - The API provides a way to manually set a rocket status, unless you try to send a rocket into space without 
a mission, which is forbidden and will cause an exception. Rocket status influences mission status, which is described below.

Mission statuses - Besides the situation when a mission is ended, mission status solely depends on the rockets that are assigned
to a mission, if any. Therefore, it makes no sense to set status on demand as it would only make the stored data
inconsistent. The API provides a method to end a mission, which will remove rockets from the mission and set its status
explicitly as ended. Mission statuses are handled automatically by the provided API, there is no need to manually change them each time.

That being said, the task is somewhat vague on status handling. More interpretations could be made and more solutions
are possible. I decided to stick to automatic status changing for missions and manual status handling for rockets.

The author considers the task description more like product/business requirements to implement, not like a technical recipe
to rewrite in a programming language; therefore, the API was implemented this way.

### LLM usage
LLM was used to generate some of the tests after a couple of them were written by hand (so the LLM had an example of what to write).
This approach works well with the classical testing approach, where the LLM can write tests and then the implementation can be
done by a human.

### Testing
As mentioned above, I used a classical approach to tests; therefore, no mocks were used. This is for two reasons:
1. Considering we use in-memory storage, there is no overhead of running a database, application context, etc. The tests are
fast, and from this perspective, using mocks provides no advantages
2. In this task, I consider this approach to be simpler and better suited

### Example usage
Here is an example usage and output printed from the summary produced.

```java
SpaceXDragonRocketsRepository dragonRocketsRepository = new SpaceXDragonRocketsRepository(new InMemoryRocketRepository(), new InMemoryMissionRepository());

String mission1Name = "alpha-mission";
String mission2Name = "beta-mission";
String mission3Name = "gamma-mission";
String mission4Name = "delta-mission";

dragonRocketsRepository.addMission(mission1Name); // will have 3 rockets
dragonRocketsRepository.addMission(mission2Name); // will have 1 rocket
dragonRocketsRepository.addMission(mission3Name); // will have 3 rockets
dragonRocketsRepository.addMission(mission4Name); // will have 0 rockets

//and rockets assigned to missions
dragonRocketsRepository.addRocket("rocket-1");
dragonRocketsRepository.addRocket("rocket-2");
dragonRocketsRepository.addRocket("rocket-3");
dragonRocketsRepository.addRocket("rocket-4");
dragonRocketsRepository.addRocket("rocket-5");
dragonRocketsRepository.addRocket("rocket-6");
dragonRocketsRepository.addRocket("rocket-7");

// Assign rockets to create different counts
dragonRocketsRepository.assignRocketToMission("rocket-1", mission1Name);
dragonRocketsRepository.assignRocketToMission("rocket-2", mission1Name);
dragonRocketsRepository.assignRocketToMission("rocket-3", mission1Name); // mission1: 3 rockets
//one rocket set as damaged
dragonRocketsRepository.setRocketStatus(rocket1.name(), RocketStatus.IN_REPAIR);

dragonRocketsRepository.assignRocketToMission("rocket-4", mission2Name); // mission2: 1 rocket

dragonRocketsRepository.assignRocketToMission("rocket-5", mission3Name);
dragonRocketsRepository.assignRocketToMission("rocket-6", mission3Name);
dragonRocketsRepository.assignRocketToMission("rocket-7", mission3Name); // mission3: 3 rockets
dragonRocketsRepository.setRocketStatus(rocket5.name(), RocketStatus.IN_SPACE);
dragonRocketsRepository.setRocketStatus(rocket6.name(), RocketStatus.IN_SPACE);
Summary summary = dragonRocketsRepository.getDragonsSummary();
summary.printSummary();
```
Output:
```
gamma-mission - In Progress - Dragons: 3
	rocket-5 - In space
	rocket-6 - In space
	rocket-7 - On ground

alpha-mission - Pending - Dragons: 3
	rocket-2 - On ground
	rocket-3 - On ground
	rocket-1 - In repair

beta-mission - In Progress - Dragons: 1
	rocket-4 - On ground

delta-mission - Scheduled - Dragons: 0
```
When one mission is ended:
```java
dragonRocketsRepository.endMission(mission1Name);

// mission4: 0 rockets

//when getting dragons summary
Summary summary = dragonRocketsRepository.getDragonsSummary();
summary.printSummary();
```

Output:
```
gamma-mission - In Progress - Dragons: 3
	rocket-5 - In space
	rocket-6 - In space
	rocket-7 - On ground

beta-mission - In Progress - Dragons: 1
	rocket-4 - On ground

delta-mission - Scheduled - Dragons: 0

alpha-mission - Ended - Dragons: 0
```
