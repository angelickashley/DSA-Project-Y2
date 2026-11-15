package controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.function.BiFunction;

import model.Database;
import model.Student;


public class AppSystem {
	private static String STUDENTS_CSV_PATH = "students.csv";

	public static void StartWindow_NewStudentList() {
		if (view.UI2_AddStudentWindow.IsWindowOpen()) return;
		// delete old list file
		File studentsFile = new File(STUDENTS_CSV_PATH);
		try (PrintWriter studentsWriter = new PrintWriter(studentsFile)) {
			studentsWriter.print("");
			studentsWriter.close();
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		view.UI2_AddStudentWindow.StartAddStudentWindow();
	}
	public static void StartWindow_ContinueStudentList() {
		if (view.UI3_ListWindow.IsWindowOpen()) return;

		// open list and add items to model list
		File studentsFile = new File(STUDENTS_CSV_PATH);
		
		try (Scanner studentsReader = new Scanner(studentsFile)) {
			while (studentsReader.hasNextLine()) {
				String line = studentsReader.nextLine();
				String[] values = line.split(",");
				if (values.length != 3) continue;

				String name   = values[0].trim();
				int    number = Integer.parseInt(values[1].trim());
				double grade  = Double.parseDouble(values[2].trim());
				AppSystem.AddNewStudent(name, number, grade);
				// System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		view.UI3_ListWindow.StartListWindow();
	}

	public static void AddNewStudent(String name, int num, double grade) {
		Student student = new Student(name, num, grade);
		if (!Database.Students.contains(student)) {
			Database.Students.append(student);
		}

		// System.out.println(Database.Students.toString());
	}

	public static int GetNumberOfStudents() {
		return Database.Students.size();
	}

	public static Student GetStudent(int index) {
		return Database.Students.get(index);
	}

	public static void SortStudents(BiFunction<Student,Student,Integer> comparator) {
		Database.Students.sort(comparator, true);
		// return Database.Students.get(index);
	}

	public static Student GetMostRecentlyAddedStudent() {
		return Database.Students.get(Database.Students.size()-1);
	}
}
