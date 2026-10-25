package controller;

import model.Database;
import model.Student;


public class AppSystem {
	public static void StartWindow_NewStudentList() {
		if (view.UI2_AddStudentWindow.IsWindowOpen()) return;
		// delete old list file
		view.UI2_AddStudentWindow.StartAddStudentWindow();
	}
	public static void StartWindow_ContinueStudentList() {
		if (view.UI3_ListWindow.IsWindowOpen()) return;

		// open list and add items to model list
		
		view.UI3_ListWindow.StartListWindow();
	}

	public static void AddNewStudent(String name, int num, double grade) {
		Student student = new Student(name, num, grade);
		if (!Database.Students.contains(student)) {
			Database.Students.append(student);
		}

		System.out.println(Database.Students.toString());
	}

	public static Student GetMostRecentlyAddedStudent() {
		return Database.Students.get(Database.Students.size()-1);
	}
}
