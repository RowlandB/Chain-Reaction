import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Person
{
	public Person()
	{
		my_knowledge = new Knowledge();
		this.character_inventory = new Inventory();
	}
	
	abstract public Person get_hate();
	
	public String get_name()
	{
		return name;
	}
	
	abstract public void add_fact(Fact new_fact);
	
	public void learn_about_location(String location_name, int how_much)
	{
		location_knowledge.learn_about_location(location_name, how_much);
	}
	
	public Location getCurrent_location()
	{
		return current_location;
	}
//	
//	public void does_fight_often(Person winner)
//	{
//		//TODO already have it
//		//if()
//			
//		//TODO
//		int number_of_fights = 0;
//		for(Fact potential_fights : my_knowledge.get_facts())
//		{
//			if(potential_fights.Get_Description().contains("fought"))
//			{
//				if(potential_fights.concerns(winner))
//				{
//					number_of_fights++;
//				}
//			}
//		}
//		
//		if(number_of_fights >= 5)
//		{
//			person_fights_alot blarg =  new person_fights_alot(winner);
//			
//		}
//		
//	}

	public void decrease_item(String consume)
	{	
		character_inventory.remove_item(character_inventory.check_item(consume), 1);
	}
	
	public void decrease_item(String consume, int how_much)
	{	
		character_inventory.remove_item(character_inventory.check_item(consume), how_much);
	}
	
	protected void Give(Person target, String name_of_item, int how_much)
	{
		item what = character_inventory.get(name_of_item);
		int really_how_much = Math.min(how_much, what.get_count());
		decrease_item(name_of_item, really_how_much);
		
		target.add_item(what, how_much);		
	}
	
	public void add_item(item new_item)
	{
		int where = character_inventory.check_item(new_item.get_name());
		
		if(where >= 0)
		{
			character_inventory.add_more(where,new_item.get_count());
		}
		else
		{
			character_inventory.add_new_item(new_item);
		}
	}
	
	public void add_item(item new_item, int how_many)
	{
		character_inventory.add_item(new_item, how_many);
	}
	
	public void decrement_consumable(item consume)
	{
		character_inventory.remove_item(consume, 1);
	}
	
	public boolean likes(Person liked_person, int how_much)
	{
		return false;
	}	
	
	public void alter_liking(Person target, int how_much)
	{
		
	}
	
	public boolean is_Weak()
	{
		return (current_hp < (total_hp/2));
	}
	
	abstract public void react_to_oppressing_peasants(Person who);
	

	public boolean has_item(item what)
	{
		return has_item(what.get_name());
	}
	
	public boolean has_item(String item_name)
	{
		if(character_inventory.check_item(item_name) >= 0)
		{
			return true;			
		}
		else
		{
			return false;
		}
	}
	
	abstract public void injure(int x);
	abstract protected void die();
	abstract int get_attack_power();
	abstract int get_defense_power();
	
	protected int current_hp;
	protected Location current_location;
	protected String name;
	protected int total_hp;
	protected mobility_controller location_knowledge;
	protected Knowledge my_knowledge;
	//////////////////////////////////////
	
	protected Inventory character_inventory;
	protected int attack_power;
	protected int defense_power;
	
	
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
		
		public item get(String what)
		{
			for(int x=0; x<the_items.size(); x++)
			{
				item i = the_items.get(x);
				if(i.get_name().equals(what))
				{
					return i;
				}
			}
			return null;
		}
		
		public void add_item(item new_item)
		{
			add_item(new_item, 1);
		}
		
		public void add_item(item new_item, int how_many)
		{
			int x = check_item(new_item.get_name());
			if(x >= 0)
			{
				add_more(x, how_many);
			}
			else
			{
				add_new_item(new_item.copy_item(how_many, Person.this));
			}
		}
		
		private void add_more(int where, int count)
		{
			the_items.get(where).quantity+=count;
		}
		
		public int size()
		{
			return the_items.size();
		}
		
		//TODO generalize for PC and NPC
		public void Equip_Item()
		{	
			helpers.output("Which item would you like to equip?");
		
			if(!this.isEmpty())
			{
				int useable_item = this.Select_Item_Location();
				
				this.equip_item(useable_item);
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
		
		protected item Select_Item()
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
		
		private void add_new_item(item to_be_added)
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
		
		//please don't open this. I'm embarrassed. It's spaghetti code. And it hurts.
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
				
				System.err.println(new_item.getClass().toString());
				
				//call the on_equip function
				equippable_item the_one = (equippable_item) new_item;
				
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
					
					
				the_one.on_equip(Person.this);
			}
					
		}
		
		private void unequip_item(item_location which)
		{	
			//call on_unequip
			equippable_item the_one = (equippable_item) the_items.get(which.iventory_location);
			the_one.on_unequip(Person.this);
			
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
	
	
	public void increase_attack_power(int how_much)
	{
		attack_power = attack_power + how_much;
	}
	
	public void increase_defense_power(int how_much)
	{
		defense_power = defense_power + how_much;
	}
	
}

class mobility_controller
{
	mobility_controller()
	{
		mobility = new HashMap<String, Integer>(0);
	}
	
	//TODO add 'location likability'
	
	public void learn_about_location(String location_name, int how_much)
	{
		int initial = 1;
		if(mobility.containsKey(location_name))
		{
			initial = mobility.get(location_name);
		}
		
		mobility.put(location_name, (how_much+initial));
	}

	public int get_score(String location_name)
	{
		if(!mobility.containsKey(location_name))
		{
			mobility.put(location_name, 1);
		}
		
		return mobility.get(location_name);
	}
	
	private HashMap<String, Integer> mobility;	

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
	
	public void display_all_facts()
	{
		if(the_Facts.size()==0)
		{
			helpers.output("You know nothing");
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
	
	public boolean contains_id(int id)
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
	
	public Set<Fact> get_facts()
	{
		return new HashSet<Fact>(the_Facts);
	}
	
	private ArrayList<Fact> the_Facts;
}


