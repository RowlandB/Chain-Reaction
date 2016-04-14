import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Scanner;

public class Main
{
	static Vector<Location> places = new Vector<Location>(0);
	static Vector<Person> NPCs = new Vector<Person> (0);
	
	
	public static void main(String [ ] args)
	{	
		/////Locations	
		Location SL = new Start_Location();
		Location Blacksmith = new Forge("Blacksmith's");
		Location Bobs_Fields = new Field("Bob's Field");
		Location Keshies_Castle = new Castle("Keshie's Castle");
		places.add(Blacksmith);
		places.add(Bobs_Fields);
		places.add(Keshies_Castle);
		
		
		/////Player
		Player_Character The_Player = new Player_Character(SL);
		

		
		////Persons
//		Person Generic_Peasant = new Commoner(Bobs_Fields);
//		Bobs_Fields.AddPerson(Generic_Peasant);
//		NPCs.add(Generic_Peasant);
		
//		Person Generic_Noble = new Noble(Keshies_Castle);
//		Keshies_Castle.AddPerson(Generic_Noble);
//		NPCs.add(Generic_Noble);
		
		
		//TODO: initialize all the NPCs
		
		while(true)
		{
			The_Player.InteractWithEnvironment();
			for(int x = 0; x < NPCs.size(); x++)
				NPCs.get(x).Act();
		}
		
	}
}

///////////////////////////////
class Player_Character
{
	
	public  Player_Character(Location Start)
	{
		IO = new System_IO_Object();
		
		this.what_do = new Vector<Action>(0);			//TODO: set an actual length

		what_do.add(new LookAroundtheRoom());
		what_do.add(new Move());
		
		//TODO: initilize each Action
		
		//view inventory
		//use item in inventory
		
		
		this.here = Start;
		this.current_hp = 100;
		this.total_hp = 100;
	}
	
	
	
	//displays at all the actions the player can do
	public void InteractWithEnvironment()
	{
		int num_player_actions = what_do.size();
		for(int x = 0; x < num_player_actions; x++)
		{
			int y = x+1;
			String output = y +") " + what_do.elementAt(x).description;
			IO.Output_String(output);
		}
		
		Vector<Action> other_options = here.Get_Actions();
		
		for(int z = 0; z < other_options.size(); z++)
		{
			int y = z+1+what_do.size();
			String output = y +") " + other_options.elementAt(z).description;
			IO.Output_String(output);
		}
		
		int answer = helpers.which_one(num_player_actions + other_options.size());
		
		if(answer < num_player_actions)
		{
			Action_Flag_Test(this.what_do.get(answer));
		}
		else
		{
			Action_Flag_Test(here.Get_Actions().get(answer-num_player_actions));
		}
		
	}
	
	
	
	class LookAroundtheRoom extends Action
	{
		LookAroundtheRoom()
		{
			description = "Check out who's near";
		}
		
		public void What_Happens()
		{
			String people;
			LinkedList<Person> everyone =  here.GetEveryone();
			if(everyone.size() == 0)
			{
				people = "There's no one here but you!";
				IO.Output_String(people);
			}
			else
			{
				IO.Output_String("Present is:");
				for(int x = 0; x <everyone.size(); x++)
				{
					IO.Output_String(everyone.get(x).Get_Name());
				}
			}

		}		
	}
	
	class Move extends Action
	{
		Move()
		{
			description = "Go someplace else";
		}
		
		public void What_Happens()
		{
			IO.Output_String("Where?");
			
			for(int x = 0; x<Main.places.size(); x++)
			{
				int y = x+1;
				String output = y + ") " + Main.places.get(x).Where();
				IO.Output_String(output);
			}
			
			int answer = helpers.which_one(Main.places.size());
			
			Move_Character(Main.places.get(answer));
			
		}
	}
	
	class Chat extends Action
	{
		Chat()
		{
			description = "talk to someone near you";
		}
		
		public void What_Happens()
		{
			String people;
			LinkedList<Person> everyone =  here.GetEveryone();
			if(everyone.size() == 0)
			{
				people = "There's no one to talk to! (unless you want to talk to yourself)";
				IO.Output_String(people);
			}
			else
			{
				IO.Output_String("With whom would you like to speak ?");
				for(int x = 0; x <everyone.size(); x++)
				{
					int y = x +1;
					String output = Integer.toString(y) + ") " + everyone.get(x).Get_Name();
					IO.Output_String(output);
				}
				int whom = Integer.parseInt(IO.Input_String());
				
				everyone.get(whom-1).Chat_with_PC();
			}
		}
	}
	
	class DoNothing extends Action
	{
		DoNothing()
		{
			description = "wait for things to happen";
		}
		
		public void What_Happens()
		{}
	}
	
	
	//////
	
	
	//tests all the possible things that might stop the player from doing what he wants
	//if he can't do what he wants, ask for another action. Nothing will have changed in the meantime, so it should be the same list as before
	private void Action_Flag_Test(Action potential)
	{
		if(potential.is_strenuous)
		{
			if(Player_is_Weak(potential))
			{
				IO.Output_String("It looks like you're too weak to " + potential.description);
				this.InteractWithEnvironment();
			}
		}
		else
		{
			potential.What_Happens();
		}
		
	}
	
	private boolean Player_is_Weak(Action stren)
	{
		return (current_hp < total_hp/2);
	}
	
	private void Move_Character(Location where_to)
	{
		here = where_to;
	}
	
//////////////////////////////////////
	class inventory
	{
		inventory() //TODO: finish Inventory and Item. Also the implementation of them.
		{
			the_items = new ArrayList(0);
			total_weight = 0;
			IO = new System_IO_Object();
		}
		
		public void Display_Inventory()
		{
			for(int x=0; x < the_items.size(); x++)
			{
				the_items.get(x).Display();
			}
			
			IO.Output_String("Total Weight: " + total_weight);
		}
		
		public void add_item(item_helper to_be_added)
		{
			the_items.add(to_be_added);
			total_weight += to_be_added.get_weight();
		}
		
		public void remove_1_item(int which)
		{
			
			
		}
		
		public void remove_all_of_item(int which)
		{
			total_weight -= the_items.get(which).get_weight();
			the_items.remove(which);		
		}
		
		private equippable_item empty_slot;
		private IO_Object IO;
		private int total_weight;
		private ArrayList<item_helper> the_items;
	}

	
	
//////////////////////////////////////	
	
	private inventory my_inventory;
	private IO_Object IO;
	private Vector<Action> what_do;
	private Location here;
	private int total_hp;
	private int current_hp;
}

///////////////////////////////
class Fact
{
	Fact(String descr, String hf)
	{
		basic_description = descr;
		hard_facts = hf;
		IO = new System_IO_Object();
		my_id = unique_id;
		unique_id++;
	}
	
	public String Get_Description()
	{
		return basic_description;
	}
	
	public String Get_Fact()
	{
		return hard_facts;
	}
	
	public int Get_id()
	{
		return my_id;
	}

	public void display_fact()
	{
		IO.Output_String(basic_description);
		IO.Output_String(hard_facts);
		IO.Output_String(Integer.toString(my_id));
	}
	
	private String basic_description;
	private String hard_facts;
	private  IO_Object IO;
	private int my_id;
	
	private static int unique_id = 1;
}

///////////////////////////////
abstract class Action
{	
	Action()
	{
		IO = new System_IO_Object();
		is_strenuous = false;
	}
	
	public void What_Happens(){}
	public String description;
	IO_Object IO;
	public boolean is_strenuous;
}



///////////////////////////////
abstract class  Person
{
	public  Person()
	{
		name = "Mysterious Stranger with no distinguishing characteristics";
		IO = new System_IO_Object();
	}
	
	public String Get_Name()
	{
		return name;
	}
	
	//Terminating Simple Action
	//Moves the character to a new location
	public void MoveTo(Location new_place)
	{
		place.RemovePerson(this);
		place = new_place;
		new_place.AddPerson(this);
	}
	
	/**Non-Terminating Simple Action
	 * fights another Person*/
	public void Fight(Person opponent)
	{
		int difference = this.fight_power - opponent.fight_power;
		if(difference >0)
		{
			opponent.LoseFight(difference);
		}
		else
		{
			this.LoseFight((difference * -1));
		}
	}
	
	//Non-Terminating, Giving Simple Action
	//Tells everyone in the location a piece of knowledge
	public void Gossip(Fact knowledge)
	{
		LinkedList<Person> everyone =  place.GetEveryone();
		for(int x = 0; x < everyone.size(); x++)
		{
			everyone.get(x).GainInformation(knowledge);
		}
	}
	
	public void Chat_with_PC()
	{
		Chat_Greeting();
		
		boolean stay = true;
		while(stay)
		for(int x = 0; x < knowledge_base.size(); x++)
		{
			int y = x +1;
			String output = Integer.toString(y) + ") " + knowledge_base.get(x).Get_Description();
			IO.Output_String(output);
		}
	
		int answer = helpers.which_one(knowledge_base.size());
		
		//TODO maybe do something depending on how character feels about him
		
	}
	
	private void Chat_Greeting()
	{
		if(pc_friendlyness_level == 0)
		{
			IO.Output_String("Hello, Stranger.");	
		}
		else if(pc_friendlyness_level < -10)
		{
			IO.Output_String("You've got some never, showing your face around here.");	
		}
		else if(pc_friendlyness_level > 10)
		{
			IO.Output_String("Hello, my good friend.");	
		}
		else if(pc_friendlyness_level > 0)
		{
			IO.Output_String("Hello, friend.");
		}
		else if(pc_friendlyness_level < 0)
		{
			IO.Output_String("[disgruntaled silence].");	
		}
		Personal_Greeting();
	}
	
	private void Personal_Greeting()
	{
		IO.Output_String("I know a few things...");
	}
	
	public void GainInformation(Fact knowledge)
	{
		if(!fact_already_known(knowledge))
			knowledge_base.add(knowledge);	
	}

	//Default Action used by Main
	public void Act()
	{
		
		String output = name + " does nothing interesting";
		IO.Output_String(output);
		
	}
	
	
	private void LoseFight(int how_bad)
	{
		if(how_bad > 15)
		{
			//TODO: kill the character
			
		}
		else if(how_bad > 5)
		{
			//TODO: a bit fucked up
			
		}
		else
		{
			//TODO: not much
			
		}
	}
	
	private boolean fact_already_known(Fact new_info)
	{
		int the_id = new_info.Get_id();
		for(int x = 0; x < knowledge_base.size(); x++)
		{
			if(knowledge_base.get(x).Get_id() == the_id)
				return true;
		}
		return false;
	}
	
	/////////////////////////////
	protected enum Mood
	{
	happy, sad, angry, ambivalent
	}
	
	protected Mood mood;
	protected int pc_friendlyness_level; //0 = neutral, bigger is friendlier
	protected Vector<Fact> knowledge_base;
	protected  int fight_power;
	protected String name;
	protected Location place;
	protected IO_Object IO;
}

abstract class Commoner extends Person
{
	Commoner(Location Start, String their_name)
	{
		name = their_name;
		mood = Mood.angry;
	}
	
	public void Act()
	{
		
	}

}

abstract class Noble extends Person
{
	Noble(Location Start, String their_name)
	{
		name = their_name;
		mood =Mood. ambivalent;
	}
	
	public void Act()
	{
		
	}
	
}



///////////////////////////////
abstract class Location
{
	Location()
	{
		who_is_here = new LinkedList<Person>();
		options = new Vector<Action>();
	}
	
	public Location(String name)
	{
		
		who_is_here = new LinkedList<Person>();
		options = new Vector<Action>();
		Loc_name = name;
	}
	
	//A location will never remove a person by itself: this should only be called from Person
	public void RemovePerson(Person leaver)
	{
		who_is_here.remove(leaver);		
	}
	
	public void AddPerson(Person arrival)
	{
		who_is_here.add(arrival);
	}
	
	public LinkedList<Person> GetEveryone()
	{
		return who_is_here;
	}
	
	public Vector<Action> Get_Actions()
	{
		return options;
	}
	
	public String Where()
	{
		return Loc_name;
	}
	
	private String Loc_name;
	protected Vector<Action> options;
	protected LinkedList<Person> who_is_here;
}

///////////////////////////////
class Start_Location extends Location
{



}

///////////////////////////////
class Forge extends Location
{
	public Forge(String name)
	{
		super(name);
	} 
	
}

///////////////////////////////
class Field extends Location
{
	public Field(String name)
	{
		super(name);
	}


}

///////////////////////////////
class Castle extends Location
{
	public Castle(String name)
	{
		super(name);
		Action laugh_at_plebs = new oppress_peasants();
		options.add(laugh_at_plebs);
	}

	class oppress_peasants extends Action
	{
			oppress_peasants()
			{
				description = "Oppress some peasants";
				IO = new System_IO_Object();
			}
			
			public void What_Happens()
			{
				IO.Output_String("Haha! Those fools should have been born with money. Let's make them fight for coppers some more!");
			}
		
	}

}

///////////////////////////////

class item_helper
{
	public int get_weight()
	{
		return how_many*the_actual_item.get_weight();
	}
	
	public void Display()
	{
		the_actual_item.Display();
		IO.Output_String("Number: " + how_many);
	}
	
	private int how_many;
	private item the_actual_item;
	private IO_Object IO;
}

//TODO: finish item
class item
{
	item()
	{
		
		
	}
	
	public void Display()
	{
		IO.Output_String("Name: " +name);
		IO.Output_String(description);
		IO.Output_String("Weight: " + weight + " Value: " + value + weight/value);
		IO.Output_String("Stolen: " + stolen);
	}
	
	public int get_weight()
	{
		return weight;
	}
	
	public boolean get_equipped()
	{
		return is_equipped;
	}
	
	
	private String name;
	private String description;
	private boolean is_equipped;
	private int weight;
	private int value;
	private boolean stolen;
	protected IO_Object IO;
}

//TODO: finish readable_item
class readable_item extends item
{
	
	
	private boolean read;
}

//TODO: finish equippable_item
class equippable_item extends item
{
	public void Display()
	{
		super.Display();
		IO.Output_String(slot.toString()); //I really hope this works
	}
	
	private void on_equip()
	{
		
		
	}
	
	private void on_unequip()
	{
		
		
	}
	
	protected enum equip_region
	{
		head, body, ring, amulet, one_handed, two_handed
	}
	
	private equip_region slot;
}

//TODO: finish consumable_item
class consumable_item extends item
{
	
	
	void on_use()
	{
		
	}
}


//////////////////////////////
//Exactly what it says on the tin: handles input and output
abstract class IO_Object
{
	public String Input_String(){
		return "balls. This shouldn't have been called";
	}
	
	public void Output_String(String output){}
	
}

//uses System IO
class System_IO_Object extends IO_Object
{
	System_IO_Object(){}
	
	public String Input_String()
	{
		Scanner keyboard = new Scanner(System.in);
		String the_input = keyboard.nextLine();
		return the_input;
	}
	
	public void Output_String(String output)
	{
		System.out.println(output);
	}
}

//////////////////////////////
abstract class helpers
{
	//Assumption: We've already output a question with 'size' number of choices
	//Yells at the player until they give a viable option (one from the list)
	static int which_one(int size)
	{
		IO_Object IO = new System_IO_Object();
	
		int x = Integer.parseInt(IO.Input_String());
	
		while(x > size || x < 1)
		{
			IO.Output_String("That's not a selectable option. Try again");
			x = Integer.parseInt(IO.Input_String());
		}
	
		return x - 1;
	}
}





