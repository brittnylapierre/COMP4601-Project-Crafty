package comp4601.project.resources;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import comp4601.project.models.User;







//Creates communities from profiled users
/*
 * {	
		initialize	population;	
		evaluate	population;	
		while	TerminationCriteriaNotSatsfied
		{	
			select parents for reproduction;	
			perform	recombination and mutation;	
			evaluate population;	
		}	
	}	
	
	use jgap https://karczmarczuk.users.greyc.fr/TEACH/IAD/java/jgap_tutorial.html
 * */
public class Communitizer {
	
	private int no_clusters = 2;
	private int no_features=10;
	private int no_users;
	private KmeansUser[] users;
	private boolean changed;
	
	
	public Communitizer(int no_users) {
		super();
		this.no_users = no_users;
		this.users= new KmeansUser[no_users];
	}

	private KmeansUser createCenter(int v){
		KmeansUser u=new KmeansUser("center",v,this.no_clusters);
		for (int j = 0; j < v; j++) {//accounts for total reviews in genre average rating and average sentiment 

				int top=1;
				int bottom=0;

			double random = ThreadLocalRandom.current().nextDouble(0.05, 0.20);
			System.out.println(random);
			u.features[j]= random;
		}
		
		return u;		
	}
	
	private KmeansUser moveCenter(int v,KmeansUser u){
		//User u=new User("center",v,2);
		double[] totals= new double[this.no_features];
		int count=0;
		for(KmeansUser ur :users){
			if(ur.cluster==v){
				count++;
				for (int j = 0; j < no_features; j++) {
					totals[j]+=ur.features[j] ;
				}
			}
		}
		if(count!=0){
		for (int j = 0; j < no_features; j++) {
			u.features[j]=totals[j]/count ;
		}
		}
		
		return u;		
	}
	public void addUser(User u,int i){
		users[i]=new KmeansUser(u);
		//System.out.println("Added user");
	}
	public int getCommuityForUser(int i){
		return users[i].cluster;
	}
	public void algorithm() {

		KmeansUser[]centers = new KmeansUser[this.no_clusters];
		centers[0]=null;
		int itterations=0;
		changed=true;
		System.out.println("in the algorithm");
		for(int i=0;i<this.no_clusters;i++){
			centers[i]=createCenter(this.no_features);
		}
		while (changed) {
			System.out.println("*******************************"+itterations+"**********");
			changed=false;
			itterations++;
				for(int i=0;i<this.no_clusters;i++){
					centers[i]=this.moveCenter(i, centers[i]);
				}
			// Your code here
			//change centers
			for(KmeansUser u :users){
				double min=10.0;
				int minIndex=0;
				for(int i=0;i<this.no_clusters;i++){
					double d = distance(u,centers[i]);
					u.distance[i]=d;
					if(d<min){
						min=d;
						minIndex=i;
					}
					
				}
				u.update();
				u.cluster=minIndex;
				if(u.changed()){
					changed=true;
				}
			}	
			for(int i=0;i<this.no_clusters;i++){
				int count=0;
				for(KmeansUser u :users){
					if(u.cluster==i){
						count++;
					}
					//System.out.println(u.distance[i]);
				}
				
				System.out.println("cluster "+i+"has: "+count+" points");
			}
		}
		
		for(int i=0;i<this.no_clusters;i++){
			int count=0;
			for(KmeansUser u :users){
				if(u.cluster==i){
					count++;
				}
				//System.out.println(u.distance[i]);
			}
			
			System.out.println("cluster "+i+"has: "+count+" points");
		}
		
	}
	private double getDistances(){
		double total=0.0;
		for(KmeansUser u:users){
			total+=u.distance[u.cluster];
		}
		return total;
	}
	
	/* 
	 * Computes distance between two users
	 * Could implement this on User too.
	 */
	private double distance(KmeansUser a, KmeansUser b) {
		double rtn = 0.0;
		// Assumes a and b have same number of features
		for (int i = 0; i < a.features.length; i++) {
			rtn += (a.features[i] - b.features[i])
					* (a.features[i] - b.features[i]);
		}
		return Math.sqrt(rtn);
	}
	private class KmeansUser {
		public double[] features;
		public double[] distance;
		public String name;
		public int cluster;
		public int last_cluster;

		public KmeansUser(String name, int noFeatures, int noClusters) {
			this.name = name;
			this.features = new double[no_features];
			this.distance = new double[no_clusters];
			this.cluster = -1;
			this.last_cluster = -2;
		}
		//make kmeans user with normal user
		public KmeansUser(User u) {
			this.name = u.getUsername();
			
			this.features = new double[no_features];
			this.distance = new double[no_clusters];
			this.cluster = -1;
			this.last_cluster = -2;
			int count = u.getNumVisited();
			//System.out.println("Wallack"+(u.wallack+0.0)/count+"##############################");
			features[0]=(u.wallack+0.0)/count;
			features[1]=(u.deserres+0.0)/count;
			features[2]=(u.aboveground+0.0)/count;
			features[3]=(u.artshack+0.0)/count;
			features[4]=(u.jerrys+0.0)/count;
			features[5]=(u.oneTwenty+0.0)/count;
			features[6]=(u.twentyFifty+0.0)/count;
			features[7]=(u.fiftyHunderd+0.0)/count;
			features[8]=(u.hundredThree+0.0)/count;
			features[9]=(u.overThree+0.0)/count;
			
		}

		// Check if cluster association has changed.
		public boolean changed() {
			return last_cluster != cluster;
		}
		
		// Update the saved cluster from iteration to iteration
		public void update() {
			last_cluster = cluster;
		}

		public String toString() {
			StringBuffer b = new StringBuffer(name);
			for (int i = 0; i < features.length; i++) {
				b.append(' ');
				b.append(features[i]);
			}
			return b.toString();
		}
	}

}

