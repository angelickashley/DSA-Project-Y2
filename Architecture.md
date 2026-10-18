# Project Architecture Suggestions

This document is here to help us organise the project so that code stays clean and readable for both of us.

## Packages:
```
Model <--> Controller <--> View
```

- `Model` is for classes and their own functions that do **not** interact `View` package.
- `Controller` is the backend and allows the classes to talk to the GUI by passing along data. This package does what the `Model` package tells it to, i.e. what data it needs to display, what to calculate, what step of a visualisation it needs next, etc.

	The `controller` should not have anything to do with how data is displayed.
- `View` is the frontend and is purely visual to the best of our ability. buttons & interactive elements should contain their own logic, and it should be minimal by offloading work to the `controller`.
	
	The View's job should GUI. Displaying content, organising the page and styling the page.

The `Model` package should not be changed much unless we need a new class, struct or interface. Im happy to mostly write the `controller` packages.

## `Controller` functions needed by the `View` package
- `Student getStudentByName(String name)` optional clarification or explanation of function
- `Student getStudentByID(int ID)`
- `void addNewStudent(String name, int ID)`

## Amelia's Code preferences
These are not in any way necessary, I just like reading & writing code that follows these "rules".
If you're ok with my suggestions, I'll implement them so don't feel obligated to change them for me.
### tabs & spaces
I prefer tabs over spaces as they let each person choose how big the indentation should be. InteliJ should let you change this easily.
```Java
void main() {
····System.out.println("everyone has to use 4 spacesnow");
····System.out.println("and if someone doesn't (me, i like 2), it messes up everything");

··if (weUseSpaces == true) {
····System.out.println("So stuff will be misaligned between our code :(");
··}
}
```

```Java
void main() {
--->System.out.println("tabs can be any width, and it changes all the code at once for easy changes");
--->if (weUseSpaces == true) {
--->--->System.out.println("code is aligned for both of us");
--->}
}
```
I like variables to be aligned when declared together.
```Java
class Car {
	Wheel  wheels[];
	int    carID;
	String carBrand;
	Colour paintColour;
}
```

## Angelic's code preferences
Add whatever you want, or just things you wish I would do so you can read my code easier.
