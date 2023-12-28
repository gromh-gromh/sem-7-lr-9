package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	final String BASE_URL = "https://atlas.herzen.spb.ru";
	String[] departmentUrls;
	String[] professorInfo;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		parseDepartmentUrl(args[0]);
		parseProfessorInfo();		
		printProfessorInfo();
	}

	public void printProfessorInfo(){
		for (String info : professorInfo) {
			if (info != null) System.out.println(info);
		}
	}

	private void parseDepartmentUrl(String department) throws Exception{
		final var html = Jsoup.connect(BASE_URL + "/faculty.php").get();
		final var departmentTag = html.select("a.alist:contains(" + department + ")");
		final var departmentUri = departmentTag.attr("href");

		if (departmentUri.equals("faculty.php")) {
			final var facultyId = departmentTag.attr("onclick").replaceAll("\\D+","");
			final var departmentTable = html.select("ul#fac_" + facultyId + " li a.alist");
			departmentUrls = new String[departmentTable.size() - 1];

			for (int i=0; i<departmentTable.size(); i++){
				final var _department = departmentTable.get(i);
				final var _departmentUri = _department.attr("href");
				if (!_departmentUri.contains("opop")){
					final var _departmentUrl = BASE_URL + "/" + _departmentUri;
					departmentUrls[i] = _departmentUrl;
				}
			}
		} else {
			departmentUrls = new String[1];
			departmentUrls[0] = BASE_URL + "/" + departmentUri;
		}
	}

	private void parseProfessorInfo() throws Exception{
		for (String url : departmentUrls) {
			final var html_2 = Jsoup.connect(url).get();
			final var rows = html_2.select("table.table_good tbody tr");
			int offset = 0;
			
			if (professorInfo != null){
				String[] _professorInfo = professorInfo.clone();
				professorInfo = new String[_professorInfo.length + rows.size()];
				for(int i=0; i<_professorInfo.length; i++){
					professorInfo[i] = _professorInfo[i];
				}
				offset = _professorInfo.length;
			} else {
				professorInfo = new String[rows.size()];
			}

			for (int i=1; i<rows.size(); i++) {
				final var row = rows.get(i);
				String info = "";
				for (final var td : row.select("td")) {
					info += td.text() + " ";
				}
				professorInfo[i + offset] = info;
			}
		}
	}
}
