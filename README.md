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

Rocket statuses - The API provides a way to either set a rocket as being in repair or to set a rocket to a state indicating it has been repaired.
The other status depends on whether the rocket was assigned to a mission. Therefore, from this perspective, it does not make
sense to allow explicitly setting the ON_GROUND status. The API allows setting a rocket as damaged, which will change its status to
IN_REPAIR, and allows setting a rocket as being fine, and the status will be set to ON_GROUND if no mission is assigned or
to IN_SPACE if a mission was assigned.

Mission statuses - Besides the situation when a mission is ended, mission status solely depends on the rockets that are assigned
to a mission, if any. Therefore, as above, it makes no sense to set status on demand as it would only make the stored data
inconsistent. The API provides a method to end a mission, which will remove rockets from the mission and set its status
explicitly as ended.

Mission statuses and rocket statuses (besides the situations described above) are handled automatically by the provided API;
there is no need to manually change them each time.

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
dragonRocketsRepository.setRocketAsDamaged("rocket-1");

dragonRocketsRepository.assignRocketToMission("rocket-4", mission2Name); // mission2: 1 rocket

dragonRocketsRepository.assignRocketToMission("rocket-5", mission3Name);
dragonRocketsRepository.assignRocketToMission("rocket-6", mission3Name);
dragonRocketsRepository.assignRocketToMission("rocket-7", mission3Name); // mission3: 3 rockets
Summary summary = dragonRocketsRepository.getDragonsSummary();
summary.printSummary();
```
Output:
```
gamma-mission - In Progress - Dragons: 3
	rocket-6 - In space
	rocket-7 - In space
	rocket-5 - In space

alpha-mission - Pending - Dragons: 3
	rocket-1 - In repair
	rocket-2 - In space
	rocket-3 - In space

beta-mission - In Progress - Dragons: 1
	rocket-4 - In space

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
	rocket-7 - In space
	rocket-6 - In space
	rocket-5 - In space

beta-mission - In Progress - Dragons: 1
	rocket-4 - In space

delta-mission - Scheduled - Dragons: 0

alpha-mission - Ended - Dragons: 0
```
