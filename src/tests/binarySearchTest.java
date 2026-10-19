import model.LeftJustifiedList;
import model.Student;
import model.searchStateEnum;
import model.BinarySearcher;

void main() {
	LeftJustifiedList<model.Student> list = new LeftJustifiedList<model.Student>(10, 1);

	list.append(new Student("A. Power",    1,  68));
	list.append(new Student("D. O'Hare",   3, 100));
	list.append(new Student("B. Mulligan", 2,  80));
	list.append(new Student("B. Grant",    4,  45));
	
	System.out.println("list before sorting:\n" + list.toString() + "\n");

	list.sort(Student::compareName, true);
	System.out.println("list sorted by name:\n" + list.toString() + "\n");
	
	list.sort(Student::compareNumber, true);
	System.out.println("list sorted by num: \n" + list.toString() + "\n");

	list.sort(Student::compareGrade, true);
	System.out.println("list sorted by grade\n" + list.toString() + "\n");


	list.append(new Student("C. Cwikla", 6,  66));
	list.append(new Student("E. Hamer", 51,  44));
	list.append(new Student("G. Batan", 12,  55));
	list.append(new Student("K. Healy", 91,  33));

	list.sort(Student::compareName, true);
	System.out.println("final list sorted by name:");
	for (int i = 0; i < list.size(); i++) {
		System.out.printf("%02d: ", i);
		System.out.println(list.get(i));
	}

	// search by name, only that field matters, others do not have to match
	BinarySearcher<Student> searcher = new BinarySearcher<Student>(
		new Student("K. Healy", 91, 33),
		list,
		Student::compareName
	);

	Scanner scanner = new Scanner(System.in);  // Create a Scanner object
	do {
		for (int i = 0; i < list.size(); i++) {
			System.out.printf("%02d", i);
			if (i < list.size() - 1) System.out.print(",  ");
		}
		System.out.println();

		for (int i = 0; i <= searcher.end(); i++) {
			if (i == searcher.start()) System.out.print(" ^   ");
			else if (i == searcher.mid()) System.out.print(" ^   ");
			else if (i == searcher.end()) System.out.print(" ^   ");
			else System.out.print("     ");
		}

		System.out.println();
		for (int i = 0; i <= searcher.end(); i++) {
			if (i == searcher.start()) System.out.print("strt ");
			else if (i == searcher.mid()) System.out.print("mid  ");
			else if (i == searcher.end()) System.out.print("end  ");
			else System.out.print("     ");
		}

		scanner.nextLine();
		searcher.stepAlgorithm();
		System.out.println();
		System.out.println();
	} while (searcher.internalState() == searchStateEnum.SEARCHING);
	
	if (searcher.foundKey()) {
		System.out.println("found key at index: " + searcher.getKeyIndex());
	}
	else {
		System.out.println("key not in list!!");
	}
	scanner.close();
}
