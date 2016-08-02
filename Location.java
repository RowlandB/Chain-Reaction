import java.util.ArrayList;
import java.util.HashMap;

abstract class Location
{
	Location()
	{
		who_is_here = new HashMap<String, NPC>();
		options = new HashMap<String, Action>();
		flammability = 0;

		options.put("Search", new Search());
		unattended_stuff = new HashMap<String, item>();
	}
	
	public Location(String name)
	{
		
		who_is_here = new HashMap<String, NPC>();
		options = new HashMap<String, Action>();
		Loc_name = name;
		accessibility = 0;
		flammability = 0;
	
		
		unattended_stuff = new HashMap<String, item>();
		options.put("Search", new Search());
	}
	
	public void time_passes()
	{
		if(this.burning)
		{
			if(helpers.random(0, 100) < this.get_flammability())
			{
				//building has burnt
				this.Loc_name = "The Burnt Remains of " + this.Loc_name;
				helpers.output(this.Where() + " has burnt to the ground");
				burning = false;
			}
			else
			{
				//helpers.get_PC().injure(5);
				for(NPC guys : who_is_here.values())
				{
					guys.injure(5);
				}
				
				if(helpers.get_PC().getCurrent_location().equals(this))
				{
					helpers.output(this.Where() + " is burning!");			
				}
				
				
			}
		}
	}
	
	protected int get_flammability()
	{
		return flammability;
	}

	public boolean can_individual_visit(int mobility)
	{
		if(mobility > accessibility)
		{
			return true;
		}
		return false;
	}
	
	public void notify_fight(Person winner, Person loser)
	{
		for(Person people : GetEveryone().values())
		{
			people.add_fact(new person_has_fought(winner, loser));
		}
	}
	
	//A location will never remove a person by itself: this should only be called from Person
	public void RemovePerson(NPC leaver)
	{
		who_is_here.remove(leaver.get_name());		
	}
	
	public void AddPerson(NPC arrival)
	{
		who_is_here.put(arrival.get_name(), arrival);
	}
	
	public HashMap<String, NPC> GetEveryone()
	{
		return who_is_here;
	}
	
	public HashMap<String, Action> Get_Actions()
	{
		return options;
	}
	
	public String Where()
	{
		return Loc_name;
	}
	
	public void set_name(String new_name)
	{
		Loc_name = new_name;
	}
	
	public boolean has_present(String name)
	{
		return who_is_here.containsKey(name);
	}
	
	public boolean burn_location()
	{
		if(this.get_flammability()==0)
		{
			//can't be burnt
			return false;			
		}
		else
		{
			burning = true;
		}
		
		return true;
	}
	

	class Search extends Action
	{
		Search()
		{
			description = "Search the area";
		}
		
		@Override
		public void What_Happens()
		{
			if(unattended_stuff.size()==0)
			{
				helpers.output("You find nothing");
			}
			else
			{
				ArrayList<String> temp = new ArrayList<String>(0);
				int x = 1;
				helpers.output("You find:");
				for(item stuff : unattended_stuff.values())
				{
					if(stuff.get_stolen())
					{
						//TODO: add stealth
						helpers.output_partial_list(x, stuff.get_name() + " (" + Integer.toString(stuff.get_count()) + ")", true);
					}
					else
					{
						helpers.output_partial_list(x, stuff.get_name() + " (" + Integer.toString(stuff.get_count()) + ")");
					}
					x++;
					temp.add(stuff.get_name());
				}

				helpers.output_partial_list(unattended_stuff.size() + 1, "Nothing");
				
				helpers.output("What would you like to take?");
				helpers.finish_output();
				
				int which = helpers.which_one(unattended_stuff.size() + 1);
				
				if(which < unattended_stuff.size()) //else it's 'nothing'
				{
					String name = temp.get(which);
					
					helpers.output("How many?");
	
					int how_many = helpers.which_one(10000) +1; //adding 1 because of how which_one is coded
					
					boolean stealthy = helpers.get_PC().be_stealthy(2*how_many);
					
					if(!unattended_stuff.get(name).get_stolen() || stealthy)
					{
						helpers.get_PC().gain(unattended_stuff.get(name), how_many);
						
						if(unattended_stuff.get(name).get_count() > how_many)
						{
							unattended_stuff.get(name).set_count(unattended_stuff.get(name).get_count() - how_many);
						}
						else
						{
							unattended_stuff.remove(name);
						}
					}
					else
					{
						helpers.output("You fail to steal the " + name);
					}
				}
			}
		}
	}
	
	
	
	private String Loc_name;
	protected HashMap<String, Action> options;
	protected HashMap<String, NPC> who_is_here;
	protected int accessibility; //lower is easier
	protected HashMap<String, item> unattended_stuff;
	protected int flammability; //percent chance to be done burning
	protected boolean burning;

}

