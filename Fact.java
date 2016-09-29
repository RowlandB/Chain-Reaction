abstract class Fact
{
	Fact(String descr, String hf)
	{
		basic_description = descr;
		hard_facts = hf;
		my_id = unique_id;
		unique_id++;
	}
	
	public void on_learn(Person learner)
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

	public boolean concerns(Person individual)
	{
		return false;
	}
}

class person_has_fought extends Fact
{
	person_has_fought(Person the_winner, Person the_loser)
	{
		super("a fight", the_winner.get_name() + " beat " + the_loser.get_name() + " in a fight");
		winner = the_winner;
		loser = the_loser;
	}
	
	@Override
	public void on_learn(Person Learner)
	{
		//Learner.does_fight_often(winner);
		//Learner.does_fight_often(loser);
	}
	
	@Override
	public boolean concerns(Person individual)
	{
		if(individual.equals(winner))
		{
			return true;
		}
		else if(individual.equals(loser))
		{
			return true;
		}
		
		return false;
	}
	
	Person winner;
	Person loser;
}

class person_fights_alot extends Fact
{
	public person_fights_alot(Person fighter)
	{
		// TODO Auto-generated constructor stub
		super("pugalism", fighter.get_name() + " fights a lot");
		
		who = fighter;
	}
	
	Person who;
}

