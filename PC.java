import java.util.ArrayList;
import java.util.HashMap;


///////////////////////////////
class Player_Character extends Person
{
	public  Player_Character(Location Start)
	{
		this.my_inventory = new Inventory();

//note: the commented-out items are POC items. They can be ignored
		my_inventory.add_new_item(new readable_item("mysterious note", "The stones are growing restless. Beware the Great God Jamie Ter", Player_Character.this));
//		my_inventory.add_new_item(new item());
//		my_inventory.add_new_item(new health_potion());
//		my_inventory.add_new_item(new lazy_equip(equip_region.one_handed));
//		my_inventory.add_new_item(new lazy_equip(equip_region.one_handed));
//		my_inventory.add_new_item(new lazy_equip(equip_region.two_handed));
		my_inventory.add_new_item(new torch(Player_Character.this));
		wine starting_wine = new wine(Player_Character.this);
		starting_wine.set_count(10);
		add_item(starting_wine);
		
		this.my_knowledge = new Knowledge();
		
		this.what_do = new HashMap<String, Action>(0);

		what_do.put("Move", new Move());
		what_do.put("Chat", new Chat());
		what_do.put("Inventory", new ExamineInventory());
		what_do.put("Facts", new Ruminate());
		what_do.put("Nothing", new DoNothing());
		
		this.location_knowledge = new mobility_controller();
		
		this.name = "Jon Snow";
		this.current_location = Start;
		this.current_hp = 100;
		this.total_hp = 100;
		this.drunk = 0;
		this.stealth = 10;
	}
	
	public void add_action(Action new_action)
	{
		what_do.put(new_action.Get_Description(),new_action);
	}
	
	@Override
	public void react_to_oppressing_peasants(Person who){}//nothing because I'm doing it
	
	//displays at all the actions the player can do
	public void InteractWithEnvironment()
	{
		
		this.time_passes();
		
		ArrayList<Action> potential_actions = Get_Viable_Actions();
		
		//helpers.output("");
		helpers.output("Time: " + String.valueOf(helpers.Get_Time()));
		helpers.output("What would you like to do?");
		for(int x=0; x<potential_actions.size(); x++)
		{
			helpers.output_partial_list(x+1, potential_actions.get(x).Get_Description());
		}
		helpers.finish_output();

		int which = helpers.which_one(potential_actions.size());
		
		potential_actions.get(which).What_Happens();
	}
	
	
	private ArrayList<Action> Get_Viable_Actions()
	{
		ArrayList<Action> potentials = new ArrayList<Action>();
		
		for(Action possible_action: what_do.values())
		{
			if(possible_action.can_be_done())
			{
				potentials.add(possible_action);
			}
		}
		
		HashMap<String, Action> other_options = current_location.Get_Actions();
		
		for(Action possible_action: other_options.values())
		{
			if(possible_action.can_be_done())
			{
				potentials.add(possible_action);
			}
		}
		
		return potentials;
	}
	
	class Move extends Action
	{
		Move()
		{
			super(Player_Character.this);
			description = "Go someplace else";
		}
		
		public void What_Happens()
		{
			helpers.output("Where?");
			
			int y=1;
			ArrayList<Location> potential_places = new ArrayList<Location>();
			for(Location places: helpers.Location_List.values())
			{
				
				if(places.can_individual_visit(location_knowledge.get_score(places.Where())))
				{
					potential_places.add(places);
					helpers.output_partial_list(y, places.Where());
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
			super(Player_Character.this);
			description = "Talk to someone near you";
		}
		
		public void What_Happens()
		{
			String people;
			ArrayList<NPC> everyone = new ArrayList<NPC>(current_location.GetEveryone().values());
			if(everyone.size() == 0)
			{
				people = "There's no one to talk to! (unless you want to talk to yourself)";
				helpers.output(people);
				helpers.finish_output();
			}
			else
			{
				helpers.output("With whom would you like to speak?");
				for(int x=0; x< everyone.size(); x++)
				{
					helpers.output_partial_list(x+1, everyone.get(x).get_name());
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
			super(Player_Character.this);
			description = "Wait for things to happen";
		}
		
		public void What_Happens()
		{}
	}
	
	class ExamineInventory extends Action
	{
		ExamineInventory()
		{
			super(Player_Character.this);
			description = "Check out your items";
		}
		
		public void What_Happens()
		{
			int answer = 0;
			//options
			while(answer != 4)
			{
				helpers.output_partial_list(1, "View Inventory");
				helpers.output_partial_list(2, "Use an Item");
				helpers.output_partial_list(3, "Equip an Item");
				helpers.output_partial_list(4, "Done");
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
			super(Player_Character.this);
			description = "View previously discovered facts";
		}
		
		public void What_Happens()
		{
			my_knowledge.display_all_facts();
		}
	}
	
	class Equip_Items extends Action
	{
		Equip_Items()
		{
			super(Player_Character.this);
			description = "Equip Items";
		}
		
		public void What_Happens()
		{
			int the_item = my_inventory.Select_Item_Location();
			my_inventory.equip_item(the_item);
		}
		
	}
	
	/*
	public void gain_item(item which)
	{
		
	}*/
	
	public boolean be_stealthy(int how_hard)
	{
		return (how_hard < helpers.random(0, 100)+stealth);
	}
	
	public void remove_action(Action one_to_remove)
	{
		what_do.remove(one_to_remove.description);
	}
	
	
	//////
	
	public void add_drunkeness(int how_long)
	{
		drunk = drunk + how_long;
	}
	
	public boolean Player_is_Weak()
	{
		return (current_hp < total_hp/2);
	}
	
	private void Move_Character(Location where_to)
	{
		current_location = where_to;
	}
	
	protected void time_passes()
	{
		if(drunk > 0)
		{
			drunk--;
		}
	}
	
	////////////////
	
	public void gain_health(int how_much)
	{
		if(current_hp + how_much > total_hp)
		{
			current_hp = total_hp;
		}
	}
	
	public void add_fact(Fact new_fact)
	{
		my_knowledge.add_Fact(new_fact);
		new_fact.on_learn(this);
	}
	
	public void steal(item item)
	{
		my_inventory.remove_item(my_inventory.check_item(item.get_name()), 1);		
	}
	
	public void decrement_consumable(item consume)
	{
		my_inventory.remove_item(consume, 1);
	}
	
	public void decrement_consumable(String consume)
	{	
		my_inventory.remove_item(my_inventory.check_item(consume), 1);
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
	
	public item PC_select_item()
	{
		return my_inventory.Select_Item();
	}
	
	public void remove_item(item which)
	{
		my_inventory.remove_item(which, 100000);
	}
	
	@Override
	public void gain(item new_item, int how_many)
	{
		int x = my_inventory.check_item(new_item.get_name());
		if(x >= 0)
		{
			my_inventory.add_more(x, how_many);
		}
		else
		{
			my_inventory.add_new_item(new item(new_item, how_many, Player_Character.this));
		}
	}
	
	
	@Override
	public void injure(int x)
	{
		if(current_hp-x <= 0)
		{
			this.die();
		}
		else
		{
			current_hp = current_hp - x;
		}
		
	}
	
	protected void die()
	{
		// TODO Auto-generated method stub
		
	}

	public int get_attack_power()
	{
		//TODO
		return 100;
	}
	
	public int get_defense_power()
	{
		//TODO
		return 100;
	}
	
	
	//////////////////////////////////////
	class Inventory
	{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Inventory()
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
				helpers.output_partial_list(x, the_items.get(x-1).get_name());
			}
		
			helpers.finish_output();

			int which_one = helpers.which_one(the_items.size());
			
			return the_items.get(which_one);
		}
		
		private int Select_Item_Location()
		{
			for(int x=1; x <= the_items.size(); x++)
			{
				helpers.output_partial_list(x, the_items.get(x-1).get_name());
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
			if(the_items.get(which).get_count() > how_many)
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
			the_items.get(which).increase_quantity(how_many);
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

	@Override
	public boolean has_item(item what)
	{
		return has_item(what.get_name());
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
	
	public boolean knows_fact(int fact_id)
	{
		return my_knowledge.contains_id(fact_id);
	}
	
//////////////////	


	@Override
	public Person get_hate()
	{
		return null;
	}
	
//////////////////////////////////////

	private Inventory my_inventory;
	private HashMap<String, Action> what_do;
	private int drunk;
	private int stealth;

}

