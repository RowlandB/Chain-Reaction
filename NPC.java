import java.util.ArrayList;
import java.util.HashMap;


class Body_Guard extends NPC
{
	Body_Guard(Location Start, String their_name, Person protect_target)
	{
		super(Start, their_name);
		mood = Mood.ambivalent;
		attack_power = 10;
		defense_power = 10;
		
		my_protect_target = protect_target;
	}
	
	@Override
	protected String get_personal_greeting()
	{
		return ("I do what " + my_protect_target.get_name() + " tells me.");
	}
	
	@Override
	protected counter make_new_counter()
	{
		return new Attack();
	}
	
	@Override
	public void react_to_oppressing_peasants(Person who)
	{
		alter_liking(who, 1);
	}
	
	@Override
	protected support make_new_support()
	{
		return new direct_hate();
	}
	
	//TODO targeted actions?
	class Attack extends counter
	{
		Attack()
		{
			description = "attacks";
		}
		
		@Override
		public void What_Happens()
		{
			//hate him a bit more 
			//this is to avoid strange things like searching him out, then running off and doing something else
			
			//attack that guy
			if(getCurrent_location().equals(target.getCurrent_location()))
			{
				Fight(target);
			}
			else
			{
				Search_For(target);
				alter_liking(target, -2);
			}
		}
	}
	
	class direct_hate extends support
	{
		public direct_hate()
		{
			description = "thinks about all the reasons he hates the guys hated by";
		}
		
		@Override
		public void What_Happens()
		{
			//pick a person that guy hates
			//note: we don't choose a person they like because they might [uuuuhhh... what was I going to say?]
			Person hated_one = target.get_hate();
			
			//if a body_guard is supporting a PC, they can't return someone they hate. So do nothing in that case.
			if(hated_one!=null)
			{
				//hate them even more
				alter_liking(target, -1);
			}
		}
	}
	
	private Person my_protect_target;
}

class Commoner extends NPC
{
	Commoner(Location Start, String their_name)
	{
		super(Start, their_name);
		mood = Mood.angry;
		
		potential_actions.put("go home", new go_home());
		potential_actions.put("go to work", new go_to_work());
	}
	
	@Override
	public void react_to_oppressing_peasants(Person who)
	{
		alter_liking(who, -5);
	}
	
	@Override
	protected counter make_new_counter()
	{
		return new Kvetch();
	}
	
	@Override
	protected support make_new_support()
	{
		// TODO Auto-generated method stub
		return (new awoooga());
	}
	
	class awoooga extends support
	{
		awoooga()
		{
			description = "makes train noises with";
		}
		
		@Override
		public void What_Happens()
		{
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class Kvetch extends counter
	{
		public Kvetch()
		{
			description = "Kvetches about";
		}
		
		@Override
		public void What_Happens()
		{
			// TODO Auto-generated method stub
			
			Location where = this.by_whom.getCurrent_location();
			for(Person who : where.GetEveryone().values())
			{
				if(who.likes(this.by_whom,5))
				{
					who.alter_liking(target, 1);
				}
			}
		}
	}

	class go_home extends Action implements NPC_Action
	{
		go_home()
		{
			super(Commoner.this);
			description = " comes home";
		}
		
		public void What_Happens()
		{
			if(helpers.get_PC().getCurrent_location().equals(current_location))
			{
				helpers.output(name + " goes home");				
			}
			
			MoveTo(Get_Home());
		}
		
		public int how_likely()
		{
			if(current_location.equals(home))
			{
				return -10000;
			}
			
			int t = helpers.Get_Time();
			if(t > 6 && t < 18)
			{
				return (-1 * 30);
			}
			else
			{
				return 30;
			}
		}
	}
	
	class go_to_work extends Action implements NPC_Action
	{
		go_to_work()
		{
			super(Commoner.this);
			description = " comes to work";
		}
		
		public void What_Happens()
		{
			if(helpers.get_PC().getCurrent_location().equals(current_location))
			{
				helpers.output(name + " goes to work");			
			}
			
			MoveTo(work);
		}
		
		public int how_likely()
		{
			if(current_location.equals(work))
			{
				return -10000;
			}
			
			int t = helpers.Get_Time();
			if(t < 6 || t > 18)
			{
				return (-1 * 30);
			}
			else
			{
				return 30;
			}
		}
	}
}

class Noble extends NPC
{
	Noble(Location Start, String their_name)
	{
		super(Start, their_name);
		mood = Mood.ambivalent;
	}
	
	@Override
	public void react_to_oppressing_peasants(Person who)
	{
		alter_liking(who, 3);
	}
	
	@Override
	protected counter make_new_counter()
	{
		return new call_guards();
	}
	
	@Override
	protected support make_new_support()
	{
		return new give_gold();
	}
	
	class give_gold extends support
	{
		give_gold()
		{
			description = "gives gold to";
		}
		
		@Override
		public boolean can_be_done()
		{
			return by_whom.has_item("gold");
		}
		
		@Override
		public void What_Happens()
		{
			//give the dude some money
			Give(target, "gold", 20);
		}
	}
	
	class call_guards extends counter
	{
		public call_guards()
		{
			description = "calls the guards on";
		}
		
		@Override
		public void What_Happens()
		{
			//find a guard
			NPC Guard = find_guard_here();
			
			if(Guard!=null)
			{
				//tell him to off that guy
				Guard.alter_liking(target, -10);
			}
			else
			{
				Search_For(Body_Guard.class);
			}
		}
	}

	private NPC find_guard_here()
	{
		Location here = getCurrent_location();
		for(NPC person : here.GetEveryone().values())
		{
			if(person instanceof NPC)
			{
				return person;
			}
		}
		
		return null;
	}
}


abstract class  NPC extends Person
{
	
	NPC(Location L, String S)
	{
		current_location = L;
		home = L;
		work = L;
		name = S;

		add_fact(new boring_fact("done","I have nothing else interesting to talk about"));
		
		potential_actions = new HashMap<String, NPC_Action>();
		potential_actions.put("nothing", new does_nothing());
		potential_actions.put("flee", new Flee());
		potential_actions.put("support", make_new_support());
		potential_actions.put("counter", make_new_counter());
		
		location_knowledge = new mobility_controller();
		recent_damage = 0;
		
		character_inventory.add_item(new gold(NPC.this, 3));
		
		attack_power = 5;
		
		Person_Likability = new HashMap<Person, Integer>(0);
		
	}
	
	abstract protected support make_new_support();
	abstract protected counter make_new_counter();
	
	public void set_personal_greeting(String new_greeting)
	{
		personal_greeting = new_greeting;
	}
	
	public void set_mood(Mood new_mood)
	{
		mood = new_mood;
	}
	
	public void set_home(Location new_home)
	{
		home = new_home;
	}
	
	public void set_work(Location new_work)
	{
		work = new_work;
	}
	
	@Override
	public boolean likes(Person liked_person, int how_much)
	{
		int initial_liking = 0;
		if(this.Person_Likability.containsKey(liked_person))
		{
			initial_liking = this.Person_Likability.get(liked_person);
		}
		
		int random  = helpers.grandom(-5, 5);
		return (initial_liking + random >= how_much);
	}	
	
	//Default Action used by Main
	public void Act()
	{
		//'decide' on an action
			//for now, always do nothing
		NPC_Action the_action = find_best_action();

		//do the action
		the_action.What_Happens();
		
		//check if the player is in the same location
			//notify player
		if(helpers.get_PC().getCurrent_location().Where() == this.current_location.Where())
		{
			helpers.output(name + " " + the_action.Get_Description());
		}
	}
	
	public void Chat_with_PC()
	{
		Chat_Greeting();
		
		boolean stay = true;
		boolean still_talking = true;
		
		while(stay && still_talking)
		{
			ArrayList<Fact> sharable_facts = new ArrayList<Fact>(0);
			int y = 1;
			for(Fact intel : my_knowledge.get_facts())
			{
				if(!helpers.get_PC().knows_fact(intel.Get_id()))
				{
					//TODO: check if the NPC would share this
			//		if(intel.is_sharable())
			//		{
						sharable_facts.add(intel);
						helpers.output_partial_list(y, intel.Get_Description());
						y++;
			//		}	
				}				
			}
			helpers.finish_output();
			
			int answer = helpers.which_one(y);
			
			if(sharable_facts.get(answer).Get_Description()=="done")
			{
				still_talking = false;
			}
			else
			{
				
				helpers.output(sharable_facts.get(answer).Get_Fact());
				helpers.get_PC().add_fact(sharable_facts.get(answer));
			}
		}
		//TODO maybe do something depending on how character feels about him
		
	}
	
	public void add_action(NPC_Action A)
	{
		potential_actions.put(A.Get_Description(), A);
	}
	
	public void Fight(Person opponent)
	{
		int me_hurt = opponent.get_attack_power() - this.get_defense_power();
		int him_hurt = this.get_attack_power() - opponent.get_defense_power();
		
		this.injure(me_hurt);
		opponent.injure(him_hurt);
		
		if(me_hurt < him_hurt)
		{
			this.getCurrent_location().notify_fight(this, opponent);
		}
		else
		{
			this.getCurrent_location().notify_fight(opponent, this);
		}
	}
	
	public int get_attack_power()
	{
		return attack_power;
	}
	
	public int get_defense_power()
	{
		return defense_power;
	}
	
	//Non-Terminating, Giving Simple Action
	//Tells everyone in the location a piece of knowledge
	public void Gossip(Fact knowledge)
	{
		HashMap<String, NPC> everyone = current_location.GetEveryone();
		for(int x = 0; x < everyone.size(); x++)
		{
			everyone.get(x).add_fact(knowledge);
		}
	}
	
//	public void increase_friendliness(Person who, int how_much)
//	{
//		int initial = 0;
//		if(this.Person_Likability.containsKey(who))
//		{
//			initial = this.Person_Likability.get(who);
//			this.Person_Likability.remove(who);
//		}
//		this.Person_Likability.put(who, (initial + how_much));
//	}
	
	
	
	
	@Override
	public void injure(int x)
	{
		
		current_hp = current_hp - x;
		recent_damage = recent_damage + x;
		
		if(current_hp < 0)
		{
			die();
		}
	}
	
	//Terminating Simple Action
	//Moves the character to a new location
	public void MoveTo(Location new_place)
	{
		current_location.RemovePerson(this);
		current_location = new_place;
		new_place.AddPerson(this);
	}
	
	public void time_passes()
	{
		recent_damage--;
	}
	
	Location Get_Home()
	{
		return home;
	}
	
	@Override
	public Person get_hate()
	{
		int hatingmost = -100;
		Person dude = null;
		for(Person people : helpers.NPC_List.values())
		{
			if(Person_Likability.containsKey(people))
			{
				int x = Person_Likability.get(people)*(-1);
				
				if(!this.getCurrent_location().equals(people.getCurrent_location()))
				{
					x = x-5;
				}
				
				if(x > hatingmost)
				{
					hatingmost = x;
					dude = people;
				}
			}
		}
		
		return dude;
	}
	
	protected void Search_For(Class<? extends NPC> NPC_type)
	{
		//TODO this will search for a *type* of person, as opposed to a specific person
		Search_For((NPC) null);
	}
	
	protected void Search_For(Person target)
	{
		// TODO make this better
		
		//go to a random place that isn't here
		Object[] values = helpers.Location_List.values().toArray();
		Location new_location = (Location) values[helpers.random(0,values.length-1)];
		
		while(new_location.equals(current_location))
		{
			new_location = (Location) values[helpers.random(0,values.length-1)];
		}
		
		if(helpers.get_PC().getCurrent_location().equals(current_location))
		{
			helpers.output(name + " leaves in search of " + target.get_name());				
		}
		MoveTo(new_location);
	}

	private void Chat_Greeting()
	{
		if(get_likeability(helpers.get_PC()) == 0)
		{
			helpers.output("Hello, Stranger.");	
		}
		else if(get_likeability(helpers.get_PC()) < -10)
		{
			helpers.output("You've got some nerve, showing your face around here.");	
		}
		else if(get_likeability(helpers.get_PC()) > 10)
		{
			helpers.output("Hello, my good friend.");	
		}
		else if(get_likeability(helpers.get_PC()) > 0)
		{
			helpers.output("Hello, friend.");
		}
		else if(get_likeability(helpers.get_PC()) < 0)
		{
			helpers.output("[disgruntaled silence].");	
		}
		
		helpers.output(get_personal_greeting());
		
		helpers.finish_output();
	}
	
	protected String get_personal_greeting()
	{
		return personal_greeting;
	}

	private int get_likeability(Person who)
	{
		if(Person_Likability.containsKey(who))
		{
			return Person_Likability.get(who);			
		}
		else
		{
			Person_Likability.put(who, 0);
			return 0;
		}
	}
	
	private NPC_Action find_best_action() 
	{
		NPC_Action the_one =  null;
		int action_value = -1000;
		for(NPC_Action this_action : potential_actions.values())
		{
			if(this_action.can_be_done())
			{
				int x = this_action.how_likely() + helpers.grandom(-50, 50);
				if(x > action_value)
				{
					action_value = x;
					the_one = this_action;
				}
			}
		}
		
		return the_one;
	}
	
	protected void die()
	{
		// TODO Auto-generated method stub
		System.err.println(this.get_name() + " has died in " + this.getCurrent_location());
		
	}
	
	abstract class counter extends Action implements NPC_Action
	{
		counter(){super(NPC.this);}
		
		public void setTarget(Person new_target)
		{
			this.target = new_target;
		}

		public String Get_Description()
		{
			return (this.description + " " + this.target.get_name());
		}
		
		public int how_likely()
		{
			int hatingmost = -1000;
			for(Person people : Person_Likability.keySet())
			{
				int x = Person_Likability.get(people)*(-1);
					
				if(!getCurrent_location().equals(people.getCurrent_location()))
				{
					x = x-20;
				}
					
				if(x > hatingmost)
				{
					hatingmost = x;
					setTarget(people);
				}
				
			}
			
			return hatingmost;
		}
		
		protected Person target;
	}
	
	abstract class support extends Action implements NPC_Action
	{
		support(){super(NPC.this);}
		
		public void setTarget(Person new_target)
		{
			this.target = new_target;
			
		}

		public String Get_Description()
		{
			return (this.description + " " + this.target.get_name());
		}
		
		public int how_likely()
		{
			int helpingmost = -1000;
			for(Person people : Person_Likability.keySet())
			{
				int x = Person_Likability.get(people);
					
				if(!getCurrent_location().equals(people.getCurrent_location()))
				{
					x = x-20;
				}
					
				if(x > helpingmost)
				{
					helpingmost = x;
						
					setTarget(people);
				}
			
			}
			
			return helpingmost;
		}
		
		Person target;
	}
	
	class does_nothing extends Action implements NPC_Action
	{
		does_nothing()
		{
			super(NPC.this);
			description =  "does nothing";
		}
		
		public void What_Happens()
		{
			
		}

		@Override
		public int how_likely()
		{
			return 1;
		}
	}
	
	class Flee extends Action implements NPC_Action
	{
		public Flee()
		{
			super(NPC.this);
			description = "runs away";
		}
		
		@Override
		public void What_Happens()
		{
			Object[] values = helpers.Location_List.values().toArray();
			Location new_location = (Location) values[helpers.random(0,values.length-1)];
			
			while(new_location.equals(current_location))
			{
				new_location = (Location) values[helpers.random(0,values.length-1)];
			}
			
			if(helpers.get_PC().getCurrent_location().equals(current_location))
			{
				helpers.output(name + " runs away");				
			}
			MoveTo(new_location);
		}
		
		@Override
		public int how_likely()
		{
			if(mood.equals(Mood.angry))
			{
				return (recent_damage - 60);
			}
			return (recent_damage - 55);
		}
		
	}
	
	@Override
	public void add_fact(Fact new_fact)
	{
		my_knowledge.add_Fact(new_fact);
		new_fact.on_learn(this);
	}
	
	
	@Override
	final public void alter_liking(Person target, int how_much)
	{
		int initial_liking = 0;
		if(Person_Likability.containsKey(target))
		{
			initial_liking = Person_Likability.get(target);
			Person_Likability.remove(target);
		}
		
		Person_Likability.put(target, (initial_liking + how_much));
	}

	/////////////////////////////

	
	private int recent_damage;
	protected Location home;
	protected String personal_greeting;
	protected Mood mood;
	protected Location work;
	protected HashMap<String, NPC_Action> potential_actions;
	//protected HashMap<String, item> NPC_items;
	protected HashMap<Person, Integer> Person_Likability;
}

