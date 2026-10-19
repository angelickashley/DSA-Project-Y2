package model;

public class Student {
	private String name;
	private int    number;
	private double grade;

	public Student(String name, int num, long grade) {
		this.name   = name;
		this.number = num;
		this.grade  = Math.clamp(grade, 0, 100);
	}

	public String getName() { return this.name; }
	public void   setName(String name) { this.name = name; }

	public int  getNumber() { return this.number; }
	public void setNumber(int number) { this.number = number; }

	public double getGrade() { return this.grade; }
	public void   setGrade(double grade) { this.grade = Math.clamp(grade, 0, 100); }

	/**
	 * @param s the student you want to compare names with
	 * @return <ul>
	 * <li><code>returns -1 if this.name  < s.name</code></li>
	 * <li><code>returns  0 if this.name == s.name</code></li>
	 * <li><code>returns  1 if this.name  > s.name</code></li>
	 * </ul>
	 */
	public int compareName(Student s) {
		return Math.clamp(this.name.compareTo(s.getName()), -1, 1);
	}

	/**
	 * @param s the student you want to compare names with
	 * @return <ul>
	 * <li><code>returns -1 if this.number  < s.number</code></li>
	 * <li><code>returns  0 if this.number == s.number</code></li>
	 * <li><code>returns  1 if this.number  > s.number</code></li>
	 * </ul>
	 */
	public int compareNumber(Student s) {
		return this.number - s.getNumber();
	}

	/**
	 * @param s the student you want to compare names with
	 * @return <ul>
	 * <li><code>returns -1 if this.grade  < s.grade</code></li>
	 * <li><code>returns  0 if this.grade == s.grade</code></li>
	 * <li><code>returns  1 if this.grade  > s.grade</code></li>
	 * </ul>
	 */
	public int compareGrade(Student s) {
		double difference =  this.grade - s.getGrade();
		if (Math.abs(difference) < 0.001)
			return 0;
		else if (difference < 0)
			return -1;
		else
			return 1;
	}

	@Override
	public String toString() {
		return
		"student:{\"" + this.name + "\", " + this.number + ", " + this.grade + "%}";
	}

}
