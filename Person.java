import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Person
{
	public Person()
	{
		my_knowledge = new Knowledge();
	}
	
	abstract public void add_item(item new_item);
	
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
	
	public void decrement_consumable(String item)
	{
		// TODO Auto-generated method stub
		
	}

	
	public boolean is_Weak()
	{
		return (current_hp < (total_hp/2));
	}
	
	abstract public void react_to_oppressing_peasants(Person who);
	
	abstract public boolean has_item(item what);
	abstract public boolean has_item(String what);
	abstract public void gain(item what, int how_much);	
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
