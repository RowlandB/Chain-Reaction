import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

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
		
		new helpers(The_Player);
		
		
		////Persons
		Person Generic_Peasant = new Bob(Bobs_Fields, "Bob");
		Bobs_Fields.AddPerson(Generic_Peasant);
		NPCs.add(Generic_Peasant);
		
		Person Generic_Noble = new Noble_Kesh(Keshies_Castle, "High Lord Kesh of No-Funnington");
		Keshies_Castle.AddPerson(Generic_Noble);
		NPCs.add(Generic_Noble);
		
		
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
		this.my_inventory = new Inventory();

		
		this.what_do = new Vector<Action>(0);

		what_do.add(new LookAroundtheRoom());
		what_do.add(new Move());
		what_do.add(new Chat());
		what_do.add(new DoNothing());
		what_do.add(new CheckInventory());
		
		//TODO: initilize each Action
		
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
			helpers.output(output);
		}
		
		Vector<Action> other_options = here.Get_Actions();
		
		for(int z = 0; z < other_options.size(); z++)
		{
			int y = z+1+what_do.size();
			String output = y +") " + other_options.elementAt(z).description;
			helpers.output(output);
		}
		helpers.finish_output();
		
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
				helpers.output(people);
				helpers.finish_output();
			}
			else
			{
				helpers.output("Present is:");
				for(int x = 0; x <everyone.size(); x++)
				{
					helpers.output(everyone.get(x).Get_Name());
				}
				helpers.finish_output();
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
			helpers.output("Where?");
			
			for(int x = 0; x<Main.places.size(); x++)
			{
				int y = x+1;
				String output = y + ") " + Main.places.get(x).Where();
				helpers.output(output);
			}
			helpers.finish_output();
			
			int answer = helpers.which_one(Main.places.size());
			
			Move_Character(Main.places.get(answer));
			
		}
	}
	
	class Chat extends Action
	{
		Chat()
		{
			description = "Talk to someone near you";
		}
		
		public void What_Happens()
		{
			String people;
			LinkedList<Person> everyone =  here.GetEveryone();
			if(everyone.size() == 0)
			{
				people = "There's no one to talk to! (unless you want to talk to yourself)";
				helpers.output(people);
				helpers.finish_output();
			}
			else
			{
				helpers.output("With whom would you like to speak ?");
				for(int x = 0; x <everyone.size(); x++)
				{
					int y = x +1;
					String output = Integer.toString(y) + ") " + everyone.get(x).Get_Name();
					helpers.output(output);
				}
				helpers.finish_output();
				
				int whom = helpers.which_one(everyone.size());
				
//				int whom = Integer.parseInt(helpers.input());
//				
//				//TODO check if this is just the WHICH function
//				
				everyone.get(whom).Chat_with_PC();
			}
		}
	}
	
	class DoNothing extends Action
	{
		DoNothing()
		{
			description = "Wait for things to happen";
		}
		
		public void What_Happens()
		{}
	}
	
	class CheckInventory extends Action
	{
		CheckInventory()
		{
			description = "Check out your items";
		}
		
		public void What_Happens()
		{
			my_inventory.Display_Inventory();
		}
	}
	
	class UseItem extends Action
	{
		UseItem()
		{
			description = "Use an item in your inventory";
		}
		
		public void What_Happens()
		{
			helpers.output("Which item would you like to use?");
			item useable_item = my_inventory.Select_Item();
			
		}
		
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
				helpers.output("It looks like you're too weak to " + potential.description);
				helpers.finish_output();
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
	
	public void gain_health(int how_much)
	{
		
	}
	
//////////////////////////////////////
	class Inventory
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Inventory() //TODO: finish Inventory and Item. Also the implementation of them.
		{
			the_items = new ArrayList(0);
			total_weight = 0;
		}
		
		public item Select_Item()
		{
			if(the_items.size()==0)
			{
				helpers.output("You have nothing. Dang, you're poor");
			}
			else
			{
				for(int x=0; x < the_items.size(); x++)
				{
					helpers.output(Integer.toString(x) + " " + the_items.get(x).get_name());
				}
			}
			helpers.finish_output();
			int which_one = helpers.which_one(the_items.size());
			
			return the_items.get(which_one);
		}
		
		public void Display_Inventory()
		{
			if(the_items.size()==0)
			{
				helpers.output("You have nothing. Dang, you're poor");
			}
			else
			{
				for(int x=0; x < the_items.size(); x++)
				{
					the_items.get(x).Display();
				}
			}
			helpers.output("Total Weight: " + total_weight);
			helpers.finish_output();
		}
		
		public void add_item(item to_be_added)
		{
			the_items.add(to_be_added);
			total_weight += to_be_added.get_weight();
		}
		
		public void remove_item(int which, int how_many)
		{
			if(the_items.get(which).check_quantity(how_many))
			{
				remove_all_of_item(which);
			}
			else
			{
				remove_some_of_item(which, how_many);
			}
			
		}
		
		private void remove_some_of_item(int which, int how_many)
		{
			total_weight -= the_items.get(which).get_weight();
			the_items.get(which).change_quantity(how_many);
		}
		
		private void remove_all_of_item(int which)
		{
			total_weight -= the_items.get(which).get_weight();
			the_items.remove(which);
		}
		
		private equippable_item empty_slot;
		private int total_weight;
		private ArrayList<item> the_items;
	}

	
	
//////////////////////////////////////	
	
	private Inventory my_inventory;
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
		helpers.output(basic_description);
		helpers.output(hard_facts);
		helpers.output(Integer.toString(my_id));
		helpers.finish_output();

	}
	
	private String basic_description;
	private String hard_facts;
	private int my_id;
	
	private static int unique_id = 1;
}

///////////////////////////////
abstract class Action
{	
	Action()
	{
		is_strenuous = false;
	}
	
	public void What_Happens(){}
	public String description;
	public boolean is_strenuous;
}



///////////////////////////////



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
			}
			
			public void What_Happens()
			{
				helpers.output("Haha! Those fools should have been born with money. Let's make them fight for coppers some more!");
				helpers.finish_output();
			}
		
	}

}

///////////////////////////////

/*class item_helper
{
	public int get_weight()
	{
		return how_many*the_actual_item.get_weight();
	}
	
	public void Display()
	{
		the_actual_item.Display();
		helpers.output("Number: " + how_many);
		helpers.finish_output();
	}
	
//	public String Get_Name()
//	{
//		return the_actual_item.get_name();
//	}
	
	public boolean removeOne()
	{
		if(how_many > 1)
		{
			how_many = how_many-1;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public void QuickDisplay()
	{
		helpers.output(the_actual_item.get_name() + "    " + Integer.toString(how_many));
	}
	
	private int how_many;
	public item the_actual_item;
}
*/


//TODO: finish item
class item
{
	item()
	{
		
		
	}
	
	public void Display()
	{
		helpers.output("Name: " +name);
		helpers.output(description);
		helpers.output("Weight: " + weight + " Value: " + value + weight/value);
		helpers.output("Stolen: " + stolen);
		helpers.output("Count: " + quantity);
		helpers.finish_output();
	}
	
	public int get_weight()
	{
		return weight;
	}
	
	public boolean get_equipped()
	{
		return is_equipped;
	}
	
	public String get_name()
	{
		return name;
	}
	
	public boolean has_action()
	{
		return has_action;
	}
	
	public int get_count()
	{
		return quantity;
	}
	
	public boolean check_quantity(int check_number)
	{
		return (check_number <= this.quantity);
	}
	
	
	/**
	 * a check should be done that this does not set to negative. I will error you if you do.
	 * same with going to 0. Delete the item if you're going to 0 of them.
	 * 
	 * @param how_many to add
	 * if negative, removes them
	 */
	public void change_quantity(int how_many) 
	{
		assert(this.quantity + how_many <= 0);
		
		quantity = quantity + how_many;
	}
	
	protected String name;
	protected String description;
	private boolean is_equipped;
	protected int weight;
	protected int value;
	protected boolean stolen;
	protected boolean has_action;
	protected int quantity;
}

//TODO: finish readable_item
class readable_item extends item
{
	
	public void Display()
	{
		helpers.output("Name: " + name);
		helpers.output(description);
		helpers.output("Weight: " + weight + " Value: " + value + weight/value);
		helpers.output("Stolen: " + stolen);
		helpers.output("Count: " + quantity);
		helpers.output("Read: " + read);
		helpers.finish_output();
	}
	
	public void Read()
	{
		helpers.output(text);
		helpers.finish_output();
		
		read = true;
	}
	
	private String text;
	private boolean read;
}

//TODO: finish equippable_item
class equippable_item extends item
{
	public void Display()
	{
		super.Display();
		helpers.output(slot.toString()); //I really hope this works
		helpers.finish_output();
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

////TODO: finish consumable_item
//interface consumable_item
//{
//	class on_use extends Action{};
//	
//	on_use ability = new on_use();
//}
//
//class health_potion extends item implements consumable_item
//{
//	health_potion()
//	{
//		ability = new Gain_HP_hp_pot();
//		
//	}
//	
//	class Gain_HP_hp_pot extends on_use
//	{
//		Gain_HP_hp_pot()
//		{
//			description = "Drink a Potion to restore health";
//		}
//		
//		public void What_Happens()
//		{
//			helpers.PC.gain_health(5);
//		}
//	}
//
//}

//////////////////////////////
class helpers
{
	helpers(Player_Character hero)
	{
		PC = hero;
		IO = new Frame_IO_Object();
	}
	
	//Assumption: We've already output a question with 'size' number of choices
	//Yells at the player until they give a viable option (one from the list)
	static int which_one(int size)
	{
		int x = Integer.parseInt(IO.Input_String());
	
		while(x > size || x < 1)
		{
			helpers.output("That's not a selectable option. Try again");
			helpers.finish_output();
			x = Integer.parseInt(IO.Input_String());
		}
	
		return x - 1;
	}
	
	static String input()
	{
		return IO.Input_String();
	}
	
	static void output(String the_output)
	{
		IO.Output_String(the_output);
	}
	
	static void finish_output()
	{
		IO.Output_Batch();
	}
	
	static Player_Character get_PC()
	{
		return PC;
	}
	
	static Player_Character PC;
	static IO_Object IO;
}





