# University Event Management System (Console Version)

**Course:** Software Design & Analysis (SDA) - Assignment 4
**Name:** Subhan Baig
**Registration No:** 24P-0632
**Section:** BS(CS) 9A

A Java console application that implements the Assignment 3 class diagram of the
University Event Management System (classes, inheritance, polymorphism, abstract class, interface).

## Project structure

```
src/
  model/   -> all domain classes (User, Student, Event, Venue, Budget, ...)
  main/    -> Main.java (console menu, program entry point)
```

## List of classes

| Class | Type | Package | Purpose |
|-------|------|---------|---------|
| Cancellable | interface | model | Common `cancel()` behaviour (Event, Registration) |
| User | abstract | model | Parent of all people; login, permissions |
| Student | concrete | model | Registers for / cancels events |
| Staff | abstract | model | Parent of staff roles |
| EventOrganizer | concrete | model | Creates events, records attendance |
| UniversityAdministrator | concrete | model | Approves budgets, reports |
| SystemAdministrator | concrete | model | Creates accounts, assigns roles |
| Guild | concrete | model | Guild details, members, activities |
| GuildPresidentsCouncil | concrete | model | Council meetings and decisions |
| Event | concrete | model | Event lifecycle; owns a Budget (composition) |
| Venue | concrete | model | Availability and reservation |
| Budget | concrete | model | Allocated / planned / actual amounts |
| Registration | concrete | model | Student registration for an event |
| Notification | concrete | model | Notification preparation and sending |
| Attendance | concrete | model | Attendance record |
| Report | concrete | model | Event, budget and participation reports |
| EventController | concrete | model | Coordinates use cases |
| Main | concrete | main | Console menu |

## How to run

**Eclipse**
1. File > New > Java Project (name: `UniversityEventManagement`).
2. Copy the `model` and `main` folders from this repository's `src` into the project's `src` folder.
3. Right-click `src/main/Main.java` > Run As > Java Application.

**Command line**
```
javac -d bin src/model/*.java src/main/*.java
java -cp bin main.Main
```
