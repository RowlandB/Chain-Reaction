import java.util.HashMap;


public class Main
{
	
	public static void main(String [ ] args)
	{
		My_Game_Initializer blarg = new My_Game_Initializer();
		
		
		blarg.Play_Game();
	}
}

class Bob extends Commoner
{
	Bob(Location L, Location new_home)
	{
		super(L,"Bob");
		
		home = new_home;
		
		//proof-of-concept, shows we can make actions that change our actions
		/*
		class become_single_minded extends NPC_Action
		{
			become_single_minded()
			{
				String name = Get_Name();
				description =  name + " becomes more single-minded";
				likelihood = 1;
			}
			
			public void What_Happens()
			{
				this.likelihood++;
			}
		}
	
		this.potential_actions.add(new become_single_minded());
		*/
		
		
		class steal_wine extends NPC_Action
		{
			steal_wine()
			{
				description = " does nothing suspicious";
				likelihood = 3;
			}
			
			@Override
			public boolean can_be_done()
			{
				return (helpers.get_PC().getCurrent_location().equals(current_location) && helpers.get_PC().has_item("wine"));
			}
			
			public void What_Happens()
			{
				helpers.get_PC().steal(new wine());
			}
			
		}
		
		this.potential_actions.put("steal wine", new steal_wine());
		
		personal_greeting = "'allo, govna'";
	}
	
}

///////////////////////////////
class Bobs_Field extends Fields
{
	Bobs_Field(String name)
	{
		super(name, new corn(), 5); //TODO extend this to a more realistic number
		flammability = 90;
	}
}

///////////////////////////////
class Castle extends Location
{
	Castle(String name)
	{
		super(name);
		Action laugh_at_plebs = new oppress_peasants();
		options.put("laugh_at_plebs", laugh_at_plebs);
	}

	class oppress_peasants extends Action
	{
		oppress_peasants()
		{
			description = "Oppress some peasants";
		}
	
		public boolean can_be_done()
		{
			return helpers.get_PC().Player_is_Weak();
		}
		
		public void What_Happens()
		{
			helpers.output("Haha! Those fools should have been born with money. Let's make them fight for coppers some more!");
			helpers.finish_output();
		}
	}
}


class corn extends consumable_item
{
	corn()
	{
		ability = new eat_tasty_corn();
		rules_description = ability.description;

		name = "corn";
		flavor_text = "a cob with corn on it";
		weight = 1;
		value = 1;
	}
	
	class eat_tasty_corn extends consume
	{
		eat_tasty_corn()
		{
			description = "eat some tasty corn";
		}
		
		public void Other_Happenings()
		{
			helpers.output("Yum! That was delicious!");
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
		
		unattended_stuff.put("torch", new torch());
	}
	
	
	
}

class Dungeon_Fact extends Fact
{
	Dungeon_Fact()
	{
		super("dungeons","We have a dungeon. You should visit it some time.");
	}
	
	public void on_learn()
	{
		helpers.get_PC().learn_about_location("Dungeon Below Keshie's Castle", 25);
	}
}

class Fields extends Location
{
	Fields(String name, item grow_thing, int how_quickly)
	{
		super(name);
		thing_that_grows = grow_thing;
		time_it_takes = how_quickly;
		time_till_done = how_quickly;
	}
	
	@Override
	public boolean burn_location()
	{
		unattended_stuff.remove(thing_that_grows.get_name());
		return super.burn_location();
	}
	
	@Override
	public void time_passes()
	{
		super.time_passes();
		if(!burning)
		{
			grow();
		}	
	}
	
	void grow()
	{
		time_till_done--;
		if(time_till_done==0)
		{
			time_till_done = time_it_takes;
			harvest();
		}
	}
	
	void harvest()
	{
		if(unattended_stuff.containsKey(thing_that_grows.get_name()))
		{
			unattended_stuff.get(thing_that_grows.get_name()).increase_quantity(1);
		}
		else
		{
			unattended_stuff.put(thing_that_grows.get_name(), thing_that_grows);
		}
	}
	
	protected int time_till_done;
	protected int time_it_takes;
	protected item thing_that_grows;
}

///////////////////////////////
class Forge extends Location
{
	Forge(String name)
	{
		super(name);
		flammability = 20;
	} 

}

class Hank_likes_wine extends Fact
{
	Hank_likes_wine()
	{
		super("wine", "Hank *really* likes wine. You could say he has a hankering for it.");
	}
	
	public void on_learn()
	{
		helpers.get_PC().add_action(new give_Hank_wine());
	}
	
	
	class give_Hank_wine extends Action
	{
		give_Hank_wine()
		{
			description = "give Hank some wine";
		}
		
		public boolean can_be_done()
		{
			boolean has_wine = helpers.get_PC().has_item("wine");
			if(has_wine)
			{
				boolean same_location = helpers.get_PC().getCurrent_location().has_present("Hank");
				if(same_location)
				{
					return true;
				}
			}
			
			return false;
		}
		
		public void What_Happens()
		{
			//player loses wine
			helpers.get_PC().decrement_consumable("wine");
			
			helpers.Get_Person_by_name("Hank").increase_friendliness(helpers.get_PC(), 20);
			helpers.Get_Person_by_name("Hank").add_item(new wine());

			//helpers.get_PC().remove_action(myself);
		}
		
		Action myself = this;
	}
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

///////////////////////////////
class Hovel extends Location
{
	Hovel(String name)
	{
		super(name);
		flammability = 75;
	}
}


class My_Game_Initializer extends Game_Initializer
{
	public void Initialize(HashMap<String, Location> places, HashMap<String, NPC> NPCs)
	{
			/////Locations
			Location Blacksmith = new Forge("Blacksmith's");
			Location Bobs_Fields = new Bobs_Field("Bob's Field");
			Location Keshies_Castle = new Castle("Keshie's Castle");
			Location Dungeon = new Dungeon("Dungeon Below Keshie's Castle");
			Location Bobs_Hovel = new Hovel("Bob's Hovel");
			Location Tavern = new Tavern("The Tavern");
			places.put("Blacksmith's", Blacksmith);
			places.put("Bob's Field", Bobs_Fields);
			places.put("Keshie's Castle", Keshies_Castle);
			places.put("Dungeon Below Keshie's Castle", Dungeon);
			places.put("Bob's Hovel", Bobs_Hovel);
			places.put("The Tavern", Tavern);			
			
			
			////Persons
			NPC Generic_Peasant = new Bob(Bobs_Fields, Bobs_Hovel);
			Bobs_Fields.AddPerson(Generic_Peasant);
			NPCs.put("Bob", Generic_Peasant);
			
			NPC Generic_Noble = new Noble_Kesh(Keshies_Castle);
			Keshies_Castle.AddPerson(Generic_Noble);
			NPCs.put("Noble Kesh", Generic_Noble);
			
			NPC Angry_Peasant = new Norton(Tavern);
			Tavern.AddPerson(Angry_Peasant);
			NPCs.put("Norton", Angry_Peasant);
			
			NPC Hank = new Noble_Hank(Keshies_Castle);
			Keshies_Castle.AddPerson(Hank);
			NPCs.put("Hank", Hank);
			
			NPC Doug = new Doug(Keshies_Castle, Hank, Generic_Noble);
			Keshies_Castle.AddPerson(Doug);
			NPCs.put("Doug", Doug);
	}

	public Player_Character new_Player()
	{
		return new Player_Character(new Start_Location());
	}
}

class Doug extends Body_Guard
{
	Doug(Location L, Person who_support, Person test_who_hate)
	{
		super(L, "Doug");
		increase_friendliness(who_support, 5);
		increase_friendliness(test_who_hate, -10);
		
		knowledge_base.put("hank_likes_wine", new Hank_likes_wine());
		
		personal_greeting = "Uh do wut "  + who_support.get_name() + " tells muh to do";
	}
}

class Noble_Hank extends Noble
{
	Noble_Hank(Location L)
	{
		super(L, "Hank");
		
		knowledge_base.put("hank_likes_wine", new Hank_likes_wine());
		
		potential_actions.put("drink wine", new drink_wine());
		
		personal_greeting = "Do you have any wine?";
	}
	
	class drink_wine extends NPC_Action
	{
		drink_wine()
		{
			description = "drinks some wine";
			likelihood = 20;
		}
		
		public boolean can_be_done()
		{
			return has("wine");
		}
		
		@Override
		public void What_Happens()
		{
			mood = Mood.happy;
			
			//decrement wine
			if(helpers.random(1, 3)==1)
			{
				decrease_item("wine", 1);
			}
		}
	}
}

class Noble_Kesh extends Noble
{
	Noble_Kesh(Location L)
	{
		super(L,"High Lord Kesh of No-Funnington");
		
		knowledge_base.put("goblins", new boring_fact("goblins","There are goblins in the mountains. It's as good a plot hook as any."));
		knowledge_base.put("dungeon", new Dungeon_Fact());
	
		personal_greeting = "greetings, peasant";
	}
}

class Norton extends Commoner
{
	Norton(Location L)
	{
		super(L,"Norton");
		
		this.mood=Mood.angry;
		this.increase_friendliness(helpers.get_PC(), -1);
		
		personal_greeting = "hey, you git";
	}	
}



///////////////////////////////
class Start_Location extends Location
{

}

///////////////////////////////
class Tavern extends Location
{
	Tavern(String name)
	{
		super(name);
		
		wine unattended_wine = new wine();
		unattended_wine.set_stolen(true);
		unattended_wine.set_count(100);
		
		unattended_stuff.put("wine", unattended_wine);
		
		flammability = 30;
	}
	
	@Override
	protected int get_flammability()
	{
		int how_much = unattended_stuff.get("wine").get_count();
		
		return Math.max(0, how_much);
	}
}

class torch extends consumable_item
{
	torch()
	{
		ability = new burn();
		rules_description = ability.description;

		name = "torch";
		flavor_text = "it's for lighting things on fire";
		weight = 1;
		value = 1;
	}
	
	class burn extends consume
	{
		burn()
		{
			description = "burn things";
		}
		
		protected void Other_Happenings()
		{
			helpers.output("Which?");
			helpers.output_partial_list(1, "Building");
			helpers.output_partial_list(2, "Item");
			helpers.finish_output();
			
			int which = helpers.which_one(2) + 1;
			
			if(which==1)
			{
				Location here = helpers.get_PC().getCurrent_location();
				
				if(!here.burn_location())
				{
					helpers.output("it doesn't catch");
				}
			}
			else
			{
				item which_item = helpers.get_PC().PC_select_item();
				
				if(which_item.isFlammable())
				{
					helpers.get_PC().remove_item(which_item);
				}
			}
		}
	}
}

class wine extends consumable_item
{
	wine()
	{
		ability = new get_drunk();
		rules_description = ability.description;

		name = "wine";
		flavor_text = "well, it's better than water";
		weight = 0;
		value = 10;
	}

	class get_drunk extends consume
	{
		get_drunk()
		{
			description = "you drink, and you get drunk";
		}

		protected void Other_Happenings()
		{
			helpers.get_PC().add_drunkeness(5);
		}
	}
}