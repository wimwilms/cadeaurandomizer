package be.discobolus.oudjaar.cadeau;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;

public class CadeauRandomizer {
	private MailSender mailSender;
	private Map<String, User> users;
	private List<User> randomized;
	
	public CadeauRandomizer() throws Exception {
		mailSender = new MailSender();
		mailSender.readConfig();
	}
	
	public void readConfig() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/config/users.csv");
		CSVReader reader = new CSVReader(new InputStreamReader(is), ',');
		populateUsers(reader.readAll());
		
		is = this.getClass().getResourceAsStream("/config/notbuyfor.csv");
		reader = new CSVReader(new InputStreamReader(is), ',');
		updateUsers(reader.readAll());
	}
	
	private void updateUsers(List<String[]> lines) {
		for (String[] line: lines) {
			if (!line[0].startsWith("#")) {
				User user = users.get(line[0]);
				user.addNotBuyFor(line);
			}
		}
	}

	private void populateUsers(List<String[]> lines) {
		users = new HashMap<String, User> ();
		for (String[] line: lines) {
			if (!line[0].startsWith("#")) {
				User user = new User(line);
				users.put(user.getId(), user);
			}
		}
	}

	private void randomize() {
		randomized = new ArrayList<User> ();
		List<User> userList =  new ArrayList<User>(users.values());
		User user = getUser(userList);
		randomized.add(user);
		while (randomized.size() < userList.size()) {
			User nextUser = getNextUser(user, userList);
			randomized.add(nextUser);
			user.setBuyFor(nextUser);
			System.out.println(randomized.size());
			user = nextUser;
		}
		User lastUser = randomized.get(randomized.size() - 1);
		User firstUser = randomized.get(0);
		if (lastUser.userAllowed(firstUser)) {
			lastUser.setBuyFor(firstUser);
		} else {
			throw new RuntimeException("Run again, not correct");
		}
	}

	private User getNextUser(User user, List<User> userList) {
		User nextUser = getUser(userList);
		boolean allowed = user.userAllowed(nextUser) && !randomized.contains(nextUser);
		if (!allowed) {
			nextUser = getNextUser(user, userList);
			System.out.println("Get Next User");
		}
		return nextUser;
	}

	private User getUser(List<User> userList) {
		Random random = new Random();
		int number = random.nextInt(userList.size());
		return userList.get(number);
	}
	
	private void sendMails() throws Exception {
		for (User user: randomized) {
			String subject = user.getSubject();
			String body = user.getBody();
			List<String> emails = user.getMailAdresses();
		//	System.out.println(subject);
		//	System.out.println(body);
			try {
				mailSender.sendMail(emails, subject, body);
			} catch (Exception e) {
				System.out.println(user.getId() + " failed");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		CadeauRandomizer randomizer = new CadeauRandomizer();
		randomizer.readConfig();
		randomizer.randomize();
		randomizer.sendMails();
	}
}
