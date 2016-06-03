import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;


///////////////////////////////
class Player_Character
{
	public  Player_Character(Location Start)
	{
		this.my_inventory = new Inventory();

//note: the commented-out items are POC items. They can be ignored
		my_inventory.add_new_item(new readable_item("mysterious note", "The stones are growing restless. Beware the Great God Jamie Ter"));
//		my_inventory.add_new_item(new item());
		my_inventory.add_new_item(new health_potion());
//		my_inventory.add_new_item(new lazy_equip(equip_region.one_handed));
//		my_inventory.add_new_item(new lazy_equip(equip_region.one_handed));
//		my_inventory.add_new_item(new lazy_equip(equip_region.two_handed));
		
		this.my_knowledge = new Knowledge();
		
		this.what_do = new Vector<Action>(0);

		what_do.add(new Move());
		what_do.add(new Chat());
		what_do.add(new ExamineInventory());
		what_do.add(new Ruminate());
		what_do.add(new DoNothing());
		
		//TODO: initilize each Action
		
		//use item in inventory
		
		this.location_knowledge = new mobility_controller();
		
		this.name = "Jon Snow";
		this.here = Start;
		this.current_hp = 100;
		this.total_hp = 100;
	}
	
	public void add_action(Action new_action)
	{
		what_do.add(new_action);
	}
	
	//displays at all the actions the player can do
	public void InteractWithEnvironment()
	{
		Vector<Action> potential_actions = Get_Viable_Actions();
		
		helpers.output("");
		helpers.output("Time: " + String.valueOf(helpers.Get_Time()));
		helpers.output("What would you like to do?");
		for(int x=0; x<potential_actions.size(); x++)
		{
			helpers.output(String.valueOf(x+1) + ") " + potential_actions.get(x).description);
		}
		helpers.finish_output();

		int which = helpers.which_one(potential_actions.size());
		
		potential_actions.get(which).What_Happens();
	}
	
	
	//TODO: add logic to ensure that a player can never get a blank action
	private Vector<Action> Get_Viable_Actions()
	{
		Vector<Action> potentials = new Vector<Action>();
		
		for(int x=0; x<what_do.size(); x++)
		{
			if(what_do.get(x).can_be_done())
			{
				potentials.add(what_do.get(x));
			}
		}
		
		Vector<Action> other_options = here.Get_Actions();
		
		for(int x=0; x<other_options.size(); x++)
		{
			if(other_options.get(x).can_be_done())
			{
				potentials.add(other_options.get(x));
			}
		}
		
		return potentials;
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
			for(int x = 0; x<helpers.Location_List.size(); x++)
			{
				Location place = helpers.Location_List.get(x);
				
				
				if(place.can_individual_visit(location_knowledge.get_score(place.Where())))
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
			Vector<Person> everyone =  here.GetEveryone();
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
	
	public void remove_action(Action one_to_remove)
	{
		what_do.remove(one_to_remove);
	}
	
	
	//////
	
	public boolean Player_is_Weak()
	{
		return (current_hp < total_hp/2);
	}
	
	private void Move_Character(Location where_to)
	{
		here = where_to;
	}
	
	////////////////
	
	
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
	
	public void decrement_consumable(String consume)
	{	
		my_inventory.remove_item(my_inventory.check_item(consume), 1);
	}
	
	public Location Get_Location()
	{
		return this.here;
	}
	
	public void learn_about_location(String location_name, int how_much)
	{
		location_knowledge.learn_about_location(location_name, how_much);
		
	}
	
	public void add_item(item new_item)
	{
		int where = my_inventory.check_item(new_item.get_name());
		
		if(where >= 0)
		{
			my_inventory.add_more(where,new_item.get_count());
		}
		else
		{
			my_inventory.add_new_item(new_item);
		}
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
		
		public void add_more(int where, int count)
		{
			the_items.get(where).quantity+=count;
		}

		public int size()
		{
			return the_items.size();
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
		
		//TODO change to 'add new item'
		public void add_new_item(item to_be_added)
		{
			the_items.add(to_be_added);
			total_weight += to_be_added.get_weight();
		}
		
		public int check_item(String item_name)
		{
			for(int x=0; x<the_items.size(); x++)
			{
				if(the_items.get(x).name==item_name)
				{
					return x;
				}
			}
			
			return -1;
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
		private mobility_controller arg;
		
	}

	public boolean has_item(String item_name)
	{
		if(my_inventory.check_item(item_name) >= 0)
		{
			return true;			
		}
		else
		{
			return false;
		}
	}
	
//////////////////	
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
	
	
	
	
//////////////////////////////////////

	private String name;
	private Knowledge my_knowledge;
	private Inventory my_inventory;
	private Vector<Action> what_do;
	private Location here;
	private int total_hp;
	private int current_hp;
	private mobility_controller location_knowledge;
	
	
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


///////////////////////////////
abstract class Action
{	
	Action(){}
	
	public boolean can_be_done(){return true;}
	public void What_Happens(){}
	public String description;
}



///////////////////////////////



///////////////////////////////
abstract class Location
{
	Location()
	{
		who_is_here = new Vector<Person>();
		options = new Vector<Action>();
	}
	
	public Location(String name)
	{
		
		who_is_here = new Vector<Person>();
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
	
	public Vector<Person> GetEveryone()
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
	
	public boolean has_present(String name)
	{
		for(int x=0; x<who_is_here.size(); x++)
		{
			if(who_is_here.get(x).Get_Name().equals(name))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private String Loc_name;
	protected Vector<Action> options;
	protected Vector<Person> who_is_here;
	protected int accessibility; //lower is easier
	
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


//////////////////////////////

class mobility_controller
{
	mobility_controller()
	{
		mobility = new ArrayList<mobility_score>();
	}
	
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

	public int get_score(String location_name)
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

}
