package be.discobolus.oudjaar.cadeau;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {
	private String id;
	private String name;
	private String mail1;
	private String mail2;
	
	private User buyFor;
	
	private Collection<String> notBuyFor;
	
	public User(String[] line) {
		id = line[0].trim();
		name = line[1].trim();
		mail1 = line[2].trim();
		if (line.length > 3) {
			mail2 = line[3].trim();
		}
		notBuyFor = new ArrayList<String> ();
	}

	public String getId() {
		return id;
	}

	public void addNotBuyFor(String[] line) {
		for (int i = 1; i < line.length; i++) {
			notBuyFor.add(line[i].trim());
		}
	}
	
	public boolean userAllowed(User user) {
		boolean equals = id.equals(user.getId());
		boolean contains = notBuyFor.contains(user.getId());
		return !(equals || contains); 
	}
	
	public String toString() {
		return id;
	}

	public void setBuyFor(User nextUser) {
		this.buyFor = nextUser;
	}
	
	public String getSubject() {
		return "Cadeaukes Oudjaar (" + name + ") (2014 - 2015)";
	}
	
	public String getBody() {
		StringBuilder sb = new StringBuilder();
		sb.append("Hey " + name + ",\n");
		sb.append("\n");
		sb.append("Je bent uitgekozen om voor oudjaar een cadeautje te kopen voor " + buyFor.name + ".\n");
		sb.append("Kun je een mailtje sturen naar cyberpunk@discobolus.be zodat we weten dat iedereen deze (persoonlijke) mail gekregen heeft (liefst wel een nieuwe mail maken of de naam van de begunstigde wegcensureren).\n");
		sb.append("\n");
		sb.append("Groetjes,\n");
		sb.append("Wim");
		return sb.toString();
	}

	public List<String> getMailAdresses() {
		List<String> adresses = new ArrayList<String> ();
		adresses.add(mail1);
		if (mail2 != null) {
			adresses.add(mail2);
		}
		return adresses;
	}
}
