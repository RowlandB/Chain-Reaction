import java.util.ArrayList;
import java.util.HashMap;


///////////////////////////////
class Player_Character extends Person
{
	public  Player_Character(Location Start)
	{
		
//note: the commented-out items are POC items. They can be ignored
		character_inventory.add_item(new readable_item("mysterious note", "The stones are growing restless. Beware the Great God Jamie Ter", Player_Character.this));
//		character_inventory.add_new_item(new item());
//		character_inventory.add_new_item(new health_potion());
//		character_inventory.add_item(new lazy_equip(equip_region.one_handed));
//		character_inventory.add_item(new lazy_equip(equip_region.one_handed));
//		character_inventory.add_item(new lazy_equip(equip_region.two_handed));
		character_inventory.add_item(new Destromath_the_Desolator());
		character_inventory.add_item(new torch(Player_Character.this));
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
//		what_do.put("Insult", new Insult());
		
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
	
	class Insult extends Action
	{
		Insult()
		{
			super(Player_Character.this);
			description = "Insult a dude and make them hate you";
		}

		@Override
		public void What_Happens()
		{
			String people;
			ArrayList<NPC> everyone = new ArrayList<NPC>(current_location.GetEveryone().values());
			if(everyone.size() == 0)
			{
				people = "There's no one to insult! (unless you want to insult yourself)";
				helpers.output(people);
				helpers.finish_output();
			}
			else
			{
				helpers.output("Who do you want to insult?");
				for(int x=0; x< everyone.size(); x++)
				{
					helpers.output_partial_list(x+1, everyone.get(x).get_name());
				}
				helpers.finish_output();
				
				int whom = helpers.which_one(everyone.size());
				everyone.get(whom).alter_liking(by_whom, -1000);
			}
		}
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
					character_inventory.Display_Inventory();					
				}
				else if(answer == 2)
				{
					helpers.output("Which item would you like to use?");
					
					if(!character_inventory.isEmpty())
					{
						item useable_item = character_inventory.Select_Item();
						
						useable_item.get_abiltiy().What_Happens();
						
					}
					else
					{
						helpers.output("You have nothing. Dang, you're poor");
					}
				}
				else if(answer == 3)
				{
					character_inventory.Equip_Item();
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
		character_inventory.remove_item(character_inventory.check_item(item.get_name()), 1);		
	}
	
	
	public item PC_select_item()
	{
		return character_inventory.Select_Item();
	}
	
	public void remove_item(item which)
	{
		character_inventory.remove_item(which, 100000);
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
		System.err.println("you died");
	}

	public int get_attack_power()
	{
		//TODO
		return attack_power;
	}
	
	public int get_defense_power()
	{
		//TODO
		return defense_power;
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

	private HashMap<String, Action> what_do;
	private int drunk;
	private int stealth;

}

