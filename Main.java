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
		Location Dungeon = new Dungeon("Dungeon Below Keshie's Castle");
		places.add(Blacksmith);
		places.add(Bobs_Fields);
		places.add(Keshies_Castle);
		places.add(Dungeon);
				
		/////Player
		Player_Character The_Player = new Player_Character(SL, "Jon Snow");
		
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
	
	public  Player_Character(Location Start, String my_name)
	{
		this.my_inventory = new Inventory();

		my_inventory.add_item(new readable_item("mysterious note", "The stones are growing restless. Beware the Great God Jamie Ter"));
		my_inventory.add_item(new item());
		my_inventory.add_item(new health_potion());
		my_inventory.add_item(new lazy_equip(equip_region.one_handed));
		my_inventory.add_item(new lazy_equip(equip_region.one_handed));
		my_inventory.add_item(new lazy_equip(equip_region.two_handed));
		
		this.my_knowledge = new Knowledge();
		
		this.what_do = new Vector<Action>(0);

		what_do.add(new LookAroundtheRoom());
		what_do.add(new Move());
		what_do.add(new Chat());
		//what_do.add(new ExamineInventory());
		what_do.add(new Ruminate());
		what_do.add(new DoNothing());
		
		//TODO: initilize each Action
		
		//use item in inventory
		
		this.mobility = new ArrayList<mobility_score>();
		
		this.name = my_name;
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
			
			int y=1;
			ArrayList<Location> potential_places = new ArrayList<Location>();
			for(int x = 0; x<Main.places.size(); x++)
			{
				Location place = Main.places.get(x);
				
				
				if(place.can_individual_visit(get_score(place.Where())))
				{
					potential_places.add(place);
					String output = y + ") " + place.Where();
					helpers.output(output);
					y++;
				}
			}
			helpers.finish_output();
			
			int answer = helpers.which_one(potential_places.size());
			
			Move_Character(potential_places.get(answer));
			
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
	
	class ExamineInventory extends Action
	{
		ExamineInventory()
		{
			description = "Check out your items";
		}
		
		public void What_Happens()
		{
			int answer = 0;
			//options
			while(answer != 4)
			{
				helpers.output("1) View Inventory");
				helpers.output("2) Use an Item");
				helpers.output("3) Equip an Item");
				helpers.output("4) Done");
				helpers.finish_output();
			
				answer = helpers.which_one(4) + 1;

				if(answer == 1)
				{
					my_inventory.Display_Inventory();					
				}
				else if(answer == 2)
				{
					helpers.output("Which item would you like to use?");
					
					if(!my_inventory.isEmpty())
					{
						item useable_item = my_inventory.Select_Item();
						
						useable_item.get_abiltiy().What_Happens();
						
					}
					else
					{
						helpers.output("You have nothing. Dang, you're poor");
					}
				}
				else if(answer == 3)
				{
					my_inventory.Equip_Item();
				}
			}
			
			
			
		}
	}
	
	class Ruminate extends Action
	{
		Ruminate()
		{
			description = "View previously discovered facts";
		}
		
		public void What_Happens()
		{
			my_knowledge.ruminate();
		}
	}
	
	class Equip_Items extends Action
	{
		Equip_Items()
		{
			description = "Equip Items";
		}
		
		public void What_Happens()
		{
			int the_item = my_inventory.Select_Item_Location();
			my_inventory.equip_item(the_item);
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
	
	public void learn_about_location(String location_name, int how_much)
	{
		for(int x=0; x<mobility.size(); x++)
		{
			if(mobility.get(x).name == location_name)
			{
				mobility.get(x).score = mobility.get(x).score + how_much;
				return;
			}
		}
		
		//we didn't find it
		mobility.add(new mobility_score(location_name, how_much+1));
	}
	
	public void gain_health(int how_much)
	{
		
	}
	
	public void add_fact(Fact new_fact)
	{
		my_knowledge.add_Fact(new_fact);
		
		new_fact.on_learn();
	}
	
	public void decrement_consumable(item consume)
	{
		my_inventory.remove_item(consume, 1);
	}
	
//////////////////////////////////////
	class Inventory
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Inventory() //TODO: finish Inventory and Item. Also the implementation of them.
		{
			the_items = new ArrayList(0);
			total_weight = 0;
			
			head = new item_location(equip_region.head, -1);        
			body = new item_location(equip_region.body, -1);        
			ring1 = new item_location(equip_region.ring, -1);       
			ring2 = new item_location(equip_region.ring, -1);       
			amulet = new item_location(equip_region.amulet, -1);      
			one_handed1 = new item_location(equip_region.one_handed, -1); 
			one_handed2 = new item_location(equip_region.one_handed, -1); 
			two_handed = new item_location(equip_region.two_handed, -1);
		}
		
		public void Equip_Item()
		{	
			helpers.output("Which item would you like to equip?");
			
			if(!my_inventory.isEmpty())
			{
				int useable_item = my_inventory.Select_Item_Location();
				
				my_inventory.equip_item(useable_item);
			}
			else
			{
				helpers.output("You have nothing. Dang, you're poor");
			}
		}

		public boolean isEmpty()
		{
			if(the_items.size()==0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		private item Select_Item()
		{
			for(int x=1; x <= the_items.size(); x++)
			{
				helpers.output(Integer.toString(x) + ") " + the_items.get(x-1).get_name());
			}
		
			helpers.finish_output();

			int which_one = helpers.which_one(the_items.size());
			
			return the_items.get(which_one);
		}
		
		private int Select_Item_Location()
		{
			for(int x=1; x <= the_items.size(); x++)
			{
				helpers.output(Integer.toString(x) + ") " + the_items.get(x-1).get_name());
			}
		
			helpers.finish_output();

			int which_one = helpers.which_one(the_items.size());
			
			return which_one;
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
					helpers.output("=============");
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
			if(the_items.get(which).have_more_than(how_many))
			{
				remove_some_of_item(which, how_many);
				
			}
			else
			{
				remove_all_of_item(which);
			}
		}
		
		/**
		 * note that this simply calls the overloaded int,int version. Call that if possible
		 * 
		 * @param which
		 * @param how_many
		 */
		public void remove_item(item which, int how_many)
		{
			for(int x=0; x<the_items.size(); x++)
			{
				if(the_items.get(x).equals(which))
				{
					remove_item(x,how_many);
					break;
				}
			}
		}
		
		private void remove_some_of_item(int which, int how_many)
		{
			how_many = how_many*(-1);
			total_weight -= the_items.get(which).get_weight();
			the_items.get(which).change_quantity(how_many);
		}
		
		private void remove_all_of_item(int which)
		{
			total_weight -= the_items.get(which).get_weight();
			the_items.remove(which);
		}
		
		//please don't open this. I'm embarrased. It's spaghetti code. And it hurts.
		private void equip_item(int which)
		{	
			item new_item = the_items.get(which);
			equip_region slot = new_item.get_slot();
						
			if(slot==equip_region.not_equippable)
			{
				helpers.output("uh, you can't equip that");
				helpers.finish_output();
			}
			else
			{
				//check if its slot has something in it
					//unequip it
				
				//call the on_equip function
				equippable_item the_one = (equippable_item) the_items.get(which);
				
				if(slot == equip_region.head)
				{
					if(head.iventory_location >= 0)
					{
						unequip_item(head);
					}
					head.iventory_location = which;
				}
				else if(slot == equip_region.body)
				{
					if(body.iventory_location >= 0)
					{
						unequip_item(body);
					}
					body.iventory_location = which;
				}
				else if(slot == equip_region.ring)
				{
					//check both rings
					if(ring1.iventory_location >= 0)
					{
						//check other one instead
						if(ring2.iventory_location >= 0)
						{
							unequip_item(ring2);
						}
						ring2.iventory_location = which;
					}
					else
					{
						ring1.iventory_location = which;
					}
				}
				else if(slot == equip_region.amulet)
				{
					if(amulet.iventory_location >= 0)
					{
						unequip_item(amulet);
					}
					amulet.iventory_location = which;
				}
				else if(slot == equip_region.one_handed)
				{
					//check 1st hand
					if(one_handed1.iventory_location >= 0)
					{
						//check other hand
						if(one_handed2.iventory_location >= 0)
						{
							unequip_item(one_handed2);
						}
						one_handed2.iventory_location = which;
					}
					else if(two_handed.iventory_location >= 0) //check 2 handed
					{
						unequip_item(two_handed);
					}
					else
					{
						one_handed1.iventory_location = which;
					}
				}
				else if(slot == equip_region.two_handed)
				{
					//check 1, 2, and both
					if(two_handed.iventory_location >= 0)
					{
						unequip_item(two_handed);
					}
					else if(one_handed1.iventory_location >= 0)
					{
						unequip_item(one_handed1);
						
						//check other hand
						if(one_handed2.iventory_location >= 0)
						{
							unequip_item(one_handed2);
						}
					}
					two_handed.iventory_location = which;
				}
				
				
				the_one.on_equip();
			}
			
		}
		
		
		
		private void unequip_item(item_location which)
		{	
			//call on_unequip
			equippable_item the_one = (equippable_item) the_items.get(which.iventory_location);
			the_one.on_unequip();
			
			//set slot's value to -1
			which.iventory_location = -1;
		}
		
		private class item_location
		{
			item_location(equip_region body_slot, int inventory_place)
			{
				slot = body_slot;
				iventory_location = inventory_place;
			}
			
			public equip_region slot;
			public int iventory_location;
		}
		
		//store the location of the item in the inventory arraylist
		//store '-1' if nothing is equipped
		private item_location head;
		private item_location body;
		private item_location ring1;
		private item_location ring2;
		private item_location amulet;
		private item_location one_handed1;
		private item_location one_handed2;
		private item_location two_handed;
		
		private int total_weight;
		private ArrayList<item> the_items;
	}

	class Knowledge
	{
		Knowledge()
		{
			the_Facts = new ArrayList<Fact>();
		}
		
		public void add_Fact(Fact new_info)
		{
			if(!this.contains_id(new_info.Get_id()))
			{
				the_Facts.add(new_info);
			}
			//TODO: else?
		}
		
		public void ruminate()
		{
			//TODO: looks at known facts
			if(the_Facts.size()==0)
			{
				helpers.output("You know nothing, " + name);
				helpers.finish_output();
			}
			else
			{
				for(int x=0; x<the_Facts.size(); x++)
				{
					helpers.output("=============");
					the_Facts.get(x).display_fact();
				}
			}
		}
		
		private boolean contains_id(int id)
		{
			for(int x=0; x<the_Facts.size(); x++)
			{
				if(the_Facts.get(x).Get_id()==id)
				{
					return true;
				}
			}
			return false;
		}
		
		
		private ArrayList<Fact> the_Facts;
	}
	
	
//////////////////////////////////////	
	//TODO: wrap all this in a class so it can be here and in Person
	
	private class mobility_score
	{
		mobility_score(String the_name, int new_score)
		{
			name = the_name;
			score = new_score;
		}
		
		public int score;
		public String name;
	}
	
	private int get_score(String location_name)
	{
		for(int x=0; x<mobility.size(); x++)
		{
			if(mobility.get(x).name == location_name)
			{
				return mobility.get(x).score;
			}
		}
		return 1;
	}

	private ArrayList<mobility_score> mobility;	
//////////////////////////////////////

	private String name;
	private Knowledge my_knowledge;
	private Inventory my_inventory;
	private Vector<Action> what_do;
	private Location here;
	private int total_hp;
	private int current_hp;
	
}

///////////////////////////////
abstract class Fact
{
	Fact(String descr, String hf)
	{
		basic_description = descr;
		hard_facts = hf;
		my_id = unique_id;
		unique_id++;
	}
	
	public void on_learn()
	{
		//nothing
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
		//helpers.output(Integer.toString(my_id));
		helpers.finish_output();
	}
	
	protected String basic_description;
	protected String hard_facts;
	protected int my_id;
	
	protected static int unique_id = 1;
}

class Dungeon_Fact extends Fact
{
	Dungeon_Fact(String descr, String hf)
	{
		super(descr,hf);
	}
	
	public void on_learn()
	{
		helpers.get_PC().learn_about_location("Dungeon Below Keshie's Castle", 25);
	}
}

class Goblins_Fact extends Fact
{
	Goblins_Fact(String descr, String hf)
	{
		super(descr,hf);
	}
	
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
		accessibility = 0;
	}
	
	public boolean can_individual_visit(int mobility)
	{
		if(mobility > accessibility)
		{
			return true;
		}
		return false;
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
	protected int accessibility; //lower is easier
}

///////////////////////////////
class Start_Location extends Location
{



}

///////////////////////////////
class Forge extends Location
{
	Forge(String name)
	{
		super(name);
	} 
	
}

///////////////////////////////
class Field extends Location
{
	Field(String name)
	{
		super(name);
	}


}

///////////////////////////////
class Castle extends Location
{
	Castle(String name)
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
class Dungeon extends Location
{
	Dungeon(String name)
	{
		super(name);
		accessibility = 20;
	}
	
}


//TODO: finish item
//don't make this abstract. You can have random 'stuff' items
class item
{
	item()
	{
		
		name = "a 'thing'";
		rules_description = "no, really, this is just the default item description, it does nothing";
		flavor_text = "a boring 'thing' with no discernable use";
		weight = 0;
		value = 0.0000001;
		stolen = false;
		ability = new Nothing();
		slot=equip_region.not_equippable;
		quantity=1;
	}
	
	class Nothing extends Action
	{
		Nothing()
		{
			description = "nothing will happen";
		}
		
		public void What_Happens()
		{
			helpers.output("surprisingly, nothing happens");
			helpers.finish_output();
		}
	}
	
	public void Display()
	{
		helpers.output("Name: " +name);
		helpers.output(rules_description);
		helpers.output("Weight: " + weight + " Value: " + value + weight/value);
		helpers.output("Stolen: " + stolen);
		helpers.output("Count: " + quantity);
		helpers.finish_output();
	}
	
	public int get_weight()
	{
		return weight;
	}
	
	public equip_region get_slot()
	{
		return slot;
	}
	
	public String get_name()
	{
		return name;
	}
	
	public Action get_abiltiy()
	{
		return ability;
	}
	
	public int get_count()
	{
		return quantity;
	}
	
	public boolean have_more_than(int check_number)
	{
		return (check_number > this.quantity);
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
	protected String rules_description;
	protected String flavor_text;
	protected int weight;
	protected double value;
	protected boolean stolen;
	protected Action ability;
	protected equip_region slot;
	protected int quantity;
}


class readable_item extends item
{
	readable_item(String base_name, String base_text)
	{
		name = base_name;
		weight = 0;
		value = 0;
		stolen = false;
		read = false;
		
		text = base_text;
	}
	
	public void Display()
	{
		helpers.output("Name: " + name);
		helpers.output(rules_description);
		helpers.output(flavor_text);
		helpers.output("Weight: " + weight + " Value: " + value + weight/value);
		helpers.output("Stolen: " + stolen);
		helpers.output("Count: " + quantity);
		helpers.output("Read: " + read);
		helpers.finish_output();
	}
	
	class Read extends Action
	{
		Read()
		{
			description = "Display the writing";
		}
		
		public void What_Happens()
		{
			helpers.output(text);
			helpers.finish_output();
			
			read = true;
		}

	}
	
	
	private String text;
	private boolean read;
}

//TODO: finish equippable_item
abstract class equippable_item extends item
{
	equippable_item(equip_region where)
	{
		equipped = false;
		slot = where;
	}
	
	public void Display()
	{
		super.Display();
		helpers.output(slot.toString()); //I really hope this works
		helpers.finish_output();
	}
	
	public void on_equip()
	{
		System.err.println("equipped " + this.name + " item");
		equipped = true;
		
	}
	
	public void on_unequip()
	{
		System.err.println("unequipped " + this.name + " item");
		equipped = false;
	}
	
	private boolean equipped;
}

//only here for testing
class lazy_equip extends equippable_item
{
	lazy_equip(equip_region where)
	{
		super(where);
	}
}



abstract class consumable_item extends item
{
	
	abstract class consume extends Action
	{
		final public void What_Happens()
		{
			helpers.get_PC().decrement_consumable(the_item);
			Other_Happenings();
		}
		
		abstract protected void Other_Happenings();
	}
	
	private item the_item=this;
}

class health_potion extends consumable_item
{
	health_potion()
	{
		ability = new Gain_HP_hp_pot();
		rules_description = ability.description;

		name = "health potion";
		flavor_text = "a vial filled with a red liquid. It smells of cherries";
		weight = 0;
		value = 150;
	}
	
	class Gain_HP_hp_pot extends consume
	{
		Gain_HP_hp_pot()
		{
			description = "Drink a Potion to restore health";
		}
		
		public void Other_Happenings()
		{
			helpers.PC.gain_health(5);
			helpers.output("You feel your wounds re-knit and close");
			helpers.finish_output();
		}
	}

}


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

enum equip_region
{
	head, body, ring, amulet, one_handed, two_handed, not_equippable
}

